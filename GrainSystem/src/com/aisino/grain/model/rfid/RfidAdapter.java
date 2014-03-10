package com.aisino.grain.model.rfid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.io.UnsupportedEncodingException;

import com.aisino.grain.beans.DeductedInfo;
import com.aisino.grain.beans.EnrollInfoGeneral;
import com.aisino.grain.beans.QualityIndexResultInfo;
import com.aisino.grain.beans.WeighInfo;

import android.content.Context;
import android.util.Log;
import android.os.RfidManager;

public class RfidAdapter{
	private static final String TAG = "RfidAdapter";
	private Context mContext = null;
	private RfidManager mRfidManager = null;
	private final int blocklen = 16;
	private byte[] mOrigindata = null;
	private byte[] mAuthkey = null;
	private final byte PICC_AUTHENT1A = 0x60;
	private String mCardNo = null;  //卡号
	private String mCarNum = null;  //车船号
	private String mOwnerName = null;  //货主
	private String mOperateNum = null;
	private Hex mHex = null;
	private int mCursor1 = 0;
	private int mCursor2 = 0;
	private String mLastCardNo = null;
	private byte mLastOperatedBlock = -4;
	private boolean mNeedReRead = true;
	public final int ADD_ONE = 1;
	public final int DEL_ONE = 2;
	public final int MOD_ONE = 3;

	private Block1Data mBlock1 = null;
	private Block2Data mBlock2 = null;
	private Block4Data mBlock4 = null;
	private Block5Data mBlock5 = null;
	private Block6Data mBlock6 = null;
	private Block8Data mBlock8 = null;
	private Block9Data mBlock9 = null;
	private Block16Data mBlock16 = null;

	private boolean mHasInited = false;
	private InitFlags mInitFlags = new InitFlags();

	private static RfidAdapter instance = null;
	
	public class Block16Etc{
		public String mStroageNo;	//仓号
		public String mPoundNo;		//磅号
		public String mPoundNote;	//磅单号
		public String mPoundWeight; //称重号
		public String mFur;			//毛皮
		public String mRoughWeight;	//重量
	}

	private class InitFlags{
		private boolean mBlock1Flag = false;
		private boolean mBlock2Flag = false;
		private boolean mBlock4Flag = false;
		private boolean mBlock5Flag = false;
		private boolean mBlock6Flag = false;
		private boolean mBlock8Flag = false;
		private boolean mBlock9Flag = false;
		private boolean mBlock16Flag = false;

		void ResetAllFlags(){
			mBlock1Flag = false;
			mBlock2Flag = false;
			mBlock4Flag = false;
			mBlock5Flag = false;
			mBlock6Flag = false;
		 	mBlock8Flag = false;
		 	mBlock9Flag = false;
		 	mBlock9.ClearTreeMap();
			mBlock16Flag = false;
			mBlock16.ClearHaspMap();
		}

		boolean GetBlock1Flag(){
			return mBlock1Flag;
		}
		boolean GetBlock2Flag(){
			return mBlock2Flag;
		}
		boolean GetBlock4Flag(){
			return mBlock4Flag;
		}
		boolean GetBlock5Flag(){
			return mBlock5Flag;
		}
		boolean GetBlock6Flag(){
			return mBlock6Flag;
		}
		boolean GetBlock8Flag(){
			return mBlock8Flag;
		}
		boolean GetBlock9Flag(){
			return mBlock9Flag;
		}
		boolean GetBlock16Flag(){
			return mBlock16Flag;
		}
		void SetBlock1Flag(){
			mBlock1Flag = true;
		}
		void SetBlock2Flag(){
			mBlock2Flag = true;
		}
		void SetBlock4Flag(){
			mBlock4Flag = true;
		}
		void SetBlock5Flag(){
			mBlock5Flag = true;
		}
		void SetBlock6Flag(){
			mBlock6Flag = true;
		}
		void SetBlock8Flag(){
			mBlock8Flag = true;
		}
		void SetBlock9Flag(){
			mBlock9Flag = true;
		}
		void SetBlock16Flag(){
			mBlock16Flag = true;
		}
		
	}

	public static synchronized RfidAdapter getInstance(Context context){
		if (null == instance)
			instance = new RfidAdapter(context);

		return instance;
	}
	
	private RfidAdapter(Context context){
		mContext = context;
		mRfidManager = (RfidManager)mContext.getSystemService(Context.RFID_SERVICE);
		mHex = new Hex();
		mBlock1 = new Block1Data();
		mBlock2 = new Block2Data();
		mBlock4 = new Block4Data();
		mBlock5 = new Block5Data();
		mBlock6 = new Block6Data();
		mBlock8 = new Block8Data();
		mBlock9 = new Block9Data();
		mBlock16 = new Block16Data();
		
		if (null != mRfidManager){
			if (0 == mRfidManager.Open()){
				if (0== mRfidManager.Init()){
				}
			}
		}
	}

	protected void finalize() throws Throwable{
		if (null != mRfidManager){
			mRfidManager.Close();
			mRfidManager = null;
		}
		super.finalize();
	}
	
	private void ResetOriginData(){
		if (null != mOrigindata)
			Arrays.fill(mOrigindata, (byte)0x00);
	}
	
	private boolean isKeyBlock(int block){
		if (0 == (block+1)%4) {
			return true;
		}
		return false;
	}

	public String getTagNo(){
		return mBlock4.getTagNo();
	}
	
	public EnrollInfoGeneral getEnrollInfo() {
		EnrollInfoGeneral enrollInfoCard = new EnrollInfoGeneral();
		EnrollInfoInBlock4 enrollInfoInBlock4 = mBlock4.getEnrollInfoInBlock4();
		enrollInfoCard.setSinged(enrollInfoInBlock4.getSinged());
		enrollInfoCard.setGrainType(enrollInfoInBlock4.getGrainType());
		enrollInfoCard.setVehiceType(enrollInfoInBlock4.getVehiceType());
		enrollInfoCard.setBusinessType(enrollInfoInBlock4.getBusinessType());
		enrollInfoCard.setWorkWarehouseID(enrollInfoInBlock4.getWorkWarehouseID());
		enrollInfoCard.setWorkNode(enrollInfoInBlock4.getWorkNode());
		enrollInfoCard.setOperatorID(enrollInfoInBlock4.getOperatorID());
		enrollInfoCard.setFirstWeight(enrollInfoInBlock4.getFirstWeight());
		enrollInfoCard.setWorkNumber(mBlock5.getOperateNum());
		enrollInfoCard.setVehiclePlate(mBlock6.getCarNum());
		enrollInfoCard.setGoodsOwner(mBlock8.getOwnerName());
		
		return enrollInfoCard;
	}

/*	public String getBusinessNo(){
		return mBlock1.getBusinessNo();
	}*/

	public String getVarietyNo(){
		return mBlock4.getVarietyNo();
	}

	public String getCarType(){
		return mBlock4.getCarType();
	}

	public String getBusinessType(){
		return mBlock4.getBusinessType();
	}

	public String getPrice(){
		return mBlock4.getPrice();
	}

	public String getStorage(){
		return mBlock4.getStorage();
	}

	public String getStorageShift(){
		return mBlock4.getStorageShift();
	}

	public String getOperateLink(){
		return mBlock4.getOperateLink();
	}

	public String getCursor1(){
		return mBlock4.getCursor1();
	}

	public String getCursor2(){
		return mBlock4.getCursor2();
	}
	
	public String getOprID(){
		return mBlock4.getOprID();
	}
	
	public String getGrainGrad(){
		return mBlock4.getGrainGrade();
	}
	
	public String getTareMem(){
		return mBlock4.getTareMem();
	}
	
	public String getFirstWeight(){
		return mBlock4.getFirstWeight();
	}

	public void setTagNo(String tagno){
		mBlock4.setTagNo(tagno);
	}

/*	public void setBusinessNo(String busino){
		mBlock4.setBusinessNo(busino);
	}*/

	public void setVarietyNo(String varno){
		mBlock4.setVarietyNo(varno);
	}

	public void setCarType(String cartype){
		mBlock4.setCarType(cartype);
	}

	void setBusinessType(String busitype){
		mBlock4.setBusinessType(busitype);
	}

	public void setPrice(String price){
		mBlock4.setPrice(price);
	}

	public void setStorage(String storage){
		mBlock4.setStorage(storage);
	}

	public void setStorageShift(String storshift){
		mBlock4.setStorageShift(storshift);
	}

	public void setOperateLink(String oprlink){
		mBlock4.setOperateLink(oprlink);
	}	
	
	public void setOprID(String oprid){
		mBlock4.setOprID(oprid);
	}
	
	public void setGrainGrade(String gg){
		mBlock4.setGrainGrade(gg);
	}
	
	public void setTareMem(String taremem){
		mBlock4.setTareMem(taremem);
	}
	
	public void setFirstWeight(String ft){
		mBlock4.setFirstWeight(ft);
	}

