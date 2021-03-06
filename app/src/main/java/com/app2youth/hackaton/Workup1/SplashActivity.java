package com.app2youth.hackaton.Workup1;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;

import java.lang.reflect.Field;
import java.sql.SQLException;


public class SplashActivity extends BasicClass {
	public String getString(String name){
		SharedPreferences mPreferences = getSharedPreferences("WorkUp",MODE_PRIVATE);
		return mPreferences.getString(name,"");
	}
	public boolean readAndLogin(){
		String phone = getString("phone");
		String species = getString("species");

		Log.d("Phone", phone);
		Log.d("Species", species);

		try {
			if (phone !=null && !phone.equals("") && !species.equals("")){

				if (species.equals("t")){

					if (Controller.teacherExists(phone)){
						BasicClass.teacher=true;
						BasicClass.phone=phone;
						BasicClass.id=Controller.getTeacherIDByPhone(phone);
						saveInt("id", BasicClass.id);
						Intent i=new Intent(getBaseContext(),TeacherMainActivity.class);
						startActivity(i);
						finish();
						return true;
					}

				}
				else if (species.equals("s")){
					if (Controller.studentExists(phone)){
						BasicClass.teacher=false;
						BasicClass.phone=phone;
						BasicClass.id=Controller.getStudentIDByPhone(phone);
						saveInt("id", BasicClass.id);
						Intent i=new Intent(getBaseContext(),StudentAllTasksActivity.class);
						startActivity(i);
						finish();
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
	    getSupportActionBar().hide();
        setContentView(R.layout.activity_splash);

	    View decorView = getWindow().getDecorView();
	    int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
			    | View.SYSTEM_UI_FLAG_FULLSCREEN;
	    decorView.setSystemUiVisibility(uiOptions);

	    FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/CHOCRG__.TTF");

        Thread background = new Thread() {
            public void run() {
                try {

	                SQL.start();

					if(!readAndLogin()){
						Intent i=new Intent(getBaseContext(),RegistrationActivity.class);
						finish();
						startActivity(i);
					}

                    //Remove activity
	                finish();

                } catch (Exception e) {

                }
            }
        };

        // start thread
        background.start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

final class FontsOverride {

	public static void setDefaultFont(Context context,
	                                  String staticTypefaceFieldName, String fontAssetName) {
		final Typeface regular = Typeface.createFromAsset(context.getAssets(),
				fontAssetName);
		replaceFont(staticTypefaceFieldName, regular);
	}

	private static void replaceFont(String staticTypefaceFieldName,
	                                  final Typeface newTypeface) {
		try {
			final Field staticField = Typeface.class
					.getDeclaredField(staticTypefaceFieldName);
			staticField.setAccessible(true);
			staticField.set(null, newTypeface);
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
