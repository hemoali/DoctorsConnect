package ibrahim.radwan.doctorsconnect.models;

/**
 * Created by ibrahimradwan on 8/21/16.
 */
public class Conference {
    private String id, name, topic_id, datetime;

    public String getId () {
        return id;
    }

    public void setId (String id) {
        this.id = id;
    }

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public String getTopic_id () {
        return topic_id;
    }

    public void setTopic_id (String topic_id) {
        this.topic_id = topic_id;
    }

    public String getDatetime () {
        return datetime;
    }

    public void setDatetime (String datetime) {
        this.datetime = datetime;
    }

    public Conference (String id, String name, String topic_id, String datetime) {

        this.id = id;
        this.name = name;
        this.topic_id = topic_id;
        this.datetime = datetime;
    }
}
