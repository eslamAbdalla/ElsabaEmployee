package com.elsabaautoservice.elsabaemployee;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class GetResult_JsonObject {
    @SerializedName("result")
    private JsonObject result ;

    public GetResult_JsonObject(){

    }

    public JsonObject getResult() {
        return result;
    }

}
