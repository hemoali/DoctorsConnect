package ibrahim.radwan.doctorsconnect.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by ibrahimradwan on 8/20/16.
 */
public class DataProvider extends ContentProvider {
    private static final UriMatcher uriMatcher = getUriMatcher();

    static final int ADD_USER = 100;
    static final int LOGIN_USER = 101;
    static final int GET_USERS = 102;
    static final int CHECK_EMAIL = 103;
    static final int GET_USER_BY_ID = 104;

    static final int ADD_INVITE = 200;
    static final int GET_INVITES = 201;
    static final int ACCEPT_INVITE = 202;
    static final int REJECT_INVITE = 203;

    static final int ADD_CONF = 300;
    static final int GET_CONFS = 301;
    static final int UPDATE_CONF = 302;
    static final int DELETE_CONF = 303;
    static final int GET_CONF_BY_ID = 304;

    static final int ADD_TOPIC = 400;
    static final int GET_TOPICS = 401;
    static final int GET_TOPIC_BY_ID = 402;

    private Database database;

    private static UriMatcher getUriMatcher () {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = Contract.CONTENT_AUTHORITY;

        matcher.addURI(authority, Contract.PATH_USERS + "/" + Contract.UserEntry.PATH_USERS_SIGNUP, ADD_USER);
        matcher.addURI(authority, Contract.PATH_USERS + "/" + Contract.UserEntry.PATH_USERS_LOGIN, LOGIN_USER);
        matcher.addURI(authority, Contract.PATH_USERS, GET_USERS);
        matcher.addURI(authority, Contract.PATH_USERS + "/#", GET_USER_BY_ID);
        matcher.addURI(authority, Contract.PATH_USERS + "/" + Contract.UserEntry.PATH_USERS_EMAIL_CHECK, CHECK_EMAIL);

        matcher.addURI(authority, Contract.PATH_INVITES + "/" + Contract.InvitesEntry.PATH_GET_INVITES + "/#", GET_INVITES);
        matcher.addURI(authority, Contract.PATH_INVITES + "/" + Contract.InvitesEntry.PATH_ACCEPT_INVITE, ACCEPT_INVITE);
        matcher.addURI(authority, Contract.PATH_INVITES + "/" + Contract.InvitesEntry.PATH_REJECT_INVITE, REJECT_INVITE);
        matcher.addURI(authority, Contract.PATH_INVITES + "/" + Contract.InvitesEntry.PATH_ADD_INVITE, ADD_INVITE);

        matcher.addURI(authority, Contract.PATH_CONFS + "/" + Contract.ConfsEntry.PATH_ADD_CONF, ADD_CONF);
        matcher.addURI(authority, Contract.PATH_CONFS + "/" + Contract.ConfsEntry.PATH_GET_CONFS, GET_CONFS);
        matcher.addURI(authority, Contract.PATH_CONFS + "/" + Contract.ConfsEntry.PATH_UPDATE_CONF, UPDATE_CONF);
        matcher.addURI(authority, Contract.PATH_CONFS + "/" + Contract.ConfsEntry.PATH_DELETE_CONF + "/#", DELETE_CONF);
        matcher.addURI(authority, Contract.PATH_CONFS + "/#", GET_CONF_BY_ID);

        matcher.addURI(authority, Contract.PATH_TOPICS + "/" + Contract.TopicEntry.PATH_ADD_TOPIC, ADD_TOPIC);
        matcher.addURI(authority, Contract.PATH_TOPICS + "/" + Contract.TopicEntry.PATH_GET_TOPICS, GET_TOPICS);
        matcher.addURI(authority, Contract.PATH_TOPICS + "/#", GET_TOPIC_BY_ID);

        return matcher;
    }

    @Override
    public boolean onCreate () {
        Context context = getContext();
        database = new Database(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query (Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.e("TAG", "" + uriMatcher.match(uri));
        if (uriMatcher.match(uri) == GET_USERS) {
            return database.fetchDoctors();
        } else if (uriMatcher.match(uri) == LOGIN_USER) {
            ContentValues values = new ContentValues();
            values.put(Contract.UserEntry.COLUMN_USER_EMAIL, selectionArgs[0]);
            values.put(Contract.UserEntry.COLUMN_USER_PASS, selectionArgs[1]);
            return database.userLogin(values);
        } else if (uriMatcher.match(uri) == CHECK_EMAIL) {
            return database.checkEmail(selectionArgs[0]);
        } else if (uriMatcher.match(uri) == GET_USER_BY_ID) {
            return database.getUserByID(uri.getPathSegments().get(1));
        } else if (uriMatcher.match(uri) == GET_INVITES) {
            ContentValues values = new ContentValues();
            values.put(Contract.InvitesEntry.COLUMN_DOC_ID, uri.getPathSegments().get(2));
            return database.fetchInvites(values);
        } else if (uriMatcher.match(uri) == GET_CONFS) {
            return database.fetchConfs();
        } else if (uriMatcher.match(uri) == GET_TOPICS) {
            return database.fetchTopics();
        } else if (uriMatcher.match(uri) == GET_TOPIC_BY_ID) {
            return database.getTopicByID(uri.getPathSegments().get(1));
        } else if (uriMatcher.match(uri) == GET_CONF_BY_ID) {
            return database.fetchConfByID(uri.getPathSegments().get(1));
        }
        return null;
    }

    @Nullable
    @Override
    public String getType (Uri uri) { // ToDo:implement

        return null;
    }

    @Nullable
    @Override
    public Uri insert (Uri uri, ContentValues contentValues) {
        Uri returnUri = null;
        try {
            if (uriMatcher.match(uri) == ADD_USER) {
                long id = database.insertUser(contentValues);
                returnUri = ContentUris.withAppendedId(Contract.UserEntry.CONTENT_URI_SIGNUP, id);
            } else if (uriMatcher.match(uri) == ADD_INVITE) {
                long id = database.insertInvite(contentValues);
                returnUri = ContentUris.withAppendedId(Contract.InvitesEntry.CONTENT_URI_ADD_INVITE, id);
            } else if (uriMatcher.match(uri) == ADD_CONF) {
                long id = database.insertConf(contentValues);
                returnUri = ContentUris.withAppendedId(Contract.ConfsEntry.CONTENT_URI_ADD_CONF, id);
            } else if (uriMatcher.match(uri) == ADD_TOPIC) {
                long id = database.insertTopic(contentValues);
                returnUri = ContentUris.withAppendedId(Contract.TopicEntry.CONTENT_URI_ADD_TOPIC, id);
            }
        } catch (Exception e) {
        }
        return returnUri;
    }

    @Override
    public int delete (Uri uri, String s, String[] strings) {
        if (uriMatcher.match(uri) == DELETE_CONF) {
            if (database.deleteConf(uri.getPathSegments().get(2))) return 1;
        }
        return 0;
    }

    @Override
    public int update (Uri uri, ContentValues contentValues, String s, String[] strings) {
        if (uriMatcher.match(uri) == ACCEPT_INVITE) {
            if (database.acceptInvite(strings[0]))
                return 1;
        } else if (uriMatcher.match(uri) == REJECT_INVITE) {
            if (database.rejectInvite(strings[0]))
                return 1;
        } else if (uriMatcher.match(uri) == UPDATE_CONF) {
            if (database.updateConf(strings[0], contentValues))
                return 1;
        }
        return 0;
    }
}
