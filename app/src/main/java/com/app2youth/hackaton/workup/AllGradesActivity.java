package com.app2youth.hackaton.workup;

import android.app.Activity;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;


public class AllGradesActivity extends BasicClass
        implements NavigationDrawerFragmentStudent.NavigationDrawerCallbacks {

    private ListView mDrawerListView;

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragmentStudent mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_grades);

        mNavigationDrawerFragment = (NavigationDrawerFragmentStudent)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        positionInMenu=1;

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout),positionInMenu,this);
    }

    @Override
    public void onStart(){
        super.onStart();

        String[][] dataToListView = {
                {"פיזקה", "אברהם יונה", "20/5/2015", "0xff00ff00"},
                {"מתמטיקה", "סבטלנה קונדרצקי", "20/5/2015", "0xff00ff00"},
                {"אנגלית", "מיסיס ברודי", "22/5/2015", "0xff00ff00"},
                {"תנך", "אלקה", "23/5/2015", "0xffff0000"},
                {"מכניקת מוצקים", "חיים אברמוביץ", "23/5/2015", "0xffff0000"},
                {"הנדסת חומרים", "ערן ליפ", "27/5/2015", "0xff00ff00"},
                {"מדר ח", "דניאלה אבידן המלכה", "27/5/2015", "0xff00ff00"},
                {"הנדסת חומרים", "ערן ליפ", "27/5/2015", "0xff00ff00"},
                {"מדר ח", "דניאלה אבידן המלכה", "27/5/2015", "0xff00ff00"},
                {"חדוא 2מ", "יוסי כהן", "30/5/2015", "0xffff0000"},
                {"הנדסת חומרים", "ערן ליפ", "30/5/2015", "0xff00ff00"},
                {"חדוא 2מ", "יוסי כהן", "30/5/2015", "0xffff0000"},
                {"הנדסת חומרים", "ערן ליפ", "30/5/2015", "0xff00ff00"},
                {"אנליזה נומרית", "מוט ברשקוביץ", "1/6/2015", "0xff00ff00"},
                {"פיזקה", "אברהם יונה", "20/5/2015", "0xff00ff00"},
                {"מתמטיקה", "סבטלנה קונדרצקי", "20/5/2015", "0xff00ff00"},
                {"אנגלית", "מיסיס ברודי", "22/5/2015", "0xff00ff00"},
                {"תנך", "אלקה", "23/5/2015", "0xffff0000"},
                {"מכניקת מוצקים", "חיים אברמוביץ", "23/5/2015", "0xffff0000"},
                {"אנליזה נומרית", "מוט ברשקוביץ", "1/6/2015", "0xff00ff00"}
        };
        String[] arrayOfNames = new String[dataToListView.length];
        for(int i=0;i<dataToListView.length;i++){
            arrayOfNames[i]=dataToListView[i][0];
        }

        mDrawerListView = (ListView) findViewById(R.id.list_of_grades);
        mDrawerListView.setDivider(new ColorDrawable(0xff11a7ff));
        mDrawerListView.setDividerHeight(1);
        mDrawerListView.setAdapter(new BasicClass.HWArrayAdapter(this,dataToListView,arrayOfNames));
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
            getMenuInflater().inflate(R.menu.all_grades, menu);
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
            View rootView = inflater.inflate(R.layout.fragment_all_grades, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((AllGradesActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
