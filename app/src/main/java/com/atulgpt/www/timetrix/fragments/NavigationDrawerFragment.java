package com.atulgpt.www.timetrix.fragments;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.atulgpt.www.timetrix.R;
import com.atulgpt.www.timetrix.adapters.CustomAdapter;
import com.atulgpt.www.timetrix.adapters.DatabaseAdapter;

import java.util.ArrayList;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends android.support.v4.app.Fragment {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final boolean DEBUG = true;
    private static final String TAG = "NAV_DRAW";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private android.support.v7.app.ActionBarDrawerToggle mSupportDrawerToggle;
    //private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private View mFragmentContainerView;

    private int mCurrentSelectedPosition = 1;
    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;
    public ArrayList<String> arrayList;
    public CustomAdapter customAdapter;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences (getActivity ());
        mUserLearnedDrawer = sp.getBoolean (PREF_USER_LEARNED_DRAWER, false);

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt (STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
            if (DEBUG)
                Log.d (TAG, "onCreate: inside saveInstance not null, mCurr " + mCurrentSelectedPosition);
        }

        // Select either the default item (0) or the last selected item.
        if (DEBUG)
            Log.d (TAG, "onCreate: currentPos = " + mCurrentSelectedPosition + " savedInstance: " + savedInstanceState);
//        selectItemWithCallback (mCurrentSelectedPosition);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDrawerListView = (ListView) inflater.inflate (
                R.layout.fragment_navigation_drawer, container, false);
        arrayList = new ArrayList<> ();
        DatabaseAdapter databaseAdapter = new DatabaseAdapter (getActivity (), null);
        arrayList = databaseAdapter.getAllSectionNames ();
        arrayList.add (getString (R.string.add_section_str));
        customAdapter = new CustomAdapter (arrayList, getActivity (), "navigation_drawer", null);
        mDrawerListView.setAdapter (customAdapter);
        View headerView = View.inflate (getActivity (), R.layout.header_navigation, null);
        mDrawerListView.addHeaderView (headerView);
        mDrawerListView.setOnItemClickListener (new AdapterView.OnItemClickListener () {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (DEBUG) Log.d (TAG, "onCreateView: currentPos = " + position);
                selectItemWithCallback (position);
            }
        });
        mDrawerListView.setItemChecked (mCurrentSelectedPosition, true);
        return mDrawerListView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen (mFragmentContainerView);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated (savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu (true);
        if (DEBUG) Log.d (TAG, "onActivityCreated: currentPos = " + mCurrentSelectedPosition);
        selectItemWithCallback (mCurrentSelectedPosition);
//        Toast.makeText(getActivity(), "position = " + mCurrentSelectedPosition + " d list = " + mDrawerListView, Toast.LENGTH_LONG).show();
        //Toast.makeText(getActivity(), "position in onActivityCreated = "+mCurrentSelectedPosition, Toast.LENGTH_SHORT).show();
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity ().findViewById (fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow (R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list rowView with items and click listener
        //getActivity().getSupportActionBar();
        //ActionBar actionBar = getActionBar();
        //android.support.v7.app.ActionBar actionBar1 = getSupportActionBar();
//        if(actionBar != null){
//            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setHomeButtonEnabled(true);
//        }else if(StartupPage.actionBar != null){
//            StartupPage.actionBar.setDisplayHomeAsUpEnabled(true);
//            StartupPage.actionBar.setHomeButtonEnabled(true);
//        }


        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        //R.drawable.ic_drawer
        mSupportDrawerToggle = new android.support.v7.app.ActionBarDrawerToggle (
                getActivity (),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                (Toolbar) getActivity ().findViewById (R.id.toolbar),             /* toolbar as required by support lib */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed (drawerView);
                if (!isAdded ()) {
                    return;
                }

                getActivity ().invalidateOptionsMenu (); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened (drawerView);
                if (!isAdded ()) {
                    selectItemWithCallback (mCurrentSelectedPosition);
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences (getActivity ());
                    sp.edit ().putBoolean (PREF_USER_LEARNED_DRAWER, true).apply ();
                }

                getActivity ().invalidateOptionsMenu (); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer (mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post (new Runnable () {
            @Override
            public void run() {
                mSupportDrawerToggle.syncState ();
            }
        });

        mDrawerLayout.addDrawerListener (mSupportDrawerToggle);
    }

    public void selectItemWithCallback(int position) {
        mCurrentSelectedPosition = position;
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked (position, true);
            customAdapter.notifyDataSetChanged ();
        }
        if (mDrawerLayout != null) {
            mDrawerLayout.closeDrawer (mFragmentContainerView);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected (position);
        }
    }

    public void selectItemPosAndHighlight(int position) {
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked (position, true);
            customAdapter.notifyDataSetChanged ();
        }
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach (activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException ("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach ();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState (outState);
        outState.putInt (STATE_SELECTED_POSITION, mCurrentSelectedPosition);
        if (DEBUG) Log.d (TAG, "onSaveInstanceState: " + mCurrentSelectedPosition);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged (newConfig);
        // Forward the new configuration the drawer toggle component.
        mSupportDrawerToggle.onConfigurationChanged (newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen ()) {
            inflater.inflate (R.menu.global, menu);
            showGlobalContextActionBar ();
        }
        super.onCreateOptionsMenu (menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mSupportDrawerToggle.onOptionsItemSelected (item) || super.onOptionsItemSelected (item);

    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        android.support.v7.app.ActionBar actionBar = getSupportActionBar ();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled (true);
            actionBar.setTitle (R.string.app_name);
        }
    }

    private android.support.v7.app.ActionBar getSupportActionBar() {
        Object object = getHost ();
        AppCompatActivity appCompatActivity = (AppCompatActivity) object;
        return appCompatActivity.getSupportActionBar ();

    }

    public void populateListView() {
        DatabaseAdapter databaseAdapter = new DatabaseAdapter (getActivity (), null);
        ArrayList<String> arrayListTemp;
        arrayListTemp = databaseAdapter.getAllSectionNames ();
        arrayList.clear ();
        for (int i = 0; i < arrayListTemp.size (); i++) {
            arrayList.add (arrayListTemp.get (i));
        }
        arrayList.add (getString (R.string.add_section_str));
        mDrawerListView.setAdapter (customAdapter);
        // NotifyDataSetChanged is called in below function
        NavigationDrawerFragment.this.selectItemPosAndHighlight (mCurrentSelectedPosition);
    }

    public long getSectionNo() {
        return mDrawerListView.getAdapter ().getCount () - 2; // One is subtracted because we have
        // to exclude last item and other for header view
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position);
    }
}
