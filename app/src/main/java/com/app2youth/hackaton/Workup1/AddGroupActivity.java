package com.app2youth.hackaton.Workup1;

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
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

        positionInMenu=1;

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
		        R.id.navigation_drawer,
		        (DrawerLayout) findViewById(R.id.drawer_layout), positionInMenu, this);



    }



    @Override
    public void onStart() {
        super.onStart();
	    firstRun=false;
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
			String phonetext = addPhone.getText().toString();
			new AddStudent().execute(phonetext);
		}
	}




	private class AddStudent extends AsyncTask<String, Void, Boolean> {
		ProgressDialog pdLoading = new ProgressDialog(AddGroupActivity.this);
		@Override
		protected Boolean doInBackground(String... ints){

			final String phone = ints[0];
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

			pdLoading.setMessage("\t" + getString(R.string.adding_student));
			pdLoading.setCanceledOnTouchOutside(false);
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
				alert.setTitle(getString(R.string.add_student_title)+phone);

				LinearLayout layout = new LinearLayout(AddGroupActivity.this);
				layout.setOrientation(LinearLayout.VERTICAL);

				newStudentName = new EditText(AddGroupActivity.this);
				newStudentName.setHint(getString(R.string.enter_student_name_for_registration));
				newStudentName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
				layout.addView(newStudentName);

				newStudentFName = new EditText(AddGroupActivity.this);
				newStudentFName.setHint(getString(R.string.enter_student_last_name_for_registration));
				newStudentFName.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
				layout.addView(newStudentFName);

				alert.setView(layout);
				alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						if (newStudentName.getText().toString().equals("")){
							Toast t = Toast.makeText(getApplicationContext(), getString(R.string.enter_student_name_alert), Toast.LENGTH_LONG);
							t.show();
						}
						else if (newStudentFName.getText().toString().equals("")){
							Toast t = Toast.makeText(getApplicationContext(), getString(R.string.enter_student_last_name_alert), Toast.LENGTH_LONG);
							t.show();
						}
						else {
							String newname=newStudentName.getText().toString(), fname = newStudentFName.getText().toString(), phone=addPhone.getText().toString();
							new SignupAndAddStudent().execute(newname, fname, phone);
						}
					}
				});

				alert.show();

			}
		}
	}


	private class SignupAndAddStudent extends AsyncTask<String, Void, Void> {
		ProgressDialog pdLoading = new ProgressDialog(AddGroupActivity.this);
		@Override
		protected Void doInBackground(String... ints){

			try {
				Controller.addStudent(ints[0], ints[1], ints[2]);
			} catch (SQLException e) {
				e.printStackTrace();
			}

			return null;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();

			pdLoading.setMessage("\t" + getString(R.string.signing_student_up));
			pdLoading.setCanceledOnTouchOutside(false);
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


			Toast t = Toast.makeText(getApplicationContext(), getString(R.string.signed_student_up), Toast.LENGTH_LONG);
			t.show();
		}
	}




	public void addGroupButton(View view){


		String phonesString = phoneList.getText().toString();
		String group = groupName.getText().toString();

		Log.d("Add Group: ", group + ", " + phonesString);
		if (group.equals("")){
			Toast t = Toast.makeText(getApplicationContext(), getString(R.string.enter_group_name_alert), Toast.LENGTH_LONG);
			t.show();
		}
		else{
			String phonelist = phoneList.getText().toString(), groupname = groupName.getText().toString();
			new AddGroup().execute(phonelist, groupname);
		}
	}


	private class AddGroup extends AsyncTask<String, Void, Void> {
		ProgressDialog pdLoading = new ProgressDialog(AddGroupActivity.this);

		@Override
		protected Void doInBackground(String... ints){
			String phonesString = ints[0];
			String group = ints[1];
			try {

				Controller.addGroup(Controller.getTeacherIDByPhone(BasicClass.phone), group);

				if (phonesString.length()!=0){
					String[] phones = ints[0].split(", ");

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

			pdLoading.setMessage("\t"+getString(R.string.adding_group));
			pdLoading.setCanceledOnTouchOutside(false);
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
			Toast t = Toast.makeText(getApplicationContext(), getString(R.string.added_group), Toast.LENGTH_LONG);
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
