package com.app2youth.hackaton.workup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Random;

/**
 * Created by Nuriel on 19-May-15.
 */
public class BasicClass extends ActionBarActivity
        implements NavigationDrawerFragmentStudent.NavigationDrawerCallbacks {


	public static String phone;
	public static boolean teacher;
	public static boolean registeringTeacher;

    protected int positionInMenu;

    public static String startingPath;
    public void saveInt(String name,int num){
        SharedPreferences mPreferences = getSharedPreferences("WorkUp",MODE_PRIVATE);
        SharedPreferences.Editor editor= mPreferences.edit();
        editor.putInt(name,num);
        editor.commit();
    }

    public int getInt(String name){
        SharedPreferences mPreferences = getSharedPreferences("WorkUp",MODE_PRIVATE);
        return mPreferences.getInt(name,-1);
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

    public void closeKeyboard(){
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    public void showError(String msg){
        new AlertDialog.Builder(this)
                .setMessage(msg)
                .setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                })
                .show();
    }

    public void showError(String msg, final int id){
        new AlertDialog.Builder(this)
                .setMessage(msg)
                .setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        findViewById(id).requestFocus();
                    }
                })
                .show();
    }

    public void openMainActivity(View v){
        Intent intent = new Intent(this, StudentAllTasksActivity.class);
        startActivity(intent);
    }

    public void openGraphActivity(View v){
        Intent intent = new Intent(this, GraphActivity.class);
        startActivity(intent);
    }


    public void openAddStudentsToGroupActivity(View v){
        Intent intent = new Intent(this, AddStudentsToGroupActivity.class);
        startActivity(intent);
    }
    public void openAddGroupActivity(View v){
        Intent intent = new Intent(this, AddGroupActivity.class);
        startActivity(intent);
    }
    public void openAddTaskActivity(View v){
        Intent intent = new Intent(this, AddTaskActivity.class);
        startActivity(intent);
    }

    public void openAllGradesActivity(View v){
        Intent intent = new Intent(this, AllGradesActivity.class);
        startActivity(intent);
    }

    public void openLessonTableActivity(View v){
        Intent intent = new Intent(this, LessonsTableActivityStudent.class);
        startActivity(intent);
    }

    public void openLessonTableActivityTeacher(View v){
        Intent intent = new Intent(this, LessonsTableActivityTeacher.class);
        startActivity(intent);
    }


    public void hideToShowTextToEditText(final int idOfEdit,final int idOfText){
        EditText etName= (EditText) findViewById(idOfEdit);
        etName.clearFocus();
        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                TextView tvName = (TextView) findViewById(idOfText);
                EditText etName= (EditText) findViewById(idOfEdit);
                if(hasFocus){
                    tvName.setVisibility(View.GONE);
                }else{
                    if(etName.getText()==null){
                        tvName.setVisibility(View.VISIBLE);
                    }else{
                        if(etName.getText().toString()==""||etName.getText().toString().length()==0){
                            tvName.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        });
    }

    public void loading_dialog(){
        ProgressDialog progressDialog= ProgressDialog.show(this,"טוען נתונים","טוען נתונים",true);
        /*
            thread = bla bla
            bla
            bla
            bla
         */
        progressDialog.dismiss();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

    }

    public void openActivityFromMenu(int position){
        switch (position) {
            case 1:
                openMainActivity(new View(this));
                break;
            case 2:
                openAllGradesActivity(new View(this));
                break;
            case 3:
                openLessonTableActivity(new View(this));
                break;
        }
    }

    public void openActivityFromMenuTeacher(int position){
        switch (position) {
            case 1:
                //openHWListForTeacherActivity(new View(this));
                Intent intent = new Intent(this, TeacherMainActivity.class);
                startActivity(intent);
                break;
            case 2:
                openAddTaskActivity(new View(this));
                break;
            case 3:
                openAddStudentsToGroupActivity(new View(this));
                break;
            case 4:
                openLessonTableActivityTeacher(new View(this));
                break;
            case 5:
                openAddGroupActivity(new View(this));
                break;

        }
    }

    public void pushNotification(String title, String text, Class whereToBeTransferred, Class stackClass, int idOfNotification){
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_drawer)
                        .setContentTitle(title)
                        .setContentText(text);
        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, whereToBeTransferred);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(stackClass);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // id allows you to update the notification later on.
        mNotificationManager.notify(idOfNotification, mBuilder.build());
    }

    public class HWArrayAdapter extends BaseAdapter {
        private Context context;
        private String[][] values;

        public HWArrayAdapter(Context context, String[][] objects,String[] programs){
            this.context=context;
            this.values=objects;
        }

        @Override
        public int getCount() {
            return values.length;
        }

        @Override
        public Object getItem(int position) {
            return values[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView = inflater.inflate(R.layout.hwlayout,parent,false);
            TextView subject = (TextView) rowView.findViewById(R.id.subject);
            TextView teacher = (TextView) rowView.findViewById(R.id.teacher);
            TextView do_date = (TextView) rowView.findViewById(R.id.do_date);
            ImageView symbol = (ImageView) rowView.findViewById(R.id.symbol);

            //((SwipeListView)parent).recycle(convertView, position);

            subject.setText(values[position][0]);
            teacher.setText(values[position][1]);
            do_date.setText(values[position][2]);
            Random rnd= new Random();
            if(rnd.nextBoolean()) {
                symbol.setBackgroundColor(0xff00ff00);
            }else{
                symbol.setBackgroundColor(0xffff0000);
            }

            return rowView;
        }
    }

    /*public void submitTask(String subject,String teacher, String doDate, int rating){
        Log.d("Msg","submited");
        ArrayList<Task> tasks =  ((StudentSystem) Manager.system).getSortedTasks();
        for (int i=0; i<tasks.size(); i++){
            if (tasks.get(i).subject.equals(subject)){
                ((StudentSystem) Manager.system).sendFeedback(tasks.get(i), rating);
                ((StudentSystem) Manager.system).removeTask(tasks.get(i));
            }
        }

    }*/

    public void delayTask(String subject,String teacher, String doDate){

    }

    public class SchoolBagArrayAdapter extends ArrayAdapter<String> {
        private Context context;
        private String[][] values;

        public SchoolBagArrayAdapter(Context context, String[][] objects, String[] programs) {
            super(context, R.layout.hwlayout, programs);
            this.context = context;
            this.values = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View rowView = inflater.inflate(R.layout.subjectlayout, parent, false);
            TextView subject = (TextView) rowView.findViewById(R.id.subject);
            TextView teacher = (TextView) rowView.findViewById(R.id.teacher);
            TextView add1 = (TextView) rowView.findViewById(R.id.add1);
            TextView add2 = (TextView) rowView.findViewById(R.id.add2);
            TextView add3 = (TextView) rowView.findViewById(R.id.add3);
            TextView add4 = (TextView) rowView.findViewById(R.id.add4);
            subject.setText(values[position][0]);
            teacher.setText(values[position][1]);
            add1.setText("");
            add2.setText("");
            add3.setText("");
            add4.setText("");
            if(values[position].length>2){
                if (values[position][2] != null && values[position][2] != "" && values[position][2] != "Zereg") {
                    add1.setText("  "+values[position][2]);
                    if(values[position].length>3){
                    if (values[position][3] != null && values[position][3] != "" && values[position][3] != "Zereg") {
                        add2.setText("  "+values[position][3]);
                        if(values[position].length>4) {
                            if (values[position][4] != null && values[position][4] != "" && values[position][4] != "Zereg") {
                                add3.setText("  "+values[position][4]);
                                if (values[position].length > 5) {
                                    if (values[position][5] != null && values[position][5] != "" && values[position][5] != "Zereg") {
                                        add4.setText("  "+values[position][5]);
                                    }
                                }
                            }
                        }
                    }
                    }
                }
            }
            return rowView;
        }
    }
}
