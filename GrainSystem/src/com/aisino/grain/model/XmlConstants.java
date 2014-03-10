package com.aisino.grain.model;

import org.dom4j.Document;

public class XmlConstants {
	//Xml通用变量
	public static Document Document = null;
	public static String StringXml = null;
	
//	public static String SynchWarehouse = "SynchWarehouse";							//查找某账户能值的仓库。如果LoginUser值为空，则返回所有仓库
	public static String LoginUser = "LoginUser";									//当前登录用户名称
	
//	public static String ReturnWarehouse = "ReturnWarehouse";						//返回查找的仓库
	public static String Warehouse = "Warehouse";									//所有仓库列表
	public static String warehouseID = "warehouseID";								//仓库ID
	public static String warehouseName = "warehouseName";							//仓库名称
	
//	public static String SynchBusinessType = "SynchBusinessType";					//同步所有业务类型
	
//	public static String ReturnBusinessType = "ReturnBusinessType";					//返回所有业务类型
	public static String BusinessType = "BusinessType";								//业务类型
	public static String typeID = "typeID";											//业务类型ID
	public static String typeName = "typeName";										//业务类型名称
	
//	public static String SynchGrainType = "SynchGrainType";							//同步所有粮食品种
	
//	public static String ReturnGrainType = "ReturnGrainType";						//返回所有粮食品种
	public static String GrainType = "GrainType";									//粮食品种
//	public static String typeID = "typeID";											//粮食品种ID
//	public static String typeName = "typeName";										//粮食品种名称
	
//	public static String SynchQualityIndex = "SynchQualityIndex";					//同步所有质检指标
	
//	public static String ReturnQualityIndex = "ReturnQualityIndex";					//返回所有质检指标
	public static String QualityIndex = "QualityIndex";								//质检指标
	public static String id = "id";													//质检指标ID
	public static String name = "name";												//质检指标名称
	
//	public static String SynchAcountInfo = "SynchAcountInfo";						//同步所有有手持机操作权限的账户信息
	
//	public static String ReturnAcountInfo = "ReturnAcountInfo";						//返回所有有手持机操作权限的账户信息
	public static String Acount = "Acount";											//账户信息
//	public static String name = "name";												//登陆账户名
	public static String password = "password";										//登陆密码
	public static String cardID = "cardID";											//人员卡的卡内ID
	public static String operations = "operations";									//操作权限代码，以逗号分隔
	public static String warehouses = "warehouses";									//能值的仓库ID，以逗号分隔
	
//	public static String LoginRequest = "LoginRequest";								//登录请求
	public static String LoginUserName = "LoginUserName";							//登录用户名称
	public static String LoginPassword = "LoginPassword";							//登录密码
	public static String RfidTagId = "RfidTagId";									//人员卡的卡内ID
	
//	public static String LoginMessage = "LoginMessage";								//登录消息
	public static String LoginResult = "LoginResult";								//登录结果：成功返回true，失败返回失败原因
//	public static String Acount = "Acount";											//账户信息
//	public static String name = "name";												/登陆账户名
//	public static String password = "password";										//登陆密码
//	public static String cardID = "cardID";											//人员卡的卡内ID
//	public static String operations = "operations";									//操作权限代码，以逗号分隔
//	public static String warehouses = "warehouses";									//能值的仓库ID，以逗号分隔
	
//	public static String GetWarehouseInfo = "GetWarehouseInfo";						//查找指定仓库的仓容、存粮信息及逐日出入库汇总信息
	public static String warehouseId = "warehouseId";								//要查看的仓库ID
	public static String inOutFlag = "inOutFlag";									//查看入库还是出库逐日汇总：0表示入库，1表示出库，2表示出入库
	public static String inOutLength = "inOutLength";								//请求返回的逐日汇总记录的条数，默认为4
	public static String startIndex = "startIndex";									//起始记录的序号
	
//	public static String WarehouseInfoMessage = "WarehouseInfoMessage";				//返回指定仓库的仓容、存粮信息及逐日出入库汇总信息
//	public static String warehouseId = "warehouseId";								//仓库ID
	public static String capacity = "capacity";										//仓容
	public static String grainKind = "grainKind";									//粮食品种
	public static String totalQuantity = "totalQuantity";							//存粮总数
	public static String owner = "owner";											//粮权所属
	public static String grainAttribute = "grainAttribute";							//粮食性质
	public static String InOutInfoOfDay = "InOutInfoOfDay";
	public static String date = "date";
	public static String netWeight = "netWeight";
	public static String deductibleWeight = "deductibleWeight";

//	public static String CheckVehicleWorkRequest = "CheckVehicleWorkRequest";		//校验车辆作业请求
	public static String RFIDTag = "RFIDTag";										//RFID标签的ID号
	public static String WarehouseID = "WarehouseID";								//值仓仓库的ID号
	
	
//	public static String CheckVehicleWorkMessage = "CheckVehicleWorkMessage";		//校验车辆作业消息
	public static String VechileInfo = "VechileInfo";								//车辆信息
	public static String rfidTag = "rfidTag";										//车辆RFID标签标签
	public static String VehiclePlate = "VehiclePlate";								//车辆牌照
	public static String WorkInfo = "WorkInfo";										//车辆作业信息
	public static String WorkNumber = "WorkNumber";									//作业编号
	public static String WorkType = "WorkType";										//作业类型
	public static String WorkNode = "WorkNode";										//作业环节
	public static String WorkWarehouse = "WorkWarehouse";
//	public static String WarehouseID = "WarehouseID";								//仓库ID
	public static String WarehouseName = "WarehouseName";							//仓库名称
	public static String Location = "Location";		
	public static String LocationID = "LocationID";									//货位ID		
	public static String LocationName = "LocationName";								//货位名称		
	public static String WorkGrainKind = "WorkGrainKind";							//粮食品种
	public static String GoodsOwner = "GoodsOwner";									//货主
	public static String GrossWeight = "GrossWeight";								//毛重
	public static String TareWeight = "TareWeight";									//皮重
	public static String NetWeight = "NetWeight";									//净重
	public static String DeductWeight = "DeductWeight";								//扣重
	public static String ApprovedWeight = "ApprovedWeight";							//批准重量
	public static String CompletedWeight = "CompletedWeight";						//已完成重量
	public static String WarnFlag = "WarnFlag";										//提醒标志，0不提醒，1提醒
	public static String CheckResult = "CheckResult";								//校验结果
	

//	public static String RegisterVehicleWorkRequest = "RegisterVehicleWorkRequest";	//登记车辆作业请求
//	public static String RFIDTag = "RFIDTag";										//车辆RFID标识
	//public static String WarehouseID = "WarehouseID";								//作业仓库ID
	public static String WarehouseChangeFlag = "WarehouseChangeFlag";				//值仓时是否有换仓：0 否；1 是
//	public static String WorkNumber = "WorkNumber";									//作业编号
	public static String WarehouseWorkType = "WarehouseWorkType";					//仓库作业类型
	public static String WorkSituation = "WorkSituation";							//作业完成情况
	//public static String LoginUser = "LoginUser";									//当前登录用户名称
	
//	public static String RegisterVehicleWorkMessage = "RegisterVehicleWorkMessage";	//登记车辆作业消息									//当前登录用户名称
	public static String RegisterResult = "RegisterResult";							//登记结果								//当前登录用户名称
	
//	public static String GetQualityInfo = "GetQualityInfo";							//得到车辆的粮食化验信息
	public static String RFIDTagID = "RFIDTagID";									//车辆关联的标签卡的卡内ID
	
//	public static String ReturnQualityInfo = "ReturnQualityInfo";					//返回车辆的粮食化验信息
	public static String FirstQualityInfo = "FirstQualityInfo";						//初次扣量扣价
	public static String MoistureDeductAmount = "MoistureDeductAmount";				//水分扣量
	public static String MoistureAmountFlag = "MoistureAmountFlag";					//水分扣量标记：0按百分比扣，1按重量扣
	public static String ImpurityDeductAmount = "ImpurityDeductAmount";				//杂质扣量
	public static String ImpurityAmountFlag = "ImpurityAmountFlag";					//杂质扣量标记：0按百分比扣，1按重量扣
	public static String MoistureDeductPrice = "MoistureDeductPrice";				//扣整晒费
	public static String MoisturePriceFlag = "MoisturePriceFlag";					//整晒费扣费标记：0按照百分比扣，1按元扣
	public static String ImpurityDeductPrice = "ImpurityDeductPrice";				//扣清杂费
	public static String ImpurityPriceFlag = "ImpurityPriceFlag";					//扣清杂费标记：0按百分比扣，1按元扣
	public static String AdjustQualityInfo = "AdjustQualityInfo";
//	public static String MoistureDeductAmount = "MoistureDeductAmount";				//水分扣量
//	public static String MoistureAmountFlag = "MoistureAmountFlag";					//水分扣量标记：0按百分比扣，1按重量扣
//	public static String ImpurityDeductAmount = "ImpurityDeductAmount";				//杂质扣量
//	public static String ImpurityAmountFlag = "ImpurityAmountFlag";					//杂质扣量标记：0按百分比扣，1按重量扣
//	public static String MoistureDeductPrice = "MoistureDeductPrice";				//扣整晒费
//	public static String MoisturePriceFlag = "MoisturePriceFlag";					//整晒费扣费标记：0按照百分比扣，1按元扣
//	public static String ImpurityDeductPrice = "ImpurityDeductPrice";				//扣清杂费
//	public static String ImpurityPriceFlag = "ImpurityPriceFlag";					//扣清杂费标记：0按百分比扣，1按元扣
//	public static String QualityIndex = "QualityIndex";								//质量指标
	public static String IndexID = "IndexID";										//化验指标ID
	public static String IndexValue = "IndexValue";									//化验指标值
	
//	public static String AdjustQualityInfo = "AdjustQualityInfo";					//值仓调整扣量扣价（二次增扣）
//	public static String RFIDTagID = "RFIDTagID";									//车辆关联的RFID标签卡的卡内ID
//	public static String MoistureDeductAmount = "MoistureDeductAmount";				//水分扣量
//	public static String MoistureAmountFlag = "MoistureAmountFlag";					//水分扣量标记：0按百分比扣，1按重量扣
//	public static String ImpurityDeductAmount = "ImpurityDeductAmount";				//杂质扣量
//	public static String ImpurityAmountFlag = "ImpurityAmountFlag";					//杂质扣量标记：0按百分比扣，1按重量扣
//	public static String MoistureDeductPrice = "MoistureDeductPrice";				//扣整晒费
//	public static String MoisturePriceFlag = "MoisturePriceFlag";					//整晒费扣费标记：0按照百分比扣，1按元扣
//	public static String ImpurityDeductPrice = "ImpurityDeductPrice";				//扣清杂费
//	public static String ImpurityPriceFlag = "ImpurityPriceFlag";					//扣清杂费标记：0按百分比扣，1按元扣

//	public static String ResponseMessage = "ResponseMessage";						//应答消息：成功则返回success，失败在返回原因字符串
	
//	public static String TaskingRequest = "TaskingRequest";							//待处理扦样任务列表请求
//	public static String LoginUser = "LoginUser";									//当前登录用户名称
	public static String PageNumber	 = "PageNumber";								//列表页号
	
//	public static String TaskingMessage = "TaskingMessage";							//待处理扦样任务消息
//	public static String LoginUser = "LoginUser";									//当前登录用户名称
	public static String TaskTotal = "TaskTotal";									//任务总数
	public static String TaskCount = "TaskCount";									//返回任务数
//	public static String PageNumber = "PageNumber";									//列表页号
	public static String Tasks = "Tasks";											//任务集合
	public static String Task = "Task";												//每页条数
	public static String number = "number";											//任务编号
	public static String status = "status";											//任务状态
	public static String CreateDate = "CreateDate";									//任务创建日期
	public static String EnrolNumber = "EnrolNumber";								//报港编号
	public static String CarShipLicense = "CarShipLicense";							//车船牌照
	public static String SampleKind = "SampleKind";									//扦样品种
	public static String SampleNumber = "SampleNumber";								//样品编号
	
//	public static String CheckingRFIDTagRequest = "CheckingRFIDTagRequest";			//校验车船RFID标签请求
//	public static String RFIDTagID = "RFIDTagID";									//车船RFID标签ID
	public static String TaskNumber = "TaskNumber";									//当前处理的任务编号
	public static String TaskCarShipLicense = "TaskCarShipLicense";					//任务车船牌照
	public static String TaskEnrolNumber = "TaskEnrolNumber";						//任务报港编号
	
//	public static String CheckingRFIDMessage = "CheckingRFIDMessage";				//校验车船RFID标签消息
	public static String CheckingResult = "CheckingResult";							//校验结果	
//	public static String CarShipLicense = "CarShipLicense";							//车船牌照
//	public static String EnrolNumber = "EnrolNumber";								//报港编号
	
//	public static String RegisterSampleRequest = "RegisterSampleRequest";			//登记样品请求
//	public static String RegisterResult = "RegisterResult";	
	public static String result = "result";
//	public static String TaskNumber = "TaskNumber";									//任务编号
	public static String SampleCount = "SampleCount";								//样品数量
	
//	public static String RegisterSampleMessage = "RegisterSampleMessage";			//登记样品消息
	public static String RegisterSampleResult = "RegisterSampleResult";				//登记样品结果
//	public static String SampleNumber = "SampleNumber";								//返回的样品编号

//	public static String TaskedRequest = "TaskedRequest";							//已处理扦样任务列表请求
//	public static String LoginUser = "LoginUser";									//当前登录用户名称
//	public static String PageNumber = "PageNumber";									//列表页号
	public static String PageSize = "PageSize";										//每页条数
	public static String SearchCondition = "SearchCondition";						//检索条件
//	public static String CarShipLicense = "CarShipLicense";							//车船牌照
//	public static String SampleNumber = "SampleNumber";								//样品编号
	
//	public static String TaskedMessage = "TaskedMessage";							//已处理扦样任务消息
//	public static String LoginUser = "LoginUser";									//当前登录用户名称
//	public static String TaskTotal = "TaskTotal";									//任务总数
//	public static String PageSize = "PageSize";										//每页条数
//	public static String PageNumber = "PageNumber";									//列表页号
//	public static String Tasks = "Tasks";											//任务集合
//	public static String Task = "Task";												//每页条数
//	public static String number = "number";											//任务编号
//	public static String status = "status";											//任务状态
//	public static String CreateDate = "CreateDate";									//任务创建日期
//	public static String EnrolNumber = "EnrolNumber";								//报港编号
//	public static String CarShipLicense = "CarShipLicense";							//车船牌照
//	public static String SampleKind = "SampleKind";									//扦样品种
//	public static String SampleNumber = "SampleNumber";								//样品编号

	public static String SendMessageRequest = "SendMessageRequest";					//发送消息请求
	public static String Message = "Message";										//消息
//	public static String WarehouseID = "WarehouseID";								//仓库标识
	public static String Locations = "Locations";									
//	public static String Location = "Location";										//仓库货位
//	public static String LocationID = "LocationID";									//货位ID
//	public static String LocationName = "LocationName";								//货位名称
	public static String WarehouseMessage = "WarehouseMessage";						//仓库消息
	
	public static String SendMessageMessage = "SendMessageMessage";					//发送消息消息
//	public static String WarehouseID = "WarehouseID";								//仓库标识
	public static String SendResult = "SendResult";									//发送结果
}
