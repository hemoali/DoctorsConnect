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

    static final int ADD_INVITE = 200;
    static final int GET_INVITES = 201;
    static final int ACCEPT_INVITE = 202;
    static final int REJECT_INVITE = 203;

    static final int ADD_CONF = 300;
    static final int GET_CONFS = 301;
    static final int UPDATE_CONF = 302;
    static final int DELETE_CONF = 303;

    static final int ADD_TOPIC = 400;
    static final int GET_TOPICS = 401;

    private Database database;

    private static UriMatcher getUriMatcher () {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = Contract.CONTENT_AUTHORITY;

        matcher.addURI(authority, Contract.PATH_USERS + "/" + Contract.UserEntry.PATH_USERS_SIGNUP, ADD_USER);
        matcher.addURI(authority, Contract.PATH_USERS + "/" + Contract.UserEntry.PATH_USERS_LOGIN, LOGIN_USER);
        matcher.addURI(authority, Contract.PATH_USERS, GET_USERS);

        matcher.addURI(authority, Contract.PATH_INVITES + "/get/#", GET_INVITES);
        matcher.addURI(authority, Contract.PATH_INVITES + "/accept", ACCEPT_INVITE);
        matcher.addURI(authority, Contract.PATH_INVITES + "/reject", REJECT_INVITE);
        matcher.addURI(authority, Contract.PATH_INVITES + "/" + Contract.InvitesEntry.PATH_ADD_INVITE, ADD_INVITE);

        matcher.addURI(authority, Contract.PATH_CONFS + "add/#/*/*", ADD_CONF);
        matcher.addURI(authority, Contract.PATH_CONFS, GET_CONFS);
        matcher.addURI(authority, Contract.PATH_CONFS + "update/#/*/*", UPDATE_CONF);
        matcher.addURI(authority, Contract.PATH_CONFS + "/#", DELETE_CONF);

        matcher.addURI(authority, Contract.PATH_TOPICS + "/#/*", ADD_TOPIC);
        matcher.addURI(authority, Contract.PATH_TOPICS, GET_TOPICS);

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
        Log.v("TAG", uriMatcher.match(uri) + "");
        if (uriMatcher.match(uri) == GET_USERS) {
            return database.fetchDoctors();
        } else if (uriMatcher.match(uri) == LOGIN_USER) {
            ContentValues values = new ContentValues();
            values.put(Contract.UserEntry.COLUMN_USER_EMAIL, selectionArgs[0]);
            values.put(Contract.UserEntry.COLUMN_USER_PASS, selectionArgs[1]);
            return database.userLogin(values);
        } else if (uriMatcher.match(uri) == GET_INVITES) {
            ContentValues values = new ContentValues();
            values.put(Contract.InvitesEntry.COLUMN_DOC_ID, uri.getPathSegments().get(2));
            return database.fetchInvites(values);
        } else if (uriMatcher.match(uri) == GET_CONFS) {
            return database.fetchConfs();
        } else if (uriMatcher.match(uri) == GET_TOPICS) {
            return database.fetchTopics();
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
        if (uriMatcher.match(uri) == ADD_USER) {
            try {
                long id = database.insertUser(contentValues);
                Uri returnUri = ContentUris.withAppendedId(Contract.UserEntry.CONTENT_URI_SIGNUP, id);
                return returnUri;
            } catch (Exception e) {
                return null;
            }
        } else if (uriMatcher.match(uri) == ADD_INVITE) {
            try {
                long id = database.insertInvite(contentValues);
                Uri returnUri = ContentUris.withAppendedId(Contract.InvitesEntry.CONTENT_URI_ADD_INVITE, id);
                return returnUri;
            } catch (Exception e) {
                return null;
            }
        } else if (uriMatcher.match(uri) == ADD_CONF) {
        } else if (uriMatcher.match(uri) == ADD_TOPIC) {
        }
        return null;
    }

    @Override
    public int delete (Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update (Uri uri, ContentValues contentValues, String s, String[] strings) {
        if (uriMatcher.match(uri) == ACCEPT_INVITE) {
            boolean update = database.acceptInvite(strings[0]);
            if (update) return 1;
        } else if (uriMatcher.match(uri) == REJECT_INVITE) {
            boolean update = database.rejectInvite(strings[0]);
            if (update) return 1;
        }
        return 0;
    }
}
