package com.ayansh.pnrprediction;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.ayansh.pnrprediction.exception.ClassNotSupportedException;
import com.ayansh.pnrprediction.exception.InvalidTrainNoException;
import com.ayansh.pnrprediction.exception.UnKnownDBError;

public interface DBServer {

	public void setUpConnection(String user, String pwd) throws SQLException;

	public void close() throws SQLException;

	public int getRACQuota(String trainNo, String travelClass)
			throws SQLException, ClassNotSupportedException, UnKnownDBError;

	public ResultSet getAvailabilityHistory(String trainNo, String travelClass,
			int dayDiff) throws SQLException, UnKnownDBError;

	public void validateTrainNo(String trainNo) throws SQLException, InvalidTrainNoException;
}
