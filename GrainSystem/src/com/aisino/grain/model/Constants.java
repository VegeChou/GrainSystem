package com.aisino.grain.model;


public class Constants {
	public static String SynchWarehouse = "SynchWarehouse";							//����ĳ�˻���ֵ�Ĳֿ⡣���LoginUserֵΪ�գ��򷵻����вֿ�
	public static String ReturnWarehouse = "ReturnWarehouse";						//���ز��ҵĲֿ�
	public static String SynchBusinessType = "SynchBusinessType";					//ͬ������ҵ������
	public static String ReturnBusinessType = "ReturnBusinessType";					//��������ҵ������
	public static String SynchGrainType = "SynchGrainType";							//ͬ��������ʳƷ��
	public static String ReturnGrainType = "ReturnGrainType";						//����������ʳƷ��
	public static String SynchQualityIndex = "SynchQualityIndex";					//ͬ�������ʼ�ָ��
	public static String ReturnQualityIndex = "ReturnQualityIndex";					//���������ʼ�ָ��
	public static String SynchAcountInfo = "SynchAcountInfo";						//ͬ���������ֳֻ�����Ȩ�޵��˻���Ϣ
	public static String ReturnAcountInfo = "ReturnAcountInfo";						//�����������ֳֻ�����Ȩ�޵��˻���Ϣ
	public static String LoginRequest = "LoginRequest";								//��¼����
	public static String LoginMessage = "LoginMessage";								//��¼��Ϣ
	public static String GetWarehouseInfo = "GetWarehouseInfo";						//����ָ���ֿ�Ĳ��ݡ�������Ϣ�����ճ���������Ϣ
	public static String WarehouseInfoMessage = "WarehouseInfoMessage";				//����ָ���ֿ�Ĳ��ݡ�������Ϣ�����ճ���������Ϣ
	public static String CheckVehicleWorkRequest = "CheckVehicleWorkRequest";		//У�鳵����ҵ����
	public static String CheckVehicleWorkMessage = "CheckVehicleWorkMessage";		//У�鳵����ҵ��Ϣ
	public static String RegisterVehicleWorkRequest = "RegisterVehicleWorkRequest";	//�Ǽǳ�����ҵ����
	public static String RegisterVehicleWorkMessage = "RegisterVehicleWorkMessage";	//�Ǽǳ�����ҵ��Ϣ									//��ǰ��¼�û�����
	public static String GetQualityInfo = "GetQualityInfo";							//�õ���������ʳ������Ϣ
	public static String ReturnQualityInfo = "ReturnQualityInfo";					//���س�������ʳ������Ϣ
	public static String AdjustQualityInfo = "AdjustQualityInfo";					//ֵ�ֵ��������ۼۣ��������ۣ�
	public static String ResponseMessage = "ResponseMessage";						//Ӧ����Ϣ���ɹ��򷵻�success��ʧ���ڷ���ԭ���ַ���
	public static String TaskingRequest = "TaskingRequest";							//������Ǥ�������б�����
	public static String TaskingMessage = "TaskingMessage";							//������Ǥ��������Ϣ
	public static String CheckingRFIDTagRequest = "CheckingRFIDTagRequest";			//У�鳵��RFID��ǩ����
	public static String CheckingRFIDMessage = "CheckingRFIDMessage";				//У�鳵��RFID��ǩ��Ϣ
	public static String RegisterSampleRequest = "RegisterSampleRequest";			//�Ǽ���Ʒ����
	public static String RegisterSampleMessage = "RegisterSampleMessage";			//�Ǽ���Ʒ��Ϣ
	public static String TaskedRequest = "TaskedRequest";							//�Ѵ���Ǥ�������б�����
	public static String TaskedMessage = "TaskedMessage";							//�Ѵ���Ǥ��������Ϣ
	public static String SendMessageRequest = "SendMessageRequest";					//������Ϣ����
	public static String SendMessageMessage = "SendMessageMessage";					//������Ϣ��Ϣ
	
	//������Ϣ
	//RFID��־λ
	public static int CARD_NOT_ISSUED = 0;											//��δ����
	public static int CARD_ISSUED = 1;												//���ѷ���
	public static int MOBILE_RETREATED = 2;											//�ֳֻ������˿�
	
	public static final int START_WAREHOUSE_INFO = 100;		//ͬ���ֿ���Ϣ��ʼ
	public static final int STOP_WAREHOUSE_INFO = 101;		//ͬ���ֿ���Ϣ����
	public static final int START_BUSINESS_TYPE = 102;		//ͬ��ҵ�����࿪ʼ
	public static final int STOP_BUSINESS_TYPE = 103;		//ͬ��ҵ���������
	public static final int START_GRAIN_TYPE = 104;			//ͬ����ʳƷ�ֿ�ʼ
	public static final int STOP_GRAIN_TYPE = 105;			//ͬ����ʳƷ�ֽ���
	public static final int START_QUALITY_INDEX = 106;		//ͬ������ָ�꿪ʼ
	public static final int STOP_QUALITY_INDEX = 107;		//ͬ������ָ�����
	public static final int START_ACOUNT_INFO = 108;		//ͬ���û���Ϣ��ʼ
	public static final int STOP_ACOUNT_INFO = 109;			//ͬ���û���Ϣ����
	public static final int STOP_SYNC = 110;				//ͬ����Ϣ����
	
	public static final int NOT_WAREHOUSE = 0;				//δֵ��
	public static final int WAREHOUSE_CONFIRM = 1;			//ֵ��ȷ��
	public static final int CHANGE_WAREHOUSE = 2;			//װжδ��ɣ�ֵ��ȷ����Ҫ����
	public static final int ASSAY_CHANGE_WAREHOUSE = 3;		//����������Ҫ����
	public static final int WEIGHT_TARE = 4;				//����Ƥ��
	public static final int WEIGHT_GROSS = 5;				//����ë��
	public static final int WHOLE_VEHICLE_CHANGE_WAREHOUSE = 6;		//��������
	
	public static final int ENTER_WAREHOUSE = 1;	//���ҵ��
	public static final int OUT_OF_WAREHOUSE = 2;	//����ҵ��

	public static final int TARE_FIRST = 0;			//��Ƥ��ë
	public static final int GROSS_FIRST = 1;		//��ë��Ƥ

	public static final int IN_VEHICLE = 0;			//�ڲ���
	public static final int OUT_VEHICLE= 1;			//�ⲿ��

	public static final int CARD = 1;		//��
	public static final int INIT = 0;		//��ʼ��
	public static final int NET = 2;		//����
	
	
	//LoginActivity
	public static int LOCAL_LOGIN_SUCCESS = 2;						//������֤�ɹ�
	public static int LOCAL_LOGIN_SUCCESS_CARD_NOT_ACTIVITY = 1;	//������֤�ɹ���δ����
	public static int LOCAL_LOGIN_FAILED = 0;						//������֤ʧ��
	public static int ONLINE_LOGIN_SUCCESS = 0;						//������֤�ɹ�
	public static int ONLINE_LOGIN_NET_FAILED = 1;					//���ߵ�¼����ʧ��
	public static int ONLINE_LOGIN_CHECK_FAILED = 2;				//���ߵ�¼У��ʧ��
	
	//WarehouseInfoActivity
	public static int WAREHOUSE_EMPTY_MSG = 1;						//�ֿ��ѿ���Ϣ
	public static int WAREHOUSE_FULL_MSG = 2;						//�ֿ�������Ϣ
	
	//HelpActivity
	public static String APP_NAME = "GrainSystem.apk";
	
	public static String ACTION_READ_CARD = "ACTION_READ_CARD";
}
