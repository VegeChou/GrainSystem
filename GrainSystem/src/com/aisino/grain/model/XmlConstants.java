package com.aisino.grain.model;

import org.dom4j.Document;

public class XmlConstants {
	//Xmlͨ�ñ���
	public static Document Document = null;
	public static String StringXml = null;
	
//	public static String SynchWarehouse = "SynchWarehouse";							//����ĳ�˻���ֵ�Ĳֿ⡣���LoginUserֵΪ�գ��򷵻����вֿ�
	public static String LoginUser = "LoginUser";									//��ǰ��¼�û�����
	
//	public static String ReturnWarehouse = "ReturnWarehouse";						//���ز��ҵĲֿ�
	public static String Warehouse = "Warehouse";									//���вֿ��б�
	public static String warehouseID = "warehouseID";								//�ֿ�ID
	public static String warehouseName = "warehouseName";							//�ֿ�����
	
//	public static String SynchBusinessType = "SynchBusinessType";					//ͬ������ҵ������
	
//	public static String ReturnBusinessType = "ReturnBusinessType";					//��������ҵ������
	public static String BusinessType = "BusinessType";								//ҵ������
	public static String typeID = "typeID";											//ҵ������ID
	public static String typeName = "typeName";										//ҵ����������
	
//	public static String SynchGrainType = "SynchGrainType";							//ͬ��������ʳƷ��
	
//	public static String ReturnGrainType = "ReturnGrainType";						//����������ʳƷ��
	public static String GrainType = "GrainType";									//��ʳƷ��
//	public static String typeID = "typeID";											//��ʳƷ��ID
//	public static String typeName = "typeName";										//��ʳƷ������
	
//	public static String SynchQualityIndex = "SynchQualityIndex";					//ͬ�������ʼ�ָ��
	
//	public static String ReturnQualityIndex = "ReturnQualityIndex";					//���������ʼ�ָ��
	public static String QualityIndex = "QualityIndex";								//�ʼ�ָ��
	public static String id = "id";													//�ʼ�ָ��ID
	public static String name = "name";												//�ʼ�ָ������
	
//	public static String SynchAcountInfo = "SynchAcountInfo";						//ͬ���������ֳֻ�����Ȩ�޵��˻���Ϣ
	
//	public static String ReturnAcountInfo = "ReturnAcountInfo";						//�����������ֳֻ�����Ȩ�޵��˻���Ϣ
	public static String Acount = "Acount";											//�˻���Ϣ
//	public static String name = "name";												//��½�˻���
	public static String password = "password";										//��½����
	public static String cardID = "cardID";											//��Ա���Ŀ���ID
	public static String operations = "operations";									//����Ȩ�޴��룬�Զ��ŷָ�
	public static String warehouses = "warehouses";									//��ֵ�Ĳֿ�ID���Զ��ŷָ�
	
//	public static String LoginRequest = "LoginRequest";								//��¼����
	public static String LoginUserName = "LoginUserName";							//��¼�û�����
	public static String LoginPassword = "LoginPassword";							//��¼����
	public static String RfidTagId = "RfidTagId";									//��Ա���Ŀ���ID
	
//	public static String LoginMessage = "LoginMessage";								//��¼��Ϣ
	public static String LoginResult = "LoginResult";								//��¼������ɹ�����true��ʧ�ܷ���ʧ��ԭ��
//	public static String Acount = "Acount";											//�˻���Ϣ
//	public static String name = "name";												/��½�˻���
//	public static String password = "password";										//��½����
//	public static String cardID = "cardID";											//��Ա���Ŀ���ID
//	public static String operations = "operations";									//����Ȩ�޴��룬�Զ��ŷָ�
//	public static String warehouses = "warehouses";									//��ֵ�Ĳֿ�ID���Զ��ŷָ�
	
//	public static String GetWarehouseInfo = "GetWarehouseInfo";						//����ָ���ֿ�Ĳ��ݡ�������Ϣ�����ճ���������Ϣ
	public static String warehouseId = "warehouseId";								//Ҫ�鿴�Ĳֿ�ID
	public static String inOutFlag = "inOutFlag";									//�鿴��⻹�ǳ������ջ��ܣ�0��ʾ��⣬1��ʾ���⣬2��ʾ�����
	public static String inOutLength = "inOutLength";								//���󷵻ص����ջ��ܼ�¼��������Ĭ��Ϊ4
	public static String startIndex = "startIndex";									//��ʼ��¼�����
	
//	public static String WarehouseInfoMessage = "WarehouseInfoMessage";				//����ָ���ֿ�Ĳ��ݡ�������Ϣ�����ճ���������Ϣ
//	public static String warehouseId = "warehouseId";								//�ֿ�ID
	public static String capacity = "capacity";										//����
	public static String grainKind = "grainKind";									//��ʳƷ��
	public static String totalQuantity = "totalQuantity";							//��������
	public static String owner = "owner";											//��Ȩ����
	public static String grainAttribute = "grainAttribute";							//��ʳ����
	public static String InOutInfoOfDay = "InOutInfoOfDay";
	public static String date = "date";
	public static String netWeight = "netWeight";
	public static String deductibleWeight = "deductibleWeight";

//	public static String CheckVehicleWorkRequest = "CheckVehicleWorkRequest";		//У�鳵����ҵ����
	public static String RFIDTag = "RFIDTag";										//RFID��ǩ��ID��
	public static String WarehouseID = "WarehouseID";								//ֵ�ֲֿ��ID��
	
	
//	public static String CheckVehicleWorkMessage = "CheckVehicleWorkMessage";		//У�鳵����ҵ��Ϣ
	public static String VechileInfo = "VechileInfo";								//������Ϣ
	public static String rfidTag = "rfidTag";										//����RFID��ǩ��ǩ
	public static String VehiclePlate = "VehiclePlate";								//��������
	public static String WorkInfo = "WorkInfo";										//������ҵ��Ϣ
	public static String WorkNumber = "WorkNumber";									//��ҵ���
	public static String WorkType = "WorkType";										//��ҵ����
	public static String WorkNode = "WorkNode";										//��ҵ����
	public static String WorkWarehouse = "WorkWarehouse";
//	public static String WarehouseID = "WarehouseID";								//�ֿ�ID
	public static String WarehouseName = "WarehouseName";							//�ֿ�����
	public static String Location = "Location";		
	public static String LocationID = "LocationID";									//��λID		
	public static String LocationName = "LocationName";								//��λ����		
	public static String WorkGrainKind = "WorkGrainKind";							//��ʳƷ��
	public static String GoodsOwner = "GoodsOwner";									//����
	public static String GrossWeight = "GrossWeight";								//ë��
	public static String TareWeight = "TareWeight";									//Ƥ��
	public static String NetWeight = "NetWeight";									//����
	public static String DeductWeight = "DeductWeight";								//����
	public static String ApprovedWeight = "ApprovedWeight";							//��׼����
	public static String CompletedWeight = "CompletedWeight";						//���������
	public static String WarnFlag = "WarnFlag";										//���ѱ�־��0�����ѣ�1����
	public static String CheckResult = "CheckResult";								//У����
	

//	public static String RegisterVehicleWorkRequest = "RegisterVehicleWorkRequest";	//�Ǽǳ�����ҵ����
//	public static String RFIDTag = "RFIDTag";										//����RFID��ʶ
	//public static String WarehouseID = "WarehouseID";								//��ҵ�ֿ�ID
	public static String WarehouseChangeFlag = "WarehouseChangeFlag";				//ֵ��ʱ�Ƿ��л��֣�0 ��1 ��
//	public static String WorkNumber = "WorkNumber";									//��ҵ���
	public static String WarehouseWorkType = "WarehouseWorkType";					//�ֿ���ҵ����
	public static String WorkSituation = "WorkSituation";							//��ҵ������
	//public static String LoginUser = "LoginUser";									//��ǰ��¼�û�����
	
//	public static String RegisterVehicleWorkMessage = "RegisterVehicleWorkMessage";	//�Ǽǳ�����ҵ��Ϣ									//��ǰ��¼�û�����
	public static String RegisterResult = "RegisterResult";							//�Ǽǽ��								//��ǰ��¼�û�����
	
//	public static String GetQualityInfo = "GetQualityInfo";							//�õ���������ʳ������Ϣ
	public static String RFIDTagID = "RFIDTagID";									//���������ı�ǩ���Ŀ���ID
	
//	public static String ReturnQualityInfo = "ReturnQualityInfo";					//���س�������ʳ������Ϣ
	public static String FirstQualityInfo = "FirstQualityInfo";						//���ο����ۼ�
	public static String MoistureDeductAmount = "MoistureDeductAmount";				//ˮ�ֿ���
	public static String MoistureAmountFlag = "MoistureAmountFlag";					//ˮ�ֿ�����ǣ�0���ٷֱȿۣ�1��������
	public static String ImpurityDeductAmount = "ImpurityDeductAmount";				//���ʿ���
	public static String ImpurityAmountFlag = "ImpurityAmountFlag";					//���ʿ�����ǣ�0���ٷֱȿۣ�1��������
	public static String MoistureDeductPrice = "MoistureDeductPrice";				//����ɹ��
	public static String MoisturePriceFlag = "MoisturePriceFlag";					//��ɹ�ѿ۷ѱ�ǣ�0���հٷֱȿۣ�1��Ԫ��
	public static String ImpurityDeductPrice = "ImpurityDeductPrice";				//�����ӷ�
	public static String ImpurityPriceFlag = "ImpurityPriceFlag";					//�����ӷѱ�ǣ�0���ٷֱȿۣ�1��Ԫ��
	public static String AdjustQualityInfo = "AdjustQualityInfo";
//	public static String MoistureDeductAmount = "MoistureDeductAmount";				//ˮ�ֿ���
//	public static String MoistureAmountFlag = "MoistureAmountFlag";					//ˮ�ֿ�����ǣ�0���ٷֱȿۣ�1��������
//	public static String ImpurityDeductAmount = "ImpurityDeductAmount";				//���ʿ���
//	public static String ImpurityAmountFlag = "ImpurityAmountFlag";					//���ʿ�����ǣ�0���ٷֱȿۣ�1��������
//	public static String MoistureDeductPrice = "MoistureDeductPrice";				//����ɹ��
//	public static String MoisturePriceFlag = "MoisturePriceFlag";					//��ɹ�ѿ۷ѱ�ǣ�0���հٷֱȿۣ�1��Ԫ��
//	public static String ImpurityDeductPrice = "ImpurityDeductPrice";				//�����ӷ�
//	public static String ImpurityPriceFlag = "ImpurityPriceFlag";					//�����ӷѱ�ǣ�0���ٷֱȿۣ�1��Ԫ��
//	public static String QualityIndex = "QualityIndex";								//����ָ��
	public static String IndexID = "IndexID";										//����ָ��ID
	public static String IndexValue = "IndexValue";									//����ָ��ֵ
	
//	public static String AdjustQualityInfo = "AdjustQualityInfo";					//ֵ�ֵ��������ۼۣ��������ۣ�
//	public static String RFIDTagID = "RFIDTagID";									//����������RFID��ǩ���Ŀ���ID
//	public static String MoistureDeductAmount = "MoistureDeductAmount";				//ˮ�ֿ���
//	public static String MoistureAmountFlag = "MoistureAmountFlag";					//ˮ�ֿ�����ǣ�0���ٷֱȿۣ�1��������
//	public static String ImpurityDeductAmount = "ImpurityDeductAmount";				//���ʿ���
//	public static String ImpurityAmountFlag = "ImpurityAmountFlag";					//���ʿ�����ǣ�0���ٷֱȿۣ�1��������
//	public static String MoistureDeductPrice = "MoistureDeductPrice";				//����ɹ��
//	public static String MoisturePriceFlag = "MoisturePriceFlag";					//��ɹ�ѿ۷ѱ�ǣ�0���հٷֱȿۣ�1��Ԫ��
//	public static String ImpurityDeductPrice = "ImpurityDeductPrice";				//�����ӷ�
//	public static String ImpurityPriceFlag = "ImpurityPriceFlag";					//�����ӷѱ�ǣ�0���ٷֱȿۣ�1��Ԫ��

//	public static String ResponseMessage = "ResponseMessage";						//Ӧ����Ϣ���ɹ��򷵻�success��ʧ���ڷ���ԭ���ַ���
	
//	public static String TaskingRequest = "TaskingRequest";							//������Ǥ�������б�����
//	public static String LoginUser = "LoginUser";									//��ǰ��¼�û�����
	public static String PageNumber	 = "PageNumber";								//�б�ҳ��
	
//	public static String TaskingMessage = "TaskingMessage";							//������Ǥ��������Ϣ
//	public static String LoginUser = "LoginUser";									//��ǰ��¼�û�����
	public static String TaskTotal = "TaskTotal";									//��������
	public static String TaskCount = "TaskCount";									//����������
//	public static String PageNumber = "PageNumber";									//�б�ҳ��
	public static String Tasks = "Tasks";											//���񼯺�
	public static String Task = "Task";												//ÿҳ����
	public static String number = "number";											//������
	public static String status = "status";											//����״̬
	public static String CreateDate = "CreateDate";									//���񴴽�����
	public static String EnrolNumber = "EnrolNumber";								//���۱��
	public static String CarShipLicense = "CarShipLicense";							//��������
	public static String SampleKind = "SampleKind";									//Ǥ��Ʒ��
	public static String SampleNumber = "SampleNumber";								//��Ʒ���
	
//	public static String CheckingRFIDTagRequest = "CheckingRFIDTagRequest";			//У�鳵��RFID��ǩ����
//	public static String RFIDTagID = "RFIDTagID";									//����RFID��ǩID
	public static String TaskNumber = "TaskNumber";									//��ǰ�����������
	public static String TaskCarShipLicense = "TaskCarShipLicense";					//���񳵴�����
	public static String TaskEnrolNumber = "TaskEnrolNumber";						//���񱨸۱��
	
//	public static String CheckingRFIDMessage = "CheckingRFIDMessage";				//У�鳵��RFID��ǩ��Ϣ
	public static String CheckingResult = "CheckingResult";							//У����	
//	public static String CarShipLicense = "CarShipLicense";							//��������
//	public static String EnrolNumber = "EnrolNumber";								//���۱��
	
//	public static String RegisterSampleRequest = "RegisterSampleRequest";			//�Ǽ���Ʒ����
//	public static String RegisterResult = "RegisterResult";	
	public static String result = "result";
//	public static String TaskNumber = "TaskNumber";									//������
	public static String SampleCount = "SampleCount";								//��Ʒ����
	
//	public static String RegisterSampleMessage = "RegisterSampleMessage";			//�Ǽ���Ʒ��Ϣ
	public static String RegisterSampleResult = "RegisterSampleResult";				//�Ǽ���Ʒ���
//	public static String SampleNumber = "SampleNumber";								//���ص���Ʒ���

//	public static String TaskedRequest = "TaskedRequest";							//�Ѵ���Ǥ�������б�����
//	public static String LoginUser = "LoginUser";									//��ǰ��¼�û�����
//	public static String PageNumber = "PageNumber";									//�б�ҳ��
	public static String PageSize = "PageSize";										//ÿҳ����
	public static String SearchCondition = "SearchCondition";						//��������
//	public static String CarShipLicense = "CarShipLicense";							//��������
//	public static String SampleNumber = "SampleNumber";								//��Ʒ���
	
//	public static String TaskedMessage = "TaskedMessage";							//�Ѵ���Ǥ��������Ϣ
//	public static String LoginUser = "LoginUser";									//��ǰ��¼�û�����
//	public static String TaskTotal = "TaskTotal";									//��������
//	public static String PageSize = "PageSize";										//ÿҳ����
//	public static String PageNumber = "PageNumber";									//�б�ҳ��
//	public static String Tasks = "Tasks";											//���񼯺�
//	public static String Task = "Task";												//ÿҳ����
//	public static String number = "number";											//������
//	public static String status = "status";											//����״̬
//	public static String CreateDate = "CreateDate";									//���񴴽�����
//	public static String EnrolNumber = "EnrolNumber";								//���۱��
//	public static String CarShipLicense = "CarShipLicense";							//��������
//	public static String SampleKind = "SampleKind";									//Ǥ��Ʒ��
//	public static String SampleNumber = "SampleNumber";								//��Ʒ���

	public static String SendMessageRequest = "SendMessageRequest";					//������Ϣ����
	public static String Message = "Message";										//��Ϣ
//	public static String WarehouseID = "WarehouseID";								//�ֿ��ʶ
	public static String Locations = "Locations";									
//	public static String Location = "Location";										//�ֿ��λ
//	public static String LocationID = "LocationID";									//��λID
//	public static String LocationName = "LocationName";								//��λ����
	public static String WarehouseMessage = "WarehouseMessage";						//�ֿ���Ϣ
	
	public static String SendMessageMessage = "SendMessageMessage";					//������Ϣ��Ϣ
//	public static String WarehouseID = "WarehouseID";								//�ֿ��ʶ
	public static String SendResult = "SendResult";									//���ͽ��
}
