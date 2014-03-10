package com.aisino.grain.model.rest;

import java.util.List;

import retrofit.RestAdapter;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.aisino.grain.beans.AdjustDeductedRequest;
import com.aisino.grain.beans.AdjustDeductedResponse;
import com.aisino.grain.beans.AssayIndex;
import com.aisino.grain.beans.AssayIndexTypeRequest;
import com.aisino.grain.beans.CheckVehicleWorkRequest;
import com.aisino.grain.beans.CheckVehicleWorkResponse;
import com.aisino.grain.beans.CommitWareHouseStateRequest;
import com.aisino.grain.beans.CommitWareHouseStateResponse;
import com.aisino.grain.beans.GetDecutedInfoRequest;
import com.aisino.grain.beans.GetDecutedInfoResponse;
import com.aisino.grain.beans.GetUserInfoByNameRequest;
import com.aisino.grain.beans.GetUserInfoByRFIDRequest;
import com.aisino.grain.beans.GetUserInfoResponse;
import com.aisino.grain.beans.GetWareHouseInfoRequest;
import com.aisino.grain.beans.GrainGradeTypeRequest;
import com.aisino.grain.beans.GrainGradeTypeResponse;
import com.aisino.grain.beans.GrainType;
import com.aisino.grain.beans.RegisterSampleRequest;
import com.aisino.grain.beans.RegisterSampleResponse;
import com.aisino.grain.beans.RegisterVehicleWorkRequest;
import com.aisino.grain.beans.RegisterVehicleWorkResponse;
import com.aisino.grain.beans.SearchTaskedRequest;
import com.aisino.grain.beans.SearchTaskedResponse;
import com.aisino.grain.beans.SearchTaskingRequest;
import com.aisino.grain.beans.SearchTaskingResponse;
import com.aisino.grain.beans.ServiceAvailableTestRequest;
import com.aisino.grain.beans.UpdateGrainTypeRequest;
import com.aisino.grain.beans.UpdateUserInfoRequest;
import com.aisino.grain.beans.UpdateWareHouseRequest;
import com.aisino.grain.beans.UserLoginInfo;
import com.aisino.grain.beans.WareHouse;
import com.aisino.grain.beans.WareHouseInfoResponse;
import com.aisino.grain.model.rest.method.RestWebServiceInterface.IAdjustDeducted;
import com.aisino.grain.model.rest.method.RestWebServiceInterface.IAssayIndexType;
import com.aisino.grain.model.rest.method.RestWebServiceInterface.ICheckVehicleWork;
import com.aisino.grain.model.rest.method.RestWebServiceInterface.ICommitWareHouseState;
import com.aisino.grain.model.rest.method.RestWebServiceInterface.IGetDecutedInfo;
import com.aisino.grain.model.rest.method.RestWebServiceInterface.IGetUserInfoByName;
import com.aisino.grain.model.rest.method.RestWebServiceInterface.IGetUserInfoByRFID;
import com.aisino.grain.model.rest.method.RestWebServiceInterface.IGetWareHouseInfo;
import com.aisino.grain.model.rest.method.RestWebServiceInterface.IGrainGradeType;
import com.aisino.grain.model.rest.method.RestWebServiceInterface.IRegisterSample;
import com.aisino.grain.model.rest.method.RestWebServiceInterface.IRegisterVehicleWork;
import com.aisino.grain.model.rest.method.RestWebServiceInterface.ISearchTasked;
import com.aisino.grain.model.rest.method.RestWebServiceInterface.ISearchTasking;
import com.aisino.grain.model.rest.method.RestWebServiceInterface.IServiceAvailableTest;
import com.aisino.grain.model.rest.method.RestWebServiceInterface.IUpdateGrainType;
import com.aisino.grain.model.rest.method.RestWebServiceInterface.IUpdateUserInfo;
import com.aisino.grain.model.rest.method.RestWebServiceInterface.IUpdateWareHouse;

public class RestWebServiceAdapter {
	private static final String TAG = "RestWebServiceAdapter";
	
	private String URL = null;	 // 服务器URL地址

