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
public class SearchTaskingRequest{
	private int PageNumber = 0;
	private int PageSize = 0;
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
	
}
