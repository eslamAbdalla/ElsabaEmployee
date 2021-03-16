package com.elsabaautoservice.elsabaemployee;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.gson.JsonObject;

public class LeavesDetails extends AppCompatActivity {

    public static JsonObject selectedLeave ;

    TextView LeaveName,LeaveStatus,LeaveFromDate, LeaveToDate ,LeaveFromTime, LeaveToTime,LeaveRequestDate,LeaveManagerStatus,LeaveHrStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaves_details);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


         LeaveName = findViewById(R.id.leavesDetails_LeaveName);
         LeaveStatus =findViewById(R.id.leavesDetails_LeaveStatus);
         LeaveFromDate =findViewById(R.id.leavesDetails_LeaveFromDate);
         LeaveToDate = findViewById(R.id.leavesDetails_LeaveToDate);
         LeaveFromTime =findViewById(R.id.leavesDetails_LeaveFromTime);
         LeaveToTime = findViewById(R.id.leavesDetails_LeaveToTime);
         LeaveRequestDate =findViewById(R.id.leavesDetails_LeaveRequestedDate);
         LeaveManagerStatus = findViewById(R.id.leavesDetails_ManagerStatus);
         LeaveHrStatus = findViewById(R.id.leavesDetails_HrStatus);


        Methods format = new Methods();

        String leaveName = selectedLeave.get("leaveTypeName").toString().replaceAll("\"","");
        String leaveStatus = selectedLeave.get("leaveStatus").toString().replaceAll("\"","");
        String leaveFromDate = selectedLeave.get("leaveDateFrom").toString().replaceAll("\"","");
        String leaveToDate = selectedLeave.get("leaveDateTo").toString().replaceAll("\"","");
        String leaveFromTime = selectedLeave.get("leaveTimeFrom").toString().replaceAll("\"","");
        String leaveToTime = selectedLeave.get("leaveTimeTo").toString().replaceAll("\"","");
        String leaveRequestDate = selectedLeave.get("leaveTimeStamp").toString().replaceAll("\"","");
        String leaveManagerStatus = selectedLeave.get("managerStatus").toString().replaceAll("\"","");
        String leaveHrStatus = selectedLeave.get("hrStatus").toString().replaceAll("\"","");

        if (leaveFromTime.equals("null") && leaveToTime.equals("null")){
            LeaveFromTime.setText("------");
            LeaveToTime.setText("------");
        }else {
            LeaveFromTime.setText(format.timeFormat(leaveFromTime));
            LeaveToTime.setText(format.timeFormat(leaveToTime));
        }

        LeaveName.setText(leaveName);
        LeaveStatus.setText(leaveStatus);
        LeaveFromDate.setText(format.dateFormat(leaveFromDate,getString(R.string.date_format)));
        LeaveToDate.setText(format.dateFormat(leaveToDate,getString(R.string.date_format)));
        LeaveRequestDate.setText(format.dateFormat(leaveRequestDate,getString(R.string.date_format)));
        LeaveManagerStatus.setText(leaveManagerStatus);
        LeaveHrStatus.setText(leaveHrStatus);





    }


    public boolean onOptionsItemSelected (MenuItem item){
//        startActivity(new Intent(LeavesDetails.this,Home.class));
        onBackPressed();
        return true ;
    }

}