/**
 * 
 */
package com.aisino.grain.beans;

/**
 * @author zwz
 * @date 2013-10-10
 * @description
 *
 */
public class AdjustDeductedRequest{
	private String VehicleRFIDTag = null;
	private DeductedInfo AdjustDeductedInfo = null;
	public String getVehicleRFIDTag() {
		return VehicleRFIDTag;
	}
	public void setVehicleRFIDTag(String vehicleRFIDTag) {
		VehicleRFIDTag = vehicleRFIDTag;
	}
	public DeductedInfo getAdjustDeductedInfo() {
		return AdjustDeductedInfo;
	}
	public void setAdjustDeductedInfo(DeductedInfo adjustDeductedInfo) {
		AdjustDeductedInfo = adjustDeductedInfo;
	}
	
}
