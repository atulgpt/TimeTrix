package com.atulgpt.www.timetrix.Fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.atulgpt.www.timetrix.Adapters.CustomAdapter;
import com.atulgpt.www.timetrix.Adapters.DatabaseAdapter;
import com.atulgpt.www.timetrix.Adapters.RecyclerViewAdapter;
import com.atulgpt.www.timetrix.R;
import com.atulgpt.www.timetrix.Utils.NoteUtil;
import com.atulgpt.www.timetrix.Utils.Util;

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
 * {@link FragmentAllNotes.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentAllNotes#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentAllNotes extends android.support.v4.app.Fragment implements View.OnClickListener, DialogInterface.OnClickListener,
        CustomAdapter.OnListAdapterInteractionListener, RecyclerViewAdapter.OnListAdapterInteractionListener {
    private static final String TAG = FragmentAllNotes.class.getSimpleName ();
    private static final boolean DEBUG = true;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int DIALOG_BUTTON_CLICKED = 1;
    private static final int DIALOG_BUTTON_NOT_CLICKED = 0;

    // TODOo: Rename and change types of parameters
    private String mFileID;
    private String mTabPosition;
    private int mDialogStatus;
    private OnFragmentInteractionListener mListener;
    //private static CustomAdapter.OnListAdapterInteractionListener mOnListAdapterInteractionListener;

    private CustomAdapter mCustomAdapterAllNotes;
    private RecyclerViewAdapter mCustomRecyclerAdapterAllNotes;
    private ArrayList<String> mNotesListAllNotes;
    private Handler mHandlerAll = null;
    private ListView mListViewAll;
    private RecyclerView mRecyclerViewAll;

    public static final String tempString = "subject_fragment_all";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SubjectTabFragment.
     */
    // TODOo: Rename and change types and number of parameters
    public static FragmentAllNotes newInstance(String param1, String param2) {
        FragmentAllNotes fragment = new FragmentAllNotes ();
        Bundle args = new Bundle ();
        args.putString (ARG_PARAM1, param1);
        args.putString (ARG_PARAM2, param2);
        fragment.setArguments (args);
        return fragment;
    }

    public void setArgParam1(String param1) {
        this.mFileID = param1;
    }


    public FragmentAllNotes() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState (outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        if (getArguments () != null) {
            mFileID = getArguments ().getString (ARG_PARAM1);
            mTabPosition = getArguments ().getString (ARG_PARAM2);
        }

    }

    @Override
    public void onStart() {
        super.onStart ();
    }

    @Override
    public void onResume() {
        super.onResume ();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate (R.layout.fragment_all_notes, container, false);
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments (args);
        if (DEBUG) Log.d (TAG, "setArgument " + args);
    }

    // TODOo: Rename method, update argument and hook method into UI event
    public void listDataSetChanged() {
        if (mListener != null) {
            mListener.onFragmentInteraction (this.getClass ().getName (), null);
        } else
            Toast.makeText (getActivity (), "Couldn't update the list!", Toast.LENGTH_LONG).show ();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach (activity);
        //Toast.makeText(getActivity(), "onAttach in fragmentAll", Toast.LENGTH_SHORT).show();
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException (activity.toString ()
                    + " must implement OnFragmentInteractionListener");
        }
//        ((StartupPage)activity).onSectionAttached (Integer.parseInt (mFileID));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach (context);
        //Toast.makeText(getActivity(), "onAttach in fragmentAll", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated (savedInstanceState);
        FloatingActionButton btnAddNotes = (FloatingActionButton) getActivity ().findViewById (R.id.buttonAddNotes);
        btnAddNotes.setOnClickListener (this);
//        mListViewAll = (ListView) getActivity ().findViewById (R.id.listNotesAll);
        mRecyclerViewAll = (RecyclerView) getActivity ().findViewById (R.id.recyclerNotesAll);

        mHandlerAll = new Handler () {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case Util.POPULATE_LIST_VIEW:// if receive massage
                        //populateListView();
                        listDataSetChanged ();
                }
            }
        };

        mNotesListAllNotes = new ArrayList<> ();

