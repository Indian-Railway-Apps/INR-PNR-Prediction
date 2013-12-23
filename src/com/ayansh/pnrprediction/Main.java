/**
 * 
 */
package com.ayansh.pnrprediction;

import java.io.IOException;
import java.sql.SQLException;


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
		
		if (args.length < 4) {
			// Wrong Input !!
			app.getResultObject().setResultCode(3);
			app.getResultObject().setMessage("Wrong Input !");
			finish();
		}
		
	}
	
	private static void finish(){
		
		Application app = Application.getInstance();
		
		app.close();
		
		System.out.println(app.getResultObject().JSONify());
		
	}

}
