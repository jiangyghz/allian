package com.bond.allianz.Dao;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MtContractDao extends BaseDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;


    public String selectContractAll(String data){
        int pageindex=1 ,  pagesize=10,time1=0,time2=0,notbill=0,paytime1=0,paytime2=0,submittime1=0,submittime2=0;
        String dealerno="",  dealername="",  icode="",  productname="",    status="",contractno="",productid="",dealernamefull="",zone="";
        String vin="",claimstatus="",cname="",brand="",keystring="";
        String cars="",vehicletype="",updatetime1="",updatetime2="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        int multi=0;
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();

        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();

        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;

        String id="",uptime="";
        if (jsonObject.keySet().contains("dealerno"))dealerno=jsonObject.get("dealerno").getAsString();
        if (jsonObject.keySet().contains("dealername"))dealername=jsonObject.get("dealername").getAsString();
        if (jsonObject.keySet().contains("icode"))icode=jsonObject.get("icode").getAsString();
        if (jsonObject.keySet().contains("productname"))productname=jsonObject.get("productname").getAsString();
        if (jsonObject.keySet().contains("status"))status=jsonObject.get("status").getAsString();
        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();
        if (jsonObject.keySet().contains("vin"))vin=jsonObject.get("vin").getAsString();
        if (jsonObject.keySet().contains("claimstatus"))claimstatus=jsonObject.get("claimstatus").getAsString();
        if (jsonObject.keySet().contains("dealernamefull"))dealernamefull=jsonObject.get("dealernamefull").getAsString();
        if (jsonObject.keySet().contains("cname"))cname=jsonObject.get("cname").getAsString();
        if (jsonObject.keySet().contains("productid"))productid=jsonObject.get("productid").getAsString();
        if (jsonObject.keySet().contains("brand"))brand=jsonObject.get("brand").getAsString();
        if (jsonObject.keySet().contains("zone"))zone=jsonObject.get("zone").getAsString();
        if (jsonObject.keySet().contains("cars"))cars=jsonObject.get("cars").getAsString();
        if (jsonObject.keySet().contains("vehicletype"))vehicletype=jsonObject.get("vehicletype").getAsString();
        if (jsonObject.keySet().contains("keystring"))keystring=jsonObject.get("keystring").getAsString();

        if (jsonObject.keySet().contains("pageindex"))pageindex=jsonObject.get("pageindex").getAsInt();
        if (jsonObject.keySet().contains("pagesize"))pagesize=jsonObject.get("pagesize").getAsInt();
        if (jsonObject.keySet().contains("time1"))time1=setting.StrToInt(jsonObject.get("time1").getAsString());
        if (jsonObject.keySet().contains("time2"))time2=setting.StrToInt(jsonObject.get("time2").getAsString());
        if (jsonObject.keySet().contains("paytime1"))paytime1=setting.StrToInt(jsonObject.get("paytime1").getAsString());
        if (jsonObject.keySet().contains("paytime2"))paytime2=setting.StrToInt(jsonObject.get("paytime2").getAsString());
        if (jsonObject.keySet().contains("notbill"))notbill=setting.StrToInt(jsonObject.get("notbill").getAsString());
        if (jsonObject.keySet().contains("submittime1"))submittime1=setting.StrToInt(jsonObject.get("submittime1").getAsString());
        if (jsonObject.keySet().contains("submittime2"))submittime2=setting.StrToInt(jsonObject.get("submittime2").getAsString());
        if (jsonObject.keySet().contains("updatetime1"))updatetime1=jsonObject.get("updatetime1").getAsString();
        if (jsonObject.keySet().contains("updatetime2"))updatetime2=jsonObject.get("updatetime2").getAsString();
        String sql ="select u.username,c.*,DATE_FORMAT(c.canceltime,'%Y-%m-%d') as canceltime1,DATE_FORMAT(c.paytime,'%Y-%m-%d %H:%i:%s') as paytime1,DATE_FORMAT(c.submittime,'%Y-%m-%d %H:%i:%s') as submittime1,DATE_FORMAT(c.updatetime,'%Y-%m-%d %H:%i:%s') as updatetime1, if(isloan=1,'是','否') as isloans,FROM_UNIXTIME(c.ttime,'%Y-%m-%d %H:%i:%s') as createtime,d.dealername,d.bank,d.bankacount,v.brand as vbrand,v.code,i.disc \n" +
                ",(select remark from  contractremark where contractno=c.contractno order by ttime desc limit 0,1 ) as contractremark from mt_contract c inner join dealer d on c.dealerno=d.dealerno left outer join mt_vehicletype v on v.vehicletype=c.vehicletype left outer join mt_product i on i.productid=c.productid left outer join usermember u on c.userid=u.id";

        sql+=" where c.valid=1 " ;
        List<Object> queryList=new ArrayList<Object>();
        if (!dealerno.equals("")&&!dealerno.equals("zb")) {
            sql += " and c.dealerno=?";
            queryList.add(dealerno);
        }else
        {
            sql += " and c.status!='草稿'";
            if (brand.equals("BMW")||brand.equals("MINI"))   sql += " and c.status!='待支付'";
        }

   /*     if (!icode.equals("")) {
            sql += " and c.icode=? ";
            queryList.add(icode);
        }*/
        if (!vehicletype.equals("")) {
            sql += " and c.vehicletype=?";
            queryList.add(vehicletype);
        }
        if (!cars.equals("")) {
            sql += " and c.cars=?";
            queryList.add(cars);
        }
        if (!brand.equals("")) {
            sql += " and d.brand=?";
            queryList.add(brand);
        }
        if (!zone.equals("")) {
            sql += " and d.zone=?";
            queryList.add(zone);
        }

        if (!keystring.equals("")) {
            sql += " and (c.vin like ? or c.cname like ? or c.contractno like ?)";
            queryList.add("%"+keystring+"%");
            queryList.add("%"+keystring+"%");
            queryList.add("%"+keystring+"%");
        }
        if (!vin.equals("")) {
            sql += " and c.vin like ?";
            queryList.add("%"+vin+"%");
        }
        if (!claimstatus.equals("")) {
            sql += " and c.claimstatus=?";
            queryList.add(claimstatus);
        }
        if (!cname.equals("")) {
            sql += " and c.cname like  ?";
            queryList.add("%"+cname+"%");
        }
        if (!productname.equals("")) {
            sql += " and c.pname like  ?";
            queryList.add("%"+productname+"%");
        }
        if (!status.equals("")) {
            if (status.equals("已审批"))
            {
                sql += " and (c.status='已批准' or c.status='已拒绝')";
            }else if (status.equals("报告"))
            {
                sql += " and c.status!='草稿'";

            }else
            {
                sql += " and c.status=?";
                queryList.add(status);
            }

        }

        if (!productid.equals("")) {
            sql += " and c.productid like ?";
            queryList.add("%"+productid+"%");
        }
        if (!contractno.equals("")) {
            sql += " and c.contractno=?";
            queryList.add(contractno);
        }
        if (!dealername.equals("")) {
            sql += " and d.dealername like ?";
            queryList.add("%" + dealername + "%");
        }
        if (!dealernamefull.equals("")) {
            sql += " and d.dealername = ?";
            queryList.add(dealernamefull );
        }
        if (time1!=0) {
            sql += " and c.ttime>?";
            queryList.add(time1);
        }
        if (time2!=0) {
            sql += " and c.ttime<?";
            queryList.add(time2);
        }
        if (!updatetime1.equals("")) {
            sql += " and c.updatetime>=?";
            queryList.add(updatetime1);
        }

        if (!updatetime2.equals("")) {
            sql += " and c.updatetime<?";
            queryList.add(updatetime2);
        }
        if (paytime1!=0)
        {

            sql+=" and c.paytime >? ";
            queryList.add(stampToDate("yyyy-MM-dd HH:mm:ss",paytime1));
        }
        if (paytime2!=0)
        {
            sql+=" and c.paytime<? ";
            queryList.add(stampToDate("yyyy-MM-dd HH:mm:ss",paytime2));
            //queryList.add(paytime2);
        }
        if (submittime1!=0)
        {

            sql+=" and c.submittime>?";
            queryList.add(stampToDate("yyyy-MM-dd HH:mm:ss",submittime1));
           // queryList.add(submittime1);
        }
        if (submittime2!=0)
        {

            sql+=" and c.submittime<? ";
            queryList.add(stampToDate("yyyy-MM-dd HH:mm:ss",submittime2));
            //queryList.add(submittime2);
        }
        if (notbill==1)
        {
            return  getNotinBillContract(data);
            //  sql += " and c.contractno in (select contractno from billdetail  where (billno='' or billno is null) ) and c.status!='草稿' and c.status!='已提交' and c.status!='已出账单'";
            // if (brand.equals("BMW")||brand.equals("MINI")) sql+=" and c.status!='待支付'";
        }
        sql+=" order by c.ttime desc";
        String resultss="";
        //   List<Map<String, Object>> list=queryForList(jdbcTemplate,sql,queryList.toArray());
        resultss=GetResultString1(sql,jdbcTemplate,pageindex,pagesize,queryList.toArray());
        return  resultss;
    }
    public String getNotinBillContract(String data){
        int pageindex=1 ,  pagesize=10,time1=0,time2=0,notbill=0;
        String dealerno="",  dealername="",  icode="",  productname="",    status="",contractno="",productid="",dealernamefull="";
        String vin="",claimstatus="",cname="",brand="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        int multi=0;
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();

        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();

        // result=checkSign(nonce,time,sign,appid);
        //if (!result.equals(""))return result;

        String id="",uptime="";
        if (jsonObject.keySet().contains("dealerno"))dealerno=jsonObject.get("dealerno").getAsString();
        if (jsonObject.keySet().contains("dealername"))dealername=jsonObject.get("dealername").getAsString();
        if (jsonObject.keySet().contains("icode"))icode=jsonObject.get("icode").getAsString();
        if (jsonObject.keySet().contains("productname"))productname=jsonObject.get("productname").getAsString();
        if (jsonObject.keySet().contains("status"))status=jsonObject.get("status").getAsString();
        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();
        if (jsonObject.keySet().contains("vin"))vin=jsonObject.get("vin").getAsString();
        if (jsonObject.keySet().contains("claimstatus"))claimstatus=jsonObject.get("claimstatus").getAsString();
        if (jsonObject.keySet().contains("dealernamefull"))dealernamefull=jsonObject.get("dealernamefull").getAsString();
        if (jsonObject.keySet().contains("cname"))cname=jsonObject.get("cname").getAsString();
        if (jsonObject.keySet().contains("productid"))productid=jsonObject.get("productid").getAsString();
        if (jsonObject.keySet().contains("brand"))brand=jsonObject.get("brand").getAsString();

        if (jsonObject.keySet().contains("pageindex"))pageindex=jsonObject.get("pageindex").getAsInt();
        if (jsonObject.keySet().contains("pagesize"))pagesize=jsonObject.get("pagesize").getAsInt();
        if (jsonObject.keySet().contains("time1"))time1=jsonObject.get("time1").getAsInt();
        if (jsonObject.keySet().contains("time2"))time2=jsonObject.get("time2").getAsInt();
        if (jsonObject.keySet().contains("notbill"))notbill=jsonObject.get("notbill").getAsInt();
        String sql ="";
        sql ="select c.*,FROM_UNIXTIME(c.ttime,'%Y-%m-%d %H:%i:%s') as createtime,d.dealername,d.bank,d.brand,d.bankacount from billdetail b inner join mt_contract c on c.contractno=b.contractno inner join  dealer d on c.dealerno=d.dealerno";
        sql+=" where c.valid=1 and  (b.billno='' or b.billno is null)" ;
        List<Object> queryList=new ArrayList<Object>();
        if (!dealerno.equals("")&&!dealerno.equals("zb")) {
            sql += " and c.dealerno=?";
            queryList.add(dealerno);
        }else
        {
            sql += " and c.status!='草稿' ";
            if (brand.equals("BMW"))   sql += " and  c.status!='待支付'";
        }

        if (!icode.equals("")) {
            sql += " and c.icode=?";
            queryList.add(icode);
        }
        if (!brand.equals("")) {
            sql += " and d.brand=?";
            queryList.add(brand);
        }
        if (!vin.equals("")) {
            sql += " and c.vin like  ?";
            queryList.add("%"+vin+"%");
        }
        if (!claimstatus.equals("")) {
            sql += " and c.claimstatus=?";
            queryList.add(claimstatus);
        }
        if (!cname.equals("")) {
            sql += " and c.cname like  ?";
            queryList.add("%"+cname+"%");
        }
        if (!productname.equals("")) {
            sql += " and c.pname like  ?";
            queryList.add("%"+productname+"%");
        }
        if (!status.equals("")) {
            if (status.equals("已审批"))
            {
                sql += " and (c.status='已批准' or c.status='已拒绝')";
            }else if (status.equals("报告"))
            {
                sql += " and c.status!='草稿'";

            }else
            {
                sql += " and c.status=?";
                queryList.add(status);
            }

        }

        if (!productid.equals("")) {
            sql += " and c.productid like ?";
            queryList.add("%"+productid+"%");
        }
        if (!contractno.equals("")) {
            sql += " and c.contractno=?";
            queryList.add(contractno);
        }
        if (!dealername.equals("")) {
            sql += " and d.dealername like ?";
            queryList.add("%" + dealername + "%");
        }
        if (!dealernamefull.equals("")) {
            sql += " and d.dealername =?";
            queryList.add(dealernamefull );
        }
        if (time1!=0) {
            sql += " and c.ttime>?";
            queryList.add(time1);
        }
        if (time2!=0) {
            sql += " and c.ttime<?";
            queryList.add(time2);
        }
        sql+=" and c.status!='待支付'";
        sql+=" order by c.ttime desc";
        String resultss="";
        resultss=GetResultString1(sql,jdbcTemplate,pageindex,pagesize,queryList.toArray());
        //   List<Map<String, Object>> list=queryForList(jdbcTemplate,sql,queryList.toArray());
        // resultss=GetRsultString(list,pageindex,pagesize);
        return  resultss;
    }

    public String addorUpdateContract(String data){
        int isloan=0;
        String dealerno="", productid="", cname="",  icode="",  IdNo="",    mobile="",contractno="",email="",address="",brand="",cars="",vehicletype="",vin="";
        float guideprice=0,Invoiceprice=0,inputretailprice=0;
        String companyname="";
        int iscompany=0,mileage=0;
        String Invoiceno="",Invoicedate="",Businessinsurancepolicyno="",insurancecompany="",begindate="",enddate="",insuranceTypes="",insuranceForm="",remark="",userid="",forceno="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String contact_person="",contact_tel="",keytype="",isnewcar="0";
        String newkeyurl="",vehiclelicense="",oldkeyurl="",keypayurl="",PMPurl="",originInvoicedate="",insuranceurl="",Invoiceurl="",carstarttime="";
        String sale_person_tel="",sale_person="";
        int multi=0;
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;
        String id="",uptime="";
        int mactiveid=0;
        if (jsonObject.keySet().contains("dealerno"))dealerno=jsonObject.get("dealerno").getAsString();else return  GetErrorString(1,"");
        if (jsonObject.keySet().contains("cname"))cname=jsonObject.get("cname").getAsString();
        // if (jsonObject.keySet().contains("icode"))icode=jsonObject.get("icode").getAsString();
        if (jsonObject.keySet().contains("IdNo"))IdNo=jsonObject.get("IdNo").getAsString();
        if (jsonObject.keySet().contains("mobile"))mobile=jsonObject.get("mobile").getAsString();
        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();
        if (jsonObject.keySet().contains("productid"))productid=jsonObject.get("productid").getAsString();else return GetErrorString(3,"保险产品错误！");
        if (jsonObject.keySet().contains("isloan"))isloan=jsonObject.get("isloan").getAsInt();
        if (jsonObject.keySet().contains("guideprice"))guideprice=nullToZero(jsonObject.get("guideprice").getAsString());else guideprice=0;
        if (jsonObject.keySet().contains("Invoiceprice"))Invoiceprice=nullToZero(jsonObject.get("Invoiceprice").getAsString());
        if (jsonObject.keySet().contains("email"))email=jsonObject.get("email").getAsString();
        if (jsonObject.keySet().contains("address"))address=jsonObject.get("address").getAsString();
        if (jsonObject.keySet().contains("brand"))brand=jsonObject.get("brand").getAsString();
        if (jsonObject.keySet().contains("cars"))cars=jsonObject.get("cars").getAsString();
        if (jsonObject.keySet().contains("vehicletype"))vehicletype=jsonObject.get("vehicletype").getAsString();
        if (jsonObject.keySet().contains("vin"))vin=jsonObject.get("vin").getAsString();
        if (jsonObject.keySet().contains("Invoiceno"))Invoiceno=jsonObject.get("Invoiceno").getAsString();
        if (jsonObject.keySet().contains("Invoicedate"))Invoicedate=jsonObject.get("Invoicedate").getAsString();
        if (jsonObject.keySet().contains("Businessinsurancepolicyno"))Businessinsurancepolicyno=jsonObject.get("Businessinsurancepolicyno").getAsString();
        if (jsonObject.keySet().contains("insurancecompany"))insurancecompany=jsonObject.get("insurancecompany").getAsString();
        if (jsonObject.keySet().contains("begindate"))begindate=jsonObject.get("begindate").getAsString();
        if (jsonObject.keySet().contains("enddate"))enddate=jsonObject.get("enddate").getAsString();
        if (jsonObject.keySet().contains("insuranceTypes"))insuranceTypes=jsonObject.get("insuranceTypes").getAsString();
        if (jsonObject.keySet().contains("insuranceForm"))insuranceForm=jsonObject.get("insuranceForm").getAsString();
        if (jsonObject.keySet().contains("remark"))remark=jsonObject.get("remark").getAsString();
        if (jsonObject.keySet().contains("userid"))userid=jsonObject.get("userid").getAsString();
        if (jsonObject.keySet().contains("companyname"))companyname=jsonObject.get("companyname").getAsString();
        if (jsonObject.keySet().contains("iscompany"))iscompany=setting.StrToInt(jsonObject.get("iscompany").getAsString());
        if (jsonObject.keySet().contains("forceno"))forceno=jsonObject.get("forceno").getAsString();
        if (jsonObject.keySet().contains("mactiveid"))mactiveid=jsonObject.get("mactiveid").getAsInt();
        if (jsonObject.keySet().contains("mileage"))mileage=setting.StrToInt(jsonObject.get("mileage").getAsString());
        if (jsonObject.keySet().contains("contact_person"))contact_person=jsonObject.get("contact_person").getAsString();
        if (jsonObject.keySet().contains("contact_tel"))contact_tel=jsonObject.get("contact_tel").getAsString();
        if (jsonObject.keySet().contains("keytype"))keytype=jsonObject.get("keytype").getAsString();
        if (jsonObject.keySet().contains("isnewcar"))isnewcar=jsonObject.get("isnewcar").getAsString();
        if (jsonObject.keySet().contains("insuranceurl"))insuranceurl=jsonObject.get("insuranceurl").getAsString();
        if (jsonObject.keySet().contains("Invoiceurl"))Invoiceurl=jsonObject.get("Invoiceurl").getAsString();
        if (jsonObject.keySet().contains("newkeyurl"))newkeyurl=jsonObject.get("newkeyurl").getAsString();
        if (jsonObject.keySet().contains("vehiclelicense"))vehiclelicense=jsonObject.get("vehiclelicense").getAsString();
        if (jsonObject.keySet().contains("oldkeyurl"))oldkeyurl=jsonObject.get("oldkeyurl").getAsString();
        if (jsonObject.keySet().contains("keypayurl"))keypayurl=jsonObject.get("keypayurl").getAsString();
        if (jsonObject.keySet().contains("PMPurl"))PMPurl=jsonObject.get("PMPurl").getAsString();
        if (jsonObject.keySet().contains("inputretailprice"))inputretailprice=nullToZero( jsonObject.get("inputretailprice").getAsString());else inputretailprice=0;
        if (jsonObject.keySet().contains("originInvoicedate"))originInvoicedate=jsonObject.get("originInvoicedate").getAsString();
        if (jsonObject.keySet().contains("carstarttime"))carstarttime=jsonObject.get("carstarttime").getAsString();

        if (jsonObject.keySet().contains("sale_person_tel"))sale_person_tel= jsonObject.get("sale_person_tel").getAsString();else sale_person_tel="";
        if (jsonObject.keySet().contains("sale_person"))sale_person=jsonObject.get("sale_person").getAsString();else sale_person="";

        //sale_person_tel,sale_person

        if (vehicletype.equals("")&&!cars.equals(""))vehicletype=cars;
        String sql ="";
        int tt=getCurrenntTime();
        float agentprice=0,retailprice=0,cost=0;
        float agentprice1=0,retailprice1=0,cost1=0;
        Object []queryList;
        if (contractno.equals("")) {
            contractno = getContractno(dealerno,brand);
            sql="insert into mt_contract (contractno,ttime,valid,status,dealerno,userid) values (?,"+tt+",0,'草稿',?,?)";
            queryList=new Object[3];
            queryList[0]=contractno;
            queryList[1]=dealerno;
            queryList[2]=userid;
            jdbcTemplate.update(sql,queryList);
        }
        String pname="";
        sql="delete from contractdetail where contractno=?";
        jdbcTemplate.update(sql,contractno);
        sql="select * from mt_product where  productid ='"+productid+"'";
        List<Map<String,Object>>list;
        float insuranceamount=0;

        Map<String,Object>map=queryForMap(jdbcTemplate,sql);

      if (map!=null)
        {
            cost1=0;
            icode=map.get("icode")+"";
            sql="select agentprice,retailprice from daimlerproduct where  productid=?  and (price1<? ) and (price2>=?)";

            queryList=new Object[3];
            queryList[0]=productid;
            queryList[1]=Invoiceprice;
            queryList[2]=Invoiceprice;
            Map<String,Object>map2=queryForMap(jdbcTemplate,sql,queryList);
            if (map2!=null)
            {
                agentprice1+=setting.NullToZero(map2.get("agentprice")+"");
                retailprice1+=setting.NullToZero(map2.get("retailprice")+"");
            }


            agentprice+=agentprice1;
            retailprice+=retailprice1;
            cost+=cost1;
            pname+=setting.NUllToSpace(map.get("pname"))+" ";
        }
//        agentprice = (float) Math.round(agentprice * 100) / 100;
//        retailprice = (float) Math.round(retailprice * 100) / 100;


        sql="update mt_contract set cname=?,IdNo=?,mobile=?,email=?,address=?,brand=?,cars=?,vehicletype=?,vin=?,guideprice=? ";
        sql+=",Invoiceno=?,Invoiceprice=?,Invoicedate=?,isloan=?,Businessinsurancepolicyno=?,insurancecompany=?,begindate=?,enddate=?,insuranceTypes=?,insuranceForm=?,productid=?";

        sql+=" ,remark=?,icode=?,pname=?,agentprice=?,retailprice=?,cost=?,valid=1,companyname=?,iscompany=?,forceno=?,mactiveid=?,mileage=?,contact_person=?,contact_tel=?,isnewcar=?,keytype=?,newkeyurl=?,vehiclelicense=?,oldkeyurl=?,keypayurl=?,PMPurl=?,originInvoicedate=?,insuranceurl=?,Invoiceurl=? ";
        sql+=",insuranceamount=?,appid=?,sale_person_tel=?,sale_person=?";
        if (!carstarttime.equals(""))sql+=",carstarttime='"+carstarttime+"'";
        sql+="  where contractno=? ";

        queryList=new Object[49];
        queryList[0]=cname;
        queryList[1]=IdNo;
        queryList[2]=mobile;
        queryList[3]=email;
        queryList[4]=address;
        queryList[5]=brand;
        queryList[6]=cars;
        queryList[7]=vehicletype;
        queryList[8]=vin;
        queryList[9]=guideprice;
        queryList[10]=Invoiceno;
        queryList[11]=Invoiceprice;
        queryList[12]=Invoicedate;
        queryList[13]=isloan;
        queryList[14]=Businessinsurancepolicyno;
        queryList[15]=insurancecompany;
        queryList[16]=begindate;
        queryList[17]=enddate;
        queryList[18]=insuranceTypes;
        queryList[19]=insuranceForm;
        queryList[20]=productid;
        queryList[21]=remark;

        queryList[22]=icode;

        queryList[23]=pname;
        queryList[24]=agentprice;
        queryList[25]=retailprice;
        queryList[26]=cost;
        queryList[27]=companyname;
        queryList[28]=iscompany;
        queryList[29]=forceno;
        queryList[30]=mactiveid;
        queryList[31]=mileage;
        queryList[32]=contact_person;
        queryList[33]=contact_tel;
        queryList[34]=isnewcar;
        queryList[35]=keytype;
        //  ,newkeyurl=?,vehiclelicense=?,oldkeyurl=?,keypayurl=?
        queryList[36]=newkeyurl;
        queryList[37]=vehiclelicense;
        queryList[38]=oldkeyurl;
        queryList[39]=keypayurl;
        queryList[40]=PMPurl;
        queryList[41]=originInvoicedate;//insuranceurl="",Invoiceurl=""
        queryList[42]=insuranceurl;
        queryList[43]=Invoiceurl;
        queryList[44]=insuranceamount;

        queryList[45]=appid;
        queryList[46]=sale_person_tel;
        queryList[47]=sale_person;
        queryList[48]=contractno;
        //  marketingamount=retailprice-insuranceamount-tpaamount-brandamount-taxamount;
        String resultss="";


        try

        {
            if (jdbcTemplate.update(sql,queryList)==1)resultss="{\"errcode\":\"0\",\"agentprice\":\""+agentprice+"\",\"retailprice\":\""+retailprice+"\",\"contractno\":\""+contractno+"\"}";
            else resultss=GetErrorString(3,"提交不成功！");
        }catch (Exception e1)
        {
            resultss=e1.toString();
        }

        return  resultss;
    }

    private String getContractno(String dealerno,String brand)
    {
        String resultss="";
        String sqlstring="";
        Map<String,Object>map;

        while (resultss.equals(""))
        {
            //  Random r = new Random(1);
            String ss =  RandomUtils.nextInt(1,100000000)+"";
            for (int i=ss.length();i<8;i++)
            {
                ss="0"+ss;
            }

             resultss="MT"+dealerno+ss;

            sqlstring="select contractno from mt_contract where contractno=?";
            map=queryForMap(jdbcTemplate,sqlstring,resultss);
            if (map!=null)resultss="";
        }

        return resultss;
    }
    public String vincheck(String data)
    {
        String dealerno="",vin="";
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

        if (jsonObject.keySet().contains("dealerno"))dealerno=jsonObject.get("dealerno").getAsString();else return  GetErrorString(1,"");
        if (jsonObject.keySet().contains("vin"))vin=jsonObject.get("vin").getAsString();else return  GetErrorString(1,"");
        String sql ="select vin from mt_contract where  dealerno=? and vin=? and status!='草稿'";
        Object[] args=new Object[2];
        args[0]=dealerno;
        args[1]=vin;
        String resultss="";
        Map<String,Object>map=queryForMap(jdbcTemplate,sql,args);
        if (map!=null)
        {
            resultss = GetErrorString(3, "此VIN已录入过，请确认是否重复录入。");
        }
        else  resultss = GetErrorString(0, "");

        return  resultss;
    }
    public String uploadinsurance(String data, MultipartFile file){
        String contractno="",brand="";
        String insuranceurl="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="",userid="",dealerno="";
        int multi=0;
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;


        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();
        if (jsonObject.keySet().contains("userid"))userid=jsonObject.get("userid").getAsString();
        if (jsonObject.keySet().contains("dealerno"))dealerno=jsonObject.get("dealerno").getAsString();
        if (jsonObject.keySet().contains("brand"))brand=jsonObject.get("brand").getAsString();
        if (dealerno.equals("")&&contractno.equals(""))return  GetErrorString(1,"");
        String sql ="";
        Object[] queryList;
        int tt=setting.GetCurrenntTime();
        if (contractno.equals(""))
        {
            contractno=getContractno(dealerno,brand);
            sql="insert into mt_contract (contractno,ttime,valid,status,dealerno,userid) values (?,"+tt+",0,'草稿',?,?)";
            queryList=new Object[3];
            queryList[0]=contractno;
            queryList[1]=dealerno;
            queryList[2]=userid;
            jdbcTemplate.update(sql,queryList);
        }
        insuranceurl=uploadJpg(file);
        sql="update mt_contract set insuranceurl=? where contractno=?";
        queryList=new Object[2];
        queryList[0]=insuranceurl;
        queryList[1]=contractno;


        String resultss="";

        if (jdbcTemplate.update(sql,queryList)==1)resultss="{\"errcode\":\"0\",\"contractno\":\""+contractno+"\",\"insuranceurl\":\""+insuranceurl+"\"}";
        else resultss=GetErrorString(3,"提交不成功！");
        return  resultss;
    }
    public String uploadInvoice(String data, MultipartFile file){
        String contractno="",userid="",dealerno="",brand="";
        String Invoiceurl="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        int multi=0;
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;


        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();
        if (jsonObject.keySet().contains("userid"))userid=jsonObject.get("userid").getAsString();
        if (jsonObject.keySet().contains("dealerno"))dealerno=jsonObject.get("dealerno").getAsString();
        if (jsonObject.keySet().contains("brand"))brand=jsonObject.get("brand").getAsString();
        if (dealerno.equals("")&&contractno.equals(""))return  GetErrorString(1,"");
        String sql ="";
        int tt=getCurrenntTime();
        Object[]queryList;
        if (contractno.equals(""))
        {
            contractno=getContractno(dealerno,brand);
            sql="insert into mt_contract (contractno,ttime,valid,status,dealerno,userid) values (?,"+tt+",0,'草稿',?,?)";
            queryList=new Object[3];
            queryList[0]=contractno;
            queryList[1]=dealerno;
            queryList[2]=userid;
            jdbcTemplate.update(sql,queryList);
        }


        Invoiceurl=uploadJpg(file);



        sql="update mt_contract set Invoiceurl=? where contractno=?";
        queryList=new Object[2];
        queryList[0]=Invoiceurl;
        queryList[1]=contractno;

        String resultss="";

        if (jdbcTemplate.update(sql,queryList)==1)resultss="{\"errcode\":\"0\",\"contractno\":\""+contractno+"\",\"Invoiceurl\":\""+Invoiceurl+"\"}";
        else resultss=GetErrorString(3,"提交不成功！");
        return  resultss;
    }
    public String submitcontract(String data){
        String contractno="";
        String userid="";

        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        int multi=0;
        String result="",productid="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;


        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();else return  GetErrorString(1,"");
        if (jsonObject.keySet().contains("userid"))userid=jsonObject.get("userid").getAsString();

        contractno=contractno.trim();
        String sql ="";
        int tt=getCurrenntTime();
        String status="",dealerno="";
        String mactiveid="",isloan="",vehicletype="",originInvoicedate="";
        float agentprice=0,Invoiceprice=0;
        sql="select status,brand,agentprice,dealerno,mactiveid,vehicletype,isloan,Invoiceprice,productid,originInvoicedate,isnewcar from  mt_contract where contractno=?";
        String brand="",isnewcar="";

        Map<String ,Object>map=queryForMap(jdbcTemplate,sql,contractno);
        if (map!=null)
        {
            status=map.get("status")+"";
            brand=map.get("brand")+"";
            mactiveid=map.get("mactiveid")+"";
            vehicletype=map.get("vehicletype")+"";
            isloan=map.get("isloan")+"";
            dealerno=setting.NUllToSpace(map.get("dealerno"));
            agentprice=setting.NullToZero(map.get("agentprice")+"");
            Invoiceprice=setting.NullToZero(map.get("Invoiceprice")+"");
            productid=map.get("productid")+"";
            isnewcar=map.get("isnewcar")+"";
            originInvoicedate=map.get("originInvoicedate")+"";
        }else return  GetErrorString(3,"合同号不存在！");
        String newstatus="已打印";
        if (!status.equals("草稿"))return  GetErrorString(3,"合同已经提交，不能重复提交！");
        //判断产品是否已经下架，下架的产品不能提交
        //保时捷需要判断产品是否下架


        Object[] queryList;

         newstatus="待支付";//保时捷没有待支付状态
        if (agentprice==0) newstatus="已支付";




        sql="update  mt_contract set status=?,submittime=? where contractno=?";
        queryList=new Object[3];
        queryList[0]=newstatus;
        queryList[1]=GetNowDate("yyyy-MM-dd HH:mm:ss");
        queryList[2]=contractno;
        String resultss="";

        if (jdbcTemplate.update(sql,queryList)==1)resultss="{\"errcode\":\"0\",\"contractno\":\""+contractno+"\"}";
        else resultss=GetErrorString(3,"提交不成功！");
        return  resultss;
    }
    public String deletecontract(String data){
        String contractno="";
        String userid="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        int multi=0;
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;


        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();else return  GetErrorString(1,"");
        if (jsonObject.keySet().contains("userid"))userid=jsonObject.get("userid").getAsString();
        String sql ="",dealerno="",status="";


        sql="select status,dealerno,cost from  mt_contract where contractno=? and valid=1";
        Map<String ,Object>map=queryForMap(jdbcTemplate,sql,contractno);
        if (map==null)return  GetErrorString(3,"合同号不存在！");
        else {
            status=map.get("status")+"";

        }
        if (!status.equals("草稿")&&!status.equals("待支付"))return  GetErrorString(3,"合同已经提交，不能删除！");
        sql="update  mt_contract set valid=0 where contractno=?";
        String resultss="";
        if (jdbcTemplate.update(sql,contractno)==1)resultss="{\"errcode\":\"0\",\"contractno\":\""+contractno+"\"}";
        else resultss=GetErrorString(3,"提交不成功！");
        return  resultss;
    }
    public String contract_down(String data)
    {
        int time1=0,time2=0,paytime1=0,paytime2=0,submittime1=0,submittime2=0;
        String dealerno="",  dealername="",  icode="",  productname="",    status="",contractno="",productid="",dealernamefull="",act="",zone="";
        String vin="",claimstatus="",cname="",brand="",filetype="";
        String vehicletype="",cars="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        int  isnewcar=1;
        int multi=0;
        String result="";

        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();

        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();

        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;

        String id="",uptime="";

        if (jsonObject.keySet().contains("filetype"))filetype=jsonObject.get("filetype").getAsString();else filetype="file";
        if (jsonObject.keySet().contains("dealerno"))dealerno=jsonObject.get("dealerno").getAsString();
        if (jsonObject.keySet().contains("dealername"))dealername=jsonObject.get("dealername").getAsString();
        if (jsonObject.keySet().contains("icode"))icode=jsonObject.get("icode").getAsString();
        if (jsonObject.keySet().contains("productname"))productname=jsonObject.get("productname").getAsString();
        if (jsonObject.keySet().contains("status"))status=jsonObject.get("status").getAsString();
        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();
        if (jsonObject.keySet().contains("vin"))vin=jsonObject.get("vin").getAsString();
        if (jsonObject.keySet().contains("claimstatus"))claimstatus=jsonObject.get("claimstatus").getAsString();
        if (jsonObject.keySet().contains("dealernamefull"))dealernamefull=jsonObject.get("dealernamefull").getAsString();
        if (jsonObject.keySet().contains("cname"))cname=jsonObject.get("cname").getAsString();
        if (jsonObject.keySet().contains("productid"))productid=jsonObject.get("productid").getAsString();
        if (jsonObject.keySet().contains("brand"))brand=jsonObject.get("brand").getAsString();
        if (jsonObject.keySet().contains("vehicletype"))vehicletype=jsonObject.get("vehicletype").getAsString();
        if (jsonObject.keySet().contains("cars"))cars=jsonObject.get("cars").getAsString();
        if (jsonObject.keySet().contains("act"))act=jsonObject.get("act").getAsString();
        if (jsonObject.keySet().contains("zone"))zone=jsonObject.get("zone").getAsString();
        if (jsonObject.keySet().contains("time1"))time1=jsonObject.get("time1").getAsInt();
        if (jsonObject.keySet().contains("time2"))time2=jsonObject.get("time2").getAsInt();
        if (jsonObject.keySet().contains("paytime1"))paytime1=jsonObject.get("paytime1").getAsInt();
        if (jsonObject.keySet().contains("paytime2"))paytime2=jsonObject.get("paytime2").getAsInt();
        if (jsonObject.keySet().contains("submittime1"))submittime1=jsonObject.get("submittime1").getAsInt();
        if (jsonObject.keySet().contains("submittime2"))submittime2=jsonObject.get("submittime2").getAsInt();
        String sql ="select (@i:=@i+1)   as   i,'' as contractenddate,'' as p1,'' as p2,'' as p3,'' as p4,'' as s1,'' as s2,'' as s3,'' as s4,'' as e1,'' as e2,'' as e3,'' as e4,u.username ,c.*,DATE_FORMAT(c.paytime,'%Y-%m-%d') as paytime1,DATE_FORMAT(c.paytime,'%Y-%m-%d')  as contractstrartdate, if(iscompany=1,'是','否') as iscompanys,if(isloan=1,'是','否') as isloans,FROM_UNIXTIME(c.ttime,'%Y-%m-%d %H:%i:%s') as createtime,d.dealername,d.bank,d.bankacount,v.brand as vbrand,v.code,i.groupname,i.disc,c.tradechanel,d.zone,d.province ";
        sql+=",(select remark from  contractremark where contractno=c.contractno  order by ttime desc limit 0,1 ) as contractremark";
        sql+=" ,DATE_FORMAT(c.submittime,'%Y-%m-%d') as submittime1,DATE_FORMAT(c.updatetime,'%Y-%m-%d') as updatetime1,DATE_FORMAT(c.canceltime,'%Y-%m-%d') as canceltime1,'' as ifdelay";
        sql+=",'' as keystrartdate,'' as keyenddate,i.rti,i.tire,i.carkey,i.detail,i.cars as i_cars from mt_contract c inner join  dealer d on c.dealerno=d.dealerno left outer join mt_vehicletype v on v.vehicletype=c.vehicletype left outer join mt_product i on i.productid=c.productid left outer join usermember u on c.userid=u.id";
        sql+=" ,(SELECT @i:=0) as i where c.valid=1 " ;
        sql += " and  c.status!='草稿' ";
        List<Object> queryList=new ArrayList<Object>();
        if (!dealerno.equals("")&&!dealerno.equals("zb")) {
            sql += " and c.dealerno=?";
            queryList.add(dealerno);
        }
      /*  if (!icode.equals("")) {
            sql += " and contract.icode=?";
            queryList.add(icode);
        }*/
        if (!vehicletype.equals("")) {
            sql += " and c.vehicletype=?";
            queryList.add(vehicletype);
        }
        if (!cars.equals("")) {
            sql += " and c.cars=?";
            queryList.add(cars);
        }
        if (!brand.equals("")) {
            sql += " and d.brand=?";
            queryList.add(brand);
        }
        if (!zone.equals("")) {
            sql += " and d.zone=?";
            queryList.add(zone);
        }
        if (!vin.equals("")) {
            sql += " and c.vin like  ?";
            queryList.add("%"+vin+"%");
        }
        if (!claimstatus.equals("")) {
            sql += " and c.claimstatus=?";
            queryList.add(claimstatus);
        }
        if (!cname.equals("")) {
            sql += " and c.cname like  ?";
            queryList.add("%"+cname+"%");
        }
        if (!productname.equals("")) {
            sql += " and c.pname like  ?";
            queryList.add("%"+productname+"%");
        }
        if (!status.equals("")) {
            if (status.equals("已审批"))
            {
                sql += " and (c.status='已批准' or c.status='已拒绝')";
            }else
            {
                sql += " and c.status=?";
                queryList.add(status);
            }

        }

        if (!productid.equals("")) {
            sql += " and c.productid like ?";
            queryList.add("%"+productid+"%");
        }
        if (!contractno.equals("")) {
            sql += " and c.contractno=?";
            queryList.add(contractno);
        }
        if (!dealername.equals("")) {
            sql += " and d.dealername like ?";
            queryList.add("%" + dealername + "%");
        }
        if (!dealernamefull.equals("")) {
            sql += " and d.dealername = ?";
            queryList.add(dealernamefull );
        }
        if (time1!=0) {
            sql += " and c.ttime>?";
            queryList.add(time1);
        }
        if (time2!=0) {
            sql += " and c.ttime<?";
            queryList.add(time2);
        }
        if (paytime1!=0)
        {
            sql+=" and c.paytime >? ";
            queryList.add(stampToDate("yyyy-MM-dd HH:mm:ss",paytime1));
        }
        if (paytime2!=0)
        {
            sql+=" and c.paytime <? ";
            queryList.add(stampToDate("yyyy-MM-dd HH:mm:ss",paytime2));
        }
        if (submittime1!=0)
        {

            sql+=" and c.submittime >?";
            queryList.add(stampToDate("yyyy-MM-dd HH:mm:ss",submittime1));

        }
        if (submittime2!=0)
        {

            sql+=" and c.submittime <?";
            queryList.add(stampToDate("yyyy-MM-dd HH:mm:ss",submittime2));
        }
        Map<String,Object> title=new LinkedHashMap<>();
        title.put("i","No. 序号");
        title.put("dealername","Dealer Name 经销商名称");
        title.put("dealerno","Dealer Code 经销商代码");
        title.put("contractno","Contract No. 服务凭证号");
        title.put("status","Contract Status 凭证状态");
        title.put("tradechanel","Payment method 支付方式");
        title.put("claimstatus","Claim Status 理赔状态");
        title.put("createtime","Creation Date 创建日期");
        if (!act.equals("审核员")&&!act.equals("普通管理员"))
        {
            title.put("cname","Customer Name 客户姓名");
            title.put("IdNo","ID No. 身份证号");
            title.put("mobile","Mobile 手机");
            title.put("email","Email 邮箱");
            title.put("address","Address 地址");
            title.put("vin","VIN");
        }

        title.put("vbrand","Brand 品牌");
        title.put("cars","Series 车系");
        title.put("vehicletype","Model 车型");
        title.put("code","Model Code车型代码");

        title.put("iscompanys","是否公司车辆 If Company Car");
        title.put("companyname","公司名称 Company Name");
        title.put("guideprice","MSRP 厂家指导价");
        title.put("Invoiceno","Invoice No. 购车发票号");
        title.put("Invoiceprice","Invoice Amount 发票金额");
        title.put("Invoicedate","Invoice Date 发票日期");
        title.put("isloans","If loaned 是否贷款");


        //Certificate start date
        title.put("Businessinsurancepolicyno","Commercial Ins. No. 商业保单号");
        title.put("insurancecompany","Ins. Company 保险公司");
        title.put("begindate","Ins. Start Date 商业保险起始日期");
        title.put("enddate","Ins. End Date 商业保险终止日期");
        title.put("insuranceTypes","Ins. Type 商业险种");
        title.put("insuranceForm","New/Renew 新保/续保");
        title.put("forceno","SALI No. 交强险保单号");
        if(!act.equals("保险公司"))
        {
            title.put("agentprice","Dealer Cost 产品价格");
            title.put("retailprice","Service MSRP 产品零售价");

        }

        title.put("pname","Service Type 产品类别");
        title.put("disc","Service Content 服务内容");
        title.put("keytype","KEY Type 钥匙类型");
        title.put("contractstrartdate","Contract start date 凭证开始日期");
        title.put("contractenddate","Contract end date 凭证结束日期");
        title.put("e3","骑手险结束日期");

        if(!act.equals("保险公司"))
        {
            title.put("username","User 提交人");
            title.put("refuseremark","Reject Reason 拒绝原因");
        }

        if (dealerno.equals("")||dealerno.equals("zb"))
        {
            title.put("contractremark","Remark 投保备注");
            title.put("cancelremark","Cancel Remark 取消备注");


        }
        Map<String,Object>productmap=null;

        title.put("submittime1","Submit date 提交日期");
        if(!act.equals("保险公司"))
        {
            title.put("ifdelay","提交是否超过30天");
        }
        title.put("paytime1","Payment date 支付日期");
        title.put("zone","Zone 区域");
        title.put("province","Province 省份");
        title.put("activityname","Market activity 市场活动");
        title.put("canceltime1","Cancel Time 取消时间");
        //,DATE_FORMAT(c.canceltime,'%Y-%m-%d') as canceltime1
        title.put("mileage","当前行驶里程");
        title.put("sale_person","销售人员姓名");
        title.put("sale_person_tel","销售人员手机");
        List<Map<String,Object>>list=queryForList(jdbcTemplate,sql,queryList.toArray());
        String pname="";
        for (int i=list.size()-1;i>-1;i--)
        {
            Map<String,Object>map=list.get(i);

            isnewcar=setting.StrToInt(map.get("isnewcar")+"");
            if (isnewcar==0)
            {
                map.put("neworusedcar","旧车");
            }else  if (isnewcar==1)
            {
                map.put("neworusedcar","新车");
            } else if (isnewcar==2)
            {
                map.put("neworusedcar","二手车");
            }
            pname=map.get("pname")+"";
            //现在都是2年有效期，以后要根据产品年限来控制
            //开始时间为支付时间
            map.put("contractenddate",addYear(setting.NUllToSpace(map.get("Invoicedate")),2));
            map.put("e3",addYear(setting.NUllToSpace(map.get("contractstrartdate")),2));
            if (BetweenDays2(map.get("Invoicedate")+"",map.get("submittime")+"")>30)map.put("ifdelay","是");
            else map.put("ifdelay","否");
        }



        title.put("remark","Remark 备注");

        if (filetype.toLowerCase().equals("file"))
            result= toexcel.createCSVFile(list,title,"","");
        else
        {
            Gson gson = new Gson();
            result = gson.toJson(list);
        }
        return  result;
    }


}
