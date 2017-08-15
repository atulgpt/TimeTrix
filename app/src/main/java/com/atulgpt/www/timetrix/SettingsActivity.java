package com.atulgpt.www.timetrix;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.atulgpt.www.timetrix.adapters.DatabaseAdapter;
import com.atulgpt.www.timetrix.utils.GlobalData;
import com.atulgpt.www.timetrix.utils.SharedPrefsUtil;
//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.FacebookSdk;
//import com.facebook.appevents.AppEventsLogger;
//import com.facebook.login.LoginResult;
//import com.facebook.login.widget.LoginButton;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = SettingsActivity.class.getSimpleName ();
    private SwitchCompat mPasswordSwitch;
    //private CallbackManager mCallbackManager;
    private int mSectionIndex = 0;

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
        if (getIntent ().hasExtra (GlobalData.SECTION_INDEX)) {
            mSectionIndex = getIntent ().getIntExtra (GlobalData.SECTION_INDEX, 0);
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
        SharedPrefsUtil sharedPrefsUtil = new SharedPrefsUtil (SettingsActivity.this);
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

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        startActivity (new Intent (SettingsActivity.this, StartupPage.class).
                putExtra (GlobalData.SECTION_INDEX, mSectionIndex));
        SettingsActivity.this.finish ();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId ();

        if (id == android.R.id.home) {
            startActivity (new Intent (SettingsActivity.this, StartupPage.class).
                    putExtra (GlobalData.SECTION_INDEX, mSectionIndex));
            SettingsActivity.this.finish ();
            return true;
        }
        return super.onOptionsItemSelected (item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId () == R.id.button_save_pdf) {
            Toast.makeText (SettingsActivity.this, "Oops... This feature is still being baked!!", Toast.LENGTH_SHORT).show ();
        }
        if (v.getId () == R.id.button_delete_data) {
            AlertDialog.Builder builder = new AlertDialog.Builder (SettingsActivity.this).setTitle (getString (R.string.delete_all_the_data_str)).setMessage (getString (R.string.it_ll_delete_all_the_data_str));
            builder.setPositiveButton (getString (R.string.yes_str), new DialogInterface.OnClickListener () {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    DatabaseAdapter databaseAdapter = new DatabaseAdapter (SettingsActivity.this, null);
                    databaseAdapter.deleteDatabase ();
                    Toast.makeText (SettingsActivity.this, "Complete data Deleted!", Toast.LENGTH_SHORT).show ();
                    startActivity (new Intent (SettingsActivity.this, AddAnotherSection.class).putExtra (GlobalData.ADD_ANOTHER_SEC_HOME, false));
                    SettingsActivity.this.finish ();
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
        final SharedPrefsUtil sharedPrefsUtil = new SharedPrefsUtil (SettingsActivity.this);
        if (buttonView.getId () == R.id.switch_password) {
            if (isChecked) {
                AlertDialog.Builder builder = new AlertDialog.Builder (SettingsActivity.this).setTitle ("Set Password").setMessage ("Set Password to secure the app");
                builder.setView (R.layout.dialog_password_entry);
                builder.setPositiveButton (R.string.done_str, new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setNegativeButton (R.string.cancel_str, new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mPasswordSwitch.setChecked (false);
                        dialog.dismiss ();
                    }
                });
                final AlertDialog dialog = builder.create ();
                dialog.show ();
                dialog.getButton (AlertDialog.BUTTON_POSITIVE).setOnClickListener (new View.OnClickListener () {
                    @Override
                    public void onClick(View v) {
                        EditText passwordEditText = (EditText) dialog.findViewById (R.id.editText_password);
                        EditText confirmPasswordEditText = (EditText) dialog.findViewById (R.id.editText_confirm_password);
                        if (passwordEditText != null && confirmPasswordEditText != null) {
                            String password = passwordEditText.getText ().toString ();
                            String confirmPassword = confirmPasswordEditText.getText ().toString ();
                            View view = null;
                            if (password.trim ().isEmpty ()) {
                                view = passwordEditText;
                            } else if (confirmPassword.trim ().isEmpty ()) {
                                view = confirmPasswordEditText;
                            }
                            if (view != null) {
                                ((EditText) view).setError (getString (R.string.empty_field_not_allowed_str));
                                view.requestFocus ();
                            } else {
                                if (password.equals (confirmPassword)) {
                                    sharedPrefsUtil.setUserPassAuth (confirmPassword);
                                    sharedPrefsUtil.enablePassword ();
                                    dialog.dismiss ();
                                } else {
                                    confirmPasswordEditText.setError (getString (R.string.password_didnt_match_str));
                                    confirmPasswordEditText.requestFocus ();
                                }
                            }
                        } else {
                            startActivity (new Intent (SettingsActivity.this, StartupPage.class).putExtra (GlobalData.SECTION_INDEX, mSectionIndex));
                            SettingsActivity.this.finish ();
                            Log.d (TAG, "onClick: error in password dialog, fields are null");
                        }
                    }
                });

            } else {
                sharedPrefsUtil.disablePassword ();
            }
        }
        if (buttonView.getId () == R.id.switch_sync) {
            if (isChecked) {
                sharedPrefsUtil.enableSyncInCloud ();
            } else {
                sharedPrefsUtil.disableSyncInCloud ();
            }
        }
    }
}