	public int syncBlock4(){
		return mBlock4.syncToCard();
	}

	public String getOperateNum() {
		return mBlock5.getOperateNum();
	}
	
	public String getCarNum(){
		return mBlock6.getCarNum();
	}

	public String getOwnerName(){
		return mBlock8.getOwnerName();
	}
	
	public DeductedInfo getAssayDeductedInfo(){
		return mBlock1.getAssayDeductedInfo();
	}

	public String getF_1(){
		return mBlock1.getF_1();
	}

	public String getF1(){
		return mBlock1.getF1();
	}

	public String getD1(){
		return mBlock1.getD1();
	}

	public String getF2(){
		return mBlock1.getF2();
	}

	public String getD2(){
		return mBlock1.getD2();
	}

	public String getF3(){
		return mBlock1.getF3();
	}

	public String getD3(){
		return mBlock1.getD3();
	}

	public String getF4(){
		return mBlock1.getF4();
	}

	public String getD4(){
		return mBlock1.getD4();
	}

	public void setF_1(String f){
		mBlock1.setF_1(f);
	}

	public void setF1(String f1){
		mBlock1.setF1(f1);
	}

	public void setD1(String d1){
		mBlock1.setD1(d1);
	}

	public void setF2(String f2){
		mBlock1.setF2(f2);
	}

	public void setD2(String d2){
		mBlock1.setD2(d2);
	}

	public void setF3(String f3){
		mBlock1.setF3(f3);
	}

	public void setD3(String d3){
		mBlock1.setD3(d3);
	}

	public void setF4(String f4){
		mBlock1.setF4(f4);
	}

	public void setD4(String d4){
		mBlock1.setD4(d4);
	}

	public int syncBlock1(){
		return mBlock1.syncToCard();
	}
	
	public DeductedInfo getAdjustDeductedInfo(){
		return mBlock2.getAdjustDeductedInfo();
	}
	
	public String getF_2(){
		return mBlock2.getF_2();
	}
	
	public String getF5(){
		return mBlock2.getF5();
	}

	public String getD5(){
		return mBlock2.getD5();
	}

	public String getF6(){
		return mBlock2.getF6();
	}

	public String getD6(){
		return mBlock2.getD6();
	}

	public String getF7(){
		return mBlock2.getF7();
	}

	public String getD7(){
		return mBlock2.getD7();
	}

	public String getF8(){
		return mBlock2.getF8();
	}

	public String getD8(){
		return mBlock2.getD8();
	}

	public void setF_2(String f){
		mBlock2.setF_2(f);
	}
	
	public void setF5(String f5){
		mBlock2.setF5(f5);
	}

	public void setD5(String d5){
		mBlock2.setD5(d5);
	}

	public void setF6(String f6){
		mBlock2.setF6(f6);
	}

	public void setD6(String d6){
		mBlock2.setD6(d6);
	}

	public void setF7(String f7){
		mBlock2.setF7(f7);
	}

	public void setD7(String d7){
		mBlock2.setD7(d7);
	}

	public void setF8(String f8){
		mBlock2.setF8(f8);
	}

	public void setD8(String d8){
		mBlock2.setD8(d8);
	}

	public int syncBlock2(){
		return mBlock2.syncToCard();
	}
	
	public TreeMap<?, ?> getAssayData(){
		return mBlock9.getDataToMap();
	}

	public List<QualityIndexResultInfo> getResultQualityInfos(){
		return mBlock9.getResultQualityInfos();
	}

	public HashMap<?, ?> getWeightData(){
		return mBlock16.getDataToMap();
	}
	
	public WeighInfo getWeighInfo() {
		return mBlock16.getWeightInfo();
	}

	public int addWeightData(Block16Etc block16){
		return mBlock16.modifyData(ADD_ONE, 0, block16);
	}
	
	public int cleanHYData(){
		return mBlock9.ClearAllData();
	}
	
	public int cleanCZData() {
		return mBlock16.ClearAllData();
	}
	
	private int CleanData(byte block){
//		String dataStr = "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF";
		String dataStr = "00000000000000000000000000000000";
		
		try {
			mOrigindata = mHex.hexStr2ByteArr(dataStr);
		}
		catch (Exception e){
			e.printStackTrace();
			return -1;
		}
		
		return WriteBlock(block);
	}
	public synchronized String HasCard(){
		mHasInited = false;
		mInitFlags.ResetAllFlags();
		mLastOperatedBlock = -4;
		
		if(null == mRfidManager)
			return null;

		int count = 0;
		while (0 != mRfidManager.Request()) {
			Log.d(TAG, "Request is error");
			count++;
			if (3 <= count) {
				return null;
			}
			else {
				try {
					Thread.sleep(500);
				} catch (Exception e) {
				}
			}
		}
		/*if (0 != mRfidManager.Request()){
			Log.d(TAG, "Request is error");
			return null;
		}*/

		byte[] cardserno = mRfidManager.Antisel();
		if (null == cardserno){
			Log.d(TAG, "Antisel is error");
			return null;
		}
		mCardNo = mHex.encodeHexStr(cardserno);
		Log.d(TAG, "mCardNo is " + mCardNo);
		if (mCardNo.equals(mLastCardNo)){
			mNeedReRead = false;
		}
		else {
			mLastCardNo = mCardNo;
			mNeedReRead = true;
			String authkey = CalculateKey(mCardNo);
			Log.d(TAG, "auth key is " + authkey);
			if (null != authkey){
				mAuthkey = mHex.decodeHex(authkey.toCharArray());
			}
			else {
				mAuthkey = null;
				return null;
			}
		}
		mHasInited = true;

		return mCardNo;
	}
	
	String CalculateKey(String tagID)
	{

		String tmp = null;
		try {
			//民族、民权、民生-MZMQMS-4D5A4D514D53
			String hexKey = "4D5A4D514D53" + tagID;
	//		Log.d(TAG, "hexKeyStr is " + hexKey);
			MessageDigest md = MessageDigest.getInstance("SHA-1");
	//		md.update(hexKey.getBytes("iso-8859-1"), 0, hexKey.length());
	//		md.update(hexKey.getBytes("ISO646-US"), 0, hexKey.length());
			byte[] sha1hash = md.digest(mHex.hexStr2ByteArr(hexKey));
			Log.d(TAG, "sha1hash len is " + sha1hash.length);
			tmp = new String(mHex.encodeHex(sha1hash));
			Log.d(TAG, "authkey is " + tmp);
		}
		catch (NoSuchAlgorithmException e){
			Log.d(TAG, "NoSuchAlgorithmException ");
			e.printStackTrace();
			return null;
		}
		/*catch (UnsupportedEncodingException e){
			Log.d(TAG, "UnsupportedEncodingException" + tmp);
			e.printStackTrace();
		}*/
		catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		
		return tmp.substring(0,12);

	}

	private synchronized int ReadBlock(byte block, byte keytype){
		boolean needAuth = true;

		Log.d(TAG, "###read NO: " + block + " block" + " mLastOperatedBlock is " + mLastOperatedBlock + "###");
		Log.d(TAG, "block/4 is " + (int)block/4 + " lastblock/4 is " + (int)mLastOperatedBlock/4);

		if ((int)block/4 == (int)mLastOperatedBlock/4){
			needAuth = false;
		}
		else {
			needAuth = true;
		}

		Log.d(TAG, " needAuth is " + needAuth + " mNeedReRead  is " + mNeedReRead);
		for (int i=0; i<mAuthkey.length; i++) {
			Log.d(TAG, " " + mAuthkey[i]);
		}
		
		if (needAuth){
			Log.d(TAG, "ReadBlock begin");
			byte[] cardno = mHex.decodeHex(mCardNo.toCharArray());
//			for (byte b : cardno){
//				Log.d(TAG, " " + b);
//			}
//			Log.d(TAG, "read NO: " + block + "block");
//			Log.d(TAG, "keytype is " + keytype);
//			for (byte d : mAuthkey){
//				Log.d(TAG, " " + d);
//			}
			
			if (!mRfidManager.AuthCheck(cardno, block, keytype, mAuthkey)){
				Log.d(TAG, "auth check is error");
				return -1;
			}
		}
		
		ResetOriginData();
		
		mOrigindata = mRfidManager.ReadBlockData(block);
		if (null == mOrigindata){
			Log.d(TAG, "readblockdata is error");
			return -1;
		}
		mLastOperatedBlock = block;

		return 0;
	}

