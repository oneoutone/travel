package travel.entity;

import java.util.Date;

public class Product {
    private long id;
    private String name;
    private String description;
    private String status; //
    private int type; //1: 自营 2: 合作 3: 第三方
    private long userId;
    private Date created;
    private Date modified;

    public long getId() {
        return id;
    }

    public void setName(String name) { this.name = name; }

    public String getName() { return name; }

    public void setDescription(String description) { this.description = description; }

    public String getDescription() { return description; }

    public void setStatus(String status) { this.status = status; }

    public String getStatus() { return status; }

    public void setType(int type) { this.type = type; }

    public int getType() { return type; }

    public void setUserId(long userId) { this.userId = userId; }

    public long getUserId() {
        return userId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getModified() {
        return modified;
    }

    public void setModified(Date modified) {
        this.modified = modified;
    }


}
