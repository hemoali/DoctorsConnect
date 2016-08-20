package ibrahim.radwan.doctorsconnect;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import java.util.HashSet;

import ibrahim.radwan.doctorsconnect.Utils.InvalidDoctorIDException;
import ibrahim.radwan.doctorsconnect.data.Contract;
import ibrahim.radwan.doctorsconnect.data.Database;

/**
 * Created by ibrahimradwan on 8/20/16.
 */
public class TestDb extends AndroidTestCase {
    @Override
    protected void setUp () throws Exception {
        super.setUp();
        mContext.deleteDatabase(Contract.DATABASE_NAME);
    }

    public void testCreateDB () {
        SQLiteDatabase db = new Database(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // Check tables
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(Contract.UserEntry.TABLE_USERS);
        tableNameHashSet.add(Contract.ConfsEntry.TABLE_CONFS);
        tableNameHashSet.add(Contract.InvitesEntry.TABLE_INVITES);
        tableNameHashSet.add(Contract.InviteStatusEntry.TABLE_INVITE_STATUS);
        tableNameHashSet.add(Contract.TopicEntry.TABLE_TOPICS);
        tableNameHashSet.add(Contract.UserTypeEntry.TABLE_USER_TYPE);

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created

        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        assertTrue("Error: Your database was not created correctly tables",
                tableNameHashSet.isEmpty());

        // Check for columns of users table
        c = db.rawQuery("PRAGMA table_info(" + Contract.UserEntry.TABLE_USERS + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for users table information.",
                c.moveToFirst());
        final HashSet<String> usersColumnHashSet = new HashSet<String>();
        usersColumnHashSet.add(Contract.UserEntry.COLUMN_USER_EMAIL);
        usersColumnHashSet.add(Contract.UserEntry.COLUMN_USER_ID);
        usersColumnHashSet.add(Contract.UserEntry.COLUMN_USER_PASS);
        usersColumnHashSet.add(Contract.UserEntry.COLUMN_USER_TYPE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            usersColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required user entry columns",
                usersColumnHashSet.isEmpty());

        // Check for columns of users type
        c = db.rawQuery("PRAGMA table_info(" + Contract.UserTypeEntry.TABLE_USER_TYPE + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for users entry table information.",
                c.moveToFirst());
        final HashSet<String> userTypeColumnHashSet = new HashSet<String>();
        userTypeColumnHashSet.add(Contract.UserTypeEntry.COLUMN_TYPE_ID);
        userTypeColumnHashSet.add(Contract.UserTypeEntry.COLUMN_TYPE_NAME);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            userTypeColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required user type entry columns",
                userTypeColumnHashSet.isEmpty());

        //Check for preset raws
        Cursor userTypeCursor = db.query(
                Contract.UserTypeEntry.TABLE_USER_TYPE,  // Table to Query
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertTrue("Error: No Records returned from user type values query", userTypeCursor.moveToFirst());

        final HashSet<String> userTypeListColumnHashSet = new HashSet<String>();
        userTypeListColumnHashSet.add(Contract.UserTypeEntry.USER_TYPE_ADMIN);
        userTypeListColumnHashSet.add(Contract.UserTypeEntry.USER_TYPE_USER);
        columnNameIndex = userTypeCursor.getColumnIndex("type_name");
        Log.e("Error", "Count " + userTypeCursor.getCount());
        do {
            String val = userTypeCursor.getString(columnNameIndex);
            userTypeListColumnHashSet.remove(val);
        } while (userTypeCursor.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required user type entries",
                userTypeListColumnHashSet.isEmpty());

        // Check for columns of invites
        c = db.rawQuery("PRAGMA table_info(" + Contract.InvitesEntry.TABLE_INVITES + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for invites table information.",
                c.moveToFirst());
        final HashSet<String> invitesColumnHashSet = new HashSet<String>();
        invitesColumnHashSet.add(Contract.InvitesEntry.COLUMN_ADMIN_ID);
        invitesColumnHashSet.add(Contract.InvitesEntry.COLUMN_CONF_ID);
        invitesColumnHashSet.add(Contract.InvitesEntry.COLUMN_DOC_ID);
        invitesColumnHashSet.add(Contract.InvitesEntry.COLUMN_INVITE_ID);
        invitesColumnHashSet.add(Contract.InvitesEntry.COLUMN_STATUS_ID);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            invitesColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required invites entry columns",
                invitesColumnHashSet.isEmpty());

        // Check for columns of confs
        c = db.rawQuery("PRAGMA table_info(" + Contract.ConfsEntry.TABLE_CONFS + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for confs table information.",
                c.moveToFirst());
        final HashSet<String> confsColumnHashSet = new HashSet<String>();
        confsColumnHashSet.add(Contract.ConfsEntry.COLUMN_CONF_ID);
        confsColumnHashSet.add(Contract.ConfsEntry.COLUMN_CONF_DATETIME);
        confsColumnHashSet.add(Contract.ConfsEntry.COLUMN_CONF_NAME);
        confsColumnHashSet.add(Contract.ConfsEntry.COLUMN_TOPIC_ID);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            confsColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required confs entry columns",
                confsColumnHashSet.isEmpty());


