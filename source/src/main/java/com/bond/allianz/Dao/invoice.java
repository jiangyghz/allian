package com.bond.allianz.Dao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.text.SimpleDateFormat;
import java.util.*;


import com.bond.allianz.utils.HttpUtil;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vpiaotong.openapi.OpenApi;
import com.vpiaotong.openapi.util.Base64Util;
import com.vpiaotong.openapi.util.HttpUtils;
import com.vpiaotong.openapi.util.JsonUtil;
import com.vpiaotong.openapi.util.RSAUtil;
import com.vpiaotong.openapi.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;

@Repository
public class invoice extends BaseDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private String privateKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAIVLAoolDaE7m5oMB1ZrILHkMXMF6qmC8I/FCejz4hwBcj59H3rbtcycBEmExOJTGwexFkNgRakhqM+3uP3VybWu1GBYNmqVzggWKKzThul9VPE3+OTMlxeG4H63RsCO1//J0MoUavXMMkL3txkZBO5EtTqek182eePOV8fC3ZxpAgMBAAECgYBp4Gg3BTGrZaa2mWFmspd41lK1E/kPBrRA7vltMfPj3P47RrYvp7/js/Xv0+d0AyFQXcjaYelTbCokPMJT1nJumb2A/Cqy3yGKX3Z6QibvByBlCKK29lZkw8WVRGFIzCIXhGKdqukXf8RyqfhInqHpZ9AoY2W60bbSP6EXj/rhNQJBAL76SmpQOrnCI8Xu75di0eXBN/bE9tKsf7AgMkpFRhaU8VLbvd27U9vRWqtu67RY3sOeRMh38JZBwAIS8tp5hgcCQQCyrOS6vfXIUxKoWyvGyMyhqoLsiAdnxBKHh8tMINo0ioCbU+jc2dgPDipL0ym5nhvg5fCXZC2rvkKUltLEqq4PAkAqBf9b932EpKCkjFgyUq9nRCYhaeP6JbUPN3Z5e1bZ3zpfBjV4ViE0zJOMB6NcEvYpy2jNR/8rwRoUGsFPq8//AkAklw18RJyJuqFugsUzPznQvad0IuNJV7jnsmJqo6ur6NUvef6NA7ugUalNv9+imINjChO8HRLRQfRGk6B0D/P3AkBt54UBMtFefOLXgUdilwLdCUSw4KpbuBPw+cyWlMjcXCkj4rHoeksekyBH1GrBJkLqDMRqtVQUubuFwSzBAtlc";

    //票通公钥(票通提供)
    private  String ptPublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCJkx3HelhEm/U7jOCor29oHsIjCMSTyKbX5rpoAY8KDIs9mmr5Y9r+jvNJH8pK3u5gNnvleT6rQgJQW1mk0zHuPO00vy62tSA53fkSjtM+n0oC1Fkm4DRFd5qJgoP7uFQHR5OEffMjy2qIuxChY4Au0kq+6RruEgIttb7wUxy8TwIDAQAB";

    //3DES秘钥(票通提供)
    private   String password = "lsBnINDxtct8HZB7KCMyhWSJ";

    //请更换请求平台简称(票通提供)
    private    String platform_alias = "DEMO";

    //请更换请求平台编码(票通提供)
    private    String platform_code = "11111111";
