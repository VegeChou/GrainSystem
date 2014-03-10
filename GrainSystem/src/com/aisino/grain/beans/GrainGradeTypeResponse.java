/**
 * 
 */
package com.aisino.grain.beans;

import java.util.List;

/**
 * @author zwz
 * @date 2013-10-11
 * @description
 *
 */
public class GrainGradeTypeResponse extends ResponseBean {
	private List<GrainGradeType> GrainGradeTypeList = null;

	public List<GrainGradeType> getGrainGradeTypeList() {
		return GrainGradeTypeList;
	}

	public void setGrainGradeTypeList(List<GrainGradeType> grainGradeTypeList) {
		GrainGradeTypeList = grainGradeTypeList;
	}
	
}
