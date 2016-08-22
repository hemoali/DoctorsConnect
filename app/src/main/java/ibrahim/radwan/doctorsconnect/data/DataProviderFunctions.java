package ibrahim.radwan.doctorsconnect.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import ibrahim.radwan.doctorsconnect.models.User;
import ibrahim.radwan.doctorsconnect.models.Topic;
import ibrahim.radwan.doctorsconnect.utils.PermissionException;

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

    /**
     * Auth user to login
     *
     * @param email
     * @param pass
     * @param context
     * @return User object if operation done, null otherwise
     */
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

    /**
     * Adds user to database
     *
     * @param email
     * @param pass
     * @param type
     * @param context
     * @return User object if the inserting was successful, null if error happened
     * @throws SQLiteConstraintException: When user type id doesn't exist
     */
    public User AddUser (String email, String pass, String type, Context context) throws SQLiteConstraintException {
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

    /**
     * Check if this email exists in the db
     *
     * @param email
     * @param context
     * @return Cursor from db
     */
    public Cursor checkForEmail (String email, Context context) {
        CursorLoader cursorLoader = new CursorLoader(context, Contract.UserEntry.CONTENT_URI_EMAIL_CHECK,
                null, null, new String[]{email}, null);
        return cursorLoader.loadInBackground();
    }

    /**
     * Gets the user from db by ID
     *
     * @param id
     * @param context
     * @return User object, null if user doesn't exist
     */
    public User getUserByID (String id, Context context) {
        CursorLoader cursorLoader = new CursorLoader(context, Contract.UserEntry.CONTENT_URI_GET_USER.buildUpon().appendPath(id).build(),
                null, null, null, null);
        Cursor c = cursorLoader.loadInBackground();
        User u = null;
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            u = new User(id,
                    c.getString(c.getColumnIndex(Contract.UserEntry.COLUMN_USER_EMAIL)),
                    null,
                    c.getString(c.getColumnIndex(Contract.UserEntry.COLUMN_USER_TYPE)));
            c.close();
        }
        return u;
    }

    /**
     * Fetches all doctors from db
     *
     * @param context
     * @return cursor points to doctors
     */
    public Cursor getDoctors (Context context) throws PermissionException {
        CursorLoader cursorLoader = new CursorLoader(context, Contract.UserEntry.CONTENT_URI_GET_USER,
                null, null, null, null);
        Cursor c = cursorLoader.loadInBackground();
        if (c != null && c.getCount() > 0) {
            return c;
        }

        return null;
    }

    /**
     * Adds topic to the db
     *
     * @param doc_id
     * @param title
     * @param context
     * @return topic _id and -1 if cannot insert
     * @throws SQLiteConstraintException: When doctor id doesn't exist
     */
    public long AddTopic (String doc_id, String title, Context context) throws SQLiteConstraintException, PermissionException {
        ContentValues values = new ContentValues();
        values.put(Contract.TopicEntry.COLUMN_DOC_ID, doc_id);
        values.put(Contract.TopicEntry.COLUMN_TOPIC_TITLE, title);
        Uri uri = context.getContentResolver().insert(Contract.TopicEntry.CONTENT_URI_ADD_TOPIC, values);
        if (uri == null) return -1;
        return ContentUris.parseId(uri);
    }

    /**
     * Fetchs all topics from db
     *
     * @param context
     * @return cursor points to all topics
     */
    public Cursor getTopics (Context context) {
        CursorLoader cursorLoader = new CursorLoader(context, Contract.TopicEntry.CONTENT_URI_GET_TOPICS,
                null, null, null, null);
        Cursor c = cursorLoader.loadInBackground();
        return c;
    }

    /**
     * Gets topic using its id from db
     *
     * @param id
     * @param context
     * @return Topic object, null if not existing
     */
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

    /**
     * Fethces all conferences
     *
     * @param context
     * @return cursor points to the conferences
     */
    public Cursor getConfs (Context context) {
        CursorLoader cursorLoader = new CursorLoader(context, Contract.ConfsEntry.CONTENT_URI_GET_CONFS,
                null, null, null, null);
        Cursor c = cursorLoader.loadInBackground();
        if (c != null & c.getCount() > 0) c.moveToFirst();
        return c;
    }

    /**
     * Fetches specific conference using the id
     *
     * @param id
     * @param context
     * @return Cursor
     */
    public Cursor getConfByID (String id, Context context) {
        CursorLoader cursorLoader = new CursorLoader(context, Contract.ConfsEntry.CONTENT_URI_GET_CONF_BY_ID.buildUpon().appendPath(id).build(),
                null, null, null, null);
        Cursor c = cursorLoader.loadInBackground();
        if (c != null & c.getCount() > 0) c.moveToFirst();
        return c;
    }

    /**
     * Inserts conference into the db
     *
     * @param name
     * @param time
     * @param topic_id
     * @param context
     * @return new conference ID, -1 if operation failed
     * @throws SQLiteConstraintException: When topic id doesn't exist
     */
    public long AddConf (String name, String time, String topic_id, Context context) throws SQLiteConstraintException, PermissionException {
        ContentValues values = new ContentValues();
        values.put(Contract.ConfsEntry.COLUMN_CONF_NAME, name);
        values.put(Contract.ConfsEntry.COLUMN_CONF_DATETIME, time);
        values.put(Contract.ConfsEntry.COLUMN_TOPIC_ID, topic_id);
        Uri uri = context.getContentResolver().insert(Contract.ConfsEntry.CONTENT_URI_ADD_CONF, values);
        if (uri == null) return -1;
        return ContentUris.parseId(uri);
    }

    /**
     * Deletes specific conference
     *
     * @param id
     * @param context
     * @return true if deleted, false otherwise
     */
    public boolean deleteConf (String id, Context context) throws PermissionException {
        if (context.getContentResolver().delete(Contract.ConfsEntry.CONTENT_URI_DELETE_CONF.buildUpon().appendPath(id).build(), null, null) == 1) {
            return true;
        }
        return false;
    }

    /**
     * Updates specific conference
     *
     * @param conf_id
     * @param name
     * @param time
     * @param topic_id
     * @param context
     * @return true if updated, false otherwise
     * @throws SQLiteConstraintException: When topic_id doesn't exist
     */
    public boolean UpdateConf (String conf_id, String name, String time, String topic_id, Context context) throws SQLiteConstraintException, PermissionException {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.ConfsEntry.COLUMN_TOPIC_ID, topic_id);
        contentValues.put(Contract.ConfsEntry.COLUMN_CONF_NAME, name);
        contentValues.put(Contract.ConfsEntry.COLUMN_CONF_DATETIME, time);
        if (context.getContentResolver().update(Contract.ConfsEntry.CONTENT_URI_UPDATE_CONF, contentValues, null, new String[]{conf_id}) == 1)
            return true;
        return false;
    }

    /**
     * Adds invite to db
     *
     * @param doc_id
     * @param admin_id
     * @param conf_id
     * @param context
     * @return new invite id, -1 if operation failed
     * @throws SQLiteConstraintException: When doctor_id, admin_id or conf_id don't exist
     */
    public long AddInvite (String doc_id, String admin_id, String conf_id, Context context) throws SQLiteConstraintException, PermissionException {
        ContentValues values = new ContentValues();
        values.put(Contract.InvitesEntry.COLUMN_DOC_ID, doc_id);
        values.put(Contract.InvitesEntry.COLUMN_ADMIN_ID, admin_id);
        values.put(Contract.InvitesEntry.COLUMN_CONF_ID, conf_id);
        values.put(Contract.InvitesEntry.COLUMN_STATUS_ID, Contract.InviteStatusEntry.INVITE_STATUS_PENDING_ID);
        Uri uri = context.getContentResolver().insert(Contract.InvitesEntry.CONTENT_URI_ADD_INVITE, values);
        if (uri == null) return -1;
        return ContentUris.parseId(uri);
    }

    /**
     * Get all invites for a specific doctor
     *
     * @param context
     * @param doc_id
     * @return cursor points to all invites
     */
    public Cursor getInvitesByDocID (Context context, String doc_id) throws PermissionException {
        CursorLoader cursorLoader = new CursorLoader(context, Contract.InvitesEntry.CONTENT_URI_GET_INVITES.buildUpon().appendPath(doc_id).build(),
                null, null, null, null);
        Cursor c = cursorLoader.loadInBackground();
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
        }
        return c;
    }

    /**
     * Accepts specific invite
     *
     * @param id
     * @param context
     * @return true if accpeted, false otherwise
     */
    public boolean AcceptInvite (String id, Context context) throws PermissionException {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.InvitesEntry.COLUMN_STATUS_ID, Contract.InviteStatusEntry.INVITE_STATUS_ACCEPTED_ID);
        if (context.getContentResolver().update(Contract.InvitesEntry.CONTENT_URI_ACCEPT_INVITE, contentValues, null, new String[]{id}) == 1) {
            return true;
        }
        return false;
    }

    /**
     * Reject specific invite
     *
     * @param id
     * @param context
     * @return true if rejected, false otherwise
     */

    public boolean RejectInvite (String id, Context context) throws PermissionException {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.InvitesEntry.COLUMN_STATUS_ID, Contract.InviteStatusEntry.INVITE_STATUS_REJECTED_ID);
        if (1 == context.getContentResolver().update(Contract.InvitesEntry.CONTENT_URI_REJECT_INVITE, contentValues, null, new String[]{id}))
            return true;
        return false;
    }


}
