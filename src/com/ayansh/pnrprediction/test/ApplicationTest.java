/**
 * 
 */
package com.ayansh.pnrprediction.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.text.ParseException;

import org.json.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ayansh.pnrprediction.Application;
import com.ayansh.pnrprediction.Result;
import com.ayansh.pnrprediction.exception.ClassNotSupportedException;
import com.ayansh.pnrprediction.exception.InvalidStationCodesException;
import com.ayansh.pnrprediction.exception.InvalidTrainNoException;
import com.ayansh.pnrprediction.exception.UnKnownDBError;

/**
 * @author I041474
 *
 */
public class ApplicationTest {
	
	private JSONObject input;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		// Set up.
		Application app = Application.getInstance();
		app.initializeApplication();
		
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		
		// Finish Testing.
		Application.getInstance().close();
		
	}

	@Test
	public void ClassNotSupportedException() {
		
		try {
			input = new JSONObject();
			
			input.put("TrainNo", "12627");
			input.put("TravelDate", "10-10-2014");
			input.put("CurrentStatus", "GNWL30/WL10");
			input.put("FromStation", "SBC");
			input.put("ToStation", "NDLS");
			input.put("TravelClass", "1A");
			Application.getInstance().calculateProbability(input);
			fail();
		} catch (ClassNotSupportedException e) {
		} catch (Exception e) {
			fail();
		}
		
		try {
			input = new JSONObject();
			
			input.put("TrainNo", "12627");
			input.put("TravelDate", "10-10-2014");
			input.put("TravelClass", "2A");
			input.put("CurrentStatus", "GNWL30/WL10");
			input.put("FromStation", "SBC");
			input.put("ToStation", "NDLS");
			Application.getInstance().calculateProbability(input);
			fail();
		} catch (ClassNotSupportedException e) {
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void TrainNotSupportedException() {
		
		try {
			input = new JSONObject();
			
			input.put("TravelDate", "10-10-2014");
			input.put("TravelClass", "3A");
			input.put("CurrentStatus", "GNWL30/WL10");
			input.put("FromStation", "SBC");
			input.put("ToStation", "NDLS");
			input.put("TrainNo", "XXXXX");
			Application.getInstance().calculateProbability(input);
			fail();
		} catch (InvalidTrainNoException e) {
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test
	public void InvalidStationCodeException() {
		
		input = new JSONObject();
		
		input.put("TravelDate", "10-10-2014");
		input.put("TravelClass", "3A");
		input.put("CurrentStatus", "GNWL30/WL10");
		input.put("FromStation", "XXX");
		input.put("ToStation", "YYY");
		input.put("TrainNo", "12627");
		
		try {
			Application.getInstance().calculateProbability(input);
			fail();
		} catch (InvalidStationCodesException e) {
		} catch (Exception e) {
			fail();
		}
		
		try {
			input.put("FromStation", "NDLS");
			input.put("ToStation", "SBC");
			Application.getInstance().calculateProbability(input);
			fail();
		} catch (InvalidStationCodesException e) {
		} catch (Exception e) {
			fail();
		}
		
	}
	
	/**
	 * Test method for {@link com.ayansh.pnrprediction.Application#calculateProbability(java.lang.String, java.lang.String, java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testCalculateProbability() {
		
		try {
			
			Result result;
			JSONObject r;
			
			input = new JSONObject();
			
			input.put("TrainNo", "12627");
			input.put("TravelDate", "10-10-2014");
			input.put("TravelClass", "3A");
			input.put("CurrentStatus", "GNWL30/WL10");
			input.put("FromStation", "SBC");
			input.put("ToStation", "NDLS");
			
			result = Application.getInstance().calculateProbability(input);
			r = new JSONObject(result.JSONify());
			assertEquals("Calculate Prob Test", 0, r.getInt("ResultCode"));
			
			input.put("TravelClass", "SL");
			result = Application.getInstance().calculateProbability(input);
			r = new JSONObject(result.JSONify());
			assertEquals("Calculate Prob Test", 0, r.getInt("ResultCode"));
			
		} catch (SQLException
				| com.ayansh.pnrprediction.exception.ClassNotSupportedException
				| UnKnownDBError | ParseException | InvalidTrainNoException | InvalidStationCodesException e) {
			
			fail("Exception occured: " + e.getMessage());
			
		}
		
	}
	
}