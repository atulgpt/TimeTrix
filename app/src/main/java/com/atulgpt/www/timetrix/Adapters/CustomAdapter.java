package com.atulgpt.www.timetrix.Adapters;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.provider.CalendarContract;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.atulgpt.www.timetrix.R;
import com.atulgpt.www.timetrix.Utils.NoteUtil;
import com.atulgpt.www.timetrix.Utils.GlobalData;
import com.doodle.android.chips.ChipsView;
import com.doodle.android.chips.model.Contact;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Atul Gupta on 25-02-2016 at 05:29 PM at 01:59 AM for TimeTrix .
 * Custom adapter for the list view
 */

public class CustomAdapter extends BaseAdapter implements ListAdapter, View.OnClickListener {
    private static final String TAG = CustomAdapter.class.getSimpleName ();
    private static final boolean DEBUG = true;
    private ArrayList<String> mList = new ArrayList<> ();
    private final Context mContext;
    private String mParam;
    private Handler mHandler = null;
    private String fileID;

    public CustomAdapter(ArrayList<String> list, Context context, String param, Handler handler) {
        this.mList = list;
        this.mContext = context;
        this.mParam = param;
        this.mHandler = handler;
    }

    public void setFileID(String fileID) {
        this.fileID = fileID;
    }

    private static class ViewHolderNotes {
        final TextView notes;
        TextView date;
        final TextView title;
        Button popupMenuDots;

        public ViewHolderNotes(View view) {
            notes = (TextView) view.findViewById (R.id.testViewNotes);
            date = (TextView) view.findViewById (R.id.testViewNotesDate);
            title = (TextView) view.findViewById (R.id.testViewNotesTitle);
            popupMenuDots = (Button) view.findViewById (R.id.btnVerticalMenu);
        }
    }


    private OnListAdapterInteractionListener mOnListAdapterInteractionListener;

    public void setOnListAdapterInteractionListener(OnListAdapterInteractionListener onListAdapterInteractionListener) {
        mOnListAdapterInteractionListener = onListAdapterInteractionListener;
        //Toast.makeText(mContext, "check = "+mOnListAdapterInteractionListener, Toast.LENGTH_SHORT).show();
    }


    @Override
    public int getCount() {
        return mList.size ();
    }

    @Override
    public Object getItem(int position) {
        return mList.get (position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, final View convertView, ViewGroup parent) {
        View view = convertView;
        if (mParam.equals ("navigation_drawer")) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate (R.layout.custom_row, parent, false);
            TextView subjectName = (TextView) view.findViewById (R.id.textViewRow);
            Typeface typeface = Typeface.createFromAsset(mContext.getAssets (),"font/Roboto_Medium.ttf");
            subjectName.setTypeface(typeface);
            subjectName.setText (mList.get (position));
            if (mList.get (position).equals (mContext.getString (R.string.add_subject_str))) {
                ImageView listIcon = (ImageView) view.findViewById (R.id.listIcon);
                listIcon.setImageResource (R.drawable.ic_add_circle_outline_black_24dp);
            }
        }
        if (mParam.contains ("subject_fragment")) {
            ViewHolderNotes viewHolderNotes;
            if (view == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate (R.layout.custom_row_notes, parent, false);
                viewHolderNotes = new ViewHolderNotes (view);
                view.setTag (viewHolderNotes);
            } else {
                viewHolderNotes = (ViewHolderNotes) view.getTag ();
            }

            JSONObject jsonObject = new JSONObject ();
            String noteText = mContext.getString (R.string.lorem_ipsum_str), noteDateStamp = mContext.getString (R.string.lorem_ipsum_str), titleText = mContext.getString (R.string.lorem_ipsum_str);
            long noteTimeInMillis;
            try {
                jsonObject = new JSONObject (mList.get (position));
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
            viewHolderNotes.notes.setText (noteText);
            viewHolderNotes.date.setText (" " + noteDateStamp);
            viewHolderNotes.title.setText (titleText);
//            Toast.makeText(mContext, "mParam in adapt ="+mParam, Toast.LENGTH_SHORT).show();

            viewHolderNotes.popupMenuDots.setTag (position);
            viewHolderNotes.popupMenuDots.setOnClickListener (this);

        }
        return view;
    }


    @Override
    public void onClick(View v) {
//        Toast.makeText(mContext, "Listener"+ mOnListAdapterInteractionListener +" handle = "+mHandler, Toast.LENGTH_SHORT).show();
        if (v.getId () == R.id.btnVerticalMenu) {
            PopupMenu popupMenu = new PopupMenu (mContext, v);
            popupMenu.inflate (R.menu.menu_context_notes_list);
            PopupMenuHandler popupMenuHandler = new PopupMenuHandler (mContext, v);
            popupMenu.setOnMenuItemClickListener (popupMenuHandler);
            int subjectID = (int) (Long.parseLong (fileID) + 1);
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
        //String fileID;
        ListView listView;
        int rowPosition;
        long noteIndex;

        public PopupMenuHandler(Context context, View view) {
            this.context = context;
            this.contextMenuDots = view;

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int id = item.getItemId ();
            rowPosition = (int) contextMenuDots.getTag ();
            JSONObject jsonObject;
            try {
                jsonObject = new JSONObject (mList.get (rowPosition));
                noteIndex = jsonObject.getLong (GlobalData.NOTE_INDEX);
            } catch (JSONException e) {
                e.printStackTrace ();
            }
            String title = null, note = null;
            View rowView = (View) this.contextMenuDots.getParent ();
            listView = (ListView) rowView.getParent ();
            //fileID = (String) cardView.getTag(R.string.filename);
            final int subjectID = (Integer.parseInt (fileID) + 1);
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
                populateListView ();
                if (DEBUG) Log.d (TAG, "onMenuItemClick deleted");
                Toast.makeText (context, R.string.note_deleted_to_str, Toast.LENGTH_SHORT).show ();
                if (mOnListAdapterInteractionListener != null) {
                    mOnListAdapterInteractionListener.onListAdapterInteractionListener (String.valueOf (rowPosition), String.valueOf (deletedNote));
                }
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
                                    populateListView ();
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
                ChipsView chipsView = (ChipsView) viewDialogTag.findViewById (R.id.chipViewTag);
                URI uri = null;
                try {
                    uri = new URI ("http://android.com/");
                } catch (URISyntaxException e) {
                    e.printStackTrace ();
                }

                Contact f =null;

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
                    populateListView ();
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

    private void populateListView() {
        mHandler.obtainMessage (GlobalData.POPULATE_LIST_VIEW).sendToTarget ();
    }

    public interface OnListAdapterInteractionListener {
        void onListAdapterInteractionListener(String data1, String data2);
    }

}
