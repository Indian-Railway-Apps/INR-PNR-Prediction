package com.ayansh.pnrprediction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDB implements DBServer {
	
	private Connection mySQL;

	@Override
	public void setUpConnection() throws SQLException {
		
		mySQL = DriverManager.getConnection("jdbc:mysql://localhost/admin_INR", "admin_GUser","PaHxvQ0TJC2L");
		
	}

	@Override
	public void close() throws SQLException {
		mySQL.close();
	}

}
