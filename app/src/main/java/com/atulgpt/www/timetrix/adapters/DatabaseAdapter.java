package com.atulgpt.www.timetrix.adapters;
/**
 * Created by atulgupta on 13-06-2015 at 12:59 PM for TimeTrix at 01:18 PM for TimeTrix .
 * Manages database(SQLite) interaction
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.atulgpt.www.timetrix.utils.GlobalData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;


public class DatabaseAdapter {
    private static final boolean DEBUG = true;
    private static final String TAG = DatabaseHelper.class.getSimpleName ();
    private DatabaseHelper mDatabaseHelper;
    private Context mContext;
    private DatabaseAdapterListener mDatabaseAdapterListener;

    public DatabaseAdapter(Context context, DatabaseAdapterListener databaseAdapterListener) {
        mDatabaseHelper = new DatabaseHelper (context);
        mContext = context;
        mDatabaseAdapterListener = databaseAdapterListener;
    }

    public void addSection(String subjectName, String profName) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase ();
        ContentValues values = new ContentValues ();
        values.put (DatabaseHelper.KEY_SEC_NAME, subjectName);
        values.put (DatabaseHelper.KEY_PROF_NAME, profName);
        /* Inserting Row */
        Long result = db.insert (DatabaseHelper.TABLE_NAME, null, values);
        db.close (); /* Closing database connection */
    }

