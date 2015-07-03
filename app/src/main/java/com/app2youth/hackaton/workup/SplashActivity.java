package com.app2youth.hackaton.workup;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.telephony.SmsManager;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import SQL.SQL;
import Server.Controller;


public class SplashActivity extends ActionBarActivity {
    private String getMyPhoneNumber(){
        TelephonyManager mTelephonyMgr;
        mTelephonyMgr = (TelephonyManager)
                getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getLine1Number();
    }

    private String getMy10DigitPhoneNumber(){
        String s = getMyPhoneNumber();
        return s != null && s.length() > 2 ? s.substring(2) : null;
    }



    //public static String phone;
    //static String name;
    //static String fname;
    //public static boolean teacher;

    static int code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        Thread background = new Thread() {
            public void run() {

                try {
	                /*
                    long start = System.currentTimeMillis();
                    SQL.start();
                    long timeLost = System.currentTimeMillis()-start;
                    */
                    sleep(2000);

                    // After 5 seconds redirect to another intent
                    Intent i=new Intent(getBaseContext(),RegistrationActivity.class);
                    startActivity(i);

                    //Remove activity
                    finish();

                } catch (Exception e) {

                }
            }
        };

        // start thread
        background.start();




        /*
        //read file, or create it
        try {
            //First connect to SQL database
            SQL.start();

            //Read file
            String path = getFilesDir().getAbsolutePath();
            final File file = new File(path + "/data.txt");

            //file.delete();
            //finish();

            if (file.exists()) {
                int length = (int) file.length();
                byte[] bytes = new byte[length];
                FileInputStream in = new FileInputStream(file);
                try {
                    in.read(bytes);
                } finally {
                    in.close();
                }

                String contents = new String(bytes);
                Log.d("READ: ", contents);

                if (contents==null || contents.equals("")) {
                    registration(file);
                    return;
                }


                phone = contents;

                if (Controller.teacherExists(phone)){
                    teacher=true;
                    Intent i=new Intent(getBaseContext(),TeacherMainActivity.class);
                    startActivity(i);
                }
                else if (Controller.studentExists(phone)){
                    teacher=false;
                    Intent i=new Intent(getBaseContext(),StudentAllTasksActivity.class);
                    startActivity(i);
                }
                else{
                    phone=null;
                    registration(file);
                    return;
                }



            } else {
                registration(file);

            }
        }
        catch (IOException e){

        } catch (SQLException e) {
            e.printStackTrace();
        }
        */
    }

	/*
    public void registration(final File file){

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Registration");
        //alert.setMessage("Message");


        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText inputPhone = new EditText(this);
        inputPhone.setInputType(InputType.TYPE_CLASS_PHONE);
        inputPhone.setHint("Phone");
        layout.addView(inputPhone);

        final EditText inputName = new EditText(this);
        inputName.setHint("Name");
        layout.addView(inputName);

        final EditText inputFName = new EditText(this);
        inputFName.setHint("Last name");
        layout.addView(inputFName);

        final RadioButton[] rb = new RadioButton[2];
        final RadioGroup rg = new RadioGroup(this);
        rg.setOrientation(RadioGroup.HORIZONTAL);


        for(int i=0; i<2; i++){
            rb[i]  = new RadioButton(this);
            rb[i].setId(i);
            rg.addView(rb[i]);

        }
        rb[0].setText("Teacher");
        rb[1].setText("Student");


        layout.addView(rg);


        alert.setView(layout);



        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                phone = inputPhone.getText().toString();
                name = inputName.getText().toString();
                fname = inputFName.getText().toString();
                teacher = (rg.getCheckedRadioButtonId() == 0);

            }
        });
        alert.show();


        Thread receiveAlertButton = new Thread(){
            public void run(){



                while(phone==null){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


                code = 10000+(int)(Math.random()*89999);
                SmsManager sm = SmsManager.getDefault();
                sm.sendTextMessage(phone, null, ""+code, null, null);

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
                else{

                    try {
                        FileOutputStream stream = new FileOutputStream(file);
                        stream.write(phone.getBytes());
                        if ( Controller.teacherExists(phone)){
                            teacher=true;
                            Log.d("WROTE: ", phone);
                        }
                        else if (Controller.studentExists(phone)){
                            teacher=false;
                            Log.d("WROTE: ", phone);
                        }




                        if (teacher){
                            if (!Controller.teacherExists(phone))
                                Controller.addTeacher(name,fname,phone);
                            Intent i=new Intent(getBaseContext(),TeacherMainActivity.class);
                            startActivity(i);
                        }
                        else{
                            if (!Controller.studentExists(phone))
                                Controller.addStudent(name,fname,phone);
                            Intent i=new Intent(getBaseContext(),StudentAllTasksActivity.class);
                            startActivity(i);
                        }






                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }


            }
        };
        receiveAlertButton.start();

    }
	*/

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

