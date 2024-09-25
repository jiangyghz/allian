package com.bond.allianz.entity;

public class Dictionary {
    private Integer dicid;

    private String name;

    private String memo;

    private Integer sort;

    private Integer parent_id;

    private Integer display;

    private Integer disabled;

    private String code;

    public Integer getDicid() {
        return dicid;
    }

    public void setDicid(Integer dicid) {
        this.dicid = dicid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Integer getParentId() {
        return parent_id;
    }

    public void setParentId(Integer parent_id) {
        this.parent_id = parent_id;
    }

    public Integer getDisplay() {
        return display;
    }

    public void setDisplay(Integer display) {
        this.display = display;
    }

    public Integer getDisabled() {
        return disabled;
    }

    public void setDisabled(Integer disabled) {
        this.disabled = disabled;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}