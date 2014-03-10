package com.aisino.grain.beans;

public class EnrollInfoGeneral {
	private int Singed = 0;					//RFID卡标志位
	private int GrainType = 0;				//粮食品种ID
	private int VehiceType = 0;				//车辆类型0:内;1:外
	private int BusinessType = 0;			//业务类型1:入库;2:出库;3:倒仓
	private int WorkWarehouseID = 0;		//作业仓库ID
	private int WorkNode = 0;				//作业环节
	private int OperatorID = 0;				//值仓人员ID
	private int FirstWeight = 0;			//0:先皮;1:先毛
	private String WorkNumber = null;		//作业编号
	private String VehiclePlate = null;		//车船号
	private String GoodsOwner = null;		//货主姓名
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
