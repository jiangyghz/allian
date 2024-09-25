package com.bond.allianz.Dao;

import com.bond.allianz.entity.PageInfo;
import com.bond.allianz.utils.GuidUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class UserResetDao extends BaseDao {

    /**
     * 查询重置密码记录
     * @param id
     * @return
     */

    public  Map<String,Object> selectByKey(String id){
        String sql ="select * from userreset where id=? ";
        List<Map<String,Object>> target=jdbcTemplate.queryForList(sql,id);
        if(target.size()>0){
            return target.get(0);
        }else{
            return null;
        }
    }

    /**
     * 查询最后一次修改密码时间
     * @param userid
     * @return
     */
    public Date selectLastDateByUserID(String userid) {
        String sql = "select * from userreset where userid=?  and state=1 order by createddate desc";
        List<Map<String, Object>> target = jdbcTemplate.queryForList(sql, userid);
        if (target.size() > 0) {
            return (Date) target.get(0).get("createddate");
        } else {
            return null;
        }
    }

    /**
     * 修改状态标识
     * @param id
     * @param state
     * @return
     */
    public int updateStateByKey(String id, int state) {

        int flag = 0;
        try {
            String sql = "update  userreset set state=?  where id=? ";
            flag = jdbcTemplate.update(sql, new Object[]{state, id});

        } catch (Exception ex) {
            logs.error("车系保存错误", ex);
            ex.printStackTrace();
        }
        return flag;
    }
    public int insert(String id,String userid,String username,String email) {
        int flag=0;
        try {
            String sql = "insert into   userreset (id ,userid,username,email,state) values(?,?,?,?,?) ";
            flag= jdbcTemplate.update(sql, new Object[]{id, userid, username, email,0});
        }
        catch (Exception ex){
            logs.error("重置密码记录添加错误",ex);
            ex.printStackTrace();
        }
        return  flag;
    }
    public int insertState(String id,String userid,String username,String email,int state) {
        int flag=0;
        try {
            String sql = "insert into   userreset (id ,userid,username,email,state) values(?,?,?,?,?) ";
            flag= jdbcTemplate.update(sql, new Object[]{id, userid, username, email,state});
        }
        catch (Exception ex){
            logs.error("重置密码记录添加错误",ex);
            ex.printStackTrace();
        }
        return  flag;
    }





}
