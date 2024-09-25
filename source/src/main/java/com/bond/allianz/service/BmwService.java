package com.bond.allianz.service;


import com.bond.allianz.Dao.BmwDao;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BmwService {
    @Autowired
    private BmwDao bmwDao;
    public String getBillBmw(String data){return bmwDao.getBillBmw(data);}
    public String addPayrequest(String data){return bmwDao.addPayrequest(data);}
    public String getSalebrand(String data){return bmwDao.getSalebrand(data);}

    public  String paysuccess(String id,float amount,String tradechanel,String chanelno,String remark)
    {
        return bmwDao.paysuccess( id, amount, tradechanel, chanelno, remark);
    }
    public String billbmw_down(String data){return bmwDao.billbmw_down(data);}
    public String payment_down(String data){return bmwDao.payment_down(data);}
    public String balancepay(String data){return bmwDao.balancepay(data);}
    public String getBalance(String data){return bmwDao.getbalancestring(data);}
    public float getBalanceBydealerno(String dealerno){
        return bmwDao.getBalance(dealerno);
    }


    public void paybacksuccess(String contractno)
    {
        bmwDao.paybacksuccess(contractno);
    }
    public String updatebillinvoice(String data)
    {
        return bmwDao.updatebillinvoice(data);
    }

    public String addbillmanul(String data)
    {
        //String dealerno,float amount,String userid,String tradetype,String contractno,int in_or_out,String tradechanel,String chanelno,String remark
        String dealerno="",tradetype="",chanelno="",userid="",remark="";
        int in_or_out=0;
        String tradechanel="线下支付";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String result="";
        float amount=0;
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=bmwDao.checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;

        if (jsonObject.keySet().contains("chanelno"))chanelno=jsonObject.get("chanelno").getAsString();
        if (jsonObject.keySet().contains("dealerno"))dealerno=jsonObject.get("dealerno").getAsString();else return bmwDao.GetErrorString(1,"");
        if (jsonObject.keySet().contains("remark"))remark=jsonObject.get("remark").getAsString();
        if (jsonObject.keySet().contains("tradetype"))tradetype=jsonObject.get("tradetype").getAsString();

        if (jsonObject.keySet().contains("userid"))userid=jsonObject.get("userid").getAsString();
        if (jsonObject.keySet().contains("amount"))amount=jsonObject.get("amount").getAsFloat();
        if (tradetype.equals("充值")) {
            in_or_out = 1;
            if (bmwDao.getBalance(dealerno)+amount>200000)return bmwDao.GetErrorString(3,"余额不能大于20万，不能充值！");
        }else if (tradetype.equals("退款"))
        {
            in_or_out=-1;
            if (bmwDao.getBalance(dealerno)<amount) return bmwDao.GetErrorString(3,"退款金额大于余额，不能退款！");
        }

        if (bmwDao.addBmwbill(dealerno,amount,userid,tradetype,"",in_or_out,tradechanel,chanelno,remark)==1)
            result=bmwDao.GetErrorString(0,"");
        else result=bmwDao.GetErrorString(3,"添加失败！");
        return result;
    }
    public String getsystemremind(String data)
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
        result=bmwDao.checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;

        if (jsonObject.keySet().contains("dealerno"))dealerno=jsonObject.get("dealerno").getAsString();else return bmwDao.GetErrorString(1,"");


        return bmwDao.getsystemremind(dealerno);
    }

}
