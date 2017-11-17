package com.atulgpt.www.timetrix.utils;

import android.content.Context;
import android.util.Log;

import com.atulgpt.www.timetrix.adapters.DatabaseAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by atulgupta on 07-04-2016 at 11:47 PM.
 * Mainly deals with different notes options
 */
public class NoteUtil {
    private static final String TAG = NoteUtil.class.getSimpleName ();
    private static final boolean DEBUG = true;
    private final Context mContext;

    public NoteUtil(Context context) {
        mContext = context;
    }

    private Boolean setNote(int sectionID, String notes) {
        if (sectionID < 0)
            return false;
        DatabaseAdapter databaseAdapter = new DatabaseAdapter (mContext, null);
        return databaseAdapter.setNote (sectionID, notes);
    }

    public JSONArray getNoteJSONArray(int sectionID) {
        if (sectionID < 0)
            return null;
        DatabaseAdapter databaseAdapter = new DatabaseAdapter (mContext, null);
        String allNote = databaseAdapter.getNotesForSection (sectionID);
        try {
            if (allNote != null)
                return new JSONArray (allNote);
        } catch (JSONException e) {
            e.printStackTrace ();
        }
        return new JSONArray ();
    }

    public JSONObject deleteNote(long notePosition, int sectionID) {
        if (notePosition < 0 || sectionID < 0) {
            return null;
        }
        JSONObject deletedNote = null;
        JSONArray jsonArray = getNoteJSONArray (sectionID);
        JSONArray jsonArrayNew = new JSONArray ();
        int len, offset = 0;
        len = jsonArray.length ();
        for (int i = 0; i < len; i++) {
            //Excluding the item at position
            if (i != notePosition) {
                try {
                    JSONObject tempJSONObject = jsonArray.getJSONObject (i);
                    tempJSONObject.put (GlobalData.NOTE_INDEX, i + offset);
                    jsonArrayNew.put (jsonArray.get (i));
                } catch (JSONException e) {
                    e.printStackTrace ();
                }
            } else {
                try {
                    deletedNote = jsonArray.getJSONObject (i);
                    offset = -1;
                } catch (JSONException e) {
                    e.printStackTrace ();
                }
            }
        }
        setNote (sectionID, jsonArrayNew.toString ());
        return deletedNote;
    }

    public Boolean addNoteAtPosition(long notePosition, JSONObject note, int sectionID) {
        if (notePosition < 0 || sectionID < 0)
            return false;
        JSONArray jsonArray = getNoteJSONArray (sectionID);
        JSONArray jsonArrayNew = new JSONArray ();
        int len, offset = 0;
        len = jsonArray.length ();
        for (int i = 0; i < len + 1; i++) {
            if (i != notePosition) {
                try {
                    JSONObject tempJSONObject = jsonArray.getJSONObject (i + offset);
                    tempJSONObject.put (GlobalData.NOTE_INDEX, i);
                    jsonArrayNew.put (tempJSONObject);
                } catch (JSONException e) {
                    e.printStackTrace ();
                }
            } else {
                try {
                    note.put (GlobalData.NOTE_INDEX, i);
                    jsonArrayNew.put (i, note);
                } catch (JSONException e) {
                    e.printStackTrace ();
                }
                offset = -1;
            }
        }
        return setNote (sectionID, jsonArrayNew.toString ());
    }

