package com.aisino.grain.beans;

public class UserLoginInfo {
	private int UserID = 0;				//用户ID
	private String LoginName = null;	//登录名
	private String Password = null;		//登录密码
	private String UserName = null;		//用户名称
	private String UserRight = null;	//用户权限，多个权限以逗号（，）隔开
	private String UserRFID = null;		//用户RFID卡号
	private int UserRFIDState = 0;		//卡状态：0未激活，1已激活，2已注销
	private String WareHouse = null;	//仓库ID，多个仓库以逗号（，）隔开
	
	public int getUserID() {
		return UserID;
	}
	public void setUserID(int userID) {
		UserID = userID;
	}
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
	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
	public String getUserRight() {
		return UserRight;
	}
	public void setUserRight(String userRight) {
		UserRight = userRight;
	}
	public String getUserRFID() {
		return UserRFID;
	}
	public void setUserRFID(String userRFID) {
		UserRFID = userRFID;
	}
	public int getUserRFIDState() {
		return UserRFIDState;
	}
	public void setUserRFIDState(int userRFIDState) {
		UserRFIDState = userRFIDState;
	}
	public String getWareHouse() {
		return WareHouse;
	}
	public void setWareHouse(String wareHouse) {
		WareHouse = wareHouse;
	}
}
