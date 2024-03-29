/**
 * 
 */
package com.ayansh.pnrprediction;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import org.json.JSONObject;

import com.ayansh.pnrprediction.exception.ClassNotSupportedException;
import com.ayansh.pnrprediction.exception.InvalidStationCodesException;
import com.ayansh.pnrprediction.exception.InvalidTrainNoException;
import com.ayansh.pnrprediction.exception.UnKnownDBError;


/**
 * @author varun
 * 
 */
public class Application {

	private static Application app;
	private Properties properties;
	private Result result;
	private DBServer db;
	
	public static Application getInstance() {

		if (app == null) {
			app = new Application();
		}

		return app;

	}

	private Application() {
		
		properties = new Properties();
		result = new Result();
		
	}
	
	public Result getResultObject(){
		return result;
	}
	
	public void initializeApplication() throws IOException, SQLException{
		
		properties.load(getClass().getClassLoader().getResourceAsStream("config.properties"));
		
		// Set up DB Connection
		db = new MySQLDB("jdbc:mysql://" + properties.getProperty("mysql_server"));
		
		String user, pwd;
		
		user = properties.getProperty("db_user");
		pwd = properties.getProperty("pwd");
		
		if(user == null || user.contentEquals("")){
			user = "appuser";
			pwd = "x8w4ySzIV";
		}
		
		db.setUpConnection(user,pwd);
		
	}
	
	public Properties getApplicationProperties(){
		return properties;
	}

	public void close() {

		try {
			db.close();
		} catch (SQLException e) {
			throw new IllegalArgumentException();
		}
	}
	
	public Result calculateProbability(JSONObject input) throws SQLException,
			ClassNotSupportedException, UnKnownDBError, ParseException, InvalidTrainNoException, InvalidStationCodesException {
		
		String trainNo = input.getString("TrainNo");
		String travelDate = input.getString("TravelDate");
		String travelClass = input.getString("TravelClass");
		String currentStatus = input.getString("CurrentStatus");
		String fromStation = input.getString("FromStation");
		String toStation = input.getString("ToStation");
		
		// Validate Train Number
		db.validateTrainNo(trainNo);
		
		// Validate Station Codes
		db.validateStationCodes(trainNo, fromStation, toStation);
		
		// Get Quota
		TrainQuota quotaInfo = db.getQuota(trainNo, travelClass, fromStation, toStation);
		
		int racQuota = quotaInfo.getRacQuota();
		int eq = quotaInfo.getEmergencyQuota();
		
		// Calculate Current Deficit.
		int cnfDeficit = 0, racDeficit = 0;
		
		currentStatus = currentStatus.replace(" ", "");
		
		String[] cs = currentStatus.split("/");
		if(cs.length == 1){
			currentStatus = cs[0];
		}
		else{
			currentStatus = cs[1];	
		}
		
		if(currentStatus.contains("AVAILABLE")){
			cnfDeficit = racDeficit = 0;
		}
		
		if(currentStatus.contains("WL")){
			racDeficit = cnfDeficit = Integer.valueOf(currentStatus.substring(2));
			cnfDeficit += racQuota;
		}
		
		if(currentStatus.contains("RAC")){
			cnfDeficit = Integer.valueOf(currentStatus.substring(3));
			racDeficit = 0;
		}
		
		// Calculate Date Diff
		Date today = new Date();
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Date trDate = sdf.parse(travelDate);
		
		int dayDiff = (int) ((trDate.getTime() - today.getTime())/(1000*60*60*24));
		
		// Because we cast to INTEGER, so less that 1 day becomes 0 days!
		// TODO : Ideally I should take the Hour diff.
		dayDiff++;
		
		int cancellations, cnfCount = 0, racCount = 0, total = 0, cancSum = 0;
		int optCNFCount = 0, optRACCount = 0;
		
		ResultSet resultSet = db.getAvailabilityHistory(trainNo, travelClass, fromStation, toStation, dayDiff);
		while(resultSet.next()){
			
			cancellations = resultSet.getInt(4);
			cancSum += cancellations;
			
			// Pessimistic Calculations
			if(cancellations >= cnfDeficit){
				cnfCount++;
			}
			
			if(cancellations >= racDeficit){
				racCount++;
			}
			
			// Optimistic Calculations
			if(cancellations + eq >= cnfDeficit){
				optCNFCount++;
			}
			
			if(cancellations + eq >= racDeficit){
				optRACCount++;
			}
			
			total++;
		}
				
		resultSet.close();
		
		int avjCancellations = cancSum / total;
		int expectedDeficit;
		String expectedStatus = "";
		
		// Calculate Probability
		result.setCNFProbability(100 * cnfCount / total);
		result.setRACProbability(100 * racCount / total);
		result.setOptimisticCNFProb(100 * optCNFCount / total);
		result.setOptimisticRACProb(100 * optRACCount / total);
		
		// Calculate Expected status

		expectedDeficit = cnfDeficit - avjCancellations;

		if (expectedDeficit > 0 && expectedDeficit > racQuota) {
			// Still waiting :(
			expectedStatus = "WL" + (expectedDeficit - racQuota);
		}

		if (expectedDeficit > 0 && expectedDeficit <= racQuota) {
			// Still RAC :|
			expectedStatus = "RAC" + (racQuota - expectedDeficit + 1);
		}

		if (expectedDeficit <= 0) {
			expectedStatus = "CNF";
		}
		
		result.setExpectedStatus(expectedStatus);
		result.setMessage("Probability Calculated Successfully");
		result.setResultCode(0);
		
		// After successful calculation, save into Query History
		try{
			db.saveQueryHistory(input,result);
		}catch(Exception e){
			// Its OK !
			result.addMessageToLog("We could not save into query history");
		}
		
		return result;

	}
}
