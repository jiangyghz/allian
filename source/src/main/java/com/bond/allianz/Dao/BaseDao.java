package com.bond.allianz.Dao;

import com.bond.allianz.entity.PageInfo;
import com.bond.allianz.utils.SendMailUtil;
import com.google.gson.*;
import org.apache.commons.io.FilenameUtils;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItemFactory;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.apache.xmlbeans.impl.xb.xsdschema.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class BaseDao {

    @Autowired
    public JdbcTemplate jdbcTemplate;
    @Autowired
    public HttpServletRequest request;
//    @Autowired
//    public ServletContext servletContext;
    @Value("${domain.root}")
    public String domainroot;

    @Value("${upload.wximage}")
    public String wximage;
    /**
     * 分页包装 暂支持 包含一个from的简单sql
     * @param pageindex
     * @param pagesize
     * @param sql
     * @return
     */
    public PageInfo<Map<String,Object>> queryForPage(int pageindex, int pagesize, String sql) {
        return queryForPage(pageindex,pagesize,sql,null);
    }

    /**
     * 分页包装 暂支持 包含一个from的简单sql
     * @param pageindex
     * @param pagesize
     * @param sql
     * @param arg
     * @return
     */
    public PageInfo<Map<String,Object>> queryForPage(int pageindex, int pagesize, String sql, Object[] arg) {
        String limit = " limit " + (pageindex - 1) * pagesize + " , " + pagesize;
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql + limit, arg);
        String countsql = "select count(*) from " + sql.split("from")[1];
        long total = jdbcTemplate.queryForObject(countsql, arg, long.class);
        PageInfo<Map<String, Object>> page = new PageInfo<Map<String, Object>>(list, pageindex, pagesize, total);
        return page;
    }
    public String doubletostring(double d)
    {
       DecimalFormat df=new DecimalFormat("######0.00");
       String ss=df.format(d);
        return  ss;
    }
    public String foramtss(int ii,String ss)
    {
        for (int i=ss.length();i<ii;i++)
            ss=ss+"&nbsp;";
        return  ss;
    }
    public String addYear(String datess,int addyear)
    {
        String enddate="";
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date=df.parse(datess);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.add(Calendar.YEAR, addyear);
            cal.add(Calendar.DATE, -1);
            enddate=df.format(cal.getTime() );
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return enddate;
    }
    public String addDay(String datess,int addday)
    {
        String enddate="";
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date=df.parse(datess);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
           cal.add(Calendar.DATE, addday);
            enddate=df.format(cal.getTime() );
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return enddate;
    }
    public static String parseString(Date date, String pattern)
    {
        String datestring="";
        SimpleDateFormat df=new SimpleDateFormat(pattern);
        try {

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            datestring=df.format(cal.getTime() );
        } catch (Exception e) {
            e.printStackTrace();
        }

        return datestring;
    }
    @Autowired
    WebApplicationContext webApplicationConnect;

    public   ServletContext getContext(){
        ServletContext sc=null;
        try {
            if(request!=null) {
                sc=request.getServletContext();
            }else{
                //WebApplicationContext webApplicationContext = ContextLoader.getCurrentWebApplicationContext();
                sc = webApplicationConnect.getServletContext();
            }
        }catch (Exception ex){
          //  sc =((WebApplicationContext)springTool.getApplicationContext()).getServletContext();
        }
        return  sc;
    }
    public String GetErrorString(int errorcode, String ss)
    {
        ArrayList error = new ArrayList();
        error.add("ok");
        error.add("参数错误");
        String allss = "";
        String ess = "";
        if (errorcode < error.size()) ess = error.get(errorcode).toString(); else ess = ss;
        if (errorcode==1&&!ss.equals(""))ess = ss;
        allss = "{\"errcode\":" + errorcode + ",\"errmsg\":\"" + ess + "\"}";
        return allss;
    }

   public String GetRsultString(List<Map<String, Object>> list,int pageno,int pagesize)
    {

        int count=list.size();
        int i0=(pageno-1)*pagesize;
        int i1=pageno*pagesize;
        if (i1>count)i1=count;
        List<Map<String, Object>> list1=new ArrayList<>();
        for (int i=i0;i<i1;i++)
        {
            list1.add(list.get(i));
        }
        Gson gson = new Gson();
        String  result=gson.toJson(list1);
        list.clear();
        list1.clear();
        result="{\"recordcount\":"+count+",\"pageno\":"+pageno+",\"pagesize\":"+pagesize+",\"record\":"+result+"}";
        return result;
    }
    public String GetResultString1(String sqlstring, JdbcTemplate jdbcTemplate, int pageno, int pagesize) {
        String result ="";
        try {

            String newsqlstring=" select count(*) as sl "+sqlstring.substring(sqlstring.toLowerCase().lastIndexOf("from"));
            Map<String, Object>map=queryForMap(jdbcTemplate,newsqlstring);
            int count=0;
            if (map!=null)
                count=setting.StrToInt(map.get("sl").toString());
            logs.WriteLog(count+"","GetResultString1");
            int i0 = (pageno - 1) * pagesize;
            int i1 = pageno * pagesize;
            sqlstring+=" limit "+i0+","+pagesize;
            List<Map<String, Object>> list1 = queryForList(jdbcTemplate, sqlstring);

            Gson gson = new Gson();
            result = gson.toJson(list1);
            if(list1!=null)list1.clear();
            result = "{\"recordcount\":" + count + ",\"pageno\":" + pageno + ",\"pagesize\":" + pagesize + ",\"record\":" + result + "}";
        }
        catch (Exception ex)
        {
            logs.WriteLog(ex.toString(),"GetResultString1");
        }

        return result;
    }
    public String GetResultString1(String sqlstring, JdbcTemplate jdbcTemplate, int pageno, int pagesize, Object[] args) {
        String result ="";
        try {
            String newsqlstring=" select count(*) as sl "+sqlstring.substring(sqlstring.toLowerCase().lastIndexOf("from"));
            newsqlstring=" select count(*) as sl from ( "+sqlstring.replace("u.username as userid,","")+" )  aaa";
           // newsqlstring=" select count(*) as sl from ( "+sqlstring+" )  aaa";
            Map<String, Object>map=queryForMap(jdbcTemplate,newsqlstring,args);
            int count=0;
            if (map!=null)
                count=setting.StrToInt(map.get("sl").toString());
           // logs.WriteLog(count+"","GetResultString1");
            int i0 = (pageno - 1) * pagesize;
            int i1 = pageno * pagesize;
            sqlstring+=" limit "+i0+","+pagesize;
            List<Map<String, Object>> list1 = queryForList(jdbcTemplate, sqlstring,args);

            Gson gson = new Gson();
            result = gson.toJson(list1);
            if(list1!=null)list1.clear();
            result = "{\"recordcount\":" + count + ",\"pageno\":" + pageno + ",\"pagesize\":" + pagesize + ",\"record\":" + result + "}";
        }
        catch (Exception ex)
        {
            logs.WriteLog(ex.toString(),"GetResultString1");
        }

        return result;
    }
    public String GetRsultString(String sqlstring, JdbcTemplate jdbcTemplate,int pageno,int pagesize)
    {
        List<Map<String, Object>> list = queryForList(jdbcTemplate,sqlstring);
        int count=list.size();
        int i0=(pageno-1)*pagesize;
        int i1=pageno*pagesize;
        if (i1>count)i1=count;
        List<Map<String, Object>> list1=new ArrayList<>();
        for (int i=i0;i<i1;i++)
        {
            list1.add(list.get(i));
        }
        Gson gson = new Gson();
        String  result=gson.toJson(list1);
        list.clear();
        list1.clear();
        result="{\"recordcount\":"+count+",\"pageno\":"+pageno+",\"pagesize\":"+pagesize+",\"record\":"+result+"}";
        return result;
    }

    public String GetRsultString(String sqlstring, JdbcTemplate jdbcTemplate)
    {
        List<Map<String, Object>> list = queryForList(jdbcTemplate,sqlstring);
        Gson gson = new Gson();
        String  result=gson.toJson(list);
        list.clear();
        return result;
    }
    public String GetRsultString(String sqlstring, JdbcTemplate jdbcTemplate, Object[] args)
    {
        List<Map<String, Object>> list = queryForList(jdbcTemplate,sqlstring,args);
        Gson gson = new Gson();
        String  result=gson.toJson(list);
        list.clear();
        return result;
    }
    public JsonArray GetJsonArray(String sqlstring, JdbcTemplate jdbcTemplate, Object[] args)
    {
        List<Map<String, Object>> list = queryForList(jdbcTemplate,sqlstring,args);
        JsonArray jsonArray=new JsonArray();
        Map<String, Object>map;
        JsonObject jb;
        for (int i=0;i<list.size();i++)
        {
            map=list.get(i);
            jb=new JsonObject();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey(); // 获取key值
               jb.addProperty(key, NUllToSpace( map.get(key)));
            }
            jsonArray.add(jb);
        }
        list.clear();
        //     resultss = "{\"recordcount\":" + count + ",\"pageno\":" + pageindex + ",\"pagesize\":" + pagesize + ",\"record\":" + result + "}";
        return jsonArray;
    }
    public JsonObject GetJsonObject(String sqlstring, JdbcTemplate jdbcTemplate, Object[] args,int pageno,int pagesize)
    {

        List<Map<String, Object>> list = queryForList(jdbcTemplate,sqlstring,args);
        JsonArray jsonArray=new JsonArray();
        Map<String, Object>map;
        JsonObject jb;
        int i0=(pageno-1)*pagesize;
        int i1=pageno*pagesize;
        if (i1>list.size())i1= list.size();
        for (int i=i0;i<i1;i++)
        {
            map=list.get(i);
            jb=new JsonObject();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey(); // 获取key值
                jb.addProperty(key, NUllToSpace( map.get(key)));
            }
            jsonArray.add(jb);
        }
        jb=new JsonObject();
        jb.addProperty("recordcount",list.size());
        jb.addProperty("pageno",pageno);
        jb.addProperty("pagesize",pagesize);
        jb.add("record",jsonArray);
        list.clear();
        //     resultss = "{\"recordcount\":" + count + ",\"pageno\":" + pageindex + ",\"pagesize\":" + pagesize + ",\"record\":" + result + "}";
        return jb;
    }
    public static String RTrim(String ss,String s)
    {
        if (ss.endsWith(s))ss= ss.substring(0,ss.length() - s.length());
        return  ss;
    }
    public String GetRsultString(String sqlstring, JdbcTemplate jdbcTemplate, String arg)
    {
        List<Map<String, Object>> list = queryForList(jdbcTemplate,sqlstring,arg);
        Gson gson = new Gson();
        String  result=gson.toJson(list);
        list.clear();
        return result;
    }
    public  Map<String, Object> queryForMap(JdbcTemplate jdbcTemplate,String sqlstring)
    {
        Map<String, Object> map ;
        if (!sqlstring.contains("LIMIT"))sqlstring=sqlstring+" LIMIT 0 , 1";
        try
        {
            map =jdbcTemplate.queryForMap(sqlstring);
        }
        catch (Exception e)
        {
            map=null;
        }
        return  map;
    }
    public  List<Map<String, Object>> queryForList(JdbcTemplate jdbcTemplate, String sqlstring, String args)
    {
        List<Map<String, Object>> list ;

        try
        {
            list =  jdbcTemplate.queryForList(sqlstring,args);
        }
        catch (Exception e)
        {
            list=null;
        }
        return  list;
    }
    public  List<Map<String, Object>> queryForList(JdbcTemplate jdbcTemplate, String sqlstring, Object[] args)
    {
        List<Map<String, Object>> list ;

        try
        {
            list =  jdbcTemplate.queryForList(sqlstring,args);
        }
        catch (Exception e)
        {
            list=null;
        }
        return  list;
    }
    public  List<Map<String, Object>> queryForList(JdbcTemplate jdbcTemplate, String sqlstring)
    {
        List<Map<String, Object>> list ;

        try
        {
            list =  jdbcTemplate.queryForList(sqlstring);
        }
        catch (Exception e)
        {
            list=null;
        }
        return  list;
    }
    public  Map<String, Object> queryForMap(JdbcTemplate jdbcTemplate,String sqlstring,Object[] args)
    {
        Map<String, Object> map ;
        if (!sqlstring.contains("LIMIT"))sqlstring=sqlstring+" LIMIT 0 , 1";
        try
        {
            map =jdbcTemplate.queryForMap(sqlstring,args);
        }
        catch (Exception e)
        {
            map=null;
        }
        return  map;
    }
    public  Map<String, Object> queryForMap(JdbcTemplate jdbcTemplate,String sqlstring,String args)
    {
        Map<String, Object> map ;
        if (!sqlstring.contains("LIMIT"))sqlstring=sqlstring+" LIMIT 0 , 1";
        try
        {
            map =jdbcTemplate.queryForMap(sqlstring,args);
        }
        catch (Exception e)
        {
            map=null;
        }
        return  map;
    }
    public String getnonce_str()
    {
        int length=32;
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        int randomNum;
        char randomChar;
        Random random = new Random();
        // StringBuffer类型的可以append增加字符
        StringBuffer str = new StringBuffer();

        for (int i = 0; i < length; i++) {
            // 可生成[0,n)之间的整数，获得随机位置
            randomNum = random.nextInt(base.length());
            // 获得随机位置对应的字符
            randomChar = base.charAt(randomNum);
            // 组成一个随机字符串
            str.append(randomChar);
        }
        return str.toString();
    }
    public String GetSign(String nonce, String ttime, String Appid,String appsecret,String data)
    {



        String SignTemp = "appid=" + Appid + "&appsecret=" + appsecret + "&nonce=" + nonce + "&time=" + ttime+ "&data=" + data;

        String sign = MD5.encodeByMD5(SignTemp).toUpperCase();

        return sign;

    }
    public static int GetMonthtime(){
        String time=GetNowDate("yyyy-MM")+"-01 00:00:00";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = simpleDateFormat.parse(time);
            int ts = (int) (date.getTime() / 1000);
            return ts;
        } catch (ParseException e) {
            return 0;
        }
    }
    public String checkSign(String Nonce, String Time, String Sign, String Appid,String data)
    {
        String pathurl=GetPathurl();
        if (pathurl.contains("localhost"))return "";
      // if (pathurl.contains("120.27.208.187"))return "";
      //  if (Appid.equals("jchtest"))return "";
        Map<String ,String>map=new HashMap<>();
        map.put("allianzweb","Y1iQbLnMAQxQ56RWjIqErItcow7zKK2q");
        map.put("ibsapi2022","Rtogw8oz8gdoRY4KHfzzu5toSWIqPqVn");
        String allss = "";
        if (!map.containsKey(Appid)) {
            return "{\"errcode\":\"2\",\"errmsg\":\"appid不正确！\"}";
        }
        String appsecret="";
        appsecret= setting.NUllToSpace(map.get(Appid));
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        if (jsonObject.keySet().contains("data")) {
          //  jsonObject = jsonObject.get("data").getAsJsonObject();
           // data=jsonObject.toString();
            data=jsonObject.get("data").toString();
        }else data="";


        String sign = GetSign(Nonce, Time, Appid,appsecret,data);
        if (sign.equals(Sign.toUpperCase()))
        {
            allss = "";

        }
        else
        {
            String errmsg="签名不正确！";
          /*  if (Appid.equals("ibsapi2022")) {
                String SignTemp = "appid=" + Appid + "&appsecret=" + appsecret + "&nonce=" + Nonce + "&time=" + Time+ "&data=" + data;

                errmsg += "sign:" + sign+"  signstring:"+SignTemp;

            }*/
            return    "{\"errcode\":3,\"errmsg\":\""+errmsg+"\"}";
        }
        if (setting.GetCurrenntTime() - setting.StrToInt(Time) > 120)
        {
            return    "{\"errcode\":4,\"errmsg\":\"签名已经过有效期！\"}";
        }
     /*   String sqlstring="select sign from apisign where sign=?";
        Map<String,Object>map1=queryForMap(jdbcTemplate,sqlstring,sign);
        if (map1!=null)    allss = "{\"errcode\":4,\"errmsg\":\"签名已经失效！\"}";
        else
        {
            sqlstring="insert into apisign (sign,ttime) values (?,"+getCurrenntTime()+")";
            jdbcTemplate.update(sqlstring,sign);
        }*/
        return allss;
    }
    public  String NUllToSpace(Object ob)
    {
        String ss="";
        if (ob!=null)ss=ob.toString();
        if (ss.startsWith("\""))ss=ss.substring(1);
        if (ss.endsWith("\""))ss=ss.substring(0,ss.length()-1);

        if(ss.equals("null"))ss="";
        return  ss;
    }
    public  String GetPathurl()
    {
        return domainroot;
//        try {  //定时程序task没有request请求。root 在登录界面后台有添加值
//            return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + "/";
//        }
//        catch (Exception ex){
//            return servletContext.getAttribute("root").toString();
//        }
    }
    public void SendEmailDealer(String dealerno,String ss,String title)
    {
        String  sql="select email,dealername,brand from dealer where dealerno=?";
      Map<String,Object>  map=queryForMap(jdbcTemplate,sql,dealerno);
      String email="",content="",brand="";
        if (map!=null) {
            email = setting.NUllToSpace(map.get("email"));
            brand= setting.NUllToSpace(map.get("brand"));
            content=setting.NUllToSpace(map.get("dealername"))+":\n\t";
        }
        if (!email.equals(""))
        {
            try {

                content+="    您好！\n\t";
                if (brand.equals("Porsche"))
                {
                    content+=ss;
                }
                else
                {
                    content+=ss;
                }
                SendMailUtil.sendMail(email, title, content, "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static String GetNowDate( String formatss){


        String temp_str="";
        Date dt = new Date();
        //最后的aa表示“上午”或“下午”    HH表示24小时制    如果换成hh表示12小时制
        SimpleDateFormat sdf = new SimpleDateFormat(formatss);
        temp_str=sdf.format(dt);
        return temp_str;
    }
    public static String stampToDate(String formatss,int stamp){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(formatss);
        long lt=(long) stamp*1000;
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static String formatMoney(String moneyAmount_Fen)
    {
        DecimalFormat df = new DecimalFormat("#0.00");
        String money = df.format(Double.parseDouble(moneyAmount_Fen) ); // 单位元
        return money;
    }


    public String uploadJpg( MultipartFile file){

        String savePath =wximage;// request.getServletContext().getRealPath("/wximage/");
    /*    File path = new File(savePath);
        //如果目录不存在
       if(!path.exists()) {
            //创建目录
            path.mkdirs();
        }*/
        String  saveFilename="";
        DiskFileItemFactory factory = new DiskFileItemFactory();
        //2、创建一个文件上传解析器
        ServletFileUpload upload = new ServletFileUpload(factory);
        //解决上传文件名的中文乱码
        upload.setHeaderEncoding("UTF-8");

        try
        {
            if (!file.isEmpty()) {
                String fileName = file.getOriginalFilename();
                saveFilename =Long.toString( setting.GetCurrenntTimeToLong())+(int)(Math.random()* (999999)+1) + "." + FilenameUtils.getExtension(file.getOriginalFilename());
                File dest = new File(savePath + saveFilename);
                try {
                    file.transferTo(dest);
                } catch (IOException e) {
                    logs.error("文件保存错误:",e);
                }
                //result = JsonSerializer(new Object[]{true, newName});
            }

        }catch (Exception e)
        {
            logs.error(e.toString(),"uploadJpg");
        }

        return "wximage/"+saveFilename;
    }
    public  static  float nullToZero(String ss)
    {
        float ii=0;
        try {

            ii=Float.parseFloat(ss);
        }catch (Exception e)
        {
            ii=0;
        }
        return  ii;
    }
    public  static  long nullToInt(String ss)
    {
        long ii=0;
        try {

            ii=Long.parseLong(ss);
        }catch (Exception e)
        {
            ii=0;
        }
        return  ii;
    }
    public  static  int strToShortInt(String ss)
    {
        int ii=0;
        try {

            ii=Integer.parseInt(ss);
        }catch (Exception e)
        {
            ii=0;
        }
        return  ii;
    }
    public static int getCurrenntTime( ){

        try {

            int ts = (int) (new Date().getTime() / 1000);
            return ts;
        } catch (Exception e) {
            return 0;
        }
    }
    public static int GetTodaytime(){
        String time=GetNowDate("yyyy-MM-dd")+" 00:00:00";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = simpleDateFormat.parse(time);
            int ts = (int) (date.getTime() / 1000);
            return ts;
        } catch (ParseException e) {
            return 0;
        }
    }
    public static Date parseDate(String dateStr, String pattern)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date date;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            throw  new RuntimeException("日期转化错误");
        }

        return date;
    }
    public long BetweenDays2(String dateStr1,String dateStr2){
        // 日期字符串
        // 获取日期
        Date date1 = parseDate("2020-01-01", "yyyy-MM-dd");
        try
        {
            date1 = parseDate(dateStr1, "yyyy-MM-dd");
        }catch (Exception e)
        {

        }

        Date date2 = new Date();
        try
        {
            date2 = parseDate(dateStr2, "yyyy-MM-dd");
        }catch (Exception e)
        {

        }
        long betweenDays = (date2.getTime() - date1.getTime()) / (1000L*3600L*24L);
        return  betweenDays;
    }
}
