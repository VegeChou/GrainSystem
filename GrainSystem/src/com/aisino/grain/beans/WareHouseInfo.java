/**
 * 
 */
package com.aisino.grain.beans;

/**
 * @author zwz
 * @date 2013-9-22
 * @description
 *
 */
public class WareHouseInfo{
	private int WareHouseID;
	private String Capacity;
	private int GrainType;
	private String GrainTypeName;
	private String TotalWeight;
	private String GrainOwner;
	private String GrainAttribute;
	
	public int getWareHouseID() {
		return WareHouseID;
	}
	public void setWareHouseID(int wareHouseID) {
		WareHouseID = wareHouseID;
	}
	public String getCapacity() {
		return Capacity;
	}
	public void setCapacity(String capacity) {
		Capacity = capacity;
	}
	public int getGrainType() {
		return GrainType;
	}
	public void setGrainType(int grainType) {
		GrainType = grainType;
	}
	public String getGrainTypeName() {
		return GrainTypeName;
	}
	public void setGrainTypeName(String grainTypeName) {
		GrainTypeName = grainTypeName;
	}
	public String getTotalWeight() {
		return TotalWeight;
	}
	public void setTotalWeight(String totalWeight) {
		TotalWeight = totalWeight;
	}
	public String getGrainOwner() {
		return GrainOwner;
	}
	public void setGrainOwner(String grainOwner) {
		GrainOwner = grainOwner;
	}
	public String getGrainAttribute() {
		return GrainAttribute;
	}
	public void setGrainAttribute(String grainAttribute) {
		GrainAttribute = grainAttribute;
	}
}