	private synchronized int WriteBlock(byte block){
//		if (0 != InitCard())
//			return -1;
		boolean needAuth = true;

		Log.d(TAG, "###WriteBlock NO: " + block + " block" + " mLastOperatedBlock is " + mLastOperatedBlock + "###");
		Log.d(TAG, "block/4 is " + (int)block/4 + " lastblock/4 is " + (int)mLastOperatedBlock/4);

		if ((int)block/4 == (int)mLastOperatedBlock/4){
			needAuth = false;
		}
		else {
			needAuth = true;
		}

		if (needAuth){
			Log.d(TAG, "WriteBlock AuthCheck");
			if (!mRfidManager.AuthCheck(mHex.decodeHex(mCardNo.toCharArray()), block, PICC_AUTHENT1A, mAuthkey)){
				Log.d(TAG, "write block auth error");
				return -1;
			}
		}
		if (null == mOrigindata){
			Log.d(TAG, "write data is not correct");
			return -1;
		}

		Log.d(TAG, "mRfidManager.WriteBlockData " + block);
		if (0 == mRfidManager.WriteBlockData(block, mOrigindata)){
//			mWLastBlock = block;
			mLastOperatedBlock = block;
			return 0;
		}
		else {
			Log.d(TAG, "WriteBlockData Error!");
			return -1;
		}
	}

	class Block4Data {
		private final byte BLOCK = 0x04;

		/**
        	 *标记号
        	 */
		String tagNo;
		/**
        	 *业务号
        	 */
//		String businessNo = null;
		/**
        	 *品种号
        	 */
        String varietyNo = null;
		/**
        	 *内外车
        	 */
        String carType;
		/**
        	 *业务类型
        	 */
        String businessType;
		/**
        	 *单价
        	 */
        String price;
		/**
        	 *仓库
        	 */
        String storage;
		/**
        	 *换仓标识
        	 */
        String storageShift;
		/**
        	 *作业环节
        	 */
        String operateLink;
		/**
        	 *游标1
        	 */
        String cursor1;
		/**
        	 *游标2
        	 */
        String cursor2;
        
        /**
    	 *值仓人员ID
    	 */
	    String oprID;
	    
	    /**
		 *粮食等级
		 */
	    String grainGrade;
	    
	    /**
		 *皮重记忆
		 */
	    String tareMem;
	    
	    /**
		 *先称毛/皮
		 */
	    String firstWeight;
	    
	    EnrollInfoInBlock4 enrollInfoInBlock4;

		private boolean parseData(){
			String mData = mHex.encodeHexStr(mOrigindata);
			
			Log.d(TAG, "mData length is " + mData.length());
			tagNo = String.valueOf(mData.charAt(0));
			
			varietyNo = String.valueOf(Integer.parseInt(mData.substring(1,5), 16));
			carType = String.valueOf(mData.charAt(5));
			businessType = String.valueOf(mData.charAt(6));
			price = String.valueOf(Integer.parseInt(mData.substring(7,15), 16));
			storage = String.valueOf(Integer.parseInt(mData.substring(15,18), 16));
			storageShift = String.valueOf(mData.charAt(18));
			operateLink = String.valueOf(mData.charAt(19));
			mCursor1 = Integer.parseInt(mData.substring(20,22), 16);
			mCursor2 = Integer.parseInt(mData.substring(22,24), 16);
			Log.d(TAG, "mCursor1 = " + mCursor1 + " mCursor2 = " + mCursor2);
			cursor1 = String.valueOf(mCursor1);
			cursor2 = String.valueOf(mCursor2);
			oprID = String.valueOf(Integer.parseInt(mData.substring(24,29), 16));
			grainGrade = String.valueOf(mData.charAt(29));
			tareMem = String.valueOf(mData.charAt(30));
			firstWeight = String.valueOf(mData.charAt(31));
			
//			mData = mHex.byteArr2HexStr(mOrigindata);
			
			enrollInfoInBlock4 = new EnrollInfoInBlock4();
			enrollInfoInBlock4.setSinged(Integer.valueOf(tagNo));
			enrollInfoInBlock4.setGrainType(Integer.valueOf(varietyNo));
			enrollInfoInBlock4.setVehiceType(Integer.valueOf(carType));
			enrollInfoInBlock4.setBusinessType(Integer.valueOf(businessType));
			enrollInfoInBlock4.setWorkWarehouseID(Integer.valueOf(storage));
			enrollInfoInBlock4.setWorkNode(Integer.valueOf(operateLink));
			enrollInfoInBlock4.setOperatorID(Integer.valueOf(oprID));
			enrollInfoInBlock4.setFirstWeight(Integer.valueOf(firstWeight));
			
			return true;
		}

		private boolean InitBlock4Data(){
/*
			if (0 != InitCard()) {
				return false;
			}
			Log.d(TAG, "mNeedReRead is " + mNeedReRead + " mInitFlag is " + mInitFlag);
			if ((!mNeedReRead) && mInitFlag){
				return true;
			}
*/			
			
			if (mInitFlags.GetBlock4Flag())
				return true;

			if (!mHasInited)
				return false;
			
			Log.d(TAG, "@@@Read block4@@@");
			if (0 != ReadBlock(BLOCK, PICC_AUTHENT1A)){
				return false;
			}
			if (!parseData()){
				return false;
			}

			mInitFlags.SetBlock4Flag();
			
			return true;
		}
		
		String getTagNo(){
			if (InitBlock4Data()){
				return tagNo;
			}
			else {
				return null;
			}
		}

/*		String getBusinessNo(){
			if (InitBlock1Data()){
				return businessNo;
			}
			else {
				return null;
			}
		}*/
		
		String getVarietyNo(){
			if (InitBlock4Data()){
				return varietyNo;
			}
			else {
				return null;
			}
		}

		EnrollInfoInBlock4 getEnrollInfoInBlock4(){
			if (InitBlock4Data()){
				return enrollInfoInBlock4;
			}
			else {
				return null;
			}
		}

		String getCarType(){
			if (InitBlock4Data()){	
				return carType;
			}
			else {
				return null;
			}
		}

		String getBusinessType(){
			if (InitBlock4Data()){
				return businessType;
			}
			else {
				return null;
			}
		}

		String getPrice(){
			if (InitBlock4Data()){
				return price;
			}
			else {
				return null;
			}
		}

		String getStorage(){
			if (InitBlock4Data()){
				return storage;
			}
			else {
				return null;
			}
		}

		String getStorageShift(){
			if (InitBlock4Data()){
				return storageShift;
			}
			else {
				return null;
			}
		}

		String getOperateLink(){
			if (InitBlock4Data()){
				return operateLink;
			}
			else {
				return null;
			}
		}

		String getCursor1(){
			if (InitBlock4Data()){
				return cursor1;
			}
			else {
				return null;
			}
		}

		String getCursor2(){
			if (InitBlock4Data()){
				return cursor2;
			}
			else {
				return null;
			}
		}
		
		String getOprID(){
			if (InitBlock4Data()){
				return oprID;
			}
			else {
				return null;
			}
		}
		
		String getGrainGrade(){
			if (InitBlock4Data()){
				return grainGrade;
			}
			else {
				return null;
			}
		}
		
		String getTareMem(){
			if (InitBlock4Data()){
				return tareMem;
			}
			else {
				return null;
			}
		}
		
		String getFirstWeight(){
			if (InitBlock4Data()){
				return firstWeight;
			}
			else {
				return null;
			}
		}

		void setTagNo(String tagno){
			Log.d(TAG, "setTagNo is " + tagno);
			this.tagNo = tagno;
		}

/*		void setBusinessNo(String busino){
			Log.d(TAG, "setBusinessNo is " + busino);
			this.businessNo = busino;
		}*/

		void setVarietyNo(String varno){
			Log.d(TAG, "setVarietyNo is " + varno);
			if (null != varno) {
				this.varietyNo = varno;
			}
		}

		void setCarType(String cartype){
			Log.d(TAG, "setCarType is " + cartype);
			if (null != cartype) {
				this.carType = cartype;
			}
		}

		void setBusinessType(String busitype){
			Log.d(TAG, "setBusinessType is " + busitype);
			if (null != busitype) {
				this.businessType = busitype;
			}
		}

		void setPrice(String price){
			Log.d(TAG, "setPrice is " + price);
			if (null != price) {
				this.price = price;
			}
		}

		void setStorage(String storage){
			Log.d(TAG, "setStorage is " + storage);
			if (null != storage) {
				this.storage = storage;
			}
		}

		void setStorageShift(String storshift){
			Log.d(TAG, "setStorageShift is " + storshift);
			if (null != storshift) {
				this.storageShift = storshift;
			}
		}

		void setOperateLink(String oprlink){
			Log.d(TAG, "setOperateLink is " + oprlink);
			if (null != oprlink) {
				this.operateLink = oprlink;
			}
		}

		void setCursor1(String cursor){
			Log.d(TAG, "setCursor1 is " + cursor);
			if (null != cursor) {
				this.cursor1= cursor;
			}
		}

		void setCursor2(String cursor){
			Log.d(TAG, "setCursor2 is " + cursor);
			if (null != cursor) {
				this.cursor2= cursor;
			}
		}
		
		void setOprID(String oprid){
			if (null != oprid) {
				this.oprID = oprid;
			}
		}
		