        // Check for columns of topic
        c = db.rawQuery("PRAGMA table_info(" + Contract.TopicEntry.TABLE_TOPICS + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for topics table information.",
                c.moveToFirst());
        final HashSet<String> topicsColumnHashSet = new HashSet<String>();
        topicsColumnHashSet.add(Contract.TopicEntry.COLUMN_DOC_ID);
        topicsColumnHashSet.add(Contract.TopicEntry.COLUMN_TOPIC_TITLE);
        topicsColumnHashSet.add(Contract.TopicEntry.COLUMN_TOPIC_ID);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            topicsColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required topics entry columns",
                topicsColumnHashSet.isEmpty());


        // Check for columns of invite status
        c = db.rawQuery("PRAGMA table_info(" + Contract.InviteStatusEntry.TABLE_INVITE_STATUS + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for invite status table information.",
                c.moveToFirst());
        final HashSet<String> inviteStatusColumnHashSet = new HashSet<String>();
        inviteStatusColumnHashSet.add(Contract.InviteStatusEntry.COLUMN_STATUS_ID);
        inviteStatusColumnHashSet.add(Contract.InviteStatusEntry.COLUMN_STATUS_NAME);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            inviteStatusColumnHashSet.remove(columnName);
        } while (c.moveToNext());

        assertTrue("Error: The database doesn't contain all of the required invite status entry columns",
                inviteStatusColumnHashSet.isEmpty());

        //Check for preset raws
        Cursor invitesStatusCursor = db.query(
                Contract.InviteStatusEntry.TABLE_INVITE_STATUS,  // Table to Query
                null,
                null,
                null,
                null,
                null,
                null
        );

        assertTrue("Error: No Records returned from invite status values query", invitesStatusCursor.moveToFirst());

        final HashSet<String> inviteStatusListColumnHashSet = new HashSet<String>();
        inviteStatusListColumnHashSet.add(Contract.InviteStatusEntry.INVITE_STATUS_ACCEPTED);
        inviteStatusListColumnHashSet.add(Contract.InviteStatusEntry.INVITE_STATUS_PENDING);
        inviteStatusListColumnHashSet.add(Contract.InviteStatusEntry.INVITE_STATUS_REJECTED);
        columnNameIndex = invitesStatusCursor.getColumnIndex("status_name");
        do {
            String val = invitesStatusCursor.getString(columnNameIndex);
            inviteStatusListColumnHashSet.remove(val);
        } while (invitesStatusCursor.moveToNext());
        assertTrue("Error: The database doesn't contain all of the required status values entries",
                inviteStatusListColumnHashSet.isEmpty());

        c.close();
        db.close();
        userTypeCursor.close();
        invitesStatusCursor.close();
    }

    public void testUser () throws Throwable {
        Database database = new Database(
                this.mContext);
        SQLiteDatabase db = database.getWritableDatabase();
        assertEquals(true, db.isOpen());
        //insert good user
        ContentValues values = new ContentValues();
        values.put(Contract.UserEntry.COLUMN_USER_EMAIL, "ex@ex.com");
        values.put(Contract.UserEntry.COLUMN_USER_PASS, "exexex");
        values.put(Contract.UserEntry.COLUMN_USER_TYPE, "1");//user
        long id = database.insertUser(values);
        assertFalse("Error inserting user", id == -1);


        // reinsert user with same email
        values = new ContentValues();
        values.put(Contract.UserEntry.COLUMN_USER_EMAIL, "ex@ex.com");
        values.put(Contract.UserEntry.COLUMN_USER_PASS, "exexex2");
        values.put(Contract.UserEntry.COLUMN_USER_TYPE, "2");//user
        try {
            id = database.insertUser(values);
            fail("ERROR: The user with same email created");
        } catch (SQLException expectedException) {
        }

        // insert user other than admin or user
        values = new ContentValues();
        values.put(Contract.UserEntry.COLUMN_USER_EMAIL, "ex@ex.cosam");
        values.put(Contract.UserEntry.COLUMN_USER_PASS, "exexex2");
        values.put(Contract.UserEntry.COLUMN_USER_TYPE, "3");//user
        try {
            id = database.insertUser(values);
            fail("ERROR: The user with non-availabe type added");
        } catch (SQLException expectedException) {
        }
        // Check for inserted user
        values = new ContentValues();
        values.put(Contract.UserEntry.COLUMN_USER_EMAIL, "ex@ex.com");
        values.put(Contract.UserEntry.COLUMN_USER_PASS, "exexex");
        Cursor c = database.userLogin(values);
        assertTrue("Error fetching users", c.moveToFirst());

        // Check for non-inserted user
        values = new ContentValues();
        values.put(Contract.UserEntry.COLUMN_USER_EMAIL, "ex2@ex2.com");
        values.put(Contract.UserEntry.COLUMN_USER_PASS, "exexex");
        c = database.userLogin(values);
        assertFalse("Error fetching users", c != null && c.moveToFirst());
        //Insert Admin
        values = new ContentValues();
        values.put(Contract.UserEntry.COLUMN_USER_EMAIL, "exADMIN@ex.com");
        values.put(Contract.UserEntry.COLUMN_USER_PASS, "exexex");
        values.put(Contract.UserEntry.COLUMN_USER_TYPE, "2");//user
        id = database.insertUser(values);
        assertFalse("Error inserting user", id == -1);
        //Fetch all Users
        c = database.fetchDoctors();
        c.moveToFirst();
        assertTrue("Error: cannot fetch doctor !!", c.getCount() == 1 && c.getString(c.getColumnIndex(Contract.UserEntry.COLUMN_USER_EMAIL)).equals("ex@ex.com"));
        c.close();
        db.close();
        database.close();
    }

    public void testTopics () {
        Database database = new Database(
                this.mContext);
        SQLiteDatabase db = database.getWritableDatabase();
        assertEquals(true, db.isOpen());

        // test insert invalid topic (invalid doctor id)
        ContentValues values = new ContentValues();
        values.put(Contract.TopicEntry.COLUMN_DOC_ID, 1);
        values.put(Contract.TopicEntry.COLUMN_TOPIC_TITLE, "");
        try {
            long id = database.insertTopic(values);
            fail("Error: invalid topic inserted: no such doctor");
        } catch (SQLException | InvalidDoctorIDException e) {
        }
        // Insert user to test inset null topic
        values = new ContentValues();
        values.put(Contract.UserEntry.COLUMN_USER_EMAIL, "ex@ex.com");
        values.put(Contract.UserEntry.COLUMN_USER_PASS, "exexex");
        values.put(Contract.UserEntry.COLUMN_USER_TYPE, "1");//user
        long user_id = -1;
        try {
            user_id = database.insertUser(values);
            Log.e("ERROR", "ID " + user_id);
            assertFalse("Error inserting user", user_id == -1);
        } catch (SQLException e) {
        }
        values = new ContentValues();
        values.put(Contract.TopicEntry.COLUMN_DOC_ID, 1);
        values.putNull(Contract.TopicEntry.COLUMN_TOPIC_TITLE);
        try {
            long id = database.insertTopic(values);
            fail("Error: inserting null topic worked !!!");
        } catch (SQLException | InvalidDoctorIDException e) {
        }
        // Insert user to test inset good topic
        values = new ContentValues();
        values.put(Contract.TopicEntry.COLUMN_DOC_ID, 1);
        values.put(Contract.TopicEntry.COLUMN_TOPIC_TITLE, "aasd");
        try {
            long id = database.insertTopic(values);
            assertFalse("Error inserting topic", id == -1);
        } catch (SQLException | InvalidDoctorIDException e) {
            fail("Error inserting topic");
        }

        // Fetch topic
        Cursor topicsCursor = database.fetchTopics();

        assertTrue("Error: No Records returned from topics query", topicsCursor.getCount() == 1);
        topicsCursor.close();
        db.close();
        database.close();
    }

    public void testConf () {
        Database database = new Database(
                this.mContext);
        SQLiteDatabase db = database.getWritableDatabase();
        assertEquals(true, db.isOpen());

        // Insert user to test inset null topic
        ContentValues values = new ContentValues();
        values.put(Contract.UserEntry.COLUMN_USER_EMAIL, "ex@ex.com");
        values.put(Contract.UserEntry.COLUMN_USER_PASS, "exexex");
        values.put(Contract.UserEntry.COLUMN_USER_TYPE, "1");//user
        long user_id = -1;
        try {
            user_id = database.insertUser(values);
            Log.e("ERROR", "ID " + user_id);
            assertFalse("Error inserting user", user_id == -1);
        } catch (SQLException e) {
        }
        // Insert topic to test inset good topic
        values = new ContentValues();
        values.put(Contract.TopicEntry.COLUMN_DOC_ID, 1);
        values.put(Contract.TopicEntry.COLUMN_TOPIC_TITLE, "aasd");
        try {
            long id = database.insertTopic(values);
            assertFalse("Error inserting topic", id == -1);
        } catch (SQLException | InvalidDoctorIDException e) {
            fail("Error inserting topic");
        }
        // Insert invalid topic-id-conf
        values = new ContentValues();
        values.put(Contract.ConfsEntry.COLUMN_CONF_DATETIME, "123124");
        values.put(Contract.ConfsEntry.COLUMN_CONF_NAME, "Conf1");
        values.put(Contract.ConfsEntry.COLUMN_TOPIC_ID, "3");
        try {
            long id = database.insertConf(values);
            fail("Error: inserting invalid topic-id-conf worked !!!");
        } catch (SQLException e) {
        }
        // test insert null conf
        values = new ContentValues();
        values.put(Contract.ConfsEntry.COLUMN_CONF_DATETIME, "123124");
        values.putNull(Contract.ConfsEntry.COLUMN_CONF_NAME);
        values.put(Contract.ConfsEntry.COLUMN_TOPIC_ID, "3");
        try {
            long id = database.insertConf(values);
            fail("Error: inserting invalid null-name-conf worked !!!");
        } catch (SQLException e) {
        }
        // test insert null conf
        values = new ContentValues();
        values.putNull(Contract.ConfsEntry.COLUMN_CONF_DATETIME);
        values.put(Contract.ConfsEntry.COLUMN_CONF_NAME, "asd");
        values.put(Contract.ConfsEntry.COLUMN_TOPIC_ID, "3");
        try {
            long id = database.insertConf(values);
            fail("Error: inserting invalid null-datetime-conf worked !!!");
        } catch (SQLException e) {
        }
        // test insert good conf
        values = new ContentValues();
        values.put(Contract.ConfsEntry.COLUMN_CONF_DATETIME, "123");
        values.put(Contract.ConfsEntry.COLUMN_CONF_NAME, "asd");
        values.put(Contract.ConfsEntry.COLUMN_TOPIC_ID, "1");
        try {
            long id = database.insertConf(values);
            assertFalse("Error: cannot insert valid conf", id == -1);
        } catch (SQLException e) {
            fail("Error: cannot insert valid conf");
        }
        // fetch confs
        Cursor c = database.fetchConfs();
        assertTrue("Error, cannot fetch confs", c.moveToFirst() && c.getCount() == 1);
        assertTrue("Error, cannot fetch correct vals of conf", c.getString(c.getColumnIndex(Contract.ConfsEntry.COLUMN_CONF_NAME)).equals("asd")
                && c.getString(c.getColumnIndex(Contract.ConfsEntry.COLUMN_CONF_DATETIME)).equals("123")
                && c.getString(c.getColumnIndex(Contract.ConfsEntry.COLUMN_TOPIC_ID)).equals("1")
        );

        values = new ContentValues();
        values.put(Contract.ConfsEntry.COLUMN_CONF_DATETIME, "321");
        values.put(Contract.ConfsEntry.COLUMN_CONF_NAME, "dsa");
        values.put(Contract.ConfsEntry.COLUMN_TOPIC_ID, "1");
        //update conf
        assertTrue("Error updating conf!", database.updateConf("1", values));
        // check if confs updated
        c = database.fetchConfs();
        assertTrue("Error, cannot fetch confs", c.moveToFirst() && c.getCount() == 1);
        assertTrue("Error, cannot fetch correct vals of conf", c.getString(c.getColumnIndex(Contract.ConfsEntry.COLUMN_CONF_NAME)).equals("dsa")
                && c.getString(c.getColumnIndex(Contract.ConfsEntry.COLUMN_CONF_DATETIME)).equals("321")
                && c.getString(c.getColumnIndex(Contract.ConfsEntry.COLUMN_TOPIC_ID)).equals("1")
        );
        // delete conf
        database.deleteConf("1");
        c = database.fetchConfs();
        assertTrue("Error: cannot delete conf !!", c.getCount() == 0);
        db.close();
        database.close();
        c.close();
    }

    public void testInvites () {
        Database database = new Database(
                this.mContext);
        SQLiteDatabase db = database.getWritableDatabase();
        assertEquals(true, db.isOpen());
        //insert Doctor, topic, Admin for the invite
        ContentValues values = new ContentValues();
        values.put(Contract.UserEntry.COLUMN_USER_EMAIL, "ex@ex.com");
        values.put(Contract.UserEntry.COLUMN_USER_PASS, "exexex");
        values.put(Contract.UserEntry.COLUMN_USER_TYPE, "1");//user
        long id = database.insertUser(values);
        assertFalse("Error inserting user", id == -1);

        values = new ContentValues();
        values.put(Contract.UserEntry.COLUMN_USER_EMAIL, "exADMIN@ex.com");
        values.put(Contract.UserEntry.COLUMN_USER_PASS, "exexex");
        values.put(Contract.UserEntry.COLUMN_USER_TYPE, "2");//admin
        id = database.insertUser(values);
        assertFalse("Error inserting user", id == -1);

        values = new ContentValues();
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
        // Insert invalid invite
        values = new ContentValues();
        values.put(Contract.InvitesEntry.COLUMN_DOC_ID, "123");
        values.put(Contract.InvitesEntry.COLUMN_ADMIN_ID, "1223");
        values.put(Contract.InvitesEntry.COLUMN_CONF_ID, "3421");
        values.put(Contract.InvitesEntry.COLUMN_STATUS_ID, "34121");
        try {
            id = database.insertConf(values);
            fail("Error: inserting invalid invite worked !!!");
        } catch (SQLException e) {
        }

        // Insert invalid invite
        values = new ContentValues();
        values.put(Contract.InvitesEntry.COLUMN_DOC_ID, "1");
        values.put(Contract.InvitesEntry.COLUMN_ADMIN_ID, "1223");
        values.put(Contract.InvitesEntry.COLUMN_CONF_ID, "3421");
        values.put(Contract.InvitesEntry.COLUMN_STATUS_ID, "34121");
        try {
            id = database.insertConf(values);
            fail("Error: inserting invalid invite worked !!!");
        } catch (SQLException e) {
        }
        // Insert invalid invite
        values = new ContentValues();
        values.put(Contract.InvitesEntry.COLUMN_DOC_ID, "1");
        values.put(Contract.InvitesEntry.COLUMN_ADMIN_ID, "2");
        values.put(Contract.InvitesEntry.COLUMN_CONF_ID, "3421");
        values.put(Contract.InvitesEntry.COLUMN_STATUS_ID, "34121");
        try {
            id = database.insertConf(values);
            fail("Error: inserting invalid invite worked !!!");
        } catch (SQLException e) {
        }
        // Insert invalid invite
        values = new ContentValues();
        values.put(Contract.InvitesEntry.COLUMN_DOC_ID, "1");
        values.put(Contract.InvitesEntry.COLUMN_ADMIN_ID, "2");
        values.put(Contract.InvitesEntry.COLUMN_CONF_ID, "1");
        values.put(Contract.InvitesEntry.COLUMN_STATUS_ID, "34121");
        try {
            id = database.insertConf(values);
            fail("Error: inserting invalid invite worked !!!");
        } catch (SQLException e) {
        }
        // Insert valid invite
        values = new ContentValues();
        values.put(Contract.InvitesEntry.COLUMN_DOC_ID, "1");
        values.put(Contract.InvitesEntry.COLUMN_ADMIN_ID, "2");
        values.put(Contract.InvitesEntry.COLUMN_CONF_ID, "1");
        values.put(Contract.InvitesEntry.COLUMN_STATUS_ID, Contract.InviteStatusEntry.INVITE_STATUS_PENDING_ID);
        try {
            id = database.insertInvite(values);
            assertFalse("Error: cannot insert valid conf", id == -1);
        } catch (SQLException | InvalidDoctorIDException e) {
            fail("Error: cannot insert valid conf");
        }
        //accept Invite
        assertTrue(database.acceptInvite("1"));
        //reject Invite
        assertTrue(database.rejectInvite("1"));

        //Check if updated to rejected
        values = new ContentValues();
        values.put(Contract.InvitesEntry.COLUMN_DOC_ID, "1");
        Cursor c = database.fetchInvites(values);
        c.moveToFirst();
        assertTrue("Error, the invite isn't updated as expected!!", c.getString(c.getColumnIndex(Contract.InvitesEntry.COLUMN_STATUS_ID)).equals(Contract.InviteStatusEntry.INVITE_STATUS_REJECTED_ID));
        c.close();
        db.close();
        database.close();
    }

    @Override
    protected void tearDown () throws Exception {
        super.tearDown();
        mContext.deleteDatabase(Contract.DATABASE_NAME);
    }
}
