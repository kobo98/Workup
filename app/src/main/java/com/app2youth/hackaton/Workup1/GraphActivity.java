package com.app2youth.hackaton.Workup1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.sql.SQLException;


public class GraphActivity extends BasicClass
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
        setContentView(R.layout.activity_graph);

        mNavigationDrawerFragment = (NavigationDrawerFragmentTeacher)getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        positionInMenu=2;
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout),positionInMenu,this);


	    Display display = getWindowManager().getDefaultDisplay();
	    Point size = new Point();
	    display.getSize(size);
	    width = size.x;
    }
	public static int width=0;
    @Override
    public void onStart() {
	    super.onStart();
	    firstRun = false;
	    start();
    }

	public void start(){

        graph = (GraphView) findViewById(R.id.graph);
		staticLabelsFormatter = new StaticLabelsFormatter(graph);

	    //staticLabelsFormatter.setHorizontalLabels(new String[] {"zift", "beef", "kleaf","hide", "bife"});
		graph.getViewport().setScrollable(true);
		graph.setHorizontalScrollBarEnabled(true);


	    graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);



	    group = (RadioGroup) findViewById(R.id.select_group);


	    layoutParams = new RadioGroup.LayoutParams(
			    RadioGroup.LayoutParams.WRAP_CONTENT,
			    RadioGroup.LayoutParams.WRAP_CONTENT);

		RelativeLayout layout = (RelativeLayout)findViewById(R.id.layout);

		new LoadGroups().execute((Void)null);
    }

	StaticLabelsFormatter staticLabelsFormatter;
	GraphView graph;
	RadioGroup group;
	LinearLayout.LayoutParams layoutParams;

	private class LoadGroups extends AsyncTask<Void, Void, String[]> {
		ProgressDialog pdLoading = new ProgressDialog(GraphActivity.this);

		@Override
		protected String[] doInBackground(Void... ints){
			String[] list = null;

			try {
				list = Controller.getGroupNamesForTeacher(BasicClass.phone);
			} catch (SQLException e) {
				e.printStackTrace();
			}




			return list;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();

			pdLoading.setMessage("\t" + getString(R.string.loading_groups));
			pdLoading.setCanceledOnTouchOutside(false);
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(String[] result) {
			pdLoading.dismiss();
			group.removeAllViews();
			for (int i=0; i<result.length; i++){
				RadioButton b = new RadioButton(GraphActivity.this);
				b.setText(result[i]);
				//b.setId(i);
				b.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						graph.removeAllSeries();
						new LoadGraphData().execute(((RadioButton)view).getText().toString());
					}
				});
				group.addView(b, 0, layoutParams);
				//b1.setId(0);
			}
		}
	}




	private class LoadGraphData extends AsyncTask<String, Void, LineGraphSeries> {
		ProgressDialog pdLoading = new ProgressDialog(GraphActivity.this);
		String[] labels=null;
		@Override
		protected LineGraphSeries doInBackground(String... name){
			String[] list;
			DataPoint[] dataPoints = null;

			try {

				int group = Controller.getGroupIDByNameAndPhone(name[0], phone);
				list = Controller.getTasksFromGroup(group).split(";");

				dataPoints = new DataPoint[list.length];
				labels = new String[list.length];


				for (int i=0; i<list.length; i++){
					if (list[i].equals(""))
						return null;
					dataPoints[i] = new DataPoint(2*i, Controller.getAverageTaskGrade(Integer.parseInt(list[i])));
					labels[i] = Controller.getTaskTitle(Integer.parseInt(list[i]));
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
			pdLoading.setCanceledOnTouchOutside(false);
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

			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) graph.getLayoutParams();
			params.width=100*(graph.getSeries().size()-1);
			if (params.width<width)
				params.width = width;
			graph.setLayoutParams(params);
		}
	}



	public void addGroupMenuButton(MenuItem bs){
		openAddGroupActivity(new View(GraphActivity.this));
	}
	public void deleteUser(MenuItem bs){
		final AlertDialog.Builder alert = new AlertDialog.Builder(GraphActivity.this);

		alert.setTitle(getString(R.string.delete_user));
		alert.setMessage(getString(R.string.delete_user_confirmation));


		alert.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				new DeleteTeacher().execute((Void) null);
			}
		});
		alert.setNegativeButton(getString(R.string.no), null);

		alert.show();
	}

	private class DeleteTeacher extends AsyncTask<Void, Void, Void> {
		ProgressDialog pdLoading = new ProgressDialog(GraphActivity.this);
		@Override
		protected Void doInBackground(Void... in){
			try {
				Controller.deleteTeacher(BasicClass.id);
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
			startActivity(new Intent(GraphActivity.this, SplashActivity.class));
			finish();
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
            openActivityFromMenuTeacher(position + 1);
        }
        */
	    if (!firstRun) {
		    openActivityFromMenuTeacher(position + 1);
		    firstRun=false;
	    }
    }

    public void onSectionAttached(int number) {
	    mTitle = getString(R.string.teacher_title_section6);
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
            getMenuInflater().inflate(R.menu.graph, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_graph, container, false);
			/*
	        GraphView graphView = (GraphView) rootView.findViewById(R.id.graph);
	        graphView.getViewport().setScrollable(true);
			*/
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((GraphActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
