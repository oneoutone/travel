package travel.entity;

import java.util.Date;

public class Purchase {
    private long id;
    private String name;
    private String detail;
    private String industry;
    private String location;
    private String status;
    private String type; //1: 自建 2: 抓取
    private long userId;
    private double price;
    private Date start;
    private Date end;

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDetail() {
        return detail;
    }

    public String getIndustry() {
        return industry;
    }

    public String getLocation() {
        return location;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public long getUserId() {
        return userId;
    }

    public double getPrice() {
        return price;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
