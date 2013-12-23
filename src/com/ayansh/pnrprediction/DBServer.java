package com.ayansh.pnrprediction;

import java.sql.SQLException;

public interface DBServer {
	
	public void setUpConnection() throws SQLException;

	public void close() throws SQLException;
	
}
