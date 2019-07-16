package travel.entity;

public class PurchaseApply {
    private long id;
    private long purchaseId;
    private long bidInfoId;
    private String type;

    public long getId() {
        return id;
    }

    public long getPurchaseId() {
        return purchaseId;
    }

    public long getBidInfoId() {
        return bidInfoId;
    }

    public String getType() {
        return type;
    }

    public void setPurchaseId(long purchaseId) {
        this.purchaseId = purchaseId;
    }

    public void setBidInfoId(long bidInfoId) {
        this.bidInfoId = bidInfoId;
    }

    public void setType(String type) {
        this.type = type;
    }
}
