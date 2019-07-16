package travel.entity;

import java.util.Date;

public class Advice {

    private long id;
    private String title;
    private String content;
    private long userId;
    private long managerId;
    private Date created;

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public long getUserId() {
        return userId;
    }

    public long getManagerId() {
        return managerId;
    }

    public Date getCreated() {
        return created;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setManagerId(long managerId) {
        this.managerId = managerId;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
