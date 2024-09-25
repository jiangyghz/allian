package com.bond.allianz.controller;

import com.bond.allianz.Dao.InsuranceDao;
import com.bond.allianz.entity.PageInfo;
import com.bond.allianz.entity.User;
import com.bond.allianz.service.*;
import com.bond.allianz.utils.Cryptography;
import com.bond.allianz.utils.ExcelUtil;
import com.bond.allianz.utils.GuidUtil;
import com.github.pagehelper.PageHelper;
import com.google.gson.JsonObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import java.io.File;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/agency")
public class AgencyController extends BaseController{

    @Value("${domain.enter}")
    public String domainenter;

    @Autowired
    private AgencyService agencyService;

    @Autowired
    private ActService actService;

    @Autowired
    private UserService userService;
    @Autowired
    private InsuranceService insuranceService;
    @Autowired
    private CarService carService;
    @Autowired
    private RegionService regionService;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public ModelAndView list(){
        ModelAndView mv = new ModelAndView();

        List<Map<String,Object>> brand=carService.selectBrand(1);
        mv.addObject("brand", brand);
        return mv;
    }
    @RequestMapping(value = "/agencyreport", method = RequestMethod.GET)
    public ModelAndView agencyreport(){
        ModelAndView mv = new ModelAndView();

        List<Map<String,Object>> brand=carService.selectBrand(1);
        mv.addObject("brand", brand);
        return mv;
    }

