package com.aisino.grain.beans;

import java.io.IOException;

import org.ksoap2.serialization.Marshal;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

public class WareHouseInfoRequestMarshal implements Marshal{
	private static String MAPURL = "Aisino.MES.DataCenter.WCFService.Models";//要注意这儿的定义,具体查看wcf端的xml是如何定义的
	
	@Override
	public Object readInstance(XmlPullParser parser, String arg1, String arg2,
	PropertyInfo arg3) throws IOException, XmlPullParserException {
		return WareHouseInfoRequest.fromString(parser.nextText());
	}

	@Override
	public void register(SoapSerializationEnvelope cm) {
	    cm.addMapping(MAPURL, "WareHouseInfoRequest", WareHouseInfoRequest.class, this);
	}

	@Override
	public void writeInstance(XmlSerializer writer, Object obj)
	throws IOException {
		WareHouseInfoRequest wareHouseInfoRequest = (WareHouseInfoRequest)obj;
		writer.startTag(MAPURL, "WareHouseID");
		writer.text(wareHouseInfoRequest.getWareHouseID());
		writer.endTag(MAPURL, "WareHouseID");
		
		writer.startTag(MAPURL, "SearchFlag");
		writer.text(String.valueOf(wareHouseInfoRequest.getSearchFlag()));
		writer.endTag(MAPURL, "SearchFlag");
		
		writer.startTag(MAPURL, "SearchLength");
		writer.text(String.valueOf(wareHouseInfoRequest.getSearchLength()));
		writer.endTag(MAPURL, "SearchLength");
		
		writer.startTag(MAPURL, "Page");
		writer.text(String.valueOf(wareHouseInfoRequest.getPage()));
		writer.endTag(MAPURL, "Page");
	}
}
