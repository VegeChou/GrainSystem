package com.aisino.grain.beans;

public class ApkInfo {
	private String packageName;
	private String apkName;
	private int versionCode;
	private String versionName;
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getApkName() {
		return apkName;
	}
	public void setApkName(String apkName) {
		this.apkName = apkName;
	}
	public int getVersionCode() {
		return versionCode;
	}
	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}
	public String getVersionName() {
		return versionName;
	}
	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}
	@Override
	public String toString() {
		return "ApkInfo [packageName=" + packageName + ", apkName=" + apkName
				+ ", versionCode=" + versionCode + ", versionName="
				+ versionName + "]";
	}
	
	
}
