package com.app2youth.hackaton.workup;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.gsm.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.SQLException;

import Server.Controller;


public class RegistrationDetailsActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration_details);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_registration_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();

		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}


	public void register(View v){
		final String phone = ((EditText) findViewById(R.id.phone)).getText().toString();
		final String fname = ((EditText) findViewById(R.id.fname)).getText().toString();
		final String lname = ((EditText) findViewById(R.id.lname)).getText().toString();

		if (phone.equals("")){
			Toast t = Toast.makeText(getApplicationContext(), "Please enter phone", Toast.LENGTH_LONG);
			t.show();
			return;
		}
		else if (fname.equals("")){
			Toast t = Toast.makeText(getApplicationContext(), "Please enter first name", Toast.LENGTH_LONG);
			t.show();
			return;
		}
		else if (lname.equals("")){
			Toast t = Toast.makeText(getApplicationContext(), "Please enter last name", Toast.LENGTH_LONG);
			t.show();
			return;
		}


		int code = 10000+(int)(Math.random()*89999);
		SmsManager sm = SmsManager.getDefault();
		sm.sendTextMessage(phone, null, ""+code, null, null);

		Thread smsReceive = new Thread(){
			public void run(){
						int count=0;
						while(!IncomingSms.received && count<22){
							count++;
							try {
								Thread.sleep(500);
							} catch (InterruptedException e) {
								e.printStackTrace();
					}
					Thread.yield();
				}

				if (!IncomingSms.received){
					finish();
				}

				else {
					try {
						saveString("phone", phone);

						//Teacher
						if (BasicClass.registeringTeacher){
							saveString("species", "t");
							BasicClass.teacher=true;
							if (!Controller.teacherExists(phone)){
								Controller.addTeacher(fname,lname,phone);
							}

							Intent i=new Intent(getBaseContext(),TeacherMainActivity.class);
							startActivity(i);
						}

						//Student
						else{
							saveString("species", "s");
							BasicClass.teacher=false;
							if (!Controller.studentExists(phone)){
								Controller.addStudent(fname,lname,phone);
							}

							Intent i=new Intent(getBaseContext(),StudentAllTasksActivity.class);
							startActivity(i);
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}

			}
		};
		smsReceive.start();

	}


	public boolean smsReceived(int code){



		return false;
	}

	public void saveString(String name,String str){
		SharedPreferences mPreferences = getSharedPreferences("WorkUp",MODE_PRIVATE);
		SharedPreferences.Editor editor= mPreferences.edit();
		editor.putString(name,str);
		editor.commit();
	}

	public String getString(String name){
		SharedPreferences mPreferences = getSharedPreferences("WorkUp",MODE_PRIVATE);
		return mPreferences.getString(name,"");
	}

}
