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
    private String roles;
    private Date created;
    private Date modified;
    private boolean email_warn;
    private boolean wechat_warn;
    private boolean message_warn;
    private boolean app_warn;
    private boolean specify_source;
    private boolean specify_warn_setting;
    private String openId;
    private String headerUrl;
    private String warn_phones;
    private String warn_emails;
    private String type; //C: 个人, B： 公司
    private long companyId;

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

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
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

    public String getOpenId() {
        return openId;
    }

    public String getHeaderUrl() {
        return headerUrl;
    }

    public void setOpenId(String openId) {
        this.openId = openId;
    }

    public void setHeaderUrl(String headerUrl) {
        this.headerUrl = headerUrl;
    }

    public void setWarn_phones(String warn_phones) {
        this.warn_phones = warn_phones;
    }

    public String getWarn_phones() {
        return warn_phones;
    }

    public void setWarn_emails(String warn_emails) {
        this.warn_emails = warn_emails;
    }

    public String getWarn_emails() { return warn_emails; }

    public boolean isSpecify_warn_setting() {
        return specify_warn_setting;
    }

    public void setSpecify_warn_setting(boolean specify_warn_setting) {
        this.specify_warn_setting = specify_warn_setting;
    }

    public boolean isApp_warn() {
        return app_warn;
    }

    public void setApp_warn(boolean app_warn) {
        this.app_warn = app_warn;
    }

    public String getType() {
        return type;
    }

    public long getCompanyId() {
        return companyId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCompanyId(long companyId) {
        this.companyId = companyId;
    }
}
