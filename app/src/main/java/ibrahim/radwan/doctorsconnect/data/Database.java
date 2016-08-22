package ibrahim.radwan.doctorsconnect.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;

import ibrahim.radwan.doctorsconnect.Utils.BCrypt;
import ibrahim.radwan.doctorsconnect.Utils.InvalidDoctorIDException;

/**
 * Created by ibrahimradwan on 8/20/16.
 */
public class Database extends SQLiteOpenHelper {

    private static final String ENABLE_FOREIGN_KEY = "PRAGMA foreign_keys = ON;";
    private static final String SQL_CREATE_TABLE_USER_TYPE =
            "CREATE TABLE `" + Contract.UserTypeEntry.TABLE_USER_TYPE + "` (\n" +
                    "\t`" + Contract.UserTypeEntry.COLUMN_TYPE_ID + "`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                    "\t`" + Contract.UserTypeEntry.COLUMN_TYPE_NAME + "`\tTEXT NOT NULL\n" +
                    ");\n";

    private static final String SQL_CREATE_TABLE_USERS =
            "CREATE TABLE `" + Contract.UserEntry.TABLE_USERS + "` (\n" +
                    "\t`" + Contract.UserEntry.COLUMN_USER_ID + "`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
                    "\t`" + Contract.UserEntry.COLUMN_USER_EMAIL + "`\tTEXT NOT NULL UNIQUE,\n" +
                    "\t`" + Contract.UserEntry.COLUMN_USER_PASS + "`\tTEXT NOT NULL,\n" +
                    "\t`" + Contract.UserEntry.COLUMN_USER_TYPE + "`\tINTEGER,\n" +
                    "\tFOREIGN KEY(`" + Contract.UserEntry.COLUMN_USER_TYPE + "`) REFERENCES " + Contract.UserTypeEntry.TABLE_USER_TYPE + " (`" + Contract.UserTypeEntry.COLUMN_TYPE_ID + "`)\n" +
                    ");\n";

    private static final String SQL_INSERT_USER_TYPE_USER =
            "INSERT INTO `" + Contract.UserTypeEntry.TABLE_USER_TYPE + "` VALUES (" + Contract.UserTypeEntry.USER_TYPE_USER_ID + ",'" + Contract.UserTypeEntry.USER_TYPE_USER + "');\n";
    private static final String SQL_INSERT_USER_TYPE_ADMIN =
            "INSERT INTO `" + Contract.UserTypeEntry.TABLE_USER_TYPE + "` VALUES (" + Contract.UserTypeEntry.USER_TYPE_ADMIN_ID + ",'" + Contract.UserTypeEntry.USER_TYPE_ADMIN + "');\n";

    private static final String SQL_CREATE_TABLE_TOPICS =
            "CREATE TABLE `" + Contract.TopicEntry.TABLE_TOPICS + "` (\n" +
                    "\t`" + Contract.TopicEntry.COLUMN_TOPIC_ID + "`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "\t`" + Contract.TopicEntry.COLUMN_DOC_ID + "`\tINTEGER,\n" +
                    "\t`" + Contract.TopicEntry.COLUMN_TOPIC_TITLE + "`\tTEXT NOT NULL,\n" +
                    "\tFOREIGN KEY(`" + Contract.TopicEntry.COLUMN_DOC_ID + "`) REFERENCES " + Contract.UserEntry.TABLE_USERS + " ( `" + Contract.UserEntry.COLUMN_USER_ID + "` )\n" +
                    ");\n";
    private static final String SQL_CREATE_TABLE_INVITES =
            "CREATE TABLE  `" + Contract.InvitesEntry.TABLE_INVITES + "` (\n" +
                    "\t`" + Contract.InvitesEntry.COLUMN_INVITE_ID + "`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "\t`" + Contract.InvitesEntry.COLUMN_CONF_ID + "`\tINTEGER,\n" +
                    "\t`" + Contract.InvitesEntry.COLUMN_ADMIN_ID + "`\tINTEGER,\n" +
                    "\t`" + Contract.InvitesEntry.COLUMN_DOC_ID + "`\tINTEGER,\n" +
                    "\t`" + Contract.InvitesEntry.COLUMN_STATUS_ID + "`\tINTEGER,\n" +
                    "\tFOREIGN KEY(`" + Contract.InvitesEntry.COLUMN_CONF_ID + "`) REFERENCES " + Contract.ConfsEntry.TABLE_CONFS + " (`" + Contract.ConfsEntry.COLUMN_CONF_ID + "`),\n" +
                    "\tFOREIGN KEY(`" + Contract.InvitesEntry.COLUMN_ADMIN_ID + "`) REFERENCES " + Contract.UserEntry.TABLE_USERS + " (`" + Contract.UserEntry.COLUMN_USER_ID + "`),\n" +
                    "\tFOREIGN KEY(`" + Contract.InvitesEntry.COLUMN_DOC_ID + "`) REFERENCES " + Contract.UserEntry.TABLE_USERS + " (`" + Contract.UserEntry.COLUMN_USER_ID + "`),\n" +
                    "\tFOREIGN KEY(`" + Contract.InvitesEntry.COLUMN_STATUS_ID + "`) REFERENCES " + Contract.InviteStatusEntry.TABLE_INVITE_STATUS + " (`" + Contract.InviteStatusEntry.COLUMN_STATUS_ID + "`)\n" +
                    ");\n";

