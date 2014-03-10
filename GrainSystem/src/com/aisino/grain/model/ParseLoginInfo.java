package com.aisino.grain.model;

import java.util.ArrayList;
import java.util.List;

import com.aisino.grain.model.db.DataHelper;
import com.j256.ormlite.android.apptools.OrmLiteBaseActivity;


public class ParseLoginInfo extends OrmLiteBaseActivity<DataHelper>{
	private static List<String> mOperList = null;
	
	public static List<String> ParseOpeartions(String operations) {
		mOperList = new ArrayList<String>();
		String[] strarray = operations.split(","); 
		for (int i = 0; i < strarray.length; i++) 
			mOperList.add(strarray[i]);
		return mOperList;
	}
}
