package com.ayansh.pnrprediction.exception;

public class ClassNotSupportedException extends Exception {

	private static final long serialVersionUID = 1L;

	@Override
    public String getMessage(){
		return "As of now, we support PNR Prediction only on 3A and SL class";
    }
}