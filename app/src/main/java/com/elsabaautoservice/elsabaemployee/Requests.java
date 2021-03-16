package com.elsabaautoservice.elsabaemployee;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.net.Uri;
import android.net.nsd.NsdServiceInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.MenuItem;
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

import java.io.File;
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
    TextView fromTime,toTime,fileName ;
    EditText Notes ;
    Button save ,selectFile ;

    LinearLayout timeLayout,attachmentLayout ;

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

    //    File file ;
    public static final int PICK_IMAGE = 1;
    String filePath ;
    String FileName ;
    String attachmentName ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        leaveTypeSpinner = findViewById(R.id.requests_LeaveTypeSpinner);
        replacementEmpSpinner = findViewById(R.id.requests_ReplacementSpinner);
        fromDate = findViewById(R.id.requests_LeaveDateFrom);
        toDate = findViewById(R.id.requests_LeaveDateTo);
        fromTime = findViewById(R.id.requests_LeaveTimeFrom);
        toTime = findViewById(R.id.requests_LeaveTimeTo);
        Notes = findViewById(R.id.requests_Note) ;
        fileName = findViewById(R.id.requests_attachmentName) ;
        timeLayout = findViewById(R.id.timeLayoute);
        attachmentLayout = findViewById(R.id.attachmentLayout);

        save = findViewById(R.id.requests_Save);
        selectFile = findViewById(R.id.selectFile);

        progressDialog = new Dialog(Requests.this);
        progressDialog.setContentView(R.layout.progressdialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCancelable(false);


        if (ContextCompat.checkSelfPermission(Requests.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Requests.this,new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE
            },100);
        }


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

        selectFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SelectFile() ;

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
//                    if (!requireAttachment){
//                        LeaveTypes leaveTypes = new LeaveTypes(leaveTypeId,leaveTypeName,requireTime,CountOfTimes,requireAttachment);
//                        leaveTypesList.add(leaveTypes);
//                    }
                    LeaveTypes leaveTypes = new LeaveTypes(leaveTypeId,leaveTypeName,requireTime,CountOfTimes,requireAttachment);
                    leaveTypesList.add(leaveTypes);

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
                            requireAttachment = leaveType.isRequireAttachment();

                            if (!requireTime){
                                timeLayout.setVisibility(View.GONE);
                                toDate.setClickable(true);
                            }else {
                                timeLayout.setVisibility(View.VISIBLE);
                                toDate.setClickable(false);
                            }
                        if (requireAttachment){
                            attachmentLayout.setVisibility(View.VISIBLE);

                        }else {
                            attachmentLayout.setVisibility(View.GONE);

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
        Attachment attachment = new Attachment(attachmentName,FileName);
        attachments.add(attachment) ;
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



    private  void uploadImage (){



        progressDialog.show();

        MultipartBody.Part requestImage = null ;
        File file = new File(filePath);

        FileName = file.getName();

//        fileName.setText(FileName);

        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"),file);
        requestImage = MultipartBody.Part.createFormData("image",file.getName(),requestFile);


        Call<Result> call = placeHolderApi.UploadImage(requestImage);
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {


//                String aaaaaaaa = response.body().getResult();


                int code = response.code();
                if (code == 200) {

                    attachmentName = response.body().getResult();



                    fileName.setText(FileName);

                    progressDialog.dismiss();


                }else  {

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
            public void onFailure(Call<Result> call, Throwable t) {

                progressDialog.dismiss();
                String errorMessage = t.getMessage();
                ReloadDialog(errorMessage,new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        uploadImage();
                        return null;
                    }
                });
            }
        });






    }



    private void SelectFile (){
//        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
//        chooseFile.setType("*/*");
//        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
//        startActivityForResult(chooseFile, PICK_IMAGE);


//
//        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
//        chooseFile.setType("image/*");
//        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
//        startActivityForResult(chooseFile, PICK_IMAGE);



//        Intent chooseFile = new Intent(Intent.ACTION_PICK);
        Intent chooseFile = new Intent(Intent.ACTION_PICK);
        chooseFile.setType("*/*");
//        chooseFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        chooseFile = Intent.createChooser(chooseFile, "Choose a file");
        startActivityForResult(chooseFile, PICK_IMAGE);




    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int aaa = resultCode;

        if (requestCode == PICK_IMAGE && data != null ) {

            Uri uri = data.getData();

//            String [] proj={MediaStore.Images.Media.DATA};
//            Cursor cursor = managedQuery( uri,
//                    proj, // Which columns to return
//                    null,       // WHERE clause; which rows to return (all rows)
//                    null,       // WHERE clause selection arguments (none)
//                    null); // Order-by clause (ascending by name)
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            cursor.moveToFirst();
//
//            filePath =  cursor.getString(column_index);

//            uploadImage() ;

            filePath = getPath(Requests.this,uri);

            uploadImage() ;

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
    public boolean onOptionsItemSelected (MenuItem item){
        startActivity(new Intent(Requests.this,Home.class));
        return true ;
    }




    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public  String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
             if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);



            }









            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        uploadImage();
        return null;


    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri The Uri to query.
     * @param selection (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }






}