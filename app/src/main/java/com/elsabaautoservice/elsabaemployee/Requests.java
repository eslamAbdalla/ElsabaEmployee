package com.elsabaautoservice.elsabaemployee;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.Nullable;

public class Requests extends AppCompatActivity{

    Spinner leaveTypeSpinner ;
    Spinner replacementEmpSpinner ;
    Dialog progressDialog ;
    List<LeaveTypes> leaveTypesList ;
    List<ReplacementEmployee> replacementEmployeeListList ;

    ArrayAdapter<LeaveTypes> leaveTypeAdapter ;
    ArrayAdapter<ReplacementEmployee> replacementAdapter ;

    TextView fromDate,toDate ;
    TextView fromTime,toTime ;
    EditText Notes ;
    Button save ;

    LinearLayout timeLayout ;

    int countOfTimes ;
    boolean requireTime  ;
    boolean requireAttachment ;
    long startDate ;
    private PlaceHolderApi placeHolderApi ;
    String errorMesaage ;

//    ------------------------Params-------------------------------------------
    int selectedLeaveTypeId ;
    long selectedReplacementEmp ;
    String requestFromDate = "" ;
    String requestToDate = "" ;
    String requestFromTime = "" ;
    String requestToTime = "" ;
    String notes = "" ;

    List<Attachment> attachments ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        leaveTypeSpinner = findViewById(R.id.requests_LeaveTypeSpinner);
        replacementEmpSpinner = findViewById(R.id.requests_ReplacementSpinner);
        fromDate = findViewById(R.id.requests_LeaveDateFrom);
        toDate = findViewById(R.id.requests_LeaveDateTo);
        fromTime = findViewById(R.id.requests_LeaveTimeFrom);
        toTime = findViewById(R.id.requests_LeaveTimeTo);
        Notes = findViewById(R.id.requests_Note) ;
        timeLayout = findViewById(R.id.timeLayoute);

        save = findViewById(R.id.requests_Save);

        progressDialog = new Dialog(Requests.this);
        progressDialog.setContentView(R.layout.progressdialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCancelable(false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.basic_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        placeHolderApi = retrofit.create(PlaceHolderApi.class);

        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showFromDatePickerDialog();
            }
        });
        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showToDatePickerDialog();
            }
        });
        fromTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFromTimePickerDialog();
            }
        });
        toTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToTimePickerDialog();
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (selectedLeaveTypeId == 0 ){
                    ErrorDialog(getString(R.string.please_select_leavetype));
                }else if (selectedReplacementEmp == 0){
                    ErrorDialog(getString(R.string.please_select_replacement));
                }else if (requestFromDate == ""){
                    ErrorDialog(getString(R.string.please_select_date));
                }else if (requestToDate == ""){
                    ErrorDialog(getString(R.string.please_select_date));
                }else if (requestFromTime == "" && requireTime){
                    ErrorDialog(getString(R.string.please_select_time));
                }else if (requestToTime == "" && requireTime){
                    ErrorDialog(getString(R.string.please_select_time));
                }else {

                addRequest();
            }}
        });

        getLeaveTypes();
        getReplacementEmp();

    }

    private void showFromDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                String date = year+" - "+(month+1)+" - "+dayOfMonth ;

                requestFromDate = date ;
                fromDate.setText(requestFromDate);
                toDate.setEnabled(true);
                requestToDate =date ;
                toDate.setText(requestFromDate);
                try {
                    String dateString = requestFromDate;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy - MM - dd");
                    Date date1 = sdf.parse(dateString);
                    startDate = date1.getTime();
                } catch (ParseException | java.text.ParseException e) {
                    e.printStackTrace();
                }

                if (requireTime){
                    requestToDate = date ;
                    toDate.setText(requestFromDate);
                }

            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
//        datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());
        datePickerDialog.show();
    }
    private void showToDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                String date = year+" - "+(month+1)+" - "+dayOfMonth ;
                requestToDate =date ;
                toDate.setText(requestToDate);
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
//        datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());

        datePickerDialog.getDatePicker().setMinDate(startDate);

