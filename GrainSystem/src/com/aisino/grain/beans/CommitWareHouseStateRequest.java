package com.aisino.grain.beans;

public class CommitWareHouseStateRequest extends RequestBean{
	private int WareHouseID = 0;
	private int WareHouseState = 0;
	public int getWareHouseID() {
		return WareHouseID;
	}
	public void setWareHouseID(int wareHouseID) {
		WareHouseID = wareHouseID;
	}
	public int getWareHouseState() {
		return WareHouseState;
	}
	public void setWareHouseState(int wareHouseState) {
		WareHouseState = wareHouseState;
	}
}
