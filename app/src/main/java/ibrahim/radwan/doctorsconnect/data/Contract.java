package ibrahim.radwan.doctorsconnect.data;

import android.net.Uri;

/**
 * Created by ibrahimradwan on 8/20/16.
 */
public class Contract {
    public static final String DATABASE_NAME = "Database.db";

    //Content provider constants
    public static final String CONTENT_AUTHORITY = "ibrahim.radwan.doctorsconnect.data";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_USERS = "users";
    public static final String PATH_INVITES = "invites";
    public static final String PATH_CONFS = "confs";
    public static final String PATH_TOPICS = "topics";

    public static final class UserEntry {
        public static final String PATH_USERS_LOGIN = "login";
        public static final String PATH_USERS_SIGNUP = "signup";

        public static final Uri CONTENT_URI_GET_USER =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_USERS).build();

        public static final Uri CONTENT_URI_LOGIN =
                CONTENT_URI_GET_USER.buildUpon().appendPath(PATH_USERS_LOGIN).build();

        public static final Uri CONTENT_URI_SIGNUP =
                CONTENT_URI_GET_USER.buildUpon().appendPath(PATH_USERS_SIGNUP).build();

        public static final String TABLE_USERS = "users";
        public static final String COLUMN_USER_ID = "_id";
        public static final String COLUMN_USER_EMAIL = "user_email";
        public static final String COLUMN_USER_PASS = "user_password";
        public static final String COLUMN_USER_TYPE = "user_type";


    }

    public static final class UserTypeEntry {
        public static final String TABLE_USER_TYPE = "user_type";

        public static final String COLUMN_TYPE_ID = "type_id";
        public static final String COLUMN_TYPE_NAME = "type_name";

        public static final String USER_TYPE_USER = "user";
        public static final String USER_TYPE_ADMIN = "admin";

        public static final String USER_TYPE_USER_ID = "1";
        public static final String USER_TYPE_ADMIN_ID = "2";
    }

    public static final class InvitesEntry {
        public static final String PATH_GET_INVITES = "get";
        public static final String PATH_ADD_INVITE = "add";
        public static final String PATH_ACCEPT_INVITE = "accept";
        public static final String PATH_REJECT_INVITE = "reject";

        public static final Uri CONTENT_URI_GET_INVITES =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_INVITES).appendPath(PATH_GET_INVITES).build();
        public static final Uri CONTENT_URI_ADD_INVITE =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_INVITES).appendPath(PATH_ADD_INVITE).build();
        public static final Uri CONTENT_URI_ACCEPT_INVITE =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_INVITES).appendPath(PATH_ACCEPT_INVITE).build();
        public static final Uri CONTENT_URI_REJECT_INVITE =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_INVITES).appendPath(PATH_REJECT_INVITE).build();


        public static final String TABLE_INVITES = "invites";

        public static final String COLUMN_INVITE_ID = "invite_id";
        public static final String COLUMN_CONF_ID = "conf_id";
        public static final String COLUMN_ADMIN_ID = "admin_id";
        public static final String COLUMN_DOC_ID = "doc_id";
        public static final String COLUMN_STATUS_ID = "status_id";
    }

    public static final class InviteStatusEntry {
        public static final String TABLE_INVITE_STATUS = "invite_status";
        public static final String COLUMN_STATUS_ID = "status_id";
        public static final String COLUMN_STATUS_NAME = "status_name";


        public static final String INVITE_STATUS_ACCEPTED = "accepted";
        public static final String INVITE_STATUS_REJECTED = "rejected";
        public static final String INVITE_STATUS_PENDING = "pending";

        public static final String INVITE_STATUS_ACCEPTED_ID = "1";
        public static final String INVITE_STATUS_REJECTED_ID = "2";
        public static final String INVITE_STATUS_PENDING_ID = "3";
    }

    public static final class ConfsEntry {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_CONFS).build();

        public static final String TABLE_CONFS = "confs";
        public static final String COLUMN_CONF_ID = "conf_id";
        public static final String COLUMN_CONF_NAME = "conf_name";
        public static final String COLUMN_CONF_DATETIME = "conf_datetime";
        public static final String COLUMN_TOPIC_ID = "topic_id";

    }

    public static final class TopicEntry {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TOPICS).build();

        public static final String TABLE_TOPICS = "topics";
        public static final String COLUMN_TOPIC_ID = "topic_id";
        public static final String COLUMN_DOC_ID = "doc_id";
        public static final String COLUMN_TOPIC_TITLE = "topic_title";
    }
}
