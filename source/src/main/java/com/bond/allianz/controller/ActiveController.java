package com.bond.allianz.controller;

import com.bond.allianz.Dao.logs;
import com.bond.allianz.service.ActiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/active")
public class ActiveController extends BaseController{
    @Autowired
    ActiveService activeService;
    @RequestMapping(value = "/list_active_subject",method = RequestMethod.POST)
    public String addContractRemark (String data)  {
         if (data==null)data=getPostString();
        logs.WriteLog(data,"list_active_subject");
        String resultss=activeService.list_active_subject(data);
        logs.WriteLog(resultss,"list_active_subject");
        return resultss;

    }
    @RequestMapping(value = "/addorUpdateActive_subject",method = RequestMethod.POST)
    public String addorUpdateActive_subject (String data)  {
        if (data==null)data=getPostString();
        logs.WriteLog(data,"addorUpdateActive_subject");
        String resultss=activeService.addorUpdateActive_subject(data);
        logs.WriteLog(resultss,"addorUpdateActive_subject");
        return resultss;

    }
    @RequestMapping(value = "/add_active",method = RequestMethod.POST)
    public String add_active (String data)  {
        if (data==null)data=getPostString();
        logs.WriteLog(data,"add_active");
        String resultss="";
        try {
            resultss=activeService.add_active(data);
        }catch (Exception e)
        {
            resultss=e.toString();
        }
        logs.WriteLog(resultss,"add_active");
        return resultss;

    }
    @RequestMapping(value = "/get_active",method = RequestMethod.POST)
    public String get_active (String data)  {
        if (data==null)data=getPostString();
        logs.WriteLog(data,"get_active");
        String resultss=activeService.get_active(data);
        logs.WriteLog(resultss,"get_active");
        return resultss;

    }
    @RequestMapping(value = "/add_active_dealer",method = RequestMethod.POST)
    public String add_active_dealer (String data)  {
        if (data==null)data=getPostString();
        logs.WriteLog(data,"add_active_dealer");
        String resultss=activeService.add_active_dealer(data);
        logs.WriteLog(resultss,"add_active_dealer");
        return resultss;

    }
    @RequestMapping(value = "/get_active_dealer",method = RequestMethod.POST)
    public String get_active_dealer (String data)  {
        if (data==null)data=getPostString();
        logs.WriteLog(data,"get_active_dealer");
        String resultss=activeService.get_active_dealer(data);
        logs.WriteLog(resultss,"get_active_dealer");
        return resultss;

    }
    @RequestMapping(value = "/add_active_product",method = RequestMethod.POST)
    public String add_active_product (String data)  {
        if (data==null)data=getPostString();
        logs.WriteLog(data,"add_active_product");
        String resultss="";
        try {
            resultss=activeService.add_active_product(data);
        }catch (Exception e)
        {
            resultss=e.toString();
        }

        logs.WriteLog(resultss,"add_active_product");
        return resultss;

    }
    @RequestMapping(value = "/get_active_product",method = RequestMethod.POST)
    public String get_active_product (String data)  {
        if (data==null)data=getPostString();
        logs.WriteLog(data,"get_active_product");
        String resultss=activeService.get_active_product(data);
        logs.WriteLog(resultss,"get_active_product");
        return resultss;

    }
    @RequestMapping(value = "/add_active_cars",method = RequestMethod.POST)
    public String add_active_cars (String data)  {
        if (data==null)data=getPostString();
        logs.WriteLog(data,"add_active_cars");
        String resultss=activeService.add_active_cars(data);
        logs.WriteLog(resultss,"add_active_cars");
        return resultss;

    }
    @RequestMapping(value = "/get_active_cars",method = RequestMethod.POST)
    public String get_active_cars (String data)  {
        if (data==null)data=getPostString();
        logs.WriteLog(data,"get_active_cars");
        String resultss=activeService.get_active_cars(data);
        logs.WriteLog(resultss,"get_active_cars");
        return resultss;

    }
    @RequestMapping(value = "/get_all_cars",method = RequestMethod.POST)
    public String get_all_cars (String data)  {
        if (data==null)data=getPostString();
        logs.WriteLog(data,"get_all_cars");
        String resultss=activeService.get_all_cars(data);
        logs.WriteLog(resultss,"get_all_cars");
        return resultss;

    }
}
