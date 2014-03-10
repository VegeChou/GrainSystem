package com.aisino.grain.beans;

import java.util.List;

public class UserLoginInfoResponse extends ResponseBean{
	private List<UserLoginInfo> UserLoginInfoList = null;

	public List<UserLoginInfo> getUserLoginInfoList() {
		return UserLoginInfoList;
	}

	public void setUserLoginInfoList(List<UserLoginInfo> userLoginInfoList) {
		UserLoginInfoList = userLoginInfoList;
	}
	
}
