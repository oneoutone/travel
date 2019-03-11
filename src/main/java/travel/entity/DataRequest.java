package travel.entity;

import java.util.Date;

public class DataRequest {
    private long id;
    private String jobName;
    private String source;
    private String keyWord;
    private String type;
    private String remark;
    private boolean message;
    private String phone;
    private String email;
    private String companyMobile;
    private String companyEmail;
    private String name;
    private String position;
    private String company;
    private long userId;
    private Date created;
    private String status;
    private double price;

    public long getId() {
        return id;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }
    public String getSource() {
        return source;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public String getType() {
        return type;
    }

    public String getRemark() {
        return remark;
    }

    public boolean isMessage() {
        return message;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getCompanyMobile() {
        return companyMobile;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public String getCompany() {
        return company;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setMessage(boolean message) {
        this.message = message;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCompanyMobile(String companyMobile) {
        this.companyMobile = companyMobile;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setCompany(String company) {
        this.company = company;
    }

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

    public String getStatus() {
        return status;
    }

    public double getPrice() {
        return price;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
