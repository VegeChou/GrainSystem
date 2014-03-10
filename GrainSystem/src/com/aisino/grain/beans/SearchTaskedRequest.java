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
public class SearchTaskedRequest{
	private int UserID = 0;
	private int PageNumber = 0;
	private int PageSize = 0;
	private String VehiclePlate = null;
	public int getUserID() {
		return UserID;
	}
	public void setUserID(int userID) {
		UserID = userID;
	}
	public int getPageNumber() {
		return PageNumber;
	}
	public void setPageNumber(int pageNumber) {
		PageNumber = pageNumber;
	}
	public int getPageSize() {
		return PageSize;
	}
	public void setPageSize(int pageSize) {
		PageSize = pageSize;
	}
	public String getVehiclePlate() {
		return VehiclePlate;
	}
	public void setVehiclePlate(String vehiclePlate) {
		VehiclePlate = vehiclePlate;
	}
	
}
