package com.ayansh.pnrprediction.exception;

public class InvalidStationCodesException extends Exception {

	private static final long serialVersionUID = 6702148180054069852L;
	private String trainNo, fromStation, toStation;
		
	public InvalidStationCodesException(String tNo, String fs, String ts){
		trainNo = tNo;
		fromStation = fs;
		toStation = ts;
	}

	@Override
    public String getMessage(){
		return "Given combination of Train No: " + trainNo + ", From Station: " + fromStation +
				", To Station:" + toStation + " is invalid";
    }
}