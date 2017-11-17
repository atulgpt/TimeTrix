package com.atulgpt.www.timetrix.adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.atulgpt.www.timetrix.R;
import com.atulgpt.www.timetrix.utils.GlobalData;
import com.atulgpt.www.timetrix.utils.NoteUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by atulgupta on 02-05-2016 at 12:22 AM for TimeTrix .
 * Custom adapter for tag tab
 */
public class CustomAdapterTags extends BaseExpandableListAdapter {
    private final Context mContext;
    private int mSectionID;

    public CustomAdapterTags(Context context, int param) {
        this.mContext = context;
        this.mSectionID = param;
    }

    public void setFileID(int fileID) {
        this.mSectionID = fileID;
    }

    private static class ViewHolderNotes {
        final TextView notes;
        TextView date;
        final TextView title;
        Button popupMenuDots;

        ViewHolderNotes(View view) {
            notes = (TextView) view.findViewById (R.id.testViewNotes);
            date = (TextView) view.findViewById (R.id.testViewNotesDate);
            title = (TextView) view.findViewById (R.id.testViewNotesTitle);
            popupMenuDots = (Button) view.findViewById (R.id.btnVerticalMenu);
        }
    }

    @Override
    public int getGroupCount() {
        NoteUtil noteUtil = new NoteUtil (mContext);
        return noteUtil.getSubjectTagsCount (mSectionID);
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        NoteUtil noteUtil = new NoteUtil (mContext);
        String tag = null;
        try {
            tag = noteUtil.getDistinctTags (mSectionID).getJSONObject (groupPosition).
                    toString ();
        } catch (JSONException e) {
            e.printStackTrace ();
        }
        return noteUtil.getNotesForATagCount (mSectionID, tag);
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
        JSONArray tagList = noteUtil.getDistinctTags (mSectionID);
        TextView textView = (TextView) view.findViewById (R.id.textViewTagRow);
        try {
            textView.setText (tagList.getJSONObject (groupPosition).getString (GlobalData.NOTE_TAG_NAME));
            String tagColor = tagList.getJSONObject (groupPosition).getString (GlobalData.NOTE_TAG_COLOR);
            switch (tagColor) {
                case "Yellow":
                    textView.setTextColor (Color.YELLOW);
                    break;
                case "Red":
                    textView.setTextColor (Color.RED);
                    break;
                case "Green":
                    textView.setTextColor (Color.GREEN);
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace ();
        }
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
        JSONArray jsonArrayNotesTag = null;
        try {
            jsonArrayNotesTag = noteUtil.getNotesForATag (mSectionID, noteUtil
                    .getDistinctTags (mSectionID).getJSONObject (groupPosition).toString ());
            JSONObject jsonObject;
            String noteText = mContext.getString (R.string.lorem_ipsum_str), noteDateStamp = mContext.getString (R.string.lorem_ipsum_str), titleText = mContext.getString (R.string.lorem_ipsum_str);
            long noteTimeInMillis;
            jsonObject = jsonArrayNotesTag.getJSONObject (childPosition);
            noteText = jsonObject.getString (GlobalData.NOTE_BODY);
            noteTimeInMillis = jsonObject.getLong (GlobalData.NOTE_TIME_MILLIS);
            noteDateStamp = (String) DateUtils.getRelativeTimeSpanString (noteTimeInMillis, System.currentTimeMillis (), 3, DateUtils.FORMAT_ABBREV_RELATIVE);
            titleText = jsonObject.getString (GlobalData.NOTE_TITLE);
            viewHolderNotes.notes.setText (noteText);
            viewHolderNotes.date.setText (noteDateStamp);
            viewHolderNotes.title.setText (titleText);
        } catch (JSONException e) {
            e.printStackTrace ();
        }
        viewHolderNotes.popupMenuDots.setVisibility (View.INVISIBLE);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}
