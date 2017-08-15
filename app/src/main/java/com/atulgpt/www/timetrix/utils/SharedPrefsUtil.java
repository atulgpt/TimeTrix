package com.atulgpt.www.timetrix.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by atulgupta on 10-05-2016 and 10 at 12:59 PM for TimeTrix .
 * Utility class for managing the shared preferences
 */
public class SharedPrefsUtil {
    private final Context mContext;

    public SharedPrefsUtil(Context context) {
        mContext = context;
    }


    public boolean isNotificationDisabled() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences (mContext);
        return sharedPreferences.getBoolean (GlobalData.IS_NOTIFICATION_DISABLED, true);
    }

    public void enableNotification() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences (mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit ();
        editor.putBoolean (GlobalData.IS_NOTIFICATION_DISABLED, false);
        editor.apply ();
    }

    public void disableNotification() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences (mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit ();
        editor.putBoolean (GlobalData.IS_NOTIFICATION_DISABLED,true);
        editor.apply ();
    }

    public boolean isPasswordEnabled() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences (mContext);
        return sharedPreferences.getBoolean (GlobalData.IS_PASSWORD_ENABLED, false);
    }

    public void enablePassword() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences (mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit ();
        editor.putBoolean (GlobalData.IS_PASSWORD_ENABLED, true);
        editor.apply ();
    }

    public void disablePassword() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences (mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit ();
        editor.putBoolean (GlobalData.IS_PASSWORD_ENABLED, false);
        editor.apply ();
    }

    public void setUserPassAuth(String userPassAuth) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences (mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit ();
        editor.putString (GlobalData.USER_PASS_AUTH, userPassAuth);
        editor.apply ();
    }

    public String getUserPassAuth() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences (mContext);
        return sharedPreferences.getString (GlobalData.USER_PASS_AUTH, "1234");
    }
/*
    public void setUserName(String userName) {
        //SharedPreferences sharedPreferences = mContext.getSharedPreferences (GlobalData.PREF_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences (mContext);
        SharedPreferences.Editor editor = sharedPreferences.edit ();
        editor.putString (GlobalData.USER_NAME, userName);
        editor.apply ();
    }
*/
    public String getUserName() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences (mContext);
        return sharedPreferences.getString (GlobalData.USER_NAME, "TimeTrix");
    }

    public String getUserEmail() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences (mContext);
        return sharedPreferences.getString (GlobalData.USER_EMAIL, "TimeTrix");
    }

    public void enableSyncInCloud() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences (GlobalData.PREF_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit ();
        editor.putBoolean (GlobalData.IS_SYNC_ENABLE, true);
        editor.apply ();
    }

    public void disableSyncInCloud() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences (GlobalData.PREF_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit ();
        editor.putBoolean (GlobalData.IS_PASSWORD_ENABLED, false);
        editor.apply ();
    }

    public boolean isSyncCloudEnabled(){
        SharedPreferences sharedPreferences = mContext.getSharedPreferences (GlobalData.PREF_SETTINGS, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean (GlobalData.IS_SYNC_ENABLE, false);
    }
}