    private static final String SQL_CREATE_TABLE_INVITE_STATUS =
            "CREATE TABLE `" + Contract.InviteStatusEntry.TABLE_INVITE_STATUS + "` (\n" +
                    "\t`" + Contract.InviteStatusEntry.COLUMN_STATUS_ID + "`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "\t`" + Contract.InviteStatusEntry.COLUMN_STATUS_NAME + "`\tTEXT NOT NULL\n" +
                    ");\n";
    private static final String SQL_INSERT_INTO_INVITE_STATUS_ACCEPTED =
            "INSERT INTO `" + Contract.InviteStatusEntry.TABLE_INVITE_STATUS + "`  VALUES (" + Contract.InviteStatusEntry.INVITE_STATUS_ACCEPTED_ID + ",'" + Contract.InviteStatusEntry.INVITE_STATUS_ACCEPTED + "');\n";
    private static final String SQL_INSERT_INTO_INVITE_STATUS_REJECTED =
            "INSERT INTO `" + Contract.InviteStatusEntry.TABLE_INVITE_STATUS + "`  VALUES (" + Contract.InviteStatusEntry.INVITE_STATUS_REJECTED_ID + ",'" + Contract.InviteStatusEntry.INVITE_STATUS_REJECTED + "');\n";
    private static final String SQL_INSERT_INTO_INVITE_STATUS_PENDING =
            "INSERT INTO `" + Contract.InviteStatusEntry.TABLE_INVITE_STATUS + "`  VALUES (" + Contract.InviteStatusEntry.INVITE_STATUS_PENDING_ID + ",'" + Contract.InviteStatusEntry.INVITE_STATUS_PENDING + "');\n";

    private static final String SQL_CREATE_TABLE_CONFS =
            "CREATE TABLE `" + Contract.ConfsEntry.TABLE_CONFS + "`  (\n" +
                    "\t`" + Contract.ConfsEntry.COLUMN_CONF_ID + "`\tINTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                    "\t`" + Contract.ConfsEntry.COLUMN_CONF_NAME + "`\tTEXT NOT NULL,\n" +
                    "\t`" + Contract.ConfsEntry.COLUMN_CONF_DATETIME + "`\tTEXT NOT NULL,\n" +
                    "\t`" + Contract.ConfsEntry.COLUMN_TOPIC_ID + "`\tINTEGER NOT NULL,\n" +
                    "\tFOREIGN KEY(`" + Contract.ConfsEntry.COLUMN_TOPIC_ID + "`) REFERENCES " + Contract.TopicEntry.TABLE_TOPICS + " (`" + Contract.TopicEntry.COLUMN_TOPIC_ID + "`)\n" +
                    ");\n";

    private static final String SQL_DROP = "DROP TABLE IF EXISTS `" + Contract.UserEntry.TABLE_USERS + "`; " + "DROP TABLE IF EXISTS `" + Contract.UserTypeEntry.TABLE_USER_TYPE + "`; " + "DROP TABLE IF EXISTS `" + Contract.InvitesEntry.TABLE_INVITES + "`; " + "DROP TABLE IF EXISTS `" + Contract.ConfsEntry.TABLE_CONFS + "`; " + "DROP TABLE IF EXISTS `" + Contract.TopicEntry.TABLE_TOPICS + "`; ";

    public Database (Context context) {
        super(context, Contract.DATABASE_NAME, null, 1);
    }

