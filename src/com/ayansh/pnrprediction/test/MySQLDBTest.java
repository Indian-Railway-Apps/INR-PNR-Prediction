/**
 * 
 */
package com.ayansh.pnrprediction.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.ayansh.pnrprediction.MySQLDB;
import com.ayansh.pnrprediction.exception.ClassNotSupportedException;
import com.ayansh.pnrprediction.exception.UnKnownDBError;

/**
 * @author I041474
 *
 */
public class MySQLDBTest {

	private MySQLDB db;
	
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
		
		db = new MySQLDB("jdbc:mysql://localhost/INR");
		db.setUpConnection("root","mastram");

	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		db.close();
	}

	/**
	 * Test method for {@link com.ayansh.pnrprediction.MySQLDB#getRACQuota(java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testGetQuota() {
		
		try {
			
			// Test RAC Quota
			assertEquals("3A For known train", 12, db.getQuota("12627", "3A", "SBC", "NDLS").getRacQuota());
			assertEquals("SL For known train", 60, db.getQuota("12627", "SL", "SBC", "NDLS").getRacQuota());
			assertEquals("3A for unknown train", 12, db.getQuota("1234", "3A", "SBC", "NDLS").getRacQuota());
			
			// Test EQ Quota
			assertEquals("3A For known train", 6, db.getQuota("12627", "3A", "SBC", "NDLS").getEmergencyQuota());
			assertEquals("SL For known train", 20, db.getQuota("12627", "SL", "SBC", "NDLS").getEmergencyQuota());
			assertEquals("3A for unknown train", 6, db.getQuota("1234", "3A", "SBC", "NDLS").getEmergencyQuota());
			
		} catch (SQLException | ClassNotSupportedException | UnKnownDBError e) {
			fail("Exception occured" + e.getMessage());
		}
		
	}

	@Test
	public void ClassNotSupportedException() {
		
		try {
			db.getQuota("12627", "1A", "SBC", "NDLS");
			fail();
		} catch (ClassNotSupportedException e) {
		} catch (Exception e) {
			fail();
		}
		
		try {
			db.getQuota("12627", "2A", "SBC", "NDLS");
			fail();
		} catch (ClassNotSupportedException e) {
		} catch (Exception e) {
			fail();
		}
	}
}
