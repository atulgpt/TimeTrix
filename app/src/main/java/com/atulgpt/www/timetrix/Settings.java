package com.atulgpt.www.timetrix;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.atulgpt.www.timetrix.Adapters.DatabaseAdapter;
import com.atulgpt.www.timetrix.Utils.SharedPrefsUtil;
import com.atulgpt.www.timetrix.Utils.Util;
//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.FacebookSdk;
//import com.facebook.appevents.AppEventsLogger;
//import com.facebook.login.LoginResult;
//import com.facebook.login.widget.LoginButton;

public class Settings extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private SwitchCompat mPasswordSwitch;
    //private CallbackManager mCallbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
//        FacebookSdk.sdkInitialize (getApplicationContext ());
//        AppEventsLogger.activateApp (this);
        setContentView (R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById (R.id.toolbarSettings);
        setSupportActionBar (toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar ();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled (true);
        }

//        LoginButton loginButton = (LoginButton) findViewById (R.id.login_button_1);
//        loginButton.setReadPermissions ("email");
//
//
//        mCallbackManager = CallbackManager.Factory.create ();
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
        SharedPrefsUtil sharedPrefsUtil = new SharedPrefsUtil (Settings.this);
        mPasswordSwitch = (SwitchCompat) findViewById (R.id.switch_password);
        mPasswordSwitch.setChecked (sharedPrefsUtil.isPasswordEnabled ());
        SwitchCompat syncSwitch = (SwitchCompat) findViewById (R.id.switch_sync);
        syncSwitch.setChecked (sharedPrefsUtil.isSyncCloudEnabled ());
        mPasswordSwitch.setOnCheckedChangeListener (this);
        syncSwitch.setOnCheckedChangeListener (this);
        Button deleteAllData = (Button) findViewById (R.id.button_delete_data);
        Button saveAsPDF = (Button) findViewById (R.id.button_save_pdf);
        deleteAllData.setOnClickListener (this);
        saveAsPDF.setOnClickListener (this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater ().inflate (R.menu.menu_settings, menu);
        return super.onCreateOptionsMenu (menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId ();

        if (id == android.R.id.home) {
            startActivity (new Intent (Settings.this, StartupPage.class));
            Settings.this.finish ();
            return true;
        }
        return super.onOptionsItemSelected (item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId () == R.id.button_save_pdf) {
            Toast.makeText (Settings.this, "Oops... This feature is still being baked!!", Toast.LENGTH_SHORT).show ();
        }
        if (v.getId () == R.id.button_delete_data) {
            AlertDialog.Builder builder = new AlertDialog.Builder (Settings.this).setTitle (getString (R.string.delete_all_the_data_str)).setMessage (getString (R.string.it_ll_delete_all_the_data_str));
            builder.setPositiveButton (getString (R.string.yes_str), new DialogInterface.OnClickListener () {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DatabaseAdapter databaseAdapter = new DatabaseAdapter (Settings.this);
                    databaseAdapter.deleteDatabase ();
                    Toast.makeText (Settings.this, "Table Deleted!", Toast.LENGTH_SHORT).show ();
                    startActivity (new Intent (Settings.this, AddAnotherSubject.class).putExtra (Util.ADD_ANOTHER_SUB_HOME,false));
                    Settings.this.finish ();
                    dialog.dismiss ();
                }
            });
            builder.setNegativeButton (getString (R.string.no_str), new DialogInterface.OnClickListener () {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss ();
                }
            });
            builder.show ();
        }

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        final SharedPrefsUtil sharedPrefsUtil = new SharedPrefsUtil (Settings.this);
        if (buttonView.getId () == R.id.switch_password) {
            if (isChecked) {
                AlertDialog.Builder builder = new AlertDialog.Builder (Settings.this).setTitle ("Set Password").setMessage ("Set Password to secure the app");
                builder.setView (R.layout.dialog_password_entry);
                builder.setPositiveButton (R.string.done_str, new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Dialog d = (Dialog) dialog;
                        EditText userNameEditText = (EditText) d.findViewById (R.id.editText_userName);
                        EditText passwordEditText = (EditText) d.findViewById (R.id.editText_password);
                        String userName = userNameEditText.getText ().toString ();
                        String password = passwordEditText.getText ().toString ();
                        if(userName.trim ().isEmpty () || password.trim ().isEmpty ()){
                            dialog.dismiss ();
                            Toast.makeText (Settings.this, R.string.empty_fields_nt_allowed_str, Toast.LENGTH_LONG).show ();
                            mPasswordSwitch.setChecked (sharedPrefsUtil.isPasswordEnabled ());
                            return;
                        }
                        sharedPrefsUtil.setUserNameAuth (userName);
                        sharedPrefsUtil.setUserPassAuth (password);
                        sharedPrefsUtil.enablePassword ();
                        dialog.dismiss ();
                    }
                });
                builder.setNegativeButton (R.string.cancel_str, new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss ();
                    }
                });
                builder.show ();
            } else {
                sharedPrefsUtil.disablePassword ();
            }
        }
        if (buttonView.getId () == R.id.switch_sync) {
            if(isChecked){
                sharedPrefsUtil.enableSyncInCloud ();
            } else {
                sharedPrefsUtil.disableSyncInCloud ();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        //mCallbackManager.onActivityResult (requestCode, resultCode, data);
    }
}
