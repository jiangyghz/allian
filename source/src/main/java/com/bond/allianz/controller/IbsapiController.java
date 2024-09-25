package com.bond.allianz.controller;

import com.bond.allianz.Dao.logs;
import com.bond.allianz.service.*;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;

@RestController
@RequestMapping("/ibsapi")
public class IbsapiController extends BaseController{

    @Autowired
    private DicService dicService;
    @Autowired
    private ContractService contractService;
    @Autowired
    private AgencyService agencyService;
    @Autowired
    private UserService userService;
    @Autowired
    private BmwService bmwService;
    @Autowired
    private BillService billService;
    @Autowired
    private ProductService productService;
    @RequestMapping(value = "/selectContractAll",method = RequestMethod.POST)
    public String selectContractAllPost ()  {

        String data=getPostString();
        if (data.equals("")) return getErrorstring(2);

        logs.WriteLog(data,"selectContractAll");
        String resultss=contractService.selectContractAll(data);
        logs.WriteLog(resultss,"selectContractAll");
        return resultss;
    }
    @RequestMapping(value = "/getuserid",method = RequestMethod.POST)
    public String getuserid ()  {

        String data=getPostString();
        if (data.equals("")) return getErrorstring(2);
        logs.WriteLog(data,"getuserid");
        String resultss=userService.getuserid(data);
        logs.WriteLog(resultss,"getuserid");
        return resultss;
    }
    @RequestMapping(value = "/addorUpdateContract",method = RequestMethod.POST)
    public String addorUpdateContract ()  {

        String data=getPostString();
        if (data.equals("")) return getErrorstring(2);
        logs.WriteLog(data,"addorUpdateContract");
        String resultss=contractService.addorUpdateContract(data);
        logs.WriteLog(resultss,"addorUpdateContract");
        return resultss;
    }
    @RequestMapping(value = "/submitcontract",method = RequestMethod.POST)
    public String submitcontract ()  {

        String data=getPostString();
        if (data.equals("")) return getErrorstring(2);
        logs.WriteLog(data,"submitcontract");
        String resultss=contractService.submitcontract(data);
        logs.WriteLog(resultss,"submitcontract");
        return resultss;
    }
    @RequestMapping(value = "/getsystemremind",method = RequestMethod.POST)
    public String getsystemremind() {
        String data=getPostString();
        if (data.equals("")) return getErrorstring(2);
        logs.WriteLog(data,"getsystemremind");
        String resultss="";
        resultss=bmwService.getsystemremind( data);
        logs.WriteLog(resultss,"getsystemremind");
        return resultss;

    }
    @RequestMapping(value = "/gettierdetail",method = RequestMethod.POST)
    public String gettierdetail ()  {
        String data=getPostString();
        if (data.equals("")) return getErrorstring(2);
        logs.WriteLog(data,"gettierdetail");

        String resultss=contractService.gettierdetail(data);
        logs.WriteLog(resultss,"gettierdetail");
        return resultss;
    }
    @RequestMapping(value = "/getSalebrand",method = RequestMethod.POST)
    public String getSalebrand ()  {

        String data=getPostString();
        if (data.equals("")) return getErrorstring(2);
        logs.WriteLog(data,"getSalebrand");
        String resultss=bmwService.getSalebrand(data);
        logs.WriteLog(resultss,"getSalebrand");
        return resultss;
    }
    @RequestMapping(value = "/getBalance",method = RequestMethod.POST)
    public String getBalance ()  {

        String data=getPostString();
        if (data.equals("")) return getErrorstring(2);
        logs.WriteLog(data,"getBalance");
        String resultss=bmwService.getBalance(data);
        logs.WriteLog(resultss,"getBalance");
        return resultss;
    }
    @RequestMapping(value = "/contractPrintBmw",method = RequestMethod.POST)
    public String contractPrintBmw ()  {

        String data=getPostString();
        if (data.equals("")) return getErrorstring(2);
        logs.WriteLog(data,"contractPrintBmw");
        String resultss=contractService.contractPrintBmw(data);
        logs.WriteLog(resultss,"contractPrintBmw");
        return resultss;
    }
    @RequestMapping(value = "/getvaildmarketactivity",method = RequestMethod.POST)
    public String getvaildmarketactivity ()  {

        String data=getPostString();
        if (data.equals("")) return getErrorstring(2);
        logs.WriteLog(data,"getvaildmarketactivity");
        String resultss=contractService.getvaildmarketactivity(data);
        logs.WriteLog(resultss,"getvaildmarketactivity");
        return resultss;
    }
    @RequestMapping(value = "/cantractBill",method = RequestMethod.POST)
    public String cantractBill ()  {
        String data=getPostString();
        if (data.equals("")) return getErrorstring(2);
        logs.WriteLog(data,"cantractBill");
        String resultss=billService.cantractBill(data);
        logs.WriteLog(resultss,"cantractBill");
        return resultss;
    }
    @RequestMapping(value = "/getcars",method = RequestMethod.POST)
    public String getcars ()  {

        String data=getPostString();
        if (data.equals("")) return getErrorstring(2);
        logs.WriteLog(data,"getcars");
        String resultss=contractService.getcars(data);
        logs.WriteLog(resultss,"getcars");
        return resultss;
    }
    @RequestMapping(value = "/getvehicletype",method = RequestMethod.POST)
    public String getvehicletype ()  {

        String data=getPostString();
        if (data.equals("")) return getErrorstring(2);
        logs.WriteLog(data,"getvehicletype");
        String resultss=contractService.getvehicletype(data);
        logs.WriteLog(resultss,"getvehicletype");
        return resultss;
    }
    @RequestMapping(value = "/getProduct",method = RequestMethod.POST)
    public String getProduct(){
        String data=getPostString();
        if (data.equals("")) return getErrorstring(2);
        logs.WriteLog(data,"getProduct");
        String resultss=productService.getProduct(data);
        logs.WriteLog(resultss,"getProduct");
        return resultss;

    }
    @RequestMapping(value = "/getGroupProduct",method = RequestMethod.POST)
    public String getGroupProduct(){
        String data=getPostString();
        if (data.equals("")) return getErrorstring(2);
        logs.WriteLog(data,"getGroupProduct");
        String resultss=productService.getGroupProduct(data);
        logs.WriteLog(resultss,"getGroupProduct");
        return resultss;

    }

