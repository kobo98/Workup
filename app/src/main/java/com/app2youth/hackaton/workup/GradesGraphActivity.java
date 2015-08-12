package com.app2youth.hackaton.workup;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.sql.SQLException;
import java.util.ArrayList;

public class GradesGraphActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grades_graph);
	}

	@Override
	public void onStart(){
		super.onStart();
		start();
	}

	public void start(){
		graph = (GraphView) findViewById(R.id.graph);
		staticLabelsFormatter = new StaticLabelsFormatter(graph);

		graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
		group = (RadioGroup) findViewById(R.id.select_group);


		layoutParams = new RadioGroup.LayoutParams(
				RadioGroup.LayoutParams.WRAP_CONTENT,
				RadioGroup.LayoutParams.WRAP_CONTENT);

		new LoadGroups().execute((Void)null);
	}

	StaticLabelsFormatter staticLabelsFormatter;
	GraphView graph;
	RadioGroup group;
	LinearLayout.LayoutParams layoutParams;


	int[] savedGroupIDS = null;
	private class LoadGroups extends AsyncTask<Void, Void, String[]> {
		ProgressDialog pdLoading = new ProgressDialog(GradesGraphActivity.this);

		@Override
		protected String[] doInBackground(Void... ints){
			String[] list = null;

			try {
				list = Controller.getGroupNamesForStudent(BasicClass.phone);
				String[] ids = Controller.getGroupIDsForStudent(BasicClass.phone);
				savedGroupIDS = new int[ids.length];
				for (int i=0; i<ids.length; i++)
					savedGroupIDS[i]=Integer.parseInt(ids[i]);
			} catch (SQLException e) {
				e.printStackTrace();
			}

			return list;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();

			pdLoading.setMessage("\t"+getString(R.string.loading_groups));
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(String[] result) {
			pdLoading.dismiss();
			group.removeAllViews();


			for (int i=0; i<result.length; i++){
				RadioButton b = new RadioButton(GradesGraphActivity.this);
				b.setText(result[i]);
				//b.setId(i);
				final int index=i;
				b.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						graph.removeAllSeries();
						//new LoadGraphData().execute(((RadioButton)view).getText().toString());

						new LoadGraphData().execute(savedGroupIDS[index]);
					}
				});
				group.addView(b, 0, layoutParams);
				//b1.setId(0);
			}
		}
	}




	private class LoadGraphData extends AsyncTask<Integer, Void, LineGraphSeries> {
		ProgressDialog pdLoading = new ProgressDialog(GradesGraphActivity.this);
		String[] labels=null;
		@Override
		protected LineGraphSeries doInBackground(Integer... input){
			String[] list;
			DataPoint[] dataPoints = null;

			try {
				int groupID = input[0];
				ArrayList<Integer> grades = Controller.getGradesFromStudentInGroup(Controller.getStudentIDByPhone(BasicClass.phone), groupID);



				dataPoints = new DataPoint[grades.size()];
				labels = new String[dataPoints.length];

				for (int i=0; i<dataPoints.length; i++){

					dataPoints[i] = new DataPoint(2*i, Controller.getGrade(grades.get(i)));
					labels[i] = Controller.getGradeDescription(grades.get(i));
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}

			LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(dataPoints);


			return series;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();

			pdLoading.setMessage("\t"+getString(R.string.loading_graph_data));
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(LineGraphSeries result) {
			pdLoading.dismiss();
			if (result!=null && labels.length>1){
				graph.addSeries(result);
				staticLabelsFormatter.setHorizontalLabels(labels);
			}
			else {
				graph.addSeries(new LineGraphSeries(new DataPoint[]{
						new DataPoint(0,0),
						new DataPoint(2,0)
				}));
				staticLabelsFormatter.setHorizontalLabels(new String[]{" - ", " - "});
			}

		}
	}







	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_grades_graph, menu);
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
}
