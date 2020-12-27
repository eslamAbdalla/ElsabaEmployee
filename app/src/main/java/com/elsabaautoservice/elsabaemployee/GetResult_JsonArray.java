package com.elsabaautoservice.elsabaemployee;

import com.google.gson.JsonArray;
import com.google.gson.annotations.SerializedName;

public class GetResult_JsonArray {

    @SerializedName("result")
    private JsonArray result ;

    public GetResult_JsonArray(){}


    public JsonArray getResult() {
        return result;
    }
}
