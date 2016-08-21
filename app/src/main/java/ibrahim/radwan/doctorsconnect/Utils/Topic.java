package ibrahim.radwan.doctorsconnect.Utils;

/**
 * Created by ibrahimradwan on 8/21/16.
 */
public class Topic {
    String id, doc_id, title;

    @Override
    public String toString () {
        return getTitle();
    }

    public Topic (String id, String doc_id, String title) {
        this.id = id;
        this.doc_id = doc_id;
        this.title = title;
    }

    public String getId () {

        return id;
    }

    public void setId (String id) {
        this.id = id;
    }

    public String getDoc_id () {
        return doc_id;
    }

    public void setDoc_id (String doc_id) {
        this.doc_id = doc_id;
    }

    public String getTitle () {
        return title;
    }

    public void setTitle (String title) {
        this.title = title;
    }
}
