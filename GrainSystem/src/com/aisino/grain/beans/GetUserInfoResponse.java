package com.aisino.grain.beans;

public class GetUserInfoResponse extends ResponseBean{
	
	private boolean ValidResult = false;
	private String InvalidReason = null;
	private ResultUserLoginInfo ResultUserLoginInfo = null;
	
	public boolean getValidResult() {
		return ValidResult;
	}
	public void setValidResult(boolean validResult) {
		ValidResult = validResult;
	}
	public String getInvalidReason() {
		return InvalidReason;
	}
	public void setInvalidReason(String invalidReason) {
		InvalidReason = invalidReason;
	}
	public ResultUserLoginInfo getResultUserLoginInfo() {
		return ResultUserLoginInfo;
	}
	public void setUserLoginInfo(ResultUserLoginInfo ResultUserLoginInfo) {
		this.ResultUserLoginInfo = ResultUserLoginInfo;
	}
}
