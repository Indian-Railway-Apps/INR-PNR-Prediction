/**
 * 
 */
package com.ayansh.pnrprediction;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;

import com.ayansh.pnrprediction.exception.ClassNotSupportedException;
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
		
		if (args.length != 4) {
			// Wrong Input !!
			app.getResultObject().setResultCode(3);
			app.getResultObject().setMessage("Wrong Input !");
			finish();
		}
		
		// Input seems to be correct. so we can calculate probability
		String trainNo = args[0];
		String travelDate = args[1];
		String travelClass = args[2];
		String currentStatus = args[3];
		
		try {
			
			app.calculateProbability(trainNo, travelDate, travelClass, currentStatus);
			
		} catch (SQLException | ClassNotSupportedException | UnKnownDBError | ParseException | InvalidTrainNoException e) {
			
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
