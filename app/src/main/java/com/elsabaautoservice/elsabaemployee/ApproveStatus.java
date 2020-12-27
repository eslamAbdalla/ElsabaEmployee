package com.elsabaautoservice.elsabaemployee;

public class ApproveStatus {

    private int statusId ;
    private String statusName ;





    public ApproveStatus(int statusId, String statusName) {
        this.statusId = statusId;
        this.statusName = statusName;
    }



    public int getStatusId() {
        return statusId;
    }

    @Override
    public String toString() {
        return  statusName ;
    }


}
