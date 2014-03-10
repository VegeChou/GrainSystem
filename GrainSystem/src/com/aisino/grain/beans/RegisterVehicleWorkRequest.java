package com.aisino.grain.beans;

public class RegisterVehicleWorkRequest extends RequestBean{
	private String VehicleRFIDTag = null;
	private int WareHouseID = 0;
	private int WorkNode = 0;
	private String WorkNumber = null; 
	private int BusinessType = 0; 
	private int UserID = 0;
	public String getVehicleRFIDTag() {
		return VehicleRFIDTag;
	}
	public void setVehicleRFIDTag(String vehicleRFIDTag) {
		VehicleRFIDTag = vehicleRFIDTag;
	}
	public int getWareHouseID() {
		return WareHouseID;
	}
	public void setWareHouseID(int wareHouseID) {
		WareHouseID = wareHouseID;
	}
	public int getWorkNode() {
		return WorkNode;
	}
	public void setWorkNode(int workNode) {
		WorkNode = workNode;
	}
	public String getWorkNumber() {
		return WorkNumber;
	}
	public void setWorkNumber(String workNumber) {
		WorkNumber = workNumber;
	}
	public int getBusinessType() {
		return BusinessType;
	}
	public void setBusinessType(int businessType) {
		BusinessType = businessType;
	}
	public int getUserID() {
		return UserID;
	}
	public void setUserID(int userID) {
		UserID = userID;
	}
	
	
}