		void setGrainGrade(String gg){
			if (null != gg) {
				this.grainGrade = gg;
			}
		}
		
		void setTareMem(String taremem){
			if (null !=  taremem) {
				this.tareMem = taremem;
			}
		}
		void setFirstWeight(String ft){
			if (null !=  ft) {
				this.firstWeight = ft;
			}
		}

		int syncToCard(){
			String tmp = "";
			String temp = "";

			Log.d(TAG, "Block1data synctocard step1");

			if (0 == tagNo.length() || 1 < tagNo.length()){
				Log.d(TAG, "tagNo data is unavalible");
				return -1;
			}
			else {
				tmp += Integer.toString(Integer.parseInt(tagNo));
				Log.d(TAG, "step1 tmp is " + tmp);
			}

			Log.d(TAG, "Block1data synctocard step2");
/*			if (0 == businessNo.length() || 10 < businessNo.length()){
				Log.d(TAG, "businessNo data is unavalible");
				return -1;
			}
			else {
				temp = mHex.addZeroForNum(Integer.toHexString(Integer.parseInt(businessNo)), 10);
				tmp += temp;
				Log.d(TAG, "step2 tmp is " + tmp);
			}*/

			Log.d(TAG, "Block1data synctocard step3");
			if (0 == varietyNo.length()|| 4 < varietyNo.length()){
				Log.d(TAG, "varietyNo data is unavalible");
				return -1;
			}else{
				temp = mHex.addZeroForNum(varietyNo,4);
				tmp += temp;
				Log.d(TAG, "step3 tmp is " + tmp);
			}

			Log.d(TAG, "Block1data synctocard step4");
			if (0 == carType.length() || 1 < carType.length()){
				Log.d(TAG, "carType data is unavalible");
				return -1;
			}
			else {
				tmp += Integer.toString(Integer.parseInt(carType));
				Log.d(TAG, "step4 tmp is " + tmp);
			}

			Log.d(TAG, "Block1data synctocard step5");
			if (0 == businessType.length() || 1 < businessType.length()){
				Log.d(TAG, "businessType data is unavalible");
				return -1;
			}
			else {
				tmp += Integer.toString(Integer.parseInt(businessType));
				Log.d(TAG, "step5 tmp is " + tmp);
			}

			Log.d(TAG, "Block1data synctocard step6");
			if (0 == price.length() || 8 < price.length()){
				Log.d(TAG, "price data is unavalible");
				return -1;
			}
			else {
				temp = mHex.addZeroForNum(Integer.toHexString(Integer.parseInt(price)), 8);
				tmp += temp;
				Log.d(TAG, "step6 tmp is " + tmp);
			}

			Log.d(TAG, "Block1data synctocard step7");
			if (0 == storage.length() || 3 < storage.length()){
				Log.d(TAG, "price data is unavalible");
				return -1;
			}
			else {
				temp = mHex.addZeroForNum(Integer.toHexString(Integer.parseInt(storage)), 3);
				tmp += temp;
				Log.d(TAG, "step7 tmp is " + tmp);
			}

			Log.d(TAG, "Block1data synctocard step8");
			if (0 == storageShift.length() || 1 < storageShift.length()){
				Log.d(TAG, "storageShift data is unavalible");
				return -1;
			}
			else {
				tmp += Integer.toString(Integer.parseInt(storageShift));
				Log.d(TAG, "step8 tmp is " + tmp);
			}

			Log.d(TAG, "Block1data synctocard step9");
			if (0 == operateLink.length() || 1 < operateLink.length()){
				Log.d(TAG, "operateLink data is unavalible");
				return -1;
			}
			else {
				tmp += Integer.toString(Integer.parseInt(operateLink));
				Log.d(TAG, "step9 tmp is " + tmp);
			}

			Log.d(TAG, "Block1data synctocard step10");
			if (0 == cursor1.length() || 2 < cursor1.length()){
				Log.d(TAG, "cursor1 data is unavalible");
				return -1;
			}
			else {
				temp = mHex.addZeroForNum(Integer.toHexString(Integer.parseInt(cursor1)), 2);
				tmp += temp;
				Log.d(TAG, "step10 tmp is " + tmp);
			}

			Log.d(TAG, "Block1data synctocard step11");
			if (0 == cursor2.length() || 2 < cursor2.length()){
				Log.d(TAG, "cursor2 data is unavalible");
				return -1;
			}
			else {
				temp = mHex.addZeroForNum(Integer.toHexString(Integer.parseInt(cursor2)), 2);
				tmp += temp;
				Log.d(TAG, "step11 tmp is " + tmp);
			}
			
			Log.d(TAG, "Block1data synctocard step12");
			if (0 == oprID.length() || 5 < oprID.length()){
				Log.d(TAG, "oprID data is unavalible");
				return -1;
			}
			else {
				temp = mHex.addZeroForNum(Integer.toHexString(Integer.parseInt(oprID)), 5);
				tmp += temp;
				Log.d(TAG, "step12 tmp is " + tmp);
			}
			
			Log.d(TAG, "Block1data synctocard step13");
			if (0 == grainGrade.length() || 1 < grainGrade.length()){
				Log.d(TAG, "grainGrade data is unavalible");
				return -1;
			}
			else {
				tmp += Integer.toString(Integer.parseInt(grainGrade));
				Log.d(TAG, "step13 tmp is " + tmp);
			}
			
			Log.d(TAG, "Block1data synctocard step14");
			if (0 == tareMem.length() || 1 < tareMem.length()){
				Log.d(TAG, "tareMem data is unavalible");
				return -1;
			}
			else {
				tmp += Integer.toString(Integer.parseInt(tareMem));
				Log.d(TAG, "step14 tmp is " + tmp);
			}
			
			Log.d(TAG, "Block1data synctocard step15");
			if (0 == firstWeight.length() || 1 < firstWeight.length()){
				Log.d(TAG, "firstWeight data is unavalible");
				return -1;
			}
			else {
				tmp += Integer.toString(Integer.parseInt(firstWeight));
				Log.d(TAG, "step15 tmp is " + tmp);
			}

/*			temp = mHex.addZeroForNum(Integer.toHexString(0), 2);
			tmp += temp;
			Log.d(TAG, "step12 tmp is " + tmp);*/

			Log.d(TAG, "syncblock1 datastr is " + tmp);

			try{
				mOrigindata = mHex.hexStr2ByteArr(tmp);
				Log.d(TAG, "sync data hex data len is " + mOrigindata.length);
				if (mOrigindata.length > 16){
					return -1;
				}
			}
			catch (Exception e){
				e.printStackTrace();
				return -1;
			}
			
			return WriteBlock(BLOCK);
		}
		
	}
	
	class Block5Data {
		private final byte BLOCK = 0x05;

		String getOperateNum(){
/*
			if (0 != InitCard()) {
				return null;
			}
*/
			if (!mHasInited)
				return null;

			if (mInitFlags.GetBlock5Flag())
				return mOperateNum;
			
			if (0 != ReadBlock(BLOCK, PICC_AUTHENT1A)){
				mOperateNum = null;
				return null;
			}

//			mCarNum = mHex.encodeHexStr(mOrigindata);
			
//			String tmp = mHex.encodeHexStr(mOrigindata);
//			mCarNum = mHex.convertHexStrToAsciiStr(tmp);

			try{
				mOperateNum = new String(mOrigindata, "GBK");
			}
			catch(UnsupportedEncodingException e){
				Log.d(TAG, "GBK is not supported");
				mOperateNum = "";
			}
			mInitFlags.SetBlock5Flag();
			return mOperateNum;
		}

		boolean setOperateNum(String num){
			if (!mHasInited)
				return false;

			if (!mInitFlags.GetBlock5Flag())
				return false;
			
			mOperateNum = num;
			this.syncToCard();

			return true;
		}

		int syncToCard(){
			if (null != mOperateNum){
				if (mOperateNum.length() > blocklen)
					return -1;
				
				mOrigindata = mHex.decodeHex(mOperateNum.toCharArray());
				return WriteBlock(BLOCK);
			}
			
			return -1;
		}
	}

	class Block6Data {
		private final byte BLOCK = 0x06;

		String getCarNum(){
/*
			if (0 != InitCard()) {
				return null;
			}
*/
			if (!mHasInited)
				return null;

			if (mInitFlags.GetBlock6Flag())
				return mCarNum;
			
			if (0 != ReadBlock(BLOCK, PICC_AUTHENT1A)){
				mCarNum = null;
				return null;
			}

//			mCarNum = mHex.encodeHexStr(mOrigindata);
			
//			String tmp = mHex.encodeHexStr(mOrigindata);
//			mCarNum = mHex.convertHexStrToAsciiStr(tmp);

			try{
				mCarNum = new String(mOrigindata, "GBK");
			}
			catch(UnsupportedEncodingException e){
				Log.d(TAG, "GBK is not supported");
				mCarNum = "";
			}
			mInitFlags.SetBlock6Flag();
			mCarNum = mCarNum.trim();
			return mCarNum;
		}

