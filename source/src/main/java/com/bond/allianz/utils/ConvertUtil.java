package com.bond.allianz.utils;

import com.bond.allianz.entity.PageInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConvertUtil {

    /**
     * 转换page 字段
     * @param page
     * @param containsColumns
     * @return
     */
    public  static PageInfo<Map<String, Object>> PageConvert(PageInfo<Map<String, Object>> page, List<String> containsColumns){
        PageInfo<Map<String, Object>> result=new PageInfo<>();
        result.Context = page.Context;
        result.CurrentPage = page.CurrentPage;
        result.ItemsPerPage = page.ItemsPerPage;
        result.Time = page.Time;
        result.TotalItems = page.TotalItems;
        result.TotalPages = page.TotalPages;
        if (page.Items != null)
        {
            result.Items=new ArrayList<Map<String, Object>>();
            if(containsColumns!=null){
                for(Map<String,Object> map:page.Items){
                    Map<String,Object> temp=new HashMap<>();
                    for (String col:containsColumns){
                        if(map.containsKey(col)){
                            temp.put(col,map.get(col));
                        }
                    }
                    result.Items.add(temp);
                }
            }else{
                result.Items=page.Items;
            }
        }
        else
        {
            result.Items = new ArrayList<Map<String, Object>>();
        }
        return  result;
    }

    public  static List<Map<String, Object>> MapConvert(List<Map<String, Object>> map, List<String> containsColumns) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (containsColumns != null) {
            for (Map<String, Object> item : map) {
                Map<String, Object> temp = new HashMap<>();
                for (String col : containsColumns) {
                    if (item.containsKey(col)) {
                        temp.put(col, item.get(col));
                    }
                }
                result.add(temp);
            }
        } else {
            result = map;
        }

        return result;
    }


}
