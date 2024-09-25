package com.bond.allianz.global;


import com.bond.allianz.Dao.logs;
import com.bond.allianz.Dao.setting;
import com.bond.allianz.service.BillService;

import com.bond.allianz.service.PayOrderService;
import com.bond.allianz.service.invoiceService;
import com.bond.allianz.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


/**
 * 定时任务类
 */
@Component
@EnableAsync //  开启多线程
public class ScheduledTask {

    /**
     * 是否启用定时程序
     */
    @Value("${domain.task}")
    private String domaintask;
    @Value("${domain.enter}")
    public String domainenter;
   @Autowired
   private BillService billService;
    @Autowired
    private invoiceService invoiceService1;
    @Autowired
    private PayOrderService payOrderService;
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * fixedRate = 5000表示每隔5000ms，Spring scheduling会调用一次该方法，不论该方法的执行时间是多少
     * fixedDelay = 5000表示当方法执行完毕5000ms后，Spring scheduling会再次调用该方法
     */
    //cron = "*/5 * * * * * *" 提供了一种通用的定时任务表达式，这里表示每隔5秒执行一次
    //Cron 表达式是一个字符串，分为 6 或 7 个域，每一个域代表一个含义
    //Seconds Minutes Hours Day Month Week Year
    //Seconds Minutes Hours Day Month Week

    //(*)：可用在所有字段中，表示对应时间域的每一个时刻，例如，*在分钟字段时，表示“每分钟”
    //(?）：该字符只在日期和星期字段中使用，它通常指定为“无意义的值”，相当于占位符；
    //减号(-)：表达一个范围，如在小时字段中使用“10-12”，则表示从 10 到 12 点，即 10,11,12；
    //逗号(,)：表达一个列表值，如在星期字段中使用“MON,WED,FRI”，则表示星期一，星期三和星期
    //五；
    //斜杠(/)：x/y 表达一个等步长序列，x 为起始值，y 为增量步长值。如在分钟字段中使用 0/15，则
    //表示为 0,15,30 和 45 秒，而 5/15 在分钟字段中表示 5,20,35,50，你也可以使用*/y，它等同于 0/y；

    //@Scheduled(cron = "0 0 1 1 1 ?")//每年一月的一号的 1:00:00 执行一次
    //@Scheduled(cron = "0 0 1 1 1,6 ?") //一月和六月的一号的 1:00:00 执行一次
    //@Scheduled(cron = "0 0 1 1 1,4,7,10 ?") //每个季度的第一个月的一号的 1:00:00 执行一次
    //@Scheduled(cron = "0 0 1 1 * ?")//每月一号 1:00:00 执行一次
    //@Scheduled(cron="0 0 1 * * *") //每天凌晨 1 点执行一次

//    @Async
//    @Scheduled(fixedDelay = 5000)  //间隔5秒
//    public void first() throws InterruptedException {
//        System.out.println("第一个定时任务开始 : " + dateFormat.format(new Date()) + "\r\n线程 : " + Thread.currentThread().getName());
//        System.out.println();
//    }

//    @Async
//    @Scheduled(fixedDelay = 5000)  //执行完后5秒再执行
//    public void first2() throws InterruptedException {
//        System.out.println("第二个定时任务开始 : " + dateFormat.format(new Date()) + "\r\n线程 : " + Thread.currentThread().getName());
//        System.out.println();
//    }

    /**
     * 生成账单
     * @throws InterruptedException
     */
    @Async
    @Scheduled(cron = "0 0 1 1 * ?")  //每月一号 1:00:00 执行一次
    public void createdbill() throws InterruptedException {
        Random r = new Random(1);
        int ran1 = r.nextInt(3600);
        try {
            Thread.sleep(ran1*1000);//60分钟内随机休眠、每1秒一个随机数
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
//                 int tt=setting.GetMonthtime()-1;
//                 if (setting.StrToInt(redisUtil.get("createdbill"+domainenter)+"")>tt)return;
//                redisUtil.set("createdbill"+domainenter,String.valueOf(setting.GetCurrenntTime()));
                //判断数据库里最新的账单日期是否大于当月日期
                //if (billService.getlassbilltime()>tt)return;
               // logs.info(setting.GetNowDate("yyyy-MM-dd HH:mm:ss") + " taskmonth:启动自动账单");
                billService.updatelassbilltime();
                billService.autoBill1();

            } catch (Exception ex) {
                logs.error("生成账单错误," + ex.toString(), "task");
                logs.error("生成账单错误",ex);
            }
    }

    /**
     * 提示账单邮件
     * @throws InterruptedException
     */
    @Async
    @Scheduled(cron = "0 0 9 * * *")  //每天凌晨 9 点执行一次
    public void warnbill() throws InterruptedException {
        Random r = new Random(1);
        int ran1 = r.nextInt(1000);
        try {
            Thread.sleep(ran1);//1秒内随机休眠
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
            try {
//                int tt=setting.GetTodaytime();
//                if (setting.StrToInt(redisUtil.get("warnbill"+domainenter)+"")>tt)return;
//                redisUtil.set("warnbill"+domainenter,String.valueOf(setting.GetCurrenntTime()));
                //r-bp1ae040db1848a4.redis.rds.aliyuncs.com
                logs.info(setting.GetNowDate("yyyy-MM-dd HH:mm:ss") + " taskday:启动账单提醒");
                invoiceService1.autoinvoicerequest();
                billService.autoBillmail();
            } catch (Exception ex) {
                ex.printStackTrace();
                logs.error("发送提示邮件错误," + ex.toString(), "task");
                logs.error("账单邮件错误",ex);
            }

    }

    /**
     * 支付订单过期状态修改
     * @throws InterruptedException
     */
    @Async
    @Scheduled(cron = "0 */1 * * * ?")  //每分钟执行一次
    public void overpayorder() throws InterruptedException {

            try {
              //  int tt=setting.GetCurrenntTime();
               // if (tt-setting.StrToInt(redisUtil.get("overpayorder"+domainenter)+"")<60)return;
                //redisUtil.set("overpayorder"+domainenter,String.valueOf(tt));
                logs.info(setting.GetNowDate("yyyy-MM-dd HH:mm:ss") + " taskday:支付订单过期状态修改");
                payOrderService.updateOverState(new Date());
            } catch (Exception ex) {
                logs.error("支付订单过期状态修改错误," + ex.toString(), "task");
                ex.printStackTrace();
            }

    }

}
