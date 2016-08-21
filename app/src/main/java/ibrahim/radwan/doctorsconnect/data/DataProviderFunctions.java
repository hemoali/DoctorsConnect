package ibrahim.radwan.doctorsconnect.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import ibrahim.radwan.doctorsconnect.Models.User;
import ibrahim.radwan.doctorsconnect.Models.Topic;

/**
 * Created by ibrahimradwan on 8/21/16.
 */
public class DataProviderFunctions {
    private static DataProviderFunctions dataProviderFunctions = null;

    private DataProviderFunctions () {
    }

    public static DataProviderFunctions getInstance () {
        if (dataProviderFunctions == null) {
            dataProviderFunctions = new DataProviderFunctions();
        }
        return dataProviderFunctions;
    }

    public User userLogin (String email, String pass, Context context) {
        CursorLoader cursorLoader = new CursorLoader(context, Contract.UserEntry.CONTENT_URI_LOGIN,
                null, null, new String[]{email, pass}, null);
        Cursor c = cursorLoader.loadInBackground();
        if (c != null) {
            if (c.getCount() > 0) {
                c.moveToFirst();
                User user = new User(c.getString(c.getColumnIndex(Contract.UserEntry.COLUMN_USER_ID)),
                        email,
                        null,
                        c.getString(c.getColumnIndex(Contract.UserEntry.COLUMN_USER_TYPE)));
                c.close();
                return user;
            }
            c.close();
        }
        return null;
    }

    public User AddUser (String email, String pass, String type, Context context) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.UserEntry.COLUMN_USER_EMAIL, email);
        contentValues.put(Contract.UserEntry.COLUMN_USER_PASS, pass);
        contentValues.put(Contract.UserEntry.COLUMN_USER_TYPE, type);
        Uri uri = context.getContentResolver().insert(Contract.UserEntry.CONTENT_URI_SIGNUP, contentValues);
        if (uri != null && ContentUris.parseId(uri) != -1) {
            return new User(String.valueOf(ContentUris.parseId(uri)), email, null, type);
        }
        return null;
    }

    public Cursor checkForEmail (String email, Context context) {
        CursorLoader cursorLoader = new CursorLoader(context, Contract.UserEntry.CONTENT_URI_EMAIL_CHECK,
                null, null, new String[]{email}, null);
        return cursorLoader.loadInBackground();
    }

    public User getUserByID (String id, Context context) {
        CursorLoader cursorLoader = new CursorLoader(context, Contract.UserEntry.CONTENT_URI_GET_USER.buildUpon().appendPath(id).build(),
                null, null, null, null);
        Cursor c = cursorLoader.loadInBackground();
        c.moveToFirst();
        User u = new User(id,
                c.getString(c.getColumnIndex(Contract.UserEntry.COLUMN_USER_EMAIL)),
                null,
                c.getString(c.getColumnIndex(Contract.UserEntry.COLUMN_USER_TYPE)));
        c.close();
        return u;
    }

    public Cursor getDoctors (Context context) {
        CursorLoader cursorLoader = new CursorLoader(context, Contract.UserEntry.CONTENT_URI_GET_USER,
                null, null, null, null);
        Cursor c = cursorLoader.loadInBackground();
        if (c != null && c.getCount() > 0) {
            return c;
        }

        return null;
    }

    public long AddTopic (String doc_id, String title, Context context) {
        ContentValues values = new ContentValues();
        values.put(Contract.TopicEntry.COLUMN_DOC_ID, doc_id);
        values.put(Contract.TopicEntry.COLUMN_TOPIC_TITLE, title);
        Uri uri = context.getContentResolver().insert(Contract.TopicEntry.CONTENT_URI_ADD_TOPIC, values);
        if (uri == null) return -1;
        return ContentUris.parseId(uri);
    }

    public Cursor getTopics (Context context) {
        CursorLoader cursorLoader = new CursorLoader(context, Contract.TopicEntry.CONTENT_URI_GET_TOPICS,
                null, null, null, null);
        Cursor c = cursorLoader.loadInBackground();
        return c;
    }

    public Topic getTopicByID (String id, Context context) {
        CursorLoader cursorLoader = new CursorLoader(context, Contract.TopicEntry.CONTENT_URI_GET_TOPIC_BY_ID.buildUpon().appendPath(id).build(),
                null, null, null, null);
        Cursor c = cursorLoader.loadInBackground();
        c.moveToFirst();
        Topic t = null;
        if (c.getCount() > 0) {
            t = new Topic(c.getString(c.getColumnIndex(Contract.TopicEntry.COLUMN_TOPIC_ID)),
                    c.getString(c.getColumnIndex(Contract.TopicEntry.COLUMN_DOC_ID)),
                    c.getString(c.getColumnIndex(Contract.TopicEntry.COLUMN_TOPIC_TITLE)));
        }
        c.close();
        return t;
    }

    public Cursor getConfs (Context context) {
        CursorLoader cursorLoader = new CursorLoader(context, Contract.ConfsEntry.CONTENT_URI_GET_CONFS,
                null, null, null, null);
        Cursor c = cursorLoader.loadInBackground();
        c.moveToFirst();
        return c;
    }

    public long AddConf (String name, String time, String topic_id, Context context) {
        ContentValues values = new ContentValues();
        values.put(Contract.ConfsEntry.COLUMN_CONF_NAME, name);
        values.put(Contract.ConfsEntry.COLUMN_CONF_DATETIME, time);
        values.put(Contract.ConfsEntry.COLUMN_TOPIC_ID, topic_id);
        Uri uri = context.getContentResolver().insert(Contract.ConfsEntry.CONTENT_URI_ADD_CONF, values);
        if (uri == null) return -1;
        return ContentUris.parseId(uri);
    }

    public boolean deleteConf (String id, Context context) {
        if (context.getContentResolver().delete(Contract.ConfsEntry.CONTENT_URI_DELETE_CONF.buildUpon().appendPath(id).build(), null, null) == 1) {
            return true;
        }
        return false;
    }

    public boolean UpdateConf (String conf_id, String name, String time, String topic_id, Context context) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.ConfsEntry.COLUMN_TOPIC_ID, topic_id);
        contentValues.put(Contract.ConfsEntry.COLUMN_CONF_NAME, name);
        contentValues.put(Contract.ConfsEntry.COLUMN_CONF_DATETIME, time);
        if (context.getContentResolver().update(Contract.ConfsEntry.CONTENT_URI_UPDATE_CONF, contentValues, null, new String[]{conf_id}) == 1)
            return true;
        return false;
    }

    public long AddInvite (String doc_id, String admin_id, String conf_id, Context context) {
        ContentValues values = new ContentValues();
        values.put(Contract.InvitesEntry.COLUMN_DOC_ID, doc_id);
        values.put(Contract.InvitesEntry.COLUMN_ADMIN_ID, admin_id);
        values.put(Contract.InvitesEntry.COLUMN_CONF_ID, conf_id);
        values.put(Contract.InvitesEntry.COLUMN_STATUS_ID, Contract.InviteStatusEntry.INVITE_STATUS_PENDING_ID);
        Uri uri = context.getContentResolver().insert(Contract.InvitesEntry.CONTENT_URI_ADD_INVITE, values);
        if (uri == null) return -1;
        return ContentUris.parseId(uri);
    }
}
