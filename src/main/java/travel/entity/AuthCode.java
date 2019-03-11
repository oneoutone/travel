package travel.entity;

import java.util.Date;

public class AuthCode {
    private long id;
    private String phone;
    private String code;
    //code使用的渠道
    private String channel;
    private Date expire;
    private Boolean used;

    public long getId() {
        return id;
    }

    public String getPhone() { return phone; }

    public void setPhone(String phone) { this.phone = phone; }

    public String getCode() { return code; }

    public void setCode(String code) { this.code = code; }

    public String getChannel() { return channel; }

    public void setChannel(String channel) { this.channel = channel; }

    public Date getExpire() { return expire; }

    public void setExpire(Date expire) { this.expire = expire; }

    public Boolean getUsed() { return used; }

    public void setUsed(Boolean used) { this.used = used; }
}
