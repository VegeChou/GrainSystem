package com.aisino.grain.model.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "BusinessType")

public class BusinessType {
	@DatabaseField (id = true)
	private int business_type_id = -1;
	@DatabaseField
	private String business_type_name = null;
	
	public BusinessType() {
		// TODO Auto-generated constructor stub
	}

	public int getBusiness_type_id() {
		return business_type_id;
	}

	public void setBusiness_type_id(int business_type_id) {
		this.business_type_id = business_type_id;
	}

	public String getBusiness_type_name() {
		return business_type_name;
	}

	public void setBusiness_type_name(String business_type_name) {
		this.business_type_name = business_type_name;
	}
}
