package com.aisino.grain.beans;

public class EnrollInfoGeneral {
	private int Singed = 0;					//RFID����־λ
	private int GrainType = 0;				//��ʳƷ��ID
	private int VehiceType = 0;				//��������0:��;1:��
	private int BusinessType = 0;			//ҵ������1:���;2:����;3:����
	private int WorkWarehouseID = 0;		//��ҵ�ֿ�ID
	private int WorkNode = 0;				//��ҵ����
	private int OperatorID = 0;				//ֵ����ԱID
	private int FirstWeight = 0;			//0:��Ƥ;1:��ë
	private String WorkNumber = null;		//��ҵ���
	private String VehiclePlate = null;		//������
	private String GoodsOwner = null;		//��������
	public int getSinged() {
		return Singed;
	}
	public void setSinged(int singed) {
		Singed = singed;
	}
	public int getGrainType() {
		return GrainType;
	}
	public void setGrainType(int grainType) {
		GrainType = grainType;
	}
	public int getVehiceType() {
		return VehiceType;
	}
	public void setVehiceType(int vehiceType) {
		VehiceType = vehiceType;
	}
	public int getBusinessType() {
		return BusinessType;
	}
	public void setBusinessType(int businessType) {
		BusinessType = businessType;
	}
	public int getWorkWarehouseID() {
		return WorkWarehouseID;
	}
	public void setWorkWarehouseID(int workWarehouseID) {
		WorkWarehouseID = workWarehouseID;
	}
	public int getWorkNode() {
		return WorkNode;
	}
	public void setWorkNode(int workNode) {
		WorkNode = workNode;
	}
	public int getOperatorID() {
		return OperatorID;
	}
	public void setOperatorID(int operatorID) {
		OperatorID = operatorID;
	}
	public int getFirstWeight() {
		return FirstWeight;
	}
	public void setFirstWeight(int firstWeight) {
		FirstWeight = firstWeight;
	}
	public String getWorkNumber() {
		return WorkNumber;
	}
	public void setWorkNumber(String workNumber) {
		WorkNumber = workNumber;
	}
	public String getVehiclePlate() {
		return VehiclePlate;
	}
	public void setVehiclePlate(String vehiclePlate) {
		VehiclePlate = vehiclePlate;
	}
	public String getGoodsOwner() {
		return GoodsOwner;
	}
	public void setGoodsOwner(String goodsOwner) {
		GoodsOwner = goodsOwner;
	}
}
