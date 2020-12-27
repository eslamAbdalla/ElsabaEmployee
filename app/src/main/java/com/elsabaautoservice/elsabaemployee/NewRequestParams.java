package com.elsabaautoservice.elsabaemployee;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class NewRequestParams {

    @SerializedName("EmployeeDetailsId")
    private long EmployeeDetailsId;
    @SerializedName("LeaveTypeId")
    private int LeaveTypeId;
    @SerializedName("LeaveDateFrom")
    private String LeaveDateFrom;
    @SerializedName("LeaveDateTo")
    private String LeaveDateTo;
    @SerializedName("LeaveTimeFrom")
    private String  LeaveTimeFrom;
    @SerializedName("LeaveTimeTo")
    private String LeaveTimeTo;
    @SerializedName("ByUserId")
    private long ByUserId;
    @SerializedName("LeaveComments")
    private String LeaveComments;
    @SerializedName("ReplacementEmployeeId")
    private long ReplacementEmployeeId;
    @SerializedName("LeaveAttachments")
    private List<Attachment> attachments ;


    public NewRequestParams(long employeeDetailsId, int leaveTypeId, String leaveDateFrom, String leaveDateTo,
                            String leaveTimeFrom, String leaveTimeTo, long byUserId, String leaveComments,
                            long replacementEmployeeId,List<Attachment> attachment) {
        EmployeeDetailsId = employeeDetailsId;
        LeaveTypeId = leaveTypeId;
        LeaveDateFrom = leaveDateFrom;
        LeaveDateTo = leaveDateTo;
        LeaveTimeFrom = leaveTimeFrom;
        LeaveTimeTo = leaveTimeTo;
        ByUserId = byUserId;
        LeaveComments = leaveComments;
        ReplacementEmployeeId = replacementEmployeeId;
        attachments = attachment ;
    }
}

class Attachment {

    public Attachment() {
    }
}
