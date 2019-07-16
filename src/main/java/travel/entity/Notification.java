package travel.entity;

import java.util.Date;

public class Notification {
    private long id;
    private String artcleId;
    private long userId;
    private long taskId;
    private Date created;

    public long getId() {
        return id;
    }

    public String getArtcleId() {
        return artcleId;
    }

    public long getUserId() {
        return userId;
    }

    public Date getCreated() {
        return created;
    }

    public void setArtcleId(String artcleId) {
        this.artcleId = artcleId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }
}
