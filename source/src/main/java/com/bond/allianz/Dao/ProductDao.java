package com.bond.allianz.Dao;

import com.bond.allianz.entity.PageInfo;
import com.bond.allianz.utils.GuidUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Repository
public class ProductDao extends BaseDao {

    @Value("${domain.enter}")
    public String domainenter;
    public PageInfo<Map<String,Object>> selectpage(int pageindex, int pagesize,String icode,String pnamelike, int valid){
        String sql ="select * from insuranceproduct where 1=1 ";
        List<Object> queryList=new ArrayList<Object>();
        if(icode!=null&&!"".equals(icode)){
            sql+=" and icode = ?";
            queryList.add(icode);
        }
        if(pnamelike!=null&&!"".equals(pnamelike)){
            sql+=" and pname like ?";
            queryList.add("%" + pnamelike + "%");
        }
        if(valid>-1){
            sql+=" and valid = ?";
            queryList.add(valid);
        }
        sql+=" order by createtime";

        PageInfo<Map<String,Object>> list= queryForPage(pageindex,pagesize,sql,queryList.toArray());
        return  list;
    }

    public String uploaddocument(String data,  MultipartFile file){
        String filename="";
        String userid="",fileurl="",type="";

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

        if (jsonObject.keySet().contains("type"))type=jsonObject.get("type").getAsString();

        if (jsonObject.keySet().contains("filename"))filename=jsonObject.get("filename").getAsString();else return  GetErrorString(1,"");
        if (jsonObject.keySet().contains("userid"))userid=jsonObject.get("userid").getAsString();else return  GetErrorString(1,"");
        fileurl=uploadJpg(file);
        String sql ="";
        String tid="";
        sql="insert  into training (fileurl,filename,userid,valid,type) values (?,?,?,1,?)";

        Object[] queryList=new Object[4];
        queryList[0]=fileurl;
        queryList[1]=filename;
        queryList[2]=userid;
        queryList[3]=type;
        jdbcTemplate.update(sql,queryList);
        sql="select max(tid) as tid from training";
        Map<String,Object>   map=queryForMap(jdbcTemplate,sql);
        if (map!=null)
        {
            tid=setting.NUllToSpace(map.get("tid"));
        }
      String resultss="";

       resultss="{\"errcode\":\"0\",\"tid\":\""+tid+"\"}";

        return  resultss;
    }
    public String deletedocument(String data){
        String tid="";


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


        if (jsonObject.keySet().contains("tid"))tid=jsonObject.get("tid").getAsString();else return  GetErrorString(1,"");

        String  sql="update training set valid=0 where tid="+tid;


        String resultss="";

        if (jdbcTemplate.update(sql)==1)resultss=GetErrorString(0,"");
        else resultss=GetErrorString(3,"删除不成功！");
        return  resultss;
    }
    public String getdocument(String data){


        int pageindex=1 ,  pagesize=10;
        String filename="",time1="",time2="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String result="",type="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;

        if (jsonObject.keySet().contains("pageindex"))pageindex=jsonObject.get("pageindex").getAsInt();
        if (jsonObject.keySet().contains("pagesize"))pagesize=jsonObject.get("pagesize").getAsInt();
        if (jsonObject.keySet().contains("time1"))time1=jsonObject.get("time1").getAsString();
        if (jsonObject.keySet().contains("time2"))time2=jsonObject.get("time2").getAsString();
        if (jsonObject.keySet().contains("filename"))filename=jsonObject.get("filename").getAsString();
        if (jsonObject.keySet().contains("type"))type=jsonObject.get("type").getAsString();
        String  sql="select *  from  training  where valid=1 ";
        List<Object> queryList=new ArrayList<Object>();
        if (!time1.equals("")) {
            sql += " and createtime>?";
            queryList.add(time1);
        }
        if (!time2.equals("")) {
            sql += " and createtime<?";
            queryList.add(time2+" 23:59:59");
        }
        if (!filename.equals("")) {
            sql += " and filename like ?";
            queryList.add("%"+filename+"%");
        }
        if (!type.equals("")) {
            sql += " and type = ?";
            queryList.add(type);
        }

        sql+=" order by tid desc";
        String resultss="";
         resultss=GetResultString1(sql,jdbcTemplate,pageindex,pagesize,queryList.toArray());
        return  resultss;
    }
    public  Map<String,Object> selectByKey(String productid){
        String sql ="select * from insuranceproduct where productid=? ";
        Map<String,Object> target=jdbcTemplate.queryForMap(sql,productid);
        return  target;
    }
    public  int deleteVirtualByKey(String productid){
        String sql ="update  insuranceproduct set valid=0 where productid=? ";
        int target=jdbcTemplate.update(sql,productid);
        return  target;
    }

