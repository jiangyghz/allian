package com.bond.allianz.service;

import com.bond.allianz.Dao.AgencyDao;
import com.bond.allianz.entity.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AgencyService {
    @Autowired
    private AgencyDao agencyDao ;

    public PageInfo<Map<String,Object>> selectpage(int pageindex, int pagesize,String idlike,String namelike,String brand,int valid,String zone,String province,String city){
        return  agencyDao.selectpage(pageindex,pagesize,idlike,namelike,brand,valid,zone,province,city);
    }
    public List<Map<String,Object>> selectlist(String idlike,String namelike,String brand,int valid){
        return  agencyDao.selectlist(idlike,namelike,brand,valid);
    }
    public  String selectNameByKey(String tid){

        return  agencyDao.selectNameByKey(tid);
    }
    public  int existsByCode(String dealerno){
        return  agencyDao.existsByCode(dealerno);
    }
    public  String getSellByDealerNo(String data){
        return  agencyDao.getSellByDealerNo(data);
    }
    public  String selectNameByCode(String dealerno){
        return  agencyDao.selectNameByCode(dealerno);
    }
    public  List<Map<String,Object>> select(int valid){
        return  agencyDao.select(valid);
    }
    public  int deleteByKey(String tid){

        return  agencyDao.deleteByKey(tid);
    }
    public  int deleteVirtualByKey(String tid){
        return  agencyDao.deleteVirtualByKey(tid);
    }
    public  Map<String,Object> selectByKey(String tid){
        return  agencyDao.selectByKey(tid);
    }
    public  Map<String,Object> selectByDealerNo(String dealerno){
        return  agencyDao.selectByDealerNo(dealerno);
    }
    public int updateByKey(String tid, Map<String,Object> map){
        return agencyDao.updateByKey(tid,map);
    }
    public int insert( Map<String,Object> map){
        return agencyDao.insert(map);
    }

}
