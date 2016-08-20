package ibrahim.radwan.doctorsconnect;

import android.content.Intent;
import android.provider.CalendarContract;
import android.test.AndroidTestCase;

import java.util.Calendar;

/**
 * Created by ibrahimradwan on 8/20/16.
 */
public class TestCalendarContentProvider extends AndroidTestCase {
    @Override
    protected void setUp () throws Exception {
        super.setUp();
    }

    public void testCalendarAPI () {
        Calendar beginTime = Calendar.getInstance();
        beginTime.set(2016, 7, 20, 22, 30);
        Calendar endTime = Calendar.getInstance();
        endTime.set(2016, 7, 20, 23, 30);
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(CalendarContract.Events.TITLE, "Yoga")
                .putExtra(CalendarContract.Events.DESCRIPTION, "Group class")
                .putExtra(CalendarContract.Events.EVENT_LOCATION, "The gym")
                .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_BUSY)
                .putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com")
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);

    }
}
