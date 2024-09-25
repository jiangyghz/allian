package com.bond.allianz.controller;

import com.bond.allianz.Dao.logs;
import com.bond.allianz.entity.JsonResult;
import com.bond.allianz.entity.User;
import com.bond.allianz.entity.UserInfo;
import com.bond.allianz.service.*;
import com.bond.allianz.utils.Cryptography;
import com.bond.allianz.utils.GuidUtil;
import com.bond.allianz.utils.ImageVerificationCode;
import com.bond.allianz.utils.SendMailUtil;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import java.awt.image.BufferedImage;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * RestController =Controller +ResponseBody
 *
 */
@RestController
@RequestMapping("/login")
public class LoginController extends BaseController {

    @Value("${domain.enter}")
    public String domainenter;

    @Autowired
    private UserService userService;
    @Autowired
    private AgencyService agencyService;
    @Autowired
    private UserLogService userLogService;
    @Autowired
    private ActService actService;
    @Autowired
    private UserResetService userResetService;

    @RequestMapping(value = "/login",method = RequestMethod.GET )
    public ModelAndView  login(){
        ModelAndView mv = new ModelAndView();
//        String root=GetPathurl();
//        if(root.indexOf("localhost")==-1&&root.indexOf("127.0.0.1")==-1) {
//            request.getServletContext().setAttribute("root", root);
//        }
        return mv;
    }

    @RequestMapping(value = "/index",method = RequestMethod.GET )
    public ModelAndView  index(){
        ModelAndView mv = new ModelAndView();
//        String root=GetPathurl();
//        if(root.indexOf("localhost")==-1&&root.indexOf("127.0.0.1")==-1) {
//            request.getServletContext().setAttribute("root", root);
//        }
        return mv;
    }
    @RequestMapping(value = "/index3",method = RequestMethod.GET )
    public ModelAndView  index3(){
        ModelAndView mv = new ModelAndView();
//        String root=GetPathurl();
//        if(root.indexOf("localhost")==-1&&root.indexOf("127.0.0.1")==-1) {
//            request.getServletContext().setAttribute("root", root);
//        }
        return mv;
    }
    @RequestMapping(value = "/index4",method = RequestMethod.GET )
    public ModelAndView  index4() {
        ModelAndView mv = new ModelAndView();
        String r = GetRequesturl();
        if (r.indexOf("hq.autofu.cn") > -1) {
            ModelAndView mv2 = new ModelAndView("index5");
            return mv2;
        }
        if (r.indexOf("bt.autofu.cn") > -1) {
            ModelAndView mv2 = new ModelAndView("index6");
            return mv2;
        }
        if (r.indexOf("volkswagen") > -1) {
            ModelAndView mv2 = new ModelAndView("index7");
            return mv2;
        }
//        String root=GetPathurl();
//        if(root.indexOf("localhost")==-1&&root.indexOf("127.0.0.1")==-1) {
//            request.getServletContext().setAttribute("root", root);
//        }
        return mv;
    }
    @RequestMapping(value = "/index5",method = RequestMethod.GET )
    public ModelAndView  index5(){
        ModelAndView mv = new ModelAndView();
//        String root=GetPathurl();
//        if(root.indexOf("localhost")==-1&&root.indexOf("127.0.0.1")==-1) {
//            request.getServletContext().setAttribute("root", root);
//        }
        return mv;
    }
    @RequestMapping(value = "/index6",method = RequestMethod.GET )
    public ModelAndView  index6(){
        ModelAndView mv = new ModelAndView();
//        String root=GetPathurl();
//        if(root.indexOf("localhost")==-1&&root.indexOf("127.0.0.1")==-1) {
//            request.getServletContext().setAttribute("root", root);
//        }
        return mv;
    }
    @RequestMapping(value = "/index7",method = RequestMethod.GET )
    public ModelAndView  index7(){
        ModelAndView mv = new ModelAndView();
//        String root=GetPathurl();
//        if(root.indexOf("localhost")==-1&&root.indexOf("127.0.0.1")==-1) {
//            request.getServletContext().setAttribute("root", root);
//        }
        return mv;
    }
    @RequestMapping(value = "/index8",method = RequestMethod.GET )
    public ModelAndView  index8(){
        ModelAndView mv = new ModelAndView();
//        String root=GetPathurl();
//        if(root.indexOf("localhost")==-1&&root.indexOf("127.0.0.1")==-1) {
//            request.getServletContext().setAttribute("root", root);
//        }
        return mv;
    }

