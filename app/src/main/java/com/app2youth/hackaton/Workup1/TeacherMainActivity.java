package com.app2youth.hackaton.Workup1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


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
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), positionInMenu, this);



    }
	ListView lView;
    public int[] savedGroupIDs = null;

    @Override
    public void onStart(){
        super.onStart();
	    firstRun=false;
	    start();
    }

	public void start(){
		//new LoadData().execute((Void)null);
		new LoadDataAdapter().execute((Void) null);
	}

	private class LoadDataAdapter extends AsyncTask<Void, Void, Void> {
		ProgressDialog pdLoading = new ProgressDialog(TeacherMainActivity.this);
		final ArrayList<String> data = new ArrayList<String>();

		@Override
		protected Void doInBackground(Void... ints){
			try {
				String[] groupIDs = Controller.getGroupIDsForTeacher(phone);
				savedGroupIDs = new int[groupIDs.length];
				for (int i=0; i<groupIDs.length; i++){
					savedGroupIDs[i] = Integer.parseInt(groupIDs[i]);
					data.add(Controller.getGroupName(savedGroupIDs[i]));

				}

			} catch (SQLException e) {
				e.printStackTrace();
			}



			return null;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();

			pdLoading.setMessage("\t"+getString(R.string.loading_data));
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(Void result) {
			pdLoading.dismiss();


			GroupListAdapter adapter = new GroupListAdapter(data, TeacherMainActivity.this);

			//handle listview and assign adapter
			lView = (ListView)findViewById(R.id.list_of_groups);
			lView.setAdapter(adapter);

			if (data.size()==0){
				AlertDialog.Builder alert = new AlertDialog.Builder(TeacherMainActivity.this);

				alert.setTitle(getString(R.string.title_no_groups));
				alert.setMessage(getString(R.string.notification_would_you_like_to_create_a_group));


				alert.setNegativeButton(getString(R.string.no), null);
				alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						openAddGroupActivity(new View(TeacherMainActivity.this));
					}
				});

				alert.show();

			}

		}
	}

	public void deleteGroupButton(int position){
		final int groupID = savedGroupIDs[position];
		AlertDialog.Builder alert = new AlertDialog.Builder(TeacherMainActivity.this);

		alert.setTitle(getString(R.string.delete_group_question));
		alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				new DeleteGroup().execute(groupID);
			}
		});

		alert.setNegativeButton(getString(R.string.no), null);
		alert.show();
	}

	public void plusButton(final int position){

		final AlertDialog.Builder alert = new AlertDialog.Builder(TeacherMainActivity.this);

		//alert.setTitle(getString(R.string.add_to_group));
		//alert.setMessage("Message");

		LinearLayout layout = new LinearLayout(TeacherMainActivity.this);
		layout.setOrientation(LinearLayout.VERTICAL);

		final Button addClassButton = new Button(TeacherMainActivity.this);
		addClassButton.setText(getString(R.string.add_class_title));
		layout.addView(addClassButton);


		final Button addStudentsToGroupButton = new Button(TeacherMainActivity.this);
		addStudentsToGroupButton.setText(getString(R.string.add_students_to_class));
		layout.addView(addStudentsToGroupButton);


		final Button newGradeButton = new Button(TeacherMainActivity.this);
		newGradeButton.setText(getString(R.string.add_grade));
		layout.addView(newGradeButton);

		alert.setView(layout);

		alert.setNegativeButton(getString(R.string.cancel), null);
		final AlertDialog ad = alert.show();

		addClassButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				addClass(position);
				ad.dismiss();
			}
		});


		addStudentsToGroupButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				BasicClass.selectedGroup=((GroupListAdapter)lView.getAdapter()).list.get(position);
				Log.d("Selected group name", BasicClass.selectedGroup);
				openAddStudentsToGroupActivity(new View(TeacherMainActivity.this), true);
				ad.dismiss();
			}
		});

		newGradeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				BasicClass.selectedGroup=((GroupListAdapter)lView.getAdapter()).list.get(position);
				openAddGradeActivity(new View(TeacherMainActivity.this), true);
				ad.dismiss();
			}
		});

	}

	public void addClass(int position){
		final int groupID = savedGroupIDs[position];
		Log.d("Selected item", lView.getSelectedItemPosition()+"");

		final AlertDialog.Builder alert = new AlertDialog.Builder(TeacherMainActivity.this);

		alert.setTitle(getString(R.string.add_class_title));
		//alert.setMessage("Message");

		LinearLayout layout = new LinearLayout(TeacherMainActivity.this);
		layout.setOrientation(LinearLayout.VERTICAL);

		final RadioButton[] day = new RadioButton[7];
		final RadioGroup rg = new RadioGroup(TeacherMainActivity.this);
		rg.setOrientation(RadioGroup.VERTICAL);

		for (int i = 0; i < 7; i++) {
			day[i] = new RadioButton(TeacherMainActivity.this);
			day[i].setId(i + 1);

			rg.addView(day[i]);

		}
		day[0].setText(getString(R.string.day_1));
		day[1].setText(getString(R.string.day_2));
		day[2].setText(getString(R.string.day_3));
		day[3].setText(getString(R.string.day_4));
		day[4].setText(getString(R.string.day_5));
		day[5].setText(getString(R.string.day_6));
		day[6].setText(getString(R.string.day_7));

		LinearLayout.LayoutParams lpView = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		lpView.gravity = Gravity.RIGHT;

		layout.addView(rg, lpView);

		final EditText hour = new EditText(TeacherMainActivity.this);
		hour.setInputType(InputType.TYPE_DATETIME_VARIATION_TIME);
		hour.setHint(getString(R.string.hint_hour));
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
						hour.setText(selectedHour + ":" + selectedMinute);
					}
				}, currentHour, currentMinute, true);
				mTimePicker.setTitle(getString(R.string.select_time_title));
				mTimePicker.show();
			}
		});

		layout.addView(hour);


		alert.setView(layout);

		alert.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				final String classHour = hour.getText().toString();
				final int day = rg.getCheckedRadioButtonId();
				Log.d("ADD CLASS", day + ", " + classHour);

				if (!classHour.equals("")) {
					new AddClass().execute("" + groupID, "" + day, classHour);
				} else {

					Toast t = Toast.makeText(getApplicationContext(), getString(R.string.choose_hour_alert), Toast.LENGTH_LONG);
					t.show();
				}

			}
		});

		alert.setNegativeButton(getString(R.string.cancel), null);
		alert.show();
	}


	private class DeleteGroup extends AsyncTask<Integer, Void, Void> {
		ProgressDialog pdLoading = new ProgressDialog(TeacherMainActivity.this);

		@Override
		protected Void doInBackground(Integer... input){
			try {
				Controller.deleteGroup(input[0]);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();

			pdLoading.setMessage("\t"+getString(R.string.deleting_group));
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(Void result) {
			pdLoading.dismiss();
			Toast t = Toast.makeText(getApplicationContext(), getString(R.string.group_deleted), Toast.LENGTH_LONG);
			t.show();

			openTeacherMainActivity();
		}
	}








	public void presentGroup(int position){
		group=savedGroupIDs[position];
		new ShowGroupInfo().execute();
	}
	int group = -1;

	private class ShowGroupInfo extends AsyncTask<Void, Void, String> {
		ProgressDialog pdLoading = new ProgressDialog(TeacherMainActivity.this);

		@Override
		protected String doInBackground(Void... input){
			String info = "";
			try {
				info+=getString(R.string.teachers_title)+" ";
				info+=Controller.getTeacherName(Controller.getGroupTeacher(group));
				info+="\r\n";
				info+="\r\n";
				info+=getString(R.string.students_title)+" ";

				String[] studentIDs = Controller.getStudentsFromGroup(group);
				for (String id:studentIDs)
					info+=Controller.getStudentName(Integer.parseInt(id))+", ";

				if (studentIDs.length>0)
					info = info.substring(0,info.length()-2);

			} catch (SQLException e) {
				e.printStackTrace();
			}

			return info;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();

			pdLoading.setMessage("\t"+getString(R.string.loading_group_info));
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(String result) {
			pdLoading.dismiss();
			AlertDialog.Builder alert = new AlertDialog.Builder(TeacherMainActivity.this);

			alert.setTitle(getString(R.string.group_info_title));
			alert.setMessage(result);

			alert.show();

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

			pdLoading.setMessage("\t"+getString(R.string.adding_class));
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(Void result) {
			pdLoading.dismiss();
			Toast t = Toast.makeText(getApplicationContext(), getString(R.string.added_class), Toast.LENGTH_LONG);
			t.show();
		}
	}


	public static int selectedTask=-1;
	String groupName=null;
	public void showTasks(int position){
		groupName=((GroupListAdapter)lView.getAdapter()).list.get(position);
		final int groupID = savedGroupIDs[position];
		new ShowTasks().execute(groupID, position);
	}
	private class ShowTasks extends AsyncTask<Integer, Void, HashMap<Integer,String>>{
		ProgressDialog pdLoading = new ProgressDialog(TeacherMainActivity.this);
		int position;
		@Override
		protected HashMap<Integer,String> doInBackground(Integer... inputs){
			position=inputs[1];
			HashMap<Integer,String> taskNames = new HashMap<Integer,String>();
			try {
				String tasks = Controller.getTasksFromGroup(inputs[0]);
				String[] taskList = tasks.split(";");
				if (!tasks.equals(""))
					for (int i=0; i<taskList.length; i++){
						int id=Integer.parseInt(taskList[i]);
						taskNames.put(id, Controller.getTaskTitle(id));
					}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return taskNames;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();

			pdLoading.setMessage("\t" + getString(R.string.loading_tasks));
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(HashMap<Integer,String> result) {
			pdLoading.dismiss();

			final AlertDialog.Builder alert = new AlertDialog.Builder(TeacherMainActivity.this);

			alert.setTitle(getString(R.string.select_task)+": "+groupName);
			//alert.setMessage("Message");

			LinearLayout layout = new LinearLayout(TeacherMainActivity.this);
			layout.setOrientation(LinearLayout.VERTICAL);

			//here should be

			alert.setView(layout);

			alert.setNegativeButton(getString(R.string.cancel), null);
			final AlertDialog ad = alert.show();

			for (final int id:result.keySet()){
				LinearLayout taskRow = new LinearLayout(TeacherMainActivity.this);
				taskRow.setOrientation(LinearLayout.HORIZONTAL);



				Button deleteTask = new Button(TeacherMainActivity.this, null, android.R.attr.buttonStyleSmall);
				deleteTask.setTypeface(null, Typeface.BOLD);
				deleteTask.setTextSize(18);
				deleteTask.setText("-");
				deleteTask.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						selectedTask = id;
						Thread deleteTask = new Thread(new Runnable() {
							public void run() {
								try {
									Controller.deleteTask(id, savedGroupIDs[position]);
								} catch (SQLException e) {
									e.printStackTrace();
								}
							}
						});
						deleteTask.start();

						ad.dismiss();

					}
				});
				taskRow.addView(deleteTask);

				Button taskInfo = new Button(TeacherMainActivity.this, null, android.R.attr.buttonStyleSmall);
				taskInfo.setTypeface(null, Typeface.BOLD);
				taskInfo.setTextSize(18);
				taskInfo.setText("?");
				taskInfo.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						openTaskInfoDialog(id);
						ad.dismiss();
					}
				});
				taskRow.addView(taskInfo);

				Button taskButton = new Button(TeacherMainActivity.this, null, android.R.attr.buttonStyleSmall);
				taskButton.setText(getString(R.string.button_text_replies));
				taskButton.setTypeface(null, Typeface.BOLD);
				taskButton.setTextSize(18);

				taskButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						selectedTask = id;
						openCommentsActivity(new View(TeacherMainActivity.this));
						ad.dismiss();
					}
				});
				taskRow.addView(taskButton);


				TextView taskName = new TextView(TeacherMainActivity.this);
				taskName.setText(result.get(id));
				taskName.setTypeface(null, Typeface.BOLD);
				taskName.setTextSize(18);

				LinearLayout.LayoutParams relativeParams = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
				relativeParams.leftMargin=100;
				relativeParams.topMargin=20;

				taskRow.addView(taskName, relativeParams);


				layout.addView(taskRow);



				View v = new View(TeacherMainActivity.this);
				v.setLayoutParams(new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.MATCH_PARENT,
						5
				));
				v.setBackgroundColor(Color.parseColor("#B3B3B3"));

				layout.addView(v);
			}

			Button addTaskButton = new Button(TeacherMainActivity.this);
			addTaskButton.setText(getString(R.string.add_task));
			addTaskButton.setTypeface(null, Typeface.BOLD);
			addTaskButton.setTextSize(18);
			addTaskButton.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					BasicClass.selectedGroup=((GroupListAdapter)lView.getAdapter()).list.get(position);
					Log.d("Selected group name", BasicClass.selectedGroup);
					openAddTaskActivity(new View(TeacherMainActivity.this), true);
					ad.dismiss();
				}
			});
			layout.addView(addTaskButton);

		}
	}

	public void addGroupMenuButton(MenuItem bs){
		openAddGroupActivity(new View(TeacherMainActivity.this));
	}

	int infoedTaskID=-1;
	public void openTaskInfoDialog(int taskID){
		infoedTaskID=taskID;
		new LoadTaskInfo().execute((Void)null);
	}


	private class LoadTaskInfo extends AsyncTask<Void, Void, Void> {
		ProgressDialog pdLoading = new ProgressDialog(TeacherMainActivity.this);

		int finishers;
		ArrayList<String> finisherNames;
		String taskName;
		@Override
		protected Void doInBackground(Void... ints){
			try {
				finishers = Controller.getTaskFinisherAmount(infoedTaskID);
				finisherNames = Controller.getTaskFinishers(infoedTaskID);
				taskName = Controller.getTaskTitle(infoedTaskID);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();
			pdLoading.setMessage("\t" + getString(R.string.loading_data));
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(Void result) {
			pdLoading.dismiss();

			final AlertDialog.Builder alert = new AlertDialog.Builder(TeacherMainActivity.this);

			alert.setTitle(getString(R.string.info_on_task) +" "+ taskName);
			alert.setMessage(finishers + " " + getString(R.string.students_finished_task)+". "+getString(R.string.finisher_name_list)+"\n");

			LinearLayout layout = new LinearLayout(TeacherMainActivity.this);
			layout.setOrientation(LinearLayout.VERTICAL);


			for (String studentName:finisherNames){
				TextView student = new TextView(TeacherMainActivity.this);
				student.setText(studentName);
				student.setTextSize(16);
				layout.addView(student);
			}

			alert.setView(layout);

			alert.setNegativeButton(getString(R.string.cancel), null);
			alert.show();
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
	    if(!firstRun){
		    openActivityFromMenuTeacher(position+1);
		    firstRun=false;
	    }
    }

    public void onSectionAttached(int number) {
	    mTitle = getString(R.string.teacher_title_section1);
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
