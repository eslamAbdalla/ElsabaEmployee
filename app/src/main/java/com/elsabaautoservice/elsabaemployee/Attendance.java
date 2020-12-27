package com.elsabaautoservice.elsabaemployee;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ParseException;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;

public class Attendance extends AppCompatActivity {

    private PlaceHolderApi placeHolderApi ;

    LinearLayout filterLayOut ;

    TextView FromDate,ToDate ;
    Button search ;


    JsonArray attendanceList ;
    Dialog progressDialog ;
    ListView attendanceListView ;

    long startDate ;

//    ---------------------Params---------------------------------
    int employeeDetailsId ;
    String fromDate = "";
    String toDate =  "";
    String departmentID = "" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        attendanceListView = findViewById(R.id.attendance_ListView);

        FromDate = findViewById(R.id.attendance_AttendanceDateFrom);
        ToDate = findViewById(R.id.attendance_AttendanceDateTo);

        search = findViewById(R.id.attendance_Search);
        filterLayOut = findViewById(R.id.attendance_DateLayout) ;


        progressDialog = new Dialog(Attendance.this);
        progressDialog.setContentView(R.layout.progressdialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCancelable(false);
//        progressDialog.show();



        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.basic_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        placeHolderApi = retrofit.create(PlaceHolderApi.class);


        FromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showFromDatePickerDialog();

            }
        });
        ToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showToDatePickerDialog();

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                filterLayOut.setVisibility(View.GONE);
                GetAttendance();


            }
        });


        GetConfiguredDates();

    }

    private void GetConfiguredDates(){
        progressDialog.show();
        Call<GetResult_JsonObject> call = placeHolderApi.getConfiguredDates("Bearer "+LogIn.token);

        call.enqueue(new Callback<GetResult_JsonObject>() {
            @Override
            public void onResponse(Call<GetResult_JsonObject> call, Response<GetResult_JsonObject> response) {

                int code = response.code();
                if (code == 200) {

                    GetResult_JsonObject getResult = response.body();
                    JsonObject result = getResult.getResult();
                    fromDate = result.get("monthStartDate").toString().replaceAll("\"","");
                    toDate = result.get("monthEndDate").toString().replaceAll("\"","");
                    GetAttendance();

                }else {

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.errorBody().string());
                        JSONObject aaa = jsonObject.getJSONObject("responseException");
                        String userMessage = aaa.getString("exceptionMessage");
                        progressDialog.dismiss();
                        if (userMessage == "You are not Authorized."||userMessage.equals("You are not Authorized."))
                        {
                            Toast.makeText(Attendance.this, userMessage, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(Attendance.this,LogIn.class));
                        }else {
                            Toast.makeText(Attendance.this, userMessage, Toast.LENGTH_LONG).show();
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
                progressDialog.dismiss();
                String errorMessage = t.getMessage();
                ReloadDialog(errorMessage,new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        GetConfiguredDates();
                        return null;
                    }
                });

            }
        });

    }



    private void GetAttendance() {

        GetAttendance params = new GetAttendance(LogIn.employeeDetailsId,fromDate,toDate,departmentID);

        Call<GetAttendance> call = placeHolderApi.getAttendance("Bearer " + LogIn.token,params);

        call.enqueue(new Callback<GetAttendance>() {
            @Override
            public void onResponse(Call<GetAttendance> call, Response<GetAttendance> response) {

                int code = response.code();
                if (code == 200) {

                    attendanceList = response.body().getResult();

                    AttendanceDetails attendanceDetails = new AttendanceDetails();
                    attendanceListView.setAdapter(attendanceDetails);

                    progressDialog.dismiss();
                }else {

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.errorBody().string());
                        JSONObject aaa = jsonObject.getJSONObject("responseException");
                        String userMessage = aaa.getString("exceptionMessage");
                        progressDialog.dismiss();
                        if (userMessage == "You are not Authorized."||userMessage.equals("You are not Authorized."))
                        {
                            Toast.makeText(Attendance.this, userMessage, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(Attendance.this,LogIn.class));
                        }else {
                            Toast.makeText(Attendance.this, userMessage, Toast.LENGTH_LONG).show();
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
            public void onFailure(Call<GetAttendance> call, Throwable t) {
                progressDialog.dismiss();
                String errorMessage = t.getMessage();
                ReloadDialog(errorMessage,new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        GetAttendance();
                        return null;
                    }
                });
            }
        });
    }



    class AttendanceDetails extends BaseAdapter {

        @Override
        public int getCount() {
            return attendanceList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup parent) {

            Methods format = new Methods();

            view = getLayoutInflater().inflate(R.layout.attendance_details,null);

            TextView AttendanceDate = view.findViewById(R.id.attendance_Date);
            TextView AttendanceIn = view.findViewById(R.id.attendance_CheckIn);
            TextView AttendanceOut = view.findViewById(R.id.attendance_CheckOut);
            TextView AttendanceHolidayName = view.findViewById(R.id.attendance_HolidayName);

            JsonObject attendanceObj = attendanceList.get(i).getAsJsonObject();

            String attendanceDate = attendanceObj.get("workDay").toString().replaceAll("\"","");
            String attendanceIn = attendanceObj.get("attendanceTimeFrom").toString().replaceAll("\"","");
            String attendanceOut = attendanceObj.get("attendanceTimeTo").toString().replaceAll("\"","");
            String attendanceHolidayName = attendanceObj.get("holidayName").toString().replaceAll("\"","");
            String attendanceleaveTypeName = attendanceObj.get("leaveTypeName").toString().replaceAll("\"","");

            AttendanceDate.setText(format.dateFormat(attendanceDate,getString(R.string.date_format)));

            if (attendanceIn.equals("null")||attendanceIn.equals("")){
                AttendanceIn.setText("-----");
            }else {
                AttendanceIn.setText(format.timeFormat(attendanceIn));
            }

            if (attendanceOut.equals("null")||attendanceOut.equals("")){
                AttendanceOut.setText("-----");
            }else {
                AttendanceOut.setText(format.timeFormat(attendanceOut));
            }

//            AttendanceHolidayName.setText(attendanceHolidayName);

            if (attendanceleaveTypeName.equals("null")||attendanceleaveTypeName.equals("")){

                if (attendanceHolidayName.equals("null")||attendanceHolidayName.equals("")){
                    AttendanceHolidayName.setText("-----");
                }else {
                    AttendanceHolidayName.setText(attendanceHolidayName);
                }
            }else {
                AttendanceHolidayName.setText(attendanceleaveTypeName);
            }

            return view;
        }
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





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.attendance, menu);
        return true;
    }

    public boolean onOptionsItemSelected (MenuItem item){

        int id = item.getItemId();
        if (id == R.id.action_filter) {

          int visibility =   filterLayOut.getVisibility();

          if (visibility == 0) {
              filterLayOut.setVisibility(View.GONE);
          }else if(visibility == 8)  {
              filterLayOut.setVisibility(View.VISIBLE);
          }



            return true;
        }else {
            startActivity(new Intent(Attendance.this,Home.class));
        }
        return super.onOptionsItemSelected(item);
    }


    private void showFromDatePickerDialog(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                String date = year+" - "+(month+1)+" - "+dayOfMonth ;

                fromDate = date ;
                FromDate.setText(fromDate);

                toDate =date ;
                ToDate.setText(fromDate);




                try {
                    String dateString = fromDate;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy - MM - dd");
                    Date date1 = sdf.parse(dateString);

                    startDate = date1.getTime();

                } catch (ParseException | java.text.ParseException e) {
                    e.printStackTrace();
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
                toDate =date ;
                ToDate.setText(toDate);
            }
        }, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
//        datePickerDialog.getDatePicker().setMinDate(Calendar.getInstance().getTimeInMillis());



        datePickerDialog.getDatePicker().setMinDate(startDate);



//        datePickerDialog.getDatePicker().setMinDate();
        datePickerDialog.show();
    }




}