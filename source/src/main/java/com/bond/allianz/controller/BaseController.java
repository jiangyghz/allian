package com.bond.allianz.controller;


import com.bond.allianz.Dao.MD5;
import com.bond.allianz.Dao.logs;
import com.bond.allianz.Dao.setting;
import com.bond.allianz.entity.User;
import com.bond.allianz.entity.UserInfo;
import com.bond.allianz.global.StringNullAdapter;
import com.bond.allianz.utils.Cryptography;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * controller基类
 */
public class BaseController {

    @Autowired
    public HttpServletResponse response;

    @Autowired
    public HttpServletRequest request;

    @Value("${domain.root}")
    public String domainroot;

    @Value("${domain.enter}")
    public String domainenter;
    /**
     * 序列化
     * @param target
     * @return
     */
    public  String  JsonSerializer(Object target) {
        return JsonSerializer(target,"yyyy-MM-dd");
    }

    /**
     * 序列化
     * @param target
     * @param datetimeformat 默认 yyyy-MM-dd
     * @return
     */
    public  String  JsonSerializer(Object target,String datetimeformat){
        GsonBuilder gsonBuilder = new GsonBuilder();
        if(datetimeformat==null||datetimeformat.isEmpty()){
            datetimeformat="yyyy-MM-dd";
        }
        gsonBuilder.setDateFormat(datetimeformat);//设置时间格式
        gsonBuilder.registerTypeAdapter(String.class, new StringNullAdapter());
        Gson gson = gsonBuilder.create();
        return gson.toJson(target);
    }
  
    /**
     * 反序列化
     * @param target
     * @param object
     * @param <T>
     * @return
     */
    public  <T> T JsonDeserializer(String target,Class<T> object) {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("yyyy-MM-dd");//设置时间格式
        gsonBuilder.registerTypeAdapter(String.class, new StringNullAdapter());
        Gson gson = gsonBuilder.create();
        //Type type = new TypeToken <T>(){}.getType();
        return gson.fromJson(target,object);
    }

    /**
     * 获取参数
     * @param name
     * @return
     */
    public String Params(String name){
        String result=request.getParameter(name);
        if(result!=null&&!"null".equals(result)){
            return result.trim();
        }else{
            return "";
        }
    }

    /**
     * 不获取为空的参数
     * @param request
     * @return
     */
    public static Map<String, String> getAllRequestParam(
            final HttpServletRequest request) {
        Map<String, String> res = new HashMap<String, String>();
        Enumeration<?> temp = request.getParameterNames();
        if (null != temp) {
            while (temp.hasMoreElements()) {
                String en = (String) temp.nextElement();
                String value = request.getParameter(en);
                res.put(en, value);
                if (res.get(en) == null || "".equals(res.get(en))) {
                    res.remove(en);
                }
            }
        }
        return res;
    }
    /**
     * 获取参数转为int 默认为0
     * @param name
     * @return
     */
    public int  ParamsInt(String name){
        return ParamsInt(name,0);
    }
    public int  ParseInt(String input){
        if(input.length()==0){
            return 0;
        }else {
            return Integer.parseInt(input);
        }
    }
    public float  ParseFloat(String input){
        if(input==null||input.length()==0){
            return 0;
        }else {
            return Float.parseFloat(input);
        }
    }
    public int  ParamsInt(String name,int _default){
        String result=request.getParameter(name);
        if(result!=null&&!"null".equals(result)){
            result=result.trim();
        }else{
            result="";
        }
        if(result.length()==0){
            return _default;
        }else {
            return Integer.parseInt(result);
        }
    }
    /**
     * 登录用户信息
     * @return
     * @throws Exception
     */
    public  UserInfo UserInfo(){
        String json="";
        UserInfo result=new UserInfo();
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("allianz")){
                    json= Cryptography.AESDecrypt(cookie.getValue());
                }
            }
        }
        //json=Cryptography.base64Decoder(request.getSession().getAttribute("user").toString());
        if(json!="") {
            try {
                User u = JsonDeserializer(URLDecoder.decode(json, "utf-8"), User.class);
                result = new UserInfo();
                result.UserID = u.getId();
                result.UserName = u.getTruename();
                result.LoginName = u.getUsername();
                result.Act = u.getAct() == "" ? null : (Arrays.asList(u.getAct().split(",")));
                result.AgencyID = u.getAgencyid().toLowerCase() == "zb" ? "" : u.getAgencyid();
                result.Brand=u.getBrand();
            }
            catch (Exception ex){

            }
        }
        return result;
    }

    /**
     * 获取客户端ip
     * @return
     */
    public  String GetIpAddress() {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip.contains(",")) {
            return ip.split(",")[0];
        } else {
            return ip;
        }
    }
    public  String GetPathurl()
    {
        return domainroot;
        //return  request.getScheme()+"://"+request.getServerName() +":"+request.getServerPort()+request.getContextPath()+"/";
    }
    public  String GetRequesturl()
    {
        String url=request.getScheme()+"://"+request.getServerName() +":"+request.getServerPort()+request.getContextPath()+"/";
        return  url.toLowerCase();
    }
    public String PostUrl(String url,String Message)
    {
        String allss="";
        try
        {
            allss= setting.getURLContent(url,Message,"POST");
        }catch (Exception e)
        {
            logs.error("url:"+url+" Message:"+Message+" Error:"+allss);
        }
        return  allss;
    }
    public  String encodeBase64File(String path) throws Exception {
        File file = new File(path);
        FileInputStream inputFile = new FileInputStream(file);
        byte[] buffer = new byte[(int)file.length()];
        inputFile.read(buffer);
        inputFile.close();
        return new BASE64Encoder().encode(buffer);
    }
    public String filecheck (@RequestParam(value="image") MultipartFile file)  {

        if (file==null)return "";
        String resultss="";
        String fileextension= FilenameUtils.getExtension(file.getOriginalFilename()).toLowerCase();
        if (!fileextension.contains("jpg")&&!fileextension.contains("xls")&&!fileextension.equals("png")&&!fileextension.equals("jpeg")&&!fileextension.contains("doc")&&!fileextension.equals("bmp")&&!fileextension.equals("pdf")&&!fileextension.equals("mp4")&&!fileextension.equals("mov")&&!fileextension.equals("amv"))
            resultss="{\"errcode\":3,\"errmsg\":\"文件格式错误！\"}";

        return resultss;
    }
    public String getCookie(String name) {
        String result = "";
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (name.equals(cookie.getName())) {
                    result = cookie.getValue();
                }
            }
        }
        return result;
    }
    public String getPostString() {
        String result = "";
        try {
            InputStream is = request.getInputStream();
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                result = sb.toString();
                //System.err.println(result);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                is.close();
            }
        } catch (Exception e) {
            //logger.error("获取回调数据异常:" + e.getMessage());
        }
        return result;
    }
    public  String getErrorstring(int i)
    {
        JsonObject jb=new JsonObject();
        jb.addProperty("errcode",i+"");
        jb.addProperty("errmsg",getError(i));
        return jb.toString();
    }
    public String getError(int i)
    {
        String ss="";
        if (i==0)ss="ok";
        else  if (i==1)ss="data参数错误！";
        else if (i==2)ss="没有收到参数！";
        return ss;
    }
}
