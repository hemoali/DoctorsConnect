package ibrahim.radwan.doctorsconnect;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ibrahim.radwan.doctorsconnect.Models.Conference;
import ibrahim.radwan.doctorsconnect.Models.Invite;
import ibrahim.radwan.doctorsconnect.Models.User;
import ibrahim.radwan.doctorsconnect.Utils.Utils;
import ibrahim.radwan.doctorsconnect.adapters.ConferenceAdapter;
import ibrahim.radwan.doctorsconnect.adapters.InviteAdapter;
import ibrahim.radwan.doctorsconnect.data.Contract;
import ibrahim.radwan.doctorsconnect.data.DataProviderFunctions;

/**
 * Created by ibrahimradwan on 8/22/16.
 */

public class ListViewFragment extends Fragment {
    static private User mUser; // Current user

    private List<Conference> allConferences = new ArrayList<>();
    static private List<Invite> allInvites = new ArrayList<>();

    // Adapter
    static private ConferenceAdapter mConferenceAdapter;
    static private SimpleCursorAdapter topicsAdapter;
    static private InviteAdapter mInviteAdapter;

    //Admin Cursors
    static private Cursor mConferenceCursor, topicsCursor, allDoctorsCursor;

    //Doc Cursors
    static private Cursor mTopicsCursor, mInvitesCursor, confCursor;
    private static final String ARG_SECTION_NUMBER = "section_number";
    //Views
    private ListView mainListView;
    private TextView noElementsView;
    static FloatingActionButton fab;

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
    public void onDestroyView () {
        super.onDestroyView();
        if (mConferenceCursor != null) mConferenceCursor.close();
        if (topicsCursor != null) topicsCursor.close();
        if (allDoctorsCursor != null) allDoctorsCursor.close();

        if (mTopicsCursor != null) mTopicsCursor.close();
        if (mInvitesCursor != null) mInvitesCursor.close();
    }

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {

        //Get user
        mUser = Utils.getUserDataFromSharedPreferences(getContext());

        //Define views
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mainListView = (ListView) rootView.findViewById(R.id.main_list_view);
        noElementsView = (TextView) rootView.findViewById(R.id.no_elements_view);
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);

