package com.elsabaautoservice.elsabaemployee;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class GetAttendance {

    @SerializedName("EmployeeDetailsId")
    private long EmployeeDetailsId ;
    @SerializedName("DateFrom")
    private String DateFrom ;
    @SerializedName("DateTo")
    private String DateTo ;
    @SerializedName("DepartmentID")
    private String DepartmentID ;
    @SerializedName("result")
    private JsonArray result ;

    public GetAttendance(long employeeDetailsId, String dateFrom, String dateTo, String departmentID) {
        EmployeeDetailsId = employeeDetailsId;
        DateFrom = dateFrom;
        DateTo = dateTo;
        DepartmentID = departmentID;
    }

    public JsonArray getResult() {
        return result;
    }
}
