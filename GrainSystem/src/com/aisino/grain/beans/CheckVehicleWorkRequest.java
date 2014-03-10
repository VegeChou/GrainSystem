package com.aisino.grain.beans;

public class CheckVehicleWorkRequest extends RequestBean{
	private String VehicleRFIDTag = null;	//±Í«©ID
	private int WareHouseID = 0;			//≤÷ø‚ID
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
}
