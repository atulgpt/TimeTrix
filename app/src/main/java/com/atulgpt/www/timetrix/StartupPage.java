package com.atulgpt.www.timetrix;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.atulgpt.www.timetrix.adapters.DatabaseAdapter;
import com.atulgpt.www.timetrix.fragments.FragmentAllNotes;
import com.atulgpt.www.timetrix.fragments.FragmentStarredNotes;
import com.atulgpt.www.timetrix.fragments.FragmentTagNotes;
import com.atulgpt.www.timetrix.fragments.NavigationDrawerFragment;
import com.atulgpt.www.timetrix.utils.GlobalData;
import com.atulgpt.www.timetrix.utils.SharedPrefsUtil;

import java.util.ArrayList;


public class StartupPage extends AppCompatActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks,
        FragmentAllNotes.OnFragmentInteractionListener,
        FragmentStarredNotes.OnFragmentInteractionListener,
        FragmentTagNotes.OnFragmentInteractionListener,
        SearchView.OnQueryTextListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private static final String TAG = StartupPage.class.getSimpleName ();
    private static final Boolean DEBUG = true;
    private static final int NO_OF_TABS = 3;
    private static final String SECTION_SELECTED_POSITION = "state";

    private int mSectionIndex;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private final DatabaseAdapter mDatabaseAdapter = new DatabaseAdapter (this);
    private ViewPager mViewPager;
    private SparseArray<Fragment> mRegisteredFragments = new SparseArray<> ();
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private android.support.v7.app.ActionBar mActionBar;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_startup_page);
        Toast.makeText (this, "onCreate", Toast.LENGTH_SHORT).show ();
        mActionBar = this.getSupportActionBar ();
        Toolbar toolbar = (Toolbar) this.findViewById (R.id.toolbar);
        this.setSupportActionBar (toolbar);
