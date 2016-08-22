package ibrahim.radwan.doctorsconnect.models;

/**
 * Created by ibrahimradwan on 8/22/16.
 */
public class Invite {
    private String id, confID, adminID, docID, statusID;

    public String getId () {
        return id;
    }

    public void setId (String id) {
        this.id = id;
    }

    public String getConfID () {
        return confID;
    }

    public void setConfID (String confID) {
        this.confID = confID;
    }

    public String getAdminID () {
        return adminID;
    }

    public void setAdminID (String adminID) {
        this.adminID = adminID;
    }

    public String getDocID () {
        return docID;
    }

    public void setDocID (String docID) {
        this.docID = docID;
    }

    public String getStatusID () {
        return statusID;
    }

    public void setStatusID (String statusID) {
        this.statusID = statusID;
    }

    public Invite (String id, String confID, String adminID, String docID, String statusID) {

        this.id = id;
        this.confID = confID;
        this.adminID = adminID;
        this.docID = docID;
        this.statusID = statusID;
    }
}
