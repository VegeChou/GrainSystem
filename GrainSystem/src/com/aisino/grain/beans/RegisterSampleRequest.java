package com.aisino.grain.beans;

public class RegisterSampleRequest extends RequestBean {
	private int UserID = 0;
	private String TaskNumber = null;
	private int SampleCount = 0;
	public int getUserID() {
		return UserID;
	}
	public void setUserID(int userID) {
		UserID = userID;
	}
	public String getTaskNumber() {
		return TaskNumber;
	}
	public void setTaskNumber(String taskNumber) {
		TaskNumber = taskNumber;
	}
	public int getSampleCount() {
		return SampleCount;
	}
	public void setSampleCount(int sampleCount) {
		SampleCount = sampleCount;
	}
	
}
