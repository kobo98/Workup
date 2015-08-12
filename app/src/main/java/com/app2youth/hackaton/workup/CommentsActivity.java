package com.app2youth.hackaton.workup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.w3c.dom.Comment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class CommentsActivity extends ActionBarActivity {


	int height=0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comments);

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		height = size.y;
	}


	ListView lView;

	@Override
	public void onStart(){
		super.onStart();
		start();
	}

	public void start(){
		Log.d("Comments Activity", "Selected task: "+TeacherMainActivity.selectedTask);
		new LoadComments().execute(TeacherMainActivity.selectedTask);
	}


	boolean firstTime=true;
	ArrayList<Integer> savedIDs = new ArrayList<Integer>();

	private class LoadComments extends AsyncTask<Integer, Void, Void> {
		ProgressDialog pdLoading = new ProgressDialog(CommentsActivity.this);
		final ArrayList<String> data = new ArrayList<String>();
		String taskDescription=null;
		@Override
		protected Void doInBackground(Integer... ints){
			try {
				ResultSet rstask = SQL.statement.executeQuery("SELECT description FROM tasks WHERE taskID = "+TeacherMainActivity.selectedTask+";");
				while(rstask.next())
					taskDescription = rstask.getString(1);


				String commentIDs = Controller.getCommentsFromTask(TeacherMainActivity.selectedTask);

				commentIDs = commentIDs.replace(";", ",");
				if (commentIDs.length()>0)
					commentIDs = commentIDs.substring(0, commentIDs.length()-1);
				else
					commentIDs = ""+ -1;
				final ResultSet rs = SQL.spareStatement.executeQuery("SELECT comment, isStudent, commentor, commentID FROM comments WHERE commentID IN ("+commentIDs+");");//THIS IS A MISTAKE! SHOULD BE taskID

				savedIDs.clear();
				while(rs.next()) {
					String comment = rs.getString(1);
					boolean isStudent = rs.getBoolean(2);
					int commentor = rs.getInt(3);
					int id = rs.getInt(4);

					savedIDs.add(id);
					if (isStudent)
						data.add("<font size='14' color='#0f20ff'>"+Controller.getStudentName(commentor)+"</font><br><font size='18' color='#000000'>"+comment+"</font>");
					else
						data.add("<font size='14' color='#0f20ff'>"+Controller.getTeacherName(commentor)+"</font><br><font size='18' color='#000000'>"+comment+"</font>");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}




			return null;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();

			pdLoading.setMessage("\t"+getString(R.string.loading_comments));
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}

		@Override
		protected void onPostExecute(Void result) {
			pdLoading.dismiss();

			final TextView taskDescriptionView = (TextView) findViewById(R.id.task_description);
			taskDescriptionView.setText(taskDescription);

			final EditText commentField = (EditText) findViewById(R.id.comment_field);

			CommentsListAdapter adapter = new CommentsListAdapter(data, CommentsActivity.this);

			//handle listview and assign adapter
			lView = (ListView) findViewById(R.id.list_of_comments);
			lView.setAdapter(adapter);

			if (firstTime){
				lView.getLayoutParams().height = (int) (((double) height - taskDescriptionView.getWidth() - commentField.getWidth()));
				firstTime=false;
			}
		}
	}



	public void addComment(View v){
		/*
		final TextView taskDescriptionView = (TextView)findViewById(R.id.task_description);
		final EditText commentField = (EditText)findViewById(R.id.comment_field);
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int height = size.y;
		lView.getLayoutParams().height=(int) (((double)height-taskDescriptionView.getWidth()-commentField.getWidth()));
		*/

		EditText commentField = (EditText)findViewById(R.id.comment_field);

		new AddComment().execute(commentField.getText().toString());

		commentField.clearFocus();
		commentField.setText("");
		hideKeyboard(CommentsActivity.this);
	}

	public static void hideKeyboard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		//Find the currently focused view, so we can grab the correct window token from it.
		View view = activity.getCurrentFocus();
		//If no view currently has focus, create a new one, just so we can grab a window token from it
		if(view == null) {
			view = new View(activity);
		}
		inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
	}


	private class AddComment extends AsyncTask<String, Void, Void> {
		ProgressDialog pdLoading = new ProgressDialog(CommentsActivity.this);

		@Override
		protected Void doInBackground(String... ints){
			String comment = ints[0];
			try {
				if (!comment.equals(""))
					Controller.addComment(comment, TeacherMainActivity.selectedTask, !BasicClass.teacher, BasicClass.teacher? Controller.getTeacherIDByPhone(BasicClass.phone):Controller.getStudentIDByPhone(BasicClass.phone));
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
			start();
		}
	}





	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_comments, menu);
		return true;
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








	public class CommentsListAdapter extends BaseAdapter implements ListAdapter {
		private ArrayList<String> list = new ArrayList<String>();//index 0 is commentor, 1 is comment
		private Context context;
		public CommentsListAdapter(ArrayList<String> list, Context context) {
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
				view = inflater.inflate(R.layout.comments_list_layout, null);
			}

			final TextView commentor = (TextView)view.findViewById(R.id.commentor);
			commentor.setText(Html.fromHtml(list.get(position)), TextView.BufferType.SPANNABLE);

			final Button openComments = (Button)view.findViewById(R.id.open_comments_button);
			openComments.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {





					/*
					try {
						final int pos = position;

						String commentIDs = Controller.getCommentsFromComment(savedIDs.get(position));

						commentIDs = commentIDs.replace(";", ",");
						if (commentIDs.length() > 0)
							commentIDs = commentIDs.substring(0, commentIDs.length() - 1);
						else
							commentIDs = "" + -1;
						final ResultSet rs;//THIS IS A MISTAKE! SHOULD BE taskID

						rs = SQL.spareStatement.executeQuery("SELECT comment, isStudent, commentor FROM comments WHERE commentID IN (" + commentIDs + ");");


						final ArrayList<String> comments = new ArrayList<String>();
						final ArrayList<String> names = new ArrayList<String>();

						while (rs.next()) {
							String comment = rs.getString(1);
							boolean isStudent = rs.getBoolean(2);
							int commentor = rs.getInt(3);

							comments.add(comment);
							names.add(Controller.getStudentName(commentor));
						}
					}catch (SQLException e) {
						e.printStackTrace();
					}



					final AlertDialog.Builder alert = new AlertDialog.Builder(CommentsActivity.this);
					alert.setTitle(getString(R.string.comments_title));


					ScrollView scroll = new ScrollView(CommentsActivity.this);

					LinearLayout layout = new LinearLayout(CommentsActivity.this);
					layout.setOrientation(LinearLayout.VERTICAL);

					TextView task = new TextView(CommentsActivity.this);
					task.setTypeface(null, Typeface.BOLD);
					task.setText("\n"+getString(R.string.task_description_title)+openComments.getText().toString()+"\n");
					layout.addView(task);

					for (int i=0; i<comments.size(); i++){
						final TextView nameToDisplay = new TextView(CommentsActivity.this);
						nameToDisplay.setPaintFlags(nameToDisplay.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
						nameToDisplay.setTypeface(null, Typeface.BOLD_ITALIC);
						nameToDisplay.setText("\n"+names.get(i)+":");
						layout.addView(nameToDisplay);


						final TextView commentToDisplay = new TextView(CommentsActivity.this);
						commentToDisplay.setText(comments.get(i));
						layout.addView(commentToDisplay);

					}

					final EditText yourComment = new EditText(CommentsActivity.this);
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
*/


					final ProgressDialog load = new ProgressDialog(CommentsActivity.this);
					load.setMessage("\t"+getString(R.string.loading_comments));
					load.show();
					Thread helper = new Thread() {
						public void run() {





							final int pos = position;
							try {


								String description = commentor.getText().toString();

								String commentIDs = Controller.getCommentsFromComment(savedIDs.get(position));

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
									if (isStudent)
										names.add(Controller.getStudentName(commentor));
									else
										names.add(Controller.getTeacherName(commentor));
								}

								final String descriptionCopy = description;
								runOnUiThread(new Runnable() {
									@Override
									public void run() {
										load.dismiss();
										final AlertDialog.Builder alert = new AlertDialog.Builder(CommentsActivity.this);
										alert.setTitle(getString(R.string.comments_title));


										ScrollView scroll = new ScrollView(CommentsActivity.this);

										LinearLayout layout = new LinearLayout(CommentsActivity.this);
										layout.setOrientation(LinearLayout.VERTICAL);

										TextView task = new TextView(CommentsActivity.this);
										task.setTypeface(null, Typeface.BOLD);
										task.setText("\n"+getString(R.string.task_description_title)+descriptionCopy+"\n");
										layout.addView(task);

										for (int i=0; i<comments.size(); i++){
											final TextView nameToDisplay = new TextView(CommentsActivity.this);
											nameToDisplay.setPaintFlags(nameToDisplay.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
											nameToDisplay.setTypeface(null, Typeface.BOLD_ITALIC);
											nameToDisplay.setText("\n"+names.get(i)+":");
											layout.addView(nameToDisplay);


											final TextView commentToDisplay = new TextView(CommentsActivity.this);
											commentToDisplay.setText(comments.get(i));
											layout.addView(commentToDisplay);

										}

										final EditText yourComment = new EditText(CommentsActivity.this);
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
																Controller.addCommentToComment(comment, savedIDs.get(pos), !BasicClass.teacher, BasicClass.teacher ? Controller.getTeacherIDByPhone(BasicClass.phone) : Controller.getStudentIDByPhone(BasicClass.phone));
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









						}
					};
					helper.start();








				}
			});

		/*
		TextView comment = (TextView)view.findViewById(R.id.comment);
		comment.setText(list.get(position));
		*/
			return view;
		}
	}


	private class LoadCommentsOnComment extends AsyncTask<Void, Void, Void> {
		ProgressDialog pdLoading = new ProgressDialog(CommentsActivity.this);
		@Override
		protected Void doInBackground(Void... ints){
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
		}
	}

}



