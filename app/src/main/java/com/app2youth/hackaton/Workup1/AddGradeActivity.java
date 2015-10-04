package com.app2youth.hackaton.Workup1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;


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

		groupSpinner = (Spinner)findViewById(R.id.choose_group);
		examDecription = (EditText) findViewById(R.id.exam_description);

		new LoadGroups().execute((Void)null);
	}

	ListView gradesList;
	static String selectedGroupSpinner=null;
	Spinner groupSpinner;
	EditText examDecription;
	GradesListAdapter gradesAdapter;


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

			return items;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();

			pdLoading.setMessage("\t" + getString(R.string.loading_groups));
			pdLoading.setCanceledOnTouchOutside(false);
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(String[] result) {
			pdLoading.dismiss();

			groupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
					selectedGroupSpinner = groupSpinner.getSelectedItem().toString();
					if (!selectedGroupSpinner.equals(getString(R.string.spinner_select_group)))
						new LoadStudentsList().execute((Void) null);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});

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
	private class LoadStudentsList extends AsyncTask<Void, Void, String[]> {
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
			String[] items = new String[list.length];
			for (int i=0; i<list.length; i++){
				items[i]=list[i];
			}

			return items;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();

			pdLoading.setMessage("\t" + getString(R.string.loading_students));
			pdLoading.setCanceledOnTouchOutside(false);
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(String[] result) {
			pdLoading.dismiss();


			gradesAdapter = new GradesListAdapter(result, studentIDs, AddGradeActivity.this);

			//handle listview and assign adapter
			gradesList = (ListView) findViewById(R.id.list_of_grades);
			gradesList.setAdapter(gradesAdapter);


		}
	}


	public void addGrade(View v){

		if (selectedGroupSpinner.equals(getString(R.string.spinner_select_group))){
			Toast t = Toast.makeText(getApplicationContext(), getString(R.string.select_group_alert), Toast.LENGTH_LONG);
			t.show();
		}
		else if (examDecription.getText().toString().equals("")){
			Toast t = Toast.makeText(getApplicationContext(), getString(R.string.enter_exam_description_alert), Toast.LENGTH_LONG);
			t.show();
		}
		else{


			String description = examDecription.getText().toString();
			/*
			pdLoading = new ProgressDialog(AddGradeActivity.this);
			pdLoading.setMessage("\t" + getString(R.string.adding_grade));
			pdLoading.setCanceledOnTouchOutside(false);
			pdLoading.show();
			*/
			/*
			for (int i=0; i<gradesAdapter.selected.length; i++){
				if (gradesAdapter.selected[i]){
					String grade = ""+gradesAdapter.grades[i];
					new AddGrade().execute(gradesAdapter.ids[i]+"", description, grade);
				}
			}
			*/
			new AddGrade().execute(description);

			//finish();
			//Toast t = Toast.makeText(getApplicationContext(), getString("Grades added successfully!"), Toast.LENGTH_LONG);
			//t.show();
		}


	}

	private class AddGrade extends AsyncTask<String, Void, Void> {
		ProgressDialog pdLoading = new ProgressDialog(AddGradeActivity.this);

		@Override
		protected Void doInBackground(String... ints){
			try {


				for (int i=0; i<gradesAdapter.selected.length; i++){
					if (gradesAdapter.selected[i]){
						int grade = gradesAdapter.grades[i];

						Controller.addGrade(
								gradesAdapter.ids[i],
								Controller.getGroupIDByNameAndPhone(selectedGroupSpinner, BasicClass.phone),
								ints[0],
								grade,
								AddGradeActivity.this
						);
					}
				}


			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();


			pdLoading.setMessage("\t" + getString(R.string.adding_grade));
			pdLoading.setCanceledOnTouchOutside(false);
			pdLoading.show();

		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(Void result) {
			pdLoading.dismiss();
			Toast t = Toast.makeText(getApplicationContext(), getString(R.string.added_grade), Toast.LENGTH_LONG);
			t.show();

			finish();
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








	public class GradesListAdapter extends BaseAdapter implements ListAdapter {
		private String[] list;
		private Context context;
		public static final int defaultGrade=0;

		public int[] grades;
		public int[] ids;
		public boolean[] selected;

		public GradesListAdapter(String[] list, int[] ids, Context context) {
			this.list = list;
			this.context = context;

			grades = new int[list.length];
			for (int i=0; i<grades.length; i++){
				grades[i]=defaultGrade;
			}
			this.ids = ids;
			selected = new boolean[list.length];
			for (int i=0; i<selected.length; i++){
				selected[i]=true;
			}
		}
		@Override
		public int getCount() {
			return list.length;
		}
		@Override
		public Object getItem(int pos) {
			return list[pos];
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
				view = inflater.inflate(R.layout.grade_list_layout, null);
			}

			final CheckBox included = (CheckBox)view.findViewById(R.id.include_checkbox);
			included.setChecked(true);
			included.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					selected[position] = included.isChecked();
				}
			});


			final TextView student = (TextView)view.findViewById(R.id.student_name);
			student.setText(list[position]);

			final NumberPicker grade = (NumberPicker) view.findViewById(R.id.grade_picker);
			grade.setMinValue(0);
			grade.setMaxValue(100);
			grade.setValue(defaultGrade);
			grade.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
				@Override
				public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
					grades[position] = newVal;
				}
			});

			return view;
		}
	}







}
