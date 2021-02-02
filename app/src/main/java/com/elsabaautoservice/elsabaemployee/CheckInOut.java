package com.elsabaautoservice.elsabaemployee;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.text.Layout;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

public class CheckInOut extends AppCompatActivity  implements LocationListener {

    private PlaceHolderApi placeHolderApi ;

    LocationManager locationManager ;

    JsonArray attendanceList ;
    Dialog progressDialog ;
    ListView attendanceListView ;
    Button checkInOut ;
    Button checkInOutImage ;

    TextView AttendanceDate ;
    TextView AttendanceIn ;
    TextView AttendanceOut ;

    Vibrator vibrator ;
    LinearLayout inLayout ,outLayout ;

    //    ---------------------Params---------------------------------
    int employeeDetailsId ;
    String fromDate = "";
    String toDate =  "";
    String departmentID = "" ;
    Double latitude,Longitude;
    String attendanceNote = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in_out);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);




        attendanceListView = findViewById(R.id.checkInOut_ListView);
        checkInOut = findViewById(R.id.checkInOut_CheckInOut);

        checkInOutImage = findViewById(R.id.checkInOut_CheckInOut1);

         AttendanceDate = findViewById(R.id.checkInOut_Date);
         AttendanceIn = findViewById(R.id.checkInOut_CheckIn);
         AttendanceOut = findViewById(R.id.checkInOut_CheckOut);

        inLayout = findViewById(R.id.inLayout);
        outLayout = findViewById(R.id.outLayout) ;





        Methods format = new Methods();

         vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
        Date date = Calendar.getInstance().getTime();

        String out = format.arabicToDecimal(dateFormat2.format(date));

        fromDate = out ;
        toDate = out ;


        progressDialog = new Dialog(CheckInOut.this);
        progressDialog.setContentView(R.layout.progressdialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCancelable(false);
//        progressDialog.show();

        if (ContextCompat.checkSelfPermission(CheckInOut.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(CheckInOut.this,new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            },100);
        }



        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.basic_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        placeHolderApi = retrofit.create(PlaceHolderApi.class);


        checkInOutImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                progressDialog.show();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    vibrator.vibrate(300);
                }

                checkInOutImage.setClickable(false);

                GetLocation();

                return true;
            }
        });
//        checkInOutImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                GetLocation();
//            }
//        });








//        checkInOut.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                CheckInOut();
//                GetLocation();
//            }
//        });



