package com.atulgpt.www.timetrix.adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.atulgpt.www.timetrix.R;

import java.util.ArrayList;

/**
 * Created by Atul Gupta on 25-02-2016 at 05:29 PM at 01:59 AM for TimeTrix .
 * Custom adapter for the list view
 */

public class CustomAdapter extends BaseAdapter implements ListAdapter {
    private static final String TAG = CustomAdapter.class.getSimpleName ();
    private static final boolean DEBUG = true;
    private ArrayList<String> mArrayList = new ArrayList<> ();
    private final Context mContext;
    private String mParam;
    private Handler mHandler = null;
    private String fileID;

    public CustomAdapter(ArrayList<String> arrayList, Context context, String param, Handler handler) {
        this.mArrayList = arrayList;
        this.mContext = context;
        this.mParam = param;
        this.mHandler = handler;
    }

    @Override
    public int getCount() {
        return mArrayList.size ();
    }

    @Override
    public Object getItem(int position) {
        return mArrayList.get (position);
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
            view = inflater.inflate (R.layout.custom_row_nav_item, parent, false);
            TextView subjectName = (TextView) view.findViewById (R.id.textViewRow);
            Typeface typeface = Typeface.createFromAsset (mContext.getAssets (), "font/Roboto_Medium.ttf");
            subjectName.setTypeface (typeface);
            subjectName.setText (mArrayList.get (position));
            ImageView drawerItemIcon = (ImageView) view.findViewById (R.id.listIcon);
            if (mArrayList.get (position).equals (mContext.getString (R.string.add_section_str))) {
                drawerItemIcon.setImageResource (R.drawable.ic_add_circle_outline_black_24dp);
            }
            ListView listView = (ListView) parent;
            if (position == listView.getCheckedItemPosition () - 1) {  // - 1 for header adjustment
                view.setBackgroundResource (R.color.highlight_grey);
                drawerItemIcon.setColorFilter (mContext.getResources ().getColor (R.color.accentColor));
                subjectName.setTextColor (mContext.getResources ().getColor (R.color.accentColor));
                if (DEBUG) Log.d (TAG, "getView: pos = " + position);
            }
            Log.d (TAG, "getView: view.getParent() = " + view.getParent () + " parent = " + parent);
        }
        return view;
    }

    public interface OnListAdapterInteractionListener {
        void onListAdapterInteractionListener(String data1, String data2);
    }

    private class ViewHolder {
        private TextView mSectionName;
        private ImageView mSectionIcon;

    }
}