    public int updateByKey(String productid, String icode,String pname,float retailprice,float agentprice,float cost, String brand,float retaildiscount,float agentdiscount,float carcostrate,float tpa,String disc,String groupname) {

        int flag = 0;
        try {
            String sql = "update  insuranceproduct set icode=?,pname=?,retailprice=?,agentprice=?, cost=?,brand=?, retaildiscount=?,agentdiscount=?,carcostrate=?,tpa=?,disc=?,group=? where productid=? ";
            flag = jdbcTemplate.update(sql, new Object[]{icode, pname, retailprice, agentprice,cost,brand,retaildiscount,agentdiscount,carcostrate,tpa,disc,groupname,productid});

        } catch (Exception ex) {
            logs.error("产品保存错误", ex);
            ex.printStackTrace();
        }
        return flag;
    }
    public int insert( String icode,String pname,float retailprice,float agentprice,float cost,String brand,float retaildiscount,float agentdiscount,float carcostrate,float tpa,String disc,String groupname) {
        int flag=0;
        try {
            String sql = "insert into   insuranceproduct (productid ,icode,pname,retailprice,agentprice,cost,valid,brand,retaildiscount,agentdiscount,carcostrate,tpa,disc,groupname) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";
            flag= jdbcTemplate.update(sql, new Object[]{GuidUtil.newGuid(), icode, pname, retailprice,agentprice,cost,1,brand,retaildiscount,agentdiscount,carcostrate,tpa,disc,groupname});
        }
        catch (Exception ex){
            logs.error("产品添加错误",ex);
            ex.printStackTrace();
        }
        return  flag;
    }
    public String getProduct(String data){
        String userid="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String sql ="",brand="",cars="",Invoicedate="",productid="";
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;
        if (jsonObject.keySet().contains("brand"))brand=jsonObject.get("brand").getAsString();
        if (jsonObject.keySet().contains("cars"))cars=jsonObject.get("cars").getAsString();
        if (jsonObject.keySet().contains("Invoicedate"))Invoicedate=jsonObject.get("Invoicedate").getAsString();
        if (jsonObject.keySet().contains("productid"))productid=jsonObject.get("productid").getAsString();
     sql="select productid,pname,agentprice,brand,retaildiscount,agentdiscount,carcostrate,tpa,disc,'升级计划' as groupname,groupname as typename,startmonth,endmonth,cars from insuranceproduct where 1=1 ";
        if (domainenter.equals("0")||domainenter.equals("1"))//保时捷、宝马加入分类
        {
            sql="select productid,pname,agentprice,brand,retaildiscount,agentdiscount,carcostrate,tpa,disc, groupname,groupname as typename,startmonth,endmonth,cars from insuranceproduct where 1=1 ";
           if (domainenter.equals("1"))//宝马增加产品的到期时间，到期后自动不显示
           {
               sql="select productid,pname,agentprice,brand,retaildiscount,agentdiscount,carcostrate,tpa,disc, groupname,groupname as typename,startmonth,endmonth,cars,endtime,detail_productid from insuranceproduct where (endtime is null or endtime>'"+GetNowDate("yyyy-MM-dd HH:mm:ss")+"') ";

           }
        }
        if (brand.equals("MINI"))brand="BMW";
     if (!brand.equals(""))sql+="  and brand='"+brand+"'";
     if (!cars.equals(""))sql+=" and (cars='' or cars is null or cars like '%"+cars+"%')";
     if (!Invoicedate.equals(""))sql+=" and DATE_ADD(curdate(),INTERVAL 0-startmonth MONTH)>='"+Invoicedate+"' and DATE_ADD(curdate(),INTERVAL 0-endmonth MONTH)<'"+Invoicedate+"'";
        if (!productid.equals(""))sql+=" and (productid='"+productid+"')";
        else      sql+=" and valid=1 ";

     sql+=" order by groupname,productid";
   List<Map<String,Object>>list=queryForList(jdbcTemplate,sql);
   String oldgroupname="";
   jsonObject=new JsonObject();
        JsonArray jsonArray=new JsonArray();
        JsonArray jsonArrayall=new JsonArray();
   for (int i=0;i<list.size();i++)
   {
       Map<String,Object>map=list.get(i);
       if (i==0)
       {
           oldgroupname=map.get("groupname")+"";
           jsonArray.add(mapTOjsonObject(map));
           continue;
       }
       if  (oldgroupname.equals(map.get("groupname")+""))
       {
           jsonArray.add(mapTOjsonObject(map));
       }else
       {
           jsonObject.addProperty("groupname",oldgroupname);
           jsonObject.add("groupdetail",jsonArray);
           jsonArrayall.add(jsonObject);
           jsonObject=new JsonObject();
            jsonArray=new JsonArray();
           oldgroupname=map.get("groupname")+"";
           jsonArray.add(mapTOjsonObject(map));
       }

   }
        jsonObject.addProperty("groupname",oldgroupname);
        jsonObject.add("groupdetail",jsonArray);
        jsonArrayall.add(jsonObject);
        Gson gson = new Gson();
        String  resultss=gson.toJson(jsonArrayall);
        list.clear();

        return  resultss;
    }
    public String getGroupProduct(String data){

        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String sql ="",detail_productid="",productid="",pname="",groupname="",detail="",disc="",cars="新车";
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;
        if (jsonObject.keySet().contains("detail_productid"))detail_productid=jsonObject.get("detail_productid").getAsString();
        else return  GetErrorString(3,"保险产品代码错误！");
        String old_detail_productid=detail_productid;
        String[]dd=detail_productid.split(",");
        Arrays.sort(dd);
        detail_productid="";
        for (int i=0;i<dd.length;i++)
        {
          if (!dd[i].equals(""))
            detail_productid+=dd[i]+",";
        }
        detail_productid=RTrim(detail_productid,",");
        //如果只有1个产品就直接返回原来的id,并且不是售后胎的产品
           if (detail_productid.split(",").length==1&&!detail_productid.contains("BMW09-"))
            {
                sql="select productid,pname,agentprice,retailprice,rti,tire,carkey, groupname,groupname as typename,startmonth,endmonth,cars,detail_productid,disc from insuranceproduct where 1=1 ";
                sql+=" and productid=?";
                Map<String,Object>map=queryForMap(jdbcTemplate,sql,detail_productid);
                if (map!=null)
                {
                    Gson gson = new Gson();
                    result=gson.toJson(map);
                    return result;
                }
            }

        sql="select productid,pname,agentprice,retailprice,rti,tire,carkey, groupname,groupname as typename,startmonth,endmonth,cars,detail_productid,disc from insuranceproduct where 1=1 ";
       //售后新换胎不能做排序处理
        String ss=detail_productid;
        if (detail_productid.contains("BMW09-"))ss=old_detail_productid;
        Map<String,Object>map=queryForMap(jdbcTemplate,sql+" and detail_productid=?",ss);
        double retailprice=0,agentprice=0;
        if (map!=null)
        {
            Gson gson = new Gson();
              result=gson.toJson(map);
              productid=map.get("productid")+"";
            groupname=map.get("groupname")+"";
            if (!groupname.equals("临时套餐")) return result;

              //更新价格
            sql="select productid,pname,agentprice,retailprice,rti,tire,carkey,detail, groupname,groupname as typename,startmonth,endmonth,cars,detail_productid,disc from insuranceproduct where 1=1 ";
            sql+=" and productid in ('"+detail_productid.replace(",","','")+"') ";
            List<Map<String,Object>>list=queryForList(jdbcTemplate,sql);
            Map<String,Object>pmap=new Hashtable<>();
            for (int i=0;i<list.size();i++)
            {
                map=list.get(i);
                pmap.put(map.get("productid")+"",map);
            }
            for (int i=0;i<dd.length;i++)
            {
                if (dd[i].equals(""))continue;
                map=(Map<String,Object>)pmap.get(dd[i]);
                retailprice+=nullToZero(map.get("retailprice")+"");
                agentprice+=nullToZero(map.get("agentprice")+"");
            }
            sql="update insuranceproduct set agentprice=?,retailprice=? where productid=?";
            Object[]args=new Object[3];
            args[0]=agentprice;
            args[1]=retailprice;
            args[2]=productid;
            jdbcTemplate.update(sql,args);
            update_detail_product(productid);
            return result;
        }
        else
        {
             productid="BMW10-0001";
            sql="select productid from insuranceproduct where productid like 'BMW10-%' order by productid desc";
            map=queryForMap(jdbcTemplate,sql);
            if (map!=null)
            {
                productid="BMW10-"+String.format("%04d", nullToInt( map.get("productid").toString().replace("BMW10-",""))+1);
            }

            int rti=0,tire=0,carkey=0;
            sql="select productid,pname,agentprice,retailprice,rti,tire,carkey,detail, groupname,groupname as typename,startmonth,endmonth,cars,detail_productid,disc from insuranceproduct where 1=1 ";

            sql+=" and productid in ('"+detail_productid.replace(",","','")+"') ";
            List<Map<String,Object>>list=queryForList(jdbcTemplate,sql);
            Map<String,Object>pmap=new Hashtable<>();
            for (int i=0;i<list.size();i++)
            {
                map=list.get(i);
                pmap.put(map.get("productid")+"",map);
            }
            groupname="临时套餐";
            for (int i=0;i<dd.length;i++)
            {
                if (dd[i].equals(""))continue;
                map=(Map<String,Object>)pmap.get(dd[i]);
                pname+=map.get("pname")+"+";
                if (map.get("detail")!=null)
                detail+=map.get("detail")+",";
                retailprice+=nullToZero(map.get("retailprice")+"");
                agentprice+=nullToZero(map.get("agentprice")+"");
                rti+=strToShortInt(map.get("rti")+"");
               if (tire==0) tire+=strToShortInt(map.get("tire")+"");
                carkey+=strToShortInt(map.get("carkey")+"");
                if ("售后新换胎".equals(map.get("cars")+""))cars="售后新换胎";
            }
            pname=RTrim(pname,"+");
            detail=RTrim(detail,",");
            disc=pname.replace("+",",");
            sql="insert into insuranceproduct (productid,pname,agentprice,retailprice,rti,tire,carkey,detail, groupname,disc,valid,icode,brand,cars,detail_productid)";

            sql+=" values (?,?,?,?,?,?,?,?,?,?,0,'CPICB','BMW','"+cars+"',?)";
            Object[]args=new Object[11];
            args[0]=productid;
            args[1]=pname;
            args[2]=agentprice;
            args[3]=retailprice;
            args[4]=rti;
            args[5]=tire;
            args[6]=carkey;
            args[7]=detail;
            args[8]=groupname;
            args[9]=disc;
            if (cars.equals("售后新换胎"))
                args[10]=old_detail_productid;
                else
            args[10]=detail_productid;
            jdbcTemplate.update(sql,args);
            //插入到明细对应表
            update_detail_product(productid);
            jsonObject=new JsonObject();
            jsonObject.addProperty("productid",productid);
            jsonObject.addProperty("pname",pname);
            jsonObject.addProperty("agentprice",agentprice);
            jsonObject.addProperty("retailprice",retailprice);
            jsonObject.addProperty("rti",rti);
            jsonObject.addProperty("tire",tire);
            jsonObject.addProperty("carkey",carkey);
            jsonObject.addProperty("groupname",groupname);
            jsonObject.addProperty("disc",disc);
            jsonObject.addProperty("typename",groupname);
            Gson gson = new Gson();
            result=gson.toJson(jsonObject);
            return result;
        }

    }

