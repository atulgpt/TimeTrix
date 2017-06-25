package com.atulgpt.www.timetrix.Adapters;
/**
 * Created by atulgupta on 13-06-2015 at 12:59 PM for TimeTrix at 01:18 PM for TimeTrix .
 * Manages database(SQLite) interaction
 */

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.atulgpt.www.timetrix.Utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class DatabaseAdapter {
    private static final boolean DEBUG = true;
    private static final String TAG = DatabaseHelper.class.getSimpleName ();
    private DatabaseHelper mDatabaseHelper;
    private Context mContext;

    public DatabaseAdapter(Context context) {
        mDatabaseHelper = new DatabaseHelper (context);
        mContext = context;
    }

    public void addTable(String subjectName, String profName) {
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase ();
        ContentValues values = new ContentValues ();
        values.put (DatabaseHelper.KEY_SUB_NAME, subjectName);
        values.put (DatabaseHelper.KEY_PROF_NAME, profName);
        /* Inserting Row */
        Long result = db.insert (DatabaseHelper.TABLE_NAME, null, values);
//        Toast.makeText(this.mContext,"long result = "+result,Toast.LENGTH_LONG).show ();
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

    public ArrayList<String> getAllData() {
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
        return arrayList;
    }

    public String getNote(long rowCount) {
        int UID = getUIDFromRowCount ((int) rowCount);
        if (DEBUG) Log.d (TAG, "getNote: rowCount: " + rowCount);
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
            return temp;
        }
        return "[]";
    }

    public JSONObject getSubjectBundle(long rowCount) {
        if (DEBUG) Log.d (TAG, "getSubjectBundle: rowCount: "+rowCount);
        int UID = getUIDFromRowCount ((int) rowCount);
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase ();
        //String[] whereArgs = new String[]{index+""};
        JSONObject jsonObject = new JSONObject ();
        Cursor cursor = db.query (DatabaseHelper.TABLE_NAME, null, DatabaseHelper.KEY_ID + " = '" + UID + "'", null, null, null, null);
        if (cursor.moveToFirst ()) {
//            Toast.makeText(mContext,"index = "+index+" cursor val = "+cursor.getString(index),Toast.LENGTH_LONG).show();
//            Toast.makeText(mContext,"index = "+index+" cursor count = "+cursor.getCount()+"id = "+id,Toast.LENGTH_LONG).show();
            String temp = cursor.getString (cursor.getColumnIndex (DatabaseHelper.KEY_SUB_NAME));
            try {
                jsonObject.put (Util.SUBJECT_NAME, temp);
            } catch (JSONException e) {
                e.printStackTrace ();
            }
            temp = cursor.getString (cursor.getColumnIndex (DatabaseHelper.KEY_PROF_NAME));
            try {
                jsonObject.put (Util.PROF_NAME, temp);
            } catch (JSONException e) {
                e.printStackTrace ();
            }
            cursor.close ();
            db.close ();
        }
        return jsonObject;
    }

    public Boolean setNote(long rowCount, String data) {
        int UID = getUIDFromRowCount ((int) rowCount);
        String where = DatabaseHelper.KEY_ID + " = " + UID;
        ContentValues contentValues = new ContentValues ();
        contentValues.put (DatabaseHelper.KEY_NOTES, data);
        SQLiteDatabase sqLiteDatabase = mDatabaseHelper.getWritableDatabase ();
        Boolean result = sqLiteDatabase.update (DatabaseHelper.TABLE_NAME, contentValues, where, null) != 0;
        sqLiteDatabase.close ();
        if (DEBUG) Log.d (TAG, "setNote resultOfOperation = " + result);
//        if (DEBUG) Toast.makeText (mContext, DatabaseHelper.class.getSimpleName ()+" :setNote resultOfOperation = "+result+" rowID = "+UID, Toast.LENGTH_SHORT).show ();
        return result;
    }

    public Boolean editSubject(int subjectID) {
        return false;
    }

    public Boolean deleteSubject(long rowCount) {
        int UID = getUIDFromRowCount (rowCount);
        SQLiteDatabase db = mDatabaseHelper.getReadableDatabase ();
        db.delete (DatabaseHelper.TABLE_NAME, DatabaseHelper.KEY_ID + " = " + UID, null);
        if (DEBUG) Log.d (TAG, "deleteSubject: subjectID = " + UID);
        return false;
    }

    @SuppressLint("NewApi")
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

    public int getUIDFromRowCount(long rowCount) {
        int UID = -1;
        int intRowCount = (int) rowCount;
        SQLiteDatabase db = mDatabaseHelper.getWritableDatabase ();
        Cursor cursor = db.query (DatabaseHelper.TABLE_NAME, null, null, null, null, null, null);
        if (cursor.getCount () != 0) {
            cursor.moveToPosition (intRowCount - 1);
            UID = cursor.getInt (cursor.getColumnIndex (DatabaseHelper.KEY_ID));
        }
        cursor.close ();
        db.close ();
        return UID;
    }

    static class DatabaseHelper extends SQLiteOpenHelper {
        private static final String KEY_ID = "_id";  /* will contain integer */
        private static final String KEY_SUB_NAME = "subject_name";  /* will contain string */
        private static final String KEY_PROF_NAME = "prof_name";   /* will contain string */
        //private static final String KEY_STATUS = "status";  /* will contain integer */
        private static final String KEY_NOTES = "notes";  /* will contain string */

        private static final String DATABASE_NAME = "Subjects";
        private static final String TABLE_NAME = "Subject_name";
        private static final int DATABASE_VERSION = 3;
        private static final String DATABASE_CREATE =
                String.format ("create table if not exists %s (%s integer primary key autoincrement,%s VARCHAR not null,%s integer not null,%s TEXT);", TABLE_NAME, KEY_ID, KEY_SUB_NAME, KEY_PROF_NAME, KEY_NOTES);
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
}

