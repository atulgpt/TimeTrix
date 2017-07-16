package com.atulgpt.www.timetrix.Adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.atulgpt.www.timetrix.R;
import com.atulgpt.www.timetrix.Utils.NoteUtil;
import com.atulgpt.www.timetrix.Utils.GlobalData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by atulgupta on 02-05-2016 at 12:22 AM for TimeTrix .
 * Custom adapter for tag tab
 */
public class CustomAdapterTags extends BaseExpandableListAdapter {
    private final Context mContext;
    private Long mFileID;

    public CustomAdapterTags(Context context, long param) {
        this.mContext = context;
        this.mFileID = param;
    }

    public void setFileID(Long fileID) {
        this.mFileID = fileID;
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

    @Override
    public int getGroupCount() {
        NoteUtil noteUtil = new NoteUtil (mContext);
        return noteUtil.getSubjectTagsCount (mFileID);
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        NoteUtil noteUtil = new NoteUtil (mContext);
        String tag = noteUtil.getTotalTagsString (mFileID).get (groupPosition);
        return noteUtil.getNotesForATagCount (mFileID, tag);
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        View view;
        view = inflater.inflate (R.layout.custom_row_tags, parent, false);
        NoteUtil noteUtil = new NoteUtil (mContext);
        ArrayList<String> tagList = noteUtil.getTotalTagsString (mFileID);
        TextView textView = (TextView) view.findViewById (R.id.textViewTagRow);
        textView.setText (tagList.get (groupPosition));
        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService (Context.LAYOUT_INFLATER_SERVICE);
        ViewHolderNotes viewHolderNotes;
        if (convertView == null) {
            convertView = inflater.inflate (R.layout.custom_row_notes, parent, false);
            viewHolderNotes = new ViewHolderNotes (convertView);
            convertView.setTag (viewHolderNotes);
        } else {
            viewHolderNotes = (ViewHolderNotes) convertView.getTag ();
        }
        NoteUtil noteUtil = new NoteUtil (mContext);
        JSONArray jsonArrayNotesTag = noteUtil.getNotesForATag (mFileID, noteUtil.getTotalTagsString (mFileID).get (groupPosition));
        JSONObject jsonObject = new JSONObject ();
        String noteText = mContext.getString (R.string.lorem_ipsum_str), noteDateStamp = mContext.getString (R.string.lorem_ipsum_str), titleText = mContext.getString (R.string.lorem_ipsum_str);
        long noteTimeInMillis;
        try {
            jsonObject = jsonArrayNotesTag.getJSONObject (childPosition);
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
        viewHolderNotes.popupMenuDots.setVisibility (View.INVISIBLE);
//            Toast.makeText(mContext, "mParam in adapt ="+mParam, Toast.LENGTH_SHORT).show();


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}
