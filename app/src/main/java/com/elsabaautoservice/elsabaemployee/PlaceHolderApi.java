package com.elsabaautoservice.elsabaemployee;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PlaceHolderApi {

    @POST("auth/login")
    Call<LogInUser> logInUser (@Body LogInUser user );


    @GET("leaves/getbalance/{id}")
    Call<GetResult_JsonArray> getUserBalance (@Header("Authorization")String authToken , @Path("id") long id );

    @GET("leavetypes/GetAllLeaveTypes")
    Call<LeaveTypes> getLeaveTypes (@Header("Authorization")String authToken );

    @GET("leaves/GetConfiguredDates")
    Call<GetResult_JsonObject> getConfiguredDates (@Header("Authorization")String authToken );

    @GET("employees/GetAllReplacements")
    Call<GetResult_JsonArray> getReplacementEmp (@Header("Authorization")String authToken ,@Query("empDetailsId") long empDetailsId );

    @GET("leaves/GetLeavesMobile")
    Call<GetResult_JsonArray> getUserLeaves (@Header("Authorization")String authToken ,
                                             @Query("FromDate") String FromDate ,
                                             @Query("ToDate") String ToDate ,
                                             @Query("EmployeeDetailsId") long EmployeeDetailsId ,
                                             @Query("DepartmentId") String DepartmentId ,
                                             @Query("FingerPrint") String FingerPrint ,
                                             @Query("ApproveStatus") int ApproveStatus ,
                                             @Query("LeaveTypeId") String LeaveTypeId ,
                                             @Query("IncludeSubDepartments") boolean IncludeSubDepartments ,
                                             @Query("CurrentEmpDetailsId") long CurrentEmployeeDetailsId);



    @GET("message/getmessage")
    Call<GetResult_JsonObject> getErrorMessage (@Query("messageCode") String messageCode );



    @POST("attendance/GetAttendanceWorkShift")
    Call<GetAttendance> getAttendance (@Header("Authorization")String authToken,  @Body GetAttendance getAttendance );

    @POST("attendance/InsertAttendanceAndroid")
    Call<CheckInOutParams> checkInOut (@Header("Authorization")String authToken,  @Body CheckInOutParams checkInOutParams );

    @POST("leaves/addrequest")
    Call<NewRequestParams> addRequest (@Header("Authorization")String authToken,  @Body NewRequestParams newRequestParams );




}