    /**
     * 经销商导出
     * @return
     */
    @RequestMapping(value = "/exportagency")
    public  String exportagency(){
        String action=Params("action");
        String result="";
        if(!action.isEmpty()){
            switch (action) {
                case "Exportss":
                    int pageIndex = ParamsInt("curPageIndex", 1);
                    int pageSize = ParamsInt("pageSize", 10);
                    String keyid = Params("keyid");
                    String keyname = Params("keyname");
                    String keybrand = Params("keybrand");
                    int keyvalid = ParamsInt("keyvalid");
                    try {
                        List<Map<String, Object>> list = agencyService.selectlist(keyid, keyname, keybrand, keyvalid);
                        if (list.size() == 0) {
                            result = "未找到数据";
                        } else {
                            String root = request.getSession().getServletContext().getRealPath("/");
                            //File file = new File(root, "temp/");
                            //if (!file.exists()) file.mkdirs();
                            String fileName = "经销商信息_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xls";
                            //String filepath = new File(root, "temp/" + fileName).getPath();
                            String model = new File(root, "download/agencymodel.xls").getPath();
                            HSSFWorkbook workbook = ExcelUtil.ExportAgency(list, model);
                            response.setContentType("application/octet-stream");
                            response.setHeader("name", fileName);
                            response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
                            response.setHeader("Pragma", "public");
                            response.setDateHeader("Expires", 0);
                            response.setHeader("Content-disposition", "attachment; filename=\"" + URLEncoder.encode(fileName, "UTF-8") + "\"");

                            workbook.write(response.getOutputStream()); // 输出流控制workbook

                            response.getOutputStream().flush();

                            response.getOutputStream().close();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                    break;
            }
        }
        return result;
    }
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    public  String listpost() {
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
                    PageInfo<Map<String, Object>> page = agencyService.selectpage(pageIndex, pageSize, keyid, keyname, keybrand, keyvalid,"","","");
                    result = JsonSerializer(page);

                    break;
                case "delete":
                    try {
                        agencyService.deleteVirtualByKey(Params("tid"));
                        result = JsonSerializer(new Object[]{true, ""});
                    } catch (Exception ex) {
                        result = JsonSerializer(new Object[]{false, ex.getMessage()});
                    }
                    break;
            }
        }
        return result;
    }
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public  ModelAndView edit(String id){
        ModelAndView mv = new ModelAndView();
        Map<String,Object> map=new HashMap<String,Object>();
        if(id!=null&&!"".equals(id)){
            map=agencyService.selectByKey(id);
        }
        mv.addObject("info", map);
        mv.addObject("id", id);
        List<Map<String,Object>> brand=carService.selectBrand(1);
        List<Map<String,Object>> province=regionService.selectProvince();
        mv.addObject("brand", brand);
        mv.addObject("province", province);
        mv.addObject("domainenter", domainenter);
        return mv;
    }
    @RequestMapping(value = "/editadd", method = RequestMethod.GET)
    public  ModelAndView editadd(){
        ModelAndView mv = new ModelAndView();
        List<Map<String,Object>> brand=carService.selectBrand(1);
        List<Map<String,Object>> province=regionService.selectProvince();
        mv.addObject("brand", brand);
        mv.addObject("province", province);
        mv.addObject("domainenter", domainenter);
        return mv;
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String editsave(){

        String action=Params("action");
        String result="";
        if(!action.isEmpty()){
            switch (action){
                case "save":
                    try {
                        String id=Params("id");
                        String dealerno=Params("dealerno");
                        if("".equals(id)&&agencyService.existsByCode(dealerno)>0){
                            result = JsonSerializer(new Object[]{false, "经销商代码已存在"});
                            return  result;
                        }
                        Map<String,Object> map=new HashMap<String,Object>();
                        map.put("brand",Params("brand"));
                        map.put("dealerno",Params("dealerno"));
                        map.put("dealername",Params("dealername"));
                        map.put("address",Params("address"));
                        map.put("tel",Params("tel"));
                        map.put("email",Params("email"));
                        map.put("invoiceheading",Params("invoiceheading"));
                        map.put("bank",Params("bank"));
                        map.put("invoiceaddress",Params("invoiceaddress"));
                        map.put("bankacount",Params("bankacount"));
                        map.put("invoicetel",Params("invoicetel"));
                        map.put("vatno",Params("vatno"));
                        //map.put("is_elec_invoice",ParamsInt("is_elec_invoice"));
                        map.put("valid",ParamsInt("valid"));
                        map.put("linkman",Params("linkman"));
                        map.put("issellkey",ParamsInt("issellkey"));
                        map.put("isselltire",ParamsInt("isselltire"));
                        map.put("notirepic",ParamsInt("notirepic"));
                        map.put("linkmantel",Params("linkmantel"));
                        map.put("groupname",Params("groupname"));
                        map.put("zone",Params("zone"));
                        map.put("province",Params("province"));
                        map.put("city",Params("city"));
                        map.put("pcname",Params("pcname"));
                        map.put("salebrand",Params("salebrand"));
                        if("".equals(id)){
                            agencyService.insert(map);
                        }else {
                            agencyService.updateByKey(id, map);
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
    @RequestMapping(value = "/insurancelist", method = RequestMethod.GET)
    public ModelAndView insurancelist(){
        ModelAndView mv = new ModelAndView();
        return mv;
    }
    @RequestMapping(value = "/insurancelist", method = RequestMethod.POST)
    public  String insurancelistpost(){
        String action=Params("action");
        String result="";
        if(!action.isEmpty()){
            switch (action) {
                case "GetData":
                    int pageIndex = ParamsInt("curPageIndex", 1);
                    int pageSize = ParamsInt("pageSize", 10);
                    String keyiname = Params("keyiname");
                    int keyvalid = ParamsInt("keyvalid");
                    PageInfo<Map<String, Object>> page = insuranceService.selectpage(pageIndex, pageSize, keyiname, keyvalid);
                    result = JsonSerializer(page);
                    break;
                case "delete":
                    try {
                        insuranceService.deleteVirtualByKey(Params("tid"));
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
    @RequestMapping(value = "/insuranceedit", method = RequestMethod.GET)
    public  ModelAndView insuranceedit(String id){
        ModelAndView mv = new ModelAndView();
        Map<String,Object> map=new HashMap<String,Object>();
        if(id!=null&&!"".equals(id)){
            map=insuranceService.selectByCode(id);
        }else{
            map.put("code","");
            map.put("iname","");
            map.put("shortname","");
            map.put("address","");
            map.put("email","");
            map.put("zip","");
            map.put("valid",1);
        }
        mv.addObject("info", map);
        mv.addObject("id", id);
        return mv;
    }
    @RequestMapping(value = "/insuranceedit", method = RequestMethod.POST)
    public String insuranceeditsave(String code,String iname,String shortname,String address,String email,String zip){

        String action=Params("action");
        String result="";
        if(!action.isEmpty()){
            switch (action){
                case "save":
                    try {
                        String id=Params("id");
                        if("".equals(id)){
                            insuranceService.insert(code, iname,shortname, address,email,zip);
                        }else{
                            insuranceService.updateByKey(id,iname,shortname, address,email,zip);
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
