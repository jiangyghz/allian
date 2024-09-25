package com.bond.allianz.service;

import com.bond.allianz.Dao.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
public class MtContractService {
    @Autowired
    private MtContractDao mtContractDao;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private BmwDao bmwDao;
    @Autowired
    private PayRequestDao payRequestDao;
    @Autowired
    private BmwPrintDao bmwPrintDao;
    @Autowired
    invoice invoice1;
    public String selectContractAll(String data){return mtContractDao.selectContractAll(data);}
    public String addorUpdateContract(String data){return mtContractDao.addorUpdateContract(data);}
    public String vincheck(String data){return mtContractDao.vincheck(data);}
    public String uploadinsurance(String data, MultipartFile file){return mtContractDao.uploadinsurance(data,file);}
    public String uploadInvoice(String data, MultipartFile file){return mtContractDao.uploadInvoice(data,file);}
    public String submitcontract(String data){return mtContractDao.submitcontract(data);}
    public String deletecontract(String data){return mtContractDao.deletecontract(data);}
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
        result=mtContractDao.checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;

        String status="",newstatus="";
        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();else return  bmwDao.GetErrorString(1,"");
        if (jsonObject.keySet().contains("userid"))userid=jsonObject.get("userid").getAsString();
        if (jsonObject.keySet().contains("cancelremark"))cancelremark=jsonObject.get("cancelremark").getAsString();
        if (jsonObject.keySet().contains("backamount"))backamount= setting.NullToZero(jsonObject.get("backamount").getAsString());
        String sql ="",dealerno="",brand="",claimstatus="";
        int tt=mtContractDao.getCurrenntTime();
        float agentprice=0;
        sql="select status,dealerno,agentprice,brand,claimstatus from  mt_contract where contractno=?";
        Map<String ,Object> map=mtContractDao.queryForMap(jdbcTemplate,sql,contractno);
        if (map==null)return mtContractDao.GetErrorString(3,"合同号不存在！");
        else {
            status=map.get("status")+"";
            brand=map.get("brand")+"";
            dealerno=map.get("dealerno")+"";
            claimstatus=map.get("claimstatus")+"";
            agentprice=setting.NullToZero(map.get("agentprice")+"");
        }


        newstatus="已取消";
        if (status.equals("草稿"))return  mtContractDao.GetErrorString(3,"合同没有提交，不需要取消！");
        if (claimstatus.equals("理赔已结案"))return  mtContractDao.GetErrorString(3,"理赔已结案，不能取消！");
        if (claimstatus.equals("补充材料"))return  mtContractDao.GetErrorString(3,"理赔补充材料中，不能取消！");
        if (claimstatus.equals("理赔预审已批准"))return  mtContractDao.GetErrorString(3,"理赔预审已批准，不能取消！");
        sql="select * from mt_claim where status!='草稿' and status!='理赔已拒绝' and status!='理赔预审已拒绝' and   contractno=?";
        map=mtContractDao.queryForMap(jdbcTemplate,sql,contractno);
        if (map!=null)return mtContractDao.GetErrorString(3,"该凭证有理赔，不能取消！");

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

        sql="update  mt_contract set  canceltime=?,cancelusername=?,cancelremark=?  where contractno=?";
        queryList=new Object[4];
        queryList[0]=mtContractDao.GetNowDate("yyyy-MM-dd HH:mm:ss");
        queryList[1]=userid;
        queryList[2]=cancelremark;
        queryList[3]=contractno;
        jdbcTemplate.update(sql,queryList);
        if (!status.equals("待支付")&&!status.equals("草稿")&&!status.equals("已取消")) {

            resultss = payback(dealerno, backamount, userid, contractno);
        }
        else resultss=bmwDao.GetErrorString(3,"凭证不是已支付状态，不能取消！");



        return  resultss;
    }
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


        if (tradechanel.equals("余额支付"))
        {
            bmwDao.addBmwbill( dealerno, amount, userid, "退款", contractno,1, "余额支付", "", "");
            String billno=bmwDao.getcantractbillno(contractno,amount);
            sql="update  mt_contract set status=?,backamount=? where contractno=?";
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
            sql = "insert into billdetail (id,contractno,amount,ttime,dealerno) values (uuid(),?,?," + tt + ",?)";
            queryList=new Object[3];
            queryList[0] = contractno;
            queryList[1] =0- amount;
            queryList[2] = dealerno;
            jdbcTemplate.update(sql, queryList);
        }
        else
        {
            sql="update  mt_contract set  backamount=? where contractno=?";
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

    public String contract_down(String data){return mtContractDao.contract_down(data);}
    public String contractPrint(String data){return bmwPrintDao.contractPrintMotor(data);}
}
