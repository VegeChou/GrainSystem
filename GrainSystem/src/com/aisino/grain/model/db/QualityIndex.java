package com.aisino.grain.model.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "QualityIndex")

public class QualityIndex {
	@DatabaseField (id = true)
	private int quality_index_id = -1;
	@DatabaseField
	private String quality_index_name = null;
	
	public QualityIndex() {
		// TODO Auto-generated constructor stub
	}

	public int getQuality_index_id() {
		return quality_index_id;
	}

	public void setQuality_index_id(int quality_index_id) {
		this.quality_index_id = quality_index_id;
	}

	public String getQuality_index_name() {
		return quality_index_name;
	}

	public void setQuality_index_name(String quality_index_name) {
		this.quality_index_name = quality_index_name;
	}
}
