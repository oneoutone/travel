package travel.entity;

import org.apache.shiro.authc.HostAuthenticationToken;

import java.util.Date;

public class JWTToken implements HostAuthenticationToken  {

    private long id;
    private String token;
    private Date expire;
    private long userId;
    private String host;
    public JWTToken(String token) {
        this(token, null);
    }
    public JWTToken(String token, String host) {
        this.token = token;
        this.host = host;
    }
    public String getHost() {
        return host;
    }
    public long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getExpire() {
        return expire;
    }

    public void setExpire(Date expire) {
        this.expire = expire;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }
    @Override
    public Object getPrincipal() {
        return token;
    }
    @Override
    public Object getCredentials() {
        return token;
    }
    @Override
    public String toString(){
        return token + ':' + host;
    }
}
