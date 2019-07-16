package travel.entity;

import java.util.Date;

public class Report {
    private long id;
    private String type;
    private Date start;
    private Date end;
    private Date created;
    private String url;

    public long getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public Date getCreated() {
        return created;
    }

    public String getUrl() {
        return url;
    }

    public void setType(String type) { this.type = type; }

    public void setStart(Date start) { this.start = start; }

    public void setEnd(Date end) {
        this.end = end;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
