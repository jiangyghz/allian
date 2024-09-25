package com.bond.allianz.service;

import com.bond.allianz.Dao.InsuranceDao;
import com.bond.allianz.Dao.InvoiceInfoDao;
import com.bond.allianz.Dao.logs;
import com.bond.allianz.entity.PageInfo;
import com.bond.allianz.utils.GuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class InvoiceInfoService {
    @Autowired
    private InvoiceInfoDao invoiceInfoDao ;


    public  Map<String,Object> select(){
        return  invoiceInfoDao.select();
    }

    public int save(Map<String,Object> map) {
        return  invoiceInfoDao.save(map);
    }






}
