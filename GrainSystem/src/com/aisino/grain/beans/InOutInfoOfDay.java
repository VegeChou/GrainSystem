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
public class InOutInfoOfDay{
	private String WorkDate;
	private int NetWeight;
	private int Deducted;
	
	public String getWorkDate() {
		return WorkDate;
	}
	public void setWorkDate(String workDate) {
		WorkDate = workDate;
	}
	public int getNetWeight() {
		return NetWeight;
	}
	public void setNetWeight(int netWeight) {
		NetWeight = netWeight;
	}
	public int getDeducted() {
		return Deducted;
	}
	public void setDeducted(int deducted) {
		Deducted = deducted;
	}
}
