package com.elsabaautoservice.elsabaemployee;

import com.google.gson.annotations.SerializedName;

public class ReplacementEmployee {


    private long empId ;
    private String empName ;

    public ReplacementEmployee(long empId, String empName) {
        this.empId = empId;
        this.empName = empName;
    }

    public long getEmpId() {
        return empId;
    }

    @Override
    public String toString() {
        return empName ;
    }
}
