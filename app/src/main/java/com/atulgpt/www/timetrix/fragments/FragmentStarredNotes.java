package com.atulgpt.www.timetrix.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.atulgpt.www.timetrix.R;
import com.atulgpt.www.timetrix.adapters.DatabaseAdapter;
import com.atulgpt.www.timetrix.adapters.RecyclerViewAdapter;
import com.atulgpt.www.timetrix.utils.GlobalData;
import com.atulgpt.www.timetrix.utils.NoteUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentStarredNotes.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentStarredNotes#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentStarredNotes extends android.support.v4.app.Fragment
        implements View.OnClickListener, DialogInterface.OnClickListener,
        RecyclerViewAdapter.OnListAdapterInteractionListener,
        DatabaseAdapter.DatabaseAdapterListener {
    private static final String TAG = FragmentStarredNotes.class.getSimpleName ();
    private static final Boolean DEBUG = true;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int DIALOG_BUTTON_CLICKED = 1;
    private static final int DIALOG_BUTTON_NOT_CLICKED = 0;

    // TODOo: Rename and change types of parameters
    private String mSectionIndex;
    //private String mTabPosition;
    private int mDialogStatus;
    private OnFragmentInteractionListener mOnFragmentInteractionListener;

    private ArrayList<String> mArrayListStarredNotes;

    private RecyclerView mRecyclerViewStar;
    private RecyclerViewAdapter mRecyclerViewAdapter;

    public static String tempString = "subject_fragment_star";
//    private ListView mListViewStar;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SubjectTabFragment.
     */
    // TODOo: Rename and change types and number of parameters
    public static FragmentStarredNotes newInstance(String param1, String param2) {
        FragmentStarredNotes fragment = new FragmentStarredNotes ();
        Bundle args = new Bundle ();
        args.putString (ARG_PARAM1, param1);
        args.putString (ARG_PARAM2, param2);
        fragment.setArguments (args);
        return fragment;
    }

    public void setSectionIndex(String sectionIndex) {
        this.mSectionIndex = sectionIndex;
    }

    public FragmentStarredNotes() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        if (getArguments () != null) {
            mSectionIndex = getArguments ().getString (ARG_PARAM1);
            //mTabPosition = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        return inflater.inflate (R.layout.fragment_starred_notes, container, false);

    }

    public void listDataSetChanged() {
        if (mOnFragmentInteractionListener != null) {
            mOnFragmentInteractionListener.onFragmentInteraction (this.getClass ().getName (), null);
        }
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach (activity);
        try {
            mOnFragmentInteractionListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException (activity.toString ()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated (savedInstanceState);
        mSectionIndex = String.valueOf (mOnFragmentInteractionListener.getSectionIndex ());
        populateListViewInBackground ();
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated (view, savedInstanceState);
        ImageButton btnAddNotes = (ImageButton) view.findViewById (R.id.buttonAddNotesStar);
        btnAddNotes.setVisibility (View.INVISIBLE);
        //btnAddNotes.setOnClickListener(this);
//        mListViewStar = (ListView) getActivity().findViewById(R.id.listNotesStarred);
        mRecyclerViewStar = (RecyclerView) view.findViewById (R.id.recyclerNotesStarred);

        mArrayListStarredNotes = new ArrayList<> ();
//        mCustomAdapterStarredNotes = new CustomAdapter(mArrayListStarredNotes, getActivity(), tempString);
//        mListViewStar.setTag(R.string.filename, mSectionIndex);
//        mListViewStar.setTag(R.string.list_object, mArrayListStarredNotes);
//        mListViewStar.setAdapter(mCustomAdapterStarredNotes);
//        mListViewStar.setItemsCanFocus(true);
//        registerForContextMenu(mListViewStar);

        mRecyclerViewStar.setHasFixedSize (true);
        mRecyclerViewAdapter = new RecyclerViewAdapter (mArrayListStarredNotes, getActivity (),
                this, tempString);
        mRecyclerViewStar.setAdapter (mRecyclerViewAdapter);
        LinearLayoutManager llm = new LinearLayoutManager (getActivity ());
        llm.setOrientation (LinearLayoutManager.VERTICAL);
        mRecyclerViewStar.setLayoutManager (llm);
    }

    @Override
    public void onDetach() {
        super.onDetach ();
        mOnFragmentInteractionListener = null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId () == R.id.buttonAddNotes) {
            //AlertDialog alertDialog = new AlertDialog(getActivity());
            if (mDialogStatus == DIALOG_BUTTON_NOT_CLICKED) {
                android.support.v7.app.AlertDialog.Builder builder =
                        new android.support.v7.app.AlertDialog.Builder (getActivity ());
                builder.setTitle (R.string.add_notes_str);
                builder.setCancelable (true);
                builder.setView (R.layout.dialog_add_notes);
                builder.setNegativeButton (R.string.cancel_str, new DialogInterface.OnClickListener () {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                builder.setPositiveButton (R.string.done_str, this);
                android.support.v7.app.AlertDialog alertDialog = builder.create ();
                alertDialog.setOnDismissListener (new DialogInterface.OnDismissListener () {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mDialogStatus = DIALOG_BUTTON_NOT_CLICKED;
                    }
                });
                alertDialog.show ();
                mDialogStatus = DIALOG_BUTTON_CLICKED;
            }
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Dialog d = (Dialog) dialog;
        EditText editText = (EditText) d.findViewById (R.id.editTextNotes);
        EditText editTextAddTitle = (EditText) d.findViewById (R.id.editTextNotesTitle);
        String note = editText.getText ().toString ();
        String title = editTextAddTitle.getText ().toString ();
        if (note.trim ().isEmpty ())
            return;
        JSONObject jsonObject = new JSONObject ();
        DatabaseAdapter databaseAdapter = new DatabaseAdapter (getActivity (), this);
        String allNote = databaseAdapter.getNotesForSection (Integer.parseInt (mSectionIndex) + 1);

        JSONArray jsonArray = new JSONArray ();
        try {
            if (allNote != null) {
                jsonArray = new JSONArray (allNote);
            } else {
                Toast.makeText (getActivity (), "Note = null", Toast.LENGTH_LONG).show ();
            }
        } catch (JSONException e) {
            if (DEBUG) Log.d (TAG, "onClick allNote couldn't be parsed");
            e.printStackTrace ();
            Toast.makeText (getActivity (), R.string.JSONArray_parse_fail_err_str, Toast.LENGTH_SHORT).show ();
        }
        try {
            int length;
            length = jsonArray.length ();
            jsonObject.put (GlobalData.NOTE_BODY, note);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm", Locale.US);
            long timeInMillis = System.currentTimeMillis ();
            String date = simpleDateFormat.format (new Date ());
            jsonObject.put (GlobalData.NOTE_DATE_STAMP, date);
            jsonObject.put (GlobalData.NOTE_TIME_MILLIS, timeInMillis);
            jsonObject.put (GlobalData.NOTE_TITLE, title);
            jsonObject.put (GlobalData.NOTE_IS_STAR, false);
            jsonArray.put (length, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace ();
            Toast.makeText (getActivity (), R.string.file_could_not_update_str, Toast.LENGTH_LONG).show ();
        }
        Boolean bool;
        bool = databaseAdapter.setNote (Integer.valueOf (mSectionIndex) + 1, jsonArray.toString ());
        if (bool) {
            populateListViewInBackground ();
        } else {
            Toast.makeText (getActivity (), R.string.file_could_not_update_str, Toast.LENGTH_LONG).show ();
        }

    }

    @Override
    public void onListAdapterInteractionListener(final String eventName, final String data2,
                                                 final String data3) {
        if (eventName.equals ("populate") && !eventName.equals ("delete")) {
            listDataSetChanged ();
        }
        if (eventName.equals ("delete")) {
            listDataSetChanged ();
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) getActivity ()
                    .findViewById (R.id.coordinatorLayout);
            View.OnClickListener mOnClickListener = new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    NoteUtil noteUtil = new NoteUtil (getActivity ());
                    try {
                        JSONObject jsonObjectNote = new JSONObject (data3);
                        Boolean bool = noteUtil.addNoteAtPosition (Long.valueOf (data2),
                                jsonObjectNote, Integer.valueOf (mSectionIndex) + 1);
                        if (DEBUG)
                            Toast.makeText (getActivity (), "Note Added " + bool, Toast.LENGTH_SHORT).show ();
                        populateListViewInBackground ();
                    } catch (JSONException e) {
                        e.printStackTrace ();
                        Toast.makeText (getActivity (), R.string.undo_failed_err_str, Toast.LENGTH_SHORT).show ();
                    }
                }
            };
            Snackbar snackbar = Snackbar
                    .make (coordinatorLayout, R.string.oops_made_a_mistake_str, Snackbar.LENGTH_LONG)
                    .setAction (R.string.undo_str, mOnClickListener);
            snackbar.setActionTextColor (Color.RED);
            View snackView = snackbar.getView ();
            snackView.setBackgroundColor (Color.DKGRAY);
            snackbar.show ();
        }
    }

    @Override
    public void itemClicked(int sectionIndex, int noteIndex) {

    }

    @Override
    public void populateListViewData(String allNotes, String query) {
        JSONArray jsonArray = new JSONArray ();
        try {
            if (allNotes != null)
                jsonArray = new JSONArray (allNotes);
        } catch (JSONException e) {
            e.printStackTrace ();
        }
        mArrayListStarredNotes.clear ();

        for (int count = 0; count < jsonArray.length (); count++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject (count);
                String temp = jsonObject.toString ();
                String noteTitle = jsonObject.getString (GlobalData.NOTE_TITLE);
                String noteBody = jsonObject.getString (GlobalData.NOTE_BODY);
                if (jsonObject.getBoolean (GlobalData.NOTE_IS_STAR)) {
                    if ((noteBody.toLowerCase ()).contains (query.toLowerCase ()) || (noteTitle.toLowerCase ()).contains (query.toLowerCase ()) || query.equals ("")) {
                        mArrayListStarredNotes.add (temp);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace ();
            }
        }
        mRecyclerViewAdapter.setNoteIndex (mSectionIndex);
        mRecyclerViewStar.setAdapter (mRecyclerViewAdapter);
        mRecyclerViewAdapter.notifyDataSetChanged ();
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TOD: Update argument type and name
        void onFragmentInteraction(String data1, String data2);

        int getSectionIndex();
    }

    public void populateListViewInBackground() {
        populateListViewInBackground ("");
    }

    public void populateListViewInBackground(String query) {
        DatabaseAdapter databaseAdapter = new DatabaseAdapter (getActivity (), this);
        databaseAdapter.getNotesForSectionInBackground (Integer.parseInt (mSectionIndex) + 1, query);
    }
}