    @Override
    public void onConfigure (SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);

    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        db.execSQL(ENABLE_FOREIGN_KEY);

        db.execSQL(SQL_CREATE_TABLE_USER_TYPE);
        db.execSQL(SQL_CREATE_TABLE_USERS);
        db.execSQL(SQL_CREATE_TABLE_INVITE_STATUS);
        db.execSQL(SQL_CREATE_TABLE_INVITES);
        db.execSQL(SQL_CREATE_TABLE_TOPICS);
        db.execSQL(SQL_CREATE_TABLE_CONFS);

        db.execSQL(SQL_INSERT_INTO_INVITE_STATUS_ACCEPTED);
        db.execSQL(SQL_INSERT_INTO_INVITE_STATUS_REJECTED);
        db.execSQL(SQL_INSERT_INTO_INVITE_STATUS_PENDING);
        db.execSQL(SQL_INSERT_USER_TYPE_ADMIN);
        db.execSQL(SQL_INSERT_USER_TYPE_USER);
    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DROP);
        onCreate(db);
    }

    /**
     * @param values : email, pass, user_type
     * @return user_id
     */
    public long insertUser (ContentValues values) throws SQLException {
        values.put(Contract.UserEntry.COLUMN_USER_PASS, BCrypt.hashpw(values.getAsString(Contract.UserEntry.COLUMN_USER_PASS), BCrypt.gensalt()));
        long user_id = getWritableDatabase().insert(Contract.UserEntry.TABLE_USERS, "", values);
        if (user_id <= 0) {
            throw new SQLException("Failed to add new user");
        }
        return user_id;
    }

    /**
     * Checks if user login completed and store the data into sharedpreferences
     *
     * @param values : email, pass
     * @return user_id (-1: false data)
     */
    public Cursor userLogin (ContentValues values) {
        SQLiteQueryBuilder sqliteQueryBuilder = new SQLiteQueryBuilder();
        sqliteQueryBuilder.setTables(Contract.UserEntry.TABLE_USERS);
        Cursor cursor = sqliteQueryBuilder.query(getReadableDatabase(),
                new String[]{Contract.UserEntry.COLUMN_USER_ID, Contract.UserEntry.COLUMN_USER_PASS, Contract.UserEntry.COLUMN_USER_TYPE},
                Contract.UserEntry.COLUMN_USER_EMAIL + " = ?",
                new String[]{values.getAsString(Contract.UserEntry.COLUMN_USER_EMAIL)},
                null,
                null,
                "");
        if (cursor.moveToFirst()) {
            if (BCrypt.checkpw(values.getAsString(Contract.UserEntry.COLUMN_USER_PASS), cursor.getString(cursor.getColumnIndex(Contract.UserEntry.COLUMN_USER_PASS)))) {
                return cursor;
            }
        }
        return null;
    }

    /**
     * @return All doctors
     */
    public Cursor fetchDoctors () {
        SQLiteQueryBuilder sqliteQueryBuilder = new SQLiteQueryBuilder();
        sqliteQueryBuilder.setTables(Contract.UserEntry.TABLE_USERS);
        Cursor cursor = sqliteQueryBuilder.query(getReadableDatabase(),
                null,
                Contract.UserEntry.COLUMN_USER_TYPE + " = ?",
                new String[]{Contract.UserTypeEntry.USER_TYPE_USER_ID},
                null,
                null,
                "");
        return cursor;
    }

    /**
     * @return true if email exists
     */
    public Cursor checkEmail (String email) {
        SQLiteQueryBuilder sqliteQueryBuilder = new SQLiteQueryBuilder();
        sqliteQueryBuilder.setTables(Contract.UserEntry.TABLE_USERS);
        Cursor cursor = sqliteQueryBuilder.query(getReadableDatabase(),
                null,
                Contract.UserEntry.COLUMN_USER_EMAIL + " = ?",
                new String[]{email},
                null,
                null,
                "");
        if (cursor.moveToFirst() && cursor.getCount() > 0)
            return cursor;
        return null;
    }

    /**
     * Checks if the user doc ot admin
     *
     * @param id: user id
     * @return USER_TYPE_USER_ID if doctor, USER_TYPE_ADMIN_ID if admin
     */
    public String checkUserType (String id) {
        SQLiteQueryBuilder sqliteQueryBuilder = new SQLiteQueryBuilder();
        sqliteQueryBuilder.setTables(Contract.UserEntry.TABLE_USERS);
        Cursor cursor = sqliteQueryBuilder.query(getReadableDatabase(),
                null,
                Contract.UserEntry.COLUMN_USER_ID + " = ?",
                new String[]{id},
                null,
                null,
                "");
        if (cursor.moveToFirst()) {
            String userType = cursor.getString(cursor.getColumnIndex(Contract.UserEntry.COLUMN_USER_TYPE));
            if (userType.equals(Contract.UserTypeEntry.USER_TYPE_USER_ID)) {
                return Contract.UserTypeEntry.USER_TYPE_USER_ID;
            } else {
                return Contract.UserTypeEntry.USER_TYPE_ADMIN_ID;
            }
        }
        return null;
    }

    /**
     * returns user by id
     *
     * @param userID
     * @return
     */
    public Cursor getUserByID (String userID) {
        SQLiteQueryBuilder sqliteQueryBuilder = new SQLiteQueryBuilder();
        sqliteQueryBuilder.setTables(Contract.UserEntry.TABLE_USERS);
        Cursor cursor = sqliteQueryBuilder.query(getReadableDatabase(),
                null,
                Contract.UserEntry.COLUMN_USER_ID + " = ?",
                new String[]{userID},
                null,
                null,
                "");
        cursor.moveToFirst();
        return cursor;
    }

    /**
     * @param values (doctor_id, title)
     * @return topic_id
     * @throws Throwable
     */
    public long insertTopic (ContentValues values) throws SQLException, InvalidDoctorIDException {
        if (checkUserType(values.getAsString(Contract.TopicEntry.COLUMN_DOC_ID)) != Contract.UserTypeEntry.USER_TYPE_USER_ID) {
            throw new InvalidDoctorIDException("The ID doesn't belong to doctor");
        }
        long topic_id = getWritableDatabase().insert(Contract.TopicEntry.TABLE_TOPICS, "", values);
        if (topic_id <= 0) {
            throw new SQLException("Failed to add new topic");
        }
        return topic_id;
    }

    /**
     * @return All topics
     */
    public Cursor fetchTopics () {
        SQLiteQueryBuilder sqliteQueryBuilder = new SQLiteQueryBuilder();
        sqliteQueryBuilder.setTables(Contract.TopicEntry.TABLE_TOPICS);
        Cursor cursor = sqliteQueryBuilder.query(getReadableDatabase(),
                null,
                null,
                null,
                null,
                null,
                Contract.TopicEntry.COLUMN_TOPIC_ID + " DESC");
        return cursor;
    }

    /**
     * @return All topics
     */
    public Cursor getTopicByID (String id) {
        SQLiteQueryBuilder sqliteQueryBuilder = new SQLiteQueryBuilder();
        sqliteQueryBuilder.setTables(Contract.TopicEntry.TABLE_TOPICS);
        Cursor cursor = sqliteQueryBuilder.query(getReadableDatabase(),
                null,
                Contract.TopicEntry.COLUMN_TOPIC_ID + " = ?",
                new String[]{id},
                null,
                null,
                "");
        return cursor;
    }

    /**
     * @param values (conf name, time, topic_id)
     * @return conf id
     * @throws SQLException
     */
    public long insertConf (ContentValues values) throws SQLException {
        long conf_id = getWritableDatabase().insert(Contract.ConfsEntry.TABLE_CONFS, "", values);
        if (conf_id <= 0) {
            throw new SQLException("Failed to add new conf");
        }
        return conf_id;
    }

    /**
     * @return All confs
     */
    public Cursor fetchConfs () {
        SQLiteQueryBuilder sqliteQueryBuilder = new SQLiteQueryBuilder();
        sqliteQueryBuilder.setTables(Contract.ConfsEntry.TABLE_CONFS);
        Cursor cursor = sqliteQueryBuilder.query(getReadableDatabase(),
                null,
                null,
                null,
                null,
                null,
                Contract.ConfsEntry.COLUMN_CONF_ID + " DESC");
        return cursor;
    }

    /**
     * @return confs
     */
    public Cursor fetchConfByID (String conf_id) {
        SQLiteQueryBuilder sqliteQueryBuilder = new SQLiteQueryBuilder();
        sqliteQueryBuilder.setTables(Contract.ConfsEntry.TABLE_CONFS);
        Cursor cursor = sqliteQueryBuilder.query(getReadableDatabase(),
                null,
                Contract.ConfsEntry.COLUMN_CONF_ID + " = ?",
                new String[]{conf_id},
                null,
                null,
                "");
        return cursor;
    }

    /**
     * updates conf values
     *
     * @param id
     * @param values (datetime, topic_id, name)
     * @return true if updated
     */
    public boolean updateConf (String id, ContentValues values) {
        if (id != null) {
            return (getWritableDatabase().update(Contract.ConfsEntry.TABLE_CONFS, values, Contract.ConfsEntry.COLUMN_CONF_ID + "=?", new String[]{id}) == 1);
        }
        return false;
    }

    /**
     * Deletes conf by id
     *
     * @param id : conf id
     * @return true if deleted
     */
    public boolean deleteConf (String id) {
        boolean deleted = false;
        if (id != null) {
            //First delete invites
            deleteInvitesByConfID(id);
            try {
                deleted = (getWritableDatabase().delete(Contract.ConfsEntry.TABLE_CONFS, Contract.ConfsEntry.COLUMN_CONF_ID + "=?", new String[]{id}) == 1);
            } catch (SQLiteConstraintException e) {
            }
        }
        return deleted;
    }

    /**
     * @param values (conf_id, Admin_id, Doc_id, status_id)
     * @return invite id
     * @throws SQLException
     */
    public long insertInvite (ContentValues values) throws SQLException, InvalidDoctorIDException {
        if (checkUserType(values.getAsString(Contract.TopicEntry.COLUMN_DOC_ID)) != Contract.UserTypeEntry.USER_TYPE_USER_ID) {
            throw new InvalidDoctorIDException("The ID doesn't belong to doctor");
        }
        long invite_id = getWritableDatabase().insert(Contract.InvitesEntry.TABLE_INVITES, "", values);
        if (invite_id <= 0) {
            throw new SQLException("Failed to add new invite");
        }
        return invite_id;
    }

    /**
     * Accepts the invite
     *
     * @param id : invite id
     * @return
     */
    public boolean acceptInvite (String id) {
        if (id != null) {
            ContentValues values = new ContentValues();
            values.put(Contract.InvitesEntry.COLUMN_STATUS_ID, Contract.InviteStatusEntry.INVITE_STATUS_ACCEPTED_ID);
            return (getWritableDatabase().update(Contract.InvitesEntry.TABLE_INVITES, values, Contract.InvitesEntry.COLUMN_INVITE_ID + "=?", new String[]{id}) == 1);
        }
        return false;
    }

    /**
     * Rejects the invite
     *
     * @param id : invite id
     * @return
     */
    public boolean rejectInvite (String id) {
        if (id != null) {
            ContentValues values = new ContentValues();
            values.put(Contract.InvitesEntry.COLUMN_STATUS_ID, Contract.InviteStatusEntry.INVITE_STATUS_REJECTED_ID);
            return (getWritableDatabase().update(Contract.InvitesEntry.TABLE_INVITES, values, Contract.InvitesEntry.COLUMN_INVITE_ID + "=?", new String[]{id}) == 1);
        }
        return false;
    }

    /**
     * @return All invites
     */
    public Cursor fetchInvites (ContentValues values) {
        SQLiteQueryBuilder sqliteQueryBuilder = new SQLiteQueryBuilder();
        sqliteQueryBuilder.setTables(Contract.InvitesEntry.TABLE_INVITES);
        Cursor cursor = sqliteQueryBuilder.query(getReadableDatabase(),
                null,
                Contract.InvitesEntry.COLUMN_DOC_ID + " = ? AND " + Contract.InvitesEntry.COLUMN_STATUS_ID + " != ?",
                new String[]{values.getAsString(Contract.InvitesEntry.COLUMN_DOC_ID), Contract.InviteStatusEntry.INVITE_STATUS_REJECTED_ID},
                null,
                null,
                Contract.InvitesEntry.COLUMN_INVITE_ID + " DESC");
        return cursor;
    }

    /**
     * Deletes all invites that are connected to conference (in order to delete this conference)
     *
     * @param conf_id: conference id
     * @return true if deleted
     */
    public boolean deleteInvitesByConfID (String conf_id) {
        boolean deleted = false;
        if (conf_id != null) {
            try {
                deleted = (getWritableDatabase().delete(Contract.InvitesEntry.TABLE_INVITES, Contract.InvitesEntry.COLUMN_CONF_ID + "=?", new String[]{conf_id}) > 0);
            } catch (SQLiteConstraintException e) {
            }
        }
        return deleted;
    }
}
