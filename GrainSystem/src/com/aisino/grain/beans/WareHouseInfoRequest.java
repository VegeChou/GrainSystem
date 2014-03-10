/**
 * 
 */
package com.aisino.grain.beans;

import java.util.Hashtable;

import org.ksoap2.serialization.KvmSerializable;
import org.ksoap2.serialization.PropertyInfo;

/**
 * @author zwz
 * @date 2013-9-22
 * @description
 *
 */
public class WareHouseInfoRequest extends RequestBean implements KvmSerializable{
	private static final int index_Page = 0;
	private static final int index_SearchFlag = 1;
	private static final int index_SearchLength = 2;
	private static final int index_WareHouseID = 3;
	
	private String WareHouseID;
	private int SearchFlag;
	private int SearchLength;
	private int Page;

	public String getWareHouseID() {
		return WareHouseID;
	}
	public void setWareHouseID(String wareHouseID) {
		WareHouseID = wareHouseID;
	}
	public int getSearchFlag() {
		return SearchFlag;
	}
	public void setSearchFlag(int searchFlag) {
		SearchFlag = searchFlag;
	}
	public int getSearchLength() {
		return SearchLength;
	}
	public void setSearchLength(int searchLength) {
		SearchLength = searchLength;
	}
	public int getPage() {
		return Page;
	}
	public void setPage(int page) {
		Page = page;
	}
	
	@Override
	public Object getProperty(int arg0) {
		Object res = null;
		switch(arg0){
			case index_WareHouseID:
				res = getWareHouseID();
				break;
			case index_SearchFlag:
				res = getSearchFlag();
				break;
			case index_SearchLength:
				res = getSearchLength();
				break;
			case index_Page:
				res = getPage();
				break;
		}
		return res;
	}

	@Override
	public int getPropertyCount() {
		return 4;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void getPropertyInfo(int arg0, Hashtable arg1, PropertyInfo arg2) {
		 switch(arg0){
	        case index_WareHouseID:
	        	arg2.type = PropertyInfo.STRING_CLASS;//设置info type的类型
	        	arg2.name = "WareHouseID";
		        break;
	        case index_SearchFlag:
	        	arg2.type = PropertyInfo.INTEGER_CLASS;
	        	arg2.name = "SearchFlag";
	        	break;
	        case index_SearchLength:
	        	arg2.type = PropertyInfo.INTEGER_CLASS;
	        	arg2.name = "SearchLength";
	        	break;
	        case index_Page:
	        	arg2.type = PropertyInfo.INTEGER_CLASS;
	        	arg2.name = "Page";
		        break;
	        default:
	        	break;
        }
	}

	@Override
	public void setProperty(int arg0, Object arg1) {
		switch(arg0){
	    	case index_WareHouseID:
	    		if (arg1 == null) {
	    			setWareHouseID(null);
	    			break;
				}
		    	setWareHouseID(arg1.toString());
		    	break;
	    	case index_SearchFlag:
	    		if (arg1 == null) {
	    			setSearchFlag(-1);
	    			break;
				}
	    		setSearchFlag(Integer.parseInt(arg1.toString()));
	    		break;
	    	case index_SearchLength:
	    		if (arg1 == null) {
	    			setSearchLength(-1);
	    			break;
				}
	    		setSearchLength(Integer.parseInt(arg1.toString()));
	    		break;
		    case index_Page:
		    	if (arg1 == null) {
		    		setPage(-1);
	    			break;
				}
		    	setPage(Integer.parseInt(arg1.toString()));
		    	break;
		    default:
			   	break;
        }
	}
	
	 public static WareHouseInfoRequest fromString(String s) {
		 WareHouseInfoRequest result = new WareHouseInfoRequest();
		 String[] tokens = s.split("=");
		 String wareHouseID = tokens[1].split(";")[0];
		 String searchFlag = tokens[2].split(";")[0];
		 String searchLength = tokens[3].split(";")[0];
		 String page = tokens[4].split(";")[0];
	    
		 result.WareHouseID = wareHouseID;
		 result.SearchFlag = Integer.valueOf(searchFlag); 
		 result.SearchLength = Integer.valueOf(searchLength); 
		 result.SearchFlag = Integer.valueOf(page);
		 return result;
    }
}
