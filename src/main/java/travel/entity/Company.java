package travel.entity;

import java.util.Date;

public class Company {
    private long id;
    private String name;
    private String regNo;
    private String acNo;
    private String bank;
    private String address;
    private String phone;
    private String contactPeople;
    private String type; //C, B
    private long managerId;
    private long adminId;
    private String status;
    private Date created;
    private String location;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRegNo() {
        return regNo;
    }

    public String getAcNo() {
        return acNo;
    }

    public String getBank() {
        return bank;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getContactPeople() {
        return contactPeople;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRegNo(String regNo) {
        this.regNo = regNo;
    }

    public void setAcNo(String acNo) {
        this.acNo = acNo;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setContactPeople(String contactPeople) {
        this.contactPeople = contactPeople;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setManagerId(long managerId) {
        this.managerId = managerId;
    }

    public void setAdminId(long adminId) {
        this.adminId = adminId;
    }

    public long getManagerId() {
        return managerId;
    }

    public long getAdminId() {
        return adminId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
