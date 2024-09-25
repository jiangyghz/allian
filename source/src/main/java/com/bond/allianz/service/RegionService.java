package com.bond.allianz.service;

import com.bond.allianz.Dao.RegionDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RegionService {
    @Autowired
    private RegionDao regionDao ;

    /**
     * 查询省份
     * @return
     */
    public  List<Map<String,Object>> selectProvince(){
        return  regionDao.selectProvince();
    }
    public  String dealerzone(String data){
        return  regionDao.dealerzone(data);
    }
    /**
     * 查询城市
     * @param provincecode
     * @return
     */
    public  List<Map<String,Object>> selectCity(String provincecode){
        return  regionDao.selectCity(provincecode);
    }
}
