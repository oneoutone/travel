package travel.entity;

import java.util.Date;

public class Batch {
    private long id;
    private String status;
    private Date start;
    private Date end;
    private Date created;
    private Date finished;
    private int count;

    public long getId() {
        return id;
    }

    public String getStatus() {
        return status;
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

    public Date getFinished() {
        return finished;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setFinished(Date finished) {
        this.finished = finished;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