    public  void update_detail_product(String productid)
    {
      String sql="select productid,detail_productid from insuranceproduct where 1=1";
        List<Object> queryList = new ArrayList<Object>();
      if (!productid.equals(""))
      {
          sql+=" and productid=?";
          queryList.add(productid);
      }
        List<Map<String,Object>> list=queryForList(jdbcTemplate,sql,queryList.toArray());
        Map<String,Object>map;
        String detail_productid="";
        Object[] args;
      for (int i=0;i<list.size();i++)
      {
          map=list.get(i);
          productid=NUllToSpace(map.get("productid"));
          detail_productid=NUllToSpace(map.get("detail_productid"));
          sql="delete from sub_productid where productid=?";
          jdbcTemplate.update(sql,productid);
          if (detail_productid.equals(""))
          {
              sql="insert into sub_productid (productid,sub_productid) values (?,?)";
              args=new Object[2];
              args[0]=productid;
              args[1]=productid;
              jdbcTemplate.update(sql,args);
          }else
          {
              String[] ss=detail_productid.split(",");
              for (int ii=0;ii<ss.length;ii++)
              {
                  if (!ss[ii].equals(""))
                  {
                      sql="insert into sub_productid (productid,sub_productid) values (?,?)";
                      args=new Object[2];
                      args[0]=productid;
                      args[1]=ss[ii];
                      jdbcTemplate.update(sql,args);
                  }
              }
          }
      }
    }

private JsonObject mapTOjsonObject( Map<String,Object> map)
{
    if (map == null)
        return null;
    JsonObject   jsonObject=new JsonObject();
    for(Map.Entry<String, Object> entry : map.entrySet()){

        jsonObject.addProperty(entry.getKey()+"",map.get(entry.getKey()+"")+"");
    }


    return jsonObject;
}

