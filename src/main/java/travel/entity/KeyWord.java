package travel.entity;

import java.util.Date;

public class KeyWord {
    private long id;
    private String word;
    private String status; //enable, disable
    private long userId;
    private Date created;
    private Date modified;

    public long getId() {
        return id;
    }

    public void setWord(String word) { this.word = word; }

    public String getWord() { return word; }

    public void setStatus(String status) { this.status = status; }

    public String getStatus() { return status; }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
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
