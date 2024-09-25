package com.bond.allianz.controller;


import com.bond.allianz.Dao.logs;
import com.bond.allianz.entity.PageInfo;
import com.bond.allianz.service.*;
import com.bond.allianz.utils.RedisUtil;
import com.bond.allianz.utils.SendMailUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController extends BaseController{


    @Autowired
    private ContractService contractService;
    @Autowired
    private BillService billService;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private AgencyService agencyService;


    @RequestMapping(value = "/agencylist", method = RequestMethod.POST)
    public  String agencylist() {
        String action = Params("action");
        String result = "";
        if (!action.isEmpty()) {
            switch (action) {
                case "GetData":
                    int pageIndex = ParamsInt("curPageIndex", 1);
                    int pageSize = ParamsInt("pageSize", 10);
                    String keyid = Params("keyid");
                    String keyname = Params("keyname");
                    String keybrand = Params("keybrand");
                    int keyvalid = ParamsInt("keyvalid");
                    String keyzone = Params("keyzone");
                    String keyprovince = Params("keyprovince");
                    String keycity = Params("keycity");
                    PageInfo<Map<String, Object>> page = agencyService.selectpage(pageIndex, pageSize, keyid, keyname, keybrand, keyvalid,keyzone,keyprovince,keycity);
                    result = JsonSerializer(page);

                    break;
            }
        }
        return result;
    }
    @RequestMapping(value = "/selectContractAll",method = RequestMethod.GET)
    public String selectContractAll (String data)  {

        data= URLDecoder.decode(data);
        logs.WriteLog(data,"selectContractAll");
        String resultss=contractService.selectContractAll(data);
        logs.WriteLog(resultss,"selectContractAll");
        return resultss;
    }
    @RequestMapping(value = "/selectContractAll",method = RequestMethod.POST)
    public String selectContractAllPost (String data)  {


        logs.WriteLog(data,"selectContractAll");
        String resultss=contractService.selectContractAll(data);
        logs.WriteLog(resultss,"selectContractAll");
        return resultss;
    }
    @RequestMapping(value = "/autoBillmail",method = RequestMethod.POST)
    public String autoBillmail(String data) {

        String resultss="";
        try
        {
            resultss=     billService.autoBill(data);
          //billService.autoBillmail();
            logs.WriteLog(resultss,"autoBillmail");
        }
        catch (Exception e1)
        {

        }
        return resultss;

    }
    @RequestMapping(value = "/testmail")
    public String testmail(String e) {

        String resultss = "";
        try {
            SendMailUtil.sendMail(e, "测试邮件", "这是一个测试邮件", null);
            resultss = "成功,"+e;
        } catch (Exception e1) {
            e1.printStackTrace();
            resultss = "失败," + e1.getMessage();
        }
        return resultss;

    }
    //添加
    @RequestMapping(value = "/saveRedis",method = RequestMethod.GET)
    public void saveRedis(){

        redisUtil.set("a","测试");

    }

    //获取
    @RequestMapping(value = "/getRedis",method = RequestMethod.GET)
    public String getRedis(){
        return redisUtil.get("a").toString();
    }

}
