/**
 * 
 */
package com.ayansh.pnrprediction;

import java.io.IOException;
import java.sql.SQLException;
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
		}
	}
	
	Result calculateProbability(String trainNo, String travelDate,
			String travelClass, String currentStatus) throws SQLException,
			ClassNotSupportedException, UnKnownDBError {

		// Get RAC Quota
		db.getRACQuota(trainNo, travelClass);
		
		return result;

	}
}
