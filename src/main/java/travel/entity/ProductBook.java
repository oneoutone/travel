package travel.entity;

import java.util.Date;

public class ProductBook {
    private long id;
    private long userId;
    private long productId;
    private Date dueDate;
    private Date created;

    public long getId() {
        return id;
    }

    public long getUserId() { return userId; }

    public void setUserId(long userId) { this.userId = userId; }

    public long getProductId() { return productId; }

    public void setProductId(long productId) { this.productId = productId; }

    public Date getDueDate() { return dueDate; }

    public void setDueDate(Date dueDate) { this.dueDate = dueDate; }

    public Date getCreated() { return created; }

    public void setCreated(Date created) { this.created = created; }
}
