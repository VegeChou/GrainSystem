package com.aisino.grain.model.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Warehouse")

public class Warehouse {
	@DatabaseField (id = true)
	private int warehouse_id = -1;
	@DatabaseField
	private String warehouse_name = null;
	
	public Warehouse() {
		// TODO Auto-generated constructor stub
	}

	public int getWarehouse_id() {
		return warehouse_id;
	}

	public void setWarehouse_id(int warehouse_id) {
		this.warehouse_id = warehouse_id;
	}

	public String getWarehouse_name() {
		return warehouse_name;
	}

	public void setWarehouse_name(String warehouse_name) {
		this.warehouse_name = warehouse_name;
	}
}
