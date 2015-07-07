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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;


public class AddGroupActivity extends BasicClass
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
        setContentView(R.layout.activity_add_group);

        mNavigationDrawerFragment = (NavigationDrawerFragmentTeacher)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        positionInMenu=4;

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout), positionInMenu, this);
    }



    @Override
    public void onStart() {
        super.onStart();
	    start();
    }

	public void start(){
		phoneList = (TextView) findViewById(R.id.phone_list);
		addPhone = (EditText) findViewById(R.id.add_phone);
		groupName = (EditText) findViewById(R.id.groupName);
	}

	EditText newStudentName;
	EditText newStudentFName;

	TextView phoneList;
	EditText addPhone;
	EditText groupName;


	public void addStudentButton(View view){
		if (!((EditText) findViewById(R.id.add_phone)).getText().toString().equals("")){
			new AddStudent().execute((Void)null);
		}
	}




	private class AddStudent extends AsyncTask<Void, Void, Boolean> {
		ProgressDialog pdLoading = new ProgressDialog(AddGroupActivity.this);
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
				if (phoneList.getText().toString().length()>0)
					phoneList.setText(phoneList.getText().toString()+", "+addPhone.getText().toString());
				else
					phoneList.setText(addPhone.getText().toString());
				addPhone.setText("");
			}
			else{
				final String phone = addPhone.getText().toString();

				final AlertDialog.Builder alert = new AlertDialog.Builder(AddGroupActivity.this);
				alert.setTitle("Add student with number "+phone);

				LinearLayout layout = new LinearLayout(AddGroupActivity.this);
				layout.setOrientation(LinearLayout.VERTICAL);

				newStudentName = new EditText(AddGroupActivity.this);
				newStudentName.setHint("Enter name");
				layout.addView(newStudentName);

				newStudentFName = new EditText(AddGroupActivity.this);
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
		ProgressDialog pdLoading = new ProgressDialog(AddGroupActivity.this);
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




	public void addGroupButton(View view){


		String phonesString = phoneList.getText().toString();
		String group = groupName.getText().toString();

		Log.d("Add Group: ", group + ", " + phonesString);
		if (group.equals("")){
			Toast t = Toast.makeText(getApplicationContext(), "Please enter group name", Toast.LENGTH_LONG);
			t.show();
		}
		else{
			new AddGroup().execute((Void)null);
		}
	}


	private class AddGroup extends AsyncTask<Void, Void, Void> {
		ProgressDialog pdLoading = new ProgressDialog(AddGroupActivity.this);

		@Override
		protected Void doInBackground(Void... ints){
			String phonesString = phoneList.getText().toString();
			String group = groupName.getText().toString();
			try {

				Controller.addGroup(Controller.getTeacherIDByPhone(BasicClass.phone), group);

				if (phonesString.length()!=0){
					String[] phones = phoneList.getText().toString().split(", ");

					for (String phone:phones){
						Controller.addStudentToGroup(
								Controller.getStudentIDByPhone(phone),
								Controller.getGroupIDByNameAndPhone(group,BasicClass.phone)
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

			pdLoading.setMessage("\tAdding group...");
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(Void result) {
			pdLoading.dismiss();

			groupName.setText("");
			addPhone.setText("");
			phoneList.setText("");
			Toast t = Toast.makeText(getApplicationContext(), "Group added", Toast.LENGTH_LONG);
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
            View rootView = inflater.inflate(R.layout.fragment_add_group, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((AddGroupActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
