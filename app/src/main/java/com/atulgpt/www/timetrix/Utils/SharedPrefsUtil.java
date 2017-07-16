package com.atulgpt.www.timetrix.Utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by atulgupta on 10-05-2016 and 10 at 12:59 PM for TimeTrix .
 * Utility class for managing the shared preferences
 */
public class SharedPrefsUtil {
    private final Context mContext;

    public SharedPrefsUtil(Context context) {
        mContext = context;
    }


    public boolean isNotificationEnabled() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences (GlobalData.PREF_SETTINGS, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean (GlobalData.IS_NOTIFICATION_ENABLED, true);
    }

    public void enableNotification() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences (GlobalData.PREF_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit ();
        editor.putBoolean (GlobalData.IS_NOTIFICATION_ENABLED, true);
        editor.apply ();
    }

    public void disableNotification() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences (GlobalData.PREF_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit ();
        editor.putBoolean (GlobalData.IS_NOTIFICATION_ENABLED, false);
        editor.apply ();
    }

    public boolean isPasswordEnabled() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences (GlobalData.PREF_SETTINGS, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean (GlobalData.IS_PASSWORD_ENABLED, false);
    }

    public void enablePassword() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences (GlobalData.PREF_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit ();
        editor.putBoolean (GlobalData.IS_PASSWORD_ENABLED, true);
        editor.apply ();
    }

    public void disablePassword() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences (GlobalData.PREF_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit ();
        editor.putBoolean (GlobalData.IS_PASSWORD_ENABLED, false);
        editor.apply ();
    }

    public void setUserNameAuth(String userNameAuth) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences (GlobalData.PREF_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit ();
        editor.putString (GlobalData.USER_NAME_AUTH, userNameAuth);
        editor.apply ();
    }

    public void setUserPassAuth(String userPassAuth) {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences (GlobalData.PREF_SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit ();
        editor.putString (GlobalData.USER_PASS_AUTH, userPassAuth);
        editor.apply ();
    }

    public String getUserNameAuth() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences (GlobalData.PREF_SETTINGS, Context.MODE_PRIVATE);
        return sharedPreferences.getString (GlobalData.USER_NAME_AUTH, "");
    }

    public String getUserPassAuth() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences (GlobalData.PREF_SETTINGS, Context.MODE_PRIVATE);
        return sharedPreferences.getString (GlobalData.USER_PASS_AUTH, "");
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
