package com.bond.allianz.Dao;

import com.bond.allianz.entity.PageInfo;
import com.bond.allianz.utils.GuidUtil;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class ContractDao extends BaseDao {
    @Value("${bill.autocheck}")
    public String  billautocheck;
    @Value("${domain.enter}")
    public String domainenter;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    BmwDao bmwDao;
    @Autowired
    QrCodeUtil qrCodeUtil;
    @Autowired
    private BmwPrintDao bmwPrintDao;

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
        String sql ="select u.username,u.username as userid,c.*, if(isloan=1,'是','否') as isloans,FROM_UNIXTIME(c.ttime,'%Y-%m-%d %H:%i:%s') as createtime,d.dealername,d.bank,d.bankacount,(select FROM_UNIXTIME(ttime,'%Y-%m-%d %H:%i:%s') from contractcheck where contractno=c.contractno and  checkcontent='合同提交'  order by ttime desc  limit 0,1 ) as checktime ,(select FROM_UNIXTIME(ttime,'%Y-%m-%d %H:%i:%s') from contractcheck where contractno=c.contractno and checkcontent='合同取消' order by ttime desc limit 0,1 ) as canceltime,(select remark from  contractcheck where contractno=c.contractno and  checkcontent='合同取消' order by ttime desc limit 0,1 ) as cancelremark ,(select remark from  contractremark where contractno=c.contractno order by ttime desc limit 0,1 ) as contractremark,v.brand as vbrand,v.code,i.disc,(select FROM_UNIXTIME(billbmw.ttime,'%Y-%m-%d %H:%i:%s') from billbmw where contractno=c.contractno and in_or_out=-1 and (tradetype='订单支付消费' or tradetype='支付') ORDER BY ttime desc LIMIT 0,1 ) as paytime,(select tradechanel from billbmw where contractno=c.contractno and in_or_out=-1 ORDER BY ttime desc LIMIT 0,1 ) as tradechanel ,(select activityname from marketactivity where tid=c.mactiveid LIMIT 0,1 ) as activityname,(select username from contractcheck INNER JOIN usermember u on u.id=contractcheck.userid where contractno=c.contractno and  checkcontent='合同取消' order by ttime desc limit 0,1 ) as cancelusername";
        sql+=",i.groupname as typename,(select FROM_UNIXTIME(ttime,'%Y-%m-%d') from  contractcheck where contractno=c.contractno and  checkcontent='合同提交' order by ttime desc limit 0,1 ) as submittime ";
        if (domainenter.equals("1"))//宝马添加轮胎、钥匙参数
        sql+=" ,ex.key_invoicedate,ex.key_invoiceno,ex.key_price,ex.tier_invoicedate,ex.tire_invoiceno,ex.tier_price,i.detail_productid,DATE_FORMAT(c.updateTime,'%Y-%m-%d %H:%i:%s') as updateTime1 ";
        sql+=" from contract c inner join dealer d on c.dealerno=d.dealerno left outer join vehicletype v on v.vehicletype=c.vehicletype left outer join insuranceproduct i on i.productid=c.productid left outer join usermember u on c.userid=u.id";
        if (domainenter.equals("1"))//宝马添加轮胎、钥匙参数
        sql+=" left join contract_extension ex on ex.contractno=c.contractno";
        sql+=" where c.valid=1 " ;
        String countsql,listsql;
        listsql=sql;
        sql="";
        countsql="select c.contractno from contract c inner join dealer d on c.dealerno=d.dealerno where c.valid=1 " ;
        List<Object> queryList=new ArrayList<Object>();
        if (!dealerno.equals("")&&!dealerno.equals("zb")) {
            sql += " and c.dealerno=?";
            queryList.add(dealerno);
        }else
        {
            sql += " and c.status!='草稿'";
            if (brand.equals("BMW")||brand.equals("MINI"))   sql += " and c.status!='待支付'";
        }

        if (!icode.equals("")) {
            sql += " and c.icode=?";
            queryList.add(icode);
        }
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
        if (appid.equals("ibsapi2022")) {
            //(select FROM_UNIXTIME(billbmw.ttime,'%Y-%m-%d %H:%i:%s') from billbmw where contractno=c.contractno and in_or_out=-1 and (tradetype='订单支付消费' or tradetype='支付') ORDER BY ttime desc LIMIT 0,1 ) as paytime
           //加入支付时间的条件
            sql += " and (c.ttime>=1672502400 or c.contractno in (select contractno from billbmw where ttime>=1672502400 and contractno is not null))";
            //ibs开放2023年1月1日后的单子
        }
        if (!updatetime2.equals("")) {
            sql += " and c.updatetime<?";
            queryList.add(updatetime2);
        }
        if (paytime1!=0)
        {
            sql+=" and c.contractno in (select contractno from billbmw where ttime>? and contractno is not null)";
            queryList.add(paytime1);
        }
        if (paytime2!=0)
        {
            sql+=" and c.contractno in (select contractno from billbmw where ttime<? and contractno is not null)";
            queryList.add(paytime2);
        }
        if (submittime1!=0)
        {

            sql+=" and c.contractno in (select contractno from contractcheck where ttime>? and  checkcontent='合同提交' )";
            queryList.add(submittime1);
        }
        if (submittime2!=0)
        {

            sql+=" and c.contractno in (select contractno from contractcheck where ttime<? and  checkcontent='合同提交' )";
            queryList.add(submittime2);
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
        try {
            String     newsqlstring=" select count(*) as sl from ( "+countsql+sql+" )  aaa";
            // newsqlstring=" select count(*) as sl from ( "+sqlstring+" )  aaa";
            Map<String, Object>map=queryForMap(jdbcTemplate,newsqlstring,queryList.toArray());
            int count=0;
            if (map!=null)
                count=setting.StrToInt(map.get("sl").toString());
            // logs.WriteLog(count+"","GetResultString1");
            int i0 = (pageindex - 1) * pagesize;
            int i1 = pageindex * pagesize;
            sql=listsql+sql;
            sql+=" limit "+i0+","+pagesize;
            List<Map<String, Object>> list1 = queryForList(jdbcTemplate, sql,queryList.toArray());

            Gson gson = new Gson();
            result = gson.toJson(list1);
            if(list1!=null)list1.clear();
            resultss = "{\"recordcount\":" + count + ",\"pageno\":" + pageindex + ",\"pagesize\":" + pagesize + ",\"record\":" + result + "}";
        }
        catch (Exception ex)
        {
            logs.WriteLog(ex.toString(),"GetResultString1");
        }

      //  resultss=GetResultString1(sql,jdbcTemplate,pageindex,pagesize,queryList.toArray());
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
        sql ="select contract.*,FROM_UNIXTIME(contract.ttime,'%Y-%m-%d %H:%i:%s') as createtime,dealer.dealername,dealer.bank,dealer.bankacount,(select FROM_UNIXTIME(ttime,'%Y-%m-%d %H:%i:%s') from  contractcheck where contractno=contract.contractno and  checkcontent='合同审核'  order by ttime desc  limit 0,1 ) as checktime ,(select FROM_UNIXTIME(ttime,'%Y-%m-%d %H:%i:%s') from  contractcheck where contractno=contract.contractno and  checkcontent='合同取消' order by ttime desc limit 0,1 ) as canceltime,(select remark from  contractcheck where contractno=contract.contractno and  checkcontent='合同取消' order by ttime desc limit 0,1 ) as cancelremark ,(select remark from  contractremark where contractno=contract.contractno  order by ttime desc limit 0,1 ) as contractremark from billdetail inner join contract on contract.contractno=billdetail.contractno inner join  dealer on contract.dealerno=dealer.dealerno";
        sql+=" where contract.valid=1 and  (billdetail.billno='' or billdetail.billno is null)" ;
           List<Object> queryList=new ArrayList<Object>();
        if (!dealerno.equals("")&&!dealerno.equals("zb")) {
            sql += " and contract.dealerno=?";
            queryList.add(dealerno);
        }else
        {
            sql += " and contract.status!='草稿' ";
            if (brand.equals("BMW"))   sql += " and  contract.status!='待支付'";
        }

        if (!icode.equals("")) {
            sql += " and contract.icode=?";
            queryList.add(icode);
        }
        if (!brand.equals("")) {
            sql += " and contract.brand=?";
            queryList.add(brand);
        }
        if (!vin.equals("")) {
            sql += " and contract.vin like  ?";
            queryList.add("%"+vin+"%");
        }
        if (!claimstatus.equals("")) {
            sql += " and contract.claimstatus=?";
            queryList.add(claimstatus);
        }
        if (!cname.equals("")) {
            sql += " and contract.cname like  ?";
            queryList.add("%"+cname+"%");
        }
        if (!productname.equals("")) {
            sql += " and contract.pname like  ?";
            queryList.add("%"+productname+"%");
        }
        if (!status.equals("")) {
            if (status.equals("已审批"))
            {
                sql += " and (contract.status='已批准' or contract.status='已拒绝')";
            }else if (status.equals("报告"))
            {
                sql += " and contract.status!='草稿'";

            }else
            {
                sql += " and contract.status=?";
                queryList.add(status);
            }

        }

        if (!productid.equals("")) {
            sql += " and contract.productid like ?";
            queryList.add("%"+productid+"%");
        }
        if (!contractno.equals("")) {
            sql += " and contract.contractno=?";
            queryList.add(contractno);
        }
        if (!dealername.equals("")) {
            sql += " and dealer.dealername like ?";
            queryList.add("%" + dealername + "%");
        }
        if (!dealernamefull.equals("")) {
            sql += " and dealer.dealername =?";
            queryList.add(dealernamefull );
        }
        if (time1!=0) {
            sql += " and contract.ttime>?";
            queryList.add(time1);
        }
        if (time2!=0) {
            sql += " and contract.ttime<?";
            queryList.add(time2);
        }
       sql+=" and contract.status!='待支付'";
        sql+=" order by contract.ttime desc";
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
        String key_invoiceno="",key_invoicedate="",tire_invoiceno="",tier_invoicedate="";
        double key_price=0,tier_price=0;
        String sale_person_tel="",sale_person="",activity_id="",active_productid="",active_subject="",active_url="";
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

        if (jsonObject.keySet().contains("key_invoiceno"))key_invoiceno=jsonObject.get("key_invoiceno").getAsString();
        if (jsonObject.keySet().contains("key_invoicedate"))key_invoicedate=jsonObject.get("key_invoicedate").getAsString();
        if (jsonObject.keySet().contains("tire_invoiceno"))tire_invoiceno=jsonObject.get("tire_invoiceno").getAsString();
        if (jsonObject.keySet().contains("tier_invoicedate"))tier_invoicedate=jsonObject.get("tier_invoicedate").getAsString();
        if (jsonObject.keySet().contains("key_price"))key_price=nullToZero( jsonObject.get("key_price").getAsString());else key_price=0;
        if (jsonObject.keySet().contains("tier_price"))tier_price=nullToZero( jsonObject.get("tier_price").getAsString());else tier_price=0;
        if (jsonObject.keySet().contains("sale_person_tel"))sale_person_tel= jsonObject.get("sale_person_tel").getAsString();else sale_person_tel="";
        if (jsonObject.keySet().contains("sale_person"))sale_person=jsonObject.get("sale_person").getAsString();else sale_person="";
        if (jsonObject.keySet().contains("activity_id"))activity_id=jsonObject.get("activity_id").getAsString();else activity_id="";
        if (jsonObject.keySet().contains("active_productid"))active_productid=jsonObject.get("active_productid").getAsString();else active_productid="";
        if (jsonObject.keySet().contains("active_subject"))active_subject=jsonObject.get("active_subject").getAsString();else active_subject="";
        if (jsonObject.keySet().contains("active_url"))active_url=jsonObject.get("active_url").getAsString();else active_url="";


        //BMW07 旧车钥匙  BMW09 售后新换胎 根据产品切换参数
        if (!productid.startsWith("BMW07"))
        {
            key_invoiceno="";key_invoicedate="";key_price=0;
        }
        if (!productid.startsWith("BMW09")&&!productid.startsWith("BMW10"))
        {
            tire_invoiceno="";tier_invoicedate="";tier_price=0;
        }
        double paid_amount=0,paid_amount_rti=0,paid_amount_key=0,paid_amount_tire=0,paid_amount_group=0;
        double key_amount=0,tire_amount=0,rti_amount=0,group_amount=0;
        if (jsonObject.keySet().contains("paid_amount"))paid_amount=nullToZero( jsonObject.get("paid_amount").getAsString());else paid_amount=0;
        if (jsonObject.keySet().contains("paid_amount_rti"))paid_amount_rti=nullToZero( jsonObject.get("paid_amount_rti").getAsString());else paid_amount_rti=0;
        if (jsonObject.keySet().contains("paid_amount_key"))paid_amount_key=nullToZero( jsonObject.get("paid_amount_key").getAsString());else paid_amount_key=0;
        if (jsonObject.keySet().contains("paid_amount_tire"))paid_amount_tire=nullToZero( jsonObject.get("paid_amount_tire").getAsString());else paid_amount_tire=0;
        if (jsonObject.keySet().contains("paid_amount_group"))paid_amount_group=nullToZero( jsonObject.get("paid_amount_group").getAsString());else paid_amount_group=0;

        //sale_person_tel,sale_person

        if (vehicletype.equals("")&&!cars.equals(""))vehicletype=cars;
        String sql ="";
        int tt=getCurrenntTime();
        float agentprice=0,retailprice=0,cost=0;
        float agentprice1=0,retailprice1=0,cost1=0;
        Object []queryList;
        if (contractno.equals("")) {
            contractno = getContractno(dealerno,brand);
            sql="insert into contract (contractno,ttime,valid,status,dealerno,userid) values (?,"+tt+",0,'草稿',?,?)";
            queryList=new Object[3];
            queryList[0]=contractno;
            queryList[1]=dealerno;
            queryList[2]=userid;
            jdbcTemplate.update(sql,queryList);
        }
        String pname="";
        sql="delete from contractdetail where contractno=?";
        jdbcTemplate.update(sql,contractno);
        sql="select * from insuranceproduct where  productid in ('"+productid.replace(",","','")+"')";
        List<Map<String,Object>>list=queryForList(jdbcTemplate,sql);
        float taxrate=0,brandrate=0,tparate=0,insurancerate=0,insuranceamount=0;
        float tpaamount=0,marketingamount=0,brandamount=0,taxamount=0;
        Map<String,Object>map;
        String detail_productid="";
        String old_active_productid=active_productid;
        for (int i=0;i<list.size();i++)
        {
          cost1=0;
           map=list.get(i);
            detail_productid="";
            taxrate=  setting.NullToZero(map.get("taxrate")+"");
            brandrate=  setting.NullToZero(map.get("brandrate")+"");
            tparate=  setting.NullToZero(map.get("tparate")+"");
            insurancerate=  setting.NullToZero(map.get("insurancerate")+"");
            insuranceamount=  setting.NullToZero(map.get("insuranceamount")+"");
            icode=map.get("icode")+"";
            String pcars=setting.NUllToSpace(map.get("cars")+"");

            if (!pcars.equals("售后新换胎"))
            {
                tire_invoiceno="";tier_invoicedate="";tier_price=0;
            }
           if (domainenter.equals("0"))
           {


               agentprice1=(Invoiceprice*setting.NullToZero(map.get("carcostrate")+"")+setting.NullToZero(map.get("agentprice")+""))*setting.NullToZero(map.get("agentdiscount")+"")+setting.NullToZero(map.get("tpa")+"");
              // retailprice1=agentprice1*setting.NullToZero(map.get("retaildiscount")+"");
               retailprice1=(float)Math.ceil(setting.NullToZero(map.get("tpa")+"")*setting.NullToZero(map.get("retaildiscount")+"")/10)*10;
               retailprice1+=(float)Math.ceil(Invoiceprice*setting.NullToZero(map.get("carcostrate")+"")*setting.NullToZero(map.get("agentdiscount")+"")*setting.NullToZero(map.get("retaildiscount")+"")/10)*10;
               retailprice1+=(float)Math.ceil(setting.NullToZero(map.get("agentprice")+"")*setting.NullToZero(map.get("agentdiscount")+"")*setting.NullToZero(map.get("retaildiscount")+"")/10)*10;
                String  pid=map.get("productid")+"";
               if (pid.startsWith("PS001") )//基础套餐赠送//赠送套餐
               {
                   cost1=agentprice1;
                   agentprice1=0;
                   retailprice1=0;
               }else if  (pid.equals("PS00241")||pid.equals("PS00242")||pid.equals("PS00243")||pid.equals("PS00251")||pid.equals("PS00253")||pid.equals("PS00263"))
               {
                   retailprice1+=10;
               }

           }else    if  (domainenter.equals("1")||(productid.equals("Mazda01")))//(brand.equals("BMW")||brand.equals("MINI"))
           {
               String groupname=map.get("groupname")+"";
               Map<String,Object>map2;
               detail_productid= NUllToSpace(map.get("detail_productid"));
               if (active_productid.equals(productid)&&!detail_productid.equals(""))active_productid=detail_productid;
               retailprice1+=setting.NullToZero(map.get("retailprice")+"");
               if (activity_id.equals(""))
               {
                   agentprice1+=setting.NullToZero(map.get("agentprice")+"");


               }
              else
               {

                   String[]detail_productids=detail_productid.split(",");
                   if (detail_productid.equals(""))detail_productids=productid.split(",");
                   for (int k=0;k<detail_productids.length;k++)
                   {
                       if (detail_productids[k].equals("BMW01")||detail_productids[k].equals(""))continue;
                       if (!active_productid.contains(detail_productids[k]))
                       {
                           sql="select agentprice,retailprice from insuranceproduct where  productid=?  ";
                           queryList=new Object[1];
                           queryList[0]=detail_productids[k];
                       }else
                       {
                           sql="select agentprice,retailprice from bmwproduct where  productid=?  and  ifnull(activity_id,'')=?";
                           queryList=new Object[2];
                           queryList[0]=detail_productids[k];
                           queryList[1]=activity_id;
                           active_productid=active_productid.substring(0,active_productid.indexOf(detail_productids[k]))+active_productid.substring(active_productid.indexOf(detail_productids[k])+detail_productids[k].length());
                       }
                       map2=queryForMap(jdbcTemplate,sql,queryList);
                       if (map2!=null)
                       {

                           agentprice1+=setting.NullToZero(map2.get("agentprice")+"");

                       }

                   }

               }


               if (groupname.equals("钥匙"))key_amount=retailprice1;
               else if (groupname.equals("轮胎"))tire_amount=retailprice1;
               else if (groupname.equals("常用套餐"))group_amount=retailprice1;
               else if (groupname.equals("临时套餐"))
               {
                   sql="select * from insuranceproduct where  productid in ('"+detail_productid.replace(",","','")+"')";
                   List<Map<String,Object>>list1=queryForList(jdbcTemplate,sql);
                   for (int ii=0;ii<list1.size();ii++)
                   {
                       map2=list1.get(ii);
                       if ((map2.get("groupname")+"").equals("钥匙"))key_amount=setting.NullToZero(map2.get("retailprice")+"");
                       else if ((map2.get("groupname")+"").equals("轮胎"))tire_amount=setting.NullToZero(map2.get("retailprice")+"");
                       else if ((map2.get("groupname")+"").equals("常用套餐"))group_amount=setting.NullToZero(map2.get("retailprice")+"");

                   }
               }
//               sql="select agentprice,retailprice from bmwproduct where  productid=?  and (cars=? or cars ='ALL') and (vehicletype=? or vehicletype ='ALL')";
//               queryList=new Object[3];
//               queryList[0]=productid;
//               queryList[1]=cars;
//               if (!productid.equals("BMW01")&&!productid.equals("Mazda01")) {
//                   if(isnewcar.equals("0")) queryList[1]="OLD";else queryList[1]="NEW";
//               }
//               queryList[2]=vehicletype;
//               map2=queryForMap(jdbcTemplate,sql,queryList);
//               if (map2!=null)
//               {
//                   if (!isnewcar.equals("2"))//二手车产品另外的逻辑
//                   {
//                       agentprice1+=setting.NullToZero(map2.get("agentprice")+"");
//                       retailprice1+=setting.NullToZero(map2.get("retailprice")+"");
//                   }
//
//               }
//               //0 旧车 1 新车 2 二手车
                if (productid.equals("BMW01-3")&&isnewcar.equals("2")) {

                   sql="select agentprice,retailprice from usedcarproduct where  productid=?  and (price1<=? ) and (price2>?) and  ifnull(activity_id,'')=?";

                   queryList=new Object[4];
                   queryList[0]=productid;
                   queryList[1]=Invoiceprice;
                   queryList[2]=Invoiceprice;
                    queryList[3]=activity_id;
                   map2=queryForMap(jdbcTemplate,sql,queryList);
                   if (map2!=null)
                   {
                       agentprice1+=setting.NullToZero(map2.get("agentprice")+"");
                       retailprice1+=setting.NullToZero(map2.get("retailprice")+"");
                   }
               }
                //BMW01-3走另外的逻辑
              if (productid.equals("BMW01")||productid.startsWith("BMW04")||detail_productid.startsWith("BMW01")||detail_productid.startsWith("BMW04"))//组合套餐加上rti的价格
              {
                  String productid_rti="";
                  if (productid.equals("BMW01")||productid.startsWith("BMW04"))productid_rti=productid;
                  String[]detail_productids=detail_productid.split(",");
                  for (int k=0;k<detail_productids.length;k++)
                  {
                      if (detail_productids[k].equals("BMW01")||detail_productids[k].startsWith("BMW04"))productid_rti=detail_productids[k];
                  }


                  sql="select agentprice,retailprice from bmwproduct where  productid=?  and (cars=? ) and (vehicletype=? ) and  ifnull(activity_id,'')=?";
                  queryList=new Object[4];
                  queryList[0]="BMW01";
                  queryList[1]=cars;
                  queryList[2]=vehicletype;
                  if (!active_productid.contains(productid_rti))
                  queryList[3]="";
                  else
                      queryList[3]=activity_id;
                  map2=queryForMap(jdbcTemplate,sql,queryList);
                  if (map2!=null)
                  {
                      agentprice1+=setting.NullToZero(map2.get("agentprice")+"");
                      retailprice1+=setting.NullToZero(map2.get("retailprice")+"");
                      rti_amount=setting.NullToZero(map2.get("retailprice")+"");
                      //插入子产品的数据
                      if (!detail_productid.equals(""))
                      {
                          if (!active_productid.contains(productid_rti))
                              sql="insert into contractdetail (contractno,productid,agentprice,retailprice,cost) values (?,?,?,?,?)";
                          else sql="insert into contractdetail (contractno,productid,agentprice,retailprice,cost,activity_id) values (?,?,?,?,?,"+activity_id+")";
                          queryList=new Object[5];
                          queryList[0]=contractno;
                          queryList[1]=productid_rti;
                          queryList[2]=setting.NullToZero(map2.get("agentprice")+"");
                          queryList[3]=setting.NullToZero(map2.get("retailprice")+"");
                          queryList[4]=0;
                          jdbcTemplate.update(sql,queryList);
                      }
                  }else
                  {
                      sql="select agentprice,retailprice from bmwproduct where  productid=?  and (cars=? ) and (vehicletype=? ) and  ifnull(activity_id,'')=''";
                      queryList=new Object[3];
                      queryList[0]="BMW01";
                      queryList[1]=cars;
                      queryList[2]=vehicletype;
                      map2=queryForMap(jdbcTemplate,sql,queryList);
                      if (map2!=null) {
                          retailprice1 += setting.NullToZero(map2.get("retailprice") + "");
                          rti_amount = setting.NullToZero(map2.get("retailprice") + "");
                      }
                  }

              }
           }
           else    if  (domainenter.equals("2"))
           {
               sql="select agentprice,retailprice from daimlerproduct where  productid=?  and (price1<=? ) and (price2>?)";

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

           }
          //  if  ( (brand.equals("一汽红旗")&&productid.equals("HQ01"))|| (brand.equals("一汽奔腾")&&productid.equals("BT01"))|| (brand.equals("长安欧尚")&&productid.equals("OS01")))
          if ((domainenter.equals("3")&&!productid.equals("Mazda01")))
            {
                sql="select agentprice,retailprice from daimlerproduct where  productid=?  and (price1<=? ) and (price2>?)";

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

            }
          //改成插入明细产品2024-03-05
            if (detail_productid.equals(""))
            {
                if (activity_id.equals(""))
                sql="insert into contractdetail (contractno,productid,agentprice,retailprice,cost) values (?,?,?,?,?)";
                else sql="insert into contractdetail (contractno,productid,agentprice,retailprice,cost,activity_id) values (?,?,?,?,?,"+activity_id+")";
                queryList=new Object[5];
                queryList[0]=contractno;
                queryList[1]=setting.NUllToSpace(map.get("productid"));
                queryList[2]=agentprice1;
                queryList[3]=retailprice1;
                queryList[4]=cost1;
                jdbcTemplate.update(sql,queryList);
            }else
            {
                sql="select * from insuranceproduct where  productid in ('"+detail_productid.replace(",","','")+"')";
                List<Map<String,Object>>list1=queryForList(jdbcTemplate,sql);
                for (int ii=0;ii<list1.size();ii++)
                {
                 Map<String,Object>   map2=list1.get(ii);
                    String productid_son="";
                    productid_son=map2.get("productid")+"";
                    if (productid_son.equals("BMW01")||productid_son.startsWith("BMW04"))continue;
                    if (!active_productid.contains(productid_son))
                        sql="insert into contractdetail (contractno,productid,agentprice,retailprice,cost) values (?,?,?,?,?)";
                    else sql="insert into contractdetail (contractno,productid,agentprice,retailprice,cost,activity_id) values (?,?,?,?,?,"+activity_id+")";
                    queryList=new Object[5];
                    queryList[0]=contractno;
                    queryList[1]=productid_son;
                    queryList[2]=setting.NullToZero(map2.get("agentprice")+"");
                    queryList[3]=setting.NullToZero(map2.get("retailprice")+"");
                    queryList[4]=0;
                    jdbcTemplate.update(sql,queryList);

                }
            }

            agentprice+=agentprice1;
            retailprice+=retailprice1;
            cost+=cost1;
            pname+=setting.NUllToSpace(map.get("pname"))+" ";
        }

        if (productid.equals("BMW03")) {
            keytype = "标准钥匙1年期1把";
                   }else if (!productid.startsWith("BMW06")&&keytype.equals("")&&pname.contains("钥匙"))
        {
            keytype=pname;
        }
       /* if (pname.contains("+")) {
            keytype = pname.substring(pname.indexOf("+") + 1);

        }*/
        agentprice = (float) Math.round(agentprice * 100) / 100;
        if (domainenter.equals("0"))//保时捷向上取整，保时捷成本取成本字段
           // retailprice=(float)Math.ceil(retailprice/10)*10;//向上取整
            retailprice=0;
        else
            retailprice = (float) Math.round(retailprice * 100) / 100;
        if (inputretailprice!=0&& (domainenter.equals("0")||domainenter.equals("2")))retailprice=inputretailprice;
        if (domainenter.equals("0")||domainenter.equals("1")||domainenter.equals("2")||domainenter.equals("3"))//(brand.equals("BMW")||brand.equals("MINI"))
        {
            marketactive ma=new marketactive();
            if (mactiveid!=0)//判断是否符合市场活动
            {
                ma=isfitmarketactivid(mactiveid+"",vehicletype,isloan+"",productid);
                if (!ma.valid)return  GetErrorString(3,"该合同不符合市场活动要求，不能提交！");
                else
                {
                    if (ma.atype==0)
                    {
                        cost=agentprice*(ma.amount/100);
                        agentprice=agentprice*(1-ma.amount/100);
                    }else if (ma.atype==1) {
                        cost=ma.amount;
                        if (cost>agentprice)cost=agentprice;
                        agentprice = agentprice - ma.amount;
                    }
                    if (agentprice<0)agentprice=0;
                }
            }

        }
   //计算各个费用
        insuranceamount = (float) Math.round((insuranceamount+Invoiceprice * insurancerate)*100)/100;
        tpaamount= (float) Math.round(agentprice * tparate*100)/100;
        brandamount= (float) Math.round(agentprice *brandrate*100)/100;
        taxamount= (float) Math.round(agentprice *taxrate*100)/100;
        marketingamount=agentprice-insuranceamount-tpaamount-brandamount-taxamount;
        if (group_amount!=0)
        {
            group_amount+=rti_amount;
            rti_amount=0;
            key_amount=0;
        }

        sql="update contract set cname=?,IdNo=?,mobile=?,email=?,address=?,brand=?,cars=?,vehicletype=?,vin=?,guideprice=? ";
            sql+=",Invoiceno=?,Invoiceprice=?,Invoicedate=?,isloan=?,Businessinsurancepolicyno=?,insurancecompany=?,begindate=?,enddate=?,insuranceTypes=?,insuranceForm=?,productid=?";

            sql+=" ,remark=?,icode=?,pname=?,agentprice=?,retailprice=?,cost=?,valid=1,companyname=?,iscompany=?,forceno=?,mactiveid=?,mileage=?,contact_person=?,contact_tel=?,isnewcar=?,keytype=?,newkeyurl=?,vehiclelicense=?,oldkeyurl=?,keypayurl=?,PMPurl=?,originInvoicedate=?,insuranceurl=?,Invoiceurl=? ";
            sql+=" ,marketingamount=?,insuranceamount=?,tpaamount=?,brandamount=?,taxamount=?,appid=?";
            sql+=",sale_person_tel=?,sale_person=?";
            if (domainenter.equals("1")) {
                sql += ",paid_amount=" + paid_amount + ",paid_amount_rti=" + paid_amount_rti + ",paid_amount_key=" + paid_amount_key + ",paid_amount_tire=" + paid_amount_tire + ",paid_amount_group=" + paid_amount_group;
                sql+=",key_amount="+key_amount+",tire_amount="+tire_amount+",group_amount="+group_amount+",rti_amount="+rti_amount;
                sql+=",active_productid='"+old_active_productid+"'";
               if (!activity_id.equals("")) sql+=",activity_id="+activity_id;
               else
               {
                   String sqlstring="update contract set activity_id=? where contractno=?";
                   Object[] args=new Object[2];
                   args[0]=null;
                   args[1]=contractno;
                   jdbcTemplate.update(sqlstring,args);
               }
                sql+=",active_subject='"+active_subject+"'";
                sql+=",active_url='"+active_url+"' ";
            }
            if (!carstarttime.equals(""))sql+=",carstarttime='"+carstarttime+"'";

            sql+="  where contractno=? ";

       queryList=new Object[53];
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
        queryList[44]=marketingamount;
        queryList[45]=insuranceamount;
        queryList[46]=tpaamount;
        queryList[47]=brandamount;
        queryList[48]=taxamount;
        queryList[49]=appid;
        queryList[50]=sale_person_tel;
        queryList[51]=sale_person;
        queryList[52]=contractno;
        //  marketingamount=retailprice-insuranceamount-tpaamount-brandamount-taxamount;
        String resultss="";
        //选择常用套餐后，rti和钥匙的建议零售价就不需要

       try

       {
           if (jdbcTemplate.update(sql,queryList)==1)resultss="{\"errcode\":\"0\",\"agentprice\":\""+agentprice+"\",\"retailprice\":\""+retailprice+"\",\"contractno\":\""+contractno+"\",\"rti_amount\":\""+rti_amount+"\",\"key_amount\":\""+key_amount+"\",\"tire_amount\":\""+tire_amount+"\",\"group_amount\":\""+group_amount+"\"}";
           else resultss=GetErrorString(3,"提交不成功！");
       }catch (Exception e1)
       {
           resultss=e1.toString();
       }
        if  (domainenter.equals("1"))//宝马旧车钥匙或售后胎增加新的字段
        {
            if (!key_invoicedate.equals("")||!key_invoiceno.equals("")||key_price!=0||!tier_invoicedate.equals("")||!tire_invoiceno.equals("")||tier_price!=0)
            {
                sql="update contract_extension set key_invoicedate=?,key_invoiceno=?,key_price=?,tier_invoicedate=?,tire_invoiceno=?,tier_price=? where contractno=?";
                queryList=new Object[7];
                queryList[0]=key_invoicedate;
                queryList[1]=key_invoiceno;
                queryList[2]=key_price;
                queryList[3]=tier_invoicedate;
                queryList[4]=tire_invoiceno;
                queryList[5]=tier_price;
                queryList[6]=contractno;
                if (jdbcTemplate.update(sql,queryList)==0)
                {
                    sql="insert into contract_extension (key_invoicedate,key_invoiceno,key_price,tier_invoicedate,tire_invoiceno,tier_price,contractno)  values (?,?,?,?,?,?,?)";
                    jdbcTemplate.update(sql,queryList);
                }

            }
        }

        return  resultss;
    }
    public String addContractRemark(String data){

        String contractno="",remark="",userid="";

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
        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();
        if (jsonObject.keySet().contains("remark"))remark=jsonObject.get("remark").getAsString();
        if (jsonObject.keySet().contains("userid"))userid=jsonObject.get("userid").getAsString();
        String sql ="";
        int tt=getCurrenntTime();
      sql="insert into contractremark (contractno,userid,remark,ttime,valid) values (?,?,?,"+tt+",1)";


     Object[]   queryList=new Object[3];
        queryList[0]=contractno;
        queryList[1]=userid;
        queryList[2]=remark;


        String resultss="";
        try

        {
            if (jdbcTemplate.update(sql,queryList)==1)resultss="{\"errcode\":\"0\",\"contractno\":\""+contractno+"\"}";
            else resultss=GetErrorString(3,"提交不成功！");
        }catch (Exception e1)
        {
            resultss=e1.toString();
        }

        return  resultss;
    }
    public String addContractremainingamount(String data){

        String contractno="";
        float remainingamount=0;
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
        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();
        if (jsonObject.keySet().contains("remainingamount"))remainingamount=jsonObject.get("remainingamount").getAsFloat();

        String sql ="";
        int tt=getCurrenntTime();
        sql="update contract set remainingamount=? where contractno=?";


        Object[]   queryList=new Object[2];

        queryList[0]=remainingamount;
        queryList[1]=contractno;


        String resultss="";
        try

        {
            if (jdbcTemplate.update(sql,queryList)==1)resultss="{\"errcode\":\"0\",\"contractno\":\""+contractno+"\"}";
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

            if (domainenter.equals("2"))   resultss="MB"+dealerno+ss;
            else resultss=brand+dealerno+ss;
            if (brand.equals("一汽红旗"))resultss="HQ"+dealerno+ss;
            if (brand.equals("一汽奔腾"))resultss="BT"+dealerno+ss;
            sqlstring="select contractno from contract where contractno=?";
            map=queryForMap(jdbcTemplate,sqlstring,resultss);
            if (map!=null)resultss="";
        }

        return resultss;
    }

    private String getContractnoold(String dealerno)
    {
        String resultss="",brand="";
        int t1=GetMonthtime();
        long  ii=0;
        String sqlstring="select brand from dealer where dealerno=?";
        Map<String,Object>map=queryForMap(jdbcTemplate,sqlstring,dealerno);
        if (map!=null)
        {
            brand=setting.NUllToSpace(map.get("brand"));
        }
         sqlstring="select  contractno from contract where ttime>"+t1+" order by ttime desc ";
        map=queryForMap(jdbcTemplate,sqlstring);
        String ss="";
        if (map!=null)
        {
             ss=map.get("contractno")+"";
            if (ss.length()>4)
            {
                ss=ss.substring(ss.length()-4,ss.length());
                ii=nullToInt(ss) ;
            }

        }
        if (ii==0)resultss=brand+dealerno+GetNowDate("yyyyMM")+"0001";
        else
        {
            ss=(ii+1)+"";
            for (int i=ss.length();i<4;i++)
            {
                ss="0"+ss;
            }
            resultss=brand+dealerno+GetNowDate("yyyyMM")+ss;
        }
        return resultss;
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
            sql="insert into contract (contractno,ttime,valid,status,dealerno,userid) values (?,"+tt+",0,'草稿',?,?)";
            queryList=new Object[3];
            queryList[0]=contractno;
            queryList[1]=dealerno;
            queryList[2]=userid;
            jdbcTemplate.update(sql,queryList);
        }


        Invoiceurl=uploadJpg(file);



            sql="update contract set Invoiceurl=? where contractno=?";
        queryList=new Object[2];
        queryList[0]=Invoiceurl;
        queryList[1]=contractno;

        String resultss="";

        if (jdbcTemplate.update(sql,queryList)==1)resultss="{\"errcode\":\"0\",\"contractno\":\""+contractno+"\",\"Invoiceurl\":\""+Invoiceurl+"\"}";
        else resultss=GetErrorString(3,"提交不成功！");
        return  resultss;
    }
    public String uploadinsurance(String data,MultipartFile file){
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
            sql="insert into contract (contractno,ttime,valid,status,dealerno,userid) values (?,"+tt+",0,'草稿',?,?)";
           queryList=new Object[3];
            queryList[0]=contractno;
            queryList[1]=dealerno;
            queryList[2]=userid;
            jdbcTemplate.update(sql,queryList);
        }
        insuranceurl=uploadJpg(file);
        sql="update contract set insuranceurl=? where contractno=?";
        queryList=new Object[2];
        queryList[0]=insuranceurl;
        queryList[1]=contractno;


        String resultss="";

        if (jdbcTemplate.update(sql,queryList)==1)resultss="{\"errcode\":\"0\",\"contractno\":\""+contractno+"\",\"insuranceurl\":\""+insuranceurl+"\"}";
        else resultss=GetErrorString(3,"提交不成功！");
        return  resultss;
    }
    public String uploadJpg(String data,MultipartFile file){

        String url="";
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
         url=uploadJpg(file);
         String resultss="{\"errcode\":\"0\",\"url\":\""+url+"\"}";

        return  resultss;
    }
    public String uploadtierpic(String data,MultipartFile file){
        String tid="";
        String picurl="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="",action="";
        int multi=0;
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;

        String resultss="";
        String sql ="";
        if (jsonObject.keySet().contains("tid"))tid=jsonObject.get("tid").getAsString();
        if (jsonObject.keySet().contains("action")) action = jsonObject.get("action").getAsString();
        if (action.equals("delete"))
        {
            if (tid.equals(""))return GetErrorString(1,"");
            else
            {
                sql="update tiredetail set picurl='' where tid="+tid;
                jdbcTemplate.update(sql);
                resultss="{\"errcode\":\"0\",\"tid\":\""+tid+"\"}";
                return  resultss;
            }
        }

        int tt=setting.GetCurrenntTime();
        picurl=uploadJpg(file);
        if (tid.equals(""))
        {

            sql="insert into tiredetail (ttime,picurl) values ("+tt+",?)";


        }else
        {
            sql="update tiredetail set picurl=? where tid="+tid;
        }
        jdbcTemplate.update(sql,picurl);
        if (tid.equals(""))
        {
            sql="select tid from tiredetail where ttime="+tt;
            Map<String, Object> map = queryForMap(jdbcTemplate, sql);
            if (map == null) resultss=GetErrorString(3,"提交不成功！");
            else {
                tid = map.get("tid") + "";
                resultss="{\"errcode\":\"0\",\"tid\":\""+tid+"\",\"picurl\":\""+picurl+"\"}";
            }
        }else
        {
            resultss="{\"errcode\":\"0\",\"tid\":\""+tid+"\",\"picurl\":\""+picurl+"\"}";
        }

        return  resultss;
    }

    public String updatetierpic(String data){
        String tid="";
        String picurl="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="",action="";
        int multi=0;
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;

        String resultss="";
        String sql ="";
        if (jsonObject.keySet().contains("tid"))tid=jsonObject.get("tid").getAsString();
        if (jsonObject.keySet().contains("action")) action = jsonObject.get("action").getAsString();
        if (tid.equals(""))return GetErrorString(1,"");
        else
        {
            sql="update tiredetail set picurl='' where tid="+tid;
            jdbcTemplate.update(sql);
            resultss="{\"errcode\":\"0\",\"tid\":\""+tid+"\"}";

        }


        return  resultss;
    }
    public String tierpic(String data){
        String tid="";
        String picurl="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="",action="";

        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;

        String resultss="";
        String sql ="";
        if (jsonObject.keySet().contains("tid"))tid=jsonObject.get("tid").getAsString();
        if (jsonObject.keySet().contains("action")) action = jsonObject.get("action").getAsString();
        if (action.equals("delete"))
        {
            if (tid.equals(""))return GetErrorString(1,"");
            else
            {
                sql="update tiredetail set picurl='' where tid="+tid;
                jdbcTemplate.update(sql);
                resultss="{\"errcode\":\"0\",\"tid\":\""+tid+"\"}";
                return  resultss;
            }
        }else return GetErrorString(1,"");



    }

    public String addorupdatetier(String data){

        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="",contractno="",brandname="",tiretype="",dot="",tid="",position="",picurl="";

        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();

        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;
        JsonArray jsonArray=new JsonArray();

        if (jsonObject.keySet().contains("data")) {
            //jsonArray = jsonObject.get("data").getAsJsonArray();
           data = jsonObject.get("data").toString();
            jsonArray = new JsonParser().parse(data).getAsJsonArray();
        }
        String resultss = "",tids="";

        String sql = "";
        int tt = setting.GetCurrenntTime();
        int tire_count=0;
        Map<String, Object> map;
        for (int i=0;i<jsonArray.size();i++) {
             tt++;
            jsonObject=jsonArray.get(i).getAsJsonObject();
            tid="";
            if (jsonObject.keySet().contains("tid")) tid = jsonObject.get("tid").getAsString().trim();
            if (jsonObject.keySet().contains("contractno")) contractno = jsonObject.get("contractno").getAsString().trim();
            if (jsonObject.keySet().contains("brandname")) brandname = jsonObject.get("brandname").getAsString().trim();
            if (jsonObject.keySet().contains("tiretype")) tiretype = jsonObject.get("tiretype").getAsString().trim();
            if (jsonObject.keySet().contains("dot")) dot = jsonObject.get("dot").getAsString().trim();
            if (jsonObject.keySet().contains("position")) position = jsonObject.get("position").getAsString().trim();
            if (jsonObject.keySet().contains("picurl")) picurl = jsonObject.get("picurl").getAsString().trim();

            Object[] args = new Object[5];
            args[0] = contractno;
            args[1] = brandname;
            args[2] = tiretype;
            args[3] = dot;
            args[4] = position;
            if (tid.equals("")) {

                sql = "insert into tiredetail (ttime,contractno,brandname,tiretype,dot,position,picurl) values (" + tt + ",?,?,?,?,?,?)";
                args = new Object[6];
                args[0] = contractno;
                args[1] = brandname;
                args[2] = tiretype;
                args[3] = dot;
                args[4] = position;
                args[5] = picurl;
                tire_count++;
            } else {
                sql = "update tiredetail set contractno=?,brandname=?,tiretype=?,dot=?,position=? where tid=" + tid;
                if (!tids.contains(tid+","))tire_count++;
            }

            jdbcTemplate.update(sql, args);
            if (tid.equals(""))
            {
                sql="select tid from tiredetail where ttime="+tt;
                 map = queryForMap(jdbcTemplate, sql);
                if (map == null) tid="";
                else {
                    tid = map.get("tid") + "";

                }
            }
            tids+=tid+",";
        }

        tids=setting.RTrim(tids,",");
        //判断是不是新车轮胎，如果是新车轮胎少于4个提示保存不成功
        sql="select i.cars from contract c inner join insuranceproduct i on c.productid=i.productid where c.contractno=? ";
        map=queryForMap(jdbcTemplate,sql,contractno);
        if (map!=null)
        {
            if ((map.get("cars")+"").equals("新车")&&tire_count<4)
            {
                resultss=GetErrorString(3,"轮胎数量不足，提交不成功！");
                return  resultss;
            }
        }
        if (tids.equals(""))
        {
           resultss=GetErrorString(3,"提交不成功！");

        } else {

            resultss="{\"errcode\":\"0\",\"tid\":\""+tids+"\"}";
        }

        return  resultss;
    }
    public String gettierdetail(String data){

        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="",contractno="",brandname="",type="",dot="",tid="";
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

        String resultss="";
        String sql ="";
        int tt=setting.GetCurrenntTime();
        sql="select * from tiredetail where contractno=?";

        resultss=GetRsultString(sql,jdbcTemplate,contractno);
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
        sql="select status,brand,agentprice,dealerno,mactiveid,vehicletype,isloan,Invoiceprice,productid,originInvoicedate,isnewcar,vin,pname,carstarttime  from  contract where contractno=?";
        String brand="",isnewcar="",vin="",pname="",carstarttime="";

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
          vin=setting.NUllToSpace(map.get("vin"));
          pname=setting.NUllToSpace(map.get("pname"));
          carstarttime=setting.NUllToSpace(map.get("carstarttime"));
       }else return  GetErrorString(3,"合同号不存在！");
     String newstatus="已打印";
       if (!status.equals("草稿"))return  GetErrorString(3,"合同已经提交，不能重复提交！");
       //判断产品是否已经下架，下架的产品不能提交
        //保时捷需要判断产品是否下架
        if (!domainenter.equals("1"))
        {
            sql="select valid,productid,accident from insuranceproduct where productid=? and valid=1";
            map=queryForMap(jdbcTemplate,sql,productid);
            if (map==null)
            {
                return  GetErrorString(3,"产品已下架，不能提交！");
            }
            //保时捷判断代步车有没有已经投保的产品
            int accident=strToShortInt(map.get("accident")+"");
            accident=1;
          //  if (pname.contains("代步保障")||pname.contains("出险保障"))
            if (accident>0)
            {
              String ss="";
              try
              {

              }catch (Exception e2)
              {

              }
                ss=check_accident(contractno,vin,carstarttime);
              if (!ss.equals(""))return  GetErrorString(3,ss);
            }
        }

        Object[] queryList;
        if (billautocheck.equals("1"))
        {
            newstatus="已批准";
           if (domainenter.equals("0")&&agentprice!=0)
            {
                sql = "insert into billdetail (id,contractno,amount,ttime,dealerno) values (uuid(),?,?," + tt + ",?)";
                queryList = new Object[3];
                queryList[0] = contractno;
                queryList[1] = agentprice;
                queryList[2] = dealerno;
                jdbcTemplate.update(sql, queryList);
            }
        }
        if (domainenter.equals("0")||domainenter.equals("1"))
       {
         if (!mactiveid.equals("0"))//判断是否符合市场活动
         {

             if (!isfitmarketactivid(mactiveid,vehicletype,isloan,productid).valid)return  GetErrorString(3,"该合同不符合市场活动要求，不能提交！");
         }

       }
        if (!domainenter.equals("0"))newstatus="待支付";//保时捷没有待支付状态
        if (agentprice==0) {
            newstatus = "已支付";
            //宝马插入支付记录，添加支付日期
            if (domainenter.equals("1"))
            {
                bmwDao.addBmwbill(dealerno,0,userid,"订单支付消费",contractno,-1,"","","");
            }
        }
        if (domainenter.equals("2")&&Invoiceprice>6000000)   newstatus="待审核";
        if (domainenter.equals("1")&&isnewcar.equals("2")) {
            //判断日期差

            if (Invoiceprice>=1100000) newstatus = "待审核";//宝马二手车 金额大于110万的时候
        }
        sql="insert into contractcheck (tid,contractno,checkcontent,userid,oldstatus,newstatus,ttime) values (uuid(),?,'合同提交',?,?,'"+newstatus+"',"+tt+")";
        queryList=new Object[3];
        queryList[0]=contractno;
        queryList[1]=userid;
        queryList[2]=status;

        jdbcTemplate.update(sql,queryList);
        sql="update  contract set status=? where contractno=?";
        queryList=new Object[2];
        queryList[0]=newstatus;
        queryList[1]=contractno;
        String resultss="";

        if (jdbcTemplate.update(sql,queryList)==1)resultss="{\"errcode\":\"0\",\"contractno\":\""+contractno+"\"}";
        else resultss=GetErrorString(3,"提交不成功！");
        return  resultss;
    }
    private marketactive isfitmarketactivid(String mactiveid,String vehicletype,String isloan,String productid)
    {
        marketactive ma=new marketactive();
        boolean bl=true;
        String vtype="";

        String sql="select  vtype,amount,atype,productid from marketactivity where stime<=? and etime>=? and valid=1 and (loan=? or loan=2) and tid=?";
       Object[]  queryList=new Object[4];
        queryList[0]=GetNowDate("yyyy-MM-dd");
        queryList[1]=GetNowDate("yyyy-MM-dd");
        queryList[2]=isloan;
        queryList[3]=mactiveid;
        Map<String ,Object>   map=queryForMap(jdbcTemplate,sql,queryList);
        if (map!=null)
        {
            vtype=map.get("vtype")+",";
            ma.amount=setting.NullToZero(map.get("amount")+"");
            ma.atype=setting.StrToInt(map.get("atype")+"");
            ma.productid=map.get("productid")+",";

        }else bl=false;
        if (vehicletype.equals("")||(!vtype.contains(vehicletype+",")&&!vtype.contains("全部")))bl=false;
        if ( !ma.productid.equals(",")&& !ma.productid.contains(productid+","))bl=false;
        ma.valid=bl;

        return  ma;
    }
    public String checkcontract(String data){
        String contractno="",remark="";
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

        String status="",newstatus="";
        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();else return  GetErrorString(1,"");
        if (jsonObject.keySet().contains("userid"))userid=jsonObject.get("userid").getAsString();
        if (jsonObject.keySet().contains("remark"))remark=jsonObject.get("remark").getAsString();
        if (jsonObject.keySet().contains("status"))newstatus=jsonObject.get("status").getAsString();else return  GetErrorString(1,"");
        String sql ="",dealerno="";
        int tt=getCurrenntTime();
        float agentprice=0;
        int count=0;
        String []contractnos=contractno.split(",");
        for (int ii=0;ii<contractnos.length;ii++) {

            contractno=contractnos[ii];
            sql = "select status,dealerno,agentprice from  contract where contractno=?";
            Map<String, Object> map = queryForMap(jdbcTemplate, sql, contractno);
            if (map == null) continue;
            else {
                status = map.get("status") + "";
                dealerno = map.get("dealerno") + "";
                agentprice = nullToZero(map.get("agentprice") + "");
            }

            if (!status.equals("已打印")) continue;
            sql = "insert into contractcheck (tid,contractno,checkcontent,userid,oldstatus,newstatus,ttime,remark) values (uuid(),?,'合同审核',?,?,'" + newstatus + "'," + tt + ",?)";
            Object[] queryList = new Object[4];
            queryList[0] = contractno;
            queryList[1] = userid;
            queryList[2] = status;
            queryList[3] = remark;
            jdbcTemplate.update(sql, queryList);
            sql = "update  contract set status=? where contractno=?";
            queryList = new Object[2];
            queryList[0] = newstatus;
            queryList[1] = contractno;



            if (jdbcTemplate.update(sql, queryList) == 1)count++;

            if (newstatus.equals("已批准")) {
                sql = "insert into billdetail (id,contractno,amount,ttime,dealerno) values (uuid(),?,?," + tt + ",?)";
                queryList = new Object[3];
                queryList[0] = contractno;
                queryList[1] = agentprice;
                queryList[2] = dealerno;

                jdbcTemplate.update(sql, queryList);
            }else
            {
                String content="";
                content+="    您的服务凭证审核被拒绝，拒绝原因："+remark+"，如有疑问请联系管理员，点击链接查看"+GetPathurl()+"background/index.html#/market。\n";

                SendEmailDealer(dealerno,content,"服务凭证");
            }
        }
        String resultss = "";
        resultss = "{\"errcode\":\"0\",\"count\":\"" + count + "\"}";
        return  resultss;
    }
    public String checkcontractDaimler(String data){
        String contractno="",remark="";
        String userid="";
        float agentprice=0;
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

        String status="";
        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();else return  GetErrorString(1,"");
        if (jsonObject.keySet().contains("userid"))userid=jsonObject.get("userid").getAsString();
        if (jsonObject.keySet().contains("agentprice"))agentprice=jsonObject.get("agentprice").getAsFloat();
        status="待支付";
        String sql ="",dealerno="";
        int tt=getCurrenntTime();

            sql = "insert into contractcheck (tid,contractno,checkcontent,userid,oldstatus,newstatus,ttime,remark) values (uuid(),?,'合同审核',?,?,'" + status + "'," + tt + ",?)";
            Object[] queryList = new Object[4];
            queryList[0] = contractno;
            queryList[1] = userid;
            queryList[2] = "待审核";
            queryList[3] = remark;
            jdbcTemplate.update(sql, queryList);
            sql = "update  contract set status=?,agentprice="+agentprice+" where contractno=?";
            queryList = new Object[2];
            queryList[0] = status;
            queryList[1] = contractno;



            jdbcTemplate.update(sql, queryList) ;

          /*  if (newstatus.equals("已批准")) {
                sql = "insert into billdetail (id,contractno,amount,ttime,dealerno) values (uuid(),?,?," + tt + ",?)";
                queryList = new Object[3];
                queryList[0] = contractno;
                queryList[1] = agentprice;
                queryList[2] = dealerno;

                jdbcTemplate.update(sql, queryList);
            }else
            {
                String content="";
                content+="    您的服务凭证审核被拒绝，拒绝原因："+remark+"，如有疑问请联系管理员，点击链接查看"+GetPathurl()+"background/index.html#/market。\n";

                SendEmailDealer(dealerno,content,"服务凭证");
            }*/

        String resultss = "";
        resultss = "{\"errcode\":\"0\",\"count\":\"1\"}";
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


        sql="select status,dealerno,cost from  contract where contractno=? and valid=1";
        Map<String ,Object>map=queryForMap(jdbcTemplate,sql,contractno);
        if (map==null)return  GetErrorString(3,"合同号不存在！");
        else {
            status=map.get("status")+"";

        }
        if (!status.equals("草稿")&&!status.equals("待支付"))return  GetErrorString(3,"合同已经提交，不能删除！");
        sql="update  contract set valid=0 where contractno=?";
        String resultss="";
        if (jdbcTemplate.update(sql,contractno)==1)resultss="{\"errcode\":\"0\",\"contractno\":\""+contractno+"\"}";
        else resultss=GetErrorString(3,"提交不成功！");
        return  resultss;
    }

    public String getbrand(String data){
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
        if (jsonObject.keySet().contains("userid"))userid=jsonObject.get("userid").getAsString();
        String sql ="",brand="";
        if (!userid.equals("")) {
            sql = "select brand from  usermember where id='" + userid + "'";
            Map<String,Object>map=queryForMap(jdbcTemplate,sql);
            if (map!=null)brand=map.get("brand")+"";
        }
        sql="select DISTINCT salebrand as brand from  brand where valid=1 ";
        if (!brand.equals("")&&!brand.equals("全部"))sql+=" and brand='"+brand+"'";
       String resultss=GetRsultString(sql,jdbcTemplate);
        return  resultss;
    }
    public String getcars(String data){
        String brand="";
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

        if (jsonObject.keySet().contains("brand"))brand=jsonObject.get("brand").getAsString();else return  GetErrorString(1,"");
        String sql ="";

        sql="select cars from  cars where valid=1 and brand=?";

        String resultss=GetRsultString(sql,jdbcTemplate,brand);
        return  resultss;
    }
    public String getdealer(String data){
        String brand="";
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

        if (jsonObject.keySet().contains("brand"))brand=jsonObject.get("brand").getAsString();
        //else return  GetErrorString(1,"");
        String sql ="";

        sql="select dealerno,dealername from  dealer where valid=1 ";

        if (!brand.equals("")) {
            if (domainenter.equals("1"))//宝马过滤销售品牌
            {
                sql += " and salebrand like '%" + brand + "%'";
            }
           else  sql += " and brand='" + brand + "'";
        }

        String resultss=GetRsultString(sql,jdbcTemplate);
        return  resultss;
    }
    public String getvehicletype(String data){
        String cars="",brand="";
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

        if (jsonObject.keySet().contains("cars"))cars=jsonObject.get("cars").getAsString();else return  GetErrorString(1,"");
        if (jsonObject.keySet().contains("brand"))brand=jsonObject.get("brand").getAsString();
        String sql ="";

        sql="select vehicletype from  vehicletype where valid=1 and cars=?";
         if (!brand.equals(""))sql+=" and brand='"+brand+"'";
        String resultss=GetRsultString(sql,jdbcTemplate,cars);
        return  resultss;
    }
    public String newContract(String data)
    {
        String dealerno="";
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
        String sql ="select brand,salebrand from dealer where  dealerno=? ";
        String brand="",salebrand="";
        Map<String,Object>map=queryForMap(jdbcTemplate,sql,dealerno);
        if (map!=null)
        {
            brand=map.get("brand")+"";
            salebrand=map.get("salebrand")+"";
            if (brand.equals("BMW")||brand.equals("MINI"))return "{\"errcode\":\"0\",\"salebrand\":\""+salebrand+"\"}";
        }

         sql ="select ttime from bill where status='未确认' and dealerno=? order by ttime";
        int ttime=0;

//and ttime<"+(setting.GetCurrenntTime()-3*24*60*60)+"
        String resultss="";
        map=queryForMap(jdbcTemplate,sql,dealerno);
         if (map==null) {
             sql ="select ttime from bill where status!='已提交支付证明'  and status!='已支付' and dealerno=? and amount>0 and ttime<"+(setting.GetCurrenntTime()-40*24*60*60)+" order by ttime";
             map=queryForMap(jdbcTemplate,sql,dealerno);
             if (map==null)
             {
                if (brand.equals("Porsche")) resultss = "{\"errcode\":\"0\",\"salebrand\":\"Porsche\"}";
             }

             else{
                 ttime=setting.StrToInt(map.get("ttime")+"");
                 if (ttime<(setting.GetCurrenntTime()-55*24*60*60))
                     resultss = GetErrorString(1, "您有账单尚未付款，完成付款后才能继续使用销售模块！");
                 else {
                     ttime=ttime+55*24*60*60;
                     resultss = GetErrorString(3, "您有账单尚未付款，请尽快付款！如"+stampToDate("yyyy-MM-dd",ttime)+"仍未付款，您将无法使用本系统。");
                 }
             }
         }
         else {
             ttime=setting.StrToInt(map.get("ttime")+"");
             if (ttime<(setting.GetCurrenntTime()-5*24*60*60))
             resultss = GetErrorString(1, "您有账单尚未确认，确认账单后才能继续使用销售模块！");
             else {
                 ttime=ttime+5*24*60*60;
                 resultss = GetErrorString(3, "您有账单尚未确认，请尽快前往账单模块确认！如"+stampToDate("yyyy-MM-dd",ttime)+"仍未确认，您将无法使用本系统。");
             }
         }
        return  resultss;
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
        String sql ="select vin from contract where  dealerno=? and vin=? and status!='草稿'";
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


    public  int updateStateByBillno(String billno){
        String sql ="update contract set status='已支付' where contractno in (select contractno from billdetail where billno=?) and status!='已取消'";
        int r = jdbcTemplate.update(sql,billno);
        return  r;
    }
    public String contractPrintdaimler(String data)
    {
        String pathurl=GetPathurl();
        String contractno="",codeurl="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        // result=checkSign(nonce,time,sign,appid);
        //if (!result.equals(""))return result;

        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();else return  GetErrorString(1,"");
        if (jsonObject.keySet().contains("codeurl"))codeurl=jsonObject.get("codeurl").getAsString();

        StringBuilder allss = new StringBuilder();
        String dealerno="";
        String sqlstring="select *,(select FROM_UNIXTIME(billbmw.ttime,'%Y-%m-%d') from billbmw where contractno=contract.contractno and in_or_out=-1 and (tradetype='订单支付消费' or tradetype='支付') ORDER BY ttime desc LIMIT 0,1 ) as paytime from contract where contractno=?";
        Map<String,Object>map=queryForMap(jdbcTemplate,sqlstring,contractno);
        String brand="",vehicletype="",vin="",Invoiceno="",Invoicedate="",guideprice="",Invoiceprice="",pname="";
        String cname="",IdNo="",caddress="",insurancecompany="",Businessinsurancepolicyno="",insurancepolicyno="",productid="",companyname="",cars="";
        String contact_person="",contact_tel="";
        int iscompany=0;
        String retailprice="0";
        String paytime="";
        if (map!=null)
        {
            paytime=setting.NUllToSpace(map.get("paytime"));
            brand=setting.NUllToSpace(map.get("brand"));
            vehicletype=setting.NUllToSpace(map.get("vehicletype"));
            vin=setting.NUllToSpace(map.get("vin"));
            Invoiceno=setting.NUllToSpace(map.get("Invoiceno"));
            Invoicedate=setting.NUllToSpace(map.get("Invoicedate"));
            guideprice=setting.NUllToSpace(map.get("guideprice"));
            Invoiceprice=setting.NUllToSpace(map.get("Invoiceprice"));
            dealerno=setting.NUllToSpace(map.get("dealerno"));
            cname=setting.NUllToSpace(map.get("cname"));
            IdNo=setting.NUllToSpace(map.get("IdNo"));
            caddress=setting.NUllToSpace(map.get("address"));
            insurancecompany=setting.NUllToSpace(map.get("insurancecompany"));
            Businessinsurancepolicyno=setting.NUllToSpace(map.get("Businessinsurancepolicyno"));
            productid=setting.NUllToSpace(map.get("productid"));
            retailprice=setting.NUllToSpace(map.get("retailprice"));
            insurancepolicyno=setting.NUllToSpace(map.get("forceno"));
            companyname=setting.NUllToSpace(map.get("companyname"));
            iscompany=setting.StrToInt(map.get("iscompany")+"");
            contact_person=setting.NUllToSpace(map.get("contact_person"));
            contact_tel=setting.NUllToSpace(map.get("contact_tel"));
            pname=setting.NUllToSpace(map.get("pname"));
            cars=setting.NUllToSpace(map.get("cars"));
        }
        if  (productid.startsWith("BMW02")||productid.startsWith("BMW03")||productid.startsWith("BMW05")||productid.startsWith("BMW06")||productid.startsWith("BMW07"))return  contractPrintBmwKey(data);
        if (productid.startsWith("BMW04"))//组合套餐获取rti的价格
        {
            sqlstring="select agentprice,retailprice from bmwproduct where  productid=?  and (cars=? ) and (vehicletype=? )";
            Object[] queryList=new Object[3];
            queryList[0]="BMW01";
            queryList[1]=cars;
            queryList[2]=vehicletype;
            Map<String,Object> map2=queryForMap(jdbcTemplate,sqlstring,queryList);
            if (map2!=null)
            {

                retailprice=setting.NUllToSpace(map2.get("retailprice"));
            }
        }
        sqlstring="select * from dealer where dealerno=?";
        map=queryForMap(jdbcTemplate,sqlstring,dealerno);
        String dealername="",address="",tel="";
        if (map!=null)
        {
            dealername=setting.NUllToSpace(map.get("dealername"));
            address=setting.NUllToSpace(map.get("address"));
            tel=setting.NUllToSpace(map.get("tel"));
        }
//        allss.append("<table class=\"table-print\" border=\"0\"  ><tr>");
////        allss.append("<td align=\"left\" >Allianz Worldwide Partners</td>");
////        allss.append("<td rowspan=\"2\" align=\"right\"><img src=\"images/anllianlog.png\" alt=\"\"/></td></tr>");
////        allss.append("<td align=\"left\">安世联合商务服务(北京)有限公司</td></tr>");
//////        allss.append("</table>");
        allss.append("<head> ");
        allss.append("<style>");
        allss.append(".print-title{ font-size:18px} ");
        allss.append(".print-body{ font-size:14px} ");
        allss.append(".print-hint{ font-size:6px} ");
        // allss.append(".table-print{ font-size:12px;font-color:#403f45} ");
        allss.append(".table-print{border:1px solid #000;border-collapse:collapse;width:100%;margin-bottom:10px;}/*打印表格的效果*/\n" +
                ".table-print th,.table-print td{padding:2px 4px;font-size:14px;color:#403f45!important;font-weight:normal;}\n");

        allss.append(".table-nobody{border:0px solid #fff;border-collapse:collapse;width:100%;margin-bottom:10px;}/*打印表格的效果*/\n");
        allss.append("</style>");
        allss.append("</head> ");
        allss.append("<body>");
        // allss.append("<div align=\"right\" size=\"2\">本服务由中国太平洋财产保险股份有限公司江苏分公司提供保险保障和承保服务</div>");

        codeurl=qrCodeUtil.createQrCode(pathurl+codeurl);
        allss.append("<hr/>");
      //  allss.append("<div align=\"right\" class=\"print-hint\">本服务由中国大地财产保险股份有限公司上海分公司</div>");
       // allss.append("<div align=\"right\" class=\"print-hint\">提供保险保障和承保服务</div>");
        String title="焕新享服务";
        allss.append("<div align=\"center\" class=\"print-title\">" + title + "</div>");
        allss.append("<div align=\"center\" class=\"print-body\">车辆置换服务合同</div>");
       allss.append("<div align=\"right\"><img src=\""+pathurl+codeurl+"\"  style=\"width: 100px;\"  alt=\"\"/></div>");
        allss.append("<div align=\"right\" size=\"2\">扫描二维码，验证服务合同与保障信息</div>");
        allss.append("<div align=\"right\">编号：【"+contractno+"】</div>");
        allss.append("<br>");
        allss.append("<div align=\"left\" class=\"print-body\">车主："+cname+"</div>");
        allss.append("<br>");
        allss.append("<div align=\"left\" class=\"print-body\">授权经销商："+dealername+"</div>");
        allss.append("<div align=\"left\" class=\"print-body\">&nbsp;&nbsp;&nbsp;&nbsp;通过签署本车辆置换服务合同（以下简称“服务合同”），车主同意接受由授权经销商提供的车辆置换服务（以下简称“服务”或“本服务”）。为了便于授权经销商向车主提供更贴心的服务，车主需仔细阅读本服务合同条款，并准确填写下列信息：</div>");
        allss.append("<br>");
        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">一、享权车辆信息：</font> </td></tr>");
        StringBuilder ss = new StringBuilder();
        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" ><tr>");
        ss.append("<td>品   牌："+brand+"</td>");
        ss.append("<td>车   型："+vehicletype+"</td></tr>");
        ss.append("<tr><td colspan=\"2\">车辆用途：     非   营   运</td></tr>");
        ss.append("<tr><td colspan=\"2\">车架号码（VIN码）："+vin+"</td></tr>");
        ss.append("<tr><td colspan=\"2\">购买时间（购车发票时间）："+Invoicedate+"</td></tr>");
        ss.append("<tr><td colspan=\"2\">购车发票号码："+Invoiceno+"</td></tr>");
        ss.append("<tr><td>享权车建议零售价："+formatMoney(guideprice)+"</td>");
        ss.append("<td>享权车辆发票价（原车购置价）："+formatMoney(Invoiceprice)+"</td></tr>");
        if (nullToZero(retailprice) !=0)
        ss.append("<td>焕新享服务（车辆置换服务）价格："+formatMoney(retailprice)+"</td></tr>");
        else    ss.append("<td>焕新享服务（车辆置换服务）价格：</td></tr>");
        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");


        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">二、服务提供方信息：</font> </td></tr>");
        ss = new StringBuilder();
        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" >");
        ss.append("<tr><td colspan=\"2\">授权经销商名称："+dealername+"</td></tr>");
        ss.append("<tr><td colspan=\"2\">地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址："+address+"</td></tr>");
        ss.append("<tr><td colspan=\"2\">电&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;话："+tel+"</td></tr>");
        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");

//        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
//        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">三、服务提供方信息：</font> </td></tr>");
//        ss = new StringBuilder();
//        ss.append("<table class=\"table-print\" border=\"0\"  width=\"80%\" >");
//        ss.append("<tr><td colspan=\"2\">服务商名称：<u>安世联合商务服务（北京）有限公司</u></td></tr>");
//        ss.append("<tr><td colspan=\"2\">地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址：<u>北京市朝阳区东三环北路19号中青大厦16层</u></td></tr>");
//        ss.append("<tr><td colspan=\"2\">电&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;话：<u>021-61620636转6212</u></td></tr>");
//        ss.append("</table>");
//        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
//        allss.append("</table>");
//        allss.append("<br>");


        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">三、车主信息：</font> </td></tr>");
        ss = new StringBuilder();
        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" >");
        if (companyname.equals(""))companyname=cname;
      /*  if (iscompany==0)

            ss.append("<tr><td colspan=\"2\">姓名/企业名称："+cname+"</td></tr>");
        else {
            ss.append("<tr><td colspan=\"2\">姓名/企业名称：" + companyname + "</td></tr>");
            ss.append("<tr><td colspan=\"2\">办理人姓名：" + contact_person + "</td></tr>");
            ss.append("<tr><td colspan=\"2\">办理人电话：" + contact_tel + "</td></tr>");
        }*/
        ss.append("<tr><td colspan=\"2\">姓名/企业名称："+cname+"</td></tr>");
        ss.append("<tr><td colspan=\"2\">身份证号码："+IdNo+"</td></tr>");
        ss.append("<tr><td colspan=\"2\">或统一社会信用代码："+foramtss(20,"")+"</td></tr>");
        ss.append("<tr><td colspan=\"2\">地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址："+foramtss(30,caddress)+"</td></tr>");
        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");

        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">四、享权车辆机动车辆保险信息：</font> </td></tr>");
        ss = new StringBuilder();
        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" >");
        ss.append("<tr><td colspan=\"2\">机动车辆保险公司："+insurancecompany+"</td></tr>");
        ss.append("<tr><td colspan=\"2\">机动车交通事故责任强制保险保单号码："+insurancepolicyno+"</td></tr>");
        ss.append("<tr><td colspan=\"2\">机动车辆商业保险保单号码："+Businessinsurancepolicyno+"</td></tr>");
        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");

        allss.append("<div><b>五、<u>服务前提：</u></b></div>");
        allss.append("<div>1、享权车辆：非营运9座及9座以下梅赛德斯-奔驰品牌乘用车。</div>");
        allss.append("<div>2、车龄要求：车主购买本服务时享权车辆车龄自享权车辆首次购车发票开具之日起计算不得超过十二（12）个月。</div>");
        allss.append("<div>3、享权车辆的所有权未发生过转让。</div>");

        allss.append("<div>4、机动车辆保险投保要求：在本服务合同约定的服务期限内车主须为享权车辆投保机动车交通事故责任强制保险和足额投保以下机动车辆商业保险险种，且须保持所有险种于本服务合同约定的服务期限内持续有效。</div>");
        allss.append("<div>1)机动车损失保险；</div>");
        allss.append("<div>2)______________________（如有）。</div>");
       // allss.append("<div>3)自燃损失保险；及，</div>");
        //allss.append("<div>4)上述险种的全部附属不计免赔特约条款（如有）。</div>");
        allss.append("<div>享权车辆因机动车辆商业保险的上述任一险种承保的保险事故造成全损或推定全损，且该险种和机动车交通事故责任强制保险于保险事故发生时保险期间尚未届满，车主方可向授权经销商申请服务。全损或推定全损以承保享权车辆上述机动车辆商业保险之保险公司的定损结果为惟一依据。</div>");

        allss.append("<div><b>六、<u>服务承诺：</u></b></div>");
        allss.append("<div>&nbsp;&nbsp;享权车辆在服务期限内遭受全损或推定全损，且满足本服务合同第五条规定的服务前提，车主可向授权经销商申请服务。授权经销商将协助车主置换一辆梅赛德斯-奔驰品牌的新车，可供选择的新车品牌视该授权经销商的具体授权情况而定，置换新车过程中产生的车辆置换费用由授权经销商承担。新车发票价（其定义与享权车辆发票价一致）高于享权车辆发票价的，超出部分由车主自行承担。新车的官方建议零售价不得低于享权车辆官方建议零售价的90%。</div>");
        allss.append("<div>&nbsp;&nbsp;本服务中所指的车辆置换费用包括以下两部分：</div>");

        allss.append("<div>1、车辆折旧费用：\n车辆折旧费用 = 享权车辆发票价x已使用月数 x月折旧率（0.6%），\n" +
                "其中，“已使用月数”指享权车辆购车发票显示的购车日期至发生全损或推定全损事故之日计算的已使用月数，不足一月，不计算折旧。\n</div>");
        allss.append("<div>2、额外重置费用：\n" +
                "在新车购置过程中所产生的费用，包括新车登记费用、新车交付费用、新车的车辆购置税、新车的机动车辆保险保费和车辆置换服务费用。其金额为上述各项费用金额之和，但不超过享权车辆发票价的15%；超出部分由车主自行承担。\n</div>");
        allss.append("<div>上述税费、保险费及服务费之金额应当符合同一时期同类税种、保险产品和服务的税率或市场公允价格。</div>");
        allss.append("<div>享权车辆发票价以其首次购车发票所载明之价税合计金额（不含车辆购置税）为准。</div>");

        allss.append("<div><b>七、<u>除外情况：</u></b></div>");

        allss.append("<div><b>1、出现下列任一情形时，车主不能享受服务</b></div>");
        allss.append("<div><b>1)\t车主要求提供服务的享权车辆信息和/或车主身份信息与本服务合同第一条和第三条记载的信息不一致；</b></div>");
        allss.append("<div><b>2)\t车主要求提供服务的享权车辆用途与本服务合同中限定不一致；</b></div>");
        allss.append("<div><b>3)\t享权车辆遭受的事故发生在本服务合同约定服务期限生效日期前；</b></div>");
        allss.append("<div><b>4)\t享权车辆因遭受事故仅导致部分损失，未达到本服务合同所约定的车辆全损或推定全损标准（以承保享权车辆机动车辆商业保险之保险公司的定损结果为唯一依据）；</b></div>");
        allss.append("<div><b>5)\t享权车辆遭受事故时不存在相关机动车辆保险保障，或遭受事故后因不符合法律规定或相关机动车辆保险约定的理赔条件、未获得与全损或推定全损相应的机动车辆保险赔偿；</b></div>");
        allss.append("<div><b>6)\t享权车辆经过改装或拼装，其工况受到影响；</b></div>");
        allss.append("<div><b>7)\t享权车辆已被转让给第三人；</b></div>");
        allss.append("<div><b>8)\t车主要求置换的新车的官方建议零售价低于享权车辆官方建议零售价的90%。</b></div>");

        allss.append("<div><b>2、下列任何原因造成享权车辆全损或推定全损，车主不能就相关损失享受服务：</b></div>");
        allss.append("<div><b>1）车主的故意行为、重大过失、欺诈、不诚实、违法犯罪行为；</b></div>");
        allss.append("<div><b>2）车主使用、维护、保管不当；</b></div>");
        allss.append("<div><b>3）享权车辆内在或潜在缺陷、自然磨损、自然损耗；</b></div>");
        allss.append("<div><b>4）战争、敌对行为、军事行动、武装冲突、恐怖主义活动、罢工、暴动、骚乱；</b></div>");
        allss.append("<div><b>5）核爆炸、核裂变、核聚变；</b></div>");
        allss.append("<div><b>6）放射性污染及其他各种环境污染；</b></div>");
        allss.append("<div><b>7）行政行为、司法行为。</b></div>");

        allss.append("<div><b>3、下列损失、费用和责任，不在服务范围内：</b></div>");
        allss.append("<div><b>1)\t新车发票价超出享权车辆发票价的部分；</b></div>");
        allss.append("<div><b>2)\t额外重置费用超出本合同第六条所约定上限的部分；</b></div>");
        allss.append("<div><b>3)\t在车辆置换过程中，因更换非梅赛德斯-奔驰品牌新车产生的费用；</b></div>");
        allss.append("<div><b>4)\t任何形式的人身伤害、财产损失，及除本服务合同第六条所列“车辆置换费用”外的其他任何费用支出；</b></div>");
        allss.append("<div><b>5)\t服务过程中所产生的任何间接损失、赔偿责任；</b></div>");
        allss.append("<div><b>6)\t因未投保本服务合同第五条约定的机动车辆保险任一险种承保的保险事故造成的车辆全损或推定全损，或者该险种的保险期间于保险事故发生时已届满。</b></div>");

        allss.append("<div><b>八、<u>服务终止：</u></b></div>");
        allss.append("<div>&nbsp;&nbsp;除法律另有规定和本服务合同另有约定外，若车主于服务期限开始后请求取消本服务合同的，授权经销商应当在按照日比例从已收取的服务费中扣除自服务期限生效日期起算至服务合同解除之日止应收取服务费后的余额部分退还车主。服务合同解除之日以车主签署取消订单证明载明的日期为准。</div>");
        allss.append("<div>&nbsp;&nbsp;一旦车主就享权车辆获得了车辆置换服务，无论是从授权经销商处获得或者按照本服务合同第十条的规定从中国太平洋财产保险股份有限公司北京分公司建议的其他授权经销商处获得，在车辆置换服务提供完毕后，本服务合同自动终止。</div>");

        allss.append("<div><b>九、<u>服务期限：</u></b></div>");



        allss.append("<div>&nbsp;&nbsp;本服务合同项下服务期限的生效日期为"+paytime+"，服务期限至享权车辆车龄的第36个月届满时自动终止，车龄自享权车辆首次购车发票开具之日起计算。</div>");

        allss.append("<div><b>十、<u>申请资料：</u></b></div>");
        allss.append("<div>&nbsp;&nbsp;享权车辆发生本服务合同约定的事故后，车主可以通过联系授权经销商申请服务，并按照要求提交所需材料。如无法联系授权经销商，可通过联系此服务之提供商申请服务，热线电话400-060-0820。</div>");
        allss.append("<div><b>1、车主需提供确认享权车辆事故性质、原因、损失程度等有关证明和资料，包括但不限于：</b></div>");
        allss.append("<div>1)\t享权车辆发生全损或推定全损时仍处于生效状态的机动车辆商业保险和机动车交通事故责任强制保险的保险单复印件；</div>");
        allss.append("<div>2)\t本服务合同；</div>");
        allss.append("<div>3)\t享权车辆行驶本复印件、车主驾驶本复印件、车主身份证或企业营业执照复印件；</div>");
        allss.append("<div>4)\t享权车辆首次购车发票复印件；</div>");
        allss.append("<div>5)\t机动车辆保险之保险公司已按照全损、推定全损赔付完毕的证明（划账水单原件，以及一次性赔偿协议原件或机动车辆保险之保险公司盖章的复印件）；</div>");
        allss.append("<div>6)\t享权车辆全损或推定全损后处理手续（权益转让书、拍卖或注销解体手续）；</div>");
        allss.append("<div>7)\t证明事故发生原因的证明，属于道路交通事故的提供事故证明（证明材料复印件，需机动车辆保险之保险公司盖章）。</div>");
        allss.append("<div><b>2、车主需提供车辆置换补偿的相关材料、单据，包括但不限于：</b></div>");
        allss.append("<div>1)\t新车的购车合同的复印件；</div>");
        allss.append("<div>2)\t新车的车辆购置税完税证明的复印件；</div>");
        allss.append("<div>3)\t新车的机动车辆保险的保险单的复印件或电子保单及保费发票的复印件；</div>");
        allss.append("<div>4)\t新车的车辆置换服务的服务合同和服务费发票的复印件，如有；</div>");
        allss.append("<div>5)\t新车交付所涉及项目的费用发票的复印件。</div>");
        allss.append("<div><b>3、车主需将机动车辆保险之全损或推定全损的赔付款项支付给提供车辆置换服务的授权经销商。</b></div>");
        allss.append("<div>&nbsp;&nbsp;因车主未能提交任何上述约定的申请资料，导致部分或全部服务责任无法确定，对于无法确定的部分不能享受服务。</div>");
        allss.append("<div>&nbsp;&nbsp;若由于车主原因导致未完成新车置换或授权经销商未能提供服务的，车主不能主张本服务合同项下的任何服务和/或补偿。</div>");

        allss.append("<div><b>十一、<u>服务流程：</u></b></div>");

        allss.append("<div align=\"center\"><img src=\""+pathurl+"images/anllianlog1daimler.png\" alt=\"\"/></div>");
        allss.append("<div>车主声明：</div>");
        allss.append("<div>&nbsp;&nbsp;提供本服务的授权经销商已通过上述书面形式向本人详细介绍并提供了本服务的服务内容和条款。本人已完整阅读本服务合同，并清楚知晓本服务合同的各项条款，特别是保持本服务有效的“服务前提”、将导致无法获得服务的“除外情况”和申请服务所必需的“申请资料”等条款。</div>");
        allss.append("<div>&nbsp;&nbsp;本人知悉并同意，提供本服务的授权经销商可以在必要范围内将本服务合同信息及个人信息提供给其他实体，包括：授权经销商会将合同信息提供给与其合作的、为车辆置换服务提供管理服务的第三方服务提供商；为服务合同提供承保服务的保险公司也需获得本合同内的享权车辆信息、车辆保险信息、车主信息及其联系电话。授权经销商提供上述信息的目的包括：及时、正确处理您的车辆置换服务申请；使本服务合同得到保险公司的及时承保；在合法范围内对服务购买和使用情况进行数据分析。授权经销商将采取协议约定等合理措施，要求前述信息接收方遵守适用的法律、法规及合约，妥善管理并仅为授权经销商指定的目的使用您的上述信息｡</div>");
        allss.append("<div>&nbsp;&nbsp;本人已充分理解并接受上述内容，同意以此作为订立服务合同的依据，并愿意遵守本服务合同的条款。</div>");
        allss.append("<br>");
        allss.append("<hr style= \"border:1px dashed #000\" />");

        allss.append("<br>");
        allss.append("<br>");


        allss.append("<div>车主签字（适用于个人客户）：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>车主盖章（适用于企业客户）：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>授权经销商：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>盖  章：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("</body>");
        if  (productid.startsWith("BMW04")) {
            allss.append("<br>");
            allss.append("<br>");
            allss.append("<br>");
            allss.append("<br>");
            allss.append("<br>");
            allss.append("<br>");
            allss.append(contractPrintBmwKey(data));
        }
        return allss.toString();
    }
    public String contractPrintBmw(String data)
    {
        String pathurl=GetPathurl();
        String contractno="",codeurl="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
       // result=checkSign(nonce,time,sign,appid);
        //if (!result.equals(""))return result;

        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();else return  GetErrorString(1,"");
        if (jsonObject.keySet().contains("codeurl"))codeurl=jsonObject.get("codeurl").getAsString();

        StringBuilder allss = new StringBuilder();
        String dealerno="";
        String sqlstring="select *,(select FROM_UNIXTIME(billbmw.ttime,'%Y-%m-%d') from billbmw where contractno=contract.contractno and in_or_out=-1 and (tradetype='订单支付消费' or tradetype='支付') ORDER BY ttime desc LIMIT 0,1 ) as paytime from contract where contractno=?";
        Map<String,Object>map=queryForMap(jdbcTemplate,sqlstring,contractno);
        String brand="",vehicletype="",vin="",Invoiceno="",Invoicedate="",guideprice="",Invoiceprice="",pname="";
        String cname="",IdNo="",caddress="",insurancecompany="",Businessinsurancepolicyno="",insurancepolicyno="",productid="",companyname="",cars="";
        String contact_person="",contact_tel="",originInvoicedate="";
        int iscompany=0;
        String retailprice="0";
        String paytime="";
        if (map!=null)
        {
            paytime=setting.NUllToSpace(map.get("paytime"));
            brand=setting.NUllToSpace(map.get("brand"));
            vehicletype=setting.NUllToSpace(map.get("vehicletype"));
            vin=setting.NUllToSpace(map.get("vin"));
            Invoiceno=setting.NUllToSpace(map.get("Invoiceno"));
            Invoicedate=setting.NUllToSpace(map.get("Invoicedate"));
            guideprice=setting.NUllToSpace(map.get("guideprice"));
            Invoiceprice=setting.NUllToSpace(map.get("Invoiceprice"));
            dealerno=setting.NUllToSpace(map.get("dealerno"));
            cname=setting.NUllToSpace(map.get("cname"));
            IdNo=setting.NUllToSpace(map.get("IdNo"));
            caddress=setting.NUllToSpace(map.get("address"));
            insurancecompany=setting.NUllToSpace(map.get("insurancecompany"));
            Businessinsurancepolicyno=setting.NUllToSpace(map.get("Businessinsurancepolicyno"));
            productid=setting.NUllToSpace(map.get("productid"));
            retailprice=setting.NUllToSpace(map.get("retailprice"));
            insurancepolicyno=setting.NUllToSpace(map.get("forceno"));
            companyname=setting.NUllToSpace(map.get("companyname"));
            iscompany=setting.StrToInt(map.get("iscompany")+"");
            contact_person=setting.NUllToSpace(map.get("contact_person"));
            contact_tel=setting.NUllToSpace(map.get("contact_tel"));
            pname=setting.NUllToSpace(map.get("pname"));
            cars=setting.NUllToSpace(map.get("cars"));
            //originInvoicedate=setting.NUllToSpace(map.get("originInvoicedate"));
        }
        if  (productid.startsWith("BMW02")||productid.startsWith("BMW03")||productid.startsWith("BMW05")||productid.startsWith("BMW06")||productid.startsWith("BMW07"))return  contractPrintBmwKey(data);
        if (productid.startsWith("BMW04"))//组合套餐获取rti的价格
        {
            sqlstring="select agentprice,retailprice from bmwproduct where  productid=?  and (cars=? ) and (vehicletype=? )";
           Object[] queryList=new Object[3];
            queryList[0]="BMW01";
            queryList[1]=cars;
            queryList[2]=vehicletype;
            Map<String,Object> map2=queryForMap(jdbcTemplate,sqlstring,queryList);
            if (map2!=null)
            {

                retailprice=setting.NUllToSpace(map2.get("retailprice"));
            }
        }
        sqlstring="select * from dealer where dealerno=?";
        map=queryForMap(jdbcTemplate,sqlstring,dealerno);
        String dealername="",address="",tel="";
        if (map!=null)
        {
            dealername=setting.NUllToSpace(map.get("dealername"));
            address=setting.NUllToSpace(map.get("address"));
            tel=setting.NUllToSpace(map.get("tel"));
        }
//        allss.append("<table class=\"table-print\" border=\"0\"  ><tr>");
////        allss.append("<td align=\"left\" >Allianz Worldwide Partners</td>");
////        allss.append("<td rowspan=\"2\" align=\"right\"><img src=\"images/anllianlog.png\" alt=\"\"/></td></tr>");
////        allss.append("<td align=\"left\">安世联合商务服务(北京)有限公司</td></tr>");
//////        allss.append("</table>");
        allss.append("<head> ");
        allss.append("<style>");
        allss.append(".print-title{ font-size:18px} ");
        allss.append(".print-body{ font-size:14px} ");
        allss.append(".print-hint{ font-size:6px} ");
        // allss.append(".table-print{ font-size:12px;font-color:#403f45} ");
        allss.append(".table-print{border:1px solid #000;border-collapse:collapse;width:100%;margin-bottom:10px;}/*打印表格的效果*/\n" +
                ".table-print th,.table-print td{padding:2px 4px;font-size:14px;color:#403f45!important;font-weight:normal;}\n");

        allss.append(".table-nobody{border:0px solid #fff;border-collapse:collapse;width:100%;margin-bottom:10px;}/*打印表格的效果*/\n");
        allss.append("</style>");
        allss.append("</head> ");
            allss.append("<body>");
       // allss.append("<div align=\"right\" size=\"2\">本服务由中国太平洋财产保险股份有限公司江苏分公司提供保险保障和承保服务</div>");

        codeurl=qrCodeUtil.createQrCode(pathurl+codeurl);
        allss.append("<hr/>");
        allss.append("<div align=\"right\" class=\"print-hint\">本服务由中国大地财产保险股份有限公司上海分公司</div>");
        allss.append("<div align=\"right\" class=\"print-hint\">提供保险保障和承保服务</div>");
        String title=brand+"悦然焕新服务";
        allss.append("<div align=\"center\" class=\"print-title\">" + title + "</div>");
        allss.append("<div align=\"center\" class=\"print-body\">机动车置换服务合同</div>");
        allss.append("<div align=\"right\"><img src=\""+pathurl+codeurl+"\"  style=\"width: 100px;\"  alt=\"\"/></div>");
        allss.append("<div align=\"right\" size=\"2\">扫描二维码，验证服务合同与保障信息</div>");
        allss.append("<div align=\"right\">合同编号：【"+contractno+"】</div>");
        allss.append("<br>");
        allss.append("<div align=\"left\" class=\"print-body\">车主："+cname+"</div>");
        allss.append("<br>");
        allss.append("<div align=\"left\" class=\"print-body\">授权经销商："+dealername+"</div>");
        allss.append("<div align=\"left\" class=\"print-body\">&nbsp;&nbsp;&nbsp;&nbsp;通过签署本机动车置换服务合同（以下简称“服务合同”），车主同意接受由授权经销商提供的机动车置换服务（以下简称“服务”或“本服务”）。为了便于授权经销商向车主提供更贴心的服务，车主需仔细阅读本服务合同条款，并准确填写下列信息：</div>");
        allss.append("<br>");
        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">一、享权车辆信息：</font> </td></tr>");
        StringBuilder ss = new StringBuilder();
        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" ><tr>");
        ss.append("<td>品   牌："+brand+"</td>");
        ss.append("<td>车   型："+vehicletype+"</td></tr>");
        ss.append("<tr><td colspan=\"2\">车辆用途：     非   营   运</td></tr>");
        ss.append("<tr><td colspan=\"2\">车架号码（VIN码）："+vin+"</td></tr>");
       // if (!originInvoicedate.equals(""))Invoicedate=originInvoicedate;
        ss.append("<tr><td colspan=\"2\">购车时间（新车购车发票日期或二手车购车发票日期）："+Invoicedate+"</td></tr>");
       // ss.append("<tr><td colspan=\"2\">购车发票号码："+Invoiceno+"</td></tr>");
        ss.append("<tr><td>享权车辆官方建议零售价（仅适用于新车）："+formatMoney(guideprice)+"</td>");
        ss.append("<td>享权车辆新车购车发票价或二手车销售发票价："+formatMoney(Invoiceprice)+"</td></tr>");
        if (nullToZero(retailprice) !=0)
        ss.append("<td>悦然焕新服务（机动车置换服务）建议零售价："+formatMoney(retailprice)+"</td></tr>");
        else
            ss.append("<td>悦然焕新服务（机动车置换服务）建议零售价：</td></tr>");
        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");


        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">二、服务提供方信息：</font> </td></tr>");
        ss = new StringBuilder();
        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" >");
        ss.append("<tr><td colspan=\"2\">授权经销商名称："+dealername+"</td></tr>");
        ss.append("<tr><td colspan=\"2\">地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址："+address+"</td></tr>");
        ss.append("<tr><td colspan=\"2\">电&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;话："+tel+"</td></tr>");
        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");

//        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
//        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">三、服务提供方信息：</font> </td></tr>");
//        ss = new StringBuilder();
//        ss.append("<table class=\"table-print\" border=\"0\"  width=\"80%\" >");
//        ss.append("<tr><td colspan=\"2\">服务商名称：<u>安世联合商务服务（北京）有限公司</u></td></tr>");
//        ss.append("<tr><td colspan=\"2\">地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址：<u>北京市朝阳区东三环北路19号中青大厦16层</u></td></tr>");
//        ss.append("<tr><td colspan=\"2\">电&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;话：<u>021-61620636转6212</u></td></tr>");
//        ss.append("</table>");
//        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
//        allss.append("</table>");
//        allss.append("<br>");


        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">三、车主信息：</font> </td></tr>");
        ss = new StringBuilder();
        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" >");
        if (companyname.equals(""))companyname=cname;
        if (iscompany==0)

        ss.append("<tr><td colspan=\"2\">姓名/企业名称："+cname+"</td></tr>");
        else {
            ss.append("<tr><td colspan=\"2\">姓名/企业名称：" + companyname + "</td></tr>");
            ss.append("<tr><td colspan=\"2\">办理人姓名：" + contact_person + "</td></tr>");
            ss.append("<tr><td colspan=\"2\">办理人电话：" + contact_tel + "</td></tr>");
        }
        ss.append("<tr><td colspan=\"2\">身份证号码或统一社会信用代码："+IdNo+"</td></tr>");
        ss.append("<tr><td colspan=\"2\">地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址："+foramtss(30,caddress)+"</td></tr>");
        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");

        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">四、享权车辆机动车辆保险信息：</font> </td></tr>");
        ss = new StringBuilder();
        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" >");
        ss.append("<tr><td colspan=\"2\">机动车辆保险公司："+insurancecompany+"</td></tr>");
        ss.append("<tr><td colspan=\"2\">机动车交通事故责任强制保险保单号码："+insurancepolicyno+"</td></tr>");
        ss.append("<tr><td colspan=\"2\">机动车辆商业保险保单号码："+Businessinsurancepolicyno+"</td></tr>");
        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");

        allss.append("<div><b>五、<u>服务前提：</u></b></div>");
        allss.append("<div>1、享权车辆：非营运9座及9座以下"+brand+"品牌乘用车</div>");
        allss.append("<div>2、车龄要求：</div>");
        allss.append("<div>&nbsp;&nbsp;1)\t新车：车主购买本服务时，享权车辆车龄自享权车辆首次购车发票开具之日起计算不得超过十二（12）个月，且享权车辆的所有权未发生过转让；</div>");
        allss.append("<div>&nbsp;&nbsp;2)\t二手车：车主购买本服务时，享权车辆车龄自享权车辆首次购车发票（即其作为新车时所开具的发票）开具之日起计算不得超过四十八（48）个月。</div>");
        allss.append("<div>3、机动车辆保险投保要求：在本服务合同约定的服务期限内车主须为享权车辆投保机动车交通事故责任强制保险和足额投保以下全部或部分机动车辆商业保险险种，且须保持其于本服务合同约定的服务期限内持续有效。</div>");
        allss.append("<div>1)\t机动车损失保险；及，</div>");
        allss.append("<div>2)\t其它附加险险种（如有）。</div>");

        allss.append("<div>享权车辆因机动车辆商业保险的上述任一险种承保的保险事故造成全损或推定全损，且该险种和机动车交通事故责任强制保险于保险事故发生时保险期间尚未届满，车主方可向授权经销商申请服务。全损或推定全损以承保享权车辆上述机动车辆商业保险之保险公司的定损结果为惟一依据。</div>");

        allss.append("<div><b>六、<u>服务承诺：</u></b></div>");
        allss.append("<div>&nbsp;&nbsp;享权车辆在服务期限内遭受全损或推定全损，且满足本服务合同第五条规定的服务前提，车主可向授权经销商申请服务。授权经销商将协助车主置换一辆BMW或MINI品牌的新车，可供选择的新车品牌视该授权经销商的具体授权情况而定，置换新车过程中产生的机动车置换费用由授权经销商承担。\n" +
                "如享权车辆为新车，所置换的新车的官方建议零售价不得低于享权车辆官方建议零售价的90%；如享权车辆为二手车，则所置换的新车的官方建议零售价不得低于享权车辆发票价的90%。\n</div>");
        allss.append("<div>&nbsp;&nbsp;本服务中所指的机动车置换费用包括车辆折旧费用与机动车置换补偿两部分，其中：</div>");
        allss.append("<div>&nbsp;&nbsp;1、\t针对享权车辆为新车的情形：</div>");
        allss.append("<div>&nbsp;&nbsp;1）\t车辆折旧费用：“享权车辆发票价”与“享权车辆对应的机动车辆保险保单年度内如第五条第3款列明的机动车辆商业保险保单中载明的保险金额”的差额（仅在此差值大于零时有效）。</div>");
        allss.append("<div>2）\t机动车置换补偿：在置换新车过程中所产生的费用，包括车辆购置税、新车交付费用、新车登记费用，及新车首年机动车辆保险保费（其中，若所置换新车的车辆购置税或机动车辆保险保费高于享权车辆的车辆购置税或机动车辆保险保费，则上述两项费用应分别以较低值为准）。补偿金额为上述各项费用金额之和，但不超过享权车辆发票价的15%；超出部分由车主自行承担。</div>");
        allss.append("<div>&nbsp;&nbsp;2、\t针对享权车辆为二手车的情形：</div>");
        allss.append("<div>&nbsp;&nbsp;1）\t车辆折旧费用 = 享权车辆发票价x已使用月数 x月折旧率（0.6%）\n" +
                "“已使用月数”指享权车辆二手车销售发票显示的购车日期至发生全损或推定全损事故之日计算的已使用月数，不足一月，不计算折旧；\n</div>");
        allss.append("<div>&nbsp;&nbsp;2）\t机动车置换补偿：在置换新车过程中所产生的费用，包含新车交付费用、新车登记费用，及新车首年机动车辆保险保费（若所置换新车的的机动车辆保险保费高于享权车辆的机动车辆保险保费，则应以较低值为准）。补偿金额为上述各项费用金额之和，但不超过享权车辆发票价的15%；超出部分由车主自行承担。</div>");

        allss.append("<div>上述税费、保险费及服务费之金额应当符合同一时期同类税种、保险产品和服务的税率或市场公允价格。</div>");
        allss.append("<div>享权车辆发票价以其购车发票（新车为首次购车发票，二手车为二手车销售发票）所载明之价税合计金额为准。</div>");

        allss.append("<div><b>七、<u>除外情况：</u></b></div>");

        allss.append("<div><b>1、出现下列任一情形时，车主不能享受服务</b></div>");
        allss.append("<div><b>1）车主要求提供服务的享权车辆信息和/或车主身份信息与本服务合同第一条和第三条记载的信息不一致；</b></div>");
        allss.append("<div><b>2）车主要求提供服务的享权车辆用途与本服务合同中限定不一致；</b></div>");
        allss.append("<div><b>3）享权车辆遭受的事故发生在本服务合同约定服务期限生效日期前；</b></div>");
        allss.append("<div><b>4）享权车辆因遭受事故仅导致部分损失，未达到本服务合同所约定的车辆全损或推定全损标准（以承保享权车辆机动车辆商业保险之保险公司的定损结果为唯一依据）；</b></div>");
        allss.append("<div><b>5）享权车辆遭受事故时不存在相关机动车辆保险保障，或遭受事故后因不符合法律规定或相关机动车辆保险约定的理赔条件、未获得与全损或推定全损相应的机动车辆保险赔偿；</b></div>");
        allss.append("<div><b>6）享权车辆经过改装或拼装，其工况受到影响；</b></div>");
        if (brand.equals("BMW"))
        {
            allss.append("<div><b>7）享权车辆已被转让给第三人；</b></div>");
            allss.append("<div><b>8）车主要求置换的新车的官方建议零售价低于享权车辆官方建议零售价的90%。</b></div>");
        }else
        {
            allss.append("<div><b>7）享权车辆已被转让给第三人。</b></div>");
        }


        allss.append("<div><b>2、 下列任何原因造成享权车辆全损或推定全损，车主不能就相关损失享受服务：</b></div>");
        allss.append("<div><b>1）车主的故意行为、重大过失、欺诈、不诚实、违法犯罪行为；</b></div>");
        allss.append("<div><b>2）车主使用、维护、保管不当；</b></div>");
        allss.append("<div><b>3）享权车辆内在或潜在缺陷、自然磨损、自然损耗；</b></div>");
        allss.append("<div><b>4）战争、敌对行为、军事行动、武装冲突、恐怖主义活动、罢工、暴动、骚乱；</b></div>");
        allss.append("<div><b>5）核爆炸、核裂变、核聚变；</b></div>");
        allss.append("<div><b>6）放射性污染及其他各种环境污染；</b></div>");
        allss.append("<div><b>7）行政行为、司法行为。</b></div>");

        allss.append("<div><b>3、下列损失、费用和责任，不在服务范围内：</b></div>");
        allss.append("<div><b>1）在车辆置换过程中，因更换非BMW或MINI品牌新车产生的费用；</b></div>");
        allss.append("<div><b>2）任何形式的人身伤害、财产损失，及除本服务合同第六条所列“机动车置换费用”外的其他任何费用支出；</b></div>");
        allss.append("<div><b>3）服务过程中所产生的任何间接损失、赔偿责任；</b></div>");
        allss.append("<div><b>4）因未投保本服务合同第五条约定的机动车辆保险任一险种承保的保险事故造成的车辆全损或推定全损，或者该险种的保险期间于保险事故发生时已届满。</b></div>");

        allss.append("<div><b>八、<u>合同终止：</u></b></div>");
        allss.append("<div>&nbsp;&nbsp;除法律另有规定和本服务合同另有约定外，服务期限开始后解除本服务合同的，授权经销商应当将已收取的服务费，按照日比例扣除自服务期限的生效日期起至合同解除之日止应收取的部分后，退还车主。</div>");
        allss.append("<div>&nbsp;&nbsp;一旦车主就享权车辆获得了机动车置换服务，无论该等服务是由授权经销商或者按照本服务合同第十条的规定由中国大地财产保险股份有限公司上海分公司建议的其他授权经销商提供，在机动车置换服务提供完毕后，本服务合同自动终止。</div>");

        allss.append("<div><b>九、<u>服务期限：</u></b></div>");



        allss.append("<div>&nbsp;&nbsp;本服务合同项下服务期限的生效日期为"+paytime+"，服务期限为：</div>");
        allss.append("<div>&nbsp;&nbsp;1、\t新车：至享权车辆车龄的第36个月，车龄自享权车辆首次购车发票开具之日起计算；</div>");
        allss.append("<div>&nbsp;&nbsp;2、\t二手车：本服务合同生效后连续24个月。</div>");

        allss.append("<div><b>十、<u>申请资料：</u></b></div>");
        allss.append("<div>&nbsp;&nbsp;享权车辆发生本服务合同约定的事故后，车主可以通过联系授权经销商申请服务，并按照要求提交所需材料。如无法联系授权经销商，可通过联系此服务之保险保障和承保服务提供商中国大地财产保险股份有限公司上海分公司申请服务，咨询热线电话400-610-6200。</div>");
        allss.append("<div><b>1、车主需提供确认享权车辆事故性质、原因、损失程度等有关证明和资料，包括但不限于：</b></div>");
        allss.append("<div>1）享权车辆发生全损或推定全损时仍处于生效状态的机动车辆商业保险和机动车交通事故责任强制保险的保险单复印件；</div>");
        allss.append("<div>2）本服务合同；</div>");
        allss.append("<div>3）享权车辆行驶本复印件、车主驾驶本复印件、车主身份证或企业营业执照复印件；</div>");
        allss.append("<div>4）享权车辆首次购车发票复印件；</div>");
        allss.append("<div>5）机动车辆保险之保险公司已按照全损、推定全损赔付完毕的证明（划账水单原件，以及一次性赔偿协议原件或机动车辆保险之保险公司盖章的复印件）；</div>");
        allss.append("<div>6）享权车辆全损或推定全损后处理手续（权益转让书、拍卖或注销解体手续）；</div>");
        allss.append("<div>7）证明事故发生原因的证明，属于道路交通事故的提供事故证明（证明材料复印件，需机动车辆保险之保险公司盖章）。</div>");
        allss.append("<div><b>2、车主需提供机动车置换补偿的相关材料、单据，包括但不限于：</b></div>");
        allss.append("<div>1）新车的购车合同的复印件；</div>");
        allss.append("<div>2）新车的车辆购置税完税证明的复印件；</div>");
        allss.append("<div>3）新车的机动车辆保险的保险单的复印件或电子保单及保费发票的复印件；</div>");
        allss.append("<div>4）新车的机动车置换服务的服务合同和服务费发票的复印件，如有；</div>");
        allss.append("<div>5）新车交付所涉及项目的费用发票的复印件。</div>");
        allss.append("<div>&nbsp;&nbsp;因车主未能提交任何上述约定的申请资料，导致部分或全部服务责任无法确定，对于无法确定的部分不能享受服务。</div>");
        allss.append("<div>&nbsp;&nbsp;若由于车主原因导致未完成新车置换或授权经销商未能提供服务的，车主不能主张本服务合同项下的任何服务和/或补偿。</div>");

        allss.append("<div><b>十一、<u>服务流程：</u></b></div>");

        allss.append("<div align=\"center\"><img src=\""+pathurl+"images/anllianlog1.png\" alt=\"\"/></div>");
        allss.append("<div>车主声明：</div>");
        allss.append("<div>&nbsp;&nbsp;提供本服务的授权经销商已通过上述书面形式向本人详细介绍并提供了本服务的服务内容、合同和条款。本人已完整阅读本服务合同，并清楚知晓本服务合同的各项条款，特别是保持本服务有效的“服务前提”、将导致无法获得服务的“除外情况”和申请服务所必需的“申请资料”等条款。</div>");
        allss.append("<div>&nbsp;&nbsp;本人同意，提供本服务的授权经销商及其关联公司可以将本服务合同信息及个人信息提供给为其提供相关服务的任何其他实体（包括但不限于为本服务合同提供承保服务的保险公司、第三方服务提供商）、提供数据分析、处理的技术服务公司，以及任何实际的或可能的参与人、代理人。前述信息接收方应有权依据其各自的规定、适用的法律、法规及合约使用信息｡</div>");
        allss.append("<div>&nbsp;&nbsp;本人已充分理解并接受上述内容，同意以此作为订立服务合同的依据，并愿意遵守本服务合同的条款。</div>");
        allss.append("<br>");
        allss.append("<hr style= \"border:1px dashed #000\" />");

        allss.append("<br>");
        allss.append("<br>");


        allss.append("<div>车主签字（适用于个人客户）：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>车主盖章（适用于企业客户）：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>授权经销商：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>盖  章：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("</body>");
           if  (productid.startsWith("BMW04")) {
               allss.append("<br>");
               allss.append("<br>");
               allss.append("<br>");
               allss.append("<br>");
               allss.append("<br>");
               allss.append("<br>");
               allss.append(contractPrintBmwKey(data));
           }
        return allss.toString();
    }
    public String contractPrintHQ(String data)
    {
        String pathurl=GetPathurl();
        String contractno="",codeurl="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        // result=checkSign(nonce,time,sign,appid);
        //if (!result.equals(""))return result;

        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();else return  GetErrorString(1,"");
        if (jsonObject.keySet().contains("codeurl"))codeurl=jsonObject.get("codeurl").getAsString();

        StringBuilder allss = new StringBuilder();
        String dealerno="";
        int carkey=0;
        String sqlstring="select *,(select carkey from insuranceproduct where productid=contract.productid LIMIT 0,1 ) as carkey,(select retailprice from bmwproduct where productid=contract.productid LIMIT 0,1 ) as retailprice1 ,(select FROM_UNIXTIME(billbmw.ttime,'%Y-%m-%d') from billbmw where contractno=contract.contractno and in_or_out=-1 and (tradetype='订单支付消费' or tradetype='支付') ORDER BY ttime desc LIMIT 0,1 ) as paytime from contract where contractno=?";
        Map<String,Object>map=queryForMap(jdbcTemplate,sqlstring,contractno);
        String brand="",vehicletype="",vin="",Invoiceno="",Invoicedate="",guideprice="",Invoiceprice="",pname="";
        String cname="",IdNo="",caddress="",insurancecompany="",Businessinsurancepolicyno="",insurancepolicyno="",productid="",companyname="";
        String contact_person="",contact_tel="";
        String keytype="";
        int iscompany=0,isnewcar=0;
        String retailprice="0";
        String paytime="";
        if (map!=null)
        {
            paytime=setting.NUllToSpace(map.get("paytime"));
            brand=setting.NUllToSpace(map.get("brand"));
            vehicletype=setting.NUllToSpace(map.get("vehicletype"));
            vin=setting.NUllToSpace(map.get("vin"));
            Invoiceno=setting.NUllToSpace(map.get("Invoiceno"));
            Invoicedate=setting.NUllToSpace(map.get("Invoicedate"));
            guideprice=setting.NUllToSpace(map.get("guideprice"));
            Invoiceprice=setting.NUllToSpace(map.get("Invoiceprice"));
            dealerno=setting.NUllToSpace(map.get("dealerno"));
            cname=setting.NUllToSpace(map.get("cname"));
            IdNo=setting.NUllToSpace(map.get("IdNo"));
            caddress=setting.NUllToSpace(map.get("address"));
            insurancecompany=setting.NUllToSpace(map.get("insurancecompany"));
            Businessinsurancepolicyno=setting.NUllToSpace(map.get("Businessinsurancepolicyno"));
            productid=setting.NUllToSpace(map.get("productid"));
            retailprice=setting.NUllToSpace(map.get("retailprice"));
            if (productid.startsWith("BMW04"))       retailprice=setting.NUllToSpace(map.get("retailprice1"));
            insurancepolicyno=setting.NUllToSpace(map.get("forceno"));
            companyname=setting.NUllToSpace(map.get("companyname"));
            iscompany=setting.StrToInt(map.get("iscompany")+"");
            carkey=setting.StrToInt(map.get("carkey")+"");
            contact_person=setting.NUllToSpace(map.get("contact_person"));
            contact_tel=setting.NUllToSpace(map.get("contact_tel"));
            pname=setting.NUllToSpace(map.get("pname")).trim();
            keytype=setting.NUllToSpace(map.get("keytype"));
            isnewcar=setting.StrToInt(map.get("isnewcar")+"");

        }

        sqlstring="select * from dealer where dealerno=?";
        map=queryForMap(jdbcTemplate,sqlstring,dealerno);
        String dealername="",address="",tel="";
        if (map!=null)
        {
            dealername=setting.NUllToSpace(map.get("dealername"));
            address=setting.NUllToSpace(map.get("address"));
            tel=setting.NUllToSpace(map.get("tel"));
        }

        if (companyname.equals(""))companyname=cname;
        allss.append("<head> ");
        allss.append("<style>");
        allss.append(".print-title{ font-size:18px;color:#403f45} ");
        allss.append(".print-body{ font-size:14px;color:#403f45} ");
        // allss.append(".table-print{ font-size:12px;font-color:#403f45} ");
        allss.append(".table-print{border:1px solid #000;border-collapse:collapse;width:100%;margin-bottom:10px;}/*打印表格的效果*/\n" +
                ".table-print th,.table-print td{padding:2px 4px;font-size:14px;color:#403f45!important;font-weight:normal;}\n");

        allss.append(".table-nobody{border:0px solid #fff;border-collapse:collapse;width:100%;margin-bottom:10px;}/*打印表格的效果*/\n");
        allss.append("</style>");
        allss.append("</head> ");

        // allss.append("<div align=\"center\"><img src=\""+pathurl+"images/porsche.png\" width=\"120\" height=\"61\"  alt=\"\"/></div>");
        // allss.append("<div align=\"center\" width=\"50%\" ><img src=\""+pathurl+"images/porsche.png\"  alt=\"\"/></div>");
        allss.append("<br>");
        String title="一汽红旗品牌保障服务合同";
        allss.append("<div align=\"center\" class=\"print-title\" ><b>" + title + "</b></div>");

        //  allss.append("<br>");
        //allss.append("<div align=\"center\" class=\"print-body\" >车辆置换服务合同</div>");

        allss.append("<br>");
        allss.append("<div align=\"right\"  class=\"print-body\" >编号：【"+contractno+"】</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >车主："+companyname+"</div>");
        allss.append("<br>");
        allss.append("<div align=\"left\"  class=\"print-body\" >授权经销商："+dealername+"</div>");

        allss.append("<br>");
        allss.append("<div align=\"left\" class=\"print-body\">&nbsp;&nbsp;&nbsp;&nbsp;通过签署本服务合同（以下简称“服务合同”），车主同意接受由授权经销商提供的品牌保障服务（以下简称“服务”或“本服务”）。为了便于授权经销商向车主提供更贴心的服务，车主需仔细阅读本服务合同条款，并准确填写下列信息：</div>");
        allss.append("<br>");
        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">一、享权车辆信息：</font> </td></tr>");
        StringBuilder ss = new StringBuilder();
        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" ><tr>");
        ss.append("<td>品   牌：<u>"+foramtss(20,brand)+"</u></td>");
        ss.append("<td>车   型：<u>"+foramtss(20,vehicletype)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">车辆用途：     非   营   运</td></tr>");
        ss.append("<tr><td colspan=\"2\">车架号码（VIN码）：<u>"+foramtss(20,vin)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">购买时间（购车发票时间）：<u>"+foramtss(20,Invoicedate)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">购车发票号码：<u>"+foramtss(20,Invoiceno)+"</u></td></tr>");
        ss.append("<tr><td>新车指导价：<u>"+foramtss(20,formatMoney(guideprice))+"</u></td>");
        ss.append("<td> 车辆购买价格（购车发票价）：<u>"+foramtss(20,formatMoney(Invoiceprice))+"</u></td></tr>");

        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");


        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">二、服务产品销售及提供方信息：</font> </td></tr>");
        ss = new StringBuilder();
        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" >");
        ss.append("<tr><td colspan=\"2\">授权经销商名称：<u>"+foramtss(30,dealername)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址：<u>"+foramtss(30,address)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">电&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;话：<u>"+foramtss(30,tel)+"</u></td></tr>");
        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");




        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">三、客户（车主）信息：</font> </td></tr>");
        ss = new StringBuilder();


        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" >");
        if (iscompany==0)
        {
            ss.append("<tr><td colspan=\"2\">姓名/企业名称：<u>"+foramtss(30,cname)+"</u></td></tr>");
            ss.append("<tr><td colspan=\"2\">身份证号码：<u>"+foramtss(30,IdNo)+"</u></td></tr>");
            ss.append("<tr><td colspan=\"2\">或统一社会信用代码：<u>"+foramtss(30,"")+"</u></td></tr>");
        }else
        {
            ss.append("<tr><td colspan=\"2\">姓名/企业名称：<u>"+foramtss(30,companyname)+"</u></td></tr>");
            ss.append("<tr><td colspan=\"2\">身份证号码：<u>"+foramtss(30,"")+"</u></td></tr>");
            ss.append("<tr><td colspan=\"2\">或统一社会信用代码：<u>"+foramtss(30,IdNo)+"</u></td></tr>");
        }


        ss.append("<tr><td colspan=\"2\">地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址：<u>"+foramtss(30,caddress)+"</u></td></tr>");
        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");

        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">四、商业保险信息：</font> </td></tr>");
        ss = new StringBuilder();
        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" >");
        ss.append("<tr><td colspan=\"2\">投保机动车辆保险公司：<u>"+foramtss(30,insurancecompany)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">交 强 险 保 单 号 码：<u>"+foramtss(30,insurancepolicyno)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">商 业 险 保 单 号 码：<u>"+foramtss(30,Businessinsurancepolicyno)+"</u></td></tr>");
        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");
        String startdate="";
        if (isnewcar==1)
            startdate=formatdate(Invoicedate,"yyyy年MM月dd日");
        else
            startdate=formatdate(paytime,"yyyy年MM月dd日");

        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\" colspan=\"6\"><font color=\"#fff\">五、服务产品及内容：</font> </td></tr>");
        allss.append("<tr>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\"><b>序号</b></td>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\"><b>产品</b></td>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\"><b>是否\n" +
                "选择</b></td>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\"><b>服务起始\n" +
                "日期</b></td>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\"><b>服务终止\n" +
                "日期</b></td>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\" width=\"50%\"><b>保障内容</b></td>");
        allss.append("</tr>");
        sqlstring="select * from insuranceproduct where productid in ('"+productid.replace(",","','")+"')";
        map=queryForMap(jdbcTemplate,sqlstring);
        int ii=1;

        if (map!=null)
        {
            String pdetail=map.get("detail")+"";


            String[] pdtails=pdetail.split(",");
            sqlstring="select * from insuracedetail where 1=0 ";
            for (int i=0;i<pdtails.length;i++)
            {
                sqlstring+=" or tid="+pdtails[i];
            }
            List<Map<String,Object>>list=queryForList(jdbcTemplate,sqlstring);

            for (int i=0;i<list.size();i++)
            {
                map=list.get(i);
                pname=map.get("pname")+"";

                allss.append("<tr><td align=\"center\" bgcolor=\"#fff\">"+ii+"</td>");
                allss.append("<td align=\"center\" bgcolor=\"#fff\">"+pname+"</td>");
                allss.append("<td align=\"center\" bgcolor=\"#fff\">是</td>");
                allss.append("<td align=\"center\" bgcolor=\"#fff\">"+startdate +
                        "</td>");
                allss.append("<td align=\"center\" bgcolor=\"#fff\">"+addYear(startdate,setting.StrToInt(map.get("year")+"")) +
                        "</td>");
                allss.append("<td align=\"left\" bgcolor=\"#fff\">"+map.get("disc") +
                        "</td></tr>");
                ii++;
            }

        }

        allss.append("</table>");
        allss.append("<br>");
        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\" ><font color=\"#fff\">六、其他备注说明</font> </td></tr>");
        allss.append("<tr>");
        ss = new StringBuilder();
        ss.append("<div><b>客户/车主申明：</b></div>");
        ss.append("<div><b>本人确认提供给本《一汽红旗品牌保障服务合同》（“本服务合同”）项下一汽红旗授权经销商（“授权经销商”）并填写在本服务合同中的信息是真实和准确的。</b></div>");
        ss.append("<div><b>本人已经收悉本服务合同并仔细阅读相关条款（包括通用条款和各保障服务产品条款），尤其是客户的义务、服务保障除外情形、服务合同终止等条款内容，并对授权经销商就本服务合同内容的说明和提示完全理解，没有异议。本服务合同申请人需遵守本服务合同的约定方能享受本服务合同的服务权益。对上述情况，本人签字确认如下：</b></div>");
        ss.append("<div><b>"+foramtss(50,"客户签名/盖章：")+" 经销商盖章（盖章生效）："+dealername+"</b></div>");
        ss.append("<div><b>签单日期：</b></div>");
        allss.append("<td align=\"left\" bgcolor=\"#fff\">"+ss.toString()+"</td>");
        allss.append("</tr>");
        allss.append("</table>");
        allss.append("<br>");

        allss.append("<div><b>七、<u>服务前提：</u></b></div>");
        allss.append("<div>7.1 全损保障（机动车置换）服务</div>");
        allss.append("<div>&nbsp;&nbsp;1、享权车辆：非营运9座及9座以下一汽红旗品牌乘用车</div>");
        allss.append("<div>&nbsp;&nbsp;2、\t车龄要求：车主购买本服务时享权车辆车龄自享权车辆首次购车发票开具之日起计算不得超过十二（12）个月。享权车辆的所有权未发生过转让。</div>");
        allss.append("<div>&nbsp;&nbsp;3、\t机动车辆保险投保要求：在本服务合同约定的服务期限内车主须为享权车辆投保机动车交通事故责任强制保险和足额投保以下机动车辆商业保险险种，且须保持所有险种于本服务合同约定的服务期限内持续有效。</div>");
        allss.append("<div>1)机动车损失保险；</div>");
        allss.append("<div>2)\t______________________（如有）。</div>");

        allss.append("<div>享权车辆因机动车辆商业保险的上述任一险种承保的保险事故造成全损或推定全损，且该险种和机动车交通事故责任强制保险于保险事故发生时保险期间尚未届满，车主方可向授权经销商申请服务。全损或推定全损以承保享权车辆上述机动车辆商业保险之保险公司的定损结果为惟一依据。</div>");
      /*  allss.append("<div>7.2\t 钥匙保障（机动车辆钥匙重置）服务</div>");
        allss.append("<div>&nbsp;&nbsp;1、享权车辆：非营运9座及9座以下一汽红旗品牌乘用车</div>");
        allss.append("<div>&nbsp;&nbsp;2、车龄要求：新车车主购买本服务时享权车辆车龄自享权车辆购车发票开具之日起计算不得超过30天。</div>");
        allss.append("<div>&nbsp;&nbsp;3、享权车辆的所有权未发生过转让。</div>");
        allss.append("<div>&nbsp;&nbsp;4、享权车辆发生了车辆钥匙丢失或者被盗，车主方可向授权经销商申请重置车辆钥匙服务。</div>");*/

        allss.append("<div><b>八、<u>服务承诺：</u></b></div>");
        allss.append("<div>8.1 全损保障（机动车置换）服务</div>");
        allss.append("<div>&nbsp;&nbsp;享权车辆在服务期限内遭受全损或推定全损，且满足本服务合同第五条规定的服务前提，车主可向授权经销商申请服务。授权经销商将协助车主置换一辆红旗品牌的新车，可供选择的新车品牌视该授权经销商的具体授权情况而定，置换新车过程中产生的车辆置换费用由授权经销商承担。新车发票价（其定义与享权车辆发票价一致）高于享权车辆发票价的，超出部分由车主自行承担。新车的官方建议零售价不得低于享权车辆官方建议零售价的90%。</div>");
        allss.append("<div>&nbsp;&nbsp;本服务中所指的机动车置换费用包括以下两部分：</div>");

        allss.append("<div>1、车辆折旧费用：\n" +
                "车辆折旧费用 = 享权车辆发票价x已使用月数 x月折旧率（0.6%）\n" +
                "其中，“已使用月数”指享权车辆购车发票显示的购车日期至发生全损或推定全损事故之日计算的已使用月数，不足一月，不计算折旧。\n</div>");
        allss.append("<div>2、额外重置费用：\n" +
                "在新车购置过程中所产生的费用，包括新车登记费用、新车交付费用、新车的车辆购置税、新车相关保险费等。其金额为上述各项费用金额之和，但不超过享权车辆发票价的15%；超出部分由车主自行承担。\n" +
                "上述税费、保险费及服务费之金额应当符合同一时期同类税种、保险产品和服务的税率或市场公允价格。享权车辆发票价以其首次购车发票所载明之价税合计金额（不含车辆购置税）为准。\n</div>");
   /*     allss.append("<div>8.2 钥匙保障（机动车辆钥匙重置）服务</div>");
        allss.append("<div>&nbsp;&nbsp;享权车辆在服务期限内发生了车辆钥匙丢失或被盗，且满足本服务合同第四条规定的服务前提，车主可按照本服务合同条款向授权经销商提出重置车辆钥匙服务。授权经销商将协助车主重置同品牌、同型号和同规格的车辆钥匙，重置车辆钥匙过程中产生的机动车辆钥匙重置费用由授权经销商承担。机动车辆钥匙重置费用包括重置车辆钥匙所需的零配件费用（含钥匙配码的成本）和工时费。\n</div>");
        allss.append("<div>&nbsp;&nbsp;根据享权车辆所适用的车辆钥匙类型不同，在服务期限内，车主可享受重置同品牌 、同型号和规格车辆钥匙上限2把，具体补偿如下：\n" +
                "1、\t购买了本服务的新车车主：\n" +
                "补偿标准钥匙上限2把\n</div>");*/



        allss.append("<div><b>九、<u>除外情况：</u></b></div>");

        allss.append("<div><b>9.1全损保障（机动车置换）服务\n" +
                "1、\t出现下列任一情形时，车主不能享受服务：\n" +
                "1)\t车主要求提供服务的享权车辆信息和/或车主身份信息与本服务合同第一条和第三条记载的信息不一致；\n" +
                "2)\t车主要求提供服务的享权车辆用途与本服务合同中限定不一致；\n" +
                "3)\t享权车辆遭受的事故发生在本服务合同约定服务期限生效日期前；\n" +
                "4)\t享权车辆因遭受事故仅导致部分损失，未达到本服务合同所约定的车辆全损或推定全损标准（以承保享权车辆机动车辆商业保险之保险公司的定损结果为唯一依据）；\n" +
                "5)\t享权车辆遭受事故时不存在相关机动车辆保险保障，或遭受事故后因不符合法律规定或相关机动车辆保险约定的理赔条件、未获得与全损或推定全损相应的机动车辆保险赔偿；\n" +
                "6)\t享权车辆经过改装或拼装，其工况受到影响；\n" +
                "7)\t享权车辆已被转让给第三人。\n" +
                "8)\t未经授权经销商书面同意，授权经销商擅自变更服务合同中的责任和义务\n" +
                "2、 下列任何原因造成享权车辆全损或推定全损，车主不能就相关损失享受服务：\n" +
                "1)\t车主的故意行为、重大过失、欺诈、不诚实、违法犯罪行为；\n" +
                "2)\t车主使用、维护、保管不当；\n" +
                "3)\t享权车辆内在或潜在缺陷、自然磨损、自然损耗；\n" +
                "4)\t战争、敌对行为、军事行动、武装冲突、恐怖主义活动、罢工、暴动、骚乱；\n" +
                "5)\t核爆炸、核裂变、核聚变；\n" +
                "6)\t放射性污染及其他各种环境污染；\n" +
                "7)\t行政行为、司法行为。\n" +
                "3、下列损失、费用和责任，不在服务范围内：\n" +
                "1)\t新车发票价超出享权车辆发票价的部分；\n" +
                "2)\t额外重置费用超出本合同第六条所约定上限的部分；\n" +
                "3)\t在车辆置换过程中，因更换非红旗品牌新车产生的费用；\n" +
                "4)\t任何形式的人身伤害、财产损失，及除本服务合同第六条所列“车辆置换费用”外的其他任何费用支出；\n" +
                "5)\t服务过程中所产生的任何间接损失、赔偿责任；\n" +
                "6)\t因未投保本服务合同第五条约定的机动车辆保险任一险种承保的保险事故造成的车辆全损或推定全损，或者该险种的保险期间于保险事故发生时已届满。\n" +
                "7)\t道路救援、拖车、出租车或者租车等服务费用\n</b></div>");
      /*  allss.append("<div><b>9.2 钥匙保障（机动车辆钥匙重置）服务\n" +
                "1、\t出现下列任一情形时，车主不能享受服务：\n" +
                "1)\t损失发生在服务合同生效前，或车主未在服务合同规定的服务期限内提出重置要求；\n" +
                "2)\t损失发生在中华人民共和国（为本服务合同之目的，不含香港、澳门特别行政区和台湾地区）境外；\n" +
                "3)\t车主要求重置的车辆钥匙及其所属的机动车辆信息、使用性质与本服务合同中记载不一致的；\n" +
                "4)\t重置不同品牌、型号或规格的车辆钥匙；\n" +
                "5)\t车主的故意行为、欺诈、不诚实、违法犯罪行为；\n" +
                "6)\t车主使用、维护不当；\n" +
                "7)\t车辆钥匙内在或潜在缺陷、自然磨损、自然损耗； \n" +
                "8)\t行政行为、司法行为。\n" +
                "2、\t下列损失、费用和责任，不在服务范围内：\n" +
                "1）任何形式的人身伤害、财产损失，及除本服务合同第五条所列“机动车辆钥匙重置费用”外的其他任何费用支出；\n" +
                "2）\t服务过程中所产生的任何间接损失、赔偿责任；\n" +
                "3）\t道路救援、拖车、出租车或者租车等服务费用；\n" +
                "4）\t任何情况下更换车锁或维修车锁的费用；\n" +
                "5）\t车辆在处于查封、扣押期间，导致的车辆重置费用\n</b></div>");*/


        allss.append("<div><b>十、<u>合同终止：</u></b></div>");
        allss.append("<div>&nbsp;&nbsp;除法律另有规定和本服务合同另有约定外，若车主于服务期限开始后请求取消本服务合同的，授权经销商应当在按照日比例从已收取的服务费中扣除自服务期限生效日期起算至服务合同解除之日止应收取服务费后的余额部分退还车主。服务合同解除之日以车主签署取消订单证明载明的日期为准。\n" +
                "一旦车主就享权车辆获得了车辆置换服务和/或机动车辆钥匙重置服务，无论是从授权经销商处获得或者其他授权经销商处获得，在车辆置换服务和/或机动车辆钥匙重置服务提供完毕后，本服务合同自动终止。\n</div>");

        allss.append("<div><b>十一、<u>服务期限：</u></b></div>");



        allss.append("<div>&nbsp;&nbsp;本服务合同项下服务期限的生效日期为"+paytime+"，服务期限至享权车辆车龄的第36个月届满时自动终止，车龄自享权车辆首次购车发票开具之日起计算。</div>");

        allss.append("<div><b>十二、<u>申请资料：</u></b></div>");
        allss.append("<div>&nbsp;&nbsp;享权车辆发生本服务合同约定的事故后，车主可以通过联系授权经销商申请服务，并按照要求提交所需材料。如无法联系授权经销商，可通过联系此服务之提供商申请服务，热线电话400-855-9796。</div>");
        allss.append("<div>12.1 全损保障（机动车置换）服务\n" +
                "1、车主需提供确认享权车辆事故性质、原因、损失程度等有关证明和资料，包括但不限于：\n" +
                "1）享权车辆发生全损或推定全损时仍处于生效状态的机动车辆商业保险和机动车交通事故责任强制保险的保险单复印件；\n" +
                "2）本服务合同；\n" +
                "3）享权车辆行驶本复印件、车主驾驶本复印件、车主身份证或企业营业执照复印件；\n" +
                "4）享权车辆首次购车发票复印件；\n" +
                "5）机动车辆保险之保险公司已按照全损、推定全损赔付完毕的证明（划账水单原件，以及一次性赔偿协议原件或机动车辆保险之保险公司盖章的复印件）；\n" +
                "6）享权车辆全损或推定全损后处理手续（权益转让书、拍卖或注销解体手续）；\n" +
                "7）证明事故发生原因的证明，属于道路交通事故的提供事故证明（证明材料复印件，需机动车辆保险之保险公司盖章）。\n" +
                "2、车主需提供车辆置换补偿的相关材料、单据，包括但不限于：\n" +
                "1）新车的购车合同的复印件；\n" +
                "2）新车的车辆购置税完税证明的复印件；\n" +
                "3）新车的机动车辆保险的保险单的复印件或电子保单及保费发票的复印件4）新车的车辆置换服务的服务合同和服务费发票的复印件，如有；\n" +
                "5）新车交付所涉及项目的费用发票的复印件\n" +
                "6）车主需将机动车辆保险之全损或推定全损的赔付款项支付给提供车辆置换服务的授权经销商。\n</b></div>");

       /* allss.append("<div>10.2 钥匙保障（机动车辆钥匙重置）服务\n" +
                "1、车主需提供确认享权车辆钥匙的损失性质、原因、程度等有关证明和资料，包括但不限于：\n" +
                "1)\t本服务合同；\n" +
                "2)\t已发生符合服务合同约定损失的证明（钥匙禁用截屏）；\n" +
                "3)\t索赔申请书；\n" +
                "4)\t相关明细单（ 工单 ）、结算单、发票或支付凭证； \n" +
                "5)\t其它与索赔有关的、必要的，并能证明损失性质、原因和程度的其他证明和资料。\n</div>");*/
        allss.append("<div>&nbsp;&nbsp;因车主未能提交任何上述约定的申请资料，导致部分或全部服务责任无法确定，对于无法确定的部分不能享受服务。</div>");
        allss.append("<div>&nbsp;&nbsp;若由于车主原因导致未完成新车置换或授权经销商未能提供服务的，车主不能主张本服务合同项下的任何服务和/或补偿。</div>");

        allss.append("<div><b>十三、<u>服务流程：</u></b></div>");

        allss.append("<div align=\"center\"><img src=\""+pathurl+"images/anllianloghq.png\" alt=\"\"/></div>");
        allss.append("<div>车主声明：</div>");
        allss.append("<div>&nbsp;&nbsp;提供本服务的授权经销商已通过上述书面形式向本人详细介绍并提供了本服务的服务内容和条款。本人已完整阅读本服务合同，并清楚知晓本服务合同的各项条款，特别是保持本服务有效的“服务前提”、将导致无法获得服务的“除外情况”和申请服务所必需的“申请资料”等条款。</div>");
        allss.append("<div>&nbsp;&nbsp;本人知悉并同意，提供本服务的授权经销商可以在必要范围内将本服务合同信息及个人信息提供给其他实体，包括：授权经销商会将合同信息提供给与其合作的、为车辆置换服务提供管理服务的第三方服务提供商；为服务合同提供承保服务的保险公司也需获得本合同内的享权车辆信息、车辆保险信息、车主信息及其联系电话。授权经销商提供上述信息的目的包括：及时、正确处理您的车辆置换服务申请；使本服务合同得到保险公司的及时承保；在合法范围内对服务购买和使用情况进行数据分析。授权经销商将采取协议约定等合理措施，要求前述信息接收方遵守适用的法律、法规及合约，妥善管理并仅为授权经销商指定的目的使用您的上述信息｡</div>");
        allss.append("<div>&nbsp;&nbsp;本人已充分理解并接受上述内容，同意以此作为订立服务合同的依据，并愿意遵守本服务合同的条款。</div>");
        allss.append("<br>");
        allss.append("<hr style= \"border:1px dashed #000\" />");

        allss.append("<br>");
        allss.append("<br>");


        allss.append("<div>车主签字（适用于个人客户）：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>车主盖章（适用于企业客户）：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>授权经销商：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>盖  章：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("</body>");
        return allss.toString();
    }
    public String contractPrintBmwKey(String data)
    {
        String pathurl=GetPathurl();
        String contractno="",codeurl="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();

        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();else return  GetErrorString(1,"");
        if (jsonObject.keySet().contains("codeurl"))codeurl=jsonObject.get("codeurl").getAsString();

        StringBuilder allss = new StringBuilder();
        String dealerno="";
        int carkey=0;
        String sqlstring="select *,(select carkey from insuranceproduct where productid=contract.productid LIMIT 0,1 ) as carkey,(select retailprice from bmwproduct where productid=contract.productid LIMIT 0,1 ) as retailprice1 ,(select FROM_UNIXTIME(billbmw.ttime,'%Y-%m-%d') from billbmw where contractno=contract.contractno and in_or_out=-1 and (tradetype='订单支付消费' or tradetype='支付') ORDER BY ttime desc LIMIT 0,1 ) as paytime from contract where contractno=?";
        Map<String,Object>map=queryForMap(jdbcTemplate,sqlstring,contractno);
        String brand="",vehicletype="",vin="",Invoiceno="",Invoicedate="",guideprice="",Invoiceprice="",pname="";
        String cname="",IdNo="",caddress="",insurancecompany="",Businessinsurancepolicyno="",insurancepolicyno="",productid="",companyname="";
        String contact_person="",contact_tel="";
        String keytype="";
        int iscompany=0,isnewcar=0;
        String retailprice="0";
        String paytime="",icode="";

        if (map!=null)
        {
            paytime=setting.NUllToSpace(map.get("paytime"));
            brand=setting.NUllToSpace(map.get("brand"));
            vehicletype=setting.NUllToSpace(map.get("vehicletype"));
            vin=setting.NUllToSpace(map.get("vin"));
            Invoiceno=setting.NUllToSpace(map.get("Invoiceno"));
            Invoicedate=setting.NUllToSpace(map.get("Invoicedate"));
            guideprice=setting.NUllToSpace(map.get("guideprice"));
            Invoiceprice=setting.NUllToSpace(map.get("Invoiceprice"));
            dealerno=setting.NUllToSpace(map.get("dealerno"));
            cname=setting.NUllToSpace(map.get("cname"));
            IdNo=setting.NUllToSpace(map.get("IdNo"));
            caddress=setting.NUllToSpace(map.get("address"));
            insurancecompany=setting.NUllToSpace(map.get("insurancecompany"));
            Businessinsurancepolicyno=setting.NUllToSpace(map.get("Businessinsurancepolicyno"));
            productid=setting.NUllToSpace(map.get("productid"));
            retailprice=setting.NUllToSpace(map.get("retailprice"));
            if (productid.startsWith("BMW04"))       retailprice=setting.NUllToSpace(map.get("retailprice1"));
            insurancepolicyno=setting.NUllToSpace(map.get("forceno"));
            companyname=setting.NUllToSpace(map.get("companyname"));
            iscompany=setting.StrToInt(map.get("iscompany")+"");
            carkey=setting.StrToInt(map.get("carkey")+"");
            contact_person=setting.NUllToSpace(map.get("contact_person"));
            contact_tel=setting.NUllToSpace(map.get("contact_tel"));
            pname=setting.NUllToSpace(map.get("pname")).trim();
            keytype=setting.NUllToSpace(map.get("keytype"));
            isnewcar=setting.StrToInt(map.get("isnewcar")+"");
            icode=setting.NUllToSpace(map.get("icode"));
        }
        sqlstring="select * from dealer where dealerno=?";
        map=queryForMap(jdbcTemplate,sqlstring,dealerno);
        String dealername="",address="",tel="";
        if (map!=null)
        {
            dealername=setting.NUllToSpace(map.get("dealername"));
            address=setting.NUllToSpace(map.get("address"));
            tel=setting.NUllToSpace(map.get("tel"));
        }

        if (productid.equals("BMW03")) {
            keytype = "标准钥匙1年期1把";
            pname=keytype;
        }
       else if (!productid.startsWith("BMW06"))
        {
            keytype=pname;
        }
        if (pname.contains("+")) {
            pname = pname.substring(pname.indexOf("+") + 1);
            keytype=pname;
        }
//        allss.append("<table class=\"table-print\" border=\"0\"  ><tr>");
////        allss.append("<td align=\"left\" >Allianz Worldwide Partners</td>");
////        allss.append("<td rowspan=\"2\" align=\"right\"><img src=\"images/anllianlog.png\" alt=\"\"/></td></tr>");
////        allss.append("<td align=\"left\">安世联合商务服务(北京)有限公司</td></tr>");
//////        allss.append("</table>");
        allss.append("<head> ");
        allss.append("<style>");
        allss.append(".print-title{ font-size:18px} ");
        allss.append(".print-body{ font-size:14px} ");
        allss.append(".print-hint{ font-size:6px} ");
        // allss.append(".table-print{ font-size:12px;font-color:#403f45} ");
        allss.append(".table-print{border:1px solid #000;border-collapse:collapse;width:100%;margin-bottom:10px;}/*打印表格的效果*/\n" +
                ".table-print th,.table-print td{padding:2px 4px;font-size:14px;color:#403f45!important;font-weight:normal;}\n");

        allss.append(".table-nobody{border:0px solid #fff;border-collapse:collapse;width:100%;margin-bottom:10px;}/*打印表格的效果*/\n");
        allss.append("</style>");
        allss.append("</head> ");
        allss.append("<body>");

        codeurl=qrCodeUtil.createQrCode(pathurl+codeurl);
        allss.append("<hr/>");
        if (icode.equals("CPICB"))
            allss.append("<div align=\"right\" class=\"print-hint\">本服务由中国太平洋财产保险股份有限公司北京分公司</div>");
        else
        allss.append("<div align=\"right\" class=\"print-hint\">本服务由中国太平洋财产保险股份有限公司江苏分公司</div>");
        allss.append("<div align=\"right\" class=\"print-hint\">提供保险保障和承保服务</div>");
        String title=brand+pname+"服务";
        title="BMW/MINI钥匙保障服务";
        allss.append("<div align=\"center\" class=\"print-title\">" + title + "</div>");
        allss.append("<div align=\"center\" class=\"print-body\">机动车辆钥匙重置服务凭证</div>");
        allss.append("<div align=\"right\"><img src=\""+pathurl+codeurl+"\"  style=\"width: 100px;\"  alt=\"\"/></div>");
        allss.append("<div align=\"right\" size=\"2\">扫描二维码，验证服务合同与保障信息</div>");
        allss.append("<div align=\"right\">合同编号：【"+contractno+"】</div>");
        allss.append("<br>");
        allss.append("<div align=\"left\" class=\"print-body\">车主："+cname+"</div>");
        allss.append("<br>");
        allss.append("<div align=\"left\" class=\"print-body\">授权经销商："+dealername+"</div>");
        allss.append("<div align=\"left\" class=\"print-body\">&nbsp;&nbsp;&nbsp;&nbsp;通过签署本机动车辆钥匙重置服务凭证（以下简称“服务凭证”），车主同意接受由授权经销商提供的机动车辆钥匙重置服务（以下简称“服务”或“本服务”）。为了便于授权经销商向车主提供更贴心的服务，车主需仔细阅读本服务凭证条款，并准确填写下列信息：</div>");
        allss.append("<br>");
        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">一、享权车辆和车辆钥匙信息：</font> </td></tr>");
        StringBuilder ss = new StringBuilder();
        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" ><tr>");
        ss.append("<td>品   牌："+brand+"</td>");
        ss.append("<td>车   型："+vehicletype+"</td></tr>");
        ss.append("<tr><td colspan=\"2\">车辆用途：     非   营   运</td></tr>");
        ss.append("<tr><td colspan=\"2\">车架号码（VIN码）："+vin+"</td></tr>");
        ss.append("<tr><td colspan=\"2\">购买时间（购车发票时间）："+Invoicedate+"</td></tr>");
        ss.append("<tr><td colspan=\"2\">车辆钥匙型号和规格："+keytype+"</td></tr>");
      //  ss.append("<tr><td colspan=\"2\">已选钥匙保障服务（机动车辆钥匙重置服务）详情：第五条第三款内对应车主类别</td></tr>");
        ss.append("<tr><td>钥匙保障服务（机动车辆钥匙重置服务）建议零售价："+formatMoney(retailprice)+"元</td></tr>");
        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");


        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">二、服务提供方信息：</font> </td></tr>");
        ss = new StringBuilder();
        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" >");
        ss.append("<tr><td colspan=\"2\">授权经销商名称："+dealername+"</td></tr>");
        ss.append("<tr><td colspan=\"2\">地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址："+address+"</td></tr>");
        ss.append("<tr><td colspan=\"2\">电&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;话："+tel+"</td></tr>");
        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");



        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">三、车主信息：</font> </td></tr>");
        ss = new StringBuilder();
        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" >");
        if (companyname.equals(""))companyname=cname;
        if (iscompany==0)

            ss.append("<tr><td colspan=\"2\">姓名/企业名称："+cname+"</td></tr>");
        else {
            ss.append("<tr><td colspan=\"2\">姓名/企业名称：" + companyname + "</td></tr>");
            ss.append("<tr><td colspan=\"2\">办理人姓名：" + contact_person + "</td></tr>");
            ss.append("<tr><td colspan=\"2\">办理人电话：" + contact_tel + "</td></tr>");
        }
        ss.append("<tr><td colspan=\"2\">身份证号码或统一社会信用代码："+IdNo+"</td></tr>");
        ss.append("<tr><td colspan=\"2\">地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址："+foramtss(30,caddress)+"</td></tr>");
        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");

        String endtime="";
        if (isnewcar==1)
            endtime=formatdate(addYear(Invoicedate,carkey),"yyyy年MM月dd日");
        else
            endtime=formatdate(addYear(paytime,carkey),"yyyy年MM月dd日");
        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\" colspan=\"5\"><font color=\"#fff\">四、服务产品及内容：</font> </td></tr>");
        ss = new StringBuilder();

        ss.append("<tr><td align=\"center\">序号</td><td align=\"center\">服务产品类别</td><td align=\"center\">是否选择</td><td align=\"center\">服务期限\n" +
                "起始日期</td><td align=\"center\">服务期限\n" +
                "终止日期</td></tr>");
        ss.append("<tr><td align=\"center\">1</td><td align=\"left\">"+pname+"</td><td align=\"center\">是</td><td align=\"left\">" +formatdate(paytime,"yyyy年MM月dd日")
                +"</td><td align=\"left\">" +endtime+"</td></tr>");
        allss.append(ss.toString());
        allss.append("</table>");


        allss.append("<br>");

        allss.append("<div><b>五、<u>服务前提：</u></b></div>");
        allss.append("<div>1、享权车辆：非营运9座及9座以下BMW/MINI品牌乘用车。</div>");
        allss.append("<div>2、车龄要求：</div>");
        allss.append("<div>&nbsp;&nbsp;1)\t新车车主购买本服务时享权车辆车龄自享权车辆购车发票开具之日起计算不得超过30天；</div>");
        allss.append("<div>&nbsp;&nbsp;2)\t升级了智能触控钥匙后购买本服务的旧车车主购买本服务时，对享权车辆无车龄要求。\n" +
                "享权车辆的所有权未发生过转让。\n</div>");

        allss.append("<div>3、享权车辆发生了车辆钥匙丢失或者被盗，车主方可向授权经销商申请重置车辆钥匙服务。</div>");


        allss.append("<div><b>六、<u>服务承诺：</u></b></div>");
        allss.append("<div>&nbsp;&nbsp;享权车辆在服务期限内发生了车辆钥匙丢失或被盗，且满足本服务凭证第五条规定的服务前提，车主可按照本服务合同条款向授权经销商提出重置车辆钥匙服务。授权经销商将协助车主重置同品牌、同型号和同规格的车辆钥匙，重置车辆钥匙过程中产生的机动车辆钥匙重置费用由授权经销商承担。</div>");
        allss.append("<div>&nbsp;&nbsp;机动车辆钥匙重置费用包括重置车辆钥匙所需的零配件费用（含钥匙配码的成本）和工时费。</div>");
        allss.append("<div>&nbsp;&nbsp;根据享权车辆所适用的车辆钥匙类型不同，在服务期限内，车主可享受重置同品牌 、同型号和规格车辆钥匙服务的次数不同，其中：</div>");
        allss.append("<div>&nbsp;&nbsp;1、\t购买了本服务的新车车主</div>");

        allss.append("<div>&nbsp;&nbsp;&nbsp;&nbsp;1)\t标准钥匙：车主至多享受2次车辆钥匙重置服务；如您购买一把钥匙，仅赔付一次</div>");
        allss.append("<div>&nbsp;&nbsp;&nbsp;&nbsp;2)\t标准钥匙与NFC钥匙：车主至多享受2次车辆钥匙重置服务（重置NFC钥匙计作前述2次服务中的1次）；或</div>");
        allss.append("<div>&nbsp;&nbsp;&nbsp;&nbsp;3)\t智能触控钥匙：车主至多享受1次车辆钥匙重置服务。</div>");
        allss.append("<div>&nbsp;&nbsp;2、\t升级了智能触控钥匙后购买了本服务的旧车车主：</div>");
        allss.append("<div>&nbsp;&nbsp;&nbsp;&nbsp;1)\t智能触控钥匙：车主至多享受1次车辆钥匙重置服务。</div>");
        allss.append("<div>&nbsp;&nbsp;车主为享权车辆所选择的车辆钥匙重置服务详载于本服务凭证第四条。</div>");

        allss.append("<div><b>七、<u>除外情况：</u></b></div>");

        allss.append("<div><b>1、出现下列任一情形时，车主不能享受服务：</b></div>");
        allss.append("<div><b>1)\t损失发生在服务凭证生效前，或车主未在服务凭证规定的服务期限内提出重置要求；</b></div>");
        allss.append("<div><b>2)\t损失发生在中华人民共和国（为本服务凭证之目的，不含香港、澳门特别行政区和台湾地区）境外；</b></div>");
        allss.append("<div><b>3)\t车主要求重置的车辆钥匙及其所属的机动车辆信息、使用性质与本服务凭证中记载不一致的；</b></div>");
        allss.append("<div><b>4)\t重置不同品牌、型号或规格的车辆钥匙；</b></div>");
        allss.append("<div><b>5)\t车主的故意行为、欺诈、不诚实、违法犯罪行为；</b></div>");
        allss.append("<div><b>6)\t车主使用、维护不当；</b></div>");

        allss.append("<div><b>7)\t车辆钥匙内在或潜在缺陷、自然磨损、自然损耗；</b></div>");
        allss.append("<div><b>8)\t行政行为、司法行为。</b></div>");



        allss.append("<div><b>2、\t下列损失、费用和责任，不在服务范围内：</b></div>");
        allss.append("<div><b>1)\t任何形式的人身伤害、财产损失，及除本服务凭证第六条所列“机动车辆钥匙重置费用”外的其他任何费用支出；</b></div>");
        allss.append("<div><b>2)\t服务过程中所产生的任何间接损失、赔偿责任；</b></div>");
        allss.append("<div><b>3)\t道路救援、拖车、出租车或者租车等服务费用；</b></div>");
        allss.append("<div><b>4)\t任何情况下更换车锁或维修车锁的费用；</b></div>");

        allss.append("<div><b>八、<u>合同终止：</u></b></div>");
        allss.append("<div>&nbsp;&nbsp;除法律另有规定和本合同另有约定外，若车主于本服务凭证项下有权享受的服务尚未提供并于服务期限开始后申请解除本合同的，授权经销商应当将已收取的服务费，按照日比例扣除自服务期限的生效日期起至合同解除之日止应收取的部分后，退还车主。</div>");
        if (icode.equals("CPICB"))
            allss.append("<div>&nbsp;&nbsp;一旦车主就享权车辆获得了机动车辆钥匙重置服务，无论是从授权经销商处获得或者按照本服务凭证第十条的规定从中国太平洋财产保险股份有限公司北京分公司建议的其他授权经销商处获得，在本服务凭证第六条规定的机动车辆钥匙重置服务提供完毕后，本服务凭证自动终止。</div>");
        else
        allss.append("<div>&nbsp;&nbsp;一旦车主就享权车辆获得了机动车辆钥匙重置服务，无论是从授权经销商处获得或者按照本服务凭证第十条的规定从中国太平洋财产保险股份有限公司江苏分公司建议的其他授权经销商处获得，在本服务凭证第六条规定的机动车辆钥匙重置服务提供完毕后，本服务凭证自动终止。</div>");

        allss.append("<div><b>九、<u>服务期限：</u></b></div>");





        allss.append("<div>&nbsp;&nbsp;服务期限为车主购买BMW/MINI钥匙保障（机动车辆钥匙重置）服务生效后至多连续36个月。本服务凭证的生效日期和截止日期以第四条中载明的已选钥匙保障（机动车辆钥匙重置）服务项下所显示的服务期限起始日期和终止日期为准。</div>");

        allss.append("<div><b>十、<u>申请资料：</u></b></div>");
        allss.append("<div>&nbsp;&nbsp;享权车辆发生本服务合同约定的事件后，车主可以通过联系授权经销商申请服务，并按照要求提交所需材料。如无法联系授权经销商，可通过咨询热线电话400-610-6200联系此服务之保险保障和承保服务提供商申请服务。</div>");
        allss.append("<div>1、车主需提供确认享权车辆钥匙的损失性质、原因、程度等有关证明和资料，包括但不限于：</div>");
        allss.append("<div>1)\t享权车辆正面外观照片；</div>");
        allss.append("<div>2)\t享权车辆的行驶证正副两页；</div>");
        allss.append("<div>3)\t车主身份证复印件或扫描件；</div>");
        allss.append("<div>4)\t本钥匙保障服务凭证；</div>");
        allss.append("<div>5)\t索赔申请书；</div>");
        allss.append("<div>6)\t维修工单；</div>");
        allss.append("<div>7)\t享权车辆钥匙损失情况声明书；</div>");
        allss.append("<div>8)\t已发生符合服务凭证约定损失的证明（钥匙禁用截屏）；</div>");
        allss.append("<div>9)\t如车主声明享权车辆钥匙被盗的，则另需提供报警材料正面扫描件。</div>");
        allss.append("<div>&nbsp;&nbsp;因车主未能提交任何上述约定的申请资料，导致部分或全部服务责任无法确定，对于无法确定的部分不能享受服务。</div>");
        allss.append("<div>&nbsp;&nbsp;若由于车主原因导致未完成车辆钥匙重置或授权经销商未能提供服务的，车主不能主张本服务凭证项下的任何服务和/或补偿。</div>");

        allss.append("<div><b>十一、<u>服务流程：</u></b></div>");

        allss.append("<div align=\"center\"><img src=\""+pathurl+"images/anllianlog11.png\" alt=\"\"/></div>");
        allss.append("<div>车主声明：</div>");
        allss.append("<div>&nbsp;&nbsp;提供本服务的授权经销商已通过上述书面形式向本人详细介绍并提供了本服务的服务内容、合同和条款。本人已完整阅读本服务凭证，并清楚知晓本服务凭证的各项条款，特别是保持本服务有效的“服务前提”、将导致无法获得服务的“除外情况”和申请服务所必需的“申请资料”等条款。</div>");
        allss.append("<div>&nbsp;&nbsp;本人同意，提供本服务的授权经销商及其关联公司可以将本服务凭证信息及个人信息提供给为其提供相关服务的任何其他实体（包括但不限于为本服务凭证提供承保服务的保险公司、第三方服务提供商）、提供数据分析、处理的技术服务公司，以及任何实际的或可能的参与人、代理人。前述信息接收方应有权依据其各自的规定、适用的法律、法规及合约使用信息｡</div>");
        allss.append("<div>&nbsp;&nbsp;本人已充分理解并接受上述内容，同意以此作为订立服务凭证的依据，并愿意遵守本服务凭证的条款。</div>");
        allss.append("<br>");
        allss.append("<hr style= \"border:1px dashed #000\" />");

        allss.append("<br>");
        allss.append("<br>");


        allss.append("<div>车主签字（适用于个人客户）：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>车主盖章（适用于企业客户）：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>授权经销商：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>盖  章：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("</body>");
        return allss.toString();
    }

    public String contractPrintMazda(String data)
    {
        String pathurl=GetPathurl();
        String contractno="";
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

        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();else return  GetErrorString(1,"");

        StringBuilder allss = new StringBuilder();
        String dealerno="";
        String sqlstring="select *,(select FROM_UNIXTIME(billbmw.ttime,'%Y-%m-%d') from billbmw where contractno=contract.contractno and in_or_out=-1 and (tradetype='订单支付消费' or tradetype='支付') ORDER BY ttime desc LIMIT 0,1 ) as paytime from contract where contractno=?";
        Map<String,Object>map=queryForMap(jdbcTemplate,sqlstring,contractno);
        String brand="",vehicletype="",vin="",Invoiceno="",Invoicedate="",guideprice="",Invoiceprice="",retailprice="";
        String cname="",IdNo="",caddress="",insurancecompany="",Businessinsurancepolicyno="",insurancepolicyno="",productid="",companyname="",paytime="";
        int iscompany=0;
        if (map!=null)
        {
            brand=setting.NUllToSpace(map.get("brand"));
            vehicletype=setting.NUllToSpace(map.get("vehicletype"));
            vin=setting.NUllToSpace(map.get("vin"));
            Invoiceno=setting.NUllToSpace(map.get("Invoiceno"));
            Invoicedate=setting.NUllToSpace(map.get("Invoicedate"));
            guideprice=setting.NUllToSpace(map.get("guideprice"));
            retailprice=setting.NUllToSpace(map.get("retailprice"));
            Invoiceprice=setting.NUllToSpace(map.get("Invoiceprice"));
            dealerno=setting.NUllToSpace(map.get("dealerno"));
            cname=setting.NUllToSpace(map.get("cname"));
            IdNo=setting.NUllToSpace(map.get("IdNo"));
            caddress=setting.NUllToSpace(map.get("address"));
            insurancecompany=setting.NUllToSpace(map.get("insurancecompany"));
            Businessinsurancepolicyno=setting.NUllToSpace(map.get("Businessinsurancepolicyno"));
            insurancepolicyno=setting.NUllToSpace(map.get("forceno"));
            productid=setting.NUllToSpace(map.get("productid"));
            companyname=setting.NUllToSpace(map.get("companyname"));
            iscompany=setting.StrToInt(map.get("iscompany")+"");
            paytime=map.get("paytime")+"";

        }
        if (brand.equals("一汽红旗"))return  contractPrintHQ(data);
        else   if (brand.equals("一汽奔腾"))return  contractPrintBt(data);
//        allss.append("<table class=\"table-print\" border=\"0\"  ><tr>");
////        allss.append("<td align=\"left\" >Allianz Worldwide Partners</td>");
////        allss.append("<td rowspan=\"2\" align=\"right\"><img src=\"images/anllianlog.png\" alt=\"\"/></td></tr>");
////        allss.append("<td align=\"left\">安世联合商务服务(北京)有限公司</td></tr>");
//////        allss.append("</table>");

        //  allss.append("<body background=\""+pathurl+"images/anllianlog2.png\" style=\" background-repeat:no-repeat ; " + "align:top;\" />");
        sqlstring="select * from dealer where dealerno=?";
        map=queryForMap(jdbcTemplate,sqlstring,dealerno);
        String dealername="",address="",tel="";
        if (map!=null)
        {
            dealername=setting.NUllToSpace(map.get("dealername"));
            address=setting.NUllToSpace(map.get("address"));
            tel=setting.NUllToSpace(map.get("tel"));
        }
        if (companyname.equals(""))companyname=cname;
        allss.append("<head> ");
        allss.append("<style>");
        allss.append(".print-title{ font-size:18px;color:#403f45} ");
        allss.append(".print-body{ font-size:14px;color:#403f45} ");
        // allss.append(".table-print{ font-size:12px;font-color:#403f45} ");
        allss.append(".table-print{border:1px solid #000;border-collapse:collapse;width:100%;margin-bottom:10px;}/*打印表格的效果*/\n" +
                ".table-print th,.table-print td{padding:2px 4px;font-size:14px;color:#403f45!important;font-weight:normal;}\n");

        allss.append(".table-nobody{border:0px solid #fff;border-collapse:collapse;width:100%;margin-bottom:10px;}/*打印表格的效果*/\n");
        allss.append("</style>");
        allss.append("</head> ");

       // allss.append("<div align=\"center\"><img src=\""+pathurl+"images/porsche.png\" width=\"120\" height=\"61\"  alt=\"\"/></div>");
        // allss.append("<div align=\"center\" width=\"50%\" ><img src=\""+pathurl+"images/porsche.png\"  alt=\"\"/></div>");
        allss.append("<br>");
        String title="全损无忧服务";
        allss.append("<div align=\"center\" class=\"print-title\" ><b>" + title + "</b></div>");

        allss.append("<br>");
        allss.append("<div align=\"center\" class=\"print-body\" >车辆置换服务合同</div>");

        allss.append("<br>");
        allss.append("<div align=\"right\"  class=\"print-body\" >编号：【"+contractno+"】</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >车主："+companyname+"</div>");
        allss.append("<br>");
        allss.append("<div align=\"left\"  class=\"print-body\" >授权经销商："+dealername+"</div>");

        allss.append("<br>");
        allss.append("<div align=\"left\" class=\"print-body\">&nbsp;&nbsp;&nbsp;&nbsp;通过签署本车辆置换服务合同（以下简称“服务合同”），车主同意接受由授权经销商提供的车辆置换服务（以下简称“服务”或“本服务”）。为了便于授权经销商向车主提供更贴心的服务，车主需仔细阅读本服务合同条款，并准确填写下列信息：</div>");
        allss.append("<br>");
        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">一、享权车辆信息：</font> </td></tr>");
        StringBuilder ss = new StringBuilder();
        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" ><tr>");
        ss.append("<td>品   牌：<u>"+foramtss(20,brand)+"</u></td>");
        ss.append("<td>车   型：<u>"+foramtss(20,vehicletype)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">车辆用途：     非   营   运</td></tr>");
        ss.append("<tr><td colspan=\"2\">车架号码（VIN码）：<u>"+foramtss(20,vin)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">购买时间（购车发票时间）：<u>"+foramtss(20,Invoicedate)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">购车发票号码：<u>"+foramtss(20,Invoiceno)+"</u></td></tr>");
        ss.append("<tr><td>享权车辆建议零售价：<u>"+foramtss(20,formatMoney(guideprice))+"</u></td>");
        ss.append("<td> 享权车辆发票价（原车购置价）：<u>"+foramtss(20,formatMoney(Invoiceprice))+"</u></td></tr>");
        ss.append("<td> 全损无忧服务（车辆置换服务）价格：<u>"+foramtss(20,formatMoney(retailprice))+"</u></td></tr>");
        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");


        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">二、服务提供方信息：</font> </td></tr>");
        ss = new StringBuilder();
        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" >");
        ss.append("<tr><td colspan=\"2\">授权经销商名称：<u>"+foramtss(30,dealername)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址：<u>"+foramtss(30,address)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">电&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;话：<u>"+foramtss(30,tel)+"</u></td></tr>");
        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");




        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">三、客户（车主）信息：</font> </td></tr>");
        ss = new StringBuilder();


        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" >");
        if (iscompany==0)
        {
            ss.append("<tr><td colspan=\"2\">姓名/企业名称：<u>"+foramtss(30,cname)+"</u></td></tr>");
            ss.append("<tr><td colspan=\"2\">身份证号码：<u>"+foramtss(30,IdNo)+"</u></td></tr>");
            ss.append("<tr><td colspan=\"2\">或统一社会信用代码：<u>"+foramtss(30,"")+"</u></td></tr>");
        }else
        {
            ss.append("<tr><td colspan=\"2\">姓名/企业名称：<u>"+foramtss(30,companyname)+"</u></td></tr>");
            ss.append("<tr><td colspan=\"2\">身份证号码：<u>"+foramtss(30,"")+"</u></td></tr>");
            ss.append("<tr><td colspan=\"2\">或统一社会信用代码：<u>"+foramtss(30,IdNo)+"</u></td></tr>");
        }


        ss.append("<tr><td colspan=\"2\">地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址：<u>"+foramtss(30,caddress)+"</u></td></tr>");
        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");

        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">四、享权车辆机动车辆保险信息：</font> </td></tr>");
        ss = new StringBuilder();
        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" >");
        ss.append("<tr><td colspan=\"2\">投保机动车辆保险公司：<u>"+foramtss(30,insurancecompany)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">交 强 险 保 单 号 码：<u>"+foramtss(30,insurancepolicyno)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">商 业 险 保 单 号 码：<u>"+foramtss(30,Businessinsurancepolicyno)+"</u></td></tr>");
        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");


        allss.append("<div><b>五、<u>服务前提：</u></b></div>");
        allss.append("<div>1、享权车辆：非营运9座及9座以下一汽马自达品牌乘用车</div>");
        allss.append("<div>2、车龄要求：车主购买本服务时享权车辆车龄自享权车辆首次购车发票开具之日起计算不得超过十二（12）个月。享权车辆的所有权未发生过转让。</div>");
        allss.append("<div>3、\t机动车辆保险投保要求：在本服务合同约定的服务期限内车主须为享权车辆投保机动车交通事故责任强制保险和足额投保以下机动车辆商业保险险种，且须保持所有险种于本服务合同约定的服务期限内持续有效。</div>");
        allss.append("<div>1)机动车损失保险；</div>");
        allss.append("<div>2)\t______________________（如有）。</div>");

        allss.append("<div>享权车辆因机动车辆商业保险的上述任一险种承保的保险事故造成全损或推定全损，且该险种和机动车交通事故责任强制保险于保险事故发生时保险期间尚未届满，车主方可向授权经销商申请服务。全损或推定全损以承保享权车辆上述机动车辆商业保险之保险公司的定损结果为惟一依据。</div>");

        allss.append("<div><b>六、<u>服务承诺：</u></b></div>");
        allss.append("<div>&nbsp;&nbsp;享权车辆在服务期限内遭受全损或推定全损，且满足本服务合同第五条规定的服务前提，车主可向授权经销商申请服务。授权经销商将协助车主置换一辆马自达品牌的新车，可供选择的新车品牌视该授权经销商的具体授权情况而定，置换新车过程中产生的车辆置换费用由授权经销商承担。新车发票价（其定义与享权车辆发票价一致）高于享权车辆发票价的，超出部分由车主自行承担。新车的官方建议零售价不得低于享权车辆官方建议零售价的90%。</div>");
        allss.append("<div>&nbsp;&nbsp;本服务中所指的机动车置换费用包括以下两部分：</div>");

        allss.append("<div>1、\t车辆折旧费用：\n" +
                "如出险时新车购置价低于原车购置价，则：车辆折旧费用 = 被置换机动车出险时新车购置价 X 车辆已使用月数 X 月折旧率车辆折旧费用 = 享权车辆发票价x已使用月数 x月折旧率（0.6%）；\n" +
                "如出险时新车购置价高于原车购置价，则：车辆折旧费用 = 被置换机动车原车购置价 X 车辆已使用月数 X 月折旧率（0.6%）。\n" +
                "其中，“已使用月数”指享权车辆购车发票显示的购车日期至发生全损或推定全损事故之日计算的已使用月数，不足一月，不计算折旧。\n</div>");
        allss.append("<div>2、\t额外重置费用：\n" +
                "在新车购置过程中所产生的费用，包括新车登记费用、新车交付费用、新车的车辆购置税、新车相关保险费等。其金额为上述各项费用金额之和，但不超过享权车辆发票价的15%；超出部分由车主自行承担。\n</div>");
        allss.append("<div>上述税费、保险费及服务费之金额应当符合同一时期同类税种、保险产品和服务的税率或市场公允价格。享权车辆发票价以其首次购车发票所载明之价税合计金额（不含车辆购置税）为准。</div>");

        allss.append("<div><b>七、<u>除外情况：</u></b></div>");

        allss.append("<div><b>1、\t出现下列任一情形时，车主不能享受服务：</b></div>");
        allss.append("<div><b>1)\t车主要求提供服务的享权车辆信息和/或车主身份信息与本服务合同第一条和第三条记载的信息不一致；</b></div>");
        allss.append("<div><b>2)\t车主要求提供服务的享权车辆用途与本服务合同中限定不一致；</b></div>");
        allss.append("<div><b>3)\t享权车辆遭受的事故发生在本服务合同约定服务期限生效日期前；</b></div>");
        allss.append("<div><b>4)\t享权车辆因遭受事故仅导致部分损失，未达到本服务合同所约定的车辆全损或推定全损标准（以承保享权车辆机动车辆商业保险之保险公司的定损结果为唯一依据）；</b></div>");
        allss.append("<div><b>5)\t享权车辆遭受事故时不存在相关机动车辆保险保障，或遭受事故后因不符合法律规定或相关机动车辆保险约定的理赔条件、未获得与全损或推定全损相应的机动车辆保险赔偿；</b></div>");
        allss.append("<div><b>6)\t享权车辆经过改装或拼装，其工况受到影响；</b></div>");

            allss.append("<div><b>7)\t享权车辆已被转让给第三人。</b></div>");



        allss.append("<div><b>2、 下列任何原因造成享权车辆全损或推定全损，车主不能就相关损失享受服务：</b></div>");
        allss.append("<div><b>1)\t车主的故意行为、重大过失、欺诈、不诚实、违法犯罪行为；</b></div>");
        allss.append("<div><b>2)\t车主使用、维护、保管不当；</b></div>");
        allss.append("<div><b>3)\t享权车辆内在或潜在缺陷、自然磨损、自然损耗；</b></div>");
        allss.append("<div><b>4)\t战争、敌对行为、军事行动、武装冲突、恐怖主义活动、罢工、暴动、骚乱；</b></div>");
        allss.append("<div><b>5)\t核爆炸、核裂变、核聚变；</b></div>");
        allss.append("<div><b>6)\t放射性污染及其他各种环境污染；</b></div>");
        allss.append("<div><b>7)\t行政行为、司法行为。</b></div>");

        allss.append("<div><b>3、下列损失、费用和责任，不在服务范围内：</b></div>");
        allss.append("<div><b>1)\t新车发票价超出本合同第六条所约定上限的部分；</b></div>");
        allss.append("<div><b>2)\t额外重置费用超出本合同第六条所约定上限的部分；</b></div>");
        allss.append("<div><b>3)\t在车辆置换过程中，因更换非马自达品牌新车产生的费用；</b></div>");
        allss.append("<div><b>4)\t任何形式的人身伤害、财产损失，及除本服务合同第六条所列“车辆置换费用”外的其他任何费用支出；</b></div>");
        allss.append("<div><b>5)\t服务过程中所产生的任何间接损失、赔偿责任；</b></div>");
        allss.append("<div><b>6)\t因未投保本服务合同第五条约定的机动车辆保险任一险种承保的保险事故造成的车辆全损或推定全损，或者该险种的保险期间于保险事故发生时已届满。</b></div>");

        allss.append("<div><b>八、<u>合同终止：</u></b></div>");
        allss.append("<div>&nbsp;&nbsp;除法律另有规定和本服务合同另有约定外，若车主于服务期限开始后请求取消本服务合同的，授权经销商应当在按照日比例从已收取的服务费中扣除自服务期限生效日期起算至服务合同解除之日止应收取服务费后的余额部分退还车主。服务合同解除之日以车主签署取消订单证明载明的日期为准。</div>");
        allss.append("<div>&nbsp;&nbsp;一旦车主就享权车辆获得了车辆置换服务，无论是从授权经销商处获得或者按照本服务合同第十条的规定从中国太平洋财产保险股份有限公司北京分公司建议的其他授权经销商处获得，在车辆置换服务提供完毕后，本服务合同自动终止。</div>");

        allss.append("<div><b>九、<u>服务期限：</u></b></div>");



        allss.append("<div>&nbsp;&nbsp;本服务合同项下服务期限的生效日期为"+paytime+"，服务期限至享权车辆车龄的第36个月届满时自动终止，车龄自享权车辆首次购车发票开具之日起计算。</div>");

        allss.append("<div><b>十、<u>申请资料：</u></b></div>");
        allss.append("<div>&nbsp;&nbsp;享权车辆发生本服务合同约定的事故后，车主可以通过联系授权经销商申请服务，并按照要求提交所需材料。如无法联系授权经销商，可通过联系此服务之提供商申请服务，热线电话400-855-9798。</div>");
        allss.append("<div><b>1、车主需提供确认享权车辆事故性质、原因、损失程度等有关证明和资料，包括但不限于：</b></div>");
        allss.append("<div>1)\t享权车辆发生全损或推定全损时仍处于生效状态的机动车辆商业保险和机动车交通事故责任强制保险的保险单复印件；</div>");
        allss.append("<div>2)\t本服务合同；</div>");
        allss.append("<div>3)\t享权车辆行驶本复印件、车主驾驶本复印件、车主身份证或企业营业执照复印件；</div>");
        allss.append("<div>4)\t享权车辆首次购车发票复印件；</div>");
        allss.append("<div>5)\t机动车辆保险之保险公司已按照全损、推定全损赔付完毕的证明（划账水单原件，以及一次性赔偿协议原件或机动车辆保险之保险公司盖章的复印件）；</div>");
        allss.append("<div>6)\t享权车辆全损或推定全损后处理手续（权益转让书、拍卖或注销解体手续）；</div>");
        allss.append("<div>7)\t证明事故发生原因的证明，属于道路交通事故的提供事故证明（证明材料复印件，需机动车辆保险之保险公司盖章）。</div>");
        allss.append("<div><b>2、车主需提供车辆置换补偿的相关材料、单据，包括但不限于：</b></div>");
        allss.append("<div>1)\t新车的购车合同的复印件；</div>");
        allss.append("<div>2)\t新车的车辆购置税完税证明的复印件；</div>");
        allss.append("<div>3)\t新车的机动车辆保险的保险单的复印件或电子保单及保费发票的复印件；</div>");
        allss.append("<div>4)\t新车的车辆置换服务的服务合同和服务费发票的复印件，如有；</div>");
        allss.append("<div>5)\t新车交付所涉及项目的费用发票的复印件。</div>");
        allss.append("<div><b>3、车主需将机动车辆保险之全损或推定全损的赔付款项支付给提供车辆置换服务的授权经销商。</b></div>");
        allss.append("<div>&nbsp;&nbsp;因车主未能提交任何上述约定的申请资料，导致部分或全部服务责任无法确定，对于无法确定的部分不能享受服务。</div>");
        allss.append("<div>&nbsp;&nbsp;若由于车主原因导致未完成新车置换或授权经销商未能提供服务的，车主不能主张本服务合同项下的任何服务和/或补偿。</div>");

        allss.append("<div><b>十一、<u>服务流程：</u></b></div>");

        allss.append("<div align=\"center\"><img src=\""+pathurl+"images/anllianlog1.png\" alt=\"\"/></div>");
        allss.append("<div>车主声明：</div>");
        allss.append("<div>&nbsp;&nbsp;提供本服务的授权经销商已通过上述书面形式向本人详细介绍并提供了本服务的服务内容和条款。本人已完整阅读本服务合同，并清楚知晓本服务合同的各项条款，特别是保持本服务有效的“服务前提”、将导致无法获得服务的“除外情况”和申请服务所必需的“申请资料”等条款。</div>");
        allss.append("<div>&nbsp;&nbsp;本人知悉并同意，提供本服务的授权经销商可以在必要范围内将本服务合同信息及个人信息提供给其他实体，包括：授权经销商会将合同信息提供给与其合作的、为车辆置换服务提供管理服务的第三方服务提供商；为服务合同提供承保服务的保险公司也需获得本合同内的享权车辆信息、车辆保险信息、车主信息及其联系电话。授权经销商提供上述信息的目的包括：及时、正确处理您的车辆置换服务申请；使本服务合同得到保险公司的及时承保；在合法范围内对服务购买和使用情况进行数据分析。授权经销商将采取协议约定等合理措施，要求前述信息接收方遵守适用的法律、法规及合约，妥善管理并仅为授权经销商指定的目的使用您的上述信息｡</div>");
        allss.append("<div>&nbsp;&nbsp;本人已充分理解并接受上述内容，同意以此作为订立服务合同的依据，并愿意遵守本服务合同的条款。</div>");
        allss.append("<br>");
        allss.append("<hr style= \"border:1px dashed #000\" />");

        allss.append("<br>");
        allss.append("<br>");


        allss.append("<div>车主签字（适用于个人客户）：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>车主盖章（适用于企业客户）：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>授权经销商：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>盖  章：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("</body>");

        return allss.toString();

    }
    public String contractPrintBt(String data)
    {
        String pathurl=GetPathurl();
        String contractno="";
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

        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();else return  GetErrorString(1,"");

        StringBuilder allss = new StringBuilder();
        String dealerno="";
        String sqlstring="select *,(select FROM_UNIXTIME(billbmw.ttime,'%Y-%m-%d') from billbmw where contractno=contract.contractno and in_or_out=-1 and (tradetype='订单支付消费' or tradetype='支付') ORDER BY ttime desc LIMIT 0,1 ) as paytime from contract where contractno=?";
        Map<String,Object>map=queryForMap(jdbcTemplate,sqlstring,contractno);
        String brand="",vehicletype="",vin="",Invoiceno="",Invoicedate="",guideprice="",Invoiceprice="",retailprice="";
        String cname="",IdNo="",caddress="",insurancecompany="",Businessinsurancepolicyno="",insurancepolicyno="",productid="",companyname="",paytime="";
        int iscompany=0;
        if (map!=null)
        {
            brand=setting.NUllToSpace(map.get("brand"));
            vehicletype=setting.NUllToSpace(map.get("vehicletype"));
            vin=setting.NUllToSpace(map.get("vin"));
            Invoiceno=setting.NUllToSpace(map.get("Invoiceno"));
            Invoicedate=setting.NUllToSpace(map.get("Invoicedate"));
            guideprice=setting.NUllToSpace(map.get("guideprice"));
            retailprice=setting.NUllToSpace(map.get("retailprice"));
            Invoiceprice=setting.NUllToSpace(map.get("Invoiceprice"));
            dealerno=setting.NUllToSpace(map.get("dealerno"));
            cname=setting.NUllToSpace(map.get("cname"));
            IdNo=setting.NUllToSpace(map.get("IdNo"));
            caddress=setting.NUllToSpace(map.get("address"));
            insurancecompany=setting.NUllToSpace(map.get("insurancecompany"));
            Businessinsurancepolicyno=setting.NUllToSpace(map.get("Businessinsurancepolicyno"));
            insurancepolicyno=setting.NUllToSpace(map.get("forceno"));
            productid=setting.NUllToSpace(map.get("productid"));
            companyname=setting.NUllToSpace(map.get("companyname"));
            iscompany=setting.StrToInt(map.get("iscompany")+"");
            paytime=map.get("paytime")+"";

        }

        sqlstring="select * from dealer where dealerno=?";
        map=queryForMap(jdbcTemplate,sqlstring,dealerno);
        String dealername="",address="",tel="";
        if (map!=null)
        {
            dealername=setting.NUllToSpace(map.get("dealername"));
            address=setting.NUllToSpace(map.get("address"));
            tel=setting.NUllToSpace(map.get("tel"));
        }
        if (companyname.equals(""))companyname=cname;
        allss.append("<head> ");
        allss.append("<style>");
        allss.append(".print-title{ font-size:18px;color:#403f45} ");
        allss.append(".print-body{ font-size:14px;color:#403f45} ");
        // allss.append(".table-print{ font-size:12px;font-color:#403f45} ");
        allss.append(".table-print{border:1px solid #000;border-collapse:collapse;width:100%;margin-bottom:10px;}/*打印表格的效果*/\n" +
                ".table-print th,.table-print td{padding:2px 4px;font-size:14px;color:#403f45!important;font-weight:normal;}\n");

        allss.append(".table-nobody{border:0px solid #fff;border-collapse:collapse;width:100%;margin-bottom:10px;}/*打印表格的效果*/\n");
        allss.append("</style>");
        allss.append("</head> ");

        // allss.append("<div align=\"center\"><img src=\""+pathurl+"images/porsche.png\" width=\"120\" height=\"61\"  alt=\"\"/></div>");
        // allss.append("<div align=\"center\" width=\"50%\" ><img src=\""+pathurl+"images/porsche.png\"  alt=\"\"/></div>");
        allss.append("<br>");
        String title=brand+"全损保障服务";
        allss.append("<div align=\"center\" class=\"print-title\" ><b>" + title + "</b></div>");

        allss.append("<br>");
        allss.append("<div align=\"center\" class=\"print-body\" >机动车置换服务合同</div>");

        allss.append("<br>");
        allss.append("<div align=\"right\"  class=\"print-body\" >编号：【"+contractno+"】</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >车主："+companyname+"</div>");
        allss.append("<br>");
        allss.append("<div align=\"left\"  class=\"print-body\" >授权经销商："+dealername+"</div>");

        allss.append("<br>");
        allss.append("<div align=\"left\" class=\"print-body\">&nbsp;&nbsp;&nbsp;&nbsp;通过签署本务合同（以下简称“服务合同”），车主同意接受由授权经销商提供的机动车置换服务（以下简称“服务”或“本服务”）。为了便于授权经销商向车主提供更贴心的服务，车主需仔细阅读本服务合同条款，并准确填写下列信息：</div>");
        allss.append("<br>");
        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">一、享权车辆信息：</font> </td></tr>");
        StringBuilder ss = new StringBuilder();
        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" ><tr>");
        ss.append("<td>品   牌：<u>"+foramtss(20,brand)+"</u></td>");
        ss.append("<td>车   型：<u>"+foramtss(20,vehicletype)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">车辆用途：     非   营   运</td></tr>");
        ss.append("<tr><td colspan=\"2\">车架号码（VIN码）：<u>"+foramtss(20,vin)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">购买时间（购车发票时间）：<u>"+foramtss(20,Invoicedate)+"</u></td></tr>");
       // ss.append("<tr><td colspan=\"2\">购车发票号码：<u>"+foramtss(20,Invoiceno)+"</u></td></tr>");
        ss.append("<tr><td>新车指导价：<u>"+foramtss(20,formatMoney(guideprice))+"</u></td>");
        ss.append("<td> 车辆购买价格（购车发票价）：<u>"+foramtss(20,formatMoney(Invoiceprice))+"</u></td></tr>");
      //  ss.append("<td> 全损无忧服务（车辆置换服务）价格：<u>"+foramtss(20,formatMoney(retailprice))+"</u></td></tr>");
        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");


        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">二、服务产品销售及提供方信息：</font> </td></tr>");
        ss = new StringBuilder();
        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" >");
        ss.append("<tr><td colspan=\"2\">授权经销商名称：<u>"+foramtss(30,dealername)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址：<u>"+foramtss(30,address)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">电&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;话：<u>"+foramtss(30,tel)+"</u></td></tr>");
        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");




        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">三、客户（车主）信息：</font> </td></tr>");
        ss = new StringBuilder();


        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" >");
        if (iscompany==0)
        {
            ss.append("<tr><td colspan=\"2\">姓名/企业名称：<u>"+foramtss(30,cname)+"</u></td></tr>");
            ss.append("<tr><td colspan=\"2\">身份证号码：<u>"+foramtss(30,IdNo)+"</u></td></tr>");
            ss.append("<tr><td colspan=\"2\">或统一社会信用代码：<u>"+foramtss(30,"")+"</u></td></tr>");
        }else
        {
            ss.append("<tr><td colspan=\"2\">姓名/企业名称：<u>"+foramtss(30,companyname)+"</u></td></tr>");
            ss.append("<tr><td colspan=\"2\">身份证号码：<u>"+foramtss(30,"")+"</u></td></tr>");
            ss.append("<tr><td colspan=\"2\">或统一社会信用代码：<u>"+foramtss(30,IdNo)+"</u></td></tr>");
        }


        ss.append("<tr><td colspan=\"2\">地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址：<u>"+foramtss(30,caddress)+"</u></td></tr>");
        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");

        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">四、商业保险信息：</font> </td></tr>");
        ss = new StringBuilder();
        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" >");
        ss.append("<tr><td colspan=\"2\">投保机动车辆保险公司：<u>"+foramtss(30,insurancecompany)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">交 强 险 保 单 号 码：<u>"+foramtss(30,insurancepolicyno)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">商 业 险 保 单 号 码：<u>"+foramtss(30,Businessinsurancepolicyno)+"</u></td></tr>");
        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");


        allss.append("<div><b>五、<u>服务前提：</u></b></div>");
        allss.append("<div>1、享权车辆：非营运9座及9座以下"+brand+"品牌乘用车</div>");
        allss.append("<div>2、\t车龄要求：车主购买本服务时享权车辆车龄自享权车辆首次购车发票开具之日起计算不得超过十二（12）个月。</div>");
        allss.append("<div>3、\t享权车辆的所有权未发生过转让。</div>");
        allss.append("<div>4、\t机动车辆保险投保要求：在本服务合同约定的服务期限内车主须为享权车辆投保机动车交通事故责任强制保险和足额投保以下机动车辆商业保险险种，且须保持所有险种于本服务合同约定的服务期限内持续有效。\n" +
                "1)\t机动车损失保险；\n" +
                "2)\t其它附加险险种（如有）。\n</div>");
        allss.append("<div>享权车辆因机动车辆商业保险的上述任一险种承保的保险事故造成全损或推定全损，且该险种和机动车交通事故责任强制保险于保险事故发生时保险期间尚未届满，车主方可向授权经销商申请服务。全损或推定全损以承保享权车辆上述机动车辆商业保险之保险公司的定损结果为惟一依据。</div>");


        allss.append("<div><b>六、<u>服务承诺：</u></b></div>");
        allss.append("<div>&nbsp;&nbsp;享权车辆在服务期限内遭受全损或推定全损，且满足本服务合同第五条规定的服务前提，车主可向授权经销商申请服务。授权经销商将协助车主置换一辆奔腾品牌的新车，可供选择的新车品牌视该授权经销商的具体授权情况而定，置换新车过程中产生的车辆置换费用由授权经销商承担。新车发票价（其定义与享权车辆发票价一致）高于享权车辆发票价的，超出部分由车主自行承担。新车的官方建议零售价不得低于享权车辆官方建议零售价的90%。</div>");
        allss.append("<div>&nbsp;&nbsp;本服务中所指的车辆置换费用包括以下两部分：</div>");

        allss.append("<div>1、\t车辆折旧费用：\n" +
                "车辆折旧费用 = 享权车辆发票价x已使用月数 x月折旧率（0.6%）。\n" +
                "且商业车损险赔偿金额+享权车辆残值+享权车辆折旧费用不超过享权车辆购车发票价\n" +
                "其中，“已使用月数”指享权车辆购车发票显示的购车日期至发生全损或推定全损事故之日计算的已使用月数，不足一月，不计算折旧。\n</div>");
        allss.append("<div>2、\t机动车置换补偿：\n" +
                "在置换新车过程中所产生的费用，包括车辆购置税、新车交付费用、新车登记费用。补偿金额为上述各项费用金额之和，但不超过享权车辆发票价的15%；超出部分由车主自行承担。\n享权车辆发票价以其首次购车发票所载明之价税合计金额（不含车辆购置税）为准。</div>");

        allss.append("<div><b>七、<u>除外情况：</u></b></div>");

        allss.append("<div>1、\t出现下列任一情形时，车主不能享受服务：\n" +
                "1)\t车主要求提供服务的享权车辆信息和/或车主身份信息与本服务合同第一条和第三条记载的信息不一致；\n" +
                "2)\t车主要求提供服务的享权车辆用途与本服务合同中限定不一致；\n" +
                "3)\t享权车辆遭受的事故发生在本服务合同约定服务期限生效日期前；\n" +
                "4)\t享权车辆因遭受事故仅导致部分损失，未达到本服务合同所约定的车辆全损或推定全损标准（以承保享权车辆机动车辆商业保险之保险公司的定损结果为唯一依据）；\n" +
                "5)\t享权车辆遭受事故时不存在相关机动车辆保险保障，或遭受事故后因不符合法律规定或相关机动车辆保险约定的理赔条件、未获得与全损或推定全损相应的机动车辆保险赔偿；\n" +
                "6)\t享权车辆经过改装或拼装，其工况受到影响；\n" +
                "7)\t享权车辆已被转让给第三人。\n</div>");




        allss.append("<div>下列任何原因造成享权车辆全损或推定全损，车主不能就相关损失享受服务：\n" +
                "1)\t车主的故意行为、重大过失、欺诈、不诚实、违法犯罪行为；\n" +
                "2)\t车主使用、维护、保管不当；\n" +
                "3)\t享权车辆内在或潜在缺陷、自然磨损、自然损耗；\n" +
                "4)\t战争、敌对行为、军事行动、武装冲突、恐怖主义活动、罢工、暴动、骚乱；\n" +
                "5)\t核爆炸、核裂变、核聚变；\n" +
                "6)\t放射性污染及其他各种环境污染；\n" +
                "7)\t行政行为、司法行为。\n</div>");


        allss.append("<div>下列损失、费用和责任，不在服务范围内：\n" +
                "1)\t新车发票价超出享权车辆发票价的部分；\n" +
                "2)\t额外重置费用超出本合同第六条所约定上限的部分；\n" +
                "3)\t在车辆置换过程中，因更换非品牌新车产生的费用；\n" +
                "4)\t任何形式的人身伤害、财产损失，及除本服务合同第六条所列“车辆置换费用”外的其他任何费用支出；\n" +
                "5)\t服务过程中所产生的任何间接损失、赔偿责任；\n" +
                "6)\t因未投保本服务合同第五条约定的机动车辆保险任一险种承保的保险事故造成的车辆全损或推定全损，或者该险种的保险期间于保险事故发生时已届满。\n</div>");


        allss.append("<div><b>八、<u>服务终止：</u></b></div>");
        allss.append("<div>&nbsp;&nbsp;除法律另有规定和本服务合同另有约定外，若车主于服务期限开始后请求取消本服务合同的，授权经销商应当在按照日比例从已收取的服务费中扣除自服务期限生效日期起算至服务合同解除之日止应收取服务费后的余额部分退还车主。服务合同解除之日以车主签署取消订单证明载明的日期为准。</div>");
        allss.append("<div>&nbsp;&nbsp;一旦车主就享权车辆获得了车辆置换服务，无论是从授权经销商处获得或者按照本服务合同第十条的规定从中国大地财产保险股份有限公司上海分公司建议的其他授权经销商处获得，在车辆置换服务提供完毕后，本服务合同自动终止。</div>");

        allss.append("<div><b>九、<u>服务期限：</u></b></div>");



        allss.append("<div>&nbsp;&nbsp;本服务合同项下服务期限的生效日期为"+paytime+"，服务期限至享权车辆车龄的第36个月届满时自动终止，车龄自享权车辆首次购车发票开具之日起计算。</div>");

        allss.append("<div><b>十、<u>申请资料：</u></b></div>");
        allss.append("<div>&nbsp;&nbsp;享权车辆发生本服务合同约定的事故后，车主可以通过联系授权经销商申请服务，并按照要求提交所需材料。如无法联系授权经销商，可通过联系此服务之提供商申请服务，热线电话400-855-9798。</div>");
        allss.append("<div>车主需提供确认享权车辆事故性质、原因、损失程度等有关证明和资料，包括但不限于：\n" +
                "1)\t享权车辆发生全损或推定全损时仍处于生效状态的机动车辆商业保险和机动车交通事故责任强制保险的保险单复印件；\n" +
                "2)\t本服务合同；\n" +
                "3)\t享权车辆行驶本复印件、车主驾驶本复印件、车主身份证或企业营业执照复印件；\n" +
                "4)\t享权车辆首次购车发票复印件；\n" +
                "5)\t机动车辆保险之保险公司已按照全损、推定全损赔付完毕的证明（划账水单原件，以及一次性赔偿协议原件或机动车辆保险之保险公司盖章的复印件）；\n" +
                "6)\t享权车辆全损或推定全损后处理手续（权益转让书、拍卖或注销解体手续）；\n" +
                "7)\t证明事故发生原因的证明，属于道路交通事故的提供事故证明（证明材料复印件，需机动车辆保险之保险公司盖章）。\n</div>");

        allss.append("<div>车主需提供车辆置换补偿的相关材料、单据，包括但不限于：\n" +
                "1)\t新车的购车合同的复印件；\n" +
                "2)\t新车的车辆购置税完税证明的复印件；\n" +
                "3)\t新车交付、登记所涉及项目的费用发票的复印件。\n</div>");

        allss.append("<div>3、车主需将机动车辆保险之全损或推定全损的赔付款项支付给提供车辆置换服务的授权经销商。</div>");
        allss.append("<div>&nbsp;&nbsp;因车主未能提交任何上述约定的申请资料，导致部分或全部服务责任无法确定，对于无法确定的部分不能享受服务。</div>");
        allss.append("<div>&nbsp;&nbsp;若由于车主原因导致未完成新车置换或授权经销商未能提供服务的，车主不能主张本服务合同项下的任何服务和/或补偿。</div>");

        allss.append("<div><b>十一、<u>服务流程：</u></b></div>");

        allss.append("<div align=\"center\"><img src=\""+pathurl+"images/anllianlog1.png\" alt=\"\"/></div>");
        allss.append("<div>车主声明：</div>");
        allss.append("<div>&nbsp;&nbsp;提供本服务的授权经销商已通过上述书面形式向本人详细介绍并提供了本服务的服务内容和条款。本人已完整阅读本服务合同，并清楚知晓本服务合同的各项条款，特别是保持本服务有效的“服务前提”、将导致无法获得服务的“除外情况”和申请服务所必需的“申请资料”等条款。</div>");
        allss.append("<div>&nbsp;&nbsp;本人知悉并同意，提供本服务的授权经销商可以在必要范围内将本服务合同信息及个人信息提供给其他实体，包括：授权经销商会将合同信息提供给与其合作的、为车辆置换服务提供管理服务的第三方服务提供商；为服务合同提供承保服务的保险公司也需获得本合同内的享权车辆信息、车辆保险信息、车主信息及其联系电话。授权经销商提供上述信息的目的包括：及时、正确处理您的车辆置换服务申请；使本服务合同得到保险公司的及时承保；在合法范围内对服务购买和使用情况进行数据分析。授权经销商将采取协议约定等合理措施，要求前述信息接收方遵守适用的法律、法规及合约，妥善管理并仅为授权经销商指定的目的使用您的上述信息｡</div>");
        allss.append("<div>&nbsp;&nbsp;本人已充分理解并接受上述内容，同意以此作为订立服务合同的依据，并愿意遵守本服务合同的条款。</div>");
        allss.append("<br>");
        allss.append("<hr style= \"border:1px dashed #000\" />");

        allss.append("<br>");
        allss.append("<br>");


        allss.append("<div>车主签字（适用于个人客户）：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>车主盖章（适用于企业客户）：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>授权经销商：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>盖  章：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("</body>");

        return allss.toString();

    }

    public String contractPrintPorsche(String data)
    {
        if (domainenter.equals("1"))
            return  bmwPrintDao.contractPrintBmw(data);

            String pathurl=GetPathurl();
        String contractno="";
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

        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();else return  GetErrorString(1,"");
        if (domainenter.equals("1"))return contractPrintBmw(data);else   if (domainenter.equals("2"))return contractPrintdaimler(data);

        else   if (domainenter.equals("3"))return contractPrintMore(data);
        StringBuilder allss = new StringBuilder();
        String dealerno="";
        String sqlstring="select *,(select FROM_UNIXTIME(ttime,'%Y-%m-%d') from  contractcheck where contractno=contract.contractno and  checkcontent='合同提交' order by ttime desc limit 0,1 ) as submittime from contract where contractno=?";
        Map<String,Object>map=queryForMap(jdbcTemplate,sqlstring,contractno);
        String brand="",vehicletype="",vin="",Invoiceno="",Invoicedate="",guideprice="",Invoiceprice="";
        String cname="",IdNo="",caddress="",insurancecompany="",Businessinsurancepolicyno="",insurancepolicyno="",productid="",companyname="",submittime="",icode="",carstarttime="";
        int iscompany=0;
        String retailprice="0";
        if (map!=null)
        {
            brand=setting.NUllToSpace(map.get("brand"));
            vehicletype=setting.NUllToSpace(map.get("vehicletype"));
            vin=setting.NUllToSpace(map.get("vin"));
            Invoiceno=setting.NUllToSpace(map.get("Invoiceno"));
            Invoicedate=setting.NUllToSpace(map.get("Invoicedate"));
            guideprice=setting.NUllToSpace(map.get("guideprice"));
            Invoiceprice=setting.NUllToSpace(map.get("Invoiceprice"));
            dealerno=setting.NUllToSpace(map.get("dealerno"));
            cname=setting.NUllToSpace(map.get("cname"));
            IdNo=setting.NUllToSpace(map.get("IdNo"));
            caddress=setting.NUllToSpace(map.get("address"));
            insurancecompany=setting.NUllToSpace(map.get("insurancecompany"));
            Businessinsurancepolicyno=setting.NUllToSpace(map.get("Businessinsurancepolicyno"));
            insurancepolicyno=setting.NUllToSpace(map.get("forceno"));
            productid=setting.NUllToSpace(map.get("productid"));
            companyname=setting.NUllToSpace(map.get("companyname"));
            iscompany=setting.StrToInt(map.get("iscompany")+"");
            submittime=map.get("submittime")+"";
            retailprice=setting.NUllToSpace(map.get("retailprice"));
            icode=setting.NUllToSpace(map.get("icode"));
            carstarttime=  NUllToSpace  (map.get("carstarttime"));
        }
        int print_version=0;
        Date date=parseDate("2023-07-01 00:00:00","yyyy-MM-dd HH:mm:ss");
        Date date1=parseDate("2023-11-01 00:00:00","yyyy-MM-dd HH:mm:ss");
        Date date2=parseDate("2024-06-18 00:00:00","yyyy-MM-dd HH:mm:ss");
        Date date3=parseDate("2024-08-28 00:00:00","yyyy-MM-dd HH:mm:ss");
        try
        {
            Date submit_date=parseDate(submittime+" 12:00:00","yyyy-MM-dd HH:mm:ss");

            if (submit_date.after(date))print_version=1;
            if (submit_date.after(date1))print_version=2;
            if (submit_date.after(date2))print_version=3;
            if (submit_date.after(date3))print_version=4;
        }catch (Exception e)
        {

        }


//        allss.append("<table class=\"table-print\" border=\"0\"  ><tr>");
////        allss.append("<td align=\"left\" >Allianz Worldwide Partners</td>");
////        allss.append("<td rowspan=\"2\" align=\"right\"><img src=\"images/anllianlog.png\" alt=\"\"/></td></tr>");
////        allss.append("<td align=\"left\">安世联合商务服务(北京)有限公司</td></tr>");
//////        allss.append("</table>");

      //  allss.append("<body background=\""+pathurl+"images/anllianlog2.png\" style=\" background-repeat:no-repeat ; " + "align:top;\" />");
        allss.append("<head> ");
        allss.append("<style>");
        allss.append(".print-title{ font-size:18px;color:#403f45} ");
        allss.append(".print-body{ font-size:14px;color:#403f45} ");
       // allss.append(".table-print{ font-size:12px;font-color:#403f45} ");
        allss.append(".table-print{border:1px solid #000;border-collapse:collapse;width:100%;margin-bottom:10px;}/*打印表格的效果*/\n" +
                ".table-print th,.table-print td{padding:2px 4px;font-size:14px;color:#403f45!important;font-weight:normal;}\n");

        allss.append(".table-nobody{border:0px solid #fff;border-collapse:collapse;width:100%;margin-bottom:10px;}/*打印表格的效果*/\n");
        allss.append("</style>");
            allss.append("</head> ");

        allss.append("<div align=\"center\"><img src=\""+pathurl+"images/porsche.png\" width=\"120\" height=\"61\"  alt=\"\"/></div>");
       // allss.append("<div align=\"center\" width=\"50%\" ><img src=\""+pathurl+"images/porsche.png\"  alt=\"\"/></div>");
        allss.append("<br>");
        String title="保时捷保障服务凭证";
        allss.append("<div align=\"center\" class=\"print-title\" ><b>" + title + "</b></div>");

        allss.append("<br>");
        allss.append("<div align=\"center\" class=\"print-body\" ><b>I．\t服务凭证信息页</b></div>");

        allss.append("<br>");
        allss.append("<div align=\"right\"  class=\"print-body\" >合同编号："+contractno+"</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >尊敬的客户：</div>");
        allss.append("<br>");
        allss.append("<div align=\"left\" class=\"print-body\">&nbsp;&nbsp;&nbsp;&nbsp;感谢您选择保时捷品牌保障服务，为了让您享受更贴心的服务和更全面的保障，请务必仔细阅读本服务凭证条款和条件，并准确填写下列信息：</div>");
        allss.append("<br>");
        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">一、享权车辆信息：</font> </td></tr>");
        StringBuilder ss = new StringBuilder();
        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" ><tr>");
        ss.append("<td>品   牌：<u>"+foramtss(20,brand)+"</u></td>");
        ss.append("<td>车   型：<u>"+foramtss(20,vehicletype)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">车辆使用性质：     非   营   运</td></tr>");
        ss.append("<tr><td colspan=\"2\">车架号码（VIN码）：<u>"+foramtss(20,vin)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">购买时间（购车发票时间）：<u>"+foramtss(20,Invoicedate)+"</u></td></tr>");
      //  ss.append("<tr><td colspan=\"2\">购车发票号码：<u>"+foramtss(20,Invoiceno)+"</u></td></tr>");
        ss.append("<tr><td>新车指导价：<u>"+foramtss(20,formatMoney(guideprice))+"</u></td>");
        ss.append("<td> 车辆购买价格（购车发票价）：<u>"+foramtss(20,formatMoney(Invoiceprice))+"</u></td></tr>");
        if (nullToZero(retailprice) !=0)
            ss.append("<tr><td colspan=\"2\">保时捷保障服务价格：<u>"+formatMoney(retailprice)+"</u></td></tr>");
        else    ss.append("<tr><td colspan=\"2\">保时捷保障服务价格：<u>"+foramtss(20,"")+"</u></td></tr>");
        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");

        sqlstring="select * from dealer where dealerno=?";
        map=queryForMap(jdbcTemplate,sqlstring,dealerno);
        String dealername="",address="",tel="";
        if (map!=null)
        {
            dealername=setting.NUllToSpace(map.get("dealername"));
            address=setting.NUllToSpace(map.get("address"));
            tel=setting.NUllToSpace(map.get("tel"));
        }
        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">二、服务产品销售及提供方信息：</font> </td></tr>");
        ss = new StringBuilder();
        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" >");
        ss.append("<tr><td colspan=\"2\">授权经销商名称：<u>"+foramtss(30,dealername)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址：<u>"+foramtss(30,address)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">电&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;话：<u>"+foramtss(30,tel)+"</u></td></tr>");
        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");




        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">三、客户（车主）信息：</font> </td></tr>");
        ss = new StringBuilder();
        if (companyname.equals(""))companyname=cname;

        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" >");
        if (iscompany==0)
        {
            ss.append("<tr><td colspan=\"2\">姓名/企业名称：<u>"+foramtss(30,cname)+"</u></td></tr>");
            ss.append("<tr><td colspan=\"2\">身份证号码：<u>"+foramtss(30,IdNo)+"</u></td></tr>");
            ss.append("<tr><td colspan=\"2\">或统一社会信用代码：<u>"+foramtss(30,"")+"</u></td></tr>");
        }else
        {
            ss.append("<tr><td colspan=\"2\">姓名/企业名称：<u>"+foramtss(30,companyname)+"</u></td></tr>");
            ss.append("<tr><td colspan=\"2\">身份证号码：<u>"+foramtss(30,"")+"</u></td></tr>");
            ss.append("<tr><td colspan=\"2\">或统一社会信用代码：<u>"+foramtss(30,IdNo)+"</u></td></tr>");
        }


        ss.append("<tr><td colspan=\"2\">地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址：<u>"+foramtss(30,caddress)+"</u></td></tr>");
        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");

        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">四、商业保险信息：</font> </td></tr>");
        ss = new StringBuilder();
        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" >");
        ss.append("<tr><td colspan=\"2\">投保机动车辆保险公司：<u>"+foramtss(30,insurancecompany)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">交 强 险 保 单 号 码：<u>"+foramtss(30,insurancepolicyno)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">商 业 险 保 单 号 码：<u>"+foramtss(30,Businessinsurancepolicyno)+"</u></td></tr>");
        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");


        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\" colspan=\"6\"><font color=\"#fff\">五、服务产品及内容：</font> </td></tr>");
        allss.append("<tr>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\"><b>序号</b></td>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\"><b>产品</b></td>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\"><b>是否\n" +
                "选择</b></td>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\"><b>服务起始\n" +
                "日期</b></td>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\"><b>服务终止\n" +
                "日期</b></td>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\" width=\"50%\"><b>保障内容</b></td>");
        allss.append("</tr>");
        //if (print_version<3)productid=productid.replace(",28,",",6,");
        sqlstring="select * from insuranceproduct where productid in ('"+productid.replace(",","','")+"')";
        map=queryForMap(jdbcTemplate,sqlstring);
        int ii=1;
        String startdate="";
        boolean istier=false;
        if (map!=null)
        {
            String pdetail=map.get("detail")+"";
          /*  if ("1".equals(map.get("endmonth")+""))
            {
                startdate=Invoicedate;
            }else */
            if (print_version<3)pdetail=pdetail.replace(",28,",",6,");

            String[] pdtails=pdetail.split(",");
            sqlstring="select * from insuracedetail where 1=0 ";
            for (int i=0;i<pdtails.length;i++)
            {
                sqlstring+=" or tid="+pdtails[i];
            }
            List<Map<String,Object>>list=queryForList(jdbcTemplate,sqlstring);
            String disc="";
           for (int i=0;i<list.size();i++)
           {
               map=list.get(i);
               startdate=submittime;
               if (print_version>0)
               {
                   //7月1日起生效日期是提交日期加1天
                   startdate=addDay(submittime,1);
               }
               String pname=map.get("pname")+"";
             //  if (print_version<4) 等法务确认后再显示原来的名称，所以先注释
               {
                    if (pname.contains("轮胎保障"))pname="轮胎保障";
                    if (pname.contains("钥匙保障"))pname="钥匙保障";
               }

               if (pname.contains("代步")||pname.contains("出险保障"))
               {
                   if (!carstarttime.equals(""))
                   {
                     if(  parseDate(carstarttime,"yyyy-MM-dd").after(parseDate(startdate,"yyyy-MM-dd")))
                       startdate=carstarttime;
                   }
               }
               if (pname.contains("轮胎"))istier=true;
               allss.append("<tr><td align=\"center\" bgcolor=\"#fff\">"+ii+"</td>");
               allss.append("<td align=\"center\" bgcolor=\"#fff\">"+pname+"</td>");
               allss.append("<td align=\"center\" bgcolor=\"#fff\">是</td>");
               allss.append("<td align=\"center\" bgcolor=\"#fff\">"+startdate +
                       "</td>");
               allss.append("<td align=\"center\" bgcolor=\"#fff\">"+addYear(startdate,setting.StrToInt(map.get("year")+"")) +
                       "</td>");
               disc=map.get("disc")+"";
               if (print_version>=1&&print_version<3)disc=disc.replace("凭证服务期限内累计代步保障服务天数上限为","全年累计代步保障服务天数上限为");
               allss.append("<td align=\"left\" bgcolor=\"#fff\">"+disc +
                       "</td></tr>");
               ii++;
           }

        }

        allss.append("</table>");
        allss.append("<br>");
        //包含轮胎时加入第六部分

        if (print_version>2)
        {
            allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
            allss.append("<td align=\"left\" bgcolor=\"#002060\" ><font color=\"#fff\">六、个人信息处理同意</font> </td></tr>");
            allss.append("<tr>");
            ss = new StringBuilder();
            ss.append("<div><b>本人已知悉并同意：</b></div>");
            ss.append("<div><b>□\t保时捷授权经销商（“授权经销商”）的《隐私保护政策》中个人信息处理规则并同意授权经销商据此出于履行《保时捷品牌保障服务凭证》（“本服务凭证”）包括但不限于提供本服务凭证项下服务之目的处理本人个人信息。</b></div>");
            ss.append("<div><b>□\t保时捷（上海）商务服务有限公司根据为向本人提供保时捷客户保障服务客户支持、（客户端）权益展示服务之目的处理本人个人信息。</b></div>");
            ss.append("<div>个人信息的种类：车架号码、保障服务产品名称、产品有效期</div>");
            ss.append("<div>个人信息处理方式：收集、使用、存储、委托保时捷（中国）汽车销售有限公司处理个人信息，并遵守法律法规要求、最小必要原则留存个人信息。</div>");
            ss.append("<div>联系方式：4008205911</div>");
            allss.append("<td align=\"left\" bgcolor=\"#fff\">"+ss.toString()+"</td>");
            allss.append("</tr>");
            allss.append("</table>");
        }
        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        if (print_version<3)
        {
            if (istier)
                allss.append("<td align=\"left\" bgcolor=\"#002060\" ><font color=\"#fff\">六、其他备注说明</font> </td></tr>");
            else
                allss.append("<td align=\"left\" bgcolor=\"#002060\" ><font color=\"#fff\">六、其他备注说明</font> </td></tr>");
        }else {

            allss.append("<td align=\"left\" bgcolor=\"#002060\" ><font color=\"#fff\">七、其他备注说明</font> </td></tr>");
        }

       //六、个人信息处理同意
        allss.append("<tr>");
        ss = new StringBuilder();
        ss.append("<div><b>客户/车主申明：</b></div>");
        ss.append("<div><b>本人确认提供给本《保时捷品牌保障服务凭证》（“本服务凭证”）项下保时捷授权经销商（“授权经销商”）并填写在本服务凭证中的信息是真实和准确的。</b></div>");
        ss.append("<div><b>本人已经收悉本服务凭证并仔细阅读相关条款（包括通用条款和各保障服务产品条款），尤其是您的义务、服务保障除外情形、服务凭证终止等条款内容，并对授权经销商就本服务凭证内容的说明和提示完全理解，没有异议。本服务凭证申请人需遵守本服务凭证的约定方能享受本服务凭证的服务权益。对上述情况，本人签字确认如下：</b></div>");
        ss.append("<div><b>"+foramtss(50,"客户签名/盖章：")+" 经销商盖章（盖章生效）："+dealername+"</b></div>");
        ss.append("<div><b>签单日期：</b></div>");
        allss.append("<td align=\"left\" bgcolor=\"#fff\">"+ss.toString()+"</td>");
        allss.append("</tr>");
        allss.append("</table>");
        allss.append("<br>");
      // String reslutss=allss.toString()+commanstring()+rtistringnew()+tirestring()+carstring()+keystring();
        String reslutss="";
        if (!icode.equals("PAIC"))
        {
            reslutss=allss.toString()+commanstring();

           // if (print_version>=3)reslutss=allss.toString()+commanstring().replace("代步保障","出险保障");
            reslutss=reslutss+rtistringnew(print_version)+keystring()+carstring(icode,print_version);


        }
        else  //PAIC平安保险
        {
            //代步保障服务和/或轮胎保障服务
            reslutss=commanstring().replace("代步保障服务和/或钥匙保障","轮胎保障服务和/或代步保障").replace("太保上分","平安财险上海分公司").replace("钥匙保障服务","轮胎保障服务").replace("和/或人保上海分公司","");
                  reslutss=reslutss.replace("代步保障服务和/或轮胎保障","轮胎保障服务和/或代步保障");
            reslutss=reslutss.replace("代步保障服务和轮胎保障","轮胎保障服务和/或代步保障");
            reslutss=reslutss.replace("轮胎保障服务和代步保障","轮胎保障服务和/或代步保障");

            reslutss=allss.toString()+reslutss;
                    reslutss+=rtistringPAIC(print_version)+tirestring()+carstring(icode,print_version);
           // if (print_version>=3)reslutss=reslutss.replace("代步保障","出险保障");
        }
        if (print_version>=3)reslutss=reslutss.replace("代步保障","出险保障");
        return reslutss;
    }
    public String contractPrintMore(String data)//多品牌打印
    {
        String pathurl=GetPathurl();
        String contractno="";
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

        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();else return  GetErrorString(1,"");

        StringBuilder allss = new StringBuilder();
        String dealerno="";
        String sqlstring="select *,(select FROM_UNIXTIME(ttime,'%Y-%m-%d') from  contractcheck where contractno=contract.contractno and  checkcontent='合同提交' order by ttime desc limit 0,1 ) as submittime from contract where contractno=?";
        Map<String,Object>map=queryForMap(jdbcTemplate,sqlstring,contractno);
        String brand="",vehicletype="",vin="",Invoiceno="",Invoicedate="",guideprice="",Invoiceprice="";
        String cname="",IdNo="",caddress="",insurancecompany="",Businessinsurancepolicyno="",insurancepolicyno="",productid="",companyname="",submittime="",icode="";
        int iscompany=0;
        String retailprice="0";
        if (map!=null)
        {
            brand=setting.NUllToSpace(map.get("brand"));
            vehicletype=setting.NUllToSpace(map.get("vehicletype"));
            vin=setting.NUllToSpace(map.get("vin"));
            Invoiceno=setting.NUllToSpace(map.get("Invoiceno"));
            Invoicedate=setting.NUllToSpace(map.get("Invoicedate"));
            guideprice=setting.NUllToSpace(map.get("guideprice"));
            Invoiceprice=setting.NUllToSpace(map.get("Invoiceprice"));
            dealerno=setting.NUllToSpace(map.get("dealerno"));
            cname=setting.NUllToSpace(map.get("cname"));
            IdNo=setting.NUllToSpace(map.get("IdNo"));
            caddress=setting.NUllToSpace(map.get("address"));
            insurancecompany=setting.NUllToSpace(map.get("insurancecompany"));
            Businessinsurancepolicyno=setting.NUllToSpace(map.get("Businessinsurancepolicyno"));
            insurancepolicyno=setting.NUllToSpace(map.get("forceno"));
            productid=setting.NUllToSpace(map.get("productid"));
            companyname=setting.NUllToSpace(map.get("companyname"));
            iscompany=setting.StrToInt(map.get("iscompany")+"");
            submittime=map.get("submittime")+"";
            retailprice=setting.NUllToSpace(map.get("retailprice"));
            icode=setting.NUllToSpace(map.get("icode"));

        }

        allss.append("<head> ");
        allss.append("<style>");
        allss.append(".print-title{ font-size:18px;color:#403f45} ");
        allss.append(".print-body{ font-size:14px;color:#403f45} ");
        // allss.append(".table-print{ font-size:12px;font-color:#403f45} ");
        allss.append(".table-print{border:1px solid #000;border-collapse:collapse;width:100%;margin-bottom:10px;}/*打印表格的效果*/\n" +
                ".table-print th,.table-print td{padding:2px 4px;font-size:14px;color:#403f45!important;font-weight:normal;}\n");

        allss.append(".table-nobody{border:0px solid #fff;border-collapse:collapse;width:100%;margin-bottom:10px;}/*打印表格的效果*/\n");
        allss.append("</style>");
        allss.append("</head> ");

        allss.append("<div align=\"center\"><img src=\""+pathurl+"images/vwf.png\" width=\"120\" height=\"61\"  alt=\"\"/></div>");
        allss.append("<br>");
        String title="无忧出行服务凭证";
        allss.append("<div align=\"center\" class=\"print-title\" ><b>" + title + "</b></div>");

        allss.append("<br>");
        allss.append("<div align=\"center\" class=\"print-body\" ><b>I．\t服务凭证信息页</b></div>");

        allss.append("<br>");
        allss.append("<div align=\"right\"  class=\"print-body\" >编号：【"+contractno+"】</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >尊敬的客户：</div>");
        allss.append("<br>");
        allss.append("<div align=\"left\" class=\"print-body\">&nbsp;&nbsp;&nbsp;&nbsp;感谢您选择无忧出行服务，为了让您享受更贴心的服务和更全面的保障，请务必仔细阅读本服务凭证条款和条件，并准确填写下列信息：</div>");
        allss.append("<br>");
        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">一、享权车辆信息：</font> </td></tr>");
        StringBuilder ss = new StringBuilder();
        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" ><tr>");
        ss.append("<td>品   牌：<u>"+foramtss(20,brand)+"</u></td>");
        ss.append("<td>车   型：<u>"+foramtss(20,vehicletype)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">车辆使用性质：     非   营   运</td></tr>");
        ss.append("<tr><td colspan=\"2\">车架号码（VIN码）：<u>"+foramtss(20,vin)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">购买时间（购车发票时间）：<u>"+foramtss(20,Invoicedate)+"</u></td></tr>");
        ss.append("<tr><td>新车指导价：<u>"+foramtss(20,formatMoney(guideprice))+"</u></td>");
        ss.append("<td> 车辆购买价格（购车发票价）：<u>"+foramtss(20,formatMoney(Invoiceprice))+"</u></td></tr>");

            ss.append("<tr><td colspan=\"2\">保障服务价格：<u>"+formatMoney(retailprice)+"</u></td></tr>");

        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");

        sqlstring="select * from dealer where dealerno=?";
        map=queryForMap(jdbcTemplate,sqlstring,dealerno);
        String dealername="",address="",tel="";
        if (map!=null)
        {
            dealername=setting.NUllToSpace(map.get("dealername"));
            address=setting.NUllToSpace(map.get("address"));
            tel=setting.NUllToSpace(map.get("tel"));
        }
        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">二、服务产品销售及提供方信息：</font> </td></tr>");
        ss = new StringBuilder();
        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" >");
        ss.append("<tr><td colspan=\"2\">经销商名称：<u>"+foramtss(30,dealername)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址：<u>"+foramtss(30,address)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">电&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;话：<u>"+foramtss(30,tel)+"</u></td></tr>");
        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");




        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">三、客户（车主）信息：</font> </td></tr>");
        ss = new StringBuilder();
        if (companyname.equals(""))companyname=cname;

        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" >");
        if (iscompany==0)
        {
            ss.append("<tr><td colspan=\"2\">姓名/企业名称：<u>"+foramtss(30,cname)+"</u></td></tr>");
            ss.append("<tr><td colspan=\"2\">身份证号码：<u>"+foramtss(30,IdNo)+"</u></td></tr>");
            ss.append("<tr><td colspan=\"2\">或统一社会信用代码：<u>"+foramtss(30,"")+"</u></td></tr>");
        }else
        {
            ss.append("<tr><td colspan=\"2\">姓名/企业名称：<u>"+foramtss(30,companyname)+"</u></td></tr>");
            ss.append("<tr><td colspan=\"2\">身份证号码：<u>"+foramtss(30,"")+"</u></td></tr>");
            ss.append("<tr><td colspan=\"2\">或统一社会信用代码：<u>"+foramtss(30,IdNo)+"</u></td></tr>");
        }


        ss.append("<tr><td colspan=\"2\">地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址：<u>"+foramtss(30,caddress)+"</u></td></tr>");
        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");

        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">四、商业保险信息：</font> </td></tr>");
        ss = new StringBuilder();
        ss.append("<table class=\"table-nobody\" border=\"0\"  width=\"80%\" >");
        ss.append("<tr><td colspan=\"2\">投保机动车辆保险公司：<u>"+foramtss(30,insurancecompany)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">交 强 险 保 单 号 码：<u>"+foramtss(30,insurancepolicyno)+"</u></td></tr>");
        ss.append("<tr><td colspan=\"2\">商 业 险 保 单 号 码：<u>"+foramtss(30,Businessinsurancepolicyno)+"</u></td></tr>");
        ss.append("</table>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
        allss.append("</table>");
        allss.append("<br>");


        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\" colspan=\"6\"><font color=\"#fff\">五、服务产品及内容：</font> </td></tr>");
        allss.append("<tr>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\"><b>序号</b></td>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\"><b>服务产品</b></td>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\"><b>是否\n" +
                "选择</b></td>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\"><b>服务期限\n" +
                "起始日期</b></td>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\"><b>服务期限\n" +
                "终止日期</b></td>");
        allss.append("<td align=\"center\" bgcolor=\"#fff\" width=\"50%\"><b>保障内容</b></td>");
        allss.append("</tr>");
        sqlstring="select * from insuranceproduct where productid in ('"+productid.replace(",","','")+"')";
        map=queryForMap(jdbcTemplate,sqlstring);
        int ii=1;
        String startdate="";
        if (map!=null)
        {
            String pdetail=map.get("detail")+"";
          /*  if ("1".equals(map.get("endmonth")+""))
            {
                startdate=Invoicedate;
            }else */
            startdate=submittime;
            String[] pdtails=pdetail.split(",");
            sqlstring="select * from insuracedetail where 1=0 ";
            for (int i=0;i<pdtails.length;i++)
            {
                sqlstring+=" or tid="+pdtails[i];
            }
            List<Map<String,Object>>list=queryForList(jdbcTemplate,sqlstring);

            for (int i=0;i<list.size();i++)
            {
                map=list.get(i);
                String pname=map.get("pname")+"";
                if (pname.contains("轮胎保障"))pname="轮胎保障";
                if (pname.contains("钥匙保障"))pname="钥匙保障";

                allss.append("<tr><td align=\"center\" bgcolor=\"#fff\">"+ii+"</td>");
                allss.append("<td align=\"center\" bgcolor=\"#fff\">"+pname+"</td>");
                allss.append("<td align=\"center\" bgcolor=\"#fff\">是</td>");
                allss.append("<td align=\"center\" bgcolor=\"#fff\">"+startdate +
                        "</td>");
                allss.append("<td align=\"center\" bgcolor=\"#fff\">"+addYear(startdate,setting.StrToInt(map.get("year")+"")) +
                        "</td>");
                allss.append("<td align=\"left\" bgcolor=\"#fff\">"+map.get("disc") +
                        "</td></tr>");
                ii++;
            }

        }

        allss.append("</table>");
        allss.append("<br>");


        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\" ><font color=\"#fff\">六、其他备注说明</font> </td></tr>");
        allss.append("<tr>");
        ss = new StringBuilder();
        ss.append("<div><b>客户/车主申明：</b></div>");
        ss.append("<div><b>本人确认提供给本《无忧出行服务凭证》（“本服务凭证”）项下经销商（“经销商”）并填写在本服务凭证中的信息是真实和准确的。</b></div>");
        ss.append("<div><b>本人已经收悉本服务凭证并仔细阅读相关条款（包括通用条款和各保障服务产品条款），尤其是您的义务、服务保障除外情形、服务凭证终止等条款内容，并对经销商就本服务凭证内容的说明和提示完全理解，没有异议。本服务凭证申请人需遵守本服务凭证的约定方能享受本服务凭证的服务权益。对上述情况，本人签字确认如下：</b></div>");
        ss.append("<div><b>"+foramtss(50,"客户签名/盖章：")+" 经销商盖章（盖章生效）："+dealername+"</b></div>");
        ss.append("<div><b>签署日期：</b></div>");
        allss.append("<td align=\"left\" bgcolor=\"#fff\">"+ss.toString()+"</td>");
        allss.append("</tr>");
        allss.append("</table>");
        allss.append("<br>");
        // String reslutss=allss.toString()+commanstring()+rtistringnew()+tirestring()+carstring()+keystring();
        String reslutss="";
        if (!icode.equals("PAIC"))
        {
            reslutss=allss.toString()+commanstringMore();

            reslutss=reslutss  +rtistringMore()+carstringMore()+keystringMore()+tirestringMore();
        }

        else  //PAIC平安保险
        {
            //代步保障服务和/或轮胎保障服务
            reslutss=allss.toString()+commanstring().replace("代步保障服务和/或钥匙保障","轮胎保障服务和/或代步保障").replace("太保上分","平安财险上海分公司").replace("钥匙保障服务","轮胎保障服务").replace("和/或人保上海分公司","");
            reslutss=reslutss.replace("代步保障服务和/或轮胎保障","轮胎保障服务和/或代步保障");
            reslutss=reslutss.replace("代步保障服务和轮胎保障","轮胎保障服务和/或代步保障");
            reslutss=reslutss.replace("轮胎保障服务和代步保障","轮胎保障服务和/或代步保障");

            reslutss+=rtistringPAIC(0)+tirestring()+carstring(icode,0);
        }
        return reslutss;
    }

    private String formatdate(String datess,String formatss)
    {
        String enddate="";
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat dfnew=new SimpleDateFormat(formatss);
        try {
            Date date=df.parse(datess);

            enddate=dfnew.format(date );
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return enddate;
    }
    private String commanstringMore()
    {
        StringBuilder allss = new StringBuilder();
        String title="II．\t通用条款";
        allss.append("<div align=\"center\" class=\"print-body\" ><b>" + title + "</b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;感谢您签署本服务凭证购买全损保障服务、代步保障服务、钥匙保障服务和/或轮胎保障服务。请您仔细阅读本服务凭证条款和条件，包括但不限于本通用条款和下文各保障服务产品条款。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>1.\t服务凭证双方</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.1.\t本服务凭证由您和经销商于本服务凭证第I．部分签署栏所载日期共同签署。\n" +
                "1.2.\t本服务凭证项下全损保障服务、代步保障服务、钥匙保障服务和/或轮胎保障服务（依适用）应由经销商按照本服务凭证条款和条件向您提供。如果经销商因其过错或者任何其他原因无法提供该等服务，将由另一家经销商提供相关服务。\n</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>2.\t服务产品价格</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.\t在本服务凭证签署时，您应按照经销商提供的无忧出行服务价格表向经销商足额支付相应的服务产品款项。\n" +
                "2.2.\t在收到您所支付的服务产品价格全额后三（3）个工作日内，经销商应向您开具相应金额的发票。\n</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>3.\t服务地区及适用法律</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;3.1.\t本服务凭证项下的服务地区为中华人民共和国境内（仅为本服务凭证之目的，不包括香港特别行政区、澳门特别行政区和台湾地区）。\n" +
                "3.2.\t本服务凭证以及因本服务凭证所产生或与之有关的所有事宜均应受中华人民共和国法律管辖，并以中华人民共和国法律作理解、解释和执行。\n" +
                "3.3.\t如发生争议，您和经销商均有权向经销商所在地有管辖权的法院提起诉讼。\n</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>4.\t服务凭证生效和终止</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.1.\t就本服务凭证项下每一服务产品，本服务凭证项下该服务产品相关条款自签署日次日零时起生效，至相关服务期限届满、相应服务提供完毕时或根据下文第6.6条终止。\n" +
                "4.2.\t若您已购买且尚未使用本服务凭证项下的任何服务产品（全损保障服务、代步保障服务、钥匙保障服务和/或轮胎保障服务（依适用）），您有权在任何时候提前六十（60）天书面通知经销商提前终止本服务凭证，前提是您应签署和/或提供经销商所需的与服务凭证提前终止有关的文件和证明。\n" +
                "4.3.\t尽管有上文第4.2条之规定，若您已购买且使用本服务凭证项下的全损保障服务，经确认符合相关服务规则和要求并完成车辆置换的，则本服务凭证项下其他全部服务产品的相关条款提前终止，您应签署和/或提供经销商所需的与服务凭证提前终止有关的文件和证明。\n" +
                "4.4.\t若本服务凭证按照上文第4.2条和/或第4.3条之规定被提前终止，则您已支付的相关服务产品价格将自您签署提前终止服务凭证文件之日起按照该服务产品的服务期限剩余天数占整个服务期限的比例予以计算，经销商将收到上述终止通知后六十（60）天内向您返还相应比例部分的服务产品价格余额（如有）。\n" +
                "4.5.\t若您已使用本服务凭证项下的任一服务产品（上文第4.3条规定的全损保障服务除外），则本服务凭证不得提前终止。\n</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>5.\t您的隐私与信息安全</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.1.\t经销商仅可为本服务凭证之目的，向您收集与本服务凭证项下所述服务产品相关的个人信息。对于此类信息，经销商应确保其员工、代表、代理人、经理和关联企业严格保密，且不得向任何第三方披露（除非为履行本服务凭证而合法使用，或根据相关法律法规或监管部门要求)。\n" +
                "5.2.\t前款第5.1条当中的个人信息，应依据《个人信息保护法》等适用的法律、法规进行解读。就本服务凭证及其包含的相关服务的履行而言，经销商可能向您收集或需要向您核对确认如下个人信息及其他必要信息：\n" +
                "基本个人信息（姓名、手机号码、邮箱、身份证号等）； \n" +
                "车辆信息（品牌、车架号、车系、车型、发票金额、购车发票影像等）； \n" +
                "车辆的保险信息（商业保险保单号、保单有效日期、商业保险保单影像、交强险保单号等）； \n" +
                "车辆如需获得具体服务，所需补充提供的其他相关证明信息（以\"全损保障服务\"\"为例，依据保险公司规定的不同理赔类型所对应的理赔信息，可能需要您包括但不限于出险通知书、要求置换的机动车的服务凭证、已发生符合服务凭证约定的全损或推定全损的证明、享权车辆行驶本复印件、驾驶本复印件、身份证复印件等）。\n" +
                "5.3.\t前款第5.1条所述的向第三方披露、允许第三方合法使用的情况，仅限于此等第三方是受到经销商的委托或与经销商合作，为了向您提供本服务凭证下的相关服务，确有必要获取您的个人信息的主体，包括为本服务凭证提供产品销售服务、取消/终止服务及保险理赔支持服务的第三方，以及提供各项具体保障服务的具体经销商或其他服务商。具体而言 ，经销商就本服务凭证自行或委托安联世合国际救援服务（北京）有限公司（“安联世合”）向中国人寿财产保险股份有限公司青岛市分公司（“国寿财青岛分公司”）、中国大地财产保险股份有限公司上海分公司（“大地上分”）、中国太平洋财产保险股份有限公司北京分公司（“太保北分”）投保相关保险  ，经销商可因投保、保险理赔等事项委托安联世合、国寿财青岛分公司、大地上分、太保北分及相关领域的合作伙伴（“合作伙伴”）处理相关事宜。\n</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>6.\t您的义务</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;6.1.\t您必须正确且合理使用本服务凭证项下的享权车辆。\n" +
                "6.2.\t本服务凭证不得转让。若您在本服务凭证项下任一服务产品的服务期限内转让享权车辆，则会导致您丧失本服务凭证项下的一切权益。若您确需在本服务凭证项下任一服务产品的服务期限内转让享权车辆，则在尚未申请提供服务的前提下您可以按照上文第4.2条规定向经销商申请提前终止本服务凭证。  \n" +
                "6.3.\t在申请服务时，您应按照相关服务产品条款：\n" +
                "6.3.1.\t及时与经销商联系；\n" +
                "6.3.2.\t尽快将本服务凭证项下的享权车辆送至经销商处维修（如适用）；且\n" +
                "6.3.3.\t尽快向经销商提供与该等服务相关的各种证明和资料，并确保该等证明和资料具有合法性、真实性和完整性。\n" +
                "6.4.\t如因您未能完全履行上文第6.1条约定的义务，导致服务部分或全部无法提供，经销商对此不承担责任。\n" +
                "6.5.\t如因您未能完全履行上文第6.1条约定的义务，导致授经销商遭受任何损失，经销商有权向您索赔该等损失，包括但不限于要求您支付等额于已产生之费用或已给予之优惠折扣的金额。\n" +
                "6.6.\t在出现上文第6.4条或第6.5条所述情况时，经销商有权向您发出书面通知立即终止本服务凭证项下与相关服务产品有关的条款，且相关服务产品价格将不予退还。\n</div>");


        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>7.\t其他相关定义</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;7.1.\t“《中国保险行业协会商业车险综合示范条款》”：中国保险行业协会发布的，以机动车辆本身及其第三者责任等为保险标的的一种运输工具保险。\n" +
                "7.2.\t“全损或推定全损的证明”：是指在享权车辆所投保的商业车险有效期间内，享权车辆发生保险事故后，中国境内的保险公司根据《中国保险行业协会商业车险综合示范条款》为享权车辆开具的全损或推定全损证明文件。\n" +
                "7.3.\t“服务期限”：本服务凭证第I.部分所载的服务期限。\n" +
                "7.4.\t“享权车辆”：本服务凭证中列明的车辆（以登记的车架号（VIN）为准）。\n</div>");
        return  allss.toString();
    }
    private String commanstring()
    {
        StringBuilder allss = new StringBuilder();
        String title="II．\t通用条款";
        allss.append("<div align=\"center\" class=\"print-body\" ><b>" + title + "</b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;感谢您签署本服务凭证购买保时捷品牌全损保障服务、代步保障服务和/或钥匙保障服务。请您仔细阅读本服务凭证条款和条件，包括但不限于本通用条款和下文各保障服务产品条款。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>1.\t服务凭证双方</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.1.\t本服务凭证由您和授权经销商于本服务凭证第I．部分签署栏所载日期共同签署。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.2.\t本服务凭证项下全损保障服务、代步保障服务和钥匙保障服务（依适用）应由授权经销商按照本服务凭证条款和条件向您提供。如果授权经销商因其过错无法提供该等服务，将由经保时捷（上海）商务服务有限公司指定的另一家保时捷授权经销商提供相关服务。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>2.\t服务产品价格</u></b></div>");
      //  allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.\t在本服务凭证签署时，您应立即且毫无延迟地按照下文相关保障服务产品条款所载的服务产品价格表向授权经销商足额支付相应的服务产品价格。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.\t在本服务凭证签署时，您应按照授权经销商提供的保障服务价格表向授权经销商足额支付相应的服务产品款项。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.2.\t在收到您所支付的服务产品价格全额后[三（3）个]工作日内，授权经销商应向您开具相应金额的发票。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>3.\t服务地区及适用法律</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;3.1.\t本服务凭证项下的服务地区为中华人民共和国境内（仅为本服务凭证之目的，不包括香港特别行政区、澳门特别行政区和台湾地区）。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;3.2.\t本服务凭证以及因本服务凭证所产生或与之有关的所有事宜均应受中华人民共和国法律管辖，并以中华人民共和国法律作理解、解释和执行。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;3.3.\t如发生争议，您和授权经销商均有权向授权经销商所在地有管辖权的法院提起诉讼。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>4.\t服务凭证生效和终止</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.1.\t就本服务凭证项下每一服务产品，本服务凭证项下该服务产品相关条款自签署日次日零时起生效，至相关服务期限届满、相应服务/保险保障（依适用）提供完毕时或根据下文第6.6条终止。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.2.\t就本服务凭证项下您已购买但尚未申请提供服务的全损保障服务、钥匙保障服务和代步保障服务（依适用），您有权在任何时候提前[六十（60）]天书面通知授权经销商终止该等全部或部分服务产品相关条款，但前提是您应签署和/或提供授权经销商所需的与服务凭证终止有关的文件和证明。您已支付的相关服务产品价格将自您书面提出申请之日起按照该服务产品的服务期限剩余天数占整个服务期限的比例予以计算，授权经销商将收到上述终止通知后六十（60）天内向您返还相应比例部分的服务产品价格余额（如有）。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>5.\t您的隐私与信息安全</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.1.\t授权经销商仅可为本服务凭证之目的，向您收集与本服务凭证项下所述服务产品相关的个人信息。对于此类信息，授权经销商应确保其员工、代表、代理人、经理和关联企业严格保密，且不得向任何第三方披露（除非为履行本服务凭证而合法使用，或根据相关法律法规或监管部门要求)。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>6.\t您的义务</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;6.1.\t您必须正确且合理使用本服务凭证项下的享权车辆。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;6.2.\t本服务凭证于相关服务期限内不可转让。除非经授权经销商事先书面同意，若享权车辆之所有权于相关服务期限内发生变更，则将导致您和/或新所有权人无法使用本服务凭证项下任一服务产品。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;6.3.\t在申请服务/保险保障（依适用）时，您应按照相关服务产品条款：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.3.1.\t及时与授权经销商联系；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.3.2.\t尽快将本服务凭证项下的享权车辆送至授权经销商处维修（如适用）；且</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.3.3.\t尽快向授权经销商和/或太保上分（依适用）提供与该等服务/保险保障（依适用）相关的各种证明和资料，并确保该等证明和资料具有合法性、真实性和完整性。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;6.4.\t如因您未能完全履行上文第6.1条约定的义务，导致服务/保险保障（依适用）部分或全部无法提供，授权经销商对此不承担责任。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;6.5.\t如因您未能完全履行上文第6.1条约定的义务，导致授权经销商遭受任何损失，授权经销商有权向您索赔该等损失，包括但不限于要求您支付等额于已产生之费用或已给予之优惠折扣的金额。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;6.6.\t在出现上文第6.4条或第6.5条所述情况时，授权经销商有权向您发出书面通知立即终止本服务凭证项下与相关服务产品有关的条款，且相关服务产品价格将不予退还。</div>");


        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>7.\t其他相关定义</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;7.1.\t“《中国保险行业协会商业车险综合示范条款》”：中国保险行业协会发布的，以机动车辆本身及其第三者责任等为保险标的的一种运输工具保险。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;7.2.\t“《中国保险行业协会新能源汽车商业保险专属条款》”：中国保险行业协会发布的，以新能源汽车本身及其第三者责任等为保险标的的一种运输工具保险。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;7.3.\t“全损或推定全损的证明”：是指在享权车辆所投保的商业车险有效期间内，享权车辆发生保险事故后，中国境内的保险公司根据《中国保险行业协会商业车险综合示范条款》或《中国保险行业协会新能源汽车商业保险专属条款》为享权车辆开具的全损或推定全损证明文件。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;7.4.\t“服务期限”：本服务凭证第I．部分所载的服务期限。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;7.5.\t“享权车辆”：本服务凭证中列明的车辆（以登记的车架号（VIN）为准）。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;7.6.\t本服务凭证所述“授权经销商”均指第I部分服务凭证信息页第二条所载的与您签订了《保时捷保障服务凭证》的授权经销商。</div>");

        return  allss.toString();
    }
    private  String rtistringMore()
    {
        StringBuilder allss = new StringBuilder();
        String title="III．\t全损保障服务条款";
        allss.append("<div align=\"center\" class=\"print-body\" ><b>" + title + "</b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>1.\t服务保障内容</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.1.\t享权车辆在服务期限内发生了本全损保障服务条款第4.2条所载的“相关车险”责任范围内的全损或推定全损且中国境内的保险公司根据《中国保险行业协会商业车险综合示范条款》为享权车辆开具全损或推定全损的证明后，在服务期限内您按照本服务凭证条款在经销商处购置新车并向其申请全损保障服务的，经销商将按照本服务凭证条款为您提供全损保障服务。\n" +
                "1.2.\t在服务期限内，您最多享受一次全损保障服务。\n</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>2.\t服务规则</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.\t机动车置换费用 = 享权车辆折旧费用 + 机动车置换补偿。\n" +
                "2.1.1.\t享权车辆折旧费用 = 享权车辆购车发票价 * 已使用月数 * 月折旧率（即0.6%）；\n" +
                "如果置换时新车购置价低于原车购置价，则：享权车辆购车发票价为享权车辆申请全损保障服务时新车购置价；\n" +
                "如果置换时新车购置价高于原车购置价，则：享权车辆购车发票价为享权车辆申请全损保障服务时原车购置价；   \n" +
                "2.1.2.\t已使用月数指您的车辆购车发票显示的购车日期至发生全损或推定全损事故之日计算的已使用月数，不足一月，不计算折旧；及\n" +
                "2.1.3.\t机动车置换补偿包括交付新车在进行新车注册登记过程中发生的车辆购置税、关税、车船税、验车费等税费及在交付新车过程中发生的运输费等（在相关新车税率政策不变的情况下，享权车辆的前述相关税费与置换新车的前述相关税费两者取低值），且额外重置费用不超过享权车辆购车发票价的15%。\n</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>3.\t服务期限</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;本服务凭证生效后至多连续24个月，以第I.部分服务凭证信息页所载的服务期限为准。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>4.\t享权车辆要求</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.1.\t享权车辆应符合下列条件：\n" +
                "4.1.1.\t在购买本服务产品时车龄不超过1个月，系九座以下（含九座）上汽大众、一汽大众、捷达、斯柯达或奥迪品牌车辆，且所有权未发生过转让；\n" +
                "4.1.2.\t必须按照厂家车辆使用手册、厂家说明书等相关规定使用；\n" +
                "4.1.3.\t使用性质为非营运、非公共服务用途车辆、非比赛竞赛用车；及\n" +
                "4.1.4.\t为非试乘试驾车、非租赁公司车辆。\n" +
                "4.2.\t在购买本服务产品时，享权车辆已经全额投保了下列“相关车险”：\n" +
                "4.2.1.\t“相关车险”必须包括机动车损失保险。\n" +
                "4.3.\t享权车辆在服务期限内遭受保险事故时，所投保的上述“相关车险”险种依然有效且构成上述“相关车险”责任范围内的全损或推定全损。\n</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>5.\t服务保障除外情形</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;<b>5.1.\t您未能提供享权车辆投保商业车险的承保公司所出具的经盖章的全损或推定全损的证明；\n" +
                "5.2.\t您要求置换的机动车的身份认证信息、使用用途与本服务凭证中记载的享权车辆不一致；\n" +
                "5.3.\t享权车辆的车辆登记证书上的所有人信息与本服务凭证所载客户信息不一致；   \n" +
                "5.4.\t享权车辆遭受的损害事故发生在本服务凭证生效前，或您未在服务期限内申请全损保障服务；\n" +
                "5.5.\t享权车辆因遭受损害事故仅导致部分损失；\n" +
                "5.6.\t享权车辆非因本服务凭证所列原因导致全损或推定全损；\n" +
                "5.7.\t在新车购置过程中，因更换品牌、型号、规格等原因产生的额外费用；\n" +
                "5.8.\t任何形式的人身伤害、财产损失，及除本全损保障服务条款所列额外重置费用外其他任何服务费用支出；\n" +
                "5.9.\t新车购置过程中所产生的任何间接损失或赔偿责任；\n" +
                "5.10.\t“相关车险”责任范围内的全损或推定全损系因如下行为或原因导致：\n" +
                "5.10.1.\t您的故意行为、重大过失、欺诈、不诚实、违法犯罪行为；\n" +
                "5.10.2.\t您不当使用、维护、保管享权车辆；\n" +
                "5.10.3.\t享权车辆内在或潜在缺陷、自然磨损、自然损耗；\n" +
                "5.10.4.\t战争、敌对行为、军事行动、武装冲突、恐怖主义活动、罢工、暴动、骚乱；\n" +
                "5.10.5.\t核爆炸、核裂变、核聚变；\n" +
                "5.10.6.\t放射性污染及其他各种环境污染；\n" +
                "5.10.7.\t行政行为、司法行为。</b>\n</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>6.\t服务保障申请材料</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;6.1.\t请您务必保管好下述材料，并在申请全损保障服务时向经销商提交或出示：\n" +
                "6.1.1.\t本服务凭证原件；\n" +
                "6.1.2.\t享权车辆发生全损或推定全损时仍处于生效状态的机动车辆保险的保单复印件；\n" +
                "6.1.3.\t享权车辆行驶本复印件、客户驾驶本复印件、客户身份证或企业营业执照复印件\n" +
                "6.1.4.\t享权车辆首次购车发票复印件；\n" +
                "6.1.5.\t机动车辆保险公司已按照全损、推定全损赔付完毕的证明（划账水单原件，一次性赔偿协议原件或机动车辆保险公司盖章的复印件）；\n" +
                "6.1.6.\t享权车辆全损或推定全损后处理手续（权益转让书、拍卖或注销解体手续）；\n" +
                "6.1.7.\t证明事故发生原因的证明，属于道路交通事故的提供事故证明等等（证明材料复印件，机动车辆保险公司盖章）；\n" +
                "6.1.8.\t机动车置换费用的相关材料、单据，包括但不限于：\n" +
                "a.新车购车合同；\n" +
                "b.新车的车辆购置税完税证明；\n" +
                "c.新车的机动车辆保险的保单及保费发票；\n" +
                "d.新车的服务凭证和服务费发票，如有；\n" +
                "e.新车交付所涉及项目的费用发票。 \n" +
                "6.2.\t请您按照提供全损保障服务经销商的要求向其支付机动车辆保险之全损或推定全损的赔付款项。前述要求构成全损保障服务的服务前提，若您未能按照提供全损保障服务经销商的要求向其支付机动车辆保险之全损或推定全损的赔付款项，则经销商将无法向您提供全损保障服务。\n</div>");
        return  allss.toString();
    }

    private  String rtistringnew(int print_version)
    {
        StringBuilder allss = new StringBuilder();
        String title="III．\t全损保障服务条款";
        allss.append("<div align=\"center\" class=\"print-body\" ><b>" + title + "</b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>1.\t服务保障内容</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.1.\t享权车辆在服务期限内发生了本全损保障服务条款第4.2条所载的“相关车险”责任范围内的全损或推定全损且中国境内的保险公司根据《中国保险行业协会商业车险综合示范条款》或《中国保险行业协会新能源汽车商业保险专属条款》为享权车辆开具全损或推定全损的证明后，在服务期限内您按照本服务凭证条款在授权经销商处购置新车并向其申请全损保障服务的，授权经销商将按照本服务凭证条款为您提供新车置换优惠折扣服务。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.2.\t在服务期限内，您最多享受一次新车置换优惠折扣服务。</div>");
       if (print_version>=1)
       {
           allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>2.\t服务规则</u></b></div>");
           allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.\t基础套餐</div>");
           allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.1.1.\t新车置换优惠折扣 = 享权车辆置换费用。</div>");
           allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.2.\t升级套餐和保时捷活动套餐</div>");
           allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.2.1.\t新车置换优惠折扣 = 享权车辆置换费用 + 额外重置费用。</div>");
           allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.3.\t在基础套餐、升级套餐和保时捷活动套餐项下：</div>");
           allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.3.1.\t享权车辆置换费用 = 享权车辆购车发票价 – 享权车辆由机动车辆保险公司核定全损或推定全损的损失金额（含残值费用等）+ 享权车辆购置税；</div>");
           allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.3.2.\t“额外重置费用”包括新车在进行新车注册登记和交付过程中发生的车辆上牌费、车船税及中国太平洋财产保险股份有限公司承保的机动车交通事故责任强制保险和机动车商业车险（仅限机动车损失险/新能源车损失险、第三者责任险）保费；</div>");
           allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.3.3.\t新车置换优惠折扣应不得超过享权车辆购车发票价（含增值税，不含车辆购置税）的32%。</div>");


       }else
       {
           allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>2.\t服务规则</u></b></div>");
           allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.\t基础套餐</div>");
           allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.1.1.\t新车置换优惠折扣 = 享权车辆折旧费用。</div>");
           allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.2.\t升级套餐</div>");
           allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.2.1.\t新车置换优惠折扣 = 享权车辆折旧费用 + 额外重置费用。</div>");
           allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.3.\t在基础套餐和升级套餐项下：</div>");
           allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.3.1.\t享权车辆折旧费用 = 享权车辆购车发票价 * 已使用月数 * 月折旧率（即0.6%）；</div>");
           allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.3.2.\t已使用月数指您的车辆购车发票显示的购车日期至发生全损或推定全损事故之日计算的已使用月数，不足一月，不计算折旧；</div>");
           allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.3.3.\t月折旧率应参考如下折旧系数表，根据享权车辆的车辆类型和价格区间（依适用）所对应的折旧系数进行车辆折旧费用的计算；</div>");
           allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;车辆折旧系数表</div>");
           StringBuilder   ss = new StringBuilder();
           ss.append("<table class=\"table-print\" border=\"1\"  width=\"60%\" >");
           ss.append("<tr><td>车辆类型</td><td>车辆价格区间</td><td>月折旧系数</td></tr>");
           ss.append("<tr><td>燃油汽车</td><td>/</td><td>0.60%</td></tr>");
           ss.append("<tr><td rowspan=\"4\">纯电动汽车</td><td>0-100,000元</td><td>0.82%</td></tr>");
           ss.append("<tr><td>100,000-200,000元</td><td>0.77%</td></tr>");
           ss.append("<tr><td>200,000-300,000元</td><td>0.72%</td></tr>");
           ss.append("<tr><td>>300,000元</td><td>0.68%</td></tr>");
           ss.append("<tr><td>插电式混合动力与燃料电池汽车</td><td>/</td><td>0.63%</td></tr>");
           ss.append("</table>");
           allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
           allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.3.4.\t额外重置费用包括交付新车在进行新车注册登记过程中发生的车辆购置税、关税、车船税、验车费等税费及在交付新车过程中发生的运输费等（在相关新车税率政策不变的情况下，享权车辆的前述相关税费与置换新车的前述相关税费两者取低值 ），且额外重置费用不超过享权车辆购车发票价的15%。</div>");

       }




        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>3.\t服务期限</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;本服务凭证生效后至多连续36个月，以第I．部分服务凭证信息页所载的服务期限为准。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>4.\t享权车辆要求</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.1.\t享权车辆应符合下列条件：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.1.\t在购买本服务产品时车龄不超过1个月，系九座以下（含九座）保时捷品牌车辆，且所有权未发生过转让；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.2.\t必须按照厂家车辆使用手册、厂家说明书等相关规定使用；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.3.\t使用性质为非营运、非公共服务用途车辆、非比赛竞赛用车；及</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.4.\t为非试乘试驾车、非租赁公司车辆；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.2.\t在购买本服务产品时，享权车辆已经全额投保了下列“相关车险”：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.1.\t“相关车险”必须包括机动车损失保险</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.3.\t享权车辆在服务期限内遭受保险事故时，所投保的上述“相关车险”险种依然有效且构成上述“相关车险”责任范围内的全损或推定全损。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>5.\t服务保障除外情形</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;<b>5.1.\t您未能提供享权车辆投保商业车险的承保公司所出具的经盖章的全损或推定全损的证明；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.2.\t您要求置换的机动车的身份认证信息、使用性质与本服务凭证中记载的享权车辆不一致；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.3.\t享权车辆的所有人信息与本服务凭证所载客户信息不一致；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.4.\t享权车辆遭受的损害事故发生在本服务凭证生效前，或您未在服务期限内提出新车购置要求；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.5.\t享权车辆因遭受损害事故仅导致部分损失；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.6.\t享权车辆非因本服务凭证所列原因导致全损或推定全损；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.7.\t在新车购置过程中，因更换品牌、型号、规格等原因产生的额外费用；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.8.\t任何形式的人身伤害、财产损失，及除本全损保障服务条款所列额外重置费用外其他任何服务费用支出；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.9.\t新车购置过程中所产生的任何间接损失或赔偿责任；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.10.\t“相关车险”责任范围内的全损或推定全损系因如下行为或原因导致：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.10.1.\t您的故意行为、重大过失、欺诈、不诚实、违法犯罪行为；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.10.2.\t您不当使用、维护、保管享权车辆；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.10.3.\t享权车辆内在或潜在缺陷、自然磨损、自然损耗；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.10.4.\t战争、敌对行为、军事行动、武装冲突、恐怖主义活动、罢工、暴动、骚乱；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.10.5.\t核爆炸、核裂变、核聚变；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.10.6.\t放射性污染及其他各种环境污染；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.10.7.\t行政行为、司法行为；</b></div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>6.\t服务保障申请材料</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;6.1.\t请您务必保管好下述材料，并在申请全损保障服务时与下列申请材料一并向授权经销商提交或出示，包括但不限于：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.1.1.\t本服务凭证原件；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.1.2.\t享权车辆购车发票（原件影印件）；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.1.3.\t事故发生原因证明；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.1.4.\t享权车辆车损险已赔付完毕证明；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.1.5.\t享权车辆承保保险公司出具的一次性定损协议或推定全损协议；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.1.6.\t享权车辆后续处理手续相关文件。</div>");
        return  allss.toString();
    }
    private  String rtistringPAIC(int print_version)
    {
        StringBuilder allss = new StringBuilder();
        String title="III．\t全损保障服务条款";
        allss.append("<div align=\"center\" class=\"print-body\" ><b>" + title + "</b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>1.\t服务保障内容</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.1.\t享权车辆在服务期限内发生了本全损保障服务条款第4.2条所载的“相关车险”责任范围内的全损或推定全损且中国境内的保险公司根据《中国保险行业协会商业车险综合示范条款》或《中国保险行业协会新能源汽车商业保险专属条款》为享权车辆开具全损或推定全损的证明后，在服务期限内您按照本服务凭证条款在授权经销商处购置新车并向其申请全损保障服务的，授权经销商将按照本服务凭证条款为您提供新车置换优惠折扣服务。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.2.\t在服务期限内，您最多享受一次新车置换优惠折扣服务。</div>");
        if (print_version>=1)
        {
            allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>2.\t服务规则</u></b></div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.\t基础套餐</div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.1.1.\t新车置换优惠折扣 = 享权车辆置换费用。</div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.2.\t升级套餐和保时捷活动套餐</div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.2.1.\t新车置换优惠折扣 = 享权车辆置换费用 + 额外重置费用。</div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.3.\t在基础套餐、升级套餐和保时捷活动套餐项下：</div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.3.1.\t享权车辆置换费用 = 享权车辆购车发票价 – 享权车辆由机动车辆保险公司核定全损或推定全损的损失金额（含残值费用等）+ 享权车辆购置税；</div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.3.2.\t“额外重置费用”包括新车在进行新车注册登记和交付过程中发生的车辆上牌费、车船税及中国平安财产保险有限公司承保的机动车交通事故责任强制保险和机动车商业车险（仅限机动车损失险/新能源车损失险、第三者责任险）保费；</div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.3.3.\t新车置换优惠折扣应不得超过享权车辆购车发票价（含增值税，不含车辆购置税）的32%。</div>");


        }else
        {
            allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>2.\t服务规则</u></b></div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.\t基础套餐</div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.1.1.\t新车置换优惠折扣 = 享权车辆折旧费用。</div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.2.\t升级套餐</div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.2.1.\t新车置换优惠折扣 = 享权车辆折旧费用 + 额外重置费用。</div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.3.\t在基础套餐和升级套餐项下：</div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.3.1.\t享权车辆折旧费用 = 享权车辆购车发票价 * 已使用月数 * 月折旧率（即0.6%）；</div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.3.2.\t已使用月数指您的车辆购车发票显示的购车日期至发生全损或推定全损事故之日计算的已使用月数，不足一月，不计算折旧；及</div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.3.3.\t月折旧率应参考如下折旧系数表，根据享权车辆的车辆类型和价格区间（依适用）所对应的折旧系数进行车辆折旧费用的计算；</div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;车辆折旧系数表</div>");


            StringBuilder   ss = new StringBuilder();
            ss.append("<table class=\"table-print\" border=\"1\"  width=\"60%\" >");
            ss.append("<tr><td>车辆类型</td><td>车辆价格区间</td><td>月折旧系数</td></tr>");
            ss.append("<tr><td>燃油汽车</td><td>/</td><td>0.60%</td></tr>");
            ss.append("<tr><td rowspan=\"4\">纯电动汽车</td><td>0-100,000元</td><td>0.82%</td></tr>");
            ss.append("<tr><td>100,000-200,000元</td><td>0.77%</td></tr>");
            ss.append("<tr><td>200,000-300,000元</td><td>0.72%</td></tr>");
            ss.append("<tr><td>>300,000元</td><td>0.68%</td></tr>");
            ss.append("<tr><td>插电式混合动力与燃料电池汽车</td><td>/</td><td>0.63%</td></tr>");
            ss.append("</table>");
            allss.append("<td align=\"center\" bgcolor=\"#fff\">"+ss.toString()+" </td></tr>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.3.4.\t额外重置费用包括交付新车在进行新车注册登记过程中发生的车辆购置税、关税、车船税、验车费等税费及在交付新车过程中发生的运输费等（在相关新车税率政策不变的情况下，享权车辆的前述相关税费与置换新车的前述相关税费两者取低值 ），且额外重置费用不超过享权车辆购车发票价的15%。</div>");

        }


        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>3.\t服务期限</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;本服务凭证生效后至多连续36个月，以第I.部分服务凭证信息页所载的服务期限为准。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>4.\t享权车辆要求</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.1.\t享权车辆应符合下列条件：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.1.\t在购买本服务产品时车龄不超过1个月，系九座以下（含九座）保时捷品牌车辆；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.2.\t必须按照厂家车辆使用手册、厂家说明书等相关规定使用；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.3.\t使用性质为非营运、非公共服务用途车辆、非比赛竞赛用车；及</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.4.\t为非试乘试驾车、非租赁公司车辆；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.2.\t在购买本服务产品时，享权车辆已经全额投保了下列“相关车险”：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.1.\t“相关车险”必须包括机动车损失保险</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.3.\t享权车辆在服务期限内遭受保险事故时，所投保的上述“相关车险”险种依然有效且构成上述“相关车险”责任范围内的全损或推定全损。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>5.\t服务保障除外情形</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;<b>5.1.\t您未能提供享权车辆投保商业车险的承保公司所出具的经盖章的全损或推定全损的证明；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.2.\t您要求置换的机动车的身份认证信息、使用性质与本服务凭证中记载的享权车辆不一致；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.3.\t享权车辆的所有人信息与本服务凭证所载客户信息不一致；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.4.\t享权车辆遭受的损害事故发生在本服务凭证生效前，或您未在服务期限内提出新车购置要求；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.5.\t享权车辆因遭受损害事故仅导致部分损失；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.6.\t享权车辆非因本服务凭证所列原因导致全损或推定全损；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.7.\t在新车购置过程中，因更换品牌、型号、规格等原因产生的额外费用；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.8.\t任何形式的人身伤害、财产损失，及除本全损保障服务条款所列额外重置费用外其他任何服务费用支出；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.9.\t新车购置过程中所产生的任何间接损失或赔偿责任；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.10.\t“相关车险”责任范围内的全损或推定全损系因如下行为或原因导致：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.10.1.\t您的故意行为、重大过失、欺诈、不诚实、违法犯罪行为；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.10.2.\t您不当使用、维护、保管享权车辆；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.10.3.\t享权车辆内在或潜在缺陷、自然磨损、自然损耗；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.10.4.\t战争、敌对行为、军事行动、武装冲突、恐怖主义活动、罢工、暴动、骚乱；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.10.5.\t核爆炸、核裂变、核聚变；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.10.6.\t放射性污染及其他各种环境污染；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.10.7.\t行政行为、司法行为。</b></div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>6.\t服务保障申请材料</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;6.1.\t请您务必保管好下述材料，并在申请全损保障服务时与下列申请申请材料一并向授权经销商提交或出示，包括但不限于：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.1.1.\t行驶证照片；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.1.2.\t整车带牌照（或临牌）照片；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.1.3.\t整车损失照片；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.1.4.\t享权车辆车险承保保险公司开具的车辆全损或推定全损证明资料；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.1.5.\t警方或有关机关开具的事故证明；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.1.6.\t车辆登记证书；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.1.7.\t车辆购置税费凭证（缴税凭证）；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.1.8.\t车辆登记费用凭证（车辆登记费、牌照费用）。</div>");
        return  allss.toString();
    }


    private  String tirestringMore()
    {
        StringBuilder allss = new StringBuilder();
        String title="VI．\t轮胎保障服务条款";
        allss.append("<div align=\"center\" class=\"print-body\" ><b>" + title + "</b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>1.\t服务保障内容</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.1.\t享权车辆在服务期限内正常使用过程中，由于非人为故意原因导致轮胎出现本轮胎保障服务条款第1.2条所列意外单独损坏事故无法继续使用的 ，您在服务期限内按照本服务凭证条款到经销商处维修并向其提出更换轮胎的要求，经销商按照本服务凭证条款向您提供享权车辆车型原厂所搭载标配轮胎同品牌、同规格轮胎的免费更换服务 。\n" +
                "1.2.\t事故类型\n" +
                "1.2.1.\t爆胎：轮胎在极短的时间（一般以少于0.1秒）因破裂突然失去气体而瘪掉，裂口呈不规则撕裂状且穿透；\n" +
                "1.2.2.\t鼓包：轮胎本身某 部位有明显的起鼓现象。\n</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>2.\t服务规则</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;根据您的选择，享权车辆每次事故更换轮胎上限为1条同品牌、同规格的轮胎，全年累计更换轮胎上限为2条与其出厂时所搭载的同品牌、同规格的轮胎，2条轮胎申请更换间隔不得小于30天。 </div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>3.\t服务期限</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;本服务凭证生效后至多连续12个月或享权车辆行驶里程达两万公里，以先到者为准， 以第I.部分服务凭证信息页所载的服务期限为准。  </div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>4.\t享权车辆要求</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.1.\t享权车辆符合下列条件：\n" +
                "4.1.1.\t在购买本服务产品时车龄不超过30日，系七座以下（含七座） 的上汽大众、一汽大众、捷达、斯柯达或奥迪品牌新车，且享权车辆的所有权未发生过转让；\n" +
                "4.1.2.\t必须按照厂家车辆使用手册、厂家说明书等相关规定使用；\n" +
                "4.1.3.\t使用性质为非营运、非公共服务用途车辆、非比赛竞赛用车；及\n" +
                "4.1.4.\t为非试乘试驾车、非租赁公司、非登记于经销商名下的车辆 。 \n</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>5.\t服务保障除外情形</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;<b>5.1.\t您的故意或重大过失、欺诈行为所导致的轮胎事故；\n" +
                "5.2.\t轮胎的品牌、型号，及其所属享权车辆的身份认证信息与服务凭证中记载不一致；\n" +
                "5.3.\t轮胎损坏发生在服务凭证生效前，或您未在服务凭证约定的服务期限内申请轮胎保障服务；  \n" +
                "5.4.\t 轮胎生产时国内市场技术水平尚不能发现的缺陷；\n" +
                "5.5.\t地震、雷击、火灾、爆炸、暴雨、洪水、台风等灾害；\n" +
                "5.6.\t因自然磨损、腐蚀、人为损害、保管不当等原因导致的轮胎损坏；\n" +
                "5.7.\t受损轮胎被盗、被遗弃或其他任何原因导致无法收回轮胎；\n" +
                "5.8.\t轮胎的识别码、品牌型号等被破坏、移除、磨损或其他原因无法进行识别；\n" +
                "5.9.\t申请服务时，轮胎纹理磨损达到或已超过磨损线（即轮胎更换表示“▲”）或胎面剩余花纹磨损深度小于1.6mm；\n" +
                "5.10.\t货车、营运车辆、提供有偿服务的私人车辆及七座以上客车发生事故的轮胎损失；\n" +
                "5.11.\t因交通意外、轮胎安装错误、超速或超载行驶、过高或过低胎压等原因导致的轮胎损坏；及\n" +
                "5.12.\t轮胎曾进行过修补。</b>\n</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>6.\t服务保障申请材料</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;6.1.\t请您务必保管好本服务凭证，并在申请轮胎保障服务时与下列申请申请材料一并向经销商提交或出示，包括但不限于：\n" +
                "6.1.1.\t车主与享权车辆（需带牌照）的合影照片；\n" +
                "6.1.2.\t车辆损失照片：\n" +
                "a.受损轮胎方位的整车45°角照片（含有车牌/临时车牌；如轮胎已拆卸，请放车旁一并拍摄）；\n" +
                "b.受损轮胎侧远景照片（要求涵盖清晰、完整的轮胎信息）；\n" +
                "c.受损轮胎近景（要求清晰反映损失情况）+行驶证/临时牌照片；\n" +
                "d.受损轮胎生产编号/DOT+批次号+驾驶证照片；\n" +
                "e.其他所需证明资料（如：胎面、鼓包、伤口未泄气或伤口较小等损失的补充照片）；\n" +
                "6.1.3.\t车主的有效行驶证；\n" +
                "6.1.4.\t车主的有效身份证；\n" +
                "6.1.5.\t车主索赔声明（即：轮胎置换服务客户确认使用书）。\n</div>");

                allss.append("<br>");
        return  allss.toString();
    }
    private  String tirestring()
    {
        StringBuilder allss = new StringBuilder();
        String title="IV．\t轮胎保障服务条款";
        allss.append("<div align=\"center\" class=\"print-body\" ><b>" + title + "</b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>1.\t服务保障内容</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.1.\t享权车辆在服务期限内正常使用过程中，由于非人为故意原因的质量问题导致轮胎出现本轮胎保障服务条款第1.2条所列事故，您在服务期限内按照本服务凭证条款到授权经销商处维修并向其提出更换轮胎的要求，授权经销商按照本服务凭证条款向您提供享权车辆车型原厂所搭载标配轮胎同品牌、同规格轮胎的免费更换服务（客户自付费用除外）。</div>");
        // allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;1.1.1.\t不具备产品应当具备的使用性能而事先未作说明的；</div>");
        //allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;1.1.2.\t不符合在产品或者其包装上注明采用的产品标准的；</div>");
        //allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;1.1.3.\t不符合以产品说明、实物样品等方式表明的质量状况的。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.2.\t事故类型</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;1.2.1.\t爆胎：轮胎在极短的时间（一般以少于0.1秒）因破裂突然失去气体而瘪掉，裂口呈不规则撕裂状且穿透；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;1.2.2.\t鼓包：轮胎胎肩、胎侧部位有明显的起鼓现象。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>2.\t服务规则</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.\t据您的选择，享权车辆全年累计更换轮胎上限为2条同品牌、同规格的轮胎。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b>&nbsp;&nbsp;2.2.\t您需要按照轮胎零售价（包括相关税费）加人工费用总和的10%自行承担部分轮胎更换服务费用（“客户自付费用”）。</b></div>");

        //allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.3.\t您需要按照以下规则自行承担部分轮胎更换服务费用（“客户自付费用”）</div>");
        //allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.3.1.\t享权车辆从申请服务之日起算的车龄尚未超过90天，则不产生客户自付费用；</div>");
        //allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.3.2.\t享权车辆从申请服务之日起算的车龄在91-180天之间，则客户自付费用为轮胎零售价（包括相关税费）加人工费用的15%；</div>");
        //allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.3.3.\t享权车辆从申请服务之日起算的车龄在181-365天之间，则客户自付费用为轮胎零售价（包括相关税费）加人工费用的25%。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>3.\t服务期限</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;本服务凭证生效后连续12个月或享权车辆于本服务凭证生效后已累计行驶2万公里（以先到者为准），以第I.部分服务凭证信息页所载的服务期限为准。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>4.\t享权车辆要求</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.1.\t享权车辆应符合下列条件：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.1.\t在购买本服务产品时车龄不超过1个月，系九座以下（含九座）的保时捷品牌新车；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.2.\t必须按照厂家车辆使用手册、厂家说明书等相关规定使用；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.3.\t使用性质为非营运、非公共服务用途车辆、非比赛竞赛用车；及</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.4.\t为非试乘试驾车、非租赁公司车辆。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>5.\t服务保障除外情形</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;<b>5.1.\t您的故意或重大过失、欺诈行为所导致的轮胎事故；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.2.\t已超过服务期限或享权车辆于本服务凭证生效后已累计行驶2万公里（以先到者为准）；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.3.\t根据服务凭证第2.2条规定的轮胎更换服务费用中客户自行承担的部分；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.4.\t轮胎生产时国内市场技术水平尚不能发现的缺陷；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.5.\t地震、雷击、火灾、爆炸、暴雨、洪水、台风等灾害；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.6.\t因自然磨损、腐蚀、人为损害、保管不当等原因导致的轮胎损坏；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.7.\t受损轮胎被盗、被遗弃或其他任何原因导致无法收回轮胎；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.8.\t轮胎的识别码、品牌型号等被破坏、移除、磨损或其他原因无法进行识别；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.9.\t申请服务时，轮胎纹理磨损达到或已超过磨损线（即轮胎更换表示“▲”）或胎面剩余花纹磨损深度小于1.6mm；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.10.\t货车、营运车辆、提供有偿服务的私人车辆及七座以上客车发生事故的轮胎损失；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.11.\t因交通意外、轮胎安装错误、超速或超载行驶、过高或过低胎压等原因导致的轮胎损坏；及</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.12.\t轮胎曾进行过修补。</b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>6.\t服务保障申请材料</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;6.1.\t请您务必保管好本服务凭证，并在申请轮胎保障服务时与下列申请申请材料一并向授权经销商提交或出示，包括但不限于：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.1.1.\t享权车辆（需带牌照）的照片</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.1.2.\t服务凭证复印件</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.1.3.\t客户使用费服务确认书</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.1.4.\t车主的有效行驶证</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.1.5.\t损失轮胎位置照片</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.1.6.\t具体损坏部位照片</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.1.7.\t损失轮胎带DOT码的局部照片</div>");

        allss.append("<br>");
        return  allss.toString();
    }

    private  String carstring(String icode,int print_version)
    {
        if (print_version==3)return  carstring3();
        StringBuilder allss = new StringBuilder();
        String title="V．\t代步保障服务条款";
        if (print_version>=3)title="V．\t出险保障服务条款";
        allss.append("<div align=\"center\" class=\"print-body\" ><b>" + title + "</b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>1.\t服务保障内容</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.1.\t享权车辆在服务期限内发生本代步保障服务条款第1.2条所列“相关车险”的保险事故，您在服务期限内按照本服务凭证条款将享权车辆送至授权经销商处维修并向其提出使用代步保障服务的要求，授权经销商按照本服务凭证条款为您提供代步保障服务；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.2.\t本代步保障服务条款第1.1条所述“相关车险”包括：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;1.2.1.\t机动车损失保险；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;1.2.2.\t一个或多个附加险。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>2.\t服务规则</u></b></div>");
        //凭证服务期限内累计代步保障

        String ss="全年";
        if (print_version>=1)ss="凭证服务期限内";
        if (print_version<4)
        {
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.\t享权车辆单次事故代步保障服务天数上限为10天（含），"+ss+"累计代步保障服务天数上限为60天（含），配件等待期不计入代步保障服务天数；\n" +
                    "2.2.\t代步保障服务天数根据享权车辆机动车辆保险单次事故的定损金额确定：\n" +
                    "2.2.1.\t定损金额<1,600元，不触发代步保障服务；\n" +
                    "2.2.2.\t1,600 元≦定损金额<10,000 元，计为0.5天；\n" +
                    "2.2.3.\t10,000 元≦定损金额<20,000 元，计为1天；以此类推，定损金额每增加10,000元，代步车天数也将增加1天；\n" +
                    "2.2.4.\t定损金额≧100,000 元，计为10天，且达到单车事故代步车天数上限。\n" +
                    "2.3.\t如您超出规定的代步车天数不退还代步车，应向授权经销商支付超出天数相对应的代步车使用费。\n</div>");
            //2023年11月01日提交的单据使用新的服务凭证；
            if (print_version>=2)
            {
                allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.4.\t如您有特殊情况或需求的，经协商授权经销商可在其能力范围内根据您的需求为您提供以定损金额20%为限的同等规格的其他服务，具体以您与授权经销商协商结果为准。</div>");
            }
        }else
        {
            if (icode.equals("PAIC"))
            {
                allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.享权车辆单次事故出险保障服务上限为10天（含）代步车服务或价值20,000元的服务权益，服务期限内累计享权次数上限为2次；\n" +
                        "2.2.出险保障服务上限根据享权车辆机动车辆保险单次事故的定损金额确定：\n" +
                        "2.2.1.定损金额<5,000元，计为0.5天代步车或价值800元的服务权益；\n" +
                        "2.2.2.5,000 元≦定损金额<10,000元，计为2.5天代步车或价值5,000元的服务权益；\n" +
                        "2.2.3.10,000 元≦定损金额<50,000元，计为5天代步车或价值10,000元的服务权益；\n" +
                        "2.2.4.定损金额≧50,000元，计为10天代步车或价值20,000元的服务权益，且达到单车事故代步车服务天数/服务权益上限。\n" +
                        "2.3.配件等待期不计入代步车服务天数；\n" +
                        "<b>2.4.服务权益应用作享权车辆的相关服务，不得兑换成现金；</b>\n" +
                        "2.5.如您超出规定的代步车服务天数不退还代步车，应向授权经销商支付超出天数相对应的代步车使用费。\n" +
                        "2.6.如您有代步车服务外的其他服务需求，经协商授权经销商可在其能力范围内根据您的需求为您提供与对应服务权益同等价值的其他服务，具体以您与授权经销商协商结果为准。</div>");
            }else
            {
                allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.享权车辆单次事故出险保障服务上限为10天（含）代步车服务或价值20,000元的服务权益，服务期限内累计享权次数上限为2次；\n" +
                        "2.2.出险保障服务上限根据享权车辆机动车辆保险单次事故的定损金额确定：\n" +
                        "2.2.1.定损金额<5,000元，计为0.5天代步车或价值800元的服务权益；\n" +
                        "2.2.2.5,000 元≦定损金额<10,000元，计为2.5天代步车或价值5,000元的服务权益；\n" +
                        "2.2.3.10,000 元≦定损金额<50,000元，计为5天代步车或价值10,000元的服务权益；\n" +
                        "2.2.4.定损金额≧50,000元，计为10天代步车或价值20,000元的服务权益，且达到单车事故代步车服务天数/服务权益上限。\n" +
                        "2.3.配件等待期不计入代步车服务天数；\n" +
                        "<b>2.4.服务权益应用作享权车辆的相关服务，不得兑换成现金；</b>\n" +
                        "2.5.如您超出规定的代步车服务天数不退还代步车，应向授权经销商支付超出天数相对应的代步车使用费。\n" +
                        "2.6.如您有代步车服务外的其他服务需求，经协商授权经销商可在其能力范围内根据您的需求为您提供与对应服务权益同等价值的其他服务，具体以您与授权经销商协商结果为准。</div>");
            }
        }


        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>3.\t服务期限</u></b></div>");
        if (print_version>=4)
        {
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;3.1.本服务凭证生效后至多连续12个月，以第I.部分服务凭证信息页所载的服务期限为准。</div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;3.2.灵活套餐下，服务等待（生效）期为15天（含）。在此期间发生的意外事故不属于授权经销商服务责任范畴。 多份出险保障服务连续生效时，则服务生效日期晚于首份出险保障服务生效日期的出险保障服务不再适用等待期。</div>");

        }
       else allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;本服务凭证生效后至多连续12个月，以第I．部分服务凭证信息页所载的服务期限为准。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>4.\t享权车辆要求</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.1.\t享权车辆应符合下列条件：</div>");
        if (print_version>=1)
        {
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.1.\t在购买本服务产品时车龄不受限制，系保时捷品牌九座以下（含九座）车辆；但在灵活套餐下，车龄自享权车辆购车发票开具之日起算须超过270天（含）；</div>");

        }else
        {
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.1.\t保时捷品牌九座以下（含九座）车辆，不限定车龄；</div>");

        }
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.2.\t必须按照厂家车辆使用手册、厂家说明书等相关规定使用；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.3.\t使用性质为非营运、非公共服务用途车辆、非比赛竞赛用车；及</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.4.\t为非试乘试驾车、非租赁公司车辆。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.2.\t在购买本服务产品时，享权车辆已经全额投保了下列“相关车险”：</div>");
       // allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.1.\t“相关车险”必须包括机动车损失保险、不计免赔率险；及</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.1.\t“相关车险”必须包括机动车损失保险；及</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.2.\t“相关车险”也可以涵盖一个或多个附加险；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.3.\t享权车辆在服务期限内遭受意外事故时，所投保的上述“相关车险”依然有效且构成上述“相关车险”责任范围内的保险责任。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>5.\t服务保障除外情形</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;<b>5.1.\t由于下列原因造成享权车辆损失的，授权经销商不提供代步保障服务：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.1.\t地震及其次生灾害；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.2.\t战争、敌对行动、军事行为、武装冲突、罢工、骚乱、暴动、恐怖活动；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.3.\t行政行为或司法行为；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.4.\t竞赛、测试，在营业性维修、养护场所养护期间；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.5.\t您或驾驶人饮酒、吸食或注射毒品、被药物麻醉后使用享权车辆；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.6.\t改装、加装零部件；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.7.\t因修理质量不合格或处于返修期间的；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.8.\t您或驾驶人未及时修理、享权车辆进场后未及时开始修复或拖延修理时间的；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.9.\t享权车辆已列入汽车制造商产品召回范围且在行驶过程中因召回缺陷而发生损害；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.10.\t发生机动车辆保险责任范围外的原因造成的享权车辆损失。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.2.\t下列情况下，不论任何原因造成您的车辆损失，授权经销商不提供代步保障服务：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.2.1.\t发生全损或推定全损；</div>");
        if (print_version>=4)
        {
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.2.2.\t车辆玻璃单独损坏</div>");

            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.2.3.\t您、使用人、管理人或驾驶人的故意行为造成损失；</div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.2.4.\t享权车辆处于查封、扣押期间；</div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.2.5.\t享权车辆被盗窃、抢劫、抢夺，以及因被盗窃、抢劫、抢夺受到损坏。</b></div>");

        }else {
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.2.2.\t您、使用人、管理人或驾驶人的故意行为造成损失；</div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.2.3.\t享权车辆处于查封、扣押期间；</div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.2.4.\t享权车辆被盗窃、抢劫、抢夺，以及因被盗窃、抢劫、抢夺受到损坏。</b></div>");
        }

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>6.\t服务保障申请材料</u></b></div>");
        if (print_version>=3)
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.1.\t请您务必保管好本服务凭证，并在申请代步保障服务时与下列申请申请材料一并向授权经销商提交或出示，包括但不限于：\n" +
                "6.1.1.\t机动车辆保险公司的定损单（需盖章）或电子版定损单；\n" +
                "6.1.2.\t车主签署的出险保障服务使用确认书；\n</div>");
        else
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.1.\t请您务必保管好本服务凭证，并在申请代步保障服务时与下列申请申请材料一并向授权经销商提交或出示，包括但不限于：\n" +
                    "6.1.1.\t机动车辆保险公司的定损单（需盖章）或电子版定损单；\n" +
                    "6.1.2.\t车主签署的代步车使用确认书；\n</div>");
        if (icode.equals("PAIC"))
        {
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.1.3.\t机动车辆保险公司的赔款回执或支付截屏。\n</div>");
        }
       else allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.1.3.\t车主出行发票（仅在出行总费用超过10,000元时提供），发票形式限于打车发票、租车发票、服务费发票等；\n" +
                "6.1.4.\t机动车辆保险公司的赔款回执或支付截屏。\n</div>");
        //PAIC
        allss.append("<br>");
        return  allss.toString();
    }
    private  String carstring3()
    {
        StringBuilder allss = new StringBuilder();
        String title="V．\t出险保障服务条款";
        allss.append("<div align=\"center\" class=\"print-body\" ><b>" + title + "</b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>1.\t服务保障内容</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.1.\t享权车辆在服务期限内发生本出险保障服务条款第1.2条所列“相关车险”的保险事故，您在服务期限内按照本服务凭证条款将享权车辆送至授权经销商处维修并向其提出使用出险保障服务的要求，授权经销商按照本服务凭证条款为您提供出险保障服务；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.2.\t本出险保障服务条款第1.1条所述“相关车险”包括：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;1.2.1.\t机动车损失保险；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;1.2.2.\t一个或多个附加险。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>2.\t服务规则</u></b></div>");
        //凭证服务期限内累计代步保障


        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.\t享权车辆单次事故出险保障服务上限为10天（含）代步车服务或价值20,000元的服务权益，服务期限内累计享权次数上限为2次；\n" +
                "2.2.\t出险保障服务上限根据享权车辆机动车辆保险单次事故的定损金额确定：\n" +
                "2.2.1.\t定损金额<5,000元，计为0.5天代步车或价值800元的服务权益；\n" +
                "2.2.2.\t5,000 元≦定损金额<10,000 元，计为2.5天代步车或价值5,000元的服务权益；\n" +
                "2.2.3.\t10,000 元≦定损金额<30,000 元，计为5天代步车或价值10,000元的服务权益；\n" +
                "2.2.4.\t定损金额≧30,000 元，计为10天代步车或价值20,000元的服务权益，且达到单车事故代步车服务天数/出险保障服务权益上限。\n" +
                "2.3.\t配件等待期不计入代步车服务天数；\n"+
                "2.4.\t服务权益应用作享权车辆的相关服务，不得兑换成现金；\n" +
                "2.5.\t如您超出规定的代步车服务天数不退还代步车，应向授权经销商支付超出天数相对应的代步车使用费。\n" +
                "2.6.\t如您有代步车服务外的其他相关服务需求，经协商授权经销商可在其能力范围内根据您的需求为您提供与对应服务权益同等价值的其他服务，具体以您与授权经销商协商结果为准。\n</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>3.\t服务期限</u></b></div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;3.1.\t本服务凭证生效后至多连续12个月，以第I.部分服务凭证信息页所载的服务期限为准。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;3.2.\t灵活套餐下，服务等待（生效）期为15天（含）。在此期间发生的意外事故不属于授权经销商服务责任范畴。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>4.\t享权车辆要求</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.1.\t享权车辆应符合下列条件：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.1.\t在购买本服务产品时车龄不受限制，系保时捷品牌九座以下（含九座）车辆；但在灵活套餐下，车龄自享权车辆购车发票开具之日起算须超过30天；</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.2.\t必须按照厂家车辆使用手册、厂家说明书等相关规定使用；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.3.\t使用性质为非营运、非公共服务用途车辆、非比赛竞赛用车；及</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.4.\t为非试乘试驾车、非租赁公司车辆。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.2.\t在购买本服务产品时，享权车辆已经全额投保了下列“相关车险”：</div>");
        // allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.1.\t“相关车险”必须包括机动车损失保险、不计免赔率险；及</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.1.\t“相关车险”必须包括机动车损失保险；及</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.2.\t“相关车险”也可以涵盖一个或多个附加险；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.3.\t享权车辆在服务期限内遭受意外事故时，所投保的上述“相关车险”依然有效且构成上述“相关车险”责任范围内的保险责任。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>5.\t服务保障除外情形</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;<b>5.1.\t由于下列原因造成享权车辆损失的，授权经销商不提供出险保障服务：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.1.\t地震及其次生灾害；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.2.\t战争、敌对行动、军事行为、武装冲突、罢工、骚乱、暴动、恐怖活动；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.3.\t行政行为或司法行为；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.4.\t竞赛、测试，在营业性维修、养护场所养护期间；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.5.\t您或驾驶人饮酒、吸食或注射毒品、被药物麻醉后使用享权车辆；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.6.\t改装、加装零部件；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.7.\t因修理质量不合格或处于返修期间的；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.8.\t您或驾驶人未及时修理、享权车辆进场后未及时开始修复或拖延修理时间的；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.9.\t享权车辆已列入汽车制造商产品召回范围且在行驶过程中因召回缺陷而发生损害；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.10.\t发生机动车辆保险责任范围外的原因造成的享权车辆损失。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.2.\t下列情况下，不论任何原因造成您的车辆损失，授权经销商不提供出险保障服务：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.2.1.\t发生全损或推定全损；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.2.2.\t您、使用人、管理人或驾驶人的故意行为造成损失；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.2.3.\t享权车辆处于查封、扣押期间；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.2.4.\t享权车辆被盗窃、抢劫、抢夺，以及因被盗窃、抢劫、抢夺受到损坏。</b></div>");


        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>6.\t服务保障申请材料</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.1.\t请您务必保管好本服务凭证，并在申请出险保障服务时与下列申请材料一并向授权经销商提交或出示，包括但不限于：</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;6.1.1.\t机动车辆保险公司的定损单（需盖章）或电子版定损单；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;6.1.2.\t车主签署的出险保障服务使用确认书；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;6.1.3.\t车主出行发票（仅在出行总费用超过10,000元时提供），发票形式包括但不限于打车发票、租车发票、服务费发票等；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;6.1.4.\t机动车辆保险公司的赔款回执或支付截屏。</div>");
        allss.append("<br>");
        return  allss.toString();
    }
    private  String carstringMore()
    {
        StringBuilder allss = new StringBuilder();
        String title="IV．\t代步保障服务条款";
        allss.append("<div align=\"center\" class=\"print-body\" ><b>" + title + "</b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>1.\t服务保障内容</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.1.\t享权车辆在服务期限内发生本代步保障服务条款第1.2条所列“相关车险”的保险事故，您在服务期限内按照本服务凭证条款将享权车辆送至经销商处维修并向其提出使用代步车的要求，经销商按照本服务凭证条款为您提供代步车服务；\n" +
                "1.2.\t本代步保障服务条款第1.1条所述“相关车险”包括：\n" +
                "1.2.1.\t机动车损失保险；\n" +
                "1.2.2.\t一个或多个附加险。\n</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>2.\t服务规则</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.\t针对上汽大众、一汽大众、捷达或斯柯达品牌的享权车辆，单次事故代步车天数上限为5天（含），全年累计理赔服务次数上限为两次（含），配件等待期不计入代步车天数；\n" +
                "代步车天数根据享权车辆机动车辆保险的单次机动车辆保险事故定损金额确定：\n" +
                "(1) 定损金额0-2000元，1天；\n" +
                "(2) 定损金额2000（含）-5000元，2天；\n" +
                "(3) 定损金额5000（含）-10000元，3天；\n" +
                "(4) 定损金额10000（含）-30000元，4天；\n" +
                "(5) 定损金额30000（含）元以上，5天。\n" +
                "2.2.\t针对奥迪品牌的享权车辆，单次事故代步车天数上限为10天（含），全年累计理赔服务次数上限为六次（含），配件等待期不计入代步车天数；\n" +
                "代步车天数根据享权车辆机动车辆保险的单次机动车辆保险事故定损金额确定：\n" +
                "(1) 定损金额0-5000元，1天；\n" +
                "(2) 定损金额5000（含）-10000元，2天；\n" +
                "(3) 定损金额10000（含）-30000元，4天；\n" +
                "(4) 定损金额30000（含）-50000元，6天；\n" +
                "(5) 定损金额50000（含）-100000元，8天；\n" +
                "(6) 定损金额100000（含）元以上，10天。 \n" +
                "2.3.\t如您超出规定的代步车天数不退还代步车，应向经销商支付超出天数相对应的代步车使用费。\n</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>3.\t服务期限</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;本服务凭证生效后至多12个月，以第I.部分服务凭证信息页所载的服务期限为准。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>4.\t享权车辆要求</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.1.\t享权车辆应符合下列条件：\n" +
                "4.1.1.\t在购买本服务产品时车龄不超过1个月的的九座以下（含九座）上汽大众、一汽大众、捷达、斯柯达或奥迪品牌车辆；\n" +
                "4.1.2.\t必须按照厂家车辆使用手册、厂家说明书等相关规定使用；\n" +
                "4.1.3.\t使用性质为非营运、非公共服务用途车辆、非比赛竞赛用车；及\n" +
                "4.1.4.\t为非试乘试驾车、非租赁公司车辆。\n" +
                "4.2.\t在购买本服务产品时，享权车辆已经全额投保了下列“相关车险”：\n" +
                "4.2.1.\t“相关车险”必须包括机动车损失保险；及\n" +
                "4.2.2.\t“相关车险”也可以涵盖一个或多个附加险。\n" +
                "4.3.\t享权车辆在服务期限内遭受意外事故时，所投保的上述“相关车险”依然有效且构成上述“相关车险”责任范围内的保险责任。\n</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>5.\t服务保障除外情形</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;<b>5.1.\t由于下列原因造成享权车辆损失的，经销商不提供代步车：\n" +
                "5.1.1.\t未在服务证书规定的服务期限内申请代步保障服务；\n" +
                "5.1.2.\t地震及其次生灾害；\n" +
                "5.1.3.\t战争、敌对行动、军事行为、武装冲突、罢工、骚乱、暴动、恐怖活动；\n" +
                "5.1.4.\t行政行为或司法行为；\n" +
                "5.1.5.\t竞赛、测试，在营业性维修、养护场所养护期间；\n" +
                "5.1.6.\t您或驾驶人饮酒、吸食或注射毒品、被药物麻醉后使用享权车辆；\n" +
                "5.1.7.\t改装、加装零部件；\n" +
                "5.1.8.\t因修理质量不合格或处于返修期间的；\n" +
                "5.1.9.\t您或驾驶人未及时修理、享权车辆进场后未及时开始修复或拖延修理时间的；\n" +
                "5.1.10.\t享权车辆已列入汽车制造商产品召回范围且在行驶过程中因召回缺陷而发生损害；\n" +
                "5.1.11.\t发生保险责任范围外（含享权车辆在保险事故中无责的情形）的原因造成的享权车辆损失。\n" +
                "5.2.\t下列情况下，不论任何原因造成您的车辆损失，经销商不提供代步车：\n" +
                "5.2.1.\t发生全损或推定全损；\n" +
                "5.2.2.\t您、使用人、管理人或驾驶人的故意行为造成；\n" +
                "5.2.3.\t享权车辆被盗窃、抢劫、抢夺，以及因被盗窃、抢劫、抢夺受到损坏的；\n" +
                "5.2.4.\t享权车辆处于查封、扣押期间；</b>\n</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>6.\t服务保障申请材料</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;6.1.\t请您务必保管好下述材料，并在申请代步保障服务时向经销商提交或出示：\n" +
                "6.1.1.\t机动车辆保险保单复印件；\n" +
                "6.1.2.\t机动车辆保险公司出具的定损单、修车发票复印件或机动车辆保险保险公司结案凭证；\n" +
                "6.1.3.\t结算月度租赁费用发票原件一份（如无租赁费发票，则提供服务费发票）；  \n" +
                "6.1.4.\t享受代步保障服务客户所签署的代步车使用确认书（留存单）；\n" +
                "6.1.5.\t车辆损失照片（整车带牌、车架号及损失照片）；\n" +
                "6.1.6.\t车主的有效行驶证；\n" +
                "6.1.7.\t事故证明。 \n</div>");
        allss.append("<br>");
        return  allss.toString();
    }
    private  String keystringMore()
    {
        StringBuilder allss = new StringBuilder();
        String title="V．\t钥匙保障服务条款";
        allss.append("<div align=\"center\" class=\"print-body\" ><b>" + title + "</b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>1.\t服务保障内容</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >享权车辆在服务期限内在中华人民共和国（不含香港、澳门特别行政区和台湾地区）境内发生了车辆钥匙丢失或者被盗，您在服务期限内按照本服务凭证条款向经销商提出重置车辆钥匙的要求，经销商按照本服务凭证条款向您提供免费重置同品牌、同型号和同规格车辆钥匙服务。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>2.\t服务规则</u></b></div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >享权车辆在服务期限内享受一次重置同品牌、同型号和同规格车辆钥匙服务。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>3.\t服务期限</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >本服务凭证生效后至多12个月，以第I.部分服务凭证信息页所载的服务期限为准。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>4.\t享权车辆要求</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.1.\t享权车辆应符合下列条件：\n" +
                "4.1.1.\t在购买本服务产品时车龄不超过1个月内的九座以下（含九座）上汽大众、一汽大众、捷达、斯柯达或奥迪品牌新车（非二手车），且所有权未发生过转让； \n" +
                "4.1.2.\t必须按照厂家车辆使用手册、厂家说明书等相关规定使用； \n" +
                "4.1.3.\t使用性质为非营运、非公共服务用途车辆、非比赛竞赛用车； 及\n" +
                "4.1.4.\t为非试乘试驾车、非租赁公司车辆。\n</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>5.\t服务保障除外情形</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;<b>5.1.\t本服务凭证生效后14天内（含第14天）发生车辆钥匙丢失或者被盗； \n" +
                "5.2.\t未在服务凭证规定的服务期限内申请钥匙保障服务；\n" +
                "5.3.\t您要求重置钥匙的机动车的身份认证信息、车辆信息、使用性质与本服务凭证中记载的享权车辆不一致的； \n" +
                "5.4.\t重置不同品牌、型号或规格的车辆钥匙； \n" +
                "5.5.\t您的故意行为、重大过失、欺诈、不诚实、违法犯罪行为所导致的车辆钥匙丢失或被盗；\n" +
                "5.6.\t您使用、维护、保管不当所导致的车辆钥匙丢失或被盗；\n" +
                "5.7.\t车辆钥匙内在或潜在缺陷、自然磨损、自然损耗所导致的车辆钥匙丢失或被盗； \n" +
                "5.8.\t战争、敌对行为、军事行动、武装冲突、恐怖主义活动、罢工、暴动、骚乱；核爆炸、核裂变、核聚变所导致的车辆钥匙丢失或被盗；\n" +
                "5.9.\t放射性污染及其他各种环境污染所导致的车辆钥匙丢失或被盗； \n" +
                "5.10.\t行政行为、司法行为所导致的车辆钥匙丢失或被盗；\n" +
                "5.11.\t丢失或被盗发生在中华人民共和国（不含香港、澳门特别行政区和台湾地区）境外。</b>\n</div>");


        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>6.\t服务保障申请材料</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;请您务必保管好本服务凭证，并在申请钥匙保障服务时向经销商提交或出示。\n" +
                "6.1.\t请您务必保管好下述材料，并在申请代步保障服务时向经销商提交或出示：\n" +
                "6.1.1.\t本服务凭证原件；\n" +
                "6.1.2.\t已发生符合服务凭证约定损失的证明；\n" +
                "6.1.3.\t车主的有效身份证原件照片；\n" +
                "6.1.4.\t车主的有效行驶证原件照片。\n</div>");
        allss.append("<br>");
        return  allss.toString();
    }
    private  String keystring()
    {
        StringBuilder allss = new StringBuilder();
        String title="IV．\t钥匙保障服务条款";
        allss.append("<div align=\"center\" class=\"print-body\" ><b>" + title + "</b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>1.\t服务保障内容</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.1.\t享权车辆在服务期限内在中华人民共和国（不含香港、澳门特别行政区和台湾地区）境内发生了车辆钥匙丢失或者被盗，您在服务期限内按照本服务凭证条款向授权经销商提出重置车辆钥匙的要求，授权经销商按照本服务凭证条款向您提供免费重置同品牌、同型号和同规格车辆钥匙服务。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>2.\t服务规则</u></b></div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.\t享权车辆在服务期限内享受一次重置同品牌、同型号和同规格车辆钥匙服务。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;<b>2.2.\t您需要自行承担车辆钥匙零售价（包括相关税费）加人工费用的10%（“客户自付费用”）。</b></div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>3.\t服务期限</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;本服务凭证生效后至多36个月，以第I．部分服务凭证信息页所载的服务期限为准。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>4.\t享权车辆要求</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.1.\t享权车辆应符合下列条件：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.1.\t在购买本服务产品时车龄不超过1个月内的九座以下（含九座）保时捷品牌新车（非二手车），且所有权未发生过转让；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.2.\t必须按照厂家车辆使用手册、厂家说明书等相关规定使用；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.3.\t使用性质为非营运、非公共服务用途车辆、非比赛竞赛用车；及</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.4.\t为非试乘试驾车、非租赁公司车辆。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>5.\t服务保障除外情形</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;<b>5.1.\t本服务凭证生效后15天内发生车辆钥匙丢失或者被盗；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.2.\t您要求重置钥匙的机动车的身份认证信息、使用性质与本服务凭证中记载的享权车辆不一致的； </div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.3.\t重置不同品牌、型号或规格的车辆钥匙； </div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.4.\t您的故意行为、重大过失、欺诈、不诚实、违法犯罪行为所导致的车辆钥匙丢失或被盗；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.5.\t您使用、维护、保管不当所导致的车辆钥匙丢失或被盗；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.6.\t车辆钥匙内在或潜在缺陷、自然磨损、自然损耗所导致的车辆钥匙丢失或被盗；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.7.\t战争、敌对行为、军事行动、武装冲突、恐怖主义活动、罢工、暴动、骚乱；核爆炸、核裂变、核聚变所导致的车辆钥匙丢失或被盗；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.8.\t放射性污染及其他各种环境污染所导致的车辆钥匙丢失或被盗；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.9.\t行政行为、司法行为所导致的车辆钥匙丢失或被盗。</b></div>");


        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>6.\t服务保障申请材料</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;6.1.\t请您务必保管好本服务凭证，并在申请钥匙保障服务时与下列申请申请材料一并向授权经销商提交或出示，包括但不限于：\n" +
                "6.1.1.\t车主签署的钥匙丢失申明； \n" +
                "6.1.2.\t维修工单；\n" +
                "6.1.3.\t维修发票。\n</div>");
        allss.append("<br>");
        return  allss.toString();
    }
    private  String accidentstring(String dealname)
    {
        StringBuilder allss = new StringBuilder();
        String title="不便津贴";
        allss.append("<div align=\"center\" class=\"print-title\" ><b>" + title + "</b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;感谢您购买保时捷不便津贴服务，请您仔细阅读本服务凭证，本服务凭证将向您介绍更为具体的服务信息以及将来在您的车辆申请相关服务时如何处理。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>驾乘人员人身意外伤害保险及附加车主不便津贴服务范围 </u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b>1. 驾乘人员人身意外伤害保险及附加车主不便津贴概要 </b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >1) 由安世联合（北京）商务有限公司代为向中国太平洋财产保险股份有限公司进行投保驾乘人员人身意外伤害保险及附加车主不便津贴附加险；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >2) 保单将在本服务凭证签署后次日生效，并由特许经销商交付给您，或以短信或邮件方式通知。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b>2. 驾乘人员人身意外伤害保险及附加车主不便津贴服务除外情形 </b></div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >1) 投保人对被保险人的故意杀害或伤害；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >2) 被保险人自致伤害或自杀；因被保险人挑衅或故意行为而导致的打斗、被袭击或被谋杀；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >3) 被保险人违法、犯罪或者抗拒依法采取的刑事强制措施；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >4) 被保险人因疾病导致的伤害，包括但不限于猝死、食物中毒、高原反应、中暑、病毒和细菌感染（意外伤害导致的伤口感染不在此限）；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >5) 被保险人因妊娠、流产、分娩导致的伤害，但意外伤害所致的流产或分娩不在此限；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >6) 被保险人因药物过敏、整容手术、内外科手术或其他医疗行为导致的伤害；被保险人未遵医嘱私自服用、涂用、注射药物； </div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >7) 被保险人因意外伤害、自然灾害事故以外的原因失踪而被法院宣告死亡的；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >8) 被保险人不遵守有关安全驾驶或乘坐的规定； </div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >9) 被保险人驾驶超载机动车辆，因车辆超载引起的意外事故而遭受的伤害；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >10) 被保险人从事高风险运动、参加任何职业或半职业体育运动期间，包括但不限于各种车辆表演、车辆竞赛或训练等；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >11) 任何生物、化学、原子能武器，原子能或核能装置所造成的爆炸、灼伤、污染或辐射；恐怖袭击，战争、军事行动、暴动或武装叛乱期间； </div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >12) 被保险人精神失常或精神错乱期间；被保险人醉酒或受毒品、管制药物的影响期间；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >13) 被保险人酒后驾车、无有效驾驶证驾驶或驾驶无有效行驶证的车辆期间。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b>3. 驾乘人员意外伤害保险及附加车主不便津贴服务补偿标准及限额</b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >1) 在服务有效期内，客户最多享受一次驾乘人员人身意外伤害保险及附加车主不便津贴服务。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >2) 在服务有效期内，保险人按照与投保人的约定对被保险人驾驶非营运性质的机动车，在行驶过程中或为维护车辆继续运行（包括加油、加水、故障修理、换胎）的临时停放过程中遭受意外伤害承担保险责任并按保险条款给付保险金。保险限额为人民币100,000.00元。保险合同所称意外伤害，指以外来的、突发的、非本意的和非疾病的客观事件为直接且单独的原因致使身体受到的伤害。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >3）不便津贴服务补偿上限分为下面几项，您所选择的为保时捷品牌保障服务凭证中第五条对应的保障内容\n" +
                "i. 在服务有效期内，被保险人或其允许的驾驶员在驾驶保险单指定的机动车辆过程中，因发生交通事故导致该机动车辆需要维修且单次维修金额达到保险单约定金额人民币200,000.00（含）元的，对于车辆维修期间被保险人遭受的交通不便，保险人按照保险单的约定给付车主交通不便津贴人民币15000.00元\n" +
                "ii. 在服务有效期内，被保险人或其允许的驾驶员在驾驶保险单指定的机动车辆过程中，因发生交通事故导致该机动车辆需要维修且单次维修金额达到保险单约定金额人民币25,000.00（含）元的，对于车辆维修期间被保险人遭受的交通不便，保险人按照保险单的约定给付车主交通不便津贴人民币3000.00元\n" +
                "4) 只有在签署本服务凭证的保时捷特许经销商处进行车辆定损和维修的，才可享受车主不便津贴的服务权益。 \n" +
                "5) 详细保单内容请见保单措辞。\n</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>注意事项：</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >您申请补偿或保险理赔时，应及时按照特许经销商的要求或保单约定向特许经销商或中国太平洋财产保险股份有限公司提供与申请补偿或保险理赔相关的各种证明和资料，包括但不限于索赔申请书，保单、本服务凭证、身份证明、投保车辆事故车险定损单、修理费凭证、修理清单、交通事故责任认定书等，并确保其真实、准确、完整。中国太平洋财产保险股份有限公司24小时保险服务专线：95500。 \n" +
                "因您未履行前款约定的义务，导致部分或全部服务凭证责任无法确定，特许经销商和保险公司对无法确定的部分不承担责任。\n</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>特许经销商的权利</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >如果您以任何方式提出虚假的或不真实的钥匙重置服务或驾乘人员人身意外伤害保险及附加车主不便津贴服务申请，我们将会追缴已经发生或支付过的相关款项。同时，包括附赠的内容在内，本合同相关服务将立即终止，您将失去享有本服务凭证的所有权利，情节严重的，将根据中华人民共和国相关法律规定予以追究法律责任。 \n" +
                "取消服务 \n" +
                "1. 如您要求取消服务，安世联合（北京）商务有限公司将代为向中国太平洋财产保险股份有限公司申请退保，终止本服务凭证中的保障责任；由于安世联合（北京）商务有限公司为投保人，相关剩余保险服务费（如有）将退还给安世联合（北京）商务有限公司。 \n" +
                "2. 已驾乘人员人身意外伤害保险及附加车主不便津贴服务的车辆，保险服务费不予退还； \n" +
                "3. 服务有效期内，车辆转让给他人的（即车主发生变更），本服务自车辆所有权转让之日（过户之日）起自动终止。 \n</div>");
        allss.append("<br>");
        allss.append("<table class=\"table-nobody\" border=\"0\"  width=\"100%\" >");
        allss.append("<tr ><td align=\"left\">经销商名称</td><td align=\"right\">客户签名</td></tr>");
        allss.append("<tr><td>"+dealname+"</td><td></td></tr>");
        allss.append("<tr align=\"left\"><td>日期：</td><td align=\"right\">"+foramtss(10,"日期：")+"</td></tr>");
        allss.append("</table>");

        allss.append("<br>");
        return  allss.toString();
    }

   class  product
   {
       public  String pname="";
       public  int pid=0;
       public  String stime="";
       public  String etime="";
   }
    private Map<String, Object>getproductmap()
    {
        List<product>plist=new ArrayList<>();
        String sqlstring="select * from insuracedetail ";
        Map<String, Object> map;
        Map<String,Object>resultmap=new HashMap<>();

        List<Map<String, Object>> list = queryForList(jdbcTemplate, sqlstring);

        for (int i = 0; i < list.size(); i++) {
            product p=new product();
            map = list.get(i);
            resultmap.put(map.get("tid")+"",map);

        }

       return  resultmap;
}
    private Map<String, Object>getproductmapProductid()
    {

        String sqlstring="select * from insuranceproduct ";
        Map<String, Object> map;
        Map<String,Object>resultmap=new HashMap<>();

        List<Map<String, Object>> list = queryForList(jdbcTemplate, sqlstring);

        for (int i = 0; i < list.size(); i++) {

            map = list.get(i);
            resultmap.put(map.get("productid")+"",map);

        }

        return  resultmap;
    }
   private List<product> getproduct(String detail,String Invoicedate,Map<String,Object>resultmap)
   {
       List<product>plist=new ArrayList<>();
       if (detail.equals(""))return plist;
       Map<String, Object> map;
          String[]ss=detail.split(",");
           for (int i = 0; i < ss.length; i++) {
               product p=new product();
               map = (Map<String,Object>)resultmap.get(ss[i]);
               if (map!=null)
               {
                   p.pname = map.get("pname") + "";
                   p.stime=Invoicedate;
                   p.etime=addYear(Invoicedate, setting.StrToInt(map.get("year")+""));

                   plist.add(p);
               }


           }

       return  plist;
   }
   public String check_accident(String contractno,String vin,String carstarttime)
   {
       String resultss="",submittime="";
       String sql="select contractno,(select FROM_UNIXTIME(ttime,'%Y-%m-%d') from  contractcheck where contractno=c.contractno and  checkcontent='合同提交' order by ttime desc limit 0,1 ) as submittime from contract c left outer join insuranceproduct i on i.productid=c.productid where vin=? and i.accident>0 ";
       sql+=" and c.status!='草稿' and c.status!='已取消' and contractno!=? ";
       Object[] args=new Object[2];
       args[0]=vin;
       args[1]=contractno;
       Map<String,Object>map;
       List<Map<String,Object>>list=queryForList(jdbcTemplate,sql,args);
       if (list==null)return "";
       if (list.size()==0)return "";
       String[] ss;
       if (carstarttime.equals(""))carstarttime=GetNowDate("yyyy-MM-dd");
       String sdate=carstarttime;


       int allcount=5,count=0;

       for (int i=0;i<list.size();i++)
       {
           map=list.get(i);
           allcount=5;
           count=0;
           String con=map.get("contractno")+"";
           if (con.equals(""))continue;
           ss=get_startend_time(con);
           if (ss==null)continue;
           //判断时间是否重叠
           Date e1=parseDate(ss[1],"yyyy-MM-dd");
           Date s0=parseDate(sdate,"yyyy-MM-dd");
           if (e1.before(s0))continue;
           submittime= NUllToSpace(map.get("submittime"));
           if (submittime.equals(""))return "";
           Date date=parseDate("2024-06-18 00:00:00","yyyy-MM-dd HH:mm:ss");
           Date submit_date=parseDate(submittime+" 12:00:00","yyyy-MM-dd HH:mm:ss");

           if (submit_date.after(date))
           {
               //2024 年6月18日起理赔次数为2
               allcount=2;
           }
           //判断理赔次数
           sql="select count(*) as sl from claim where contractno=? and (claimtype='PS-代步车费用补偿' or claimtype='PS-出险保障') and (status='理赔已结案' or status='理赔已批准')";

           map=queryForMap(jdbcTemplate,sql,con);
           if (map!=null)count=strToShortInt(map.get("sl")+"");
           if (count<allcount)return  "所输入车架号以及生效日期与系统中已有凭证冲突，现系统中出险保障产品的终止日期为"+ss[1];
       }
       return resultss;
   }

   public String[] get_startend_time(String contractno)
   {
       String[] ss=new String[2];
       String sql ="";
       sql="select c.carstarttime ,(select FROM_UNIXTIME(ttime,'%Y-%m-%d') from  contractcheck where contractno=c.contractno and  checkcontent='合同提交' order by ttime desc limit 0,1 ) as submittime " +
               ",i.cars as i_cars,i.detail,i.accident from contract c left outer join insuranceproduct i on i.productid=c.productid where 1=1 ";
       List<Object> queryList=new ArrayList<Object>();
       if (!contractno.equals("")) {
           sql += " and c.contractno=?";
           queryList.add(contractno);

       }
       Map<String,Object>map=queryForMap(jdbcTemplate,sql,queryList.toArray());
       if (map==null)return null;
       String sdate = map.get("submittime") + "";
       int year=strToShortInt(map.get("accident") + "");
       year=1;
       Date date=parseDate("2023-07-01 00:00:00","yyyy-MM-dd HH:mm:ss");
       Date submit_date=parseDate(map.get("submittime")+" 12:00:00","yyyy-MM-dd HH:mm:ss");

       if (submit_date.after(date))
       {
           //7月1日起生效日期是提交日期加1天
           sdate=addDay(map.get("submittime") + "",1);
       }
       if (!setting.NUllToSpace(map.get("carstarttime")).equals(""))
       {
           if(  parseDate(setting.NUllToSpace(map.get("carstarttime")),"yyyy-MM-dd").after(parseDate(sdate,"yyyy-MM-dd")))
               sdate = map.get("carstarttime") + "";
       }
       String edate=addYear(sdate,year);

       ss[0]=sdate;
       ss[1]=edate;
       return  ss;
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
        String sql ="select (@i:=@i+1)   as   i,'' as contractenddate,'' as p1,'' as p2,'' as p3,'' as p4,'' as s1,'' as s2,'' as s3,'' as s4,'' as e1,'' as e2,'' as e3,'' as e4,usermember.username ,contract.*,contract.Invoicedate as contractstrartdate, if(iscompany=1,'是','否') as iscompanys,if(isloan=1,'是','否') as isloans,FROM_UNIXTIME(contract.ttime,'%Y-%m-%d %H:%i:%s') as createtime,dealer.dealername,dealer.bank,dealer.bankacount,(select remark from  contractcheck where contractno=contract.contractno and  checkcontent='合同审核' and  newstatus='已拒绝'  order by ttime desc  limit 0,1 ) as refuseremark ,(select FROM_UNIXTIME(ttime,'%Y-%m-%d %H:%i:%s') from  contractcheck where contractno=contract.contractno and  checkcontent='合同取消' order by ttime desc limit 0,1 ) as canceltime,(select remark from  contractcheck where contractno=contract.contractno and  checkcontent='合同取消' order by ttime desc limit 0,1 ) as cancelremark ,(select remark from  contractremark where contractno=contract.contractno  order by ttime desc limit 0,1 ) as contractremark,vehicletype.brand as vbrand,vehicletype.code,i.groupname,i.disc,(select tradechanel from billbmw where contractno=contract.contractno and in_or_out=-1 ORDER BY ttime desc LIMIT 0,1 ) as tradechanel,dealer.zone,dealer.province,(select activityname from marketactivity where tid=contract.mactiveid LIMIT 0,1 ) as activityname ";
        if (domainenter.equals("0"))
            sql+=",(select FROM_UNIXTIME(paytime,'%Y-%m-%d')  from bill INNER JOIN billdetail on billdetail.billno=bill.billno where billdetail.contractno=contract.contractno  order by paytime desc  limit 0,1) as paytime";
            else sql+=",(select FROM_UNIXTIME(billbmw.ttime,'%Y-%m-%d %H:%i:%s') from billbmw where contractno=contract.contractno and in_or_out=-1 and (tradetype='订单支付消费' or tradetype='支付') ORDER BY ttime desc LIMIT 0,1 ) as paytime";
        if (domainenter.equals("1"))//宝马添加轮胎、钥匙参数
        {
         sql+=" ,ex.key_invoicedate,ex.key_invoiceno,ex.key_price,ex.tier_invoicedate,ex.tire_invoiceno,ex.tier_price,i.detail_productid ";
         //宝马售后胎导出添加4个轮胎产品
         sql+=",'' as t_p1,'' as t_s1,'' as t_e1,'' as t_brand1,'' as t_type1,'' as t_dot1,'' as t_p2,'' as t_s2,'' as t_e2,'' as t_brand2,'' as t_type2,'' as t_dot2,'' as t_p3,'' as t_s3,'' as t_e3,'' as t_brand3,'' as t_type3,'' as t_dot3,'' as t_p4,'' as t_s4,'' as t_e4,'' as t_brand4,'' as t_type4,'' as t_dot4";
         sql+=",(select type from active_main where activity_id=contract.activity_id limit 1) as active_type ";
        }

        sql+=" ,'' as ifdelay,(select FROM_UNIXTIME(ttime,'%Y-%m-%d') from  contractcheck where contractno=contract.contractno and  checkcontent='合同提交' order by ttime desc limit 0,1 ) as submittime ";
        sql+=",'' as tirestrartdate,'' as tireenddate,'' as neworusedcar,'' as keystrartdate,'' as keyenddate,i.rti,i.tire,i.carkey,i.detail,i.cars as i_cars from contract  inner join  dealer on contract.dealerno=dealer.dealerno left outer join vehicletype on vehicletype.vehicletype=contract.vehicletype left outer join insuranceproduct i on i.productid=contract.productid left outer join usermember on contract.userid=usermember.id";


        if (domainenter.equals("1"))//宝马添加轮胎、钥匙参数
            sql+=" left join contract_extension ex on ex.contractno=contract.contractno";
        sql+=" ,(SELECT @i:=0) as i where contract.valid=1 " ;
        sql += " and  contract.status!='草稿' ";
        List<Object> queryList=new ArrayList<Object>();
        if (!dealerno.equals("")&&!dealerno.equals("zb")) {
            sql += " and contract.dealerno=?";
            queryList.add(dealerno);
        }
      /*  if (!icode.equals("")) {
            sql += " and contract.icode=?";
            queryList.add(icode);
        }*/
        if (!vehicletype.equals("")) {
            sql += " and contract.vehicletype=?";
            queryList.add(vehicletype);
        }
        if (!cars.equals("")) {
            sql += " and contract.cars=?";
            queryList.add(cars);
        }
        if (!brand.equals("")) {
            sql += " and dealer.brand=?";
            queryList.add(brand);
        }
        if (!zone.equals("")) {
            sql += " and dealer.zone=?";
            queryList.add(zone);
        }
        if (!vin.equals("")) {
            sql += " and contract.vin like  ?";
            queryList.add("%"+vin+"%");
        }
        if (!claimstatus.equals("")) {
            sql += " and contract.claimstatus=?";
            queryList.add(claimstatus);
        }
        if (!cname.equals("")) {
            sql += " and contract.cname like  ?";
            queryList.add("%"+cname+"%");
        }
        if (!productname.equals("")) {
            sql += " and contract.pname like  ?";
            queryList.add("%"+productname+"%");
        }
        if (!status.equals("")) {
            if (status.equals("已审批"))
            {
                sql += " and (contract.status='已批准' or contract.status='已拒绝')";
            }else
            {
                sql += " and contract.status=?";
                queryList.add(status);
            }

        }

        if (!productid.equals("")) {
            sql += " and contract.productid like ?";
            queryList.add("%"+productid+"%");
        }
        if (!contractno.equals("")) {
            sql += " and contract.contractno=?";
            queryList.add(contractno);
        }
        if (!dealername.equals("")) {
            sql += " and dealer.dealername like ?";
            queryList.add("%" + dealername + "%");
        }
        if (!dealernamefull.equals("")) {
            sql += " and dealer.dealername = ?";
            queryList.add(dealernamefull );
        }
        if (time1!=0) {
            sql += " and contract.ttime>?";
            queryList.add(time1);
        }
        if (time2!=0) {
            sql += " and contract.ttime<?";
            queryList.add(time2);
        }
        if (paytime1!=0||paytime2!=0)
        {
            if (paytime1!=0&&paytime2!=0)
            {
                sql+=" and contract.contractno in (select contractno from billbmw where ttime>? and ttime<? and contractno is not null)";
                queryList.add(paytime1);
                queryList.add(paytime2);
            }else if (paytime1!=0) {
                sql += " and contract.contractno in (select contractno from billbmw where ttime>? and contractno is not null)";
                queryList.add(paytime1);
            }else if (paytime2!=0) {
                sql += " and contract.contractno in (select contractno from billbmw where ttime<? and contractno is not null)";
                queryList.add(paytime2);
            }

        }

        if (submittime1!=0||submittime2!=0)
        {
             if  (submittime1!=0&&submittime2!=0)
             {
                    sql+=" and contract.contractno in (select contractno from contractcheck where ttime>? and ttime<? and  checkcontent='合同提交' )";
                    queryList.add(submittime1);
                    queryList.add(submittime2);
             }
            else if (submittime1!=0)
            {
                sql+=" and contract.contractno in (select contractno from contractcheck where ttime>? and  checkcontent='合同提交' )";
                queryList.add(submittime1);
            }
            else if (submittime2!=0)
            {
                sql+=" and contract.contractno in (select contractno from contractcheck where ttime<? and  checkcontent='合同提交' )";
                queryList.add(submittime2);
            }

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
        if (domainenter.equals("1"))
        {
            title.put("keytype","KEY Type 钥匙类型");
        }
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
        if (domainenter.equals("2"))
        {
            title.put("contractstrartdate","Contract start date 凭证开始日期");
            title.put("contractenddate","Contract end date 凭证结束日期");
           // title.put("groupname","Service Type 产品分类");


        }else if (domainenter.equals("1"))
    {
        title.put("groupname","Service Type 产品分类");
        title.put("contractstrartdate","RTI start date RTI开始日期");
        title.put("contractenddate","RTI end date RTI结束日期");
        title.put("keystrartdate","KEY start date 钥匙开始日期");
        title.put("keyenddate","KEY end date 钥匙结束日期");
        title.put("tirestrartdate","Tire start date 轮胎开始日期");
        title.put("tireenddate","Tire end date 轮胎结束日期");
        title.put("active_type","活动类型");
        title.put("active_subject","活动主体");

       // tirestrartdate,'' as tireenddate

    }else
        {
            title.put("submittime","Submit date 提交日期");
            if(!act.equals("保险公司"))
            {
                title.put("ifdelay","提交是否超过30天");
            }

        }
        title.put("paytime","Payment date 支付日期");
        title.put("zone","Zone 区域");
        title.put("province","Province 省份");
        title.put("activityname","Market activity 市场活动");
        title.put("canceltime","Cancel Time 取消时间");
        productmap=getproductmap();
        if (domainenter.equals("0")||domainenter.equals("3"))
        {

            if (icode.equals("CPIC")||!act.equals("保险公司"))
        {
            title.put("p1","product name 产品1");
            title.put("s1","Contract start date 凭证开始日期");
            title.put("e1","Contract end date 凭证结束日期");
            title.put("p2","product name 产品2");
            title.put("s2","Contract start date 凭证开始日期");
            title.put("e2","Contract end date 凭证结束日期");
        }

            if (icode.equals("PICC")||!act.equals("保险公司")||domainenter.equals("3"))//多品牌3个都显示
            {
                title.put("p3","product name 产品3");
                title.put("s3","Contract start date 凭证开始日期");
                title.put("e3","Contract end date 凭证结束日期");
            }
            if(!act.equals("保险公司")&&!act.equals("经销商"))
            {
              title.put("cost","Manufacturer of subsidies 厂家补贴");
            }

            title.put("remainingamount","扣款金额");

        }else  if  (domainenter.equals("1"))//宝马
        {
            title.put("mileage","当前行驶里程");
            title.put("sale_person","销售人员姓名");
            title.put("sale_person_tel","销售人员手机");
//            title.put("p1","product name 产品1");
//            title.put("s1","Contract start date 凭证开始日期");
//            title.put("e1","Contract end date 凭证结束日期");
//            title.put("p2","product name 产品2");
//            title.put("s2","Contract start date 凭证开始日期");
//            title.put("e2","Contract end date 凭证结束日期");
//            title.put("p3","product name 产品3");
//            title.put("s3","Contract start date 凭证开始日期");
//            title.put("e3","Contract end date 凭证结束日期");
//            title.put("p4","product name 产品4");
//            title.put("s4","Contract start date 凭证开始日期");
//            title.put("e4","Contract end date 凭证结束日期");
            title.put("t_p1","左前轮胎产品");
            title.put("t_brand1","左前轮胎品牌");
            title.put("t_type1","左前轮胎型号");
            title.put("t_dot1","左前DOT码");
            title.put("t_s1","左前轮胎开始日期");
            title.put("t_e1","左前轮胎结束日期");
            title.put("t_p2","右前轮胎产品");
            title.put("t_brand2","右前轮胎品牌");
            title.put("t_type2","右前轮胎型号");
            title.put("t_dot2","右前DOT码");
            title.put("t_s2","右前轮胎开始日期");
            title.put("t_e2","右前轮胎结束日期");
            title.put("t_p3","左后轮胎产品");
            title.put("t_brand3","左后轮胎品牌");
            title.put("t_type3","左后轮胎型号");
            title.put("t_dot3","左后DOT码");
            title.put("t_s3","左后轮胎开始日期");
            title.put("t_e3","左后轮胎结束日期");
            title.put("t_p4","右后轮胎产品");
            title.put("t_brand4","右后轮胎品牌");
            title.put("t_type4","右后轮胎型号");
            title.put("t_dot4","右后DOT码");
            title.put("t_s4","右后轮胎开始日期");
            title.put("t_e4","右后轮胎结束日期");
            title.put("key_invoiceno","钥匙发票号码");
            title.put("key_invoicedate","钥匙发票日期");
            title.put("key_price","钥匙发票金额");
            title.put("tire_invoiceno","轮胎发票号码");
            title.put("tier_invoicedate","轮胎发票日期");
            title.put("tier_price","轮胎发票金额");
            //ex.key_invoicedate,ex.key_invoiceno,ex.key_price,
            //tier_invoicedate,ex.tire_invoiceno,ex.tier_price
            //宝马加入实收金额
            title.put("paid_amount_rti","悦然焕新实收金额");
            title.put("paid_amount_key","钥匙实收金额");
            title.put("paid_amount_tire","轮胎实收金额");
            title.put("paid_amount_group","常用套餐实收金额");
            title.put("paid_amount","合计实收金额");

        }
        List<Map<String,Object>>list=queryForList(jdbcTemplate,sql,queryList.toArray());
   String pname="";

          for (int i=list.size()-1;i>-1;i--)
          {
              Map<String,Object>map=list.get(i);
             if( (map.get("i_cars")+"").equals("售后新换胎"))
             {
                 map.put("groupname","售后新换胎");
                 map.put("Invoiceno","");
               //  map.put("Invoiceprice","");
                 map.put("Invoicedate","");
                // map.put("isloans","");
                 map.put("Businessinsurancepolicyno","");
                 map.put("insurancecompany","");
                 map.put("begindate","");
                 map.put("enddate","");
                 map.put("insuranceTypes","");
                 map.put("insuranceForm","");
                 map.put("forceno","");
                 map.put("keytype","");
             }
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
              if (domainenter.equals("1")||domainenter.equals("2")||domainenter.equals("3")) {
                  if (strToShortInt(map.get("rti")+"")!=0)
                  {
                      map.put("contractstrartdate",map.get("paytime"));
                      map.put("contractenddate",addYear(setting.NUllToSpace(map.get("Invoicedate")),strToShortInt(map.get("rti")+"")));
                      if (isnewcar==2)//二手车日期是支付日期+期限
                      {
                          map.put("contractenddate",addYear(setting.NUllToSpace(map.get("paytime")),strToShortInt(map.get("rti")+"")));
                      }
                  }else
                  {
                      map.put("contractstrartdate","");
                      map.put("contractenddate","");
                  }

                  if (strToShortInt(map.get("carkey")+"")!=0)
                  {
                      map.put("keystrartdate",map.get("paytime"));
                      if (isnewcar==0)
                      {

                          map.put("keyenddate",addYear(setting.NUllToSpace(map.get("paytime")),strToShortInt(map.get("carkey")+"")));
                      }else    map.put("keyenddate",addYear(setting.NUllToSpace(map.get("Invoicedate")),strToShortInt(map.get("carkey")+"")));

                  }
                  if (strToShortInt(map.get("tire")+"")!=0&&!((map.get("i_cars")+"").equals("售后新换胎")))
                  {
                      map.put("tirestrartdate",map.get("paytime"));
                      map.put("tireenddate",addYear(setting.NUllToSpace(map.get("paytime")),strToShortInt(map.get("tire")+"")));


                  }

          }else
              {
                  if (BetweenDays2(map.get("Invoicedate")+"",map.get("submittime")+"")>30)map.put("ifdelay","是");
                  else map.put("ifdelay","否");
                 // List<product>plist=getproduct(map.get("detail")+"",map.get("Invoicedate")+"",productmap);

              }
              //宝马和保时捷都计算3个产品的日期
              if (domainenter.equals("0")||domainenter.equals("3"))
              {
                  String sdate="";
                  if (domainenter.equals("0")) {
                      //代步保障可以自己设置日期
                      sdate = map.get("submittime") + "";

                      Date date=parseDate("2023-07-01 00:00:00","yyyy-MM-dd HH:mm:ss");
                      Date submit_date=parseDate(map.get("submittime")+" 12:00:00","yyyy-MM-dd HH:mm:ss");

                      if (submit_date.after(date))
                      {
                          //7月1日起生效日期是提交日期加1天
                          sdate=addDay(map.get("submittime") + "",1);
                      }
                      if (!setting.NUllToSpace(map.get("carstarttime")).equals("")&&(pname.contains("代步")||pname.contains("出险保障")))
                      {
                          if(  parseDate(setting.NUllToSpace(map.get("carstarttime")),"yyyy-MM-dd").after(parseDate(sdate,"yyyy-MM-dd")))
                              sdate = map.get("carstarttime") + "";
                      }


                  }else {
                      //宝马的起始日期都是支付日期，新车结束日期是发票日期加上保险期限，旧车的结束日期是支付日期加上保险期限
                      if (isnewcar==0)
                      {
                          sdate = map.get("paytime") + "";

                      }else sdate = map.get("Invoicedate") + "";
                  }
                  List<product>plist=getproduct(map.get("detail")+"",sdate,productmap);
                  if (plist.size()<3&&icode.equals("PICC")&&act.equals("保险公司")&&domainenter.equals("0")&&filetype.toLowerCase().equals("file")) {
                      //保时捷PICC只显示第三个产品
                      list.remove(i);
                      continue;
                  }
                  for (int ii=0;ii<plist.size();ii++)
                  {
                     // if (!domainenter.equals("3"))
                      //if (ii>2)break;
                      product p=plist.get(ii);
                      map.put("p"+(ii+1),p.pname);
                      if (domainenter.equals("0"))
                      map.put("s"+(ii+1),p.stime);
                      else
                      {
                          map.put("s"+(ii+1), map.get("paytime") + "");
                      }
                      map.put("e"+(ii+1),p.etime);

                  }
              }
              else if (domainenter.equals("1"))
              {
                  String detail_productid= NUllToSpace( map.get("detail_productid"));
                  //获取全部产品，理赔时要显示，
                  if (detail_productid.equals(""))detail_productid=NUllToSpace( map.get("productid"));
                  if (!detail_productid.equals(""))
                  {
                      String sdate="";
                      //宝马的起始日期都是支付日期，新车结束日期是发票日期加上保险期限，旧车的结束日期是支付日期加上保险期限
                      sdate = map.get("paytime") + "";
                      String  Invoicedate = map.get("Invoicedate") + "";
                      Map<String,Object>pmap=getproductmapProductid();
                      String []plist=detail_productid.split(",");
                      //售后新换胎略过这部分
                      if (!(map.get("i_cars")+"").equals("售后新换胎"))
                      {
                          detail_productid="";

                          //获取最小层级的产品id
                          for (int ii=0;ii<plist.length;ii++)
                          {

                              Map<String,Object>p=(Map<String,Object>)pmap.get(plist[ii]);
                              if (p!=null)
                              {
                                  if ( !NUllToSpace(p.get("detail_productid")).equals(""))
                                  {
                                      detail_productid+=NUllToSpace(p.get("detail_productid"))+",";
                                  }else detail_productid+=NUllToSpace(p.get("productid"))+",";

                              }

                          }
                          plist=detail_productid.split(",");
                      }

                      for (int ii=0;ii<plist.length;ii++)
                      {
                          if (ii>3)break;
                          Map<String,Object>p=(Map<String,Object>)pmap.get(plist[ii]);
                          if (p!=null)
                          {
                              map.put("p"+(ii+1),p.get("pname"));
                              map.put("s"+(ii+1), sdate);
                              int year=0;
                              year=strToShortInt(p.get("rti")+"");
                              if (year==0) year=strToShortInt(p.get("carkey")+"");
                              if (year==0) year=strToShortInt(p.get("tire")+"");
                              if (isnewcar==1&&((p.get("productid")+"").startsWith("BMW01")||(p.get("productid")+"").startsWith("BMW05")||(p.get("productid")+"").startsWith("BMW06")))
                                  map.put("e"+(ii+1),addYear(Invoicedate,year));
                                  else
                                  map.put("e"+(ii+1),addYear(sdate,year));
                              if ((map.get("i_cars")+"").equals("售后新换胎"))
                              {
                                  map.put("t_p"+(ii+1),p.get("pname"));
                                  map.put("t_s"+(ii+1), sdate);
                                  map.put("t_e"+(ii+1),addYear(sdate,year));
                              }
                          }



                      }

                  }
                  if ((map.get("pname")+"").contains("轮胎"))
                  {
                      sql="select brandname,tiretype,dot,position from tiredetail where contractno=?";
                      List<Map<String,Object>>tirelist=queryForList(jdbcTemplate,sql,map.get("contractno")+"");
                    String position="";
                    int pp=1;
                      for (int ii=0;ii<tirelist.size()&&ii<4;ii++)
                      {
                          pp=1;
                          Map<String,Object>tire=tirelist.get(ii);
                          position=tire.get("position")+"";
                            if (position.contains("左前"))pp=1;
                            else if (position.contains("右前"))pp=2;
                            else if (position.contains("左后"))pp=3;
                            else if (position.contains("右后"))pp=4;
                          map.put("t_brand"+(pp),tire.get("brandname"));
                          map.put("t_type"+(pp),tire.get("tiretype"));
                          map.put("t_dot"+(pp),tire.get("dot"));
                      }
                  }
              }


        }



        title.put("remark","Remark 备注");
        if (domainenter.equals("0")||domainenter.equals("3"))
        {
            title.put("icode","Insurance 渠道");
        }else    if (domainenter.equals("1"))
        {
            title.put("neworusedcar","新车/旧车/二手车");
        }
      /*  if (domainenter.equals("3"))
        {
            title.put("insuranceamount","保费");
            title.put("tpaamount","TPA费用");
            title.put("marketingamount","市场费用");
            title.put("brandamount","品牌费用");
            title.put("taxamount","税费");
        }*/
        //   marketingamount=retailprice-insuranceamount-tpaamount-brandamount-taxamount;
        if (filetype.toLowerCase().equals("file"))
        result= toexcel.createCSVFile(list,title,"","");
        else
        {
            Gson gson = new Gson();
            result = gson.toJson(list);
        }
        return  result;
    }

    public  String addorupdatemarketactivity(String data)
    {
        int loan=0,atype=0,valid=1;
        float amount=0;
        String tid="",  activityname="",  instructions="",  stime="",    etime="",vtype="",userid="",productid="",brand="";

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
        if (jsonObject.keySet().contains("tid"))tid=jsonObject.get("tid").getAsString();
        if (jsonObject.keySet().contains("activityname"))activityname=jsonObject.get("activityname").getAsString();else return  GetErrorString(1,"");
        if (jsonObject.keySet().contains("instructions"))instructions=jsonObject.get("instructions").getAsString();else return  GetErrorString(1,"");
        if (jsonObject.keySet().contains("stime"))stime=jsonObject.get("stime").getAsString();else return  GetErrorString(1,"");
        if (jsonObject.keySet().contains("etime"))etime=jsonObject.get("etime").getAsString();else return  GetErrorString(1,"");
        if (jsonObject.keySet().contains("vtype"))vtype=jsonObject.get("vtype").getAsString();else return  GetErrorString(1,"");
        if (jsonObject.keySet().contains("userid"))userid=jsonObject.get("userid").getAsString();else return  GetErrorString(1,"");
        if (jsonObject.keySet().contains("loan"))loan=jsonObject.get("loan").getAsInt();
        if (jsonObject.keySet().contains("atype"))atype=jsonObject.get("atype").getAsInt();
        if (jsonObject.keySet().contains("valid"))valid=jsonObject.get("valid").getAsInt();
        if (jsonObject.keySet().contains("amount"))amount=jsonObject.get("amount").getAsFloat();
        if (jsonObject.keySet().contains("productid"))productid=jsonObject.get("productid").getAsString();
        if (jsonObject.keySet().contains("brand"))brand=jsonObject.get("brand").getAsString();
        String sqlstring="";
        if (tid.equals(""))
        {
            sqlstring="insert into marketactivity (activityname,instructions,stime,etime,vtype,userid,loan,atype,valid,amount,productid,brand) values (?,?,?,?,?,?,?,?,?,?,?,?)";
        }else
        {
            sqlstring="update marketactivity set activityname=?,instructions=?,stime=?,etime=?,vtype=?,userid=?,loan=?,atype=?,valid=?,amount=?,productid=?,brand=? where tid="+tid;
        }
        Object[] args=new Object[12];
        args[0]=activityname;
        args[1]=instructions;
        args[2]=stime;
        args[3]=etime;
        args[4]=vtype;
        args[5]=userid;
        args[6]=loan;
        args[7]=atype;
        args[8]=valid;
        args[9]=amount;
        args[10]=productid;
        args[11]=brand;
         jdbcTemplate.update(sqlstring,args);
        if (tid.equals(""))
        {
            sqlstring="select max(tid) as tid from marketactivity";
            Map<String,Object>map=queryForMap(jdbcTemplate,sqlstring);
            if (map!=null)
            {
                if (map.containsKey("tid"))tid=map.get("tid")+"";
            }
        }

        result = "{\"errcode\":0,\"tid\":\"" + tid + "\"}";
        return result;
    }
    public  String setmarketactivityvalid(String data)
    {
        int valid=1;

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
        if (jsonObject.keySet().contains("tid"))tid=jsonObject.get("tid").getAsString();else return GetErrorString(1,"");
        if (jsonObject.keySet().contains("valid"))valid=jsonObject.get("valid").getAsInt();else return GetErrorString(1,"");

        String sqlstring="";

            sqlstring="update marketactivity set valid=?  where tid=?";

        Object[] args=new Object[2];
        args[0]=valid;
        args[1]=tid;

        jdbcTemplate.update(sqlstring,args);

        result = "{\"errcode\":0,\"tid\":\"" + tid + "\"}";
        return result;
    }
    public  String getvaildmarketactivity(String data)
    {

        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String tid="";
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();


        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;

        int pageindex=1 ,  pagesize=10;
        String sqlstring="",status="",activityname="",stime1="",stime2="",etime1="",etime2="";
        Object[] args;
        if (jsonObject.keySet().contains("tid"))tid=jsonObject.get("tid").getAsString();else tid="";
        if (jsonObject.keySet().contains("status"))status=jsonObject.get("status").getAsString();else status="";
        if (jsonObject.keySet().contains("activityname"))activityname=jsonObject.get("activityname").getAsString();else activityname="";
        if (jsonObject.keySet().contains("stime1"))stime1=jsonObject.get("stime1").getAsString();else stime1="";
        if (jsonObject.keySet().contains("stime2"))stime2=jsonObject.get("stime2").getAsString();else stime2="";
        if (jsonObject.keySet().contains("etime1"))etime1=jsonObject.get("etime1").getAsString();else etime1="";
        if (jsonObject.keySet().contains("etime2"))etime2=jsonObject.get("etime2").getAsString();else etime2="";
        if (jsonObject.keySet().contains("pageindex"))pageindex=jsonObject.get("pageindex").getAsInt();
        if (jsonObject.keySet().contains("pagesize"))pagesize=jsonObject.get("pagesize").getAsInt();
             sqlstring = "select *,'' as status,DATE_FORMAT(stime,'%Y-%m-%d') as stimes,DATE_FORMAT(etime,'%Y-%m-%d') as etimes,(select username from usermember where id=marketactivity.userid) as username from marketactivity where 1=1";
        List<Object> queryList=new ArrayList<Object>();
        if (!tid.equals("")) {
            sqlstring += " and tid=?";
            queryList.add(tid);
        }
        if (!activityname.equals("")) {
            sqlstring += " and activityname like ?";
            queryList.add("%"+activityname+"%");
        }
        if (!stime1.equals("")) {
            sqlstring += " and stime>=?";
            queryList.add(stime1);
        }
        if (!stime2.equals("")) {
            sqlstring += " and stime<=?";
            queryList.add(stime2);
        }
        if (!etime1.equals("")) {
            sqlstring += " and etime>=?";
            queryList.add(etime1);
        }
        if (!etime2.equals("")) {
            sqlstring += " and etime<=?";
            queryList.add(etime2);
        }
        String today=GetNowDate("yyyy-MM-dd");
        if (status.equals("未开始")) {
            sqlstring += " and stime>'"+today+"' ";

        }
      else  if (status.equals("未上架")) {
            sqlstring += " and stime<='"+today+"'  and valid=0 and etime>='"+today+"' ";

        }
        else  if (status.equals("进行中")) {
             sqlstring += " and stime<='"+today+"'  and valid=1 and etime>='"+today+"' ";
        }  else  if (status.equals("已结束")) {
             sqlstring += " and  etime<'"+today+"' ";
        }
        sqlstring+=" order by tid desc";
        int tt=setting.GetCurrenntTime();
        List<Map<String, Object>> list=queryForList(jdbcTemplate,sqlstring,queryList.toArray());
        for (int i=0;i<list.size();i++)
        {
            Map<String,Object>map=list.get(i);
            if (setting.ConvertDateTimeInt(map.get("stime")+" 00:00:00")>tt)map.put("status","未开始");
            else if (setting.ConvertDateTimeInt(map.get("etime")+" 23:59:59")<tt)map.put("status","已结束");
            else if ((map.get("valid")+"").equals("0"))map.put("status","未上架");
            else map.put("status","进行中");
        }
        result=GetRsultString(list,pageindex,pagesize);



        return result;
    }
}
