package com.aisino.grain.model.rest.method;

import java.util.List;

import com.aisino.grain.beans.AdjustDeductedResponse;
import com.aisino.grain.beans.AssayIndex;
import com.aisino.grain.beans.CheckVehicleWorkResponse;
import com.aisino.grain.beans.CommitWareHouseStateResponse;
import com.aisino.grain.beans.DeductedInfo;
import com.aisino.grain.beans.GetDecutedInfoResponse;
import com.aisino.grain.beans.GetUserInfoResponse;
import com.aisino.grain.beans.GrainGradeTypeResponse;
import com.aisino.grain.beans.GrainType;
import com.aisino.grain.beans.RegisterSampleResponse;
import com.aisino.grain.beans.RegisterVehicleWorkResponse;
import com.aisino.grain.beans.SearchTaskedResponse;
import com.aisino.grain.beans.SearchTaskingResponse;
import com.aisino.grain.beans.UserLoginInfo;
import com.aisino.grain.beans.WareHouse;
import com.aisino.grain.beans.WareHouseInfoResponse;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.POST;
import retrofit.http.Path;

public class RestWebServiceInterface {
	public interface IAdjustDeducted {
		@POST("/AdjustDeducted/{VehicleRFIDTag}")
		AdjustDeductedResponse AdjustDeducted(
				@Path("VehicleRFIDTag") String VehicleRFIDTag,
				@Body DeductedInfo deductedInfo);
	}
	public interface ICommitWareHouseState {
		@Headers("Content-Length:0")
		@POST("/CommitWareHouseState/{WareHouseID}/{WareHouseState}")
		CommitWareHouseStateResponse CommitWareHouseState(
				@Path("WareHouseID") int WareHouseID,
				@Path("WareHouseState") int WareHouseState);
	}
	public interface IGetUserInfoByName {
		@GET("/GetUserInfoByName/{LoginName}/{Password}")
		GetUserInfoResponse GetUserInfoByName(
				@Path("LoginName") String LoginName,
				@Path("Password") String Password);
	}
	
	public interface IAssayIndexType {
		@GET("/GetAssayIndes")
		List<AssayIndex> AssayIndexType();
	}
	
	public interface IGetWareHouseInfo {
		@GET("/GetWareHouseInfo/{WareHouseID}/{SearchFlag}/{SearchLength}/{Page}")
		WareHouseInfoResponse GetWareHouseInfo(
				@Path("WareHouseID") int WareHouseID,
				@Path("SearchFlag") int SearchFlag,
				@Path("SearchLength") int SearchLength, 
				@Path("Page") int Page);
	}
	
	public interface IRegisterSample {
		@POST("/RegisterSample/{UserID}/{TaskNumber}/{SampleCount}")
		RegisterSampleResponse RegisterSample(
				@Path("UserID") int UserID,
				@Path("TaskNumber") String TaskNumber,
				@Path("SampleCount") int SampleCount);
	}
	

	public interface IGetDecutedInfo {
		@GET("/GetDecutedInfo/{VehicleRFIDTag}")
		GetDecutedInfoResponse GetDecutedInfo(
				@Path("VehicleRFIDTag") String VehicleRFIDTag);
	}
	
	public interface IUpdateUserInfo {
		@GET("/GetUserInfo")
		List<UserLoginInfo> UpdateUserInfo();
	}

	public interface IUpdateWareHouse {
		@GET("/GetWareHouse")
		List<WareHouse> UpdateWareHouse();
	}

	public interface IUpdateGrainType {
		@GET("/GetGrainType")
		List<GrainType> UpdateGrainType();
	}
	
	public interface IRegisterVehicleWork {
		@Headers("Content-Length:0")
		@POST("/RegisterVehicleWork/{VehicleRFIDTag}/{WareHouseID}/{WorkNode}/{WorkNumber}/{BusinessType}/{UserID}")
		RegisterVehicleWorkResponse RegisterVehicleWork(
				@Path("VehicleRFIDTag") String VehicleRFIDTag,
				@Path("WareHouseID") int WareHouseID,
				@Path("WorkNode") int WareHouseChangeFlag,
				@Path("WorkNumber") String WorkNumber,
				@Path("BusinessType") int BusinessType,
				@Path("UserID") int UserID);
	}
	
	public interface IServiceAvailableTest {
		@GET("/GetServiceAvailable")
		boolean ServiceAvailableTest();
	}
	
	public interface ICheckVehicleWork {
		@GET("/CheckVehicleWork/{VehicleRFIDTag}/{WareHouseID}")
		CheckVehicleWorkResponse CheckVehicleWork(
				@Path("VehicleRFIDTag") String VehicleRFIDTag,
				@Path("WareHouseID") int WareHouseID);
	}
//	public interface IGetUserInfoByName {
//		@GET("/GetUserInfoByName/{LoginName}/{Password}")
//		GetUserInfoResponse GetUserInfoByName(
//				@Path("LoginName") String LoginName,
//				@Path("Password") String Password);
//	}

	public interface IGetUserInfoByRFID {
		@GET("/GetUserInfoByRFID/{RFID}")
		GetUserInfoResponse GetUserInfoByRFID(@Path("RFID") String RFID);
	}
	
	public interface ISearchTasking {
		@GET("/SearchTasking/{PageNumber}/{PageSize}")
		SearchTaskingResponse SearchTasking(
				@Path("PageNumber") int PageNumber,
				@Path("PageSize") int PageSize);
	}
	
	public interface ISearchTasked {
		@GET("/SearchTasked/{UserID}/{PageNumber}/{PageSize}/{VehiclePlate}")
		SearchTaskedResponse SearchTasked(
				@Path("UserID") int UserID,
				@Path("PageNumber") int PageNumber,
				@Path("PageSize") int PageSize,
				@Path("VehiclePlate") String VehiclePlate);
	}

	public interface IGrainGradeType {
		@GET("/GrainGradeType")
		GrainGradeTypeResponse GrainGradeType();
	}
	

	public interface ITest {
		@GET("/Test/{test}}")
		GetUserInfoResponse Test(@Path("test") String test,
				@Body UserLoginInfo obj);
	}
}
