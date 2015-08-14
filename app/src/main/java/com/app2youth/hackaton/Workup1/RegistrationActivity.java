package com.app2youth.hackaton.Workup1;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class RegistrationActivity extends ActionBarActivity {

	public static boolean teacher;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_registration);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_registration, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

	public void registrationStudent(View view){
		final RegistrationActivity ra = this;
		BasicClass.registeringTeacher=false;
		Intent intent = new Intent(ra, RegistrationDetailsActivity.class);
		startActivity(intent);
	}

	public void registrationTeacher(View view){
		final RegistrationActivity ra = this;
		BasicClass.registeringTeacher=true;
		Intent intent = new Intent(ra, RegistrationDetailsActivity.class);
		startActivity(intent);
	}




}
