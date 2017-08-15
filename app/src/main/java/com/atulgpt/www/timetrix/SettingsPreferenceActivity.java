package com.atulgpt.www.timetrix;


import android.content.DialogInterface;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.atulgpt.www.timetrix.adapters.DatabaseAdapter;
import com.atulgpt.www.timetrix.utils.GlobalData;
import com.atulgpt.www.timetrix.utils.SharedPrefsUtil;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: SettingsActivity</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">SettingsActivity
 * API Guide</a> for more information on developing a SettingsActivity UI.
 */
public class SettingsPreferenceActivity extends AppCompatActivity {

    private static final boolean DEBUG = true;
    private static final String TAG = SettingsPreferenceActivity.class.getSimpleName ();
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener =
            new Preference.OnPreferenceChangeListener () {
                @Override
                public boolean onPreferenceChange(Preference preference, Object value) {
                    String stringValue = value.toString ();
                    //Log.d (TAG, "onPreferenceChange: Preference name = "+PreferenceManager.getDefaultSharedPreferencesName (preference.getContext ()));
                    if (DEBUG)
                        Log.d (TAG, "onPreferenceChange: preference = " + preference + " stringValue = " + stringValue);
                    if (preference instanceof ListPreference) {
                        // For list preferences, look up the correct display value in
                        // the preference's 'entries' list.
                        ListPreference listPreference = (ListPreference) preference;
                        int index = listPreference.findIndexOfValue (stringValue);

                        // Set the summary to reflect the new value.
                        preference.setSummary (
                                index >= 0
                                        ? listPreference.getEntries ()[index]
                                        : null);

                    } else if (preference instanceof RingtonePreference) {
                        // For ringtone preferences, look up the correct display value
                        // using RingtoneManager.
                        if (TextUtils.isEmpty (stringValue)) {
                            // Empty values correspond to 'silent' (no ringtone).
                            preference.setSummary (R.string.pref_ringtone_silent);

                        } else {
                            Ringtone ringtone = RingtoneManager.getRingtone (
                                    preference.getContext (), Uri.parse (stringValue));

                            if (ringtone == null) {
                                // Clear the summary if there was a lookup error.
                                preference.setSummary (null);
                            } else {
                                // Set the summary to reflect the new ringtone display
                                // name.
                                String name = ringtone.getTitle (preference.getContext ());
                                preference.setSummary (name);
                            }
                        }

                    } else if (preference instanceof EditTextPreference) {
                        preference.setSummary (stringValue);
                    }
                    return true;
                }
            };
    private int mSectionIndex = 0;

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener (sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        if (preference instanceof CheckBoxPreference || preference instanceof SwitchPreference) {
            sBindPreferenceSummaryToValueListener.onPreferenceChange (preference,
                    PreferenceManager
                            .getDefaultSharedPreferences (preference.getContext ())
                            .getBoolean (preference.getKey (), false));
        } else if (preference instanceof EditTextPreference) {
            sBindPreferenceSummaryToValueListener.onPreferenceChange (preference,
                    PreferenceManager
                            .getDefaultSharedPreferences (preference.getContext ())
                            .getString (preference.getKey (), ""));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setupActionBar ();
        if (getIntent ().hasExtra (GlobalData.SECTION_INDEX)) {
            mSectionIndex = getIntent ().getIntExtra (GlobalData.SECTION_INDEX, 0);
        }
        if (isValidFragment (GeneralPreferenceFragment.class.getName ()))
            getFragmentManager ().beginTransaction ().replace (R.id.settings_fragment_layout,
                    new GeneralPreferenceFragment ()).commit ();
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        //Setting custom action bar
        setContentView (R.layout.activity_settings_prefrence);
        Toolbar toolbar = (Toolbar) findViewById (R.id.toolbarSettingsPreference);
        setSupportActionBar (toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar ();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled (true);
        } else Toast.makeText (this, "in2", Toast.LENGTH_SHORT).show ();
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName ().equals (fragmentName)
                || GeneralPreferenceFragment.class.getName ().equals (fragmentName)
                || DataSyncPreferenceFragment.class.getName ().equals (fragmentName)
                || NotificationPreferenceFragment.class.getName ().equals (fragmentName);
    }

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     * <p>
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.</p>
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId () == android.R.id.home) {
            startActivity (new Intent (SettingsPreferenceActivity.this, StartupPage.class)
                    .putExtra (GlobalData.SECTION_INDEX, mSectionIndex));
            SettingsPreferenceActivity.this.finish ();
            return true;
        }
        return super.onOptionsItemSelected (item);
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        startActivity (new Intent (SettingsPreferenceActivity.this, StartupPage.class)
                .putExtra (GlobalData.SECTION_INDEX, mSectionIndex));
        SettingsPreferenceActivity.this.finish ();
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class GeneralPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate (savedInstanceState);
            addPreferencesFromResource (R.xml.pref_settings_option);
            setHasOptionsMenu (true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue (findPreference ("userNameEditText"));
            bindPreferenceSummaryToValue (findPreference ("emailEditText"));
            bindPreferenceSummaryToValue (findPreference ("notificationSwitch"));
            bindPreferenceSummaryToValue (findPreference ("cloudSwitch"));
            bindPreferenceSummaryToValue (findPreference ("passSwitch"));
            bindPreferenceSummaryToValue (findPreference ("resetData"));
            bindPreferenceSummaryToValue (findPreference ("termsAndCondition"));
            Preference preference = findPreference ("passSwitch");
            preference.setOnPreferenceClickListener (new Preference.OnPreferenceClickListener () {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    final SwitchPreference switchPreference = (SwitchPreference) preference;
                    final SharedPrefsUtil sharedPrefsUtil = new SharedPrefsUtil (getActivity ());
                    if (!switchPreference.isChecked ()) {
                        sharedPrefsUtil.disablePassword ();
                        return false;
                    }
                    switchPreference.setChecked (false);
                    if (!switchPreference.isChecked ()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder (getActivity ()).setTitle ("Set Password").setMessage ("Set Password to secure the app");
                        builder.setView (R.layout.dialog_password_entry);
                        builder.setPositiveButton (R.string.done_str, new DialogInterface.OnClickListener () {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        builder.setNegativeButton (R.string.cancel_str, new DialogInterface.OnClickListener () {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switchPreference.setChecked (false);
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
                                            switchPreference.setChecked (true);
                                            dialog.dismiss ();
                                        } else {
                                            confirmPasswordEditText.setError (getString (R.string.password_didnt_match_str));
                                            confirmPasswordEditText.requestFocus ();
                                        }
                                    }
                                } else {
                                    dialog.dismiss ();
                                }
                            }
                        });
                    }
                    return false;
                }
            });
            Preference preference1 = findPreference ("resetData");
            preference1.setOnPreferenceClickListener (new Preference.OnPreferenceClickListener () {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    AlertDialog.Builder builder = new AlertDialog.Builder (getActivity ()).setTitle (getString (R.string.delete_all_the_data_str)).setMessage (getString (R.string.it_ll_delete_all_the_data_str));
                    builder.setPositiveButton (getString (R.string.yes_str), new DialogInterface.OnClickListener () {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            DatabaseAdapter databaseAdapter = new DatabaseAdapter (getActivity (), null);
                            databaseAdapter.deleteDatabase ();
                            Toast.makeText (getActivity (), "Complete data Deleted!", Toast.LENGTH_SHORT).show ();
                            startActivity (new Intent (getActivity (), AddAnotherSection.class).putExtra (GlobalData.ADD_ANOTHER_SEC_HOME, false));
                            getActivity ().finish ();
                            dialog.dismiss ();
                        }
                    });
                    builder.setNegativeButton (getString (R.string.no_str), new DialogInterface.OnClickListener () {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss ();
                        }
                    }).show ();
                    return false;
                }
            });
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class NotificationPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate (savedInstanceState);
            addPreferencesFromResource (R.xml.pref_notification);
            setHasOptionsMenu (true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue (findPreference ("notifications_new_message_ringtone"));
        }
    }

    /**
     * This fragment shows data and sync preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    public static class DataSyncPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate (savedInstanceState);
            addPreferencesFromResource (R.xml.pref_data_sync);
            setHasOptionsMenu (true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue (findPreference ("sync_frequency"));
        }
    }
}
