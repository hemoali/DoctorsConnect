package ibrahim.radwan.doctorsconnect;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import ibrahim.radwan.doctorsconnect.Models.User;
import ibrahim.radwan.doctorsconnect.Utils.ConferenceAdapter;
import ibrahim.radwan.doctorsconnect.Utils.Utils;
import ibrahim.radwan.doctorsconnect.data.Contract;
import ibrahim.radwan.doctorsconnect.data.DataProviderFunctions;

public class MainActivity extends AppCompatActivity {
    private User mUser;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);

        //Get user
        Bundle bundle = getIntent().getExtras();
        mUser = (User) bundle.getSerializable("user");

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_signout) {
            Utils.signOutUser(getApplicationContext());
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment () {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance (int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView (LayoutInflater inflater, ViewGroup container,
                                  Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            ListView mainListView = (ListView) rootView.findViewById(R.id.main_list_view);
            TextView noElementsView = (TextView) rootView.findViewById(R.id.no_elements_view);

            //Admin Case
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 0) {
                ConferenceAdapter conferenceAdapter = new ConferenceAdapter(getContext(), null, 0);
                mainListView.setAdapter(conferenceAdapter);
                Cursor c = DataProviderFunctions.getInstance().getConfs(getContext());
                if (c.getCount() == 0) {
                    mainListView.setVisibility(View.GONE);
                } else {
                    conferenceAdapter.swapCursor(c);
                }
            } else {
                Cursor cursor = DataProviderFunctions.getInstance().getTopics(getContext());
                if (cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    User u = DataProviderFunctions.getInstance().getUserByID(cursor.getString(cursor.getColumnIndex(Contract.TopicEntry.COLUMN_DOC_ID)), getContext());
                    SimpleCursorAdapter c = new SimpleCursorAdapter(getContext(), android.R.layout.simple_list_item_2, cursor, new String[]{Contract.TopicEntry.COLUMN_TOPIC_TITLE, u.getUserEmail()}, new int[]{android.R.id.text1, android.R.id.text2}, 0);
                    mainListView.setAdapter(c);
                } else {
                    mainListView.setVisibility(View.GONE);
                }
            }
            return rootView;
        }

        @Override
        public Loader<Cursor> onCreateLoader (int id, Bundle args) {

            return null;
        }

        @Override
        public void onLoadFinished (Loader<Cursor> loader, Cursor data) {

        }

        @Override
        public void onLoaderReset (Loader<Cursor> loader) {

        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter (FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem (int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount () {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle (int position) {
            switch (position) {
                case 0:
                    return String.valueOf(mUser.getTypeID()).equals(Contract.UserTypeEntry.USER_TYPE_ADMIN_ID) ? getString(R.string.conferences) : getString(R.string.topics);
                case 1:
                    return String.valueOf(mUser.getTypeID()).equals(Contract.UserTypeEntry.USER_TYPE_ADMIN_ID) ? getString(R.string.topics) : getString(R.string.invites);
            }
            return null;
        }
    }
}