    @RequestMapping(value = "/uploadInvoice",method = RequestMethod.POST)
    public String uploadInvoice (String data,@RequestParam(value="image") MultipartFile file)  {



        logs.WriteLog(data,"uploadInvoice");
        String ss=filecheck(file);
        if (!ss.equals(""))return  ss;
        String resultss=contractService.uploadInvoice(data,file);
        logs.WriteLog(resultss,"uploadInvoice");
        return resultss;
    }
    @RequestMapping(value = "/uploadinsurance",method = RequestMethod.POST)
    public String uploadinsurance (String data,@RequestParam(value="image") MultipartFile file)  {



        logs.WriteLog(data,"uploadinsurance");
        String ss=filecheck(file);
        if (!ss.equals(""))return  ss;
        String resultss=contractService.uploadinsurance(data,file);
        logs.WriteLog(resultss,"uploadinsurance");
        return resultss;
    }
    @RequestMapping(value = "/uploadJpg",method = RequestMethod.POST)
    public String uploadJpg (String data,@RequestParam(value="image") MultipartFile file)  {
        logs.WriteLog(data,"uploadJpg");
        String ss=filecheck(file);
        if (!ss.equals(""))return  ss;
        String resultss=contractService.uploadJpg(data,file);
        logs.WriteLog(resultss,"uploadJpg");
        return resultss;
    }
    @RequestMapping(value = "/uploadtierpic",method = RequestMethod.POST)
    public String uploadtierpic (String data,@RequestParam(value="image") MultipartFile file)  {

        logs.WriteLog(data,"uploadtierpic");
        try
        {
            String ss=filecheck(file);
            if (!ss.equals(""))return  ss;
        }catch (Exception e)
        {

        }

        //  data= URLDecoder.decode(data);
        String resultss=contractService.uploadtierpic(data,file);
        logs.WriteLog(resultss,"uploadtierpic");
        return resultss;
    }
    @RequestMapping(value = "/getTireParameter",method = RequestMethod.POST)
    public String getTireParameter ()  {

        String data=getPostString();
        if (data.equals("")) return getErrorstring(2);
        logs.WriteLog(data,"getTireParameter");
         String resultss=productService.getTireParameter(data);
        logs.WriteLog(resultss,"getTireParameter");
        return resultss;
    }
    @RequestMapping(value = "/addorupdatetier",method = RequestMethod.POST)
    public String addorupdatetier ()  {
        String data=getPostString();
        if (data.equals("")) return getErrorstring(2);
        logs.WriteLog(data,"addorupdatetier");
        String resultss="";
        try
        {
            resultss=contractService.addorupdatetier(data);
            logs.WriteLog(resultss,"addorupdatetier");
        }catch (Exception e)
        {
            logs.WriteLog(e.toString(),"addorupdatetier");
        }


        return resultss;
    }

