package com.app2youth.hackaton.workup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;


public class AddStudentsToGroupActivity extends BasicClass
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
        setContentView(R.layout.activity_add_students_to_group);

        mNavigationDrawerFragment = (NavigationDrawerFragmentTeacher)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        Log.d("Msg","1");

        positionInMenu=2;

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout),positionInMenu,this);
        Log.d("Msg","2");
    }

    static String selectedSpinner="Select group";

    @Override
    public void onStart(){
        super.onStart();
	    start();
    }

	public void start(){
		dropdown = (Spinner)findViewById(R.id.spinner);
		phoneList = (TextView) findViewById(R.id.phone_list);
		addPhone = (EditText) findViewById(R.id.add_phone);

		new LoadGroups().execute((Void)null);
	}


	Spinner dropdown;
	EditText newStudentName;
	EditText newStudentFName;

	TextView phoneList;
	EditText addPhone;

	private class LoadGroups extends AsyncTask<Void, Void, String[]> {
		ProgressDialog pdLoading = new ProgressDialog(AddStudentsToGroupActivity.this);

		@Override
		protected String[] doInBackground(Void... ints){
			String[] list = null;
			try {
				list = Controller.getGroupNamesForTeacher(BasicClass.phone);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			String[] items = new String[list.length+1];
			items[0]="Select group";
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

			pdLoading.setMessage("\tLoading groups...");
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(String[] result) {
			pdLoading.dismiss();

			dropdown.setAdapter(new ArrayAdapter<String>(AddStudentsToGroupActivity.this, android.R.layout.simple_spinner_item, result));
		}
	}


	public void addStudentButton(View v){
		if (!((EditText) findViewById(R.id.add_phone)).getText().toString().equals("")){
			new AddStudent().execute((Void)null);
		}
	}

	private class AddStudent extends AsyncTask<Void, Void, Boolean> {
		ProgressDialog pdLoading = new ProgressDialog(AddStudentsToGroupActivity.this);
		@Override
		protected Boolean doInBackground(Void... ints){

			final String phone = addPhone.getText().toString();
			try {
				if (Controller.studentExists(phone)){

					return true;
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			return false;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();

			pdLoading.setMessage("\tAdding student...");
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(Boolean result) {
			pdLoading.dismiss();
			if (result) {
				if (phoneList.getText().toString().length() > 0)
					phoneList.setText(phoneList.getText().toString() + ", " + addPhone.getText().toString());
				else
					phoneList.setText(addPhone.getText().toString());
				addPhone.setText("");
			}
			else{

				final String phone = addPhone.getText().toString();

				final AlertDialog.Builder alert = new AlertDialog.Builder(AddStudentsToGroupActivity.this);
				alert.setTitle("Add student with number "+phone);

				LinearLayout layout = new LinearLayout(AddStudentsToGroupActivity.this);
				layout.setOrientation(LinearLayout.VERTICAL);

				newStudentName = new EditText(AddStudentsToGroupActivity.this);
				newStudentName.setHint("Enter name");
				layout.addView(newStudentName);

				newStudentFName = new EditText(AddStudentsToGroupActivity.this);
				newStudentFName.setHint("Enter last name");
				layout.addView(newStudentFName);

				alert.setView(layout);
				alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (newStudentName.getText().toString().equals("")){
							Toast t = Toast.makeText(getApplicationContext(), "Please enter student's name", Toast.LENGTH_LONG);
							t.show();
						}
						else if (newStudentFName.getText().toString().equals("")){
							Toast t = Toast.makeText(getApplicationContext(), "Please enter student's last name", Toast.LENGTH_LONG);
							t.show();
						}
						else
							new SignupAndAddStudent().execute((Void) null);
					}
				});

				alert.show();

			}
		}
	}


	private class SignupAndAddStudent extends AsyncTask<Void, Void, Void> {
		ProgressDialog pdLoading = new ProgressDialog(AddStudentsToGroupActivity.this);
		@Override
		protected Void doInBackground(Void... ints){

			try {
				Controller.addStudent(newStudentName.getText().toString(), newStudentFName.getText().toString(), addPhone.getText().toString());
			} catch (SQLException e) {
				e.printStackTrace();
			}

			return null;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();

			pdLoading.setMessage("\tRegistering student...");
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(Void result) {
			pdLoading.dismiss();

			if (phoneList.getText().toString().length()>0)
				phoneList.setText(phoneList.getText().toString()+", "+addPhone.getText().toString());
			else
				phoneList.setText(addPhone.getText().toString());
			addPhone.setText("");


			Toast t = Toast.makeText(getApplicationContext(), "Student added to database", Toast.LENGTH_LONG);
			t.show();
		}
	}


	public void addStudentsToGroupButton(View v){
		String phonesString = phoneList.getText().toString();

		Log.d("Add students to group: ",selectedSpinner+", "+phonesString);
		if (selectedSpinner.equals("Select group")){
			Toast t = Toast.makeText(getApplicationContext(), "Please select group", Toast.LENGTH_LONG);
			t.show();
		}
		else if (phonesString.length()==0){
			Toast t = Toast.makeText(getApplicationContext(), "Please enter at least one phone number", Toast.LENGTH_LONG);
			t.show();
		}
		else{
			new AddStudentsToGroup().execute((Void)null);
		}
	}

	private class AddStudentsToGroup extends AsyncTask<Void, Void, Void> {
		ProgressDialog pdLoading = new ProgressDialog(AddStudentsToGroupActivity.this);

		@Override
		protected Void doInBackground(Void... ints){
			String[] phones = phoneList.getText().toString().split(", ");

			for (String phone:phones){
				try {
					Controller.addStudentToGroup(
							Controller.getStudentIDByPhone(phone),
							Controller.getGroupIDByNameAndPhone(selectedSpinner, BasicClass.phone)
					);
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}

			return null;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();

			pdLoading.setMessage("\tAdding students to group...");
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(Void result) {
			pdLoading.dismiss();

			dropdown.setSelection(0);
			addPhone.setText("");
			phoneList.setText("");
			Toast t = Toast.makeText(getApplicationContext(), "Students added to group", Toast.LENGTH_LONG);
			t.show();

		}
	}




    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
        if(position!=positionInMenu) {
            openActivityFromMenuTeacher(position + 1);
        }
    }

    public void onSectionAttached(int number) {
	    //start();
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
            case 4:
                mTitle="Section 4";
                break;
            case 5:
                mTitle = "Section 5";
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
            getMenuInflater().inflate(R.menu.add_group, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_add_students_to_group, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((AddStudentsToGroupActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
