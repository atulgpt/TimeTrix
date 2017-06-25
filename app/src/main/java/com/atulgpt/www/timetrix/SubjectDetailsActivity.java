package com.atulgpt.www.timetrix;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.atulgpt.www.timetrix.Utils.Util;
import com.atulgpt.www.timetrix.dataBinders.SubjectsDetailsBinder;
import com.atulgpt.www.timetrix.databinding.ActivitySubjectDetailsBinding;

public class SubjectDetailsActivity extends AppCompatActivity {
    private long mFileID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        mFileID = getIntent ().getLongExtra (Util.FILE_ID,1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarSubDetail);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        ActivitySubjectDetailsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_subject_details);
//        Toast.makeText (SubjectDetailsActivity.this, "file id ="+getIntent ().getLongExtra (Util.FILE_ID,-1), Toast.LENGTH_LONG).show ();
        SubjectsDetailsBinder subject = new SubjectsDetailsBinder (SubjectDetailsActivity.this,mFileID + 1);
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
            Intent intent = new Intent (this, Settings.class);
            startActivity (intent);
            return true;
        }
        if(id == android.R.id.home){
            Intent intent = new Intent(SubjectDetailsActivity.this, StartupPage.class);
            SubjectDetailsActivity.this.finish ();
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        SubjectDetailsActivity.this.finish ();
        startActivity (new Intent (SubjectDetailsActivity.this,StartupPage.class).putExtra (Util.RESUME_STATE_FILE_ID,mFileID));
    }
}

class subject{

}
