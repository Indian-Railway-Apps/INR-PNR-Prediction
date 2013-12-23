/**
 * 
 */
package com.ayansh.pnrprediction;

import org.json.JSONObject;

/**
 * @author varun
 *
 */
public class Result {

	private String message;
	private int resultCode;
	private String cnfProbability, racProbability;
	
	public void setResultCode(int code){
		resultCode = code;
	}
	
	public void setMessage(String message){
		this.message = message;
	}
	
	public void setCNFProbability(String probability){
		cnfProbability = probability;
	}
	
	public void setRACProbability(String probability){
		racProbability = probability;
	}
	
	public String JSONify(){
				
		JSONObject result = new JSONObject();
		
		result.put("ResultCode", resultCode);
		result.put("CNFProbability", cnfProbability);
		result.put("RACProbability", racProbability);
		result.put("Message", message);
		
		return result.toString();
		
	}
}