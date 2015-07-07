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
import android.provider.SyncStateContract;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

		public HWArrayAdapter(Context context, String[][] objects, String[] programs) {
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
			Random rnd = new Random();
			if (rnd.nextBoolean()) {
				symbol.setBackgroundColor(0xff00ff00);
			} else {
				symbol.setBackgroundColor(0xffff0000);
			}

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
}
