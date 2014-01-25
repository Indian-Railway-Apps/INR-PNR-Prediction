/**
 * 
 */
package com.ayansh.pnrprediction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @author varun
 *
 */
public class Result {

	private String message;
	private int resultCode;
	private float cnfProbability, racProbability;
	private List<String> log;
	
	public Result(){
		log = new ArrayList<String>();
	}
	
	public void setResultCode(int code){
		resultCode = code;
	}
	
	public void setMessage(String message){
		this.message = message;
	}
	
	public void setCNFProbability(float probability){
		cnfProbability = probability;
	}
	
	public String getCNFProbability(){
		return String.valueOf(cnfProbability);
	}
	
	public String getRACProbability(){
		return String.valueOf(racProbability);
	}
	
	public void setRACProbability(float probability){
		racProbability = probability;
	}
	
	public void addMessageToLog(String message){
		log.add(message);
	}
	
	public String JSONify(){
				
		JSONObject result = new JSONObject();
		
		result.put("ResultCode", resultCode);
		result.put("CNFProbability", cnfProbability);
		result.put("RACProbability", racProbability);
		result.put("Message", message);
		
		JSONArray logMessage = new JSONArray();
		
		Iterator<String> i = log.iterator();
		while(i.hasNext()){
			logMessage.put(i.next());
		}
		
		result.put("Log", logMessage);
		
		return result.toString();
		
	}
}