		boolean setCarNum(String carnum){
			if (!mHasInited)
				return false;

			if (!mInitFlags.GetBlock6Flag())
				return false;
			
			mCarNum = carnum;
			this.syncToCard();

			return true;
		}

		int syncToCard(){
			if (null != mCarNum){
				if (mCarNum.length() > blocklen)
					return -1;
				
				mOrigindata = mHex.decodeHex(mCarNum.toCharArray());
				return WriteBlock(BLOCK);
			}
			
			return -1;
		}
	}

	class Block8Data {
		private final byte BLOCK = 0x08;

		String getOwnerName(){
/*
			if (0 != InitCard()) {
				return null;
			}
*/			

			if (!mHasInited)
				return null;

			if (mInitFlags.GetBlock8Flag())
				return mOwnerName;
			
			if (0 != ReadBlock(BLOCK, PICC_AUTHENT1A)){
				mOwnerName = null;
				return null;
			}

//			mOwnerName= mHex.encodeHexStr(mOrigindata);

//			String tmp = mHex.encodeHexStr(mOrigindata);
//			mOwnerName= mHex.convertHexStrToAsciiStr(tmp);

			try{
				mOwnerName =  new String(mOrigindata, "GBK");
			}
			catch(UnsupportedEncodingException e){
				Log.d(TAG, "GBK is not supported");
				mOwnerName = "";
			}
			mInitFlags.SetBlock8Flag();
			mOwnerName = mOwnerName.trim();
			return mOwnerName;
		}

		boolean setOwnerName(String ownername){
			if (!mHasInited)
				return false;

			if (!mInitFlags.GetBlock8Flag())
				return false;
			
			mOwnerName = ownername;
			this.syncToCard();

			return true;
		}

		int syncToCard(){
			if (null != mOwnerName){
				if (mOwnerName.length() > blocklen)
					return -1;
				
				mOrigindata = mHex.decodeHex(mOwnerName.toCharArray());
				return WriteBlock(BLOCK);
			}
			
			return -1;
		}
	}

	class Block1Data {
		private final byte BLOCK = 0x01;
		String F_1 = null;
		String F1 = null;
		String D1 = null;
		String F2 = null;
		String D2 = null;
		String F3 = null;
		String D3 = null;
		String F4 = null;
		String D4 = null;
		DeductedInfo AssayDeductedInfo = null;


		private boolean InitBlock1Data(){
/*			if (0 != InitCard()) {
				return false;
			}

			if ((!mNeedReRead) && mInitFlag){
				return true;
			}
*/
			if (mInitFlags.GetBlock1Flag())
				return true;

			if (!mHasInited)
				return false;
			
			if (0 != ReadBlock(BLOCK, PICC_AUTHENT1A)){
				return false;
			}
			if (!parseData()){
				return false;
			}

			mInitFlags.SetBlock1Flag();
			
			return true;
		}
		boolean parseData(){			
			String tmp = mHex.encodeHexStr(mOrigindata);

			F_1 = String.valueOf(Integer.parseInt(tmp.substring(0, 4), 16));
			F1 = String.valueOf(Integer.parseInt(tmp.substring(4, 6), 16));
			D1 = String.valueOf(Integer.parseInt(tmp.substring(6, 10), 16));
			F2 = String.valueOf(Integer.parseInt(tmp.substring(10, 12), 16));
			D2 = String.valueOf(Integer.parseInt(tmp.substring(12, 16), 16));
			F3 = String.valueOf(Integer.parseInt(tmp.substring(16, 18), 16));
			D3 = String.valueOf(Integer.parseInt(tmp.substring(18, 24), 16));
			F4 = String.valueOf(Integer.parseInt(tmp.substring(24, 26), 16));
			D4 = String.valueOf(Integer.parseInt(tmp.substring(26, 32), 16));
			
			AssayDeductedInfo = new DeductedInfo();
			
			if (F_1.equals("0")) {
				AssayDeductedInfo.setIsDeducted(false);
			} else if (F_1.equals("1")) {
				AssayDeductedInfo.setIsDeducted(true);
			}
			
			AssayDeductedInfo.setAssayOperatorID(0);
			
			if (F1.equals("0")) {
				AssayDeductedInfo.setMoistureDeducted(Double.valueOf(D1));
			} else if (F1.equals("1")) {
				AssayDeductedInfo.setMoistureDeducted(Double.valueOf(D1)/100);
			}
			AssayDeductedInfo.setMoistureDeductedMode(Integer.valueOf(F1));
			
			if (F2.equals("0")) {
				AssayDeductedInfo.setImpurityDeducted(Double.valueOf(D2));
			} else if (F2.equals("1")) {
				AssayDeductedInfo.setImpurityDeducted(Double.valueOf(D2)/100);
			}
			AssayDeductedInfo.setImpurityDeductedMode(Integer.valueOf(F2));
			
			if (F3.equals("0")) {
				AssayDeductedInfo.setIncidentalExpensesDeducted(Double.valueOf(D3));
			} else if (F3.equals("1")) {
				AssayDeductedInfo.setIncidentalExpensesDeducted(Double.valueOf(D3)/100);
			}
			AssayDeductedInfo.setIncidentalExpensesDeductedMode(Integer.valueOf(F3));
			
			if (F4.equals("0")) {
				AssayDeductedInfo.setBakedExpensesDeducted(Double.valueOf(D4));
			} else if (F4.equals("1")) {
				AssayDeductedInfo.setBakedExpensesDeducted(Double.valueOf(D4)/100);
			}
			AssayDeductedInfo.setBakedExpensesDeductedMode(Integer.valueOf(F4));
			
			return true;
		}
		
		DeductedInfo getAssayDeductedInfo(){
			if (InitBlock1Data()){
				return AssayDeductedInfo;
			}
			else {
				return null;
			}
		}
		
		String getF_1(){
			if (InitBlock1Data()){
				return F_1;
			}
			else {
				return null;
			}
		}

		String getF1(){
			if (InitBlock1Data()){
				return F1;
			}
			else {
				return null;
			}
		}

		String getD1(){
			if (InitBlock1Data()){
				return D1;
			}
			else {
				return null;
			}
		}
		
		String getF2(){
			if (InitBlock1Data()){
				return F2;
			}
			else {
				return null;
			}
		}

		String getD2(){
			if (InitBlock1Data()){
				return D2;
			}
			else {
				return null;
			}
		}

		String getF3(){
			if (InitBlock1Data()){
				return F3;
			}
			else {
				return null;
			}
		}

		String getD3(){
			if (InitBlock1Data()){
				return D3;
			}
			else {
				return null;
			}
		}

		String getF4(){
			if (InitBlock1Data()){
				return F4;
			}
			else {
				return null;
			}
		}

		String getD4(){
			if (InitBlock1Data()){
				return D4;
			}
			else {
				return null;
			}
		}

		void setF_1(String f){
			this.F_1 = f;
		}

		void setF1(String f1){
			this.F1 = f1;
		}

		void setD1(String d1){
			this.D1 = d1;
		}

		void setF2(String f2){
			this.F2 = f2;
		}

		void setD2(String d2){
			this.D2 = d2;
		}

		void setF3(String f3){
			this.F3 = f3;
		}

		void setD3(String d3){
			this.D3 = d3;
		}

		void setF4(String f4){
			this.F4 = f4;
		}

		void setD4(String d4){
			this.D4 = d4;
		}

