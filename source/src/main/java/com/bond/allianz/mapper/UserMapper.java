package com.bond.allianz.mapper;

import com.bond.allianz.entity.User;

import java.util.List;

public interface UserMapper {
    int deleteByPrimaryKey(String id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(String id);

    User selectByLoginName(String agencyid,String username);

    User selectByLogin(String username);
    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
    List<User> selectAllList(String agencyid, String truename, String tel, String act, Integer valid,Integer isactived);
}