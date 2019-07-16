package travel.entity;


import java.util.Date;

public class WechatAccessToken {
    private long id;
    private String access_token;
    private Date expire;

    public long getId() {
        return id;
    }

    public String getAccessToken() {
        return access_token;
    }

    public void setAccessToken(String access_token) {
        this.access_token = access_token;
    }

    public Date getExpire() {
        return expire;
    }

    public void setExpire(Date expire) {
        this.expire = expire;
    }
}