	private Context mContext = null;
	private SharedPreferences sharedPreferences = null; // 保存状态

	private RestAdapter mRestAdapter = null;
	
	private static RestWebServiceAdapter mRestWebServiceAdapter = null; 
	
	public static RestWebServiceAdapter getInstance(Context context) {    
		if (mRestWebServiceAdapter == null) {   
			mRestWebServiceAdapter = new RestWebServiceAdapter(context);  
		}
		return mRestWebServiceAdapter;    
	}
	
	private RestWebServiceAdapter(Context context) {
		mContext = context;
		GetURL(context);
	}
	
	public void UpdateURL() {
		sharedPreferences = mContext.getSharedPreferences("System_Config",
				Context.MODE_PRIVATE);
		URL = sharedPreferences.getString("Server_URL_Address",
				"http://localhost/MobileAndroidService.svc");
		mRestAdapter = new RestAdapter.Builder().setServer(URL).build();
	}

	private void GetURL(Context context) {
		sharedPreferences = mContext.getSharedPreferences("System_Config",
				Context.MODE_PRIVATE);
		URL = sharedPreferences.getString("Server_URL_Address",
				"http://localhost/MobileAndroidService.svc");
		mRestAdapter = new RestAdapter.Builder().setServer(URL).build();
	}

	// 获取用户信息
	public GetUserInfoResponse Rest(GetUserInfoByNameRequest requestBean) {
		GetUserInfoResponse res = null;
		try {
			IGetUserInfoByName service = mRestAdapter
					.create(IGetUserInfoByName.class);
			res = service.GetUserInfoByName(
					requestBean.getLoginName(), requestBean.getPassword());
		} catch (Exception e) {
			return null;
		}
		return res;
	}

	// 通过读卡获取用户信息
	public GetUserInfoResponse Rest(GetUserInfoByRFIDRequest requestBean) {
		GetUserInfoResponse res = null;
		try {
			IGetUserInfoByRFID service = mRestAdapter
					.create(IGetUserInfoByRFID.class);
			res = service.GetUserInfoByRFID(requestBean
					.getUserRFID());
		} catch (Exception e) {
			return null;
		}
		return res;
	}
	
	// 获取仓库信息
	public WareHouseInfoResponse Rest(GetWareHouseInfoRequest requestBean) {
		WareHouseInfoResponse res = null;
		try{
			IGetWareHouseInfo service = mRestAdapter
					.create(IGetWareHouseInfo.class);
			res = service.GetWareHouseInfo(
					requestBean.getWareHouseID(), requestBean.getSearchFlag(),
					requestBean.getSearchLength(), requestBean.getPage());
		} catch (Exception e) {
			return null;
		}
		return res;
	}
	
	// 获取仓库信息
	public RegisterSampleResponse Rest(RegisterSampleRequest requestBean) {
		RegisterSampleResponse res = null;
		try{
			IRegisterSample service = mRestAdapter
					.create(IRegisterSample.class);
			res = service.RegisterSample(requestBean.getUserID(),requestBean.getTaskNumber(),requestBean.getSampleCount());
		} catch (Exception e) {
			return null;
		}
		return res;
	}
	
	//查询待处理扦样任务（扦样）
	public SearchTaskingResponse Rest(SearchTaskingRequest requestBean) {
		SearchTaskingResponse res = null;
		try{
			ISearchTasking service = mRestAdapter
					.create(ISearchTasking.class);
			res = service.SearchTasking(
					requestBean.getPageNumber(),requestBean.getPageSize());
		} catch (Exception e) {
			return null;
		}
		return res;
	}

	//查询已处理扦样任务（扦样）
	public SearchTaskedResponse Rest(SearchTaskedRequest requestBean) {
		SearchTaskedResponse res = null;
		try{
		ISearchTasked service = mRestAdapter
				.create(ISearchTasked.class);
		res = service.SearchTasked(
				requestBean.getUserID(),
				requestBean.getPageNumber(),
				requestBean.getPageSize(),
				requestBean.getVehiclePlate());
		} catch (Exception e) {
			return null;
		}
		return res;
	}

