package com.aisino.grain.beans;

public class GetDecutedInfoRequest extends RequestBean	 {
	private String VehicleRFIDTag = null;
	
	public String getVehicleRFIDTag() {
		return VehicleRFIDTag;
	}
	
	public void setVehicleRFIDTag(String VehicleRFIDTag) {
		this.VehicleRFIDTag = VehicleRFIDTag;
	}
}
