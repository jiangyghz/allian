package com.bond.allianz.Dao;

import org.springframework.jdbc.core.JdbcTemplate;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.apache.commons.io.IOUtils.toByteArray;

public class setting {
    public  static int jghours=2;
    public  static  String  savePath="\\wximage";
    public  static  String FormatString2(String ss)
    {
        DecimalFormat df = new DecimalFormat("#0.00");
        String money = df.format(NullToZero(ss)); // 单位元
        return money;

    }
    public static Map<String, Object> queryForMap(JdbcTemplate jdbcTemplate,String sqlstring)
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
    public static List<Map<String, Object>> queryForList(JdbcTemplate jdbcTemplate, String sqlstring, Object[] args)
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
    public static List<Map<String, Object>> queryForList(JdbcTemplate jdbcTemplate, String sqlstring)
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
    public static Map<String, Object> queryForMap(JdbcTemplate jdbcTemplate,String sqlstring,Object[] args)
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
    public static Map<String, Object> queryForMap(JdbcTemplate jdbcTemplate,String sqlstring,String args)
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
    public static String NUllToSpace(Object ob)
    {
        String ss="";
        if (ob!=null)ss=ob.toString();
        return  ss;
    }
    public  static  float NullToZero(String ss)
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

    public  static  int StrToInt(String ss)
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
    public static int ConvertDateTimeInt(String time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = simpleDateFormat.parse(time);
            int ts = (int) (date.getTime() / 1000);
            return ts;
        } catch (ParseException e) {
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
    public static long GetTodaytimeTolong(){
        String time=GetNowDate("yyyy-MM-dd")+" 00:00:00";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = simpleDateFormat.parse(time);
            long ts = date.getTime();
            return ts;
        } catch (ParseException e) {
            return 0;
        }
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
    public static int ConvertDateTimeInt(Date date ){

        try {

            int ts = (int) (date.getTime() / 1000);
            return ts;
        } catch (Exception e) {
            return 0;
        }
    }
    public static int GetCurrenntTime( ){

        try {

            int ts = (int) (new Date().getTime() / 1000);
            return ts;
        } catch (Exception e) {
            return 0;
        }
    }
    public static long GetCurrenntTimeToLong( ){

        try {

            long ts = new Date().getTime();
            return ts;
        } catch (Exception e) {
            return 0;
        }
    }
    public  static String getURLContent (String strURL,String data,String requestmode) throws Exception {

        URL url = new URL(strURL);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        PrintWriter out = null;
        if (requestmode == null || requestmode == "")requestmode="GET";
        conn.setRequestMethod(requestmode);

        conn.setRequestProperty("accept", "*/*");
        conn.setRequestProperty("contentType", "utf-8");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
        //设置是否向httpUrlConnection输出，设置是否从httpUrlConnection读入，此外发送post请求必须设置这两个
        //最常用的Http请求无非是get和post，get请求可以获取静态页面，也可以把参数放在URL字串后面，传递给servlet，
        //post与get的 不同之处在于post的参数不是放在URL字串里面，而是放在http请求的正文内。
        conn.setDoOutput(true);
        conn.setDoInput(true);
        //获取URLConnection对象对应的输出流
        out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"utf-8"));
        //发送请求参数即数据
        out.print(data);
        //缓冲数据
        out.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
        String line;
        StringBuffer buffer = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        reader.close();
        conn.disconnect();
        return buffer.toString();
    }
    public static String GetNowDate( String formatss){


        String temp_str="";
        Date dt = new Date();
        //最后的aa表示“上午”或“下午”    HH表示24小时制    如果换成hh表示12小时制
        SimpleDateFormat sdf = new SimpleDateFormat(formatss);
        temp_str=sdf.format(dt);
        return temp_str;
    }
    public static byte[] InputStream2ByteArray(String filePath) throws IOException {

        InputStream in = new FileInputStream(filePath);
        byte[] data = toByteArray(in);
        in.close();

        return data;
    }
    public static String RTrim(String ss,String s)
    {
        if (ss.endsWith(s))ss= ss.substring(0,ss.length() - s.length());
        return  ss;
    }
    public static  String getURLContent (String strURL,String data,String requestmode,String sign,String appId,String timestamp) throws Exception {

        URL url = new URL(strURL);

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        if (requestmode == null || requestmode == "")requestmode="GET";
        conn.setRequestMethod(requestmode);
        conn.setRequestProperty("contentType", "GBK");
        conn.setRequestProperty("connection", "Keep-Alive");
        conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
        conn.setRequestProperty("Content-Type","application/json;charset=UTF-8");
        conn.setRequestProperty("sign", sign);
        conn.setRequestProperty("appid", appId);
        conn.setRequestProperty("timestamp", timestamp);
        conn.setRequestProperty("RequestBody", data);


        //设置是否向httpUrlConnection输出，设置是否从httpUrlConnection读入，此外发送post请求必须设置这两个
        //最常用的Http请求无非是get和post，get请求可以获取静态页面，也可以把参数放在URL字串后面，传递给servlet，
        //post与get的 不同之处在于post的参数不是放在URL字串里面，而是放在http请求的正文内。
        conn.setDoOutput(true);
        conn.setDoInput(true);
        //获取URLConnection对象对应的输出流
        // PrintWriter out = null;
        //out = new PrintWriter(conn.getOutputStream());
        //发送请求参数即数据
        //  out.print(data);

        OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "utf-8");


        out.write(data);
        //缓冲数据
        out.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
        String line;
        StringBuffer buffer = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        reader.close();
        conn.disconnect();
        return buffer.toString();
    }
}
