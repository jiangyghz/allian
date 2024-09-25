package com.bond.allianz.service;

import com.bond.allianz.Dao.ActDao;
import com.bond.allianz.Dao.BillDao;
import com.bond.allianz.Dao.ContractDao;
import com.bond.allianz.Dao.invoice;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class BillService {
    @Autowired
    private BillDao billDao;
    @Autowired
    invoice invoice1;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    /**
     * 根据账单no 查询
     * @param billno
     * @return
     */
    public  Map<String,Object> selectByKey(String billno){
        return  billDao.selectByKey(billno);
    }
    /**
     * 修改状态
     * @param billno
     * @param status
     * @return
     */
    public  int updateStateByKey(String billno,String status,float payamount,String paytime,String payorderno){
        return  billDao.updateStateByKey(billno,status,payamount,paytime,payorderno);
    }
    public String manualBill(String data){return billDao.manualBill(data);}
    public String billdetail_down(String data){return billDao.billdetail_down(data);}

    public long getlassbilltime(){return billDao.getlassbilltime();}
    public int updatelassbilltime(){return billDao.updatelassbilltime();}
    public String uploadpayment(String data,MultipartFile file){return billDao.uploadpayment(data, file);}
    public String getBill(String data){return billDao.getBill(data);}
    public String getNotinBillContract(String data){return billDao.getNotinBillContract(data);}
    public String getBilldetail(String data){return billDao.getBilldetail(data);}
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
        result=billDao.checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;


        if (jsonObject.keySet().contains("billno"))billno=jsonObject.get("billno").getAsString();else return billDao.GetErrorString(1,"");
        if (jsonObject.keySet().contains("userid"))userid=jsonObject.get("userid").getAsString();
        String sql ="";
        int tt=billDao.getCurrenntTime();

        sql="update billdetail set confirm=1,confirmtime="+tt+",confirmuserid=? where (billno=?) and (confirm is null)";
        String resultss="";
        Object[] parm=new Object[2];
        parm[0]=userid;
        parm[1]=billno;
        Map<String,Object>map;
        if (jdbcTemplate.update(sql,parm)>0){
            String dealerno="";
            sql="select  dealerno from bill where billno=?";
            map=billDao.queryForMap(jdbcTemplate,sql,billno);
            if (map!=null)
            {

                dealerno=map.get("dealerno")+"";
                try
                {
                //    invoice1.GetInvoice(dealerno,billno);
                }catch (Exception e)
                {

                }

            }

            resultss=billDao.GetErrorString(0,"");
        }
        else
            resultss=billDao.GetErrorString(3,"提交不成功！");
        sql="select sum(amount) as amount,count(id)  as sl,confirm from billdetail where billno=? group by confirm";
        List<Map<String,Object>>list=billDao.queryForList(jdbcTemplate,sql,billno);
        int refusequality=0,confirmquality=0;
        float confirmamount=0;
        for (int i=0;i<list.size();i++)
        {
            map=list.get(i);
            if ((map.get("confirm")+"").equals("0"))refusequality=billDao.strToShortInt(map.get("sl")+"");
            if ((map.get("confirm")+"").equals("1"))
            {
                confirmquality=billDao.strToShortInt(map.get("sl")+"");
                confirmamount=billDao.nullToZero(map.get("amount")+"");
            }

        }
        sql="update bill set confirmamount=?,confirmquality=?,refusequality=? where billno=?";
        Object[] pararms=new Object[4];
        pararms[0]=confirmamount;
        pararms[1]=confirmquality;
        pararms[2]=refusequality;
        pararms[3]=billno;
        jdbcTemplate.update(sql,pararms);
        billDao.updatebillstatus(billno);
        return  resultss;
    }
    public String getSign(String data){
        String billno="";

        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String result="",userid="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))data=jsonObject.get("data").toString();
        //       data=jsonObject.get("data").toString();
        result=billDao.GetSign(nonce,time,appid,"Rtogw8oz8gdoRY4KHfzzu5toSWIqPqVn",data);

        return  result;
    }
    public String cantractBill(String data){
        String contractno="",invoiceno="",invoiceurl="",billno="";
        String userid="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=billDao.checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;
        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();else return  billDao.GetErrorString(1,"");
        if (jsonObject.keySet().contains("userid"))userid=jsonObject.get("userid").getAsString();else return  billDao.GetErrorString(1,"");
       String resultss="";
        resultss=invoice1.GetContractInvoice(contractno);
        jsonObject = new JsonParser().parse(resultss).getAsJsonObject();
        if (jsonObject.keySet().contains("invoiceno"))invoiceno=jsonObject.get("invoiceno").getAsString();
        if (jsonObject.keySet().contains("invoiceurl"))invoiceurl=jsonObject.get("invoiceurl").getAsString();
        if (jsonObject.keySet().contains("billno"))billno=jsonObject.get("billno").getAsString();
        if (invoiceno.equals(""))
        {
            if (!resultss.equals(""))
            {
                jsonObject = new JsonParser().parse(resultss).getAsJsonObject();
                  if (jsonObject.keySet().contains("msg"))resultss= billDao.GetErrorString(3,jsonObject.get("msg").getAsString());
            }
            return  resultss;
        }
        if (!billno.equals(""))
        {
            resultss="{\"errcode\":\"0\",\"billno\":\""+billno+"\",\"contractno\":\""+contractno+"\",\"invoiceurl\":\""+invoiceurl+"\"}";

            return  resultss;
        }

        resultss=billDao.cantractBill(contractno,userid,invoiceno,invoiceurl);

        return  resultss;
    }
    public String refuseBilldetail(String data){return billDao.refuseBilldetail(data);}
    public String autoBill(String data)

    {
        int time1=0, time2=0;
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        if (jsonObject.keySet().contains("time1"))time1=jsonObject.get("time1").getAsInt();
        if (jsonObject.keySet().contains("time2"))time2=jsonObject.get("time2").getAsInt();

        result=billDao.checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;

          result=  billDao.autoBill(time1,time2);
       // invoice1.AutoInvoice();
        return result;
    }
    public String autoBill_motor(String data)

    {
        int time1=0, time2=0;
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        if (jsonObject.keySet().contains("time1"))time1=jsonObject.get("time1").getAsInt();
        if (jsonObject.keySet().contains("time2"))time2=jsonObject.get("time2").getAsInt();

        result=billDao.checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;

        result=  billDao.autoBill_motor(time1,time2);
        // invoice1.AutoInvoice();
        return result;
    }
    public String autoBill1()
    {
      /*  Random r = new Random(1);
        int ran1 = r.nextInt(100*12);
        try {
            Thread.sleep(ran1*1000);//20分钟内随机休眠、每1秒一个随机数
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
       String  result=billDao.autoBill(0,0);
      //  invoice1.AutoInvoice();
        return result;
    }

    public String getUnconfirmBillCount(String data){return billDao.getUnconfirmBillCount(data);}
    public void autoBillmail() {

       /* Random r = new Random(1);
        int ran1 = r.nextInt(60000*5);
        try {

            Thread.sleep(ran1);//5分钟内随机休眠
        } catch (InterruptedException e) {
            e.printStackTrace();
        }*/
        billDao.autoBillmail();
    }
    public String getBillPage(String data){return billDao.getBillPage(data);}
    public String updatebillinvoiceno(String data){return billDao.updatebillinvoiceno(data);}
    public String bill_down(String data){return billDao.bill_down(data);}
    public String billpaycheck(String data){return billDao.billpaycheck(data);}
    public String adminbillpay(String data){return billDao.adminbillpay(data);}
}
