package com.aisino.grain.beans;

public class GetUserInfoByRFIDRequest extends GetUserInfoRequest {
	private String UserRFID = null;		//ÓÃ»§RFID¿¨ºÅ
	public String getUserRFID() {
		return UserRFID;
	}
	public void setUserRFID(String userRFID) {
		UserRFID = userRFID;
	}
}
