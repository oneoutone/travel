package travel.entity;

import java.util.Date;

public class Product {
    private long id;
    private String name;
    private String description;
    private String status; //
    private String type; //1: 自营 2: 合作 3: 第三方
    private long userId;
    private double price;
    private Date created;
    private Date modified;
    private Boolean deleted;

    public long getId() {
        return id;
    }

    public void setName(String name) { this.name = name; }

    public String getName() { return name; }

    public void setDescription(String description) { this.description = description; }

    public String getDescription() { return description; }

    public void setStatus(String status) { this.status = status; }

    public String getStatus() { return status; }

    public void setType(String type) { this.type = type; }

    public String getType() { return type; }

    public void setUserId(long userId) { this.userId = userId; }

    public long getUserId() {
        return userId;
    }

    public void setPrice(double price) { this.price = price; }

    public double getPrice() {
        return price;
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

    public Boolean getDeleted() { return deleted; }

    public void getDeleted(Boolean deleted) { this.deleted = deleted; }

}
