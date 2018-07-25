package com.example.mytestwechat;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * ClassName:   SharedPreferencesHelp.java
 * Description:
 * Author :     leach.chen
 * Date:        2018/1/19 13:11
 **/

public class SharedPreferencesHelp {

    public static SharedPreferencesHelp mInstance;
    private SharedPreferences mSharedPreferences;

    public SharedPreferencesHelp(Context context)
    {
        mSharedPreferences = context.getSharedPreferences("sharedpreference",0);
    }


    public static SharedPreferencesHelp getInstance(Context context)
    {
        if(mInstance == null)
        {
            mInstance = new SharedPreferencesHelp(context);
        }
        return mInstance;
    }


    public void saveBoolean(String key, boolean value)
    {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.commit();
    }

    //默认false,DataManager.java中需要依赖该默认值
    public boolean getBooleanDefaultFalse(String key)
    {
        return mSharedPreferences.getBoolean(key,false);
    }


    //默认false,DataManager.java中需要依赖该默认值
    public boolean getBooleanDefaultTrue(String key)
    {
        return mSharedPreferences.getBoolean(key,true);
    }

}
