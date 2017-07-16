package com.atulgpt.www.timetrix.Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.atulgpt.www.timetrix.Adapters.CustomAdapter;
import com.atulgpt.www.timetrix.Adapters.CustomAdapterTags;
import com.atulgpt.www.timetrix.Adapters.DatabaseAdapter;
import com.atulgpt.www.timetrix.R;
import com.atulgpt.www.timetrix.Utils.GlobalData;

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
public class FragmentTagNotes extends android.support.v4.app.Fragment implements View.OnClickListener, DialogInterface.OnClickListener {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int DIALOG_BUTTON_CLICKED = 1;
    private static final int DIALOG_BUTTON_NOT_CLICKED = 0;

    // TODOo: Rename and change types of parameters
    private String mFileID;
    private String mTabPosition;
    private int mDialogStatus;
    private OnFragmentInteractionListener mListener;


    CustomAdapter customAdapterTagNotes;
    private CustomAdapterTags mCustomAdapterTags;
    private ArrayList<String> mNotesListTagNotes;
    private ExpandableListView mListViewTag;
    private Handler mHandler = null;
    public static String tempString = "subject_fragment_tag";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SubjectTabFragment.
     */
    // TODOo: Rename and change types and number of parameters
    public static FragmentTagNotes newInstance(String param1, String param2) {
        FragmentTagNotes fragment = new FragmentTagNotes();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public void setArgParam1(String param1){
        this.mFileID = param1;
    }

    public FragmentTagNotes() {
        // Required empty public constructor
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFileID = getArguments().getString(ARG_PARAM1);
            mTabPosition = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tag_notes, container, false);
    }

    // TODOo: Rename method, update argument and hook method into UI event
    public void listDataSetChanged() {
        if (mListener != null) {
            mListener.onFragmentInteraction(this.getClass().getName(),null);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated (savedInstanceState);
        ImageButton btnAddNotes = (ImageButton) getActivity().findViewById(R.id.buttonAddNotesTags);
        btnAddNotes.setOnClickListener (this);
        btnAddNotes.setVisibility(View.INVISIBLE);
//        ListView mListViewTag = (ListView) getActivity().findViewById(R.id.listNotesAll);

        mListViewTag = (ExpandableListView) getActivity().findViewById(R.id.expandableListNotesTags);
        mHandler = new Handler() {
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case GlobalData.POPULATE_LIST_VIEW:// if receive massage
                        //populateListView();
                        listDataSetChanged();
                }
            }
        };

