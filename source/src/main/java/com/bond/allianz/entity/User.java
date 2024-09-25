package com.bond.allianz.entity;

import java.util.Date;

public class User {
    private String id;

    private String agencyid;

    private String username;

    private String truename;

    private String password;

    private String tel;

    private String email;

    private String act;

    private Integer valid;

    private Date createddate;

    private Integer islock;

    private Integer errorcount;

    private Date errortime;

    private String brand;

    private Integer isactived;
    private Date activetime;
    private Integer conditions;
    private String claimpro;
    private Integer isapproval;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAgencyid() {
        return agencyid;
    }

    public void setAgencyid(String agencyid) {
        this.agencyid = agencyid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTruename() {
        return truename;
    }

    public void setTruename(String truename) {
        this.truename = truename;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAct() {
        return act;
    }

    public void setAct(String act) {
        this.act = act;
    }

    public Integer getValid() {
        return valid;
    }

    public void setValid(Integer valid) {
        this.valid = valid;
    }

    public Date getCreateddate() {
        return createddate;
    }

    public void setCreateddate(Date createddate) {
        this.createddate = createddate;
    }

    public Integer getIslock() {
        return islock;
    }

    public void setIslock(Integer islock) {
        this.islock = islock;
    }

    public Integer getErrorcount() {
        return errorcount;
    }

    public void setErrorcount(Integer errorcount) {
        this.errorcount = errorcount;
    }

    public Date getErrortime() {
        return errortime;
    }

    public void setErrortime(Date errortime) {
        this.errortime = errortime;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Integer getIsactived() {
        return isactived;
    }

    public void setIsactived(Integer isactived) {
        this.isactived = isactived;
    }


    public Date getActivetime() {
        return activetime;
    }

    public void setActivetime(Date activetime) {
        this.activetime = activetime;
    }

    public Integer getConditions() {
        return conditions;
    }

    public void setConditions(Integer conditions) {
        this.conditions = conditions;
    }


    public String getClaimpro() {
        return claimpro;
    }

    public void setClaimpro(String claimpro) {
        this.claimpro = claimpro;
    }

    public Integer getIsapproval() {
        return isapproval;
    }

    public void setIsapproval(Integer isapproval) {
        this.isapproval = isapproval;
    }

}