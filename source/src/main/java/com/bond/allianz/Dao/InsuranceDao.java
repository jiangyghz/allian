package com.bond.allianz.Dao;

import com.bond.allianz.entity.PageInfo;
import com.bond.allianz.utils.GuidUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class InsuranceDao extends BaseDao {


    public PageInfo<Map<String,Object>> selectpage(int pageindex, int pagesize,String inamelike, int valid){
        String sql ="select * from insurance where 1=1 ";
        List<Object> queryList=new ArrayList<Object>();
        if(inamelike!=null&&!"".equals(inamelike)){
            sql+=" and iname like ?";
            queryList.add("%" + inamelike + "%");
        }
        if(valid>-1){
            sql+=" and valid = ?";
            queryList.add(valid);
        }
        sql+=" order by createtime";

        PageInfo<Map<String,Object>> list= queryForPage(pageindex,pagesize,sql,queryList.toArray());
        return  list;
    }

    public  List<Map<String,Object>> select(int valid){
        String sql ="select * from insurance where 1=1 ";
        List<Object> queryList=new ArrayList<Object>();
        if(valid>-1){
            sql+=" and valid = ?";
            queryList.add(valid);
        }
        sql+=" order by code";
        List<Map<String,Object>> target=jdbcTemplate.queryForList(sql,queryList.toArray());
        return  target;
    }

    public  Map<String,Object> selectByCode(String code){
        String sql ="select * from insurance where code=? ";
        Map<String,Object> target=jdbcTemplate.queryForMap(sql,code);
        return  target;
    }
    public  String selectNameByCode(String code){
        String sql ="select iname from insurance where code=? ";
        List<String> target=jdbcTemplate.queryForList(sql,new Object[]{code},String.class);
        if(target.size()>0){
            return target.get(0);
        }else{
            return "";
        }
    }
    public  int deleteVirtualByKey(String code){
        String sql ="update  insurance set valid=0 where code=? ";
        int target=jdbcTemplate.update(sql,code);
        return  target;
    }

    public int updateByKey(String code, String iname,String shortname,String address,String  email,String zip) {

        int flag = 0;
        try {
            String sql = "update  insurance set iname=?,shortname=?,address=?,email=?, zip=? where code=? ";
            flag = jdbcTemplate.update(sql, new Object[]{iname, shortname, address, email,zip,code});

        } catch (Exception ex) {
            logs.error("保险公司保存错误", ex);
            ex.printStackTrace();
        }
        return flag;
    }
    public int insert( String code,String iname,String shortname,String address,String email,String zip) {
        int flag=0;
        try {
            String sql = "insert into   insurance (code ,iname,shortname,address,email,zip,valid) values(?,?,?,?,?,?,?) ";
            flag= jdbcTemplate.update(sql, new Object[]{ code, iname, shortname,address,email,zip,1});
        }
        catch (Exception ex){
            logs.error("保险公司添加错误",ex);
            ex.printStackTrace();
        }
        return  flag;
    }




}
