/**
 * 
 */
package com.ayansh.pnrprediction;

/**
 * @author varun
 *
 */
public class TrainQuota {

	private int racQuota, emergencyQuota;
	private String travelClass;
	
	public TrainQuota(){
		
		setRacQuota(setEmergencyQuota(0));
		setTravelClass("");
		
	}

	public int getRacQuota() {
		return racQuota;
	}

	public void setRacQuota(int racQuota) {
		this.racQuota = racQuota;
	}

	public int getEmergencyQuota() {
		return emergencyQuota;
	}

	public int setEmergencyQuota(int emergencyQuota) {
		this.emergencyQuota = emergencyQuota;
		return emergencyQuota;
	}

	public String getTravelClass() {
		return travelClass;
	}

	public void setTravelClass(String travelClass) {
		this.travelClass = travelClass;
	}
	
}