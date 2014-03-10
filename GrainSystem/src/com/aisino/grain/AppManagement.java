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
    
    //单例模式中获取唯一的AppManagement实例   
    public static AppManagement getInstance() {  
    	if(null == mInstance) {  
    		mInstance = new AppManagement();  
        }  
    	return mInstance;               
    }  
    
    //添加Activity到容器中  
    public void addActivity(Activity activity) {  
    	mActivityList.add(activity);  
    }  
    
	//遍历所有Activity并finish  
	public void exit() {  
		for(Activity activity:mActivityList) {  
			activity.finish();  
		}  
		System.exit(0);  
	}  
}