        //Admin Case
        if (mUser.getTypeID().equals(Contract.UserTypeEntry.USER_TYPE_ADMIN_ID)) {
            // Floating button to add new conference
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick (View view) {
                    Intent i = new Intent(getContext(), AddConferenceActivity.class);
                    startActivity(i);
                    getActivity().finish();
                }
            });
            //Setup list views depending on fragment
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) { // confs
                //Show add button
                fab.setVisibility(View.VISIBLE);

                mConferenceAdapter = new ConferenceAdapter(getContext(), null, 0);
                mConferenceCursor = DataProviderFunctions.getInstance().getConfs(getContext());
                mConferenceAdapter.swapCursor(mConferenceCursor);
                mainListView.setAdapter(mConferenceAdapter);

                if (mConferenceCursor.getCount() == 0) {
                    mainListView.setVisibility(View.GONE);
                    noElementsView.setVisibility(View.VISIBLE);
                } else {
                    mConferenceCursor.moveToFirst();
                    allInvites.clear();
                    do {
                        allConferences.add(new Conference(mConferenceCursor.getString(mConferenceCursor.getColumnIndex(Contract.ConfsEntry.COLUMN_CONF_ID)),
                                mConferenceCursor.getString(mConferenceCursor.getColumnIndex(Contract.ConfsEntry.COLUMN_CONF_NAME)),
                                mConferenceCursor.getString(mConferenceCursor.getColumnIndex(Contract.ConfsEntry.COLUMN_TOPIC_ID)),
                                mConferenceCursor.getString(mConferenceCursor.getColumnIndex(Contract.ConfsEntry.COLUMN_CONF_DATETIME))));
                    } while (mConferenceCursor.moveToNext());
                    registerForContextMenu(mainListView);
                    //to open the context menu on one click
                    mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick (AdapterView<?> adapterView, View view, int i, long l) {
                            getActivity().openContextMenu(view);
                        }
                    });
                }
            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) { // topics

                //Hide add button
                fab.setVisibility(View.INVISIBLE);

                topicsCursor = DataProviderFunctions.getInstance().getTopics(getContext());
                if (topicsCursor.getCount() > 0) {
                    topicsCursor.moveToFirst();
                    SimpleCursorAdapter c = new SimpleCursorAdapter(getContext(), android.R.layout.simple_list_item_2, topicsCursor, new String[]{Contract.TopicEntry.COLUMN_TOPIC_TITLE, Contract.TopicEntry.COLUMN_DOC_ID}, new int[]{android.R.id.text1, android.R.id.text2}, 0);
                    c.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                        @Override
                        public boolean setViewValue (View view, Cursor cursor, int columnIndex) {
                            if (view.getId() == android.R.id.text2) {
                                User u = DataProviderFunctions.getInstance().getUserByID(cursor.getString(cursor.getColumnIndex(Contract.TopicEntry.COLUMN_DOC_ID)), getContext());
                                if (u == null) {
                                    ((TextView) view).setText(R.string.try_again);
                                    ((TextView) view).setTextColor(Color.RED);
                                } else {
                                    ((TextView) view).setText((u).getUserEmail());
                                }
                                return true;
                            } else if (view.getId() == android.R.id.text1) {
                                ((TextView) view).setTextSize(22);
                                return false;
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
        } else { // Doctor
            // Setup floating button to add new Topic

            fab.setOnClickListener(new View.OnClickListener() {
                String topicText = "";

                @Override
                public void onClick (View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle(getString(R.string.topic_title));

                    // Set up the input edit text
                    final EditText input = new EditText(getActivity());
                    input.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(input);

                    // Set up the buttons
                    builder.setPositiveButton("ADD", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick (DialogInterface dialog, int which) {
                            topicText = input.getText().toString().trim();
                            if (topicText.length() < 10) {
                                Toast.makeText(getActivity(), R.string.minimum_tem_length, Toast.LENGTH_SHORT).show();
                                return;
                            }
                            // ContentProvider to add the topic
                            if (-1 != DataProviderFunctions.getInstance().AddTopic(mUser.getUserID(), topicText, getContext())) {
                                //Refetch topics
                                mTopicsCursor = DataProviderFunctions.getInstance().getTopics(getContext());
                                //Update adapter
                                topicsAdapter.swapCursor(mTopicsCursor);
                                mainListView.setVisibility(View.VISIBLE);
                                noElementsView.setVisibility(View.GONE);
                            } else {
                                Toast.makeText(getContext(), R.string.try_again, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick (DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                }
            });
            // Setup listviews depending on the fragment
            if (getArguments().getInt(ARG_SECTION_NUMBER) == 1) { // topics
                //Hide add button
                fab.setVisibility(View.VISIBLE);

                mTopicsCursor = DataProviderFunctions.getInstance().getTopics(getContext());
                topicsAdapter = new SimpleCursorAdapter(getContext(), android.R.layout.simple_list_item_2, mTopicsCursor, new String[]{Contract.TopicEntry.COLUMN_TOPIC_TITLE, Contract.TopicEntry.COLUMN_DOC_ID}, new int[]{android.R.id.text1, android.R.id.text2}, 0);
                mainListView.setAdapter(topicsAdapter);

                topicsAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
                    @Override
                    public boolean setViewValue (View view, Cursor cursor, int columnIndex) {
                        if (view.getId() == android.R.id.text2) {
                            User u = (DataProviderFunctions.getInstance().getUserByID(cursor.getString(cursor.getColumnIndex(Contract.TopicEntry.COLUMN_DOC_ID)), getContext()));
                            if (u == null) {
                                ((TextView) view).setText(R.string.try_again);
                                ((TextView) view).setTextColor(Color.RED);
                            } else {
                                ((TextView) view).setText(u.getUserEmail());
                            }
                            return true;
                        } else if (view.getId() == android.R.id.text1) {
                            ((TextView) view).setTextSize(22);
                            return false;
                        }
                        return false;
                    }
                });
                if (mTopicsCursor.getCount() == 0) {
                    mainListView.setVisibility(View.GONE);
                    noElementsView.setVisibility(View.VISIBLE);
                } else {
                    mainListView.setVisibility(View.VISIBLE);
                    noElementsView.setVisibility(View.GONE);
                }
            } else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2) { // invites
                //Hide add button
                fab.setVisibility(View.INVISIBLE);
                //Get invites
                mInviteAdapter = new InviteAdapter(getContext(), null, 0);
                mInvitesCursor = DataProviderFunctions.getInstance().getInvitesByDocID(getContext(), mUser.getUserID());
                mainListView.setAdapter(mInviteAdapter);
                if (mInvitesCursor.getCount() > 0) {
                    mInviteAdapter.swapCursor(mInvitesCursor);
                    mInvitesCursor.moveToFirst();
                    do {
                        allInvites.add(new Invite(mInvitesCursor.getString(mInvitesCursor.getColumnIndex(Contract.InvitesEntry.COLUMN_INVITE_ID)),
                                mInvitesCursor.getString(mInvitesCursor.getColumnIndex(Contract.InvitesEntry.COLUMN_CONF_ID)),
                                mInvitesCursor.getString(mInvitesCursor.getColumnIndex(Contract.InvitesEntry.COLUMN_ADMIN_ID)),
                                mInvitesCursor.getString(mInvitesCursor.getColumnIndex(Contract.InvitesEntry.COLUMN_DOC_ID)),
                                mInvitesCursor.getString(mInvitesCursor.getColumnIndex(Contract.InvitesEntry.COLUMN_STATUS_ID))));
                    } while (mInvitesCursor.moveToNext());

                    registerForContextMenu(mainListView);
                    //to open the context menu on one click
                    mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick (AdapterView<?> adapterView, View view, int i, long l) {
                            getActivity().openContextMenu(view);
                        }
                    });
                } else {
                    mainListView.setVisibility(View.GONE);
                    noElementsView.setVisibility(View.VISIBLE);
                }
            }
        }
        return rootView;
    }

    @Override
    public void onCreateContextMenu (ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (mUser.getTypeID().equals(Contract.UserTypeEntry.USER_TYPE_ADMIN_ID)) {
            menu.setHeaderTitle(R.string.conference_menu);
            menu.add(0, v.getId(), 0, R.string.edit);
            menu.add(0, v.getId(), 0, R.string.delete);
            menu.add(0, v.getId(), 0, R.string.send_invites);
        } else {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            int position = info.position;

            menu.setHeaderTitle(R.string.invite_menu);

            if (allInvites.get(position).getStatusID().equals(Contract.InviteStatusEntry.INVITE_STATUS_PENDING_ID)) {
                menu.add(0, v.getId(), 0, R.string.accept);
                menu.add(0, v.getId(), 0, R.string.reject);
            } else if (allInvites.get(position).getStatusID().equals(Contract.InviteStatusEntry.INVITE_STATUS_ACCEPTED_ID)) {
                menu.add(0, v.getId(), 0, R.string.add_to_calendar);
            }
        }
    }

    @Override
    public boolean onContextItemSelected (MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //Get selected conf
        final int listPosition = info.position;
        if (mUser.getTypeID().equals(Contract.UserTypeEntry.USER_TYPE_ADMIN_ID)) {
            //Delete conf
            if (item.getTitle().toString().equals(getResources().getString(R.string.edit))) {
                Intent i = new Intent(getContext(), AddConferenceActivity.class);
                i.putExtra(Contract.ConfsEntry.COLUMN_CONF_ID, allConferences.get(listPosition).getId());
                i.putExtra(Contract.ConfsEntry.COLUMN_CONF_NAME, allConferences.get(listPosition).getName());
                i.putExtra(Contract.ConfsEntry.COLUMN_TOPIC_ID, allConferences.get(listPosition).getTopic_id());
                i.putExtra(Contract.ConfsEntry.COLUMN_CONF_DATETIME, allConferences.get(listPosition).getDatetime());
                startActivity(i);
                getActivity().finish();
            } else if (item.getTitle().toString().equals(getResources().getString(R.string.delete))) {
                if (DataProviderFunctions.getInstance().deleteConf(allConferences.get(listPosition).getId(), getContext())) {
                    //Update list view
                    mConferenceCursor = DataProviderFunctions.getInstance().getConfs(getContext());
                    mConferenceAdapter.swapCursor(mConferenceCursor);
                    mConferenceAdapter.notifyDataSetChanged();
                    allConferences.remove(listPosition);
                    if (allConferences.size() == 0) {
                        mainListView.setVisibility(View.GONE);
                        noElementsView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getContext(), R.string.try_again, Toast.LENGTH_SHORT).show();
                }
            } else if (item.getTitle().toString().equals(getResources().getString(R.string.send_invites))) {
                //Get all docotrs
                allDoctorsCursor = DataProviderFunctions.getInstance().getDoctors(getActivity());
                if (allDoctorsCursor.getCount() > 0) {
                    AlertDialog.Builder builderSingle = new AlertDialog.Builder(getActivity());
                    //Array adapter for dialog list view
                    final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                            getContext(),
                            android.R.layout.select_dialog_singlechoice) {
                        // To fix the color and size of list text
                        @Override
                        public View getView (int position, View convertView, ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);
                            TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                            text1.setTextColor(Color.BLACK);
                            text1.setTextSize(getResources().getDimension(R.dimen.normal_font_size));
                            return view;
                        }
                    };
                    //Fill all doctors list
                    allDoctorsCursor.moveToFirst();
                    do {
                        arrayAdapter.add(allDoctorsCursor.getString(allDoctorsCursor.getColumnIndex(Contract.UserEntry.COLUMN_USER_EMAIL)));
                    } while (allDoctorsCursor.moveToNext());
                    //Cancel button to dismiss
                    builderSingle.setNegativeButton(
                            "cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick (DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    //Setting the adapter to dialog
                    builderSingle.setAdapter(
                            arrayAdapter,
                            new DialogInterface.OnClickListener() {
                                @Override // What happens when user clicks doctor
                                public void onClick (DialogInterface dialog, int which) {
                                    allDoctorsCursor.moveToPosition(which);
                                    // Insert invite with the selected doc id
                                    if (DataProviderFunctions.getInstance().AddInvite(allDoctorsCursor.getString(allDoctorsCursor.getColumnIndex(Contract.UserEntry.COLUMN_USER_ID)),
                                            Utils.getUserDataFromSharedPreferences(getContext()).getUserID(),
                                            allConferences.get(listPosition).getId(),
                                            getContext()) != -1) {
                                        Toast.makeText(getContext(), R.string.invite_sent, Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    } else {
                                        Toast.makeText(getContext(), R.string.try_again, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    builderSingle.show();
                }
            }
        } else if (mUser.getTypeID().equals(Contract.UserTypeEntry.USER_TYPE_USER_ID)) {
            if (item.getTitle().toString().equals(getResources().getString(R.string.accept))) {
                if (DataProviderFunctions.getInstance().AcceptInvite(allInvites.get(listPosition).getId(), getContext())) {
                    mInvitesCursor = DataProviderFunctions.getInstance().getInvitesByDocID(getContext(), mUser.getUserID());
                    mInviteAdapter.swapCursor(mInvitesCursor);
                    allInvites.get(listPosition).setStatusID(Contract.InviteStatusEntry.INVITE_STATUS_ACCEPTED_ID);
                } else {
                    Toast.makeText(getContext(), R.string.try_again, Toast.LENGTH_SHORT).show();
                }
            } else if (item.getTitle().toString().equals(getResources().getString(R.string.reject))) {
                if (DataProviderFunctions.getInstance().RejectInvite(allInvites.get(listPosition).getId(), getContext())) {
                    mInvitesCursor = DataProviderFunctions.getInstance().getInvitesByDocID(getContext(), mUser.getUserID());
                    mInviteAdapter.swapCursor(mInvitesCursor);
                    allInvites.remove(listPosition);
                    if (allInvites.size() == 0) {
                        mainListView.setVisibility(View.GONE);
                        noElementsView.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(getContext(), R.string.try_again, Toast.LENGTH_SHORT).show();
                }
            } else if (item.getTitle().toString().equals(getResources().getString(R.string.add_to_calendar))) {

                Calendar beginTime = Calendar.getInstance();
                SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM, dd yyyy  k:m", Locale.US);
                //Get invite data
                confCursor = DataProviderFunctions.getInstance().getConfByID(allInvites.get(listPosition).getConfID(), getContext());
                if (confCursor.getCount() != 0) {
                    try {
                        beginTime.setTime(dateFormatter.parse(confCursor.getString(confCursor.getColumnIndex(Contract.ConfsEntry.COLUMN_CONF_DATETIME))));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(Intent.ACTION_INSERT)
                            .setData(CalendarContract.Events.CONTENT_URI)
                            .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                            .putExtra(CalendarContract.Events.TITLE, "Conference: " + confCursor.getString(confCursor.getColumnIndex(Contract.ConfsEntry.COLUMN_CONF_NAME)))
                            .putExtra(CalendarContract.Events.DESCRIPTION, "Attend the medical conference which will discuss: " + DataProviderFunctions.getInstance().getTopicByID(confCursor.getString(confCursor.getColumnIndex(Contract.ConfsEntry.COLUMN_TOPIC_ID)), getContext()).getTitle())
                            .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getActivity().startActivity(intent);
                }
            }
        }
        return true;
    }
}