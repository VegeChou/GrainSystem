package com.aisino.grain.beans;

public class GetWareHouseInfoRequest extends RequestBean {
	private int WareHouseID;
	private int SearchFlag;
	private int SearchLength;
	private int Page;

	public int getWareHouseID() {
		return WareHouseID;
	}
	public void setWareHouseID(int wareHouseID) {
		WareHouseID = wareHouseID;
	}
	public int getSearchFlag() {
		return SearchFlag;
	}
	public void setSearchFlag(int searchFlag) {
		SearchFlag = searchFlag;
	}
	public int getSearchLength() {
		return SearchLength;
	}
	public void setSearchLength(int searchLength) {
		SearchLength = searchLength;
	}
	public int getPage() {
		return Page;
	}
	public void setPage(int page) {
		Page = page;
	}
}
