package com.app2youth.hackaton.Workup1;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


public class AddTaskActivity extends BasicClass
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
        setContentView(R.layout.activity_add_task);

        mNavigationDrawerFragment = (NavigationDrawerFragmentTeacher)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        positionInMenu=4;

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout),positionInMenu,this);




	    Display display = getWindowManager().getDefaultDisplay();
	    Point size = new Point();
	    display.getSize(size);
	    width = size.x;



    }
	public static double width=0;



    private void updateLabel(EditText edittext, Calendar myCalendar) {

        String myFormat = "yy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        edittext.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public void onStart(){
        super.onStart();
	    firstRun=false;
	    start();
    }

	public void start(){
		selectedSpinner=getString(R.string.spinner_select_group);

		dropdown = (Spinner)findViewById(R.id.groupSpinner);
		taskName = (EditText) findViewById(R.id.taskName);
		taskDescription = (EditText) findViewById(R.id.taskDescription);
		filingDate = (EditText) findViewById(R.id.filingDate);
		imageTask = (ImageView) findViewById(R.id.displayImage);

		myCalendar = Calendar.getInstance();
		datePicker = new DatePickerDialog.OnDateSetListener() {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
			                      int dayOfMonth) {
				myCalendar.set(Calendar.YEAR, year);
				myCalendar.set(Calendar.MONTH, monthOfYear);
				myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				updateLabel(filingDate, myCalendar);
			}
		};

		new LoadGroups().execute((Void)null);
	}

	static String selectedSpinner=null;
	DatePickerDialog.OnDateSetListener datePicker;
	Calendar myCalendar;
	Spinner dropdown;
	EditText taskName;
	EditText taskDescription;
	EditText filingDate;
	ImageView imageTask;
	Bitmap image=null;

	private class LoadGroups extends AsyncTask<Void, Void, String[]> {
		ProgressDialog pdLoading = new ProgressDialog(AddTaskActivity.this);

		@Override
		protected String[] doInBackground(Void... ints){
			String[] list = null;
			try {
				list = Controller.getGroupNamesForTeacher(BasicClass.phone);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			String[] items = new String[list.length+1];
			items[0]=getString(R.string.spinner_select_group);
			for (int i=0; i<list.length; i++){
				items[i+1]=list[i];
			}

			return items;
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

			dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
				@Override
				public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
					selectedSpinner = dropdown.getSelectedItem().toString();
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {

				}
			});


			dropdown.setAdapter(new ArrayAdapter<String>(AddTaskActivity.this, android.R.layout.simple_spinner_item, result));
			if (BasicClass.groupSelected) {
				for (int i = 0; i < result.length; i++)
					if (result[i].equals(BasicClass.selectedGroup)) {
						dropdown.setSelection(i);
						Log.d("dropdown selected", "dropdown selected");
					}

			}
		}
	}


	public void openDateDialog(View v){
		InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		im.hideSoftInputFromWindow(filingDate.getWindowToken(), 0);

		new DatePickerDialog(AddTaskActivity.this, datePicker, myCalendar
				.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
				myCalendar.get(Calendar.DAY_OF_MONTH)).show();

	}

	private static final int SELECT_PHOTO = 100;
	public void openImageExplorer(View v){
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, SELECT_PHOTO);
	}

	public static int MAX_IMAGE_SIZE=300;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
		super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

		switch(requestCode) {
			case SELECT_PHOTO:
				if(resultCode == RESULT_OK){
					Log.d("SHIT", "photo chosent");
					Uri selectedImage = imageReturnedIntent.getData();
					/*
					InputStream imageStream = null;
					try {
						imageStream = getContentResolver().openInputStream(selectedImage);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
					Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
					*/
					try {
						image = decodeUri(selectedImage, MAX_IMAGE_SIZE);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}

					imageTask.setImageBitmap(image);
				}
		}
	}


	private Bitmap decodeUri(Uri selectedImage, int size) throws FileNotFoundException {

		// Decode image size
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

		// The new size we want to scale to
		final int REQUIRED_SIZE = size;

		// Find the correct scale value. It should be the power of 2.
		int width_tmp = o.outWidth, height_tmp = o.outHeight;
		int scale = 1;
		while (true) {
			if (width_tmp / 2 < REQUIRED_SIZE
					|| height_tmp / 2 < REQUIRED_SIZE) {
				break;
			}
			width_tmp /= 2;
			height_tmp /= 2;
			scale *= 2;
		}

		// Decode with inSampleSize
		BitmapFactory.Options o2 = new BitmapFactory.Options();
		o2.inSampleSize = scale;
		return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);

	}


	public void createTask(View v){
		Log.d("Add Task: ", selectedSpinner + ", " + taskName.getText().toString()+", "+taskDescription.getText().toString()+", "+filingDate.getText().toString()+", "+(image==null? "Without image":"With image"));
		if (selectedSpinner.equals(getString(R.string.spinner_select_group))){
			Toast t = Toast.makeText(getApplicationContext(), getString(R.string.select_group_alert), Toast.LENGTH_LONG);
			t.show();
		}
		else if (taskName.getText().toString().equals("")){
			Toast t = Toast.makeText(getApplicationContext(), getString(R.string.enter_task_name_alert), Toast.LENGTH_LONG);
			t.show();
		}
		else if (filingDate.getText().toString().equals("")){
			Toast t = Toast.makeText(getApplicationContext(), getString(R.string.enter_filing_date_alert), Toast.LENGTH_LONG);
			t.show();
		}
		else{
			String name = taskName.getText().toString(), description = taskDescription.getText().toString(), date = filingDate.getText().toString();

			new AddTask().execute(name,description,date);
		}
	}


	private class AddTask extends AsyncTask<String, Void, Void> {
		ProgressDialog pdLoading = new ProgressDialog(AddTaskActivity.this);

		@Override
		protected Void doInBackground(String... ints){
			try {
				if (image==null)
					Controller.addTask(
							Controller.getGroupIDByNameAndPhone(selectedSpinner, BasicClass.phone),
							ints[0],
							ints[1],
							ints[2],
							AddTaskActivity.this
					);
				else
					Controller.addTask(
							Controller.getGroupIDByNameAndPhone(selectedSpinner, BasicClass.phone),
							ints[0],
							ints[1],
							ints[2],
							BitmapToString(image),
							AddTaskActivity.this
					);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return null;
		}
		@Override
		public void onPreExecute(){
			super.onPreExecute();

			pdLoading.setMessage("\t" + getString(R.string.adding_task));
			pdLoading.setCanceledOnTouchOutside(false);
			pdLoading.show();
		}
		@Override
		protected void onProgressUpdate(Void... progress) {}
		@Override
		protected void onPostExecute(Void result) {
			pdLoading.dismiss();
			taskName.setText("");
			taskDescription.setText("");
			filingDate.setText("");
			imageTask.setImageDrawable(null);

			Toast t = Toast.makeText(getApplicationContext(), getString(R.string.added_task), Toast.LENGTH_LONG);
			t.show();

		}
	}

	public String BitmapToString(Bitmap bitmap){
		ByteArrayOutputStream baos=new  ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
		byte [] b=baos.toByteArray();
		String temp= Base64.encodeToString(b, Base64.DEFAULT);
		return temp;
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
	    mTitle = getString(R.string.teacher_title_section5);
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
            getMenuInflater().inflate(R.menu.add_task, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_add_task, container, false);





	        ImageView back = (ImageView) rootView.findViewById(R.id.background);

	        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) back.getLayoutParams();
	        params.width = (int)width;
	        params.height = (int)((double)width/4.0);
	        back.setLayoutParams(params);
	        back.bringToFront();

	        TextView title = (TextView) rootView.findViewById(R.id.title_new);
	        title.bringToFront();


            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);

	        ((AddTaskActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