public  invoice(JdbcTemplate jd)
{
    jdbcTemplate=jd;
}
    /**
     *
     * @title: testRSAGenerate
     * @description: RAS公钥私钥的生成   1024bit   pkcs8格式       公钥提供给票通  私钥保留
     * @throws Exception
     */

    public void testRSAGenerate() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();
        String publicKeyStr = RSAUtil.getKeyString(publicKey);
        System.out.println("publicKeyString:" + publicKeyStr);
        String privateKeyStr = RSAUtil.getKeyString(privateKey);
        System.out.println("privateKeyString:" + privateKeyStr);
    }


    /**
     *
     * @title: testRegister
     * @description: 注册接口调用
     */
    private  String taxpayerNum="";
    private  String enterpriseName="";
    private  String legalPersonName="";
    private  String contactsName="";
    private  String contactsEmail="";
    private  String contactsPhone="";
    private  String regionCode="";
    private  String cityName="";
    private  String enterpriseAddress="";
    private  String taxRegistrationCertificate="";
    private  String pathurl="";
    private String taxClassificationCode="";
    private String goodsName="";
    private String taxRateValue="";
    private String drawerName="";
    private  void getinvoiceinfo()
    {
        String sqlstring="select * from invoiceinfo";
        Map<String,Object>map=queryForMap(jdbcTemplate,sqlstring);
        if (map!=null)
        {
            privateKey=map.get("privateKey")+"";
            ptPublicKey=map.get("ptPublicKey")+"";
            password=map.get("password")+"";
            platform_alias=map.get("platform_alias")+"";
            platform_code=map.get("platform_code")+"";
            taxpayerNum=map.get("taxpayerNum")+"";
            enterpriseName=map.get("enterpriseName")+"";
            legalPersonName=map.get("legalPersonName")+"";
            contactsName=map.get("contactsName")+"";
            contactsEmail=map.get("contactsEmail")+"";
            contactsPhone=map.get("contactsPhone")+"";
            regionCode=map.get("regionCode")+"";

            cityName=map.get("cityName")+"";
            enterpriseAddress=map.get("enterpriseAddress")+"";
            taxRegistrationCertificate=map.get("taxRegistrationCertificate")+"";
            pathurl=map.get("pathurl")+"";
            taxClassificationCode=map.get("taxClassificationCode")+"";
            goodsName=map.get("goodsName")+"";
            taxRateValue=map.get("taxRateValue")+"";
            drawerName=map.get("drawerName")+"";
        }
    }
    public String Register() {
        getinvoiceinfo();
        String url = pathurl+"register.pt";
        Map<String, String> map = new HashMap<String, String>();
        map.put("taxpayerNum", platform_alias +taxpayerNum );//销方纳税人识别号
        map.put("enterpriseName", enterpriseName);//销方企业名称
        map.put("legalPersonName", legalPersonName);//法人名称
        map.put("contactsName", contactsName);//联系人名称
        map.put("contactsEmail",contactsEmail);//联系人邮箱
        map.put("contactsPhone", contactsPhone);//联系人手机号
        map.put("regionCode",regionCode);//地区编码
        map.put("cityName", cityName);//市(区)名
        map.put("enterpriseAddress", enterpriseAddress);//详细地址
        // TODO 请修改为正确的图片Base64传
        map.put("taxRegistrationCertificate",GetImageStr(taxRegistrationCertificate));//证件图片base64
        String content = JsonUtil.toJson(map);
        System.out.println(content);
        //OpenApi参数内容(3des秘钥(票通提供),平台编码(票通提供),平台前缀(票通提供),私钥)
        String buildRequest = new OpenApi(password, platform_code, platform_alias, privateKey).buildRequest(content);
        System.out.println(buildRequest);
        String response = HttpUtil.postJson(url, buildRequest);
        return  response;
    }
    public  String GetImageStr(String imgFile)
    {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理

       // imgFile=  request.getServletContext().getRealPath(imgFile);
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try
        {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        //对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);//返回Base64编码过的字节数组字符串
    }

    public static String getGuid() {

        long now = System.currentTimeMillis();
        //获取4位年份数字
        SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy");
        //获取时间戳
        String time=dateFormat.format(now);
        String info=now+"";
        //获取三位随机数
        Random rand = new Random();
        int ran=(int) rand.nextInt(900)+ 100;;
        //要是一段时间内的数据连过大会有重复的情况，所以做以下修改

        return info+ran;
    }
    public String InvoiceBlueOneItemAndPreferentialPolicy(String data) {

        String dealerno="",billno="";

        String appid="",nonce="",time="",sign="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();

        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
       String result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;
        if (jsonObject.keySet().contains("dealerno"))dealerno=jsonObject.get("dealerno").getAsString();else return GetErrorString(1,"");
        if (jsonObject.keySet().contains("billno"))billno=jsonObject.get("billno").getAsString();else return GetErrorString(1,"");
      String response="";
      try
      {
          response=GetInvoice(dealerno,billno);
      }catch (Exception e1)
        {
            response=e1.toString();
            logs.WriteLog(e1.toString(),"GetInvoice");
        }

        return  response;
    }
    public void updateInvoiceNo()
    {
        int tt=getCurrenntTime()-15*24*60*60;
      //  tt=0;
        String sqlstring="select billno,invoiceno,invoicerequestno from  bill where invoiceno!='' and invoiceurl!='' and (invoicerequestno='' or invoicerequestno is null) and ttime>"+tt;
        List<Map<String,Object>>list=queryForList(jdbcTemplate,sqlstring);
        if (list==null)return;
        logs.WriteLog(list.size()+"条","updateInvoiceNo");
        String billno="",invoiceno="",id="";
        String    invoiceCode="";
        String  invoiceNo="";
        String invoicerequestno="";
        Map<String,Object> map;
        Object[] parms;
        for (int i=0;i<list.size();i++)
        {
            invoiceNo="";
            map=list.get(i);
            if (map.get("billno")!=null) billno=map.get("billno")+"";else continue;
            if (map.get("invoiceno")!=null) {
                invoicerequestno = map.get("invoiceno") + "";


                try
                {
                    invoiceNo=   queryInvoice(invoicerequestno);
                }
                catch (Exception ee){}
                if (invoiceNo.equals(""))continue;
                sqlstring="update bill set invoiceno=?,invoicerequestno=? where billno=? ";
                parms=new Object[3];
                parms[0]=invoiceNo;
                parms[1]=invoicerequestno;
                parms[2]=billno;
                jdbcTemplate.update(sqlstring,parms);

            }

        }
    }
    public String  updateInvoiceNoBillno(String billno)
    {

        String sqlstring="select billno,invoiceno,invoicerequestno from  bill where billno=?";
        Map<String,Object>map=queryForMap(jdbcTemplate,sqlstring,billno);

        String  invoiceNo="";
        String invoicerequestno="";
      if (map!=null) {
          if (map.get("billno") != null) billno = map.get("billno") + "";
          else return "";
          if (map.get("invoiceno") != null) {
              invoicerequestno = map.get("invoiceno") + "";
              try {
                  invoiceNo = queryInvoice(invoicerequestno);
              } catch (Exception ee) {
              }
              if (invoiceNo.equals("")) return "";
              sqlstring = "update bill set invoiceno=?,invoicerequestno=? where billno=? ";
              Object[]    parms = new Object[3];
              parms[0] = invoiceNo;
              parms[1] = invoicerequestno;
              parms[2] = billno;
              jdbcTemplate.update(sqlstring, parms);

          }
      }
      return  invoiceNo;
    }
    public String queryInvoice(String invoiceReqSerialNo) {
        String url = pathurl+"queryInvoice.pt";
        String invoiceCode="",invoiceNo1="";
        String content = "{ \"taxpayerNum\": \""+taxpayerNum+"\", \"invoiceReqSerialNo\": \""+invoiceReqSerialNo+"\"}";
        //OpenApi参数内容(3des秘钥(票通提供),平台编码(票通提供),平台前缀(票通提供),私钥)
        String buildRequest = new OpenApi(password, platform_code, platform_alias, privateKey).buildRequest(content);
        // System.out.println(buildRequest);
        String response = HttpUtil.postJson(url, buildRequest);
        // System.out.println(response);
        if (response.equals(""))return "";
        JsonObject   jsonObject = new JsonParser().parse(response).getAsJsonObject();
        if (jsonObject.keySet().contains("content"))
        {
            String str= SecurityUtil.decrypt3DES(password, jsonObject.get("content").getAsString());
            logs.WriteLog(str,"queryInvoice");
            jsonObject= new JsonParser().parse(str).getAsJsonObject();
            if (jsonObject.keySet().contains("invoiceCode"))invoiceCode=jsonObject.get("invoiceCode").getAsString();
            if (jsonObject.keySet().contains("invoiceNo"))invoiceNo1=jsonObject.get("invoiceNo").getAsString();
        }
        //System.out.println(new OpenApi(password, platform_code, platform_alias, privateKey).disposeResponse(response, ptPublicKey));
       return invoiceCode+invoiceNo1;
    }
    public String GetContractInvoice(String contractno) {
        String dealerno="";
        getinvoiceinfo();
        String buyerName="",buyerTaxpayerNum="",invoiceno="",invoiceurl="",billno="";
        float quantity=1,unitPrice=0,invoiceAmount=0;
         String sqlstring="select confirmamount,confirmquality,invoiceno,invoiceurl,invoicetime,contractquality,amount,billno from bill where  billno in (select billno from billdetail where contractno=?)";
        Map<String,Object>map1=queryForMap(jdbcTemplate,sqlstring,contractno);
        if (map1!=null)
        {

            if (map1.get("invoiceno")!=null) invoiceno=map1.get("invoiceno")+"";
            if (map1.get("invoiceurl")!=null)  invoiceurl=map1.get("invoiceurl")+"";
            if (map1.get("billno")!=null)  billno=map1.get("billno")+"";
        }
        if (!invoiceno.equals(""))
        {
            return "{\"billno\":\""+billno+"\",\"invoiceno\":\""+invoiceno+"\",\"invoiceurl\":\""+invoiceurl+"\"}";
        }

        sqlstring="select agentprice,dealerno from contract where contractno=?";
        map1=queryForMap(jdbcTemplate,sqlstring,contractno);
        if (map1!=null)
        {
            invoiceAmount=nullToZero( map1.get("agentprice")+"");
            dealerno=map1.get("dealerno")+"";
        }
        sqlstring="select invoiceheading,vatno,is_elec_invoice,brand,bank,invoiceaddress,bankacount,invoicetel from dealer where dealerno=?";
        int is_elec_invoice=0;
        String brand="",buyerAddress="",buyerTel="",buyerBankName="",buyerBankAccount="";
        map1=queryForMap(jdbcTemplate,sqlstring,dealerno);
        if (map1==null)return GetErrorString(3,"开票信息不全！");
        else {
            buyerName=map1.get("invoiceheading")+"";
            brand=map1.get("brand")+"";
            buyerTaxpayerNum=map1.get("vatno")+"";
            is_elec_invoice=setting.StrToInt(map1.get("is_elec_invoice")+"");
            buyerAddress=map1.get("invoiceaddress")+"";
            buyerTel=map1.get("invoicetel")+"";
            buyerBankName=map1.get("bank")+"";
            buyerBankAccount=map1.get("bankacount")+"";
        }
        if (buyerName.equals(""))return GetErrorString(3,"发票抬头不全！");
        if (buyerTaxpayerNum.length()<10)return GetErrorString(3,"税号不全！");
        unitPrice=invoiceAmount;
      //  if (is_elec_invoice==0)return GetErrorString(3,"该企业不支持电子发票！");
        String url = pathurl+"invoiceBlue.pt";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("taxpayerNum", taxpayerNum);  //销方税号
        map.put("invoiceReqSerialNo", platform_alias + getGuid());//发票请求流水号
        map.put("buyerName", buyerName);//购买方名称
        map.put("buyerTaxpayerNum", buyerTaxpayerNum);//购买方税号(非必填,个人发票传null)
        if (!buyerAddress.equals(""))  map.put("buyerAddress", buyerAddress);
        if (!buyerTel.equals(""))  map.put("buyerTel", buyerTel);
        if (!buyerBankName.equals(""))  map.put("buyerBankName", buyerBankName);
        if (!buyerBankAccount.equals(""))  map.put("buyerBankAccount", buyerBankAccount);
        map.put("drawerName", drawerName);//"李芳娟"
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> listMapOne = new HashMap<String, String>();
        listMapOne.put("taxClassificationCode", taxClassificationCode);//税收分类编码(可以按照Excel文档填写)
        listMapOne.put("includeTaxFlag", "1");//含税标记
        listMapOne.put("quantity", String.format("%.2f", quantity)+"");//数量
        listMapOne.put("goodsName", goodsName);//货物名称
        listMapOne.put("unitPrice", String.format("%.8f", unitPrice)+"");//单价
        listMapOne.put("invoiceAmount", String.format("%.2f", invoiceAmount)+"");//金额
        listMapOne.put("taxRateValue", taxRateValue);//税率
        //listMapOne.put("taxRateAmount", String.format("%.2f", taxRateAmount)+"");//金额
        //listMapOne.put("includeTaxFlag", "1");//含税标识
        //以下为零税率开票相关参数
        //   listMapOne.put("zeroTaxFlag", "");//零税率标识(空:非零税率,0:出口零税率,1:免税,2:不征税,3:普通零税率)
        // listMapOne.put("preferentialPolicyFlag", "");//优惠政策标识(空:不使用,1:使用)   注:零税率标识传非空 此字段必须填写为"1"
        // listMapOne.put("vatSpecialManage", "");//增值税特殊管理(preferentialPolicyFlag为1 此参数必填)
        list.add(listMapOne);
        map.put("itemList", list);
        // String content = JsonUtil.toJson(map);
        Gson gson = new Gson();
        String content =   gson.toJson(map);
        String response="";
        JsonObject jsonObject;
        //OpenApi参数内容(3des秘钥(票通提供),平台编码(票通提供),平台前缀(票通提供),私钥)
        try
        {
            String buildRequest = new OpenApi(password, platform_code, platform_alias, privateKey).buildRequest(content);

            response = HttpUtil.postJson(url, buildRequest);

            jsonObject = new JsonParser().parse(response).getAsJsonObject();
            if (jsonObject.keySet().contains("content"))
            {
                String str=SecurityUtil.decrypt3DES(password, jsonObject.get("content").getAsString());
                logs.WriteLog(str,"GetInvoice");
                jsonObject= new JsonParser().parse(str).getAsJsonObject();
            }
            if (jsonObject.keySet().contains("invoiceReqSerialNo"))invoiceno=jsonObject.get("invoiceReqSerialNo").getAsString();
            if (jsonObject.keySet().contains("qrCodePath")) {
                invoiceurl = jsonObject.get("qrCodePath").getAsString();
                invoiceurl=Base64Util.decode(invoiceurl);
                response=   "{\"invoiceno\":\""+invoiceno+"\",\"invoiceurl\":\""+invoiceurl+"\"}";

            }
        }catch (Exception e1)
        {
            logs.WriteLog(e1.toString(),"GetInvoice");
        }

        return  response;
    }

    public String InvoiceRed(String dealerno,String billno,String userid) {
        getinvoiceinfo();
        String buyerName="",buyerTaxpayerNum="",invoiceno="",invoiceurl="",invoicerequestno="";
        String invoiceCode="", invoiceNo="";
        float invoiceAmount=0;
        String sqlstring="select billno,confirmamount,confirmquality,invoiceno,invoiceurl,invoicetime,contractquality,amount,invoicerequestno from bill where billno=?";
        Map<String,Object>map1=queryForMap(jdbcTemplate,sqlstring,billno);
        if (map1!=null)
        {

            invoiceAmount=0-nullToZero( map1.get("confirmamount")+"");
            invoicerequestno=setting.NUllToSpace(map1.get("invoicerequestno"));//更新发票号码
            if (invoicerequestno.equals(""))invoiceno=updateInvoiceNoBillno(billno);else invoiceno=setting.NUllToSpace(map1.get("invoiceno"));

        }
        if (!invoiceno.equals(""))
        {
            invoiceCode=invoiceno.substring(0,12);
            invoiceNo=invoiceno.substring(12);
        }else return GetErrorString(3,"蓝票号码错误！");
        sqlstring="select invoiceheading,vatno,is_elec_invoice,brand,bank,invoiceaddress,bankacount,invoicetel from dealer where dealerno=?";

        String brand="",buyerAddress="",buyerTel="",buyerBankName="",buyerBankAccount="";
        map1=queryForMap(jdbcTemplate,sqlstring,dealerno);
        if (map1==null)return GetErrorString(3,"开票信息不全！");
        else {
            buyerName=map1.get("invoiceheading")+"";
            brand=map1.get("brand")+"";
            buyerTaxpayerNum=map1.get("vatno")+"";
                buyerAddress=map1.get("invoiceaddress")+"";
            buyerTel=map1.get("invoicetel")+"";
            buyerBankName=map1.get("bank")+"";
            buyerBankAccount=map1.get("bankacount")+"";
        }
        if (buyerName.equals(""))return GetErrorString(3,"发票抬头不全！");
        if (buyerTaxpayerNum.length()<10)return GetErrorString(3,"税号不全！");
        String url = pathurl+"invoiceBlue.pt";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("taxpayerNum", taxpayerNum);//销方税号(请于要冲红的蓝票税号一致)
        // TODO 请更换请求流水号前缀
        map.put("invoiceReqSerialNo",  platform_alias + getGuid());//发票流水号 (唯一, 与蓝票发票流水号不一致)
        map.put("invoiceCode", invoiceCode);//冲红发票的发票代码
        map.put("invoiceNo", invoiceNo);//冲红发票的发票号码
        map.put("redReason", "冲红");//冲红原因
        map.put("amount", invoiceAmount);//冲红金额 (要与原发票的总金额一致)
        String content = JsonUtil.toJson(map);
        String response="";
        int tt=getCurrenntTime();
        try
        {
            String buildRequest = new OpenApi(password, platform_code, platform_alias, privateKey).buildRequest(content);

             response = HttpUtil.postJson(url, buildRequest);

            JsonObject jsonObject;

            response = HttpUtil.postJson(url, buildRequest);

            jsonObject = new JsonParser().parse(response).getAsJsonObject();
            if (jsonObject.keySet().contains("content"))
            {
                String str=SecurityUtil.decrypt3DES(password, jsonObject.get("content").getAsString());
                logs.WriteLog(str,"InvoiceRed");
                jsonObject= new JsonParser().parse(str).getAsJsonObject();
            }
            if (jsonObject.keySet().contains("invoiceReqSerialNo"))invoiceno=jsonObject.get("invoiceReqSerialNo").getAsString();
            if (jsonObject.keySet().contains("qrCodePath")) {
                invoiceurl = jsonObject.get("qrCodePath").getAsString();
                invoiceurl=Base64Util.decode(invoiceurl);
                response=   "{\"invoiceno\":\""+invoiceno+"\",\"invoiceurl\":\""+invoiceurl+"\"}";
                sqlstring="insert into bill (billno,confirmamount,confirmquality,invoiceno,invoiceurl,invoicetime,contractquality,amount,ttime,status,refusequality,payquality,payamount,paytime,userid,drawerName,invoicerequestno,invoicetype) ";
                sqlstring+=" values  (?,confirmamount,1,?,?,"+tt+",1,?,"+tt+",'已开票',0,1,?,"+tt+",?,?,?,'电子发票')";
                Object[]parms=new Object[9];
                parms[0]=billno+"-1";
                parms[1]=invoiceAmount;
                parms[2]=invoiceno;
                parms[3]=invoiceurl;
                parms[4]=invoiceAmount;
                parms[5]=invoiceAmount;
                parms[6]=userid;
                parms[7]=drawerName;
                parms[8]=invoiceno;
                jdbcTemplate.update(sqlstring,parms);
                String contractno=billno.substring(10);
               /* sqlstring="update contract set status='已取消' where contractno=? ";
                jdbcTemplate.update(sqlstring,contractno);*/
                sqlstring = "insert into billdetail (id,contractno,amount,ttime,dealerno,billno) values (uuid(),?,?," + tt + ",?,?)";
                parms = new Object[4];
                parms[0] = contractno;
                parms[1] = invoiceAmount;
                parms[2] = dealerno;
                parms[3] = billno+"-1";
                jdbcTemplate.update(sqlstring, parms);
            }
        }catch (Exception e1)
        {
            logs.WriteLog(e1.toString(),"InvoiceRed");
        }


       return response;
    }
    public String GetInvoice(String dealerno,String billno) {
        getinvoiceinfo();
        String buyerName="",buyerTaxpayerNum="",invoiceno="",invoiceurl="";
        float quantity=1,unitPrice=0,invoiceAmount=0;
        float cquantity=1,cunitPrice=0,cinvoiceAmount=0;
        String sqlstring="select confirmamount,confirmquality,invoiceno,invoiceurl,invoicetime,contractquality,amount from bill where billno=?";
        Map<String,Object>map1=queryForMap(jdbcTemplate,sqlstring,billno);
        if (map1!=null)
        {
            if (map1.get("invoiceno")!=null) invoiceno=map1.get("invoiceno")+"";
            if (map1.get("invoiceurl")!=null)  invoiceurl=map1.get("invoiceurl")+"";
            invoiceAmount=nullToZero( map1.get("confirmamount")+"");
            quantity=nullToZero( map1.get("confirmquality")+"");
            if (quantity<=0)quantity=1;
            if (quantity!=0)unitPrice=invoiceAmount/quantity;
            cinvoiceAmount=nullToZero( map1.get("amount")+"");
            cquantity=nullToZero( map1.get("contractquality")+"");
            if (cquantity!=0)cunitPrice=cinvoiceAmount/cquantity;
        }
        if (!invoiceno.equals(""))
        {
            return "{\"invoiceno\":\""+invoiceno+"\",\"invoiceurl\":\""+invoiceurl+"\"}";
        }
        sqlstring="select invoiceheading,vatno,is_elec_invoice,brand,bank,invoiceaddress,bankacount,invoicetel from dealer where dealerno=?";
        int is_elec_invoice=0;
        String brand="",buyerAddress="",buyerTel="",buyerBankName="",buyerBankAccount="";
        map1=queryForMap(jdbcTemplate,sqlstring,dealerno);
        if (map1==null)return GetErrorString(3,"开票信息不全！");
        else {
            buyerName=map1.get("invoiceheading")+"";
            brand=map1.get("brand")+"";
            buyerTaxpayerNum=map1.get("vatno")+"";
            is_elec_invoice=setting.StrToInt(map1.get("is_elec_invoice")+"");
            buyerAddress=map1.get("invoiceaddress")+"";
            buyerTel=map1.get("invoicetel")+"";
            buyerBankName=map1.get("bank")+"";
            buyerBankAccount=map1.get("bankacount")+"";
        }
        if (buyerName.equals(""))return GetErrorString(3,"发票抬头不全！");
        if (buyerTaxpayerNum.length()<10)return GetErrorString(3,"税号不全！");
      //  if (brand.equals("BMW"))
        {
            sqlstring="select count(billdetail.contractno) as sl from billdetail INNER JOIN contract on contract.contractno=billdetail.contractno where contract.`status`!='已取消' and billdetail.billno=?";
            map1=queryForMap(jdbcTemplate,sqlstring,billno);
            if (map1!=null) {
                cquantity=nullToZero( map1.get("sl")+"");
            }
            if (cquantity<=0)cquantity=1;
            quantity=cquantity;
            if (cquantity!=0)cunitPrice=cinvoiceAmount/cquantity;
            unitPrice=cunitPrice;
            invoiceAmount=cinvoiceAmount;
        }
        if (invoiceAmount<0)return GetErrorString(3,"开票金额不对！");
       /* float sl=1+setting.NullToZero(taxRateValue);
        float invoiceAmountnotax=0,taxRateAmount=0;
        invoiceAmountnotax=invoiceAmount;
        if (sl!=0)
        {
            unitPrice=unitPrice/sl;
            invoiceAmountnotax=invoiceAmount/sl;

            BigDecimal b   =   new   BigDecimal(invoiceAmountnotax);
            invoiceAmountnotax  =   b.setScale(2,   BigDecimal.ROUND_HALF_UP).floatValue();
            taxRateAmount=invoiceAmount-invoiceAmountnotax;
        }*/


       // if (is_elec_invoice==0)return GetErrorString(3,"该企业不支持电子发票！");
        if (buyerName.equals("")||buyerTaxpayerNum.equals(""))return GetErrorString(3,"开票信息不全！");
        String url = pathurl+"invoiceBlue.pt";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("taxpayerNum", taxpayerNum);  //销方税号
        map.put("invoiceReqSerialNo", platform_alias + getGuid());//发票请求流水号
        map.put("buyerName", buyerName);//购买方名称
        map.put("buyerTaxpayerNum", buyerTaxpayerNum);//购买方税号(非必填,个人发票传null)
       if (!buyerAddress.equals(""))  map.put("buyerAddress", buyerAddress);
        if (!buyerTel.equals(""))  map.put("buyerTel", buyerTel);
        if (!buyerBankName.equals(""))  map.put("buyerBankName", buyerBankName);
        if (!buyerBankAccount.equals(""))  map.put("buyerBankAccount", buyerBankAccount);
        map.put("drawerName", drawerName);//"李芳娟"
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> listMapOne = new HashMap<String, String>();
        listMapOne.put("taxClassificationCode", taxClassificationCode);//税收分类编码(可以按照Excel文档填写)
        listMapOne.put("includeTaxFlag", "1");//含税标记
        listMapOne.put("quantity", String.format("%.2f", quantity)+"");//数量
        listMapOne.put("goodsName", goodsName);//货物名称
        listMapOne.put("unitPrice", String.format("%.8f", unitPrice)+"");//单价
        listMapOne.put("invoiceAmount", String.format("%.2f", invoiceAmount)+"");//金额
        listMapOne.put("taxRateValue", taxRateValue);//税率
        //listMapOne.put("taxRateAmount", String.format("%.2f", taxRateAmount)+"");//金额
        //listMapOne.put("includeTaxFlag", "1");//含税标识
        //以下为零税率开票相关参数
        //   listMapOne.put("zeroTaxFlag", "");//零税率标识(空:非零税率,0:出口零税率,1:免税,2:不征税,3:普通零税率)
        // listMapOne.put("preferentialPolicyFlag", "");//优惠政策标识(空:不使用,1:使用)   注:零税率标识传非空 此字段必须填写为"1"
        // listMapOne.put("vatSpecialManage", "");//增值税特殊管理(preferentialPolicyFlag为1 此参数必填)
        list.add(listMapOne);
        map.put("itemList", list);
        // String content = JsonUtil.toJson(map);
        Gson gson = new Gson();
        String content =   gson.toJson(map);
        String response="";
        JsonObject jsonObject;
        //OpenApi参数内容(3des秘钥(票通提供),平台编码(票通提供),平台前缀(票通提供),私钥)
        try
        {
            String buildRequest = new OpenApi(password, platform_code, platform_alias, privateKey).buildRequest(content);

            response = HttpUtil.postJson(url, buildRequest);

            jsonObject = new JsonParser().parse(response).getAsJsonObject();
            if (jsonObject.keySet().contains("content"))
            {
                String str=SecurityUtil.decrypt3DES(password, jsonObject.get("content").getAsString());
                logs.WriteLog(str,"GetInvoice");
                jsonObject= new JsonParser().parse(str).getAsJsonObject();
            }
            if (jsonObject.keySet().contains("invoiceReqSerialNo"))invoiceno=jsonObject.get("invoiceReqSerialNo").getAsString();
            if (jsonObject.keySet().contains("qrCodePath")) {
                invoiceurl = jsonObject.get("qrCodePath").getAsString();
                invoiceurl=Base64Util.decode(invoiceurl);
                response=   "{\"invoiceno\":\""+invoiceno+"\",\"invoiceurl\":\""+invoiceurl+"\"}";
                sqlstring="update bill set status='已开票',invoiceno=?,invoiceurl=?,drawerName=?,invoicetime="+getCurrenntTime()+",invoicetype='普通发票' where billno=?";
                Object[]parms=new Object[4];
                parms[0]=invoiceno;
                parms[1]=invoiceurl;
                parms[2]=drawerName;
                parms[3]=billno;
                jdbcTemplate.update(sqlstring,parms);
                sqlstring="update contract set status='已开票' where contractno in (select contractno from billdetail where  billno=?) and (status!='已取消')";
                jdbcTemplate.update(sqlstring,billno);
            }
        }catch (Exception e1)
        {
            logs.WriteLog(e1.toString(),"GetInvoice");
        }

        return  response;
    }
    public  void AutoInvoice()//自动开票
    {
        String sqlstring="select bill.billno,bill.dealerno from bill INNER JOIN dealer on dealer.dealerno=bill.dealerno where dealer.valid=1 and dealer.is_elec_invoice=1 and bill.`status`='待开票' ";
        sqlstring+=" and (dealer.brand='BMW' or dealer.brand='Mercedes-Benz' or dealer.brand='Mazda')";
        List<Map<String ,Object>>list=queryForList(jdbcTemplate,sqlstring);
        Map<String ,Object>map;
        String dealerno="";
        String billno="";

        if(list!=null) {
            for (int i = 0; i < list.size(); i++) {
                map = list.get(i);
                dealerno = NUllToSpace(map.get("dealerno") + "");
                billno = NUllToSpace(map.get("billno") + "");
                GetInvoice(dealerno,billno);
            }
        }
    }
    public void testRegister() {

        String url = "http://fpkj.testnw.vpiaotong.cn/tp/openapi/register.pt";
        Map<String, String> map = new HashMap<String, String>();
        map.put("taxpayerNum", platform_alias + "000000000000");//销方纳税人识别号
        map.put("enterpriseName", "票通信息");//销方企业名称
        map.put("legalPersonName", "AA");//法人名称
        map.put("contactsName", "AA");//联系人名称
        map.put("contactsEmail", "1121@qq.com");//联系人邮箱
        map.put("contactsPhone", "15111111133");//联系人手机号
        map.put("regionCode", "11");//地区编码
        map.put("cityName", "海淀区");//市(区)名
        map.put("enterpriseAddress", "地址");//详细地址
        // TODO 请修改为正确的图片Base64传
        map.put("taxRegistrationCertificate", "sdddddddddddddddddddd");//证件图片base64
        String content = JsonUtil.toJson(map);
        System.out.println(content);
        //OpenApi参数内容(3des秘钥(票通提供),平台编码(票通提供),平台前缀(票通提供),私钥)
        String buildRequest = new OpenApi(password, platform_code, platform_alias, privateKey).buildRequest(content);
        System.out.println(buildRequest);
        String response = HttpUtil.postJson(url, buildRequest);
        System.out.println(response);
    }

    /**
     *
     * @title: testGetInvoiceQrAndExtractCode
     * @description: 获取多项目开票二维码和提取码接口
     */

    public void testGetQrCodeByItems() {
        String url = "http://fpkj.testnw.vpiaotong.cn/tp/openapi/getQrCodeByItems.pt";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("taxpayerNum", "110101201705230001");   //销方纳税人识别号
        map.put("enterpriseName", "电子票测试新1");			//销方企业名称
        map.put("tradeNo", platform_alias + "10000001");//订单号(唯一)
        map.put("tradeTime", "2017-06-26 09:15:54");	//交易时间
        map.put("invoiceAmount", "200");				//发票金额(含税)
        map.put("casherName", "收款人A");					//收款人姓名(校验规则: 中文/字母大小写/及其两者组合)
        map.put("reviewerName", "审核人A");				//审核人姓名(校验规则: 中文/字母大小写/及其两者组合)
        map.put("drawerName", "开票人A");					//开票人姓名(校验规则: 中文/字母大小写/及其两者组合)
        map.put("allowInvoiceCount", "1");				//允许开票张数(非必填  默认值:1)
        // map.put("smsFlag", "false");					//是否发送短信 (非必填  默认值:false 测试环境不发送短信)
        // map.put("expireTime", "");					//有效时间 (非必填  默认值:永久有效  填写格式 yyyy-MM-dd HH:mm:ss)
        // map.put("email","XXXXX@XX.com");				//二维码发送邮箱地址(非必填)
        //其他参数见接口文档
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> listMapOne = new HashMap<String, String>();
        listMapOne.put("itemName", "小麦");  				//开票项目名
        listMapOne.put("taxRateValue", "0.16");			//税率
        listMapOne.put("taxClassificationCode", "110101201705230001");//税收分类编码
        listMapOne.put("quantity", "1");				//数量
        listMapOne.put("unitPrice", "50");				//单价
        listMapOne.put("invoiceItemAmount", "50");		//金额
        Map<String, String> listMapTwo = new HashMap<String, String>();
        listMapTwo.put("itemName", "大米");
        listMapTwo.put("taxRateValue", "0.16");
        listMapTwo.put("taxClassificationCode", "110101201705230001");
        listMapTwo.put("quantity", "1");
        listMapTwo.put("unitPrice", "50");
        listMapTwo.put("invoiceItemAmount", "50");
        list.add(listMapOne);
        list.add(listMapTwo);
        map.put("itemList", list);
        String content = JsonUtil.toJson(map);
        //OpenApi参数内容(3des秘钥(票通提供),平台编码(票通提供),平台前缀(票通提供),私钥)
        String buildRequest = new OpenApi(password, platform_code, platform_alias, privateKey).buildRequest(content);
        String response = HttpUtil.postJson(url, buildRequest);
        System.out.println(response);
        System.out
                .println(new OpenApi(password, platform_code, platform_alias, privateKey).disposeResponse(response, ptPublicKey));

    }

    /**
     *
     *  @title: testdeleteInvoiceQrCode
     * @description: 作废二维码接口
     */

    public void testdeleteInvoiceQrCode(){
        String url = "http://fpkj.testnw.vpiaotong.cn/tp/openapi/deleteInvoiceQrCode.pt";
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        Map<String, String> map = new HashMap<String, String>();
        map.put("taxpayerNum", "");  //销方税号
        map.put("enterpriseName", "测试");//销方企业名
        map.put("tradeNo", "CTXP2018091910241715");//与开票二维码的订单号一致
        map.put("tradeTime", "2017-06-26 09:15:54"); //与开票二维码的时间一致
        map.put("invoiceAmount", "1.00");//金额一致
        list.add(map);
        String content = JsonUtil.toJson(list);
        //OpenApi参数内容(3des秘钥(票通提供),平台编码(票通提供),平台前缀(票通提供),私钥)
        String buildRequest = new OpenApi(password, platform_code, platform_alias, privateKey).buildRequest(content);
        System.out.println(buildRequest);
        String response = HttpUtil.postJson(url, buildRequest);
        System.out.println(response);
        System.out
                .println(new OpenApi(password, platform_code, platform_alias, privateKey).disposeResponse(response, ptPublicKey));
    }

    /**
     *
     * @title: testInvoiceBlueMultiItem
     * @description: 蓝票接口调用
     */




    /**
     *
     * @title: testInvoiceRed
     * @description: 红票接口调用
     */

    public void testInvoiceRed() {
        String url = "http://fpkj.testnw.vpiaotong.cn/tp/openapi/invoiceRed.pt";
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("taxpayerNum", "110101201705230001");//销方税号(请于要冲红的蓝票税号一致)
        // TODO 请更换请求流水号前缀
        map.put("invoiceReqSerialNo", platform_alias + "5678902234568903");//发票流水号 (唯一, 与蓝票发票流水号不一致)
        map.put("invoiceCode", "050003522444");//冲红发票的发票代码
        map.put("invoiceNo", "11302054");//冲红发票的发票号码
        map.put("redReason", "冲红");//冲红原因
        map.put("amount", "-56.64");//冲红金额 (要与原发票的总金额一致)
        String content = JsonUtil.toJson(map);
        System.out.println(content);
        //OpenApi参数内容(3des秘钥(票通提供),平台编码(票通提供),平台前缀(票通提供),私钥)
        String buildRequest = new OpenApi(password, platform_code, platform_alias, privateKey).buildRequest(content);
        System.out.println(buildRequest);
        String response = HttpUtil.postJson(url, buildRequest);
        System.out.println(response);
    }
    /**
     *
     * @title: testAuthWeChatCards
     * @description:查询发票
     */

    public void testqueryInvoice() {
        String url = "http://fpkj.testnw.vpiaotong.cn/tp/openapi/queryInvoice.pt";
//        String url = "http://fpkj.testnw.vpiaotong.cn/tp/openapi/queryInvoiceInfo.pt";   //查询发票票面全面信息地址
        String content = "{ \"taxpayerNum\": \"110105201606160003\", \"invoiceReqSerialNo\": \"CTXP2018091314310031\"}";
        //OpenApi参数内容(3des秘钥(票通提供),平台编码(票通提供),平台前缀(票通提供),私钥)
        String buildRequest = new OpenApi(password, platform_code, platform_alias, privateKey).buildRequest(content);
        System.out.println(buildRequest);
        String response = HttpUtil.postJson(url, buildRequest);
        System.out.println(response);
        System.out
                .println(new OpenApi(password, platform_code, platform_alias, privateKey).disposeResponse(response, ptPublicKey));

    }

    /**
     *
     * @title: testAuthWeChatCards
     * @description:查询发票抬头信息
     */

    public void testTitleInfo() {
        String url = "http://fpkj.testnw.vpiaotong.cn/tp/openapi/getInvoiceTitleInfo.pt";
        String content = "{\"enterpriseName\":\"测试\"}";
        //OpenApi参数内容(3des秘钥(票通提供),平台编码(票通提供),平台前缀(票通提供),私钥)
        String buildRequest = new OpenApi(password, platform_code, platform_alias, privateKey).buildRequest(content);
        System.out.println(buildRequest);
        String response = HttpUtil.postJson(url, buildRequest);
        System.out.println(response);
        System.out
                .println(new OpenApi(password, platform_code, platform_alias, privateKey).disposeResponse(response, ptPublicKey));

    }

    /**
     *
     * @title: testGetPTBoxStatus
     * @description: 获取票通宝状态接口
     */

    public void testGetPTBoxStatus() {
        String url = "http://fpkj.testnw.vpiaotong.cn/tp/openapi/getPTBoxStatus.pt";
        String content = "{\"taxpayerNum\":\"110101201705230001\",\"enterpriseName\":\"电子票测试新1\"}";
        //OpenApi参数内容(3des秘钥(票通提供),平台编码(票通提供),平台前缀(票通提供),私钥)
        String buildRequest = new OpenApi(password, platform_code, platform_alias, privateKey).buildRequest(content);
        String response = HttpUtil.postJson(url, buildRequest);
        System.out.println(response);
        System.out
                .println(new OpenApi(password, platform_code, platform_alias, privateKey).disposeResponse(response, ptPublicKey));

    }

    /**
     *
     * @title: testGetInvoiceRepertoryInfo
     * @description: 获取库存接口
     */

    public void testGetInvoiceRepertoryInfo() {
        String url = "http://fpkj.testnw.vpiaotong.cn/tp/openapi/getInvoiceRepertoryInfo.pt";
        String content = "{\"taxpayerNum\":\"110101201702073\",\"enterpriseName\":\"电子票测试新1\"}";
        //OpenApi参数内容(3des秘钥(票通提供),平台编码(票通提供),平台前缀(票通提供),私钥)
        String buildRequest = new OpenApi(password, platform_code, platform_alias, privateKey).buildRequest(content);
        String response = HttpUtil.postJson(url, buildRequest);
        System.out.println(response);
        System.out
                .println(new OpenApi(password, platform_code, platform_alias, privateKey).disposeResponse(response, ptPublicKey));

    }

    /**
     *
     * @title: testAuthWeChatCards
     * @description:微信卡包授
     */

    public void testAuthWeChatCards() {
        String url = "http://fpkj.testnw.vpiaotong.cn/tp/openapi/authWeChatCards.pt";
        String content = "{\"taxpayerNum\":\"110101201705230001\",\"invoiceReqSerialNo\":\"GAGA0000000000000009\"}";
        //OpenApi参数内容(3des秘钥(票通提供),平台编码(票通提供),平台前缀(票通提供),私钥)
        String buildRequest = new OpenApi(password, platform_code, platform_alias, privateKey).buildRequest(content);
        String response = HttpUtil.postJson(url, buildRequest);
        System.out.println(response);
        System.out
                .println(new OpenApi(password, platform_code, platform_alias, privateKey).disposeResponse(response, ptPublicKey));

    }









    /**
     *
     * 以下为便捷工具 ,非调用接口
     */



    /**
     *
     * @title: test3DES
     * @description: 3DES加密
     */

    public void test3DES() {
        String content = "{\"taxpayerNum\": \"9120931023801231\",\"enterpriseName\": \"西单大悦城有限公司\",\"paymentTransID\": \"12109238102831023102983\",\"paymentType\": \"2\",\"paymentTransTime\": \"2017-01-19 18:20:09\",\"paymentTransMoney\": \"20\",\"orderID\": \"12109238102831023102981\",\"orderMoney\": \"30\"}";
        System.out.println(SecurityUtil.encrypt3DES("8f1$OeJ@eSR0z5Jh%!LmiBzi", content));
    }

    /**
     *
     * @title: test3DESDecry
     * @description: 3DES解密
     */

    public void test3DESDecry() {
        String str = "/WMVSKtTr2fzX4ZIeJQiqWfjst0dxVJoMrupVju7ZHFOBUD6YnTUGMAbwnaSp/wLEKFxxbGLsj9jQKSPxhKPvVZXmnEGTYsfby0KS/VrbSVAUo2EWz/53K9fajQ4Q1aoELsU/hTHO2KnTBEAEMRowGHX8lMFbvJ/ayu+PYUJ0/OHDr/dyI56fh6R6HfTvH2vsQb7ORjAk2fHHy3Bm/DFMhyqCk42ed6ygstwCXNYkjFjjCYTthYR/z801LDcOKq9TAI8TrMh/DtUXFmCRH70+e616we0Rv1zfxcmt9nqe9AI8uJUF0Bd+tdImWMF98dcclATKZ1Nk7We/xv4QUvoxsQJX2BFnCnw0rx6mPvisFcQDgo270EYLR5eFsBHy1oH1UD8VkgdjapOQzR0QhExSJ2z2bMBe56RW+kxITUE4qbP4pQd5MtSkBcc8NCSPrP2si8kQ/9R2XB+e3vCL97dvsyW4s9n2XDOsi8kQ/9R2XCwRlI/fcxKDsT8CTecGsv9Ps6UCdRNfzdBq/+JH66xM5yWBq5l5ZDSmZfvpMFpwc3dn/Zh4wbNEPhJi8S/TmAHIr8gLw2EgzT2f979G/8kboBAaWbrt1bgQ7sUHNGmvrE=";
        System.out.println(SecurityUtil.decrypt3DES("IskuI3GAMm2DAfaweCcndyCp", str));
    }

    /**
     *
     * @title: testBase64
     * @description: Base64编码
     */

    public void testBase64() {
        String str = "JJON0d93C9nQN013N+cCwwIYbRVYlWChGQkSgAWG8g4mD1xFU6oGPauqih5gW7ZTcpejSPS8TqRbdBFdBATSXdwZqPM0q8sVYf3xwlp8OEw6INcUCvRW7myiFkzSJLV4Ost42d5Xp+sicgMj0bn99BsRSqe06BMvYTA46L/vGGPqN4tuuy2B/enpkGLcOQdPdtC+wG8ub6+zykisJT5I7EMls73cjaSlj1iRw/PT9huULu97iPHIiqnKhK05AXkvgWMcfg42+bLeG/kPgbaAtwAkXN/yDkKACcDML2WE8TZ+BFsaQPbH+BfY/XQ4VXSYF5NGeulhDJr1DLIHgH+KNQ==";
        System.out.println(Base64Util.encode2String(str));
    }

    /**
     *
     * @title: testBase64
     * @description: Base64解码
     */

    public void testBase64toURL() {
        String str = "aHR0cDovL2Zwa2oudGVzdG53LnZwaWFvdG9uZy5jbi9kL01UQTFPVEk0TWpJME16a3lPRGcxTkRVeU9BPT0ucHQ=";
        System.out.println(Base64Util.decode2String(str));
    }

    /**
     *
     * @title: testSign
     * @description: RSA签名
     */

    public void testSign() {
        String content = "{\"taxpayerNum\":\"GAGA000000000001\",\"enterpriseName\":\"测试注册A\",\"platformCode\":\"13242753\",\"registrationCode\":\"87774618\",\"authorizationCode\":\"4209110715\"}";
        String buildRequest = new OpenApi("D5ImkGeVdsCaZyna4G73jv6j", "vZpDTUSV", "GAGA",
                "")
                .buildRequest(content);
        System.out.println(buildRequest);
    }

    /**
     *
     * @title: testVerify
     * @description:RSA验签
     */

    public void testVerify() {
        String requestJson = "{\"format\":\"JSON\",\"sign\":\"i8VGz3Qs4tnw89aeJON21nn2h8oQAynM4BALX8iKb+BlfXvIc2PS1hHen0cnJm53bvgRfyVCEgXQkpFK1Rdiw6ht3KD1IxtYMSkISUuwRphuhwPQiVVsODZiGSRQpHCwcv+I7szWhOErY0sYBqPHfHSsvZ4bD/rOp7K+0Lp/sw0\\u003d\",\"signType\":\"RSA\",\"version\":\"1.0\",\"platformCode\":\"TohDKeNU\",\"content\":\"EYY+FTkOvZs70g6eplhvD8P3oOZBVwtO1mN3dwDgNo0wCi9URbwtGJBqiaKUpjvn43HuE04aelW7J+ZgewZG5AmgdbY0aFhDejb5czd6UPt8DWLK9NyrgKMtKBc4LULfPKFhQwchtfgWWJtEHMrIyyHQPe5N5vgmAGGBE4nYUyazp6p42JnMLJCUHHNKsI2zApQWp20I1BCMhT/XZc+jXZ3q50UmHJP9l4vKCfESLxwahdADgB2HLqRWLr1OgxYenUUNkokwtV5TxQBYr/iKdQ03HFEVjNV+tuTTrcdakA/tF0UOc71roxxmHFgJU8bl64U+KRMEM1cQoEDwRzjqcbfGRrj/JFEqQel2k7mDsNZi24EvIExNDFautR9XLsuryZ8vrDwK33Ln/oYz+K5wooyf13kv8KDQgVhWCkW3XmmWwGMCttzOP9pVbiD3WP6MJ5qmhtK7R4JBibMoLAjFUcA2IZgk9tnphTUlQZcFEIPCT0cv0fyvuw\\u003d\\u003d\",\"timestamp\":\"2017-05-12 13:55:06\",\"serialNo\":\"CHSE20170512135506OW6jE08r\"}";

        Map<String, String> paramMap = JsonUtil.json2Map(requestJson);
        String sign = paramMap.remove("sign");
        String a = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCdL0fHPUtT9bTc8tTx5cSNnLO09P/4pTnYyPpp9Jt+NU+nTYiVjDrX97a7M0NuuoxpX4KSFz3WKTgGir+uYcQUm50+oEaRI1gQZrjnIfXEyafdS1Gcr34OAvObxvShQst/p3swVatqK4b7/SfYrQN6S4tQ4L+06QBKgvvaxpGPTwIDAQAB";
        boolean verify = RSAUtil.verify(RSAUtil.getSignatureContent(paramMap), sign, a);
        System.out.println(RSAUtil.getSignatureContent(paramMap));

        System.out
                .println(new OpenApi(password, platform_code, platform_alias, privateKey).disposeResponse("", ptPublicKey));

        System.out.println(verify);
        System.out.println(ptPublicKey.length());
        System.out.println(privateKey.length());
    }

}
