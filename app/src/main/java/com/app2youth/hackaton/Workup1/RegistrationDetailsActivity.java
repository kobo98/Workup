package com.app2youth.hackaton.Workup1;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;


public class RegistrationDetailsActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_registration_details);


		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;

		ImageView back = (ImageView) findViewById(R.id.background);

		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) back.getLayoutParams();
		params.width = width;
		params.height = (int)((double)width/4.0);
		back.setLayoutParams(params);
		back.bringToFront();

		TextView title = (TextView) findViewById(R.id.title_new);
		title.bringToFront();

	}

	@Override
	public void onResume(){
		super.onResume();
		ComponentName component=new ComponentName(this, IncomingSms.class);
		getPackageManager()
				.setComponentEnabledSetting(component,
						PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
						PackageManager.DONT_KILL_APP);
	}

	@Override
	public void onPause(){
		super.onPause();
		ComponentName component=new ComponentName(this, IncomingSms.class);
		getPackageManager()
				.setComponentEnabledSetting(component,
						PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
						PackageManager.DONT_KILL_APP);
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

	static int code;
	public void register(View v){
		doRegister();
		/*
		Thread register = new Thread(){
			public void run(){
				doRegister();
			}
		};
		register.start();
		*/
	}

	public void doRegister(){
		final String phone = ((EditText) findViewById(R.id.phone)).getText().toString();
		final String fname = ((EditText) findViewById(R.id.fname)).getText().toString();
		final String lname = ((EditText) findViewById(R.id.lname)).getText().toString();

		if (phone.equals("")){
			Toast t = Toast.makeText(getApplicationContext(), getString(R.string.enter_phone_registration_alert), Toast.LENGTH_LONG);
			t.show();
			return;
		}
		else if (fname.equals("")){
			Toast t = Toast.makeText(getApplicationContext(), getString(R.string.enter_name_registration_alert), Toast.LENGTH_LONG);
			t.show();
			return;
		}
		else if (lname.equals("")){
			Toast t = Toast.makeText(getApplicationContext(), getString(R.string.enter_last_name_registration_alert), Toast.LENGTH_LONG);
			t.show();
			return;
		}

		new RegisterUser().execute(phone, fname, lname);

		/*
		Thread smsReceive = new Thread(){
			public void run(){
				int count=0;
				while(!IncomingSms.received && count<600){
					count++;
					try {
						Thread.sleep(100);
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
							if (Controller.studentExists(phone)) {
								Toast t = Toast.makeText(getApplicationContext(), "You are already registered as student!", Toast.LENGTH_LONG);
								t.show();
								finish();
								return;
							}

							saveString("species", "t");
							BasicClass.teacher=true;
							BasicClass.phone=phone;
							if (!Controller.teacherExists(phone)){
								Controller.addTeacher(fname,lname,phone);
							}

							Intent i=new Intent(getBaseContext(),TeacherMainActivity.class);
							startActivity(i);
						}

						//Student
						else{
							if (Controller.teacherExists(phone)) {
								Toast t = Toast.makeText(getApplicationContext(), "You are already registered as teacher!", Toast.LENGTH_LONG);
								t.show();
								finish();
								return;
							}
							saveString("species", "s");
							BasicClass.teacher=false;
							BasicClass.phone=phone;
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
		*/
	}


	private class RegisterUser extends AsyncTask<String, Void, Void> {
		ProgressDialog pdLoading = new ProgressDialog(RegistrationDetailsActivity.this);

		@Override
		protected Void doInBackground(String... in){
			code = 10000+(int)(Math.random()*89999);
			SmsManager sm = SmsManager.getDefault();
			sm.sendTextMessage(in[0], null, ""+code, null, null);

			int count=0;
			while(!IncomingSms.received && count<600){
				count++;
				try {
					Thread.sleep(100);
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
					saveString("phone", in[0]);
					//Teacher
					if (BasicClass.registeringTeacher){
						if (Controller.studentExists(in[0])) {
							Looper.prepare();
							Toast t = Toast.makeText(getApplicationContext(), getString(R.string.already_registered_student_alert), Toast.LENGTH_LONG);
							t.show();
							finish();
							return null;
						}

						saveString("species", "t");
						BasicClass.teacher=true;
						BasicClass.phone=in[0];
						if (!Controller.teacherExists(in[0])){
							Controller.addTeacher(in[1],in[2],in[0]);
						}

						Intent i=new Intent(getBaseContext(),TeacherMainActivity.class);
						finish();
						startActivity(i);


					}

					//Student
					else{
						if (Controller.teacherExists(in[0])) {
							Looper.prepare();
							Toast t = Toast.makeText(getApplicationContext(), getString(R.string.already_registered_teacher_alert), Toast.LENGTH_LONG);
							t.show();
							finish();
							return null;
						}
						saveString("species", "s");
						BasicClass.teacher=false;
						BasicClass.phone=in[0];
						if (!Controller.studentExists(in[0])){
							Controller.addStudent(in[1],in[2],in[0]);
							saveString("groups","");
						}

						Intent i=new Intent(getBaseContext(),StudentAllTasksActivity.class);
						finish();
						startActivity(i);
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			return null;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();
			pdLoading.setMessage("\t"+getString(R.string.wait));
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(Void result) {
			pdLoading.dismiss();

		}
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
