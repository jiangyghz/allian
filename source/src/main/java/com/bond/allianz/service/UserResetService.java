package com.bond.allianz.service;

import com.bond.allianz.Dao.CarDao;
import com.bond.allianz.Dao.UserResetDao;
import com.bond.allianz.Dao.logs;
import com.bond.allianz.entity.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserResetService {
    @Autowired
    private UserResetDao userResetDao ;


    public  Map<String,Object> selectByKey(String id){
        return userResetDao.selectByKey(id);
    }

    public Date selectLastDateByUserID(String userid) {
        return  userResetDao.selectLastDateByUserID(userid);
    }
    public int updateStateByKey(String id, int state) {
        return userResetDao.updateStateByKey(id,state);
    }
    public int insert(String id,String userid,String username,String email) {
        return userResetDao.insert(id,userid,username,email);
    }

    public int insertState(String id,String userid,String username,String email,int state) {
        return userResetDao.insertState(id,userid,username,email,state);
    }
}
