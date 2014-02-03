package com.ayansh.pnrprediction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONObject;

import com.ayansh.pnrprediction.exception.ClassNotSupportedException;
import com.ayansh.pnrprediction.exception.InvalidStationCodesException;
import com.ayansh.pnrprediction.exception.InvalidTrainNoException;
import com.ayansh.pnrprediction.exception.UnKnownDBError;

public class MySQLDB implements DBServer {
	
	private Connection mySQL;
	private String dbURL;
	
	public MySQLDB(String dbURL){
		this.dbURL = dbURL;
	}
	
	@Override
	public void setUpConnection(String user, String pwd) throws SQLException {
		
		mySQL = DriverManager.getConnection(dbURL, user, pwd);
		
	}

	@Override
	public void close() throws SQLException {
		
		if(mySQL != null){
			mySQL.close();
		}
	}

	@Override
	public TrainQuota getQuota(String trainNo, String travelClass, String fromStation, String toStation)
			throws SQLException, ClassNotSupportedException, UnKnownDBError {

		TrainQuota quotaInfo = new TrainQuota();
		
		quotaInfo.setTravelClass(travelClass);
		
		if (travelClass.contentEquals("3A") || travelClass.contentEquals("SL")) {
			// If the Class of travel is 3A or SL then we are good...
		} else {
			throw new ClassNotSupportedException();
		}
		
		Application app = Application.getInstance();
		
		Statement stmt = mySQL.createStatement();
		
		String sql = "SELECT RACQuota, EQ FROM TrainQuota where TrainNo = '"
				+ trainNo + "' and Class = '" + travelClass + "'";

		ResultSet result = stmt.executeQuery(sql);
		
		if(result.next()){
			quotaInfo.setRacQuota(result.getInt(1));
			quotaInfo.setEmergencyQuota(result.getInt(2));
		}
		else{
			// Looks like we are not tracking this train !!
			app.getResultObject().addMessageToLog("We are not tracking this train. We can only calculate approx.");
			
			// Find list of other trains that run b/w the given stations
			String trainList = getTrainsBetweenStations(fromStation, toStation);
			
			if(trainList.contentEquals("*")){
				sql = "SELECT (sum(RACQuota) / count(*)) as RacQuota, (sum(EQ) / count(*)) as EQ " +
						"FROM TrainQuota where Class = '" + travelClass + "'";
			}
			else{
				sql = "SELECT (sum(RACQuota) / count(*)) as RacQuota, (sum(EQ) / count(*)) as EQ " +
						"FROM TrainQuota where Class = '" + travelClass + "' "
						+ "and TrainNo IN (" + trainList + ")";
			}
			
			result = stmt.executeQuery(sql);
			
			if(result.next()){
				quotaInfo.setRacQuota(result.getInt(1));
				quotaInfo.setEmergencyQuota(result.getInt(2));
			}
			else{
				throw new UnKnownDBError();
			}
		}
		
		result.close();
		
		return quotaInfo;
	}
	
	@Override
	public String getTrainsBetweenStations(String fromStation, String toStation) throws SQLException {
		
		Statement stmt = mySQL.createStatement();
		
		String sql = "Select a.TrainNo from TrainStops as a inner join TrainStops as b "
				+ "on a.TrainNo = b.TrainNo where a.StationCode = '" + fromStation + "' and "
						+ "b.StationCode = '" + toStation + "' and a.StopNo < b.StopNo";
		
		ResultSet result = stmt.executeQuery(sql);
		
		if(result.next()){
			
			String trainList = "'" + result.getString(1) + "'";
			
			while(result.next()){
				trainList = trainList + ",'" + result.getString(1) + "'";
			}
			
			result.close();
			return trainList;
		}
		else{
			// We are not tracking any train on this route
			return "*";
		}
	}

