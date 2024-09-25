package com.bond.allianz.Dao;

import org.springframework.stereotype.Repository;

@Repository
public class UserLogDao extends BaseDao {


    /**
     * 登录日志
     * @param agencyid
     * @param uid
     * @param loginname
     * @param ip
     */
    public void insertLog(String agencyid,String uid,String loginname,String ip) {
        try {
            jdbcTemplate.update("insert into userlog(agencyid,userid,login_name,login_ip) values(?,?,?,?)", new Object[]{agencyid, uid, loginname, ip});
        } catch (Exception ex) {
            logs.error("登录日志错误", ex);
        }
    }

    /**
     * 点击菜单日志
     * @param agencyid
     * @param uid
     * @param name
     * @param url
     */
    public void insertClick(String agencyid,String uid,String name,String url) {
        try {
            jdbcTemplate.update("insert into userclick (agencyid,userid,name,url) values(?,?,?,?)", new Object[]{agencyid, uid, name, url});
        } catch (Exception ex) {
            logs.error("点击日志错误", ex);
        }
    }

}
