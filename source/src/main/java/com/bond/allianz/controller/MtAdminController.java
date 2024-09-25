package com.bond.allianz.controller;

import com.bond.allianz.Dao.logs;
import com.bond.allianz.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLDecoder;

@RestController
@RequestMapping("/mtadmin")
public class MtAdminController extends BaseController{
    @Autowired
    private MtContractService mtContractService;
    @Autowired
    private ProductService productService;
    @Autowired
    private BillService billService;
    @Autowired
    private MtClaimService mtclaimService;
    @RequestMapping(value = "/selectContractAll",method = RequestMethod.POST)
    public String selectContractAll (String data)  {
        if (data==null)data=getPostString();
        logs.WriteLog(data,"mtselectContractAll");
        String resultss=mtContractService.selectContractAll(data);
        logs.WriteLog(resultss,"mtselectContractAll");
        return resultss;
    }
    @RequestMapping(value = "/addorUpdatContract",method = RequestMethod.POST)
    public String addorUpdatContract (String data)  {
        if (data==null)data=getPostString();
        logs.WriteLog(data,"mtaddorUpdateContract");
        String resultss=mtContractService.addorUpdateContract(data);
        logs.WriteLog(resultss,"mtaddorUpdateContract");
        return resultss;

    }
@RequestMapping(value = "/vincheck",method = RequestMethod.POST)
    public String vincheck (String data)  {
    if (data==null)data=getPostString();
        logs.WriteLog(data,"mtvincheck");
        String resultss=mtContractService.vincheck(data);
        logs.WriteLog(resultss,"mtvincheck");
        return resultss;

    }
    @RequestMapping(value = "/uploadinsurance",method = RequestMethod.POST)
    public String uploadinsurance (String data,@RequestParam(value="image") MultipartFile file)  {
        if (data==null)data=getPostString();
        //  else data= URLDecoder.decode(data);

        logs.WriteLog(data,"mtuploadinsurance");
        String ss=filecheck(file);
        if (!ss.equals(""))return  ss;

        String resultss=mtContractService.uploadinsurance(data,file);
        logs.WriteLog(resultss,"mtuploadinsurance");
        return resultss;
    }
    @RequestMapping(value = "/uploadInvoice",method = RequestMethod.POST)
    public String uploadInvoice (String data,@RequestParam(value="image") MultipartFile file)  {
        if (data==null)data=getPostString();
        // else  data= URLDecoder.decode(data);

        logs.WriteLog(data,"mtuploadInvoice");
        String ss=filecheck(file);
        if (!ss.equals(""))return  ss;

        String resultss=mtContractService.uploadInvoice(data,file);
        logs.WriteLog(resultss,"mtuploadInvoice");
        return resultss;
    }
    @RequestMapping(value = "/submitcontract",method = RequestMethod.POST)
    public String submitcontract (String data)  {
        if (data==null)data=getPostString();
        logs.WriteLog(data,"mtsubmitcontract");
        String resultss=mtContractService.submitcontract(data);
        logs.WriteLog(resultss,"mtsubmitcontract");
        return resultss;
    }
    @RequestMapping(value = "/getMt_Product",method = RequestMethod.POST)
    public String getProduct(String data){
        if (data==null)data=getPostString();
        logs.WriteLog(data,"getMt_Product");
        String resultss=productService.getMt_Product(data);
        logs.WriteLog(resultss,"getMt_Product");
        return resultss;

    }
    @RequestMapping(value = "/deletecontract",method = RequestMethod.POST)
    public String deletecontract(String data){
        if (data==null)data=getPostString();
        logs.WriteLog(data,"deletecontract");
        String resultss=mtContractService.deletecontract(data);
        logs.WriteLog(resultss,"deletecontract");
        return resultss;

    }
    @RequestMapping(value = "/cancelcontract",method = RequestMethod.POST)
    public String cancelcontract (String data)  {
        if (data==null)data=getPostString();
        logs.WriteLog(data,"cancelcontract");

        String resultss=mtContractService.cancelcontract(data);
        logs.WriteLog(resultss,"cancelcontract");
        return resultss;
    }
    @RequestMapping(value = "/contract_down",method = RequestMethod.POST)
    public String contract_down(String data){
        if (data==null)data=getPostString();
        logs.WriteLog(data,"mtcontract_down");
        String resultss=mtContractService.contract_down(data);
        logs.WriteLog(resultss,"mtcontract_down");
        return resultss;

    }
    @RequestMapping(value = "/addorUpdateClaim",method = RequestMethod.POST)
    public String addorUpdateClaim (String data)  {


        if (data==null)data=getPostString();
        logs.WriteLog(data,"mtaddorUpdateClaim");
        String resultss=mtclaimService.addorUpdateClaim(data);
        logs.WriteLog(resultss,"mtaddorUpdateClaim");
        return resultss;
    }
    @RequestMapping(value = "/getClaim",method = RequestMethod.POST)
    public String getClaim (String data)  {

        if (data==null)data=getPostString();
        logs.WriteLog(data,"mtgetClaim");
        String resultss=mtclaimService.getClaim(data);
        logs.WriteLog(resultss,"mtgetClaim");
        return resultss;
    }
    @RequestMapping(value = "/checkClaim",method = RequestMethod.POST)
    public String checkClaim (String data)  {
        if (data==null)data=getPostString();
        logs.WriteLog(data,"mtcheckClaim");
        String resultss=mtclaimService.checkClaim(data);
        logs.WriteLog(resultss,"mtcheckClaim");
        return resultss;
    }