    @RequestMapping(value = "/balancepay",method = RequestMethod.POST)
    public String balancepay() {
        String data=getPostString();
        if (data.equals("")) return getErrorstring(2);
        logs.WriteLog(data,"balancepay");
        String resultss=bmwService.balancepay( data);
        logs.WriteLog(resultss,"balancepay");
        return resultss;

    }
    @RequestMapping(value = "/getSellByDealerNo",method = RequestMethod.POST)
    public String getSellByDealerNo() {
        String data=getPostString();
        if (data.equals("")) return getErrorstring(2);
        logs.WriteLog(data,"getSellByDealerNo");
        String resultss=agencyService.getSellByDealerNo( data);
        logs.WriteLog(resultss,"getSellByDealerNo");
        return resultss;

    }
    @RequestMapping(value = "/vincheck",method = RequestMethod.POST)
    public String vincheck() {
        String data=getPostString();
        if (data.equals("")) return getErrorstring(2);
        logs.WriteLog(data,"vincheck");

        String resultss="";
        resultss=contractService.vincheck( data);
        logs.WriteLog(resultss,"vincheck");
        return resultss;

    }
    @RequestMapping(value = "/updatetierpic",method = RequestMethod.POST)
    public String updatetierpic ()  {
        String data=getPostString();
        if (data.equals("")) return getErrorstring(2);
        logs.WriteLog(data,"updatetierpic");

        String resultss=contractService.updatetierpic(data);
        logs.WriteLog(resultss,"updatetierpic");
        return resultss;
    }
    @RequestMapping(value = "/getinsurancetype",method = RequestMethod.POST)
    public String getinsurancetype(){
        String data=getPostString();
        if (data.equals("")) return getErrorstring(2);
        logs.WriteLog(data,"getinsurancetype");
        String resultss=dicService.getinsurancetype(data);
        logs.WriteLog(resultss,"getinsurancetype");
        return resultss;

    }
    @RequestMapping(value = "/deletecontract",method = RequestMethod.POST)
    public String deletecontract ()  {
        String data=getPostString();
        if (data.equals("")) return getErrorstring(2);
        logs.WriteLog(data,"deletecontract");

        String resultss=contractService.deletecontract(data);
        logs.WriteLog(resultss,"deletecontract");
        return resultss;
    }
    @RequestMapping(value = "/getTireList",method = RequestMethod.POST)
    public String getTireList ()  {
        String data=getPostString();
        if (data.equals("")) return getErrorstring(2);
        logs.WriteLog(data,"getTireList");

        String resultss=productService.getTireList(data);
        logs.WriteLog(resultss,"getTireList");
        return resultss;
    }
  /* @RequestMapping(value = "/getSign",method = RequestMethod.POST)
    public String getSign ()  {
        String data=getPostString();
        logs.WriteLog(data,"getSign");

        String resultss=billService.getSign(data);
        logs.WriteLog(resultss,"getSign");
        return resultss;
    }*/
}
