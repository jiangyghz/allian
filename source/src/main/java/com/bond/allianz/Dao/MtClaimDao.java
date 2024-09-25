package com.bond.allianz.Dao;

import com.bond.allianz.utils.PdfUtil;
import com.bond.allianz.utils.SendMailUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class MtClaimDao extends BaseDao{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    public String addorUpdateClaim(String data){
        String claimno="",contractno="",dealerno="",bank="",bankacount="",vin="",claimtype="",province="",icode="";
        String city="",reason="",linkman="",linkmobile="",accountname="",status="草稿",claimdesc="",contract_dealerno="";
        float claimamount=0,vehicleVesselTax=0,vehiclePurchaseTax=0,vehicleLicenseFee=0,vehicleBZPremium=0,vehicleBusinessPremium=0,partsLossSum=0,hoursLossSum=0;
        int mileage=0,submittimes=0;
        String submituserid="",dangerdate="";
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
        if (jsonObject.keySet().contains("submituserid"))submituserid=jsonObject.get("submituserid").getAsString();
        if (jsonObject.keySet().contains("claimno"))claimno=jsonObject.get("claimno").getAsString();
        if (jsonObject.keySet().contains("bank"))bank=jsonObject.get("bank").getAsString();
        if (jsonObject.keySet().contains("bankacount"))bankacount=jsonObject.get("bankacount").getAsString();
        if (jsonObject.keySet().contains("vin"))vin=jsonObject.get("vin").getAsString();
        if (jsonObject.keySet().contains("claimtype"))claimtype=jsonObject.get("claimtype").getAsString();
        if (jsonObject.keySet().contains("province"))province=jsonObject.get("province").getAsString();
        if (jsonObject.keySet().contains("city"))city=jsonObject.get("city").getAsString();
        if (jsonObject.keySet().contains("reason"))reason=jsonObject.get("reason").getAsString();
        if (jsonObject.keySet().contains("linkman"))linkman=jsonObject.get("linkman").getAsString();
        if (jsonObject.keySet().contains("icode"))icode=jsonObject.get("icode").getAsString();
        if (jsonObject.keySet().contains("status"))status=jsonObject.get("status").getAsString();
        if (jsonObject.keySet().contains("linkmobile"))linkmobile=jsonObject.get("linkmobile").getAsString();
        if (jsonObject.keySet().contains("accountname"))accountname=jsonObject.get("accountname").getAsString();
        if (jsonObject.keySet().contains("claimdesc"))claimdesc=jsonObject.get("claimdesc").getAsString();
        if (jsonObject.keySet().contains("claimamount"))claimamount=jsonObject.get("claimamount").getAsFloat();
        if (jsonObject.keySet().contains("mileage"))mileage=jsonObject.get("mileage").getAsInt();
        if (jsonObject.keySet().contains("submittimes"))submittimes=jsonObject.get("submittimes").getAsInt();
//vehicleVesselTax,vehiclePurchaseTax,vehicleLicenseFee,vehicleBZPremium,vehicleBusinessPremium,partsLossSum,hoursLossSum
        if (jsonObject.keySet().contains("vehicleVesselTax"))vehicleVesselTax=jsonObject.get("vehicleVesselTax").getAsFloat();
        if (jsonObject.keySet().contains("vehiclePurchaseTax"))vehiclePurchaseTax=jsonObject.get("vehiclePurchaseTax").getAsFloat();
        if (jsonObject.keySet().contains("vehicleLicenseFee"))vehicleLicenseFee=jsonObject.get("vehicleLicenseFee").getAsFloat();
        if (jsonObject.keySet().contains("vehicleBZPremium"))vehicleBZPremium=jsonObject.get("vehicleBZPremium").getAsFloat();
        if (jsonObject.keySet().contains("vehicleBusinessPremium"))vehicleBusinessPremium=jsonObject.get("vehicleBusinessPremium").getAsFloat();
        if (jsonObject.keySet().contains("partsLossSum"))partsLossSum=jsonObject.get("partsLossSum").getAsFloat();
        if (jsonObject.keySet().contains("hoursLossSum"))hoursLossSum=jsonObject.get("hoursLossSum").getAsFloat();
        if (jsonObject.keySet().contains("dangerdate"))dangerdate=jsonObject.get("dangerdate").getAsString();
        if (jsonObject.keySet().contains("dealerno"))dealerno=jsonObject.get("dealerno").getAsString();
        String sql ="";
        int tt=getCurrenntTime();
        sql="select dealerno,status,vin,claimstatus,icode from  mt_contract where contractno ='"+contractno+"'";
        Map<String ,Object> map=queryForMap(jdbcTemplate,sql);
        String cantractstatus="";
        if (map!=null)
        {
            cantractstatus=map.get("status")+"";
            // icode=map.get("icode")+"";
            contract_dealerno=map.get("dealerno")+"";
            vin=map.get("vin")+"";
            if (!(map.get("icode")+"").equals(""))icode=map.get("icode")+"";
        }else return  GetErrorString(3,"合同号不存在！");
        if (dealerno.equals(""))dealerno=contract_dealerno;
        //宝马的rti全部是大地保险，这里要做逻辑控制
        if (claimtype.contains("车辆置换"))
        {
            icode="CCIC";
        }else if (claimtype.contains("钥匙")) icode="CPIC";
        //多品牌的时候根据理赔类型来控制保险公司
        //摩托车的保险公司都是太平洋保险苏州分公司
        icode="CPIC_SZH";
        if (cantractstatus.equals("草稿")||cantractstatus.equals("已取消"))return  GetErrorString(3,"合同号不符合理赔条件！");
        if (icode.equals(""))return  GetErrorString(3,"没有符合的保险公司！");
        if (dealerno.equals(""))return  GetErrorString(3,"没有符合的经销商！");
        if (claimno.equals(""))
        {
            claimno=getClaimno();
            if (status.equals(""))status="草稿";
            if (claimtype.equals("PS-车辆置换服务"))
            {
                String Invoiceurl="",insuranceurl="";
                sql="select Invoiceurl,insuranceurl from mt_contract where contractno=?";
                map=queryForMap(jdbcTemplate,sql,contractno);
                if (map!=null) {
                    Invoiceurl = map.get("Invoiceurl") + "";
                    insuranceurl = map.get("insuranceurl") + "";
                }
                if (!Invoiceurl.equals(""))
                {
                    sql="insert into mt_claimpic (tid,claimno,picurl,valid,userid,pictype) values ( uuid(),?,?,1,?,?)";
                    Object[] parms=new Object[4];
                    parms[0]=claimno;
                    parms[1]=Invoiceurl;
                    parms[2]=submituserid;
                    parms[3]="购车发票（原车）";
                    jdbcTemplate.update(sql,parms);
                }
                if (!insuranceurl.equals(""))
                {
                    sql="insert into mt_claimpic (tid,claimno,picurl,valid,userid,pictype) values ( uuid(),?,?,1,?,?)";
                    Object[] parms=new Object[4];
                    parms[0]=claimno;
                    parms[1]=insuranceurl;
                    parms[2]=submituserid;
                    parms[3]="车险保单";
                    jdbcTemplate.update(sql,parms);
                }

            }
            sql="insert into mt_claim (dealerno,bank,bankacount,claimtype,province,icode,city,reason,linkman,linkmobile,claimamount,submituserid,ttime,contractno,accountname,status,claimdesc,vin,mileage,submittimes,vehicleVesselTax,vehiclePurchaseTax,vehicleLicenseFee,vehicleBZPremium,vehicleBusinessPremium,partsLossSum,hoursLossSum,dangerdate,claimno,submittime) ";
            sql+=" values (?,?,?,?,?,?,?,?,?,?,?,?,"+tt+",?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,"+tt+")";
        }
        else
        {
            if (status.equals(""))
            {
                sql="select status from mt_claim where claimno=?";
                map=queryForMap(jdbcTemplate,sql,claimno);
                if (map!=null)status=setting.NUllToSpace(map.get("status"));
            }
            if (status.equals(""))status="草稿";
            sql="update mt_claim set dealerno=?,bank=?,bankacount=?,claimtype=?,province=?,icode=?,city=?,reason=?,linkman=?,linkmobile=?,claimamount=?,submituserid=?,contractno=?,accountname=?,status=?,claimdesc=?,vin=?,mileage=?,submittimes=?,vehicleVesselTax=?,vehiclePurchaseTax=?,vehicleLicenseFee=?,vehicleBZPremium=?,vehicleBusinessPremium=?,partsLossSum=?,hoursLossSum=?,dangerdate=?,submittime="+tt+" where claimno=?";
        }
        Object[] queryList=new Object[28];
        queryList[0]=dealerno;
        queryList[1]=bank;
        queryList[2]=bankacount;
        queryList[3]=claimtype;
        queryList[4]=province;
        queryList[5]=icode;
        queryList[6]=city;
        queryList[7]=reason;
        queryList[8]=linkman;
        queryList[9]=linkmobile;
        queryList[10]=claimamount;
        queryList[11]=submituserid;
        queryList[12]=contractno;
        queryList[13]=accountname;
        queryList[14]=status;
        queryList[15]=claimdesc;
        queryList[16]=vin;
        queryList[17]=mileage;
        queryList[18]= submittimes;
        //vehicleVesselTax,vehiclePurchaseTax,vehicleLicenseFee,vehicleBZPremium,vehicleBusinessPremium,partsLossSum,hoursLossSum
        queryList[19]=vehicleVesselTax;
        queryList[20]=vehiclePurchaseTax;
        queryList[21]=vehicleLicenseFee;
        queryList[22]=vehicleBZPremium;
        queryList[23]=vehicleBusinessPremium;
        queryList[24]=partsLossSum;
        queryList[25]=hoursLossSum;
        queryList[26]=dangerdate;
        queryList[27]=claimno;
        int ii=0;
        try {
             ii=  jdbcTemplate.update(sql,queryList);
        }catch (Exception e)
        {
            String ss=e.toString();
            return  GetErrorString(3,"保存理赔信息失败！"+ss);
        }


        if (status.contains("提交"))
        {
            sql="update  mt_contract  set claimstatus='理赔已提交' where  contractno =?";
            jdbcTemplate.update(sql,contractno);
            sql="update mt_claimpic set candel=0 where claimno=?";
            jdbcTemplate.update(sql,claimno);
            sql="select email from insurance where code=?";
            map=queryForMap(jdbcTemplate,sql,icode);
            String email="",content="";
            if (map!=null) {
                email = setting.NUllToSpace(map.get("email"));

            }

            sql="insert into mt_claimcheck (status,checkuserid,ttime,checkremark,claimno) values  (?,?,"+tt+",?,?)";

            queryList=new Object[4];
            queryList[0]="理赔已提交";
            queryList[1]=submituserid;
            queryList[2]=claimdesc;
            queryList[3]=claimno;
            jdbcTemplate.update(sql,queryList);
            if (!email.equals(""))
            {
                try {

                    content+="    您好！\n";
                    content+="您收到了一个新的理赔申请，理赔号为："+claimno+"，请及时处理，<a href=\""+GetPathurl()+"background/index.html#/welcome\">点击链接查看</a>。\n";
                    SendMailUtil.sendMail(email, "理赔", content, "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //跨店理赔时给凭证创建门店发送邮件
            if (dealerno!=contract_dealerno)
            {
                sql="select email,dealername from dealer where valid=1 and dealerno=?";
                map=queryForMap(jdbcTemplate,sql,contract_dealerno);
                if (map!=null)
                {
                    String dealer_email=setting.NUllToSpace(map.get("email"));
                    if (!dealer_email.equals(""))
                    {
                        try {
                            sql="select dealername from dealer where  dealerno=?";
                            map=queryForMap(jdbcTemplate,sql,dealerno);
                            String new_dealername="";
                            if (map!=null)new_dealername=NUllToSpace(map.get("dealername"));
                            content+="    您好！\n";
                            content+="          "+vin+"车架号的客户在"+new_dealername+"进行了理赔申请。\n";
                            SendMailUtil.sendMail(dealer_email, "理赔", content, "");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
//        else
////        {
////            sql="update  contract  set claimstatus='' where  contractno =?";
////            jdbcTemplate.update(sql,contractno);
////        }


        String resultss="";

        if (ii==1)resultss="{\"errcode\":\"0\",\"claimno\":\""+claimno+"\"}";
        else resultss=GetErrorString(3,"提交不成功！");
        return  resultss;
    }
    public  void  createPdf(String contractno) {

        String dealerno="";
        String sqlstring="select *,(select FROM_UNIXTIME(ttime,'%Y-%m-%d') from  contractcheck where contractno=contract.contractno and  checkcontent='合同提交' order by ttime desc limit 0,1 ) as submittime from contract where contractno=?";
        Map<String,Object>mappdf=queryForMap(jdbcTemplate,sqlstring,contractno);
        String brand="",vehicletype="",vin="",Invoiceno="",Invoicedate="",guideprice="",Invoiceprice="";
        String cname="",IdNo="",pname="",insurancecompany="",Businessinsurancepolicyno="",insurancepolicyno="",productid="",companyname="",submittime="",icode="",carstarttime="";

        int iscompany=0;
        String retailprice="0";

        if (mappdf!=null)
        {
            brand=setting.NUllToSpace(mappdf.get("brand"));
            vehicletype=setting.NUllToSpace(mappdf.get("vehicletype"));
            vin=setting.NUllToSpace(mappdf.get("vin"));
            Invoiceno=setting.NUllToSpace(mappdf.get("Invoiceno"));
            Invoicedate=setting.NUllToSpace(mappdf.get("Invoicedate"));
            guideprice=setting.NUllToSpace(mappdf.get("guideprice"));
            Invoiceprice=setting.NUllToSpace(mappdf.get("Invoiceprice"));
            dealerno=setting.NUllToSpace(mappdf.get("dealerno"));
            cname=setting.NUllToSpace(mappdf.get("cname"));
            IdNo=setting.NUllToSpace(mappdf.get("IdNo"));
            pname=setting.NUllToSpace(mappdf.get("pname"));
            insurancecompany=setting.NUllToSpace(mappdf.get("insurancecompany"));
            Businessinsurancepolicyno=setting.NUllToSpace(mappdf.get("Businessinsurancepolicyno"));
            insurancepolicyno=setting.NUllToSpace(mappdf.get("forceno"));
            productid=setting.NUllToSpace(mappdf.get("productid"));
            companyname=setting.NUllToSpace(mappdf.get("companyname"));
            iscompany=setting.StrToInt(mappdf.get("iscompany")+"");
            submittime=mappdf.get("submittime")+"";
            retailprice=setting.NUllToSpace(mappdf.get("retailprice"));
            icode=setting.NUllToSpace(mappdf.get("icode"));
            carstarttime=  NUllToSpace  (mappdf.get("carstarttime"));
        }

        String root = getContext().getRealPath("/");
        String model = "";
        if (pname.contains("轮胎"))
            model = new File(root, "images/paicmodel.pdf").getPath();
        else
            model = new File(root, "images/cpicmodel.pdf").getPath();

        sqlstring="select * from dealer where dealerno=?";
        Map<String,Object> map=queryForMap(jdbcTemplate,sqlstring,dealerno);
        String dealername="",address="",tel="";
        if (map!=null)
        {
            dealername=setting.NUllToSpace(map.get("dealername"));

        }
        mappdf.put("dealername",dealername);
        mappdf.put("idno_c",IdNo);
        if (companyname.equals(""))
        {
            mappdf.put("companyname",cname);
            mappdf.put("idno_c","");

        }

        String xh1="",xh2="",xh3="",choice1="",choice2="",choice3="",start1="",start2="",start3="",end1="",end2="",end3="";
        sqlstring="select * from insuranceproduct where productid in ('"+productid.replace(",","','")+"')";
        map=queryForMap(jdbcTemplate,sqlstring);
        int ii=1;
        String startdate=submittime;
        if (!carstarttime.equals(""))startdate=carstarttime;
        if (map!=null) {
            String disc = map.get("disc") + "";
            xh1 = "1";
            xh2 = "2";
            xh3 = "3";
            choice1 = "否";
            choice2 = "否";
            choice3 = "否";
            if (disc.contains("全损保障")) {

                choice1 = "是";
                start1 = startdate;
                end1 = addYear(startdate, setting.StrToInt(map.get("rti") + ""));
            }
            if (disc.contains("轮胎保障")) {

                choice2 = "是";
                start2 = startdate;
                end2 = addYear(startdate, setting.StrToInt(map.get("tire") + ""));
            }
            if (disc.contains("钥匙保障")) {

                choice2 = "是";
                start2 = startdate;
                end2 = addYear(startdate, setting.StrToInt(map.get("carkey") + ""));
            }
            if (disc.contains("代步保障")) {

                choice3 = "是";
                start3 = startdate;
                end3 = addYear(startdate, setting.StrToInt(map.get("accident") + ""));
            }
        }
        mappdf.put("xh1",xh1);
        mappdf.put("xh2",xh2);
        mappdf.put("xh3",xh3);
        mappdf.put("choice1",choice1);
        mappdf.put("choice2",choice2);
        mappdf.put("choice3",choice3);
        mappdf.put("start1",start1);
        mappdf.put("start2",start2);
        mappdf.put("start3",start3);
        mappdf.put("end1",end1);
        mappdf.put("end2",end2);
        mappdf.put("end3",end3);
        mappdf.put("tbrand1","");
        mappdf.put("tbrand2","");
        mappdf.put("tbrand3","");
        mappdf.put("tbrand4","");
        mappdf.put("ttype1","");
        mappdf.put("ttype2","");
        mappdf.put("ttype3","");
        mappdf.put("ttype4","");
        mappdf.put("dot1","");
        mappdf.put("dot2","");
        mappdf.put("dot3","");
        mappdf.put("dot4","");
        sqlstring = "select * from tiredetail where contractno=? ";
        List<Map<String,Object>> list = queryForList(jdbcTemplate, sqlstring,contractno);
        if (list.size() > 0) {
            for (int i=0;i<list.size();i++)
            {
                mappdf.put("tbrand"+(i+1),  list.get(i).get("brandname"));
                mappdf.put("ttype"+(i+1),  list.get(i).get("tiretype"));
                mappdf.put("dot"+(i+1),  list.get(i).get("dot"));
            }

        }

        String savepath =wximage+contractno+".pdf";

        //生成pdf
        PdfUtil.createdPdfByModel(mappdf, model, savepath);


    }


    private String getClaimno()
    {
        String resultss="";
        int t1=GetMonthtime();
        long  ii=0;
        String sqlstring="select max(claimno) as claimno from mt_claim where ttime>"+t1;
        Map<String,Object>map=queryForMap(jdbcTemplate,sqlstring);
        if (map!=null)
        {
            ii=nullToInt(map.get("claimno")+"") ;
        }
        if (ii==0)resultss=GetNowDate("yyyyMM")+"0001";
        else resultss=(ii+1)+"";
        return resultss;
    }

    public String getClaim(String data){
        int pageindex=1 ,  pagesize=10;
        String claimno="",contractno="",dealerno="",claimtype="",province="",icode="",vin="",keystring="";
        String city="",status="",linkman="",linkmobile="",cname="",userid="";
        int time1=0,time2=0;
        String submituserid="";
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
        if (jsonObject.keySet().contains("submituserid"))submituserid=jsonObject.get("submituserid").getAsString();
        if (jsonObject.keySet().contains("claimno"))claimno=jsonObject.get("claimno").getAsString();
        if (jsonObject.keySet().contains("time1"))time1=setting.StrToInt(jsonObject.get("time1").getAsString());
        if (jsonObject.keySet().contains("time2"))time2=setting.StrToInt(jsonObject.get("time2").getAsString());
        if (jsonObject.keySet().contains("status"))status=jsonObject.get("status").getAsString();
        if (jsonObject.keySet().contains("claimtype"))claimtype=jsonObject.get("claimtype").getAsString();
        if (jsonObject.keySet().contains("province"))province=jsonObject.get("province").getAsString();
        if (jsonObject.keySet().contains("city"))city=jsonObject.get("city").getAsString();
        if (jsonObject.keySet().contains("linkman"))linkman=jsonObject.get("linkman").getAsString();
        if (jsonObject.keySet().contains("dealerno"))dealerno=jsonObject.get("dealerno").getAsString();
        if (jsonObject.keySet().contains("icode"))icode=jsonObject.get("icode").getAsString();
        if (jsonObject.keySet().contains("linkmobile"))linkmobile=jsonObject.get("linkmobile").getAsString();
        if (jsonObject.keySet().contains("vin"))vin=jsonObject.get("vin").getAsString();
        if (jsonObject.keySet().contains("cname"))cname=jsonObject.get("cname").getAsString();
        if (jsonObject.keySet().contains("userid"))userid=jsonObject.get("userid").getAsString();
        if (jsonObject.keySet().contains("pageindex"))pageindex=jsonObject.get("pageindex").getAsInt();
        if (jsonObject.keySet().contains("pagesize"))pagesize=jsonObject.get("pagesize").getAsInt();
        String sql ="",claimpro="";
       /* if (!userid.equals(""))
        {
            sql="select claimpro from usermember where id=?";
            Map<String,Object>map=queryForMap(jdbcTemplate,sql,userid);
            if (map!=null)claimpro=NUllToSpace(map.get("claimpro"));
        }*/
        List<Object> queryList=new ArrayList<Object>();
        sql="select c.brand,c.vin,cl.*,FROM_UNIXTIME(cl.submittime,'%Y-%m-%d %H:%i:%s') as dsubmittime,FROM_UNIXTIME(cl.ttime,'%Y-%m-%d %H:%i:%s') as createtime,FROM_UNIXTIME(cl.checktime,'%Y-%m-%d %H:%i:%s') as claimtime,d.dealername,i.iname as insurancename,c.cname from  mt_claim cl LEFT OUTER JOIN dealer d on d.dealerno=cl.dealerno inner join insurance i on i.code=cl.icode inner join mt_contract c on c.contractno=cl.contractno where 1=1 ";
        if (!contractno.equals(""))
        {
            sql+=" and cl.contractno like ?";
            queryList.add("%"+contractno+"%");
        }

        if (!keystring.equals("")) {
            sql += " and (cl.vin like  ?  or c.cname  like ? or cl.linkman like ? or  cl.contractno like  ?)";
            queryList.add("%"+keystring+"%");
            queryList.add("%"+keystring+"%");
            queryList.add("%"+keystring+"%");
            queryList.add("%"+keystring+"%");
        }
        if (!cname.equals(""))
        {
            sql+=" and c.cname like ?";
            queryList.add("%"+cname+"%");
        }
        if (!submituserid.equals("")){
            sql+=" and cl.submituserid=?";
            queryList.add(submituserid);
        }
        if (!claimno.equals("")){
            sql+=" and cl.claimno=?";
            queryList.add(claimno);
        }
        if (!claimpro.equals("")){
            sql+=" and cl.claimtype in ('"+claimpro.replace(",","','")+"')";
        }

        if (time1!=0){
            sql+=" and cl.ttime>?";
            queryList.add(time1);
        }
        if (time2!=0){
            sql+=" and cl.ttime<?";
            queryList.add(time2);
        }

        if (!status.equals("")){
            if (status.equals("未处理"))sql+=" and cl.status like '%已提交'";
            else if (status.equals("已处理"))sql+=" and (cl.status like '%已批准' or cl.status like '%已拒绝' or cl.status like '%已结案'  )";
            else if (status.equals("报告"))sql+=" and (cl.status !='草稿' and cl.status !='' and cl.status is not null)";
            else {
                sql+=" and cl.status=?";
                queryList.add(status);
            }

        }
        if (!claimtype.equals("")){
            sql+=" and cl.claimtype=?";
            queryList.add(claimtype);
        }
        if (!province.equals("")){
            sql+=" and cl.province=?";
            queryList.add(province);
        }
        if (!city.equals("")){
            sql+=" and cl.city=?";
            queryList.add(city);
        }
        if (!linkman.equals("")){
            sql+=" and cl.linkman like ?";
            queryList.add("%"+linkman+"%");
        }
        if (!dealerno.equals("")&&!dealerno.equals("zb")){
            sql+=" and cl.dealerno=?";
            queryList.add(dealerno);
        }else
        {
            sql += " and c.status!='草稿' ";
        }
        if (!icode.equals("")){
            sql+=" and cl.icode=?";
            queryList.add(icode);
        }
        if (!linkmobile.equals("")){
            sql+=" and cl.linkmobile=?";
            queryList.add(linkmobile);
        }
        if (!vin.equals("")){
            sql+=" and c.vin like ?";
            queryList.add("%"+vin+"%");
        }
        sql+=" order by cl.submittime desc ,cl.ttime desc ";
        String resultss="";
        List<Map<String, Object>> list=queryForList(jdbcTemplate,sql,queryList.toArray());
        resultss=GetRsultString(list,pageindex,pagesize);
        return  resultss;
    }
    public String getClaimCount(String data){

        String contractno="",claimtype="";


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
        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();else return GetErrorString(1,"参数错误！");
        if (jsonObject.keySet().contains("claimtype"))claimtype=jsonObject.get("claimtype").getAsString();else return GetErrorString(1,"参数错误！");

        String sql ="";
        sql="select status from  mt_claim  where contractno=? and claimtype=? ";

        String resultss="";
        Object[] ob=new Object[2];
        ob[0]=contractno;
        ob[1]=claimtype;
        List<Map<String, Object>> list=queryForList(jdbcTemplate,sql,ob);
        Map<String, Object>map;
        int finish=0,submit=0;
        for (int i=0;i<list.size();i++)
        {
            map=list.get(i);
            String claim_status=setting.NUllToSpace(map.get("status"));
            if (claim_status.equals("理赔已结案"))finish++;
            else   if (!claim_status.equals("草稿")&&!claim_status.contains("已拒绝"))submit++;
        }
        jsonObject=new JsonObject();
        jsonObject.addProperty("finish",finish);
        jsonObject.addProperty("submit",submit);

        return  jsonObject.toString();
    }
    public String getClaimRemark(String data){
        String claimno="";


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
        if (jsonObject.keySet().contains("claimno"))claimno=jsonObject.get("claimno").getAsString();else return GetErrorString(1,"");

        String sql ="";
        ;
        sql="select ck.tid,ck.checkremark as remark,ck.checkuserid,ck.claimno,FROM_UNIXTIME(ck.ttime,'%Y-%m-%d %H:%i:%s') as createtime,ck.status,u.username,u.truename from  mt_claimcheck ck LEFT OUTER JOIN usermember u on u.id=ck.checkuserid  where ck.claimno=? order by ttime ";

        String resultss="";
        List<Map<String, Object>> list=queryForList(jdbcTemplate,sql,claimno);
        jsonObject=new JsonObject();
        JsonObject  jsonObject1=new JsonObject();
        JsonArray jsonArray1=new JsonArray();
        JsonArray jsonArray2=new JsonArray();
        Map<String, Object> map;
        for (int i=0;i<list.size();i++)
        {
            map=list.get(i);
            jsonObject1=new JsonObject();
            jsonObject1.addProperty("createtime",map.get("createtime")+"");
            jsonObject1.addProperty("remark",map.get("remark")+"");
            jsonObject1.addProperty("tid",map.get("tid")+"");
            jsonObject1.addProperty("username",map.get("username")+"");
            jsonObject1.addProperty("truename",map.get("truename")+"");
            jsonObject1.addProperty("status",map.get("status")+"");
            if (map.get("status").toString().equals("理赔已提交"))jsonArray1.add(jsonObject1);else jsonArray2.add(jsonObject1);

        }
        jsonObject.add("submit",jsonArray1);
        jsonObject.add("check",jsonArray2);
        Gson gson = new Gson();
        resultss=gson.toJson(jsonObject);
        return  resultss;
    }

    public String checkClaim(String data){
        String claimno="";
        String status="",checkremark="";
        String checkuserid="";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String result="";
        float payamount=0;
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;
        if (jsonObject.keySet().contains("checkuserid"))checkuserid=jsonObject.get("checkuserid").getAsString();
        if (jsonObject.keySet().contains("claimno"))claimno=jsonObject.get("claimno").getAsString();else return GetErrorString(1,"");
        if (jsonObject.keySet().contains("status"))status=jsonObject.get("status").getAsString();else return GetErrorString(1,"");
        if (jsonObject.keySet().contains("checkremark"))checkremark=jsonObject.get("checkremark").getAsString();
        if (jsonObject.keySet().contains("payamount"))payamount=nullToZero( jsonObject.get("payamount").getAsString());
        String sql ="";
        ;int tt=getCurrenntTime();
        String contractno="",icode="",dealerno="";
        sql="select contractno,icode,dealerno from mt_claim where claimno=?";
        Map<String,Object>map=queryForMap(jdbcTemplate,sql,claimno);
        if (map!=null) {
            contractno = setting.NUllToSpace(map.get("contractno"));
            icode = setting.NUllToSpace(map.get("icode"));
            dealerno = setting.NUllToSpace(map.get("dealerno"));
        }
        Object[]args;
        if (!status.contains("理赔")&&!status.equals("补充材料"))status="理赔"+status;
        if (!contractno.equals(""))
        {
            sql="update mt_contract set claimstatus=? where contractno=?";
            args=new Object[2];
            args[0]=status;
            args[1]=contractno;
            jdbcTemplate.update(sql,args);
        }
        sql="update mt_claim set status=?,checkuserid=?,checktime="+tt+",checkremark=? where claimno=?";
        args=new Object[4];
        args[0]=status;
        args[1]=checkuserid;
        args[2]=checkremark;
        args[3]=claimno;

        int ii=jdbcTemplate.update(sql,args);
        if (status.contains("已结案")&&payamount!=0)//已结案的时候更新赔付金额和支付时间
        {
            sql="update mt_claim set payamount=?,paytime="+tt+"  where claimno=?";
            args=new Object[2];
            args[0]=payamount;
            args[1]=claimno;
            jdbcTemplate.update(sql,args);
        }
        String resultss="";
        if (ii==1) {
            sql="insert into mt_claimcheck (status,checkuserid,ttime,checkremark,claimno) values  (?,?,"+tt+",?,?)";
            args=new Object[4];
            args[0]=status;
            args[1]=checkuserid;
            args[2]=checkremark;
            args[3]=claimno;
            jdbcTemplate.update(sql,args);

            sql="select max(tid) as id from mt_claimcheck";
            int flag= jdbcTemplate.queryForObject(sql,int.class);
            resultss = "{\"errcode\":\"0\",\"claimno\":\"" + claimno + "\",\"tid\":\""+flag+"\"}";
            sql="select email from insurance where code=?";
            map=queryForMap(jdbcTemplate,sql,icode);
            String email="",content="";
            if (map!=null) {
                email = setting.NUllToSpace(map.get("email"));
            }
            if (status.contains("拒绝"))
                content="    您的理赔号为："+claimno+"的理赔审核被拒绝。拒绝原因为："+checkremark+"，<a href=\""+GetPathurl()+"background/index.html#/welcome\">直接点击链接查看</a>。如有疑问，请直接咨询"+email+"。";
            else  if (status.contains("补充材料"))
                content="    您的理赔号为："+claimno+"的理赔需要补充材料。原因为："+checkremark+"，<a href=\""+GetPathurl()+"background/index.html#/welcome\">直接点击链接查看</a> 。如有疑问，请直接咨询"+email+"。";
            else
                content="    您的理赔号为："+claimno+"的理赔审核已通过。点击链接查看"+GetPathurl()+"background/index.html#/welcome。如有疑问，请直接咨询"+email+"。";
            SendEmailDealer(dealerno,content,"理赔审核");
        }
        else resultss=GetErrorString(3,"提交不成功！");
        return  resultss;
    }
    public String addClaimpic(String data, String filetype, MultipartFile file ){
        String claimno="",userid="",pictype="";
        String picurl="";
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


        if (jsonObject.keySet().contains("claimno"))claimno=jsonObject.get("claimno").getAsString();else return  GetErrorString(1,"");
        if (jsonObject.keySet().contains("userid"))userid=jsonObject.get("userid").getAsString();
        if (jsonObject.keySet().contains("pictype"))pictype=jsonObject.get("pictype").getAsString();

        picurl=uploadJpg(file);
        String sql ="";
        sql="insert into mt_claimpic (tid,claimno,picurl,valid,userid,pictype,candel) values ( uuid(),?,?,1,?,?,1)";
        Object[] parms=new Object[4];
        parms[0]=claimno;
        parms[1]=picurl;
        parms[2]=userid;
        parms[3]=pictype;
        jdbcTemplate.update(sql,parms);
        String resultss="";
        sql="select tid,claimno,picurl,userid,pictype from mt_claimpic where claimno=? and picurl=?";
        parms=new Object[2];
        parms[0]=claimno;
        parms[1]=picurl;
        resultss=GetRsultString(sql,jdbcTemplate,parms);
        return  resultss;
    }
    public String getClaimpic(String data){
        String claimno="";

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
        if (jsonObject.keySet().contains("claimno"))claimno=jsonObject.get("claimno").getAsString();else return  GetErrorString(1,"");
        String sql ="";


        sql="select tid,claimno,picurl,pictype,candel from mt_claimpic where valid=1 and claimno=? order by createtime desc";

        String resultss="";
        resultss=GetRsultString(sql,jdbcTemplate,claimno);
        return  resultss;
    }
    public String deleteClaimpic(String data){
        String tid="";
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
        if (jsonObject.keySet().contains("tid"))tid=jsonObject.get("tid").getAsString();else return  GetErrorString(1,"");

        String     sql="update mt_claimpic set valid=0 where tid=?";

        String resultss="";
        if (jdbcTemplate.update(sql,tid)==1)resultss=GetErrorString(0,"");
        else resultss=GetErrorString(3,"提交不成功！");
        return  resultss;
    }
    public String getProvince(String data){

        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";

        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();

        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;

        String     sql="select * from province ";

        String resultss=GetRsultString(sql,jdbcTemplate);

        return  resultss;
    }
    public String getCity(String data){

        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String provincecode;
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;
        if (jsonObject.keySet().contains("provincecode"))provincecode=jsonObject.get("provincecode").getAsString();else return  GetErrorString(1,"");

        String     sql="select * from city where provincecode='"+provincecode+"' ";

        String resultss=GetRsultString(sql,jdbcTemplate);

        return  resultss;
    }
    public String claim_down(String data){
        String claimno="",contractno="",dealerno="",claimtype="",province="",icode="",vin="",act="";
        String city="",status="",linkman="",linkmobile="",userid="";
        int time1=0,time2=0;
        String submituserid="";
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
        if (jsonObject.keySet().contains("submituserid"))submituserid=jsonObject.get("submituserid").getAsString();
        if (jsonObject.keySet().contains("claimno"))claimno=jsonObject.get("claimno").getAsString();
        if (jsonObject.keySet().contains("time1"))time1=setting.StrToInt(jsonObject.get("time1").getAsString());
        if (jsonObject.keySet().contains("time2"))time2=setting.StrToInt(jsonObject.get("time2").getAsString());
        if (jsonObject.keySet().contains("status"))status=jsonObject.get("status").getAsString();
        if (jsonObject.keySet().contains("claimtype"))claimtype=jsonObject.get("claimtype").getAsString();
        if (jsonObject.keySet().contains("province"))province=jsonObject.get("province").getAsString();
        if (jsonObject.keySet().contains("city"))city=jsonObject.get("city").getAsString();
        if (jsonObject.keySet().contains("linkman"))linkman=jsonObject.get("linkman").getAsString();
        if (jsonObject.keySet().contains("dealerno"))dealerno=jsonObject.get("dealerno").getAsString();
        if (jsonObject.keySet().contains("icode"))icode=jsonObject.get("icode").getAsString();
        if (jsonObject.keySet().contains("linkmobile"))linkmobile=jsonObject.get("linkmobile").getAsString();
        if (jsonObject.keySet().contains("vin"))vin=jsonObject.get("vin").getAsString();
        if (jsonObject.keySet().contains("act"))act=jsonObject.get("act").getAsString();
        if (jsonObject.keySet().contains("userid"))userid=jsonObject.get("userid").getAsString();
        String sql ="",claimpro="";
       /* if (!userid.equals(""))
        {
            sql="select claimpro from usermember where id=?";
            Map<String,Object>map=queryForMap(jdbcTemplate,sql,userid);
            if (map!=null)claimpro=NUllToSpace(map.get("claimpro"));
        }*/
        List<Object> queryList=new ArrayList<Object>();
        sql="select (@i:=@i+1)   as   i,c.vin,c.brand,u.tel,c.Invoiceprice,c.Invoicedate,u.username as userid,FROM_UNIXTIME(cl.paytime,'%Y-%m-%d %H:%i:%s') as paytime,cl.*,FROM_UNIXTIME(cl.ttime,'%Y-%m-%d %H:%i:%s') as createtime,FROM_UNIXTIME(cl.submittime,'%Y-%m-%d %H:%i:%s') as submittime1,FROM_UNIXTIME(cl.checktime,'%Y-%m-%d %H:%i:%s') as claimtime,d.dealername,i.iname as insurancename,c.cname,c.dealerno as contract_dealerno \n" +
                ",CASE when c.dealerno=cl.dealerno then '同店理赔' else '跨店理赔' end as dealerstate\n" +
                "from  mt_claim cl LEFT OUTER JOIN dealer d on d.dealerno=cl.dealerno LEFT join insurance i on i.code=cl.icode LEFT join mt_contract c on c.contractno=cl.contractno left outer join usermember u on cl.submituserid=u.id ,(SELECT @i:=0) as i where 1=1 ";
        if (!contractno.equals(""))
        {
            sql+=" and cl.contractno like ?";
            queryList.add("%"+contractno+"%");
        }
        if (!submituserid.equals("")){
            sql+=" and cl.submituserid=?";
            queryList.add(submituserid);
        }
        if (!claimno.equals("")){
            sql+=" and cl.claimno=?";
            queryList.add(claimno);
        }
        if (time1!=0){
            sql+=" and cl.ttime>?";
            queryList.add(time1);
        }
        if (time2!=0){
            sql+=" and cl.ttime<?";
            queryList.add(time2);
        }
        if (!claimpro.equals("")){
            sql+=" and cl.claimtype in ('"+claimpro.replace(",","','")+"')";
        }
        if (!status.equals("")){
            if (status.equals("未处理"))sql+=" and cl.status like '%已提交'";
            else if (status.equals("已处理"))sql+=" and (cl.status like '%已批准' or cl.status like '%已拒绝')";
            else {
                sql+=" and cl.status=?";
                queryList.add(status);
            }

        }
        if (!claimtype.equals("")){
            sql+=" and cl.claimtype=?";
            queryList.add(claimtype);
        }
        if (!province.equals("")){
            sql+=" and cl.province=?";
            queryList.add(province);
        }
        if (!city.equals("")){
            sql+=" and cl.city=?";
            queryList.add(city);
        }
        if (!linkman.equals("")){
            sql+=" and cl.linkman like ?";
            queryList.add("%"+linkman+"%");
        }
        if (!dealerno.equals("")&&!dealerno.equals("zb")){
            sql+=" and cl.dealerno=?";
            queryList.add(dealerno);
        }else
        {
            sql += " and c.status!='草稿' ";
        }
        if (!icode.equals("")){
            sql+=" and cl.icode=?";
            queryList.add(icode);
        }
        if (!linkmobile.equals("")){
            sql+=" and cl.linkmobile=?";
            queryList.add(linkmobile);
        }
        if (!vin.equals("")){
            sql+=" and c.vin like ?";
            queryList.add("%"+linkmobile+"%");
        }

        String resultss="";
        Map<String,Object> title=new LinkedHashMap<>();
        title.put("i","No. 序号");
        title.put("claimno","Claim No. 理赔号");
        title.put("claimtime","Claim Date 理赔日期");
        title.put("claimtype","Claim Type 理赔类型");
        if (!act.equals("审核员"))
        {
            title.put("cname","Customer Name 客户姓名");
        }

        title.put("contractno","Contract No. 服务凭证号");
        title.put("createtime","Creation Date 创建日期");
        title.put("insurancename","Ins. Company 保险公司");
        title.put("dealername","Dealer Name 经销商名称");
        title.put("dealerno","Dealer Code 经销商代码");
        title.put("claimamount","Amount 理赔金额");
        title.put("userid","User 提交人");
        title.put("submittime1","Submit time 最后提交时间");
        title.put("status","Claim Status 理赔状态");
        title.put("checkremark","Reject Reason 拒绝原因");
        title.put("payamount","Claim Amount 实际理赔金额");
        title.put("paytime","Claim Time 赔付时间");

        resultss= toexcel.createCSVFile(queryForList(jdbcTemplate,sql,queryList.toArray()),title,"","");
        return  resultss;
    }

    public List<Map<String,Object>> getClaimPic(String claimnos) {

        String sql = "select * from mt_claimpic where valid=1 and claimno in ('" + String.join("','", claimnos.split(",")) + "')";
        sql += " order by claimno , createtime";

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        return list;
    }

    public List<Map<String,Object>> selectCheckPicList(int tid,String claimno ) {
        String sql = "select * from mt_claimcheckpic where 1=1 ";
        List<Object> queryList = new ArrayList<Object>();
        if (tid > 0) {
            sql += " and tid = ?";
            queryList.add(tid);
        }
        if (claimno != null && !"".equals(claimno)) {
            sql += " and claimno = ?";
            queryList.add(claimno);
        }
        sql += " order by claimno ,tid, createtime";

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql, queryList.toArray());
        return list;
    }
    public int insertCheckPic(int tid,String claimno,String name,String picurl) {
        int flag=0;
        try {
            String sql = "insert into   mt_claimcheckpic (tid,claimno,name,picurl) values(?,?,?,?) ";
            flag = jdbcTemplate.update(sql, new Object[]{tid, claimno, name, picurl});

            sql="select max(id) as id from mt_claimcheckpic";
            flag= jdbcTemplate.queryForObject(sql,int.class);
        }
        catch (Exception ex) {
            logs.error("理赔审核附件错误", ex);
            ex.printStackTrace();
        }
        return  flag;
    }
    public  int deleteCheckPicByID(int id){
        String sql ="delete from mt_claimcheckpic where id=? ";
        int target=jdbcTemplate.update(sql,id);
        return  target;
    }
}
