package com.aisino.grain;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

public class AppManagement extends Application {
	private List<Activity> mActivityList = new ArrayList<Activity>();   
	private static AppManagement mInstance;  
	   
    private AppManagement() {  
    }  
    
    //����ģʽ�л�ȡΨһ��AppManagementʵ��   
    public static AppManagement getInstance() {  
    	if(null == mInstance) {  
    		mInstance = new AppManagement();  
        }  
    	return mInstance;               
    }  
    
    //���Activity��������  
    public void addActivity(Activity activity) {  
    	mActivityList.add(activity);  
    }  
    
	//��������Activity��finish  
	public void exit() {  
		for(Activity activity:mActivityList) {  
			activity.finish();  
		}  
		System.exit(0);  
	}  
}
