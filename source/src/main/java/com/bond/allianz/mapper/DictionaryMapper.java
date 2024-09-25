package com.bond.allianz.mapper;

import com.bond.allianz.entity.Dictionary;

import java.util.List;

public interface DictionaryMapper {
    int deleteByPrimaryKey(Integer dicid);

    int insert(Dictionary record);

    int insertSelective(Dictionary record);

    Dictionary selectByPrimaryKey(Integer dicid);

    int updateByPrimaryKeySelective(Dictionary record);

    int updateByPrimaryKey(Dictionary record);

    List<Dictionary> selectListByPId(Integer parent_id, Integer display);
    List<Dictionary> selectAllList();
}