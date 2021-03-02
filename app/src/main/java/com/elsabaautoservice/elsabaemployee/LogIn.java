package com.elsabaautoservice.elsabaemployee;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class LogIn extends AppCompatActivity {

    private PlaceHolderApi placeHolderApi ;
    Button btnLogin ;
    CheckBox rememberMe ;


    TextInputLayout inputLayoutName,inputLayoutPassword;
    EditText eT_UserName,eT_Password ;
    TextView errorText ;

    public static String token="";
    public static long employeeDetailsId ;
    public static String language ;
    String userName = "";
    String password="" ;

    NetworkInfo netInfo ;
    ConnectivityManager connectivityManager ;

    Dialog progressDialog ;



    String errorMessage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setLocale("Ar");
        setContentView(R.layout.activity_log_in);


        language = Locale.getDefault().getDisplayLanguage();

        btnLogin = findViewById(R.id.btnLogIn);
        eT_UserName= findViewById(R.id.lgnUserName);
        eT_Password= findViewById(R.id.lgnPassword);
        inputLayoutName = findViewById(R.id.usernameInputLayout);
        inputLayoutPassword = findViewById(R.id.passwordInputLayout);
        errorText = findViewById(R.id.logIn_ErrorText);
        rememberMe = findViewById(R.id.logIn_RememberMe);

        progressDialog = new Dialog(LogIn.this);
        progressDialog.setContentView(R.layout.progressdialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCancelable(false);

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo == null){
            Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), "Check Internet", Snackbar.LENGTH_LONG);
            snackbar.show();
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.basic_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        placeHolderApi = retrofit.create(PlaceHolderApi.class);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                netInfo = connectivityManager.getActiveNetworkInfo();
                if (netInfo == null){
                    Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.check_internet_connection, Snackbar.LENGTH_LONG);
                    snackbar.show();
                }else {


                    userName = eT_UserName.getText().toString();
                    password = eT_Password.getText().toString();
                    errorText.setVisibility(View.GONE);

                    boolean isValid = true ;
                    if (userName.isEmpty()){
                        inputLayoutName.setError(getString(R.string.mandatory));
                        isValid = false ;
                    }else {
                        inputLayoutName.setErrorEnabled(false);

                    }
                    if (password.isEmpty()){
                        inputLayoutPassword.setError(getString(R.string.mandatory));
                        isValid = false ;
                    }else {
                        inputLayoutPassword.setErrorEnabled(false);
                    }
                    if (isValid) {

                        UserLogIn();
                    }}
            }
        });

        SessionManager sessionManager = new SessionManager(LogIn.this,SessionManager.SESION_REMEMBERME);
        if (sessionManager.checkRememberMe()) {
            HashMap<String, String> rememberMeDetails = sessionManager.getRememberMeDetailsFromSession();
            eT_UserName.setText(rememberMeDetails.get(SessionManager.KEY_SESSIONUSERNAME));
            eT_Password.setText(rememberMeDetails.get(SessionManager.KEY_SESSIONPASWORD));
            userName = (rememberMeDetails.get(SessionManager.KEY_SESSIONUSERNAME));
            password = (rememberMeDetails.get(SessionManager.KEY_SESSIONPASWORD));



            UserLogIn();
        }


    }
    private void UserLogIn(){
        progressDialog.show();
        LogInUser user = new LogInUser(userName,password);

        if (rememberMe.isChecked()){
            SessionManager sessionManager = new SessionManager(LogIn.this,SessionManager.SESION_REMEMBERME);
            sessionManager.createRememberMeSession(userName,password);

        }
        Call<LogInUser> call = placeHolderApi.logInUser(user);


        call.enqueue(new Callback<LogInUser>(){
            @Override
            public void onResponse(Call<LogInUser> call, Response<LogInUser> response) {
                int code = response.code();
                if (code == 200) {
                    LogInUser responseuser = response.body();
                    JsonObject result = responseuser.getResult();
                    token = result.get("token").toString().replaceAll("\"", "");
                    employeeDetailsId = result.get("employeeDetailsId").getAsLong();

                    String empName = result.get("fullName").toString().replaceAll("\"", "");
//                    String empName = result.get("username").toString().replaceAll("\"", "");

                    String empFingerPrint = result.get("fingerPrint").toString().replaceAll("\"", "");

                    if (token.equals("null")){
                        progressDialog.dismiss();
                        errorText.setVisibility(View.VISIBLE);

                    }else {
                        progressDialog.dismiss();
                        startActivity(new Intent(LogIn.this, Home.class));
                        Home.empName = empName + "  ("+empFingerPrint+")" ;
                    }
                }else {

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.errorBody().string());
                        JSONObject aaa = jsonObject.getJSONObject("responseException");
                        String userMessage = aaa.getString("exceptionMessage").replaceAll("\"","");

                        progressDialog.dismiss();
                        errorText.setVisibility(View.VISIBLE);
//                        errorText.setText(userMessage);
//---------------------------Error Message--------------------------------------------

//ErrorMessage errorMessage = new ErrorMessage();

                       ErrorMessage(userMessage);














//---------------------------Error Message--------------------------------------------
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<LogInUser> call, Throwable t) {
                progressDialog.dismiss();
                String errorMessage = t.getMessage();
                ReloadDialog(errorMessage,new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        UserLogIn();
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

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle(R.string.exit_app);
        alertDialogBuilder
                .setMessage(R.string.click_yes_to_exit)
                .setCancelable(false)
                .setPositiveButton(R.string.yes,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                moveTaskToBack(true);
                                Process.killProcess(Process.myPid());
                                System.exit(0);
                                finish();
                            }
                        })

                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                        errorMessage = result.get("messageLatinValue").toString().replaceAll("\"","");
                    }else {
                        errorMessage = result.get("messageLocalValue").toString().replaceAll("\"","");
                    }

                    errorText.setText(errorMessage);


                }else {

                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.errorBody().string());
                        JSONObject aaa = jsonObject.getJSONObject("responseException");
                        String userMessage = aaa.getString("exceptionMessage");
                        progressDialog.dismiss();
                        if (userMessage == "You are not Authorized."||userMessage.equals("You are not Authorized."))
                        {
                            Toast.makeText(LogIn.this, userMessage, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(LogIn.this,LogIn.class));
                        }else {

                            errorText.setText(userMessage);
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

//    public void setLocale(String lang) {
//
//        Resources res = getResources();
//        DisplayMetrics dm = res.getDisplayMetrics();
//        Configuration conf = res.getConfiguration();
//       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
//           conf.setLocale(new Locale(lang.toLowerCase()));
//       }else {
//           conf.locale = new Locale(lang.toLowerCase());
//       }
//        res.updateConfiguration(conf, dm);
//
//    }

}