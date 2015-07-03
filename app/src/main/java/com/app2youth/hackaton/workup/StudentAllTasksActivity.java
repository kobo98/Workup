package com.app2youth.hackaton.workup;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fortysevendeg.swipelistview.BaseSwipeListViewListener;
import com.fortysevendeg.swipelistview.SwipeListView;

import java.sql.ResultSet;
import java.sql.SQLException;

import SQL.SQL;
import Server.Controller;


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
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,(DrawerLayout) findViewById(R.id.drawer_layout),positionInMenu,this);



/////////////////////////////////////////
        /*if (Manager.system==null){
            String startingPath = new File(((Context)this).getExternalFilesDir(null), "").getAbsolutePath();
            BasicClass.startingPath=startingPath;
            if (Manager.mainActivityInitialized()){
                System.out.println("didnt find files");
                openOrientationActivity(new View(this));
            }
            else
                System.out.println("find files");
        }*/


        /*Random rnd= new Random();
        switch(rnd.nextInt(2)){
            case 0:
                openLogInActivity(new View(this));
                break;
            case 1:
                openAllClass
        }*/
        //openAddTaskActivity(new View(this));

    }



    @Override
    public void onStart(){
        super.onStart();

        String[][] dataToListView = null;

        String taskIDs = null;
        try {

            taskIDs = Controller.getTasksFromStudent(BasicClass.phone);

            taskIDs = taskIDs.replace(";", ",");
            if (taskIDs.length()>0)
                taskIDs = taskIDs.substring(0, taskIDs.length()-1);
            else
                taskIDs="-1";
            dataToListView = new String[taskIDs.split(",").length][4];
            savedTaskIDs = new int[taskIDs.split(",").length];

            if (taskIDs.equals("-1")) {
                savedTaskIDs = new int[]{};
                dataToListView = new String[0][4];
            }

            ResultSet rs = SQL.statement.executeQuery("SELECT title, teachGroup, filingDate, taskID FROM tasks WHERE taskID IN ("+taskIDs+");");

            int index=0;
            while(rs.next()){
                String title  = rs.getString(1);
                int groupID = rs.getInt(2);
                String date = rs.getString(3);
                int taskID = rs.getInt(4);

                ResultSet groupRS = SQL.spareStatement.executeQuery("SELECT name FROM groups WHERE groupID = "+groupID+";");
                String groupName="";
                while(groupRS.next())
                    groupName=groupRS.getString(1);

                dataToListView[index] = new String[]{title,groupName,date,"0x00ff00"};
                savedTaskIDs[index] = taskID;
                index++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }



        String[] arrayOfNames = new String[dataToListView.length];
        for(int i=0;i<dataToListView.length;i++){
            arrayOfNames[i]=dataToListView[i][0];
        }
        final HWArrayAdapter adapter= new HWArrayAdapter(this,dataToListView,arrayOfNames);
        swipeListView = (SwipeListView) findViewById(R.id.example_lv_list);

        swipeListView.setSwipeListViewListener(new BaseSwipeListViewListener() {
            @Override
            public void onOpened(int position, boolean toRight) {
                if(toRight){
                    requestRating();
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
                            SQL.statement.execute("UPDATE students SET tasks = '"+updatedTasks+"' WHERE studentID = "+Controller.getStudentIDByPhone(BasicClass.phone)+";");

                            onStart();
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    //Delete task here

                }else{
                    //openAskForHelpActivity(new View(swipeListView.getContext()));
                    showCommentDialog(position);
                    Log.d("Main","Ask for help");
                }
            }

            @Override
            public void onClosed(int position, boolean fromRight) {
            }

            @Override
            public void onListChanged() {
            }

            @Override
            public void onMove(int position, float x) {
            }

            @Override
            public void onStartOpen(int position, int action, boolean right) {
                if(right) {
                    (swipeListView.getChildAt(position).findViewById(R.id.back)).setBackgroundColor(Color.GREEN);
                }else{
                    (swipeListView.getChildAt(position).findViewById(R.id.back)).setBackgroundColor(Color.YELLOW);
                }
                swipeListView.invalidate();
                Log.d("swipe", String.format("onStartOpen %d - action %d - "+right, position, action));
            }

            @Override
            public void onStartClose(int position, boolean right) {
                Log.d("swipe", String.format("onStartClose %d", position));
            }

            @Override
            public void onClickFrontView(int position) {
                Log.d("swipe", String.format("onClickFrontView %d", position));

                showCommentDialog(position);
            }


            public void showCommentDialog(final int position){

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
                    ResultSet rs = SQL.statement.executeQuery("SELECT comment, isStudent, commentor FROM comments WHERE commentID IN ("+commentIDs+");");//THIS IS A MISTAKE! SHOULD BE taskID





                    final AlertDialog.Builder alert = new AlertDialog.Builder(StudentAllTasksActivity.this);
                    alert.setTitle("Comments on Task");
                    alert.setMessage("Task: "+description);


                    LinearLayout layout = new LinearLayout(StudentAllTasksActivity.this);
                    layout.setOrientation(LinearLayout.VERTICAL);

                    while(rs.next()){
                        String comment  = rs.getString(1);
                        boolean isStudent = rs.getBoolean(2);
                        int commentor = rs.getInt(3);
                        /*
                        ResultSet groupRS = SQL.spareStatement.executeQuery("SELECT name FROM groups WHERE groupID = "+groupID+";");
                        String groupName="";
                        while(groupRS.next())
                            groupName=groupRS.getString(1);
                        */

                        final TextView commentToDisplay = new TextView(StudentAllTasksActivity.this);
                        commentToDisplay.setText("\n"+comment);
                        layout.addView(commentToDisplay);
                    }



                    final EditText yourComment = new EditText(StudentAllTasksActivity.this);
                    yourComment.setHint("Comment");
                    layout.addView(yourComment);

                    alert.setView(layout);



                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {

                            String comment = yourComment.getText().toString();

                            try {
                                Controller.addComment(comment,savedTaskIDs[pos],true, Controller.getStudentIDByPhone(BasicClass.phone));
                            } catch (SQLException e1) {
                                e1.printStackTrace();
                            }
                        }
                    });
                    alert.show();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }


                @Override
            public void onClickBackView(int position) {
                Log.d("swipe", String.format("onClickBackView %d", position));
            }

        });

        swipeListView.setAdapter(adapter);

        reload();

        /*String[][] dataToListView;
        if (Manager.system!=null){
            ArrayList<Task> tasks = ((StudentSystem)Manager.system).getSortedTasks();

            dataToListView = new String[tasks.size()][4];
            for (int i=0; i<tasks.size(); i++){
                dataToListView[i] = new String[]{ tasks.get(i).subject, tasks.get(i).getDescription(), ""+tasks.get(i).getDaysToFinish() , ""};
            }
        }

        dataToListView = new String[0][4];

        String[] arrayOfNames = new String[dataToListView.length];
        for(int i=0;i<dataToListView.length;i++){
            arrayOfNames[i]=dataToListView[i][0];
        }

        mDrawerListView = (ListView) findViewById(R.id.hwList);
        mDrawerListView.setDivider(new ColorDrawable(0xff11a7ff));
        mDrawerListView.setDividerHeight(1);
        mDrawerListView.setAdapter(new HWArrayAdapter(this,dataToListView,arrayOfNames));


        pushNotification("Hello","You have entered the best students app ever", GraphActivity.class,SchoolBagActivity.class, 0);*/
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

    public void requestRating() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.rating_layout);
        dialog.findViewById(R.id.imageView3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Main","RATE 1");
                dialog.cancel();
            }
        });
        dialog.findViewById(R.id.imageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Main", "RATE 2");
                dialog.cancel();
            }
        });
        dialog.findViewById(R.id.imageView2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Main", "RATE 3");
                dialog.cancel();
            }
        });
        dialog.findViewById(R.id.imageView4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Main", "RATE 4");
                dialog.cancel();
            }
        });
        dialog.findViewById(R.id.imageView5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Main", "RATE 5");
                dialog.cancel();
            }
        });
        dialog.show();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
        if(position!=positionInMenu) {
            openActivityFromMenu(position + 1);
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

}
