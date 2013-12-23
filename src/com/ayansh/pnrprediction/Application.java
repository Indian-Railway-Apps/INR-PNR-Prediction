/**
 * 
 */
package com.ayansh.pnrprediction;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;


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
		db = new MySQLDB();
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
}
