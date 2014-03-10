package com.aisino.grain.beans;

public class RegisterSampleResponse extends ResponseBean {
	private boolean ResponseResult = false;
	private String SampleNumber = null;
	public boolean getResponseResult() {
		return ResponseResult;
	}
	public void setResponseResult(boolean responseResult) {
		ResponseResult = responseResult;
	}
	public String getSampleNumber() {
		return SampleNumber;
	}
	public void setSampleNumber(String sampleNumber) {
		SampleNumber = sampleNumber;
	}
	
}
