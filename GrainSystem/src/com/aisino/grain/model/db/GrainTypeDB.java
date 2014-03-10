package com.aisino.grain.model.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "GrainType")

public class GrainTypeDB {
	@DatabaseField (id = true)
	private int grain_type_id = -1;
	@DatabaseField
	private String grain_type_name = null;
	
	public GrainTypeDB() {
		// TODO Auto-generated constructor stub
	}

	public int getGrain_type_id() {
		return grain_type_id;
	}

	public void setGrain_type_id(int grain_type_id) {
		this.grain_type_id = grain_type_id;
	}

	public String getGrain_type_name() {
		return grain_type_name;
	}

	public void setGrain_type_name(String grain_type_name) {
		this.grain_type_name = grain_type_name;
	}
}