//        searchBox.addTextChangedListener (new TextWatcher () {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                Toast.makeText (StartupPage.this, ""+charSequence.toString (), Toast.LENGTH_SHORT).show ();
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
        if (savedInstanceState != null) {
            mSectionIndex = savedInstanceState.getInt (SECTION_SELECTED_POSITION);
            mNavigationDrawerFragment.selectItem (mSectionIndex);
        }
        mViewPager = (ViewPager) this.findViewById (R.id.pager);
        mPagerAdapter = new PagerAdapter (getSupportFragmentManager ());
        mViewPager.setAdapter (mPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById (R.id.tabLayout);
        if (tabLayout != null) {
            tabLayout.setTabGravity (TabLayout.GRAVITY_FILL);
            tabLayout.addTab (tabLayout.newTab ().setText (R.string.all_notes_str), 0);
            tabLayout.addTab (tabLayout.newTab ().setText (R.string.starred_str), 1);
            tabLayout.addTab (tabLayout.newTab ().setText (R.string.expired_str), 2);
            tabLayout.setupWithViewPager (mViewPager);
            tabLayout.setOnTabSelectedListener (new TabLayout.OnTabSelectedListener () {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    mViewPager.setCurrentItem (tab.getPosition ());
                    populateListView ();
//                    Toast.makeText (StartupPage.this, "1", Toast.LENGTH_SHORT).show ();
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
//                    Toast.makeText (StartupPage.this, "2", Toast.LENGTH_SHORT).show ();

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
//                    Toast.makeText (StartupPage.this, "3", Toast.LENGTH_SHORT).show ();

                }
            });
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager ().findFragmentById (R.id.navigation_drawer);
        mTitle = getTitle ();
        mNavigationDrawerFragment.setUp (R.id.navigation_drawer,
                (DrawerLayout) findViewById (R.id.drawer_layout));
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause ();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (DEBUG)
            Log.d (TAG, "onNavigationDrawerItemSelected: count of sections = " +
                    mNavigationDrawerFragment.getSectionNo ());
        if (DEBUG)
            Log.d (TAG,
                    "onNavigationDrawerItemSelected in add another before adjusting for header view" + position);
        if (position <= 0)
            return;
        if (DEBUG)
            Log.d (TAG, "onNavigationDrawerItemSelected: val: " + getIntent ()
                    .getLongExtra (GlobalData.RESUME_STATE_FILE_ID, -1));
        if (getIntent ().getLongExtra (GlobalData.RESUME_STATE_FILE_ID, -1) >= 0) {
            mSectionIndex = getIntent ().getIntExtra (GlobalData.RESUME_STATE_FILE_ID, -1);
            getIntent ().putExtra (GlobalData.RESUME_STATE_FILE_ID, -1);
        } else {
            mSectionIndex = position - 1;
        }
        onSectionAttached (mSectionIndex);
        if (mDatabaseAdapter.countRows () == position - 1) {
            Intent intent = new Intent (StartupPage.this, AddAnotherSection.class);
            if (position == 1) {
                intent.putExtra (GlobalData.ADD_ANOTHER_SEC_HOME, false);
            }
            startActivity (intent);
            StartupPage.this.finish ();
//            Toast.makeText (StartupPage.this, "in ADD ANOTHER", Toast.LENGTH_SHORT).show ();
        } else {
            if (mViewPager != null && mPagerAdapter != null) {
//                Toast.makeText (StartupPage.this, "in drawer", Toast.LENGTH_SHORT).show ();
//              Toast.makeText(StartupPage.this, "in drawer   completed   "+ mSectionIndex, Toast.LENGTH_SHORT).show();
                populateListView ();
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Called everytime like onPause() but not in the case of pressing back button
        super.onSaveInstanceState (outState);
        outState.putLong (SECTION_SELECTED_POSITION, mSectionIndex);
    }

    public void onSectionAttached(long number) {
        if (number < 0)
            return;
        ArrayList<String> arrayList = mDatabaseAdapter.getAllData ();
        arrayList.add (getString (R.string.add_another_subject_str));
        mTitle = arrayList.get ((int) number);
        restoreActionBar ();
    }

    private void restoreActionBar() {
        mActionBar = this.getSupportActionBar ();
        if (mActionBar != null) {
            //mActionBar.setNavigationMode(android.support.v7.app.ActionBar.NAVIGATION_MODE_STANDARD);
            mActionBar.setDisplayShowTitleEnabled (true);
            mActionBar.setTitle (mTitle);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        SearchView searchView = (SearchView) searchItem.getActionView ();
        SharedPrefsUtil sharedPrefsUtil = new SharedPrefsUtil (StartupPage.this);
        if (!mNavigationDrawerFragment.isDrawerOpen ()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater ().inflate (R.menu.menu_startup_page, menu);
            restoreActionBar ();
            menu.findItem (R.id.action_mute).setChecked (!sharedPrefsUtil.isNotificationEnabled ());

            final MenuItem searchItem = menu.findItem (R.id.action_note_search);
            final SearchView searchView;

            searchView = (SearchView) MenuItemCompat.getActionView (searchItem);
            searchView.setIconifiedByDefault (true);
            searchView.setQueryHint ("Search");
            searchView.setOnCloseListener (new SearchView.OnCloseListener () {
                @Override
                public boolean onClose() {
                    if (!searchView.getQuery ().toString ().isEmpty ()) {
                        populateListView ();
                    } else {
                        searchView.setQuery ("", false);
                        searchView.clearFocus ();
                        return false;
                    }
                    return false;
                }
            });
            SearchView.OnQueryTextListener on = this;
            searchView.setOnQueryTextListener (on);
            View searchPlateView = searchView.
                    findViewById (android.support.v7.appcompat.R.id.search_plate);
            searchPlateView.setBackgroundColor (ContextCompat.
                    getColor (this, android.R.color.transparent));
            return true;
        } else {
            getMenuInflater ().inflate (R.menu.menu_startup_page, menu);
            menu.findItem (R.id.action_mute).setChecked (!sharedPrefsUtil.isNotificationEnabled ());
            //restoreActionBar();
        }
        //Log.d("atul","drawer is open");


        return super.onCreateOptionsMenu (menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId ();

        //noinspection SimplifiableIfStatement
        //if (id == R.id.action_note_search) {
            //Toast.makeText (StartupPage.this, "search btn clicked", Toast.LENGTH_SHORT).show ();
        //}
        if (id == R.id.action_detail_sub) {
            Intent intent = new Intent (StartupPage.this, SectionDetailsActivity.class);
            intent.putExtra (GlobalData.FILE_ID, mSectionIndex);
            //StartupPage.this.finish ();
            startActivity (intent);
        }
        if (id == R.id.action_delete_section) {
            android.support.v7.app.AlertDialog.Builder builder =
                    new android.support.v7.app.AlertDialog.Builder (this);
            builder.setTitle (getString (R.string.are_you_sure_str));
            builder.setMessage (getString (R.string.all_your_inform_deleted_str));
            builder.setPositiveButton (getString (R.string.ok_str), new DialogInterface.OnClickListener () {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    if (DEBUG)
                        Log.d (TAG, "onClick: menu delete sub rowID =" + mSectionIndex);
                    mDatabaseAdapter.deleteSubject (mSectionIndex + 1);
                    mNavigationDrawerFragment.populateListView ();
                    if (mNavigationDrawerFragment.getSectionNo () == 0) {
                        Intent intent = new Intent (StartupPage.this, AddAnotherSection.class);
                        intent.putExtra (GlobalData.ADD_ANOTHER_SEC_HOME, false);
                        startActivity (intent);
                        StartupPage.this.finish ();
                    } else if (mNavigationDrawerFragment.getSectionNo () > 0) {
                        mSectionIndex = mSectionIndex - 1;
                        onSectionAttached (mSectionIndex);
                        populateListView ();
                    }
                }
            });
            builder.setNegativeButton (R.string.cancel_str, new DialogInterface.OnClickListener () {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            builder.show ();
        }
        if (id == R.id.action_settings) {
            Intent intent = new Intent (this, SettingsActivity.class);
            startActivity (intent);
            StartupPage.this.finish ();
            return true;
        }
        if (id == R.id.action_mute) {
            SharedPrefsUtil sharedPrefsUtil = new SharedPrefsUtil (StartupPage.this);
            if (item.isChecked ()) {
                item.setChecked (false);
                sharedPrefsUtil.enableNotification ();
            } else {
                item.setChecked (true);
                sharedPrefsUtil.disableNotification ();
            }
        }
        if (id == R.id.action_feedback) {
            LayoutInflater inflater = (LayoutInflater) this.
                    getSystemService (Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate (R.layout.dialog_feedback, null);
            android.support.v7.app.AlertDialog.Builder builder =
                    new android.support.v7.app.AlertDialog.Builder (this);
            builder.setTitle (R.string.write_a_feedback_str);
            builder.setView (view);
            builder.setPositiveButton (R.string.submit_str, new DialogInterface.OnClickListener () {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Dialog dialog1 = (Dialog) dialog;
                    Spinner spinner = (Spinner) dialog1.findViewById (R.id.feedback_spinner);
                    EditText editText = (EditText) dialog1.findViewById (R.id.feedback_editText);
                    String feedbackTitle = spinner.getSelectedItem ().toString ();
                    String feedback = editText.getText ().toString ();
                    if (feedback.trim ().isEmpty ()) {
                        return;
                    }
                    Intent email = new Intent (Intent.ACTION_SEND);
                    email.putExtra (Intent.EXTRA_EMAIL, new String[]{GlobalData.APP_DEV_EMAIL});
                    email.putExtra (Intent.EXTRA_SUBJECT, feedbackTitle);
                    email.putExtra (Intent.EXTRA_TEXT, feedback);
                    email.setType ("message/rfc822");
                    startActivity (Intent.createChooser (email, "Choose an Email client :"));
                }
            });
            builder.show ();
        }
        return super.onOptionsItemSelected (item);
    }

    @Override
    public void onFragmentInteraction(String data1, String data2) {
        populateListView ();
    }

    private void populateListView() {
        int position = mViewPager.getCurrentItem ();
        if (position == 0) {
            FragmentAllNotes fragmentAllNotes = (FragmentAllNotes) mRegisteredFragments.get (position);
            if (fragmentAllNotes != null) {
                fragmentAllNotes.setArgParam1 (String.valueOf (mSectionIndex));
                fragmentAllNotes.populateListView ();
            }
//            Toast.makeText(StartupPage.this, "in populate"+ position, Toast.LENGTH_SHORT).show();
        }
        if (position == 1) {
            FragmentStarredNotes fragmentStarredNotes = (FragmentStarredNotes) mRegisteredFragments.get (position);
            if (fragmentStarredNotes != null) {
                fragmentStarredNotes.setArgParam1 (String.valueOf (mSectionIndex));
                fragmentStarredNotes.populateListView ();
            }
        }
        if (position == 2) {
            FragmentTagNotes fragmentTagNotes = (FragmentTagNotes) mRegisteredFragments.get (position);
            if (fragmentTagNotes != null) {
                fragmentTagNotes.setArgParam1 (String.valueOf (mSectionIndex));
                fragmentTagNotes.populateListView ();
            }
        }

        if (DEBUG) Log.d (TAG, "populateListView  tab position :" + position);
    }

    private void populateListViewWithSearchQuery(String searchQuery) {
        int position = mViewPager.getCurrentItem ();
        if (position == 0) {
            FragmentAllNotes fragmentAllNotes = (FragmentAllNotes) mRegisteredFragments.get (position);
            if (fragmentAllNotes != null) {
                fragmentAllNotes.populateListView (searchQuery);
            }
//            Toast.makeText(StartupPage.this, "in populate"+ position, Toast.LENGTH_SHORT).show();
        }
        if (position == 1) {
            FragmentStarredNotes fragmentStarredNotes = (FragmentStarredNotes) mRegisteredFragments.get (position);
            if (fragmentStarredNotes != null) {
                fragmentStarredNotes.populateListView (searchQuery);
            }
        }
        if (position == 2) {
            FragmentTagNotes fragmentTagNotes = (FragmentTagNotes) mRegisteredFragments.get (position);
            if (fragmentTagNotes != null) {
                fragmentTagNotes.populateListView (searchQuery);
            }
        }
    }

    /**
     * Called when the user submits the query. This could be due to a key press on the
     * keyboard or due to pressing a submit button.
     * The listener can override the standard behavior by returning true
     * to indicate that it has handled the submit request. Otherwise return false to
     * let the SearchView handle the submission by launching any associated intent.
     *
     * @param query the query text that is to be submitted
     * @return true if the query has been handled by the listener, false to let the
     * SearchView perform the default action.
     */
    @Override
    public boolean onQueryTextSubmit(String query) {
        return true;
    }

    /**
     * Called when the query text is changed by the user.
     *
     * @param newText the new content of the query text field.
     * @return false if the SearchView should perform the default action of showing any
     * suggestions if available, true if the action was handled by the listener.
     */
    @Override
    public boolean onQueryTextChange(String newText) {
        populateListViewWithSearchQuery (newText);
        return true;
    }


    private class PagerAdapter extends FragmentPagerAdapter {
        final String[] tabTitles;

        PagerAdapter(android.support.v4.app.FragmentManager fm) {
            super (fm);
            tabTitles = getResources ().getStringArray (R.array.tabs_name);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem (container, position);
            mRegisteredFragments.put (position, fragment);
            //Toast.makeText(StartupPage.this, "instantiate pos = "+position, Toast.LENGTH_SHORT).show();
            return fragment;
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem (container, position, object);
            //Toast.makeText(StartupPage.this, "setPrim pos = "+position+" container "+container+" object= "+object, Toast.LENGTH_SHORT).show();
            //Fragment fragment = (Fragment)object;
        }


        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            // Toast.makeText(StartupPage.this, "position in getItem = "+position, Toast.LENGTH_SHORT).show();
            if (position == 0) {
                return FragmentAllNotes.newInstance (String.valueOf (mSectionIndex), String.valueOf (position));
            }
            if (position == 1) {
                return FragmentStarredNotes.newInstance (String.valueOf (mSectionIndex), String.valueOf (position));
            }
            if (position == 2) {
                return FragmentTagNotes.newInstance (String.valueOf (mSectionIndex), String.valueOf (position));
            }
            return null;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            mRegisteredFragments.remove (position);
            super.destroyItem (container, position, object);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public int getCount() {
            return NO_OF_TABS;
        }
    }
//    @Override
//     public void onAttach(Activity activity) {
//        super.onAttach (activity);
//        ((Navigation) activity).onSectionAttached (
//                getArguments ().getInt (ARG_SECTION_NUMBER));
//    }

}
