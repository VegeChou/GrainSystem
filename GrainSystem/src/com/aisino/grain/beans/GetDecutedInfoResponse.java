/**
 * 
 */
package com.aisino.grain.beans;

import java.util.List;

/**
 * @author zwz
 * @date 2013-10-10
 * @description
 *
 */
public class GetDecutedInfoResponse extends ResponseBean {
	private DeductedInfo AssayDeductedInfo = null;
	private DeductedInfo AdjustDeductedInfo = null;
	private List<QualityIndexResultInfo> ResultQualityInfos = null;
	public DeductedInfo getAssayDeductedInfo() {
		return AssayDeductedInfo;
	}
	public void setAssayDeductedInfo(DeductedInfo assayDeductedInfo) {
		AssayDeductedInfo = assayDeductedInfo;
	}
	public DeductedInfo getAdjustDeductedInfo() {
		return AdjustDeductedInfo;
	}
	public void setAdjustDeductedInfo(DeductedInfo adjustDeductedInfo) {
		AdjustDeductedInfo = adjustDeductedInfo;
	}
	public List<QualityIndexResultInfo> getResultQualityInfos() {
		return ResultQualityInfos;
	}
	public void setResultQualityInfos(
			List<QualityIndexResultInfo> resultQualityInfos) {
		ResultQualityInfos = resultQualityInfos;
	}
	
	
}
