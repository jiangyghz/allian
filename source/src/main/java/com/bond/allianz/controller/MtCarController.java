package com.bond.allianz.controller;

import com.bond.allianz.entity.PageInfo;
import com.bond.allianz.service.CarService;
import com.bond.allianz.service.MtCarService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mtcar")
public class MtCarController   extends BaseController {
    @Autowired
    private MtCarService mtCarService;
    @RequestMapping(value = "/carlist", method = RequestMethod.POST)
    public  String carlistpost(String data){
        if (data==null)data=getPostString();
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        String result=mtCarService.checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;
        String action="",brand="",id="",valid="1";
        if (jsonObject.keySet().contains("action"))action=jsonObject.get("action").getAsString();else return  "参数错误！";
        if (jsonObject.keySet().contains("brand"))brand=jsonObject.get("brand").getAsString();
        if (jsonObject.keySet().contains("id"))id=jsonObject.get("id").getAsString();
        int pageIndex=1,pageSize=10;
        if (jsonObject.keySet().contains("pageIndex"))pageIndex=jsonObject.get("pageIndex").getAsInt();
        if (jsonObject.keySet().contains("pageSize"))pageSize=jsonObject.get("pageSize").getAsInt();
        if (jsonObject.keySet().contains("valid"))valid=jsonObject.get("valid").getAsString();
        result="";
        if(!action.isEmpty()){
            switch (action){
                case "GetData":

                    PageInfo<Map<String, Object>> page = mtCarService.selectCarpage(pageIndex, pageSize,brand,valid);
                    result=JsonSerializer(page);
                    break;
                case "del":
                    try {
                        mtCarService.deleteCarByKey(id);
                        result = JsonSerializer(new Object[]{true, ""});
                    }
                    catch (Exception ex) {
                        result = JsonSerializer(new Object[]{false, ex.getMessage()});
                    }
                    break;
            }
        }
        return result;
    }

    @RequestMapping(value = "/caredit", method = RequestMethod.POST)
    public String careditsave(String data){
        if (data==null)data=getPostString();
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        String result=mtCarService.checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;
        String action="",brand="",id="",cars="";
        if (jsonObject.keySet().contains("action"))action=jsonObject.get("action").getAsString();else return  "参数错误！";
        if (jsonObject.keySet().contains("brand"))brand=jsonObject.get("brand").getAsString();
        if (jsonObject.keySet().contains("id"))id=jsonObject.get("id").getAsString();
        if (jsonObject.keySet().contains("cars"))cars=jsonObject.get("cars").getAsString();
        int valid=1;

        if (jsonObject.keySet().contains("valid"))valid=jsonObject.get("valid").getAsInt();

         result="";
        if(!action.isEmpty()){
            switch (action){
                case "save":
                    try {

                        if("".equals(id)){
                            mtCarService.insertCar(brand,cars, valid);
                        }else{
                            mtCarService.updateCarByKey(id,brand,cars, valid);
                        }
                        result = JsonSerializer(new Object[]{true, ""});

                    }
                    catch (Exception ex){
                        result = JsonSerializer(new Object[]{false, ex.getMessage()});
                    }
                    break;
            }
        }
        return result;
    }


    /**
     * 查询车系接口
     * @return
     */

    @RequestMapping(value = "/vehiclelist", method = RequestMethod.POST)
    public  String vehiclelistpost(String data){
        if (data==null)data=getPostString();
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        String result=mtCarService.checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;
        String action="",brand="",cars="",id="",valid="";
        if (jsonObject.keySet().contains("action"))action=jsonObject.get("action").getAsString();else return  "参数错误！";
        if (jsonObject.keySet().contains("brand"))brand=jsonObject.get("brand").getAsString();
        if (jsonObject.keySet().contains("cars"))cars=jsonObject.get("cars").getAsString();
        if (jsonObject.keySet().contains("id"))id=jsonObject.get("id").getAsString();
        int pageIndex=1,pageSize=10;
        if (jsonObject.keySet().contains("pageIndex"))pageIndex=jsonObject.get("pageIndex").getAsInt();
        if (jsonObject.keySet().contains("pageSize"))pageSize=jsonObject.get("pageSize").getAsInt();
        if (jsonObject.keySet().contains("valid"))valid=jsonObject.get("valid").getAsString();
        result="";

        if(!action.isEmpty()){
            switch (action){
                case "GetData":

                    PageInfo<Map<String, Object>> page = mtCarService.selectVehiclepage(pageIndex, pageSize,brand,cars,valid);
                    result=JsonSerializer(page);
                    break;
                case "del":
                    try {
                        mtCarService.deleteVehicleByKey(id);
                        result = JsonSerializer(new Object[]{true, ""});
                    }
                    catch (Exception ex) {
                        result = JsonSerializer(new Object[]{false, ex.getMessage()});
                    }
                    break;
            }
        }
        return result;
    }

    @RequestMapping(value = "/vehicleedit", method = RequestMethod.POST)
    public String vehicleeditsave(String data){
        if (data==null)data=getPostString();
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        String result=mtCarService.checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;
        String action="",brand="",id="",cars="",vehicletype="";
        if (jsonObject.keySet().contains("action"))action=jsonObject.get("action").getAsString();else return  "参数错误！";
        if (jsonObject.keySet().contains("brand"))brand=jsonObject.get("brand").getAsString();
        if (jsonObject.keySet().contains("id"))id=jsonObject.get("id").getAsString();
        if (jsonObject.keySet().contains("cars"))cars=jsonObject.get("cars").getAsString();
        if (jsonObject.keySet().contains("vehicletype"))vehicletype=jsonObject.get("vehicletype").getAsString();
        int valid=1;

        if (jsonObject.keySet().contains("valid"))valid=jsonObject.get("valid").getAsInt();

        result="";
        if(!action.isEmpty()){
            switch (action){
                case "save":
                    try {
                        if("".equals(id)){
                            mtCarService.insertVehicle(brand,cars, vehicletype,valid);
                        }else{
                            mtCarService.updateVehicleByKey(id,brand,cars,vehicletype, valid);
                        }
                        result = JsonSerializer(new Object[]{true, ""});

                    }
                    catch (Exception ex){
                        result = JsonSerializer(new Object[]{false, ex.getMessage()});
                    }
                    break;
            }
        }
        return result;
    }
}
