package ibrahim.radwan.doctorsconnect;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import ibrahim.radwan.doctorsconnect.Models.Topic;
import ibrahim.radwan.doctorsconnect.adapters.TopicsSpinnerAdapter;
import ibrahim.radwan.doctorsconnect.data.Contract;
import ibrahim.radwan.doctorsconnect.data.DataProviderFunctions;

public class AddConferenceActivity extends AppCompatActivity {
    //Topic connected to conf
    String topic_id = null;

    //Pickers
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    //date formatter
    private SimpleDateFormat dateFormatter;

    // All topics holder (To display)
    private List<Topic> topics = new ArrayList<>();

    //Views
    EditText confNameEditText, confDateTimeEditText;
    Spinner topicSpinner;
    TextView noTopicsTV;
    Button addConf;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Setup Toolbar
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.mipmap.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setTitle(R.string.new_conference);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setContentView(R.layout.activity_add_conference);


        defineViews();

        //setup date time edit text to not accept input and to show the pickers when clicked
        confDateTimeEditText.setKeyListener(null); // disable datetime field
        confDateTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                datePickerDialog.show();
            }
        });

        setupTopicsSpinner();
        //setup topics spinner

        // Setup date picker
        dateFormatter = new SimpleDateFormat("MMM, dd yyyy  k:m", Locale.US);

        Calendar currentCalendar = Calendar.getInstance();
        final Calendar newDate = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet (DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                newDate.set(year, monthOfYear, dayOfMonth);
                timePickerDialog.show();
            }

        }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setCancelable(false);
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        //Setup Timepicker
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet (TimePicker timePicker, int hour, int minute) {
                newDate.set(Calendar.HOUR_OF_DAY, hour);
                newDate.set(Calendar.MINUTE, minute);
                confDateTimeEditText.setText(dateFormatter.format(newDate.getTime()));
            }
        }, currentCalendar.get(Calendar.HOUR_OF_DAY), currentCalendar.get(Calendar.MINUTE), true);

        //Check for intent extras (Edit conf)
        if (getIntent().getStringExtra(Contract.ConfsEntry.COLUMN_CONF_ID) != null) {
            confNameEditText.setText(getIntent().getStringExtra(Contract.ConfsEntry.COLUMN_CONF_NAME));
            confDateTimeEditText.setText(getIntent().getStringExtra(Contract.ConfsEntry.COLUMN_CONF_DATETIME));
            Topic toBeSelected = null;
            for (Topic t : topics) {
                if (t.getId().equals(getIntent().getStringExtra(Contract.ConfsEntry.COLUMN_TOPIC_ID)))
                    toBeSelected = t;
            }
            topic_id = getIntent().getStringExtra(Contract.ConfsEntry.COLUMN_TOPIC_ID);
            topicSpinner.setSelection(topics.indexOf(toBeSelected));
            addConf.setText(R.string.edit_conference);

        }

        // Adding/Editing topic button
        addConf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                String confName = confNameEditText.getText().toString().trim();
                String confDateTime = confDateTimeEditText.getText().toString().trim();
                if (confName.length() == 0 || topic_id == null || confDateTime.length() == 0) {
                    Toast.makeText(getApplicationContext(), R.string.all_fileds_are_required, Toast.LENGTH_SHORT).show();
                    return;
                }
                if (getIntent().getStringExtra(Contract.ConfsEntry.COLUMN_CONF_ID) != null) {
                    DataProviderFunctions.getInstance().UpdateConf(getIntent().getStringExtra(Contract.ConfsEntry.COLUMN_CONF_ID), confName, confDateTime, topic_id, getBaseContext());
                    Toast.makeText(getApplicationContext(), R.string.conference_updated, Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } else {
                    DataProviderFunctions.getInstance().AddConf(confName, confDateTime, topic_id, getApplicationContext());
                    Toast.makeText(getApplicationContext(), R.string.conference_added_successfully, Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        });

    }

    /**
     * Sets up the topics spinner so the admin can pick the topic title from the list
     */
    private void setupTopicsSpinner () {
        // Get the topics
        Cursor topicsCursor = DataProviderFunctions.getInstance().getTopics(getApplicationContext());

        if (topicsCursor != null && topicsCursor.getCount() > 0) {

            topicsCursor.moveToFirst();
            /* Fill topics to the @topics list */
            do {
                topics.add(
                        new Topic(topicsCursor.getString(topicsCursor.getColumnIndex(Contract.TopicEntry.COLUMN_TOPIC_ID)),
                                topicsCursor.getString(topicsCursor.getColumnIndex(Contract.TopicEntry.COLUMN_DOC_ID)),
                                topicsCursor.getString(topicsCursor.getColumnIndex(Contract.TopicEntry.COLUMN_TOPIC_TITLE))));
            } while (topicsCursor.moveToNext());
            /* Define the adapter with the @topics list */
            final TopicsSpinnerAdapter dataAdapter = new TopicsSpinnerAdapter(getBaseContext(), android.R.layout.simple_spinner_item, topics);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            topicSpinner.setAdapter(dataAdapter);

            //Change the topic_id when the admin clicks the wanted topic
            topicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected (AdapterView<?> adapterView, View view, int i, long l) {
                    topic_id = dataAdapter.getItem(i).getId();
                }

                @Override
                public void onNothingSelected (AdapterView<?> adapterView) {

                }
            });
        } else {//if courser empty: hide the spinner and show 'no topics' tv
            noTopicsTV.setVisibility(View.VISIBLE);
            topicSpinner.setVisibility(View.GONE);
        }

    }

    /**
     * Defines the activity views
     */
    public void defineViews () {
        confNameEditText = (EditText) findViewById(R.id.conf_name_edittext);
        confDateTimeEditText = (EditText) findViewById(R.id.conf_datetime_editText);
        topicSpinner = (Spinner) findViewById(R.id.topic_spinner);
        noTopicsTV = (TextView) findViewById(R.id.no_topics_view);
        addConf = (Button) findViewById(R.id.add_conference_button);
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed () {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
    }
}