        mNotesListTagNotes = new ArrayList<>();
//        customAdapterTagNotes = new CustomAdapter(mNotesListTagNotes, getActivity(),tempString , mHandler);
        mCustomAdapterTags = new CustomAdapterTags(getActivity(),Long.parseLong(mFileID) + 1);
        mListViewTag.setTag (R.string.filename, mFileID);
        mListViewTag.setTag (R.string.list_object, mNotesListTagNotes);
        mListViewTag.setAdapter (mCustomAdapterTags);
        mListViewTag.setItemsCanFocus (true);
        registerForContextMenu (mListViewTag);
//        DisplayMetrics dm = new DisplayMetrics();
//        getActivity ().getWindowManager().getDefaultDisplay().getMetrics (dm);
//        int width = dm.widthPixels;
//
//        mListViewTag.setIndicatorBounds (width, width);
        populateListView();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.buttonAddNotes) {
            if (mDialogStatus == DIALOG_BUTTON_NOT_CLICKED) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder (getActivity());
                builder.setTitle(R.string.add_notes_str);
                builder.setCancelable(true);
                LayoutInflater inflater = getActivity().getLayoutInflater();
                builder.setView(inflater.inflate(R.layout.dialog_add_notes, null));
                builder.setNegativeButton(R.string.cancel_str, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });
                builder.setPositiveButton(R.string.done_str, this);
                android.support.v7.app.AlertDialog alertDialog = builder.create ();
                alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mDialogStatus = DIALOG_BUTTON_NOT_CLICKED;
                    }
                });
                alertDialog.show();
                mDialogStatus = DIALOG_BUTTON_CLICKED;
            }
        }
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        Dialog d = (Dialog) dialog;
        EditText editText = (EditText) d.findViewById(R.id.editTextNotes);
        EditText editTextAddTitle = (EditText) d.findViewById(R.id.editTextNotesTitle);
        String note = editText.getText().toString();
        String title = editTextAddTitle.getText().toString();
        if (note.trim().isEmpty())
            return;
        JSONObject jsonObject = new JSONObject();
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(getActivity());
        String allNote = databaseAdapter.getNote(Long.parseLong(mFileID) + 1);

        JSONArray jsonArray = new JSONArray();
        try {
            if (allNote != null) {
                jsonArray = new JSONArray(allNote);
            } else {
                Toast.makeText(getActivity(), "Note = null", Toast.LENGTH_LONG).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.JSONArray_parse_fail_err_str, Toast.LENGTH_SHORT).show();
        }
        try {
            int length;
            length = jsonArray.length();
            jsonObject.put(GlobalData.NOTE_BODY, note);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
            long timeInMillis = System.currentTimeMillis();
            String date = simpleDateFormat.format(new Date());
            jsonObject.put(GlobalData.NOTE_DATE_STAMP, date);
            jsonObject.put(GlobalData.NOTE_TIME_MILLIS, timeInMillis);
            jsonObject.put(GlobalData.NOTE_TITLE, title);
            jsonObject.put(GlobalData.NOTE_IS_STAR, false);
            jsonArray.put(length, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), R.string.file_could_not_update_str, Toast.LENGTH_LONG).show();
        }
        Boolean bool;
        bool = databaseAdapter.setNote(Long.valueOf(mFileID) + 1, jsonArray.toString());
        if (bool) {
            populateListView();
        } else {
            Toast.makeText(getActivity(), R.string.file_could_not_update_str, Toast.LENGTH_LONG).show();
        }

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
        // TODOo: Update argument type and name
        void onFragmentInteraction(String data1, String data2);
    }

    public void populateListView() {
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(getActivity());
        String allNote = databaseAdapter.getNote(Long.parseLong(mFileID) + 1);
        JSONArray jsonArray = new JSONArray();
        //Toast.makeText(getActivity(),"jsonArray = "+jsonArray.toString()+"allNote"+allNote,Toast.LENGTH_LONG).show();
        try {
            if (allNote != null)
                jsonArray = new JSONArray(allNote);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mNotesListTagNotes.clear();

        for (int count = 0; count < jsonArray.length(); count++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(count);
                String temp = jsonObject.toString();
                mNotesListTagNotes.add(temp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mCustomAdapterTags.setFileID(Long.valueOf (mFileID)+1);
        mListViewTag.setAdapter (mCustomAdapterTags);
        mCustomAdapterTags.notifyDataSetChanged();
    }

    public void populateListView(String query) {
        DatabaseAdapter databaseAdapter = new DatabaseAdapter(getActivity());
        String allNote = databaseAdapter.getNote(Long.parseLong(mFileID) + 1);
        JSONArray jsonArray = new JSONArray();
        //Toast.makeText(getActivity(),"jsonArray = "+jsonArray.toString()+"allNote"+allNote,Toast.LENGTH_LONG).show();
        try {
            if (allNote != null)
                jsonArray = new JSONArray(allNote);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mNotesListTagNotes.clear();

        for (int count = 0; count < jsonArray.length(); count++) {
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(count);
                String temp = jsonObject.toString();
                String noteTitle = jsonObject.getString (GlobalData.NOTE_TITLE);
                String noteBody = jsonObject.getString (GlobalData.NOTE_BODY);
                if((noteBody.toLowerCase ()).contains (query.toLowerCase ()) || (noteTitle.toLowerCase ()).contains (query.toLowerCase ())){
                    mNotesListTagNotes.add(temp);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mCustomAdapterTags.setFileID(Long.valueOf (mFileID)+1);
        mListViewTag.setAdapter (mCustomAdapterTags);
        mCustomAdapterTags.notifyDataSetChanged();
    }

}
