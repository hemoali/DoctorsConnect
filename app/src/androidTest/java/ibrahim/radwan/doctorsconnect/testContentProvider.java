package ibrahim.radwan.doctorsconnect;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.test.AndroidTestCase;

import ibrahim.radwan.doctorsconnect.data.Contract;
import ibrahim.radwan.doctorsconnect.data.Database;

/**
 * Created by ibrahimradwan on 8/20/16.
 */
public class TestContentProvider extends AndroidTestCase {
    @Override
    protected void setUp () throws Exception {
        super.setUp();
        mContext.deleteDatabase(Contract.DATABASE_NAME);
    }

    private void AddUser (String email, String pass, String type) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.UserEntry.COLUMN_USER_EMAIL, email);
        contentValues.put(Contract.UserEntry.COLUMN_USER_PASS, pass);
        contentValues.put(Contract.UserEntry.COLUMN_USER_TYPE, type);
        Uri uri = mContext.getContentResolver().insert(Contract.UserEntry.CONTENT_URI_SIGNUP, contentValues);
        assertTrue("Error, cannot insert user!!!", ContentUris.parseId(uri) != -1);
    }

    private void AddInvite (String doc_id, String admin_id, String conf_id) {
        ContentValues values = new ContentValues();
        values.put(Contract.InvitesEntry.COLUMN_DOC_ID, doc_id);
        values.put(Contract.InvitesEntry.COLUMN_ADMIN_ID, admin_id);
        values.put(Contract.InvitesEntry.COLUMN_CONF_ID, conf_id);
        values.put(Contract.InvitesEntry.COLUMN_STATUS_ID, Contract.InviteStatusEntry.INVITE_STATUS_PENDING_ID);
        Uri uri = mContext.getContentResolver().insert(Contract.InvitesEntry.CONTENT_URI_ADD_INVITE, values);
        assertTrue("Error, cannot insert invite!!!", ContentUris.parseId(uri) != -1);
    }

    private void AcceptInvite (String id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.InvitesEntry.COLUMN_STATUS_ID, Contract.InviteStatusEntry.INVITE_STATUS_ACCEPTED_ID);
        mContext.getContentResolver().update(Contract.InvitesEntry.CONTENT_URI_ACCEPT_INVITE, contentValues, null, new String[]{id});

    }

    private void RejectInvite (String id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.InvitesEntry.COLUMN_STATUS_ID, Contract.InviteStatusEntry.INVITE_STATUS_REJECTED_ID);
        mContext.getContentResolver().update(Contract.InvitesEntry.CONTENT_URI_REJECT_INVITE, contentValues, null, new String[]{id});
    }

    private void getInvites () {
        CursorLoader cursorLoader = new CursorLoader(mContext, Contract.InvitesEntry.CONTENT_URI_GET_INVITES.buildUpon().appendPath("1").build(),
                null, null, null, null);
        Cursor c = cursorLoader.loadInBackground();
        c.moveToFirst();
        assertTrue("Error: Cannot fetch invites", c.getCount() > 0);
        c.close();
    }

    private void checkLastInviteStatus (String inviteStatusId) {
        CursorLoader cursorLoader = new CursorLoader(mContext, Contract.InvitesEntry.CONTENT_URI_GET_INVITES.buildUpon().appendPath("1").build(),
                null, null, null, null);
        Cursor c = cursorLoader.loadInBackground();
        c.moveToFirst();
        assertTrue("Error: Cannot fetch invites or some invites are incorrect", c.getCount() > 0 && c.getString(c.getColumnIndex(Contract.InvitesEntry.COLUMN_STATUS_ID)).equals(inviteStatusId));
        c.close();
    }

    private void AddConf (String name, String time, String topic_id) {
        ContentValues values = new ContentValues();
        values.put(Contract.ConfsEntry.COLUMN_CONF_NAME, name);
        values.put(Contract.ConfsEntry.COLUMN_CONF_DATETIME, time);
        values.put(Contract.ConfsEntry.COLUMN_TOPIC_ID, topic_id);
        Uri uri = mContext.getContentResolver().insert(Contract.ConfsEntry.CONTENT_URI_ADD_CONF, values);
        assertTrue("Error, cannot insert conf!!!", ContentUris.parseId(uri) != -1);
    }

    private void AddTopic (String doc_id, String title) {
        ContentValues values = new ContentValues();
        values.put(Contract.TopicEntry.COLUMN_DOC_ID, doc_id);
        values.put(Contract.TopicEntry.COLUMN_TOPIC_TITLE, title);
        Uri uri = mContext.getContentResolver().insert(Contract.TopicEntry.CONTENT_URI_ADD_TOPIC, values);
        assertTrue("Error, cannot insert topic!!!", ContentUris.parseId(uri) != -1);
    }

    private void getUsers () {
        CursorLoader cursorLoader = new CursorLoader(mContext, Contract.UserEntry.CONTENT_URI_GET_USER,
                null, null, null, null);
        Cursor c = cursorLoader.loadInBackground();
        c.moveToFirst();
        assertFalse("Error: Cannot fetch doctors", c.getCount() == 0);
        c.close();
    }

    private void userLogin () {
        CursorLoader cursorLoader = new CursorLoader(mContext, Contract.UserEntry.CONTENT_URI_LOGIN,
                null, null, new String[]{"ex@exx.com", "exexex"}, null);
        Cursor c = cursorLoader.loadInBackground();
        c.moveToFirst();
        assertFalse("Error: Cannot login user", c.getCount() == 0);
        c.close();
    }

    private void getConfs () {
        CursorLoader cursorLoader = new CursorLoader(mContext, Contract.ConfsEntry.CONTENT_URI_GET_CONFS,
                null, null, null, null);
        Cursor c = cursorLoader.loadInBackground();
        c.moveToFirst();
        assertFalse("Error: Cannot fetch confs", c.getCount() == 0);
        c.close();
    }

    private void getTopics () {
        CursorLoader cursorLoader = new CursorLoader(mContext, Contract.TopicEntry.CONTENT_URI_GET_TOPICS,
                null, null, null, null);
        Cursor c = cursorLoader.loadInBackground();
        c.moveToFirst();
        assertFalse("Error: Cannot fetch topics", c.getCount() == 0);
        c.close();
    }

    private void UpdateConf (String conf_id, String name, String time, String topic_id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.ConfsEntry.COLUMN_TOPIC_ID, topic_id);
        contentValues.put(Contract.ConfsEntry.COLUMN_CONF_NAME, name);
        contentValues.put(Contract.ConfsEntry.COLUMN_CONF_DATETIME, time);
        mContext.getContentResolver().update(Contract.ConfsEntry.CONTENT_URI_UPDATE_CONF, contentValues, null, new String[]{conf_id});
    }

    private void checkLastConfStatus () {
        CursorLoader cursorLoader = new CursorLoader(mContext, Contract.ConfsEntry.CONTENT_URI_GET_CONFS,
                null, null, null, null);
        Cursor c = cursorLoader.loadInBackground();
        c.moveToFirst();
        assertTrue("Error: Cannot fetch confs or some confs are incorrect", c.getCount() > 0 && c.getString(c.getColumnIndex(Contract.ConfsEntry.COLUMN_CONF_NAME)).equals("aaa"));
        c.close();
    }

    private void checkEmptyConfList () {
        CursorLoader cursorLoader = new CursorLoader(mContext, Contract.ConfsEntry.CONTENT_URI_GET_CONFS,
                null, null, null, null);
        Cursor c = cursorLoader.loadInBackground();
        assertTrue("Error: Loaded even if no data", c.getCount() == 0);
        c.close();
    }

    private void deleteConf (String id) {
        mContext.getContentResolver().delete(Contract.ConfsEntry.CONTENT_URI_DELETE_CONF.buildUpon().appendPath(id).build(), null, null);
    }

    public void testProvider () {

        Database database = new Database(
                this.mContext);
        SQLiteDatabase db = database.getWritableDatabase();
        assertEquals(true, db.isOpen());

        // testing insert using URI

        AddUser("ex@ex.com", "exexex", "1");
        AddUser("exADMIN@ex.com", "exexex", "2");
        AddUser("ex@exx.com", "exexex", "1");


        AddTopic("1", "aasd");

        //Test confs actions
        AddConf("asd", "!23", "1");

        UpdateConf("1", "aaa", "!we", "1");
        checkLastConfStatus();
        deleteConf("1");
        checkEmptyConfList();

        AddConf("asd", "!23", "1");

        //testing adding invites
        AddInvite("1", "2", "2");

        // update/check statuses
        checkLastInviteStatus(Contract.InviteStatusEntry.INVITE_STATUS_PENDING_ID);
        AcceptInvite("1");
        checkLastInviteStatus(Contract.InviteStatusEntry.INVITE_STATUS_ACCEPTED_ID);
        RejectInvite("1");
        checkLastInviteStatus(Contract.InviteStatusEntry.INVITE_STATUS_REJECTED_ID);

        // Query get users
        getUsers();

        //Login user

        userLogin();

        //Get invites
        getInvites();
        //Get confs
        getConfs();

        //Get Topics
        getTopics();
        database.close();
        db.close();
    }


}
