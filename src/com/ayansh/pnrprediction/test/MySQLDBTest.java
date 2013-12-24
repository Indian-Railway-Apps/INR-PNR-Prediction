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
		db.setUpConnection();

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
	public final void testGetRACQuota() {
		
		try {
			
			assertEquals("3A For known train", 12, db.getRACQuota("12627", "3A"));
			assertEquals("SL For known train", 60, db.getRACQuota("12627", "SL"));
			assertEquals("3A for unknown train", 17, db.getRACQuota("1234", "3A"));
			
		} catch (SQLException | ClassNotSupportedException | UnKnownDBError e) {
			fail("Exception occured" + e.getMessage());
		}
		
	}

	@Test
	public void ClassNotSupportedException() {
		
		try {
			db.getRACQuota("12627", "1A");
			fail();
		} catch (ClassNotSupportedException e) {
		} catch (Exception e) {
			fail();
		}
		
		try {
			db.getRACQuota("12627", "2A");
			fail();
		} catch (ClassNotSupportedException e) {
		} catch (Exception e) {
			fail();
		}
	}
}
