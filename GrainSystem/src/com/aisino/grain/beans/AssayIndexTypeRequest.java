package com.aisino.grain.beans;

import java.util.List;

public class AssayIndexTypeRequest extends RequestBean {
	private List<AssayIndex> AssayIndexList = null;

	public List<AssayIndex> getAssayIndexList() {
		return AssayIndexList;
	}

	public void setAssayIndexList(List<AssayIndex> assayIndexList) {
		AssayIndexList = assayIndexList;
	}
	
}
