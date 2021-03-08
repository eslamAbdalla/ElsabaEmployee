package com.elsabaautoservice.elsabaemployee;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Context;
import android.os.Process;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

public class HomeRV extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener , LeavesAdapter.OnItemClickListener{

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

    FloatingActionButton fabRequest, fabCheckInOut;

    RecyclerView leavesRecyclerView;
    RecyclerView.Adapter leavesAdapter;

    TextView employeeName, message;

    JsonArray balanceList;
    RecyclerView balanceRecyclerView;
    RecyclerView.Adapter balanceAdapter;

    JsonArray leavesList;

    private PlaceHolderApi placeHolderApi;
    Dialog progressDialog;

    public static String empName;

    Spinner approveStatusSpinner;
    ArrayAdapter<ApproveStatus> approveStatusAdapter;
    List<ApproveStatus> approveStatusList;

    //    ----------------------------------Params--------------------------------
    int aproveStatus = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_r_v);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        drawerLayout = findViewById(R.id.activity_main_drawer_layout);
        leavesRecyclerView = findViewById(R.id.home_LeavesRecyclerView);
        fabRequest = findViewById(R.id.home_fabAddRequest);
        fabCheckInOut = findViewById(R.id.home_CheckInOut);
        approveStatusSpinner = findViewById(R.id.home_ApproveStatusSpinner);
        employeeName = findViewById(R.id.home_EmpName);
        message = findViewById(R.id.home_message);

        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(this);

        progressDialog = new Dialog(HomeRV.this);
        progressDialog.setContentView(R.layout.progressdialog);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCancelable(false);
        progressDialog.show();


        balanceRecyclerView = findViewById(R.id.home_BalanceRecyclerView);
        balanceRecyclerView.setHasFixedSize(true);
        balanceRecyclerView.setLayoutManager(new LinearLayoutManager(this));


        leavesRecyclerView = findViewById(R.id.home_LeavesRecyclerView);
        leavesRecyclerView.setHasFixedSize(true);
        leavesRecyclerView.setLayoutManager(new LinearLayoutManager(this));


//        startActivity(new Intent(HomeRV.this,LeavesDetails.class));
//        LeavesDetails.selectedLeave = leavesList.get(position).getAsJsonObject();


        fabCheckInOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeRV.this, CheckInOut.class));
            }
        });

        fabRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeRV.this, Requests.class));
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.basic_url))
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        placeHolderApi = retrofit.create(PlaceHolderApi.class);

        employeeName.setText(empName);

        TaskStatusSpinner();
        getUserBalance();
