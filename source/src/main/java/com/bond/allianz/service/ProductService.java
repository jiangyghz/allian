package com.bond.allianz.service;

import com.bond.allianz.Dao.CarDao;
import com.bond.allianz.Dao.ProductDao;
import com.bond.allianz.Dao.logs;
import com.bond.allianz.entity.PageInfo;
import com.bond.allianz.utils.GuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ProductService {
    @Autowired
    private ProductDao poductDao ;


    public PageInfo<Map<String,Object>> selectpage(int pageindex, int pagesize,String icode,String pnamelike, int valid){
        return  poductDao.selectpage(pageindex,pagesize,icode,pnamelike,valid);
    }


    public  Map<String,Object> selectByKey(String productid){
        return  poductDao.selectByKey(productid);
    }
    public  int deleteVirtualByKey(String productid){
        return  poductDao.deleteVirtualByKey(productid);
    }

    public int updateByKey(String productid, String icode,String pname,float retailprice,float agentprice,float cost,String brand,float retaildiscount,float agentdiscount,float carcostrate,float tpa,String disc,String groupname) {
        return  poductDao.updateByKey(productid,icode,pname,retailprice,agentprice,cost,brand,retaildiscount,agentdiscount,carcostrate,tpa,disc,groupname);

    }
    public int insert( String icode,String pname,float retailprice,float agentprice,float cost,String brand,float retaildiscount,float agentdiscount,float carcostrate,float tpa,String disc,String groupname) {
        return  poductDao.insert(icode,pname,retailprice,agentprice,cost,brand,retaildiscount,agentdiscount,carcostrate,tpa,disc,groupname);
    }

    public String getProduct(String data){return poductDao.getProduct(data);}
    public String getMt_Product(String data){return poductDao.getMt_Product(data);}

    public String getTireList(String data){return poductDao.getTireList(data);}
    public String getdocument(String data){return poductDao.getdocument(data);}
    public String getGroupProduct(String data){return poductDao.getGroupProduct(data);}
    public String getTireParameter(String data){return poductDao.getTireParameter(data);}
    public String deletedocument(String data){return poductDao.deletedocument(data);}
    public String uploaddocument(String data,  MultipartFile file){return poductDao.uploaddocument(data,file);}
}
