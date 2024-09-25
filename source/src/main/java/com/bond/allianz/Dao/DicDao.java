package com.bond.allianz.Dao;

import com.bond.allianz.controller.BaseController;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class DicDao extends BaseDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 查询同级别中最大的dicid
     * @param dicidlike
     * @return
     */
    public String selectMaxID(String dicidlike){
        String sql="select max(dicid) from jt_dictionary where dicid like concat(?,'%')";//dic like 查询
        String id =jdbcTemplate.queryForObject(sql,new Object[]{ dicidlike},String.class);
        if(id!=null){
            return id;
        }else {
            return "";
        }
    }

    /**
     * 已父id 删除
     * @param pid
     * @return
     */
    public int deleteByPid(int pid){
        String sql="update  jt_dictionary set display=-1 where parent_id =?";//
        return jdbcTemplate.update(sql, pid);
    }
    public  String getinsurancetype()
    {
        String sqlstring="select * from insurancetype";
        return  GetRsultString(sqlstring,jdbcTemplate);
    }
    public  String getclaimtype(String data)
    {
        String brand="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;


        if (jsonObject.keySet().contains("brand"))brand=jsonObject.get("brand").getAsString();
        String sqlstring="select * from claimtype where brand=?";
         if (brand.equals(""))sqlstring="select distinct ClaimType from claimtype where 1=1 or brand=?";//brand没有的时候，只做显示用
        return  GetRsultString(sqlstring,jdbcTemplate,brand);
    }
    public String getDealerinfo(String data){
        String dealername="",dealerno="";
        String userid="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        int multi=0;
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;
        if (jsonObject.keySet().contains("userid"))userid=jsonObject.get("userid").getAsString();
        if (jsonObject.keySet().contains("dealerno"))dealerno=jsonObject.get("dealerno").getAsString();else return  GetErrorString(1,"");

        String sql ="";


        sql="select * from dealer where valid=1";
        List<Object> queryList=new ArrayList<Object>();
        if (!dealerno.equals(""))
        {
            sql+=" and dealername like ?";
            queryList.add("%"+dealername+"%");
        }
        if (!dealerno.equals(""))
        {
            sql+=" and dealerno=?";
            queryList.add(dealerno);
        }

      String resultss=GetRsultString(sql,jdbcTemplate,queryList.toArray());
        return  resultss;
    }
}