		int syncToCard(){

/*
			mOrigindata[0] = F;
			mOrigindata[1] = F1;
			System.arraycopy(D1,0 , mOrigindata, 2, D1.length);
			mOrigindata[4] = F2;
			System.arraycopy(D2,0 , mOrigindata, 5, D2.length);
			mOrigindata[7] = F3;
			System.arraycopy(D3,0 , mOrigindata, 8, D3.length);
			mOrigindata[11] = F4;
			System.arraycopy(D4,0 , mOrigindata, 12, D4.length);
*/
			String tmp = "";
			String temp = "";
			
			temp = mHex.addZeroForNum(Integer.toHexString(Integer.parseInt(F_1)), 4);
			tmp += temp;
			Log.d(TAG, "step1 tmp is " + tmp);
			
			temp = mHex.addZeroForNum(Integer.toHexString(Integer.parseInt(F1)), 2);
			tmp += temp;
			Log.d(TAG, "step2 tmp is " + tmp);

			temp = mHex.addZeroForNum(Integer.toHexString(Integer.parseInt(D1)), 4);
			tmp += temp;
			Log.d(TAG, "step3 tmp is " + tmp);

			temp = mHex.addZeroForNum(Integer.toHexString(Integer.parseInt(F2)), 2);
			tmp += temp;
			Log.d(TAG, "step4 tmp is " + tmp);

			temp = mHex.addZeroForNum(Integer.toHexString(Integer.parseInt(D2)), 4);
			tmp += temp;
			Log.d(TAG, "step5 tmp is " + tmp);

			temp = mHex.addZeroForNum(Integer.toHexString(Integer.parseInt(F3)), 2);
			tmp += temp;
			Log.d(TAG, "step6 tmp is " + tmp);

			temp = mHex.addZeroForNum(Integer.toHexString(Integer.parseInt(D3)), 6);
			tmp += temp;
			Log.d(TAG, "step7 tmp is " + tmp);

			temp = mHex.addZeroForNum(Integer.toHexString(Integer.parseInt(F4)), 2);
			tmp += temp;
			Log.d(TAG, "step8 tmp is " + tmp);

			temp = mHex.addZeroForNum(Integer.toHexString(Integer.parseInt(D4)), 6);
			tmp += temp;
			Log.d(TAG, "step9 tmp is " + tmp);

//			temp = mHex.addZeroForNum(Integer.toHexString(0), 2);
//			tmp += temp;
//			Log.d(TAG, "step10 tmp is " + tmp);
			

//			mOrigindata = mHex.decodeHex(tmp.toCharArray());
			try {
				mOrigindata = mHex.hexStr2ByteArr(tmp);
				Log.d(TAG, "sync data len is " + mOrigindata.length);
			}
			catch (Exception e){
				e.printStackTrace();
				return -1;
			}
			
			return WriteBlock(BLOCK);
		}
	}

	class Block2Data {
		private final byte BLOCK = 0x02;
		String F_2 = null;
		String F5 = null;
		String D5 = null;
		String F6 = null;
		String D6 = null;
		String F7 = null;
		String D7 = null;
		String F8 = null;
		String D8 = null;
		DeductedInfo AdjustDeductedInfo = null;

		private boolean InitBlock2Data(){
/*			if (0 != InitCard()) {
				return false;
			}

			if ((!mNeedReRead) && mInitFlag){
				return true;
			}
*/
			if (mInitFlags.GetBlock2Flag())
				return true;

			if (!mHasInited)
				return false;
			
			if (0 != ReadBlock(BLOCK, PICC_AUTHENT1A)){	
				return false;
			}
			if (!parseData()){
				return false;
			}

			mInitFlags.SetBlock2Flag();
			
			return true;
		}
		boolean parseData(){
			String tmp = mHex.encodeHexStr(mOrigindata);
			int value = 0;
			Integer val = null;
			
			value = Integer.parseInt(tmp.substring(0, 4), 16);
			val = new Integer(value);
			F_2 = val.toString();

			value = Integer.parseInt(tmp.substring(4, 6), 16);
			val = new Integer(value);
			F5 = val.toString();

			value = Integer.parseInt(tmp.substring(6, 10), 16);
			val = new Integer(value);
			D5 = val.toString();

			value = Integer.parseInt(tmp.substring(10, 12), 16);
			val = new Integer(value);
			F6 = val.toString();

			value = Integer.parseInt(tmp.substring(12, 16), 16);
			val = new Integer(value);
			D6 = val.toString();

			value = Integer.parseInt(tmp.substring(16, 18), 16);
			val = new Integer(value);
			F7 = val.toString();

			value = Integer.parseInt(tmp.substring(18, 24), 16);
			val = new Integer(value);
			D7 = val.toString();

			value = Integer.parseInt(tmp.substring(24, 26), 16);
			val = new Integer(value);
			F8 = val.toString();

			value = Integer.parseInt(tmp.substring(26, 32), 16);
			val = new Integer(value);
			D8 = val.toString();
			
			AdjustDeductedInfo = new DeductedInfo();
			if (F_2.equals("0")) {
				AdjustDeductedInfo.setIsDeducted(false);
			} else if (F_2.equals("1")) {
				AdjustDeductedInfo.setIsDeducted(true);
			}
			
			AdjustDeductedInfo.setAssayOperatorID(0);
			
			if (F5.equals("0")) {
				AdjustDeductedInfo.setMoistureDeducted(Double.valueOf(D5));
			} else if (F5.equals("1")) {
				AdjustDeductedInfo.setMoistureDeducted(Double.valueOf(D5)/100);
			}
			AdjustDeductedInfo.setMoistureDeductedMode(Integer.valueOf(F5));
			
			if (F6.equals("0")) {
				AdjustDeductedInfo.setImpurityDeducted(Double.valueOf(D6));
			} else if (F6.equals("1")) {
				AdjustDeductedInfo.setImpurityDeducted(Double.valueOf(D6)/100);
			}
			AdjustDeductedInfo.setImpurityDeductedMode(Integer.valueOf(F6));
			
			if (F7.equals("0")) {
				AdjustDeductedInfo.setIncidentalExpensesDeducted(Double.valueOf(D7));
			} else if (F7.equals("1")) {
				AdjustDeductedInfo.setIncidentalExpensesDeducted(Double.valueOf(D7)/100);
			}
			AdjustDeductedInfo.setIncidentalExpensesDeductedMode(Integer.valueOf(F7));
			
			if (F8.equals("0")) {
				AdjustDeductedInfo.setBakedExpensesDeducted(Double.valueOf(D8));
			} else if (F8.equals("1")) {
				AdjustDeductedInfo.setBakedExpensesDeducted(Double.valueOf(D8)/100);
			}
			AdjustDeductedInfo.setBakedExpensesDeductedMode(Integer.valueOf(F8));
			
			return true;
		}
		
		DeductedInfo getAdjustDeductedInfo(){
			if (InitBlock2Data()){
				return AdjustDeductedInfo;
			}
			else {
				return null;
			}
		}

		String getF_2(){
			if (InitBlock2Data()){
				return F_2;
			}
			else {
				return null;
			}
		}
		
		String getF5(){
			if (InitBlock2Data()){
				return F5;
			}
			else {
				return null;
			}
		}

		String getD5(){
			if (InitBlock2Data()){
				return D5;
			}
			else {
				return null;
			}
		}

		String getF6(){
			if (InitBlock2Data()){
				return F6;
			}
			else {
				return null;
			}
		}

		String getD6(){
			if (InitBlock2Data()){
				return D6;
			}
			else {
				return null;
			}
		}

		String getF7(){
			if (InitBlock2Data()){
				return F7;
			}
			else {
				return null;
			}
		}

		String getD7(){
			if (InitBlock2Data()){
				return D7;
			}
			else {
				return null;
			}
		}

		String getF8(){
			if (InitBlock2Data()){
				return F8;
			}
			else {
				return null;
			}
		}

		String getD8(){
			if (InitBlock2Data()){
				return D8;
			}
			else {
				return null;
			}
		}

		void setF_2(String f2){
			this.F_2 = f2;
		}
		
		void setF5(String f5){
			this.F5 = f5;
		}

		void setD5(String d5){
			this.D5 = d5;
		}

		void setF6(String f6){
			this.F6 = f6;
		}

		void setD6(String d6){
			this.D6 = d6;
		}

		void setF7(String f7){
			this.F7 = f7;
		}

		void setD7(String d7){
			this.D7 = d7;
		}

		void setF8(String f8){
			this.F8 = f8;
		}

		void setD8(String d8){
			this.D8 = d8;
		}

		int syncToCard(){
			
			String tmp = "";
			String temp = "";
			
			D5 = ToIntString(D5);
			D6 = ToIntString(D6);
			D7 = ToIntString(D7);
			D8 = ToIntString(D8);
			
			temp = mHex.addZeroForNum(Integer.toHexString(Integer.parseInt(F_2)), 4);
			tmp += temp;
			Log.d(TAG, "step1 tmp is " + tmp);
			
			temp = mHex.addZeroForNum(Integer.toHexString(Integer.parseInt(F5)), 2);
			tmp += temp;
			Log.d(TAG, "step2 tmp is " + tmp);

			temp = mHex.addZeroForNum(Integer.toHexString(Integer.parseInt(D5)), 4);
			tmp += temp;
			Log.d(TAG, "step3 tmp is " + tmp);

			temp = mHex.addZeroForNum(Integer.toHexString(Integer.parseInt(F6)), 2);
			tmp += temp;
			Log.d(TAG, "step4 tmp is " + tmp);

			temp = mHex.addZeroForNum(Integer.toHexString(Integer.parseInt(D6)), 4);
			tmp += temp;
			Log.d(TAG, "step5 tmp is " + tmp);

			temp = mHex.addZeroForNum(Integer.toHexString(Integer.parseInt(F7)), 2);
			tmp += temp;
			Log.d(TAG, "step6 tmp is " + tmp);

			temp = mHex.addZeroForNum(Integer.toHexString(Integer.parseInt(D7)), 6);
			tmp += temp;
			Log.d(TAG, "step7 tmp is " + tmp);

			temp = mHex.addZeroForNum(Integer.toHexString(Integer.parseInt(F8)), 2);
			tmp += temp;
			Log.d(TAG, "step8 tmp is " + tmp);

			temp = mHex.addZeroForNum(Integer.toHexString(Integer.parseInt(D8)), 6);
			tmp += temp;
			Log.d(TAG, "step9 tmp is " + tmp);

//			temp = mHex.addZeroForNum(Integer.toHexString(0), 2);
//			tmp += temp;
//			Log.d(TAG, "step10 tmp is " + tmp);

			try {
				mOrigindata = mHex.hexStr2ByteArr(tmp);
				Log.d(TAG, "sync data len is " + mOrigindata.length);
			}
			catch (Exception e){
				e.printStackTrace();
				return -1;
			}
			
			return WriteBlock(BLOCK);
		}
	}