	public CommitWareHouseStateResponse Rest(
			CommitWareHouseStateRequest requestBean) {
		ICommitWareHouseState service = mRestAdapter
				.create(ICommitWareHouseState.class);
		CommitWareHouseStateResponse res = service.CommitWareHouseState(
				requestBean.getWareHouseID(), requestBean.getWareHouseState());
		return res;
	}

	public GetDecutedInfoResponse Rest(GetDecutedInfoRequest requestBean) {
		GetDecutedInfoResponse res = null;
		try {
			IGetDecutedInfo service = mRestAdapter.create(IGetDecutedInfo.class);
			res = service.GetDecutedInfo(requestBean
					.getVehicleRFIDTag());
		} catch (Exception e) {
			return res;
		}
		
		return res;
	}

	// 检查网络状态
	public boolean Rest(
			ServiceAvailableTestRequest requestBean) {
		boolean res = false;
		try {
			IServiceAvailableTest service = mRestAdapter
					.create(IServiceAvailableTest.class);
			res = service.ServiceAvailableTest();
		} catch (Exception e) {
			return false;
		}
		return res;
	}

	// 更新用户信息
	public  List<UserLoginInfo> Rest(UpdateUserInfoRequest requestBean) {
		IUpdateUserInfo service = mRestAdapter.create(IUpdateUserInfo.class);
		List<UserLoginInfo> res = service.UpdateUserInfo();
		return res;
	}

	// 更新用户信息
	public List<WareHouse> Rest(UpdateWareHouseRequest requestBean) {
		List<WareHouse> res = null;
		try {
			IUpdateWareHouse service = mRestAdapter.create(IUpdateWareHouse.class);
			res = service.UpdateWareHouse();
		} catch (Exception e) {
			return null;
		}
		return res;
	}

	// 更新
	public List<GrainType> Rest(UpdateGrainTypeRequest requestBean) {
		List<GrainType> res = null;
		try {
			IUpdateGrainType service = mRestAdapter.create(IUpdateGrainType.class);
			 res = service.UpdateGrainType();
		} catch (Exception e) {
			return null;
		}
		return res;
	}

	// 更新用户信息
	public List<AssayIndex> Rest(AssayIndexTypeRequest requestBean) {
		IAssayIndexType service = mRestAdapter.create(IAssayIndexType.class);
		List<AssayIndex> res = service.AssayIndexType();
		return res;
	}

	//
	public GrainGradeTypeResponse Rest(GrainGradeTypeRequest requestBean) {
		IGrainGradeType service = mRestAdapter.create(IGrainGradeType.class);
		GrainGradeTypeResponse res = service.GrainGradeType();
		return res;
	}
	
	//
	public AdjustDeductedResponse Rest(AdjustDeductedRequest requestBean) {
		AdjustDeductedResponse res = null;
		try {
			IAdjustDeducted service = mRestAdapter.create(IAdjustDeducted.class);
			res = service.AdjustDeducted(
					requestBean.getVehicleRFIDTag(),
					requestBean.getAdjustDeductedInfo());
		} catch (Exception e) {
			return null;
		}
		return res;
	}

	//
	public CheckVehicleWorkResponse Rest(CheckVehicleWorkRequest requestBean) {
		CheckVehicleWorkResponse res = null;
		try {
			ICheckVehicleWork service = mRestAdapter.create(ICheckVehicleWork.class);
			res = service.CheckVehicleWork(
					requestBean.getVehicleRFIDTag(),
					requestBean.getWareHouseID());
		} catch (Exception e) {
			return null;
		}
		return res;
	}
	
	//
	public RegisterVehicleWorkResponse Rest(RegisterVehicleWorkRequest requestBean) {
		try {
			IRegisterVehicleWork service = mRestAdapter.create(IRegisterVehicleWork.class);
			RegisterVehicleWorkResponse res = service.RegisterVehicleWork(
					requestBean.getVehicleRFIDTag(),
					requestBean.getWareHouseID(),
					requestBean.getWorkNode(),
					requestBean.getWorkNumber(),
					requestBean.getBusinessType(),
					requestBean.getUserID());
			return res;
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			return null;
		}
	
	}

