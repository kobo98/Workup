package com.app2youth.hackaton.Workup1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.sql.SQLException;


public class AddGradeActivity extends BasicClass
		implements NavigationDrawerFragmentTeacher.NavigationDrawerCallbacks {

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
		setContentView(R.layout.activity_add_grade);

		mNavigationDrawerFragment = (NavigationDrawerFragmentTeacher)
				getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		positionInMenu=3;

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(
				R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout), positionInMenu, this);
	}


	@Override
	public void onStart(){
		super.onStart();
		firstRun=false;
		start();
	}


	public void start(){
		selectedGroupSpinner=getString(R.string.spinner_select_group);
		selectedStudentSpinner=getString(R.string.spinner_select_student);

		groupSpinner = (Spinner)findViewById(R.id.choose_group);
		studentSpinner = (Spinner)findViewById(R.id.choose_student);
		examDecription = (EditText) findViewById(R.id.exam_description);
		grade = (EditText) findViewById(R.id.grade);

		new LoadGroups().execute((Void)null);
	}

	static String selectedGroupSpinner=null;
	static String selectedStudentSpinner=null;
	static int selectedStudentID=-1;
	Spinner groupSpinner;
	Spinner studentSpinner;
	EditText examDecription;
	EditText grade;






	private class LoadGroups extends AsyncTask<Void, Void, String[]> {
		ProgressDialog pdLoading = new ProgressDialog(AddGradeActivity.this);

		@Override
		protected String[] doInBackground(Void... ints){
			String[] list = null;
			try {
				list = Controller.getGroupNamesForTeacher(BasicClass.phone);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			String[] items = new String[list.length+1];
			items[0]=getString(R.string.spinner_select_group);
			for (int i=0; i<list.length; i++){
				items[i+1]=list[i];
			}
			groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
					selectedGroupSpinner=groupSpinner.getSelectedItem().toString();
					if (!selectedGroupSpinner.equals(getString(R.string.spinner_select_group)))
						new LoadStudents().execute((Void)null);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});
			return items;
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
		protected void onPostExecute(String[] result) {
			pdLoading.dismiss();

			studentSpinner.setAdapter(new ArrayAdapter<String>(AddGradeActivity.this, android.R.layout.simple_spinner_item, new String[]{getString(R.string.select_group_first)}));
			groupSpinner.setAdapter(new ArrayAdapter<String>(AddGradeActivity.this, android.R.layout.simple_spinner_item, result));
			if (BasicClass.groupSelected) {
				for (int i = 0; i < result.length; i++)
					if (result[i].equals(BasicClass.selectedGroup)) {
						groupSpinner.setSelection(i);
						Log.d("dropdown selected", "dropdown selected");
					}

			}
		}
	}


	int[] studentIDs=null;
	private class LoadStudents extends AsyncTask<Void, Void, String[]> {
		ProgressDialog pdLoading = new ProgressDialog(AddGradeActivity.this);

		@Override
		protected String[] doInBackground(Void... ints){
			String[] list = null;
			try {
				String[] students = Controller.getStudentsFromGroup(Controller.getGroupIDByNameAndPhone(selectedGroupSpinner, BasicClass.phone));
				list = new String[students.length];
				studentIDs = new int[students.length];
				for (int i=0; i<students.length; i++){
					list[i] = Controller.getStudentName(Integer.parseInt(students[i]))+" "+Controller.getStudentLastName(Integer.parseInt(students[i]));
					studentIDs[i]=Integer.parseInt(students[i]);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
			String[] items = new String[list.length+1];
			items[0]=getString(R.string.spinner_select_student);
			for (int i=0; i<list.length; i++){
				items[i+1]=list[i];
			}
			studentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
					selectedStudentSpinner=studentSpinner.getSelectedItem().toString();
					if (position>0)
						selectedStudentID=studentIDs[position-1];
					else
						selectedStudentID=-1;
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});
			return items;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();

			pdLoading.setMessage("\t"+getString(R.string.loading_students));
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(String[] result) {
			pdLoading.dismiss();

			studentSpinner.setAdapter(new ArrayAdapter<String>(AddGradeActivity.this, android.R.layout.simple_spinner_item, result));
		}
	}


	public void addGrade(View v){

		if (selectedGroupSpinner.equals(getString(R.string.spinner_select_group))){
			Toast t = Toast.makeText(getApplicationContext(), getString(R.string.select_group_alert), Toast.LENGTH_LONG);
			t.show();
		}
		else if (selectedStudentSpinner.equals(getString(R.string.spinner_select_student))){
			Toast t = Toast.makeText(getApplicationContext(), getString(R.string.select_student_alert), Toast.LENGTH_LONG);
			t.show();
		}
		else if (examDecription.getText().toString().equals("")){
			Toast t = Toast.makeText(getApplicationContext(), getString(R.string.enter_exam_description_alert), Toast.LENGTH_LONG);
			t.show();
		}
		else if (grade.getText().toString().equals("")){
			Toast t = Toast.makeText(getApplicationContext(), getString(R.string.enter_grade_alert), Toast.LENGTH_LONG);
			t.show();
		}
		else if (Integer.parseInt(grade.getText().toString())<0 || Integer.parseInt(grade.getText().toString())>100){
			Toast t = Toast.makeText(getApplicationContext(), getString(R.string.grade_range_alert), Toast.LENGTH_LONG);
			t.show();
		}
		else{
			new AddGrade().execute((Void)null);
		}
	}


	private class AddGrade extends AsyncTask<Void, Void, Void> {
		ProgressDialog pdLoading = new ProgressDialog(AddGradeActivity.this);

		@Override
		protected Void doInBackground(Void... ints){
			try {
				Controller.addGrade(
						selectedStudentID,
						Controller.getGroupIDByNameAndPhone(selectedGroupSpinner, BasicClass.phone),
						examDecription.getText().toString(),
						Integer.parseInt(grade.getText().toString())
				);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();

			pdLoading.setMessage("\t"+getString(R.string.adding_grade));
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(Void result) {
			pdLoading.dismiss();
			grade.setText("");
			Toast t = Toast.makeText(getApplicationContext(), getString(R.string.added_grade), Toast.LENGTH_LONG);
			t.show();
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

		if (!firstRun) {
			openActivityFromMenuTeacher(position + 1);
			firstRun=false;
		}
	}

	public void onSectionAttached(int number) {
		mTitle = getString(R.string.teacher_title_section7);
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
			getMenuInflater().inflate(R.menu.add_grade, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_add_grade, container, false);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((AddGradeActivity) activity).onSectionAttached(
					getArguments().getInt(ARG_SECTION_NUMBER));
		}
	}

}