	class Block9Data {
		private final byte[] BLOCK = new byte[]{(byte)9, (byte)10, (byte)12, (byte)13, (byte)14};

		TreeMap<String, String> mMap = new TreeMap<String, String>();
		List<QualityIndexResultInfo> ResultQualityInfos = null;
		String key = null;
		String value = null;
		
		void ClearTreeMap(){
			mMap.clear();
		}

		TreeMap<?, ?> getDataToMap(){
			int count = 0;
			
			if (mInitFlags.GetBlock9Flag())
				return mMap;
			
			if (!mHasInited)
				return null;
			
			for (int i=0; i < BLOCK.length; i++){
				if (0 != ReadBlock(BLOCK[i], PICC_AUTHENT1A))
				{
					if (count > 0)
						mInitFlags.SetBlock9Flag();
					return mMap;
				}
				
				Log.d(TAG, "Block8 Read " + BLOCK[i] + " block " + " count is " + count + " mCursor2 is " + mCursor2);
				if (count >= mCursor2){
					break;
				}else {
					String tmp = mHex.encodeHexStr(mOrigindata);
					Log.d(TAG, "Hexstr is " + tmp);
					for (int j=0; j<4; j++) {
						key = mHex.convertHexStrToIntStr(tmp.substring(8*j, 8*j+3));
						value = mHex.convertHexStrToIntStr(tmp.substring(8*j+3, 8*j+8));
						Log.d(TAG, "key is " + key + " value is " + value);
						//if ("4095" == key && "1048575" == value)
						/*if ("000" == key && "00000" == value)
						{
							if (count > 0)
								mInitFlags.SetBlock9Flag();
							return mMap;
						}*/
						mMap.put(key, value);
						count++;
						if (count >= mCursor2){
							mInitFlags.SetBlock9Flag();
							return mMap;
						}
					}
				}	
			}
			
			mInitFlags.SetBlock9Flag();
			return mMap;
		}
		
		List<QualityIndexResultInfo> getResultQualityInfos(){
			ResultQualityInfos = new ArrayList<QualityIndexResultInfo>();
			TreeMap<?, ?> map = new TreeMap<String, String>();
			map = getAssayData();
			
			Iterator<?> it = map.entrySet().iterator();
			while (it.hasNext()) {
				//获取map中每项index和value
				@SuppressWarnings("rawtypes")
				Map.Entry entry =(Map.Entry) it.next();
				Object key = entry.getKey();
				Object value = entry.getValue();
				QualityIndexResultInfo qualityIndexResultInfo = new QualityIndexResultInfo();
				qualityIndexResultInfo.setQualityIndexID(Integer.valueOf(key.toString()));
				qualityIndexResultInfo.setQualityIndexResult(Double.valueOf(value.toString())/100);
				ResultQualityInfos.add(qualityIndexResultInfo);
			}
			
			return ResultQualityInfos;
		}
		
		int ClearAllData(){
			for (int i=0; i < BLOCK.length; i++){
				CleanData(BLOCK[i]);
			}
			
			mCursor2 = 0;
			mBlock4.setCursor2("00");
			return mBlock4.syncToCard();

		}
		
	}

	class Block16Data {
	//	private final byte BLOCK = 0x01;
		private static final int mStartBlock = 16;
		HashMap<Integer, Block16Etc> mMap = new HashMap<Integer, Block16Etc>();
		
		void ClearHaspMap(){
			mMap.clear();
		}
		
		int ClearAllData(){
			for (int i=mStartBlock; i < mCursor1; i++){
				CleanData((byte)i);
			}
			
			mCursor1 = 0;
			mBlock4.setCursor1("00");
			return mBlock4.syncToCard();
		}
		
		WeighInfo getWeightInfo(){
			WeighInfo weighInfo = new WeighInfo();
			HashMap<?, ?> hashMap = getWeightData();
			if (hashMap == null) {
				return null;
			}
			
			Block16Etc etc = null;
			Iterator<?> it = hashMap.entrySet().iterator();
			while (it.hasNext()) {
				// entry的输出结果如key0=value0等
				@SuppressWarnings("rawtypes")
				Map.Entry entry =(Map.Entry) it.next();
				@SuppressWarnings("unused")
				Object key = entry.getKey();
				Object value = entry.getValue();
				etc = (Block16Etc)value;
				if (Integer.parseInt(etc.mFur) == 0) {	//皮重
					weighInfo.setTareWeight(Integer.valueOf(etc.mRoughWeight));
				}
				if (Integer.parseInt(etc.mFur) == 1) {	//毛重
					weighInfo.setGrossWeight(Integer.valueOf(etc.mRoughWeight));
				}
			}
			return weighInfo;
		}
		
		HashMap<?, ?> getDataToMap(){
			if (mInitFlags.GetBlock16Flag())
				return mMap;

			if (!mHasInited)
				return null;

			Log.d(TAG, "Block16 cursor is " + mCursor1);
			for (int i=mStartBlock; i<=(mStartBlock+mCursor1-1); i++){
				if (isKeyBlock(i)) {
					continue;
				}
				if (0 != ReadBlock((byte)(i), PICC_AUTHENT1A))
				{
					if (0 < mMap.size())
					{
						mInitFlags.SetBlock16Flag();
						return mMap;
					}
					else{
						return null;
					}
					
				}

				String tmp = mHex.encodeHexStr(mOrigindata);
				Log.d(TAG, "Block" + i + " data is " + tmp);
				Block16Etc bl16 = new Block16Etc();
				bl16.mStroageNo = mHex.convertHexStrToIntStr(tmp.substring(0, 2));
				bl16.mPoundNo = mHex.convertHexStrToIntStr(tmp.substring(2, 4));
				bl16.mPoundNote = tmp.substring(4,14);
				bl16.mPoundWeight = tmp.substring(14, 24);
				bl16.mFur = mHex.convertHexStrToIntStr(tmp.substring(24, 26));
				bl16.mRoughWeight = mHex.convertHexStrToIntStr(tmp.substring(26, 32));
				mMap.put(new Integer(i), bl16);
			}
			mInitFlags.SetBlock16Flag();
			return mMap;
		}

		int modifyData(int type, int block, Block16Etc blockdata){
			if ((ADD_ONE != type && DEL_ONE != type && MOD_ONE != type)
				|| 64 <= block)
				return -1;

			if (ADD_ONE != type){
				if (mMap.containsKey(new Integer(block)))
					return -1;
				else {
					mMap.put(new Integer(mCursor1+1), blockdata);
				
					String tmp = mHex.convertIntStrToHexStr(blockdata.mStroageNo);
					tmp += mHex.convertIntStrToHexStr(blockdata.mPoundNo);
					//tmp += mHex.convertIntStrToHexStr(block13.mPoundNote);
					//tmp += mHex.convertIntStrToHexStr(block13.mPoundWeight);
					tmp += blockdata.mPoundNote;
					tmp += blockdata.mPoundWeight;
					tmp += mHex.convertIntStrToHexStr(blockdata.mFur);
					tmp += mHex.convertIntStrToHexStr(blockdata.mRoughWeight);
					
					mOrigindata = mHex.decodeHex(tmp.toCharArray());
					if (0 != WriteBlock((byte)(mCursor1+1)))
						return -1;
					else {
						mCursor1++;
						mBlock4.setCursor1(Integer.toString(mCursor1));
						if (0 != mBlock4.syncToCard()){
							mCursor1--;
							mBlock4.setCursor1(Integer.toString(mCursor1));
						}	
					}
					//修改块1内容
					
				}
			}
			else if (DEL_ONE == type){
				if (!mMap.containsKey(new Integer(block)))
					return 0;
				mMap.remove(new Integer(block));
				//未涉及卡的操作
			}
			else{
				if (mMap.containsKey(new Integer(block))){
					
				}
				else {
					//未找到对应则新增
				}
			}

			return 0;
		}
		
	}


	/** 
	 * reference apache commons <a 
	 * href="http://commons.apache.org/codec/">http://commons.apache.org/codec/</a> 
	 *	
	 * @author Aub 
	 *	
	 */
	
