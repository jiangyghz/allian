package com.bond.allianz.utils;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.Map;

public class HttpUtil {

    /**
     * 扩展post 请求 适配代理
     * @param url
     * @param json
     * @return
     */
    public static String postJson(String url, String json) {
        OutputStreamWriter out = null;
        BufferedReader in = null;
        String result = "";
        try {
            URL realUrl  = new URL(url);
            String isProxy = PropertyConfig.getProperty("proxy.isProxy");
            HttpURLConnection conn;
            if (realUrl.getProtocol().equals("https")) {
                if (Boolean.parseBoolean(isProxy)) {//添加代理
                    conn = (HttpsURLConnection) realUrl.openConnection(getProxy());
                } else {
                    conn = (HttpsURLConnection) realUrl.openConnection();
                }
            } else {
                if (Boolean.parseBoolean(isProxy)) {//添加代理{
                    conn = (HttpURLConnection) realUrl.openConnection(getProxy());
                } else {
                    conn = (HttpURLConnection) realUrl.openConnection();
                }
            }

            conn.setConnectTimeout(30 * 1000);
            conn.setReadTimeout(80 * 1000);
            conn.setUseCaches(false);

            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            // 设置通用的请求属性

//            conn.setRequestProperty("accept", "*/*");
//            conn.setRequestProperty("connection", "Keep-Alive");
//            conn.setRequestProperty("user-agent",
//                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type", "application/json");

            conn.connect();
            // 获取URLConnection对象对应的输出流
            out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            // 发送请求参数
            out.write(json);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(),"UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        }
        catch (Exception e){
            System.out.println("发送 POST 请求出现异常！"+e);
            e.printStackTrace();
        }finally {
            try{
                if(out!=null){
                    out.close();
                }
                if(in!=null){
                    in.close();
                }
            }
            catch(Exception exx){
                exx.printStackTrace();
            }
        }
        return  result;
    }

    /**
     * 获取代理
     * @return
     */
    private static Proxy getProxy(){
        String proxyUrl = PropertyConfig.getProperty("proxy.url");
        String proxyPort = PropertyConfig.getProperty("proxy.port");
        String proxyUsername = PropertyConfig.getProperty("proxy.username");
        String proxyPassword = PropertyConfig.getProperty("proxy.password");
        SocketAddress sa = new InetSocketAddress(proxyUrl, Integer.valueOf(proxyPort));
        Proxy proxy = new Proxy(Proxy.Type.HTTP, sa);
        if((!StringsUtil.isNullOrEmpty(proxyUsername))&&(!StringsUtil.isNullOrEmpty(proxyPassword))){
            Authenticator.setDefault(new BasicAuthenticator(proxyUsername, proxyPassword));
        }
        return proxy;
    }
}