    @RequestMapping(value = "/addClaimpic",method = RequestMethod.POST)
    public String addClaimpic (String data,@RequestParam(value="image") MultipartFile file)  {
        String filetype="";
        try
        {
            filetype = Params("filetype");
        }
        catch (Exception e)
        {

        }
        if (data==null)data=getPostString();
        logs.WriteLog(data,"addClaimpic");
//        data= URLDecoder.decode(data);
        String resultss=mtclaimService.addClaimpic(data,filetype,file);
        logs.WriteLog(resultss,"mtaddClaimpic");
        return resultss;
    }
    @RequestMapping(value = "/getClaimpic",method = RequestMethod.POST)
    public String getClaimpic (String data)  {

        if (data==null)data=getPostString();
        logs.WriteLog(data,"mtgetClaimpic");
        String resultss=mtclaimService.getClaimpic(data);
        logs.WriteLog(resultss,"mtgetClaimpic");
        return resultss;
    }
    @RequestMapping(value = "/getClaimRemark",method = RequestMethod.POST)
    public String getClaimRemark (String data)  {

        if (data==null)data=getPostString();
        logs.WriteLog(data,"mtgetClaimRemark");
        String resultss=mtclaimService.getClaimRemark(data);
        logs.WriteLog(resultss,"mtgetClaimRemark");
        return resultss;
    }
    @RequestMapping(value = "/deleteClaimpic",method = RequestMethod.POST)
    public String deleteClaimpic (String data)  {

        if (data==null)data=getPostString();
        logs.WriteLog(data,"mtdeleteClaimpic");
        String resultss=mtclaimService.deleteClaimpic(data);
        logs.WriteLog(resultss,"mtdeleteClaimpic");
        return resultss;
    }
    @RequestMapping(value = "/getClaimCount",method = RequestMethod.POST)
    public String getClaimCount(String data) {
        if (data==null)data=getPostString();
        logs.WriteLog(data,"mtgetClaimCount");

        String resultss="";
        resultss=mtclaimService.getClaimCount( data);
        logs.WriteLog(resultss,"mtgetClaimCount");
        return resultss;

    }
    @RequestMapping(value = "/claim_down",method = RequestMethod.POST)
    public String claim_down(String data){
        if (data==null)data=getPostString();
        String resultss=mtclaimService.claim_down(data);

        return resultss;

    }
    @RequestMapping(value = "/contractPrint",method = RequestMethod.POST)
    public String contractPrint(String data){
        if (data==null)data=getPostString();
        String resultss=mtContractService.contractPrint(data);

        return resultss;

    }
    @RequestMapping(value = "/dealerBill",method = RequestMethod.POST)
    public String autoBill(String data) {

        String resultss="";
        if (data==null)data=getPostString();
        try
        {
            resultss=billService.autoBill_motor(data);
        }
        catch (Exception e)
        {
            resultss=e.toString();
        }
        logs.WriteLog(resultss,"dealerBill_motor");
        return resultss;

    }
}
