package ibrahim.radwan.doctorsconnect;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ibrahim.radwan.doctorsconnect.Models.User;
import ibrahim.radwan.doctorsconnect.Utils.Conference;
import ibrahim.radwan.doctorsconnect.Utils.ConferenceAdapter;
import ibrahim.radwan.doctorsconnect.Utils.Utils;
import ibrahim.radwan.doctorsconnect.data.Contract;
import ibrahim.radwan.doctorsconnect.data.DataProviderFunctions;

public class MainActivity extends AppCompatActivity {
    static private User mUser;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;

    static FloatingActionButton fab;

    static private List<Conference> allConferences = new ArrayList<>();

    // Adapters
    static private ConferenceAdapter mConferenceAdapter;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);

        //Get user
        mUser = Utils.getUserDataFromSharedPreferences(getApplicationContext());

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

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Intent i = new Intent(getApplicationContext(), AddConferenceActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    @Override
    public void onCreateContextMenu (ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.setHeaderTitle(R.string.conference_menu);
        menu.add(0, v.getId(), 0, R.string.edit);
        menu.add(0, v.getId(), 0, R.string.delete);
        menu.add(0, v.getId(), 0, R.string.send_invites);
    }

    @Override
    public boolean onContextItemSelected (MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int listPosition = info.position;
        if (item.getTitle().toString().equals(getResources().getString(R.string.edit))) {
            Intent i = new Intent(getBaseContext(), AddConferenceActivity.class);
            i.putExtra(Contract.ConfsEntry.COLUMN_CONF_ID, allConferences.get(listPosition).getId());
            i.putExtra(Contract.ConfsEntry.COLUMN_CONF_NAME, allConferences.get(listPosition).getName());
            i.putExtra(Contract.ConfsEntry.COLUMN_TOPIC_ID, allConferences.get(listPosition).getTopic_id());
            i.putExtra(Contract.ConfsEntry.COLUMN_CONF_DATETIME, allConferences.get(listPosition).getDatetime());
            startActivity(i);
            finish();
        } else if (item.getTitle().toString().equals(getResources().getString(R.string.delete))) {
            DataProviderFunctions.getInstance().deleteConf(allConferences.get(listPosition).getId(), getApplicationContext());
            //Update list view
            Cursor c = DataProviderFunctions.getInstance().getConfs(getApplicationContext());
            mConferenceAdapter.swapCursor(c);
            mConferenceAdapter.notifyDataSetChanged();
        } else if (item.getTitle().toString().equals(getResources().getString(R.string.send_invites))) {
//ToDo send invites :: Extract Doctors as list
            final Cursor allDoctors = DataProviderFunctions.getInstance().getDoctors(getApplicationContext());

            AlertDialog.Builder builderSingle = new AlertDialog.Builder(getApplicationContext());

            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    getApplicationContext(),
                    android.R.layout.select_dialog_singlechoice);
            do {
                arrayAdapter.add(allDoctors.getString(allDoctors.getColumnIndex(Contract.UserEntry.COLUMN_USER_EMAIL)));
            } while (allDoctors.moveToNext());

            builderSingle.setNegativeButton(
                    "cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick (DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            builderSingle.setAdapter(
                    arrayAdapter,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick (DialogInterface dialog, int which) {
                            allDoctors.moveToPosition(which);
                            if (DataProviderFunctions.getInstance().AddInvite(allDoctors.getString(allDoctors.getColumnIndex(Contract.UserEntry.COLUMN_USER_ID)),
                                    Utils.getUserDataFromSharedPreferences(getApplicationContext()).getUserID(),
                                    allConferences.get(listPosition).getId(),
                                    getApplicationContext()) != -1) {
                                Toast.makeText(getApplicationContext(), R.string.intive_sent, Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }
                    });
            builderSingle.show();

        }
        return true;
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
    public static class ListViewFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";

        public ListViewFragment () {
        }

        public static ListViewFragment newInstance (int sectionNumber) {
            ListViewFragment fragment = new ListViewFragment();
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
            if (mUser.getTypeID().equals(Contract.UserTypeEntry.USER_TYPE_ADMIN_ID)) {
                if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) { // confs
                    //Show add button
                    fab.setVisibility(View.VISIBLE);
                    mConferenceAdapter = new ConferenceAdapter(getContext(), null, 0);
                    Cursor c = DataProviderFunctions.getInstance().getConfs(getContext());
                    mainListView.setAdapter(mConferenceAdapter);
                    if (c.getCount() == 0) {
                        mainListView.setVisibility(View.GONE);
                        noElementsView.setVisibility(View.VISIBLE);
                    } else {
                        mConferenceAdapter.swapCursor(c);
                        c.moveToFirst();
                        do {
                            allConferences.add(new Conference(c.getString(c.getColumnIndex(Contract.ConfsEntry.COLUMN_CONF_ID)),
                                    c.getString(c.getColumnIndex(Contract.ConfsEntry.COLUMN_CONF_NAME)),
                                    c.getString(c.getColumnIndex(Contract.ConfsEntry.COLUMN_TOPIC_ID)),
                                    c.getString(c.getColumnIndex(Contract.ConfsEntry.COLUMN_CONF_DATETIME))));
                        } while (c.moveToNext());
                    }
                    registerForContextMenu(mainListView);
                    //to open the context menu on one click
                    mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick (AdapterView<?> adapterView, View view, int i, long l) {
                            getActivity().openContextMenu(view);
                        }
                    });
                } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) { // topics

                    //Hide add button
                    fab.setVisibility(View.INVISIBLE);

                    Cursor cursor = DataProviderFunctions.getInstance().getTopics(getContext());
                    if (cursor.getCount() > 0) {
                        cursor.moveToFirst();
                        User u = DataProviderFunctions.getInstance().getUserByID(cursor.getString(cursor.getColumnIndex(Contract.TopicEntry.COLUMN_DOC_ID)), getContext());
                        SimpleCursorAdapter c = new SimpleCursorAdapter(getContext(), android.R.layout.simple_list_item_2, cursor, new String[]{Contract.TopicEntry.COLUMN_TOPIC_TITLE, Contract.TopicEntry.COLUMN_DOC_ID}, new int[]{android.R.id.text1, android.R.id.text2}, 0);
                        c.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                            @Override
                            public boolean setViewValue (View view, Cursor cursor, int columnIndex) {
                                if (view.getId() == android.R.id.text2) {
                                    ((TextView) view).setText((DataProviderFunctions.getInstance().getUserByID(cursor.getString(cursor.getColumnIndex(Contract.TopicEntry.COLUMN_DOC_ID)), getContext())).getUserEmail());
                                    return true;
                                } else if (view.getId() == android.R.id.text1) {
                                    ((TextView) view).setTextSize(22);
                                }
                                return false;
                            }
                        });
                        mainListView.setAdapter(c);
                    } else {
                        mainListView.setVisibility(View.GONE);
                        noElementsView.setVisibility(View.VISIBLE);
                    }
                }
            }
            return rootView;
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
