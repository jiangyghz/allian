package com.bond.allianz.global;


import com.bond.allianz.sdk.SDKConfig;
import com.pingplusplus.Pingpp;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import org.springframework.stereotype.Component;

/**
 * 启动类 程序启动后执行
 */
@Component
public class MyApplicationRunner implements ApplicationRunner {

    @Value("${pay.pingappid}")
    private String payPingAppId;
    @Value("${pay.pingapikey}")
    private String payPingapiKey;
    @Value("${pay.privatekeypath}")
    private String pingprivateKeyPath;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        //银联加载支付证书
        //SDKConfig.getConfig().loadPropertiesFromSrc();
        //ping++ 设置
        Pingpp.apiKey = payPingapiKey;
        Pingpp.appId = payPingAppId;
        Pingpp.privateKeyPath = pingprivateKeyPath;
    }
}