//        datePickerDialog.getDatePicker().setMinDate();
        datePickerDialog.show();
    }

    private void showFromTimePickerDialog(){
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = hourOfDay+":"+minute ;
                requestFromTime =time ;
                fromTime.setText(String.format("%02d:%02d", hourOfDay, minute));

                if (countOfTimes != 0){
                    String totime = (hourOfDay+countOfTimes)+":"+minute ;
                    requestToTime = totime ;
                    toTime.setText(String.format("%02d:%02d",  (hourOfDay+countOfTimes), minute));
                }
            }
        },Calendar.getInstance().get(Calendar.HOUR_OF_DAY),Calendar.getInstance().get(Calendar.MINUTE),false);
        timePickerDialog.show();
    }
    private void showToTimePickerDialog(){
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String time = hourOfDay+":"+minute ;
                requestToTime =time ;
                toTime.setText(String.format("%02d:%02d", hourOfDay, minute));
            }
        },Calendar.getInstance().get(Calendar.HOUR_OF_DAY),Calendar.getInstance().get(Calendar.MINUTE),false);
        timePickerDialog.show();
    }

    public void getLeaveTypes(){
        progressDialog.show();

        Call<LeaveTypes> call = placeHolderApi.getLeaveTypes("Bearer "+LogIn.token);
        call.enqueue(new Callback<LeaveTypes>() {
            @Override
            public void onResponse(Call<LeaveTypes> call, Response<LeaveTypes> response) {
                leaveTypesList = new ArrayList<>();
                LeaveTypes leaveTypesHead = new LeaveTypes(0,getString(R.string.please_select_leavetype));
                leaveTypesList.add(leaveTypesHead);

                JsonArray LeaveTypes = response.body().getResult();
                for (int i=0;i<LeaveTypes.size();i++){
                    JsonObject LeaveType = (JsonObject) LeaveTypes.get(i);
                    int leaveTypeId = LeaveType.get("id").getAsInt();
                    String leaveTypeName  = LeaveType.get("leaveTypeName").toString().replaceAll("\"","");
                    String stCountOfTimes = LeaveType.get("countOfTimes").toString().replaceAll("\"","");
                    boolean requireTime = LeaveType.get("requireTime").getAsBoolean();
                    boolean requireAttachment = LeaveType.get("requireAttachment").getAsBoolean();

                    int CountOfTimes ;
                    if (stCountOfTimes.equals("null")){
                        CountOfTimes = 0 ;
                    }else {
                         CountOfTimes = Integer.parseInt(stCountOfTimes);
                    }
                    if (!requireAttachment){
                        LeaveTypes leaveTypes = new LeaveTypes(leaveTypeId,leaveTypeName,requireTime,CountOfTimes,requireAttachment);
                        leaveTypesList.add(leaveTypes);
                    }

                }
                leaveTypeAdapter = new ArrayAdapter<LeaveTypes>(Requests.this,R.layout.spinner_row,leaveTypesList);
                leaveTypeAdapter.setDropDownViewResource(R.layout.spinner_row);
                leaveTypeSpinner.setAdapter(leaveTypeAdapter);
                leaveTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        progressDialog.dismiss();
                        LeaveTypes leaveType = (LeaveTypes) parent.getSelectedItem();
                        selectedLeaveTypeId = leaveType.getLeaveTypeId();

                        toDate.setText(getResources().getString(R.string.to_date));
                        requestToDate = ""  ;

                        fromDate.setText(getResources().getString(R.string.from_date));
                        requestFromDate = ""  ;


                            countOfTimes = leaveType.getCountOfTimes();
                           requireTime = leaveType.getRequireTime();

                            if (!requireTime){
                                timeLayout.setVisibility(View.GONE);
                                toDate.setClickable(true);
                            }else {
                                timeLayout.setVisibility(View.VISIBLE);
                                toDate.setClickable(false);
                            }

                        if (countOfTimes != 0){
                            toTime.setClickable(false);
                        }else {
                            toTime.setClickable(true);
                        }

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }

            @Override
            public void onFailure(Call<LeaveTypes> call, Throwable t) {

                String errorMessage = t.getMessage();
                progressDialog.dismiss();
                ReloadDialog(errorMessage,new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        getLeaveTypes();
                        return null;
                    }
                });

            }
        });

    }

    public void getReplacementEmp(){
        progressDialog.show();

        Call<GetResult_JsonArray> call = placeHolderApi.getReplacementEmp("Bearer "+LogIn.token,LogIn.employeeDetailsId);
        call.enqueue(new Callback<GetResult_JsonArray>() {
            @Override
            public void onResponse(Call<GetResult_JsonArray> call, Response<GetResult_JsonArray> response) {
                replacementEmployeeListList = new ArrayList<>();
                ReplacementEmployee replacementEmployee = new ReplacementEmployee(0,getString(R.string.please_select_replacement));
                replacementEmployeeListList.add(replacementEmployee);

                JsonArray ReplacemenEmp = response.body().getResult();
                for (int i=0;i<ReplacemenEmp.size();i++){
                    JsonObject ReplacementObj = (JsonObject) ReplacemenEmp.get(i);
                    int replacementId = ReplacementObj.get("id").getAsInt();
                    String replacementName  = ReplacementObj.get("name").toString().replaceAll("\"","");
                    ReplacementEmployee replacementEmployee1 = new ReplacementEmployee(replacementId,replacementName);
                    replacementEmployeeListList.add(replacementEmployee1);
                }

                replacementAdapter = new ArrayAdapter<ReplacementEmployee>(Requests.this,R.layout.spinner_row,replacementEmployeeListList);
                replacementAdapter.setDropDownViewResource(R.layout.spinner_row);
                replacementEmpSpinner.setAdapter(replacementAdapter);

                replacementEmpSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        progressDialog.dismiss();
                        ReplacementEmployee replacementEmployee1 = (ReplacementEmployee) parent.getSelectedItem();
                        selectedReplacementEmp = replacementEmployee1.getEmpId();

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            }

            @Override
            public void onFailure(Call<GetResult_JsonArray> call, Throwable t) {

                String errorMessage = t.getMessage();
                progressDialog.dismiss();
                ReloadDialog(errorMessage,new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        getReplacementEmp();
                        return null;
                    }
                });

            }
        });


    }

    private void addRequest(){
        progressDialog.show();

        attachments = new ArrayList<Attachment>();
        notes = Notes.getText().toString();

        NewRequestParams params = new NewRequestParams(LogIn.employeeDetailsId,selectedLeaveTypeId,requestFromDate,requestToDate,
                requestFromTime,requestToTime,LogIn.employeeDetailsId,notes,selectedReplacementEmp,attachments);

        Call<NewRequestParams> call = placeHolderApi.addRequest("Bearer " + LogIn.token,params);

        call.enqueue(new Callback<NewRequestParams>() {
            @Override
            public void onResponse(Call<NewRequestParams> call, Response<NewRequestParams> response) {

                int code = response.code();
                if (code == 200) {

                    progressDialog.dismiss();

                    SuccessDialog(getString(R.string.request_done));





                }else {

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.errorBody().string());
                        JSONObject aaa = jsonObject.getJSONObject("responseException");
                        String userMessage = aaa.getString("exceptionMessage");
                        progressDialog.dismiss();
                        if (userMessage == "You are not Authorized."||userMessage.equals("You are not Authorized."))
                        {
                            Toast.makeText(Requests.this, userMessage, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(Requests.this,LogIn.class));
                        }else {
//                            Toast.makeText(Requests.this, userMessage, Toast.LENGTH_LONG).show();


                            ErrorMessage(userMessage);




                        }
//
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<NewRequestParams> call, Throwable t) {
                progressDialog.dismiss();
                String errorMessage = t.getMessage();
                ReloadDialog(errorMessage,new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        addRequest();
                        return null;
                    }
                });
            }
        });




    }
    
    private void ReloadDialog (String Message ,final Callable<Void> methodParam){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.connection_error);
        alertDialogBuilder
                .setMessage(Message)
                .setCancelable(false)
                .setPositiveButton(R.string.reload,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
//                                        moveTaskToBack(true);
                                try {
                                    methodParam.call();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }


                            }
                        })

                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        progressDialog.dismiss();
                        onBackPressed();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }



    private void ErrorDialog (String Message ){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        alertDialogBuilder.setTitle("Connection Error");
        alertDialogBuilder
                .setMessage(Message)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
//                                        moveTaskToBack(true);



                            }
                        }

                );
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    private void SuccessDialog (String Message ){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
//        alertDialogBuilder.setTitle("Connection Error");
        alertDialogBuilder
                .setMessage(Message)
                .setCancelable(false)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
//                                        moveTaskToBack(true);
                                startActivity(new Intent(Requests.this,Home.class));
                            }
                        }
                );
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    void ErrorMessage (String messageCode){
        Call<GetResult_JsonObject> call = placeHolderApi.getErrorMessage(messageCode);
        call.enqueue(new Callback<GetResult_JsonObject>() {
            @Override
            public void onResponse(Call<GetResult_JsonObject> call, Response<GetResult_JsonObject> response) {

                int code = response.code();
                if (code == 200) {
                    GetResult_JsonObject getResult = response.body();
                    JsonObject result = getResult.getResult();
                    if (LogIn.language == "English"|| LogIn.language.equals("English")){
                        errorMesaage = result.get("messageLatinValue").toString().replaceAll("\"","");
                    }else {
                        errorMesaage = result.get("messageLocalValue").toString().replaceAll("\"","");
                    }
                    ErrorDialog(errorMesaage);
                }else {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.errorBody().string());
                        JSONObject aaa = jsonObject.getJSONObject("responseException");
                        String userMessage = aaa.getString("exceptionMessage");
                        progressDialog.dismiss();
                        if (userMessage == "You are not Authorized."||userMessage.equals("You are not Authorized."))
                        {
                            Toast.makeText(Requests.this, userMessage, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(Requests.this,LogIn.class));
                        }else {
//                            Toast.makeText(Requests.this, userMessage, Toast.LENGTH_LONG).show();

                            ErrorDialog(userMessage);
                        }
//
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<GetResult_JsonObject> call, Throwable t) {
                String errorMessage = t.getMessage();
            }
        });
    }
}