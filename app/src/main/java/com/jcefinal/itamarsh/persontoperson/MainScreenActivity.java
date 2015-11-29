package com.jcefinal.itamarsh.persontoperson;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainScreenActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private TabLayout tab;
    public int[] colorIntArray = {R.color.blue, R.color.red};
    public int[] colorIntArray2 = {R.color.dark_blue, R.color.dark_pink};
    public int[] iconIntArray = {R.drawable.ic_add_white_24dp,R.drawable.ic_stop_white_24dp};
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private String message = "Add Contact";
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mViewPager = (ViewPager) findViewById(R.id.container);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mSectionsPagerAdapter.addFragment(new PlaceholderFragment());
        mSectionsPagerAdapter.addFragment(new PlaceholderFragment());
        // Set up the ViewPager with the sections adapter.
        mViewPager.setAdapter(mSectionsPagerAdapter);
        //FLOATING BUTTON
        fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnLongClickListener(this);
        fab.setOnClickListener(this);

        tab = (TabLayout) findViewById(R.id.tabs);
        tab.setupWithViewPager(mViewPager);

        tab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
                setMessage(tab.getPosition());
                animateFab(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void setMessage(final int loc){

        switch (loc){
            case 0:
                message = "Add Contact";
                break;
            case 1:
                message = "Stop the Search";
                break;
        }
    }
    @Override
    public void onClick(final View view) {
        switch (mViewPager.getCurrentItem()){
            case 0:
                AddContactDialogFragment alert= new AddContactDialogFragment();
                alert.show(getFragmentManager(), null);
                AddContactDialogFragment d = new AddContactDialogFragment();
                d.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                  }
                });
                break;
            case 1:
            //stop the scan
                break;
        }
    }

    @Override
    public boolean onLongClick(View v){
        Snackbar.make(v, message, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
        return true;
    }
    protected void animateFab(final int position) {
        fab.clearAnimation();
        // Scale down animation
        ScaleAnimation shrink =  new ScaleAnimation(1f, 0.2f, 1f, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(150);     // animation duration in milliseconds
        shrink.setInterpolator(new DecelerateInterpolator());
        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Change FAB color and icon
                fab.setBackgroundTintList(getResources().getColorStateList(colorIntArray[position]));
                toolbar.setBackgroundColor(getResources().getColor(colorIntArray2[position]));
                tab.setBackgroundColor(getResources().getColor(colorIntArray2[position]));
                fab.setImageDrawable(getResources().getDrawable(iconIntArray[position]));

                // Scale up animation
                ScaleAnimation expand =  new ScaleAnimation(0.2f, 1f, 0.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                expand.setDuration(100);     // animation duration in milliseconds
                expand.setInterpolator(new AccelerateInterpolator());
                fab.startAnimation(expand);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fab.startAnimation(shrink);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_screen, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    //the activities themselves
    public static class PlaceholderFragment extends Fragment implements View.OnClickListener, View.OnLongClickListener{
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private Context contex;
        private Cursor cursor;
        private DAL dal;
        private SimpleCursorAdapter cursorAdapter;
        private static final String ARG_SECTION_NUMBER = "section_number";
        private ListView cursorListView;
        private ViewGroup container;
        private View rootView;

        private String[] entries = new String[] {Contacts.ContactsTable.userName, Contacts.ContactsTable.phoneNum,Contacts.ContactsTable.userID};
        private int [] viewsID = new int[] {R.id.userNameTextView, R.id.userPhoneTextView, R.id.userIdTextView};
        public PlaceholderFragment() {
        }

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

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            this.container = container;
            if(getArguments().getInt(ARG_SECTION_NUMBER)==1){
                rootView = inflater.inflate(R.layout.activity_contacts, container, false);
                cursorListView = (ListView)rootView.findViewById(R.id.cursorListView);
                dal = new DAL(container.getContext());
                contex = this.getActivity();

                cursor = dal.getAllTimeEntriesCursor();
                cursorAdapter = new SimpleCursorAdapter(this.getActivity(), R.layout.contact, cursor, entries, viewsID, BIND_ABOVE_CLIENT);
                cursorListView.setAdapter(cursorAdapter);
                cursorListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(rootView.getContext(), "test + " + position, Toast.LENGTH_SHORT).show();
                    }
                });
                return rootView;

            }
            if(getArguments().getInt(ARG_SECTION_NUMBER)==2){
                rootView = inflater.inflate(R.layout.fragment_add_page, container, false);
                return rootView;
            }
            else {
                rootView = inflater.inflate(R.layout.fragment_main_screen, container, false);
                TextView textView = (TextView) rootView.findViewById(R.id.section_label);
                textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
                return rootView;

            }
        }

        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public void onResume() {
            super.onResume();
            if(cursorListView==null)
                rootView = getLayoutInflater(null).inflate(R.layout.activity_contacts,this.container , false);
            cursorListView = (ListView)rootView.findViewById(R.id.cursorListView);

            dal = new DAL(this.getContext());
            contex = this.getActivity();
            cursor = dal.getAllTimeEntriesCursor();
            cursorAdapter = new SimpleCursorAdapter(this.getActivity(), R.layout.contact, cursor, entries, viewsID, BIND_ABOVE_CLIENT);
            cursorListView.setAdapter(cursorAdapter);

        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

        }

        @Override
        public void onClick(View v) {
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> FragmentList = new ArrayList();
        private final List<String> FragmentTitles = new ArrayList();
        private int tabCount = 2;
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }
        public void addFragment(Fragment fragment) {
            FragmentList.add(fragment);
        }
        public void setTabCount(int num){
            this.tabCount = num;
        }
        @Override
        public int getCount() {
            return tabCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Contacts";
                case 1:
                    return "Search";
            }
            return null;
        }
    }

}


