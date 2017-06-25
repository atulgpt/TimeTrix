package com.atulgpt.www.timetrix.dataBinders;

import android.content.Context;

import com.atulgpt.www.timetrix.Adapters.DatabaseAdapter;
import com.atulgpt.www.timetrix.Utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by atulgupta on 18-08-2016 and 18 at 06:09 PM for TimeTrix .
 * For dataBinding the layout
 */
public class SubjectsDetailsBinder {
    private String mName;
    private String mProfName;
    private String mSubjectLocation;

    public SubjectsDetailsBinder(Context context, Long rowID) {
        DatabaseAdapter databaseAdapter = new DatabaseAdapter (context);
        JSONObject subjectJson = databaseAdapter.getSubjectBundle (rowID);
        try {
            this.mName = subjectJson.getString (Util.SUBJECT_NAME);
        } catch (JSONException e) {
            e.printStackTrace ();
        }
        try {
            this.mProfName = subjectJson.getString (Util.PROF_NAME);
        } catch (JSONException e) {
            e.printStackTrace ();
        }
        this.mSubjectLocation ="loc";
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getProfName() {
        return mProfName;
    }

    public void setProfName(String mCourseName) {
        this.mProfName = mCourseName;
    }

    public String getSubjectLocation() {
        return mSubjectLocation;
    }

    public void setSubjectLocation(String mSubjectLocation) {
        this.mSubjectLocation = mSubjectLocation;
    }
}
