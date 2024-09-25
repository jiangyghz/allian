package com.bond.allianz.service;

import com.bond.allianz.Dao.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.sf.jsqlparser.expression.DateTimeLiteralExpression;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class ContractService  {
    @Autowired
    private ContractDao contractDao;
    @Autowired
    private BmwDao bmwDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    private BmwPrintDao bmwPrintDao;
    @Autowired
    invoice invoice1;
    @Value("${domain.enter}")
    public String domainenter;
    public String selectContractAll(String data){return contractDao.selectContractAll(data);}
    public String vincheck(String data){return contractDao.vincheck(data);}
    public String addorUpdateContract(String data){return contractDao.addorUpdateContract(data);}
    public String uploadinsurance(String data, MultipartFile file){return contractDao.uploadinsurance(data,file);}
    public String uploadJpg(String data, MultipartFile file){return contractDao.uploadJpg(data,file);}

    public String uploadInvoice(String data,MultipartFile file){return contractDao.uploadInvoice(data,file);}
    public String submitcontract(String data){return contractDao.submitcontract(data);}
    public String checkcontract(String data){return contractDao.checkcontract(data);}
    public String checkcontractDaimler(String data){return contractDao.checkcontractDaimler(data);}

    public String deletecontract(String data){return contractDao.deletecontract(data);}
    public String getbrand(String data){return contractDao.getbrand(data);}
    public String getcars(String data){return contractDao.getcars(data);}
    public String getvehicletype(String data){return contractDao.getvehicletype(data);}
    public String cancelcontract(String data){
        String contractno="";
        String userid="",cancelremark="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        int multi=0;
        String result="";
        float backamount=0;
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=bmwDao.checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;

        String status="",newstatus="";
        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();else return  bmwDao.GetErrorString(1,"");
        if (jsonObject.keySet().contains("userid"))userid=jsonObject.get("userid").getAsString();
        if (jsonObject.keySet().contains("cancelremark"))cancelremark=jsonObject.get("cancelremark").getAsString();
        if (jsonObject.keySet().contains("backamount"))backamount=setting.NullToZero(jsonObject.get("backamount").getAsString());
        String sql ="",dealerno="",brand="",claimstatus="";
        int tt=bmwDao.getCurrenntTime();
        float agentprice=0;
        sql="select status,dealerno,agentprice,brand,claimstatus from  contract where contractno=?";
        Map<String ,Object>map=bmwDao.queryForMap(jdbcTemplate,sql,contractno);
        if (map==null)return bmwDao.GetErrorString(3,"合同号不存在！");
        else {
            status=map.get("status")+"";
            brand=map.get("brand")+"";
            dealerno=map.get("dealerno")+"";
            claimstatus=map.get("claimstatus")+"";
            agentprice=setting.NullToZero(map.get("agentprice")+"");
        }


        newstatus="已取消";
        if (status.equals("草稿"))return  bmwDao.GetErrorString(3,"合同没有提交，不需要取消！");
        if (claimstatus.equals("理赔已结案"))return  bmwDao.GetErrorString(3,"理赔已结案，不能取消！");
        if (claimstatus.equals("补充材料"))return  bmwDao.GetErrorString(3,"理赔补充材料中，不能取消！");
        if (claimstatus.equals("理赔预审已批准"))return  bmwDao.GetErrorString(3,"理赔预审已批准，不能取消！");
        sql="select * from claim where status!='草稿' and status!='理赔已拒绝' and status!='理赔预审已拒绝' and   contractno=?";
        map=bmwDao.queryForMap(jdbcTemplate,sql,contractno);
        if (map!=null)return bmwDao.GetErrorString(3,"该凭证有理赔，不能取消！");
        sql="insert into contractcheck (tid,contractno,checkcontent,userid,oldstatus,newstatus,ttime,remark) values (uuid(),?,'合同取消',?,?,'"+newstatus+"',"+tt+",?)";
        Object[] queryList=new Object[4];
        queryList[0]=contractno;
        queryList[1]=userid;
        queryList[2]=status;
        queryList[3]=cancelremark;
        jdbcTemplate.update(sql,queryList);

        String resultss="";
        if (backamount==0)backamount=agentprice;
       // if (status.equals("已批准")&&brand.equals("Porsche"))
           // if (brand.equals("Porsche"))
            if (domainenter.equals("0"))
        {
            if (backamount!=0)
            {
                sql="insert into billdetail (id,contractno,amount,ttime,dealerno) values (uuid(),?,?,"+tt+",?)";
                queryList=new Object[3];
                queryList[0]=contractno;
                queryList[1]=0-backamount;
                queryList[2]=dealerno;
                jdbcTemplate.update(sql,queryList);
            }

            sql="update  contract set status=?,backamount=? where contractno=?";
            queryList=new Object[3];
            queryList[0]=newstatus;
            queryList[1]=backamount;
            queryList[2]=contractno;
            if (jdbcTemplate.update(sql,queryList)==1)resultss="{\"errcode\":\"0\",\"contractno\":\""+contractno+"\"}";
            else resultss=bmwDao.GetErrorString(3,"提交不成功！");
        }
       else
        {
            if (!status.equals("待支付")&&!status.equals("草稿")&&!status.equals("已取消")) {

                resultss = payback(dealerno, backamount, userid, contractno);
            }
            else resultss=bmwDao.GetErrorString(3,"凭证不是已支付状态，不能取消！");

        }



        return  resultss;
    }

    public String getdealer(String data){return contractDao.getdealer(data);}
    public String newContract(String data){return contractDao.newContract(data);}
    public String addContractRemark(String data){return contractDao.addContractRemark(data);}
    public String addContractremainingamount(String data){return contractDao.addContractremainingamount(data);}

    public String contractPrint(String data){
        String allss="";
        allss=contractDao.contractPrintPorsche(data);
        return allss;
    }
    public  int updateStateByBillno(String billno){
        return contractDao.updateStateByBillno(billno);
    }
    public String contractPrintPorsche(String data){return contractDao.contractPrintPorsche(data);}
    public String contractPrintBmw(String data){return bmwPrintDao.contractPrintBmw(data);}
    public String contract_down(String data){return contractDao.contract_down(data);}
    public String payback(String dealerno,float amount,String userid,String contractno)
    {
        String  sql="",tradechanel="";
        sql="select tradechanel from billbmw where contractno=?";
        Map<String,Object>map=bmwDao.queryForMap(jdbcTemplate,sql,contractno);
        if (map!=null)
        {
            tradechanel= setting.NUllToSpace(map.get("tradechanel"));
        }

        Object[]  queryList;


        if (tradechanel.equals("余额支付")||amount==0)
        {
            bmwDao.addBmwbill( dealerno, amount, userid, "退款", contractno,1, "余额支付", "", "");
            String billno=bmwDao.getcantractbillno(contractno,amount);
            sql="update  contract set status=?,backamount=? where contractno=?";
            queryList=new Object[3];
            queryList[0]="已取消";
            queryList[1]=amount;
            queryList[2]=contractno;
            jdbcTemplate.update(sql,queryList);
            if (!billno.equals(""))
            {
                invoice1.InvoiceRed(dealerno,billno,userid);
                return "{\"errcode\":\"0\",\"contractno\":\""+contractno+"\"}";
            }

            int tt=setting.GetCurrenntTime();
            if (amount!=0)
            {
                sql = "insert into billdetail (id,contractno,amount,ttime,dealerno) values (uuid(),?,?," + tt + ",?)";
                queryList=new Object[3];
                queryList[0] = contractno;
                queryList[1] =0- amount;
                queryList[2] = dealerno;
                jdbcTemplate.update(sql, queryList);
            }

        }
           else
        {
            sql="update  contract set  backamount=? where contractno=?";
             queryList=new Object[2];
            queryList[0]=amount;
            queryList[1]=contractno;
            jdbcTemplate.update(sql,queryList);
            //添加退款动作
            payRequestDao.rechargeRefunds(contractno);
        }


      String  resultss="{\"errcode\":\"0\",\"contractno\":\""+contractno+"\"}";
      return resultss;

    }
    public String addorupdatemarketactivity(String data){return contractDao.addorupdatemarketactivity(data);}
    public String setmarketactivityvalid(String data){return contractDao.setmarketactivityvalid(data);}
    public String getvaildmarketactivity(String data){return contractDao.getvaildmarketactivity(data);}
    public String uploadtierpic(String data,MultipartFile file){return contractDao.uploadtierpic(data,file);}
    public String updatetierpic(String data){return contractDao.updatetierpic(data);}
    public String tierpic(String data){return contractDao.tierpic(data);}
    public String gettierdetail(String data){return contractDao.gettierdetail(data);}
    public String addorupdatetier(String data){return contractDao.addorupdatetier(data);}
}
