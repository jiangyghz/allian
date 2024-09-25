package com.bond.allianz.Dao;

import com.bond.allianz.entity.PageInfo;
import com.bond.allianz.utils.GuidUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class MtCarDao extends BaseDao{

    public PageInfo<Map<String,Object>> selectCarpage(int pageindex, int pagesize, String brand, String  valid){
        String sql ="select * from mt_cars where 1=1 ";
        List<Object> queryList=new ArrayList<Object>();
        if(brand!=null&&!"".equals(brand)){
            sql+=" and brand = ?";
            queryList.add(brand);
        }
        if(!valid.equals("")){
            sql+=" and valid = ?";
            queryList.add(valid);
        }
        sql+=" order by createtime";

        PageInfo<Map<String,Object>> list= queryForPage(pageindex,pagesize,sql,queryList.toArray());
        return  list;
    }
    public PageInfo<Map<String,Object>> selectVehiclepage(int pageindex, int pagesize,String brand,String cars,String valid){
        String sql ="select * from mt_vehicletype where 1=1 ";
        List<Object> queryList=new ArrayList<Object>();
        if(brand!=null&&!"".equals(brand)){
            sql+=" and brand = ?";
            queryList.add(brand);
        }
        if(cars!=null&&!"".equals(cars)){
            sql+=" and cars = ?";
            queryList.add(cars);
        }
        if(!valid.equals("")){
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
        String sql ="select * from mt_cars where 1=1 ";
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

    /**
     * 查询车型
     * @param brand
     * @param cars
     * @param valid
     * @return
     */
    public  List<Map<String,Object>> selectVehicle(String brand,String cars,int valid){
        String sql ="select * from mt_vehicletype where 1=1 ";
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
        String sql ="select * from mt_cars where tid=? ";
        Map<String,Object> target=jdbcTemplate.queryForMap(sql,tid);
        return  target;
    }
    public  Map<String,Object> selectVehicleByKey(String tid){
        String sql ="select * from mt_vehicletype where tid=? ";
        Map<String,Object> target=jdbcTemplate.queryForMap(sql,tid);
        return  target;
    }
    public  int deleteCarByKey(String tid){
        String sql ="delete from mt_cars where tid=? ";
        int target=jdbcTemplate.update(sql,tid);
        return  target;
    }
    public  int deleteVehicleByKey(String tid){
        String sql ="delete from mt_vehicletype where tid=? ";
        int target=jdbcTemplate.update(sql,tid);
        return  target;
    }
    public  String selectCarNameByKey(String tid){
        String sql ="select cars from mt_cars where tid=? ";
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
            String sql = "update  mt_cars set brand=?,cars=?,valid=? where tid=? ";
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
            String sql = "update  mt_vehicletype set brand=?,cars=?,valid=?,vehicletype=? where tid=? ";
            flag = jdbcTemplate.update(sql, new Object[]{brand, cars, valid,vehicletype, tid});

        } catch (Exception ex) {
            logs.error("车型保存错误", ex);
            ex.printStackTrace();
        }
        return flag;
    }
    public int existsCar(String brand,String cars) {

        String sql = "select count(*) from   mt_cars where brand=? and cars=? ";
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
            String sql = "insert into   mt_cars (tid ,brand,cars,valid) values(?,?,?,?) ";
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
            String sql = "insert into   mt_vehicletype (tid ,brand,cars,vehicletype,valid) values(?,?,?,?,?) ";
            flag= jdbcTemplate.update(sql, new Object[]{GuidUtil.newGuid(), brand, cars,vehicletype, valid});
        }
        catch (Exception ex){
            logs.error("车型添加错误",ex);
            ex.printStackTrace();
        }
        return  flag;
    }






}
