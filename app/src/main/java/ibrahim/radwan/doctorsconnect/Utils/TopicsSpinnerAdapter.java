package ibrahim.radwan.doctorsconnect.Utils;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by ibrahimradwan on 8/21/16.
 */
public class TopicsSpinnerAdapter extends ArrayAdapter<Topic> {
    Context context;
    int layoutResourceId;
    List<Topic> data = null;

    public TopicsSpinnerAdapter (Context context, int resource, List<Topic> topics) {
        super(context, resource, topics);
        this.layoutResourceId = resource;
        this.context = context;
        this.data = topics;
    }

    @Override
    public View getView (int position, View convertView, ViewGroup parent) {
        TextView tv = new TextView(context);
        tv.setText(data.get(position).getTitle());
        return tv;
    }
}
