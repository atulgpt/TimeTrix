package com.atulgpt.www.timetrix.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.CalendarContract;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.atulgpt.www.timetrix.R;
import com.atulgpt.www.timetrix.Utils.GlobalData;
import com.atulgpt.www.timetrix.Utils.NoteUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by atulgupta on 07-06-2016 and 07 at 11:13 PM for TimeTrix .
 * This class makes the adapter for the recycler view for the starred fragment and allNotes Fragment
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolderNotes> implements View.OnClickListener {
    private static final boolean DEBUG = true;
    private static final String TAG = RecyclerViewAdapter.class.getSimpleName ();
    private ArrayList<String> mArrayList;
    //private ArrayList<String> mArrayListBackup = new ArrayList<> ();
    private final Context mContext;
    private String mParam;
    private OnListAdapterInteractionListener mOnListAdapterInteractionListener;
    private String mNoteIndex;
    //private SearchFilter mFilter;

    public RecyclerViewAdapter(ArrayList<String> list, Context context, String param) {
        this.mArrayList = list;
        this.mContext = context;
        this.mParam = param;
        //mFilter = new SearchFilter (RecyclerViewAdapter.this);
    }

    /*public SearchFilter getFilter() {
        return mFilter;
    }*/

    class ViewHolderNotes extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        final TextView notes;
        TextView date;
        final TextView title;
        Button popupMenuDots;

        ViewHolderNotes(View view) {
            super (view);
            notes = (TextView) view.findViewById (R.id.testViewNotes);
            date = (TextView) view.findViewById (R.id.testViewNotesDate);
            title = (TextView) view.findViewById (R.id.testViewNotesTitle);
            popupMenuDots = (Button) view.findViewById (R.id.btnVerticalMenu);

        }
    }
    /*
    private class SearchFilter extends Filter {
        private RecyclerViewAdapter mRecycleViewAdapter;
        private DatabaseAdapter mDatabaseAdapter = new DatabaseAdapter (mContext);

        private SearchFilter(RecyclerViewAdapter recyclerViewAdapter) {
            super ();
            this.mRecycleViewAdapter = recyclerViewAdapter;

        }

        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
//            Toast.makeText (mContext, "call 1: "+mArrayListBackup+"   "+mArrayList, Toast.LENGTH_SHORT).show ();
            for (int i = 0; i < mArrayList.size (); i++) {
                mArrayListBackup.add (mArrayList.get (i));
            }
            FilterResults filterResults = new FilterResults ();
            ArrayList<String> mListTemp = new ArrayList<> ();
            if (charSequence.length () == 0) {
                for (int i = 0; i < mArrayListBackup.size (); i++) {
                    mArrayList.add (mArrayListBackup.get (i));
                }
                filterResults.count = mArrayList.size ();
                filterResults.values = mArrayList;
            } else {
                for (int i = 0; i < mArrayList.size (); i++) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject (mArrayList.get (i));
                    } catch (JSONException e) {
                        e.printStackTrace ();
                    }
                    assert jsonObject != null;
                    String tempNoteBody = null;
                    try {
                        tempNoteBody = jsonObject.getString (GlobalData.NOTE_BODY);
                    } catch (JSONException e) {
                        e.printStackTrace ();
                    }
                    assert tempNoteBody != null;
                    if (tempNoteBody.toLowerCase ().contains (charSequence.toString ().toLowerCase ())) {
                        mListTemp.add (mArrayList.get (i));
                    }
                }
                mArrayList = mListTemp;
                filterResults.count = mListTemp.size ();
                filterResults.values = mListTemp;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            this.mRecycleViewAdapter.notifyDataSetChanged ();
        }
    }
    */
    public void setNoteIndex(String mNoteIndex) {
        this.mNoteIndex = mNoteIndex;
    }

    public void setOnListAdapterInteractionListener(OnListAdapterInteractionListener onListAdapterInteractionListener) {
        mOnListAdapterInteractionListener = onListAdapterInteractionListener;
    }

    /**
     * Called when RecyclerView needs a new {@link RecyclerView.ViewHolder} of the given type to represent
     * an item.
     * <p/>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p/>
     * The new ViewHolder will be used to display items of the adapter using
     * . Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     */


    @Override
    public ViewHolderNotes onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from (parent.getContext ()).inflate (R.layout.card_custom_row_notes, parent, false);
        // set the view's size, margins, padding's and layout parameters
        return new ViewHolderNotes (v);
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link RecyclerView.ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p/>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link RecyclerView.ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p/>
     * Override  instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ViewHolderNotes holder, int position) {
        JSONObject jsonObject = new JSONObject ();
        String noteText = mContext.getString (R.string.lorem_ipsum_str), noteDateStamp = mContext.getString (R.string.lorem_ipsum_str), titleText = mContext.getString (R.string.lorem_ipsum_str);
        long noteTimeInMillis;
        try {
            jsonObject = new JSONObject (mArrayList.get (position));
//            Toast.makeText (mContext, "value check= " + mArrayList.get (position), Toast.LENGTH_SHORT).show ();
        } catch (JSONException e) {
            e.printStackTrace ();
        }
        try {
            noteText = jsonObject.getString (GlobalData.NOTE_BODY);
        } catch (JSONException e) {
            e.printStackTrace ();
        }
        try {
            noteTimeInMillis = jsonObject.getLong (GlobalData.NOTE_TIME_MILLIS);
            noteDateStamp = (String) DateUtils.getRelativeTimeSpanString (noteTimeInMillis, System.currentTimeMillis (), 3, DateUtils.FORMAT_ABBREV_RELATIVE);
        } catch (JSONException e) {
            e.printStackTrace ();
        }
        try {
            titleText = jsonObject.getString (GlobalData.NOTE_TITLE);
        } catch (JSONException e) {
            e.printStackTrace ();
        }
        holder.notes.setText (noteText);
        holder.date.setText (" " + noteDateStamp);
        holder.title.setText (titleText);
//            Toast.makeText(mContext, "mParam in adapt ="+mParam, Toast.LENGTH_SHORT).show();

        holder.popupMenuDots.setTag (position);
        holder.popupMenuDots.setOnClickListener (this);


    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
//        Toast.makeText (mContext, "size check= "+mArrayList.size (), Toast.LENGTH_SHORT).show ();
        return mArrayList.size ();
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId () == R.id.btnVerticalMenu) {
            PopupMenu popupMenu = new PopupMenu (mContext, v);
            popupMenu.inflate (R.menu.menu_context_notes_list);
            PopupMenuHandler popupMenuHandler = new PopupMenuHandler (mContext, v);
            popupMenu.setOnMenuItemClickListener (popupMenuHandler);
            int subjectID = (int) (Long.parseLong (mNoteIndex) + 1);
            NoteUtil noteUtil = new NoteUtil (mContext);
            if (mParam.contains ("all"))
                popupMenu.getMenu ().findItem (R.id.action_star).setChecked (noteUtil.getStarStatus ((int) v.getTag (), subjectID));
            else popupMenu.getMenu ().findItem (R.id.action_star).setChecked (true);
            popupMenu.show ();
//            Toast.makeText (mContext, ""+noteUtil.getNotesForATag (subjectID,"note 3 tag"), Toast.LENGTH_LONG).show ();
//            Log.d (TAG, "onClick "+noteUtil.getNotesForATag (subjectID,"note 3 tag"));
        } else {
            ListView listView = (ListView) v.getParent ();
            String fileID = (String) listView.getTag (R.string.filename);
            int subjectID = (int) (Long.parseLong (fileID) + 1);
            int notePosition = (int) v.getTag ();
            NoteUtil noteUtil = new NoteUtil (mContext);
            JSONArray jsonArray = noteUtil.getNoteJSONArray (subjectID);
            AlertDialog.Builder builder = new AlertDialog.Builder (mContext);
            builder.setCancelable (true);
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
            View viewDialog = inflater.inflate (R.layout.note_expanded_layout, null);
            TextView titleTextView = (TextView) viewDialog.findViewById (R.id.noteTitleExpandedView);
            TextView bodyTextView = (TextView) viewDialog.findViewById (R.id.noteBodyExpanderView);
            try {
                titleTextView.setText (jsonArray.getJSONObject (notePosition).getString (GlobalData.NOTE_TITLE));
                bodyTextView.setText (jsonArray.getJSONObject (notePosition).getString (GlobalData.NOTE_BODY));
            } catch (JSONException e) {
                e.printStackTrace ();
            }
            builder.setView (viewDialog);
            builder.setPositiveButton (R.string.done_str, new DialogInterface.OnClickListener () {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Dialog d = (Dialog) dialog;
                }
            });
            AlertDialog alertDialog = builder.create ();
            alertDialog.show ();
        }
    }

    private class PopupMenuHandler implements PopupMenu.OnMenuItemClickListener {
        final Context context;
        View contextMenuDots;
        //String mNoteIndex;
        CardView cardView;
        int rowPosition;
        long noteIndex;

        PopupMenuHandler(Context context, View view) {
            this.context = context;
            this.contextMenuDots = view;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId ();
            rowPosition = (int) contextMenuDots.getTag ();
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject (mArrayList.get (rowPosition));
                noteIndex = jsonObject.getLong (GlobalData.NOTE_INDEX);
            } catch (JSONException e) {
                e.printStackTrace ();
            }
            String title = null, note = null;
//            View rowView = (View) this.contextMenuDots.getParent ();
//            cardView = (CardView) rowView.getParent ();
            //mNoteIndex = (String) cardView.getTag(R.string.filename);
            final int subjectID = (Integer.parseInt (RecyclerViewAdapter.this.mNoteIndex) + 1);
            JSONArray jsonArray;
            final NoteUtil noteUtil = new NoteUtil (context);
            LayoutInflater inflater = (LayoutInflater) context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
            jsonArray = noteUtil.getNoteJSONArray (subjectID);
            if (jsonArray == null)
                return true;

            try {
                title = jsonArray.getJSONObject ((int) noteIndex).getString (GlobalData.NOTE_TITLE);
                note = jsonArray.getJSONObject ((int) noteIndex).getString (GlobalData.NOTE_BODY);
            } catch (JSONException e) {
                e.printStackTrace ();
            }
            if (id == R.id.action_delete) {
                JSONObject deletedNote = noteUtil.deleteNote (noteIndex, subjectID);
                populateListView ("delete", String.valueOf (rowPosition), String.valueOf (deletedNote));
                if (DEBUG) Log.d (TAG, "onMenuItemClick deleted");
                Toast.makeText (context, R.string.note_deleted_to_str, Toast.LENGTH_SHORT).show ();
//                if (mOnListAdapterInteractionListener != null) {
//                    mOnListAdapterInteractionListener.onListAdapterInteractionListener (String.valueOf (rowPosition), String.valueOf (deletedNote));
//                }
                return true;
            }

            if (id == R.id.action_edit) {
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder (context)
                        .setTitle (R.string.edit_note_str)
                        .setCancelable (true);
                View viewDialogEdit = inflater.inflate (R.layout.dialog_add_notes, null);
                EditText editTextNoteTitle = (EditText) viewDialogEdit.findViewById (R.id.editTextNotesTitle);
                EditText editTextNoteBody = (EditText) viewDialogEdit.findViewById (R.id.editTextNotes);
                editTextNoteTitle.setText (title);
                editTextNoteBody.setText (note);
                builder.setView (viewDialogEdit)
                        .setNegativeButton (R.string.cancel_str, new DialogInterface.OnClickListener () {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        })

                        .setPositiveButton (R.string.done_str, new DialogInterface.OnClickListener () {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Dialog d = (Dialog) dialog;
                                EditText editTextNoteBody = (EditText) d.findViewById (R.id.editTextNotes);
                                EditText editTextNoteTitle = (EditText) d.findViewById (R.id.editTextNotesTitle);
                                String note = editTextNoteBody.getText ().toString ();
                                String title = editTextNoteTitle.getText ().toString ();
                                if (note.trim ().isEmpty ())
                                    return;
                                Boolean boolResponse = noteUtil.editNote (noteIndex, subjectID, note, title);
                                if (boolResponse) {
                                    populateListView ("edit", "", "");
                                } else {
                                    Toast.makeText (context, R.string.file_could_not_update_str, Toast.LENGTH_LONG).show ();
                                }
                            }
                        })
                        .show ();
                return true;
            }

            if (id == R.id.action_tag) {
                inflater = (LayoutInflater) context.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
                View viewDialogTag = inflater.inflate (R.layout.dialog_add_tag, null);
                AutoCompleteTextView editTextTag = (AutoCompleteTextView) viewDialogTag.findViewById (R.id.tag_editText);
                ArrayList<String> tagStringList = noteUtil.getTotalTagsString (subjectID);
                ArrayAdapter<String> adapter = new ArrayAdapter<> (context,
                        android.R.layout.simple_dropdown_item_1line, tagStringList);
                editTextTag.setAdapter (adapter);
                editTextTag.setThreshold (1);
//                Toast.makeText (context, ""+tagStringList, Toast.LENGTH_LONG).show ();
                new android.support.v7.app.AlertDialog.Builder (context)
                        .setTitle (R.string.add_tag_str).setMessage (R.string.write_tag_name_pick_a_color_str)
                        .setView (viewDialogTag)
                        .setNegativeButton (R.string.cancel_str, new DialogInterface.OnClickListener () {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss ();
                            }
                        })
                        .setPositiveButton (R.string.done_str, new DialogInterface.OnClickListener () {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Dialog d = (Dialog) dialog;
                                AutoCompleteTextView editTextTag = (AutoCompleteTextView) d.findViewById (R.id.tag_editText);
                                String noteTag = editTextTag.getText ().toString ();
                                if (noteTag.trim ().isEmpty ()) {
                                    Toast.makeText (context, R.string.empty_tag_nt_allowed_to_str, Toast.LENGTH_LONG).show ();
                                    return;
                                }
                                Spinner colorSpinner = (Spinner) d.findViewById (R.id.color_spinner);
                                String tagColor = (String) colorSpinner.getSelectedItem ();
                                if (!noteUtil.addTag (noteIndex, subjectID, noteTag, tagColor)) {
                                    Toast.makeText (context, R.string.operation_failed_tag_not_apply_to_str, Toast.LENGTH_SHORT).show ();
                                }
//                        Toast.makeText(context, "result for tag" + bool +"  "+ noteTag +"  "+ tagColor, Toast.LENGTH_SHORT).show();
                            }
                        })
                        .show ();
                return true;
            }

            if (id == R.id.action_star) {
                if (noteUtil.addOrRemoveStar (noteIndex, subjectID)) {
                    populateListView ("star", "", "");
                } else {
                    Toast.makeText (context, R.string.file_could_not_update_str, Toast.LENGTH_LONG).show ();
                }
                return true;
            }

            if (id == R.id.action_add_alarm) {
                Calendar beginTime = Calendar.getInstance ();
                beginTime.setTimeInMillis (beginTime.getTimeInMillis ());
                Intent intent = new Intent (Intent.ACTION_INSERT)
                        .setData (CalendarContract.Events.CONTENT_URI)
                        .putExtra (CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis ())
                        .putExtra (CalendarContract.Events.TITLE, title)
                        .putExtra (CalendarContract.Events.DESCRIPTION, note)
                        .putExtra (CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY);
                context.startActivity (intent);
            }
            return false;
        }
    }

    private void populateListView(String event, String data1, String data2) {
        if (!event.equals ("delete"))
            mOnListAdapterInteractionListener.onListAdapterInteractionListener ("populate", "", "");
        else
            mOnListAdapterInteractionListener.onListAdapterInteractionListener ("delete", data1, data2);
    }

    public interface OnListAdapterInteractionListener {
        void onListAdapterInteractionListener(String data1, String data2, String data3);
    }

}

