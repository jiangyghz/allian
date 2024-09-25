package com.bond.allianz.controller;


import com.bond.allianz.Dao.logs;
import com.bond.allianz.Dao.setting;
import com.bond.allianz.entity.Dictionary;
import com.bond.allianz.entity.Menu;
import com.bond.allianz.service.*;
import com.bond.allianz.utils.ZipCompressor;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;


import java.io.*;
import java.net.HttpURLConnection;

import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController extends BaseController{

    @Autowired
    private MenuService menuService;

    @Autowired
    private ActService actService;
    @Autowired
    private DicService dicService;

    @Autowired
    private ContractService contractService;
    @Autowired
    private BillService billService;
    @Autowired
    private invoiceService invoiceservice;
    @Autowired
    private ClaimService claimService;
    @Autowired
    private ProductService productService;
    @Autowired
    private BmwService bmwService;
    @Autowired
    private RegionService regionService;


    @Value("${upload.wximage}")
    private String wximage;

    /**
     * 菜单页面
     * @param type
     * @return
     */
    @RequestMapping(value = "/menu",method = RequestMethod.GET)
    public ModelAndView menu (String type)  {
        ModelAndView mv = new ModelAndView();
        type=type==null?"":type;
        List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
        if (type.equals("1")) {//经销商
            List<Menu> list = menuService.selectMenu(-1);
            for (Menu m : list) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", m.getMenuno());
                map.put("pid", m.getPmenuno());
                map.put("name", m.getMenutext());
                map.put("urls", m.getUrl());
                map.put("mainword", m.getMainword());
                map.put("valid", m.getValid());
                map.put("sort", m.getSort());
                maps.add(map);
            }
        } else {
            List<Menu> list = menuService.selectMenu(-1);
            for (Menu m : list) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("id", m.getMenuno());
                map.put("pid", m.getPmenuno());
                map.put("name", m.getMenutext());
                map.put("urls", m.getUrl());
                map.put("mainword", m.getMainword());
                map.put("valid", m.getValid());
                map.put("sort", m.getSort());
                maps.add(map);
            }
        }
        mv.addObject("list", maps);
        return mv;
    }

    /**
     * 菜单 保存
     * @param type
     * @param txtAction
     * @return
     */

    @RequestMapping(value = "/menu",method = RequestMethod.POST)
    public String menuaction (String type, String txtAction) {
        type=type==null?"":type;
        String result="";
        Menu menu=null;
        List<Menu> list=null;
        int pid = Integer.parseInt(Params("txtpid"));
        int menuno = Integer.parseInt(Params("txtmenuno"));
        String name =Params("txtname") ;
        byte valid =Byte.parseByte(Params("txtvalid")) ;
        String urls =Params("txturl") ;
        String mainword =Params("txtmainword") ;
        //if (type.equals("1")) {
            list=menuService.selectMenu(-1);
            menu = menuService.getMenuById(menuno);
            if(menu==null){
                menu=new Menu();
                menu.setMenuno(menuno);
            }
//        }else{
//            list1=menuService.selectMenu1(-1);
//            menu1 = menuService.getMenu1ById(menuno);
//            if(menu1==null){
//                menu1=new Menu1();
//                menu1.setMenuno(menuno);
//            }
//        }
        try {
            switch (txtAction) {
                case "add":
                case "modify":
                    //if (type.equals("1")) {
                        menu.setMenuname(name);
                        menu.setMenutext(name);
                        menu.setPmenuno(pid);
                        menu.setUrl(urls);
                        menu.setMainword(mainword);
                        menu.setValid(valid);
                        if(txtAction.equals("add")){
                            OptionalInt s=list.stream().filter(m->m.getPmenuno()==pid).mapToInt(Menu::getSort).max();
                            if(s.equals(OptionalInt.empty())){
                                menu.setSort(0 + 1);
                            }else {
                                menu.setSort(s.getAsInt() + 1);
                            }
                            menuService.insertMenu(menu);
                        }else{
                            menuService.updateMenuByKey(menu);
                        }
                        result=menu.getMenuno().toString();
//                    }else{
//                        menu1.setMenuname(name);
//                        menu1.setMenutext(name);
//                        menu1.setPmenuno(pid);
//                        menu1.setUrl(urls);
//                        menu1.setMainword(mainword);
//                        menu1.setValid(valid);
//                        if(txtAction.equals("add")){
//                            OptionalInt s=list1.stream().filter(m->m.getPmenuno()==pid).mapToInt(Menu1::getSort).max();
//                            if(s.equals(OptionalInt.empty())){
//                                menu1.setSort(0 + 1);
//                            }else {
//                                menu1.setSort(s.getAsInt() + 1);
//                            }
//                            menuService.insertMenu1(menu1);
//                        }else{
//                            menuService.updateMenu1ByKey(menu1);
//                        }
//                        result=menu1.getMenuno().toString();
//                    }
                    break;
                case "delete":
                    //if (type.equals("1")) {
                        menuService.deleteMenuByKey(menu.getMenuno());
//                    }else{
//                        menuService.deleteMenu1ByKey(menu1.getMenuno());
//                    }
                    result=menuno+"";
                    break;
                case "up": //上移
                    //if (type.equals("1")) {
                        int upsort = menu.getSort();
                        List<Menu> q=list.stream().filter(m->m.getPmenuno()==pid&&m.getSort()<upsort).collect(Collectors.toList());
                        if(q.size()>0){
                            Menu other=q.get(q.size()-1);
                            menu.setSort(other.getSort());
                            other.setSort(upsort);
                            menuService.updateMenuByKey(menu);
                            menuService.updateMenuByKey(other);
                        }
//                    }else{
//                        int upsort = menu1.getSort();
//                        List<Menu1> q=list1.stream().filter(m->m.getPmenuno()==pid&&m.getSort()<upsort).collect(Collectors.toList());
//                        if(q.size()>0){
//                            Menu1 other=q.get(q.size()-1);
//                            menu1.setSort(other.getSort());
//                            other.setSort(upsort);
//                            menuService.updateMenu1ByKey(menu1);
//                            menuService.updateMenu1ByKey(other);
//                        }
//                    }
                    result=menuno+"";
                    break;
                case "down":
                    //if (type.equals("1")) {
                        int downsort = menu.getSort();
                        List<Menu> q2=list.stream().filter(m->m.getPmenuno()==pid&&m.getSort()>downsort).collect(Collectors.toList());
                        if(q2.size()>0){
                            Menu other=q2.get(0);
                            menu.setSort(other.getSort());
                            other.setSort(downsort);
                            menuService.updateMenuByKey(menu);
                            menuService.updateMenuByKey(other);
                        }
//                    }else{
//                        int downsort = menu1.getSort();
//                        List<Menu1> q=list1.stream().filter(m->m.getPmenuno()==pid&&m.getSort()>downsort).collect(Collectors.toList());
//                        if(q.size()>0){
//                            Menu1 other=q.get(q.size()-1);
//                            menu1.setSort(other.getSort());
//                            other.setSort(downsort);
//                            menuService.updateMenu1ByKey(menu1);
//                            menuService.updateMenu1ByKey(other);
//                        }
//                    }
                    result=menuno+"";
                    break;
            }
        }
        catch (Exception e){
            throw e;
        }

        return result;
    }

    /**
     * 总部权限
     * @return
     */
    @RequestMapping(value = "/actsetzb",method = RequestMethod.GET)
    public ModelAndView actsetzb ()  {
        ModelAndView mv = new ModelAndView();
        List<Map<String,Object>>list=actService.selectActList();

        List<Map<String, Object>> menus = new ArrayList<Map<String, Object>>();
        List<Menu> menu = menuService.selectMenu(1);
        for (Menu m : menu) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", m.getMenuno());
            map.put("pid", m.getPmenuno());
            map.put("name", m.getMenutext());
            map.put("note", m.getMainword());
            map.put("open", true);
            map.put("extendMenu", null);
            menus.add(map);
        }
        mv.addObject("list", list);
        mv.addObject("menus", menus);
        return mv;
    }

    @RequestMapping(value = "/actsetzb",method = RequestMethod.POST)
    public String actsetzbaction (String action){
        String result="";
        try {
            switch (action) {
                case "choose": //选中角色
                    String act = Params("id");
                    List<String> menus = actService.selectRoleMenu( act);
                    result = StringUtils.join(menus.toArray(),",");
                    break;
                case "chooseMenu"://菜单的扩展权限

                    break;
//                case "setting"://权限保存
//                    String gid=Params("gid");//角色
//                    String mid=Params("mid");//菜单ids
//                    actService.saveActMenu("zb",gid, Arrays.asList(mid.split(",")));
//                    break;
                case "saveExtend"://扩展权限保存
                    break;

                case "add"://增加角色
                    String name = Params("name");
                    actService.insertAct(name);
                    result = JsonSerializer(new Object[]{true, ""});
                    break;
                case "edit"://修改角色名称
                    try {
                        String editname = Params("name");
                        String newname = Params("newname");
                        actService.updatetActName(editname, newname);
                        result = JsonSerializer(new Object[]{true, ""});
                    } catch (Exception ex) {
                        result = JsonSerializer(new Object[]{false, ex.getMessage()});
                    }
                    break;
                case "remove"://删除角色
                    try {
                        String delname = Params("name");
                        actService.deleteAct(delname);
                        result = JsonSerializer(new Object[]{true, ""});
                    } catch (Exception ex) {
                        result = JsonSerializer(new Object[]{false, ex.getMessage()});
                    }
                    break;
                case "save"://保存角色菜单
                    try {
                        String savename = Params("name");
                        String mid = Params("mid");
                        actService.saveActMenu( savename, Arrays.asList(mid.split(",")));
                        result = JsonSerializer(new Object[]{true, ""});
                    } catch (Exception ex) {
                        result = JsonSerializer(new Object[]{false, ex.getMessage()});
                    }
                    break;
            }
        }
        catch (Exception e){
            throw e;
        }
        return result;
    }

    @RequestMapping(value = "/dictionary",method = RequestMethod.GET)
    public ModelAndView dictionary ()  {
        ModelAndView mv = new ModelAndView();
        List<Dictionary> list=dicService.selectAllList();
        List<Map<String, Object>> dics = new ArrayList<Map<String, Object>>();
        for (Dictionary d : list) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("id", d.getDicid());
            map.put("pid", d.getParentId());
            map.put("name",d.getName());
            map.put("memo",d.getMemo());
            map.put("display", d.getDisplay());
            map.put("disabled", d.getDisabled());
            map.put("code", d.getCode());
            dics.add(map);
        }
        mv.addObject("dics", dics);
        return mv;
    }

    @RequestMapping(value = "/dictionary",method = RequestMethod.POST)
    public String dictionaryction ( String txtAction){
        String result="";
        Dictionary dic=null;
        List<Dictionary> list=null;
        int pid = ParamsInt("txtparentid");
        int dicid = ParamsInt("txtdicid");
        String name =Params("txtname") ;
        int display =ParamsInt("txtdisplay") ;
        int disabled =ParamsInt("txtdisabled") ;
        String memo =Params("txtmemo") ;
        String code =Params("txtcode") ;
        if(dicid!=0){
            dic=dicService.selectDicById(dicid);
            list=dicService.selectListByPId(dic.getParentId(),-1);
        }else{
            dic=new Dictionary();
            list=dicService.selectListByPId(pid,-1);
        }
        try {
            switch (txtAction) {
                case "add":
                case "modify":
                        dic.setName(name);
                        dic.setParentId(pid);
                        dic.setMemo(memo);
                        dic.setCode(code);
                        dic.setDisplay(display);
                        dic.setDisabled(disabled);

                        if(txtAction.equals("add")){
                            OptionalInt s=list.stream().mapToInt(d->d.getSort()).max();
                                if(s.equals(OptionalInt.empty())){
                                    dic.setSort(0+1);
                                }else{
                                    dic.setSort(s.getAsInt()+1);
                                }
                            dicService.insert(dic);
                        }else{
                            dicService.updateByKey(dic);
                        }
                        result=dic.getDicid().toString();
                    break;
                case "delete":
                    dicService.deleteByKey(dic);
                    result=dicid+"";
                    break;
                case "up": //上移
                        int upsort = dic.getSort();
                        List<Dictionary> q=list.stream().filter(d->d.getSort()<upsort).collect(Collectors.toList());
                        if(q.size()>0){
                            Dictionary other=q.get(q.size()-1);
                            dic.setSort(other.getSort());
                            other.setSort(upsort);
                            dicService.updateByKey(dic);
                            dicService.updateByKey(other);
                        }
                    result=dicid+"";
                    break;
                case "down":
                        int downsort = dic.getSort();
                        List<Dictionary> q2=list.stream().filter(m->m.getSort()>downsort).collect(Collectors.toList());
                        if(q2.size()>0){
                            Dictionary other=q2.get(0);
                            dic.setSort(other.getSort());
                            other.setSort(downsort);
                            dicService.updateByKey(dic);
                            dicService.updateByKey(other);
                        }
                    result=dicid+"";
                    break;
            }
        }
        catch (Exception e){
            throw e;
        }

        return result;
    }
    @RequestMapping(value = "/selectContractAll",method = RequestMethod.POST)
    public String selectContractAll (String data)  {

       // data= URLDecoder.decode(data);
        logs.WriteLog(data,"selectContractAll");
        String resultss="";
        try
        {
            resultss=contractService.selectContractAll(data);
        }catch (Exception e)
        {
            resultss=e.toString();
        }

        logs.WriteLog(resultss,"selectContractAll");
        return resultss;
    }
    @RequestMapping(value = "/addorUpdatContract",method = RequestMethod.POST)
    public String addorUpdatContract (String data)  {

      //  data= URLDecoder.decode(data);
        logs.WriteLog(data,"addorUpdateContract");
        String resultss=contractService.addorUpdateContract(data);
        logs.WriteLog(resultss,"addorUpdateContract");
        return resultss;

    }
    @RequestMapping(value = "/addContractRemark",method = RequestMethod.POST)
    public String addContractRemark (String data)  {

       //   data= URLDecoder.decode(data);
        logs.WriteLog(data,"addContractRemark");
        String resultss=contractService.addContractRemark(data);
        logs.WriteLog(resultss,"addContractRemark");
        return resultss;

    }
    @RequestMapping(value = "/addContractremainingamount",method = RequestMethod.POST)
    public String addContractremainingamount (String data)  {

        //   data= URLDecoder.decode(data);
        logs.WriteLog(data,"addContractremainingamount");
        String resultss=contractService.addContractremainingamount(data);
        logs.WriteLog(resultss,"addContractremainingamount");
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
    @RequestMapping(value = "/updatetierpic",method = RequestMethod.POST)
    public String updatetierpic (String data)  {
        logs.WriteLog(data,"updatetierpic");

        String resultss=contractService.updatetierpic(data);
        logs.WriteLog(resultss,"updatetierpic");
        return resultss;
    }
    @RequestMapping(value = "/tierpic",method = RequestMethod.POST)
    public String tierpic (String data)  {
        logs.WriteLog(data,"tierpic");

        //  data= URLDecoder.decode(data);
        String resultss=contractService.tierpic(data);
        logs.WriteLog(resultss,"tierpic");
        return resultss;
    }
    @RequestMapping(value = "/uploadinsurance",method = RequestMethod.POST)
    public String uploadinsurance (String data,@RequestParam(value="image") MultipartFile file)  {

        data= URLDecoder.decode(data);

        logs.WriteLog(data,"uploadinsurance");
        String ss=filecheck(file);
        if (!ss.equals(""))return  ss;
        data= URLDecoder.decode(data);
        String resultss=contractService.uploadinsurance(data,file);
        logs.WriteLog(resultss,"uploadinsurance");
        return resultss;
    }
    @RequestMapping(value = "/uploadJpg",method = RequestMethod.POST)
    public String uploadJpg (String data,@RequestParam(value="image") MultipartFile file)  {
        logs.WriteLog(data,"uploadJpg");
        String ss=filecheck(file);
        if (!ss.equals(""))return  ss;
        data= URLDecoder.decode(data);
        String resultss=contractService.uploadJpg(data,file);
        logs.WriteLog(resultss,"uploadJpg");
        return resultss;
    }
    @RequestMapping(value = "/uploadInvoice",method = RequestMethod.POST)
    public String uploadInvoice (String data,@RequestParam(value="image") MultipartFile file)  {

        data= URLDecoder.decode(data);

        logs.WriteLog(data,"uploadInvoice");
        String ss=filecheck(file);
        if (!ss.equals(""))return  ss;
        String resultss=contractService.uploadInvoice(data,file);
        logs.WriteLog(resultss,"uploadInvoice");
        return resultss;
    }
    @RequestMapping(value = "/submitcontract",method = RequestMethod.GET)
    public String submitcontract (String data)  {

        logs.WriteLog(data,"submitcontract");

        String resultss="";
        try {
            data= URLDecoder.decode(data);
            resultss=contractService.submitcontract(data);
        }catch (Exception e)
        {
            resultss=e.toString();
            logs.WriteLog(e.toString(),"submitcontract_error");
        }

        logs.WriteLog(resultss,"submitcontract");
        return resultss;
    }
    @RequestMapping(value = "/checkcontract",method = RequestMethod.GET)
    public String checkcontract (String data)  {

        logs.WriteLog(data,"checkcontract");
        data= URLDecoder.decode(data);
        String resultss=contractService.checkcontract(data);
        logs.WriteLog(resultss,"checkcontract");
        return resultss;
    }
    @RequestMapping(value = "/checkcontractDaimler",method = RequestMethod.POST)
    public String checkcontractDaimler (String data)  {

        logs.WriteLog(data,"checkcontractDaimler");

        String resultss=contractService.checkcontractDaimler(data);
        logs.WriteLog(resultss,"checkcontractDaimler");
        return resultss;
    }

    @RequestMapping(value = "/deletecontract",method = RequestMethod.GET)
    public String deletecontract (String data)  {

        logs.WriteLog(data,"deletecontract");
        data= URLDecoder.decode(data);
        String resultss=contractService.deletecontract(data);
        logs.WriteLog(resultss,"deletecontract");
        return resultss;
    }
    @RequestMapping(value = "/gettierdetail",method = RequestMethod.POST)
    public String gettierdetail (String data)  {

        logs.WriteLog(data,"gettierdetail");

        String resultss=contractService.gettierdetail(data);
        logs.WriteLog(resultss,"gettierdetail");
        return resultss;
    }
    @RequestMapping(value = "/addorupdatetier",method = RequestMethod.POST)
    public String addorupdatetier (String data)  {

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
    @RequestMapping(value = "/cancelcontract",method = RequestMethod.GET)
    public String cancelcontract (String data)  {

        logs.WriteLog(data,"cancelcontract");
        data= URLDecoder.decode(data);
        String resultss=contractService.cancelcontract(data);
        logs.WriteLog(resultss,"cancelcontract");
        return resultss;
    }
    @RequestMapping(value = "/getbrand",method = RequestMethod.GET)
    public String getbrand (String data)  {


        data= URLDecoder.decode(data);
        logs.WriteLog(data,"getbrand");
        String resultss=contractService.getbrand(data);
        logs.WriteLog(resultss,"getbrand");
        return resultss;
    }
    @RequestMapping(value = "/newContract",method = RequestMethod.GET)
    public String newContract (String data)  {


        data= URLDecoder.decode(data);
        logs.WriteLog(data,"newContract");
        String resultss=contractService.newContract(data);
        logs.WriteLog(resultss,"newContract");
        return resultss;
    }
    @RequestMapping(value = "/getcars",method = RequestMethod.GET)
    public String getcars (String data)  {
        data= URLDecoder.decode(data);
        logs.WriteLog(data,"getcars");
        String resultss=contractService.getcars(data);
        logs.WriteLog(resultss,"getcars");
        return resultss;

    }
    @RequestMapping(value = "/getvehicletype",method = RequestMethod.GET)
    public String getvehicletype (String data)  {

        data= URLDecoder.decode(data);
        logs.WriteLog(data,"getvehicletype");
        String resultss=contractService.getvehicletype(data);
        logs.WriteLog(resultss,"getvehicletype");
        return resultss;

    }
    @RequestMapping(value = "/manualBill",method = RequestMethod.GET)
    public String manualBill (String data)  {


        data= URLDecoder.decode(data);
        logs.WriteLog(data,"manualBill");
        String resultss=billService.manualBill(data);
        logs.WriteLog(resultss,"manualBill");
        return resultss;
    }
    @RequestMapping(value = "/billpaycheck",method = RequestMethod.POST)
    public String billpaycheck (String data)  {

        logs.WriteLog(data,"billpaycheck");
        String resultss=billService.billpaycheck(data);
        logs.WriteLog(resultss,"billpaycheck");
        return resultss;
    }
    @RequestMapping(value = "/updatebillinvoiceno",method = RequestMethod.GET)
    public String updatebillinvoiceno (String data)  {


        data= URLDecoder.decode(data);
        logs.WriteLog(data,"updatebillinvoiceno");
        String resultss=billService.updatebillinvoiceno(data);
        logs.WriteLog(resultss,"updatebillinvoiceno");
        return resultss;
    }
    @RequestMapping(value = "/getBillPage",method = RequestMethod.GET)
    public String getBillPage (String data)  {


        data= URLDecoder.decode(data);
        logs.WriteLog(data,"getBillPage");
        String resultss=billService.getBillPage(data);
        logs.WriteLog(resultss,"getBillPage");
        return resultss;
    }
    @RequestMapping(value = "/getUnconfirmBillCount",method = RequestMethod.GET)
    public String getUnconfirmBillCount (String data)  {


        data= URLDecoder.decode(data);
        logs.WriteLog(data,"getUnconfirmBillCount");
        String resultss=billService.getUnconfirmBillCount(data);
        logs.WriteLog(resultss,"getUnconfirmBillCount");
        return resultss;
    }
    @RequestMapping(value = "/cantractBill",method = RequestMethod.POST)
    public String cantractBill (String data)  {

        logs.WriteLog(data,"cantractBill");
        String resultss=billService.cantractBill(data);
        logs.WriteLog(resultss,"cantractBill");
        return resultss;
    }

    @RequestMapping(value = "/uploadpayment",method = RequestMethod.POST)
    public String uploadpayment (String data,@RequestParam(value="image") MultipartFile file) {

        logs.WriteLog(data, "uploadpayment");
        String resultss = "";
        try {
            String ss = filecheck(file);
            if (!ss.equals("")) return ss;

            resultss = billService.uploadpayment(data, file);
            logs.WriteLog(resultss, "uploadpayment");
        } catch (Exception ex) {
            logs.WriteLog(ex.getMessage(), "uploadpayment");
            resultss = ex.getMessage();
        }
        return resultss;
    }


    @RequestMapping(value = "/getBill",method = RequestMethod.GET)
    public String getBill (String data)  {
        data= URLDecoder.decode(data);
        logs.WriteLog(data,"getBill");

        String resultss=billService.getBill(data);
        logs.WriteLog(resultss,"getBill");
        return resultss;
    }

    @RequestMapping(value = "/getNotinBillContract",method = RequestMethod.GET)
    public String getNotinBillContract (String data)  {
        data= URLDecoder.decode(data);
        logs.WriteLog(data,"getNotinBillContract");

        String resultss=billService.getNotinBillContract(data);
        logs.WriteLog(resultss,"getNotinBillContract");
        return resultss;
    }
    @RequestMapping(value = "/getBilldetail",method = RequestMethod.GET)
    public String getBilldetail (String data)  {
        data= URLDecoder.decode(data);
        logs.WriteLog(data,"getBilldetail");

        String resultss=billService.getBilldetail(data);
        logs.WriteLog(resultss,"getBilldetail");
        return resultss;
    }
    @RequestMapping(value = "/confirmBilldetail",method = RequestMethod.GET)
    public String confirmBilldetail (String data)  {
        data= URLDecoder.decode(data);
        logs.WriteLog(data,"confirmBilldetail");

        String resultss=billService.confirmBilldetail(data);
        logs.WriteLog(resultss,"confirmBilldetail");
        return resultss;
    }
    @RequestMapping(value = "/refuseBilldetail",method = RequestMethod.GET)
    public String refuseBilldetail (String data)  {

        data= URLDecoder.decode(data);
        logs.WriteLog(data,"refuseBilldetail");
        String resultss=billService.refuseBilldetail(data);
        logs.WriteLog(resultss,"refuseBilldetail");
        return resultss;

    }
    @RequestMapping(value = "/Register",method = RequestMethod.GET)
    public String Register ()  {


        String resultss=invoiceservice.Register();
        logs.WriteLog(resultss,"Register");
        return resultss;

    }
    /*@RequestMapping(value = "/InvoiceBlueOneItemAndPreferentialPolicy",method = RequestMethod.GET)
    public String InvoiceBlueOneItemAndPreferentialPolicy (String data)  {
        data= URLDecoder.decode(data);
        logs.WriteLog(data,"InvoiceBlueOneItemAndPreferentialPolicy");

        String resultss=invoiceservice.InvoiceBlueOneItemAndPreferentialPolicy(data);
        logs.WriteLog(resultss,"InvoiceBlueOneItemAndPreferentialPolicy");
        return resultss;

    }*/
    @RequestMapping(value = "/addorUpdateClaim",method = RequestMethod.GET)
    public String addorUpdateClaim (String data)  {


        data= URLDecoder.decode(data);
        logs.WriteLog(data,"addorUpdateClaim");
        String resultss=claimService.addorUpdateClaim(data);
        logs.WriteLog(resultss,"addorUpdateClaim");
        return resultss;
    }
    @RequestMapping(value = "/addorUpdateClaimPost",method = RequestMethod.POST)
    public String addorUpdateClaimPost (String data)  {


      //  data= URLDecoder.decode(data);
        logs.WriteLog(data,"addorUpdateClaim");
        String resultss=claimService.addorUpdateClaim(data);
        logs.WriteLog(resultss,"addorUpdateClaim");
        return resultss;
    }
    @RequestMapping(value = "/getClaim",method = RequestMethod.GET)
    public String getClaim (String data)  {


        data= URLDecoder.decode(data);
        logs.WriteLog(data,"getClaim");
        String resultss=claimService.getClaim(data);
        logs.WriteLog(resultss,"getClaim");
        return resultss;
    }
    @RequestMapping(value = "/checkClaimPost",method = RequestMethod.POST)
    public String checkClaimPost (String data)  {

        logs.WriteLog(data,"checkClaim");
        String resultss=claimService.checkClaim(data);
        logs.WriteLog(resultss,"checkClaim");
        return resultss;
    }
    @RequestMapping(value = "/checkClaim",method = RequestMethod.GET)
    public String checkClaim (String data)  {


        data= URLDecoder.decode(data);
        logs.WriteLog(data,"checkClaim");
        String resultss=claimService.checkClaim(data);
        logs.WriteLog(resultss,"checkClaim");
        return resultss;
    }
    @RequestMapping(value = "/getclaimtierdetail",method = RequestMethod.POST)
    public String getclaimtierdetail (String data)  {
       logs.WriteLog(data,"getclaimtierdetail");
        String resultss=claimService.getclaimtierdetail(data);
        logs.WriteLog(resultss,"getclaimtierdetail");
        return resultss;
    }
    @RequestMapping(value = "/addorupdateclaimtier",method = RequestMethod.POST)
    public String addorupdateclaimtier (String data)  {
        logs.WriteLog(data,"addorupdateclaimtier");
        String resultss="";
        try
        {
            resultss=claimService.addorupdateclaimtier(data);
            logs.WriteLog(resultss,"addorupdateclaimtier");
        }catch (Exception e)
        {
            logs.WriteLog(e.toString(),"addorupdateclaimtier");
        }


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
        logs.WriteLog(data,"addClaimpic");
//        data= URLDecoder.decode(data);
        String resultss=claimService.addClaimpic(data,filetype,file);
        logs.WriteLog(resultss,"addClaimpic");
        return resultss;
    }
    @RequestMapping(value = "/getClaimpic",method = RequestMethod.GET)
    public String getClaimpic (String data)  {

        data= URLDecoder.decode(data);
        logs.WriteLog(data,"getClaimpic");
        String resultss=claimService.getClaimpic(data);
        logs.WriteLog(resultss,"getClaimpic");
        return resultss;
    }
    @RequestMapping(value = "/getClaimRemark",method = RequestMethod.POST)
    public String getClaimRemark (String data)  {

        data= URLDecoder.decode(data);
        logs.WriteLog(data,"getClaimRemark");
        String resultss=claimService.getClaimRemark(data);
        logs.WriteLog(resultss,"getClaimRemark");
        return resultss;
    }
    @RequestMapping(value = "/deleteClaimpic",method = RequestMethod.GET)
    public String deleteClaimpic (String data)  {


        data= URLDecoder.decode(data);
        logs.WriteLog(data,"deleteClaimpic");
        String resultss=claimService.deleteClaimpic(data);
        logs.WriteLog(resultss,"deleteClaimpic");
        return resultss;
    }

    @RequestMapping(value = "/getProvince",method = RequestMethod.GET)
    public String getProvince (String data)  {

        logs.WriteLog(data,"getProvince");
        data= URLDecoder.decode(data);
        String resultss=claimService.getProvince(data);
        logs.WriteLog(resultss,"getProvince");
        return resultss;
    }
    @RequestMapping(value = "/getCity",method = RequestMethod.GET)
    public String getCity (String data)  {

        logs.WriteLog(data,"getCity");
        data= URLDecoder.decode(data);
        String resultss=claimService.getCity(data);
        logs.WriteLog(resultss,"getCity");
        return resultss;
    }
    @RequestMapping(value = "/getDealerinfo",method = RequestMethod.GET)
    public String getDealerinfo(String data){

        data= URLDecoder.decode(data);
        logs.WriteLog(data,"getDealerinfo");
        String resultss=dicService.getDealerinfo(data);
        logs.WriteLog(resultss,"getDealerinfo");
        return resultss;

    }
    @RequestMapping(value = "/getclaimtype",method = RequestMethod.GET)
    public String getclaimtype(String data){
        data= URLDecoder.decode(data);
        logs.WriteLog(data,"getclaimtype");
        String resultss=dicService.getclaimtype(data);
        logs.WriteLog(resultss,"getclaimtype");
        return resultss;

    }
    @RequestMapping(value = "/getinsurancetype",method = RequestMethod.GET)
    public String getinsurancetype(){
        String resultss=dicService.getinsurancetype();
        logs.WriteLog(resultss,"getinsurancetype");
        return resultss;

    }
    @RequestMapping(value = "/getProduct",method = RequestMethod.GET)
    public String getProduct(String data){
        data= URLDecoder.decode(data);
        logs.WriteLog(data,"getProduct");
        String resultss=productService.getProduct(data);
        logs.WriteLog(resultss,"getProduct");
        return resultss;

    }
    @RequestMapping(value = "/getGroupProduct",method = RequestMethod.POST)
    public String getGroupProduct(String data){
        logs.WriteLog(data,"getGroupProduct");
        String resultss=productService.getGroupProduct(data);
        logs.WriteLog(resultss,"getGroupProduct");
        return resultss;

    }

    @RequestMapping(value = "/contractPrint",method = RequestMethod.POST)
    public String contractPrint(String data){
        String resultss=contractService.contractPrint(data);
        logs.WriteLog(resultss,"contractPrint");
        return resultss;

    }
    @RequestMapping(value = "/printBmw",method = RequestMethod.POST)
    public String contractPrintBmw(String data){
        String resultss=contractService.contractPrintBmw(data);
        logs.WriteLog(resultss,"contractPrintBmw");
        return resultss;

    }
    @RequestMapping(value = "/contract_down",method = RequestMethod.POST)
    public String contract_down(String data){
        logs.WriteLog(data,"contract_down");
        String resultss=contractService.contract_down(data);
        logs.WriteLog(resultss,"contract_down");
        return resultss;

    }
    @RequestMapping(value = "/claim_down",method = RequestMethod.POST)
    public String claim_down(String data){
        String resultss=claimService.claim_down(data);
        logs.WriteLog(resultss,"claim_down");
        return resultss;

    }
    @RequestMapping(value = "/bill_down",method = RequestMethod.POST)
    public String bill_down(String data){
        String resultss=billService.bill_down(data);
        logs.WriteLog(resultss,"bill_down");
        return resultss;

    }
    @RequestMapping(value = "/billdetail_down",method = RequestMethod.POST)
    public String billdetail_down(String data){
        String resultss=billService.billdetail_down(data);
        logs.WriteLog(resultss,"billdetail_down");
        return resultss;

    }
    @RequestMapping(value = "/adminbillpay",method = RequestMethod.POST)
    public String adminbillpay(String data){
        logs.WriteLog(data,"adminbillpay");
        String resultss=billService.adminbillpay(data);
        logs.WriteLog(resultss,"adminbillpay");
        return resultss;

    }
    @RequestMapping(value = "/invoicediscern",method = RequestMethod.GET)
    public String invoicediscern(String imgurl) throws IOException {
      if (!imgurl.contains("http"))imgurl=GetPathurl()+imgurl;
        String Category="newcar";
        String host = "http://gcfp.market.alicloudapi.com";
        String path = "/invoice/discern";
        String method = "POST";
        String appcode = "67d36acd94324d9d98b27f0f55000d49";
        String bodys = "{\"InvoiceImageUrl\":\""+imgurl+"\",\"Category\":\""+Category+"\"}";
        String resultss="";
        URL url = new URL(host+path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        PrintWriter out = null;
        conn.setRequestMethod(method);
        conn.setRequestProperty("Authorization", "APPCODE " + appcode);
        conn.setRequestProperty("contentType", "application/json; charset=UTF-8");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        //获取URLConnection对象对应的输出流
        out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"utf-8"));
        //发送请求参数即数据
        out.print(bodys);
        //缓冲数据
        out.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
        String line;
        StringBuffer buffer = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        reader.close();
        conn.disconnect();
        resultss=buffer.toString();
        return resultss;


    }
    @RequestMapping(value = "/recogliu",method = RequestMethod.GET)
    public String recogliu(String imgurl) throws Exception {
       // imgurl="d:\\bd.jpg";
        String typeId="1991";
        String host = "http://document.sinosecu.com.cn";
        String path = "/api/recogliu.do";

        String method = "POST";
        String appcode = "67d36acd94324d9d98b27f0f55000d49";
        String bodys = "img="+encodeBase64File(imgurl);
        String resultss="";
        URL url = new URL(host+path+"?typeId="+typeId);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        PrintWriter out = null;
        conn.setRequestMethod(method);
        conn.setRequestProperty("Authorization", "APPCODE " + appcode);
        conn.setRequestProperty("contentType", "application/json; charset=UTF-8");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        //获取URLConnection对象对应的输出流
        out = new PrintWriter(new OutputStreamWriter(conn.getOutputStream(),"utf-8"));
        //发送请求参数即数据
        out.print(bodys);
        //缓冲数据
        out.flush();

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
        String line;
        StringBuffer buffer = new StringBuffer();
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        reader.close();
        conn.disconnect();
        resultss=buffer.toString();
        JsonObject jsonObject = new JsonParser().parse(resultss).getAsJsonObject();
        JsonArray jarry=new JsonArray();
        if (jsonObject.keySet().contains("cardsinfo"))jarry=jsonObject.get("cardsinfo").getAsJsonArray();
        resultss="";
        for (int i=0;i<jarry.size();i++)
        {
            JsonArray jarry1=new JsonArray();
            jsonObject=jarry.get(i).getAsJsonObject();
            if (jsonObject.keySet().contains("rowitems"))jarry1=jsonObject.get("rowitems").getAsJsonArray();
            for (int i1=0;i1<jarry1.size();i1++)
            {
                jsonObject=jarry1.get(i1).getAsJsonObject();
                JsonArray jarry2=new JsonArray();
                if (jsonObject.keySet().contains("rowContext"))jarry2=jsonObject.get("rowContext").getAsJsonArray();
                for (int i2=0;i2<jarry2.size();i2++)
                {
                    jsonObject=jarry2.get(i2).getAsJsonObject();
                    if (jsonObject.keySet().contains("charValue"))jsonObject=jsonObject.get("charValue").getAsJsonObject();

                    if (jsonObject.keySet().contains("content"))resultss+=jsonObject.get("content").getAsString();

                }
                resultss+="\n";
            }

        }
        return resultss;


    }


    @RequestMapping(value = "/dealerBill",method = RequestMethod.POST)
    public String autoBill(String data) {

        String resultss="";
        try
        {
            resultss=billService.autoBill(data);
        }
        catch (Exception e)
        {
            resultss=e.toString();
        }
        logs.WriteLog(resultss,"dealerBill");
        return resultss;

    }
    @RequestMapping(value = "/getBillBmw",method = RequestMethod.GET)
    public String getBillBmw(String data) {

        data= URLDecoder.decode(data);
        logs.WriteLog(data,"getBillBmw");
        String resultss=bmwService.getBillBmw(data);
        logs.WriteLog(resultss,"getBillBmw");
        return resultss;

    }
    @RequestMapping(value = "/addPayrequest",method = RequestMethod.POST)
    public String addPayrequest(String data) {

       // data= URLDecoder.decode(data);
        logs.WriteLog(data,"addPayrequest");
        String resultss=bmwService.addPayrequest(data);
        logs.WriteLog(resultss,"addPayrequest");
        return resultss;

    }
    @RequestMapping(value = "/updatebillinvoice",method = RequestMethod.POST)
    public String updatebillinvoice(String data) {

        // data= URLDecoder.decode(data);
        logs.WriteLog(data,"updatebillinvoice");
        String resultss=bmwService.updatebillinvoice(data);
        logs.WriteLog(resultss,"updatebillinvoice");
        return resultss;

    }
    @RequestMapping(value = "/morebillinvoice",method = RequestMethod.POST)
    public String morebillinvoice(String data) {

        // data= URLDecoder.decode(data);
        logs.WriteLog(data,"updatebillinvoice");
        String resultss=bmwService.updatebillinvoice(data);
        logs.WriteLog(resultss,"updatebillinvoice");
        return resultss;

    }
    @RequestMapping(value = "/getSalebrand",method = RequestMethod.GET)
    public String getSalebrand(String data) {

         data= URLDecoder.decode(data);
        logs.WriteLog(data,"getSalebrand");
        String resultss=bmwService.getSalebrand(data);
        logs.WriteLog(resultss,"getSalebrand");
        return resultss;

    }
    @RequestMapping(value = "/billbmw_down",method = RequestMethod.GET)
    public String billbmw_down(String data) {

        data= URLDecoder.decode(data);
        logs.WriteLog(data,"billbmw_down");
        String resultss=bmwService.billbmw_down(data);
        logs.WriteLog(resultss,"billbmw_down");
        return resultss;

    }
    @RequestMapping(value = "/balancepay",method = RequestMethod.POST)
    public String balancepay(String data) {
        logs.WriteLog(data,"balancepay");
        String resultss=bmwService.balancepay( data);
        logs.WriteLog(resultss,"balancepay");
        return resultss;

    }
    @RequestMapping(value = "/getBalance",method = RequestMethod.POST)
    public String getBalance(String data) {
        logs.WriteLog(data,"getBalance");

        String resultss="";
        resultss=bmwService.getBalance( data);
        logs.WriteLog(resultss,"getBalance");
        return resultss;

    }
    @RequestMapping(value = "/getsystemremind",method = RequestMethod.POST)
    public String getsystemremind(String data) {
        logs.WriteLog(data,"getsystemremind");

        String resultss="";
        resultss=bmwService.getsystemremind( data);
        logs.WriteLog(resultss,"getsystemremind");
        return resultss;

    }

    @RequestMapping(value = "/getdealer",method = RequestMethod.POST)
    public String getdealer(String data) {
        logs.WriteLog(data,"getdealer");

        String resultss="";
        resultss=contractService.getdealer( data);
        logs.WriteLog(resultss,"getdealer");
        return resultss;

    }
    @RequestMapping(value = "/addbillmanul",method = RequestMethod.POST)
    public String addbillmanul(String data) {
        logs.WriteLog(data,"addbillmanul");
        String resultss=bmwService.addbillmanul( data);
        logs.WriteLog(resultss,"addbillmanul");
        return resultss;

    }
    //payment_down
    @RequestMapping(value = "/payment_down",method = RequestMethod.POST)
    public String payment_down(String data) {
        logs.WriteLog(data,"payment_down");
        String resultss=bmwService.payment_down( data);
        logs.WriteLog(resultss,"payment_down");
        return resultss;

    }
    @RequestMapping(value = "/addorupdatemarketactivity",method = RequestMethod.POST)
    public String addorupdatemarketactivity(String data) {
        logs.WriteLog(data,"addorupdatemarketactivity");

        String resultss="";
        resultss=contractService.addorupdatemarketactivity( data);
        logs.WriteLog(resultss,"addorupdatemarketactivity");
        return resultss;

    }
    @RequestMapping(value = "/setmarketactivityvalid",method = RequestMethod.POST)
    public String setmarketactivityvalid(String data) {
        logs.WriteLog(data,"setmarketactivityvalid");

        String resultss="";
        resultss=contractService.setmarketactivityvalid( data);
        logs.WriteLog(resultss,"setmarketactivityvalid");
        return resultss;

    }
    @RequestMapping(value = "/getvaildmarketactivity",method = RequestMethod.POST)
    public String getvaildmarketactivity(String data) {
        logs.WriteLog(data,"getvaildmarketactivity");

        String resultss="";
        resultss=contractService.getvaildmarketactivity( data);
        logs.WriteLog(resultss,"getvaildmarketactivity");
        return resultss;

    }
    @RequestMapping(value = "/vincheck",method = RequestMethod.POST)
    public String vincheck(String data) {
        logs.WriteLog(data,"vincheck");

        String resultss="";
        resultss=contractService.vincheck( data);
        logs.WriteLog(resultss,"vincheck");
        return resultss;

    }
    @RequestMapping(value = "/dealerzone",method = RequestMethod.POST)
    public String dealerzone(String data) {
        logs.WriteLog(data,"dealerzone");

        String resultss="";
        resultss=regionService.dealerzone( data);
        logs.WriteLog(resultss,"dealerzone");
        return resultss;

    }
    @RequestMapping(value = "/getClaimCount",method = RequestMethod.POST)
    public String getClaimCount(String data) {
        logs.WriteLog(data,"getClaimCount");

        String resultss="";
        resultss=claimService.getClaimCount( data);
        logs.WriteLog(resultss,"getClaimCount");
        return resultss;

    }

    @RequestMapping(value = "/uploaddocument",method = RequestMethod.POST)
    public String uploaddocument (String data,@RequestParam(value="image") MultipartFile file) {

        logs.WriteLog(data, "uploaddocument");
        String resultss = "";
        try {
            String ss=filecheck(file);
            if (!ss.equals(""))return  ss;
            resultss = productService.uploaddocument(data, file);
            logs.WriteLog(resultss, "uploaddocument");
        } catch (Exception ex) {
            logs.WriteLog(ex.getMessage(), "uploaddocument");
            resultss = ex.getMessage();
        }
        return resultss;
    }
    @RequestMapping(value = "/getdocument",method = RequestMethod.POST)
    public String getdocument(String data) {
        logs.WriteLog(data,"getdocument");

        String resultss="";
        resultss=productService.getdocument( data);
        logs.WriteLog(resultss,"getdocument");
        return resultss;

    }
    @RequestMapping(value = "/deletedocument",method = RequestMethod.POST)
    public String deletedocument(String data) {
        logs.WriteLog(data,"deletedocument");

        String resultss="";
        resultss=productService.deletedocument( data);
        logs.WriteLog(resultss,"deletedocument");
        return resultss;

    }
    @RequestMapping(value = "/getTireParameter",method = RequestMethod.POST)
    public String getTireParameter(String data) {
        logs.WriteLog(data,"getTireParameter");

        String resultss="";
        resultss=productService.getTireParameter( data);
        logs.WriteLog(resultss,"getTireParameter");
        return resultss;

    }
    @RequestMapping(value = "/getTireList",method = RequestMethod.POST)
    public String getTireList(String data) {
        logs.WriteLog(data,"getTireList");

        String resultss="";
        resultss=productService.getTireList( data);
        logs.WriteLog(resultss,"getTireList");
        return resultss;

    }

    @RequestMapping(value = "/exportclaimpic",method = RequestMethod.POST)
    public String exportclaimpic(String data) {

        String resultss = "";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid = "", nonce = "", time = "", sign = "";
        String result = "";
        if (jsonObject.keySet().contains("appid")) appid = jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce")) nonce = jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time")) time = jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign")) sign = jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data")) jsonObject = jsonObject.get("data").getAsJsonObject();
        result = actService.checkSign(nonce, time, sign, appid, data);
        if (!result.equals("")) return result;

        String claimno = "";
        try {
            if (jsonObject.keySet().contains("claimno")) claimno = jsonObject.get("claimno").getAsString();
            else return actService.GetErrorString(1, "");
            //resultss=productService.deletedocument( data);
            List<Map<String, Object>> piclist = claimService.getClaimPic(claimno);
            String root =wximage;// request.getSession().getServletContext().getRealPath("/");
            if (piclist.size() > 0) {
                String t = String.valueOf(setting.GetCurrenntTime());
                List<String> zipsource= new ArrayList<String>();
                for (int i = 0; i < piclist.size(); i++) {
                    String picurl = piclist.get(i).get("picurl").toString();
                    String claim = piclist.get(i).get("claimno").toString();
                    String pictype = piclist.get(i).get("pictype").toString();
                    File source = new File(wximage + picurl.replace("wximage/", ""));
                    File to = new File(root, "temp/" + t + "/" + claim + "/" + pictype + "." + FilenameUtils.getExtension(source.getAbsolutePath()));
                    File dic = new File(root, "temp/" + t + "/" + claim + "/");

                    if(!zipsource.contains(root + "temp/" + t + "/" + claim + "/")) {
                        zipsource.add(root + "temp/" + t + "/" + claim + "/");
                    }
                    //如果目录不存在
                    if (!dic.exists()) {
                        //创建目录
                        dic.mkdirs();
                    }
                    try {
                        FileUtils.copyFile(source, to);
                    } catch (Exception exx) {
                        exx.printStackTrace();
                    }
                }
                //String zipsource = root + "temp/" + t + "/";
                String zippath = root + "temp/" + t + ".zip";
                ZipCompressor.compressFile(zipsource.toArray(new String[0]), zippath);

                response.setContentType("application/octet-stream");
                response.setHeader("name", t + ".zip");
                response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
                response.setHeader("Pragma", "public");
                response.setDateHeader("Expires", 0);
                response.setHeader("Content-disposition", "attachment; filename=\"" + t + ".zip" + "\"");

                File ftp = ResourceUtils.getFile(root + "temp/" + t + ".zip");
                InputStream in = new FileInputStream(ftp);
                // 循环取出流中的数据
                byte[] b = new byte[1024*2];
                int len;
                while ((len = in.read(b)) !=-1) {
                    response.getOutputStream().write(b, 0, len);
                }
                response.getOutputStream().flush();
                response.getOutputStream().close();
                //logs.WriteLog(resultss,"deletedocument");
            } else {
                resultss = actService.GetErrorString(1, "未找到数据");
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
            resultss = actService.GetErrorString(1, ex.getMessage());
        }
        return resultss;

    }


    @RequestMapping(value = "/motorexportclaimpic",method = RequestMethod.POST)
    public String motorexportclaimpic(String data) {

        String resultss = "";
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid = "", nonce = "", time = "", sign = "";
        String result = "";
        if (jsonObject.keySet().contains("appid")) appid = jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce")) nonce = jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time")) time = jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign")) sign = jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data")) jsonObject = jsonObject.get("data").getAsJsonObject();
        result = actService.checkSign(nonce, time, sign, appid, data);
        if (!result.equals("")) return result;

        String claimno = "";
        try {
            if (jsonObject.keySet().contains("claimno")) claimno = jsonObject.get("claimno").getAsString();
            else return actService.GetErrorString(1, "");
            //resultss=productService.deletedocument( data);
            List<Map<String, Object>> piclist = claimService.getMotorClaimPic(claimno);
            String root =wximage;// request.getSession().getServletContext().getRealPath("/");
            if (piclist.size() > 0) {
                String t = String.valueOf(setting.GetCurrenntTime());
                List<String> zipsource= new ArrayList<String>();
                for (int i = 0; i < piclist.size(); i++) {
                    String picurl = piclist.get(i).get("picurl").toString();
                    String claim = piclist.get(i).get("claimno").toString();
                    String pictype = piclist.get(i).get("pictype").toString();
                    File source = new File(wximage + picurl.replace("wximage/", ""));
                    File to = new File(root, "temp/" + t + "/" + claim + "/" + pictype + "." + FilenameUtils.getExtension(source.getAbsolutePath()));
                    File dic = new File(root, "temp/" + t + "/" + claim + "/");

                    if(!zipsource.contains(root + "temp/" + t + "/" + claim + "/")) {
                        zipsource.add(root + "temp/" + t + "/" + claim + "/");
                    }
                    //如果目录不存在
                    if (!dic.exists()) {
                        //创建目录
                        dic.mkdirs();
                    }
                    try {
                        FileUtils.copyFile(source, to);
                    } catch (Exception exx) {
                        exx.printStackTrace();
                    }
                }
                //String zipsource = root + "temp/" + t + "/";
                String zippath = root + "temp/" + t + ".zip";
                ZipCompressor.compressFile(zipsource.toArray(new String[0]), zippath);

                response.setContentType("application/octet-stream");
                response.setHeader("name", t + ".zip");
                response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
                response.setHeader("Pragma", "public");
                response.setDateHeader("Expires", 0);
                response.setHeader("Content-disposition", "attachment; filename=\"" + t + ".zip" + "\"");

                File ftp = ResourceUtils.getFile(root + "temp/" + t + ".zip");
                InputStream in = new FileInputStream(ftp);
                // 循环取出流中的数据
                byte[] b = new byte[1024*2];
                int len;
                while ((len = in.read(b)) !=-1) {
                    response.getOutputStream().write(b, 0, len);
                }
                response.getOutputStream().flush();
                response.getOutputStream().close();
                //logs.WriteLog(resultss,"deletedocument");
            } else {
                resultss = actService.GetErrorString(1, "未找到数据");
            }
        }
        catch (Exception ex){
            ex.printStackTrace();
            resultss = actService.GetErrorString(1, ex.getMessage());
        }
        return resultss;

    }
    @RequestMapping(value = "/uploadcheckpic",method = RequestMethod.POST)
    public String uploadcheckpic (String data,@RequestParam(value="image") MultipartFile file) {

        String resultss = "";
        try {
            JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
            String appid = "", nonce = "", time = "", sign = "";
            String result = "";
            if (jsonObject.keySet().contains("appid")) appid = jsonObject.get("appid").getAsString();
            if (jsonObject.keySet().contains("nonce")) nonce = jsonObject.get("nonce").getAsString();
            if (jsonObject.keySet().contains("time")) time = jsonObject.get("time").getAsString();
            if (jsonObject.keySet().contains("sign")) sign = jsonObject.get("sign").getAsString();
            if (jsonObject.keySet().contains("data")) jsonObject = jsonObject.get("data").getAsJsonObject();
            result = actService.checkSign(nonce, time, sign, appid, data);
            if (!result.equals("")) return result;

            String claimno = "";
            int tid=0;
            String name = "";
            if (jsonObject.keySet().contains("claimno")) claimno = jsonObject.get("claimno").getAsString();
            if (jsonObject.keySet().contains("tid")) tid = jsonObject.get("tid").getAsInt();
            if (jsonObject.keySet().contains("name")) name = jsonObject.get("name").getAsString();

            String fileurl=actService.uploadJpg(file);
            int id= claimService.insertCheckPic(tid,claimno, name,fileurl);

            resultss="{\"errcode\":\"0\",\"id\":\""+id+"\",\"fileurl\":\""+fileurl+"\"}";

        } catch (Exception ex) {
            logs.WriteLog(ex.getMessage(), "uploadcheckcpic");
            resultss = ex.getMessage();
        }
        return resultss;
    }

    @RequestMapping(value = "/deletecheckpic",method = RequestMethod.POST)
    public String deletecheckpic(String data) {

        String resultss="";
        try {
            JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
            String appid = "", nonce = "", time = "", sign = "";
            String result = "";
            if (jsonObject.keySet().contains("appid")) appid = jsonObject.get("appid").getAsString();
            if (jsonObject.keySet().contains("nonce")) nonce = jsonObject.get("nonce").getAsString();
            if (jsonObject.keySet().contains("time")) time = jsonObject.get("time").getAsString();
            if (jsonObject.keySet().contains("sign")) sign = jsonObject.get("sign").getAsString();
            if (jsonObject.keySet().contains("data")) jsonObject = jsonObject.get("data").getAsJsonObject();
            result = actService.checkSign(nonce, time, sign, appid, data);
            if (!result.equals("")) return result;

            int id=0;
            if (jsonObject.keySet().contains("id")) id = jsonObject.get("id").getAsInt();

            int flag= claimService.deleteCheckPicByID(id);

            resultss="{\"errcode\":\"0\",\"errmsg\":\"\"}";

        } catch (Exception ex) {
            logs.WriteLog(ex.getMessage(), "deletecheckcpic");
            resultss = ex.getMessage();
        }
        logs.WriteLog(resultss,"deletecheckcpic");
        return resultss;

    }
    @RequestMapping(value = "/dcheckpic",method = RequestMethod.POST)
    public String dcheckpic(String data) {

        String resultss="";
        try {
            JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
            String appid = "", nonce = "", time = "", sign = "";
            String result = "";
            if (jsonObject.keySet().contains("appid")) appid = jsonObject.get("appid").getAsString();
            if (jsonObject.keySet().contains("nonce")) nonce = jsonObject.get("nonce").getAsString();
            if (jsonObject.keySet().contains("time")) time = jsonObject.get("time").getAsString();
            if (jsonObject.keySet().contains("sign")) sign = jsonObject.get("sign").getAsString();
            if (jsonObject.keySet().contains("data")) jsonObject = jsonObject.get("data").getAsJsonObject();
            result = actService.checkSign(nonce, time, sign, appid, data);
            if (!result.equals("")) return result;

            int id=0;
            if (jsonObject.keySet().contains("id")) id = jsonObject.get("id").getAsInt();

            int flag= claimService.deleteCheckPicByID(id);

            resultss="{\"errcode\":\"0\",\"errmsg\":\"\"}";

        } catch (Exception ex) {
            logs.WriteLog(ex.getMessage(), "deletecheckcpic");
            resultss = ex.getMessage();
        }
        logs.WriteLog(resultss,"deletecheckcpic");
        return resultss;

    }
    @RequestMapping(value = "/selectcheckpic",method = RequestMethod.POST)
    public String selectcheckpic(String data) {

        String resultss="";
        try {
            JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
            String appid = "", nonce = "", time = "", sign = "";
            String result = "";
            if (jsonObject.keySet().contains("appid")) appid = jsonObject.get("appid").getAsString();
            if (jsonObject.keySet().contains("nonce")) nonce = jsonObject.get("nonce").getAsString();
            if (jsonObject.keySet().contains("time")) time = jsonObject.get("time").getAsString();
            if (jsonObject.keySet().contains("sign")) sign = jsonObject.get("sign").getAsString();
            if (jsonObject.keySet().contains("data")) jsonObject = jsonObject.get("data").getAsJsonObject();
            result = actService.checkSign(nonce, time, sign, appid, data);
            if (!result.equals("")) return result;

            int id=0;
            String claimno="";
            if (jsonObject.keySet().contains("tid")) id = jsonObject.get("tid").getAsInt();
            if (jsonObject.keySet().contains("claimno")) claimno = jsonObject.get("claimno").getAsString();

            List<Map<String,Object>> list= claimService.selectCheckPicList(id,claimno);
            resultss=JsonSerializer(list,"yyyy-MM-dd HH:mm:ss");

        } catch (Exception ex) {
            logs.WriteLog(ex.getMessage(), "selectcheckcpic");
            resultss = ex.getMessage();
        }
        logs.WriteLog(resultss,"selectcheckcpic");
        return resultss;

    }
}
