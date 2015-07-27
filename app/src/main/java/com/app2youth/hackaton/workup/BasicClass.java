package com.app2youth.hackaton.workup;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.provider.SyncStateContract;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BasicClass extends ActionBarActivity
		implements NavigationDrawerFragmentStudent.NavigationDrawerCallbacks {


	public static String phone;
	public static boolean teacher;
	public static boolean registeringTeacher;

	protected int positionInMenu;

	public static String startingPath;

	public void saveInt(String name, int num) {
		SharedPreferences mPreferences = getSharedPreferences("WorkUp", MODE_PRIVATE);
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putInt(name, num);
		editor.commit();
	}

	public int getInt(String name) {
		SharedPreferences mPreferences = getSharedPreferences("WorkUp", MODE_PRIVATE);
		return mPreferences.getInt(name, -1);
	}

	public void saveString(String name, String str) {
		SharedPreferences mPreferences = getSharedPreferences("WorkUp", MODE_PRIVATE);
		SharedPreferences.Editor editor = mPreferences.edit();
		editor.putString(name, str);
		editor.commit();
	}

	public String getString(String name) {
		SharedPreferences mPreferences = getSharedPreferences("WorkUp", MODE_PRIVATE);
		return mPreferences.getString(name, "");
	}




	public void openMainActivity(View v) {
		startActivity(new Intent(this, StudentAllTasksActivity.class));
	}

	public void openGraphActivity(View v) {
		startActivity(new Intent(this, GraphActivity.class));
	}


	public void openAddStudentsToGroupActivity(View v) {
		startActivity(new Intent(this, AddStudentsToGroupActivity.class));
	}

	public void openAddGroupActivity(View v) {
		startActivity(new Intent(this, AddGroupActivity.class));
	}

	public void openAllGradesActivity(View v) {
		startActivity(new Intent(this, AllGradesActivity.class));
	}

	public void openLessonTableActivity(View v) {
		startActivity(new Intent(this, LessonsTableActivityStudent.class));
	}

	public void openAddGradeActivity(View v) {
		startActivity(new Intent(this, AddGradeActivity.class));
	}

	public void openLessonTableActivityTeacher(View v) {
		startActivity(new Intent(this, LessonsTableActivityTeacher.class));
	}

	public void openAddTaskActivity(View v) {
		startActivity(new Intent(this, AddTaskActivity.class));
	}

	public void openTeacherMainActivity(){
		startActivity(new Intent(this, TeacherMainActivity.class));
	}
	@Override
	public void onNavigationDrawerItemSelected(int position) {

	}

	public void openActivityFromMenu(int position) {

		switch (position) {
			case 1:
				openMainActivity(new View(this));
				break;
			case 2:
				openAllGradesActivity(new View(this));
				break;
			case 3:
				openLessonTableActivity(new View(this));
				break;
		}
	}

	public void openActivityFromMenuTeacher(int position) {
		switch (position) {
			case 1:
				openTeacherMainActivity();
				break;
			case 2:
				openAddTaskActivity(new View(this));
				break;
			case 3:
				openAddStudentsToGroupActivity(new View(this));
				break;
			case 4:
				openLessonTableActivityTeacher(new View(this));
				break;
			case 5:
				openAddGroupActivity(new View(this));
				break;
			case 6:
				openGraphActivity(new View(this));
				break;
			case 7:
				openAddGradeActivity(new View(this));
				break;

		}
	}

	private boolean contains(String[] s1, String s){
		for (String ss1:s1)
			if (ss1.equals(s))
				return true;
		return false;
	}

	boolean finishedFirstCheck=false;

	public void checkForNewGroups() {
		finishedFirstCheck=false;
		Thread check = new Thread(){
			public void run(){
				try {
					Log.d("Signed groups", getString("groups"));
					String[] signedGroups = getString("groups").split(";");
					final String[] groups = Controller.getGroupIDsForStudent(phone);
					final String[] groupNames = new String[groups.length];
					for (int i=0; i<groupNames.length; i++){
						groupNames[i]=Controller.getGroupName(Integer.parseInt(groups[i]));
					}
					finishedFirstCheck=true;
					if (groups==null || groups.length==0){
						return;
					}
					else{
						for (int i=0; i<groups.length; i++){
							if (!contains(signedGroups, groups[i])){

								final int index = i;
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										final AlertDialog.Builder alert = new AlertDialog.Builder(BasicClass.this);
										alert.setTitle("You have abeen added to: "+groupNames[index]);
										alert.setMessage("Would you like to stay in the group?");

										final String newGroup = groups[index];
										alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int whichButton) {
												saveString("groups",getString("groups")+newGroup+";");
												Toast t = Toast.makeText(getApplicationContext(), "Group added", Toast.LENGTH_LONG);
												t.show();
											}
										});
										alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int whichButton) {


												Thread helper = new Thread() {
													public void run() {
														finishedFirstCheck=false;

														try {
															Controller.deleteStudentFromGroup(Controller.getStudentIDByPhone(phone), Integer.parseInt(newGroup));
														} catch (SQLException e) {
															e.printStackTrace();
														}

														finishedFirstCheck=true;
													}
												};
												helper.start();

												Toast t = Toast.makeText(getApplicationContext(), "Group declined", Toast.LENGTH_LONG);
												t.show();
											}
										});
										alert.show();
									}
								});

							}

						}
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		};
		check.start();

	}


	private class LoadGroups extends AsyncTask<Void, Void, Void> {
		//ProgressDialog pdLoading = new ProgressDialog(BasicClass.this);

		@Override
		protected Void doInBackground(Void... ints){

			return null;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();

			//pdLoading.setMessage("\tLoading groups...");
			//pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(Void result) {
			//pdLoading.dismiss();

		}
	}

	public void pushNotification(String title, String text, Class whereToBeTransferred, Class stackClass, int idOfNotification) {
		NotificationCompat.Builder mBuilder =
				new NotificationCompat.Builder(this)
						.setSmallIcon(R.drawable.ic_drawer)
						.setContentTitle(title)
						.setContentText(text);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(this, whereToBeTransferred);

		// The stack builder object will contain an artificial back stack for the
		// started Activity.
		// This ensures that navigating backward from the Activity leads out of
		// your application to the Home screen.
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(stackClass);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
				stackBuilder.getPendingIntent(
						0,
						PendingIntent.FLAG_UPDATE_CURRENT
				);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		// id allows you to update the notification later on.
		mNotificationManager.notify(idOfNotification, mBuilder.build());
	}

	public class HWArrayAdapter extends BaseAdapter {
		private Context context;
		private String[][] values;

		public HWArrayAdapter(Context context, String[][] objects) {
			this.context = context;
			this.values = objects;
		}

		@Override
		public int getCount() {
			return values.length;
		}

		@Override
		public Object getItem(int position) {
			return values[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			final View rowView = inflater.inflate(R.layout.hwlayout, parent, false);
			TextView subject = (TextView) rowView.findViewById(R.id.subject);
			TextView teacher = (TextView) rowView.findViewById(R.id.teacher);
			TextView do_date = (TextView) rowView.findViewById(R.id.do_date);
			ImageView symbol = (ImageView) rowView.findViewById(R.id.symbol);
			//((SwipeListView)parent).recycle(convertView, position);

			subject.setText(values[position][0]);
			teacher.setText(values[position][1]);
			do_date.setText(values[position][2]);

			symbol.setBackgroundColor(0xffffffff);


			return rowView;
		}
	}

	public class SchoolBagArrayAdapter extends ArrayAdapter<String> {
		private Context context;
		private String[][] values;

		public SchoolBagArrayAdapter(Context context, String[][] objects, String[] programs) {
			super(context, R.layout.hwlayout, programs);
			this.context = context;
			this.values = objects;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.subjectlayout, parent, false);
			TextView subject = (TextView) rowView.findViewById(R.id.subject);
			TextView teacher = (TextView) rowView.findViewById(R.id.teacher);
			TextView add1 = (TextView) rowView.findViewById(R.id.add1);
			TextView add2 = (TextView) rowView.findViewById(R.id.add2);
			TextView add3 = (TextView) rowView.findViewById(R.id.add3);
			TextView add4 = (TextView) rowView.findViewById(R.id.add4);
			subject.setText(values[position][0]);
			teacher.setText(values[position][1]);
			add1.setText("");
			add2.setText("");
			add3.setText("");
			add4.setText("");
			if (values[position].length > 2) {
				if (values[position][2] != null && values[position][2] != "" && values[position][2] != "Zereg") {
					add1.setText("  " + values[position][2]);
					if (values[position].length > 3) {
						if (values[position][3] != null && values[position][3] != "" && values[position][3] != "Zereg") {
							add2.setText("  " + values[position][3]);
							if (values[position].length > 4) {
								if (values[position][4] != null && values[position][4] != "" && values[position][4] != "Zereg") {
									add3.setText("  " + values[position][4]);
									if (values[position].length > 5) {
										if (values[position][5] != null && values[position][5] != "" && values[position][5] != "Zereg") {
											add4.setText("  " + values[position][5]);
										}
									}
								}
							}
						}
					}
				}
			}
			return rowView;
		}
	}




	public class GroupListAdapter extends BaseAdapter implements ListAdapter {
		private ArrayList<String> list = new ArrayList<String>();
		private Context context;


		public GroupListAdapter(ArrayList<String> list, Context context) {
			this.list = list;
			this.context = context;
		}


		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int pos) {
			return list.get(pos);
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
				view = inflater.inflate(R.layout.group_list_layout, null);
			}

			//Handle TextView and display string from your list
			TextView listItemText = (TextView)view.findViewById(R.id.list_item_string);
			listItemText.setText(list.get(position));

			//final TextView groupDescription = (TextView)view.findViewById(R.id.group_description);

			//Handle buttons and add onClickListeners
			Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);
			Button addBtn = (Button)view.findViewById(R.id.add_btn);

			listItemText.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					presentGroup(position);
					notifyDataSetChanged();
				}
			});
			deleteBtn.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					//list.remove(position);
					deleteGroupButton(position);
					notifyDataSetChanged();
				}
			});
			addBtn.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
					addClassButton(position);
					notifyDataSetChanged();
				}
			});

			return view;
		}
	}
	public void addClassButton(int position){

	}
	public void deleteGroupButton(int position){

	}
	public void presentGroup(int position){

	}

}
