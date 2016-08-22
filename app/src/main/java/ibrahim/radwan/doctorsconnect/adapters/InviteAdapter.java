package ibrahim.radwan.doctorsconnect.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import ibrahim.radwan.doctorsconnect.Models.Topic;
import ibrahim.radwan.doctorsconnect.Models.User;
import ibrahim.radwan.doctorsconnect.R;
import ibrahim.radwan.doctorsconnect.data.Contract;
import ibrahim.radwan.doctorsconnect.data.DataProviderFunctions;

/**
 * Created by ibrahimradwan on 8/21/16.
 */
public class InviteAdapter extends CursorAdapter {
    public InviteAdapter (Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public void bindView (View view, Context context, Cursor cursor) {
        ImageView statusImg = (ImageView) view.findViewById(R.id.status_imageview);
        TextView confName = (TextView) view.findViewById(R.id.conf_name_textview);
        TextView confTopic = (TextView) view.findViewById(R.id.topic_textview);
        TextView confTime = (TextView) view.findViewById(R.id.conf_datetime_textview);
        TextView adminEmail = (TextView) view.findViewById(R.id.admin_textview);
        //Set image
        statusImg.setImageResource((cursor.getString(cursor.getColumnIndex(Contract.InvitesEntry.COLUMN_STATUS_ID)).equals(Contract.InviteStatusEntry.INVITE_STATUS_PENDING_ID)) ? R.drawable.pending : R.drawable.accepted);

        //Get conf name
        Cursor confCursor = DataProviderFunctions.getInstance().getConfByID((cursor.getString(cursor.getColumnIndex(Contract.InvitesEntry.COLUMN_CONF_ID))), context);
        confName.setText(confCursor.getString(confCursor.getColumnIndex(Contract.ConfsEntry.COLUMN_CONF_NAME)));
        confTime.setText(confCursor.getString(confCursor.getColumnIndex(Contract.ConfsEntry.COLUMN_CONF_DATETIME)));

        //Get Topic name
        Topic t = DataProviderFunctions.getInstance().getTopicByID(confCursor.getString(confCursor.getColumnIndex(Contract.ConfsEntry.COLUMN_TOPIC_ID)), context);
        confTopic.setText(t.getTitle());

        //Get Admin Email
        User u = DataProviderFunctions.getInstance().getUserByID(cursor.getString(cursor.getColumnIndex(Contract.InvitesEntry.COLUMN_ADMIN_ID)), context);
        adminEmail.setText("By: " + u.getUserEmail());
    }

    @Override
    public View newView (Context context, Cursor cursor, ViewGroup viewGroup) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_invite, viewGroup, false);

        return view;
    }
}
