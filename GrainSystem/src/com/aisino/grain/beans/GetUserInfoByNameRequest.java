package com.aisino.grain.beans;

public class GetUserInfoByNameRequest extends GetUserInfoRequest {
	private String LoginName = null;	//��¼��
	private String Password = null;		//��¼����
	public String getLoginName() {
		return LoginName;
	}
	public void setLoginName(String loginName) {
		LoginName = loginName;
	}
	public String getPassword() {
		return Password;
	}
	public void setPassword(String password) {
		Password = password;
	}
}
