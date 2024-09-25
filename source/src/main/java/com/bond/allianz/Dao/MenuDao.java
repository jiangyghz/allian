package com.bond.allianz.Dao;

import com.bond.allianz.entity.PageInfo;
import com.bond.allianz.utils.GuidUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class MenuDao extends BaseDao {

    /**
     * 查询角色是否有权限
     * @param actname
     * @param menuno
     * @return
     */
    public  boolean selectRoleByActMenuno(String actname,String menuno) {
        String sql = "select count(*) from actmenu where actname=?  and menuno=?";
        int target = jdbcTemplate.queryForObject(sql, new Object[]{actname, menuno}, Integer.class);
        return target > 0;
    }
    public  String selectMenoByUrlLike(String urllike) {
        String sql = "select menuno from   menu  where url like ? order by menuno";
        List<String> target = jdbcTemplate.queryForList(sql, new Object[]{"%" + urllike }, String.class);
        if(target.size()>0){
            return target.get(0);
        }else{
            return "";
        }
    }



}
