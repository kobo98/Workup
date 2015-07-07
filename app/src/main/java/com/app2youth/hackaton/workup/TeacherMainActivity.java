package com.app2youth.hackaton.workup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;


public class TeacherMainActivity extends BasicClass
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
        setContentView(R.layout.activity_all_groups_teacher);

        mNavigationDrawerFragment = (NavigationDrawerFragmentTeacher)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        positionInMenu = 0;

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,(DrawerLayout) findViewById(R.id.drawer_layout), positionInMenu, this);


    }

    public ListView mDrawerListView;



    public int[] savedGroupIDs = null;

    @Override
    public void onStart(){
        super.onStart();
	    start();
    }

	public void start(){
		new LoadData().execute((Void)null);
	}


	private class LoadData extends AsyncTask<Void, Void, Void> {
		ProgressDialog pdLoading = new ProgressDialog(TeacherMainActivity.this);
		String[][] listViewData;
		String[] names;

		@Override
		protected Void doInBackground(Void... ints){
			String[][] dataToListView = null;

			String groupIDs = null;
			try {

				ResultSet rsgr = SQL.statement.executeQuery("SELECT groups FROM teachers WHERE teacherID = "+Controller.getTeacherIDByPhone(BasicClass.phone)+";");
				while(rsgr.next())
					groupIDs=rsgr.getString(1);

				groupIDs = groupIDs.replace(";", ",");
				if (groupIDs.length()>0)
					groupIDs = groupIDs.substring(0, groupIDs.length()-1);
				else
					groupIDs="-1";
				dataToListView = new String[groupIDs.split(",").length][4];
				savedGroupIDs = new int[groupIDs.split(",").length];

				if (groupIDs.equals("-1")){
					dataToListView = new String[0][4];
					savedGroupIDs = new int[0];
				}

				ResultSet rs = SQL.statement.executeQuery("SELECT name, groupID FROM groups WHERE groupID IN ("+groupIDs+");");

				int index=0;
				while(rs.next()){
					String name  = rs.getString(1);
					int groupID = rs.getInt(2);

					dataToListView[index] = new String[]{name,"","","0x00ff00"};
					savedGroupIDs[index] = groupID;
					index++;
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

			String[] arrayOfNames = new String[dataToListView.length];
			for(int i=0;i<dataToListView.length;i++){
				arrayOfNames[i]=dataToListView[i][0];
			}

			listViewData = dataToListView;
			names = arrayOfNames;

			return null;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();

			pdLoading.setMessage("\tLoading data...");
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(Void result) {
			pdLoading.dismiss();

			mDrawerListView = (ListView) findViewById(R.id.list_of_groups);
			mDrawerListView.setDivider(new ColorDrawable(0xff11a7ff));
			mDrawerListView.setDividerHeight(1);
			mDrawerListView.setAdapter(new BasicClass.HWArrayAdapter(TeacherMainActivity.this,listViewData,names));

			mDrawerListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View v,int position, long id) {
					Log.d("Pressed: ",""+position);
					final int groupID = savedGroupIDs[position];
					final AlertDialog.Builder alert = new AlertDialog.Builder(TeacherMainActivity.this);

					alert.setTitle("Add class");
					//alert.setMessage("Message");

					LinearLayout layout = new LinearLayout(TeacherMainActivity.this);
					layout.setOrientation(LinearLayout.VERTICAL);

					final RadioButton[] day = new RadioButton[7];
					final RadioGroup rg = new RadioGroup(TeacherMainActivity.this);
					rg.setOrientation(RadioGroup.VERTICAL);

					for(int i=0; i<7; i++){
						day[i]  = new RadioButton(TeacherMainActivity.this);
						day[i].setId(i+1);
						rg.addView(day[i]);

					}
					day[0].setText("Sunday");
					day[1].setText("Monday");
					day[2].setText("Tuesday");
					day[3].setText("Wednesday");
					day[4].setText("Thursday");
					day[5].setText("Friday");
					day[6].setText("Saturday");

					layout.addView(rg);

					final EditText hour = new EditText(TeacherMainActivity.this);
					hour.setInputType(InputType.TYPE_DATETIME_VARIATION_TIME);
					hour.setHint("Hour");
					hour.setFocusable(false);

					hour.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							Calendar mcurrentTime = Calendar.getInstance();
							int currentHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
							int currentMinute = mcurrentTime.get(Calendar.MINUTE);

							TimePickerDialog mTimePicker;
							mTimePicker = new TimePickerDialog(TeacherMainActivity.this, new TimePickerDialog.OnTimeSetListener() {
								@Override
								public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
									hour.setText( selectedHour + ":" + selectedMinute);
								}
							}, currentHour, currentMinute, true);
							mTimePicker.setTitle("Select Time");
							mTimePicker.show();
						}
					});

					layout.addView(hour);


					alert.setView(layout);

					alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							final String classHour = hour.getText().toString();
							final int day = rg.getCheckedRadioButtonId();
							Log.d("ADD CLASS", day+", "+classHour);

							if (!classHour.equals("")){
								new AddClass().execute(""+groupID, ""+day, classHour);
							}
							else{

								Toast t = Toast.makeText(getApplicationContext(), "Choose hour!", Toast.LENGTH_LONG);
								t.show();
							}

						}
					});
					alert.show();

				}
			});

		}
	}

	private class AddClass extends AsyncTask<String, Void, Void> {
		ProgressDialog pdLoading = new ProgressDialog(TeacherMainActivity.this);

		@Override
		protected Void doInBackground(String... input){
			try {
				Controller.addClass(Integer.parseInt(input[0]), Integer.parseInt(input[1]), input[2]);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();

			pdLoading.setMessage("\tAdding class...");
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(Void result) {
			pdLoading.dismiss();
			Toast t = Toast.makeText(getApplicationContext(), "Class added", Toast.LENGTH_LONG);
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

	    if (position!=positionInMenu){
            openActivityFromMenuTeacher(position+1);
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
            getMenuInflater().inflate(R.menu.teacher_main, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_all_groups_teacher, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((TeacherMainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
