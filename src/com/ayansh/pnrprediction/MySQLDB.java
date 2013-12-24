package com.ayansh.pnrprediction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.ayansh.pnrprediction.exception.ClassNotSupportedException;
import com.ayansh.pnrprediction.exception.UnKnownDBError;

public class MySQLDB implements DBServer {
	
	private Connection mySQL;
	private String dbURL;
	
	public MySQLDB(String dbURL){
		this.dbURL = dbURL;
	}
	
	@Override
	public void setUpConnection() throws SQLException {
		
		mySQL = DriverManager.getConnection(dbURL, "root","");
		
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
			return result.getInt(0);
		}
		else{
			// Looks like we are not tracking this train !!
			app.getResultObject().addMessageToLog("We are not tracking this train. We can only calculate approx.");
			
			sql = "SELECT sum(RACQuota) / count(*) FROM TrainQuota where Class = '" + travelClass + "'";

			result = stmt.executeQuery(sql);
			
			if(result.next()){
				return result.getInt(0);
			}
			else{
				throw new UnKnownDBError();
			}
		}
	
	}

}
