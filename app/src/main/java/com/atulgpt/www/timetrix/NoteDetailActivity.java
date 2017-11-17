package com.atulgpt.www.timetrix;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.atulgpt.www.timetrix.utils.GlobalData;
import com.atulgpt.www.timetrix.utils.NoteUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NoteDetailActivity extends AppCompatActivity {
    String noteTitle, noteBody;
    private NoteUtil mNoteUtil;
    private int noteIndex, sectionIndex;
    private boolean mNoteBodyChanged = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_note_detail);
        Toolbar toolbar = (Toolbar) findViewById (R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)
                findViewById (R.id.toolbar_layout);
        EditText noteBodyEditText = (EditText) findViewById (R.id.noteBodyTextView);
        Intent intent = getIntent ();
        sectionIndex = 0;
        noteIndex = 0;
        if (intent.hasExtra (GlobalData.SECTION_INDEX) && intent.hasExtra (GlobalData.NOTE_INDEX)) {
            sectionIndex = intent.getIntExtra (GlobalData.SECTION_INDEX, -1);
            noteIndex = intent.getIntExtra (GlobalData.NOTE_INDEX, -1);
        } else {
            setResult (RESULT_CANCELED);
            NoteDetailActivity.this.finish ();
        }
        mNoteUtil = new NoteUtil (this);
        JSONArray noteJSONArray = mNoteUtil.getNoteJSONArray (sectionIndex + 1);
        JSONObject noteJSONObject;
        try {
            noteJSONObject = noteJSONArray.getJSONObject (noteIndex);
            noteTitle = noteJSONObject.getString (GlobalData.NOTE_TITLE);
            noteBody = noteJSONObject.getString (GlobalData.NOTE_BODY);
            collapsingToolbarLayout.setTitle (noteTitle);
            noteBodyEditText.setText (noteBody);
        } catch (JSONException e) {
            e.printStackTrace ();
        }
        setSupportActionBar (toolbar);
        ActionBar actionBar = getSupportActionBar ();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled (true);
        }
        /*FloatingActionButton fab = (FloatingActionButton) findViewById (R.id.star_fab);
        fab.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Snackbar.make (view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction ("Action", null).show ();
            }
        });*/
        noteBodyEditText.addTextChangedListener (new TextWatcher () {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Toast.makeText (NoteDetailActivity.this, "Checking", Toast.LENGTH_SHORT).show ();
                Boolean boolResponse = mNoteUtil.editNote (noteIndex, sectionIndex + 1, s.toString (), noteTitle);
                if (boolResponse) {
                    mNoteBodyChanged = true;
                } else {
                    Toast.makeText (NoteDetailActivity.this, R.string.file_could_not_update_str, Toast.LENGTH_LONG).show ();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        if (mNoteBodyChanged) {
            Intent intent = new Intent (NoteDetailActivity.this, StartupPage.class)
                    .putExtra (GlobalData.NOTE_DETAIL_ACTIVITY_ACTION,
                            GlobalData.NOTE_BODY_CHANGED_ACTION);
            setResult (RESULT_OK, intent);
            NoteDetailActivity.this.finish ();
        }
        super.onBackPressed ();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater ().inflate (R.menu.menu_note_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId ();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent (this, SettingsActivity.class);
            startActivity (intent);
            return true;
        }
        if (id == android.R.id.home) {
            setResult (RESULT_OK);
            NoteDetailActivity.this.finish ();
            return true;
        }

        return super.onOptionsItemSelected (item);
    }
}
