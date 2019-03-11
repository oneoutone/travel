package travel.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class User implements Serializable{
    private static final long serialVersionUID = -9077975168976887742L;

    private long id;
    private String username;
    private String password;
    private String name;
    private String phone;
    private List<String> roles;
    private Date created;
    private Date modified;
    private boolean email_warn;
    private boolean wechat_warn;
    private boolean message_warn;
    private boolean specify_source;

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
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

    public boolean isEmail_warn() {
        return email_warn;
    }

    public boolean isWechat_warn() {
        return wechat_warn;
    }

    public boolean isMessage_warn() {
        return message_warn;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setEmail_warn(boolean email_warn) {
        this.email_warn = email_warn;
    }

    public void setWechat_warn(boolean wechat_warn) {
        this.wechat_warn = wechat_warn;
    }

    public void setMessage_warn(boolean message_warn) {
        this.message_warn = message_warn;
    }

    public boolean isSpecify_source() {
        return specify_source;
    }

    public void setSpecify_source(boolean specify_source) {
        this.specify_source = specify_source;
    }
}
