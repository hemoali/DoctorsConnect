package ibrahim.radwan.doctorsconnect.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import ibrahim.radwan.doctorsconnect.models.User;
import ibrahim.radwan.doctorsconnect.data.Contract;

/**
 * Created by ibrahimradwan on 8/20/16.
 */
public class Utils {
    private final static String MyPREFERENCES = "prefs";

    public final static boolean isValidEmail (final CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public final static void saveUserDataToSharedPreferences (User u, Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Contract.UserEntry.COLUMN_USER_ID, u.getUserID());
        editor.putString(Contract.UserEntry.COLUMN_USER_EMAIL, u.getUserEmail());
        editor.putString(Contract.UserEntry.COLUMN_USER_TYPE, u.getTypeID());
        editor.commit();
    }

    public final static User getUserDataFromSharedPreferences (Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        User u = null;
        if (sharedpreferences.contains(Contract.UserEntry.COLUMN_USER_EMAIL)) {
            u = new User(sharedpreferences.getString(Contract.UserEntry.COLUMN_USER_ID, ""), sharedpreferences.getString(Contract.UserEntry.COLUMN_USER_EMAIL, ""), null, sharedpreferences.getString(Contract.UserEntry.COLUMN_USER_TYPE, ""));
        }
        return u;
    }

    public final static void signOutUser (Context context) {
        SharedPreferences sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.remove(Contract.UserEntry.COLUMN_USER_ID);
        editor.remove(Contract.UserEntry.COLUMN_USER_EMAIL);
        editor.remove(Contract.UserEntry.COLUMN_USER_PASS);
        editor.commit();
    }

    public static boolean isTablet (Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }
}
