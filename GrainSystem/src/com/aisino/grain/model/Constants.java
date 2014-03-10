package com.aisino.grain.model;


public class Constants {
	public static String SynchWarehouse = "SynchWarehouse";							//查找某账户能值的仓库。如果LoginUser值为空，则返回所有仓库
	public static String ReturnWarehouse = "ReturnWarehouse";						//返回查找的仓库
	public static String SynchBusinessType = "SynchBusinessType";					//同步所有业务类型
	public static String ReturnBusinessType = "ReturnBusinessType";					//返回所有业务类型
	public static String SynchGrainType = "SynchGrainType";							//同步所有粮食品种
	public static String ReturnGrainType = "ReturnGrainType";						//返回所有粮食品种
	public static String SynchQualityIndex = "SynchQualityIndex";					//同步所有质检指标
	public static String ReturnQualityIndex = "ReturnQualityIndex";					//返回所有质检指标
	public static String SynchAcountInfo = "SynchAcountInfo";						//同步所有有手持机操作权限的账户信息
	public static String ReturnAcountInfo = "ReturnAcountInfo";						//返回所有有手持机操作权限的账户信息
	public static String LoginRequest = "LoginRequest";								//登录请求
	public static String LoginMessage = "LoginMessage";								//登录消息
	public static String GetWarehouseInfo = "GetWarehouseInfo";						//查找指定仓库的仓容、存粮信息及逐日出入库汇总信息
	public static String WarehouseInfoMessage = "WarehouseInfoMessage";				//返回指定仓库的仓容、存粮信息及逐日出入库汇总信息
	public static String CheckVehicleWorkRequest = "CheckVehicleWorkRequest";		//校验车辆作业请求
	public static String CheckVehicleWorkMessage = "CheckVehicleWorkMessage";		//校验车辆作业消息
	public static String RegisterVehicleWorkRequest = "RegisterVehicleWorkRequest";	//登记车辆作业请求
	public static String RegisterVehicleWorkMessage = "RegisterVehicleWorkMessage";	//登记车辆作业消息									//当前登录用户名称
	public static String GetQualityInfo = "GetQualityInfo";							//得到车辆的粮食化验信息
	public static String ReturnQualityInfo = "ReturnQualityInfo";					//返回车辆的粮食化验信息
	public static String AdjustQualityInfo = "AdjustQualityInfo";					//值仓调整扣量扣价（二次增扣）
	public static String ResponseMessage = "ResponseMessage";						//应答消息：成功则返回success，失败在返回原因字符串
	public static String TaskingRequest = "TaskingRequest";							//待处理扦样任务列表请求
	public static String TaskingMessage = "TaskingMessage";							//待处理扦样任务消息
	public static String CheckingRFIDTagRequest = "CheckingRFIDTagRequest";			//校验车船RFID标签请求
	public static String CheckingRFIDMessage = "CheckingRFIDMessage";				//校验车船RFID标签消息
	public static String RegisterSampleRequest = "RegisterSampleRequest";			//登记样品请求
	public static String RegisterSampleMessage = "RegisterSampleMessage";			//登记样品消息
	public static String TaskedRequest = "TaskedRequest";							//已处理扦样任务列表请求
	public static String TaskedMessage = "TaskedMessage";							//已处理扦样任务消息
	public static String SendMessageRequest = "SendMessageRequest";					//发送消息请求
	public static String SendMessageMessage = "SendMessageMessage";					//发送消息消息
	
	//报港信息
	//RFID标志位
	public static int CARD_NOT_ISSUED = 0;											//卡未发行
	public static int CARD_ISSUED = 1;												//卡已发行
	public static int MOBILE_RETREATED = 2;											//手持机上已退卡
	
	public static final int START_WAREHOUSE_INFO = 100;		//同步仓库信息开始
	public static final int STOP_WAREHOUSE_INFO = 101;		//同步仓库信息结束
	public static final int START_BUSINESS_TYPE = 102;		//同步业务种类开始
	public static final int STOP_BUSINESS_TYPE = 103;		//同步业务种类结束
	public static final int START_GRAIN_TYPE = 104;			//同步粮食品种开始
	public static final int STOP_GRAIN_TYPE = 105;			//同步粮食品种结束
	public static final int START_QUALITY_INDEX = 106;		//同步化验指标开始
	public static final int STOP_QUALITY_INDEX = 107;		//同步化验指标结束
	public static final int START_ACOUNT_INFO = 108;		//同步用户信息开始
	public static final int STOP_ACOUNT_INFO = 109;			//同步用户信息结束
	public static final int STOP_SYNC = 110;				//同步信息结束
	
	public static final int NOT_WAREHOUSE = 0;				//未值仓
	public static final int WAREHOUSE_CONFIRM = 1;			//值仓确认
	public static final int CHANGE_WAREHOUSE = 2;			//装卸未完成，值仓确认需要换仓
	public static final int ASSAY_CHANGE_WAREHOUSE = 3;		//质量问题需要换仓
	public static final int WEIGHT_TARE = 4;				//称完皮重
	public static final int WEIGHT_GROSS = 5;				//称完毛重
	public static final int WHOLE_VEHICLE_CHANGE_WAREHOUSE = 6;		//整车换仓
	
	public static final int ENTER_WAREHOUSE = 1;	//入库业务
	public static final int OUT_OF_WAREHOUSE = 2;	//出库业务

	public static final int TARE_FIRST = 0;			//先皮后毛
	public static final int GROSS_FIRST = 1;		//先毛后皮

	public static final int IN_VEHICLE = 0;			//内部车
	public static final int OUT_VEHICLE= 1;			//外部车

	public static final int CARD = 1;		//卡
	public static final int INIT = 0;		//初始化
	public static final int NET = 2;		//网络
	
	
	//LoginActivity
	public static int LOCAL_LOGIN_SUCCESS = 2;						//本地验证成功
	public static int LOCAL_LOGIN_SUCCESS_CARD_NOT_ACTIVITY = 1;	//本地验证成功卡未激活
	public static int LOCAL_LOGIN_FAILED = 0;						//本地验证失败
	public static int ONLINE_LOGIN_SUCCESS = 0;						//在线验证成功
	public static int ONLINE_LOGIN_NET_FAILED = 1;					//在线登录网络失败
	public static int ONLINE_LOGIN_CHECK_FAILED = 2;				//在线登录校验失败
	
	//WarehouseInfoActivity
	public static int WAREHOUSE_EMPTY_MSG = 1;						//仓库已空消息
	public static int WAREHOUSE_FULL_MSG = 2;						//仓库已满消息
	
	//HelpActivity
	public static String APP_NAME = "GrainSystem.apk";
	
	public static String ACTION_READ_CARD = "ACTION_READ_CARD";
}
