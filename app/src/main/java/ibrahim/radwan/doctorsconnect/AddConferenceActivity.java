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

import ibrahim.radwan.doctorsconnect.Utils.Topic;
import ibrahim.radwan.doctorsconnect.Utils.TopicsSpinnerAdapter;
import ibrahim.radwan.doctorsconnect.data.Contract;
import ibrahim.radwan.doctorsconnect.data.DataProviderFunctions;

public class AddConferenceActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String topic_id = null;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private SimpleDateFormat dateFormatter;

    @Override
    public void onItemSelected (AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected (AdapterView<?> adapterView) {

    }

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

        final EditText confNameEditText = (EditText) findViewById(R.id.conf_name_edittext);
        Spinner topicSpinner = (Spinner) findViewById(R.id.topic_spinner);
        TextView noTopicsTV = (TextView) findViewById(R.id.no_topics_view);
        Button addConf = (Button) findViewById(R.id.add_conference_button);

        final EditText confDateTimeEditText = (EditText) findViewById(R.id.conf_datetime_editText);
        confDateTimeEditText.setKeyListener(null); // disable datetime field
        confDateTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                datePickerDialog.show();
            }
        });

        //setup topics spinner
        topicSpinner.setOnItemSelectedListener(this);
        Cursor topicsCursor = DataProviderFunctions.getInstance().getTopics(getApplicationContext());
        if (topicsCursor != null && topicsCursor.getCount() > 0) {
            List<Topic> topics = new ArrayList<>();
            do {
                topics.add(
                        new Topic(topicsCursor.getString(topicsCursor.getColumnIndex(Contract.TopicEntry.COLUMN_TOPIC_ID)),
                                topicsCursor.getString(topicsCursor.getColumnIndex(Contract.TopicEntry.COLUMN_DOC_ID)),
                                topicsCursor.getString(topicsCursor.getColumnIndex(Contract.TopicEntry.COLUMN_TOPIC_TITLE))));
            } while (topicsCursor.moveToNext());
            final TopicsSpinnerAdapter dataAdapter = new TopicsSpinnerAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, topics);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            topicSpinner.setAdapter(dataAdapter);
            topicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected (AdapterView<?> adapterView, View view, int i, long l) {
                    topic_id = ((Topic) dataAdapter.getItem(i)).getId();
                }

                @Override
                public void onNothingSelected (AdapterView<?> adapterView) {

                }
            });
        } else {
            noTopicsTV.setVisibility(View.VISIBLE);
            topicSpinner.setVisibility(View.GONE);
        }

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
        // Adding topic button
        addConf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                String confName = confNameEditText.getText().toString().trim();
                String confDateTime = confDateTimeEditText.getText().toString().trim();
                if (confName.length() == 0 || topic_id == null || confDateTime.length() == 0) {
                    Toast.makeText(getApplicationContext(), R.string.all_fileds_are_required, Toast.LENGTH_SHORT).show();
                    return;
                }
                DataProviderFunctions.getInstance().AddConf(confName, confDateTime, topic_id, getApplicationContext());
            }
        });
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
