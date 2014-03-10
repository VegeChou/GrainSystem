/**
 * 
 */
package com.aisino.grain.beans;

import java.util.List;

/**
 * @author zwz
 * @date 2013-9-22
 * @description
 *
 */
public class WareHouseInfoResponse extends ResponseBean{
	private WareHouseInfo ResultWareHouseInfo;
	private List<InOutInfoOfDay> InOutInfoOfDayList;
	
	public WareHouseInfo getResultWareHouseInfo() {
		return ResultWareHouseInfo;
	}
	public void setResultWareHouseInfo(WareHouseInfo resultWareHouseInfo) {
		ResultWareHouseInfo = resultWareHouseInfo;
	}
	public List<InOutInfoOfDay> getInOutInfoOfDayList() {
		return InOutInfoOfDayList;
	}
	public void setInOutInfoOfDayList(List<InOutInfoOfDay> inOutInfoOfDayList) {
		InOutInfoOfDayList = inOutInfoOfDayList;
	}
}
