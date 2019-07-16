package travel.entity;

import java.util.Date;

public class ArticleData {
    private long id;
    private String articleId;
    private String title;
    private String content;
    private Date publish_time;
    private String source_name;
    private float postive;
    private float negtive;
    private String type;
    private String articletype;
    private String sentiment;
    private String source_url;
    private String locations;

    public long getId() {
        return id;
    }

    public String getArticleId() {
        return articleId;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Date getPublish_time() {
        return publish_time;
    }

    public String getSource_name() {
        return source_name;
    }

    public float getPostive() {
        return postive;
    }

    public float getNegtive() {
        return negtive;
    }

    public String getType() {
        return type;
    }

    public String getArticletype() {
        return articletype;
    }

    public String getSentiment() {
        return sentiment;
    }

    public String getSource_url() {
        return source_url;
    }

    public String getLocations() {
        return locations;
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

    public void setPublish_time(Date publish_time) {
        this.publish_time = publish_time;
    }

    public void setSource_name(String source_name) {
        this.source_name = source_name;
    }

    public void setPostive(float postive) {
        this.postive = postive;
    }

    public void setNegtive(float negtive) {
        this.negtive = negtive;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setArticletype(String articletype) {
        this.articletype = articletype;
    }

    public void setSentiment(String sentiment) {
        this.sentiment = sentiment;
    }

    public void setSource_url(String source_url) {
        this.source_url = source_url;
    }

    public void setLocations(String locations) {
        this.locations = locations;
    }
}
