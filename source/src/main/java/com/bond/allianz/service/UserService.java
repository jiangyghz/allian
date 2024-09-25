package com.bond.allianz.service;

import com.bond.allianz.Dao.UserDao;
import com.bond.allianz.entity.PageInfo;
import com.bond.allianz.entity.User;
import com.bond.allianz.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @Author: Spark
 * @Date: Create in 2019/4/10 13:48
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserDao userDao;

    public User getUserById(String id){
        return userMapper.selectByPrimaryKey(id);
    }

    public  int insert(User target){
        return userMapper.insert(target);

    }
    public int exitsUserName(String username){
        return  userDao.exitsUserName(username);
    }
    public  int update(User target){
        return userMapper.updateByPrimaryKey(target);
    }
    public  int delete (String id){
        return userMapper.deleteByPrimaryKey(id);
    }

    public  User selectByLoginName(String agencyid,String username){
        return userMapper.selectByLoginName(agencyid,username);
    }
    public  User selectByLoginName(String username){
        return userMapper.selectByLogin(username);
    }
    public  User selectByPrimaryKey(String id){
        return userMapper.selectByPrimaryKey(id);
    }
    public  String getuserid(String data){
        return userDao.getuserid(data);
    }
    /**
     * 获取用户盐值
     * @param uid
     * @return
     */
    public  String getSalt(String uid){
        return userDao.getSalt(uid);
    }

    /**
     * 设置盐值
     * @param uid
     * @param salt
     * @return
     */
    public  int setSalt(String uid,String salt){
        return userDao.setSalt(uid,salt);
    }


    public List<User> selectAll(String agencyid, String name,String tel,String act,int valid,int isactived){
        return userMapper.selectAllList(agencyid, name,tel,act,valid,isactived);
    }
    public PageInfo<Map<String,Object>> selectAgencyAll(int pageindex , int pagesize, String agencyid, String agencyname,String brand, String name, String tel, int valid,int isactived){
        return userDao.selectAgencyAll(pageindex,pagesize,agencyid, agencyname,brand,name,tel,valid,isactived);
    }
    public PageInfo<Map<String,Object>> selectAgencyAll2(int pageindex , int pagesize, String agencyid, String brand, String name, int valid,int isactived){
        return userDao.selectAgencyAll2(pageindex,pagesize,agencyid,brand,name,valid,isactived);
    }
    public  List<Map<String, Object>> selectAgencyAllExport(String agencyid,String agencyname,String brand, String namelike, String tel, int valid,int isactived,String act){
        return userDao.selectAgencyAllExport(agencyid,agencyname,brand,namelike,tel,valid,isactived,act);
    }

    public int updateConditions(String id,int  conditions) {
        return  userDao.updateConditions(id,conditions);
    }
}
