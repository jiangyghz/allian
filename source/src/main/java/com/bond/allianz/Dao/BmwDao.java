package com.bond.allianz.Dao;

import com.bond.allianz.entity.UserInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.bond.allianz.Dao.toexcel.*;

@Repository
public class BmwDao extends BaseDao{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Value("${domain.enter}")
    public String domainenter;
    public int addBmwbill(String dealerno,float amount,String userid,String tradetype,String contractno,int in_or_out,String tradechanel,String chanelno,String remark)
    {
        int ttime=setting.GetCurrenntTime();
        float balance=0;
        String billno=getBillno();
        String sqlstring="insert into billbmw (dealerno,amount,userid,tradetype,contractno,in_or_out,tradechanel,chanelno,ttime,remark,billno) values";
        sqlstring+=" (?,?,?,?,?,?,?,?,?,?,?)";
        Object[]args=new Object[11];
        args[0]=dealerno;
        args[1]=amount;
        args[2]=userid;
        args[3]=tradetype;
        args[4]=contractno;
        args[5]=in_or_out;
        args[6]=tradechanel;
        args[7]=chanelno;
        args[8]=ttime;
        args[9]=remark;
        args[10]=billno;
        int ii=0;
        try
        {
            ii=  jdbcTemplate.update(sqlstring,args);
        }
         catch (Exception e)
         {
             String ss=e.toString();
             logs.info(ss,"addBmwbill");
         }

      sqlstring="select sum(amount*in_or_out) as balance from billbmw where dealerno=?";
      Map<String,Object> map=queryForMap(jdbcTemplate,sqlstring,dealerno);
      if (map!=null)
      {
          balance=setting.NullToZero(map.get("balance")+"");
          sqlstring="update billbmw set balance=? where dealerno=? and billno=?";
          args=new Object[3];
          args[0]=balance;
          args[1]=dealerno;
          args[2]=billno;
          jdbcTemplate.update(sqlstring,args);
      }
      return  ii;
    }
    public float getBalance(String dealerno)
    {
     String   sqlstring="select sum(amount*in_or_out) as balance from billbmw where dealerno=?";
        Map<String,Object> map=queryForMap(jdbcTemplate,sqlstring,dealerno);
        float balance=0;
        if (map!=null)
        {
            balance=setting.NullToZero(map.get("balance")+"");

        }
        return balance;
    }
    public  String getsystemremind(String dealerno)
    {
       String  sql="select remind from systemremind  where dealerno=? and remindtime is null";
       String allss="";
       allss=GetRsultString(sql,jdbcTemplate,dealerno);
       sql="update systemremind set remindtime="+getCurrenntTime()+" where dealerno=? and remindtime is null";
        jdbcTemplate.update(sql,dealerno);
        return  allss;

    }

    public String getbalancestring(String data)
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

