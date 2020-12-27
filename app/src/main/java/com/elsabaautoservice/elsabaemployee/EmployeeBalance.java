package com.elsabaautoservice.elsabaemployee;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.Callable;

public class EmployeeBalance extends AppCompatActivity {

    ListView balanceListView ;
    JsonArray balanceList ;

    private PlaceHolderApi placeHolderApi ;
    Dialog progressDialog ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_balance);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        balanceListView = findViewById(R.id.eBalance_BalanceListView);

        progressDialog = new Dialog(EmployeeBalance.this);
        progressDialog.setContentView(R.layout.progressdialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.basic_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        placeHolderApi = retrofit.create(PlaceHolderApi.class);

        getUserBalance();


    }

    private void getUserBalance(){
        progressDialog.show();
        Call<GetResult_JsonArray> call = placeHolderApi.getUserBalance("Bearer "+LogIn.token,LogIn.employeeDetailsId);

        call.enqueue(new Callback<GetResult_JsonArray>() {
            @Override
            public void onResponse(Call<GetResult_JsonArray> call, Response<GetResult_JsonArray> response) {

                int code = response.code();
                if (code == 200) {

                    GetResult_JsonArray responseBalance = response.body();
                    balanceList = responseBalance.getResult();

                    balanceListView = findViewById(R.id.eBalance_BalanceListView);
                    BalanceDetails homeBalanceDetails = new BalanceDetails();
                    balanceListView.setAdapter(homeBalanceDetails);

                    progressDialog.dismiss();
                }
                else {



                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.errorBody().string());
                        JSONObject aaa = jsonObject.getJSONObject("responseException");
                        String userMessage = aaa.getString("exceptionMessage");
//                        ProgressBar.setVisibility(View.GONE);
                        progressDialog.dismiss();
//                        errorText.setVisibility(View.VISIBLE);
//                        errorText.setText(userMessage);
                        if (userMessage == "You are not Authorized."||userMessage.equals("You are not Authorized."))
                        {
                            Toast.makeText(EmployeeBalance.this, userMessage, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(EmployeeBalance.this,LogIn.class));
                        }else {
                            Toast.makeText(EmployeeBalance.this, userMessage, Toast.LENGTH_LONG).show();
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
            public void onFailure(Call<GetResult_JsonArray> call, Throwable t) {

                progressDialog.dismiss();
                String errorMessage = t.getMessage();
                ReloadDialog(errorMessage,new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        getUserBalance();
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

    class BalanceDetails extends BaseAdapter {


        @Override
        public int getCount() {
            return balanceList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }





        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {


            view = getLayoutInflater().inflate(R.layout.ebalance_balance_details,null);

            TextView LeaveName = (TextView)view.findViewById(R.id.eBalance_LeaveName);
            TextView LeaveTotalDays = (TextView)view.findViewById(R.id.eBalance_LeaveTotalDays);
            TextView Balance = (TextView)view.findViewById(R.id.eBalance_Balance);

            JsonObject BalanceObj = balanceList.get(i).getAsJsonObject();

            String leaveName = BalanceObj.get("leaveTypeName").toString().replaceAll("\"","");
            String  leaveTotalDays = BalanceObj.get("leaveTypeTotalDays").toString();
            int balance = BalanceObj.get("balance").getAsInt();


            LeaveName.setText(leaveName);

            Balance.setText(balance+"");



            if (leaveTotalDays.equals("null")||leaveTotalDays.equals("")){
                LeaveTotalDays.setText("0");
            }else {
                LeaveTotalDays.setText(leaveTotalDays+"");
            }




            return view;
        }


    }
    public boolean onOptionsItemSelected (MenuItem item){
        startActivity(new Intent(EmployeeBalance.this,Home.class));
        return true ;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this,Home.class));
    }


}