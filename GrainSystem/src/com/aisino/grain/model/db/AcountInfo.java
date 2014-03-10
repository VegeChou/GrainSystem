package com.aisino.grain.model.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "AcountInfo")

public class AcountInfo {
	@DatabaseField (id = true)
	private String acount_name = null;
	@DatabaseField
	private String password = null;
	@DatabaseField
	private String tag_id = null;
	@DatabaseField
	private int activate_flag = -1;
	@DatabaseField
	private String operation_code = null;
	@DatabaseField
	private String wahouse_id = null;
	@DatabaseField
	private int user_id = -1;
	
	public AcountInfo() {
		// TODO Auto-generated constructor stub
	}

	public String getAcount_name() {
		return acount_name;
	}

	public void setAcount_name(String acount_name) {
		this.acount_name = acount_name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getTag_id() {
		return tag_id;
	}

	public void setTag_id(String tag_id) {
		this.tag_id = tag_id;
	}

	public int getActivate_flag() {
		return activate_flag;
	}

	public void setActivate_flag(int activate_flag) {
		this.activate_flag = activate_flag;
	}

	public String getOperation_code() {
		return operation_code;
	}

	public void setOperation_code(String operation_code) {
		this.operation_code = operation_code;
	}

	public String getWahouse_id() {
		return wahouse_id;
	}

	public void setWahouse_id(String wahouse_id) {
		this.wahouse_id = wahouse_id;
	}

	public int getUser_id() {
		return user_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
}
