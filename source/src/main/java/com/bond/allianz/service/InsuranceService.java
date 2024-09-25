package com.bond.allianz.service;

import com.bond.allianz.Dao.InsuranceDao;
import com.bond.allianz.Dao.ProductDao;
import com.bond.allianz.Dao.logs;
import com.bond.allianz.entity.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class InsuranceService {
    @Autowired
    private InsuranceDao insuranceDao ;


    public PageInfo<Map<String,Object>> selectpage(int pageindex, int pagesize,String inamelike, int valid){
        return  insuranceDao.selectpage(pageindex,pagesize,inamelike,valid);
    }

    public  List<Map<String,Object>> select(int valid){
        return  insuranceDao.select(valid);
    }
    public  Map<String,Object> selectByCode(String code){
        return  insuranceDao.selectByCode(code);
    }
    public  String selectNameByCode(String code){
        return  insuranceDao.selectNameByCode(code);
    }
    public  int deleteVirtualByKey(String code){
        return  insuranceDao.deleteVirtualByKey(code);
    }

    public int updateByKey(String code, String iname,String shortname,String address,String  email,String zip) {
        return  insuranceDao.updateByKey(code,iname,shortname,address,email,zip);

    }
    public int insert( String code,String iname,String shortname,String address,String email,String zip) {
        return  insuranceDao.insert(code,iname,shortname,address,email,zip);
    }






}