    public String getTireParameter(String data){
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String sql ="";
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;
        jsonObject=new JsonObject();
        sql="select DISTINCT brand from tireinfo ";
        List<Map<String,Object>>list=queryForList(jdbcTemplate,sql);
        JsonArray jsonArray=new JsonArray();
        for (int i=0;i<list.size();i++)
        {
            Map<String,Object>map=list.get(i);
            jsonArray.add(map.get("brand")+"");

        }
        jsonArray.add("Others 其它");
        jsonObject.add("brand",jsonArray);

        sql="select DISTINCT size from tireinfo where size!='' order by size";
        list=queryForList(jdbcTemplate,sql);
        jsonArray=new JsonArray();
        for (int i=0;i<list.size();i++)
        {
            Map<String,Object>map=list.get(i);
            jsonArray.add(map.get("size")+"");

        }
        if (!jsonArray.toString().contains("R21"))jsonArray.add("R21");
        if (!jsonArray.toString().contains("R22"))jsonArray.add("R22");
        jsonArray.add("其它");
          jsonObject.add("size",jsonArray);

        sql="select DISTINCT right(weight,1) as weight from tireinfo  where right(weight,1)!=''";
        list=queryForList(jdbcTemplate,sql);
        jsonArray=new JsonArray();
        for (int i=0;i<list.size();i++)
        {
            Map<String,Object>map=list.get(i);

            jsonArray.add(map.get("weight")+"");

        }
        jsonArray.add("其它");
        jsonObject.add("weight",jsonArray);

        Gson gson = new Gson();
        String  resultss=gson.toJson(jsonObject);
        list.clear();

        return  resultss;
    }

