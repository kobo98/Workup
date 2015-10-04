package com.app2youth.hackaton.Workup1;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import java.sql.ResultSet;
import java.sql.SQLException;


public class StudentAllTasksActivity extends BasicClass
        implements NavigationDrawerFragmentStudent.NavigationDrawerCallbacks {

    //private ListView mDrawerListView;
    private SwipeListView swipeListView;

    private NavigationDrawerFragmentStudent mNavigationDrawerFragment;
    private CharSequence mTitle;


    static int[] savedTaskIDs = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_tasks_student);


        mNavigationDrawerFragment = (NavigationDrawerFragmentStudent)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        positionInMenu=0;

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), positionInMenu, this);


    }



    @Override
    public void onStart(){
        super.onStart();

	    if (!isMyServiceRunning(PushService.class)){
		    Intent bindIntent = new Intent(this,PushService.class);
		    startService(bindIntent);
	    }

	    firstRun=false;
	    finishedFirstCheck=false;
	    checkForNewGroups();
	    start();
    }


	private boolean isMyServiceRunning(Class<?> serviceClass) {
		ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
			if (serviceClass.getName().equals(service.service.getClassName())) {
				return true;
			}
		}
		return false;
	}

	ProgressDialog pdLoading;
	public void start(){
		//new LoadTasks().execute((Void)null);
		pdLoading = new ProgressDialog(StudentAllTasksActivity.this);
		pdLoading.setMessage("\t"+getString(R.string.loading_tasks));
		pdLoading.setCanceledOnTouchOutside(false);
		pdLoading.show();

		final StudentAllTasksActivity a = this;
		Thread t = new Thread(){
			public void run(){
				while(!finishedFirstCheck)
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				loadTasksAndUpdate(a);
			}
		};
		t.start();


	}
	static int openPosition=0;


	public void sortTasks(String[][] tasks, int[] indices){
		while (true){
			boolean noSwaps=true;
			for (int i=0; i<indices.length-1; i++){

				if (!compare(tasks[i][2], tasks[i+1][2])){
					String[] tempstr = tasks[i].clone();
					tasks[i] = tasks[i+1].clone();
					tasks[i+1] = tempstr;

					int temp = indices[i];
					indices[i]=indices[i+1];
					indices[i+1]=temp;
					noSwaps=false;
				}

			}
			if (noSwaps)
				break;
		}
	}

	//if a is closer, returns true
	public boolean compare(String a, String b){
		String[] date1 = a.split("-");
		int y1=Integer.parseInt(date1[0]);
		int m1=Integer.parseInt(date1[1]);
		int d1=Integer.parseInt(date1[2]);

		String[] date2 = b.split("-");
		int y2=Integer.parseInt(date2[0]);
		int m2=Integer.parseInt(date2[1]);
		int d2=Integer.parseInt(date2[2]);

		if (y1>y2)
			return false;
		else if (y1<y2)
			return true;
		else{
			if (m1>m2)
				return false;
			else if (m1<m2)
				return true;
			else{
				if (d1>d2)
					return false;
				else
					return true;
			}
		}
	}



	public String getMonthName(int month){

		switch (month){
			case 1:
				return getString(R.string.month_1);
			case 2:
				return getString(R.string.month_2);
			case 3:
				return getString(R.string.month_3);
			case 4:
				return getString(R.string.month_4);
			case 5:
				return getString(R.string.month_5);
			case 6:
				return getString(R.string.month_6);
			case 7:
				return getString(R.string.month_7);
			case 8:
				return getString(R.string.month_8);
			case 9:
				return getString(R.string.month_9);
			case 10:
				return getString(R.string.month_10);
			case 11:
				return getString(R.string.month_11);
			case 12:
				return getString(R.string.month_12);


		}

		return "";
	}
	public String getDateLine(String date){
		String[] parts = date.split("-");
		int day = Integer.parseInt(parts[2]);
		int month = Integer.parseInt(parts[1]);
		String monthName = getMonthName(month);
		String line = day +" "+getString(R.string.in) + monthName;
		return line;
	}

	public void loadTasksAndUpdate(StudentAllTasksActivity activity){
		String[][] dataToListView = null;

		String taskIDs = null;
		try {
			Log.d("phone", phone);
			taskIDs = Controller.getTasksFromStudent(BasicClass.phone);

			taskIDs = taskIDs.replace(";", ",");
			if (taskIDs.length()>0)
				taskIDs = taskIDs.substring(0, taskIDs.length()-1);
			else
				taskIDs="-1";
			dataToListView = new String[taskIDs.split(",").length][6];
			savedTaskIDs = new int[taskIDs.split(",").length];

			if (taskIDs.equals("-1")) {
				savedTaskIDs = new int[]{};
				dataToListView = new String[0][6];
			}

			ResultSet rs = SQL.statement.executeQuery("SELECT title, teachGroup, filingDate, taskID, taskImage, description FROM tasks WHERE taskID IN ("+taskIDs+");");

			int index=0;
			while(rs.next()){
				String title  = rs.getString(1);
				int groupID = rs.getInt(2);
				String date = rs.getString(3);
				int taskID = rs.getInt(4);
				String image = rs.getString(5);
				String description = rs.getString(6);


				ResultSet groupRS = SQL.spareStatement.executeQuery("SELECT name FROM groups WHERE groupID = "+groupID+";");
				String groupName="";
				while(groupRS.next())
					groupName=groupRS.getString(1);

				dataToListView[index] = new String[]{title,groupName,date,"0x00ff00", (image==null? "":"image"), description};
				savedTaskIDs[index] = taskID;
				index++;
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}


		sortTasks(dataToListView, savedTaskIDs);
		for (int i=0; i<dataToListView.length; i++){
			dataToListView[i][2] = getDateLine(dataToListView[i][2]);
		}

		final HWArrayAdapter adapter= new HWArrayAdapter(activity,dataToListView);

		swipeListView = (SwipeListView) findViewById(R.id.example_lv_list);

		swipeListView.setFriction(100);
		swipeListView.setSwipeMode(12);
		//swipeListView.setRight(1);
		//swipeListView.setScrollX();
		swipeListView.setVelocityScale(0.01f);


		swipeListView.setSwipeListViewListener(new BaseSwipeListViewListener() {

			@Override
			public void onOpened(final int position, boolean toRight) {
				openPosition=position;
				if(toRight){

					//requestRating();
					Thread helper = new Thread() {
						public void run() {
					try {
						ResultSet rs = SQL.statement.executeQuery("SELECT tasks FROM students WHERE studentID = "+Controller.getStudentIDByPhone(BasicClass.phone)+";");
						String tasks=null;
						while(rs.next())
							tasks=rs.getString(1);

						if (tasks!=null && !tasks.equals("")){
							String[] list = tasks.split(";");

							String updatedTasks = "";
							for (int i=0; i<list.length; i++){
								Log.d("PAST TASKS: ",list[i]);
								if (!list[i].equals(""+savedTaskIDs[position])){
									updatedTasks+=list[i]+";";
								}
							}
							Log.d("UPDATING TO ",updatedTasks+"  -  position: "+savedTaskIDs[position]);

							//Deleting task:
							//SQL.statement.execute("UPDATE students SET tasks = '"+updatedTasks+"' WHERE studentID = "+Controller.getStudentIDByPhone(BasicClass.phone)+";");
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									onStart();
								}
							});
						}

					} catch (SQLException e) {
						e.printStackTrace();
					}

						}
					};
					//helper.start();

				}else{
					//openAskForHelpActivity(new View(swipeListView.getContext()));
					load = new ProgressDialog(StudentAllTasksActivity.this);
					load.setMessage("\t"+getString(R.string.loading_comments));
					load.setCanceledOnTouchOutside(false);
					load.show();
					Thread helper = new Thread() {
						public void run() {
							showCommentDialog(position);
						}
					};
					helper.start();
					Log.d("Main","Ask for help");
				}


			}
			/*
			@Override
			public void onScrollChanged(int a, int b, int c, int d){

			}
			*/
			@Override
			public void onClosed(int position, boolean fromRight) {
				Log.d("swipe", String.format("onClosed %d", position));
				(swipeListView.getChildAt(position).findViewById(R.id.back)).  setBackgroundColor(Color.rgb(255, 255, 255));
				(swipeListView.getChildAt(position).findViewById(R.id.attachment)).setBackgroundColor(Color.rgb(255, 255, 255));
				(swipeListView.getChildAt(position).findViewById(R.id.front)). setBackgroundColor(Color.rgb(255, 255, 255));

				((TextView) (swipeListView.getChildAt(position).findViewById(R.id.subject))). setTextColor(Color.rgb(0, 0, 0));
				((TextView) (swipeListView.getChildAt(position).findViewById(R.id.group_name))). setTextColor(Color.rgb( 0,0,0 ));
				((TextView) (swipeListView.getChildAt(position).findViewById(R.id.do_date))). setTextColor(Color.rgb( 0,204,0 ));
				((TextView) (swipeListView.getChildAt(position).findViewById(R.id.description))). setTextColor(Color.rgb( 0,0,0 ));
			}

			@Override
			public void onListChanged() {
				Log.d("swipe", "onListChanged");
			}

			int moves=0;

			@Override
			public void onMove(int position, float x) {
				int d = (int)Math.abs(x/4);



				if (x>0){
					(swipeListView.getChildAt(position).findViewById(R.id.back)).  setBackgroundColor(Color.rgb(255 - d, 255, 255 - d));
					(swipeListView.getChildAt(position).findViewById(R.id.attachment)).setBackgroundColor(Color.rgb(255 - d, 255, 255 - d));
					(swipeListView.getChildAt(position).findViewById(R.id.front)). setBackgroundColor(Color.rgb(255 - d, 255, 255 - d));

					int r = 0-(int)(d*1.5); if (r<0)r=0;
					int g = 0+(int)(d*1.5); if (g>255)g=255;
					int b = 0-(int)(d*1.5); if (b<0)b=0;
					int g2 = 204+(int)(d*1.5); if (g2>255)g2=255;

					((TextView) (swipeListView.getChildAt(position).findViewById(R.id.subject))). setTextColor(Color.rgb( r,g,b ));
					((TextView) (swipeListView.getChildAt(position).findViewById(R.id.group_name))). setTextColor(Color.rgb( r,g,b ));
					((TextView) (swipeListView.getChildAt(position).findViewById(R.id.do_date))). setTextColor(Color.rgb( r,g2,b ));
					((TextView) (swipeListView.getChildAt(position).findViewById(R.id.description))). setTextColor(Color.rgb( r,g,b ));
				}
				else {
					(swipeListView.getChildAt(position).findViewById(R.id.back)).  setBackgroundColor(Color.rgb(  255, 255, 255-d ));
					(swipeListView.getChildAt(position).findViewById(R.id.attachment)).setBackgroundColor(Color.rgb(  255, 255, 255-d ));
					(swipeListView.getChildAt(position).findViewById(R.id.front)). setBackgroundColor(Color.rgb(  255, 255, 255-d ));

					int r = 0-(int)(d*1.5); if (r<0)r=0;
					int g = 0+(int)(d*1.5); if (g>255)g=255;
					int b = 0-(int)(d*1.5); if (b<0)b=0;
					int g2 = 204+(int)(d*1.5); if (g2>255)g2=255;

					((TextView) (swipeListView.getChildAt(position).findViewById(R.id.subject))). setTextColor(Color.rgb( r,g,b ));
					((TextView) (swipeListView.getChildAt(position).findViewById(R.id.group_name))). setTextColor(Color.rgb( r,g,b ));
					((TextView) (swipeListView.getChildAt(position).findViewById(R.id.do_date))). setTextColor(Color.rgb( r,g2,b ));
					((TextView) (swipeListView.getChildAt(position).findViewById(R.id.description))). setTextColor(Color.rgb( r,g,b ));
				}
			}

			@Override
			public void onStartOpen(int position, int action, boolean right) {
				Log.d("swipe", String.format("onStartOpen %d", position));
				/*
				if(right) {
					(swipeListView.getChildAt(position).findViewById(R.id.back)).setBackgroundColor(Color.GREEN);
				}else{
					(swipeListView.getChildAt(position).findViewById(R.id.back)).setBackgroundColor(Color.YELLOW);
				}
				swipeListView.invalidate();
				Log.d("swipe", String.format("onStartOpen %d - action %d - "+right, position, action));
				*/
			}

			@Override
			public void onStartClose(int position, boolean right) {
				Log.d("swipe", String.format("onStartClose %d", position));
				onClosed(position, right);
			}

			@Override
			public void onClickFrontView(final int position) {
				Log.d("swipe", String.format("onClickFrontView %d", position));

			}
			ProgressDialog load;
			@Override
			public void onClickBackView(final int position) {
				Log.d("swipe", String.format("onClickBackView %d", position));
				load = new ProgressDialog(StudentAllTasksActivity.this);
				load.setMessage("\t"+getString(R.string.loading_comments));
				load.setCanceledOnTouchOutside(false);
				load.show();
				Thread helper = new Thread() {
					public void run() {
						showCommentDialog(position);
					}
				};
				helper.start();
			}

			public void showCommentDialog(final int position){

				TeacherMainActivity.selectedTask=savedTaskIDs[position];
				openCommentsActivity(new View(StudentAllTasksActivity.this));
				load.dismiss();
				/*
				final int pos = position;
				try {

					ResultSet rstask = SQL.statement.executeQuery("SELECT description FROM tasks WHERE taskID = "+savedTaskIDs[position]+";");
					String description = null;
					while(rstask.next())
						description = rstask.getString(1);


					String commentIDs = Controller.getCommentsFromTask(savedTaskIDs[position]);

					commentIDs = commentIDs.replace(";", ",");
					if (commentIDs.length()>0)
						commentIDs = commentIDs.substring(0, commentIDs.length()-1);
					else
						commentIDs = ""+ -1;
					final ResultSet rs = SQL.spareStatement.executeQuery("SELECT comment, isStudent, commentor FROM comments WHERE commentID IN ("+commentIDs+");");//THIS IS A MISTAKE! SHOULD BE taskID

					final ArrayList<String> comments = new ArrayList<String>();
					final ArrayList<String> names = new ArrayList<String>();

					while(rs.next()) {
						String comment = rs.getString(1);
						boolean isStudent = rs.getBoolean(2);
						int commentor = rs.getInt(3);

						comments.add(comment);
						names.add(Controller.getStudentName(commentor));

					}

					final String descriptionCopy = description;
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							load.dismiss();
							final AlertDialog.Builder alert = new AlertDialog.Builder(StudentAllTasksActivity.this);
							alert.setTitle(getString(R.string.comments_title));


							ScrollView scroll = new ScrollView(StudentAllTasksActivity.this);

							LinearLayout layout = new LinearLayout(StudentAllTasksActivity.this);
							layout.setOrientation(LinearLayout.VERTICAL);

							TextView task = new TextView(StudentAllTasksActivity.this);
							task.setTypeface(null, Typeface.BOLD);
							task.setText("\n"+getString(R.string.task_description_title)+descriptionCopy+"\n");
							layout.addView(task);

							for (int i=0; i<comments.size(); i++){
								final TextView nameToDisplay = new TextView(StudentAllTasksActivity.this);
								nameToDisplay.setPaintFlags(nameToDisplay.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
								nameToDisplay.setTypeface(null, Typeface.BOLD_ITALIC);
								nameToDisplay.setText("\n"+names.get(i)+":");
								layout.addView(nameToDisplay);


								final TextView commentToDisplay = new TextView(StudentAllTasksActivity.this);
								commentToDisplay.setText(comments.get(i));
								layout.addView(commentToDisplay);

							}

							final EditText yourComment = new EditText(StudentAllTasksActivity.this);
							yourComment.setInputType(InputType.TYPE_CLASS_TEXT);
							yourComment.setHint(getString(R.string.hint_comment));
							layout.addView(yourComment);

							scroll.addView(layout);
							//alert.setView(layout);
							alert.setView(scroll);


							alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int whichButton) {

									final String comment = yourComment.getText().toString();

									Thread helper = new Thread() {
										public void run() {
											try {
												if (!comment.equals(""))
													Controller.addComment(comment, savedTaskIDs[pos], true, Controller.getStudentIDByPhone(BasicClass.phone));
											} catch (SQLException e) {
												e.printStackTrace();
											}
										}
									};
									helper.start();
								}
							});
							alert.setNegativeButton("Close", null);
							alert.show();
						}
					});


				} catch (SQLException e) {
					e.printStackTrace();
				}
				*/
			}




		});
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				swipeListView.setAdapter(adapter);
				reload();
				pdLoading.dismiss();
			}
		});
	}

	public void pressedFace1(View v) throws SQLException {
		Log.d("FACEPRESS!","1");
		new SendFeedback().execute(1);
	}
	public void pressedFace2(View v) throws SQLException {
		Log.d("FACEPRESS!","2");
		new SendFeedback().execute(2);
	}
	public void pressedFace3(View v) throws SQLException {
		Log.d("FACEPRESS!","3");
		new SendFeedback().execute(3);
	}
	public void pressedFace4(View v) throws SQLException {
		Log.d("FACEPRESS!","4");
		new SendFeedback().execute(4);
	}
	public void pressedFace5(View v) throws SQLException {
		Log.d("FACEPRESS!","5");
		new SendFeedback().execute(5);
	}

	private class SendFeedback extends AsyncTask<Integer, Void, Void> {
		ProgressDialog pdLoading = new ProgressDialog(StudentAllTasksActivity.this);

		@Override
		protected Void doInBackground(Integer... ints){
			try {
				Controller.finishTaskAndSendFeedback(savedTaskIDs[openPosition], ints[0]);



				ResultSet rs = SQL.statement.executeQuery("SELECT tasks FROM students WHERE studentID = "+Controller.getStudentIDByPhone(BasicClass.phone)+";");
				String tasks=null;
				while(rs.next())
					tasks=rs.getString(1);

				if (tasks!=null && !tasks.equals("")){
					String[] list = tasks.split(";");

					String updatedTasks = "";
					for (int i=0; i<list.length; i++){
						Log.d("PAST TASKS: ",list[i]);
						if (!list[i].equals(""+savedTaskIDs[openPosition])){
							updatedTasks+=list[i]+";";
						}
					}
					Log.d("UPDATING TO ",updatedTasks+"  -  position: "+savedTaskIDs[openPosition]);

					//Deleting task:
					SQL.statement.execute("UPDATE students SET tasks = '"+updatedTasks+"' WHERE studentID = "+Controller.getStudentIDByPhone(BasicClass.phone)+";");

				}


			} catch (SQLException e) {
				e.printStackTrace();
			}




			return null;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();

			pdLoading.setMessage("\t" + getString(R.string.sending_feedback));
			pdLoading.setCanceledOnTouchOutside(false);
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}

		@Override
		protected void onPostExecute(Void result) {
			pdLoading.dismiss();

			openMainActivity(new View(StudentAllTasksActivity.this));
		}
	}

	public void deleteSelectedTask(){
		final int position=openPosition;
		Thread helper = new Thread() {
			public void run() {
				try {
					ResultSet rs = SQL.statement.executeQuery("SELECT tasks FROM students WHERE studentID = "+Controller.getStudentIDByPhone(BasicClass.phone)+";");
					String tasks=null;
					while(rs.next())
						tasks=rs.getString(1);

					if (tasks!=null && !tasks.equals("")){
						String[] list = tasks.split(";");

						String updatedTasks = "";
						for (int i=0; i<list.length; i++){
							Log.d("PAST TASKS: ",list[i]);
							if (!list[i].equals(""+savedTaskIDs[position])){
								updatedTasks+=list[i]+";";
							}
						}
						Log.d("UPDATING TO ",updatedTasks+"  -  position: "+savedTaskIDs[position]);

						//Deleting task:
						SQL.statement.execute("UPDATE students SET tasks = '"+updatedTasks+"' WHERE studentID = "+Controller.getStudentIDByPhone(BasicClass.phone)+";");
						final StudentAllTasksActivity thisClass = StudentAllTasksActivity.this;
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								openMainActivity(new View(thisClass));
							}
						});
					}

				} catch (SQLException e) {
					e.printStackTrace();
				}

			}
		};
		helper.start();
	}


    private void reload() {
        SettingsManager settings = SettingsManager.getInstance();
        swipeListView.setSwipeMode(settings.getSwipeMode());
        swipeListView.setSwipeActionLeft(settings.getSwipeActionLeft());
        swipeListView.setSwipeActionRight(settings.getSwipeActionRight());
        swipeListView.setOffsetLeft(convertDpToPixel(settings.getSwipeOffsetLeft()));
        swipeListView.setOffsetRight(convertDpToPixel(settings.getSwipeOffsetRight()));
        swipeListView.setAnimationTime(settings.getSwipeAnimationTime());
        swipeListView.setSwipeOpenOnLongPress(settings.isSwipeOpenOnLongPress());
    }

    public int convertDpToPixel(float dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return (int) px;
    }



	private class LoadTasks extends AsyncTask<Void, Void, Void> {
		ProgressDialog pdLoading = new ProgressDialog(StudentAllTasksActivity.this);

		@Override
		protected Void doInBackground(Void... ints){

			return null;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();

			pdLoading.setMessage("\t" + getString(R.string.loading_tasks));
			pdLoading.setCanceledOnTouchOutside(false);
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(Void result) {
			pdLoading.dismiss();

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
            openActivityFromMenu(position + 1);
        }
	    */
	    if (!firstRun) {
		    openActivityFromMenu(position + 1);
		    firstRun=false;
	    }
    }

    public void onSectionAttached(int number) {
	    mTitle = getString(R.string.student_title_section1);
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
            getMenuInflater().inflate(R.menu.main, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_all_tasks_student, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((StudentAllTasksActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }



	public void deleteUser(MenuItem bs){
		final AlertDialog.Builder alert = new AlertDialog.Builder(StudentAllTasksActivity.this);

		alert.setTitle(getString(R.string.delete_user));
		alert.setMessage(getString(R.string.delete_user_confirmation));


		alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				new DeleteStudent().execute((Void) null);
			}
		});
		alert.setNegativeButton(getString(R.string.no), null);

		alert.show();
	}

	private class DeleteStudent extends AsyncTask<Void, Void, Void> {
		ProgressDialog pdLoading = new ProgressDialog(StudentAllTasksActivity.this);
		@Override
		protected Void doInBackground(Void... in){
			try {
				Controller.deleteStudent(BasicClass.id);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();
			pdLoading.setMessage("\t" + getString(R.string.deleting_user));
			pdLoading.setCanceledOnTouchOutside(false);
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(Void result) {
			startActivity(new Intent(StudentAllTasksActivity.this, SplashActivity.class));
			finish();
			pdLoading.dismiss();
		}
	}


}
