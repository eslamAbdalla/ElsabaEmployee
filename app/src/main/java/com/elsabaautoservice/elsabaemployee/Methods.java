package com.elsabaautoservice.elsabaemployee;

import android.content.Context;
import android.content.DialogInterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import androidx.appcompat.app.AlertDialog;

public class Methods {





      String dateFormat (String Date ,String format){
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        java.util.Date date = null;
        try {
            date = fullDateFormat.parse(Date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String out = dateFormat.format(date);
        return out;
    }

     String timeFormat (String Time){
        SimpleDateFormat fullDateFormat = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        Date date = null;
        try {
            date = fullDateFormat.parse(Time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String out = dateFormat.format(date);
        return out;
    }


     static String arabicToDecimal(String number) {
        char[] chars = new char[number.length()];
        for (int i = 0; i < number.length(); i++) {
            char ch = number.charAt(i);
            if (ch >= 0x0660 && ch <= 0x0669)
                ch -= 0x0660 - '0';
            else if (ch >= 0x06f0 && ch <= 0x06F9)
                ch -= 0x06f0 - '0';
            chars[i] = ch;
        }
        return new String(chars);


    }



     void ErrorDialog (String Message, Context context ){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
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

                )
                .setTitle(R.string.error)
                .setIcon(R.drawable.error_512)
        ;
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }






}
