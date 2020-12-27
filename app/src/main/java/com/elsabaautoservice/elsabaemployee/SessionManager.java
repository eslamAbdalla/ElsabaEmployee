package com.elsabaautoservice.elsabaemployee;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    SharedPreferences userSession ;
    SharedPreferences.Editor editor ;
    Context context ;

    public static final  String SESION_REMEMBERME = "rememberMe" ;

    private static final String IS_REMEMBERME ="IsRememberMe";
    public static final String KEY_SESSIONUSERNAME ="UserName";
    public static final String KEY_SESSIONPASWORD ="Password";

    public SessionManager(Context context,String sessionName) {
        this.context = context;
        userSession = context.getSharedPreferences(sessionName,Context.MODE_PRIVATE);
        editor = userSession.edit();
    }
    public void  createRememberMeSession (String uName, String password){
        editor.putBoolean(IS_REMEMBERME,true);
        editor.putString(KEY_SESSIONUSERNAME,uName);
        editor.putString(KEY_SESSIONPASWORD,password);

        editor.commit() ;

    }

    public void  ClearRememberMeSession (){
        editor.putBoolean(IS_REMEMBERME,false);

        editor.commit() ;

    }

    public HashMap<String,String> getRememberMeDetailsFromSession (){
        HashMap<String,String> userData = new HashMap<>();

        userData.put(KEY_SESSIONUSERNAME,userSession.getString(KEY_SESSIONUSERNAME,null));
        userData.put(KEY_SESSIONPASWORD,userSession.getString(KEY_SESSIONPASWORD,null));

        return userData ;
    }

    public boolean checkRememberMe(){
        if (userSession.getBoolean(IS_REMEMBERME,false)){
            return true ;

        }else
            return false ;
    }

}
