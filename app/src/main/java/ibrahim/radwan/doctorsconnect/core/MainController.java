package ibrahim.radwan.doctorsconnect.core;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.widget.ArrayAdapter;

import java.util.List;

import ibrahim.radwan.doctorsconnect.data.Contract;
import ibrahim.radwan.doctorsconnect.data.DataProviderFunctions;
import ibrahim.radwan.doctorsconnect.models.Conference;
import ibrahim.radwan.doctorsconnect.models.Invite;
import ibrahim.radwan.doctorsconnect.models.Topic;
import ibrahim.radwan.doctorsconnect.models.User;
import ibrahim.radwan.doctorsconnect.utils.PermissionException;

/**
 * Created by ibrahimradwan on 8/22/16.
 */
public class MainController {
    private static MainController mainController = null;

    private MainController () {
    }

    public static MainController getInstance () {
        if (mainController == null) {
            mainController = new MainController();
        }
        return mainController;
    }


    public boolean editConf (String conf_id, String name, String time, String topic_id, Context context) throws PermissionException {
        return DataProviderFunctions.getInstance().UpdateConf(conf_id, name, time, topic_id, context);
    }

    public long AddNewConf (String name, String time, String topic_id, Context context) throws SQLiteConstraintException, PermissionException {
        return DataProviderFunctions.getInstance().AddConf(name, time, topic_id, context);
    }

    public Cursor getConfs (Context context) {
        return DataProviderFunctions.getInstance().getConfs(context);
    }

    public boolean deleteConf (String id, Context context) throws PermissionException {
        return DataProviderFunctions.getInstance().deleteConf(id, context);
    }

    public void getAllConferencesList (Cursor mConferenceCursor, List<Conference> allConferences) {
        mConferenceCursor.moveToFirst();
        allConferences.clear();
        do {
            allConferences.add(new Conference(mConferenceCursor.getString(mConferenceCursor.getColumnIndex(Contract.ConfsEntry.COLUMN_CONF_ID)),
                    mConferenceCursor.getString(mConferenceCursor.getColumnIndex(Contract.ConfsEntry.COLUMN_CONF_NAME)),
                    mConferenceCursor.getString(mConferenceCursor.getColumnIndex(Contract.ConfsEntry.COLUMN_TOPIC_ID)),
                    mConferenceCursor.getString(mConferenceCursor.getColumnIndex(Contract.ConfsEntry.COLUMN_CONF_DATETIME))));
        } while (mConferenceCursor.moveToNext());
    }

    public Cursor getConfByID (String id, Context context) {
        return DataProviderFunctions.getInstance().getConfByID(id, context);
    }

    public void getAllDoctorsList (Cursor allDoctorsCursor, ArrayAdapter<String> arrayAdapter) {
        //Fill all doctors list
        allDoctorsCursor.moveToFirst();
        do {
            arrayAdapter.add(allDoctorsCursor.getString(allDoctorsCursor.getColumnIndex(Contract.UserEntry.COLUMN_USER_EMAIL)));
        } while (allDoctorsCursor.moveToNext());
    }

    public Cursor getDoctors (Context context) throws PermissionException {
        return DataProviderFunctions.getInstance().getDoctors(context);
    }

    public User getUserByID (String id, Context context) {
        return DataProviderFunctions.getInstance().getUserByID(id, context);
    }


    /**
     * Fetches the topics list from data provider
     *
     * @param context
     * @param topics  : list to be filled by topics
     * @return true if topics exist
     */
    public boolean getTopicsListForTopicsSpinner (Context context, List<Topic> topics) {
        // Get the topics
        Cursor topicsCursor = DataProviderFunctions.getInstance().getTopics(context);

        if (topicsCursor != null && topicsCursor.getCount() > 0) {

            topicsCursor.moveToFirst();
            /* Fill topics to the @topics list */
            do {
                topics.add(
                        new Topic(topicsCursor.getString(topicsCursor.getColumnIndex(Contract.TopicEntry.COLUMN_TOPIC_ID)),
                                topicsCursor.getString(topicsCursor.getColumnIndex(Contract.TopicEntry.COLUMN_DOC_ID)),
                                topicsCursor.getString(topicsCursor.getColumnIndex(Contract.TopicEntry.COLUMN_TOPIC_TITLE))));
            } while (topicsCursor.moveToNext());
            topicsCursor.close();
            return true;
        }
        topicsCursor.close();

        return false;
    }

    public Cursor getTopics (Context context) {
        return DataProviderFunctions.getInstance().getTopics(context);
    }

    public long AddTopic (String doc_id, String title, Context context) throws SQLiteConstraintException, PermissionException {
        return DataProviderFunctions.getInstance().AddTopic(doc_id, title, context);
    }

    public long AddInvite (String doc_id, String admin_id, String conf_id, Context context) throws SQLiteConstraintException, PermissionException {
        return DataProviderFunctions.getInstance().AddInvite(doc_id,
                admin_id,
                conf_id,
                context);
    }

    public Cursor getInvitesByDocID (Context context, String doc_id) throws PermissionException {
        return DataProviderFunctions.getInstance().getInvitesByDocID(context, doc_id);
    }

    public void getAllInvitesList (Cursor mInvitesCursor, List<Invite> allInvites) {
        mInvitesCursor.moveToFirst();
        do {
            allInvites.add(new Invite(mInvitesCursor.getString(mInvitesCursor.getColumnIndex(Contract.InvitesEntry.COLUMN_INVITE_ID)),
                    mInvitesCursor.getString(mInvitesCursor.getColumnIndex(Contract.InvitesEntry.COLUMN_CONF_ID)),
                    mInvitesCursor.getString(mInvitesCursor.getColumnIndex(Contract.InvitesEntry.COLUMN_ADMIN_ID)),
                    mInvitesCursor.getString(mInvitesCursor.getColumnIndex(Contract.InvitesEntry.COLUMN_DOC_ID)),
                    mInvitesCursor.getString(mInvitesCursor.getColumnIndex(Contract.InvitesEntry.COLUMN_STATUS_ID))));
        } while (mInvitesCursor.moveToNext());
    }

    public boolean AcceptInvite (String id, Context context) throws PermissionException {
        return DataProviderFunctions.getInstance().AcceptInvite(id, context);
    }

    public boolean RejectInvite (String id, Context context) throws PermissionException {
        return DataProviderFunctions.getInstance().RejectInvite(id, context);
    }

    public Topic getTopicByID (String id, Context context) {
        return DataProviderFunctions.getInstance().getTopicByID(id, context);
    }


    public Cursor checkForEmail (String email, Context context) {
        return DataProviderFunctions.getInstance().checkForEmail(email, context);
    }

    public User userLogin (String email, String pass, Context context) {
        return DataProviderFunctions.getInstance().userLogin(email, pass, context);
    }

    public User AddUser (String email, String pass, String usertype, Context context) {
        return DataProviderFunctions.getInstance().AddUser(email, pass, usertype, context);
    }
}