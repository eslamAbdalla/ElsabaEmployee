package com.elsabaautoservice.elsabaemployee;

import com.google.gson.annotations.SerializedName;

public class CheckInOutParams {

    @SerializedName("EmployeeDetailsId")
    private long EmployeeDetailsId ;
    @SerializedName("AttendanceNotes")
    private String AttendanceNotes ;
    @SerializedName("Longitude")
    private Double Longitude ;
    @SerializedName("Latitude")
    private Double Latitude ;

    public CheckInOutParams(long employeeDetailsId, String attendanceNotes, Double longitude, Double latitude) {
        EmployeeDetailsId = employeeDetailsId;
        AttendanceNotes = attendanceNotes;
        Longitude = longitude;
        Latitude = latitude;
    }
}
