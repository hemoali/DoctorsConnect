package ibrahim.radwan.doctorsconnect.data;

/**
 * Created by ibrahimradwan on 8/20/16.
 */
public class Contract {
    public static final String DATABASE_NAME = "Database.db";

    public static final class UserEntry {
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
        public static final String TABLE_CONFS = "confs";
        public static final String COLUMN_CONF_ID = "conf_id";
        public static final String COLUMN_CONF_NAME = "conf_name";
        public static final String COLUMN_CONF_DATETIME = "conf_datetime";
        public static final String COLUMN_TOPIC_ID = "topic_id";

    }

    public static final class TopicEntry {
        public static final String TABLE_TOPICS = "topics";
        public static final String COLUMN_TOPIC_ID = "topic_id";
        public static final String COLUMN_DOC_ID = "doc_id";
        public static final String COLUMN_TOPIC_TITLE = "topic_title";
    }
}
