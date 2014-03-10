package com.aisino.grain.beans;

public class RegisterVehicleWorkResponse extends ResponseBean {
	private boolean ResponseResult = false;
	private String FailedReason = null;
	public boolean getResponseResult() {
		return ResponseResult;
	}
	public void setResponseResult(boolean responseResult) {
		ResponseResult = responseResult;
	}
	public String getFailedReason() {
		return FailedReason;
	}
	public void setFailedReason(String failedReason) {
		FailedReason = failedReason;
	}
}
