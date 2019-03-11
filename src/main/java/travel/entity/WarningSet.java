package travel.entity;

import java.util.Date;

public class WarningSet {
    private long id;
    private long userId;
    private String sourceId;
    private String sourceName;
    private String sourceUrl;
    private int readNum;
    private int discussNum;
    private int shareNum;
    private double negValue;
    private Date created;

    public String getSourceId() {
        return sourceId;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public void setNegValue(double negValue) {
        this.negValue = negValue;
    }

    public long getId() { return id; }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getSourceName() { return sourceName; }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceUrl() { return sourceUrl; }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public int getReadNum() { return readNum; }

    public void setReadNum(int readNum) {
        this.readNum = readNum;
    }

    public int getDiscussNum() { return discussNum; }

    public void setDiscussNum(int discussNum) {
        this.discussNum = discussNum;
    }

    public int getShareNum() { return shareNum; }

    public void setShareNum(int shareNum) { this.shareNum = shareNum; }

    public double getNegValue() { return negValue; }

    public void setNegValue(int negValue) { this.negValue = negValue; }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
