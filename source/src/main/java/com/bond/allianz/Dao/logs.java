package com.bond.allianz.Dao;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.OptionHelper;
import com.bond.allianz.utils.springTool;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Component
public class logs {

    private static String domainLogs;

    @Value("${domain.logs}")
    public  void setdomainlogs(String logs){
        domainLogs=logs;
    }

    public  static void WriteLog(String ss,String id)
    {
        Logger loger =  getLogger(id);
        loger.error(ss);
//        String saveFilename="";
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        String savePath = request.getServletContext().getRealPath("/logs/"+id);
//        String pathurl=request.getServerName();
//
//        File file = new File(savePath);
//        //如果目录不存在
//        if(!file.exists()) {
//            //创建目录
//            file.mkdirs();
//        }
//
//        saveFilename=setting.GetNowDate("yyyyMMdd")+".txt";
//        try {
//                      FileWriter fw = new FileWriter(savePath+"/"+saveFilename,true);
//                    fw.write(ss);
//                    fw.write("\r\n");
//                    fw.close();
//                    }
//        catch (Exception e) {
//
//                  }
       // Logger loger = LoggerFactory.getLogger(logs.class) ;
        // loger.info(id+":" +ss);
    }
    public  static void error(String ss,String id)
    {

        Logger loger =  getLogger(id);
       // Logger loger =  LoggerFactory.getLogger(logs.class);
        loger.error(id+":" +ss);
    }
    public  static void error(String ss)
    {
        Logger loger =  getLogger("default");
        loger.error(ss);
    }
    public  static void error(String ss,Exception ex)
    {
        Logger loger =  getLogger("default");
        loger.error(ss,ex);
    }
    public static void info(String ss)
    {
        try
        {
            Logger loger =  getLogger("default");
            loger.info(ss);
        }
       catch (Exception e)
       {

       }
    }
    public static void info(String ss,String id)
    {
        Logger loger =  getLogger(id);
        loger.info(ss);
//        String saveFilename="";
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        String savePath = request.getServletContext().getRealPath("/logs/"+id);
//        String pathurl=request.getServerName();
//        File file = new File(savePath);
//        //如果目录不存在
//        if(!file.exists()) {
//            //创建目录
//            file.mkdirs();
//        }
//
//        saveFilename=setting.GetNowDate("yyyyMMdd")+".txt";
//        try {
//            FileWriter fw = new FileWriter(savePath+"/"+saveFilename,true);
//            fw.write(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())+":");
//            fw.write(ss);
//            fw.write("\r\n");
//            fw.close();
//        }
//        catch (Exception e) {
//
//        }
       // Logger loger =  LoggerFactory.getLogger(logs.class);
        //loger.info(id+":"+ss);
    }

    private static final Map<String, Logger> container = new HashMap<>();

    public static Logger getLogger(String name) {
        Logger logger = container.get(name);
        if(logger != null) {
            return logger;
        }
        synchronized (logs.class) {
            logger = container.get(name);
            if(logger != null) {
                return logger;
            }
            logger = build(name);
            container.put(name,logger);
        }
        return logger;
    }
    private static Logger build(String name) {
        ServletContext sc =((WebApplicationContext) springTool.getApplicationContext()).getServletContext();
        String path="";
        if("".equals(domainLogs)||domainLogs==null){
            path= sc.getRealPath("/");
        }else{
            path=domainLogs;
        }
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        String path=request.getServletContext().getRealPath("/");
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        ch.qos.logback.classic.Logger logger = context.getLogger(name);
        logger.setAdditive(false);
        logger.setLevel(Level.INFO);
        RollingFileAppender appender = new RollingFileAppender();
        appender.setContext(context);
        appender.setName(name);
        appender.setFile(OptionHelper.substVars(path+"logs/" + name + "/error.log",context));
        appender.setAppend(true);
        appender.setPrudent(false);
        SizeAndTimeBasedRollingPolicy policy = new SizeAndTimeBasedRollingPolicy();
        String fp = OptionHelper.substVars(path+"logs/" + name + "/%d{yyyy-MM-dd}.%i.log",context);

        policy.setMaxFileSize( FileSize.valueOf("128MB"));
        policy.setFileNamePattern(fp);
        policy.setMaxHistory(30);
        policy.setTotalSizeCap(FileSize.valueOf("32GB"));
        policy.setParent(appender);
        policy.setContext(context);
        policy.start();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern("%d{yyyy-MM-dd HH:mm:ss} %5p %c{1}:%L - %m%n");
        encoder.start();

        appender.setRollingPolicy(policy);
        appender.setEncoder(encoder);
        appender.start();
        logger.addAppender(appender);
        return logger;
    }
}
