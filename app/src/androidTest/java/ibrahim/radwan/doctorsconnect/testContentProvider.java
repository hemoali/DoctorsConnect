package ibrahim.radwan.doctorsconnect;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.content.CursorLoader;
import android.test.AndroidTestCase;

import ibrahim.radwan.doctorsconnect.Utils.InvalidDoctorIDException;
import ibrahim.radwan.doctorsconnect.data.Contract;
import ibrahim.radwan.doctorsconnect.data.Database;

/**
 * Created by ibrahimradwan on 8/20/16.
 */
public class testContentProvider extends AndroidTestCase {
    @Override
    protected void setUp () throws Exception {
        super.setUp();
        mContext.deleteDatabase(Contract.DATABASE_NAME);
    }

    public void AddUser (String email, String pass, String type) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.UserEntry.COLUMN_USER_EMAIL, email);
        contentValues.put(Contract.UserEntry.COLUMN_USER_PASS, pass);
        contentValues.put(Contract.UserEntry.COLUMN_USER_TYPE, type);
        Uri uri = mContext.getContentResolver().insert(Contract.UserEntry.CONTENT_URI_SIGNUP, contentValues);
        assertTrue("Error, cannot insert user!!!", ContentUris.parseId(uri) != -1);
    }

    public void AddInvite (String doc_id, String admin_id, String conf_id) {
        ContentValues values = new ContentValues();
        values.put(Contract.InvitesEntry.COLUMN_DOC_ID, doc_id);
        values.put(Contract.InvitesEntry.COLUMN_ADMIN_ID, admin_id);
        values.put(Contract.InvitesEntry.COLUMN_CONF_ID, conf_id);
        values.put(Contract.InvitesEntry.COLUMN_STATUS_ID, Contract.InviteStatusEntry.INVITE_STATUS_PENDING_ID);
        Uri uri = mContext.getContentResolver().insert(Contract.InvitesEntry.CONTENT_URI_ADD_INVITE, values);
        assertTrue("Error, cannot insert invite!!!", ContentUris.parseId(uri) != -1);
    }

    public void AcceptInvite (String id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.InvitesEntry.COLUMN_STATUS_ID, Contract.InviteStatusEntry.INVITE_STATUS_ACCEPTED_ID);
        mContext.getContentResolver().update(Contract.InvitesEntry.CONTENT_URI_ACCEPT_INVITE, contentValues, null, new String[]{id});

    }

    public void RejectInvite (String id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Contract.InvitesEntry.COLUMN_STATUS_ID, Contract.InviteStatusEntry.INVITE_STATUS_REJECTED_ID);
        mContext.getContentResolver().update(Contract.InvitesEntry.CONTENT_URI_REJECT_INVITE, contentValues, null, new String[]{id});
    }

    public void getInvites () {
        CursorLoader cursorLoader = new CursorLoader(mContext, Contract.InvitesEntry.CONTENT_URI_GET_INVITES.buildUpon().appendPath("1").build(),
                null, null, null, null);
        Cursor c = cursorLoader.loadInBackground();
        c.moveToFirst();
        assertTrue("Error: Cannot fetch invites", c.getCount() > 0);
    }

    public void checkLastInviteStatus (String inviteStatusId) {
        CursorLoader cursorLoader = new CursorLoader(mContext, Contract.InvitesEntry.CONTENT_URI_GET_INVITES.buildUpon().appendPath("1").build(),
                null, null, null, null);
        Cursor c = cursorLoader.loadInBackground();
        c.moveToFirst();
        assertTrue("Error: Cannot fetch invites or some invites are incorrect", c.getCount() > 0 && c.getString(c.getColumnIndex(Contract.InvitesEntry.COLUMN_STATUS_ID)).equals(inviteStatusId));
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

        long id;
        ContentValues values = new ContentValues();
        values.put(Contract.TopicEntry.COLUMN_DOC_ID, 1);
        values.put(Contract.TopicEntry.COLUMN_TOPIC_TITLE, "aasd");
        try {
            id = database.insertTopic(values);
            assertFalse("Error inserting topic", id == -1);
        } catch (SQLException | InvalidDoctorIDException e) {
            fail("Error inserting topic");
        }

        values = new ContentValues();
        values.put(Contract.ConfsEntry.COLUMN_CONF_DATETIME, "123");
        values.put(Contract.ConfsEntry.COLUMN_CONF_NAME, "asd");
        values.put(Contract.ConfsEntry.COLUMN_TOPIC_ID, "1");
        try {
            id = database.insertConf(values);
            assertFalse("Error: cannot insert valid conf", id == -1);
        } catch (SQLException e) {
            fail("Error: cannot insert valid conf");
        }

        //testing adding invites
        AddInvite("1", "2", "1");

        checkLastInviteStatus(Contract.InviteStatusEntry.INVITE_STATUS_PENDING_ID);
        AcceptInvite("1");
        checkLastInviteStatus(Contract.InviteStatusEntry.INVITE_STATUS_ACCEPTED_ID);
        RejectInvite("1");
        checkLastInviteStatus(Contract.InviteStatusEntry.INVITE_STATUS_REJECTED_ID);

        // Query get users
        CursorLoader cursorLoader = new CursorLoader(mContext, Contract.UserEntry.CONTENT_URI_GET_USER,
                null, null, null, null);
        Cursor c = cursorLoader.loadInBackground();
        c.moveToFirst();
        assertFalse("Error: Cannot fetch doctors", c.getCount() == 0);

        //Login user
        cursorLoader = new CursorLoader(mContext, Contract.UserEntry.CONTENT_URI_LOGIN,
                null, null, new String[]{"ex@exx.com", "exexex"}, null);
        c = cursorLoader.loadInBackground();
        c.moveToFirst();
        assertFalse("Error: Cannot login user", c.getCount() == 0);

        //Get invites
        getInvites();
        //Get confs
        cursorLoader = new CursorLoader(mContext, Contract.ConfsEntry.CONTENT_URI,
                null, null, null, null);
        c = cursorLoader.loadInBackground();
        c.moveToFirst();
        assertFalse("Error: Cannot fetch confs", c.getCount() == 0);

        //Get Topics
        cursorLoader = new CursorLoader(mContext, Contract.TopicEntry.CONTENT_URI,
                null, null, null, null);
        c = cursorLoader.loadInBackground();
        c.moveToFirst();
        assertFalse("Error: Cannot fetch topics", c.getCount() == 0);
        c.close();
        database.close();
        db.close();
    }
}
