package travel.entity;

import java.util.Date;

public class Event {
    private long id;
    private String title;
    private Date publish_time;
    private String location;
    private String industry;
    private Date created;

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Date getPublish_time() {
        return publish_time;
    }

    public String getLocation() {
        return location;
    }

    public String getIndustry() {
        return industry;
    }

    public Date getCreated() {
        return created;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPublish_time(Date publish_time) {
        this.publish_time = publish_time;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
