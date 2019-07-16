package travel.entity;

import java.util.Date;

public class DataSource {
    private long id;
    private long userId;
    private String sourceId;
    private String sourceName;
    private String sourceUrl;
    private String type;
    private String channel;
    private Date created;

    public long getId() {
        return id;
    }

    public long getUserId() {
        return userId;
    }

    public String getSourceId() {
        return sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getSourceUrl() {
        return sourceUrl;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public void setSourceUrl(String sourceUrl) {
        this.sourceUrl = sourceUrl;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getType() {
        return type;
    }

    public String getChannel() {
        return channel;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
