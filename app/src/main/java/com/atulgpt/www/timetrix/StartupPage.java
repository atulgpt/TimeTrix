package com.atulgpt.www.timetrix;

import android.app.Dialog;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.atulgpt.www.timetrix.adapters.DatabaseAdapter;
import com.atulgpt.www.timetrix.adapters.RecyclerViewAdapter;
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
        RecyclerViewAdapter.OnListAdapterInteractionListener,
        SearchView.OnQueryTextListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private static final String TAG = StartupPage.class.getSimpleName ();
    private static final Boolean DEBUG = true;
    private static final int NO_OF_TABS = 3;
    private static final int NOTE_DETAIL_ACTIVITY_REQUEST = 1;
    private static final int SECTION_UPDATE_ACTIVITY_REQUEST = 2;
    private final DatabaseAdapter mDatabaseAdapter = new DatabaseAdapter (this, null);
    private int mSectionIndex;
    private NavigationDrawerFragment mNavigationDrawerFragment;
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
            tabLayout.addOnTabSelectedListener (new TabLayout.OnTabSelectedListener () {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    mViewPager.setCurrentItem (tab.getPosition ());
                    populateListView ();
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager ().findFragmentById (R.id.navigation_drawer);
        mTitle = getTitle ();
        mNavigationDrawerFragment.setUp (R.id.navigation_drawer,
                (DrawerLayout) findViewById (R.id.drawer_layout));
        if (savedInstanceState != null) {
            mSectionIndex = savedInstanceState.getInt (GlobalData.SECTION_INDEX);
            int itemIndexNavDrawer = mSectionIndex + 1;  // accounting for header view
            mNavigationDrawerFragment.selectItemWithCallback (itemIndexNavDrawer);
            savedInstanceState = null;
        }
        if (getIntent ().hasExtra (GlobalData.SECTION_INDEX)) {
            mSectionIndex = getIntent ().getIntExtra (GlobalData.SECTION_INDEX, 1);
            int itemIndexNavDrawer = mSectionIndex + 1;
            mNavigationDrawerFragment.selectItemWithCallback (itemIndexNavDrawer);
            getIntent ().removeExtra (GlobalData.SECTION_INDEX);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //Called everytime like onPause() but not in the case of pressing back button
        super.onSaveInstanceState (outState);
        outState.putInt (GlobalData.SECTION_INDEX, mSectionIndex);
    }

    private void populateListView() {
        populateListViewWithSearchQuery ("");
    }

    private void populateListViewWithSearchQuery(String searchQuery) {
        int position = mViewPager.getCurrentItem ();
        if (position == 0) {
            FragmentAllNotes fragmentAllNotes = (FragmentAllNotes) mRegisteredFragments.get (position);
            if (fragmentAllNotes != null) {
                fragmentAllNotes.setSectionIndex (String.valueOf (mSectionIndex));
                fragmentAllNotes.populateListView (searchQuery);
            }
        }
        if (position == 1) {
            FragmentStarredNotes fragmentStarredNotes = (FragmentStarredNotes) mRegisteredFragments.get (position);
            if (fragmentStarredNotes != null) {
                fragmentStarredNotes.setSectionIndex (String.valueOf (mSectionIndex));
                fragmentStarredNotes.populateListViewInBackground (searchQuery);
            }
        }
        if (position == 2) {
            FragmentTagNotes fragmentTagNotes = (FragmentTagNotes) mRegisteredFragments.get (position);
            if (fragmentTagNotes != null) {
                fragmentTagNotes.setSectionIndex (String.valueOf (mSectionIndex));
                fragmentTagNotes.populateListView (searchQuery);
            }
        }
        if (DEBUG)
            Log.d (TAG, "populateListViewInBackground  tab position :" + position + " with query = " + searchQuery);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        if (DEBUG)
            Log.d (TAG, "onNavigationDrawerItemSelected: count of sections = " +
                    mNavigationDrawerFragment.getSectionNo ());
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
        updateTitleOnSectionAttached (mSectionIndex);
        if (mDatabaseAdapter.countRows () == position - 1) {
            Intent intent = new Intent (StartupPage.this, AddAnotherSection.class);
            if (position == 1) {
                intent.putExtra (GlobalData.ADD_ANOTHER_SEC_HOME, false);
            }
            startActivity (intent);
            StartupPage.this.finish ();
        } else {
            if (mViewPager != null && mPagerAdapter != null) {
                populateListView ();
            }
        }
    }

    public void updateTitleOnSectionAttached(int number) {
        if (number < 0)
            return;
        ArrayList<String> arrayList = mDatabaseAdapter.getAllSectionNames ();
        arrayList.add (getString (R.string.add_another_section_str));
        mTitle = arrayList.get (number);
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
            menu.findItem (R.id.action_mute).setChecked (sharedPrefsUtil.isNotificationDisabled ());

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
            menu.findItem (R.id.action_mute).setChecked (!sharedPrefsUtil.isNotificationDisabled ());
            //restoreActionBar();
        }
        Log.d (TAG, "onCreateOptionsMenu: menu = " + menu);


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
            intent.putExtra (GlobalData.SECTION_INDEX, mSectionIndex);
            //StartupPage.this.finish ();
            startActivityForResult (intent, GlobalData.REQUEST_FOR_SECTION_UPDATE);
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
                    if (mNavigationDrawerFragment.getSectionNo () > 0 && mSectionIndex != 0) {
                        mSectionIndex = mSectionIndex - 1;
                        updateTitleOnSectionAttached (mSectionIndex);
                    } else if (mNavigationDrawerFragment.getSectionNo () > 0 && mSectionIndex == 0) {
                        mSectionIndex = 0;
                        updateTitleOnSectionAttached (mSectionIndex);
                    }
                    mNavigationDrawerFragment.selectItemWithCallback (mSectionIndex + 1);
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
            Intent intent = new Intent (this, SettingsPreferenceActivity.class);
            startActivity (intent.putExtra (GlobalData.SECTION_INDEX, mSectionIndex));
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
            android.support.v7.app.AlertDialog.Builder builder =
                    new android.support.v7.app.AlertDialog.Builder (this);
            builder.setTitle (R.string.write_a_feedback_str);
            builder.setView (R.layout.dialog_feedback);
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

    @Override
    public int getSectionIndex() {
        return mSectionIndex;
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

    /**
     * Dispatch incoming result to the correct fragment.
     *
     * @param requestCode is requestCode from GlobalData
     * @param resultCode  Result_ok or RESULT_CANCELLED
     * @param data        data is null
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult (requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == StartupPage.SECTION_UPDATE_ACTIVITY_REQUEST) {
                updateTitleOnSectionAttached (mSectionIndex);
                mNavigationDrawerFragment.populateListView ();

            } else if (requestCode == StartupPage.NOTE_DETAIL_ACTIVITY_REQUEST) {
                if (data != null) {
                    if (data.getStringExtra (GlobalData.NOTE_DETAIL_ACTIVITY_ACTION).equals (GlobalData.NOTE_BODY_CHANGED_ACTION)) {
                        populateListView ();
                    }
                }
            }
        }
    }

    /**
     * Dispatch onPause() to fragments.
     */
    @Override
    protected void onPause() {
        super.onPause ();
    }

    @Override
    public void onListAdapterInteractionListener(String data1, String data2, String data3) {

    }

    @Override
    public void itemClicked(int sectionIndex, int noteIndex) {
//        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
//                this, null, null);
//        ActivityCompat.startActivity(this, new Intent(this, NoteDetailActivity.class),
//                options.toBundle());

        Intent intent = new Intent (StartupPage.this, NoteDetailActivity.class);
        intent.putExtra (GlobalData.SECTION_INDEX, sectionIndex);
        intent.putExtra (GlobalData.NOTE_INDEX, noteIndex);
        startActivityForResult (intent, StartupPage.NOTE_DETAIL_ACTIVITY_REQUEST);
    }

    private class PagerAdapter extends FragmentPagerAdapter {
        final String[] tabTitles;

        PagerAdapter(android.support.v4.app.FragmentManager fm) {
            super (fm);
            tabTitles = getResources ().getStringArray (R.array.tabs_name);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
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
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem (container, position);
            if (DEBUG) Log.d (TAG, "instantiateItem: fragment = " + fragment);
            mRegisteredFragments.put (position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            mRegisteredFragments.remove (position);  //Fragment destroyed by the pager adapter,
            // no need to store in mRegisteredFragments
            super.destroyItem (container, position, object);
        }

        @Override
        public void setPrimaryItem(ViewGroup container, int position, Object object) {
            super.setPrimaryItem (container, position, object);
        }

        @Override
        public int getCount() {
            return NO_OF_TABS;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }


    }
//    @Override
//     public void onAttach(Activity activity) {
//        super.onAttach (activity);
//        ((Navigation) activity).updateTitleOnSectionAttached (
//                getArguments ().getInt (ARG_SECTION_NUMBER));
//    }

}
