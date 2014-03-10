package com.aisino.grain.model.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "SystenConfig")

public class SystenConfig {
	@DatabaseField (id = true)
	private String configKey = null;
	@DatabaseField
	private String configValue = null;
	@DatabaseField
	private String Description = null;
	
	public SystenConfig() {
		// TODO Auto-generated constructor stub
	}

	public String getConfigKey() {
		return configKey;
	}

	public void setConfigKey(String configKey) {
		this.configKey = configKey;
	}
	
	public String getConfigValue() {
		return configValue;
	}
	
	public void setConfigValue(String configValue) {
		this.configValue = configValue;
	}

	public String getDescription() {
		return Description;
	}

	public void setDescription(String Description) {
		this.Description = Description;
	}
}
