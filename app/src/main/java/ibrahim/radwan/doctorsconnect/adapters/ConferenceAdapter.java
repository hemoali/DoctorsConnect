package ibrahim.radwan.doctorsconnect.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import ibrahim.radwan.doctorsconnect.models.Topic;
import ibrahim.radwan.doctorsconnect.R;
import ibrahim.radwan.doctorsconnect.data.Contract;
import ibrahim.radwan.doctorsconnect.data.DataProviderFunctions;

/**
 * Created by ibrahimradwan on 8/21/16.
 */
public class ConferenceAdapter extends CursorAdapter {
    public ConferenceAdapter (Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public void bindView (View view, Context context, Cursor cursor) {
        TextView confName = (TextView) view.findViewById(R.id.conf_name);
        TextView confTopic = (TextView) view.findViewById(R.id.conf_topic);
        TextView confTime = (TextView) view.findViewById(R.id.conf_time);
        confName.setText(cursor.getString(cursor.getColumnIndex(Contract.ConfsEntry.COLUMN_CONF_NAME)));
        confTime.setText(cursor.getString(cursor.getColumnIndex(Contract.ConfsEntry.COLUMN_CONF_DATETIME)));
        Topic t = DataProviderFunctions.getInstance().getTopicByID(cursor.getString(cursor.getColumnIndex(Contract.ConfsEntry.COLUMN_TOPIC_ID)), context);
        confTopic.setText(t.getTitle());
    }

    @Override
    public View newView (Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_conf, viewGroup, false);

        return view;
    }
}
