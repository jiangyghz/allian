package com.bond.allianz.entity;


import lombok.Data;

import java.util.List;

/**
 * 登录类
 */
@Data  //自动生成Getter，Setter，toString，构造函数等


public class UserInfo {
    /**
     * 用户ID
     */
    public String UserID;
    /**
     * 用户姓名
     */
    public String UserName;
    /**
     * 登录名
     */
    public String LoginName;
    /**
     * 角色
     */
    public List<String> Act;
    /**
     * 经销商id  总部为空
     */
    public String AgencyID;

    /**
     * 品牌
     */
    public  String Brand;

}