        if (jsonObject.keySet().contains("dealerno"))dealerno=jsonObject.get("dealerno").getAsString();else return GetErrorString(1,"");
        float balance=getBalance(dealerno);
        return "{\"balance\":\""+balance+"\"}";
    }
    public String balancepay(String data)
    {
        String dealerno="",brand="",contractno="",userid="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String result="";
        float amount=0;
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;

        if (jsonObject.keySet().contains("dealerno"))dealerno=jsonObject.get("dealerno").getAsString();else return GetErrorString(1,"");
        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();
        if (jsonObject.keySet().contains("brand"))brand=jsonObject.get("brand").getAsString();
        if (jsonObject.keySet().contains("userid"))userid=jsonObject.get("userid").getAsString();
        if (jsonObject.keySet().contains("amount"))amount=jsonObject.get("amount").getAsFloat();
 //       if (amount==0)return GetErrorString(3,"支付金额不能为0");
       if (getBalance(dealerno)<amount)return GetErrorString(3,"余额不足！");
        String  sql="select  status from  contract  where contractno=?";
        if (contractno.startsWith("MT")) sql="select  status from  mt_contract  where contractno=?";
        Map<String,Object>map=queryForMap(jdbcTemplate,sql,contractno);
        if (map!=null)
        {

           if(  (map.get("status")+"").equals("已支付"))return GetErrorString(3,"订单已经支付，不能重复支付！");;
        }
        addBmwbill( dealerno, amount, userid, "支付", contractno,-1, "余额支付", "", "");
          sql="update  contract set status='已支付' where contractno=?";
        if (contractno.startsWith("MT"))  sql="update  mt_contract set status='已支付',paytime='"+GetNowDate("yyyy-MM-dd HH:mm:ss")+"' where contractno=?";
        jdbcTemplate.update(sql,contractno);
        int tt=setting.GetCurrenntTime();
        sql = "insert into billdetail (id,contractno,amount,ttime,dealerno) values (uuid(),?,?," + tt + ",?)";
       Object[] queryList=new Object[3];
        queryList[0] = contractno;
        queryList[1] = amount;
        queryList[2] = dealerno;
        jdbcTemplate.update(sql, queryList);
        result=GetErrorString(0,"");
        return  result;
    }

    //订单支付  tradechanel 支付渠道，chanelno支付渠道流水号，contractno保险合同号
    public int contractpay(String dealerno,float amount,String userid,String contractno,String tradechanel,String chanelno,String remark)
    {
        int ii=0;
        addBmwbill( dealerno, amount, userid, "订单支付充值", "",1, tradechanel, chanelno, remark);
        ii=  addBmwbill( dealerno, amount, userid, "订单支付消费", contractno,-1, "", "","");
       String  sql="update  contract set status='已支付' where contractno=?";
        if (contractno.startsWith("MT"))  sql="update  mt_contract set status='已支付',paytime='"+GetNowDate("yyyy-MM-dd HH:mm:ss")+"',tradechanel='"+tradechanel+"' where contractno=?";
       jdbcTemplate.update(sql,contractno);
        int tt=setting.GetCurrenntTime();
    //    sql="select contractno from billdetail where contractno=?";
     //   if (jdbcTemplate.queryForMap(sql,contractno)!=null)  return  ii;
        sql = "insert into billdetail (id,contractno,amount,ttime,dealerno) values (uuid(),?,?," + tt + ",?)";

        Object[]  queryList = new Object[3];
        queryList[0] = contractno;
        queryList[1] = amount;
        queryList[2] = dealerno;
        jdbcTemplate.update(sql, queryList);
       return  ii;
    }
    public String getcantractbillno(String contractno,float backamount)
    {
        String billno="";
        float amount=0;
        if (domainenter.equals("1")||domainenter.equals("2")||domainenter.equals("3"))
        {

          String  sqlstring="select billno,invoicerequestno,amount,dealerno,invoiceno from bill where billno like ?";
            Map<String,Object>  map=queryForMap(jdbcTemplate,sqlstring,"%"+contractno);
            if (map!=null)
            {

                billno=setting.NUllToSpace(map.get("billno"));
                amount=setting.NullToZero(map.get("amount")+"");
                if (backamount<amount)
                {
                    //添加手续费的账单
                }
            }
        }
        return  billno;
    }
    public String updatebillinvoice(String data){

        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="",billno="",invoiceno="",remark="";

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
        String resultss = "";

        String sql = "";
        int tt = setting.GetCurrenntTime();
        int count=0;
        for (int i=0;i<jsonArray.size();i++) {

            jsonObject=jsonArray.get(i).getAsJsonObject();

            if (jsonObject.keySet().contains("billno")) billno = jsonObject.get("billno").getAsString();else billno="";
            if (jsonObject.keySet().contains("invoiceno")) invoiceno = jsonObject.get("invoiceno").getAsString();else invoiceno="";
            if (jsonObject.keySet().contains("remark")) remark = jsonObject.get("remark").getAsString();else remark="";



            Object[] args = new Object[3];
            args[0] = invoiceno;
            args[1] = remark;
            args[2] = billno;


            if (!billno.equals("")) {

                sql = "update bill set invoiceno=?,invoicetime="+tt+",invoiceremark=?,status='已开票',invoicetype='增值税发票' where billno=? and status!='已开票'";
               if ( jdbcTemplate.update(sql, args)!=0)  count++;

                   sql="update contract set status='已开票' where (contractno in (select contractno from billdetail where billno=?) ) and status!='已取消'";
                jdbcTemplate.update(sql, billno);
                if (domainenter.equals("1"))
                {
                    sql="update mt_contract set status='已开票' where (contractno in (select contractno from billdetail where billno=?) ) and status!='已取消'";
                    jdbcTemplate.update(sql, billno);
                }

            }


        }

        resultss="{\"errcode\":\"0\",\"count\":\""+count+"\"}";
        return  resultss;
    }
    public void paybacksuccess(String contractno)
    {
        String billno="",dealerno="",tradetype="",brand="",userid="";
        float amount,agentprice;
        String sqlstring="select backamount,dealerno,agentprice from contract where contractno=?";
        if (contractno.startsWith("MT"))  sqlstring="select backamount,dealerno,agentprice from mt_contract where contractno=?";
        Map<String,Object>map=queryForMap(jdbcTemplate,sqlstring,contractno);
        if (map!=null)
        {
            amount=setting.NullToZero(map.get("backamount")+"");
            dealerno=setting.NUllToSpace(map.get("dealerno"));
            agentprice=setting.NullToZero(map.get("agentprice")+"");
        }else return ;
        addBmwbill( dealerno, amount, userid, "订单支付退款", contractno,1, "合同取消", "", "");
        addBmwbill(dealerno,amount,"","订单支付退款",contractno,-1,"线上支付","","");
        //判断是否宝马的直接开电子发票的账单
         billno=getcantractbillno(contractno,amount);
        sqlstring="update  contract set status=? where contractno=?";
        if (contractno.startsWith("MT"))  sqlstring="update  mt_contract set status=? where contractno=?";
        Object[]  queryList=new Object[2];
        queryList[0]="已取消";
        queryList[1]=contractno;
        jdbcTemplate.update(sqlstring,queryList);
        if (!billno.equals(""))
        {
            new invoice(jdbcTemplate).InvoiceRed(dealerno,billno,userid);
            return ;
        }


        int tt=setting.GetCurrenntTime();
        sqlstring = "insert into billdetail (id,contractno,amount,ttime,dealerno) values (uuid(),?,?," + tt + ",?)";
        queryList=new Object[3];
        queryList[0] = contractno;
        queryList[1] =0- amount;
        queryList[2] = dealerno;
        jdbcTemplate.update(sqlstring, queryList);
    }
    //充值支付  tradechanel 支付渠道，chanelno支付渠道流水号
    public int chargepay(String dealerno,float amount,String userid,String tradechanel,String chanelno,String remark)
    {
        int ii=0;
       ii= addBmwbill( dealerno, amount, userid, "充值", "",1, tradechanel, chanelno, remark);


        return  ii;
    }
    public  String paysuccess(String id,float amount,String tradechanel,String chanelno,String remark)
    {
        String billno="",dealerno="",tradetype="",brand="",contractno="",userid="";
        String sqlstring="select billno,dealerno,tradetype,brand,contractno,userid from payrequest where id=? and ptime is null";
        Map<String,Object>map=queryForMap(jdbcTemplate,sqlstring,id);
        int ptime=0;
        if (map!=null)
        {
            billno=setting.NUllToSpace(map.get("billno"));
            dealerno=setting.NUllToSpace(map.get("dealerno"));
            tradetype=setting.NUllToSpace(map.get("tradetype"));
            brand=setting.NUllToSpace(map.get("brand"));
            contractno=setting.NUllToSpace(map.get("contractno"));
            userid=setting.NUllToSpace(map.get("userid"));
           // ptime=strToShortInt(map.get("ptime")+"");
        }else return "支付单号不存在！";
        // if (ptime>0)return "已经回调成功！";
        String status="已支付";
        if (domainenter.equals("0"))//porsche
        {
            sqlstring="update bill set status='"+status+"',paytype='线上支付' where billno=?";
            jdbcTemplate.update(sqlstring,billno);
            sqlstring="update contract set status='"+status+"' where contractno in (select contractno from billdetail where billno=?)";
            jdbcTemplate.update(sqlstring,billno);
        }else if (domainenter.equals("1")||domainenter.equals("2")||domainenter.equals("3"))//bmw daimler
        {
            //sqlstring="select chanelno from billbmw where chanelno=?";
            //map=queryForMap(jdbcTemplate,sqlstring,chanelno);
            //if (map!=null)return "已经回调成功！";
            if (tradetype.equals("充值"))chargepay(dealerno,amount,userid,tradechanel,chanelno,remark);
            else contractpay(dealerno,amount,userid,contractno,tradechanel,chanelno,remark);
        }
        sqlstring="update payrequest set ptime="+getCurrenntTime()+" where id=?";
        jdbcTemplate.update(sqlstring,billno);
        return "OK";
    }
    private String getBillno()
    {
        String resultss="";
        int t1=GetTodaytime();
        long  ii=0;
        String sqlstring="select max(billno) as billno from billbmw where ttime>"+t1;
        Map<String,Object>map=queryForMap(jdbcTemplate,sqlstring);
        if (map!=null)
        {
            ii=nullToInt(map.get("billno")+"") ;
        }
        if (ii==0)resultss=GetNowDate("yyyyMMdd")+"0001";
        else resultss=(ii+1)+"";
        return resultss;
    }
    public String getBillBmw(String data){
        String billno="",dealerno="",tradetype="",dealername="",contractno="",chanelno="",tradechanel="",act="",brand="";
        int pageindex=1 ,  pagesize=10;
        int time1=0,time2=0;
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


        if (jsonObject.keySet().contains("billno"))billno=jsonObject.get("billno").getAsString();
        if (jsonObject.keySet().contains("dealerno"))dealerno=jsonObject.get("dealerno").getAsString();
        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();
        if (jsonObject.keySet().contains("time1"))time1=jsonObject.get("time1").getAsInt();
        if (jsonObject.keySet().contains("time2"))time2=jsonObject.get("time2").getAsInt();
        if (jsonObject.keySet().contains("pageindex"))pageindex=jsonObject.get("pageindex").getAsInt();
        if (jsonObject.keySet().contains("pagesize"))pagesize=jsonObject.get("pagesize").getAsInt();
        if (jsonObject.keySet().contains("tradetype"))tradetype=jsonObject.get("tradetype").getAsString();
        if (jsonObject.keySet().contains("dealername"))dealername=jsonObject.get("dealername").getAsString();
        if (jsonObject.keySet().contains("chanelno"))chanelno=jsonObject.get("chanelno").getAsString();
        if (jsonObject.keySet().contains("tradechanel"))tradechanel=jsonObject.get("tradechanel").getAsString();
        if (jsonObject.keySet().contains("act"))act=jsonObject.get("act").getAsString();
        if (jsonObject.keySet().contains("brand"))brand=jsonObject.get("brand").getAsString();
        String sql ="";


        sql="select dealer.brand,usermember.username as userid,billbmw.*,FROM_UNIXTIME(billbmw.ttime,'%Y-%m-%d %H:%i:%s') as createtime,dealer.dealername,dealer.is_elec_invoice,payorder.payorderno from billbmw inner join dealer on dealer.dealerno=billbmw.dealerno left outer join usermember on billbmw.userid=usermember.id left outer join payorder on payorder.traceno=billbmw.chanelno where 1=1 ";
       if (!act.equals("Admin")) sql+=" and billbmw.tradetype not like '订单支付%' ";
        List<Object> queryList=new ArrayList<Object>();
        if (!billno.equals(""))
        {
            sql+=" and billbmw.billno=?";
            queryList.add(billno);
        }
        if (!brand.equals(""))
        {
            sql+=" and dealer.brand=?";
            queryList.add(brand);
        }
        if (!tradetype.equals(""))
        {
            sql+=" and billbmw.tradetype=?";
            queryList.add(tradetype);
        }
        if (!dealername.equals(""))
        {
            sql+=" and dealer.dealername like ?";
            queryList.add("%"+dealername+"%");
        }
        if (!dealerno.equals("")&&!dealerno.equals("zb"))
        {
            sql+=" and billbmw.dealerno=?";
            queryList.add(dealerno);
        }
        if (time1!=0)
        {
            sql+=" and billbmw.ttime>?";
            queryList.add(time1);
        }
        if (time2!=0)
        {
            sql+=" and billbmw.ttime<?";
            queryList.add(time2);
        }
        if (!contractno.equals(""))
        {
            sql+=" and billbmw.contractno=? ";
            queryList.add(contractno);
        }
        if (!chanelno.equals(""))
        {
            sql+=" and billbmw.chanelno like ? ";
            queryList.add("%"+chanelno+"%");
        }
        if (!tradechanel.equals(""))
        {
            sql+=" and billbmw.tradechanel = ? ";
            queryList.add(tradechanel);
        }
        sql+=" order by billbmw.billno desc";
        String resultss="";
        List<Map<String, Object>> list=queryForList(jdbcTemplate,sql,queryList.toArray());
        resultss=GetRsultString(list,pageindex,pagesize);
        return  resultss;
    }

    public String billbmw_down(String data)
    {
        String billno="",dealerno="",tradetype="",dealername="",contractno="",act="",brand="";
        int time1=0,time2=0;
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
        if (jsonObject.keySet().contains("billno"))billno=jsonObject.get("billno").getAsString();
        if (jsonObject.keySet().contains("dealerno"))dealerno=jsonObject.get("dealerno").getAsString();
        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();
        if (jsonObject.keySet().contains("time1"))time1=jsonObject.get("time1").getAsInt();
        if (jsonObject.keySet().contains("time2"))time2=jsonObject.get("time2").getAsInt();
        if (jsonObject.keySet().contains("tradetype"))tradetype=jsonObject.get("tradetype").getAsString();
        if (jsonObject.keySet().contains("dealername"))dealername=jsonObject.get("dealername").getAsString();
        if (jsonObject.keySet().contains("act"))act=jsonObject.get("act").getAsString();
        if (jsonObject.keySet().contains("brand"))brand=jsonObject.get("brand").getAsString();
        String sql ="";


        sql="select billbmw.*,FROM_UNIXTIME(billbmw.ttime,'%Y-%m-%d %H:%i:%s') as createtime,dealer.dealername,dealer.brand,dealer.is_elec_invoice,usermember.username,payorder.payorderno from billbmw inner join dealer on dealer.dealerno=billbmw.dealerno left outer join usermember on billbmw.userid=usermember.id left outer join payorder on payorder.traceno=billbmw.chanelno where 1=1";
        if (!act.toLowerCase().equals("管理员")) sql+=" and billbmw.tradetype not like '订单支付%' ";
        List<Object> queryList=new ArrayList<Object>();
        if (!billno.equals(""))
        {
            sql+=" and billbmw.billno=?";
            queryList.add(billno);
        }
        if (!tradetype.equals(""))
        {
            sql+=" and billbmw.tradetype=?";
            queryList.add(tradetype);
        }
        if (!dealername.equals(""))
        {
            sql+=" and dealer.dealername like ?";
            queryList.add("%"+dealername+"%");
        }
        if (!brand.equals(""))
        {
            sql+=" and dealer.brand = ?";
            queryList.add(brand);
        }
        if (!dealerno.equals("")&&!dealerno.equals("zb"))
        {
            sql+=" and billbmw.dealerno=?";
            queryList.add(dealerno);
        }
        if (time1!=0)
        {
            sql+=" and billbmw.ttime>?";
            queryList.add(time1);
        }
        if (time2!=0)
        {
            sql+=" and billbmw.ttime<?";
            queryList.add(time2);
        }
        if (!contractno.equals(""))
        {
            sql+=" and billbmw.contractno=? ";
            queryList.add(contractno);
        }
        sql+=" order by billbmw.ttime desc";
        Map<String,Object> title=new LinkedHashMap<>();
        title.put("billno","交易编号");
        title.put("createtime","交易时间");
        title.put("in_or_out","收支类型");
        title.put("tradetype","交易类型");
        title.put("contractno","销售凭证号");
        title.put("payorderno","商户订单号");
        title.put("tradechanel","交易渠道");
        title.put("chanelno","交易流水号");
        title.put("amount","交易金额");
        title.put("balance","余额");
        title.put("brand","品牌");
        title.put("dealerno","经销商代码");
        title.put("dealername","经销商名称");
        title.put("username","操作人");
        title.put("remark","备注");
        List<Map<String,Object>>list=queryForList(jdbcTemplate,sql,queryList.toArray());
       String  resultss= createCSVFile(list,title,"","");
        return  resultss;
    }
    public String payment_down(String data)
    {
        String billno="",dealerno="",tradetype="",dealername="",contractno="",tradechanel="",brand="";
        int time1=0,time2=0;
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
        if (jsonObject.keySet().contains("billno"))billno=jsonObject.get("billno").getAsString();
        if (jsonObject.keySet().contains("brand"))brand=jsonObject.get("brand").getAsString();

        if (jsonObject.keySet().contains("dealerno"))dealerno=jsonObject.get("dealerno").getAsString();
        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();
        if (jsonObject.keySet().contains("time1"))time1=jsonObject.get("time1").getAsInt();
        if (jsonObject.keySet().contains("time2"))time2=jsonObject.get("time2").getAsInt();
        if (jsonObject.keySet().contains("tradetype"))tradetype=jsonObject.get("tradetype").getAsString();
        if (jsonObject.keySet().contains("dealername"))dealername=jsonObject.get("dealername").getAsString();
        if (jsonObject.keySet().contains("tradechanel"))tradechanel=jsonObject.get("tradechanel").getAsString();
        String sql ="";


        sql="select billbmw.*,amount*in_or_out as payamount,FROM_UNIXTIME(billbmw.ttime,'%Y-%m-%d %H:%i:%s') as createtime,dealer.brand,dealer.dealername,dealer.is_elec_invoice,(select username from usermember where id=billbmw.userid) as username from billbmw inner join dealer on dealer.dealerno=billbmw.dealerno where billbmw.tradetype not like '订单支付%'";

        List<Object> queryList=new ArrayList<Object>();
        if (!billno.equals(""))
        {
            sql+=" and billbmw.billno=?";
            queryList.add(billno);
        }
        if (!tradetype.equals(""))
        {
            sql+=" and billbmw.tradetype=?";
            queryList.add(tradetype);
        }
        if (!dealername.equals(""))
        {
            sql+=" and dealer.dealername like ?";
            queryList.add("%"+dealername+"%");
        }
        if (!brand.equals(""))
        {
            sql+=" and dealer.brand = ?";
            queryList.add(brand);
        }
        if (!dealerno.equals("")&&!dealerno.equals("zb"))
        {
            sql+=" and billbmw.dealerno=?";
            queryList.add(dealerno);
        }
        if (time1!=0)
        {
            sql+=" and billbmw.ttime>?";
            queryList.add(time1);
        }
        if (time2!=0)
        {
            sql+=" and billbmw.ttime<?";
            queryList.add(time2);
        }
        if (!contractno.equals(""))
        {
            sql+=" and billbmw.contractno=? ";
            queryList.add(contractno);
        }
        if (!tradechanel.equals(""))
        {
            sql+=" and billbmw.tradechanel = ? ";
            queryList.add(tradechanel);
        }
        sql+=" order by billbmw.ttime desc";
        Map<String,Object> title=new LinkedHashMap<>();
        title.put("brand","品牌");
        title.put("dealername","经销商");
        title.put("tradetype","类型");
        title.put("chanelno","付款单号");
        title.put("payamount","金额");
        title.put("remark","备注");
        title.put("username","操作人");
        title.put("createtime","交易时间");

        List<Map<String,Object>>list=queryForList(jdbcTemplate,sql,queryList.toArray());
        String  resultss= createCSVFile(list,title,"","");
        return  resultss;
    }
    public String addPayrequest(String data){
        String billno="",dealerno="",tradetype="",brand="",contractno="",userid="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String result="";
        float amount=0;
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;

        if (jsonObject.keySet().contains("billno"))billno=jsonObject.get("billno").getAsString();
        if (jsonObject.keySet().contains("dealerno"))dealerno=jsonObject.get("dealerno").getAsString();else return GetErrorString(1,"");
        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();
        if (jsonObject.keySet().contains("tradetype"))tradetype=jsonObject.get("tradetype").getAsString();
        if (jsonObject.keySet().contains("brand"))brand=jsonObject.get("brand").getAsString();
        if (jsonObject.keySet().contains("userid"))userid=jsonObject.get("userid").getAsString();
        if (jsonObject.keySet().contains("amount"))amount=jsonObject.get("amount").getAsFloat();
        if (amount==0)return GetErrorString(3,"支付金额不能为0");
        String sql ="";
        String resultss="";
        sql="select status,agentprice from contract where contractno=?";
        if (contractno.startsWith("MT"))sql="select status,agentprice from mt_contract where contractno=?";

        Map<String,Object>map=queryForMap(jdbcTemplate,sql,contractno);
        if (map!=null)
        {
         if (setting.NUllToSpace(map.get("status")).equals("已支付")) {
         return GetErrorString(3,"合同已经支付，不能重新支付！");
         }else   if (setting.NUllToSpace(map.get("status")).equals("草稿")) {
             return GetErrorString(3, "合同未提交，不能支付！");
         }
          if   (amount!=nullToZero(map.get("agentprice")+""))
          {
              return GetErrorString(3,"支付金额与合同金额不一致，不能支付！");
          }

        }
        Object[] queryList;
        if (!contractno.equals(""))
        {
            sql="select id from payrequest where dealerno=? and contractno=? order by ttime desc";

            queryList=new Object[2];
            queryList[0]=dealerno;
            queryList[1]=contractno;
            map=queryForMap(jdbcTemplate,sql,queryList);

            if (map!=null)
            {
                resultss=setting.NUllToSpace(map.get("id"));
                resultss="{\"id\":\""+resultss+"\"}";
                return  resultss;
            }
        }



        sql="insert into payrequest (id,billno,dealerno,contractno,tradetype,brand,userid,amount) values (uuid(),?,?,?,?,?,?,?)";

        queryList=new Object[7];
        queryList[0]=billno;
        queryList[1]=dealerno;
        queryList[2]=contractno;
        queryList[3]=tradetype;
        queryList[4]=brand;
        queryList[5]=userid;
        queryList[6]=amount;
        jdbcTemplate.update(sql,queryList);
        sql="select id from payrequest where dealerno=? and contractno=? order by ttime desc";
        queryList=new Object[2];
        queryList[0]=dealerno;
        queryList[1]=contractno;
        map=queryForMap(jdbcTemplate,sql,queryList);

        if (map!=null)
        {
            resultss=setting.NUllToSpace(map.get("id"));
        }
        resultss="{\"id\":\""+resultss+"\"}";
        return  resultss;
    }
    public String getSalebrand(String data){
        String dealerno="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String result="";
        float amout=0;
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;

        if (jsonObject.keySet().contains("dealerno"))dealerno=jsonObject.get("dealerno").getAsString();else return GetErrorString(1,"");

        String sql ="select salebrand,brand from dealer where dealerno=?";
        Map<String,Object>map=queryForMap(jdbcTemplate,sql,dealerno);
        String brand="",salbrand="";
        if (map!=null)
        {
            salbrand=setting.NUllToSpace(map.get("salebrand"));
            brand=setting.NUllToSpace(map.get("brand"));
        }
        if (domainenter.equals("0"))salbrand="Porsche";
        else   if (domainenter.equals("2"))salbrand="Mercedes-Benz";
       // else   if (domainenter.equals("3"))salbrand="Mazda";
        if (!brand.equals("")&&salbrand.equals("")&&domainenter.equals("3"))salbrand=brand;
        if (domainenter.equals("1")&&salbrand.equals(""))
        {
            //宝马的账号没有设置品牌时默认两个品牌
            salbrand="BMW MINI";
        }
        String resultss="[";
        String[]brands=salbrand.split(" ");
        for (int i=0;i<brands.length;i++)
        {
            if (!brands[i].equals(""))
            resultss+="{\"brand\":\""+brands[i]+"\"},";
        }
        resultss=setting.RTrim(resultss,",");
       resultss+="]";
        return  resultss;
    }
}
