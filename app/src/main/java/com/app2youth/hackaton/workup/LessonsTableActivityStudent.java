package com.app2youth.hackaton.workup;

import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.GridView;

import java.sql.ResultSet;
import java.sql.SQLException;
import SQL.SQL;
import Server.Controller;


public class LessonsTableActivityStudent extends BasicClass
        implements NavigationDrawerFragmentStudent.NavigationDrawerCallbacks {

    private GridView gv;
    private GridViewAdapter adapter_nur;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragmentStudent mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons_table_student);

        mNavigationDrawerFragment = (NavigationDrawerFragmentStudent)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        positionInMenu=2;

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout),positionInMenu,this);
    }

    @Override
    public void onStart(){
        super.onStart();
        this.gv = (GridView) findViewById(R.id.gridView1);
        String[] str = new String[7+7*9];
        str[0]="Day 1";str[1]="Day 2";str[2]="Day 3";str[3]="Day 4";str[4]="Day 5";str[5]="Day 6";str[6]="Day 7";
        for (int i=1; i<9; i++){
            for (int j=0; j<7; j++){
                str[i*7+j] = "---";
            }
        }


        String classIDs = null;
        try {

            int[] indices = new int[]{1,1,1,1,1,1,1};

            classIDs = BasicClass.teacher? Controller.getListOfClassesForTeacher(BasicClass.phone):Controller.getListOfClassesForStudent(BasicClass.phone);

            classIDs = classIDs.replace(";", ",");
            if (classIDs.length()==0)
                classIDs="-1";
            ResultSet rs = SQL.statement.executeQuery("SELECT teachGroup,day,startTime FROM classes WHERE classID IN ("+classIDs+");");
            while(rs.next()){
                int groupID = rs.getInt(1);
                int day = rs.getInt(2);
                String time = rs.getString(3);

                ResultSet groupRS = SQL.spareStatement.executeQuery("SELECT name FROM groups WHERE groupID = "+groupID+";");
                String groupName="";
                while(groupRS.next())
                    groupName=groupRS.getString(1);

                str[day-1+indices[day-1]*7] = groupName+", "+time;
                indices[day-1]++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }



        adapter_nur = new GridViewAdapter(this, str);


        //Binding ImageAdapter to the GridView
        gv.setAdapter(adapter_nur);
        //End of Binding ImageAdapter to the GridView
        //---------------------------------CLICK LISTENER FOR GRIDVIEW-----------
        // ----------------------
        this.gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
            }
        });
        //---------------------------------CLICK LISTENER FOR GRIDVIEW---------------------------------
    }

    ///////////////////////////////////
    /*public void addHour(View v){
        String subject= ((EditText) findViewById(R.id.editText)).getText().toString();
        String day= ((EditText) findViewById(R.id.editText2)).getText().toString();
        int hour= Integer.parseInt(((EditText) findViewById(R.id.editText3)).getText().toString());

        if (Manager.type==0)
            Manager.system.addLesson(day,hour,subject);
        else {
            Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int year=cal.get(Calendar.YEAR);
            int month=cal.get(Calendar.MONTH)+1;
            int dayplus=cal.get(Calendar.DAY_OF_MONTH)+3;

            ((TeacherSystem) Manager.system).uploadTask(new Task(subject, subject, subject, dayplus, month, year, Day.getDayNumber(day), hour));
        }
    }*/

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
        if(position!=positionInMenu) {
            openActivityFromMenu(position + 1);
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.lessons_table, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_lessons_table_student, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((LessonsTableActivityStudent) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
