package com.ayansh.pnrprediction.exception;

public class UnKnownDBError extends Exception {

	private static final long serialVersionUID = 3703515218320788167L;

	@Override
    public String getMessage(){
		return "We could not select something from DB. Please dubug.";
    }
}