package com.aisino.grain.beans;

public class CheckVehicleWorkResponse {
	private Result Result = null;
	private EnrollInfo ResultEnrollInfo = null;
	private WeighInfo ResultWeighinfo = null;
	public Result getResult() {
		return Result;
	}
	public void setResult(Result result) {
		Result = result;
	}
	public EnrollInfo getResultEnrollInfo() {
		return ResultEnrollInfo;
	}
	public void setResultEnrollInfo(EnrollInfo resultEnrollInfo) {
		ResultEnrollInfo = resultEnrollInfo;
	}
	public WeighInfo getResultWeighinfo() {
		return ResultWeighinfo;
	}
	public void setResultWeighinfo(WeighInfo resultWeighinfo) {
		ResultWeighinfo = resultWeighinfo;
	}
	@Override
	public String toString() {
		return "CheckVehicleWorkResponse [Result=" + Result
				+ ", ResultEnrollInfo=" + ResultEnrollInfo
				+ ", ResultWeighinfo=" + ResultWeighinfo + "]";
	}
	
}
