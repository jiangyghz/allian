package com.bond.allianz.entity;

import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

/**
 * 前端接口输出 json 对象
 */

public class JsonResult implements Serializable {
    /**
     * 状态 0 失败 ，1 成功
     */
    @Autowired
    public int code;
    /**
     * 对象数据
     */
    @Autowired
    public Object data;
    /**
     * 返回信息
     */
    @Autowired
    public String msg;
}
