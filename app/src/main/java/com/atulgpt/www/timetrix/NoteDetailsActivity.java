package com.atulgpt.www.timetrix;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.atulgpt.www.timetrix.utils.GlobalData;
import com.atulgpt.www.timetrix.dataBinders.SubjectsDetailsBinder;
import com.atulgpt.www.timetrix.databinding.ActivitySubjectDetailsBinding;

public class NoteDetailsActivity extends AppCompatActivity {
    private long mFileID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        mFileID = getIntent ().getLongExtra (GlobalData.FILE_ID,1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSubDetail);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        ActivitySubjectDetailsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_subject_details);
//        Toast.makeText (NoteDetailsActivity.this, "file id ="+getIntent ().getLongExtra (GlobalData.FILE_ID,-1), Toast.LENGTH_LONG).show ();
        SubjectsDetailsBinder subject = new SubjectsDetailsBinder (NoteDetailsActivity.this,mFileID + 1);
        binding.setSubject (subject);
        if (actionBar != null ) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        getIntent ();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent (this, SettingsActivity.class);
            startActivity (intent);
            return true;
        }
        if(id == android.R.id.home){
            Intent intent = new Intent(NoteDetailsActivity.this, StartupPage.class);
            NoteDetailsActivity.this.finish ();
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        NoteDetailsActivity.this.finish ();
        startActivity (new Intent (NoteDetailsActivity.this,StartupPage.class).putExtra (GlobalData.RESUME_STATE_FILE_ID,mFileID));
    }
}

