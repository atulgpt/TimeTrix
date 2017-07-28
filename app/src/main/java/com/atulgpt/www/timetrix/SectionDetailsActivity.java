package com.atulgpt.www.timetrix;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.atulgpt.www.timetrix.adapters.DatabaseAdapter;
import com.atulgpt.www.timetrix.dataBinders.SectionDetailsBinder;
import com.atulgpt.www.timetrix.databinding.ActivitySubjectDetailsBinding;
import com.atulgpt.www.timetrix.utils.GlobalData;

public class SectionDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private long mSectionIndex;
    private EditText mSectionNameEditText, mSectionDescriptionEditText;
    SectionDetailsBinder mSubject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        ActivitySubjectDetailsBinding binding = DataBindingUtil.setContentView (this, R.layout.activity_subject_details);
        mSectionIndex = getIntent ().getIntExtra (GlobalData.SECTION_INDEX, 1);
        Toolbar toolbar = (Toolbar) findViewById (R.id.toolbarSubDetail);
        setSupportActionBar (toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar ();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled (true);
        }
        Button updateButton = (Button) findViewById (R.id.update);
        mSectionNameEditText = (EditText) findViewById (R.id.textViewSectionName);
        mSectionDescriptionEditText = (EditText) findViewById (R.id.textViewDescriptionName);
        updateButton.setOnClickListener (this);
//        Toast.makeText (SectionDetailsActivity.this, "file id ="+getIntent ().getLongExtra (GlobalData.SECTION_INDEX,-1), Toast.LENGTH_LONG).show ();
        mSubject = new SectionDetailsBinder (SectionDetailsActivity.this, mSectionIndex + 1);
        mSubject.setInputType ("none");
        binding.setSubject (mSubject);
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled (true);
        }
        getIntent ();
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
            //Intent intent = new Intent(SectionDetailsActivity.this, StartupPage.class);
            SectionDetailsActivity.this.finish ();
            //startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected (item);
    }

    @Override
    public void onBackPressed() {
        SectionDetailsActivity.this.finish ();
        //startActivity (new Intent (SectionDetailsActivity.this,StartupPage.class).putExtra (GlobalData.RESUME_STATE_FILE_ID,mSectionIndex));
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v.getId () == R.id.update) {
            String sectionName, sectionDescription;
            sectionName = mSectionNameEditText.getText ().toString ();
            sectionDescription = mSectionDescriptionEditText.getText ().toString ();
            if (sectionName.equals (mSubject.getName ()) && sectionDescription.equals (mSubject.getDescription ())) {
                Toast.makeText (this, "No changes made!", Toast.LENGTH_SHORT).show ();
            } else {
                DatabaseAdapter databaseAdapter = new DatabaseAdapter (this, null);
                databaseAdapter.editSection ((int) (mSectionIndex + 1), sectionName, sectionDescription);
                setResult (RESULT_OK);
            }
            SectionDetailsActivity.this.finish ();
        }
    }
}

