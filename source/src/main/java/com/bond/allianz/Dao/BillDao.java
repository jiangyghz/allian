package com.bond.allianz.Dao;



import com.bond.allianz.utils.SendMailUtil;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
public class BillDao extends BaseDao{

    @Value("${bill.autocheck}")
    public String  billautocheck;
    @Value("${domain.enter}")
    public String domainenter;
    @Autowired
    private JdbcTemplate jdbcTemplate;



    /**
     * 根据账单no 查询
     * @param billno
     * @return
     */
    public  Map<String,Object> selectByKey(String billno){
        String sql ="select * from bill where billno=? ";
       Map<String,Object> target=jdbcTemplate.queryForMap(sql,billno);
        return  target;
    }

    /**
     * 修改状态
     * @param billno
     * @param status
     * @return
     */
    public  int updateStateByKey(String billno,String status,float payamount,String paytime,String payorderno){
        String sql ="update bill set status=?,payamount=?,paytime=? ,payorderno=? where billno=? ";
        int r = jdbcTemplate.update(sql,status,payamount,paytime,payorderno,billno);
        return  r;
    }

    public String manualBill(String data){
        String contractno="",dealerno="",invoiceno="",invoicetime="";
        String userid="";
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
        if (jsonObject.keySet().contains("userid"))userid=jsonObject.get("userid").getAsString();
        if (jsonObject.keySet().contains("invoiceno"))invoiceno=jsonObject.get("invoiceno").getAsString();
        if (jsonObject.keySet().contains("invoicetime"))invoicetime=jsonObject.get("invoicetime").getAsString();
        if (jsonObject.keySet().contains("dealerno"))dealerno=jsonObject.get("dealerno").getAsString();else return  GetErrorString(1,"");
       float amount=0;
        String sql ="";
        int tt=getCurrenntTime();
        int contractquality=0;
        sql="select  billno from billdetail   where  contractno in ('"+contractno.replace(",","','")+"') and (billno is null or billno='') and dealerno=?";
        Map<String ,Object>map=queryForMap(jdbcTemplate,sql,dealerno);
        if (map==null)
        {
            return  GetErrorString(3,"没有未出账单的合同号，提交不成功！");
        }
        sql="select sum(billdetail.amount) as amount,count(billdetail.contractno) as contractquality,dealer.brand,dealer.is_elec_invoice from  billdetail inner join dealer on dealer.dealerno=billdetail.dealerno where billdetail.contractno in ('"+contractno.replace(",","','")+"') and (billdetail.billno is null or billdetail.billno='') and billdetail.dealerno=?";
      sql+=" GROUP BY dealer.brand,dealer.is_elec_invoice";
       map=queryForMap(jdbcTemplate,sql,dealerno);
       int is_elec_invoice=0;
        String brand="";
        if (map!=null)
        {
            amount=nullToZero(map.get("amount")+"");
            contractquality=strToShortInt(map.get("contractquality")+"");
            brand=setting.NUllToSpace(map.get("brand"));
          //  is_elec_invoice=strToShortInt(map.get("is_elec_invoice")+"");
        }else return  GetErrorString(3,"合同号不存在！");
        String billno=getBillno();
        String status="";
       /* if (brand.equals("BMW")||brand.equals("MINI"))
        {
            if (is_elec_invoice==1)
            status="已出账单";
            else
                status="待开票";
        }else status="未确认";*/
        status="未确认";
        Object[] queryList=new Object[7];
        if (domainenter.equals("0")&&amount>0)//	保时捷生成账单时（手动&自动），系统自动增加一列发票邮寄费用（150元）
        {
            sql = "insert into billdetail (id,amount,ttime,dealerno,billno,contractno) values (uuid(),150," + tt + ",?,?,'服务费')";
            queryList = new Object[2];
            queryList[0] = dealerno;
            queryList[1] = billno;

            jdbcTemplate.update(sql, queryList);
            amount+=150;
        }
        if (brand.toLowerCase().equals("porsche")&&amount==0)status="已支付";
            sql="insert into bill (billno,dealerno,amount,userid,contractquality,ttime,invoicetime,invoiceno,status) values (?,?,?,?,?,"+tt+",?,?,'"+status+"')";
        queryList=new Object[7];
        queryList[0]=billno;
        queryList[1]=dealerno;
        queryList[2]=amount;
        queryList[3]=userid;
        queryList[4]=contractquality;
        if (invoicetime.equals(""))queryList[5]=null;else queryList[5]=invoicetime;
        queryList[6]=invoiceno;

        jdbcTemplate.update(sql,queryList);
     //   if (brand.equals("Porsche")) status="已出账单";
        status="已出账单";
        if (brand.toLowerCase().equals("porsche")&&amount==0)status="已支付";
        if (brand.equals("Motor"))
            sql="update  mt_contract  set status='"+status+"' where  contractno in ('"+contractno.replace(",","','")+"') and (status!='已取消')";
        else
        sql="update  contract  set status='"+status+"' where  contractno in ('"+contractno.replace(",","','")+"') and (status!='已取消')";
        jdbcTemplate.update(sql);
        sql="update  billdetail  set billno=? where  contractno in ('"+contractno.replace(",","','")+"') and (billno is null or billno='')";

        jdbcTemplate.update(sql,billno);
      /*  if (!brand.equals("Porsche"))
        {
            sql="update bill set confirmquality=contractquality,confirmamount=amount,confirmtime="+tt+" where billno=?";
            jdbcTemplate.update(sql,billno);
        }*/
        String resultss="",content="";
        resultss="{\"errcode\":\"0\",\"contractno\":\""+contractno+"\",\"billno\":\""+billno+"\"}";

         if (brand.equals("Porsche")) {
            content += "    您有账单尚未确认，请及时确认 <a href=\""+ GetPathurl() + "background/index.html#/check。\">账单链接</a> 。若您5个自然日内仍未确认，您的销售功能将无法使用！\n";

        } else {

            content += "    您有账单尚未确认，请及时确认 <a href=\""+ GetPathurl() + "background/index.html#/check。\">账单链接</a> 。若您5个自然日内仍未确认，系统将自动确认！\n";
        }
        SendEmailDealer(dealerno,content,"账单");
        // if (jdbcTemplate.update(sql,queryList)>0)resultss="{\"errcode\":\"0\",\"contractno\":\""+contractno+"\",\"billno\":\""+billno+"\"}";
        //else resultss=GetErrorString(3,"提交不成功！");
        return  resultss;
    }
    public String cantractBill(String contractno,String userid,String invoiceno,String invoiceurl){

        String sql ="",dealerno="";
        int tt=getCurrenntTime();
        //先判断是否已经开票，如果已经开票就直接返回开票链接
        double agentprice=0;
        sql="select agentprice,dealerno from contract where status='已支付' and contractno=?";
        Map<String ,Object>map=queryForMap(jdbcTemplate,sql,contractno);
        if (map==null)
        {
            return  GetErrorString(3,"合同号不存在或不是已支付状态！");
        }else
        {
            agentprice=setting.NullToZero(map.get("agentprice")+"");
            dealerno=map.get("dealerno")+"";
        }
        //判断是否已经生成账单
        sql="select  billno from billdetail   where  contractno = '"+contractno+"' and (billno is null or billno='') ";
        map=queryForMap(jdbcTemplate,sql);
        if (map==null)
        {
            return  GetErrorString(3,"该合同号已出账单，提交不成功！");
        }
       //开电子发票
        String billno=getBillno()+contractno;
        String status="已开票";

        sql="insert into bill (billno,dealerno,amount,userid,contractquality,ttime,invoiceno,status,confirmquality,confirmamount,invoicetype,invoiceurl,invoicetime) values (?,?,?,?,1,"+tt+",?,'"+status+"',1,"+agentprice+",'普通发票',?,?)";
        Object[] queryList=new Object[7];
        queryList[0]=billno;
        queryList[1]=dealerno;
        queryList[2]=agentprice;
        queryList[3]=userid;
        queryList[4]=invoiceno;
        queryList[5]=invoiceurl;
        queryList[6]=getCurrenntTime();
        jdbcTemplate.update(sql,queryList);

        sql="update  contract  set status='"+status+"' where  contractno ='"+contractno+"'";
        jdbcTemplate.update(sql);
        sql="update  billdetail  set billno=? where  contractno in ('"+contractno.replace(",","','")+"') and (billno is null or billno='')";

        jdbcTemplate.update(sql,billno);

        String resultss="";
        resultss="{\"errcode\":\"0\",\"contractno\":\""+contractno+"\",\"billno\":\""+billno+"\",\"invoiceurl\":\""+invoiceurl+"\"}";

        return  resultss;
    }
    public  long getlassbilltime()
    {
        long ii=0;
        String sql="select billtime from invoiceinfo";
        Map<String ,Object>map=queryForMap(jdbcTemplate,sql);
        if (map!=null)
        {
            ii=nullToInt(map.get("billtime")+"") ;
        }
        return ii;
    }
    public  int updatelassbilltime()
    {
        int ii=0;
        String sql="update invoiceinfo set billtime="+getCurrenntTime();
        ii=jdbcTemplate.update(sql);
        return ii;
    }
    public String autoBill(int time1,int time2){
        String dealerno="",productid="";

        float amount=0;
        String sql ="";
        int tt=getCurrenntTime();
        int contractquality=0;

        //logs.info(GetNowDate("yyyy-MM-dd HH:mm:ss")+"自动创建账单开始","autoBill");
      //  sql="select sum(amount) as amount,count(contractno) as contractquality,dealerno from  billdetail where  (billno is null or billno='') and dealerno in (select dealerno from dealer where brand='Porsche' and valid=1)";
        sql="select sum(billdetail.amount) as amount,count(billdetail.contractno) as contractquality,billdetail.dealerno,dealer.brand,dealer.is_elec_invoice from  billdetail  left outer join dealer on billdetail.dealerno=dealer.dealerno ";
       if (domainenter.equals("1"))//宝马账单按照产品生成
       {
           sql="select insuranceproduct.groupname ,sum(billdetail.amount) as amount,count(billdetail.contractno) as contractquality,billdetail.dealerno,dealer.brand,dealer.is_elec_invoice from  billdetail  left outer join dealer on billdetail.dealerno=dealer.dealerno ";
           sql+="  left outer join  contract on   contract.contractno=billdetail.contractno left join insuranceproduct on insuranceproduct.productid=contract.productid ";
       }
        sql+="  where  (billdetail.billno is null or billdetail.billno='') and billdetail.contractno not like 'MT%'  ";
        if (time2==0)time2=setting.GetMonthtime();
        if (time1!=0)sql+=" and billdetail.ttime>"+time1;
        if (time2!=0)sql+=" and billdetail.ttime<"+time2;
        sql+="  group by billdetail.dealerno,dealer.brand,dealer.is_elec_invoice";
        if (domainenter.equals("1"))
        {
            sql+=",insuranceproduct.groupname order by billdetail.dealerno";
        }
        List<Map<String ,Object>>list=queryForList(jdbcTemplate,sql);
        Map<String ,Object>map;
         String brand="";
         String status="已出账单";
         int is_elec_invoice=0;
        String resultss="";
        String product="";
        if(list!=null) {
            for (int i = 0; i < list.size(); i++) {
                map = list.get(i);
                dealerno = NUllToSpace(map.get("dealerno") + "");
                if (dealerno.equals(""))continue;
                if (domainenter.equals("1")) {
                    //productid = NUllToSpace(map.get("productid") + "");
                   // if (productid.equals("BMW01"))product="RTI";else  if (productid.equals("BMW02"))product="KEY";
                    product = NUllToSpace(map.get("groupname") + "");
                }
                brand = NUllToSpace(map.get("brand") + "");
                amount = nullToZero(map.get("amount") + "");
                contractquality = strToShortInt(map.get("contractquality") + "");
                is_elec_invoice = strToShortInt(map.get("is_elec_invoice") + "");
               /* if (brand.equals("BMW")||brand.equals("MINI")||billautocheck.equals("1")) {
                    if (is_elec_invoice == 1)
                        status = "已出账单";
                    else
                        status = "待开票";
                } else status = "未确认";*/
                status = "未确认";
                String billno = getBillno();
                Object[] queryList ;
                if (domainenter.equals("0")&&amount>0)//	保时捷生成账单时（手动&自动），系统自动增加一列发票邮寄费用（150元）
                {
                    queryList = new Object[2];
                    queryList[0] = dealerno;
                    queryList[1] = billno;
                    sql="select * from  billdetail where dealerno=? and billno=? and contractno='服务费'";
                    if (queryForMap(jdbcTemplate,sql,queryList)!=null)
                    {
                       //已经有服务费，说明已经启动了线程，退出生产账单
                        return "已经有服务费生成，退出账单！";
                    }
                    sql = "insert into billdetail (id,amount,ttime,dealerno,billno,contractno) values (uuid(),150," + tt + ",?,?,'服务费')";
                    jdbcTemplate.update(sql, queryList);
                    amount+=150;
                }
                if (domainenter.equals("0")&&amount==0)status="已支付";
                sql = "insert into bill (billno,dealerno,amount,userid,contractquality,ttime,status,product) values (?,?,?,?,?," + tt + ",'" + status + "',?)";
                queryList = new Object[6];
                queryList[0] = billno;
                queryList[1] = dealerno;
                queryList[2] = amount;
                queryList[3] = "系统自动";
                queryList[4] = contractquality;
                queryList[5] = product;
                jdbcTemplate.update(sql, queryList);
              //  if (brand.equals("Porsche")&&!billautocheck.equals("1")) status = "已出账单";
                status = "已出账单";
                if (domainenter.equals("0")&&amount==0)status="已支付";

                if (brand.equals("Motor"))
                    sql = "update  mt_contract  set status='" + status + "' where  contractno in (select contractno from billdetail where  dealerno=? and (billno is null or billno='') and (status!='已取消')";
                else

                    sql = "update  contract  set status='" + status + "' where  contractno in (select contractno from billdetail where  dealerno=? and (billno is null or billno='') and (status!='已取消')";
                if (time1 != 0) sql += " and ttime>" + time1;
                if (time2 != 0) sql += " and ttime<" + time2;
                sql += ")";
                //if (domainenter.equals("1"))sql+=" and productid='"+productid+"'";
                if (domainenter.equals("1"))sql+=" and productid in ( select productid from insuranceproduct where groupname ='"+product+"')";
                jdbcTemplate.update(sql, billno);
                sql = "update  billdetail  set billno=? where  dealerno=? and (billno is null or billno='')";
                if (time1 != 0) sql += " and ttime>" + time1;
                if (time2 != 0) sql += " and ttime<" + time2;
                if (domainenter.equals("1"))sql+=" and contractno in (select contractno from contract where productid in ( select productid from insuranceproduct where groupname ='"+product+"') and dealerno='"+dealerno+"')";
                queryList = new Object[2];
                queryList[0] = billno;
                queryList[1] = dealerno;
                jdbcTemplate.update(sql, queryList);
               /* if (!brand.equals("Porsche")) {
                    sql = "update bill set confirmquality=contractquality,confirmamount=amount ,confirmtime=" + tt + " where billno=?";
                    jdbcTemplate.update(sql, billno);
                }*/
                if (contractquality == 0 && amount == 0) continue;
                String content = "";

                try
                {
                    if (brand.equals("Porsche")) {
                        content += "    您有账单尚未确认，请及时确认 <a href=\""+ GetPathurl() + "background/index.html#/check。\">账单链接</a> 。若您5个自然日内仍未确认，您的销售功能将无法使用！\n";

                    } else {

                        content += "    您有账单尚未确认，请及时确认 <a href=\""+ GetPathurl() + "background/index.html#/check。\">账单链接</a> 。若您5个自然日内仍未确认，系统将自动确认！\n";
                    }
                    SendEmailDealer(dealerno, content, "账单");
                }
               catch (Exception ee)
               {

               }
            }
            resultss = "{\"errcode\":\"0\",\"count\":\"" + list.size() + "\"}";
            if (domainenter.equals("1"))autoBill_motor(time1,time2);
            //logs.info(GetNowDate("yyyy-MM-dd HH:mm:ss")+"自动创建账单"+list.size()+"条。","autoBill");
            //解决保时捷月初服务费重复的问题
            if (domainenter.equals("0"))
            {
                sql="delete from bill where billno not in (select billno from billdetail where IFNULL(billno,'')!='' and contractno!='服务费') and billno like '"+GetNowDate("yyyyMM")+"%'";
                jdbcTemplate.update(sql);
                String billno=GetNowDate("yyyyMM")+"0001";
                sql="select count(*),billno from  billdetail where  contractno='服务费' group by billno having count(*)>1";
                List<Map<String ,Object>>  list1=queryForList(jdbcTemplate,sql);
                if (list1.size()>1)
                {
                    for (int ii=0;ii<list1.size();ii++)
                    {
                        map=list1.get(ii);
                        billno=map.get("billno")+"";
                        sql="set @id=(select id from billdetail where contractno='服务费' and billno='"+billno+"' limit 1);\n" +
                                "      delete from billdetail where contractno='服务费' and billno='"+billno+"' and id!=@id;\n";
                        jdbcTemplate.update(sql);
                    }

                }
            } if (domainenter.equals("1")) {
                //宝马删除重复的空账单
                sql = "delete from bill where billno not in (select billno from billdetail where IFNULL(billno,'')!='' ) and billno like '" + GetNowDate("yyyyMM") + "%'";
                jdbcTemplate.update(sql);
            }
        }else     resultss = "{\"errcode\":\"0\",\"count\":\"0\"}";




        return  resultss;
    }
    public String autoBill_motor(int time1,int time2){
        String dealerno="",productid="";

        float amount=0;
        String sql ="";
        int tt=getCurrenntTime();
        int contractquality=0;

        //logs.info(GetNowDate("yyyy-MM-dd HH:mm:ss")+"自动创建账单开始","autoBill");
        //  sql="select sum(amount) as amount,count(contractno) as contractquality,dealerno from  billdetail where  (billno is null or billno='') and dealerno in (select dealerno from dealer where brand='Porsche' and valid=1)";

            sql="select sum(billdetail.amount) as amount,count(billdetail.contractno) as contractquality,billdetail.dealerno,dealer.brand,dealer.is_elec_invoice from  billdetail  left outer join dealer on billdetail.dealerno=dealer.dealerno ";

            sql+="  where  (billdetail.billno is null or billdetail.billno='') and billdetail.contractno like 'MT%' ";
        if (time2==0)time2=setting.GetMonthtime();
        if (time1!=0)sql+=" and billdetail.ttime>"+time1;
        if (time2!=0)sql+=" and billdetail.ttime<"+time2;
        sql+="  group by billdetail.dealerno,dealer.brand,dealer.is_elec_invoice order by billdetail.dealerno";

        List<Map<String ,Object>>list=queryForList(jdbcTemplate,sql);
        Map<String ,Object>map;
        String brand="";
        String status="已出账单";
        int is_elec_invoice=0;
        String resultss="";
        String product="常用套餐";
        if(list!=null) {
            for (int i = 0; i < list.size(); i++) {
                map = list.get(i);
                dealerno = NUllToSpace(map.get("dealerno") + "");
                if (dealerno.equals(""))continue;

                brand = NUllToSpace(map.get("brand") + "");
                amount = nullToZero(map.get("amount") + "");
                contractquality = strToShortInt(map.get("contractquality") + "");
                is_elec_invoice = strToShortInt(map.get("is_elec_invoice") + "");

                status = "未确认";
                String billno = getBillno();
                Object[] queryList ;

                sql = "insert into bill (billno,dealerno,amount,userid,contractquality,ttime,status,product) values (?,?,?,?,?," + tt + ",'" + status + "',?)";
                queryList = new Object[6];
                queryList[0] = billno;
                queryList[1] = dealerno;
                queryList[2] = amount;
                queryList[3] = "系统自动";
                queryList[4] = contractquality;
                queryList[5] = product;
                jdbcTemplate.update(sql, queryList);
                //  if (brand.equals("Porsche")&&!billautocheck.equals("1")) status = "已出账单";
                status = "已出账单";
                if (domainenter.equals("0")&&amount==0)status="已支付";


                    sql = "update  mt_contract  set status='" + status + "' where  contractno in (select contractno from billdetail where  dealerno=? and (billno is null or billno='') ";

                if (time1 != 0) sql += " and ttime>" + time1;
                if (time2 != 0) sql += " and ttime<" + time2;
                sql += ") and (status!='已取消')";
                //if (domainenter.equals("1"))sql+=" and productid='"+productid+"'";
                if (domainenter.equals("1"))sql+=" and productid in ( select productid from mt_product where groupname ='"+product+"')";
                //宝马不更新凭证状态
            //    jdbcTemplate.update(sql, billno);
                sql = "update  billdetail  set billno=? where  dealerno=? and (billno is null or billno='')";
                if (time1 != 0) sql += " and ttime>" + time1;
                if (time2 != 0) sql += " and ttime<" + time2;
                if (domainenter.equals("1"))sql+=" and contractno in (select contractno from mt_contract where productid in ( select productid from mt_product where groupname ='"+product+"') and dealerno='"+dealerno+"')";
                queryList = new Object[2];
                queryList[0] = billno;
                queryList[1] = dealerno;
                jdbcTemplate.update(sql, queryList);
               /* if (!brand.equals("Porsche")) {
                    sql = "update bill set confirmquality=contractquality,confirmamount=amount ,confirmtime=" + tt + " where billno=?";
                    jdbcTemplate.update(sql, billno);
                }*/
                if (contractquality == 0 && amount == 0) continue;
                String content = "";

                try
                {
                    if (brand.equals("Porsche")) {
                        content += "    您有账单尚未确认，请及时确认 <a href=\""+ GetPathurl() + "background/index.html#/check。\">账单链接</a> 。若您5个自然日内仍未确认，您的销售功能将无法使用！\n";

                    } else {

                        content += "    您有账单尚未确认，请及时确认 <a href=\""+ GetPathurl() + "background/index.html#/check。\">账单链接</a> 。若您5个自然日内仍未确认，系统将自动确认！\n";
                    }
                    SendEmailDealer(dealerno, content, "账单");
                }
                catch (Exception ee)
                {

                }
            }
            resultss = "{\"errcode\":\"0\",\"count\":\"" + list.size() + "\"}";

        }else     resultss = "{\"errcode\":\"0\",\"count\":\"0\"}";




        return  resultss;
    }
    public void autoBillmail()  {
        Date d1 = new Date(), d2 = new Date();
        int hh=d1.getHours();
     //   if (hh!=9)return;//9点发送
        String contractno="",dealerno="";
        String userid="";
        String result="";
        float amount=0;
        String sql ="";
        int tt=getCurrenntTime();
        int contractquality=0;
        Map<String ,Object>map;
        //宝马确认自动确认账单
        if (domainenter.equals("1")||domainenter.equals("2")||domainenter.equals("3"))
        {
            String billno="";
            sql="select distinct dealerno from bill where status='未确认' and ttime<"+(tt-5*24*60*60)+" ";
            List<Map<String ,Object>>list1=queryForList(jdbcTemplate,sql);
            for (int i=0;i<list1.size();i++) {
                map = list1.get(i);
                dealerno = NUllToSpace(map.get("dealerno") + "");
                sql="insert into systemremind (dealerno,ttime,remind) values (?,"+tt+",'您的上月账单未在期限内确认，已自动确认，如有疑问请联系管理员。')";
                jdbcTemplate.update(sql,dealerno);
            }
            sql="update bill set confirmquality=contractquality,confirmamount=amount,confirmtime="+tt+",status='待开票' where status='未确认' and ttime<"+(tt-5*24*60*60);
           // sql+=" and dealerno in (select dealerno from dealer where brand='BMW' or brand='Daimler')";
            jdbcTemplate.update(sql);
            //,billremark='您的上月账单未在期限内确认，已自动确认，如有疑问请联系管理员。'

        }
        //logs.info(GetNowDate("yyyy-MM-dd HH:mm:ss")+"账单提醒开始","autoBillmail");
        sql="select count(dealerno) as sl,dealerno,FROM_UNIXTIME(ttime, '%Y-%m-%d') as riqi,sum(amount) as amount,status  from  bill where  (status!='已支付' and status!='已提交支付证明') and dealerno in (select dealerno from dealer where brand='Porsche' and valid=1)";
        sql+="  group by dealerno,FROM_UNIXTIME(ttime, '%Y-%m-%d')";
        List<Map<String ,Object>>list=queryForList(jdbcTemplate,sql);

        //logs.info(GetNowDate("yyyy-MM-dd HH:mm:ss")+"共有个"+list.size()+"未确认账单","autoBillmail");
        int count=0;

        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
        try {
            d1= simpleDateFormat.parse(setting.GetNowDate("yyyy-MM-dd"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int t0=GetTodaytime();
        String status="";
        for (int i=0;i<list.size();i++)
        {


            map=list.get(i);
            dealerno=NUllToSpace(map.get("dealerno")+"");
            status=NUllToSpace(map.get("status")+"");
            count=setting.StrToInt(map.get("sl")+"");
            amount=setting.NullToZero(map.get("amount")+"");

            try {
                d2= simpleDateFormat.parse(map.get("riqi")+"");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            int days = (int) ((d1.getTime() - d2.getTime()) / (24*3600*1000));
            if (days%5!=0)continue;
            if (days==0)continue;
            String content="";
           sql="select cid from pageevent where cid=? and ttime>"+t0+" and Pagename='autoBillmail'";
           map=queryForMap(jdbcTemplate,sql,dealerno);
           if (map!=null)continue;;
            sql="insert into pageevent (cid,Pagename,ttime) values (?,'autoBillmail',"+tt+")";
            jdbcTemplate.update(sql,dealerno);
            if (domainenter.equals("0")) {
                content += "    您有账单尚未确认，请及时确认 <a href=\""+ GetPathurl() + "background/index.html#/check。\">账单链接</a> 。若您5个自然日内仍未确认，您的销售功能将无法使用！\n";

            } else {

                content += "    您有账单尚未确认，请及时确认 <a href=\""+ GetPathurl() + "background/index.html#/check。\">账单链接</a> 。若您5个自然日内仍未确认，系统将自动确认！\n";
            }
            SendEmailDealer(dealerno,content,"账单");

        }




    }
    private String getBillno()
    {
        String resultss="";
        int t1=GetMonthtime();
        long  ii=0;
        String sqlstring="select  left(billno,10) as billno from bill where ttime>"+t1+" order by left(billno,10) desc";
        Map<String,Object>map=queryForMap(jdbcTemplate,sqlstring);
        if (map!=null)
        {
            ii=nullToInt(map.get("billno")+"") ;
        }
        if (ii==0)resultss=GetNowDate("yyyyMM")+"0001";
        else resultss=(ii+1)+"";
        return resultss;
    }
    public String uploadpayment(String data,  MultipartFile file){
        String billno="";
        String payorderurl="";
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


        if (jsonObject.keySet().contains("billno"))billno=jsonObject.get("billno").getAsString();else return  GetErrorString(1,"");
        payorderurl=uploadJpg(file);
        String sql ="";
        int tt=getCurrenntTime();
        sql="update contract set status='已提交支付证明' where contractno in (select contractno from billdetail where billno=?)  and (status!='已取消')";
        jdbcTemplate.update(sql,billno);

        sql="update bill set paytime="+tt+",payamount=amount , payorderurl=?,status='已提交支付证明' where billno=?";

        Object[] queryList=new Object[2];
        queryList[0]=payorderurl;
        queryList[1]=billno;


        String resultss="";

        if (jdbcTemplate.update(sql,queryList)==1)resultss="{\"errcode\":\"0\",\"billno\":\""+billno+"\",\"payorderurl\":\""+payorderurl+"\"}";
        else resultss=GetErrorString(3,"提交不成功！");
        return  resultss;
    }

    public String getBill(String data){
        String billno="",dealerno="",status="",dealername="",contractno="",cname="",invoicetype="",keystring="",brand="";
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
        if (jsonObject.keySet().contains("status"))status=jsonObject.get("status").getAsString();
        if (jsonObject.keySet().contains("dealername"))dealername=jsonObject.get("dealername").getAsString();
        if (jsonObject.keySet().contains("cname"))cname=jsonObject.get("cname").getAsString();
        if (jsonObject.keySet().contains("keystring"))keystring=jsonObject.get("keystring").getAsString();
        if (jsonObject.keySet().contains("brand"))brand=jsonObject.get("brand").getAsString();

        if (jsonObject.keySet().contains("invoicetype"))invoicetype=jsonObject.get("invoicetype").getAsString();
        String sql ="";


        sql="select bill.*,FROM_UNIXTIME(bill.ttime,'%Y-%m-%d %H:%i:%s') as createtime,dealer.dealername,dealer.brand,dealer.is_elec_invoice,(select count(*) from billdetail where billno=bill.billno and amount<0 ) as backcount from bill inner join dealer on dealer.dealerno=bill.dealerno where 1=1";

        List<Object> queryList=new ArrayList<Object>();
        if (!billno.equals(""))
        {
            sql+=" and bill.billno=?";
            queryList.add(billno);
        }
        if (!brand.equals(""))
        {
            sql+=" and dealer.brand=?";
            queryList.add(brand);
        }

        if (!invoicetype.equals(""))
        {
            sql+=" and bill.invoicetype=?";
            queryList.add(invoicetype);
        }
        if (!keystring.equals("")) {
            sql += " and (bill.billno like  ?  or bill.billno in (select billdetail.billno from billdetail inner join contract on contract.contractno=billdetail.contractno where contract.cname like ?  or contract.contractno  like ?) )";
            queryList.add("%"+keystring+"%");
            queryList.add("%"+keystring+"%");
            queryList.add("%"+keystring+"%");
        }

        if (!status.equals(""))
        {
            sql+=" and bill.status=?";
            queryList.add(status);
        }
        if (!dealername.equals(""))
        {
            sql+=" and dealer.dealername like ?";
            queryList.add("%"+dealername+"%");
        }
        if (!dealerno.equals("")&&!dealerno.equals("zb"))
        {
            sql+=" and bill.dealerno=?";
            queryList.add(dealerno);
        }
        if (time1!=0)
        {
            sql+=" and bill.ttime>?";
            queryList.add(time1);
        }
        if (time2!=0)
        {
            sql+=" and bill.ttime<?";
            queryList.add(time2);
        }
        if (!contractno.equals(""))
        {
            sql+=" and bill.billno in (select billno from billdetail where contractno=?) ";
            queryList.add(contractno);
        }
        if (!cname.equals(""))
        {
             if (domainenter.equals("1"))
             {
                 sql+=" and (bill.billno in (select billdetail.billno from billdetail inner join contract on contract.contractno=billdetail.contractno where contract.cname like ? )";
                 sql+=" or bill.billno in (select billdetail.billno from billdetail inner join mt_contract on mt_contract.contractno=billdetail.contractno where mt_contract.cname like ? ))";
                 queryList.add("%"+cname+"%");
                 queryList.add("%"+cname+"%");
             }else
             {
                 sql+=" and bill.billno in (select billdetail.billno from billdetail inner join contract on contract.contractno=billdetail.contractno where contract.cname like ? )";
                 queryList.add("%"+cname+"%");

             }

        }
        sql+=" order by bill.ttime desc";
        String resultss="";
        try
        {
            resultss=   GetResultString1(sql,jdbcTemplate,pageindex,pagesize,queryList.toArray());
          //  List<Map<String, Object>> list=queryForList(jdbcTemplate,sql,queryList.toArray());
           // resultss=GetRsultString(list,pageindex,pagesize);
        }
       catch (Exception e)
       {
           resultss=e.toString();
       }
        return  resultss;
    }
    public String getBillPage(String data){
        String billno="",dealerno="";
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
        if (jsonObject.keySet().contains("time1"))time1=jsonObject.get("time1").getAsInt();
        if (jsonObject.keySet().contains("time2"))time2=jsonObject.get("time2").getAsInt();
        if (jsonObject.keySet().contains("pageindex"))pageindex=jsonObject.get("pageindex").getAsInt();
        if (jsonObject.keySet().contains("pagesize"))pagesize=jsonObject.get("pagesize").getAsInt();
        String sql ="";


        sql="select * from bill  where 1=1";

        List<Object> queryList=new ArrayList<Object>();
        if (!billno.equals(""))
        {
            sql+=" and billno=?";
            queryList.add(billno);
        }
        if (!dealerno.equals("")&&!dealerno.equals("zb"))
        {
            sql+=" and dealerno=?";
            queryList.add(dealerno);
        }
        if (time1!=0)
        {
            sql+=" and ttime>?";
            queryList.add(time1);
        }
        if (time2!=0)
        {
            sql+=" and ttime<?";
            queryList.add(time2);
        }

        String resultss="";
        resultss=GetResultString1(sql,jdbcTemplate,pageindex,pagesize,queryList.toArray());
    //    List<Map<String, Object>> list=queryForList(jdbcTemplate,sql,queryList.toArray());
      //  resultss=GetRsultString(list,pageindex,pagesize);
        return  resultss;
    }
    public String getNotinBillContract(String data){
        String dealerno="";
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


        if (jsonObject.keySet().contains("dealerno"))dealerno=jsonObject.get("dealerno").getAsString();
        if (jsonObject.keySet().contains("time1"))time1=jsonObject.get("time1").getAsInt();
        if (jsonObject.keySet().contains("time2"))time2=jsonObject.get("time2").getAsInt();
        if (jsonObject.keySet().contains("pageindex"))pageindex=jsonObject.get("pageindex").getAsInt();
        if (jsonObject.keySet().contains("pagesize"))pagesize=jsonObject.get("pagesize").getAsInt();
        String sql ="";
        int tt=getCurrenntTime();

        sql="select billdetail.* from billdetail  where (billno='' or billno is null) ";
         sql ="select contract.*,FROM_UNIXTIME(contract.ttime,'%Y-%m-%d %H:%i:%s') as createtime,dealer.dealername,dealer.bank,dealer.brand,dealer.bankacount,(select FROM_UNIXTIME(ttime,'%Y-%m-%d %H:%i:%s') from  contractcheck where contractno=contract.contractno and  checkcontent='合同审核'  order by ttime desc  limit 0,1 ) as checktime ,(select FROM_UNIXTIME(ttime,'%Y-%m-%d %H:%i:%s') from  contractcheck where contractno=contract.contractno and  checkcontent='合同取消' order by ttime desc limit 0,1 ) as canceltime,(select remark from  contractcheck where contractno=contract.contractno and  checkcontent='合同取消' order by ttime desc limit 0,1 ) as cancelremark ,(select remark from  contractremark where contractno=contract.contractno  order by ttime desc limit 0,1 ) as contractremark from contract  inner join  dealer on contract.dealerno=dealer.dealerno";
        sql+=" where contract.valid=1 and contract.contractno in (select contractno from billdetail  where (billno='' or billno is null) )" ;
        List<Object> queryList=new ArrayList<Object>();

        if (!dealerno.equals(""))
        {
            sql+=" and dealerno=?";
            queryList.add(dealerno);
        }
        if (time1!=0)
        {
            sql+=" and ttime>?";
            queryList.add(time1);
        }
        if (time2!=0)
        {
            sql+=" and ttime<?";
            queryList.add(time2);
        }

        String resultss="";
        resultss=GetResultString1(sql,jdbcTemplate,pageindex,pagesize,queryList.toArray());
    //    List<Map<String, Object>> list=queryForList(jdbcTemplate,sql,queryList.toArray());
      //  resultss=GetRsultString(list,pageindex,pagesize);
        return  resultss;
    }
    public String getBilldetail(String data){
        String billno="";
        int pageindex=1 ,  pagesize=10;
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


        if (jsonObject.keySet().contains("billno"))billno=jsonObject.get("billno").getAsString();else return GetErrorString(1,"");
        if (jsonObject.keySet().contains("pageindex"))pageindex=jsonObject.get("pageindex").getAsInt();
        if (jsonObject.keySet().contains("pagesize"))pagesize=jsonObject.get("pagesize").getAsInt();
        String sql ="",brand="";
        sql="select contractno from billdetail where 1=1 and billno=? ";
        Map<String,Object>map=queryForMap(jdbcTemplate,sql,billno);
        if (map!=null)
        {
            String contractno=map.get("contractno")+"";
            if (contractno.startsWith("MT"))
                brand="Motor";
        }

        sql="select billdetail.*,contract.vin,contract.cname,contract.Businessinsurancepolicyno,contract.status,contract.pname,(select dealername from dealer where dealerno=billdetail.dealerno limit 0,1) as  dealername from billdetail left join contract on contract.contractno=billdetail.contractno where (billdetail.billno=?) ";
        if (brand.equals("Motor"))
            sql="select billdetail.*,c.vin,c.cname,c.Businessinsurancepolicyno,c.status,c.pname,(select dealername from dealer where dealerno=billdetail.dealerno limit 0,1) as  dealername from billdetail left join mt_contract c on c.contractno=billdetail.contractno where (billdetail.billno=?) ";


        String resultss="";
        Object[] args=new Object[1];
        args[0]=billno;
        resultss=GetResultString1(sql,jdbcTemplate,pageindex,pagesize,args);
    //    List<Map<String, Object>> list=queryForList(jdbcTemplate,sql,billno);
      //  resultss=GetRsultString(list,pageindex,pagesize);
        return  resultss;
    }
    public String updatebillinvoiceno(String data){
        String billno="",invoiceno="";

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


        if (jsonObject.keySet().contains("billno"))billno=jsonObject.get("billno").getAsString();else return GetErrorString(1,"");
        if (jsonObject.keySet().contains("invoiceno"))invoiceno=jsonObject.get("invoiceno").getAsString();else return GetErrorString(1,"");
        String sql ="";
        String   status="已开票";

        sql="update bill set invoiceno=?,status='"+status+"' where billno=?";
       Object[] args=new Object[2];
       args[0]=invoiceno;
        args[1]=billno;
        int ii=jdbcTemplate.update(sql,args);

        sql="update contract set status='"+status+"' where contractno in (select contractno from billdetail where billno=?) and (status!='已取消')";
        jdbcTemplate.update(sql,billno);
        String resultss="";
        if (ii==1)resultss=GetErrorString(0,"");
      else
        resultss=GetErrorString(1,"更新不成功！");
        return  resultss;
    }
    public String refuseBilldetail(String data){
        String id="",remark="";

        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String result="",userid="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;


        if (jsonObject.keySet().contains("id"))id=jsonObject.get("id").getAsString();else return GetErrorString(1,"");
        if (jsonObject.keySet().contains("remark"))remark=jsonObject.get("remark").getAsString();
        if (jsonObject.keySet().contains("userid"))userid=jsonObject.get("userid").getAsString();
        String sql ="";
       int tt=getCurrenntTime();

        sql="update billdetail set remark=?,confirm=0,confirmtime="+tt+",confirmuserid=? where (id=?) ";
        Object[] parm=new Object[3];
        parm[0]=remark;
        parm[1]=id;
        parm[2]=userid;

        String resultss="";
        if (jdbcTemplate.update(sql,parm)==1)
        {
            resultss=GetErrorString(0,"");
            sql="select billno from billdetail  where (id=?) ";
            Map<String,Object>map=queryForMap(jdbcTemplate,sql,id);
            String billno="";
            if (map!=null)billno=setting.NUllToSpace(map.get("billno"));
            if (!billno.equals("")) updatebillstatus(billno);
        }
        else
        resultss=GetErrorString(3,"提交不成功！");

        return  resultss;
    }
    public String confirmBilldetail(String data){
        String billno="";

        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String result="",userid="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;


        if (jsonObject.keySet().contains("billno"))billno=jsonObject.get("billno").getAsString();else return GetErrorString(1,"");
        if (jsonObject.keySet().contains("userid"))userid=jsonObject.get("userid").getAsString();
        String sql ="";
        int tt=getCurrenntTime();

        sql="update billdetail set confirm=1,confirmtime="+tt+",confirmuserid=? where (billno=?) and (confirm is null)";
        String resultss="";
        Object[] parm=new Object[2];
        parm[0]=userid;
        parm[1]=billno;
        Map<String,Object>map;
        if (jdbcTemplate.update(sql,parm)>0){
            String dealerno="";
            sql="select  dealerno from bill where billno=?";
            map=queryForMap(jdbcTemplate,sql,billno);
            if (map!=null)
            {

                dealerno=map.get("dealerno")+"";
               // invoice1.GetInvoice(dealerno,billno);
            }

            resultss=GetErrorString(0,"");
        }
        else
            resultss=GetErrorString(3,"提交不成功！");
        sql="select sum(amount) as amount,count(id)  as sl,confirm from billdetail where billno=? group by confirm";
        List<Map<String,Object>>list=queryForList(jdbcTemplate,sql,billno);
        int refusequality=0,confirmquality=0;
        float confirmamount=0;
        for (int i=0;i<list.size();i++)
        {
           map=list.get(i);
          if ((map.get("confirm")+"").equals("0"))refusequality=strToShortInt(map.get("sl")+"");
            if ((map.get("confirm")+"").equals("1"))
            {
                confirmquality=strToShortInt(map.get("sl")+"");
                confirmamount=nullToZero(map.get("amount")+"");
            }

        }
        sql="update bill set confirmamount=?,confirmquality=?,refusequality=? where billno=?";
        Object[] pararms=new Object[4];
        pararms[0]=confirmamount;
        pararms[1]=confirmquality;
        pararms[2]=refusequality;
        pararms[3]=billno;
        jdbcTemplate.update(sql,pararms);
        updatebillstatus(billno);
        return  resultss;
    }
    public String getUnconfirmBillCount(String data)
    {
        String dealerno="";

        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String result="",userid="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;


        if (jsonObject.keySet().contains("dealerno"))dealerno=jsonObject.get("dealerno").getAsString();else return GetErrorString(1,"");
        String sqlstring="select count(*) as sl from bill where status='未确认' and dealerno=?";
        Map<String,Object>map=queryForMap(jdbcTemplate,sqlstring,dealerno);
        int count=0;
        if (map!=null)count=setting.StrToInt(map.get("sl")+"");
        return "{\"count\":"+count+"}";
    }
    public  void updatebillstatus(String billno)
    {
        String status="待开票",dealerno="";
        int is_elec_invoice=0;
        String    sqlstring="select dealerno  from bill where billno=?";
        Map<String,Object>map=queryForMap(jdbcTemplate,sqlstring,billno);
        if (map!=null)
        {
            dealerno= setting.NUllToSpace(map.get("dealerno"));
        }
     /*   sqlstring="select is_elec_invoice from dealer where dealerno=?";
        map=queryForMap(jdbcTemplate,sqlstring,dealerno);
        if (map!=null)
        {
            is_elec_invoice=setting.StrToInt(map.get("is_elec_invoice")+"");
        }
        if (is_elec_invoice==1)status="已开票";*/
        sqlstring="select id from billdetail where billno=? and (confirm is null or confirm=0)";

        map=queryForMap(jdbcTemplate,sqlstring,billno);
        if (map==null)
        {
            int tt=setting.GetCurrenntTime();
            sqlstring="update bill set status='"+status+"',confirmtime= "+tt+" where billno=?";
            jdbcTemplate.update(sqlstring,billno);
            sqlstring="update contract set status='"+status+"' where contractno in (select contractno from billdetail where billno=?) and (status!='已取消')";
            jdbcTemplate.update(sqlstring,billno);
            //摩托车业务没有更新凭证的状态
            if (domainenter.equals("1"))
            {
                sqlstring="update mt_contract set status='"+status+"' where contractno in (select contractno from billdetail where billno=?) and (status!='已取消')";
                jdbcTemplate.update(sqlstring,billno);
            }
        }
    }
    public String bill_down(String data){
        String billno="",dealerno="",status="",dealername="",contractno="",brand="";
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
        if (jsonObject.keySet().contains("status"))status=jsonObject.get("status").getAsString();
        if (jsonObject.keySet().contains("dealername"))dealername=jsonObject.get("dealername").getAsString();
        if (jsonObject.keySet().contains("brand"))brand=jsonObject.get("brand").getAsString();
        String sql ="";


        sql="select (@i:=@i+1)   as   i,bill.*,dealer.dealername,dealer.brand,FROM_UNIXTIME(bill.confirmtime,'%Y-%m-%d %H:%i:%s') as ctime,FROM_UNIXTIME(bill.invoicetime,'%Y-%m-%d %H:%i:%s') as invoicetime1,FROM_UNIXTIME(bill.ttime,'%Y-%m-%d %H:%i:%s') as createtime,dealer.is_elec_invoice,(select count(*) from billdetail where billno=bill.billno and amount<0 ) as backcount,if (bill.invoiceurl is null,'否','是') as elect from bill inner join dealer on dealer.dealerno=bill.dealerno ,(SELECT @i:=0) as i  where 1=1";

        List<Object> queryList=new ArrayList<Object>();
        if (!billno.equals(""))
        {
            sql+=" and bill.billno=?";
            queryList.add(billno);
        }
        if (!status.equals(""))
        {
            sql+=" and bill.status=?";
            queryList.add(status);
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
            sql+=" and bill.dealerno=?";
            queryList.add(dealerno);
        }
        if (time1!=0)
        {
            sql+=" and bill.ttime>?";
            queryList.add(time1);
        }
        if (time2!=0)
        {
            sql+=" and bill.ttime<?";
            queryList.add(time2);
        }
        if (!contractno.equals(""))
        {
            sql+=" and billno in (select billno from billdetail where contractno=?) ";
            queryList.add(contractno);
        }
        Map<String,Object> title=new LinkedHashMap<>();
        title.put("i","No. 序号");
        title.put("billno","Bill No. 账单编号");
        if (domainenter.equals("3"))
        {
            title.put("brand","Brand 品牌");
        }
        title.put("dealername","Dealer Name 经销商名称");
        title.put("dealerno","Dealer Code 经销商代码");
        title.put("createtime","Bill Date 账单日期");
        title.put("confirmquality","Approved Contract No. 已批准合同数");
        title.put("refusequality","Rejected Contract No. 已拒绝合同数");
        title.put("status","Bill Status 账单状态");
        title.put("confirmamount","账单金额 Bill Amount");
        title.put("payamount","Paid Amount 支付金额");
        if (dealerno.equals("")) title.put("invoiceno","Invoice No. 发票号");
        title.put("invoicetime1","Invoice Date 开票日期");//
        title.put("drawerName","drawerName 开票人");
        title.put("elect","Electronic invoice 是否电子发票");
        title.put("invoicetype","Invoice type 发票类型");
        if (domainenter.equals("0")) {

            title.put("ctime", "Confirm Date确认日期");
        }else if (domainenter.equals("1")||domainenter.equals("2"))
        {
            title.put("product", "Product产品");
        }
            result= toexcel.createCSVFile(queryForList(jdbcTemplate,sql,queryList.toArray()),title,"","");
        return  result;
    }
    public String billdetail_down(String data){
        String billno="",dealerno="",status="",dealername="",contractno="",brand="";
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

        String sql ="";
        if (!billno.equals(""))
        {
            sql="select contractno from billdetail where 1=1 and billno=? limit 1";
            Map<String,Object>map=queryForMap(jdbcTemplate,sql,billno);
            if (map!=null&&map.keySet().contains("contractno"))
            {
                if (map.get("contractno").toString().startsWith("MT"))
                    brand="Motor";
            }
        }


        sql="select (@i:=@i+1)   as   i,billdetail.*,FROM_UNIXTIME(billdetail.ttime,'%Y-%m-%d %H:%i:%s') as createtime,FROM_UNIXTIME(billdetail.confirmtime,'%Y-%m-%d %H:%i:%s') as ctime    ";
        sql+=" ,c.cname,c.vin,c.Businessinsurancepolicyno,c.status,c.pname";
        if (brand.equals("Motor"))
            sql+=",c.brand from billdetail left join mt_contract c on billdetail.contractno=c.contractno ,(SELECT @i:=0) as i where 1=1";
        else
        sql+=",IFNULL(c.brand ,'Motor') as brand  from billdetail left join contract c on billdetail.contractno=c.contractno ,(SELECT @i:=0) as i where 1=1";

        List<Object> queryList=new ArrayList<Object>();
        if (!billno.equals(""))
        {
            sql+=" and billdetail.billno=?";
            queryList.add(billno);
        }
        if (!brand.equals(""))
        {
            sql+=" and c.brand=?";
            queryList.add(brand);
        }
        if (!dealerno.equals("")&&!dealerno.equals("zb"))
        {
            sql+=" and billdetail.dealerno=?";
            queryList.add(dealerno);
        }
        if (time1!=0)
        {
            sql+=" and billdetail.ttime>?";
            queryList.add(time1);
        }
        if (time2!=0)
        {
            sql+=" and billdetail.ttime<?";
            queryList.add(time2);
        }
        if (!contractno.equals(""))
        {
            sql+=" and billdetail.contractno=? ";
            queryList.add(contractno);
        }
        Map<String,Object> title=new LinkedHashMap<>();
        title.put("i","No. 序号");
        title.put("billno","Bill No. 账单编号");
        title.put("contractno","Contract No 凭证号");
        title.put("brand","Brand 品牌");
        title.put("dealerno","Dealer Code 经销商代码");
        title.put("createtime","Bill Date 账单日期");

        title.put("amount","Bill Amount 账单金额 ");
        if (domainenter.equals("0"))
        {

            title.put("ctime","Confirm Date确认日期");

            title.put("Businessinsurancepolicyno","Commercial Ins. No. 商业保单号");
            title.put("cname","Customer Name 客户姓名");
            title.put("vin","VIN");
            title.put("pname","Service Type 产品类别");
            title.put("status","Contract Status 凭证状态");

        }

        result= toexcel.createCSVFile(queryForList(jdbcTemplate,sql,queryList.toArray()),title,"","");
        return  result;
    }
    public String billpaycheck(String data)
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
              if (domainenter.equals("1")||domainenter.equals("2"))return "{\"errcode\":\"0\",\"salebrand\":\""+salebrand+"\"}";
        }

        sql ="select ttime from bill where status!='已提交支付证明'  and status!='已支付' and dealerno=? and ttime<"+(setting.GetCurrenntTime()-25*24*60*60)+" order by ttime";

        int ttime=0;


        String resultss="";
        map=queryForMap(jdbcTemplate,sql,dealerno);
        if (map==null) {
            resultss = GetErrorString(0, "");
        }
        else {
            ttime=setting.StrToInt(map.get("ttime")+"");
            if (ttime<(setting.GetCurrenntTime()-40*24*60*60))
                resultss = GetErrorString(0, "");
                // resultss = GetErrorString(1, "您有账单尚未付款，完成付款后才能继续使用销售模块！");
            else {
                ttime=ttime+25*24*60*60;
                resultss = GetErrorString(3, "您有账单尚未付款，请尽快付款！如"+stampToDate("yyyy-MM-dd",ttime)+"仍未付款，您将无法使用本系统。");
            }
        }
        return  resultss;
    }
    public String adminbillpay(String data)
    {
        String paycheckuser="",paycheckremark="",billno="";
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

        if (jsonObject.keySet().contains("paycheckuser"))paycheckuser=jsonObject.get("paycheckuser").getAsString();else return  GetErrorString(1,"");
        if (jsonObject.keySet().contains("billno"))billno=jsonObject.get("billno").getAsString();else return  GetErrorString(1,"");

        if (jsonObject.keySet().contains("paycheckremark"))paycheckremark=jsonObject.get("paycheckremark").getAsString();

        String sql ="update bill set status='已支付',paycheckuser=?,paycheckremark=?,paychecktime="+getCurrenntTime()+",paytype='线下支付' where  billno=? and status='已提交支付证明'";
        Object[] args=new Object[3];
        args[0]=paycheckuser;
        args[1]=paycheckremark;
        args[2]=billno;
        int ii=jdbcTemplate.update(sql,args);
        sql="update contract set status='已支付' where contractno in (select contractno from billdetail where billno=? ) and  status!='已取消'";
        jdbcTemplate.update(sql,billno);
       String resultss="";
       if (ii==1)resultss=GetErrorString(0,"设置账单支付成功！");
       else resultss=GetErrorString(3,"账单状态错误，设置账单支付不成功！");
        return  resultss;
    }
}
