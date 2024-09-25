package com.bond.allianz.controller;

import com.bond.allianz.entity.PageInfo;
import com.bond.allianz.entity.User;
import com.bond.allianz.service.*;
import com.bond.allianz.utils.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/manage")
public class ManageController  extends BaseController {


    @Autowired
    private UserService userService;

    @Autowired
    private ActService actService;

    @Autowired
    private AgencyService agencyService;
    @Autowired
    private CarService carService;
    @Autowired
    private ProductService productService;
    @Autowired
    private InsuranceService insuranceService;
    @Autowired
    private InvoiceInfoService invoiceInfoService;
    @Autowired
    private UserResetService userResetService;
    @Autowired
    private RegionService regionService;
    /**
     * 用户列表
     * @return
     */
    @RequestMapping(value = "/userlist", method = RequestMethod.GET)
    public  ModelAndView userlist(){
        ModelAndView mv = new ModelAndView();
        List<Map<String,Object>> brand=carService.selectBrand(1);
        mv.addObject("brand", brand);
        return mv;
    }
    @RequestMapping(value = "/agencyuserlist", method = RequestMethod.GET)
    public  ModelAndView agencyuserlist(){
        ModelAndView mv = new ModelAndView();
        return mv;
    }
    @RequestMapping(value = "/agencyuserlistp", method = RequestMethod.GET)
    public  ModelAndView agencyuserlistp(){
        ModelAndView mv = new ModelAndView();
        return mv;
    }