    /**
     * 图片验证码
     */
    @RequestMapping(value = "/getverificode", method = RequestMethod.GET)
    public  void getVerifiCode(){
        try {
            ImageVerificationCode ivc = new ImageVerificationCode();     //用我们的验证码类，生成验证码类对象
            BufferedImage image = ivc.getImage();  //获取验证码
            String code=ivc.getText().toLowerCase();
            //code=Cryptography.base64Encoder(code);
            code=Cryptography.getMd5(code);
            Cookie cookie = new Cookie("alivcode", code);
            //设置Cookie的有效期为1小时  单位秒x
            cookie.setMaxAge(60 * 60);
            cookie.setPath("/");
//            if(request.getRequestURL().indexOf("localhost")==-1) {
//                cookie.setDomain("91jch.com");
//            }
            response.addCookie(cookie);
            ivc.output(image, response.getOutputStream());//将验证码图片响应给客户端
        }
        catch(Exception ex){

        }
    }
    /**
     * 登录
     * @param username
     * @param password
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "/login",method = RequestMethod.POST )
    public JsonResult login (String username, String password) throws Exception {
        JsonResult result=new JsonResult();
        User user=userService.selectByLoginName(username);
        try {
            if (user != null) {
                int isold = 0;
                String pwd = password;
                Boolean check = false;

                String decodepwd = Cryptography.base64Decoder(password);
                String vcode=Params("vcode");
                if(vcode.equals("")){
                    result.code = 0;
                    result.msg = "验证码不能为空";
                    return result;
                }
                if(!vcode.equals("")){
                    String cookiecode = getCookie("alivcode");
                    vcode = Cryptography.getMd5(vcode.toLowerCase());
                    if(!vcode.equals(cookiecode)){
                        result.code = 0;
                        result.msg = "验证码错误";
                        return result;
                    }
                }
                password = Cryptography.getSHA256(decodepwd + userService.getSalt(user.getId()));
                check = password.equals(user.getPassword());

                Boolean unlock = true;
                //锁定或者超过24小时
                unlock = user.getIslock() == 0 || (user.getIslock() == 1 && (user.getErrortime().before(DateUtils.addHours(new Date(), -24))));
                if (unlock) {
                    if (check) {
                        if (user.getValid().equals(1)) {
                            JsonObject json = new JsonObject();
                            json.addProperty("id", user.getId());
                            json.addProperty("username", user.getUsername());
                            json.addProperty("agencyid", user.getAgencyid());
                            json.addProperty("truename", user.getTruename());
                            json.addProperty("tel", user.getTel());
                            json.addProperty("act", user.getAct());
                            String brand="";
                            if(user.getBrand()!=null&&!user.getBrand().equals("")){
                                brand=user.getBrand();
                            }else{

                                brand=domainenter.equals("0")?"Porsche":"";
                            }
                            json.addProperty("brand",brand);
                            int issellkey=0;
                            int isselltire=0;
                            int notirepic=0;
                            if(!user.getAgencyid().toLowerCase().equals("zb")) {
                                if (agencyService.existsByCode(user.getAgencyid()) > 0) {
                                    Map<String, Object> ag = agencyService.selectByDealerNo(user.getAgencyid());
                                    if (ag.containsKey("issellkey")) {
                                        issellkey = Integer.parseInt(ag.get("issellkey").toString());
                                    }
                                    if (ag.containsKey("isselltire")) {
                                        isselltire = Integer.parseInt(ag.get("isselltire").toString());
                                    }
                                    if (ag.containsKey("notirepic")) {
                                        notirepic = Integer.parseInt(ag.get("notirepic").toString());
                                    }
                                }
                            }
                            json.addProperty("issellkey",issellkey);
                            json.addProperty("isselltire",isselltire);
                            json.addProperty("notirepic",notirepic);
                            json.addProperty("type", user.getAgencyid().toLowerCase().equals("zb") ? 0 : 1);//0 总部，1 经销商
                            json.addProperty("domainenter", domainenter);

                            //json.addProperty("claimpro", user.getClaimpro());
                            json.addProperty("isapproval", user.getIsapproval());
                            //json.addProperty("store", user.getStore());//门店代码
                            Cookie cookie = new Cookie("allianz", Cryptography.AESEncrypt(URLEncoder.encode(JsonSerializer(json), "utf-8")));
                            //设置Cookie的有效期为1天  单位秒
                            cookie.setMaxAge(24 * 60 * 60);
                            cookie.setPath("/");
                            response.addCookie(cookie);
                            //request.getSession().setAttribute("user", Cryptography.base64Encoder(URLEncoder.encode(JsonSerializer(json), "utf-8")));
//                        String val="id="+ user.getUserid();
//                        val+="&username="+URLEncoder.encode(user.getUsername());
//                        val+="&actname="+URLEncoder.encode(user.getAct());
//                        val+="&truename="+URLEncoder.encode(user.getTruename());
//                        Cookie cookiedms=new Cookie("hksdms",val);
//                        cookiedms.setMaxAge(24 * 60 * 60);
//                        cookiedms.setPath("/");
//                        cookiedms.setDomain("91jch.com");
//                        response.addCookie(cookiedms);
                            Date d = new Date();
//                        if (isold == 1) {
//                            user.setPassword(Cryptography.getSHA256(pwd + userService.getSalt(user.getTid())));
//                            user.setIs_pwdchange(1);
//                        }
//                        user.setLasttime(Integer.parseInt(String.valueOf(d.getTime() / 1000)));
//                        user.setLasttime2(d);
                            user.setErrorcount(0);
                            user.setIslock(0);
                            if (user.getConditions() == null) {
                                user.setConditions(0);
                            }
                            userService.update(user);

                            userLogService.insertLog(user.getAgencyid().toLowerCase(), user.getId(), user.getUsername(), GetIpAddress());
                            String returnurl =("0".equals(domainenter)?"mainadminp":("1".equals(domainenter)?"mainadmin":"mainadminb"));
                            int code = 1;
                            Date lastchange = userResetService.selectLastDateByUserID(user.getId());
                            if (lastchange == null) {
                                lastchange = user.getCreateddate();
                            }
                            //密码过期90天  90*24*60*60*1000
                            long ttime = lastchange.getTime();
                            ttime += 90 * 24 * 60 * 60 * (long) 1000;
                            if (new Date(ttime).before(new Date())) {
                                String ext="login/change4";
                                if(GetRequesturl().indexOf("hq.autofu.cn")>-1){
                                    ext="login/change5";
                                }
                                if(GetRequesturl().indexOf("bt.autofu.cn")>-1){
                                    ext="login/change6";
                                }
                                if(GetPathurl().indexOf("volkswagen")>-1){
                                    ext="login/change7";
                                }
                                returnurl = "0".equals(domainenter) ? "login/change" : ("1".equals(domainenter) ? "login/change2" : ("2".equals(domainenter) ?"login/change3": (ext)));
                                code = 2;
                            }
                            result.code = code;
                            //result.msg = user.getAgencyid().toLowerCase().equals("zb") ? "mainadmin" : "mainform";
                            result.msg = returnurl;
                            result.data = user.getConditions() == null ? 0 : user.getConditions();

                        } else {
                            result.code = 0;
                            result.msg = "用户已禁用";
                        }
                    } else {
                        if (user.getErrorcount() == null) {
                            user.setErrorcount(0);
                        }
                        if (user.getErrorcount() >= 4) { //记录错误次数
                            user.setIslock(1);
                            user.setErrorcount(0);
                            user.setErrortime(new Date());
                            result.msg = "密码错误，用户已锁定";
                        } else {
                            user.setErrorcount(user.getErrorcount() + 1);
                            result.msg = "密码错误，您还有" + (5 - user.getErrorcount()) + "次机会";
                        }
                        userService.update(user);
                        result.code = 0;
                    }
                } else {
                    result.code = 0;
                    result.msg = "用户锁定，请24小时后重试";
                }
            } else {
                result.code = 0;
                result.msg = "用户名或密码错误";
            }
        }
        catch (Exception ex){
            result.code = 0;
            result.msg = "连接出错，请联系管理员";
            logs.error("登录错误,"+ex.getMessage(),"login");
            ex.printStackTrace();
        }
        return result;
    }

    /**
     * 注销
     * @throws Exception
     */
    @RequestMapping(value = "/loginout",method = RequestMethod.GET)
    public  void loginout() throws Exception {
        Cookie[] cookies = request.getCookies();
        String u=GetPathurl();
        String u2=GetRequesturl();
        if(u2.toLowerCase().indexOf("autofu.cn")>-1){
            u=u2;
        }
        String ext="login/index4";
        if(u2.indexOf("hq.autofu.cn")>-1){
            ext="login/index5";
        }
        if(u2.indexOf("bt.autofu.cn")>-1){
            ext="login/index6";
        }
        if(GetPathurl().indexOf("volkswagen")>-1){
            ext="login/index7";
        }
        if (cookies != null) {
            int has=0;
            for(Cookie cookie : cookies){
                if(cookie.getName().equals("allianz")){
                    has=1;
                    //String json=Cryptography.base64Decoder(cookie.getValue());
                    //User u = JsonDeserializer(URLDecoder.decode(json, "utf-8"), User.class);
                    //String brand=u.getBrand();
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                    //request.getSession().removeAttribute("user");
                    //response.sendRedirect(request.getContextPath()+("BMW".equals(brand)?"/login/index":  "/login/login"));
                    response.sendRedirect(u+("0".equals(domainenter)?"login/login":("1".equals(domainenter)?"login/index":("2".equals(domainenter)?"login/index3":(ext)))));
                }
            }
            if(has==0){
                response.sendRedirect(u+("0".equals(domainenter)?"login/login":("1".equals(domainenter)?"login/index":("2".equals(domainenter)?"login/index3":(ext)))));
            }
        }else{
            response.sendRedirect(u+("0".equals(domainenter)?"login/login":("1".equals(domainenter)?"login/index":("2".equals(domainenter)?"login/index3":(ext)))));
        }
    }

