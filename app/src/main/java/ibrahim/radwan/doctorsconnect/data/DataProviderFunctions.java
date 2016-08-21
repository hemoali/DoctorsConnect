package ibrahim.radwan.doctorsconnect.data;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.CursorLoader;

import ibrahim.radwan.doctorsconnect.Models.User;

/**
 * Created by ibrahimradwan on 8/21/16.
 */
public class DataProviderFunctions {
    public static DataProviderFunctions dataProviderFunctions = null;

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
}
