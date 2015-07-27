package com.app2youth.hackaton.workup;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class AddTaskActivity extends BasicClass
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
        setContentView(R.layout.activity_add_task);

        mNavigationDrawerFragment = (NavigationDrawerFragmentTeacher)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        positionInMenu=1;

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout),positionInMenu,this);
    }




    private void updateLabel(EditText edittext, Calendar myCalendar) {

        String myFormat = "yy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edittext.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onStart(){
        super.onStart();
	    firstRun=false;
	    start();
    }

	public void start(){
		selectedSpinner=getString(R.string.spinner_select_group);

		dropdown = (Spinner)findViewById(R.id.groupSpinner);
		taskName = (EditText) findViewById(R.id.taskName);
		taskDescription = (EditText) findViewById(R.id.taskDescription);
		filingDate = (EditText) findViewById(R.id.filingDate);

		myCalendar = Calendar.getInstance();
		datePicker = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
			                      int dayOfMonth) {
				myCalendar.set(Calendar.YEAR, year);
				myCalendar.set(Calendar.MONTH, monthOfYear);
				myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				updateLabel(filingDate, myCalendar);
			}
		};

		new LoadGroups().execute((Void)null);
	}

	static String selectedSpinner=null;
	DatePickerDialog.OnDateSetListener datePicker;
	Calendar myCalendar;
	Spinner dropdown;
	EditText taskName;
	EditText taskDescription;
	EditText filingDate;


	private class LoadGroups extends AsyncTask<Void, Void, String[]> {
		ProgressDialog pdLoading = new ProgressDialog(AddTaskActivity.this);

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
			dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
					selectedSpinner=dropdown.getSelectedItem().toString();
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

			dropdown.setAdapter(new ArrayAdapter<String>(AddTaskActivity.this, android.R.layout.simple_spinner_item, result));
		}
	}


	public void openDateDialog(View v){
		InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		im.hideSoftInputFromWindow(filingDate.getWindowToken(), 0);

		new DatePickerDialog(AddTaskActivity.this, datePicker, myCalendar
				.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
				myCalendar.get(Calendar.DAY_OF_MONTH)).show();

	}


	public void createTask(View v){
		Log.d("Add Task: ", selectedSpinner + ", " + taskName.getText().toString()+", "+taskDescription.getText().toString()+", "+filingDate.getText().toString());
		if (selectedSpinner.equals(getString(R.string.spinner_select_group))){
			Toast t = Toast.makeText(getApplicationContext(), getString(R.string.select_group_alert), Toast.LENGTH_LONG);
			t.show();
		}
		else if (taskName.getText().toString().equals("")){
			Toast t = Toast.makeText(getApplicationContext(), getString(R.string.enter_task_name_alert), Toast.LENGTH_LONG);
			t.show();
		}
		else if (filingDate.getText().toString().equals("")){
			Toast t = Toast.makeText(getApplicationContext(), getString(R.string.enter_filing_date_alert), Toast.LENGTH_LONG);
			t.show();
		}
		else{
			new AddTask().execute((Void)null);
		}
	}


	private class AddTask extends AsyncTask<Void, Void, Void> {
		ProgressDialog pdLoading = new ProgressDialog(AddTaskActivity.this);

		@Override
		protected Void doInBackground(Void... ints){
			try {
				Controller.addTask(
						Controller.getGroupIDByNameAndPhone(selectedSpinner, BasicClass.phone),
						taskName.getText().toString(),
						taskDescription.getText().toString(),
						filingDate.getText().toString()
				);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();

			pdLoading.setMessage("\t"+getString(R.string.adding_task));
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(Void result) {
			pdLoading.dismiss();
			taskName.setText("");
			taskDescription.setText("");
			filingDate.setText("");
			Toast t = Toast.makeText(getApplicationContext(), getString(R.string.added_task), Toast.LENGTH_LONG);
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
        /*
	    if(position!=positionInMenu) {
            openActivityFromMenuTeacher(position + 1);
        }
        */
	    if (!firstRun) {
		    openActivityFromMenuTeacher(position + 1);
		    firstRun=false;
	    }
    }

    public void onSectionAttached(int number) {
	    mTitle = getString(R.string.teacher_title_section2);
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
            getMenuInflater().inflate(R.menu.add_task, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_add_task, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

	        ((AddTaskActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
