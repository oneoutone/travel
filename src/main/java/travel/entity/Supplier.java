package travel.entity;

import java.util.Date;

public class Supplier {
    private long id;
    private String status;
    private long userId;
    private Date created;

    public long getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public Date getCreated() {
        return created;
    }

    public long getUserId() {
        return userId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
}
