package ibrahim.radwan.doctorsconnect;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import ibrahim.radwan.doctorsconnect.data.Contract;
import ibrahim.radwan.doctorsconnect.models.User;
import ibrahim.radwan.doctorsconnect.utils.Utils;

public class MainActivity extends AppCompatActivity {
    static private User mUser; // Current user

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    static FloatingActionButton fab;

    private static final String ARG_SECTION_NUMBER = "section_number";

    private int currentDisplayedFragment = 1;

    TextView b1, b2;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fab = (FloatingActionButton) findViewById(R.id.fab);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);


        //Get user
        mUser = Utils.getUserDataFromSharedPreferences(getApplicationContext());

        if (!Utils.isTablet(getApplicationContext())) {
            // Create the adapter that will return a fragment for each of the three
            // primary sections of the activity.
            mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

            // Set up the ViewPager with the sections adapter.
            mViewPager = (ViewPager) findViewById(R.id.container);
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageSelected (int position) {
                    if (position == 0) {
                        fab.setVisibility(View.VISIBLE);
                    } else {
                        fab.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onPageScrollStateChanged (int state) {
                }

                @Override
                public void onPageScrolled (int position, float arg1, int arg2) {
                }
            });
            mViewPager.setAdapter(mSectionsPagerAdapter);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(mViewPager);
        } else {
            FragmentManager fragmentManager = getSupportFragmentManager();

            Fragment fr = ListViewFragment.newInstance(1);
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.main_fragment, fr);
            fragmentTransaction.commit();

            b1 = (TextView) findViewById(R.id.b1);
            b2 = (TextView) findViewById(R.id.b2);

            //Set buttons text
            if (mUser.getTypeID().equals(Contract.UserTypeEntry.USER_TYPE_USER_ID)) {
                b1.setText(R.string.topics);
                b2.setText(R.string.invites);
            }
        }
    }

    public void changeFragment (View view) {
        Fragment fr = null;
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        if (view == findViewById(R.id.b1) && currentDisplayedFragment != 1) {
            fr = ListViewFragment.newInstance(1);
            fragmentTransaction.replace(R.id.main_fragment, fr);
            currentDisplayedFragment = 1;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark, null));
                b2.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                findViewById(R.id.b2).setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }
        } else if (view == findViewById(R.id.b2) && currentDisplayedFragment != 2) {
            fr = ListViewFragment.newInstance(2);
            fragmentTransaction.replace(R.id.main_fragment, fr);
            currentDisplayedFragment = 2;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark, null));
                b1.setBackgroundColor(getResources().getColor(R.color.colorPrimary, null));
            } else {
                view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                b1.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }
        }
        if (fr != null) {
            fragmentTransaction.commit();
        }
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
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter (FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem (int position) {
            return ListViewFragment.newInstance(position + 1);
        }

        @Override
        public int getCount () {
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
