package travel.entity;

import java.util.Date;

public class Follow {
    private long id;
    private String articleId;
    private String title;
    private String content;
    private String publish_time;
    private String source_name;
    private float postive;
    private long userId;
    private Date created;

    public String getArticleId() {
        return articleId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getPublish_time() {
        return publish_time;
    }

    public String getSource_name() {
        return source_name;
    }

    public float getPostive() {
        return postive;
    }

    public long getUserId() {
        return userId;
    }

    public Date getCreated() {
        return created;
    }

    public void setArticleId(String articleId) {
        this.articleId = articleId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setPublish_time(String publish_time) {
        this.publish_time = publish_time;
    }

    public void setSource_name(String source_name) {
        this.source_name = source_name;
    }

    public void setPostive(float postive) {
        this.postive = postive;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
