package com.example.android.sharethemeal;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


public class Shared_Preferences_class {
    static SharedPreferences total_shared_preference;
    public static String SHARED_PREFERENCE_NAME="total_shared_preference";

    
    public static Login_credentials get_login_details(Context context)
    {
        Login_credentials login=new Login_credentials();
        total_shared_preference=context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String login_string=total_shared_preference.getString("login_details","");
        Gson jason=new Gson();
        if(login_string.equals("")==false)
            login=jason.fromJson(login_string,new TypeToken<Login_credentials>(){}.getType());
        return login;

    }



    public static void put_login_details(Context context, Login_credentials login)
    {
        total_shared_preference=context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        Gson jason=new Gson();
        String login_details=jason.toJson(login);
        SharedPreferences.Editor editor=total_shared_preference.edit();
        editor.putString("login_details",login_details);
        editor.apply();
    }



    public static boolean keep_logged_in_or_not(Context context)
    {
        total_shared_preference=context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        boolean keep_logged_in=total_shared_preference.getBoolean("keep_logged_in",false);
        return keep_logged_in;
    }


    public static void set_keep_logged_in(boolean f,Context context)
    {
        total_shared_preference=context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor edit=total_shared_preference.edit();
        edit.putBoolean("keep_logged_in",f);
        edit.apply();
    }

}


