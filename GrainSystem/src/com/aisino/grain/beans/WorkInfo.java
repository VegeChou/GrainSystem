package com.aisino.grain.beans;

public class WorkInfo {
	private String WorkNumber = null;
	private String WorkPlace = null;
	private int WorkNode = 0;
	private String WarehouseID = null;
	private int ApprovedWeight = 0;
	private int CompletedWeight = 0;
	private int WarnFlag = 0;
	private int BusinessType = 0;
	private double Price = 0;
	public String getWorkNumber() {
		return WorkNumber;
	}
	public void setWorkNumber(String workNumber) {
		WorkNumber = workNumber;
	}
	public String getWorkPlace() {
		return WorkPlace;
	}
	public void setWorkPlace(String workPlace) {
		WorkPlace = workPlace;
	}
	public int getWorkNode() {
		return WorkNode;
	}
	public void setWorkNode(int workNode) {
		WorkNode = workNode;
	}
	public String getWarehouseID() {
		return WarehouseID;
	}
	public void setWarehouseID(String warehouseID) {
		WarehouseID = warehouseID;
	}
	public int getApprovedWeight() {
		return ApprovedWeight;
	}
	public void setApprovedWeight(int approvedWeight) {
		ApprovedWeight = approvedWeight;
	}
	public int getCompletedWeight() {
		return CompletedWeight;
	}
	public void setCompletedWeight(int completedWeight) {
		CompletedWeight = completedWeight;
	}
	public int getWarnFlag() {
		return WarnFlag;
	}
	public void setWarnFlag(int warnFlag) {
		WarnFlag = warnFlag;
	}
	public int getBusinessType() {
		return BusinessType;
	}
	public void setBusinessType(int businessType) {
		BusinessType = businessType;
	}
	public double getPrice() {
		return Price;
	}
	public void setPrice(double price) {
		Price = price;
	}
	
}
