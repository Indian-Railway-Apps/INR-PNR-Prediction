package com.ayansh.pnrprediction.exception;

public class InvalidTrainNoException extends Exception {

	private String trainNo;
	
	private static final long serialVersionUID = 3591059350555199448L;
	
	public InvalidTrainNoException(String tNo){
		trainNo = tNo;
	}

	@Override
    public String getMessage(){
		return "We cannot calculate probability for train no:" + trainNo + ". Please contact the developer";
    }
}