//    public void deleteTable(Order order) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        db.delete(TABLE_NAME, KEY_ID + " = ?",
//                new String[]{String.valueOf(order.getID())});
//        db.close();
//    }

    public int countRows() {
        String countQuery = "SELECT  * FROM " + DatabaseHelper.TABLE_NAME;
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase ();
        Cursor cursor = db.rawQuery (countQuery, null);
        int count = cursor.getCount ();
        cursor.close ();
        db.close ();
        return count; //return total no of rows in the database
    }

    public ArrayList<String> getAllSectionNames() {
        ArrayList<String> arrayList = new ArrayList<> ();
        String query = "SELECT  * FROM " + DatabaseHelper.TABLE_NAME;
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase ();
        Cursor cursor = db.rawQuery (query, null);
        if (cursor.getCount () != 0) {
            while (cursor.moveToNext ()) {
                arrayList.add (cursor.getString (1));
            }
        }
        cursor.close ();
        db.close ();
        return arrayList;
    }

    public String getNotesForSection(int sectionNo) {
        int UID = getUIDFromRowCount (sectionNo);
        if (DEBUG) Log.d (TAG, "getNotesForSection: rowCount: " + sectionNo);
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase ();
        String from[] = {DatabaseHelper.KEY_NOTES};
        //String[] whereArgs = new String[]{index+""};
        Cursor cursor = db.query (DatabaseHelper.TABLE_NAME, from, DatabaseHelper.KEY_ID + " = '" + UID + "'", null, null, null, null);
        if (cursor.moveToFirst ()) {
//            Toast.makeText(mContext,"index = "+index+" cursor val = "+cursor.getString(index),Toast.LENGTH_LONG).show();
//            Toast.makeText(mContext,"index = "+index+" cursor count = "+cursor.getCount()+"id = "+id,Toast.LENGTH_LONG).show();
            String temp = cursor.getString (cursor.getColumnIndex (DatabaseHelper.KEY_NOTES));
            cursor.close ();
            db.close ();
            if (temp != null)
                return temp;
        }
        return "[]";
    }

    public void getNotesForSectionInBackground(int sectionNo, String query) {
        DatabaseBackgroundOperation backgroundOperation = new DatabaseBackgroundOperation ();
        String[] params = {"getNotesForSection", String.valueOf (sectionNo), query};
        backgroundOperation.execute (params);
    }

    public JSONObject getSubjectBundle(long rowCount) {
        if (DEBUG) Log.d (TAG, "getSubjectBundle: rowCount: " + rowCount);
        int UID = getUIDFromRowCount ((int) rowCount);
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase ();
        //String[] whereArgs = new String[]{index+""};
        JSONObject jsonObject = new JSONObject ();
        Cursor cursor = db.query (DatabaseHelper.TABLE_NAME, null, DatabaseHelper.KEY_ID + " = '" + UID + "'", null, null, null, null);
        if (cursor.moveToFirst ()) {
//            Toast.makeText(mContext,"index = "+index+" cursor val = "+cursor.getString(index),Toast.LENGTH_LONG).show();
//            Toast.makeText(mContext,"index = "+index+" cursor count = "+cursor.getCount()+"id = "+id,Toast.LENGTH_LONG).show();
            String temp = cursor.getString (cursor.getColumnIndex (DatabaseHelper.KEY_SEC_NAME));
            try {
                jsonObject.put (GlobalData.SECTION_NAME, temp);
            } catch (JSONException e) {
                e.printStackTrace ();
            }
            temp = cursor.getString (cursor.getColumnIndex (DatabaseHelper.KEY_PROF_NAME));
            try {
                jsonObject.put (GlobalData.SECTION_DESCRIPTION, temp);
            } catch (JSONException e) {
                e.printStackTrace ();
            }
            cursor.close ();
            db.close ();
        }
        return jsonObject;
    }

    public Boolean setNote(int rowCount, String data) {
        int UID = getUIDFromRowCount (rowCount);
        String where = DatabaseHelper.KEY_ID + " = " + UID;
        ContentValues contentValues = new ContentValues ();
        contentValues.put (DatabaseHelper.KEY_NOTES, data);
        SQLiteDatabase sqLiteDatabase = mDatabaseHelper.getWritableDatabase ();
        Boolean result = sqLiteDatabase.update (DatabaseHelper.TABLE_NAME, contentValues, where, null) != 0;
        sqLiteDatabase.close ();
        if (DEBUG) Log.d (TAG, "setNote resultOfOperation = " + result);
        return result;
    }

    public Boolean editSection(int sectionID, String sectionName, String sectionDescription) {
        String where = DatabaseHelper.KEY_ID + " = " + getUIDFromRowCount (sectionID);
        ContentValues contentValues = new ContentValues ();
        contentValues.put (DatabaseHelper.KEY_SEC_NAME, sectionName);
        contentValues.put (DatabaseHelper.KEY_PROF_NAME, sectionDescription);
        SQLiteDatabase sqLiteDatabase = mDatabaseHelper.getWritableDatabase ();
        Boolean result = sqLiteDatabase.update (DatabaseHelper.TABLE_NAME, contentValues, where, null) != 0;
        sqLiteDatabase.close ();
        return result;
    }

    public Boolean deleteSubject(long rowCount) {
        int UID = getUIDFromRowCount (rowCount);
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase ();
        db.delete (DatabaseHelper.TABLE_NAME, DatabaseHelper.KEY_ID + " = " + UID, null);
        if (DEBUG) Log.d (TAG, "deleteSubject: subjectID = " + UID);
        return false;
    }

    public Boolean deleteDatabase() {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase ();
        Boolean result = db.delete (DatabaseHelper.TABLE_NAME, null,
                null) != 0;
        db.getPath ();
        //Toast.makeText (mContext, "path = "+db.getPath (), Toast.LENGTH_SHORT).show ();
        //SQLiteDatabase.deleteDatabase (db.getPath ());
        File fileDataBase = new File (db.getPath ());
        SQLiteDatabase.deleteDatabase (fileDataBase);
        db.close ();
        return result;
    }

    private int getUIDFromRowCount(long rowCount) {
        int UID = -1;
        int intRowCount = (int) rowCount;
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase ();
        Cursor cursor = db.query (DatabaseHelper.TABLE_NAME, null, null, null, null, null, null);
        Log.d (TAG, "getUIDFromRowCount: rowCount = "+rowCount);
        if (cursor.getCount () != 0) {
            cursor.moveToPosition (intRowCount - 1);
            UID = cursor.getInt (cursor.getColumnIndex (DatabaseHelper.KEY_ID));
        }
        cursor.close ();
        db.close ();
        return UID;
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String KEY_ID = "_id";  /* will contain integer */
        private static final String KEY_SEC_NAME = "subject_name";  /* will contain string */
        private static final String KEY_PROF_NAME = "prof_name";   /* will contain string */
        //private static final String KEY_STATUS = "status";  /* will contain integer */
        private static final String KEY_NOTES = "notes";  /* will contain string */

        private static final String DATABASE_NAME = "Subjects";
        private static final String TABLE_NAME = "Subject_name";
        private static final int DATABASE_VERSION = 3;
        private static final String DATABASE_CREATE =
                String.format ("create table if not exists %s (%s integer primary key autoincrement,%s VARCHAR not null,%s integer not null,%s TEXT);", TABLE_NAME, KEY_ID, KEY_SEC_NAME, KEY_PROF_NAME, KEY_NOTES);
        private static final String DATABASE_DROP = "DROP TABLE IF EXISTS ";

        private DatabaseHelper(Context context) {
            super (context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL (DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w (DatabaseAdapter.class.getName (),
                    "Upgrading database from version " + oldVersion + " to "
                            + newVersion + ", which will destroy all old data");
            db.execSQL (DATABASE_DROP + TABLE_NAME);
            onCreate (db);
        }
    }

    private class DatabaseBackgroundOperation extends AsyncTask<String, Integer, JSONObject> {

        JSONObject resultJSON = new JSONObject ();
        String[] params;

        /**
         * Override this method to perform a computation on a background thread. The
         * specified parameters are the parameters passed to {@link #execute}
         * by the caller of this task.
         * <p>
         * This method can call {@link #publishProgress} to publish updates
         * on the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected JSONObject doInBackground(String... params) {
            this.params = params;
            if (params[0].equals ("getNotesForSection")) {
                String notes;
                notes = DatabaseAdapter.this.getNotesForSection (Integer.parseInt (params[1]));
                try {
                    resultJSON.put (GlobalData.STATUS, GlobalData.STATUS_OK);
                    resultJSON.put (GlobalData.DATA, notes);
                    return resultJSON;
                } catch (JSONException e) {
                    e.printStackTrace ();
                }
            }
            return null;
        }

        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The
         * specified result is the value returned by {@link #doInBackground}.</p>
         * <p>
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param jsonObject The result of the operation computed by {@link #doInBackground}.
         * @see #onPreExecute
         * @see #doInBackground
         * @see #onCancelled(Object)
         */
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute (jsonObject);
            try {
                if (jsonObject.getString (GlobalData.STATUS).equals (GlobalData.STATUS_OK)) {
                    mDatabaseAdapterListener.populateListViewData (jsonObject.getString (GlobalData.DATA), params[2]);
                }
            } catch (JSONException e) {
                e.printStackTrace ();
                Log.d (TAG, "onPostExecute: error message in JSON = " + e.getMessage ());
            }
        }
    }

    public interface DatabaseAdapterListener {
        void populateListViewData(String data, String query);
    }
}

