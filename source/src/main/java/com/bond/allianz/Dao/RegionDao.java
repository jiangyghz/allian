package com.bond.allianz.Dao;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class RegionDao extends BaseDao {



    /**
     * 查询省份
     * @return
     */
    public  List<Map<String,Object>> selectProvince(){
        String sql ="select * from province  order by code ";
        List<Map<String,Object>> target=jdbcTemplate.queryForList(sql);
        return  target;
    }
    public  String dealerzone(String data)
    {


        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";

        String result="",brand="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();

        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        if (jsonObject.keySet().contains("brand"))brand=jsonObject.get("brand").getAsString();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;
        String sql ="select distinct  zone from dealer where zone !=''";
        if (!brand.equals(""))sql+=" and brand='"+brand+"'";
        result=GetRsultString(sql,jdbcTemplate);

        return result;
    }
    /**
     * 查询城市
     * @param provincecode
     * @return
     */
    public  List<Map<String,Object>> selectCity(String provincecode){
        String sql ="select * from city where provincecode=?  order by code ";
        List<Map<String,Object>> target=jdbcTemplate.queryForList(sql,provincecode);
        return  target;
    }




}
