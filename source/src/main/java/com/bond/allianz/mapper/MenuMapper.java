package com.bond.allianz.mapper;

import com.bond.allianz.entity.Menu;

import java.util.List;
import java.util.Map;

public interface MenuMapper {
    int insert(Menu record);

    int insertSelective(Menu record);

    Menu selectByPrimaryKey(Integer tid);
    Menu selectByUrl(String url);

    int updateByPrimaryKey(Menu record);

    int deleteByPrimaryKey(Integer menuno);
    int deleteByPmenuno(Integer pmenuno);
    /**
     * valid  0 禁用的，1 启用的
     * @param valid
     * @return
     */
    List<Menu> selectList(boolean valid);
    List<Menu> selectAllList();

    /**
     * String agencyid, List<String> act
     * 如果传入的是单参数且参数类型是一个List的时候，collection属性值为list
     * 如果传入的是单参数且参数类型是一个array数组的时候，collection的属性值为array
     * 如果传入的参数是多个的时候，我们就需要把它们封装成一个Map了,
     * collection属性值就是传入的List或array对象在自己封装的map里面的key
     * @param
     * @return
     */
    List<Menu> selectListByAct(Map<String, Object> params);
}