    @RequestMapping(value = "/exportagencyuser")
    public  String exportagencyuser(){
        String action=Params("action");
        String result="";
        if(!action.isEmpty()){
            switch (action) {
                case "Exportss":
                    String keyagencyid=Params("keyagencyid");
                    String keyagencyname=Params("keyagencyname");
                    String keybrand = Params("keybrand");
                    String keyname=Params("keyname");
                    String keytel=Params("keytel");
                    int keyvalid=ParamsInt("keyvalid",-1);
                    int keyisactived=ParamsInt("keyisactived",-1);



                    try {
                        List<Map<String, Object>> list = userService.selectAgencyAllExport(keyagencyid,keyagencyname ,keybrand, keyname, keytel, keyvalid,keyisactived,"经销商");

                        if (list.size() == 0) {
                            result = "未找到数据";
                        } else {
                            String root = request.getSession().getServletContext().getRealPath("/");
                            //File file = new File(root, "temp/");
                            //if (!file.exists()) file.mkdirs();
                            String fileName = "经销商用户_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".xls";
                            //String filepath = new File(root, "temp/" + fileName).getPath();
                            String model = new File(root, "download/agencyuser.xls").getPath();
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
    /**
     * 用户列表 保存
     * @return
     */
    @RequestMapping(value = "/userlist", method = RequestMethod.POST)
    public  String userlistpost(){
        String action=Params("action");
        String result="";
        if(!action.isEmpty()){
            switch (action){
                case "GetData":
                    int pageIndex = ParamsInt("curPageIndex",1);
                    int pageSize = ParamsInt("pageSize",10);
                    String keyagencyid=Params("keyagencyid");
                    String keyagencyname=Params("keyagencyname");
                    String keybrand = Params("keybrand");
                    String keyname=Params("keyname");
                    String keytel=Params("keytel");
                    int keyvalid=ParamsInt("keyvalid");
                    int keyisactived=ParamsInt("keyisactived");
                    PageInfo<Map<String, Object>> page = userService.selectAgencyAll(pageIndex, pageSize,keyagencyid,keyagencyname ,keybrand, keyname, keytel, keyvalid,keyisactived);
                    result=JsonSerializer(page);
                    break;
                case "del":
                    try {
                        userService.delete(Params("id"));
                        result = JsonSerializer(new Object[]{true, ""});
                    }
                    catch (Exception ex) {
                        result = JsonSerializer(new Object[]{false, ex.getMessage()});
                    }
                    break;
                case "unlock":
                    try {
                        User user=userService.selectByPrimaryKey(Params("id"));
                        user.setIslock(0);
                        user.setErrorcount(0);
                        userService.update(user);
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
    @RequestMapping(value = "/agencyuserlist", method = RequestMethod.POST)
    public  String agencyuserlistpost(){
        String action=Params("action");
        String result="";
        if(!action.isEmpty()){
            switch (action){
                case "GetData":
                    int pageIndex = ParamsInt("curPageIndex",1);
                    int pageSize = ParamsInt("pageSize",10);
                    String keyagencyid= UserInfo().getAgencyID();
                    String keyagencyname=Params("keyagencyname");
                    String keyname=Params("keyname");
                    String keytel=Params("keytel");
                    int keyvalid=ParamsInt("keyvalid");
                    int keyisactived=ParamsInt("keyisactived");
                    PageInfo<Map<String, Object>> page = userService.selectAgencyAll(pageIndex, pageSize,keyagencyid,keyagencyname ,"", keyname, keytel, keyvalid,keyisactived);
                    result=JsonSerializer(page);
                    break;
                case "del":
                    try {
                        userService.delete(Params("id"));
                        result = JsonSerializer(new Object[]{true, ""});
                    }
                    catch (Exception ex) {
                        result = JsonSerializer(new Object[]{false, ex.getMessage()});
                    }
                    break;
                case "unlock":
                    try {
                        User user=userService.selectByPrimaryKey(Params("id"));
                        user.setIslock(0);
                        user.setErrorcount(0);
                        userService.update(user);
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

    @RequestMapping(value = "/agencyuserlistp", method = RequestMethod.POST)
    public  String agencyuserlistppost(){
        String action=Params("action");
        String result="";
        if(!action.isEmpty()){
            switch (action){
                case "GetData":
                    int pageIndex = ParamsInt("curPageIndex",1);
                    int pageSize = ParamsInt("pageSize",10);
                    String keyagencyid= UserInfo().getAgencyID();
                    String keyagencyname=Params("keyagencyname");
                    String keyname=Params("keyname");
                    String keytel=Params("keytel");
                    int keyvalid=ParamsInt("keyvalid");
                    int keyisactived=ParamsInt("keyisactived");
                    PageInfo<Map<String, Object>> page = userService.selectAgencyAll(pageIndex, pageSize,keyagencyid,keyagencyname ,"", keyname, keytel, keyvalid,keyisactived);
                    result=JsonSerializer(page);
                    break;
                case "del":
                    try {
                        userService.delete(Params("id"));
                        result = JsonSerializer(new Object[]{true, ""});
                    }
                    catch (Exception ex) {
                        result = JsonSerializer(new Object[]{false, ex.getMessage()});
                    }
                    break;
                case "unlock":
                    try {
                        User user=userService.selectByPrimaryKey(Params("id"));
                        user.setIslock(0);
                        user.setErrorcount(0);
                        userService.update(user);
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

    @RequestMapping(value = "/agencyuserlistp2", method = RequestMethod.POST)
    public  String agencyuserlistppost2(String data){

        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        JsonObject data2=jsonObject.get("data").getAsJsonObject();
        String action=data2.keySet().contains("action")? data2.get("action").getAsString():"";
        String result="";
        if(!action.isEmpty()){
            switch (action){
                case "GetData":
                    int pageIndex =data2.keySet().contains("curPageIndex")? data2.get("curPageIndex").getAsInt():1;
                    int pageSize =data2.keySet().contains("pageSize")? data2.get("pageSize").getAsInt():10;
                    String uid=data2.keySet().contains("uid")? data2.get("uid").getAsString():"";
                    User user2=userService.selectByPrimaryKey(uid);
                    String keyagencyid= user2.getAgencyid();
                    //String keyagencyname=Params("keyagencyname");
                    String keyname=data2.keySet().contains("keyname")? data2.get("keyname").getAsString():"";
                    //String keytel=Params("keytel");
                    int keyvalid=data2.keySet().contains("keyvalid")? data2.get("keyvalid").getAsInt():-1;
                    int keyisactived=data2.keySet().contains("keyisactived")? data2.get("keyisactived").getAsInt():-1;
                    PageInfo<Map<String, Object>> page = userService.selectAgencyAll2(pageIndex, pageSize,keyagencyid ,"", keyname, keyvalid,keyisactived);
                    result=JsonSerializer(page);
                    break;
                case "del":
                    try {
                        userService.delete(data2.keySet().contains("id")? data2.get("id").getAsString():"");
                        result = JsonSerializer(new Object[]{true, ""});
                    }
                    catch (Exception ex) {
                        result = JsonSerializer(new Object[]{false, ex.getMessage()});
                    }
                    break;
                case "unlock":
                    try {
                        User user=userService.selectByPrimaryKey(data2.keySet().contains("id")? data2.get("id").getAsString():"");
                        user.setIslock(0);
                        user.setErrorcount(0);
                        userService.update(user);
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
    @RequestMapping(value = "/useredit", method = RequestMethod.GET)
    public ModelAndView useredit(String id){
        ModelAndView mv = new ModelAndView();
        User user=null;
        if(id!=null&&!"".equals(id)){
            user=userService.getUserById(id);
        }
        if(user==null){
            user=new User();
            user.setValid(1);
            user.setIsactived(0);
            user.setAgencyid("zb");
            user.setBrand("");
        }
        List<Map<String, Object>> act=null;
            act = actService.selectActList();
        if(user.getClaimpro()==null){
            user.setClaimpro("");
        }
        mv.addObject("user", user);
        mv.addObject("act", act);
        mv.addObject("id", id==null?"":id);
        if(!"zb".equals(user.getAgencyid())){
            mv.addObject("isagency", 1);
        }else{
            mv.addObject("isagency", 0);
        }
        mv.addObject("domainenter", domainenter);
        mv.addObject("agencyid", user.getAgencyid());
        mv.addObject("agencyname",agencyService.selectNameByKey(user.getAgencyid()));
        List<Map<String,Object>> insurance=insuranceService.select(1);
        mv.addObject("insurance", insurance);
        List<Map<String,Object>> agency=agencyService.select(1);
        mv.addObject("agency", agency);
        List<Map<String,Object>> brand=carService.selectBrand(1);
        mv.addObject("brand", brand);
        return mv;
    }
    @RequestMapping(value = "/agencyuseredit", method = RequestMethod.GET)
    public ModelAndView agencyuseredit(String id){
        ModelAndView mv = new ModelAndView();
        User user=null;
        if(id!=null&&!"".equals(id)){
            user=userService.getUserById(id);
        }
        if(user==null){
            user=new User();
            user.setValid(1);
            user.setIsactived(0);
            user.setAgencyid("zb");
            user.setBrand("");
        }
        List<Map<String, Object>> act=null;
        act = actService.selectActList();

        mv.addObject("user", user);
        mv.addObject("act", act);
        mv.addObject("id", id==null?"":id);
        if(!"zb".equals(user.getAgencyid())){
            mv.addObject("isagency", 1);
        }else{
            mv.addObject("isagency", 0);
        }
        mv.addObject("agencyid", user.getAgencyid());
        mv.addObject("agencyname",agencyService.selectNameByKey(user.getAgencyid()));
        return mv;
    }
    @RequestMapping(value = "/usereditread", method = RequestMethod.POST)
    public String usereditread(String data) {
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        JsonObject data2=jsonObject.get("data").getAsJsonObject();
        String id=data2.keySet().contains("id")? data2.get("id").getAsString():"";
        User user=null;
        if(id!=null&&!"".equals(id)){
            user=userService.getUserById(id);
        }
        return  JsonSerializer(user);
    }
        @RequestMapping(value = "/useredit", method = RequestMethod.POST)
    public String usereditsave(String username,String truename,String password,String tel,String email,String act,String valid,String claimpro){

        String action=Params("action");
        String result="";
        if(!action.isEmpty()){
            switch (action){
                case "brand":
                    String brand2=Params("brand");
                    List<Map<String,Object>> agency=agencyService.selectlist("","",brand2,1);
                    result=JsonSerializer(ConvertUtil.MapConvert(agency,Arrays.asList("dealerno","dealername","brand")));
                    break;
                case "save":
                    try {
                        String id=Params("id");
                        User user =null;
                        if(!id.isEmpty()) {
                            user = userService.getUserById(id);
                        }
                        boolean isadd = false;
                        String salt = "";
                        if (user == null) {
                            user = new User();
                            user.setEmail(email);
                            user.setUsername(email);
                            user.setAgencyid(Params("agencyid1"));
                            user.setCreateddate(new Date());
                            user.setIslock(0);
                            user.setErrorcount(0);
                            user.setIsactived(0);
                            isadd = true;
                        }
                        if(!"".equals(Params("agencyid1"))){
                            user.setAgencyid(Params("agencyid1"));
                        }
                        if("".equals(user.getAgencyid())){
                            user.setAgencyid("zb");
                        }
                        user.setTruename(truename);
                        user.setTel(tel);
                        user.setEmail(email);
                        if(!"".equals(act)) {
                            user.setAct(act);
                        }
                        if(user.getAct().contains("保险公司")){
                        //if("保险公司".equals(user.getAct())){
                            user.setAgencyid(Params("icode"));
                            user.setClaimpro(claimpro);
                            user.setIsapproval(ParamsInt("isapproval",0));

                        }
                        if("经销商".equals(user.getAct())){
                            String brand=Params("brand");
                            if(brand.equals("")) {
                                 brand = agencyService.selectByDealerNo(user.getAgencyid()).get("brand").toString();
                            }
                            user.setBrand(brand);
                        }else{
                            if(user.getAct().indexOf("摩托车")>-1) {
                                user.setBrand("Motor");
                            }else {
                                user.setBrand("");
                            }
                        }
                        //user.setIsactived(Integer.parseInt(isactived));
                        if (isadd) {
                            if(userService.exitsUserName(user.getUsername())>0){
                                result = JsonSerializer(new Object[]{false, "已存在该用户名"});
                            }else {
                                salt = GuidUtil.newSalt();
                                String decodepwd=Cryptography.base64Decoder(password);
                                user.setPassword(Cryptography.getSHA256(decodepwd + salt));
                                    if(user.getAgencyid().isEmpty()) {
                                    user.setAgencyid("zb");
                                }
                                user.setId(GuidUtil.newGuid());
                                user.setValid(0);
                                user.setConditions(0);
                                userService.insert(user);
                                userService.setSalt(user.getId(), salt);

                                //发送邮件
                                String rid= GuidUtil.newGuid();
                                userResetService.insert(rid,user.getId(),user.getUsername(),user.getEmail());
                                String path="reset";
                                String title="Specialty Line Pack";
                                String urls= GetPathurl();
                                String urls2= GetRequesturl();
                                if("1".equals(domainenter)){
                                    path="reset2";
                                    title="Specialty Line Pack";
                                }else if("2".equals(domainenter)){
                                    path="reset3";
                                    title="Specialty Line Pack";
                                }else if("3".equals(domainenter)){
//                                    path="reset4";
//                                    title="Mazda Line Pack";


                                    if(user.getBrand().equals("Mazda")){
                                        path = "reset4";
                                        title = "Mazda Line Pack";
                                    }else{
                                        if(GetRequesturl().indexOf("hq.autofu.cn")>-1){
                                            path = "reset5";
                                            title = "HQ Line Pack";
                                        }
                                        if(GetRequesturl().indexOf("bt.autofu.cn")>-1){
                                            path = "reset6";
                                            title = "Line Pack";
                                        }
                                        if (GetPathurl().indexOf("volkswagen") > -1) {
                                            path = "reset7";
                                            title = "Line Pack";
                                        }
                                    }
                                }
                                //String urls= request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
                                String content=("您已申请账号，请点击后方链接激活该账号,<a href='{url}login/"+path+"?id={id}' target='_blank'>设置密码</a>。").replace("{url}",urls).replace("{id}",rid);
                                SendMailUtil.sendMail(user.getEmail(), title+"账号开通", content, "");
                                result = JsonSerializer(new Object[]{true, ""});
                            }
                        } else {
//                            if (!password.equals(user.getPassword())) {
//                                user.setPassword(Cryptography.getSHA256(password + userService.getSalt(user.getId())));
//                            }
                            user.setValid(Integer.parseInt(valid));
                            userService.update(user);
                            result = JsonSerializer(new Object[]{true, ""});
                        }

                    }
                    catch (Exception ex){
                        result = JsonSerializer(new Object[]{false, ex.getMessage()});
                    }
                    break;
            }
        }
        return result;
    }

    @RequestMapping(value = "/useredit2", method = RequestMethod.POST)
    public String usereditsave2(String data){
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        JsonObject data2=jsonObject.get("data").getAsJsonObject();
        String username=data2.keySet().contains("username")? data2.get("username").getAsString():"";
        String truename=data2.keySet().contains("truename")? data2.get("truename").getAsString():"";
        String password=data2.keySet().contains("password")? data2.get("password").getAsString():"";
        String tel=data2.keySet().contains("tel")? data2.get("tel").getAsString():"";
        String email=data2.keySet().contains("email")? data2.get("email").getAsString():"";
        String act=data2.keySet().contains("act")? data2.get("act").getAsString():"";
        String valid=data2.keySet().contains("valid")? data2.get("valid").getAsString():"";
        String action=data2.keySet().contains("action")? data2.get("action").getAsString():"" ;
        String agencyid1=data2.keySet().contains("agencyid1")? data2.get("agencyid1").getAsString():"" ;
        String icode=data2.keySet().contains("icode")? data2.get("icode").getAsString():"" ;
        String uid=data2.keySet().contains("uid")? data2.get("uid").getAsString():"";
        String claimpro=data2.keySet().contains("claimpro")? data2.get("claimpro").getAsString():"" ;
        int isapproval=data2.keySet().contains("isapproval")? data2.get("isapproval").getAsInt():0 ;
        String result="";
        if(!action.isEmpty()){
            switch (action){
                case "save":
                    try {
                        String id=data2.keySet().contains("id")? data2.get("id").getAsString():"";
                        User user =null;
                        if(!id.isEmpty()) {
                            user = userService.getUserById(id);
                        }
                        boolean isadd = false;
                        String salt = "";
                        if (user == null) {
                            user = new User();
                            user.setEmail(email);
                            user.setUsername(email);
                            //user.setAgencyid(agencyid1);
                            user.setCreateddate(new Date());
                            user.setIslock(0);
                            user.setErrorcount(0);
                            user.setIsactived(0);

                            User user2=userService.selectByPrimaryKey(uid);
                            Map<String,Object> agency= agencyService.selectByDealerNo(user2.getAgencyid());
                            if(agency!=null) {
                                user.setBrand(agency.get("brand").toString());
                                user.setAgencyid(user2.getAgencyid());
                            }
                            user.setAct("经销商");
                            isadd = true;
                        }
                        if(!"".equals(agencyid1)){
                            user.setAgencyid(agencyid1);
                        }
                        if("".equals(user.getAgencyid())){
                            user.setAgencyid("zb");
                        }
                        user.setTruename(truename);
                        user.setTel(tel);
                        user.setEmail(email);
                        if(!"".equals(act)) {
                            user.setAct(act);
                        }
                        if("保险公司".equals(user.getAct())){
                            user.setAgencyid(icode);
                            user.setClaimpro(claimpro);
                            user.setIsapproval(isapproval);
                        }
//                        if("经销商".equals(user.getAct())){
//                            String brand=agencyService.selectByDealerNo(user.getAgencyid()).get("brand").toString();
//                            user.setBrand(brand);
//                        }else{
//                            user.setBrand("");
//                        }
                        //user.setIsactived(Integer.parseInt(isactived));
                        if (isadd) {
                            if(userService.exitsUserName(user.getUsername())>0){
                                result = JsonSerializer(new Object[]{false, "已存在该用户名"});
                            }else {
                                salt = GuidUtil.newSalt();
                                String decodepwd=Cryptography.base64Decoder(password);
                                user.setPassword(Cryptography.getSHA256(decodepwd + salt));
                                if(user.getAgencyid().isEmpty()) {
                                    user.setAgencyid("zb");
                                }
                                user.setId(GuidUtil.newGuid());
                                user.setValid(0);
                                user.setConditions(0);
                                userService.insert(user);
                                userService.setSalt(user.getId(), salt);

                                //发送邮件
                                String rid= GuidUtil.newGuid();
                                userResetService.insert(rid,user.getId(),user.getUsername(),user.getEmail());
                                String path="reset";
                                String title="Specialty Line Pack";
                                if("1".equals(domainenter)){
                                    path="reset2";
                                    title="Specialty Line Pack";
                                }else if("2".equals(domainenter)){
                                    path="reset3";
                                    title="Specialty Line Pack";
                                }else if("3".equals(domainenter)){
//                                    path="reset4";
//                                    title="Mazda Line Pack";

                                    if(user.getBrand().equals("Mazda")){
                                        path = "reset4";
                                        title = "Mazda Line Pack";
                                    }else{
                                        if(GetRequesturl().indexOf("hq.autofu.cn")>-1){
                                            path = "reset5";
                                            title = "HQ Line Pack";
                                        }
                                        if(GetRequesturl().indexOf("bt.autofu.cn")>-1){
                                            path = "reset6";
                                            title = "Line Pack";
                                        }
                                        if (GetPathurl().indexOf("volkswagen") > -1) {
                                            path = "reset7";
                                            title = "Line Pack";
                                        }
                                    }
                                }
                                String urls= GetPathurl();
                                //String urls= request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
                                String content=("您已申请账号，请点击后方链接激活该账号,<a href='{url}login/"+path+"?id={id}' target='_blank'>设置密码</a>。").replace("{url}",urls).replace("{id}",rid);
                                SendMailUtil.sendMail(user.getEmail(), title+"账号开通", content, "");
                                result = JsonSerializer(new Object[]{true, ""});
                            }
                        } else {
//                            if (!password.equals(user.getPassword())) {
//                                user.setPassword(Cryptography.getSHA256(password + userService.getSalt(user.getId())));
//                            }
                            user.setValid(Integer.parseInt(valid));
                            userService.update(user);
                            result = JsonSerializer(new Object[]{true, ""});
                        }

                    }
                    catch (Exception ex){
                        result = JsonSerializer(new Object[]{false, ex.getMessage()});
                    }
                    break;
            }
        }
        return result;
    }


    @RequestMapping(value = "/agencyuseredit", method = RequestMethod.POST)
    public String agencyusereditsave(String username,String truename,String password,String tel,String email,String valid){

        String action=Params("action");
        String result="";
        if(!action.isEmpty()){
            switch (action){
                case "save":
                    try {
                        String id=Params("id");
                        User user =null;
                        if(!id.isEmpty()) {
                            user = userService.getUserById(id);
                        }
                        boolean isadd = false;
                        String salt = "";
                        if (user == null) {
                            user = new User();
                            user.setEmail(email);
                            user.setUsername(email);
                            user.setAgencyid(UserInfo().getAgencyID());
                            Map<String,Object> agency= agencyService.selectByDealerNo(UserInfo().getAgencyID());

                            user.setBrand(agency.get("brand").toString());
                            user.setCreateddate(new Date());
                            user.setIslock(0);
                            user.setErrorcount(0);
                            user.setIsactived(0);
                            user.setAct("经销商");
                            isadd = true;
                        }
                        if("".equals(user.getAgencyid())){
                            user.setAgencyid("zb");
                        }
                        user.setTruename(truename);
                        user.setTel(tel);
                        user.setEmail(email);

                        //user.setIsactived(Integer.parseInt(isactived));
                        if (isadd) {
                            if(userService.exitsUserName(user.getUsername())>0){
                                result = JsonSerializer(new Object[]{false, "已存在该用户名"});
                            }else {
                                salt = GuidUtil.newSalt();
                                String decodepwd=Cryptography.base64Decoder(password);
                                user.setPassword(Cryptography.getSHA256(decodepwd + salt));
                                if(user.getAgencyid().isEmpty()) {
                                    user.setAgencyid("zb");
                                }
                                user.setId(GuidUtil.newGuid());
                                user.setValid(0);
                                user.setConditions(0);
                                userService.insert(user);
                                userService.setSalt(user.getId(), salt);

                                //发送邮件
                                String rid= GuidUtil.newGuid();
                                userResetService.insert(rid,user.getId(),user.getUsername(),user.getEmail());
                                String path="reset";
                                String title="Specialty Line Pack";
                                if("1".equals(domainenter)){
                                    path="reset2";
                                    title="Specialty Line Pack";
                                }else if("2".equals(domainenter)){
                                    path="reset3";
                                    title="Specialty Line Pack";
                                }else if("3".equals(domainenter)){
//                                    path="reset4";
//                                    title="Mazda Line Pack";

                                    if(user.getBrand().equals("Mazda")){
                                        path = "reset4";
                                        title = "Mazda Line Pack";
                                    }else{
                                        if(GetRequesturl().indexOf("hq.autofu.cn")>-1){
                                            path = "reset5";
                                            title = "HQ Line Pack";
                                        }
                                        if(GetRequesturl().indexOf("bt.autofu.cn")>-1){
                                            path = "reset6";
                                            title = "Line Pack";
                                        }
                                        if (GetPathurl().indexOf("volkswagen") > -1) {
                                            path = "reset7";
                                            title = "Line Pack";
                                        }
                                    }
                                }
                                String urls= GetPathurl();
                                //String urls= request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath()+"/";
                                String content=("您已申请账号，请点击后方链接激活该账号,<a href='{url}login/"+path+"?id={id}' target='_blank'>设置密码</a>。").replace("{url}",urls).replace("{id}",rid);
                                SendMailUtil.sendMail(user.getEmail(), title+"账号开通", content, "");
                                result = JsonSerializer(new Object[]{true, ""});
                            }
                        } else {
//                            if (!password.equals(user.getPassword())) {
//                                user.setPassword(Cryptography.getSHA256(password + userService.getSalt(user.getId())));
//                            }
                            user.setValid(Integer.parseInt(valid));
                            userService.update(user);
                            result = JsonSerializer(new Object[]{true, ""});
                        }

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
     * 获取省份
     * @return
     */
    @RequestMapping(value = "/province")
    public  String province(){
        List<Map<String,Object>> province=regionService.selectProvince();
        return JsonSerializer(province);
    }

    /**
     * 获取城市
     * @param provincecode
     * @return
     */
    @RequestMapping(value = "/city")
    public  String city(String provincecode){
        List<Map<String,Object>> city=regionService.selectCity(provincecode);
        return JsonSerializer(city);
    }

    @RequestMapping(value = "/uinfo", method = RequestMethod.GET)
    public  ModelAndView uinfo(){
        ModelAndView mv = new ModelAndView();
        User user=userService.getUserById(UserInfo().getUserID());
        mv.addObject("user", user);
        return mv;
    }
    @RequestMapping(value = "/uinfop", method = RequestMethod.GET)
    public  ModelAndView uinfop(){
        ModelAndView mv = new ModelAndView();
        User user=userService.getUserById(UserInfo().getUserID());
        mv.addObject("user", user);
        return mv;
    }

    @RequestMapping(value = "/uinfo", method = RequestMethod.POST)
    public String uinfosave(String truename,String tel,String email,String password,String newpassword){

        String action=Params("action");
        String result="";
        if(!action.isEmpty()){
            switch (action){
                case "save":
                    try {
                        User user =userService.getUserById(UserInfo().getUserID());
                        String salt = "";
                        user.setTruename(truename);
                        user.setTel(tel);
                        user.setEmail(email);
                        String decodepwd=Cryptography.base64Decoder(password);
                        String decodepwdnew=Cryptography.base64Decoder(newpassword);
                        if(!"".equals(decodepwd)){
                                salt = userService.getSalt(UserInfo().getUserID());
                                if(user.getPassword().equals( Cryptography.getSHA256(decodepwd + salt))){
                                    user.setPassword(Cryptography.getSHA256(decodepwdnew + salt));
                                    userService.update(user);
                                    String id= GuidUtil.newGuid();
                                    userResetService.insertState(id,user.getId(),user.getUsername(),user.getEmail(),1);
                                    result = JsonSerializer(new Object[]{true, ""});
                                }else{
                                    result = JsonSerializer(new Object[]{false, "原密码错误"});
                                }

                        }else {
                            userService.update(user);
                            result = JsonSerializer(new Object[]{true, ""});
                        }

                    }
                    catch (Exception ex){
                        result = JsonSerializer(new Object[]{false, ex.getMessage()});
                    }
                    break;
            }
        }
        return result;
    }

    @RequestMapping(value = "/uinfop", method = RequestMethod.POST)
    public String uinfopsave(String truename,String tel,String email,String password,String newpassword){

        String action=Params("action");
        String result="";
        if(!action.isEmpty()){
            switch (action){
                case "save":
                    try {
                        User user =userService.getUserById(UserInfo().getUserID());
                        String salt = "";
                        user.setTruename(truename);
                        user.setTel(tel);
                        user.setEmail(email);
                        String decodepwd=Cryptography.base64Decoder(password);
                        String decodepwdnew=Cryptography.base64Decoder(newpassword);
                        if(!"".equals(decodepwd)){
                            salt = userService.getSalt(UserInfo().getUserID());
                            if(user.getPassword().equals( Cryptography.getSHA256(decodepwd + salt))){
                                user.setPassword(Cryptography.getSHA256(decodepwdnew + salt));
                                userService.update(user);
                                String id= GuidUtil.newGuid();
                                userResetService.insertState(id,user.getId(),user.getUsername(),user.getEmail(),1);
                                result = JsonSerializer(new Object[]{true, ""});
                            }else{
                                result = JsonSerializer(new Object[]{false, "原密码错误"});
                            }

                        }else {
                            userService.update(user);
                            result = JsonSerializer(new Object[]{true, ""});
                        }

                    }
                    catch (Exception ex){
                        result = JsonSerializer(new Object[]{false, ex.getMessage()});
                    }
                    break;
            }
        }
        return result;
    }

    @RequestMapping(value = "/agencyinfo", method = RequestMethod.GET)
    public  ModelAndView agencyinfo(){
        ModelAndView mv = new ModelAndView();
        Map<String,Object> agency=agencyService.selectByDealerNo(UserInfo().getAgencyID());//UserInfo().getAgencyID()
        mv.addObject("info", agency);
        return mv;
    }
    @RequestMapping(value = "/agencyinfop", method = RequestMethod.GET)
    public  ModelAndView agencyinfop(){
        ModelAndView mv = new ModelAndView();
        Map<String,Object> agency=agencyService.selectByDealerNo(UserInfo().getAgencyID());//UserInfo().getAgencyID()
        mv.addObject("info", agency);
        return mv;
    }
    @RequestMapping(value = "/agencyinfo", method = RequestMethod.POST)
    public String agencyinfosave(){
        String action=Params("action");
        String result="";
        if(!action.isEmpty()){
            switch (action){
                case "save":
                    try {
                        String id=Params("tid");//
                        Map<String,Object> map=new HashMap<String,Object>();
                        map.put("brand",Params("brand"));
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
                        if(!"".equals(Params("is_elec_invoice"))) {
                            map.put("is_elec_invoice", ParamsInt("is_elec_invoice"));
                        }
                        map.put("linkman",Params("linkman"));
                        map.put("linkmantel",Params("linkmantel"));
                        map.put("groupname",Params("groupname"));
                        map.put("zone",Params("zone"));
                        map.put("province",Params("province"));
                        map.put("city",Params("city"));
                        map.put("pcname",Params("pcname"));

                            agencyService.updateByKey(id, map);

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
    @RequestMapping(value = "/agencyinfop", method = RequestMethod.POST)
    public String agencyinfopsave(){
        String action=Params("action");
        String result="";
        if(!action.isEmpty()){
            switch (action){
                case "save":
                    try {
                        String id=Params("tid");//
                        Map<String,Object> map=new HashMap<String,Object>();
                        map.put("brand",Params("brand"));
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
                        if(!"".equals(Params("is_elec_invoice"))) {
                            map.put("is_elec_invoice", ParamsInt("is_elec_invoice"));
                        }
                        map.put("linkman",Params("linkman"));
                        map.put("linkmantel",Params("linkmantel"));
                        map.put("groupname",Params("groupname"));
                        map.put("zone",Params("zone"));
                        map.put("province",Params("province"));
                        map.put("city",Params("city"));
                        map.put("pcname",Params("pcname"));

                        agencyService.updateByKey(id, map);

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

    @RequestMapping(value = "/carlist", method = RequestMethod.GET)
    public  ModelAndView carlist() {
        ModelAndView mv = new ModelAndView();
        List<Map<String, Object>> brands = carService.selectSaleBrand(1);
        mv.addObject("brands", brands);
        return mv;
    }
    @RequestMapping(value = "/carlist", method = RequestMethod.POST)
    public  String carlistpost(){
        String action=Params("action");
        String result="";
        if(!action.isEmpty()){
            switch (action){
                case "GetData":
                    int pageIndex = ParamsInt("curPageIndex",1);
                    int pageSize = ParamsInt("pageSize",10);
                    String keybrand=Params("keybrand");
                    int keyvalid=ParamsInt("keyvalid");
                    PageInfo<Map<String, Object>> page = carService.selectCarpage(pageIndex, pageSize,keybrand,keyvalid);
                    result=JsonSerializer(page);
                    break;
                case "del":
                    try {
                        carService.deleteCarByKey(Params("id"));
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
    @RequestMapping(value = "/caredit", method = RequestMethod.GET)
    public ModelAndView caredit(String id){
        ModelAndView mv = new ModelAndView();
        Map<String,Object> car=new HashMap<String,Object>();
        if(id!=null&&!"".equals(id)){
            car=carService.selectCarByKey(id);
        }else{
            car.put("tid","");
            car.put("cars","");
            car.put("brand","");
            car.put("valid",1);
        }

        List<Map<String, Object>> brands = carService.selectSaleBrand(1);
        mv.addObject("brands", brands);
        mv.addObject("info", car);
        return mv;
    }
    @RequestMapping(value = "/caredit", method = RequestMethod.POST)
    public String careditsave(String brand,String cars,String valid){

        String action=Params("action");
        String result="";
        if(!action.isEmpty()){
            switch (action){
                case "save":
                    try {
                        String id=Params("tid");
                        if("".equals(id)){
                            carService.insertCar(brand,cars, Integer.parseInt(valid));
                        }else{
                            carService.updateCarByKey(id,brand,cars, Integer.parseInt(valid));
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
    @RequestMapping(value = "/vehiclelist", method = RequestMethod.GET)
    public  ModelAndView vehiclelist() {
        ModelAndView mv = new ModelAndView();
        List<Map<String, Object>> brands = carService.selectSaleBrand(1);
        mv.addObject("brands", brands);
        return mv;
    }

    /**
     * 查询车系接口
     * @return
     */
    @RequestMapping(value = "/cars")
    public  String cars() {
        String result = "";
        String brand=Params("brand");
        List<Map<String, Object>> car=carService.selectCar(brand,1);
        List<String> list=new ArrayList<String>();
        for (Map<String ,Object> map:car) {
            list.add(map.get("cars").toString());
        }
        result = JsonSerializer(list);
        return result;
    }
    @RequestMapping(value = "/vehiclelist", method = RequestMethod.POST)
    public  String vehiclelistpost(){
        String action=Params("action");
        String result="";
        if(!action.isEmpty()){
            switch (action){
                case "GetData":
                    int pageIndex = ParamsInt("curPageIndex",1);
                    int pageSize = ParamsInt("pageSize",10);
                    String keybrand=Params("keybrand");
                    String keycars=Params("keycars");
                    int keyvalid=ParamsInt("keyvalid");
                    PageInfo<Map<String, Object>> page = carService.selectVehiclepage(pageIndex, pageSize,keybrand,keycars,keyvalid);
                    result=JsonSerializer(page);
                    break;
                case "del":
                    try {
                        carService.deleteVehicleByKey(Params("id"));
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
    @RequestMapping(value = "/vehicleedit", method = RequestMethod.GET)
    public ModelAndView vehicleedit(String id){
        ModelAndView mv = new ModelAndView();
        Map<String,Object> vehicle=new HashMap<String,Object>();
        if(id!=null&&!"".equals(id)){
            vehicle=carService.selectVehicleByKey(id);
        }else{
            vehicle.put("tid","");
            vehicle.put("cars","");
            vehicle.put("brand","");
            vehicle.put("vehicletype","");
            vehicle.put("valid",1);
        }

        List<Map<String, Object>> brands = carService.selectSaleBrand(1);
        mv.addObject("brands", brands);
        mv.addObject("info", vehicle);
        return mv;
    }
    @RequestMapping(value = "/vehicleedit", method = RequestMethod.POST)
    public String vehicleeditsave(String brand,String cars,String  vehicletype,String valid){

        String action=Params("action");
        String result="";
        if(!action.isEmpty()){
            switch (action){
                case "save":
                    try {
                        String id=Params("tid");
                        if("".equals(id)){
                            carService.insertVehicle(brand,cars, vehicletype,Integer.parseInt(valid));
                        }else{
                            carService.updateVehicleByKey(id,brand,cars,vehicletype, Integer.parseInt(valid));
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
    @RequestMapping(value = "/productlist", method = RequestMethod.GET)
    public  ModelAndView productlist() {
        ModelAndView mv = new ModelAndView();
        return mv;
    }
    @RequestMapping(value = "/productlist", method = RequestMethod.POST)
    public  String productlistpost(){
        String action=Params("action");
        String result="";
        if(!action.isEmpty()){
            switch (action){
                case "GetData":
                    int pageIndex = ParamsInt("curPageIndex",1);
                    int pageSize = ParamsInt("pageSize",10);
                    String keyicode=Params("keyicode");
                    String keypname=Params("keypname");
                    int keyvalid=ParamsInt("keyvalid");
                    PageInfo<Map<String, Object>> page = productService.selectpage(pageIndex, pageSize,keyicode,keypname,keyvalid);
                    result=JsonSerializer(page);
                    break;
                case "del":
                    try {
                        productService.deleteVirtualByKey(Params("id"));
                        result = JsonSerializer(new Object[]{true, ""});
                    }
                    catch (Exception ex) {
                        result = JsonSerializer(new Object[]{false, ex.getMessage()});
                    }
                    break;
            }
        }
        return result;
    }@RequestMapping(value = "/productedit", method = RequestMethod.GET)
    public ModelAndView productedit(String id){
        ModelAndView mv = new ModelAndView();
        Map<String,Object> product=new HashMap<String,Object>();
        if(id!=null&&!"".equals(id)){
            product=productService.selectByKey(id);
        }else{
            id="";
            product.put("productid","");
            product.put("icode","");
            product.put("pname","");
            product.put("retailprice","");
            product.put("agentprice","");
            product.put("cost","");
            product.put("valid",1);
            product.put("brand","");
            product.put("groupname","");
            product.put("retaildiscount","");
            product.put("agentdiscount","");
            product.put("carcostrate","");
            product.put("disc","");
            product.put("tpa","");
        }

        mv.addObject("info", product);
        mv.addObject("id", id);
        List<Map<String,Object>> insurance=insuranceService.select(1);
        mv.addObject("insurance", insurance);
        return mv;
    }
    @RequestMapping(value = "/productedit", method = RequestMethod.POST)
    public String producteditsave(String icode,String pname,String  retailprice,String agentprice,String cost,String brand,String retaildiscount,String agentdiscount,String carcostrate,String tpa,String disc,String groupname){

        String action=Params("action");
        String result="";
        if(!action.isEmpty()){
            switch (action){
                case "save":
                    try {
                        String id=Params("id");
                        if("".equals(id)){
                            productService.insert(icode,pname,ParseFloat(retailprice),ParseFloat(agentprice), ParseFloat(cost),brand,ParseFloat(retaildiscount),ParseFloat(agentdiscount),ParseFloat(carcostrate),ParseFloat(tpa),disc,groupname);
                        }else{
                            productService.updateByKey(id,icode,pname,ParseFloat(retailprice),ParseFloat(agentprice), ParseFloat(cost),brand,ParseFloat(retaildiscount),ParseFloat(agentdiscount),ParseFloat(carcostrate),ParseFloat(tpa),disc,groupname);
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
    @RequestMapping(value = "/invoiceinfo", method = RequestMethod.GET)
    public  ModelAndView invoiceinfo(){
        ModelAndView mv = new ModelAndView();
        Map<String,Object> invoice=invoiceInfoService.select();
        mv.addObject("info", invoice);
        return mv;
    }
    @RequestMapping(value = "/invoiceinfo", method = RequestMethod.POST)
    public String invoiceinfosave(){
        String action=Params("action");
        String result="";
        if(!action.isEmpty()){
            switch (action){
                case "save":
                    try {
                        Map<String,Object> map=new HashMap<String,Object>();
                        map.put("taxpayerNum",Params("taxpayerNum"));
                        map.put("legalPersonName",Params("legalPersonName"));
                        map.put("contactsName",Params("contactsName"));
                        map.put("enterpriseName",Params("enterpriseName"));
                        map.put("contactsEmail",Params("contactsEmail"));
                        map.put("contactsPhone",Params("contactsPhone"));
                        map.put("regionCode",Params("regionCode"));
                        map.put("cityName",Params("cityName"));
                        map.put("enterpriseAddress",Params("enterpriseAddress"));
                        map.put("taxRegistrationCertificate",Params("taxRegistrationCertificate"));
                        map.put("privateKey",Params("privateKey"));
                        map.put("ptPublicKey",Params("ptPublicKey"));
                        map.put("password",Params("password"));
                        map.put("platform_alias",Params("platform_alias"));
                        map.put("platform_code",Params("platform_code"));
                        map.put("pathurl",Params("pathurl"));
                        map.put("taxClassificationCode",Params("taxClassificationCode"));
                        map.put("goodsName",Params("goodsName"));
                        map.put("drawername",Params("drawername"));
                        map.put("taxRateValue",Params("taxRateValue"));

                        invoiceInfoService.save(map);

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
