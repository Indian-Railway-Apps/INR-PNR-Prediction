/**
 * 
 */
package com.ayansh.pnrprediction;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import com.ayansh.pnrprediction.exception.ClassNotSupportedException;
import com.ayansh.pnrprediction.exception.InvalidStationCodesException;
import com.ayansh.pnrprediction.exception.InvalidTrainNoException;
import com.ayansh.pnrprediction.exception.UnKnownDBError;


/**
 * @author varun
 *
 */
public class Main {

	/**
	 * @param args
	 */	
	public static void main(String[] args) {
		
		// Create Application Instance
		Application app = Application.getInstance();
		
		try {
			
			app.initializeApplication();
			
		} catch (IOException e) {
			app.getResultObject().setResultCode(1);
			app.getResultObject().setMessage(e.getMessage());
			finish();
		} catch (SQLException e) {
			app.getResultObject().setResultCode(4);
			app.getResultObject().setMessage(e.getMessage());
			finish();
		}

		if (args.length <= 0) {
			// No Input !!
			app.getResultObject().setResultCode(2);
			app.getResultObject().setMessage("No Input !");
			finish();
		}
		
		if (args.length != 1) {
			// Wrong Input !!
			app.getResultObject().setResultCode(3);
			app.getResultObject().setMessage("Wrong arguments !");
			finish();
		}
		
		JSONObject input = new JSONObject();
		String trainNo = null, travelDate = null;
		String travelClass = null, currentStatus = null;
		String fromStation = null, toStation = null;
		
		try{
			
			// Parse the JSON Input
			input = new JSONObject(args[0]);
			
			trainNo = input.getString("TrainNo");
			travelDate = input.getString("TravelDate");
			travelClass = input.getString("TravelClass");
			currentStatus = input.getString("CurrentStatus");
			fromStation = input.getString("FromStation");
			toStation = input.getString("ToStation");
			
		}catch (JSONException e){
			
			// Wrong Input !!
			app.getResultObject().setResultCode(4);
			app.getResultObject().setMessage("Bad JSON !");
			finish();
		}
		
		// Validate if we have the required input
		if (trainNo == null || travelDate == null || travelClass == null
				|| currentStatus == null || fromStation == null
				|| toStation == null) {

			// Wrong Input !!
			app.getResultObject().setResultCode(5);
			app.getResultObject().setMessage("Mandatory fields not populated !");
			finish();
		}

		// Input seems to be correct. so we can calculate probability
		try {
			
			app.calculateProbability(input);
			
		} catch (SQLException | ClassNotSupportedException | UnKnownDBError | ParseException | InvalidTrainNoException | InvalidStationCodesException e) {
			
			app.getResultObject().setResultCode(99);
			app.getResultObject().setMessage(e.getMessage());
		}
		
		finish();
	}
	
	private static void finish(){
		
		Application app = Application.getInstance();
		
		app.close();
		
		System.out.println(app.getResultObject().JSONify());
		
		System.exit(0);
		
	}

}