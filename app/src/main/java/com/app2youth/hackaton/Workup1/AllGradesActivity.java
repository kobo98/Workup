package com.app2youth.hackaton.Workup1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.sql.SQLException;


public class AllGradesActivity extends BasicClass
        implements NavigationDrawerFragmentStudent.NavigationDrawerCallbacks {

    private ListView mDrawerListView;

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
        setContentView(R.layout.activity_all_grades);

        mNavigationDrawerFragment = (NavigationDrawerFragmentStudent)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        positionInMenu=1;

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout),positionInMenu,this);
    }

    @Override
    public void onStart(){
        super.onStart();
	    firstRun=false;
        new LoadGrades().execute((Void)null);
    }



	String[][] dataToListView = null;
	private class LoadGrades extends AsyncTask<Void, Void, Void> {
		ProgressDialog pdLoading = new ProgressDialog(AllGradesActivity.this);

		@Override
		protected Void doInBackground(Void... ints){
			String[] list = null;
			try {
				list = Controller.getGradesFromStudent(Controller.getStudentIDByPhone(phone));

				dataToListView = new String[list.length][4];
				for (int i=0; i<list.length; i++){
					int id = Integer.parseInt(list[i]);
					dataToListView[i] = new String[]{""+Controller.getGroupName(Controller.getGroupFromGrade(id)), Controller.getGradeDescription(id), ""+Controller.getGrade(id)};
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}






			return null;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();

			pdLoading.setMessage("\t"+getString(R.string.loading_groups));
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}

		@Override
		protected void onPostExecute(Void result) {
			pdLoading.dismiss();

			mDrawerListView = (ListView) findViewById(R.id.list_of_grades);
			mDrawerListView.setDivider(new ColorDrawable(0xff11a7ff));
			mDrawerListView.setDividerHeight(1);
			mDrawerListView.setAdapter(new BasicClass.HWArrayAdapter(AllGradesActivity.this,dataToListView));
		}
	}

	public void openGradesGraph(View v){
		openGradesGraphActivity(new View(AllGradesActivity.this));
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
        if(position!=positionInMenu) {
            openActivityFromMenu(position + 1);
        }
	    */
	    if (!firstRun) {
		    openActivityFromMenu(position + 1);
		    firstRun=false;
	    }
    }

    public void onSectionAttached(int number) {
	    mTitle = getString(R.string.student_title_section2);
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
            getMenuInflater().inflate(R.menu.all_grades, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_all_grades, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((AllGradesActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