	@Override
	public ResultSet getAvailabilityHistory(String trainNo, String travelClass, int dayDiff) throws SQLException, UnKnownDBError {
		
		Statement stmt = mySQL.createStatement();
		
		String sql = "select t.TrainNo, t.Class, t.TravelDate, sum(l.Cancellations) as Cancellations "
				+ "from (select distinct TrainNo, Class, TravelDate from AvailabilityInfo where "
				+ "TrainNo = '" + trainNo + "' and Class = '" + travelClass + "') as t "
				+ "inner join AvailabilityInfo as l on t.TrainNo = l.TrainNo and t.Class = l.class "
				+ "and t.TravelDate = l.TravelDate and l.LookupDate >= date_sub(t.TravelDate, interval " + dayDiff + " day) "
				+ "and l.LookupDate < t.TravelDate where t.TrainNo = '" + trainNo + "' "
				+ "and t.Class = '" + travelClass + "' group by t.TrainNo, t.Class, t.TravelDate";

		ResultSet result = stmt.executeQuery(sql);
		
		if(!result.next()){
			// We did not find anything !!
			
			// Find list of other trains that run b/w the given stations
			String trainList = getTrainsBetweenStations("", "");

			if (trainList.contentEquals("*")) {
				// Of all Trains
				sql = "select t.TrainNo, t.Class, t.TravelDate, sum(l.Cancellations) as Cancellations "
						+ "from (select distinct TrainNo, Class, TravelDate from AvailabilityInfo where "
						+ "Class = '" + travelClass + "') as t " 
						+ "inner join AvailabilityInfo as l on t.TrainNo = l.TrainNo and t.Class = l.class "
						+ "and t.TravelDate = l.TravelDate and l.LookupDate >= date_sub(t.TravelDate, interval " + dayDiff + " day) " 
						+ "and l.LookupDate < t.TravelDate where t.Class = '" 
						+ travelClass + "' group by t.TrainNo, t.Class, t.TravelDate";
				
			} else {
				
				sql = "select t.TrainNo, t.Class, t.TravelDate, sum(l.Cancellations) as Cancellations "
						+ "from ( select distinct TrainNo, Class, TravelDate from AvailabilityInfo where "
						+ "TrainNo in (" + trainList + "') and Class = '3A') as t "
						+ "inner join AvailabilityInfo as l on t.TrainNo = l.TrainNo and t.Class = l.class "
						+ "and t.TravelDate = l.TravelDate and l.LookupDate >= date_sub(t.TravelDate, interval " + dayDiff + " day) "
						+ "and l.LookupDate < t.TravelDate where t.TrainNo in (" + trainList + ") "
						+ "and t.Class = '3A' group by t.TrainNo, t.Class, t.TravelDate";
				
			}
			
			result = stmt.executeQuery(sql);
			
			if(!result.next()){
				throw new UnKnownDBError();
			}
		}
		
		result.beforeFirst();	// Because we called next!
		return result;
	}

	@Override
	public void validateTrainNo(String trainNo) throws SQLException, InvalidTrainNoException {
		
		Statement stmt = mySQL.createStatement();
		
		String sql = "SELECT * FROM ValidTrains where TrainNo = '" + trainNo + "'";

		ResultSet result = stmt.executeQuery(sql);
		
		if(result.next()){
			// OK Validated
			result.close();
			return;
		}
		else{
			// We are not tracking this train number.
			result.close();
			throw new InvalidTrainNoException(trainNo);
		}
		
	}

	@Override
	public void validateStationCodes(String trainNo, String fromStation,
			String toStation) throws SQLException, InvalidStationCodesException {
		
		// Check that this train runs from given from-station to to-station
		String sql = "select a.* from TrainStops as a inner join TrainStops as b "
				+ "on a.TrainNo = b.TrainNo where a.TrainNo = '" + trainNo + 
				"' and a.StationCode = '" + fromStation + "' and "
						+ "b.StationCode = '" + toStation + "' and a.StopNo < b.StopNo";
		
		Statement stmt = mySQL.createStatement();
		
		ResultSet result = stmt.executeQuery(sql);
		
		if(result.next()){
			// OK Validated
			result.close();
			return;
		}
		else{
			// We are not tracking this train number.
			result.close();
			throw new InvalidStationCodesException(trainNo, fromStation, toStation);
		}
	}

	@Override
	public void saveQueryHistory(JSONObject input, Result result) throws SQLException, ParseException {
		
		String pnr = input.getString("PNR");
		String trainNo = input.getString("TrainNo");
		String travelDate = input.getString("TravelDate");
		String travelClass = input.getString("TravelClass");
		String currentStatus = input.getString("CurrentStatus");
		String fromStation = input.getString("FromStation");
		String toStation = input.getString("ToStation");
		String cnfProb = result.getCNFProbability();
		String racProb = result.getRACProbability();
		
		// Change the Date format from Indian Format to SQL
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
		Date trDate = sdf.parse(travelDate);
		sdf = new SimpleDateFormat("yyyy-MM-dd");
		travelDate = sdf.format(trDate);
		
		Statement st = (Statement) mySQL.createStatement();
		
		String sql = "INSERT INTO QueryHistory "
				+ "(PNR, TrainNo, TravelDate, TravelClass, FromStation, ToStation, "
				+ "CurrentStatus, CNFProbability, RACProbability) "
				+ "VALUES ("
				+ "'" + pnr + "',"
				+ "'" + trainNo + "',"
				+ "'" + travelDate + "',"
				+ "'" + travelClass + "',"
				+ "'" + fromStation + "',"
				+ "'" + toStation + "',"
				+ "'" + currentStatus + "',"
				+ "'" + cnfProb + "',"
				+ "'" + racProb + "'"
				+ ")";

		st.executeUpdate(sql);
		
	}

}
