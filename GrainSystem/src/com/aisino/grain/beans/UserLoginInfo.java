package com.aisino.grain.beans;

public class UserLoginInfo {
	private int UserID = 0;				//�û�ID
	private String LoginName = null;	//��¼��
	private String Password = null;		//��¼����
	private String UserName = null;		//�û�����
	private String UserRight = null;	//�û�Ȩ�ޣ����Ȩ���Զ��ţ���������
	private String UserRFID = null;		//�û�RFID����
	private int UserRFIDState = 0;		//��״̬��0δ���1�Ѽ��2��ע��
	private String WareHouse = null;	//�ֿ�ID������ֿ��Զ��ţ���������
	
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
