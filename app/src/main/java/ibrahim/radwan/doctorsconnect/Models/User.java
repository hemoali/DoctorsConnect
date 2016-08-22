package ibrahim.radwan.doctorsconnect.models;

import java.io.Serializable;

/**
 * Created by ibrahimradwan on 8/20/16.
 */
public class User implements Serializable {
    private String userID, userEmail, pass, typeID;

    public User (String userID, String userEmail, String pass, String typeID) {
        this.userID = userID;
        this.userEmail = userEmail;
        this.pass = pass;
        this.typeID = typeID;
    }

    public String getUserID () {
        return userID;
    }

    public void setUserID (String userID) {
        this.userID = userID;
    }

    public String getUserEmail () {
        return userEmail;
    }

    public void setUserEmail (String userEmail) {
        this.userEmail = userEmail;
    }

    public String getPass () {
        return pass;
    }

    public void setPass (String pass) {
        this.pass = pass;
    }

    public String getTypeID () {
        return typeID;
    }

    public void setTypeID (String typeID) {
        this.typeID = typeID;
    }
}
