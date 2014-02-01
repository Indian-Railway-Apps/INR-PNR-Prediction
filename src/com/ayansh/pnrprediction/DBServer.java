package com.ayansh.pnrprediction;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;

import org.json.JSONObject;

import com.ayansh.pnrprediction.exception.ClassNotSupportedException;
import com.ayansh.pnrprediction.exception.InvalidStationCodesException;
import com.ayansh.pnrprediction.exception.InvalidTrainNoException;
import com.ayansh.pnrprediction.exception.UnKnownDBError;

public interface DBServer {

	public void setUpConnection(String user, String pwd) throws SQLException;

	public void close() throws SQLException;

	public TrainQuota getQuota(String trainNo, String travelClass, String fromStation, String toStation)
			throws SQLException, ClassNotSupportedException, UnKnownDBError;

	public ResultSet getAvailabilityHistory(String trainNo, String travelClass,
			int dayDiff) throws SQLException, UnKnownDBError;

	public void validateTrainNo(String trainNo) throws SQLException, InvalidTrainNoException;

	public void validateStationCodes(String trainNo, String fromStation,
			String toStation) throws SQLException, InvalidStationCodesException;
	
	public String getTrainsBetweenStations(String fromStation, String toStation) throws SQLException;

	public void saveQueryHistory(JSONObject input, Result result) throws SQLException, ParseException;
}
