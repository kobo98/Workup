package com.app2youth.hackaton.workup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.sql.SQLException;

import Server.Controller;


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

	public String getString(String name){
		SharedPreferences mPreferences = getSharedPreferences("WorkUp",MODE_PRIVATE);
		return mPreferences.getString(name,"");
	}
	public void registrationStudent(View view){
		if (!readAndLogin()){
			BasicClass.registeringTeacher=false;
			Intent intent = new Intent(this, RegistrationDetailsActivity.class);
			startActivity(intent);
		}

	}
	public void registrationTeacher(View view){
		if (!readAndLogin()){
			BasicClass.registeringTeacher=true;
			Intent intent = new Intent(this, RegistrationDetailsActivity.class);
			startActivity(intent);
		}

	}


	public boolean readAndLogin(){
		String phone = getString("phone");
		String species = getString("species");
		try {
			if (!phone.equals("") && !species.equals("")){

				if (species.equals("t")){

					if (Controller.teacherExists(phone)){
						BasicClass.teacher=true;
						BasicClass.phone=phone;
						Intent i=new Intent(getBaseContext(),TeacherMainActivity.class);
						startActivity(i);
						return true;
					}

				}
				else if (species.equals("s")){
					if (Controller.studentExists(phone)){
						BasicClass.teacher=false;
						BasicClass.phone=phone;
						Intent i=new Intent(getBaseContext(),StudentAllTasksActivity.class);
						startActivity(i);
						return true;
					}
				}
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

}
