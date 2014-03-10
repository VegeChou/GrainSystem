/**
 * 
 */
package com.aisino.grain.beans;

import java.util.List;

/**
 * @author zwz
 * @date 2013-10-10
 * @description
 *
 */
public class SearchTaskingResponse extends ResponseBean {
	private int TaskCount = 0;
	List<Task> TaskList = null;
	public int getTaskCount() {
		return TaskCount;
	}
	public void setTaskCount(int taskCount) {
		TaskCount = taskCount;
	}
	public List<Task> getTaskList() {
		return TaskList;
	}
	public void setTaskList(List<Task> taskList) {
		TaskList = taskList;
	}
	
}
