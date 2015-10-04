package com.app2youth.hackaton.Workup1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Random;


public class LessonsTableActivityTeacher extends BasicClass
        implements NavigationDrawerFragmentTeacher.NavigationDrawerCallbacks {


	DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
	ViewPager mViewPager;

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
        mNavigationDrawerFragment.setUp(
		        R.id.navigation_drawer,
		        (DrawerLayout) findViewById(R.id.drawer_layout), positionInMenu, this);
    }

	public static ArrayList<String> timetableData = new ArrayList<String>();

    @Override
    public void onStart(){
        super.onStart();
	    firstRun=false;
	    start();
    }

	public void start(){
		new LoadTimetable().execute((Void)null);
	}

	private class LoadTimetable extends AsyncTask<Void, Void, ArrayList<String>> {
		ProgressDialog pdLoading = new ProgressDialog(LessonsTableActivityTeacher.this);

		@Override
		protected ArrayList<String> doInBackground(Void... ints){
			ArrayList<String> timetable = new ArrayList<String>();

			String classIDs = null;
			try {

				int[] indices = new int[]{1,1,1,1,1,1,1};

				classIDs = BasicClass.teacher? Controller.getListOfClassesForTeacher(BasicClass.phone) : Controller.getListOfClassesForStudent(BasicClass.phone);

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
					int teacher=-1;
					while(groupRS.next()) {
						groupName = groupRS.getString(1);
						teacher = groupRS.getInt(2);
					}

					String field = day+","+groupName+","+time+","+Controller.getTeacherNameSpare(teacher);
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
			pdLoading.setCanceledOnTouchOutside(false);
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(ArrayList<String> result) {
			pdLoading.dismiss();

			timetableData=result;

			mDemoCollectionPagerAdapter = new DemoCollectionPagerAdapter(getSupportFragmentManager());

			ActionBar actionBar = getSupportActionBar();

			actionBar.setDisplayHomeAsUpEnabled(true);

			mViewPager = (ViewPager) findViewById(R.id.pager);
			mViewPager.setAdapter(mDemoCollectionPagerAdapter);
			mViewPager.setCurrentItem(6);
		}
	}



	public void addGroupMenuButton(MenuItem bs){
		openAddGroupActivity(new View(LessonsTableActivityTeacher.this));
	}
	public void deleteUser(MenuItem bs){
		final AlertDialog.Builder alert = new AlertDialog.Builder(LessonsTableActivityTeacher.this);

		alert.setTitle(getString(R.string.delete_user));
		alert.setMessage(getString(R.string.delete_user_confirmation));


		alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				new DeleteTeacher().execute((Void) null);
			}
		});
		alert.setNegativeButton(getString(R.string.no), null);

		alert.show();
	}

	private class DeleteTeacher extends AsyncTask<Void, Void, Void> {
		ProgressDialog pdLoading = new ProgressDialog(LessonsTableActivityTeacher.this);
		@Override
		protected Void doInBackground(Void... in){
			try {
				Controller.deleteTeacher(BasicClass.id);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();
			pdLoading.setMessage("\t" + getString(R.string.deleting_user));
			pdLoading.setCanceledOnTouchOutside(false);
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(Void result) {
			startActivity(new Intent(LessonsTableActivityTeacher.this, SplashActivity.class));
			finish();
			pdLoading.dismiss();
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










	public class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {

		public DemoCollectionPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int i) {
			Fragment fragment = new DemoObjectFragment();
			Bundle args = new Bundle();
			args.putInt(DemoObjectFragment.ARG_OBJECT, i + 1); // Our object is just an integer :-P
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// For this contrived example, we have a 100-object collection.
			return 7;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			String day = "";

			switch (position){
				case (0):
					day = getString(R.string.day_7);
					break;
				case (1):
					day = getString(R.string.day_6);
					break;
				case (2):
					day = getString(R.string.day_5);
					break;
				case (3):
					day = getString(R.string.day_4);
					break;
				case (4):
					day = getString(R.string.day_3);
					break;
				case (5):
					day = getString(R.string.day_2);
					break;
				case (6):
					day = getString(R.string.day_1);
					break;

			}
			return day;
		}
	}


	public static class DemoObjectFragment extends Fragment {

		public static final String ARG_OBJECT = "object";

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_collection_object_timetable_student, container, false);
			Bundle args = getArguments();

			/*
			ArrayList<String> dummy = new ArrayList<String>();
			dummy.add(" (1,???????,11:30,?????,");
			dummy.add(" (2,???????,13:30,?????");
			dummy.add(" (3,Math,11:30,Estella");
			dummy.add(" (4,Math,13:30,Estella");
			*/


			ArrayList<String> thisDay = new ArrayList<String>();
			int count=1;
			for (int i=0; i<timetableData.size(); i++){
				if (timetableData.get(i).startsWith(""+(8-args.getInt(ARG_OBJECT)))){

					String data = " ("+count+""+timetableData.get(i).substring(1);
					String[] dataBroken = data.split(",");

					String[] timePieces=dataBroken[2].split(":");
					String time = dataBroken[2];

					if (timePieces[0].length()<2)
						time="0"+time;
					if (timePieces[1].length()<2)
						time+="0";


					thisDay.add(dataBroken[0]+","+dataBroken[1]+","+time+", ");
					count++;
				}
			}

			TimetableListAdapter timetableAdapter = new TimetableListAdapter(thisDay, container.getContext());

			//handle listview and assign adapter
			ListView gradesList = (ListView) rootView.findViewById(R.id.list_for_timetable);
			gradesList.setAdapter(timetableAdapter);



			return rootView;
		}





		public class TimetableListAdapter extends BaseAdapter implements ListAdapter {
			private ArrayList<String> list;
			private Context context;
			public static final int defaultGrade=0;



			public TimetableListAdapter(ArrayList<String> list, Context context) {
				this.list = list;
				this.context = context;
			}
			@Override
			public int getCount() {
				return list.size();
			}
			@Override
			public Object getItem(int pos) {
				return list.get(pos);
			}
			@Override
			public long getItemId(int pos) {
				return 0;//list.get(pos).getId();
				//just return 0 if your list items do not have an Id variable.
			}

			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				View view = convertView;
				if (view == null) {
					LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					view = inflater.inflate(R.layout.timetable_list_layout, null);
				}

				String[] data = list.get(position).split(",");

				final TextView indexNumber = (TextView)view.findViewById(R.id.index_number);
				indexNumber.setText(data[0]);

				final TextView subject = (TextView)view.findViewById(R.id.subject);
				subject.setText(data[1]);

				final TextView time = (TextView)view.findViewById(R.id.time);
				time.setText(data[2]);

				final TextView teacher = (TextView)view.findViewById(R.id.teacher);
				teacher.setText(data[3]);

				return view;
			}
		}



	}






}
