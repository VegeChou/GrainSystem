/**
 * 
 */
package com.aisino.grain.beans;

/**
 * @author zwz
 * @date 2013-10-10
 * @description
 *
 */
public class DeductedInfo {
	private boolean IsDeducted = false;
	private int AssayOperatorID = 0;
	private double MoistureDeducted = 0;
	private int MoistureDeductedMode = 0;
	private double ImpurityDeducted = 0;
	private int ImpurityDeductedMode = 0;
	private double IncidentalExpensesDeducted = 0;
	private int IncidentalExpensesDeductedMode = 0;
	private double BakedExpensesDeducted = 0;
	private int BakedExpensesDeductedMode = 0;
	public boolean getIsDeducted() {
		return IsDeducted;
	}
	public void setIsDeducted(boolean isDeducted) {
		IsDeducted = isDeducted;
	}
	public int getAssayOperatorID() {
		return AssayOperatorID;
	}
	public void setAssayOperatorID(int assayOperatorID) {
		AssayOperatorID = assayOperatorID;
	}
	public double getMoistureDeducted() {
		return MoistureDeducted;
	}
	public void setMoistureDeducted(double moistureDeducted) {
		MoistureDeducted = moistureDeducted;
	}
	public int getMoistureDeductedMode() {
		return MoistureDeductedMode;
	}
	public void setMoistureDeductedMode(int moistureDeductedMode) {
		MoistureDeductedMode = moistureDeductedMode;
	}
	public double getImpurityDeducted() {
		return ImpurityDeducted;
	}
	public void setImpurityDeducted(double impurityDeducted) {
		ImpurityDeducted = impurityDeducted;
	}
	public int getImpurityDeductedMode() {
		return ImpurityDeductedMode;
	}
	public void setImpurityDeductedMode(int impurityDeductedMode) {
		ImpurityDeductedMode = impurityDeductedMode;
	}
	public double getIncidentalExpensesDeducted() {
		return IncidentalExpensesDeducted;
	}
	public void setIncidentalExpensesDeducted(double incidentalExpensesDeducted) {
		IncidentalExpensesDeducted = incidentalExpensesDeducted;
	}
	public int getIncidentalExpensesDeductedMode() {
		return IncidentalExpensesDeductedMode;
	}
	public void setIncidentalExpensesDeductedMode(int incidentalExpensesDeductedMode) {
		IncidentalExpensesDeductedMode = incidentalExpensesDeductedMode;
	}
	public double getBakedExpensesDeducted() {
		return BakedExpensesDeducted;
	}
	public void setBakedExpensesDeducted(double bakedExpensesDeducted) {
		BakedExpensesDeducted = bakedExpensesDeducted;
	}
	public int getBakedExpensesDeductedMode() {
		return BakedExpensesDeductedMode;
	}
	public void setBakedExpensesDeductedMode(int bakedExpensesDeductedMode) {
		BakedExpensesDeductedMode = bakedExpensesDeductedMode;
	}
	
}
