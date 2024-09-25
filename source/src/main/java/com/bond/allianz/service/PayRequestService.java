package com.bond.allianz.service;

import com.bond.allianz.Dao.PayRequestDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PayRequestService {
    @Autowired
    private PayRequestDao payRequestDao ;


    public  Map<String,Object> selectByid(String id){
        return payRequestDao.selectByid(id);
    }


    public  boolean  rechargeRefunds(String contractno){
        return  payRequestDao.rechargeRefunds(contractno);
    }
}
