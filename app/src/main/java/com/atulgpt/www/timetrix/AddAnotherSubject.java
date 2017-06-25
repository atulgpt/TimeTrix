package com.atulgpt.www.timetrix;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.atulgpt.www.timetrix.Adapters.DatabaseAdapter;
import com.atulgpt.www.timetrix.Utils.Util;

public class AddAnotherSubject extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_another_subject);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarAddSub);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
//        Toast.makeText (AddAnotherSubject.this, "bool in addAnother"+getIntent ().getBooleanExtra (Util.ADD_ANOTHER_SUB_HOME,true), Toast.LENGTH_SHORT).show ();
        if (actionBar != null && getIntent ().getBooleanExtra (Util.ADD_ANOTHER_SUB_HOME,true) ) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Button done = (Button) findViewById(R.id.done);
        final EditText editSubName = (EditText) findViewById(R.id.editTextSubName);
        final EditText editProfName = (EditText) findViewById(R.id.editTextProfName);
        done.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DatabaseAdapter databaseAdapter = new DatabaseAdapter(AddAnotherSubject.this);
                if (editSubName.getText().toString().isEmpty()) {
                    Toast.makeText(AddAnotherSubject.this, R.string.empty_sub_name_not_to_str, Toast.LENGTH_LONG).show();
                    return;
                }
                databaseAdapter.addTable (editSubName.getText ().toString (), editProfName.getText ().toString ());
                Intent intent = new Intent(AddAnotherSubject.this, StartupPage.class);
                startActivity(intent);
                AddAnotherSubject.this.finish ();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_another_subject, menu);
        return true;
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
            Intent intent = new Intent(AddAnotherSubject.this, StartupPage.class);
            startActivity(intent);
            AddAnotherSubject.this.finish ();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(!getIntent ().getBooleanExtra (Util.ADD_ANOTHER_SUB_HOME,true)){
            AddAnotherSubject.this.finish ();
        }else {
            Intent intent = new Intent(AddAnotherSubject.this, StartupPage.class);
            startActivity(intent);
            AddAnotherSubject.this.finish ();
        }
    }
}
