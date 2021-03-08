package com.elsabaautoservice.elsabaemployee;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

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


}
