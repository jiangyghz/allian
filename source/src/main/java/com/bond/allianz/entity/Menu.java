package com.bond.allianz.entity;

/**
 * 经销商菜单
 */
public class Menu {
    private Integer menuno;

    private String menutext;

    private String menuname;

    private String url;

    private Integer pmenuno;

    private Integer sendmode;

    private String mainword;

    private Byte valid;


    private Integer sort;

    public Integer getMenuno() {
        return menuno;
    }

    public void setMenuno(Integer menuno) {
        this.menuno = menuno;
    }

    public String getMenutext() {
        return menutext;
    }

    public void setMenutext(String menutext) {
        this.menutext = menutext;
    }

    public String getMenuname() {
        return menuname;
    }

    public void setMenuname(String menuname) {
        this.menuname = menuname;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getPmenuno() {
        return pmenuno;
    }

    public void setPmenuno(Integer pmenuno) {
        this.pmenuno = pmenuno;
    }

    public Integer getSendmode() {
        return sendmode;
    }

    public void setSendmode(Integer sendmode) {
        this.sendmode = sendmode;
    }

    public String getMainword() {
        return mainword;
    }

    public void setMainword(String mainword) {
        this.mainword = mainword;
    }

    public Byte getValid() {
        return valid;
    }

    public void setValid(Byte valid) {
        this.valid = valid;
    }


    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}