package com.atulgpt.www.timetrix.dataBinders;

import android.content.Context;

import com.atulgpt.www.timetrix.adapters.DatabaseAdapter;
import com.atulgpt.www.timetrix.utils.GlobalData;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by atulgupta on 18-08-2016 and 18 at 06:09 PM for TimeTrix .
 * For dataBinding the layout
 */
public class SectionDetailsBinder {
    private String mName;
    private String mDescription;
    private String mSubjectLocation;
    private String mInputType;

    public SectionDetailsBinder(Context context, Long rowID) {
        DatabaseAdapter databaseAdapter = new DatabaseAdapter (context, null);
        JSONObject subjectJson = databaseAdapter.getSubjectBundle (rowID);
        try {
            this.mName = subjectJson.getString (GlobalData.SECTION_NAME);
        } catch (JSONException e) {
            e.printStackTrace ();
        }
        try {
            this.mDescription = subjectJson.getString (GlobalData.SECTION_DESCRIPTION);
        } catch (JSONException e) {
            e.printStackTrace ();
        }
        this.mSubjectLocation = "loc";
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getDescription() {
        return mDescription;
    }

    public void setDescription(String mCourseName) {
        this.mDescription = mCourseName;
    }

    public String getSubjectLocation() {
        return mSubjectLocation;
    }

    public void setSubjectLocation(String mSubjectLocation) {
        this.mSubjectLocation = mSubjectLocation;
    }

    public String getInputType() {
        return mInputType;
    }

    public void setInputType(String inputType) {
        this.mInputType = inputType;
    }
}