    public Boolean editNote(long notePosition, int sectionID, String note, String title) {
        if (notePosition < 0 || sectionID < 0)
            return false;
        if (note.trim ().isEmpty () && title.trim ().isEmpty ()) {
            return true;
        }
        JSONObject jsonObject;
        JSONArray jsonArray;
        jsonArray = getNoteJSONArray (sectionID);
        try {
            jsonObject = jsonArray.getJSONObject ((int) notePosition);
            jsonObject.put (GlobalData.NOTE_TITLE, title);
            jsonObject.put (GlobalData.NOTE_BODY, note);
            jsonArray.put ((int) notePosition, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace ();
            return false;
        }
        return setNote (sectionID, jsonArray.toString ());
    }

    public Boolean addOrRemoveStar(long notePosition, int sectionID) {
        if (notePosition < 0 || sectionID < 0)
            return false;
        JSONArray jsonArray = getNoteJSONArray (sectionID);
        JSONObject jsonObject = null;
        //Toast.makeText(mContext, "" + jsonArray.toString(), Toast.LENGTH_LONG).show();
        try {
            jsonObject = jsonArray.getJSONObject ((int) notePosition);
        } catch (JSONException e) {
            e.printStackTrace ();
        }
        try {
            if (jsonObject != null) {
                if (jsonObject.getBoolean (GlobalData.NOTE_IS_STAR))
                    jsonObject.put (GlobalData.NOTE_IS_STAR, false);
                else jsonObject.put (GlobalData.NOTE_IS_STAR, true);
            }
        } catch (JSONException e) {
            e.printStackTrace ();
        }
//        Toast.makeText(mContext, "" + jsonArray.toString(), Toast.LENGTH_LONG).show();
        return setNote (sectionID, jsonArray.toString ());
    }

    public Boolean getStarStatus(long notePosition, int sectionID) {
        if (notePosition < 0 || sectionID < 0)
            return false;
        JSONArray jsonArray = getNoteJSONArray (sectionID);
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonArray.getJSONObject ((int) notePosition);
        } catch (JSONException e) {
            e.printStackTrace ();
        }
        try {
            if (jsonObject != null) {
                return jsonObject.getBoolean (GlobalData.NOTE_IS_STAR);
            }
        } catch (JSONException e) {
            e.printStackTrace ();
        }
        return false;
    }

    public Boolean addTag(long notePosition, int sectionID, String tag, String color) {
        if (notePosition < 0 || sectionID < 0)
            return false;
        JSONArray jsonArrayTag = new JSONArray ();
        JSONObject jsonObjectTag = new JSONObject ();
        try {
            jsonObjectTag.put (GlobalData.NOTE_TAG_NAME, tag);
            jsonObjectTag.put (GlobalData.NOTE_TAG_COLOR, color);
        } catch (JSONException e) {
            e.printStackTrace ();
            if (DEBUG) Log.d (TAG, "addTag " + e);
        }
        JSONArray jsonArrayNote = getNoteJSONArray (sectionID);
        if (jsonArrayNote == null) {
            return false;
        }
        JSONObject jsonNote = null;
        try {
            jsonNote = jsonArrayNote.getJSONObject ((int) notePosition);
        } catch (JSONException e) {
            e.printStackTrace ();
            if (DEBUG) Log.d (TAG, "addTag " + e);
        }
        try {
            if (jsonNote != null) {
                jsonArrayTag = jsonNote.getJSONArray (GlobalData.NOTE_TAG_ARRAY);
            }

        } catch (JSONException e) {
            e.printStackTrace ();
            if (DEBUG) Log.d (TAG, "addTag " + e);
        }
        if (!jsonArrayTag.toString ().contains (jsonObjectTag.toString ()))
            jsonArrayTag.put (jsonObjectTag);
        try {
            if (jsonNote != null) {
                jsonNote.put (GlobalData.NOTE_TAG_ARRAY, jsonArrayTag);
            }
        } catch (JSONException e) {
            e.printStackTrace ();
            if (DEBUG) Log.d (TAG, "addTag " + e);
        }
        try {
            jsonArrayNote.put ((int) notePosition, jsonNote);
        } catch (JSONException e) {
            e.printStackTrace ();
            if (DEBUG) Log.d (TAG, "addTag " + e);
        }
        return setNote (sectionID, jsonArrayNote.toString ());
    }

    private JSONArray getTotalTags(int sectionID) { // FIXME: 07-05-2016 jsonArray should return unique tags and avoid repetition
        JSONArray jsonArray;
        jsonArray = getNoteJSONArray (sectionID);
        JSONObject jsonObject = null;
        int noOfNotes = jsonArray.length ();
        JSONArray jsonArrayTags = new JSONArray ();
        for (int i = 0; i < noOfNotes; i++) {
            try {
                jsonObject = jsonArray.getJSONObject (i);
            } catch (JSONException e) {
                e.printStackTrace ();
                if (DEBUG) Log.d (TAG, "1. getTotalTags " + e);
            }

            if (jsonObject != null) {
                try {
                    if (jsonObject.has (GlobalData.NOTE_TAG_ARRAY)) {
                        jsonArrayTags.put (jsonObject.getJSONArray (GlobalData.NOTE_TAG_ARRAY));
                    }
                } catch (JSONException e) {
                    e.printStackTrace ();
                    if (DEBUG) Log.d (TAG, "2. getTotalTags " + e + jsonObject);
//                    if (DEBUG)
//                        Toast.makeText (mContext, "getTotalTags " + e, Toast.LENGTH_SHORT).show ();
                }
            }
        }
        return jsonArrayTags;
    }

