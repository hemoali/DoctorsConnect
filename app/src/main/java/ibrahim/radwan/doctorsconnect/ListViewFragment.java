package ibrahim.radwan.doctorsconnect;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.LayoutInflater;
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

import ibrahim.radwan.doctorsconnect.Models.Conference;
import ibrahim.radwan.doctorsconnect.Models.User;
import ibrahim.radwan.doctorsconnect.Utils.Utils;
import ibrahim.radwan.doctorsconnect.adapters.ConferenceAdapter;
import ibrahim.radwan.doctorsconnect.data.Contract;
import ibrahim.radwan.doctorsconnect.data.DataProviderFunctions;

/**
 * Created by ibrahimradwan on 8/22/16.
 */

public class ListViewFragment extends Fragment {
    static private User mUser; // Current user

    static private List<Conference> allConferences = new ArrayList<>();

    // Adapter
    static private ConferenceAdapter mConferenceAdapter;

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
        if (mUser.getTypeID() == Contract.UserTypeEntry.USER_TYPE_ADMIN_ID) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            //Get selected conf
            final int listPosition = info.position;
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
                DataProviderFunctions.getInstance().deleteConf(allConferences.get(listPosition).getId(), getContext());
                //Update list view
                Cursor c = DataProviderFunctions.getInstance().getConfs(getContext());
                mConferenceAdapter.swapCursor(c);
                mConferenceAdapter.notifyDataSetChanged();
                allConferences.remove(listPosition);
                if (allConferences.size() == 0) {
                    mainListView.setVisibility(View.GONE);
                    noElementsView.setVisibility(View.VISIBLE);
                }
            } else if (item.getTitle().toString().equals(getResources().getString(R.string.send_invites))) {
                //Get all docotrs
                final Cursor allDoctors = DataProviderFunctions.getInstance().getDoctors(getActivity());

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
                        text1.setTextSize(getResources().getDimension(R.dimen.small_font_size) / 2);
                        return view;
                    }
                };
                //Fill all doctors list
                allDoctors.moveToFirst();
                do {
                    arrayAdapter.add(allDoctors.getString(allDoctors.getColumnIndex(Contract.UserEntry.COLUMN_USER_EMAIL)));
                } while (allDoctors.moveToNext());
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
                                allDoctors.moveToPosition(which);
                                // Insert invite with the selected doc id
                                if (DataProviderFunctions.getInstance().AddInvite(allDoctors.getString(allDoctors.getColumnIndex(Contract.UserEntry.COLUMN_USER_ID)),
                                        Utils.getUserDataFromSharedPreferences(getContext()).getUserID(),
                                        allConferences.get(listPosition).getId(),
                                        getContext()) != -1) {
                                    Toast.makeText(getContext(), R.string.invite_sent, Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            }
                        });
                builderSingle.show();
            }
        }
        return true;
    }
}