	// public ResponseBean Rest(RequestBean requestBean) {
	// GetURL();
	// try {
	// if (requestBean instanceof GetUserInfoByNameRequest) {
	// IGetUserInfoByName service = mRestAdapter
	// .create(IGetUserInfoByName.class);
	// GetUserInfoResponse res = service
	// .GetUserInfoByName(
	// ((GetUserInfoByNameRequest) requestBean)
	// .getLoginName(),
	// ((GetUserInfoByNameRequest) requestBean)
	// .getPassword());
	// return res;
	// }
	// if (requestBean instanceof GetUserInfoByRFIDRequest) {
	// IGetUserInfoByRFID service = mRestAdapter
	// .create(IGetUserInfoByRFID.class);
	// GetUserInfoResponse res = service
	// .GetUserInfoByRFID(((GetUserInfoByRFIDRequest) requestBean)
	// .getUserRFID());
	// return res;
	// }
	// if (requestBean instanceof GetWareHouseInfoRequest) {
	// IGetWareHouseInfo service = mRestAdapter
	// .create(IGetWareHouseInfo.class);
	// WareHouseInfoResponse res = service
	// .GetWareHouseInfo(
	// ((GetWareHouseInfoRequest) requestBean)
	// .getWareHouseID(),
	// ((GetWareHouseInfoRequest) requestBean)
	// .getSearchFlag(),
	// ((GetWareHouseInfoRequest) requestBean)
	// .getSearchLength(),
	// ((GetWareHouseInfoRequest) requestBean)
	// .getPage());
	// return res;
	// }
	// if (requestBean instanceof CommitWareHouseStateRequest) {
	// ICommitWareHouseState service = mRestAdapter
	// .create(ICommitWareHouseState.class);
	// CommitWareHouseStateResponse res = service
	// .CommitWareHouseState(
	// ((CommitWareHouseStateRequest) requestBean)
	// .getWareHouseID(),
	// ((CommitWareHouseStateRequest) requestBean)
	// .getWareHouseState());
	// return res;
	// }
	// if (requestBean instanceof GetDecutedInfoRequest) {
	// IGetDecutedInfo service = mRestAdapter
	// .create(IGetDecutedInfo.class);
	// GetDecutedInfoResponse res = service
	// .GetDecutedInfo(((GetDecutedInfoRequest) requestBean)
	// .getVehicleRFIDTag());
	// return res;
	// }
	// if (requestBean instanceof ServiceAvailableTestRequest) {
	// IServiceAvailableTest service = mRestAdapter
	// .create(IServiceAvailableTest.class);
	// ServiceAvailableTestResponse res = service
	// .ServiceAvailableTest();
	// return res;
	// }
	// if (requestBean instanceof UpdateUserInfoRequest) {
	// IUpdateUserInfo service = mRestAdapter
	// .create(IUpdateUserInfo.class);
	// UpdateUserInfoResponse res = service.UpdateUserInfo();
	// return res;
	// }
	// if (requestBean instanceof UpdateWareHouseRequest) {
	// IUpdateWareHouse service = mRestAdapter
	// .create(IUpdateWareHouse.class);
	// UpdateWareHouseResponse res = service.UpdateWareHouse();
	// return res;
	// }
	// if (requestBean instanceof UpdateGrainTypeRequest) {
	// IUpdateGrainType service = mRestAdapter
	// .create(IUpdateGrainType.class);
	// UpdateGrainTypeResponse res = service.UpdateGrainType();
	// return res;
	// }
	// if (requestBean instanceof AssayIndexTypeRequest) {
	// IAssayIndexType service = mRestAdapter
	// .create(IAssayIndexType.class);
	// AssayIndexTypeResponse res = service.AssayIndexType();
	// return res;
	// }
	// if (requestBean instanceof GrainGradeTypeRequest) {
	// IGrainGradeType service = mRestAdapter
	// .create(IGrainGradeType.class);
	// GrainGradeTypeResponse res = service.GrainGradeType();
	// return res;
	// }
	// } catch (Exception e) {
	// return null;
	// }
	// return null;
	// }
}
