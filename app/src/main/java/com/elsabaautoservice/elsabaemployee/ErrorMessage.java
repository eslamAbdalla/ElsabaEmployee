package com.elsabaautoservice.elsabaemployee;

import com.google.gson.JsonObject;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;



public class ErrorMessage extends AppCompatActivity {


    PlaceHolderApi placeHolderApi ;

    String errorMesaageEn ;
    String errorMesaageAr ;


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(getString(R.string.basic_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build();






    String ErrorMessage (String messageCode){





        placeHolderApi = retrofit.create(PlaceHolderApi.class);


            Call<GetResult_JsonObject> call = placeHolderApi.getErrorMessage(messageCode);
            call.enqueue(new Callback<GetResult_JsonObject>() {
                @Override
                public void onResponse(Call<GetResult_JsonObject> call, Response<GetResult_JsonObject> response) {


                    GetResult_JsonObject getResult = response.body();
                    JsonObject result = getResult.getResult();
                    errorMesaageEn = result.get("messageLatinValue").toString().replaceAll("\"","");
                    errorMesaageAr = result.get("messageLocalValue").toString().replaceAll("\"","");




                }

                @Override
                public void onFailure(Call<GetResult_JsonObject> call, Throwable t) {

                    String errorMessage = t.getMessage();


                }
            });

            if (LogIn.language == "English"){
                return errorMesaageEn ;
            }else {
                return errorMesaageAr ;
            }


    }






}
