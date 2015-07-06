package com.app2youth.hackaton.workup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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




        final EditText groupName = (EditText) findViewById(R.id.groupName);
        groupName.setHint("Enter group name");

        final EditText addPhone = (EditText) findViewById(R.id.add_phone);
        addPhone.setHint("Enter phone");



        final TextView phoneList = (TextView) findViewById(R.id.phone_list);
        phoneList.setText("");


        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
	            Thread helper = new Thread(){
		            public void run(){


			            if (!addPhone.getText().toString().equals(""))
				            try {
					            final String phone = addPhone.getText().toString();
					            if (Controller.studentExists(phone)){
						            if (phoneList.getText().toString().length()>0)
							            phoneList.setText(phoneList.getText().toString()+", "+phone);
						            else
							            phoneList.setText(phone);
						            addPhone.setText("");
					            }
					            else {


						            final AlertDialog.Builder alert = new AlertDialog.Builder(AddGroupActivity.this);
						            alert.setTitle("Add student with number "+phone);

						            LinearLayout layout = new LinearLayout(AddGroupActivity.this);
						            layout.setOrientation(LinearLayout.VERTICAL);

						            final EditText name = new EditText(AddGroupActivity.this);
						            name.setHint("Enter name");
						            layout.addView(name);

						            final EditText fname = new EditText(AddGroupActivity.this);
						            fname.setHint("Enter last name");
						            layout.addView(fname);

						            alert.setView(layout);
						            alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
							            public void onClick(DialogInterface dialog, int whichButton) {
								            Thread helper = new Thread() {
									            public void run() {
										            try {
											            Controller.addStudent(name.getText().toString(), name.getText().toString(), phone);
										            } catch (SQLException e) {
											            e.printStackTrace();
										            }
										            runOnUiThread(new Runnable() {
											            @Override
											            public void run() {
												            if (phoneList.getText().toString().length()>0)
													            phoneList.setText(phoneList.getText().toString()+", "+phone);
												            else
													            phoneList.setText(phone);
												            addPhone.setText("");


												            Toast t = Toast.makeText(getApplicationContext(), "Student added to database", Toast.LENGTH_LONG);
												            t.show();
											            }
										            });

									            }
								            };
								            helper.start();
							            }
						            });

						            runOnUiThread(new Runnable() {
							            @Override
							            public void run() {
								            alert.show();
							            }
						            });


                        /*
                        Toast t = Toast.makeText(getApplicationContext(), "Phone not recognised", Toast.LENGTH_LONG);
                        t.show();
                        */
					            }

				            } catch (SQLException e) {
					            e.printStackTrace();
				            }
		            }
	            };
	            helper.start();

            }
        });

        ImageView submit = (ImageView) findViewById(R.id.add_group_button);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
	            Thread helper = new Thread() {
		            public void run() {
			            String phonesString = phoneList.getText().toString();
			            String group = groupName.getText().toString();
			            Log.d("Add Group: ", group + ", " + phonesString);
			            if (group.equals("")){
				            Toast t = Toast.makeText(getApplicationContext(), "Please enter group", Toast.LENGTH_LONG);
				            t.show();
			            }

			            else{
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
					            runOnUiThread(new Runnable() {
						            @Override
						            public void run() {
							            groupName.setText("");
							            addPhone.setText("");
							            phoneList.setText("");
							            Toast t = Toast.makeText(getApplicationContext(), "Group added", Toast.LENGTH_LONG);
							            t.show();
						            }
					            });


				            } catch (SQLException e) {
					            e.printStackTrace();
				            }
			            }
		            }
	            };
	            helper.start();

            }
        });

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
