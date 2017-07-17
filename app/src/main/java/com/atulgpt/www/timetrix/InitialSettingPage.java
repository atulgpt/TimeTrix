package com.atulgpt.www.timetrix;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.widget.EditText;
import android.widget.Toast;

import com.atulgpt.www.timetrix.utils.SharedPrefsUtil;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class InitialSettingPage extends Activity {

   // private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        //FacebookSdk.sdkInitialize (getApplicationContext ());
        //AppEventsLogger.activateApp (this);
        setContentView (R.layout.activity_inital_setting_page);
//        Toast.makeText (InitialSettingPage.this, "1. pref = " + AccessToken.getCurrentAccessToken () + " 2. = " + Profile.getCurrentProfile (), Toast.LENGTH_LONG).show ();
        try {
            PackageInfo info = getPackageManager ().getPackageInfo (
                    "com.atulgpt.www.timetrix",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance ("SHA");
                md.update (signature.toByteArray ());
                Log.d ("KeyHash:", Base64.encodeToString (md.digest (), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText (InitialSettingPage.this, "err1", Toast.LENGTH_LONG).show ();
        } catch (NoSuchAlgorithmException e) {
            Toast.makeText (InitialSettingPage.this, "err2", Toast.LENGTH_LONG).show ();
        }
        //LoginButton loginButton = (LoginButton) findViewById (R.id.login_button);
        //loginButton.setReadPermissions ("email");


        //mCallbackManager = CallbackManager.Factory.create ();
//        loginButton.registerCallback (mCallbackManager, new FacebookCallback<LoginResult> () {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                // App code
////                Toast.makeText (InitialSettingPage.this, "1. " + loginResult, Toast.LENGTH_SHORT).show ();
//            }
//
//            @Override
//            public void onCancel() {
//                // App code
////                Toast.makeText (InitialSettingPage.this, "2. ", Toast.LENGTH_SHORT).show ();
//            }
//
//            @Override
//            public void onError(FacebookException exception) {
//                // App code
////                Toast.makeText (InitialSettingPage.this, "3. " + exception, Toast.LENGTH_SHORT).show ();
//            }
//        });

//        Toast.makeText(this,"notification = "+mNotificationEnabled,Toast.LENGTH_LONG).show();
        SharedPrefsUtil sharedPrefsUtil = new SharedPrefsUtil (InitialSettingPage.this);
        if (!sharedPrefsUtil.isPasswordEnabled ()) {
            Intent intent = new Intent (this, StartupPage.class);
            startActivity (intent);
            InitialSettingPage.this.finish ();
        } else {
            showDialogForPasswordVerification();
        }
    }

    private void showDialogForPasswordVerification() {
        new AlertDialog.Builder (InitialSettingPage.this).setTitle ("Enter Password")
                .setCancelable (false)
                .setView (R.layout.dialog_password_check)
                .setPositiveButton (R.string.done_str, new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPrefsUtil sharedPrefsUtil =
                                new SharedPrefsUtil (InitialSettingPage.this);
                        Dialog d = (Dialog) dialog;
                        EditText passwordEditText = (EditText) d.
                                findViewById (R.id.editText_password_verification);
                        String password = passwordEditText.getText ().toString ();
                        if (password.equals (sharedPrefsUtil.getUserPassAuth ())) {
                            Intent intent = new Intent (InitialSettingPage.this,
                                    StartupPage.class);
                            startActivity (intent);
                            dialog.dismiss ();
                            InitialSettingPage.this.finish ();
                        } else {
                            //passwordEditText.setError ("Wrong Password");
                            //passwordEditText.requestFocus ();
                            Toast.makeText (InitialSettingPage.this, "Wrong Password",
                                    Toast.LENGTH_SHORT).show ();
                            dialog.dismiss ();
                            InitialSettingPage.this.showWrongPasswordErrorDialog();
                        }
                    }
                })
                .setNegativeButton (R.string.cancel_str, new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss ();
                        InitialSettingPage.this.finish ();
                    }
                })
                .show ();
    }

    private void showWrongPasswordErrorDialog() {
        new AlertDialog.Builder (InitialSettingPage.this).setTitle ("Error!")
                .setMessage ("Wrong password entered!")
                .setCancelable (false)
                .setPositiveButton (R.string.ok_str, new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        InitialSettingPage.this.finish ();
                    }
                }).show ();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.menu_inital_setting_page, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        //mCallbackManager.onActivityResult (requestCode, resultCode, data);
    }
}
