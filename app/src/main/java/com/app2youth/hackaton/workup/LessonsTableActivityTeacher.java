package com.app2youth.hackaton.workup;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.SQLException;


public class LessonsTableActivityTeacher extends BasicClass
        implements NavigationDrawerFragmentTeacher.NavigationDrawerCallbacks {


    private GridView gv;
    private GridViewAdapter adapter_nur;
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragmentTeacher mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lessons_table_teacher);

        mNavigationDrawerFragment = (NavigationDrawerFragmentTeacher)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        positionInMenu = 3;

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,(DrawerLayout) findViewById(R.id.drawer_layout), positionInMenu, this);
    }



    @Override
    public void onStart(){
        super.onStart();
	    firstRun=false;
	    start();
    }

	public void start(){
		new LoadTimetable().execute((Void)null);
	}

	private class LoadTimetable extends AsyncTask<Void, Void, Void> {
		ProgressDialog pdLoading = new ProgressDialog(LessonsTableActivityTeacher.this);

		@Override
		protected Void doInBackground(Void... ints){
			LessonsTableActivityTeacher.this.gv = (GridView) findViewById(R.id.gridView1);
			String[] str = new String[7+7*9];
			str[0]=getString(R.string.day_1);
			str[1]=getString(R.string.day_2);
			str[2]=getString(R.string.day_3);
			str[3]=getString(R.string.day_4);
			str[4]=getString(R.string.day_5);
			str[5]=getString(R.string.day_6);
			str[6]=getString(R.string.day_7);
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



			adapter_nur = new GridViewAdapter(LessonsTableActivityTeacher.this, str);

			return null;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();

			pdLoading.setMessage("\tLoading timetable...");
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(Void result) {
			pdLoading.dismiss();
			if (gv!=null)
				gv.setAdapter(adapter_nur);
			Log.d("TAPLE", "UPZATED");
		}
	}

	boolean firstRun=true;
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
	    /*
        if (position!=positionInMenu){
            openActivityFromMenuTeacher(position+1);
        }
	    */
	    if (!firstRun) {
		    openActivityFromMenuTeacher(position + 1);
		    firstRun=false;
	    }
    }

    public void onSectionAttached(int number) {
	    mTitle = getString(R.string.teacher_title_section4);
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
            getMenuInflater().inflate(R.menu.lessons_table_activity_teacher, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_lessons_table_teacher, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((LessonsTableActivityTeacher) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
