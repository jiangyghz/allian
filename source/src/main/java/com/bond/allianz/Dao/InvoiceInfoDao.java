package com.bond.allianz.Dao;

import com.bond.allianz.entity.PageInfo;
import com.bond.allianz.utils.GuidUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class InvoiceInfoDao extends BaseDao {




    public  Map<String,Object> select(){
        String sql ="select * from invoiceinfo  ";
        List<Map<String,Object>> target=jdbcTemplate.queryForList(sql);
        if(target.size()>0){
            return target.get(0);
        }else{
            return  new HashMap<String,Object>();
        }
    }

    public int save(Map<String,Object> map) {
        int flag=0;
        try {
            String sql = "insert into   invoiceinfo ( ";
            String v= "";
            List<Object> query = new ArrayList<Object>();
            int i = 0;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (i > 0) {
                    sql += ",";
                    v += ",";
                }
                sql += " " + entry.getKey() ;
                v+=" ? ";
                query.add(entry.getValue());
                i++;
            }

            sql += ") values("+v+")";

            jdbcTemplate.update("delete from invoiceinfo");
            flag= jdbcTemplate.update(sql, query.toArray());
        }
        catch (Exception ex){
            logs.error("开票信息添加错误",ex);
            ex.printStackTrace();
        }
        return  flag;
    }




}
