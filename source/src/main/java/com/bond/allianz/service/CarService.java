package com.bond.allianz.service;

import com.bond.allianz.Dao.AgencyDao;
import com.bond.allianz.Dao.CarDao;
import com.bond.allianz.entity.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class CarService {
    @Autowired
    private CarDao carDao ;

    public PageInfo<Map<String,Object>> selectCarpage(int pageindex, int pagesize,String brand,int valid){
        return  carDao.selectCarpage(pageindex,pagesize,brand,valid);
    }
    public PageInfo<Map<String,Object>> selectVehiclepage(int pageindex, int pagesize,String brand,String cars,int valid){
        return  carDao.selectVehiclepage(pageindex,pagesize,brand,cars,valid);
    }

    /**
     * 查询车系
     * @param brand
     * @param valid
     * @return
     */
    public  List<Map<String,Object>> selectCar(String brand,int valid){
        return  carDao.selectCar(brand,valid);
    }

    /**
     * 查询车型
     * @param brand
     * @param cars
     * @param valid
     * @return
     */
    public  List<Map<String,Object>> selectVehicle(String brand,String cars,int valid){
        return  carDao.selectVehicle(brand,cars,valid);
    }
    public  Map<String,Object> selectCarByKey(String tid){

        return  carDao.selectCarByKey(tid);
    }
    public  Map<String,Object> selectVehicleByKey(String tid){
        return  carDao.selectVehicleByKey(tid);
    }
    public  String selectCarNameByKey(String tid){

        return  carDao.selectCarNameByKey(tid);
    }
    public  int deleteCarByKey(String tid){

        return  carDao.deleteCarByKey(tid);
    }
    public  int deleteVehicleByKey(String tid){
        return  carDao.deleteVehicleByKey(tid);
    }
    public int updateCarByKey(String tid, String brand,String cars,int valid){
        return carDao.updateCarByKey(tid,brand,cars,valid);
    }
    public int updateVehicleByKey(String tid, String brand,String cars,String vehicletype,int valid) {
        return carDao.updateVehicleByKey(tid,brand,cars,vehicletype,valid);
    }
    public int existsCar( String brand,String cars){
        return carDao.existsCar(brand,cars);
    }
    public int insertCar(String brand,String cars,int valid){
        return carDao.insertCar(brand,cars,valid);
    }
    public int insertVehicle(String brand,String cars,String vehicletype,int valid) {
        return carDao.insertVehicle(brand, cars, vehicletype, valid);
    }

    /**
     * 查询品牌
     * @param valid
     * @return
     */
    public List<Map<String,Object>> selectBrand(int valid){
        return carDao.selectBrand(valid);
    }
    /**
     * 查询销售品牌
     * @param valid
     * @return
     */
    public List<Map<String,Object>> selectSaleBrand(int valid){
        return carDao.selectSaleBrand(valid);
    }
//    public int insertBrand(String brand,int valid) {
//        return carDao.insertBrand(brand,valid);
//    }
//    public int updateBrandByKey(String tid, String brand,int valid) {
//        return carDao.updateBrandByKey(tid,brand,valid);
//    }
}
