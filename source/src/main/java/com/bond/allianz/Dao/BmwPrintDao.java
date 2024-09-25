package com.bond.allianz.Dao;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Repository
public class BmwPrintDao extends BaseDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    QrCodeUtil qrCodeUtil;

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
         result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;

        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();else return  GetErrorString(1,"");
        //if (jsonObject.keySet().contains("codeurl"))codeurl=jsonObject.get("codeurl").getAsString();
        codeurl="wxcheck/wxCheck.html?contractno="+contractno;
        StringBuilder allss = new StringBuilder();
        String dealerno="";
        String sqlstring="select *,(select FROM_UNIXTIME(billbmw.ttime,'%Y-%m-%d') from billbmw where contractno=contract.contractno and in_or_out=-1 and (tradetype='订单支付消费' or tradetype='支付') ORDER BY ttime desc LIMIT 0,1 ) as paytime from contract where contractno=?";
        Map<String,Object> map=queryForMap(jdbcTemplate,sqlstring,contractno);
        String brand="",vehicletype="",vin="",Invoiceno="",Invoicedate="",guideprice="",Invoiceprice="",pname="";
        String cname="",IdNo="",caddress="",insurancecompany="",Businessinsurancepolicyno="",insurancepolicyno="",productid="",companyname="",cars="";
        String contact_person="",contact_tel="",originInvoicedate="",detail_productid="",keytype="",activity_id="",active_productid="";
        int iscompany=0,carkey=0,isnewcar=1;
        String retailprice="0",rti_retailprice="0",mileage="",tire_retailprice="0",carkey_retailprice="0",group_retailprice="0";
        double paid_amount=0,paid_amount_rti=0,paid_amount_key=0,paid_amount_tire=0,paid_amount_group=0;
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
            originInvoicedate=setting.NUllToSpace(map.get("originInvoicedate"));
            activity_id=setting.NUllToSpace(map.get("activity_id"));
            active_productid=setting.NUllToSpace(map.get("active_productid"));
            retailprice=setting.NUllToSpace(map.get("retailprice"));
            mileage=setting.NUllToSpace(map.get("mileage"));
            keytype=setting.NUllToSpace(map.get("keytype"));
            isnewcar=setting.StrToInt(map.get("isnewcar")+"");
            paid_amount=setting.NullToZero(map.get("paid_amount")+"");
            paid_amount_rti=setting.NullToZero(map.get("paid_amount_rti")+"");
            paid_amount_key=setting.NullToZero(map.get("paid_amount_key")+"");
            paid_amount_tire=setting.NullToZero(map.get("paid_amount_tire")+"");
            paid_amount_group=setting.NullToZero(map.get("paid_amount_group")+"");
        }
        int print_version=0;
        Date date=parseDate("2023-07-01 00:00:00","yyyy-MM-dd HH:mm:ss");
        try
        {
            Date submit_date=parseDate(paytime+" 12:00:00","yyyy-MM-dd HH:mm:ss");

            if (submit_date.after(date))print_version=1;
        }catch (Exception e)
        {

        }
        String active_type="",amount_show="",print_remark="";
        if (!activity_id.equals(""))
        {
            sqlstring="select * from active_main where activity_id=?";
            map=queryForMap(jdbcTemplate,sqlstring,activity_id);
            if (map!=null)
            {
                active_type=NUllToSpace(map.get("type")).trim();
                amount_show=NUllToSpace(map.get("amount_show")).trim();
                print_remark=NUllToSpace(map.get("print_remark")).trim();
            }
        }
        sqlstring="select * from insuranceproduct where productid=?";
        map=queryForMap(jdbcTemplate,sqlstring,productid);
        int rti=0,tire=0;
        String p_cars="",groupname="",keyname="",rtiname="",tirename="";
        boolean keygroup=false,rtigroup=false,tiregroup=false;
        if (map!=null)
        {
            detail_productid=NUllToSpace(map.get("detail_productid"));
            carkey=setting.StrToInt(map.get("carkey")+"");
            rti=setting.StrToInt(map.get("rti")+"");
            tire=setting.StrToInt(map.get("tire")+"");
            p_cars=setting.NUllToSpace(map.get("cars"));
            groupname=setting.NUllToSpace(map.get("groupname"));
            if (groupname.equals("钥匙")) {
                keyname = setting.NUllToSpace(map.get("pname"));
                carkey_retailprice=setting.NUllToSpace(map.get("retailprice"));
            }
            else   if (groupname.equals("悦然焕新")) {
                rtiname = setting.NUllToSpace(map.get("pname"));

            }
            else   if (groupname.equals("轮胎")) {
                tirename = setting.NUllToSpace(map.get("pname"));
                tire_retailprice=setting.NUllToSpace(map.get("retailprice"));
            }else
            {
                group_retailprice=setting.NUllToSpace(map.get("retailprice"));
            }

            if (setting.NUllToSpace(map.get("cars")).equals("售后新换胎"))return  contractPrintBmwTire(contractno,detail_productid);
        }

        if (rti!=0&&isnewcar==1&&!group_retailprice.equals(""))//组合套餐获取rti的价格
        {
            sqlstring="select agentprice,retailprice from bmwproduct where  productid=?  and (cars=? ) and (vehicletype=? )";
            Object[] queryList=new Object[3];
            queryList[0]="BMW01";
            queryList[1]=cars;
            queryList[2]=vehicletype;
            Map<String,Object> map2=queryForMap(jdbcTemplate,sqlstring,queryList);
            if (map2!=null)
            {

                rti_retailprice=setting.NUllToSpace(map2.get("retailprice"));
            }
        }
        List<Map<String, Object>>list;
        if (!detail_productid.equals(""))
        {
            sqlstring="select * from insuranceproduct where productid in ('"+detail_productid.replace(",","','")+"')";
            list = queryForList(jdbcTemplate, sqlstring);
            //包含轮胎的都不算做常用套餐2024-07-20
           if ((groupname.equals("活动套餐")||groupname.equals("常用套餐"))&&active_productid.equals("")&&!pname.contains("轮胎"))
           {
               keygroup=true;
               rtigroup=true;
               tiregroup=true;
               group_retailprice=retailprice;
           }

            for (int i = 0; i < list.size(); i++) {

                map = list.get(i);
                if (setting.NUllToSpace(map.get("groupname")).equals("钥匙")) {
                    keyname = setting.NUllToSpace(map.get("pname"));
                   carkey_retailprice=setting.NUllToSpace(map.get("retailprice"));
                  // if (keygroup)group_retailprice=(setting.NullToZero(group_retailprice)+setting.NullToZero(carkey_retailprice))+"";
                }
                else   if (setting.NUllToSpace(map.get("groupname")).equals("悦然焕新")) {
                    rtiname = setting.NUllToSpace(map.get("pname"));
                 // if (rtigroup)  group_retailprice=(setting.NullToZero(group_retailprice)+setting.NullToZero(rti_retailprice))+"";

                }
                else   if (setting.NUllToSpace(map.get("groupname")).equals("轮胎")) {
                    tirename = setting.NUllToSpace(map.get("pname"));
                    tire_retailprice=setting.NUllToSpace(map.get("retailprice"));
                   // if (tiregroup)group_retailprice=(setting.NullToZero(group_retailprice)+setting.NullToZero(tire_retailprice))+"";

                }
               String sub_detail_productid=setting.NUllToSpace(map.get("detail_productid"));
                if (!sub_detail_productid.equals(""))
                {
                    group_retailprice=setting.NUllToSpace(map.get("retailprice"));
                    sqlstring="select * from insuranceproduct where productid in ('"+sub_detail_productid.replace(",","','")+"')";
                    List<Map<String, Object>> list1 = queryForList(jdbcTemplate, sqlstring);
                    for (int ii = 0; ii < list1.size(); ii++)
                    {
                        Map<String, Object>   map11 = list1.get(ii);
                        if (setting.NUllToSpace(map11.get("groupname")).equals("钥匙")) {
                            keyname = setting.NUllToSpace(map11.get("pname"));
                            keygroup=true;
                        }
                        else   if (setting.NUllToSpace(map11.get("groupname")).equals("悦然焕新")) {
                            rtiname = setting.NUllToSpace(map11.get("pname"));
                            rtigroup=true;
                            group_retailprice=(setting.NullToZero(group_retailprice)+setting.NullToZero(rti_retailprice))+"";

                        }
                        else   if (setting.NUllToSpace(map11.get("groupname")).equals("轮胎")) {
                            tirename = setting.NUllToSpace(map11.get("pname"));
                            tiregroup=true;


                        }
                    }
                }
            }
        }
        group_retailprice=doubletostring(setting.NullToZero(group_retailprice));
        rti_retailprice=doubletostring(setting.NullToZero(rti_retailprice));
        carkey_retailprice=doubletostring(setting.NullToZero(carkey_retailprice));
        tire_retailprice=doubletostring(setting.NullToZero(tire_retailprice));

        sqlstring="select * from dealer where dealerno=?";
        map=queryForMap(jdbcTemplate,sqlstring,dealerno);
        String dealername="",address="",tel="";
        if (map!=null)
        {
            dealername=setting.NUllToSpace(map.get("dealername"));
            address=setting.NUllToSpace(map.get("address"));
            tel=setting.NUllToSpace(map.get("tel"));
        }

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
        allss.append("<hr/>");
        allss.append("<div align=\"right\"><img src=\""+pathurl+"images/"+brand.toLowerCase()+".png\"    alt=\"\"/></div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div align=\"left\" class=\"print-body\">尊敬的"+brand+"车主：\n" );
        allss.append("<br>");
        allss.append("<br>");
        allss.append("&nbsp;&nbsp;&nbsp;&nbsp;您好！\n");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("&nbsp;&nbsp;&nbsp;&nbsp;感谢您选择了"+brand+"保障服务！");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("&nbsp;&nbsp;&nbsp;&nbsp;"+brand+
                "保障服务致力于服务每一位给予我们信赖的车主，为您的爱车提供更贴心、更丰富、更全面的保障。为了更好地守护您的爱车，伴您无忧出行，尽享驾驶乐趣，选择我们的"+brand+"保障服务产品是您省心又省钱的明智决定。\n");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("&nbsp;&nbsp;&nbsp;&nbsp;"+brand+"保障服务产品*涵盖了以下服务：\n");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("&nbsp;&nbsp;&nbsp;&nbsp;<b>悦然焕新服务</b>——助你无畏前路，放胆驰骋\n");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("&nbsp;&nbsp;&nbsp;&nbsp;"+
                "若爱车遭遇意外事故或者被盗等情形而造成的车辆全损或推定全损，您只需回到原授权经销商处申请置换一台 "+brand+" 新车。我们将补贴您机动车辆保险赔付之外的车辆折旧损失，更在此基础上给予您一定金额的新车购置费用补贴，包括车辆购置税、机动车辆保险保费及其他相关费用。\n" );
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("&nbsp;&nbsp;&nbsp;&nbsp;<b>钥匙保障服务</b>——有备有换，才能有备无患\n" );
        allss.append("<br>");
        allss.append("<br>");
        allss.append("&nbsp;&nbsp;&nbsp;&nbsp;  对于不同车系和车型所适配的车辆钥匙，在至多长达3年的服务期限内，若爱车的钥匙发生意外丢失或被盗，您只需向原授权经销商提出重置钥匙的服务申请，即可获得与原车钥匙同型号、同规格的全新原厂车钥匙。\n" +
                "\n" );
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("&nbsp;&nbsp;&nbsp;&nbsp;"+   "<b>轮胎保障服务</b>——心之所向，皆为坦途\n" );
        allss.append("<br>");
        allss.append("<br>");
        allss.append("&nbsp;&nbsp;&nbsp;&nbsp;"+    "在日常驾驶和使用爱车的过程中，如遇新车轮胎或是您在"+brand+"售后服务处购买的星标轮胎发生鼓包或爆胎，您可立即返回原授权经销商处置换与其同品牌、同规格（或同等级别）的新轮胎。\n" );
        allss.append("<br>");
        allss.append("<br>");
        allss.append("&nbsp;&nbsp;&nbsp;&nbsp;"+
                "*具体适用车辆、可购产品及保障内容详情请咨询您所在地的"+brand+"授权经销商，并以"+brand+"授权经销商店内相关保障服务的最新产品信息及"+brand+"保障服务合同所载条款为准。\n");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("&nbsp;&nbsp;&nbsp;&nbsp;"+
                "再次真诚感谢您对"+brand+"保障服务的关注和支持，我们也将以更贴心、更便捷、更持久的服务回馈于您，为您和您的爱车保驾护航！\n</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        codeurl=qrCodeUtil.createQrCode(pathurl+codeurl);
        allss.append("<hr/>");
        StringBuilder ss=new StringBuilder();
        String title=brand+"保障服务合同";
        allss.append("<div align=\"center\" class=\"print-title\">" + title + "</div>");
        allss.append("<br>");
        allss.append("<div align=\"center\" class=\"print-body\">I.\t服务合同信息页</div>");
        allss.append("<div align=\"right\"><img src=\""+pathurl+codeurl+"\"  style=\"width: 100px;\"  alt=\"\"/></div>");
        allss.append("<div align=\"right\" size=\"2\">扫描二维码，验证服务合同与保障信息</div>");
        allss.append("<div align=\"right\">合同编号：【"+contractno+"】</div>");
        allss.append("<br>");
        allss.append("<div align=\"left\" class=\"print-body\">车主：<u>"+cname+"</u></div>");
        allss.append("<br>");
        allss.append("<div align=\"left\" class=\"print-body\">授权经销商：<u>"+dealername+"</u></div>");
        allss.append("<br>");
        allss.append("<div align=\"left\" class=\"print-body\">&nbsp;&nbsp;&nbsp;&nbsp;通过签署本"+brand+"保障服务合同（以下简称“服务合同”），您同意接受悦然焕新服务、钥匙保障服务和/或轮胎保障服务（依适用，以下单独或合称为“服务”或“保障服务”）。为了便于向您提供更贴心的服务和更全面的保障，请务必仔细阅读本服务合同条款和条件，并准确提供下列信息：</div>");
        allss.append("<br>");
         allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" colspan=\"4\" bgcolor=\"#002060\"><font color=\"#fff\">一、享权车辆与保障配件信息</font> </td></tr>");
        allss.append("<tr><td colspan=\"4\" ><b>享权车辆</b></td></tr>");
        allss.append("<tr><td width=\"25%\">品   牌：</td><td width=\"25%\">"+brand+"</td>");
        allss.append("<td width=\"25%\">车   型：</td><td width=\"25%\">"+vehicletype+"</td></tr>");

        allss.append("<tr><td>车架号码（VIN）：</td><td>"+vin+"</td>");
        allss.append("<td>车辆用途：</td><td>     非   营   运</td></tr>");
        if (isnewcar==2) allss.append("<tr><td>车辆购买时间（首次购车发票日期）</td><td>"+originInvoicedate+"</td>");
        else allss.append("<tr><td>车辆购买时间（首次购车发票日期）</td><td>"+Invoicedate+"</td>");

        allss.append("<td>当前里程表读数：</td><td>"+mileage+"</td></tr>");
        allss.append("<tr><td>车辆官方建议零售价\n" +
                "（仅适用于新车）：\n</td><td>"+formatMoney(guideprice)+"</td>");
        allss.append("<td>新车购车发票价\n" +
                "或二手车销售发票价：\n</td><td>"+formatMoney(Invoiceprice)+"</td></tr>");
        if (carkey!=0)
        {
            allss.append("<tr><td colspan=\"4\" ><b>享权车辆钥匙</b></td></tr>");
            allss.append("<tr><td width=\"25%\">钥匙型号和规格：</td><td colspan=\"3\" >"+keyname+"</td></tr>");
        }
        allss.append("</table>");
        if (tire!=0)
        {
            allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
            allss.append("<tr><td colspan=\"5\" ><b>享权车辆轮胎</b></td></tr>");


            sqlstring="select * from tiredetail where contractno=?";
            list = queryForList(jdbcTemplate, sqlstring,contractno);
            String[]tbrand=new String[4];
            String[]ttype=new String[4];
            String[]tdot=new String[4];
            for (int i=0;i<list.size();i++)
            {
                map=list.get(i);
                int ii=i;
                String position=map.get("position")+"";
                if (position.equals(""))
                {
                    ii=i;
                }else if (position.contains("左前"))ii=0;
                else if (position.contains("右前"))ii=1;
                else if (position.contains("左后"))ii=2;
                else if (position.contains("右后"))ii=3;


                tbrand[ii]=map.get("brandname")+"";
                ttype[ii]=map.get("tiretype")+"";
                tdot[ii]=map.get("dot")+"";
            }


            allss.append("<tr><td width=\"20%\">轮胎位置:</td><td width=\"20%\" align=\"center\" >左前轮胎</td><td width=\"20%\" align=\"center\" >右前轮胎</td><td width=\"20%\" align=\"center\" >左后轮胎</td><td width=\"20%\" align=\"center\" >右后轮胎</td></tr>");
            allss.append("<tr><td width=\"20%\">轮胎品牌:</td><td width=\"20%\" align=\"center\" >"+tbrand[0]+"</td><td width=\"20%\" align=\"center\" >"+tbrand[1]+"</td><td width=\"20%\" align=\"center\" >"+tbrand[2]+"</td><td width=\"20%\" align=\"center\" >"+tbrand[3]+"</td></tr>");
            allss.append("<tr><td width=\"20%\">轮胎型号:</td><td width=\"20%\" align=\"center\" >"+ttype[0]+"</td><td width=\"20%\" align=\"center\" >"+ttype[1]+"</td><td width=\"20%\" align=\"center\" >"+ttype[2]+"</td><td width=\"20%\" align=\"center\" >"+ttype[3]+"</td></tr>");
            allss.append("<tr><td width=\"20%\">DOT码:</td><td width=\"20%\" align=\"center\" >"+tdot[0]+"</td><td width=\"20%\" align=\"center\" >"+tdot[1]+"</td><td width=\"20%\" align=\"center\" >"+tdot[2]+"</td><td width=\"20%\" align=\"center\" >"+tdot[3]+"</td></tr>");
            allss.append("</table>");

        }
        allss.append("<br>");


        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\" colspan=\"2\"><font color=\"#fff\" >二、服务提供方信息：</font> </td></tr>");

        allss.append("<tr><td width=\"25%\">授权经销商名称：</td><td>"+dealername+"</td></tr>");
        allss.append("<tr><td width=\"25%\">地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址：</td><td>"+address+"</td></tr>");
        allss.append("<tr><td width=\"25%\">电&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;话：</td><td>"+tel+"</td></tr>");
    //    allss.append("<tr><td width=\"25%\">名称：</td><td>安联世合国际救援服务（北京）有限公司（以下简称“安联世合”）及/或安联世合指定的服务提供商（以下简称“服务提供方”）</td></tr>");
     //   allss.append("<tr><td width=\"25%\">地址：</td><td></td></tr>");
      //  allss.append("<tr><td width=\"25%\">电话：</td><td></td></tr>");

        allss.append("</table>");
        allss.append("<br>");


        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\" colspan=\"2\"><font color=\"#fff\" >三、车主信息：</font> </td></tr>");

        if (companyname.equals(""))companyname=cname;
        if (iscompany==0)
            allss.append("<tr><td >姓名/企业名称：</td><td>"+cname+"</td></tr>");

        else {
            allss.append("<tr><td >姓名/企业名称：</td><td>"+companyname+"</td></tr>");
            allss.append("<tr><td >办理人姓名：</td><td>"+contact_person+"</td></tr>");
            allss.append("<tr><td >办理人电话：</td><td>"+contact_tel+"</td></tr>");

        }
        allss.append("<tr><td >身份证号码或统一社会信用代码：</td><td>"+IdNo+"</td></tr>");
        allss.append("<tr><td >地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址：</td><td>"+foramtss(30,caddress)+"</td></tr>");
        allss.append("</table>");
        allss.append("<br>");

        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\" colspan=\"2\"><font color=\"#fff\" >四、享权车辆机动车辆保险信息：</font> </td></tr>");
        allss.append("<tr><td >机动车辆保险公司：</td><td>"+insurancecompany+"</td></tr>");
        allss.append("<tr><td >机动车交通事故责任强制保险保单号码：</td><td>"+insurancepolicyno+"</td></tr>");
        allss.append("<tr><td >机动车辆商业保险保单号码：</td><td>"+Businessinsurancepolicyno+"</td></tr>");
        allss.append("</table>");
        allss.append("<br>");

        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\" colspan=\"5\"><font color=\"#fff\" >五、保障服务产品与内容：</font> </td></tr>");
        allss.append("<tr><td width=\"25%\" rowspan=\"2\" style=\"text-align:center;vertical-align:middle;\">保障服务产品类别</td>");
        allss.append("<td width=\"25%\" style=\"text-align:center;vertical-align:middle;\" rowspan=\"2\">保障服务产品选项</td>");
        allss.append("<td  style=\"text-align:center;vertical-align:middle;\" colspan=\"2\">服务期限</td>");
        allss.append("<td  style=\"text-align:center;vertical-align:middle;\" rowspan=\"2\">建议零售价</td>");

        if (paid_amount!=0||!amount_show.equals(""))   allss.append("<td  style=\"text-align:center;vertical-align:middle;\" rowspan=\"2\">实收金额</td>");
        allss.append("</tr>");
        allss.append("<tr><td width=\"14%\" style=\"text-align:center;vertical-align:middle;\" >起始日期</td>");
        allss.append("<td style=\"text-align:center;vertical-align:middle;\">终止日期</td></tr>");
        allss.append("<tr><td rowspan=\"2\">悦然焕新服务\n" +
                "（机动车置换服务）\n</td>");
        allss.append("<td>新车悦然焕新</td>");
        if (p_cars.equals("新车")&&rti!=0&&!rtigroup)
        {
            if (paid_amount!=0||!amount_show.equals(""))
            {
                if (!amount_show.equals(""))
                    allss.append("<td>"+paytime+"</td><td>"+addYear(Invoicedate,rti)+"</td><td style=\"text-align:center;vertical-align:middle;\">"+rti_retailprice+"</td><td style=\"text-align:center;vertical-align:middle;\">"+amount_show+"</td></tr>");
                    else
                allss.append("<td>"+paytime+"</td><td>"+addYear(Invoicedate,rti)+"</td><td style=\"text-align:center;vertical-align:middle;\">"+rti_retailprice+"</td><td style=\"text-align:center;vertical-align:middle;\">"+format_paidamount(paid_amount_rti)+"</td></tr>");
            }

            else
                allss.append("<td>"+paytime+"</td><td>"+addYear(Invoicedate,rti)+"</td><td style=\"text-align:center;vertical-align:middle;\">"+rti_retailprice+"</td></tr>");
        }else
        {
            if (paid_amount!=0||!amount_show.equals(""))
            allss.append("<td></td><td></td><td></td><td></td></tr>");
            else
            allss.append("<td></td><td></td><td></td></tr>");
        }
        allss.append("<tr><td>二手车悦然焕新</td>");
        if (p_cars.equals("二手车")&&rti!=0&&!rtigroup)
        {
            if (paid_amount!=0||!amount_show.equals(""))
                if (!amount_show.equals(""))
                    allss.append("<td>"+paytime+"</td><td>"+addYear(Invoicedate,rti)+"</td><td style=\"text-align:center;vertical-align:middle;\">"+retailprice+"</td><td style=\"text-align:center;vertical-align:middle;\">"+amount_show+"</td></tr>");
                else
                    allss.append("<td>"+paytime+"</td><td>"+addYear(Invoicedate,rti)+"</td><td style=\"text-align:center;vertical-align:middle;\">"+retailprice+"</td><td style=\"text-align:center;vertical-align:middle;\">"+format_paidamount(paid_amount)+"</td></tr>");
            else
            allss.append("<td>"+paytime+"</td><td>"+addYear(paytime,rti)+"</td><td style=\"text-align:center;vertical-align:middle;\">"+retailprice+"</td></tr>");
        }else
        {
            if (paid_amount!=0||!amount_show.equals(""))
                allss.append("<td></td><td></td><td></td><td></td></tr>");
            else
            allss.append("<td></td><td></td><td></td></tr>");
        }
        allss.append("<tr><td rowspan=\"5\">钥匙保障服务\n" +
                "（机动车辆钥匙重置服务）\n</td>");
        allss.append("<td>新车标准钥匙 1把</td>");
        if (p_cars.equals("新车")&&keyname.contains("标准钥匙")&&!keygroup&&keyname.contains("1把"))
        {
            if (paid_amount!=0||!amount_show.equals(""))
                if (!amount_show.equals(""))
                    allss.append("<td>"+paytime+"</td><td>"+addYear(Invoicedate,carkey)+"</td><td style=\"text-align:center;vertical-align:middle;\">"+carkey_retailprice+"</td><td style=\"text-align:center;vertical-align:middle;\">"+amount_show+"</td></tr>");
                   else
                  allss.append("<td>"+paytime+"</td><td>"+addYear(Invoicedate,carkey)+"</td><td style=\"text-align:center;vertical-align:middle;\">"+carkey_retailprice+"</td><td style=\"text-align:center;vertical-align:middle;\">"+format_paidamount(paid_amount_key)+"</td></tr>");
            else
                allss.append("<td>"+paytime+"</td><td>"+addYear(Invoicedate,carkey)+"</td><td style=\"text-align:center;vertical-align:middle;\">"+carkey_retailprice+"</td></tr>");
        }else
        {
            if (paid_amount!=0||!amount_show.equals(""))
                allss.append("<td></td><td></td><td></td><td></td></tr>");
            else
            allss.append("<td></td><td></td><td></td></tr>");
        }
        allss.append("<tr><td>新车标准钥匙 2把</td>");
        if (p_cars.equals("新车")&&keyname.contains("钥匙多项选择")&&!keygroup&&keytype.contains("2把"))
        {
            if (paid_amount!=0||!amount_show.equals(""))
                if (!amount_show.equals(""))
                    allss.append("<td>"+paytime+"</td><td>"+addYear(Invoicedate,carkey)+"</td><td style=\"text-align:center;vertical-align:middle;\">"+carkey_retailprice+"</td><td style=\"text-align:center;vertical-align:middle;\">"+amount_show+"</td></tr>");
                else
                allss.append("<td>"+paytime+"</td><td>"+addYear(Invoicedate,carkey)+"</td><td style=\"text-align:center;vertical-align:middle;\">"+carkey_retailprice+"</td><td style=\"text-align:center;vertical-align:middle;\">"+format_paidamount(paid_amount_key)+"</td></tr>");
            else
            allss.append("<td>"+paytime+"</td><td>"+addYear(Invoicedate,carkey)+"</td><td style=\"text-align:center;vertical-align:middle;\">"+carkey_retailprice+"</td></tr>");
        }else
        {
            if (paid_amount!=0||!amount_show.equals(""))
                allss.append("<td></td><td></td><td></td><td></td></tr>");
            else
            allss.append("<td></td><td></td><td></td></tr>");
        }
        allss.append("<tr><td>新车标准钥匙\n" +
                "与NFC钥匙各1把\n</td>");
        if (p_cars.equals("新车")&&keyname.contains("钥匙多项选择")&&!keygroup&&keytype.contains("标准钥匙+NFC"))
        {
            if (paid_amount!=0||!amount_show.equals(""))
                if (!amount_show.equals(""))
                    allss.append("<td>"+paytime+"</td><td>"+addYear(Invoicedate,carkey)+"</td><td style=\"text-align:center;vertical-align:middle;\">"+carkey_retailprice+"</td><td style=\"text-align:center;vertical-align:middle;\">"+amount_show+"</td></tr>");
                else
                allss.append("<td>"+paytime+"</td><td>"+addYear(Invoicedate,carkey)+"</td><td style=\"text-align:center;vertical-align:middle;\">"+carkey_retailprice+"</td><td style=\"text-align:center;vertical-align:middle;\">"+format_paidamount(paid_amount_key)+"</td></tr>");
            else
            allss.append("<td>"+paytime+"</td><td>"+addYear(Invoicedate,carkey)+"</td><td style=\"text-align:center;vertical-align:middle;\">"+carkey_retailprice+"</td></tr>");
        }else
        {
            if (paid_amount!=0||!amount_show.equals(""))
                allss.append("<td></td><td></td><td></td><td></td></tr>");
            else
            allss.append("<td></td><td></td><td></td></tr>");
        }
        allss.append("<tr><td>新车智能触控钥匙 1把</td>");
        if (p_cars.equals("新车")&&keyname.contains("钥匙多项选择")&&!keygroup&&keytype.contains("智能触控钥匙"))
        {
            if (paid_amount!=0||!amount_show.equals(""))
                if (!amount_show.equals(""))
                    allss.append("<td>"+paytime+"</td><td>"+addYear(Invoicedate,carkey)+"</td><td style=\"text-align:center;vertical-align:middle;\">"+carkey_retailprice+"</td><td style=\"text-align:center;vertical-align:middle;\">"+amount_show+"</td></tr>");
                else
                allss.append("<td>"+paytime+"</td><td>"+addYear(Invoicedate,carkey)+"</td><td style=\"text-align:center;vertical-align:middle;\">"+carkey_retailprice+"</td><td style=\"text-align:center;vertical-align:middle;\">"+format_paidamount(paid_amount_key)+"</td></tr>");
            else
            allss.append("<td>"+paytime+"</td><td>"+addYear(Invoicedate,carkey)+"</td><td style=\"text-align:center;vertical-align:middle;\">"+carkey_retailprice+"</td></tr>");
        }else
        {
            if (paid_amount!=0||!amount_show.equals(""))
                allss.append("<td></td><td></td><td></td><td></td></tr>");
            else
            allss.append("<td></td><td></td><td></td></tr>");
        }
        allss.append("<tr><td>旧车智能触控钥匙 1把</td>");
        if (p_cars.equals("旧车")&&keyname.contains("智控钥匙")&&!keygroup)
        {
            if (paid_amount!=0||!amount_show.equals(""))
                if (!amount_show.equals(""))
                    allss.append("<td>"+paytime+"</td><td>"+addYear(Invoicedate,carkey)+"</td><td style=\"text-align:center;vertical-align:middle;\">"+carkey_retailprice+"</td><td style=\"text-align:center;vertical-align:middle;\">"+amount_show+"</td></tr>");
                else
                allss.append("<td>"+paytime+"</td><td>"+addYear(Invoicedate,carkey)+"</td><td style=\"text-align:center;vertical-align:middle;\">"+carkey_retailprice+"</td><td style=\"text-align:center;vertical-align:middle;\">"+format_paidamount(paid_amount_key)+"</td></tr>");
            else
            allss.append("<td>"+paytime+"</td><td>"+addYear(paytime,carkey)+"</td><td style=\"text-align:center;vertical-align:middle;\">"+carkey_retailprice+"</td></tr>");
        }else
        {
            if (paid_amount!=0||!amount_show.equals(""))
                allss.append("<td></td><td></td><td></td><td></td></tr>");
            else
            allss.append("<td></td><td></td><td></td></tr>");
        }
        allss.append("<tr><td>轮胎保障服务\n" +
                "（轮胎置换服务）\n</td>");
        allss.append("<td>新车轮胎保障</td>");
        if (p_cars.equals("新车")&&tire!=0&&!tiregroup)
        {
            if (paid_amount!=0||!amount_show.equals(""))
                if (!amount_show.equals(""))
                    allss.append("<td>"+paytime+"</td><td>"+addYear(paytime,tire)+"</td><td style=\"text-align:center;vertical-align:middle;\">"+tire_retailprice+"</td><td style=\"text-align:center;vertical-align:middle;\">"+amount_show+"</td></tr>");
                else
                allss.append("<td>"+paytime+"</td><td>"+addYear(paytime,tire)+"</td><td style=\"text-align:center;vertical-align:middle;\">"+tire_retailprice+"</td><td style=\"text-align:center;vertical-align:middle;\">"+format_paidamount(paid_amount_tire)+"</td></tr>");
            else
            allss.append("<td>"+paytime+"</td><td>"+addYear(paytime,tire)+"</td><td style=\"text-align:center;vertical-align:middle;\">"+tire_retailprice+"</td></tr>");
        }else
        {
            if (paid_amount!=0||!amount_show.equals(""))
                allss.append("<td></td><td></td><td></td><td></td></tr>");
            else
            allss.append("<td></td><td></td><td></td></tr>");
        }
        allss.append("<tr><td rowspan=\"2\">保障服务套餐</td>");
        allss.append("<td>新车悦然焕新</td>");
        if (p_cars.equals("新车")&&rti!=0&&rtigroup)
        {
            if (paid_amount!=0||!amount_show.equals(""))
                if (!amount_show.equals(""))
                    allss.append("<td>"+paytime+"</td><td>"+addYear(Invoicedate,rti)+"</td><td rowspan=\"2\" style=\"text-align:center;vertical-align:middle;\">"+group_retailprice+"</td><td rowspan=\"2\" style=\"text-align:center;vertical-align:middle;\">"+amount_show+"</td></tr>");
                else
                allss.append("<td>"+paytime+"</td><td>"+addYear(Invoicedate,rti)+"</td><td rowspan=\"2\" style=\"text-align:center;vertical-align:middle;\">"+group_retailprice+"</td><td rowspan=\"2\" style=\"text-align:center;vertical-align:middle;\">"+format_paidamount(paid_amount_group)+"</td></tr>");
            else
            allss.append("<td>"+paytime+"</td><td>"+addYear(Invoicedate,rti)+"</td><td rowspan=\"2\" style=\"text-align:center;vertical-align:middle;\">"+group_retailprice+"</td></tr>");
        }else
        {
            if (paid_amount!=0||!amount_show.equals(""))
                allss.append("<td></td><td></td><td></td><td></td></tr>");
            else
            allss.append("<td></td><td></td><td></td></tr>");
        }
        allss.append("<tr><td>新车标准钥匙 1把</td>");
        if (p_cars.equals("新车")&&carkey!=0&&keygroup)
        {
            allss.append("<td>"+paytime+"</td><td>"+addYear(Invoicedate,carkey)+"</td></tr>");
        }else
        {
            if (paid_amount!=0||!amount_show.equals(""))
                allss.append("<td></td><td></td><td></td><td></td></tr>");
            else
            allss.append("<td></td><td></td><td></td></tr>");
        }
        allss.append("<tr><td colspan=\"2\"></td><td colspan=\"2\" style=\"text-align:center;vertical-align:middle;\">合  计：</td>");
        allss.append("<td  style=\"text-align:center;vertical-align:middle;\">"+retailprice+"</td>");
        if (paid_amount!=0||!amount_show.equals(""))
            if (!amount_show.equals(""))
                allss.append("<td  style=\"text-align:center;vertical-align:middle;\">"+amount_show+"</td>");
                else
            allss.append("<td  style=\"text-align:center;vertical-align:middle;\">"+paid_amount+"</td>");

        allss.append("</tr></table>");
        if (!print_remark.equals(""))
        {
            allss.append("<br>");
            allss.append("&nbsp;&nbsp;&nbsp;&nbsp;"+print_remark);
            allss.append("<br>");
        }

        allss.append("<br>");
        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">六、个人信息处理同意</font> </td></tr>");
        allss.append("<tr><td ><div><b>&nbsp;&nbsp;&nbsp;&nbsp;本人同意，授权经销商可以将本服务合同信息及个人信息提供给安联世合。请于安联世合公司官网（https://www.allianz-partners.com/zh_CN/privacy-statement.html）查阅隐私政策。</b></div>" );

        allss.append("<div><b>&nbsp;&nbsp;&nbsp;&nbsp;本人进一步同意安联世合可以将本服务合同信息及个人信息提供给为其提供相关服务的任何其他实体（包括但不限于为本服务合同提供承保服务的保险公司、服务提供方、保险经纪公司）、提供数据分析、处理的技术服务公司，以及任何实际的或可能的参与人、代理人。前述信息接收方应有权依据其各自的规定、适用的法律、法规及合约使用信息｡</b></div></td></tr>");

        allss.append("<tr><td >");

        allss.append("<div>车主签字：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");

        allss.append("</td ></tr>");
        allss.append("</table>");

        allss.append("<br>");
        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">七、客户声明</font> </td></tr>");
        allss.append("<tr><td ><b>车主声明：\n" +
                "授权经销商已通过上述书面形式向本人详细介绍并提供了保障服务的服务内容、合同和条款。本人已完整阅读本服务合同，并清楚知晓本服务合同的各项条款，特别是保持保障服务有效的“服务前提”、将导致无法获得服务的“除外情况”和申请服务所必需的“申请资料”等条款。\n" +
                "本人已阅读授权经销商的隐私保护政策和知悉其个人信息处理规则并同意授权经销商出于履行本服务协议包括但不限于提供本服务协议项下服务之目的处理本人个人信息。\n" +
                "本人已充分理解并接受上述内容，同意以此作为订立服务合同的依据，并愿意遵守本服务合同的条款。\n</b></td></tr>");

        allss.append("<tr><td >");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>车主签字（适用于个人客户）：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>车主盖章（适用于企业客户）：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>授权经销商：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>授权经销商盖章：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("</td ></tr>");
        allss.append("</table>");
        allss.append("</body>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        return allss.toString()+commanstring()+rtistring()+keystring()+tirestring(print_version);
    }
    public  String format_paidamount(double paidamount)
    {
        if (paidamount==0)
        {
            return "-";
        }
        DecimalFormat df = new DecimalFormat("#.00");
        String ss=df.format(paidamount);
        return ss;
    }
    public String contractPrintBmwTire(String contractno,String detail_productid)
    {
        String pathurl=GetPathurl();
        String codeurl="";

        codeurl="wxcheck/wxCheck.html?contractno="+contractno;
        StringBuilder allss = new StringBuilder();
        String dealerno="";
        String sqlstring="select *,(select FROM_UNIXTIME(billbmw.ttime,'%Y-%m-%d') from billbmw where contractno=contract.contractno and in_or_out=-1 and (tradetype='订单支付消费' or tradetype='支付') ORDER BY ttime desc LIMIT 0,1 ) as paytime from contract where contractno=?";
        Map<String,Object> map=queryForMap(jdbcTemplate,sqlstring,contractno);
        String brand="",vehicletype="",vin="",Invoiceno="",Invoicedate="",guideprice="",Invoiceprice="",pname="";
        String cname="",IdNo="",caddress="",insurancecompany="",Businessinsurancepolicyno="",insurancepolicyno="",productid="",companyname="",cars="";
        String contact_person="",contact_tel="",originInvoicedate="",keytype="";
        int iscompany=0,carkey=0,isnewcar=1;
        String retailprice="0",rti_retailprice="0",mileage="",tire_retailprice="0",carkey_retailprice="0",group_retailprice="0";
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
            originInvoicedate=setting.NUllToSpace(map.get("originInvoicedate"));
            retailprice=setting.NUllToSpace(map.get("retailprice"));

            mileage=setting.NUllToSpace(map.get("mileage"));
            keytype=setting.NUllToSpace(map.get("keytype"));
            isnewcar=setting.StrToInt(map.get("isnewcar")+"");
        }
        int print_version=0;
        Date date=parseDate("2023-07-01 00:00:00","yyyy-MM-dd HH:mm:ss");
        try
        {
            Date submit_date=parseDate(paytime+" 12:00:00","yyyy-MM-dd HH:mm:ss");

            if (submit_date.after(date))print_version=1;
        }catch (Exception e)
        {

        }
        String[] tirename=new String[4];
        String[] tireprice=new String[4];
        int[] tire=new int[4];
        String[]tireproductid=detail_productid.split(",");
        List<Map<String, Object>>list;
        if (!detail_productid.equals(""))
        {
            sqlstring="select * from insuranceproduct where productid in ('"+detail_productid.replace(",","','")+"')";
            list = queryForList(jdbcTemplate, sqlstring);


            for (int i = 0; i < list.size(); i++) {

                map = list.get(i);
                for (int ii=0;ii<tireproductid.length;ii++)
                {
                    if (ii>3)break;
                    if (setting.NUllToSpace(map.get("productid")).equals(tireproductid[ii]))
                    {
                        tirename[ii]= setting.NUllToSpace(map.get("pname"));
                        tire[ii]=setting.StrToInt(map.get("tire")+"");
                        tireprice[ii]= setting.NUllToSpace(map.get("retailprice"));
                    }
                }



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
        StringBuilder ss=new StringBuilder();
        String title=brand+"保障服务合同";
        allss.append("<div align=\"center\" class=\"print-title\">" + title + "</div>");
        allss.append("<div align=\"center\" class=\"print-body\">轮胎保障（轮胎置换）服务合同</div>");
        allss.append("<br>");
        allss.append("<div align=\"center\" class=\"print-body\">I.\t服务合同信息页</div>");
        allss.append("<div align=\"right\"><img src=\""+pathurl+codeurl+"\"  style=\"width: 100px;\"  alt=\"\"/></div>");
        allss.append("<div align=\"right\" size=\"2\">扫描二维码，验证服务合同与保障信息</div>");
        allss.append("<div align=\"right\">合同编号：【"+contractno+"】</div>");
        allss.append("<br>");
        allss.append("<div align=\"left\" class=\"print-body\">车主：<u>"+cname+"</u></div>");
        allss.append("<br>");
        allss.append("<div align=\"left\" class=\"print-body\">授权经销商：<u>"+dealername+"</u></div>");
        allss.append("<br>");
        allss.append("<div align=\"left\" class=\"print-body\">&nbsp;&nbsp;&nbsp;&nbsp;通过签署本"+brand+"轮胎保障（轮胎置换）服务合同（以下简称“服务合同”），您同意接受由该授权经销商提供的轮胎保障服务（以下简称“服务”或“保障服务”）。为了便于授权经销商向您提供更贴心的服务和更全面的保障，请务必仔细阅读本服务合同条款和条件，并准确提供下列信息：  </div>");
        allss.append("<br>");
        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" colspan=\"4\" bgcolor=\"#002060\"><font color=\"#fff\">一、享权车辆与保障配件信息</font> </td></tr>");
        allss.append("<tr><td colspan=\"4\" ><b>享权车辆</b></td></tr>");
        allss.append("<tr><td width=\"25%\">品   牌：</td><td width=\"25%\">"+brand+"</td>");
        allss.append("<td width=\"25%\">车   型：</td><td width=\"25%\">"+vehicletype+"</td></tr>");

        allss.append("<tr><td>车架号码（VIN码）：</td><td>"+vin+"</td>");
        allss.append("<td>车辆用途：</td><td>     非   营   运</td></tr>");
        //售后新换胎不显示购车日期

        if (cars.equals("售后新换胎"))allss.append("<tr><td>车辆购买时间（首次购车发票日期）：</td><td></td>");
        else
        {
            if (isnewcar==2) allss.append("<tr><td>车辆购买时间（首次购车发票日期）：</td><td>"+originInvoicedate+"</td>");
            else allss.append("<tr><td>车辆购买时间（首次购车发票日期）：</td><td>"+Invoicedate+"</td>");
        }

      /*  if (setting.StrToInt(mileage)==0)
            allss.append("<td>当前里程表读数：</td><td></td></tr>");
        else*/
        allss.append("<td>当前里程表读数：</td><td>"+mileage+"</td></tr>");

        allss.append("</table>");
        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<tr><td colspan=\"5\" ><b>享权车辆轮胎</b></td></tr>");


        sqlstring="select * from tiredetail where contractno=?";
        list = queryForList(jdbcTemplate, sqlstring,contractno);
        String[]tbrand=new String[4];
        String[]ttype=new String[4];
        String[]tdot=new String[4];

        for (int i=0;i<list.size();i++)
        {
            map=list.get(i);
            int ii=i;
            String position=map.get("position")+"";
            if (position.equals(""))
            {
                ii=i;
            }else if (position.contains("左前"))ii=0;
            else if (position.contains("右前"))ii=1;
            else if (position.contains("左后"))ii=2;
            else if (position.contains("右后"))ii=3;


            tbrand[ii]=map.get("brandname")+"";
            ttype[ii]=map.get("tiretype")+"";
            tdot[ii]=map.get("dot")+"";
        }

        allss.append("<tr><td width=\"20%\">轮胎位置:</td><td width=\"20%\" align=\"center\" >左前轮胎</td><td width=\"20%\" align=\"center\" >右前轮胎</td><td width=\"20%\" align=\"center\" >左后轮胎</td><td width=\"20%\" align=\"center\" >右后轮胎</td></tr>");
        allss.append("<tr><td width=\"20%\">轮胎品牌:</td><td width=\"20%\" align=\"center\" >"+tbrand[0]+"</td><td width=\"20%\" align=\"center\" >"+tbrand[1]+"</td><td width=\"20%\" align=\"center\" >"+tbrand[2]+"</td><td width=\"20%\" align=\"center\" >"+tbrand[3]+"</td></tr>");
        allss.append("<tr><td width=\"20%\">轮胎型号:</td><td width=\"20%\" align=\"center\" >"+ttype[0]+"</td><td width=\"20%\" align=\"center\" >"+ttype[1]+"</td><td width=\"20%\" align=\"center\" >"+ttype[2]+"</td><td width=\"20%\" align=\"center\" >"+ttype[3]+"</td></tr>");
        allss.append("<tr><td width=\"20%\">DOT码:</td><td width=\"20%\" align=\"center\" >"+tdot[0]+"</td><td width=\"20%\" align=\"center\" >"+tdot[1]+"</td><td width=\"20%\" align=\"center\" >"+tdot[2]+"</td><td width=\"20%\" align=\"center\" >"+tdot[3]+"</td></tr>");
        allss.append("</table>");
        allss.append("<br>");


        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\" colspan=\"2\"><font color=\"#fff\" >二、服务提供方信息：</font> </td></tr>");

        allss.append("<tr><td width=\"25%\">授权经销商名称：</td><td>"+dealername+"</td></tr>");
        allss.append("<tr><td width=\"25%\">地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址：</td><td>"+address+"</td></tr>");
        allss.append("<tr><td width=\"25%\">电&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;话：</td><td>"+tel+"</td></tr>");
        allss.append("</table>");
        allss.append("<br>");


        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\" colspan=\"2\"><font color=\"#fff\" >三、车主信息：</font> </td></tr>");

        if (companyname.equals(""))companyname=cname;
        if (iscompany==0)
            allss.append("<tr><td >姓名/企业名称：</td><td>"+cname+"</td></tr>");

        else {
            allss.append("<tr><td >姓名/企业名称：</td><td>"+companyname+"</td></tr>");
            allss.append("<tr><td >办理人姓名：</td><td>"+contact_person+"</td></tr>");
            allss.append("<tr><td >办理人电话：</td><td>"+contact_tel+"</td></tr>");

        }
        allss.append("<tr><td >身份证号码或统一社会信用代码：</td><td>"+IdNo+"</td></tr>");
        allss.append("<tr><td >地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址：</td><td>"+foramtss(30,caddress)+"</td></tr>");
        allss.append("</table>");
        allss.append("<br>");


        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\" colspan=\"5\"><font color=\"#fff\" >四、保障服务产品与内容：</font> </td></tr>");
        allss.append("<tr><td width=\"25%\" rowspan=\"2\" style=\"text-align:center;vertical-align:middle;\">保障服务产品类别</td>");
        allss.append("<td width=\"25%\" style=\"text-align:center;vertical-align:middle;\" rowspan=\"2\">保障服务产品选项</td>");
        allss.append("<td width=\"28%\" style=\"text-align:center;vertical-align:middle;\" colspan=\"2\">服务期限</td>");
        allss.append("<td width=\"22%\" style=\"text-align:center;vertical-align:middle;\" rowspan=\"2\">建议零售价</td></tr>");
        allss.append("<tr><td width=\"14%\" style=\"text-align:center;vertical-align:middle;\" >起始日期</td>");
        allss.append("<td style=\"text-align:center;vertical-align:middle;\">终止日期</td></tr>");
        allss.append("<tr><td rowspan=\"4\">轮胎保障服务\n" +
                "（轮胎置换服务）\n</td>");
        allss.append("<td>左前轮胎</td>");
        if (tire[0]!=0)
        {
            allss.append("<td>"+paytime+"</td><td>"+addYear(paytime,tire[0])+"</td><td style=\"text-align:center;vertical-align:middle;\">"+tireprice[0]+"</td></tr>");
        }else
        {
            allss.append("<td></td><td></td><td></td></tr>");
        }
        allss.append("<tr><td>右前轮胎</td>");
        if (tire[1]!=0)
        {
            allss.append("<td>"+paytime+"</td><td>"+addYear(paytime,tire[1])+"</td><td style=\"text-align:center;vertical-align:middle;\">"+tireprice[1]+"</td></tr>");
        }else
        {
            allss.append("<td></td><td></td><td></td></tr>");
        }
        allss.append("<tr><td>左后轮胎</td>");
        if (tire[2]!=0)
        {
            allss.append("<td>"+paytime+"</td><td>"+addYear(paytime,tire[2])+"</td><td style=\"text-align:center;vertical-align:middle;\">"+tireprice[2]+"</td></tr>");
        }else
        {
            allss.append("<td></td><td></td><td></td></tr>");
        }
        allss.append("<tr><td>右后轮胎</td>");
        if (tire[3]!=0)
        {
            allss.append("<td>"+paytime+"</td><td>"+addYear(paytime,tire[3])+"</td><td style=\"text-align:center;vertical-align:middle;\">"+tireprice[3]+"</td></tr>");
        }else
        {
            allss.append("<td></td><td></td><td></td></tr>");
        }
        allss.append("<tr><td colspan=\"2\"></td><td colspan=\"2\" style=\"text-align:center;vertical-align:middle;\">合  计：</td>");
        allss.append("<td  style=\"text-align:center;vertical-align:middle;\">"+retailprice+"</td></tr>");
        allss.append("</table>");
        allss.append("<br>");
       /* allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">五、客户声明</font> </td></tr>");
        allss.append("<tr><td ><b>车主声明：\n" +
                "提供保障服务的授权经销商已通过上述书面形式向本人详细介绍并提供了保障服务的服务内容、合同和条款。本人已完整阅读本服务合同，并清楚知晓本服务合同的各项条款，特别是保持保障服务有效的“服务前提”、将导致无法获得服务的“除外情况”和申请服务所必需的“申请资料”等条款。\n" +
                "本人同意，提供保障服务的授权经销商及其关联公司可以将本服务合同信息及个人信息提供给为其提供相关服务的任何其他实体（包括但不限于为本服务合同提供承保服务的保险公司、第三方服务提供商）、提供数据分析、处理的技术服务公司，以及任何实际的或可能的参与人、代理人。前述信息接收方应有权依据其各自的规定、适用的法律、法规及合约使用信息｡\n" +
                "本人已充分理解并接受上述内容，同意以此作为订立服务合同的依据，并愿意遵守本服务合同的条款。\n</b></td></tr>");

        allss.append("<tr><td >");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>车主签字（适用于个人客户）：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>车主盖章（适用于企业客户）：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>授权经销商：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>授权经销商盖章：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("</td ></tr>");
        allss.append("</table>");*/
        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">五、个人信息处理同意</font> </td></tr>");
        allss.append("<tr><td ><div><b>&nbsp;&nbsp;&nbsp;&nbsp;本人同意，授权经销商可以将本服务合同信息及个人信息提供给安联世合。请于安联世合公司官网（https://www.allianz-partners.com/zh_CN/privacy-statement.html）查阅隐私政策。</b></div>" );

        allss.append("<div><b>&nbsp;&nbsp;&nbsp;&nbsp;本人进一步同意安联世合可以将本服务合同信息及个人信息提供给为其提供相关服务的任何其他实体（包括但不限于为本服务合同提供承保服务的保险公司、服务提供方、保险经纪公司）、提供数据分析、处理的技术服务公司，以及任何实际的或可能的参与人、代理人。前述信息接收方应有权依据其各自的规定、适用的法律、法规及合约使用信息｡</b></div></td></tr>");

        allss.append("<tr><td >");

        allss.append("<div>车主签字：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");

        allss.append("</td ></tr>");
        allss.append("</table>");

        allss.append("<br>");
        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">六、客户声明</font> </td></tr>");
        allss.append("<tr><td ><b>车主声明：\n" +
                "授权经销商已通过上述书面形式向本人详细介绍并提供了保障服务的服务内容、合同和条款。本人已完整阅读本服务合同，并清楚知晓本服务合同的各项条款，特别是保持保障服务有效的“服务前提”、将导致无法获得服务的“除外情况”和申请服务所必需的“申请资料”等条款。\n" +
                "本人已阅读授权经销商的隐私保护政策和知悉其个人信息处理规则并同意授权经销商出于履行本服务协议包括但不限于提供本服务协议项下服务之目的处理本人个人信息。\n" +
                "本人已充分理解并接受上述内容，同意以此作为订立服务合同的依据，并愿意遵守本服务合同的条款。\n</b></td></tr>");

        allss.append("<tr><td >");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>车主签字（适用于个人客户）：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>车主盖章（适用于企业客户）：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>授权经销商：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>授权经销商盖章：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("</td ></tr>");
        allss.append("</table>");
        allss.append("</body>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        return allss.toString()+commanstring_shouh()+tirestring_shouh(print_version);
    }
    private String commanstring_shouh()
    {
        StringBuilder allss = new StringBuilder();
        String title="II．\t通用条款";
        allss.append("<div align=\"center\" class=\"print-body\" ><b>" + title + "</b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;感谢您签署本服务合同购买保障服务。请您仔细阅读本服务合同条款和条件，包括但不限于本通用条款和下文各保障服务条款。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>1.\t服务合同双方</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.1.\t本服务合同由您和授权经销商于本服务合同第I．部分签署栏所载日期共同签署。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.2.\t本服务合同项下的保障服务将由安联世合结合您的具体情况调配适格的服务提供方为您提供，安联世合对本服务合同项下的服务履行承担相应责任。您可以于享权车辆发生本服务合同第III．部分规定的享权事件且满足服务前提时通过联系授权经销商申请相应服务，并按照要求提交所需材料。如无法联系授权经销商，可通过联系安联世合申请服务，咨询热线电话400-610-6200。</div>");

//        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.2.\t本服务合同项下的保障服务应由授权经销商按照本服务合同条款和条件向您提供。您可以于享权车辆发生本服务合同第III．部分规定的享权事件且满足服务前提时通过联系授权经销商相应申请服务，并按照要求提交所需材料。如无法联系授权经销商，可通过联系保障服务之保险保障和承保服务提供商申请服务，咨询热线电话400-610-6200。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>2.\t服务费用</u></b></div>");
        //  allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.\t在本服务凭证签署时，您应立即且毫无延迟地按照下文相关保障服务产品条款所载的服务产品价格表向授权经销商足额支付相应的服务产品价格。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.\t在本服务合同签署时，您应按照授权经销商提供的保障服务价格表向授权经销商足额支付相应的服务费用。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.2.\t在收到您所支付的服务费用全额后三（3）个工作日内，授权经销商应向您开具相应金额的发票。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>3.\t服务地区及适用法律</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;3.1.\t本服务合同项下的服务地区为中华人民共和国境内（仅为本服务合同之目的，不包括中国香港、中国澳门和中国台湾）。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;3.2.\t本服务合同以及因本服务合同所产生或与之有关的所有事宜均应受中华人民共和国法律管辖，并以中华人民共和国法律作理解、解释和执行。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;3.3.\t如发生争议，您和授权经销商均有权向授权经销商所在地有管辖权的法院提起诉讼。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>4.\t服务合同生效和终止</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.1.\t就本服务合同项下的轮胎保障服务，该保障服务相关条款自本服务合同第I．部分列明的对应服务期限的起始日期零时起生效，至相关服务期限届满、根据下文第4.3条相应保障服务提供完毕时或根据下文第6.6条终止。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.2.\t就本服务合同项下您已购买但尚未申请提供服务的轮胎保障服务，您有权在任何时候提前六十（60）个日历日 内书面通知授权经销商终止该等服务，但前提是您应签署和/或提供授权经销商所需的与服务合同终止有关的文件和证明。除法律法规和/或本服务合同另有约定外，服务期限开始后解除本服务合同的，授权经销商应当将已收取的服务费用，按照日比例扣除自服务期限的起始日期起至合同解除之日（您书面提出申请之日）止应收取的部分后，授权经销商将收到上述终止通知后六十（60）个日历日 内向您返还相应比例部分的服务费用余额（如有）。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.3.\t若您按照本服务合同条款和条件申请并已获得相关服务 ，无论该等服务是由授权经销商或者按照本服务合同第II．部分第1.2条的规定由轮胎保障服务之保险保障和承保服务提供商建议的其他授权经销商提供，则本服务合同应于该等服务提供完毕后自动终止。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>5.\t您的隐私与信息安全</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.1.\t授权经销商仅可为本服务合同之目的，向您收集与本服务合同项下所述保障服务相关的个人信息。对于此类信息，授权经销商应确保其员工、代表、代理人、经理和关联企业严格保密，且不得向任何第三方披露（除非为履行本服务合同而合法使用，或根据相关法律法规或监管部门要求）。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>6.\t您的义务</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;6.1.\t您必须正确且合理使用本服务合同项下的享权车辆。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b>&nbsp;&nbsp;6.2.\t在本服务合同服务期限内，就轮胎保障服务，若您未经授权经销商事先书面同意转让享权车辆，则您将丧失本服务合同项下的权益。</b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;6.3.\t在申请保障服务时，您应按照相关服务条款：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.3.1.\t及时与授权经销商联系；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.3.2.\t尽快将本服务合同项下的享权车辆送至授权经销商处维修（如适用）；且</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.3.3.\t尽快向授权经销商、太保北分提供与该等服务相关的各种证明和资料，并确保该等证明和资料具有合法性、真实性和完整性。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;6.4.\t如因您未能完全履行上文第6.1条约定的义务，导致保障服务部分或全部无法提供，授权经销商对此不承担责任。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;6.5.\t如因您未能完全履行上文第6.1条约定的义务，导致授权经销商遭受任何损失，授权经销商有权向您索赔该等损失，包括但不限于要求您支付等额于已产生之费用或已给予之优惠折扣的金额。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;6.6.\t在出现上文第6.4条或第6.5条所述情况时，授权经销商有权向您发出书面通知立即终止本服务合同项下与相关服务有关的条款，且相关服务费用将不予退还。</div>");


        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>7.\t其他相关定义</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;7.1.\t“太保北分”：指为轮胎保障服务提供保险保障和承保服务的中国太平洋财产保险股份有限公司北京分公司。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;7.2.\t“服务期限”：本服务合同第I.部分第四条所载的服务期限。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;7.3.\t“享权车辆”：本服务合同中列明的车辆（以登记的车架号（VIN）为准）。</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        return  allss.toString();
    }
    private String commanstring()
    {
        StringBuilder allss = new StringBuilder();
        String title="II．\t通用条款";
        allss.append("<div align=\"center\" class=\"print-body\" ><b>" + title + "</b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;感谢您签署本服务合同购买保障服务。请您仔细阅读本服务合同条款和条件，包括但不限于本通用条款和下文各保障服务条款。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>1.\t服务合同双方</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.1.\t本服务合同由您和授权经销商于本服务合同第I．部分签署栏所载日期共同签署。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.2.\t本服务合同项下的保障服务将由安联世合结合您的具体情况调配适格的服务提供方为您提供，安联世合对本服务合同项下的服务履行承担相应责任。您可以于享权车辆发生本服务合同第III．部分、第IV．部分和/或第V．部分规定的享权事件且满足服务前提时通过联系授权经销商申请相应服务，并按照要求提交所需材料。如无法联系授权经销商，可通过联系安联世合申请服务，咨询热线电话400-610-6200。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>2.\t服务费用</u></b></div>");
        //  allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.\t在本服务凭证签署时，您应立即且毫无延迟地按照下文相关保障服务产品条款所载的服务产品价格表向授权经销商足额支付相应的服务产品价格。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.\t在本服务合同签署时，您应按照本服务合同载明的保障服务价格表向授权经销商足额支付相应的服务费用。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.2.\t在收到您所支付的服务费用全额后三（3）个工作日内，授权经销商应向您开具相应金额的发票。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>3.\t服务地区及适用法律</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;3.1.\t本服务合同项下的服务地区为中华人民共和国境内（仅为本服务合同之目的，不包括中国香港、中国澳门和中国台湾）。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;3.2.\t本服务合同以及因本服务合同所产生或与之有关的所有事宜均应受中华人民共和国法律管辖，并以中华人民共和国法律作理解、解释和执行。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;3.3.\t如发生争议，您和授权经销商均有权向授权经销商所在地有管辖权的法院提起诉讼。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>4.\t服务合同生效和终止</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.1.\t就本服务合同项下每一保障服务，该保障服务相关条款自本服务合同第I．部分列明的对应服务期限的起始日期零时起生效，至相关服务期限届满、根据下文第4.3条相应保障服务提供完毕时或根据下文第6.6条终止。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.2.\t就本服务合同项下您已购买但尚未申请任何一项保障服务（若您于本服务合同项下仅购买了一项保障服务，则您应尚未申请该项保障服务），您有权在任何时候提前六十（60）个自然日书面通知授权经销商终止该等全部或部分服务，但前提是您应签署和/或提供授权经销商所需的与服务合同终止有关的文件和证明。除法律另有规定和本服务合同另有约定外，服务期限开始后解除本服务合同的，授权经销商应当将已收取的服务费用，按照日比例扣除自服务期限的起始日期起至合同解除之日（您书面提出申请之日）止应收取的部分后，授权经销商将收到上述终止通知后六十（60）个自然日内向您返还相应比例部分的服务费用余额（如有）。前述提前终止本服务合同的规定不适用于品牌方赠送的保障服务。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.3.\t若就本服务合同项下您已购买悦然焕新服务、钥匙保障服务和轮胎保障服务，则本服务合同原则上应于所有服务提供完毕后自动终止。尽管有前述规定，一旦您就享权车辆申请并已获得悦然焕新服务，无论该等服务是否由服务提供方提供，则本服务合同应于该等服务提供完毕后自动终止。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>5.\t您的隐私与信息安全</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.1.\t授权经销商仅可为本服务合同之目的，向您收集与本服务合同项下所述保障服务相关的个人信息。对于此类信息，授权经销商应确保其员工、代表、代理人、经理和关联企业严格保密，且不得向任何第三方披露（除非为履行本服务合同而合法使用，或根据相关法律法规或监管部门要求）。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>6.\t您的义务</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;6.1.\t您必须正确且合理使用本服务合同项下的享权车辆。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b>&nbsp;&nbsp;6.2.\t在相关服务期限内，就悦然焕新服务、钥匙保障服务和/或轮胎保障服务（依适用），若您未经授权经销商事先书面同意转让享权车辆，则您将丧失本服务合同项下的权益。</b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;6.3.\t在申请保障服务时，您应按照相关服务条款：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.3.1.\t及时与授权经销商联系；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.3.2.\t尽快将本服务合同项下的享权车辆送至服务提供方处维修（如适用）；且</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.3.3.\t尽快向授权经销商、服务提供方提供与该等保障服务相关的各种证明和资料，并确保该等证明和资料具有合法性、真实性和完整性。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;6.4.\t如因您未能完全履行上文第6.1条约定的义务，导致保障服务部分或全部无法提供，授权经销商、服务提供方对此不承担责任。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;6.5.\t如因您未能完全履行上文第6.1条约定的义务，导致授权经销商或服务提供方遭受任何损失，授权经销商或服务提供方有权向您索赔该等损失，包括但不限于要求您支付等额于已产生之费用或已给予之优惠折扣的金额。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;6.6.\t在出现上文第6.4条或第6.5条所述情况时，授权经销商有权向您发出书面通知立即终止本服务合同项下与相关服务有关的条款，且相关服务费用将不予退还。</div>");


        allss.append("<div align=\"left\"  class=\"print-body\" ><b>7.\t其他相关定义</b></div>");
     //   allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;7.1.\t“大地上分”：指为悦然焕新服务提供保险保障和承保服务的中国大地财产保险股份有限公司上海分公司。</div>");
      //  allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;7.2.\t“太保北分”：指为钥匙保障服务和轮胎保障服务提供保险保障和承保服务的中国太平洋财产保险股份有限公司北京分公司。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;7.1.\t“服务期限”：本服务合同第I.部分第五条所载的服务期限。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;7.2.\t“享权车辆”：本服务合同中列明的车辆（以登记的车架号（VIN）为准）。</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        return  allss.toString();
    }
    private  String rtistring()
    {
        StringBuilder allss = new StringBuilder();
        String title="III.\t悦然焕新服务";
        allss.append("<div align=\"center\" class=\"print-body\" ><b>" + title + "</b></div>");
        allss.append("<div align=\"center\" class=\"print-body\" >机动车重置服务</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>1.\t服务前提</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.1.\t享权车辆：非营运用途的9座及9座以下BMW/MINI品牌乘用车。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.2.\t车龄要求：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;1.2.1\t新车：车主购买本服务时，享权车辆车龄自享权车辆首次购车发票开具之日起计算不得超过十二（12）个月，且享权车辆的所有权未发生过转让；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;1.2.2\t二手车：车主购买本服务时，享权车辆车龄自享权车辆首次购车发票（即其作为新车时所开具的发票）开具之日起计算不得超过四十八（48）个月。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.3.\t机动车辆保险投保要求：在本服务合同约定的服务期限内车主须为享权车辆投保机动车交通事故责任强制保险和足额投保以下全部或部分机动车辆商业保险险种，且须保持其于本服务合同约定的服务期限内持续有效。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;1.3.1\t机动车损失保险；及</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;1.3.2\t其它附加险险种（如有）。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.4.\t享权事件：享权车辆因机动车辆商业保险的上述任一险种承保的保险事故造成全损或推定全损，且该险种和机动车交通事故责任强制保险于保险事故发生时保险期间尚未届满，车主方可向授权经销商申请服务。全损或推定全损以承保享权车辆上述机动车辆商业保险之保险公司的定损结果为惟一依据。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>2.\t服务规则</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.\t享权车辆在服务期限内遭受全损或推定全损，且满足上述第1条规定的服务前提，车主可向授权经销商申请服务。服务提供方将协助车主置换一辆BMW或MINI品牌的新车，可供选择的新车品牌视该授权经销商的具体授权情况而定，置换新车过程中产生的机动车置换费用由服务提供方承担。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.2.\t如享权车辆为新车，所置换的新车的官方建议零售价不得低于享权车辆官方建议零售价的90%；如享权车辆为二手车，则所置换的新车的官方建议零售价不得低于享权车辆发票价的90%。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.3.\t本服务中所指的机动车置换费用包括车辆折旧费用与机动车置换补偿两部分，其中：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.3.1\t针对享权车辆为新车的情形：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;a)\t车辆折旧费用：“享权车辆发票价”与“享权车辆对应的机动车辆保险保单年度内如第五条第3款列明的机动车辆商业保险保单中载明的保险金额”的差额（仅在此差值大于零时有效）。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;b)\t机动车置换补偿：在置换新车过程中所产生的费用，包括车辆购置税、新车交付费用、新车登记费用，及新车首年机动车辆保险保费（其中，若所置换新车的车辆购置税或机动车辆保险保费高于享权车辆的车辆购置税或机动车辆保险保费，则上述两项费用应分别以较低值为准）。补偿金额为上述各项费用金额之和，但不超过享权车辆发票价的15%；超出部分由车主自行承担。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.3.2\t针对享权车辆为二手车的情形：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;a)\t车辆折旧费用 = 享权车辆发票价x已使用月数 x月折旧率\n" +
                "“已使用月数”指享权车辆二手车销售发票显示的购车日期至发生全损或推定全损事故之日计算的已使用月数，不足一月，不计算折旧；\n</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;b)\t机动车置换补偿：在置换新车过程中所产生的费用，包含新车交付费用、新车登记费用，及新车首年机动车辆保险保费（若所置换新车的机动车辆保险保费高于享权车辆的机动车辆保险保费，则应以较低值为准）。补偿金额为上述各项费用金额之和，但不超过享权车辆发票价的15%；超出部分由车主自行承担。</div>");


        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.4.\t上述税费、保险费及服务费之金额应当符合同一时期同类税种、保险产品和服务的税率或市场公允价格。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.5.\t享权车辆发票价以其购车发票（新车为首次购车发票，二手车为二手车销售发票）所载明之价税合计金额为准。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>3.\t服务期限</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;3.1.\t新车：至享权车辆车龄的第36个月，车龄自享权车辆首次购车发票开具之日起计算；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;3.2.\t二手车：本服务合同生效后连续24个月。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;3.3.\t具体以第I.部分第五条中载明的已选悦然焕新（机动车置换）服务项下所显示的服务期限起始日期和终止日期为准。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>4.\t除外情况</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.1.\t出现下列任一情形时，车主不能享受服务：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.1\t车主要求提供服务的享权车辆信息和/或车主身份信息与本服务合同第一条和第三条记载的信息不一致；车主要求提供服务的享权车辆用途与本服务合同中限定不一致；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.2\t享权车辆遭受的事故发生在本服务合同约定服务期限生效日期前；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.3\t享权车辆因遭受事故仅导致部分损失，未达到本服务合同所约定的车辆全损或推定全损标准（以承保享权车辆机动车辆商业保险之保险公司的定损结果为唯一依据）；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.4\t享权车辆遭受事故时不存在相关机动车辆保险保障，或遭受事故后因不符合法律规定或相关机动车辆保险约定的理赔条件、未获得与全损或推定全损相应的机动车辆保险赔偿；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.5\t享权车辆经过改装或拼装，其工况受到影响；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.6\t享权车辆已被转让给第三人；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.7\t车主要求置换的新车的官方建议零售价低于享权车辆官方建议零售价的90%。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.2.\t下列任何原因造成享权车辆全损或推定全损，车主不能就相关损失享受服务：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.1\t车主的故意行为、重大过失、欺诈、不诚实、违法犯罪行为；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.2\t车主使用、维护、保管不当；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.3\t享权车辆内在或潜在缺陷、自然磨损、自然损耗；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.4\t战争、敌对行为、军事行动、武装冲突、恐怖主义活动、罢工、暴动、骚乱；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.5\t核爆炸、核裂变、核聚变；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.6\t放射性污染及其他各种环境污染；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.7\t行政行为、司法行为。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.3.\t下列损失、费用和责任，不在服务范围内：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.3.1\t在车辆置换过程中，因更换非BMW或MINI品牌新车产生的费用；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.3.2\t任何形式的人身伤害、财产损失，及除本服务合同第六条所列“机动车置换费用”外的其他任何费用支出；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.3.3\t服务过程中所产生的任何间接损失、赔偿责任；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.3.4\t因未投保本服务合同第五条约定的机动车辆保险任一险种承保的保险事故造成的车辆全损或推定全损，或者该险种的保险期间于保险事故发生时已届满。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>5.\t申请材料</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.1.\t车主需提供确认享权车辆事故性质、原因、损失程度等有关证明和资料，包括但不限于：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.1\t享权车辆发生全损或推定全损时仍处于生效状态的机动车辆商业保险和机动车交通事故责任强制保险的保险单复印件；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.2\t本服务合同；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.3\t享权车辆行驶本复印件、车主驾驶本复印件、车主身份证或企业营业执照复印件；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.4\t享权车辆首次购车发票复印件；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.5\t机动车辆保险之保险公司已按照全损、推定全损赔付完毕的证明（划账水单原件，以及一次性赔偿协议原件或机动车辆保险之保险公司盖章的复印件）；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.6\t享权车辆全损或推定全损后处理手续（权益转让书、拍卖或注销解体手续）；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.7\t证明事故发生原因的证明，属于道路交通事故的提供事故证明（证明材料复印件，需机动车辆保险之保险公司盖章）。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.2.\t车主需提供机动车置换补偿的相关材料、单据，包括但不限于：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.2.1\t新车的购车合同的复印件；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.2.2\t新车的车辆购置税完税证明的复印件；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.2.3\t新车的机动车辆保险的保险单的复印件或电子保单及保费发票的复印件；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.2.4\t新车的机动车置换服务的服务合同和服务费发票的复印件，如有；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.2.5\t新车交付所涉及项目的费用发票的复印件。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.3.\t因车主未能提交任何上述约定的申请资料，导致部分或全部服务责任无法确定，对于无法确定的部分不能享受服务。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.4.\t若由于车主原因导致未完成新车置换或服务提供方未能提供服务的，车主不能主张本服务合同项下的任何服务和/或补偿。</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        return  allss.toString();
    }

    private  String keystring()
    {
        StringBuilder allss = new StringBuilder();
        String title="IV.\t钥匙保障服务";

        allss.append("<div align=\"center\" class=\"print-body\" ><b>" + title + "</b></div>");
        allss.append("<div align=\"center\" class=\"print-body\" >机动车辆钥匙重置服务</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>1.\t服务前提</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.1.\t享权车辆：非营运9座及9座以下BMW/MINI品牌乘用车。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.2.\t车龄要求：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;1.2.1\t新车车主购买本服务时享权车辆车龄自享权车辆购车发票开具之日起计算不得超过30个自然日；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;1.2.2\t升级了智能触控钥匙后购买本服务的旧车车主购买本服务时，对享权车辆无车龄要求。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.3.\t享权车辆的所有权未发生过转让。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.4.\t享权事件：享权车辆发生了车辆钥匙丢失或者被盗，车主方可向授权经销商申请重置车辆钥匙服务。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>2.\t服务规则</u></b></div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.\t享权车辆在服务期限内发生了车辆钥匙丢失或被盗，且满足上述第1条规定的服务前提，车主可按照本服务合同条款向授权经销商申请重置车辆钥匙服务。服务提供方将协助车主重置同品牌、同型号和同规格的车辆钥匙，重置车辆钥匙过程中产生的机动车辆钥匙重置费用由服务提供方承担。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.2.\t机动车辆钥匙重置费用包括重置车辆钥匙所需的零配件费用（含钥匙配码的成本）和工时费。</b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.3.\t根据享权车辆所适用的车辆钥匙类型不同，在服务期限内，车主可享受重置同品牌 、同型号和规格车辆钥匙服务的次数不同，其中：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.3.1\t购买了本服务的新车车主：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;a)\t标准钥匙：车主至多享受2次车辆钥匙重置服务；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;b)\t标准钥匙与NFC钥匙：车主至多享受2次车辆钥匙重置服务（重置NFC钥匙计作前述2次服务中的1次）；或</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;c)\t智能触控钥匙：车主至多享受1次车辆钥匙重置服务。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.3.2\t升级了智能触控钥匙后购买了本服务的旧车车主：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;a)\t智能触控钥匙：车主至多享受1次车辆钥匙重置服务。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.4.\t车主为享权车辆所选择的车辆钥匙重置服务详载于本服务合同第一条。</div>");


        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>3.\t服务期限</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;服务期限为车主购买BMW/MINI钥匙保障（机动车辆钥匙重置）服务生效后至多连续36个月，具体以第I.部分第五条中载明的已选钥匙保障（机动车辆钥匙重置）服务项下所显示的服务期限起始日期和终止日期为准。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>4.\t除外情况</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.1.\t出现下列任一情形时，车主不能享受服务：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.1\t损失发生在服务合同生效前，或车主未在服务合同规定的服务期限内提出重置要求；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.2\t损失发生在中华人民共和国（为本服务合同之目的，不含中国香港、中国澳门和中国台湾）境外；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.3\t车主要求重置的车辆钥匙及其所属的机动车辆信息、使用性质与本服务合同中记载不一致的；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.4\t重置不同品牌、型号或规格的车辆钥匙；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.5\t车主的故意行为、欺诈、不诚实、违法犯罪行为；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.6\t车主使用、维护不当；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.7\t车辆钥匙内在或潜在缺陷、自然磨损、自然损耗；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.8\t行政行为、司法行为。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.2.\t下列损失、费用和责任，不在服务范围内：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.1\t任何形式的人身伤害、财产损失，及除本服务合同第五条所列“机动车辆钥匙重置费用”外的其他任何费用支出；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.2\t服务过程中所产生的任何间接损失、赔偿责任；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.3\t道路救援、拖车、出租车或者租车等服务费用；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.4\t任何情况下更换车锁或维修车锁的费用。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>5.\t申请材料</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.1.\t车主需提供确认享权车辆钥匙的损失性质、原因、程度等有关证明和资料，包括但不限于：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.1\t享权车辆正面外观照片；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.2\t享权车辆的行驶证正副两页；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.3\t车主身份证复印件或扫描件；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.4\t本服务合同；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.5\t索赔申请书；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.6\t维修工单；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.7\t享权车辆钥匙损失情况声明书；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.8\t已发生符合服务合同约定损失的证明（钥匙禁用截屏）；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.9\t如车主声明享权车辆钥匙被盗的，则另需提供报警材料正面扫描件。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.2.\t因车主未能提交任何上述约定的申请资料，导致部分或全部服务责任无法确定，对于无法确定的部分不能享受服务。 </div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.3.\t若由于车主原因导致未完成车辆钥匙重置或服务提供方1未能提供服务的，车主不能主张本服务合同项下的任何服务和/或补偿。 </div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        return  allss.toString();
    }
    private  String tirestring(int print_version)
    {
        StringBuilder allss = new StringBuilder();
        String title="V.\t轮胎保障服务";
        allss.append("<div align=\"center\" class=\"print-body\" ><b>" + title + "</b></div>");
        allss.append("<div align=\"center\" class=\"print-body\" >轮胎置换服务</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>1.\t服务前提</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.1.\t享权车辆：非营运9座及9座以下BMW/MINI品牌乘用车，新车车主可在享权车辆车龄自享权车辆购车发票开具之日起计算30个自然日（含）内为原厂搭载的新车轮胎购买本服务；</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.2.\t享权车辆的所有权未发生过转让。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.3.\t享权事件：享权车辆发生了轮胎鼓包或者爆胎，车主方可向授权经销商申请置换轮胎服务。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>2.\t服务规则</u></b></div>");
        if (print_version==1)
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.\t享权车辆在服务期限内正常使用过程中，由于非人为故意原因的质量问题导致轮胎出现下列事故，车主在服务期限内按照服务合同的约定尽快前往授权经销商处进行维修并向其提出更换轮胎的服务申请，服务提供方按照服务合同条款向车主提供享权车辆所搭载轮胎同品牌、同规格（或同等级别）的轮胎的免费更换服务（客户自付费用除外）。</div>");
        else
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.\t享权车辆在服务期限内正常使用过程中，由于非人为故意原因的质量问题导致轮胎出现下列事故，车主在服务期限内按照服务合同条款到授权经销商处维修并向其提出更换轮胎的要求，授权经销商按照服务合同条款向车主提供享权车辆所搭载轮胎同品牌、同规格（或同等级别）的轮胎的免费更换服务（客户自付费用除外）。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;事故类型：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;a)\t爆胎：轮胎在极短的时间（一般以少于0.1秒）因破裂突然失去气体而瘪掉，裂口呈不规则撕裂状且穿透；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;b)\t鼓包：轮胎胎肩、胎侧部位有明显的起鼓现象。</div>");
        if (print_version==1)
        {
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.2.\t根据您的选择，在服务期限内，享权车辆累计更换轮胎上限为4条同品牌、同规格的轮胎，单次服务更换轮胎上限为1条同品牌、同规格的轮胎。</div>");

            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.3.\t每条享权车辆所搭载轮胎仅可享受1次轮胎置换服务（即：每条轮胎各1次服务）。</div>");
            allss.append("<div align=\"left\"  class=\"print-body\" ><b>&nbsp;&nbsp;2.4.\t服务保障限额：服务保障限额适用于所有轮胎保障服务产品，以车主购买的轮胎保障服务生效之日起至车主向授权经销商提出轮胎置换服务申请之日止计算的月数为依据，车主应按照轮胎零售价的如下比例自行承担部分轮胎保障服务费用（“客户自付费用”）：</b></div>");
                    allss.append("<div align=\"left\"  class=\"print-body\" ><b>&nbsp;&nbsp;&nbsp;&nbsp;a)\t0-6个月：0%；</b></div>");
            allss.append("<div align=\"left\"  class=\"print-body\" ><b>&nbsp;&nbsp;&nbsp;&nbsp;b)\t7-12个月：20%；</b></div>");
            allss.append("<div align=\"left\"  class=\"print-body\" ><b>&nbsp;&nbsp;&nbsp;&nbsp;c)\t13-24个月：20%（仅适用于24个月期的轮胎保障服务产品）。</b></div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.5.\t如车主因自身原因不便或无法回到与其签署了服务合同的原授权经销商处申请轮胎置换服务的，经安联世合同意，车主有权就近选择另一BMW/MINI授权经销商并授权其根据服务合同的各项条款与规定代为提供服务。</div>");


        }else
        {
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.2.\t根据您的选择，在服务期限内，享权车辆累计更换轮胎上限为2条同品牌、同规格的轮胎，单次服务更换轮胎上限为1条同品牌、同规格的轮胎。</div>");

            allss.append("<div align=\"left\"  class=\"print-body\" ><b>&nbsp;&nbsp;2.3.\t您需要按照轮胎零售价（包括相关税费）的X%自行承担部分轮胎更换服务费用（“客户自付费用”）。根据轮胎已使用的月数，X%应为：</b></div>");
            allss.append("<div align=\"left\"  class=\"print-body\" ><b>&nbsp;&nbsp;&nbsp;&nbsp;a)\t0-6个月：0%；</b></div>");
            allss.append("<div align=\"left\"  class=\"print-body\" ><b>&nbsp;&nbsp;&nbsp;&nbsp;b)\t7-12个月：25%；</b></div>");
            allss.append("<div align=\"left\"  class=\"print-body\" ><b>&nbsp;&nbsp;&nbsp;&nbsp;c)\t13-24个月：50%（仅适用于24个月期的轮胎保障服务产品）。</b></div>");

        }
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>3.\t服务期限</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;3.1.\t12个月期轮胎保障服务产品的服务期限为本服务合同生效后，连续12个月或享权车辆于本服务合同生效后已累计行驶1.5万公里（以先到者为准）；或</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;3.2.\t24个月期轮胎保障服务产品的服务期限为本服务合同生效后，连续24个月或享权车辆于本服务合同生效后已累计行驶3万公里（以先到者为准）；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;3.3.\t具体以第I.部分第五条中载明的已选轮胎保障（轮胎置换）服务项下所显示的服务期限起始日期和终止日期为准。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>4.\t除外情况</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.1.\t出现下列任一情形时，车主不能享受服务：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.1\t已超过服务期限或享权车辆于本服务合同生效后已累计行驶1.5万或3万公里（依适用）（服务期限与累计行驶里程两者以先到者为准）；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.2\t申请服务时，轮胎纹理磨损达到或已超过磨损线（即轮胎更换表示“▲”）或胎面剩余花纹磨损深度小于3mm；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.3\t受损轮胎被盗、被遗弃或其他任何原因导致无法收回轮胎；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.4\t轮胎的识别码、品牌型号等被破坏、移除、磨损或其他原因无法进行识别；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.5\t轮胎曾进行过修补；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.6\t车主要求重置的轮胎及其所属的机动车辆信息、使用性质与本服务合同中记载不一致的；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.7\t重置不同品牌、型号或规格的轮胎；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.8\t轮胎生产时国内市场技术水平尚不能发现的缺陷；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.9\t车主的故意行为、重大过失、欺诈、不诚实、违法犯罪行为所导致的轮胎事故；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.10\t车主使用、维护不当；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.11\t提供有偿服务的私人车辆及七座以上客车发生事故的轮胎损失；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.12\t因交通意外、轮胎安装错误、超速或超载行驶、过高或过低胎压等原因导致的轮胎损坏；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.13\t地震、雷击、火灾、爆炸、暴雨、洪水、台风等灾害；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.14\t因自然磨损、腐蚀、人为损害、保管不当等原因导致的轮胎损坏；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.15\t行政行为、司法行为。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.2.\t下列损失、费用和责任，不在服务范围内：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.1\t根据第2.3款规定的轮胎更换服务费用中客户自行承担的部分；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.2\t因汽车厂家对行车安全的相关要求必须或建议同时更换与受损轮胎同轴的对侧轮胎而产生的额外轮胎更换费用；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.3\t任何形式的人身伤害、财产损失，及除本服务合同规定外的其他任何费用支出； </div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.4\t服务过程中所产生的任何间接损失、赔偿责任；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.5\t道路救援、拖车、出租车或者租车等服务费用；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.6\t任何情况下更换车锁或维修车锁的费用。</div>");


        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>5.\t申请材料</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.1.\t车主需提供确认享权车辆轮胎的损失性质、原因、程度等有关证明和资料，包括但不限于：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.1\t享权车辆（带牌照）的照片；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.2\t车主的身份证明文件或营业执照；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.3\t本服务合同；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.4\t车主服务使用确认书；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.5\t车辆的有效行驶证；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.6\t里程表读数照片；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.7\t损失轮胎位置照片；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.8\t具体损坏部位照片；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.9\t损失轮胎带 DOT 码的局部照片；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.10\t新轮胎安装后的照片及带DOT码的局部照片；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.11\t享权车辆轮胎损失情况声明书；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.12\t索赔申请书。</div>");
        if (print_version==1)
        {
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.2.\t如车主因自身原因不便或无法回到与其签署了服务合同的原授权经销商处申请轮胎置换服务的，须与实际提供该服务的授权经销商签署《BMW/MIN轮胎保障服务服务授权声明书》。</div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.3.\t因车主未能提交任何上述约定的申请资料，导致部分或全部服务责任无法确定，对于无法确定的部分不能享受服务。</div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.4.\t若由于车主原因导致未完成轮胎置换或服务提供方、车主就近选择的BMW/MINI授权经销商未能提供服务的，车主不能主张本服务合同项下的任何服务和/或补偿。</div>");

        }
        else
        {
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.2.\t因车主未能提交任何上述约定的申请资料，导致部分或全部服务责任无法确定，对于无法确定的部分不能享受服务。</div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.3.\t若由于车主原因导致未完成轮胎置换或授权经销商未能提供服务的，车主不能主张本服务合同项下的任何服务和/或补偿。</div>");

        }


        allss.append("<br>");
        return  allss.toString();
    }
    private  String tirestring_shouh(int print_version)
    {
        StringBuilder allss = new StringBuilder();
        String title="III.\t轮胎保障服务";
        allss.append("<div align=\"center\" class=\"print-body\" ><b>" + title + "</b></div>");
        allss.append("<div align=\"center\" class=\"print-body\" >轮胎置换服务</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>1.\t服务前提</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.1.\t享权车辆：非营运9座及9座以下BMW/MINI品牌乘用车，车主可在购买了宝马售后销售的售后新换胎之日起7个日历日 （含）内为该轮胎购买本服务。 </div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.2.\t享权车辆的所有权未发生过转让。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.3.\t享权事件：享权车辆发生了轮胎鼓包或者爆胎，车主方可向授权经销商申请置换轮胎服务。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>2.\t服务规则</u></b></div>");
        if (print_version==1)
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.\t享权车辆在服务期限内正常使用过程中，由于非人为故意原因的质量问题导致轮胎出现下列事故，车主在服务期限内按照服务合同的约定尽快前往授权经销商处进行维修并向其提出更换轮胎的服务要求，授权经销商按照服务合同条款向车主提供享权车辆所搭载轮胎同品牌、同规格（或同等级别）的轮胎的免费更换服务（客户自付费用除外）。</div>");

        else
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.\t享权车辆在服务期限内正常使用过程中，由于非人为故意原因的质量问题导致轮胎出现下列事故，车主在服务期限内按照服务合同条款到授权经销商处维修并向其提出更换轮胎的要求，授权经销商按照服务合同条款向车主提供享权车辆所搭载轮胎同品牌、同规格（或同等级别）的轮胎的免费更换服务（客户自付费用除外）。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;事故类型：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;a)\t爆胎：轮胎在极短的时间（一般以少于0.1秒）因破裂突然失去气体而瘪掉，裂口呈不规则撕裂状且穿透；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;b)\t鼓包：轮胎胎肩、胎侧部位有明显的起鼓现象。</div>");
        if (print_version==1)
        {
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.2.\t根据您的选择，在服务期限内，享权车辆所载每一售后新换轮胎的累计更换上限为1条同品牌、同规格（或同等级别）的轮胎，单次服务更换轮胎上限为1条同品牌、同规格（或同等级别）的轮胎。</div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.3.\t每条享权车辆所搭载轮胎仅可享受1次轮胎置换服务（即：每条轮胎各1次服务）。</div>");

            allss.append("<div align=\"left\"  class=\"print-body\" ><b>&nbsp;&nbsp;2.4.\t服务保障限额：服务保障限额适用于所有轮胎保障服务产品，以车主购买的轮胎保障服务生效之日起至车主向授权经销商提出轮胎置换服务之日止计算的月数为依据，车主应按照轮胎零售价的如下比例自行承担部分轮胎保障服务费用（“客户自付费用”）：</b></div>");
            allss.append("<div align=\"left\"  class=\"print-body\" ><b>&nbsp;&nbsp;&nbsp;&nbsp;a)\t0-6个月：0%；</b></div>");
            allss.append("<div align=\"left\"  class=\"print-body\" ><b>&nbsp;&nbsp;&nbsp;&nbsp;b)\t7-12个月：20%；</b></div>");
            allss.append("<div align=\"left\"  class=\"print-body\" ><b>&nbsp;&nbsp;&nbsp;&nbsp;c)\t13-24个月：20%（仅适用于24个月期的轮胎保障服务产品）。</b></div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.5.如车主因自身原因不便或无法回到与其签署了服务合同的原授权经销商处申请轮胎置换服务的，车主有权就近选择另一BMW/MINI授权经销商并授权其根据服务合同的各项条款与规定代为提供服务。</div>");

        }else
        {
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.2.\t根据您的选择，在服务期限内，享权车辆所载每一售后新换轮胎的累计更换上限为1条 同品牌、同规格（或同等级别）的轮胎，单次服务更换轮胎上限为1条同品牌、同规格（或同等级别）的轮胎。</div>");

            allss.append("<div align=\"left\"  class=\"print-body\" ><b>&nbsp;&nbsp;2.3.\t您需要按照轮胎零售价（包括相关税费）的X%自行承担部分轮胎更换服务费用（“客户自付费用”）。根据轮胎已使用的月数，X%应为：</b></div>");
            allss.append("<div align=\"left\"  class=\"print-body\" ><b>&nbsp;&nbsp;&nbsp;&nbsp;a)\t0-6个月：0%；</b></div>");
            allss.append("<div align=\"left\"  class=\"print-body\" ><b>&nbsp;&nbsp;&nbsp;&nbsp;b)\t7-12个月：25%；</b></div>");
            allss.append("<div align=\"left\"  class=\"print-body\" ><b>&nbsp;&nbsp;&nbsp;&nbsp;c)\t13-24个月：50%（仅适用于24个月期的轮胎保障服务产品）。</b></div>");

        }
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>3.\t服务期限</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;3.1.\t12个月期轮胎保障服务产品的服务期限为本服务合同生效后，连续12个月或享权车辆于本服务合同生效后已累计行驶1.5万公里（以先到者为准）；或</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;3.2.\t24个月期轮胎保障服务产品的服务期限为本服务合同生效后，连续24个月或享权车辆于本服务合同生效后已累计行驶3万公里（以先到者为准）；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;3.3.\t具体以第I.部分第四条中载明的已选轮胎保障（轮胎置换）服务项下所显示的服务期限起始日期和终止日期为准。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>4.\t除外情况</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.1.\t出现下列任一情形时，车主不能享受服务：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.1\t已超过服务期限或享权车辆于本服务合同生效后已累计行驶1.5万或3万公里（依适用）（服务期限与累计行驶里程两者以先到者为准）；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.2\t申请服务时，轮胎纹理磨损达到或已超过磨损线（即轮胎更换表示“▲”）或胎面剩余花纹磨损深度小于3mm；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.3\t受损轮胎被盗、被遗弃或其他任何原因导致无法收回轮胎；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.4\t轮胎的识别码、品牌型号等被破坏、移除、磨损或其他原因无法进行识别；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.5\t轮胎曾进行过修补；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.6\t车主要求重置的轮胎及其所属的机动车辆信息、使用性质与本服务合同中记载不一致的；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.7\t重置不同品牌、型号或规格的轮胎；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.8\t轮胎生产时国内市场技术水平尚不能发现的缺陷；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.9\t车主的故意行为、重大过失、欺诈、不诚实、违法犯罪行为所导致的轮胎事故；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.10\t车主使用、维护不当；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.11\t提供有偿服务的私人车辆及七座以上客车发生事故的轮胎损失；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.12\t因交通意外、轮胎安装错误、超速或超载行驶、过高或过低胎压等原因导致的轮胎损坏；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.13\t地震、雷击、火灾、爆炸、暴雨、洪水、台风等灾害；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.14\t因自然磨损、腐蚀、人为损害、保管不当等原因导致的轮胎损坏；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.15\t行政行为、司法行为。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.2.\t下列损失、费用和责任，不在服务范围内：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.1\t根据第2.3款规定的轮胎更换服务费用中客户自行承担的部分；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.2\t因汽车厂家对行车安全的相关要求必须或建议同时更换与受损轮胎同轴的对侧轮胎而产生的额外轮胎更换费用；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.3\t任何形式的人身伤害、财产损失，及除本服务合同规定外的其他任何费用支出； </div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.4\t服务过程中所产生的任何间接损失、赔偿责任；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.5\t道路救援、拖车、出租车或者租车等服务费用；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.6\t任何情况下更换车锁或维修车锁的费用。</div>");


        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>5.\t申请材料</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.1.\t车主需提供确认享权车辆轮胎的损失性质、原因、程度等有关证明和资料，包括但不限于：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.1\t享权车辆（带牌照）的照片；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.2\t车主的身份证明文件或营业执照；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.3\t本服务合同；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.4\t车主服务使用确认书；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.5\t车辆的有效行驶证；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.6\t里程表读数照片；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.7\t损失轮胎位置照片；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.8\t具体损坏部位照片；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.9\t损失轮胎带 DOT 码的局部照片；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.10\t新轮胎安装后的照片及带DOT码的局部照片；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.11\t享权车辆轮胎损失情况声明书；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.12\t索赔申请书。</div>");
        if (print_version==1)
        {
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.2.\t如车主因自身原因不便或无法回到与其签署了服务合同的原授权经销商处申请轮胎置换服务的，须与实际提供该服务的授权经销商签署《BMW/MIN轮胎保障服务服务授权声明书》。</div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.3.\t因车主未能提交任何上述约定的申请资料，导致部分或全部服务责任无法确定，对于无法确定的部分不能享受服务。</div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.4.\t若由于车主原因导致未完成轮胎置换或授权经销商未能提供服务的，车主不能主张本服务合同项下的任何服务和/或补偿。</div>");

        }else
        {
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.2.\t因车主未能提交任何上述约定的申请资料，导致部分或全部服务责任无法确定，对于无法确定的部分不能享受服务。</div>");
            allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.3.\t若由于车主原因导致未完成轮胎置换或授权经销商未能提供服务的，车主不能主张本服务合同项下的任何服务和/或补偿。</div>");

        }


        allss.append("<br>");
        return  allss.toString();
    }
    public String contractPrintMotor(String data)
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
        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;

        if (jsonObject.keySet().contains("contractno"))contractno=jsonObject.get("contractno").getAsString();else return  GetErrorString(1,"");
        //if (jsonObject.keySet().contains("codeurl"))codeurl=jsonObject.get("codeurl").getAsString();
        codeurl="wxcheck/wxCheck.html?contractno="+contractno;
        StringBuilder allss = new StringBuilder();
        String dealerno="";
        String sqlstring="select * from mt_contract where contractno=?";
        Map<String,Object> map=queryForMap(jdbcTemplate,sqlstring,contractno);
        String brand="",vehicletype="",vin="",Invoiceno="",Invoicedate="",guideprice="",Invoiceprice="",pname="";
        String cname="",IdNo="",caddress="",insurancecompany="",Businessinsurancepolicyno="",insurancepolicyno="",productid="",companyname="",cars="";
        String contact_person="",contact_tel="",originInvoicedate="",detail_productid="",keytype="";
        int iscompany=0,carkey=0,isnewcar=1;
        String retailprice="0",rti_retailprice="0",mileage="",tire_retailprice="0",carkey_retailprice="0",group_retailprice="0";
        String paytime="";

        if (map!=null)
        {
            paytime=parseString( parseDate( setting.NUllToSpace(map.get("paytime")),"yyyy-MM-dd HH:mm:ss"),"yyyy-MM-dd");
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
            insurancepolicyno=setting.NUllToSpace(map.get("forceno"));
            companyname=setting.NUllToSpace(map.get("companyname"));
            iscompany=setting.StrToInt(map.get("iscompany")+"");
            contact_person=setting.NUllToSpace(map.get("contact_person"));
            contact_tel=setting.NUllToSpace(map.get("contact_tel"));
            pname=setting.NUllToSpace(map.get("pname"));
            cars=setting.NUllToSpace(map.get("cars"));
            originInvoicedate=setting.NUllToSpace(map.get("originInvoicedate"));
            retailprice=setting.NUllToSpace(map.get("retailprice"));

            mileage=setting.NUllToSpace(map.get("mileage"));
            keytype=setting.NUllToSpace(map.get("keytype"));
            isnewcar=setting.StrToInt(map.get("isnewcar")+"");
        }

        sqlstring="select * from mt_product where productid=?";
        map=queryForMap(jdbcTemplate,sqlstring,productid);
        int rti=0,tire=0;
        String p_cars="",groupname="",keyname="";
        boolean keygroup=false,rtigroup=false,tiregroup=false;
        if (map!=null)
        {
            detail_productid=setting.NUllToSpace(map.get("detail_productid"));
            carkey=setting.StrToInt(map.get("carkey")+"");
            rti=setting.StrToInt(map.get("rti")+"");
            tire=setting.StrToInt(map.get("tire")+"");
            p_cars=setting.NUllToSpace(map.get("cars"));
            groupname=setting.NUllToSpace(map.get("groupname"));

        }


        List<Map<String, Object>>list;


        sqlstring="select * from dealer where dealerno=?";
        map=queryForMap(jdbcTemplate,sqlstring,dealerno);
        String dealername="",address="",tel="";
        if (map!=null)
        {
            dealername=setting.NUllToSpace(map.get("dealername"));
            address=setting.NUllToSpace(map.get("address"));
            tel=setting.NUllToSpace(map.get("tel"));
        }

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
       /* allss.append("<hr/>");
        allss.append("<div align=\"right\"><img src=\""+pathurl+"images/"+brand.toLowerCase()+".png\"    alt=\"\"/></div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div align=\"left\" class=\"print-body\">尊敬的"+brand+"车主：\n" );
        allss.append("<br>");
        allss.append("<br>");
        allss.append("&nbsp;&nbsp;&nbsp;&nbsp;您好！\n");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("&nbsp;&nbsp;&nbsp;&nbsp;感谢您选择了"+brand+"保障服务！");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("&nbsp;&nbsp;&nbsp;&nbsp;"+brand+
                "保障服务致力于服务每一位给予我们信赖的车主，为您的爱车提供更贴心、更丰富、更全面的保障。为了更好地守护您的爱车，伴您无忧出行，尽享驾驶乐趣，选择我们的"+brand+"保障服务产品是您省心又省钱的明智决定。\n");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("&nbsp;&nbsp;&nbsp;&nbsp;"+brand+"保障服务产品*涵盖了以下服务：\n");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("&nbsp;&nbsp;&nbsp;&nbsp;<b>悦然焕新服务</b>——助你无畏前路，放胆驰骋\n");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("&nbsp;&nbsp;&nbsp;&nbsp;"+
                "若爱车遭遇意外事故或者被盗等情形而造成的车辆全损或推定全损，您只需回到原授权经销商处申请置换一台 "+brand+" 新车。我们将补贴您机动车辆保险赔付之外的车辆折旧损失，更在此基础上给予您一定金额的新车购置费用补贴，包括车辆购置税、机动车辆保险保费及其他相关费用。\n" );
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("&nbsp;&nbsp;&nbsp;&nbsp;<b>钥匙保障服务</b>——有备有换，才能有备无患\n" );
        allss.append("<br>");
        allss.append("<br>");
        allss.append("&nbsp;&nbsp;&nbsp;&nbsp;  对于不同车系和车型所适配的车辆钥匙，在至多长达3年的服务期限内，若爱车的钥匙发生意外丢失或被盗，您只需向原授权经销商提出重置钥匙的服务申请，即可获得与原车钥匙同型号、同规格的全新原厂车钥匙。\n" +
                "\n" );
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("&nbsp;&nbsp;&nbsp;&nbsp;"+   "<b>轮胎保障服务</b>——心之所向，皆为坦途\n" );
        allss.append("<br>");
        allss.append("<br>");
        allss.append("&nbsp;&nbsp;&nbsp;&nbsp;"+    "在日常驾驶和使用爱车的过程中，如遇新车轮胎或是您在"+brand+"售后服务处购买的星标轮胎发生鼓包或爆胎，您可立即返回原授权经销商处置换与其同品牌、同规格（或同等级别）的新轮胎。\n" );
        allss.append("<br>");
        allss.append("<br>");
        allss.append("&nbsp;&nbsp;&nbsp;&nbsp;"+
                "*具体适用车辆、可购产品及保障内容详情请咨询您所在地的"+brand+"授权经销商，并以"+brand+"授权经销商店内相关保障服务的最新产品信息及"+brand+"保障服务合同所载条款为准。\n");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("&nbsp;&nbsp;&nbsp;&nbsp;"+
                "再次真诚感谢您对"+brand+"保障服务的关注和支持，我们也将以更贴心、更便捷、更持久的服务回馈于您，为您和您的爱车保驾护航！\n</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        codeurl=qrCodeUtil.createQrCode(pathurl+codeurl);
        allss.append("<hr/>");*/
        StringBuilder ss=new StringBuilder();
        codeurl=qrCodeUtil.createQrCode(pathurl+codeurl);
        String title="BMW MOTORRAD保障服务合同";
        allss.append("<div align=\"center\" class=\"print-title\">" + title + "</div>");
        allss.append("<br>");
        allss.append("<div align=\"center\" class=\"print-body\">I.\t服务合同信息页</div>");
        allss.append("<div align=\"right\"><img src=\""+pathurl+codeurl+"\"  style=\"width: 100px;\"  alt=\"\"/></div>");
        allss.append("<div align=\"right\" size=\"2\">扫描二维码，验证服务合同与保障信息</div>");
        allss.append("<div align=\"right\">合同编号：【"+contractno+"】</div>");
        allss.append("<br>");
        allss.append("<div align=\"left\" class=\"print-body\">车主：<u>"+cname+"</u></div>");
        allss.append("<br>");
        allss.append("<div align=\"left\" class=\"print-body\">授权经销商：<u>"+dealername+"</u></div>");
        allss.append("<br>");
        allss.append("<div align=\"left\" class=\"print-body\">&nbsp;&nbsp;&nbsp;&nbsp;通过签署本BMW MOTORRAD保障服务合同（以下简称“服务合同”），您同意接受由该授权经销商提供的悦然焕新服务和钥匙保障服务（以下单独或合称为“服务”或“保障服务”）及由中国太平洋财产保险股份有限公司承保的驾乘人员人身意外伤害保险和随车行李物品损失保险（以下单独或合称为“附赠保险”）。为了便于授权经销商向您提供更贴心的服务和更全面的保障，请务必仔细阅读本服务合同条款和条件，并准确提供下列信息：</div>");
        allss.append("<br>");
        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" colspan=\"4\" bgcolor=\"#002060\"><font color=\"#fff\">一、享权车辆与保障配件信息</font> </td></tr>");
        allss.append("<tr><td colspan=\"4\" ><b>享权车辆</b></td></tr>");
        allss.append("<tr><td width=\"25%\">品   牌：</td><td width=\"25%\">"+brand+"</td>");
        allss.append("<td width=\"25%\">车   型：</td><td width=\"25%\">"+vehicletype+"</td></tr>");

        allss.append("<tr><td>车架号码（VIN码）：</td><td>"+vin+"</td>");
        allss.append("<td>车辆用途：</td><td>     非   营   运</td></tr>");
        allss.append("<tr><td colspan=\"2\">车辆购买时间（首次购车发票日期）：</td><td colspan=\"2\">"+Invoicedate+"</td>");
        allss.append("</tr>");
       // allss.append("<td>当前里程表读数：</td><td>"+mileage+"</td></tr>");
        allss.append("<tr><td>车辆官方建议零售价\n" +
                "（仅适用于新车）：\n</td><td>"+formatMoney(guideprice)+"</td>");
        allss.append("<td>新车购车发票价\n" +
                "或二手车销售发票价：\n</td><td>"+formatMoney(Invoiceprice)+"</td></tr>");
      //  if (carkey!=0)
        {
            allss.append("<tr><td colspan=\"4\" ><b>享权车辆钥匙</b></td></tr>");
            allss.append("<tr><td width=\"25%\">钥匙型号和规格：</td><td colspan=\"3\" >"+keyname+"</td></tr>");
        }
        allss.append("</table>");

        allss.append("<br>");


        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\" colspan=\"2\"><font color=\"#fff\" >二、服务提供方信息：</font> </td></tr>");

        allss.append("<tr><td width=\"25%\">授权经销商名称：</td><td>"+dealername+"</td></tr>");
        allss.append("<tr><td width=\"25%\">地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址：</td><td>"+address+"</td></tr>");
        allss.append("<tr><td width=\"25%\">电&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;话：</td><td>"+tel+"</td></tr>");
        allss.append("</table>");
        allss.append("<br>");


        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\" colspan=\"2\"><font color=\"#fff\" >三、车主信息：</font> </td></tr>");

        if (companyname.equals(""))companyname=cname;
        if (iscompany==0)
            allss.append("<tr><td >姓名/企业名称：</td><td>"+cname+"</td></tr>");

        else {
            allss.append("<tr><td >姓名/企业名称：</td><td>"+companyname+"</td></tr>");
            allss.append("<tr><td >办理人姓名：</td><td>"+contact_person+"</td></tr>");
            allss.append("<tr><td >办理人电话：</td><td>"+contact_tel+"</td></tr>");

        }
        allss.append("<tr><td >身份证号码或统一社会信用代码：</td><td>"+IdNo+"</td></tr>");
        allss.append("<tr><td >地&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;址：</td><td>"+foramtss(30,caddress)+"</td></tr>");
        allss.append("</table>");
        allss.append("<br>");

        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\" colspan=\"2\"><font color=\"#fff\" >四、享权车辆摩托车保险信息：</font> </td></tr>");
        allss.append("<tr><td >摩托车保险公司：</td><td>"+insurancecompany+"</td></tr>");
        allss.append("<tr><td >机动车交通事故责任强制保险保单号码：</td><td>"+insurancepolicyno+"</td></tr>");
        allss.append("<tr><td >摩托车商业保险保单号码：</td><td>"+Businessinsurancepolicyno+"</td></tr>");
        allss.append("</table>");
        allss.append("<br>");

        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\" colspan=\"5\"><font color=\"#fff\" >五、产品与内容：</font> </td></tr>");
        allss.append("<tr><td width=\"25%\" rowspan=\"2\" style=\"text-align:center;vertical-align:middle;\">保障服务产品类别</td>");
        allss.append("<td width=\"25%\" style=\"text-align:center;vertical-align:middle;\" rowspan=\"2\">保障服务产品选项</td>");
        allss.append("<td width=\"28%\" style=\"text-align:center;vertical-align:middle;\" colspan=\"2\">服务期限</td>");
        allss.append("<td width=\"22%\" style=\"text-align:center;vertical-align:middle;\" rowspan=\"2\">建议零售价</td></tr>");
        allss.append("<tr><td width=\"14%\" style=\"text-align:center;vertical-align:middle;\" >起始日期</td>");
        allss.append("<td style=\"text-align:center;vertical-align:middle;\">终止日期</td></tr>");



        allss.append("<tr><td>悦然焕新服务\n" +
                "（车辆置换服务）</td>");
        allss.append("<td>新车悦然焕新</td>");
     //   rti_retailprice="/";
      //  carkey_retailprice="/";
        allss.append("<td>"+paytime+"</td><td>"+addYear(Invoicedate,rti)+"</td><td rowspan=\"2\" style=\"text-align:center;vertical-align:middle;\">"+retailprice+"</td></tr>");
        allss.append("<tr><td>钥匙保障服务\n" +
                "（机动车辆钥匙重置服务）</td>");
         if (pname.contains("2800钥匙"))       allss.append("<td>2800钥匙1把</td>");
        else  if (pname.contains("1000钥匙"))       allss.append("<td>1000钥匙1把</td>");
        allss.append("<td>"+paytime+"</td><td>"+addYear(Invoicedate,rti)+"</td></tr>");
        allss.append("<tr><td colspan=\"2\"></td><td colspan=\"2\" style=\"text-align:center;vertical-align:middle;\">合  计：</td>");
        allss.append("<td  style=\"text-align:center;vertical-align:middle;\">"+retailprice+"</td></tr>");
        allss.append("<tr><td colspan=\"5\">附赠保险内容 </td></tr>");
        allss.append("<tr><td colspan=\"2\">驾乘人员人身意外伤害保险</td>");
        allss.append("<td>"+paytime+"</td><td>"+addYear(paytime,rti)+"</td><td rowspan=\"2\" style=\"text-align:center;vertical-align:middle;\">/</td></tr>");
        allss.append("<tr><td colspan=\"2\">随车行李物品损失保险</td>");
        allss.append("<td>"+paytime+"</td><td>"+addYear(paytime,rti)+"</td></tr>");


        allss.append("</table>");
        allss.append("<br>");
        allss.append("<table class=\"table-print\" border=\"1\"  width=\"100%\" ><tr>");
        allss.append("<td align=\"left\" bgcolor=\"#002060\"><font color=\"#fff\">六、车主声明</font> </td></tr>");
        allss.append("<tr><td ><b>车主声明：");
        allss.append("<div>&nbsp;&nbsp;&nbsp;&nbsp;提供保障服务的授权经销商已通过上述书面形式向本人详细介绍并提供了保障服务的服务内容、合同和条款。本人已完整阅读本服务合同，清楚知晓本服务合同的各项条款，特别是保持保障服务有效的“服务前提”、将导致无法获得服务的“除外情况”和申请服务所必需的“申请资料”等条款。</div>");
        allss.append("<div>&nbsp;&nbsp;&nbsp;&nbsp;本人特此不可撤销地同意和委托提供保障服务的由授权经销商及其第三方服务提供商关联公司使用必要投保所必需的个人信息并代为向中国太平洋财产保险股份有限公司投保和订立保险合同并激活驾乘人员意外伤害保险和随车行李物品损失保险。</div>");
        allss.append("<div>&nbsp;&nbsp;&nbsp;&nbsp;本人同意，提供保障服务的授权经销商及其关联公司可以将本服务合同信息及个人信息提供给为其提供相关服务的任何其他实体（包括但不限于为本服务合同提供承保服务的保险公司、第三方服务提供商）、提供数据分析、处理的技术服务公司，以及任何实际的或可能的参与人、代理人。前述信息接收方应有权依据其各自的规定、适用的法律、法规及合约使用信息｡</div>");
        allss.append("<div>&nbsp;&nbsp;&nbsp;&nbsp;本人已充分理解并接受上述内容，同意以此作为订立服务合同的依据，并愿意遵守本服务合同的条款。</div>");

        allss.append("</b></td></tr>");
        allss.append("<tr><td >");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>车主签字（适用于个人客户）：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>车主盖章（适用于企业客户）：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>授权经销商："+dealername+"</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<div>授权经销商盖章：&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;日期：</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("</td ></tr>");
        allss.append("</table>");
        allss.append("</body>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        return allss+mt_commanstring()+mt_rtistring()+mt_keystring()+mt_accident();
    }
    private String mt_commanstring()
    {
        StringBuilder allss = new StringBuilder();
        String title="II．\t通用条款";
        allss.append("<div align=\"center\" class=\"print-body\" ><b>" + title + "</b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;感谢您签署本服务合同购买保障服务。请您仔细阅读本服务合同条款和条件，包括但不限于本通用条款和下文各保障服务条款。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>1.\t服务合同双方</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.1.\t本服务合同由您和授权经销商于本服务合同第I．部分签署栏所载日期共同签署。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.2.\t本服务合同项下的保障服务应由授权经销商按照本服务合同条款和条件向您提供。您可以于享权车辆发生本服务合同第III．部分、第IV．部分和/或第V．部分规定的享权事件且满足服务前提时通过联系授权经销商相应申请服务，并按照要求提交所需材料。如无法联系授权经销商，可通过联系保障服务之保险保障和承保服务提供商申请服务，咨询热线电话400-610-6200。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>2.\t服务费用</u></b></div>");
        //  allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.\t在本服务凭证签署时，您应立即且毫无延迟地按照下文相关保障服务产品条款所载的服务产品价格表向授权经销商足额支付相应的服务产品价格。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.\t在本服务合同签署时，您应按照授权经销商提供的保障服务价格表向授权经销商足额支付相应的服务费用。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.2.\t在收到您所支付的服务费用全额后三（3）个工作日内，授权经销商应向您开具相应金额的发票。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>3.\t服务地区及适用法律</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;3.1.\t本服务合同项下的服务地区为中华人民共和国境内（仅为本服务合同之目的，不包括中国香港、中国澳门和中国台湾）。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;3.2.\t本服务合同以及因本服务合同所产生或与之有关的所有事宜均应受中华人民共和国法律管辖，并以中华人民共和国法律作理解、解释和执行。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;3.3.\t如发生争议，您和授权经销商均有权向授权经销商所在地有管辖权的法院提起诉讼。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>4.\t服务合同生效和终止</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.1.\t就本服务合同项下每一保障服务，该保障服务相关条款自本服务合同第I．部分列明的对应服务期限的起始日期零时起生效，至相关服务期限届满、根据下文第4.3条相应保障服务提供完毕时或根据下文第6.6条终止。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.2.\t就本服务合同项下您已购买但尚未申请任何一项保障服务（若您于本服务合同项下仅购买了一项保障服务，则您应尚未申请该项保障服务），您有权在任何时候提前六十（60）个自然日书面通知授权经销商终止该等全部或部分服务，但前提是您应签署和/或提供授权经销商所需的与服务合同终止有关的文件和证明。除法律另有规定和本服务合同另有约定外，服务期限开始后解除本服务合同的，授权经销商应当将已收取的服务费用，按照日比例扣除自服务期限的起始日期起至合同解除之日（您书面提出申请之日）止应收取的部分后，授权经销商将收到上述终止通知后六十（60）个自然日内向您返还相应比例部分的服务费用余额（如有）。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.3.\t若就本服务合同项下您已购买悦然焕新服务和钥匙保障服务，则本服务合同原则上应于所有服务提供完毕后自动终止。尽管有前述规定，一旦您就享权车辆申请并已获得悦然焕新服务，无论该等服务是否由授权经销商提供，则本服务合同应于该等服务提供完毕后自动终止。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>5.\t您的隐私与信息安全</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.1.\t授权经销商仅可为本服务合同之目的，向您收集与本服务合同项下所述保障服务相关的个人信息。对于此类信息，授权经销商应确保其员工、代表、代理人、经理和关联企业严格保密，且不得向任何第三方披露（除非为履行本服务合同而合法使用，或根据相关法律法规或监管部门要求）。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>6.\t您的义务</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;6.1.\t您必须正确且合理使用本服务合同项下的享权车辆。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b>&nbsp;&nbsp;6.2.\t在相关服务期限内，就悦然焕新服务和钥匙保障服务，若您未经授权经销商事先书面同意转让享权车辆，则您将丧失本服务合同项下的权益。</b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;6.3.\t在申请保障服务时，您应按照相关服务条款：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.3.1.\t及时与授权经销商联系；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.3.2.\t尽快将本服务合同项下的享权车辆送至授权经销商处维修（如适用）；且</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6.3.3.\t尽快向授权经销商、太保苏分（依适用）提供与该等保障服务相关的各种证明和资料，并确保该等证明和资料具有合法性、真实性和完整性。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;6.4.\t如因您未能完全履行上文第6.1条约定的义务，导致保障服务部分或全部无法提供，授权经销商对此不承担责任。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;6.5.\t如因您未能完全履行上文第6.1条约定的义务，导致授权经销商遭受任何损失，授权经销商有权向您索赔该等损失，包括但不限于要求您支付等额于已产生之费用或已给予之优惠折扣的金额。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;6.6.\t在出现上文第6.4条或第6.5条所述情况时，授权经销商有权向您发出书面通知立即终止本服务合同项下与相关服务有关的条款，且相关服务费用将不予退还。</div>");


        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>7.\t其他相关定义</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;7.1.\t“太保苏分”：指为悦然焕新服务和钥匙保障服务及驾乘人员人身意外伤害保险和随车行李物品损失保险，提供保险保障和承保服务的中国太平洋财产保险股份有限公司苏州分公司。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;7.2.\t“服务期限”：本服务合同第I.部分第五条所载的服务期限。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;7.3.\t“享权车辆”：本服务合同中列明的车辆（以登记的车架号（VIN）为准）。</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        return  allss.toString();
    }
    private  String mt_accident()
    {
        StringBuilder allss = new StringBuilder();
        String title="V.\t驾乘人员意外伤害保险与随车行李物品损失保险 ";

        allss.append("<div align=\"center\" class=\"print-body\" ><b>" + title + "</b></div>");
     //   allss.append("<div align=\"center\" class=\"print-body\" >机动车辆钥匙重置服务</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>1.\t概要</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.1.\t由授权经销商及其关联公司为向中国太平洋财产保险股份有限公司进行投保驾乘人员人身意外伤害保险和随车行李物品损失保险。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.2.\t相关保单将在本服务合同生效之日起立即生效 ，并由授权经销商交付与您。  </div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>2.\t保险责任与限额</u></b></div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.\t驾乘人员人身意外伤害保险：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.1.1.\t在保险期间内，保险人按照与投保人的约定对被保险人驾驶或者乘坐保险单约定的机动车辆期间遭受的以下四类风险中的一类或几类承担保险责任：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1)\t被保险人驾驶非营运性质的机动车，在行驶过程中或为维护车辆继续运行（包括加油、加水、故障修理、换胎）的临时停放过程中遭受意外伤害；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2)\t被保险人乘坐他人合法驾驶的非营运性质的机动车，在行驶过程中或为维护车辆继续运行（包括加油、加水、故障修理、换胎）的临时停放过程中遭受意外伤害；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3)\t被保险人驾驶营运性质的机动车，在行驶过程中或为维护车辆继续运行（包括加油、加水、故障修理、换胎）的临时停放过程中遭受意外伤害；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;4)\t被保险人乘坐他人合法驾驶的营运性质的机动车，在行驶过程中或为维护车辆继续运行（包括加油、加水、故障修理、换胎）的临时停放过程中遭受意外伤害。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.1.2.\t赔偿限额应为200,000元人民币。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.1.3.\t保险合同所称意外伤害，指因外来的、突发的、非本意的和非疾病的客观事件为直接原因导致身体受到的伤害。自然死亡、疾病身故、猝死、自杀以及自伤均不属于意外伤害。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.1.4.\t详见《驾乘人员人身意外伤害保险（2022版）条款》第八条及相关保单所载保险责任。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.2.\t随车行李物品损失保险：</b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.2.1.\t在保险期间内，由于下列原因造成保险财产直接损失，保险人依照保险合同的约定负责赔偿：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1)\t雷击、暴风、暴雨、洪水、龙卷风、冰雹、台风、飓风；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2)\t非地震或海啸引发的地陷、崖崩、突发性滑坡、泥石流、雪崩、冰陷、暴雪、冰凌、沙尘暴；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3)\t火灾、爆炸；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;4)\t机动车辆发生碰撞、倾覆、行驶中坠落，外界物体倒塌、飞行物体及其他空中运行物体坠落；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;5)\t码头、桥梁和隧道坍塌；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;6)\t机动车辆在行驶中或在停车场、居住区的院内停放期间，发生抢劫或有明显撬窃痕迹的盗窃。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.2.2.\t赔偿限额应为1,000元人民币。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2.2.3.\t详见《随车行李物品损失保险条款》第四条及相关保单所载保险责任。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>3.\t保险期间</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;除非另有约定，保险期间为保单生效后连续二十四（24）个月。具体保险期间应以保单中载明的起止日期为准，并在本服务合同第I.部分第五条中载明的附赠内容项下所显示的服务期限起始日期和终止日期为准。  </div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>4.\t除外情况</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.1.\t驾乘人员人身意外伤害保险：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp4.1.1.\t因下列原因之一，直接或间接造成被保险人身故、残疾的，保险人不负任何给付保险金责任：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1)\t投保人对被保险人的故意杀害或伤害；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2)\t被保险人自致伤害或自杀，但被保险人自杀时为无民事行为能力人的除外；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3)\t因被保险人挑衅或故意行为而导致的打斗、被袭击或被谋杀；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;4)\t被保险人违法、犯罪或者抗拒依法采取的刑事强制措施；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;5)\t被保险人因疾病导致的伤害，包括但不限于猝死、食物中毒、高原反应、中暑、病毒和细菌感染（意外伤害导致的伤口感染不在此限）；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;6)\t被保险人因妊娠、流产、分娩导致的伤害，但意外伤害所致的流产或分娩不在此限；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;7)\t被保险人因药物过敏、整容手术、内外科手术或其他医疗行为导致的伤害；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;8)\t被保险人未遵医嘱私自服用、涂用、注射药物；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;9)\t被保险人因意外伤害、自然灾害事故以外的原因失踪而被法院宣告死亡的；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;10)\t被保险人不遵守有关安全驾驶或乘坐的规定；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;11)\t被保险人驾驶超载机动车辆，因车辆超载引起的意外事故而遭受的伤害；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;12)\t被保险人从事高风险运动、参加任何职业或半职业体育运动期间，包括但不限于各种车辆表演、车辆竞赛或训练等；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;13)\t任何生物、化学、原子能武器，原子能或核能装置所造成的爆炸、灼伤、污染或辐射；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;14)\t恐怖袭击。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.2.\t下列任一情形下，保险人对被保险人身故、残疾不负任何给付保险金责任：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1)\t被保险人存在精神和行为障碍（以世界卫生组织颁布的《疾病和有关健康问题的国际统计分类》第十次修订版（ICD-10）为准）期间；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2)\t战争、军事行动、暴动或武装叛乱期间；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3)\t被保险人醉酒或受毒品、管制药物的影响期间；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;4)\t被保险人酒后驾车、无有效驾驶证驾驶或驾驶无有效行驶证的机动车期间。</div>");



        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.2.\t随车行李物品损失保险：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.1.\t下列原因造成的保险财产的损失、费用，保险人不负责赔偿：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1)\t地震、海啸及其引起的次生灾害；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2)\t核爆炸、核裂变、核聚变，放射性污染和其他各种环境污染；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3)\t行政行为或司法行为；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;4)\t战争、敌对行为、军事行动、武装冲突、罢工、暴乱、骚动、恐怖活动；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;5)\t投保人、被保险人或其允许的驾驶人、乘车人的故意行为、重大过失行为或违法犯罪行为；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;6)\t被保险人未采取锁紧车门、关闭车窗、启动防盗装置等合理的安全保护措施；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;7)\t保险财产的自然磨损、内在或潜在缺陷、自然损耗。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.2.\t下列情况下，无论何种原因造成保险财产的损失或费用，保险人均不负责赔偿：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;1)\t驾驶人饮酒、吸食或注射毒品、服用国家管制的精神药品或者麻醉药品；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;2)\t驾驶人无驾驶证，驾驶证被依法扣留、暂扣、吊销、注销期间；驾驶与驾驶证载明的准驾车型不相符合的机动车；依照法律法规或公安机关交通管理部门有关规定不允许驾驶机动车辆的其他情形下驾车；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;3)\t机动车辆没有公安交通管理部门核发的行驶证和号牌，或行驶证和号牌不在有效期内，或机动车辆未按规定检验或检验不合格；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;4)\t在被保险人的其他车辆上发生的损失或费用。</div>");


        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.3.\t根据保险合同约定应由被保险人承担的免赔额，保险人不负责赔偿。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.4.\t其他不属于保险责任范围内的损失和费用，保险人不负责赔偿。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>5.\t保险理赔</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.1.\t您申请保险理赔时，应及时按照保单约定向中国太平洋财产保险股份有限公司提供与保险理赔相关的各种证明和资料，包括但不限于索赔申请书、保单、本服务合同、身份证明、交通事故责任认定书等，并确保其真实、准确、完整。中国太平洋财产保险股份有限公司24小时保险服务专线：95500。  </div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.2.\t理赔材料：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;身故保险金申请：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;1)\t保险金给付申请书；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2)\t保险单号；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;3)\t保险金申请人的身份证明</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4)\t公安部门出具的被保险人户籍注销证明、中华人民共和国境内二级以上（含二级）或保险人认可的医疗机构出具的被保险人身故证明书。若被保险人为宣告死亡，保险金申请人应提供中华人民共和国法院出具的宣告死亡证明文件；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5)\t事发当地政府有关部门或者中华人民共和国驻该国的使、领馆出具的意外伤害事故证明；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6)\t保险金申请人所能提供的与确认保险事故的性质、原因、损失程度等有关的其他证明和资料；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;7)\t若保险金申请人委托他人申请的，还应提供授权委托书原件、委托人和受托人的身份证明等相关证明文件。受益人为无民事行为能力人或者限制民事行为能力人的，由其监护人代为申领保险金，并需要提供监护人的身份证明等资料。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;残疾保险金申请：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;1)\t保险金给付申请书；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2)\t保险单号；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;3)\t保险金申请人的身份证明；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4)\t中华人民共和国境内二级以上（含二级）或保险人认可的医疗机构或司法鉴定机构出具的残疾程度鉴定诊断书；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5)\t事发当地政府有关部门或者中华人民共和国驻该国的使、领馆出具的意外伤害事故证明；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;6)\t保险金申请人所能提供的与确认保险事故的性质、原因、损失程度等有关的其他证明和资料；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;7)\t若保险金申请人委托他人申请的，还应提供授权委托书原件、委托人和受托人的身份证明等相关证明文件。受益人为无民事行为能力人或者限制民事行为能力人的，由其监护人代为申领保险金，并需要提供监护人的身份证明等资料。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;8)\t保险人认为被保险人提供的有关索赔的证明和资料不完整的，将及时一次性通知投保人、被保险人补充提供。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.2.2.\t随车行李物品损失保险：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;1)\t保险单号 或保险凭证，以及能证明损失保险财产价值的相关凭据；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;2)\t属于道路交通事故的，被保险人应当提供机动车辆的行驶证、驾驶证、公安交通管理部门或法院等机构出具的事故证明、有关法律文书；属于非交通事故的应提供相关的事故证明；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;3)\t保险财产清单及施救费用的单据；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4)\t被保险人所能提供的其他与保险事故的性质、原因、损失程度等有关的证明和资料；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5)\t本保险责任范围内的盗窃、抢劫所造成的保险财产的直接损失，被保险人需提供盗窃、抢劫所造成的损失清单、相关的公安机关证明。自公安机关立案之日起90天后仍未能破案的，保险人按照约定的赔偿处理方式予以赔偿，最高不超过保险单中载明的盗抢每次事故赔偿限额。</div>");

        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        return  allss.toString();
    }
    private  String mt_keystring()
    {
        StringBuilder allss = new StringBuilder();
        String title="IV.\t钥匙保障服务";

        allss.append("<div align=\"center\" class=\"print-body\" ><b>" + title + "</b></div>");
        allss.append("<div align=\"center\" class=\"print-body\" >机动车辆钥匙重置服务</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>1.\t服务前提</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.1.\t享权车辆：与授权经销商签订了服务合同的车主购买的BMW MOTORRAD品牌摩托车。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.2.\t车龄要求：车主购买本服务时，享权车辆车龄自享权车辆首次购车发票开具之日起计算不得超过三十（30）天。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.3.\t享权车辆的所有权未发生过转让。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.4.\t享权事件：服务合同中列明的享权车辆在服务期限内发生了车辆钥匙丢失或者被盗，车主在服务期限内按照服务合同条款向授权经销商申请重置车辆钥匙服务。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>2.\t服务规则</u></b></div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.\t享权车辆在服务期限内发生了车辆钥匙丢失或被盗，且满足上述第1条规定的服务前提，车主可按照本服务合同条款向授权经销商提出重置车辆钥匙服务。授权经销商将协助车主重置同品牌、同型号和同规格的车辆钥匙，重置车辆钥匙过程中产生的机动车辆钥匙重置费用由授权经销商承担。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.2.\t机动车辆钥匙重置费用包括重置车辆钥匙所需的零配件费用（含钥匙配码的成本）和工时费。</b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.3.\t服务保障限额：在服务期限内，车主至多可享受重置一（1）把同品牌 、同型号和规格车辆钥匙的服务。  </div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.4.\t车主为享权车辆所选择的车辆钥匙重置服务详载于本服务合同第一条。 </div>");


        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>3.\t服务期限</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;服务期限为车主购买钥匙保障（机动车辆钥匙重置）服务生效后至多连续二十四（24）个月，具体以第I.部分第五条中载明的已选钥匙保障（机动车辆钥匙重置）服务项下所显示的服务期限起始日期和终止日期为准。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>4.\t除外情况</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.1.\t出现下列任一情形时，车主不能享受服务：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.1\t损失发生在服务合同生效前，或车主未在服务合同规定的服务期限内提出重置要求；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.2\t损失发生在中华人民共和国（为本服务合同之目的，不含中国香港、中国澳门和中国台湾）境外；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.3\t车主要求重置的车辆钥匙及其所属的机动车辆信息、使用性质与本服务合同中记载不一致的；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.4\t重置不同品牌、型号或规格的车辆钥匙；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.5\t车主的故意行为、欺诈、不诚实、违法犯罪行为；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.6\t车主使用、维护不当；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.7\t车辆钥匙内在或潜在缺陷、自然磨损、自然损耗； </div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.8\t行政行为、司法行为。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.2.\t下列损失、费用和责任，不在服务范围内：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.1\t任何形式的人身伤害、财产损失，及除本服务合同第五条所列“机动车辆钥匙重置费用”外的其他任何费用支出；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.2\t服务过程中所产生的任何间接损失、赔偿责任；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.3\t道路救援、拖车、出租车或者租车等服务费用；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.4\t任何情况下更换车锁或维修车锁的费用。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>5.\t申请材料</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.1.\t车主需提供确认享权车辆钥匙的损失性质、原因、程度等有关证明和资料，包括但不限于： </div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.1\t享权车辆正面外观照片；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.2\t享权车辆的车架号与铭牌照片；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.3\t享权车辆的行驶证正副两页；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.4\t车主身份证复印件或扫描件；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.5\t本服务合同；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.6\t维修工单；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.7\t享权车辆钥匙损失情况声明书（即：《机动车辆钥匙重置合同责任保险出险声明书》）；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.8\t已发生符合服务合同约定损失的证明（钥匙禁用截屏）；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.9\t如车主声明享权车辆钥匙被盗的，则另需提供报警材料正面扫描件。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.2.\t若由于车主原因导致未完成车辆钥匙重置或授权经销商未能提供服务的，车主不能主张本服务合同项下的任何服务和/或补偿。 </div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        return  allss.toString();
    }
    private  String mt_rtistring()
    {
        StringBuilder allss = new StringBuilder();
        String title="III.\t悦然焕新服务";
        allss.append("<div align=\"center\" class=\"print-body\" ><b>" + title + "</b></div>");
        allss.append("<div align=\"center\" class=\"print-body\" >车辆置换服务</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>1.\t服务前提</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.1.\t享权车辆：与授权经销商签订了服务合同的车主购买的BMW MOTORRAD品牌摩托车。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.2.\t车龄要求：车主购买本服务时，享权车辆车龄自享权车辆首次购车发票开具之日起计算不得超过十二（12）个月。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.3.\t享权车辆的所有权未发生过转让。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.4.\t摩托车保险投保要求：在本服务合同约定的服务期限内车主须为享权车辆投保机动车交通事故责任强制保险和足额投保以下全部或部分摩托车商业保险险种，且须保持其于本服务合同约定的服务期限内持续有效。\n" +
                "1.4.1\t摩托车损失险；及\n" +
                "1.4.2\t其它附加险险种（如有）。\n</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.5.\t享权事件：享权车辆因摩托车保险，包括摩托车损失保险、摩托车全车盗抢险、自燃损失保险及前述险种附加的不计免赔特约条款，承保的保险事故造成全损或推定全损，且该险种于保险事故发生时保险期间尚未届满。全损和/或推定全损应以承保享权车辆摩托车保险的摩托车保险公司的定损结果作为唯一依据确定。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;1.6.\t车主同意：车主不可撤销地同意，授权经销商有权与第I.部分第四条列明的摩托车保险公司确认其认定的享权车辆全损/推定全损金额（含残值金额）并要求摩托车保险公司将前述享权车辆全损/推定全损金额（含残值金额）支付给授权经销商。车主将会积极配合授权经销商完成前述事宜并签署必要的法律文件。  </div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>2.\t服务规则</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.1.\t享权车辆在服务期限内遭受全损或推定全损，且满足上述第1条规定的服务前提，车主可向授权经销商申请服务。授权经销商将协助车主置换一辆BMW MOTORRAD品牌的新车，置换新车过程中产生的车辆置换费用 由授权经销商承担。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.2.\t所置换的新车的官方建议零售价不得低于享权车辆官方建议零售价的90%； </div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.3.\t服务保障限额</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;本服务中所指的车辆置换费用 包括车辆折旧费用与机动车置换补偿两部分，其中：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;a)\t车辆折旧费用 = 享权车辆发票价（即：新车购车发票所显示的价格）x已使用月数 x月折旧率（0.6%）, 其中，“已使用月数”指享权车辆新车销售发票显示的购车日期至发生全损或推定全损事故之日计算的已使用月数，不足一月的，不计算折旧；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;b)\t机动车置换补偿：在新车购置过程中产生的机动车置换补偿应包含新车车辆购置税、新车上牌费和新车车船税，享权车辆摩托车保险公司认定的全损/推定全损金额（含残值金额）与享权车辆的车辆折旧费用合计不超过享权车辆发票价，车辆置换费用总和不得超过享权车辆发票价的15%；超出部分由车主自行承担；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;c)\t在服务期限内，车主最多享受一次悦然焕新服务（车辆置换服务）。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.4.\t上述税费、保险费及服务费之金额应当符合同一时期同类税种、保险产品和服务的税率或市场公允价格。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;2.5.\t享权车辆发票价以其购车发票（新车为首次购车发票）所载明之价税合计金额为准。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>3.\t服务期限</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;3.1.\t服务期限至享权车辆车龄的第二十四（24）个月，车龄自享权车辆首次购车发票开具之日起计算；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;3.2.\t具体以第I.部分第五条中载明的已选悦然焕新服务（车辆置换服务）项下所显示的服务期限起始日期和终止日期为准。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>4.\t除外情况</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.1.\t出现下列任一情形时，车主不能享受服务：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.1\t车主要求提供服务的享权车辆信息和/或车主身份信息与本服务合同第一条和第三条记载的信息不一致；车主要求提供服务的享权车辆用途与本服务合同中限定不一致；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.2\t享权车辆遭受的事故发生在本服务合同约定服务期限生效日期前；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.3\t享权车辆因遭受事故仅导致部分损失，未达到本服务合同所约定的车辆全损或推定全损标准（以承保享权车辆机动车辆商业保险之保险公司的定损结果为唯一依据）；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.4\t享权车辆遭受事故时不存在相关摩托车保险保障，或遭受事故后因不符合法律规定或相关摩托车保险约定的理赔条件、未获得与全损或推定全损相应的摩托车保险赔偿；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.5\t享权车辆经过改装或拼装，其工况受到影响；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.6\t享权车辆已被转让给第三人；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.1.7\t车主要求置换的新车的官方建议零售价低于享权车辆官方建议零售价的90%。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.2.\t下列任何原因造成享权车辆全损或推定全损，车主不能就相关损失享受服务：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.1\t车主的故意行为、重大过失、欺诈、不诚实、违法犯罪行为；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.2\t车主使用、维护、保管不当；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.3\t享权车辆内在或潜在缺陷、自然磨损、自然损耗；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.4\t战争、敌对行为、军事行动、武装冲突、恐怖主义活动、罢工、暴动、骚乱；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.5\t核爆炸、核裂变、核聚变；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.6\t放射性污染及其他各种环境污染；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.2.7\t行政行为、司法行为。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;4.3.\t下列损失、费用和责任，不在服务范围内：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.3.1\t在车辆置换过程中，因更换非BMW MOTORRAD品牌新车产生的费用；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.3.2\t任何形式的人身伤害、财产损失，及除本服务合同第六条所列“机动车置换费用”外的其他任何费用支出；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.3.3\t服务过程中所产生的任何间接损失、赔偿责任；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;4.3.4\t因未投保本服务合同第五条约定的摩托车保险任一险种承保的保险事故造成的车辆全损或推定全损，或者该险种的保险期间于保险事故发生时已届满。</div>");

        allss.append("<div align=\"left\"  class=\"print-body\" ><b><u>5.\t申请材料</u></b></div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.1.\t车主需提供确认享权车辆事故性质、原因、损失程度等有关证明和资料，包括但不限于：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.1\t享权车辆发生全损或推定全损时仍处于生效状态的摩托车保险的保单复印件；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.2\t本服务合同；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.3\t享权车辆行驶本复印件、车主驾驶本复印件、车主身份证或企业营业执照复印件；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.4\t享权车辆新车购车发票复印件；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.5\t摩托车保险公司已按照全损、推定全损赔付完毕的证明（划账水单原件，一次性赔偿协议原件或摩托车保险公司盖章的复印件）；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.6\t享权车辆全损或推定全损后处理手续（权益转让书、拍卖或注销解体手续）；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.7\t证明事故发生原因的证明，属于道路交通事故的提供事故证明等等。（证明材料复印件，摩托车保险公司盖章）；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.8\t享权车辆全损概览照片（含车牌号）；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.1.9\t赔偿责任已履行的真实凭证；</div>");


        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.2.\t车主需提供机动车置换补偿的相关材料、单据，包括但不限于：</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.2.1\t新车购车合同；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.2.2\t新车的车辆购置税完税证明；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.2.3\t新车的服务合同和服务费发票，如有；</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;&nbsp;&nbsp;5.2.4\t新车交付所涉及项目的费用发票。</div>");


        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.3.\t因车主未能提交任何上述约定的申请资料，导致部分或全部服务责任无法确定，对于无法确定的部分不能享受服务。</div>");
        allss.append("<div align=\"left\"  class=\"print-body\" >&nbsp;&nbsp;5.4.\t若由于车主原因导致未完成新车置换或授权经销商未能提供服务的，车主不能主张本服务合同项下的任何服务和/或补偿。</div>");
        allss.append("<br>");
        allss.append("<br>");
        allss.append("<br>");
        return  allss.toString();
    }
}
