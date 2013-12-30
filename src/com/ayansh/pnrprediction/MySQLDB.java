package com.ayansh.pnrprediction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.ayansh.pnrprediction.exception.ClassNotSupportedException;
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
	public int getRACQuota(String trainNo, String travelClass)
			throws SQLException, ClassNotSupportedException, UnKnownDBError {

		int racQuota = 0;
		
		if (travelClass.contentEquals("3A") || travelClass.contentEquals("SL")) {
			// If the Class of travel is 3A or SL then we are good...
		} else {
			throw new ClassNotSupportedException();
		}
		
		Application app = Application.getInstance();
		
		Statement stmt = mySQL.createStatement();
		
		String sql = "SELECT RACQuota FROM TrainQuota where TrainNo = '"
				+ trainNo + "' and Class = '" + travelClass + "'";

		ResultSet result = stmt.executeQuery(sql);
		
		if(result.next()){
			racQuota = result.getInt(1);
		}
		else{
			// Looks like we are not tracking this train !!
			app.getResultObject().addMessageToLog("We are not tracking this train. We can only calculate approx.");
			
			sql = "SELECT sum(RACQuota) / count(*) FROM TrainQuota where Class = '" + travelClass + "'";

			result = stmt.executeQuery(sql);
			
			if(result.next()){
				racQuota = result.getInt(1);
			}
			else{
				throw new UnKnownDBError();
			}
		}
		
		result.close();
		return racQuota;
	}

	@Override
	public ResultSet getAvailabilityHistory(String trainNo, String travelClass, int dayDiff) throws SQLException, UnKnownDBError {
		
		Statement stmt = mySQL.createStatement();
		
		String sql = "select t.TrainNo, t.Class, t.TravelDate, sum(l.Cancellations) as Cancellations "
				+ "from (select distinct TrainNo, Class, TravelDate from AvailabilityInfo where "
				+ "TrainNo = '" + trainNo + "' and Class = '" + travelClass + "') as t "
				+ "inner join AvailabilityInfo as l on t.TrainNo = l.TrainNo and t.Class = l.class "
				+ "and t.TravelDate = l.TravelDate and l.LookupDate >= (t.TravelDate - " + dayDiff + ") "
				+ "and l.LookupDate < t.TravelDate where t.TrainNo = '" + trainNo + "' "
				+ "and t.Class = '" + travelClass + "' group by t.TrainNo, t.Class, t.TravelDate";

		ResultSet result = stmt.executeQuery(sql);
		
		if(!result.next()){
			// We did not find anything !!
			
			sql = "select t.TrainNo, t.Class, t.TravelDate, sum(l.Cancellations) as Cancellations "
					+ "from (select distinct TrainNo, Class, TravelDate from AvailabilityInfo where "
					+ "Class = '" + travelClass + "') as t " 
					+ "inner join AvailabilityInfo as l on t.TrainNo = l.TrainNo and t.Class = l.class "
					+ "and t.TravelDate = l.TravelDate and l.LookupDate >= (t.TravelDate - "
					+ dayDiff + ") " + "and l.LookupDate < t.TravelDate where t.Class = '" 
					+ travelClass + "' group by t.TrainNo, t.Class, t.TravelDate";
			
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

}