    @RequestMapping(value = "/forget",method = RequestMethod.GET )
    public ModelAndView  forget(){
        ModelAndView mv = new ModelAndView();
        return mv;
    }
    @RequestMapping(value = "/forget2",method = RequestMethod.GET )
    public ModelAndView  forget2(){
        ModelAndView mv = new ModelAndView();
        return mv;
    }
    @RequestMapping(value = "/forget3",method = RequestMethod.GET )
    public ModelAndView  forget3(){
        ModelAndView mv = new ModelAndView();
        return mv;
    }
    @RequestMapping(value = "/forget4",method = RequestMethod.GET )
    public ModelAndView  forget4(){
        ModelAndView mv = new ModelAndView();
        return mv;
    }
    @RequestMapping(value = "/forget5",method = RequestMethod.GET )
    public ModelAndView  forget5(){
        ModelAndView mv = new ModelAndView();
        return mv;
    }
    @RequestMapping(value = "/forget6",method = RequestMethod.GET )
    public ModelAndView  forget6(){
        ModelAndView mv = new ModelAndView();
        return mv;
    }
    @RequestMapping(value = "/forget7",method = RequestMethod.GET )
    public ModelAndView  forget7(){
        ModelAndView mv = new ModelAndView();
        return mv;
    }
    @RequestMapping(value = "/forget8",method = RequestMethod.GET )
    public ModelAndView  forget8(){
        ModelAndView mv = new ModelAndView();
        return mv;
    }
    @RequestMapping(value = "/forget",method = RequestMethod.POST )
    public JsonResult  forget(String username) throws Exception {
        JsonResult result=new JsonResult();
        User user=userService.selectByLoginName(username);
        if(user==null){
            result.code=0;
            result.msg="用户邮箱错误";
            return result;
        }
        if(user.getEmail().isEmpty()){
            result.code=0;
            result.msg="用户邮箱错误";
            return result;
        }
        if(user.getValid()==0){
            result.code=0;
            result.msg="用户已禁用";
            return result;
        }
        try {
            String id= GuidUtil.newGuid();
            userResetService.insert(id,user.getId(),user.getUsername(),user.getEmail());
            String urls= GetPathurl();
            String urls2= GetRequesturl();
            String path="reset";
            String title="Specialty Line Pack";
            if("1".equals(domainenter)){
                path="reset2";
                title="Specialty Line Pack";
            }else if("2".equals(domainenter)){
                path="reset3";
                title="Specialty Line Pack";
            }else if("3".equals(domainenter)){
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
                    if(GetPathurl().indexOf("volkswagen")>-1){
                        path = "reset7";
                        title = "Line Pack";
                    }
                }
//                if(urls.indexOf("hq.autofu.cn")>-1){
//                    path = "reset5";
//                    title = "HQ Line Pack";
//                }else {
//                    path = "reset4";
//                    title = "Mazda Line Pack";
//                }
            }
            String content=("您已申请密码重置，请点击后方链接继续操作，<a href='{url}login/"+path+"?id={id}' target='_blank'>设置密码</a>,有效10分钟。").replace("{url}",urls).replace("{id}",id);
            SendMailUtil.sendMail(user.getEmail(), title+"密码重置", content, "");
            result.code = 1;
        }
        catch (Exception ex){
            result.code=0;
            result.msg=ex.getMessage();
        }
        return result;
    }
    @RequestMapping(value = "/reset",method = RequestMethod.GET )
    public ModelAndView  reset(String id){
        ModelAndView mv = new ModelAndView();
        if(id==null||"".equals(id)){
            mv.addObject("isvalid",0);
        }else {
            Map<String, Object> map = userResetService.selectByKey(id);
            if (map != null) {
                if("1".equals(map.get("state").toString())){
                    mv.addObject("isvalid", 0);
                }else {
                    mv.addObject("map", map);
                    User user = userService.getUserById(map.get("userid").toString());
                    if(user.getIsactived()==0){ //新用户激活不限制时间
                        mv.addObject("isvalid", 1);
                    }else {
                        if ((((Date) map.get("createddate")).getTime() / 1000 + 60 * 10) > (new Date().getTime() / 1000)) {
                            mv.addObject("isvalid", 1);
                        } else {
                            mv.addObject("isvalid", 0);
                        }
                    }
                }
            } else {
                mv.addObject("isvalid", 0);
            }
        }
        mv.addObject("id", id);
        return mv;
    }
    @RequestMapping(value = "/reset2",method = RequestMethod.GET )
    public ModelAndView  reset2(String id){
        ModelAndView mv = new ModelAndView();
        if(id==null||"".equals(id)){
            mv.addObject("isvalid",0);
        }else {
            Map<String, Object> map = userResetService.selectByKey(id);
            if (map != null) {
                if("1".equals(map.get("state").toString())){
                    mv.addObject("isvalid", 0);
                }else {
                    mv.addObject("map", map);
                    User user = userService.getUserById(map.get("userid").toString());
                    if(user.getIsactived()==0){ //新用户激活不限制时间
                        mv.addObject("isvalid", 1);
                    }else {
                        if ((((Date) map.get("createddate")).getTime() / 1000 + 60 * 10) > (new Date().getTime() / 1000)) {
                            mv.addObject("isvalid", 1);
                        } else {
                            mv.addObject("isvalid", 0);
                        }
                    }
                }
            } else {
                mv.addObject("isvalid", 0);
            }
        }
        mv.addObject("id", id);
        return mv;
    }
    @RequestMapping(value = "/reset3",method = RequestMethod.GET )
    public ModelAndView  reset3(String id){
        ModelAndView mv = new ModelAndView();
        if(id==null||"".equals(id)){
            mv.addObject("isvalid",0);
        }else {
            Map<String, Object> map = userResetService.selectByKey(id);
            if (map != null) {
                if("1".equals(map.get("state").toString())){
                    mv.addObject("isvalid", 0);
                }else {
                    mv.addObject("map", map);
                    User user = userService.getUserById(map.get("userid").toString());
                    if(user.getIsactived()==0){ //新用户激活不限制时间
                        mv.addObject("isvalid", 1);
                    }else {
                        if ((((Date) map.get("createddate")).getTime() / 1000 + 60 * 10) > (new Date().getTime() / 1000)) {
                            mv.addObject("isvalid", 1);
                        } else {
                            mv.addObject("isvalid", 0);
                        }
                    }
                }
            } else {
                mv.addObject("isvalid", 0);
            }
        }
        mv.addObject("id", id);
        return mv;
    }
    @RequestMapping(value = "/reset4",method = RequestMethod.GET )
    public ModelAndView  reset4(String id){
        ModelAndView mv = new ModelAndView();
        if(id==null||"".equals(id)){
            mv.addObject("isvalid",0);
        }else {
            Map<String, Object> map = userResetService.selectByKey(id);
            if (map != null) {
                if("1".equals(map.get("state").toString())){
                    mv.addObject("isvalid", 0);
                }else {
                    mv.addObject("map", map);
                    User user = userService.getUserById(map.get("userid").toString());
                    if(user.getIsactived()==0){ //新用户激活不限制时间
                        mv.addObject("isvalid", 1);
                    }else {
                        if ((((Date) map.get("createddate")).getTime() / 1000 + 60 * 10) > (new Date().getTime() / 1000)) {
                            mv.addObject("isvalid", 1);
                        } else {
                            mv.addObject("isvalid", 0);
                        }
                    }
                }
            } else {
                mv.addObject("isvalid", 0);
            }
        }
        mv.addObject("id", id);
        return mv;
    }
    @RequestMapping(value = "/reset5",method = RequestMethod.GET )
    public ModelAndView  reset5(String id){
        ModelAndView mv = new ModelAndView();
        if(id==null||"".equals(id)){
            mv.addObject("isvalid",0);
        }else {
            Map<String, Object> map = userResetService.selectByKey(id);
            if (map != null) {
                if("1".equals(map.get("state").toString())){
                    mv.addObject("isvalid", 0);
                }else {
                    mv.addObject("map", map);
                    User user = userService.getUserById(map.get("userid").toString());
                    if(user.getIsactived()==0){ //新用户激活不限制时间
                        mv.addObject("isvalid", 1);
                    }else {
                        if ((((Date) map.get("createddate")).getTime() / 1000 + 60 * 10) > (new Date().getTime() / 1000)) {
                            mv.addObject("isvalid", 1);
                        } else {
                            mv.addObject("isvalid", 0);
                        }
                    }
                }
            } else {
                mv.addObject("isvalid", 0);
            }
        }
        mv.addObject("id", id);
        return mv;
    }
    @RequestMapping(value = "/reset6",method = RequestMethod.GET )
    public ModelAndView  reset6(String id){
        ModelAndView mv = new ModelAndView();
        if(id==null||"".equals(id)){
            mv.addObject("isvalid",0);
        }else {
            Map<String, Object> map = userResetService.selectByKey(id);
            if (map != null) {
                if("1".equals(map.get("state").toString())){
                    mv.addObject("isvalid", 0);
                }else {
                    mv.addObject("map", map);
                    User user = userService.getUserById(map.get("userid").toString());
                    if(user.getIsactived()==0){ //新用户激活不限制时间
                        mv.addObject("isvalid", 1);
                    }else {
                        if ((((Date) map.get("createddate")).getTime() / 1000 + 60 * 10) > (new Date().getTime() / 1000)) {
                            mv.addObject("isvalid", 1);
                        } else {
                            mv.addObject("isvalid", 0);
                        }
                    }
                }
            } else {
                mv.addObject("isvalid", 0);
            }
        }
        mv.addObject("id", id);
        return mv;
    }

    @RequestMapping(value = "/reset7",method = RequestMethod.GET )
    public ModelAndView  reset7(String id){
        ModelAndView mv = new ModelAndView();
        if(id==null||"".equals(id)){
            mv.addObject("isvalid",0);
        }else {
            Map<String, Object> map = userResetService.selectByKey(id);
            if (map != null) {
                if("1".equals(map.get("state").toString())){
                    mv.addObject("isvalid", 0);
                }else {
                    mv.addObject("map", map);
                    User user = userService.getUserById(map.get("userid").toString());
                    if(user.getIsactived()==0){ //新用户激活不限制时间
                        mv.addObject("isvalid", 1);
                    }else {
                        if ((((Date) map.get("createddate")).getTime() / 1000 + 60 * 10) > (new Date().getTime() / 1000)) {
                            mv.addObject("isvalid", 1);
                        } else {
                            mv.addObject("isvalid", 0);
                        }
                    }
                }
            } else {
                mv.addObject("isvalid", 0);
            }
        }
        mv.addObject("id", id);
        return mv;
    }

    @RequestMapping(value = "/reset8",method = RequestMethod.GET )
    public ModelAndView  reset8(String id){
        ModelAndView mv = new ModelAndView();
        if(id==null||"".equals(id)){
            mv.addObject("isvalid",0);
        }else {
            Map<String, Object> map = userResetService.selectByKey(id);
            if (map != null) {
                if("1".equals(map.get("state").toString())){
                    mv.addObject("isvalid", 0);
                }else {
                    mv.addObject("map", map);
                    User user = userService.getUserById(map.get("userid").toString());
                    if(user.getIsactived()==0){ //新用户激活不限制时间
                        mv.addObject("isvalid", 1);
                    }else {
                        if ((((Date) map.get("createddate")).getTime() / 1000 + 60 * 10) > (new Date().getTime() / 1000)) {
                            mv.addObject("isvalid", 1);
                        } else {
                            mv.addObject("isvalid", 0);
                        }
                    }
                }
            } else {
                mv.addObject("isvalid", 0);
            }
        }
        mv.addObject("id", id);
        return mv;
    }

    @RequestMapping(value = "/change",method = RequestMethod.GET )
    public ModelAndView  change(){
        ModelAndView mv = new ModelAndView();
        return mv;
    }
    @RequestMapping(value = "/change2",method = RequestMethod.GET )
    public ModelAndView  change2(){
        ModelAndView mv = new ModelAndView();
        return mv;
    }
    @RequestMapping(value = "/change3",method = RequestMethod.GET )
    public ModelAndView  change3(){
        ModelAndView mv = new ModelAndView();
        return mv;
    }
    @RequestMapping(value = "/change4",method = RequestMethod.GET )
    public ModelAndView  change4(){
        ModelAndView mv = new ModelAndView();
        return mv;
    }
    @RequestMapping(value = "/change5",method = RequestMethod.GET )
    public ModelAndView  change5(){
        ModelAndView mv = new ModelAndView();
        return mv;
    }
    @RequestMapping(value = "/change6",method = RequestMethod.GET )
    public ModelAndView  change6(){
        ModelAndView mv = new ModelAndView();
        return mv;
    }
    @RequestMapping(value = "/change7",method = RequestMethod.GET )
    public ModelAndView  change7(){
        ModelAndView mv = new ModelAndView();
        return mv;
    }
    @RequestMapping(value = "/change8",method = RequestMethod.GET )
    public ModelAndView  change8(){
        ModelAndView mv = new ModelAndView();
        return mv;
    }
    @RequestMapping(value = "/reset",method = RequestMethod.POST )
    public JsonResult  reset(String id,String username,String password) throws Exception {
        JsonResult result=new JsonResult();
        try {
            String decodepwd=Cryptography.base64Decoder(password);
            Map<String, Object> map = userResetService.selectByKey(id);
            User user=userService.selectByPrimaryKey(map.get("userid").toString());
            user.setPassword(Cryptography.getSHA256(decodepwd + userService.getSalt(user.getId())));
            if(user.getIsactived()==0){
                user.setIsactived(1);
                user.setValid(1);
                user.setActivetime(new Date());
            }
            userService.update(user);
            userResetService.updateStateByKey(id,1);
            result.code = 1;
        }
        catch (Exception ex){
            result.code=0;
            result.msg=ex.getMessage();
        }
        return result;
    }
    @RequestMapping(value = "/change",method = RequestMethod.POST )
    public JsonResult  change(String password,String newpassword) throws Exception {
        JsonResult result=new JsonResult();
        try {
            String decodepwd=Cryptography.base64Decoder(password);
            String decodepwdnew=Cryptography.base64Decoder(newpassword);
            User user=userService.selectByPrimaryKey(UserInfo().getUserID());
            String salt = userService.getSalt(UserInfo().getUserID());
            if(user.getPassword().equals( Cryptography.getSHA256(decodepwd + salt))){
                user.setPassword(Cryptography.getSHA256(decodepwdnew + salt));
                userService.update(user);

                String id= GuidUtil.newGuid();
                userResetService.insertState(id,user.getId(),user.getUsername(),user.getEmail(),1);
                result.code=1;
            }else{
                result.code=0;
                result.msg="原密码错误";
            }

        }
        catch (Exception ex){
            result.code=0;
            result.msg=ex.getMessage();
        }
        return result;
    }
}