//        mCustomAdapterAllNotes = new CustomAdapter (mNotesListAllNotes, getActivity (), tempString, mHandlerAll);
        mRecyclerViewAll.setHasFixedSize (true);
        mCustomRecyclerAdapterAllNotes = new RecyclerViewAdapter (mNotesListAllNotes, getActivity (), tempString);

        mRecyclerViewAll.setAdapter (mCustomRecyclerAdapterAllNotes);
        mCustomRecyclerAdapterAllNotes.setOnListAdapterInteractionListener (this);
        LinearLayoutManager llm = new LinearLayoutManager (getActivity ());
        llm.setOrientation (LinearLayoutManager.VERTICAL);
        mRecyclerViewAll.setLayoutManager (llm);
        populateListView ();
//        mCustomAdapterAllNotes.setOnListAdapterInteractionListener (this);
//        mListViewAll.setTag (R.string.filename, mFileID);
//        mListViewAll.setTag (R.string.list_object, mNotesListAllNotes);
//        mListViewAll.setAdapter (mCustomAdapterAllNotes);
//        mListViewAll.setItemsCanFocus (true);
//        populateListView ();

//        testEditext.addTextChangedListener (new TextWatcher () {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
////                mCustomRecyclerAdapterAllNotes.getFilter().filter(charSequence.toString());
//                Toast.makeText (getActivity (), "change: "+charSequence+"ch", Toast.LENGTH_SHORT).show ();
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
    }

    @Override
    public void onDetach() {
        super.onDetach ();
        mListener = null;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        if (v.getId () == R.id.buttonAddNotes) {
            //AlertDialog alertDialog = new AlertDialog(getActivity());
            if (mDialogStatus == DIALOG_BUTTON_NOT_CLICKED) {

                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder (getActivity ());
                builder.setTitle (R.string.add_notes_str);
                builder.setCancelable (true);
                LayoutInflater inflater = getActivity ().getLayoutInflater ();
                View dialogView = inflater.inflate (R.layout.dialog_add_notes, null);
                // for transition
//                String transitionName = getString (R.string.transition_string);
//                ActivityOptionsCompat activityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation (getActivity (),dialogView,transitionName);
//
//                DialogAddNewNote dialogAddNewNote = DialogAddNewNote.newInstance(5);
//                ChangeBounds changeBoundsTransition = TransitionInflater.from(getActivity ()).inflateTransition(R.transition.change_bounds);
//                dialogAddNewNote.setSharedElementEnterTransition (changeBoundsTransition);
                builder.setView (dialogView);
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
        DatabaseAdapter databaseAdapter = new DatabaseAdapter (getActivity ());
        String allNote = databaseAdapter.getNote (Long.parseLong (mFileID) + 1);
        JSONArray js = new JSONArray ();
        if (DEBUG)
            Log.d (TAG, "onClick allNote = " + allNote + " SubjectId -1 = " + mFileID + " empty json = " + js + " empty 2nd =" + js.toString ());
        JSONArray jsonArray = new JSONArray ();
        try {
            if (allNote != null) {
                jsonArray = new JSONArray (allNote);
            }
        } catch (JSONException e) {
            e.printStackTrace ();
            Toast.makeText (getActivity (), R.string.JSONArray_parse_fail_err_str, Toast.LENGTH_SHORT).show ();
            if (DEBUG)
                Log.d (TAG, "onClick " + getString (R.string.JSONArray_parse_fail_err_str) + " allNote = " + allNote);
        }
        try {
            int length;
            length = jsonArray.length ();
            jsonObject.put (Util.NOTE_INDEX, length);
            jsonObject.put (Util.NOTE_TITLE, title);
            jsonObject.put (Util.NOTE_BODY, note);
            jsonObject.put (Util.NOTE_IS_STAR, false);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm", Locale.US);
            long timeInMillis = System.currentTimeMillis ();
            String date = simpleDateFormat.format (new Date ());
            jsonObject.put (Util.NOTE_DATE_STAMP, date);
            jsonObject.put (Util.NOTE_TIME_MILLIS, timeInMillis);
            jsonArray.put (length, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace ();
            Toast.makeText (getActivity (), R.string.file_could_not_update_str, Toast.LENGTH_LONG).show ();
            if (DEBUG) Log.d (TAG, "onClick 1." + getString (R.string.file_could_not_update_str));
        }
        Boolean bool;
        bool = databaseAdapter.setNote (Long.valueOf (mFileID) + 1, jsonArray.toString ());
        if (DEBUG) Log.d (TAG, "onClick checking JSONArray" + jsonArray.toString ());
        if (bool) {
            populateListView ();
        } else {
            Toast.makeText (getActivity (), R.string.file_could_not_update_str, Toast.LENGTH_LONG).show ();
            if (DEBUG) Log.d (TAG, "onClick 2." + getString (R.string.file_could_not_update_str));
        }

    }

    @Override
    public void onListAdapterInteractionListener(final String eventName, final String data2, final String data3) {
        if (eventName.equals ("populate") && !eventName.equals ("delete")) {
            listDataSetChanged ();
        }
        if (eventName.equals ("delete")) {
            listDataSetChanged ();
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) getActivity ().findViewById (R.id.coordinatorLayout);
            View.OnClickListener mOnClickListener = new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    NoteUtil noteUtil = new NoteUtil (getActivity ());
                    try {
                        JSONObject jsonObjectNote = new JSONObject (data3);
                        Boolean bool = noteUtil.addNoteAtAPosition (Long.valueOf (data2), jsonObjectNote, Long.valueOf (mFileID) + 1);
                        if (DEBUG)
                            Toast.makeText (getActivity (), "Note Added " + bool, Toast.LENGTH_SHORT).show ();
                        populateListView ();
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
    public void onListAdapterInteractionListener(String data1, String data2) {

    }


    public interface OnFragmentInteractionListener {
        // TODOo: Update argument type and name
        void onFragmentInteraction(String data1, String data2);
    }

    public void populateListView() {
        populateListView ("");
    }

    /**
     * @param query search string from the parent activity to display in the recyclerView
     */
    public void populateListView(String query) {
        DatabaseAdapter databaseAdapter = new DatabaseAdapter (getActivity ());
        String allNote = databaseAdapter.getNote (Long.parseLong (mFileID) + 1);
        JSONArray jsonArray = new JSONArray ();
        try {
            if (allNote != null)
                jsonArray = new JSONArray (allNote);
        } catch (JSONException e) {
            e.printStackTrace ();
            Toast.makeText (getActivity (), R.string.all_note_is_corrupted_err_str, Toast.LENGTH_LONG).show ();
        }
        mNotesListAllNotes.clear ();
        // Toast.makeText(getActivity(), " 1. noteList"+mNotesListAllNotes.toString(), Toast.LENGTH_SHORT).show();

        for (int count = 0; count < jsonArray.length (); count++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject (count);
                String temp = jsonObject.toString ();
                String noteTitle = jsonObject.getString (Util.NOTE_TITLE);
                String noteBody = jsonObject.getString (Util.NOTE_BODY);
                if ((noteBody.toLowerCase ()).contains (query.toLowerCase ()) || (noteTitle.toLowerCase ()).contains (query.toLowerCase ()) || query.equals ("")) {
                    mNotesListAllNotes.add (temp);
                }
            } catch (JSONException e) {
                e.printStackTrace ();
                Toast.makeText (getActivity (), R.string.updating_list_failed_err_str, Toast.LENGTH_LONG).show ();
            }
        }
        //  Toast.makeText(getActivity(), " 2. noteList"+mNotesListAllNotes.toString(), Toast.LENGTH_SHORT).show();
//        mCustomAdapterAllNotes.setFileID (mFileID);
//        mListViewAll.setAdapter (mCustomAdapterAllNotes);
//        //Toast.makeText(getActivity(), "adapt attached " +mListViewAll.getAdapter() , Toast.LENGTH_SHORT).show();
//        mCustomAdapterAllNotes.notifyDataSetChanged ();

        mCustomRecyclerAdapterAllNotes.setFileID (mFileID);
        mRecyclerViewAll.setAdapter (mCustomRecyclerAdapterAllNotes);
        mCustomRecyclerAdapterAllNotes.notifyDataSetChanged ();
    }
}