	public class Hex {
		/** 
			 * 用于建立十六进制字符的输出的小写字符数组 
			 */
		private /*static*/ final char[] DIGITS_LOWER = { '0', '1', '2', '3', '4', '5',	
			'6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };  
		/** 
			 * 用于建立十六进制字符的输出的大写字符数组 
			 */
		private /*static*/ final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5',	
			'6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };  
		/** 
			 * 将字节数组转换为十六进制字符数组 
			 *	
			 * @param data 
			 *			  byte[] 
			 * @return 十六进制char[] 
			 */
		public /*static*/ char[] encodeHex(byte[] data) {  
		//	return encodeHex(data, true);  
			return encodeHex(data, false);
		}  
		/** 
			 * 将字节数组转换为十六进制字符数组 
			 *	
			 * @param data 
			 *			  byte[] 
			 * @param toLowerCase 
			 *			  <code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式 
			 * @return 十六进制char[] 
			 */
		public /*static*/ char[] encodeHex(byte[] data, boolean toLowerCase) {	
			return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);	
		}  
		/** 
			 * 将字节数组转换为十六进制字符数组 
			 *	
			 * @param data 
			 *			  byte[] 
			 * @param toDigits 
			 *			  用于控制输出的char[] 
			 * @return 十六进制char[] 
			 */
		protected /*static*/ char[] encodeHex(byte[] data, char[] toDigits) {  
			int l = data.length;  
			char[] out = new char[l << 1];	
			// two characters form the hex value. 
			for (int i = 0, j = 0; i < l; i++) {  
						out[j++] = toDigits[(0xF0 & data[i]) >>> 4];  
						out[j++] = toDigits[0x0F & data[i]];  
			}  
			return out;  
		}  
		/** 
			 * 将字节数组转换为十六进制字符串 
			 *	
			 * @param data 
			 *			  byte[] 
			 * @return 十六进制String 
			 */
		public /*static*/ String encodeHexStr(byte[] data) {  
		//	return encodeHexStr(data, true);  
			return encodeHexStr(data, false);  
		}  
		/** 
			 * 将字节数组转换为十六进制字符串 
			 *	
			 * @param data 
			 *			  byte[] 
			 * @param toLowerCase 
			 *			  <code>true</code> 传换成小写格式 ， <code>false</code> 传换成大写格式 
			 * @return 十六进制String 
			 */
		public /*static*/ String encodeHexStr(byte[] data, boolean toLowerCase) {  
			return encodeHexStr(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);  
		}  
		/** 
			 * 将字节数组转换为十六进制字符串 
			 *	
			 * @param data 
			 *			  byte[] 
			 * @param toDigits 
			 *			  用于控制输出的char[] 
			 * @return 十六进制String 
			 */
		public /*protected static*/ String encodeHexStr(byte[] data, char[] toDigits) {  
			return new String(encodeHex(data, toDigits));  
		}  
		/** 
			 * 将十六进制字符数组转换为字节数组 
			 *	
			 * @param data 
			 *			  十六进制char[] 
			 * @return byte[] 
			 * @throws RuntimeException 
			 *			   如果源十六进制字符数组是一个奇怪的长度，将抛出运行时异常 
			 */
		public /*static*/ byte[] decodeHex(char[] data) {  
			int len = data.length;	
			if ((len & 0x01) != 0) {  
			throw new RuntimeException("Odd number of characters.");  
					}  
			byte[] out = new byte[len >> 1];  
			// two characters form the hex value. 
			for (int i = 0, j = 0; j < len; i++) {	
			int f = toDigit(data[j], j) << 4;  
						j++;  
						f = f | toDigit(data[j], j);  
						j++;  
						out[i] = (byte) (f & 0xFF);  
			}  
			return out;  
		}  
		/** 
			 * 将十六进制字符转换成一个整数 
			 *	
			 * @param ch 
			 *			  十六进制char 
			 * @param index 
			 *			  十六进制字符在字符数组中的位置 
			 * @return 一个整数 
			 * @throws RuntimeException 
			 *			   当ch不是一个合法的十六进制字符时，抛出运行时异常 
			 */
		public /*protected static*/ int toDigit(char ch, int index) {	
			int digit = Character.digit(ch, 16);  
			if (digit == -1) {	
			throw new RuntimeException("Illegal hexadecimal character " + ch  
								+ " at index " + index);  
			}  
			return digit;  
		}  


		
		
		/**  
		  * 将byte数组转换为表示16进制值的字符串， 如：byte[]{8,18}转换为：0813， 和public static byte[]  
		  * hexStr2ByteArr(String strIn) 互为可逆的转换过程  
		  *   
		  * @param arrB  
		  * 		   需要转换的byte数组  
		  * @return 转换后的字符串	
		  * @throws Exception  
		  * 			本方法不处理任何异常，所有异常全部抛出	
		  */  
		public /*static*/ String byteArr2HexStr(byte[] arrB) /*throws Exception*/ {
			int iLen = arrB.length;
			// 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍
			StringBuffer sb = new StringBuffer(iLen * 2);
			for (int i = 0; i < iLen; i++) {
				int intTmp = arrB[i];
				// 把负数转换为正数
				while (intTmp < 0) {
					intTmp = intTmp + 256;
				}
				// 小于0F的数需要在前面补0
				if (intTmp < 16) {
					sb.append("0");
				}
				sb.append(Integer.toString(intTmp, 16));
			}
			return sb.toString();
		}
	
		/**
		 * 将表示16进制值的字符串转换为byte数组， 和public static String byteArr2HexStr(byte[] arrB)
		 * 互为可逆的转换过程
		 * 
		 * @param strIn
		 *			  需要转换的字符串
		 * @return 转换后的byte数组
		 * @throws Exception
		 *			   本方法不处理任何异常，所有异常全部抛出
		 */
		public /*static*/ byte[] hexStr2ByteArr(String strIn) throws Exception {
			byte[] arrB = strIn.getBytes();
			int iLen = arrB.length;
	
			// 两个字符表示一个字节，所以字节数组长度是字符串长度除以2
			byte[] arrOut = new byte[iLen / 2];
			for (int i = 0; i < iLen; i = i + 2) {
				String strTmp = new String(arrB, i, 2);
				arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
			}
			return arrOut;
		}

		
		public String convertAsciiStrToHexStr(String str){
		 
		  char[] chars = str.toCharArray();
	 
		  StringBuffer hex = new StringBuffer();
		  for(int i = 0; i < chars.length; i++){
			hex.append(Integer.toHexString((int)chars[i]));
		  }
	 
		  return hex.toString();
		  }
		 
		public String convertHexStrToAsciiStr(String hex){
		 
			  StringBuilder sb = new StringBuilder();
			  StringBuilder temp = new StringBuilder();
		 
			  //49204c6f7665204a617661 split into two characters 49, 20, 4c...
			  for( int i=0; i<hex.length()-1; i+=2 ){
		 
				  //grab the hex in pairs
				  String output = hex.substring(i, (i + 2));
				  //convert hex to decimal
				  int decimal = Integer.parseInt(output, 16);
				  //convert the decimal to character
				  sb.append((char)decimal);
		 
				  temp.append(decimal);
			  }
			  Log.d(TAG, "Decimal : " + temp.toString());
		 
			  return sb.toString();
		}

		public String convertHexStrToIntStr(String hex){
			Integer tmp = new Integer(Integer.parseInt(hex, 16));	
			return tmp.toString();
		}

		public String convertIntStrToHexStr(String integer) {
			return Integer.toHexString(Integer.parseInt(integer));
		}
		public String addZeroForNum(String str, int strlength){
			int strLen = str.length();
			String nstr = null;
			Log.d(TAG, "strLen is " + strLen + " strlength is " + strlength);
			if (strLen < strlength){
				StringBuffer sb = new StringBuffer();
				int zeronum = strlength - strLen;
				while (0 < zeronum){
					sb.append("0");
					zeronum--;
				}
				sb.append(str);
				nstr = sb.toString();
				
			}
			/*else if (strLen > strlength) {
				nstr = str.substring(0, strlength);
			}*/
			else {
				nstr = str;
			}

			return nstr;
		}
		 
	}
	
	private String ToIntString(String doubleString) {
		int dot_index = -1;
		dot_index = doubleString.indexOf(".");
		if (dot_index == -1) {
			return doubleString;
		}
		return doubleString.substring(0, dot_index);
	}
	
	class EnrollInfoInBlock4 {
		private int Singed = 0;				//RFID卡标志位
		private int GrainType = 0;				//粮食品种ID
		private int VehiceType = 0;			//车辆类型0:内;1:外
		private int BusinessType = 0;			//业务类型1:入库;2:出库;3:倒仓
		private int WorkWarehouseID = 0;	//作业仓库ID
		private int WorkNode = 0;				//作业环节
		private int OperatorID = 0;			//值仓人员ID
		private int FirstWeight = 0;			//0:先皮;1:先毛
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
	}
}