    public ArrayList<String> getDistinctTagsString(int sectionID) {
        ArrayList<String> tagStringList = new ArrayList<> (5);
        JSONArray jsonArray = getTotalTags (sectionID);
        for (int i = 0; i < jsonArray.length (); i++) {
            JSONArray jsonObjectArray = null;
            try {
                jsonObjectArray = (JSONArray) jsonArray.get (i);
            } catch (JSONException e) {
                e.printStackTrace ();
                if (DEBUG) Log.d (TAG, "1. getDistinctTagsString " + e);
            }
            try {
                if (jsonObjectArray != null) {
                    for (int j = 0; j < jsonObjectArray.length (); j++) {
                        JSONObject jsonObjectTag = jsonObjectArray.getJSONObject (j);
                        if (jsonObjectTag != null && !tagStringList.contains (jsonObjectTag.getString (GlobalData.NOTE_TAG_NAME))) {
                            tagStringList.add (jsonObjectTag.getString (GlobalData.NOTE_TAG_NAME));
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace ();
                if (DEBUG) Log.d (TAG, "2. getDistinctTagsString " + e + jsonObjectArray);
            }
        }
        return tagStringList;
    }

    public JSONArray getDistinctTags(int sectionID) {
        JSONArray tagJSONArray = new JSONArray ();
        JSONArray jsonArray = getTotalTags (sectionID);
        for (int i = 0; i < jsonArray.length (); i++) {
            JSONArray jsonArrayTagsForNote = null;
            try {
                jsonArrayTagsForNote = (JSONArray) jsonArray.get (i);
                for (int j = 0; j < jsonArrayTagsForNote.length (); j++) {
                    JSONObject jsonObjectTag = jsonArrayTagsForNote.getJSONObject (j);
                    if (jsonObjectTag != null && !tagJSONArray.toString ().contains (jsonObjectTag.toString ())) {
                        tagJSONArray.put (jsonObjectTag);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace ();
                if (DEBUG) Log.d (TAG, "getDistinctTags" + e + jsonArrayTagsForNote);
            }
        }
        Log.d (TAG, "getDistinctTags: tagArray" + tagJSONArray);
        return tagJSONArray;
    }

    public int getSubjectTagsCount(int sectionID) {
        return getDistinctTags (sectionID).length ();
    }

    public JSONArray getNotesForATag(int sectionID, String tagObject) {
        JSONArray jsonArrayNotes = getNoteJSONArray (sectionID);
        JSONArray outJSONArrayNotes = new JSONArray ();
        for (int i = 0; i < jsonArrayNotes.length (); i++) {
            JSONObject jsonObjectNote;
            try {
                jsonObjectNote = jsonArrayNotes.getJSONObject (i);
                JSONArray jsonArrayTagArray;
                if (jsonObjectNote.has (GlobalData.NOTE_TAG_ARRAY)) {
                    jsonArrayTagArray = jsonObjectNote.getJSONArray (GlobalData.NOTE_TAG_ARRAY);
                    for (int j = 0; j < jsonArrayTagArray.length (); j++) {
                        JSONObject jsonObjectTag;
                        jsonObjectTag = jsonArrayTagArray.getJSONObject (j);
                        if (jsonObjectTag != null) {
                            if (jsonObjectTag.toString ().equals (tagObject)) {
                                outJSONArrayNotes.put (jsonObjectNote);
                                break;
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace ();
            }
        }
        return outJSONArrayNotes;
    }

    public int getNotesForATagCount(int sectionID, String tagObject) {
        return getNotesForATag (sectionID, tagObject).length ();
    }
}
