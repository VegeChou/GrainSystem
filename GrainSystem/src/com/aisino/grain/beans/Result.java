package com.aisino.grain.beans;

public class Result {
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
	@Override
	public String toString() {
		return "Result [ResponseResult=" + ResponseResult + ", FailedReason="
				+ FailedReason + "]";
	}
	
}
