package com.example.mytestwechat;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import static android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "hongbao";
    private RadioButton radio_auto,radio_hand;
    private CheckBox check_autoback,check_autoself;

    public static final String RADIG_AUTO = "radio_auto";
    public static final String RADIG_HAND = "radio_hand";
    public static final String CHECK_AUTOBACK = "check_autoback";
    public static final String CHECK_AUTOSELF = "check_autoself";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        radio_auto = (RadioButton)this.findViewById(R.id.radio_auto);
        radio_hand = (RadioButton)this.findViewById(R.id.radio_hand);
        check_autoback = (CheckBox)this.findViewById(R.id.check_autoback);
        check_autoself = (CheckBox)this.findViewById(R.id.check_autoself);

        radio_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferencesHelp.getInstance(MainActivity.this).saveBoolean(RADIG_AUTO,true);
                SharedPreferencesHelp.getInstance(MainActivity.this).saveBoolean(RADIG_HAND,false);
            }
        });

        radio_hand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferencesHelp.getInstance(MainActivity.this).saveBoolean(RADIG_AUTO,false);
                SharedPreferencesHelp.getInstance(MainActivity.this).saveBoolean(RADIG_HAND,true);
            }
        });

        check_autoback.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferencesHelp.getInstance(MainActivity.this).saveBoolean(CHECK_AUTOBACK,b);
            }
        });

        check_autoself.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SharedPreferencesHelp.getInstance(MainActivity.this).saveBoolean(CHECK_AUTOSELF,b);
            }
        });

        boolean isAuto = SharedPreferencesHelp.getInstance(MainActivity.this).getBooleanDefaultFalse(RADIG_AUTO);
        boolean isHand= SharedPreferencesHelp.getInstance(MainActivity.this).getBooleanDefaultFalse(RADIG_HAND);
        boolean isAutoBack= SharedPreferencesHelp.getInstance(MainActivity.this).getBooleanDefaultTrue(CHECK_AUTOBACK);
        boolean isAutoSelf= SharedPreferencesHelp.getInstance(MainActivity.this).getBooleanDefaultFalse(CHECK_AUTOSELF);

        if(!isAuto && !isHand)
        {
            SharedPreferencesHelp.getInstance(MainActivity.this).saveBoolean(RADIG_AUTO,true);
            SharedPreferencesHelp.getInstance(MainActivity.this).saveBoolean(RADIG_HAND,false);
            radio_auto.setChecked(true);
        }else if(isAuto)
        {
            radio_auto.setChecked(true);
        }else if(isHand)
        {
            radio_hand.setChecked(true);
        }

        check_autoback.setChecked(isAutoBack);
        check_autoself.setChecked(isAutoSelf);

        if(isAccessibilitySettingsOn(this))
        {
            Toast.makeText(this,"已经开启无障碍模式",Toast.LENGTH_SHORT).show();
        }else
        {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setMessage("无障碍模式未开启，点击前往->找到 微信自动抢红包->点击进入并打开");


            alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });

            alertDialog.setPositiveButton("前往", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(ACTION_ACCESSIBILITY_SETTINGS));
                }
            });
            alertDialog.show();
        }
    }

    public void goSet(View v)
    {
        startActivity(new Intent(ACTION_ACCESSIBILITY_SETTINGS));
        boolean isAuto = SharedPreferencesHelp.getInstance(MainActivity.this).getBooleanDefaultFalse(RADIG_AUTO);
        boolean isHand= SharedPreferencesHelp.getInstance(MainActivity.this).getBooleanDefaultFalse(RADIG_HAND);
        boolean isAutoBack= SharedPreferencesHelp.getInstance(MainActivity.this).getBooleanDefaultTrue(CHECK_AUTOBACK);
        //boolean isAutoSelf= SharedPreferencesHelp.getInstance(MainActivity.this).getBooleanDefaultFalse(CHECK_AUTOSELF);
        //Toast.makeText(this,"isAuto:"+isAuto+" isHand:"+isHand+" isAutoBack:"+isAutoBack + " isAutoSelf:"+isAutoSelf,Toast.LENGTH_SHORT).show();
        Toast.makeText(this,"isAuto:"+isAuto+" isHand:"+isHand+" isAutoBack:"+isAutoBack,Toast.LENGTH_SHORT).show();
    }

    public void goWechat(View v)
    {
        Intent intent = new Intent();
        ComponentName cmp=new ComponentName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(cmp);
        startActivity(intent);
    }


    // To check if service is enabled
    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = getPackageName() + "/" + RedPacketService.class.getCanonicalName();
        try {
            accessibilityEnabled = Settings.Secure.getInt(
                    mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: "
                    + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(
                    mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }

        return false;
    }
}
