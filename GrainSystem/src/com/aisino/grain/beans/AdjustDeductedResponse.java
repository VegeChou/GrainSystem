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
public class AdjustDeductedResponse extends ResponseBean {
	private boolean ResponseResult = false;
	private String FailedReason = null;
	public boolean getResponseResult() {
		return ResponseResult;
	}
	public void setResponseResult(boolean responseResult) {
		ResponseResult = responseResult;
	}
	public String getFailedReason() {
		return FailedReason;
	}
	public void setFailedReason(String failedReason) {
		FailedReason = failedReason;
	}
	
}