//        getUserLeaves();
//startActivity(new Intent(this,EmployeeBalance.class));
    }

    private void TaskStatusSpinner() {
        approveStatusList = new ArrayList<>();
        ApproveStatus taskStatus0 = new ApproveStatus(0, getString(R.string.all));
        approveStatusList.add(taskStatus0);
        ApproveStatus taskStatus1 = new ApproveStatus(1, getString(R.string.pending));
        approveStatusList.add(taskStatus1);
        ApproveStatus taskStatus2 = new ApproveStatus(2, getString(R.string.approved));
        approveStatusList.add(taskStatus2);
        ApproveStatus taskStatus3 = new ApproveStatus(3, getString(R.string.rejected));
        approveStatusList.add(taskStatus3);

        approveStatusAdapter = new ArrayAdapter<ApproveStatus>(HomeRV.this, R.layout.spinner_row, approveStatusList);
        approveStatusAdapter.setDropDownViewResource(R.layout.spinner_row);
        approveStatusSpinner.setAdapter(approveStatusAdapter);

        approveStatusSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ApproveStatus approveStatus = (ApproveStatus) parent.getSelectedItem();
                aproveStatus = approveStatus.getStatusId();
                getUserLeaves();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void getUserLeaves() {
        progressDialog.show();
        Call<GetResult_JsonArray> call = placeHolderApi.getUserLeaves
                ("Bearer " + LogIn.token, "", "", LogIn.employeeDetailsId,
                        "", "", aproveStatus, "", false, LogIn.employeeDetailsId);
        call.enqueue(new Callback<GetResult_JsonArray>() {
            @Override
            public void onResponse(Call<GetResult_JsonArray> call, Response<GetResult_JsonArray> response) {
                int code = response.code();
                if (code == 200) {
                    GetResult_JsonArray responseBalance = response.body();
                    leavesList = responseBalance.getResult();

                    if (leavesList.size() == 0) {
                        message.setVisibility(View.VISIBLE);
                        message.setText(R.string.no_leaves);
                        progressDialog.dismiss();
                    } else {
                        message.setVisibility(View.GONE);

                        progressDialog.dismiss();
                    }
//                        leavesListView = findViewById(R.id.home_LeavesListView);
                    leavesAdapter = new LeavesAdapter(leavesList, HomeRV.this,HomeRV.this::onItemClick);
                    leavesRecyclerView.setAdapter(leavesAdapter);
                    progressDialog.dismiss();

                } else {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.errorBody().string());
                        JSONObject aaa = jsonObject.getJSONObject("responseException");
                        String userMessage = aaa.getString("exceptionMessage");
//                        ProgressBar.setVisibility(View.GONE);
                        progressDialog.dismiss();
//                        errorText.setVisibility(View.VISIBLE);
//                        errorText.setText(userMessage);
                        if (userMessage == "You are not Authorized." || userMessage.equals("You are not Authorized.")) {
                            Toast.makeText(HomeRV.this, userMessage, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(HomeRV.this, LogIn.class));
                        } else {
                            Toast.makeText(HomeRV.this, userMessage, Toast.LENGTH_LONG).show();
                        }
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
                ReloadDialog(errorMessage, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        getUserLeaves();
                        return null;
                    }
                });
            }
        });

    }

    private void ReloadDialog(String Message, final Callable<Void> methodParam) {
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
    public void onItemClick(int position) {


        startActivity(new Intent(HomeRV.this,LeavesDetails.class));
        LeavesDetails.selectedLeave = leavesList.get(position).getAsJsonObject();

    }


    class BalanceAdapter extends RecyclerView.Adapter<BalanceAdapter.ViewHolder> {

        JsonArray listItems;
        Context context;

        public BalanceAdapter(JsonArray listItems, Context context) {
            this.listItems = listItems;
            this.context = context;
        }

        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.balance_details_rv, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull BalanceAdapter.ViewHolder holder, int position) {

            JsonObject BalanceObj = listItems.get(position).getAsJsonObject();

            String leaveName = BalanceObj.get("leaveTypeName").toString().replaceAll("\"", "");
            String leaveTotalDays = BalanceObj.get("leaveTypeTotalDays").toString();
            int balance = BalanceObj.get("balance").getAsInt();


            holder.LeaveName.setText(leaveName);

            holder.Balance.setText(balance + "");


            if (leaveTotalDays.equals("null") || leaveTotalDays.equals("")) {
                holder.LeaveTotalDays.setText("0");
            } else {
                holder.LeaveTotalDays.setText(leaveTotalDays + "");
            }


        }

        @Override
        public int getItemCount() {
            return listItems.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView LeaveName;
            TextView LeaveTotalDays;
            TextView Balance;


            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                LeaveName = (TextView) itemView.findViewById(R.id.homeBalance_LeaveName);
                LeaveTotalDays = (TextView) itemView.findViewById(R.id.homeBalance_LeaveTotalDays);
                Balance = (TextView) itemView.findViewById(R.id.homeBalance_Balance);
            }
        }

    }



    private void getUserBalance() {
        progressDialog.show();
        Call<GetResult_JsonArray> call = placeHolderApi.getUserBalance("Bearer " + LogIn.token, LogIn.employeeDetailsId);

        call.enqueue(new Callback<GetResult_JsonArray>() {
            @Override
            public void onResponse(Call<GetResult_JsonArray> call, Response<GetResult_JsonArray> response) {

                int code = response.code();
                if (code == 200) {

                    GetResult_JsonArray responseBalance = response.body();
                    balanceList = responseBalance.getResult();

                    balanceAdapter = new BalanceAdapter(balanceList, HomeRV.this);
                    balanceRecyclerView.setAdapter(balanceAdapter);

                    progressDialog.dismiss();
                } else {


                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.errorBody().string());
                        JSONObject aaa = jsonObject.getJSONObject("responseException");
                        String userMessage = aaa.getString("exceptionMessage");
//                        ProgressBar.setVisibility(View.GONE);
                        progressDialog.dismiss();
//                        errorText.setVisibility(View.VISIBLE);
//                        errorText.setText(userMessage);
                        if (userMessage == "You are not Authorized." || userMessage.equals("You are not Authorized.")) {
                            Toast.makeText(HomeRV.this, userMessage, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(HomeRV.this, LogIn.class));
                        } else {
                            Toast.makeText(HomeRV.this, userMessage, Toast.LENGTH_LONG).show();
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
                ReloadDialog(errorMessage, new Callable<Void>() {
                    @Override
                    public Void call() throws Exception {
                        getUserBalance();
                        return null;
                    }
                });

            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_Balance) {
            drawerLayout.closeDrawers();
            startActivity(new Intent(this, EmployeeBalance.class));
        }
        if (id == R.id.nav_Attendance) {
            drawerLayout.closeDrawers();
            startActivity(new Intent(this, Attendance.class));
        }
        if (id == R.id.nav_Request) {
            drawerLayout.closeDrawers();
            startActivity(new Intent(this, Requests.class));
        }

        if (id == R.id.nav_Exit) {
            drawerLayout.closeDrawers();
            onBackPressed();
        }

        if (id == R.id.nav_LogOut) {
            SessionManager sessionManager = new SessionManager(HomeRV.this, SessionManager.SESION_REMEMBERME);
            sessionManager.ClearRememberMeSession();

            startActivity(new Intent(HomeRV.this, LogIn.class));
            drawerLayout.closeDrawers();
        }
        return false;
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
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}