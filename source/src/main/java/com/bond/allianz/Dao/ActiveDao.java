package com.bond.allianz.Dao;

import com.bond.allianz.utils.SendMailUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class ActiveDao  extends BaseDao{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    public String addorUpdateActive_subject(String data){
        String active_subject="",valid="1";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;
        if (jsonObject.keySet().contains("active_subject"))active_subject=jsonObject.get("active_subject").getAsString();
        if (jsonObject.keySet().contains("valid"))valid=jsonObject.get("valid").getAsString();
       if (active_subject.equals("")) return  GetErrorString(1,"");
        String sql ="";

        sql="update   active_subject set valid=? where active_subject=?" ;
        Object[]args=new Object[2];
        args[0]=valid;
        args[1]=active_subject;
        int ii=0;
        if (jdbcTemplate.update(sql,args)==0)
        {
            sql="insert into  active_subject(valid,active_subject) values (?,?)";
          ii=  jdbcTemplate.update(sql,args);
        }else ii=1;

        String resultss="";

        if (ii==1)resultss="{\"errcode\":\"0\",\"active_subject\":\""+active_subject+"\"}";
        else resultss=GetErrorString(3,"提交不成功！");
        return  resultss;
    }
    public String list_active_subject(String data){
        String valid="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;
        if (jsonObject.keySet().contains("valid"))valid=jsonObject.get("valid").getAsString();

        String sql ="";

        List<Object> queryList=new ArrayList<Object>();
        sql="select * from active_subject where 1=1 ";
        if (!valid.equals(""))
        {
            sql+=" and valid = ?";
            queryList.add(valid);
        }
        String resultss="";
      //  resultss=GetRsultString(sql,jdbcTemplate,queryList.toArray());
        resultss=GetJsonArray(sql,jdbcTemplate,queryList.toArray()).toString();
        return  resultss;
    }
    public String add_active(String data){
        //activity_id,activity_name,type,active_subject,start_time,end_time
        //all_cars,all_product,all_dealer,amount_show,print_remark,valid,userid
        //create_time	创建时间
        String activity_id="",activity_name="",type="",active_subject="",start_time="",end_time="",amount_show="";
        String print_remark="",valid="",userid="";
        int all_cars=0,all_product=0,all_dealer=0;
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;
        if (jsonObject.keySet().contains("activity_id"))activity_id=jsonObject.get("activity_id").getAsString();
        if (jsonObject.keySet().contains("valid"))valid=jsonObject.get("valid").getAsString();
        if (jsonObject.keySet().contains("activity_name"))activity_name=jsonObject.get("activity_name").getAsString();
        if (jsonObject.keySet().contains("type"))type=jsonObject.get("type").getAsString();
        if (jsonObject.keySet().contains("active_subject"))active_subject=jsonObject.get("active_subject").getAsString();
        if (jsonObject.keySet().contains("start_time"))start_time=jsonObject.get("start_time").getAsString();
        if (jsonObject.keySet().contains("end_time"))end_time=jsonObject.get("end_time").getAsString();
        if (jsonObject.keySet().contains("amount_show"))amount_show=jsonObject.get("amount_show").getAsString();
        if (jsonObject.keySet().contains("print_remark"))print_remark=jsonObject.get("print_remark").getAsString();
        if (jsonObject.keySet().contains("userid"))userid=jsonObject.get("userid").getAsString();

        if (jsonObject.keySet().contains("all_cars"))all_cars=strToShortInt( jsonObject.get("all_cars").getAsString());
        if (jsonObject.keySet().contains("all_product"))all_product=strToShortInt( jsonObject.get("all_product").getAsString());
        if (jsonObject.keySet().contains("all_dealer"))all_dealer=strToShortInt( jsonObject.get("all_dealer").getAsString());

       // if (active_subject.equals("")) return  GetErrorString(1,"");
        String sql ="";
        if (activity_id.equals(""))
        {
            sql="insert into active_main (activity_name,type,active_subject,start_time,end_time,all_cars,all_product,all_dealer,amount_show,print_remark,valid,userid) " ;
            sql+=" values (?,?,?,?,?,?,?,?,?,?,?,?)";
        }

        else
            sql="update active_main set activity_name=?,type=?,active_subject=?,start_time=?,end_time=?,all_cars=?,all_product=?,all_dealer=?,amount_show=?,print_remark=?,valid=?,userid=? where activity_id="+activity_id;
        Object[]args=new Object[12];
        args[0]=activity_name;
        args[1]=type;
        args[2]=active_subject;
        args[3]=start_time;
        args[4]=end_time;
        args[5]=all_cars;
        args[6]=all_product;
        args[7]=all_dealer;
        args[8]=amount_show;
        args[9]=print_remark;
        args[10]=valid;
        args[11]=userid;

        jdbcTemplate.update(sql,args);
        if (activity_id.equals(""))
        {
            sql="select max(activity_id) as  activity_id from active_main";
            Map<String,Object>map=queryForMap(jdbcTemplate,sql);
            if (map!=null)activity_id=map.get("activity_id")+"";
        }
        JsonObject jb=new JsonObject();
        jb.addProperty("activity_id",activity_id);
        return  jb.toString();
    }
    public String get_active(String data){
        String activity_name="",valid="",active_subject="",type="",dealerno="",brand="",cars="",vehicletype="",productid="",activity_id="";
        String stime1="",stime2="",etime1="",etime2="",update_time1="",update_time2="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        int pageindex=1 ,  pagesize=10;
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;
        if (jsonObject.keySet().contains("valid"))valid=jsonObject.get("valid").getAsString();
        if (jsonObject.keySet().contains("activity_name"))activity_name=jsonObject.get("activity_name").getAsString();
        if (jsonObject.keySet().contains("activity_id"))activity_id=jsonObject.get("activity_id").getAsString();
        if (jsonObject.keySet().contains("active_subject"))active_subject=jsonObject.get("active_subject").getAsString();
        if (jsonObject.keySet().contains("type"))type=jsonObject.get("type").getAsString();
        if (jsonObject.keySet().contains("dealerno"))dealerno=jsonObject.get("dealerno").getAsString();
        if (jsonObject.keySet().contains("brand"))brand=jsonObject.get("brand").getAsString();
        if (jsonObject.keySet().contains("cars"))cars=jsonObject.get("cars").getAsString();
        if (jsonObject.keySet().contains("vehicletype"))vehicletype=jsonObject.get("vehicletype").getAsString();
        if (jsonObject.keySet().contains("productid"))productid=jsonObject.get("productid").getAsString();
        if (jsonObject.keySet().contains("stime1"))stime1=jsonObject.get("stime1").getAsString();
        if (jsonObject.keySet().contains("stime2"))stime2=jsonObject.get("stime2").getAsString();
        if (jsonObject.keySet().contains("etime1"))etime1=jsonObject.get("etime1").getAsString();
        if (jsonObject.keySet().contains("etime2"))etime2=jsonObject.get("etime2").getAsString();
        if (jsonObject.keySet().contains("update_time1"))update_time1=jsonObject.get("update_time1").getAsString();
        if (jsonObject.keySet().contains("update_time2"))update_time2=jsonObject.get("update_time2").getAsString();
        if (jsonObject.keySet().contains("pageindex"))pageindex=jsonObject.get("pageindex").getAsInt();
        if (jsonObject.keySet().contains("pagesize"))pagesize=jsonObject.get("pagesize").getAsInt();

        String sql ="";

        List<Object> queryList=new ArrayList<Object>();
        sql="select * from active_main where 1=1 ";

        if (!valid.equals(""))
        {
            sql+=" and valid = ?";
            queryList.add(valid);
        }

        if (!activity_name.equals(""))
        {
            sql+=" and activity_name like ?";
            queryList.add("%"+activity_name+"%");
        }

        if (!active_subject.equals(""))
        {
            sql+=" and active_subject = ?";
            queryList.add(active_subject);
        }
        if (!type.equals(""))
        {
            sql+=" and type = ?";
            queryList.add(type);
        }
        if (!dealerno.equals("")&&!dealerno.equals("zb"))
        {
            sql+=" and (all_dealer = 1 or activity_id in (select activity_id from active_dealer where dealerno=?)) ";
            queryList.add(dealerno);
        }
        if (!brand.equals(""))
        {
            sql+=" and (all_cars = 1 or activity_id in (select activity_id from active_cars where brand=?)) ";
            queryList.add(brand);
        }
        if (!vehicletype.equals(""))
        {
            sql+=" and (all_cars = 1 or activity_id in (select activity_id from active_cars where vehicletype=?)) ";
            queryList.add(vehicletype);
        }
        if (!cars.equals(""))
        {
            sql+=" and (all_cars = 1 or activity_id in (select activity_id from active_cars where cars=?)) ";
            queryList.add(cars);
        }

        if (!stime1.equals(""))
        {
            sql+=" and start_time >= ?";
            queryList.add(stime1);
        }
        if (!stime2.equals(""))
        {
            sql+=" and start_time <= ?";
            queryList.add(stime2+" 23:59:59");
        }
        if (!etime1.equals(""))
        {
            sql+=" and end_time >= ?";
            queryList.add(etime1);
        }
        if (!etime2.equals(""))
        {
            sql+=" and end_time <= ?";
            queryList.add(etime2+" 23:59:59");
        }
        if (!update_time1.equals(""))
        {
            sql+=" and update_time >= ?";
            queryList.add(update_time1);
        }
        if (!update_time2.equals(""))
        {
            sql+=" and update_time <= ?";
            queryList.add(update_time2);
        }
        JsonArray groupJa=new JsonArray();
        List<Map<String, Object>> list;
        Map<String, Object>map;
        JsonObject jb;
        if (!productid.equals(""))
        {

            String sqlstring="select * from insuranceproduct where detail_productid like '%"+productid+"%' and groupname!='临时套餐' and valid=1";
            list = queryForList(jdbcTemplate,sqlstring);
            String group_productid="",group_pname="";

            for (int i=0;i<list.size();i++)
            {
                map=list.get(i);
                group_productid=map.get("productid")+"";
                group_pname=map.get("pname")+"";
                sqlstring=sql+"and (all_product = 1 or activity_id in (select activity_id from active_product where productid='"+group_productid+"'))";
                List<Map<String, Object>> list1=queryForList(jdbcTemplate,sqlstring,queryList.toArray());
                for (int ii=0;ii<list1.size();ii++)
                {
                    map=list1.get(ii);
                    jb=new JsonObject();
                    jb.addProperty("productid",group_productid);
                    jb.addProperty("pname",group_pname);
                    for (Map.Entry<String, Object> entry : map.entrySet()) {
                        String key = entry.getKey(); // 获取key值
                        jb.addProperty(key, NUllToSpace( map.get(key)));
                    }

                    groupJa.add(jb);
                }
            }
            sql+=" and (all_product = 1 or activity_id in (select activity_id from active_product where productid=?)) ";
            queryList.add(productid);
        }
        if (!activity_id.equals(""))
        {
            queryList=new ArrayList<Object>();
            sql="select * from active_main where 1=1 ";
            sql+=" and activity_id = ?";
            queryList.add(activity_id);
        }
        sql+=" order by create_time desc ";
        list = queryForList(jdbcTemplate,sql,queryList.toArray());
        JsonArray jsonArray=new JsonArray();


        int i0=(pageindex-1)*pagesize;
        int i1=pageindex*pagesize;
        if (i1>list.size())i1= list.size();
        for (int i=i0;i<i1;i++)
        {
            map=list.get(i);
            activity_id=map.get("activity_id")+"";
            String all_product=map.get("all_product")+"";
            String all_cars=map.get("all_cars")+"";
            jb=new JsonObject();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey(); // 获取key值
                jb.addProperty(key, NUllToSpace( map.get(key)));
            }
            jb.add("products",jsonArray_active_product(activity_id,all_product,productid));
            jb.add("cars",jsonArray_active_cars(activity_id,all_cars));
            jsonArray.add(jb);
        }
        jb=new JsonObject();
        jb.addProperty("recordcount",list.size());
        jb.addProperty("pageno",pageindex);
        jb.addProperty("pagesize",pagesize);
        jb.add("record",jsonArray);
        jb.add("group",groupJa);
        list.clear();

        return  jb.toString();
    }

    public String add_active_dealer(String data){
        String activity_id="",dealerno="",clear_activity_id="";

        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;
        JsonArray ja=new JsonArray();
        if (jsonObject.keySet().contains("clear_activity_id"))clear_activity_id=jsonObject.get("clear_activity_id").getAsString();
        if (jsonObject.keySet().contains("detail"))ja=jsonObject.get("detail").getAsJsonArray();
        String sql ="";
         if (!clear_activity_id.equals(""))
         {
             sql="delete from active_dealer where activity_id=?";
             jdbcTemplate.update(sql,clear_activity_id);
         }
         int count=0;
         Object[]args;
        for (int i=0;i< ja.size();i++)
        {
            jsonObject=ja.get(i).getAsJsonObject();
            dealerno="";activity_id="";
            if (jsonObject.keySet().contains("dealerno"))dealerno=jsonObject.get("dealerno").getAsString();else continue;
            if (jsonObject.keySet().contains("activity_id"))activity_id=jsonObject.get("activity_id").getAsString();else continue;
           sql="insert into  active_dealer (dealerno,activity_id) values  (?,?)";
            args=new Object[2];
            args[0]=dealerno;
            args[1]=activity_id;
            count+=jdbcTemplate.update(sql,args);
        }
        if (!activity_id.equals(""))
        {
            sql="update active_main set all_dealer=0 where activity_id=?";
            jdbcTemplate.update(sql,activity_id);

        }

        JsonObject jb=new JsonObject();
        jb.addProperty("count",count);
        return  jb.toString();
    }

    public String get_active_dealer(String data){
        String activity_id="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;
        if (jsonObject.keySet().contains("activity_id"))activity_id=jsonObject.get("activity_id").getAsString();
        String sql ="";
        List<Object> queryList=new ArrayList<Object>();
        sql="select * from active_dealer where 1=1 ";
        if (!activity_id.equals(""))
        {
            sql+=" and activity_id = ?";
            queryList.add(activity_id);
        }
        String resultss="";
        resultss=GetJsonArray(sql,jdbcTemplate,queryList.toArray()).toString();
        return  resultss;
    }
    public String add_active_product(String data){
        String activity_id="",productid="",clear_activity_id="";

        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;
        JsonArray ja=new JsonArray();
        if (jsonObject.keySet().contains("clear_activity_id"))clear_activity_id=jsonObject.get("clear_activity_id").getAsString();
        if (jsonObject.keySet().contains("detail"))ja=jsonObject.get("detail").getAsJsonArray();
        String sql ="";
        if (!clear_activity_id.equals(""))
        {
            sql="delete from active_product where activity_id=?";
            jdbcTemplate.update(sql,clear_activity_id);
        }

        int count=0;
        Object[]args;
        for (int i=0;i< ja.size();i++)
        {
            jsonObject=ja.get(i).getAsJsonObject();
            productid="";activity_id="";
            if (jsonObject.keySet().contains("productid"))productid=jsonObject.get("productid").getAsString();else continue;
            if (jsonObject.keySet().contains("activity_id"))activity_id=jsonObject.get("activity_id").getAsString();else continue;
            sql="insert into  active_product (productid,activity_id) values  (?,?)";
            args=new Object[2];
            args[0]=productid;
            args[1]=activity_id;
            count+=jdbcTemplate.update(sql,args);
        }
        if (!activity_id.equals(""))
        {
            sql="update active_main set all_product=0 where activity_id=?";
            jdbcTemplate.update(sql,activity_id);

        }
        JsonObject jb=new JsonObject();
        jb.addProperty("count",count);
        return  jb.toString();
    }
    public JsonArray jsonArray_active_product(String activity_id,String all_product,String productid){
        JsonArray ja=new JsonArray();
        String sql ="";
        List<Object> queryList=new ArrayList<Object>();
        sql="select p.pname,p.groupname,p.cars,p.productid from active_product a inner join insuranceproduct p on a.productid=p.productid where 1=1 ";
        if (all_product.equals("1"))
        {
            sql="select p.pname,p.groupname,p.cars,p.productid from  insuranceproduct p where p.valid=1 ";

        }
       else {
            if (!activity_id.equals("")) {
                sql += " and a.activity_id = ?";
                queryList.add(activity_id);
            }

        }
        if (!productid.equals("")) {
            sql += " and ( p.productid = ? or p.detail_productid like ? )";
            queryList.add(productid);
            queryList.add("%"+productid+"%");
        }
        ja = GetJsonArray(sql, jdbcTemplate, queryList.toArray());
        return  ja;
    }
    public String get_active_product(String data){
        String activity_id="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;
        if (jsonObject.keySet().contains("activity_id"))activity_id=jsonObject.get("activity_id").getAsString();
        String sql ="";
        List<Object> queryList=new ArrayList<Object>();
        sql="select a.tid,p.pname,p.groupname,p.cars,p.productid from active_product a inner join insuranceproduct p on a.productid=p.productid where 1=1  ";
        if (!activity_id.equals(""))
        {
            sql+=" and a.activity_id = ?";
            queryList.add(activity_id);
        }
        String resultss="";
        resultss=GetJsonArray(sql,jdbcTemplate,queryList.toArray()).toString();
        return  resultss;
    }
    public String add_active_cars(String data){
        String activity_id="",brand="",cars="",vehicletype="",clear_activity_id="";

        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;
        JsonArray ja=new JsonArray();
        if (jsonObject.keySet().contains("clear_activity_id"))clear_activity_id=jsonObject.get("clear_activity_id").getAsString();
        if (jsonObject.keySet().contains("detail"))ja=jsonObject.get("detail").getAsJsonArray();
        String sql ="";
        if (!clear_activity_id.equals(""))
        {
            sql="delete from active_cars where activity_id=?";
            jdbcTemplate.update(sql,clear_activity_id);
        }
        int count=0;
        Object[]args;
        for (int i=0;i< ja.size();i++)
        {
            jsonObject=ja.get(i).getAsJsonObject();
            brand="";cars="";vehicletype="";activity_id="";
            if (jsonObject.keySet().contains("brand"))brand=jsonObject.get("brand").getAsString();else continue;
            if (jsonObject.keySet().contains("activity_id"))activity_id=jsonObject.get("activity_id").getAsString();else continue;
            if (jsonObject.keySet().contains("cars"))cars=jsonObject.get("cars").getAsString();
            if (jsonObject.keySet().contains("vehicletype"))vehicletype=jsonObject.get("vehicletype").getAsString();

            sql="insert into  active_cars (brand,cars,vehicletype,activity_id) values  (?,?,?,?)";
            args=new Object[4];
            args[0]=brand;
            args[1]=cars;
            args[2]=vehicletype;
            args[3]=activity_id;
            count+=jdbcTemplate.update(sql,args);
        }
        if (!activity_id.equals(""))
        {
            sql="update active_main set all_cars=0 where activity_id=?";
            jdbcTemplate.update(sql,activity_id);

        }

        JsonObject jb=new JsonObject();
        jb.addProperty("count",count);
        return  jb.toString();
    }

    public String get_active_cars(String data){
        String activity_id="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;
        if (jsonObject.keySet().contains("activity_id"))activity_id=jsonObject.get("activity_id").getAsString();
        String sql ="";
        List<Object> queryList=new ArrayList<Object>();
        sql="select * from active_cars where 1=1 ";
        if (!activity_id.equals(""))
        {
            sql+=" and activity_id = ?";
            queryList.add(activity_id);
        }
        String resultss="";
        resultss=GetJsonArray(sql,jdbcTemplate,queryList.toArray()).toString();
        return  resultss;
    }
    public JsonArray jsonArray_active_cars(String activity_id,String all_cars){
        JsonArray ja=new JsonArray();
        String sql ="";
        List<Object> queryList=new ArrayList<Object>();
        sql="select brand,cars,vehicletype from active_cars where 1=1 ";
        if (all_cars.equals("1"))
        {
            sql="select brand,cars,vehicletype from vehicletype where valid=1 ";

        }
        else {
            if (!activity_id.equals("")) {
                sql += " and activity_id = ?";
                queryList.add(activity_id);
            }

        }

        ja = GetJsonArray(sql, jdbcTemplate, queryList.toArray());
        return  ja;
    }
}