    public String getTireList(String data){
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String sql ="",brand="",type="",weight="";
        int pageindex=1 ,  pagesize=10;
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;
        if (jsonObject.keySet().contains("pageindex"))pageindex=jsonObject.get("pageindex").getAsInt();
        if (jsonObject.keySet().contains("pagesize"))pagesize=jsonObject.get("pagesize").getAsInt();
        if (jsonObject.keySet().contains("brand"))brand=jsonObject.get("brand").getAsString();
        if (jsonObject.keySet().contains("type"))type=jsonObject.get("type").getAsString();
        if (jsonObject.keySet().contains("weight"))weight=jsonObject.get("weight").getAsString();
        jsonObject=new JsonObject();
        sql="select brand,type,price,weight from tireinfo where 1=1 ";
        List<Object> queryList=new ArrayList<Object>();
        if (!brand.equals("")) {
            sql += " and brand=?";
            queryList.add(brand);
        }
        if (!type.equals("")) {
            sql += " and type like ?";
            queryList.add("%"+type+"%");
        }
        if (!weight.equals("")) {
            sql += " and weight like ?";
            queryList.add("%"+weight+"%");
        }
        String resultss="";
        resultss=GetResultString1(sql,jdbcTemplate,pageindex,pagesize,queryList.toArray());

        return  resultss;
    }
    public String getMt_Product(String data){
        String userid="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String sql ="",brand="",cars="",Invoicedate="",productid="";
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;
        if (jsonObject.keySet().contains("brand"))brand=jsonObject.get("brand").getAsString();
        if (jsonObject.keySet().contains("cars"))cars=jsonObject.get("cars").getAsString();
        if (jsonObject.keySet().contains("Invoicedate"))Invoicedate=jsonObject.get("Invoicedate").getAsString();
        if (jsonObject.keySet().contains("productid"))productid=jsonObject.get("productid").getAsString();

        sql="select productid,pname,agentprice,brand,tpa,disc, groupname,groupname as typename,startmonth,endmonth,cars,endtime,detail_productid from mt_product where (endtime is null or endtime>'"+GetNowDate("yyyy-MM-dd HH:mm:ss")+"') ";
        //if (!brand.equals(""))sql+="  and brand='"+brand+"'";
        if (!cars.equals(""))sql+=" and (cars='' or cars is null or cars like '%"+cars+"%')";
        //if (!Invoicedate.equals(""))sql+=" and DATE_ADD(curdate(),INTERVAL 0-startmonth MONTH)>='"+Invoicedate+"' and DATE_ADD(curdate(),INTERVAL 0-endmonth MONTH)<'"+Invoicedate+"'";
        if (!productid.equals(""))sql+=" and (productid='"+productid+"')";
        else      sql+=" and valid=1 ";

        sql+=" order by productid";
        List<Map<String,Object>>list=queryForList(jdbcTemplate,sql);
        String oldgroupname="";
        jsonObject=new JsonObject();
        JsonArray jsonArray=new JsonArray();
        JsonArray jsonArrayall=new JsonArray();
        for (int i=0;i<list.size();i++)
        {
            Map<String,Object>map=list.get(i);
            if (i==0)
            {
                oldgroupname=map.get("groupname")+"";
                jsonArray.add(mapTOjsonObject(map));
                continue;
            }
            if  (oldgroupname.equals(map.get("groupname")+""))
            {
                jsonArray.add(mapTOjsonObject(map));
            }else
            {
                jsonObject.addProperty("groupname",oldgroupname);
                jsonObject.add("groupdetail",jsonArray);
                jsonArrayall.add(jsonObject);
                jsonObject=new JsonObject();
                jsonArray=new JsonArray();
                oldgroupname=map.get("groupname")+"";
                jsonArray.add(mapTOjsonObject(map));
            }

        }
        jsonObject.addProperty("groupname",oldgroupname);
        jsonObject.add("groupdetail",jsonArray);
        jsonArrayall.add(jsonObject);
        Gson gson = new Gson();
        String  resultss=gson.toJson(jsonArrayall);
        list.clear();

        return  resultss;
    }
}
