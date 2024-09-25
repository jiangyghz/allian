package com.bond.allianz.Dao;

import com.bond.allianz.entity.PageInfo;
import com.bond.allianz.utils.GuidUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class CarDao extends BaseDao {


    public PageInfo<Map<String,Object>> selectCarpage(int pageindex, int pagesize,String brand,int valid){
        String sql ="select * from cars where 1=1 ";
        List<Object> queryList=new ArrayList<Object>();
        if(brand!=null&&!"".equals(brand)){
            sql+=" and brand = ?";
            queryList.add(brand);
        }
        if(valid>-1){
            sql+=" and valid = ?";
            queryList.add(valid);
        }
        sql+=" order by createtime";

        PageInfo<Map<String,Object>> list= queryForPage(pageindex,pagesize,sql,queryList.toArray());
        return  list;
    }
    public PageInfo<Map<String,Object>> selectVehiclepage(int pageindex, int pagesize,String brand,String cars,int valid){
        String sql ="select * from vehicletype where 1=1 ";
        List<Object> queryList=new ArrayList<Object>();
        if(brand!=null&&!"".equals(brand)){
            sql+=" and brand = ?";
            queryList.add(brand);
        }
        if(cars!=null&&!"".equals(cars)){
            sql+=" and cars = ?";
            queryList.add(cars);
        }
        if(valid>-1){
            sql+=" and valid = ?";
            queryList.add(valid);
        }
        sql+=" order by createtime";

        PageInfo<Map<String,Object>> list= queryForPage(pageindex,pagesize,sql,queryList.toArray());
        return  list;
    }

    /**
     * 查询车系
     * @param brand
     * @param valid
     * @return
     */
    public  List<Map<String,Object>> selectCar(String brand,int valid){
        String sql ="select * from cars where 1=1 ";
        List<Object> queryList=new ArrayList<Object>();
        if(brand!=null&&!"".equals(brand)){
            sql+=" and brand = ?";
            queryList.add(brand);
        }
        if(valid>-1){
            sql+=" and valid = ?";
            queryList.add(valid);
        }
        sql+=" order by brand,createtime";
        List<Map<String,Object>> target=jdbcTemplate.queryForList(sql,queryList.toArray());
        return  target;
    }
    public String get_all_cars(String data)
    {
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
        JsonArray jsonArray=new JsonArray();
        JsonObject jb= new JsonObject();
        jb.addProperty("brand","BMW");
        jb.add("cars",GetAllvehicletype("BMW",1));
        jsonArray.add(jb);
        jb= new JsonObject();
        jb.addProperty("brand","MINI");
        jb.add("cars",GetAllvehicletype("MINI",1));
        jsonArray.add(jb);
        return jsonArray.toString();
    }

    public JsonArray GetAllvehicletype(String brand,int valid)
    {
        String sql ="select * from cars where 1=1 ";
        List<Object> queryList=new ArrayList<Object>();
        if(brand!=null&&!"".equals(brand)){
            sql+=" and brand = ?";
            queryList.add(brand);
        }
        if(valid>-1){
            sql+=" and valid = ?";
            queryList.add(valid);
        }
        sql+=" order by brand,createtime";
        List<Map<String,Object>> target=jdbcTemplate.queryForList(sql,queryList.toArray());
        JsonArray jsonArray=new JsonArray();
        JsonObject jb;
        Map<String,Object>map;
        String cars="";
        for (int i=0;i<target.size();i++)
        {
            map=target.get(i);
            jb=new JsonObject();
            cars=map.get("cars")+"";
            jb.addProperty("brand",map.get("brand")+"");
            jb.addProperty("cars",cars);
            jb.addProperty("valid",nullToInt(map.get("valid").toString()));
              sql ="select * from vehicletype where 1=1 ";
           queryList=new ArrayList<Object>();
            if(brand!=null&&!"".equals(brand)){
                sql+=" and brand = ?";
                queryList.add(brand);
            }
            if(cars!=null&&!"".equals(cars)){
                sql+=" and cars = ?";
                queryList.add(cars);
            }
            if(valid>-1){
                sql+=" and valid = ?";
                queryList.add(valid);
            }
            jb.add("vehicle",GetJsonArray(sql,jdbcTemplate,queryList.toArray()));
            jsonArray.add(jb);
        }
        return jsonArray;
    }
    /**
     * 查询车型
     * @param brand
     * @param cars
     * @param valid
     * @return
     */
    public  List<Map<String,Object>> selectVehicle(String brand,String cars,int valid){
        String sql ="select * from vehicletype where 1=1 ";
        List<Object> queryList=new ArrayList<Object>();
        if(brand!=null&&!"".equals(brand)){
            sql+=" and brand = ?";
            queryList.add(brand);
        }
        if(cars!=null&&!"".equals(cars)){
            sql+=" and cars = ?";
            queryList.add(cars);
        }
        if(valid>-1){
            sql+=" and valid = ?";
            queryList.add(valid);
        }
        sql+=" order by brand,createtime";
        List<Map<String,Object>> target=jdbcTemplate.queryForList(sql,queryList.toArray());
        return  target;
    }
    public  Map<String,Object> selectCarByKey(String tid){
        String sql ="select * from cars where tid=? ";
        Map<String,Object> target=jdbcTemplate.queryForMap(sql,tid);
        return  target;
    }
    public  Map<String,Object> selectVehicleByKey(String tid){
        String sql ="select * from vehicletype where tid=? ";
        Map<String,Object> target=jdbcTemplate.queryForMap(sql,tid);
        return  target;
    }
    public  int deleteCarByKey(String tid){
        String sql ="delete from cars where tid=? ";
        int target=jdbcTemplate.update(sql,tid);
        return  target;
    }
    public  int deleteVehicleByKey(String tid){
        String sql ="delete from vehicletype where tid=? ";
        int target=jdbcTemplate.update(sql,tid);
        return  target;
    }
    public  String selectCarNameByKey(String tid){
        String sql ="select cars from cars where tid=? ";
        List<String> target=jdbcTemplate.queryForList(sql,new Object[]{tid},String.class);
        if(target.size()>0){
            return target.get(0);
        }else{
            return "";
        }
    }

    public int updateCarByKey(String tid, String brand,String cars,int valid) {

        int flag = 0;
        try {
            String sql = "update  cars set brand=?,cars=?,valid=? where tid=? ";
            flag = jdbcTemplate.update(sql, new Object[]{brand, cars, valid, tid});

        } catch (Exception ex) {
            logs.error("车系保存错误", ex);
            ex.printStackTrace();
        }
        return flag;
    }
    public int updateVehicleByKey(String tid, String brand,String cars,String vehicletype,int valid) {

        int flag = 0;
        try {
            String sql = "update  vehicletype set brand=?,cars=?,valid=?,vehicletype=? where tid=? ";
            flag = jdbcTemplate.update(sql, new Object[]{brand, cars, valid,vehicletype, tid});

        } catch (Exception ex) {
            logs.error("车型保存错误", ex);
            ex.printStackTrace();
        }
        return flag;
    }
    public int existsCar(String brand,String cars) {

        String sql = "select count(*) from   cars where brand=? and cars=? ";
        List<Integer> has = jdbcTemplate.queryForList(sql, new Object[]{brand, cars}, int.class);
        if (has.size() > 0) {
            return has.get(0);
        } else {
            return 0;
        }
    }
    public int insertCar(String brand,String cars,int valid) {
        int flag=0;
        try {
            String sql = "insert into   cars (tid ,brand,cars,valid) values(?,?,?,?) ";
            flag= jdbcTemplate.update(sql, new Object[]{GuidUtil.newGuid(), brand, cars, valid});
        }
        catch (Exception ex){
            logs.error("车系添加错误",ex);
            ex.printStackTrace();
        }
        return  flag;
    }
    public int insertVehicle(String brand,String cars,String vehicletype,int valid) {
        int flag=0;
        try {
            String sql = "insert into   vehicletype (tid ,brand,cars,vehicletype,valid) values(?,?,?,?,?) ";
            flag= jdbcTemplate.update(sql, new Object[]{GuidUtil.newGuid(), brand, cars,vehicletype, valid});
        }
        catch (Exception ex){
            logs.error("车型添加错误",ex);
            ex.printStackTrace();
        }
        return  flag;
    }


    public  List<Map<String,Object>> selectBrand(int valid){
        String sql ="select brand from brand where 1=1 ";
        List<Object> queryList=new ArrayList<Object>();
        if(valid>-1){
            sql+=" and valid = ?";
            queryList.add(valid);
        }
        sql+=" group by brand order by brand";
        List<Map<String,Object>> target=jdbcTemplate.queryForList(sql,queryList.toArray());
        return  target;
    }
    public  List<Map<String,Object>> selectSaleBrand(int valid){
        String sql ="select salebrand from brand where 1=1 ";
        List<Object> queryList=new ArrayList<Object>();
        if(valid>-1){
            sql+=" and valid = ?";
            queryList.add(valid);
        }
        sql+=" order by salebrand";
        List<Map<String,Object>> target=jdbcTemplate.queryForList(sql,queryList.toArray());
        return  target;
    }
//    public int insertBrand(String brand,int valid) {
//        int flag=0;
//        try {
//            String sql = "insert into   brand (tid ,brand,valid) values(?,?,?) ";
//            flag= jdbcTemplate.update(sql, new Object[]{GuidUtil.newGuid(), brand, valid});
//        }
//        catch (Exception ex){
//            logs.error("品牌添加错误",ex);
//            ex.printStackTrace();
//        }
//        return  flag;
//    }
//    public int updateBrandByKey(String tid, String brand,int valid) {
//
//        int flag = 0;
//        try {
//            String sql = "update  brand set brand=?,valid=? where tid=? ";
//            flag = jdbcTemplate.update(sql, new Object[]{brand, valid, tid});
//
//        } catch (Exception ex) {
//            logs.error("品牌保存错误", ex);
//            ex.printStackTrace();
//        }
//        return flag;
//    }



}
