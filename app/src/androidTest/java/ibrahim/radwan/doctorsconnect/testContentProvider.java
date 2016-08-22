package ibrahim.radwan.doctorsconnect;

import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import ibrahim.radwan.doctorsconnect.Models.User;
import ibrahim.radwan.doctorsconnect.Utils.PermissionException;
import ibrahim.radwan.doctorsconnect.Utils.Utils;
import ibrahim.radwan.doctorsconnect.data.Contract;
import ibrahim.radwan.doctorsconnect.data.DataProviderFunctions;
import ibrahim.radwan.doctorsconnect.data.Database;

/**
 * Created by ibrahimradwan on 8/20/16.
 */
public class TestContentProvider extends AndroidTestCase {
    @Override
    protected void setUp () throws Exception {
        super.setUp();
        mContext.deleteDatabase(Contract.DATABASE_NAME);
    }

    public void testProvider () {

        Database database = new Database(
                this.mContext);
        SQLiteDatabase db = database.getWritableDatabase();
        assertEquals(true, db.isOpen());

        Cursor c;
        DataProviderFunctions dpf = DataProviderFunctions.getInstance();
        try {
            assertTrue("Error, cannot insert user!!!", dpf.AddUser("ex@ex.com", "exexex", "1", mContext) != null);
            assertTrue("Error, cannot insert user!!!", dpf.AddUser("exADMIN@ex.com", "exexex", "2", mContext) != null);
            assertTrue("Error, cannot insert user!!!", dpf.AddUser("ex@exx.com", "exexex", "1", mContext) != null);

            assertFalse("Error: cannot load user by id", dpf.getUserByID("1", mContext) == null);

            c = dpf.checkForEmail("exADMIN@ex.com", mContext);
            assertTrue("Error: Email not found although its inserted !!!", c.getCount() > 0);
            c.close();
            Utils.saveUserDataToSharedPreferences(new User("1", "ex@ex.com", null, "1"), getContext());
            assertTrue("Error, cannot insert topic!!!", dpf.AddTopic("1", "asd", mContext) != -1);

            assertFalse("Error: cannot load topic by id", dpf.getTopicByID("1", mContext) == null);
            Utils.saveUserDataToSharedPreferences(new User("2", "exADMIN@ex.com", null, "2"), getContext());
            assertTrue("Error, cannot insert conf!!!", dpf.AddConf("asd", "!23", "1", mContext) != -1);

            c = dpf.getConfByID("1", mContext);
            assertFalse("Error: cannot load user by id", c.getCount() == 0);
            c.close();

            assertTrue("Error: cannot update the conference", dpf.UpdateConf("1", "aaa", "!we", "1", mContext) == true);

            c = dpf.getConfByID("1", mContext);
            c.moveToFirst();
            assertTrue("Error: cannot update the conference" + c.getString(c.getColumnIndex(Contract.ConfsEntry.COLUMN_CONF_NAME)), c.getString(c.getColumnIndex(Contract.ConfsEntry.COLUMN_CONF_NAME)).equals("aaa"));
            c.close();

            assertTrue("Error: cannot delete the conference", dpf.deleteConf("1", mContext) == true);

            c = dpf.getConfs(mContext);
            assertTrue("Error, deleting operation failed !!!", c.getCount() == 0);
            c.close();

            assertTrue("Error, cannot insert conf!!!", dpf.AddConf("asd", "!23", "1", mContext) != -1);

            assertTrue("Error, cannot insert invite!!!", dpf.AddInvite("1", "2", "2", mContext) != -1);
            Utils.saveUserDataToSharedPreferences(new User("1", "ex@ex.com", null, "1"), getContext());

            c = dpf.getInvitesByDocID(mContext, "1");
            c.moveToFirst();
            assertTrue("Error, default invite status must be pending!!!", c.getString(c.getColumnIndex(Contract.InvitesEntry.COLUMN_STATUS_ID)).equals(Contract.InviteStatusEntry.INVITE_STATUS_PENDING_ID));
            c.close();

            assertTrue("Error, Cannot accept invite", dpf.AcceptInvite("1", mContext) == true);
            c = dpf.getInvitesByDocID(mContext, "1");
            c.moveToFirst();
            assertTrue("Error, default invite status must be pending!!!", c.getString(c.getColumnIndex(Contract.InvitesEntry.COLUMN_STATUS_ID)).equals(Contract.InviteStatusEntry.INVITE_STATUS_ACCEPTED_ID));
            c.close();

            assertTrue("Error, Cannot reject invite", dpf.RejectInvite("1", mContext) == true);

            Utils.saveUserDataToSharedPreferences(new User("2", "exADMIN@ex.com", null, "2"), getContext());

            c = dpf.getDoctors(mContext);
            assertFalse("Error: Cannot fetch doctors", c.getCount() == 0);
            c.close();

            assertFalse("Error: Cannot login user", dpf.userLogin("ex@exx.com", "exexex", mContext) == null);

            Utils.saveUserDataToSharedPreferences(new User("1", "ex@ex.com", null, "1"), getContext());

            c = DataProviderFunctions.getInstance().getInvitesByDocID(mContext, "1");
            assertFalse("Error, Can fetch rejected invites", c.getCount() > 0);
            c.close();

            c = dpf.getConfs(mContext);
            assertFalse("Error: Cannot fetch confs", c.getCount() == 0);
            c.close();

            c = dpf.getTopics(mContext);
            assertFalse("Error: Cannot fetch topics", c.getCount() == 0);
            c.close();

        } catch (SQLiteConstraintException e) {
            e.printStackTrace();
            fail("SQLite Constraint Error");
        } catch (PermissionException e) {
            e.printStackTrace();
        }
        //Get Topics
        database.close();
        db.close();
    }

    @Override
    protected void tearDown () throws Exception {
        super.tearDown();
        mContext.deleteDatabase(Contract.DATABASE_NAME);
    }


}
