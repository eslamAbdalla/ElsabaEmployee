package com.elsabaautoservice.elsabaemployee;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

import javax.annotation.Nullable;

public class LeaveTypes {

    @SerializedName("id")

    private int leaveTypeId ;
    @SerializedName("leaveTypeName")
    private String leaveTypeName ;
    @SerializedName("requireTime")
    private boolean requireTime ;

    @SerializedName("countOfTimes")
    private int countOfTimes ;

    @SerializedName("requireAttachment")
    private boolean requireAttachment ;

    @SerializedName("result")
    private JsonArray result ;

    public LeaveTypes(int leaveId, String leaveTypeName) {
        this.leaveTypeId = leaveId;
        this.leaveTypeName = leaveTypeName;
    }

    public LeaveTypes(int leaveTypeId, String leaveTypeName, boolean requireTime, int countOfTimes,boolean requireAttachment) {
        this.leaveTypeId = leaveTypeId;
        this.leaveTypeName = leaveTypeName;
        this.requireTime = requireTime;
        this.countOfTimes = countOfTimes;
        this.requireAttachment = requireAttachment ;
    }

    public int getLeaveTypeId() {
        return leaveTypeId;
    }

    public boolean getRequireTime() {
        return requireTime;
    }

    public int getCountOfTimes() {
        return countOfTimes;
    }

    public boolean isRequireAttachment() {
        return requireAttachment;
    }

    public JsonArray getResult() {
        return result;
    }

    @Override
    public String toString() {
        return leaveTypeName ;
    }
}
