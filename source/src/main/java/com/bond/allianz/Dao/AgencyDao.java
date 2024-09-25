package com.bond.allianz.Dao;

import com.bond.allianz.entity.PageInfo;
import com.bond.allianz.utils.GuidUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class AgencyDao extends BaseDao {

    /**
     * 分页查询列表
     * @param pageindex
     * @param pagesize
     * @param idlike
     * @param namelike
     * @param
     * @param valid
     * @return
     */
    public PageInfo<Map<String,Object>> selectpage(int pageindex, int pagesize,String idlike,String namelike,String brand,int valid,String zone,String province,String city){
        String sql ="select * from dealer where 1=1 ";
        List<Object> queryList=new ArrayList<Object>();
        if(idlike!=null&&!"".equals(idlike)){
            sql+=" and dealerno like ?";
            queryList.add("%" + idlike + "%");
        }
        if(namelike!=null&&!"".equals(namelike)){
            sql+=" and dealername like ?";
            queryList.add("%" + namelike + "%");
        }
        if(brand!=null&&!"".equals(brand)){
            sql+=" and brand = ?";
            queryList.add(brand);
        }
        if(zone!=null&&!"".equals(zone)){
            sql+=" and zone = ?";
            queryList.add(zone);
        }
        if(province!=null&&!"".equals(province)){
            sql+=" and province = ?";
            queryList.add(province);
        }
        if(city!=null&&!"".equals(city)){
            sql+=" and city = ?";
            queryList.add(city);
        }
        if(valid>-1){
            sql+=" and valid = ?";
            queryList.add(valid);
        }
        sql+=" order by dealerno";

        PageInfo<Map<String,Object>> list= queryForPage(pageindex,pagesize,sql,queryList.toArray());
        return  list;
    }
    public List<Map<String,Object>> selectlist(String idlike,String namelike,String brand,int valid){
        String sql ="select * from dealer where 1=1 ";
        List<Object> queryList=new ArrayList<Object>();
        if(idlike!=null&&!"".equals(idlike)){
            sql+=" and dealerno like ?";
            queryList.add("%" + idlike + "%");
        }
        if(namelike!=null&&!"".equals(namelike)){
            sql+=" and dealername like ?";
            queryList.add("%" + namelike + "%");
        }
        if(brand!=null&&!"".equals(brand)){
            sql+=" and brand = ?";
            queryList.add(brand);
        }
        if(valid>-1){
            sql+=" and valid = ?";
            queryList.add(valid);
        }
        sql+=" order by dealerno";

        List<Map<String,Object>> list= jdbcTemplate.queryForList(sql,queryList.toArray());
        return  list;
    }
    public  Map<String,Object> selectByKey(String tid){
        String sql ="select * from dealer where tid=? ";
        Map<String,Object> target=jdbcTemplate.queryForMap(sql,tid);
        return  target;
    }
    public  List<Map<String,Object>> select(int valid){
        String sql ="select * from dealer where 1=1 ";
        List<Object> queryList=new ArrayList<Object>();
        if(valid>-1){
            sql+=" and valid = ?";
            queryList.add(valid);
        }
        sql+=" order by dealerno";
        List<Map<String,Object>> target=jdbcTemplate.queryForList(sql,queryList.toArray());
        return  target;
    }
    public  Map<String,Object> selectByDealerNo(String dealerno){
        String sql ="select * from dealer where dealerno=? ";
        Map<String,Object> target=jdbcTemplate.queryForMap(sql,dealerno);
        return  target;
    }
    public  String getSellByDealerNo(String data){

        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";

        String result="",dealerno="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;


        if (jsonObject.keySet().contains("dealerno"))dealerno=jsonObject.get("dealerno").getAsString();else return  GetErrorString(1,"");
        String sql ="select issellkey,isselltire,is_elec_invoice,notirepic from dealer where dealerno=? ";
        Map<String, Object> map=queryForMap(jdbcTemplate,sql,dealerno);
        jsonObject=new JsonObject();
        jsonObject.addProperty("issellkey",strToShortInt(map.get("issellkey")+""));
        jsonObject.addProperty("isselltire",strToShortInt(map.get("isselltire")+""));
        jsonObject.addProperty("is_elec_invoice",strToShortInt(map.get("is_elec_invoice")+""));
        jsonObject.addProperty("notirepic",strToShortInt(map.get("notirepic")+""));
        Gson gson = new Gson();
        String  resultss=gson.toJson(jsonObject);

        return  resultss;
    }
    public  int deleteByKey(String tid){
        String sql ="delete from dealer where tid=? ";
        int target=jdbcTemplate.update(sql,tid);
        return  target;
    }
    public  int deleteVirtualByKey(String tid){
        String sql ="update  dealer set valid=0 where tid=? ";
        int target=jdbcTemplate.update(sql,tid);
        return  target;
    }
    public  String selectNameByKey(String tid){
        String sql ="select dealername from dealer where tid=? ";
        List<String> target=jdbcTemplate.queryForList(sql,new Object[]{tid},String.class);
        if(target.size()>0){
            return target.get(0);
        }else{
            return "";
        }
    }
    public  int existsByCode(String dealerno){
        String sql ="select count(*) from dealer where dealerno=? ";
        List<Integer> target=jdbcTemplate.queryForList(sql,new Object[]{dealerno},Integer.class);
        if(target.size()>0){
            return target.get(0);
        }else{
            return 0;
        }
    }
    public  String selectNameByCode(String dealerno){
        String sql ="select dealername from dealer where dealerno=? ";
        List<String> target=jdbcTemplate.queryForList(sql,new Object[]{dealerno},String.class);
        if(target.size()>0){
            return target.get(0);
        }else{
            return "";
        }
    }

    public int updateByKey(String tid, Map<String,Object> map) {
        int flag=0;
        try {
            String sql = "update  dealer set  ";
            List<Object> query = new ArrayList<Object>();
            int i = 0;
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (i > 0) {
                    sql += ",";
                }
                sql += " " + entry.getKey() + "= ?";
                query.add(entry.getValue());
                i++;
            }

            sql += " where tid=?";
            query.add(tid);
            flag= jdbcTemplate.update(sql, query.toArray());
        }
        catch (Exception ex){
            logs.error("经销商保存错误",ex);
            ex.printStackTrace();
        }
        return  flag;
    }
    public int insert(Map<String,Object> map) {
        int flag=0;
        try {
            String sql = "insert into   dealer (tid ";
            String v= "?";
            List<Object> query = new ArrayList<Object>();
            query.add(GuidUtil.newGuid());
            int i = 1;
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
            flag= jdbcTemplate.update(sql, query.toArray());
        }
        catch (Exception ex){
            logs.error("经销商添加错误",ex);
            ex.printStackTrace();
        }
        return  flag;
    }



}