//        GetLocation();

        GetAttendance();


    }

    private void GetAttendance() {
        progressDialog.show();

        GetAttendance params = new GetAttendance(LogIn.employeeDetailsId,fromDate,toDate,departmentID);

        Call<GetAttendance> call = placeHolderApi.getAttendance("Bearer " + LogIn.token,params);

        call.enqueue(new Callback<GetAttendance>() {
            @Override
            public void onResponse(Call<GetAttendance> call, Response<GetAttendance> response) {

                int code = response.code();





                if (code == 200) {

                    attendanceList = response.body().getResult();

                    Methods format = new Methods();

                    if (attendanceList.size()> 0) {


                        JsonObject attendanceObj = attendanceList.get(0).getAsJsonObject();

                        String attendanceDate = attendanceObj.get("workDay").toString().replaceAll("\"", "");
                        String attendanceIn = attendanceObj.get("attendanceTimeFrom").toString().replaceAll("\"", "");
                        String attendanceOut = attendanceObj.get("attendanceTimeTo").toString().replaceAll("\"", "");


//                        AttendanceDate.setText(format.dateFormat(attendanceDate, getString(R.string.date_format)));
//            AttendanceIn.setText(attendanceIn);
//            AttendanceOut.setText(attendanceOut);


                        if (attendanceIn.equals("null") || attendanceIn.equals("")) {
                            AttendanceIn.setText("-----");
                        } else {
                            AttendanceIn.setText(format.timeFormat(attendanceIn));
                        }

                        if (attendanceOut.equals("null") || attendanceOut.equals("")) {
                            AttendanceOut.setText("-----");
                        } else {
                            AttendanceOut.setText(format.timeFormat(attendanceOut));
                        }


//                        if (!attendanceOut.equals("null")) {
//                            checkInOut.setText(R.string.check_out);
//
//                            inLayout.setVisibility(View.GONE);
//                            outLayout.setVisibility(View.VISIBLE);
//
//
//                        }else {
//
//
//                            inLayout.setVisibility(View.VISIBLE);
//                            outLayout.setVisibility(View.GONE);
//                        }


                    }

//                    CheckInOutDetails checkInOutDetails = new CheckInOutDetails();
//                    attendanceListView.setAdapter(checkInOutDetails);



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
                            Toast.makeText(CheckInOut.this, userMessage, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(CheckInOut.this,LogIn.class));
                        }else {
                            Toast.makeText(CheckInOut.this, userMessage, Toast.LENGTH_LONG).show();
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

    private void CheckInOut(){

        progressDialog.show();


        CheckInOutParams params = new CheckInOutParams(LogIn.employeeDetailsId,attendanceNote,Longitude,latitude);

        Call<CheckInOutParams> call = placeHolderApi.checkInOut("Bearer " + LogIn.token,params);

        call.enqueue(new Callback<CheckInOutParams>() {
            @Override
            public void onResponse(Call<CheckInOutParams> call, Response<CheckInOutParams> response) {

                int code = response.code();
                if (code == 200) {

                    GetAttendance();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(300, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        vibrator.vibrate(300);
                    }

                    progressDialog.dismiss();

                    checkInOutImage.setClickable(true);
                }else {

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.errorBody().string());
                        JSONObject aaa = jsonObject.getJSONObject("responseException");
                        String userMessage = aaa.getString("exceptionMessage");
                        progressDialog.dismiss();
                        if (userMessage == "You are not Authorized."||userMessage.equals("You are not Authorized."))
                        {
                            Toast.makeText(CheckInOut.this, userMessage, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(CheckInOut.this,LogIn.class));
                        }else {
                            ReloadDialog(userMessage +" - "+ Longitude+" - "+latitude ,new Callable<Void>() {
                                @Override
                                public Void call() throws Exception {
                                    GetLocation();
                                    return null;
                                }
                            });
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
            public void onFailure(Call<CheckInOutParams> call, Throwable t) {
                progressDialog.dismiss();
                String errorMessage = t.getMessage();
                ReloadDialog(errorMessage,new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        CheckInOut();
                        return null;
                    }
                });
            }
        });




    }
    @SuppressLint("MissingPermission")
    private void GetLocation() {

        progressDialog.show();

        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;


        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        try {
            network_enabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled && !network_enabled) {
            // notify user
//            Toast.makeText(CheckInOut.this,"Enable GPS Please",Toast.LENGTH_LONG).show();

            ErrorDialog(getString(R.string.enable_GPS));
            progressDialog.dismiss();

        }else {

            try {
                progressDialog.show();
//            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 5, CheckInOut.this);

                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);




                latitude =location.getLatitude();
                Longitude = location.getLongitude();

//                DecimalFormat df = new DecimalFormat("#.####");
//                latitude = Double.valueOf(df.format(latitude));
//                Longitude = Double.valueOf(df.format(Longitude));



                progressDialog.dismiss();
                CheckInOut();

            } catch (Exception e) {
                progressDialog.dismiss();
                String error = e.getMessage();

                ReloadDialog(getString(R.string.no_GPS_signal),new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        GetLocation();
                        return null;
                    }
                });

            }

        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }


    class CheckInOutDetails extends BaseAdapter {

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

            view = getLayoutInflater().inflate(R.layout.checkinout_details,null);

            TextView AttendanceDate = view.findViewById(R.id.checkInOut_Date);
            TextView AttendanceIn = view.findViewById(R.id.checkInOut_CheckIn);
            TextView AttendanceOut = view.findViewById(R.id.checkInOut_CheckOut);


            JsonObject attendanceObj = attendanceList.get(i).getAsJsonObject();

            String attendanceDate = attendanceObj.get("workDay").toString().replaceAll("\"","");
            String attendanceIn = attendanceObj.get("attendanceTimeFrom").toString().replaceAll("\"","");
            String attendanceOut = attendanceObj.get("attendanceTimeTo").toString().replaceAll("\"","");

//            String attendanceDate = "" ;


//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
//            SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd");
//            try {
//                Date date = dateFormat.parse(attendanceFullDate);
//
//                String out = dateFormat2.format(date);
//
//                attendanceDate = out ;
//
//
//            } catch (ParseException e) {
//
//                String ee = e.getMessage() ;
//            }




            AttendanceDate.setText(format.dateFormat(attendanceDate,getString(R.string.date_format)));
//            AttendanceIn.setText(attendanceIn);
//            AttendanceOut.setText(attendanceOut);


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





            if (! attendanceIn.equals("null")){
                checkInOut.setText(R.string.check_out);
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



    public boolean onOptionsItemSelected (MenuItem item){
        startActivity(new Intent(CheckInOut.this,Home.class));
        return true ;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        GetLocation();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(CheckInOut.this,Home.class));
    }
}