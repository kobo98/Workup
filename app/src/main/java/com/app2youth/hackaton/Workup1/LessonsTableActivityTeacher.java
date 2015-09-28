package com.app2youth.hackaton.Workup1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Random;


public class LessonsTableActivityTeacher extends BasicClass
        implements NavigationDrawerFragmentTeacher.NavigationDrawerCallbacks {

	/*
    private GridView gv;
    private GridViewAdapter adapter_nur;
    */
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

        positionInMenu = 1;

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), positionInMenu, this);



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

	/*
	private class LoadTimetable extends AsyncTask<Void, Void, Void> {
		ProgressDialog pdLoading = new ProgressDialog(LessonsTableActivityTeacher.this);

		@Override
		protected Void doInBackground(Void... ints){
			LessonsTableActivityTeacher.this.gv = (GridView) findViewById(R.id.gridView1);
			String[] str = new String[7+7*9];


			str[0]=getString(R.string.day_7);
			str[1]=getString(R.string.day_6);
			str[2]=getString(R.string.day_5);
			str[3]=getString(R.string.day_4);
			str[4]=getString(R.string.day_3);
			str[5]=getString(R.string.day_2);
			str[6]=getString(R.string.day_1);
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
					int day = 8-rs.getInt(2);
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

			pdLoading.setMessage("\t"+getString(R.string.loading_timetable));
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
	*/


	private class LoadTimetable extends AsyncTask<Void, Void, ArrayList<String>> {
		ProgressDialog pdLoading = new ProgressDialog(LessonsTableActivityTeacher.this);

		@Override
		protected ArrayList<String> doInBackground(Void... ints){
			ArrayList<String> timetable = new ArrayList<String>();

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
					int day = rs.getInt(2);//8-rs.getInt(2);
					String time = rs.getString(3);

					ResultSet groupRS = SQL.spareStatement.executeQuery("SELECT name,teacher FROM groups WHERE groupID = " + groupID + ";");
					String groupName="";
					String teacherName="";
					while(groupRS.next()) {
						groupName = groupRS.getString(1);
						int teacher = groupRS.getInt(2);
					}

					String field = groupName+","+time+","+day;
					timetable.add(field);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

			return timetable;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();

			pdLoading.setMessage("\t" + getString(R.string.loading_timetable));
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(ArrayList<String> result) {
			pdLoading.dismiss();

			HorizontalScrollView hsv = (HorizontalScrollView) findViewById(R.id.horizontal_scroll);
			TextView button = (TextView) findViewById(R.id.son);
			int x, y;
			x = button.getLeft();
			y = button.getTop();
			hsv.scrollTo(x, y);


			for (String block:result){
				String[] data = block.split(",");

				String groupName=data[0];
				String time=data[1];
				int day=Integer.parseInt(data[2]);

				String[] timePieces=time.split(":");
				int hour = Integer.parseInt(timePieces[0]);
				int minute = Integer.parseInt(timePieces[1]);
				if (hour<6) {
					hour += 12;
					time=hour+":"+minute;
				}


				RelativeLayout layout = (RelativeLayout)findViewById(R.id.timetable_layout);

				if (timePieces[0].length()<2)
					time="0"+time;
				if (timePieces[1].length()<2)
					time+="0";

				TextView newClass = new TextView(LessonsTableActivityTeacher.this);
				newClass.setTextSize(16);
				newClass.setText(Html.fromHtml(
						"<font size='18' color='#0f20ff'>" + time + "</font><br><br><font size='26' color='#000000'>    " + groupName + "</font>"
				), TextView.BufferType.SPANNABLE);


				newClass.setBackground(getDrawable(R.drawable.timetable_class));
				GradientDrawable bgShape = (GradientDrawable)newClass.getBackground();
				bgShape.setColor(Color.rgb(
						(int) (new Random(groupName.hashCode()).nextDouble() * 255),
						(int) (new Random(new Random(groupName.hashCode()).nextInt()).nextDouble() * 255),
						(int) (new Random(new Random( new Random(groupName.hashCode()).nextInt() ).nextInt()).nextDouble() * 255)
				));

				layout.addView(newClass);

				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) newClass.getLayoutParams();
				params.leftMargin=223+300*(7-day) - groupName.length()*5;
				params.topMargin=135+150*(hour-6) + (int)((double)150/(double)60)*minute;
				newClass.setLayoutParams(params);

				Log.d("Supposable location", params.leftMargin+", "+params.topMargin+", hour: "+hour+", day: "+day);
			}


			Log.d("TAPLE", "UPZATED");
		}
	}


	public void addGroupMenuButton(MenuItem bs){
		openAddGroupActivity(new View(LessonsTableActivityTeacher.this));
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

	        HorizontalScrollView hsv = (HorizontalScrollView) rootView.findViewById(R.id.horizontal_scroll);
	        TextView button = (TextView) rootView.findViewById(R.id.son);
	        int x, y;
	        x = button.getLeft();
	        y = button.getTop();
	        hsv.scrollTo(x, y);

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
