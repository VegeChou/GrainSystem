package com.aisino.grain.beans;

public class GetUserInfoByRFIDRequest extends GetUserInfoRequest {
	private String UserRFID = null;		//�û�RFID����
	public String getUserRFID() {
		return UserRFID;
	}
	public void setUserRFID(String userRFID) {
		UserRFID = userRFID;
	}
}
