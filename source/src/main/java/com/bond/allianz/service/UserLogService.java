package com.bond.allianz.service;

import com.bond.allianz.Dao.UserLogDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UserLogService {


    @Autowired
    private UserLogDao userLogDao;
    public void insertLog(String agencyid,String uid,String loginname,String ip) {
        userLogDao.insertLog(agencyid,uid,loginname,ip);
    }
    public void insertClick(String agencyid,String uid,String name,String url){
        userLogDao.insertClick(agencyid,uid,name,url);
    }

}
