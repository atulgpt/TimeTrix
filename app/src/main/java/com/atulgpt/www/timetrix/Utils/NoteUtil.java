package com.atulgpt.www.timetrix.Utils;

import android.content.Context;
import android.util.Log;

import com.atulgpt.www.timetrix.Adapters.DatabaseAdapter;

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

    public Boolean setNote(long subjectID, String notes) {
        if (subjectID < 0)
            return false;
        DatabaseAdapter databaseAdapter = new DatabaseAdapter (mContext);
        return databaseAdapter.setNote (subjectID, notes);
    }

    public JSONArray getNoteJSONArray(long subjectID) {
        if (subjectID < 0)
            return null;
        DatabaseAdapter databaseAdapter = new DatabaseAdapter (mContext);
        String allNote = databaseAdapter.getNote (subjectID);
        try {
            if (allNote != null)
                return new JSONArray (allNote);
        } catch (JSONException e) {
            e.printStackTrace ();
        }
        return new JSONArray ();
    }

    public JSONObject deleteNote(long notePosition, long subjectID) {
        if (notePosition < 0 || subjectID < 0) {
            return null;
        }
        JSONObject deletedNote = null;
        JSONArray jsonArray = getNoteJSONArray (subjectID);
        JSONArray jsonArrayNew = new JSONArray ();
        int len, offset = 0;
        len = jsonArray.length ();
        for (int i = 0; i < len; i++) {
            //Excluding the item at position
            if (i != notePosition) {
                try {
                    JSONObject tempJSONObject = jsonArray.getJSONObject (i);
                    tempJSONObject.put (Util.NOTE_INDEX, i + offset);
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
        setNote (subjectID, jsonArrayNew.toString ());
        return deletedNote;
    }

    public Boolean addNoteAtAPosition(long notePosition, JSONObject note, long subjectID) {
        if (notePosition < 0 || subjectID < 0)
            return false;
        JSONArray jsonArray = getNoteJSONArray (subjectID);
        JSONArray jsonArrayNew = new JSONArray ();
        int len, offset = 0;
        len = jsonArray.length ();
        for (int i = 0; i < len + 1; i++) {
            if (i != notePosition) {
                try {
                    JSONObject tempJSONObject = jsonArray.getJSONObject (i + offset);
                    tempJSONObject.put (Util.NOTE_INDEX, i);
                    jsonArrayNew.put (tempJSONObject);
                } catch (JSONException e) {
                    e.printStackTrace ();
                }
            } else {
                try {
                    note.put (Util.NOTE_INDEX, i);
                    jsonArrayNew.put (i, note);
                } catch (JSONException e) {
                    e.printStackTrace ();
                }
                offset = -1;
            }
        }
        return setNote (subjectID, jsonArrayNew.toString ());
    }

    public Boolean editNote(long notePosition, long subjectID, String note, String title) {
        if (notePosition < 0 || subjectID < 0)
            return false;
        if (note.trim ().isEmpty () && title.trim ().isEmpty ()) {
            return true;
        }
        JSONObject jsonObject;
        JSONArray jsonArray;
        jsonArray = getNoteJSONArray (subjectID);
        try {
            jsonObject = jsonArray.getJSONObject ((int) notePosition);
            jsonObject.put (Util.NOTE_TITLE, title);
            jsonObject.put (Util.NOTE_BODY, note);
            jsonArray.put ((int) notePosition, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace ();
            return false;
        }
        return setNote (subjectID, jsonArray.toString ());
    }

    public Boolean addOrRemoveStar(long notePosition, long subjectID) {
        if (notePosition < 0 || subjectID < 0)
            return false;
        JSONArray jsonArray = getNoteJSONArray (subjectID);
        JSONObject jsonObject = null;
        //Toast.makeText(mContext, "" + jsonArray.toString(), Toast.LENGTH_LONG).show();
        try {
            jsonObject = jsonArray.getJSONObject ((int) notePosition);
        } catch (JSONException e) {
            e.printStackTrace ();
        }
        try {
            if (jsonObject != null) {
                if (jsonObject.getBoolean (Util.NOTE_IS_STAR))
                    jsonObject.put (Util.NOTE_IS_STAR, false);
                else jsonObject.put (Util.NOTE_IS_STAR, true);
            }
        } catch (JSONException e) {
            e.printStackTrace ();
        }
//        Toast.makeText(mContext, "" + jsonArray.toString(), Toast.LENGTH_LONG).show();
        return setNote (subjectID, jsonArray.toString ());
    }

    public Boolean getStarStatus(long notePosition, long subjectID) {
        if (notePosition < 0 || subjectID < 0)
            return false;
        JSONArray jsonArray = getNoteJSONArray (subjectID);
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonArray.getJSONObject ((int) notePosition);
        } catch (JSONException e) {
            e.printStackTrace ();
        }
        try {
            if (jsonObject != null) {
                return jsonObject.getBoolean (Util.NOTE_IS_STAR);
            }
        } catch (JSONException e) {
            e.printStackTrace ();
        }
        return false;
    }

    public Boolean addTag(long notePosition, long subjectID, String tag, String color) {
        if (notePosition < 0 || subjectID < 0)
            return false;
        JSONArray jsonArrayTag = new JSONArray ();
        JSONObject jsonObjectTag = new JSONObject ();
        try {
            jsonObjectTag.put (Util.NOTE_TAG_NAME, tag);
            jsonObjectTag.put (Util.NOTE_TAG_COLOR, color);
        } catch (JSONException e) {
            e.printStackTrace ();
            if (DEBUG) Log.d (TAG, "addTag " + e);
        }
        JSONArray jsonArray = getNoteJSONArray (subjectID);
        if (jsonArray == null) {
            return false;
        }
        JSONObject jsonObject = null;
        try {
            jsonObject = jsonArray.getJSONObject ((int) notePosition);
        } catch (JSONException e) {
            e.printStackTrace ();
            if (DEBUG) Log.d (TAG, "addTag " + e);
        }
        try {
            if (jsonObject != null) {
                jsonArrayTag = jsonObject.getJSONArray (Util.NOTE_TAG_ARRAY);
            }

        } catch (JSONException e) {
            e.printStackTrace ();
            if (DEBUG) Log.d (TAG, "addTag " + e);
        }
        jsonArrayTag.put (jsonObjectTag);
        try {
            if (jsonObject != null) {
                jsonObject.put (Util.NOTE_TAG_ARRAY, jsonArrayTag);
            }
        } catch (JSONException e) {
            e.printStackTrace ();
            if (DEBUG) Log.d (TAG, "addTag " + e);
        }
        try {
            jsonArray.put ((int) notePosition, jsonObject);
        } catch (JSONException e) {
            e.printStackTrace ();
            if (DEBUG) Log.d (TAG, "addTag " + e);
        }
        return setNote (subjectID, jsonArray.toString ());
    }

    public JSONArray getTotalTags(long subjectID) { // FIXME: 07-05-2016 jsonArray should return unique tags and avoid repetition
        JSONArray jsonArray;
        jsonArray = getNoteJSONArray (subjectID);
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
                    jsonArrayTags.put (jsonObject.getJSONArray (Util.NOTE_TAG_ARRAY));
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

    public ArrayList<String> getTotalTagsString(long subjectID) {
        ArrayList<String> tagStringList = new ArrayList<> (5);
        JSONArray jsonArray = getTotalTags (subjectID);
        for (int i = 0; i < jsonArray.length (); i++) {
            JSONArray jsonObjectArray = null;
            try {
                jsonObjectArray = (JSONArray) jsonArray.get (i);
            } catch (JSONException e) {
                e.printStackTrace ();
                if (DEBUG) Log.d (TAG, "1. getTotalTagsString " + e);
            }
            try {
                if (jsonObjectArray != null) {
                    for (int j = 0; j < jsonObjectArray.length (); j++) {
                        JSONObject jsonObjectTag = jsonObjectArray.getJSONObject (j);
                        if (jsonObjectTag != null && !tagStringList.contains (jsonObjectTag.getString (Util.NOTE_TAG_NAME))) {
                            tagStringList.add (jsonObjectTag.getString (Util.NOTE_TAG_NAME));
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace ();
                if (DEBUG) Log.d (TAG, "2. getTotalTagsString " + e + jsonObjectArray);
            }
        }
        return tagStringList;
    }

    public int getSubjectTagsCount(long subjectID) {
        return getTotalTagsString (subjectID).size ();
    }

    public JSONArray getNotesForATag(long subjectID, String tag){
        JSONArray jsonArray = getNoteJSONArray (subjectID);
        JSONArray outJSONArrayNotes = new JSONArray ();

        for (int i = 0; i < jsonArray.length (); i++) {
            JSONObject jsonObjectNote = null;
            try {
                jsonObjectNote = jsonArray.getJSONObject (i);
            } catch (JSONException e) {
                e.printStackTrace ();
                if(DEBUG) Log.d (TAG, "getNotesForATag "+e);
            }

            JSONArray jsonArrayTagArray = null;
            try {
                if (jsonObjectNote != null) {
                    jsonArrayTagArray = jsonObjectNote.getJSONArray (Util.NOTE_TAG_ARRAY);
                }
            } catch (JSONException e) {
                e.printStackTrace ();
                if(DEBUG) Log.d (TAG, "getNotesForATag "+e);
            }
            if (jsonArrayTagArray != null) {

                for (int j = 0; j < jsonArrayTagArray.length (); j++) {
                    JSONObject jsonObjectTag = null;
                    try {

                        jsonObjectTag = jsonArrayTagArray.getJSONObject (j);
                    } catch (JSONException e) {
                        e.printStackTrace ();
                        if(DEBUG) Log.d (TAG, "getNotesForATag "+e);
                    }
                    try {
                        if (jsonObjectTag != null) {
                            if(jsonObjectTag.getString (Util.NOTE_TAG_NAME).equals (tag)){
                                outJSONArrayNotes.put (jsonObjectNote);
                                break;
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace ();
                        if(DEBUG) Log.d (TAG, "getNotesForATag "+e);
                    }
                }
            }
        }
        return outJSONArrayNotes;
    }
    public int getNotesForATagCount(long subjectID, String tag){
        return getNotesForATag (subjectID,tag).length ();
    }
}
