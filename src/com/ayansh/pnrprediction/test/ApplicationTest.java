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
import com.ayansh.pnrprediction.exception.InvalidTrainNoException;
import com.ayansh.pnrprediction.exception.UnKnownDBError;

/**
 * @author I041474
 *
 */
public class ApplicationTest {

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
			Application.getInstance().calculateProbability("12627", "", "1A", "");
			fail();
		} catch (ClassNotSupportedException e) {
		} catch (Exception e) {
			fail();
		}
		
		try {
			Application.getInstance().calculateProbability("12627", "", "2A", "");
			fail();
		} catch (ClassNotSupportedException e) {
		} catch (Exception e) {
			fail();
		}
	}

	@Test
	public void TrainNotSupportedException() {
		
		try {
			Application.getInstance().calculateProbability("XXXXX", "", "1A", "");
			fail();
		} catch (InvalidTrainNoException e) {
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
			
			result = Application.getInstance().calculateProbability("12627", "31-12-2013", "3A", "GNWL102/WL12");
			r = new JSONObject(result.JSONify());
			assertEquals("Calculate Prob Test", 0, r.getInt("ResultCode"));
			
			result = Application.getInstance().calculateProbability("12627", "31-12-2013", "SL", "GNWL102/WL12");
			r = new JSONObject(result.JSONify());
			assertEquals("Calculate Prob Test", 0, r.getInt("ResultCode"));
			
		} catch (SQLException
				| com.ayansh.pnrprediction.exception.ClassNotSupportedException
				| UnKnownDBError | ParseException | InvalidTrainNoException e) {
			
			fail("Exception occured: " + e.getMessage());
			
		}
		
	}
	
}