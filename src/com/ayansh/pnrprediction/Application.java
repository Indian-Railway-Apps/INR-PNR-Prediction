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

import com.ayansh.pnrprediction.exception.ClassNotSupportedException;
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
		db.setUpConnection();
		
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
	
	public Result calculateProbability(String trainNo, String travelDate,
			String travelClass, String currentStatus) throws SQLException,
			ClassNotSupportedException, UnKnownDBError, ParseException {

		// Get RAC Quota
		int racQuota = db.getRACQuota(trainNo, travelClass);
		
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
		int calcellations, cnfCount = 0, racCount = 0, total = 0;
		
		ResultSet resultSet = db.getAvailabilityHistory(trainNo, travelClass, dayDiff);
		while(resultSet.next()){
			
			calcellations = resultSet.getInt(4);
			
			if(calcellations >= cnfDeficit){
				cnfCount++;
			}
			
			if(calcellations >= racDeficit){
				racCount++;
			}
			
			total++;
		}
		
		resultSet.close();
		
		// Calculate Probability
		result.setCNFProbability(100 * cnfCount / total);
		result.setRACProbability(100 * racCount / total);
		result.setMessage("Probability Calculated Successfully");
		result.setResultCode(0);
		
		return result;

	}
}
