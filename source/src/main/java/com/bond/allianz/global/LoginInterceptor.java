package com.bond.allianz.global;

import com.bond.allianz.entity.Menu;
import com.bond.allianz.entity.User;
import com.bond.allianz.entity.UserInfo;
import com.bond.allianz.service.CarService;
import com.bond.allianz.service.MenuService;
import com.bond.allianz.utils.Cryptography;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.dom4j.datatype.DatatypeElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Date;

/**
 * 登录拦截器
 */
public class LoginInterceptor  implements HandlerInterceptor {

    /**
     * 网站适配器注册拦截器 需要加入@Bean
     */
    @Value("${domain.root}")
    public String domainroot;

    @Value("${domain.enter}")
    public String domainenter;

    @Autowired
    private MenuService menuService;
    /**
     * 在请求被处理之前调用
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //return true;
        // 检查每个到来的请求对应的session域中是否有登录标识
        Cookie[] cookies = request.getCookies();
        boolean hascookie = false;
        String act = "";
        String v = "";
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("curt")) {
                    v = cookie.getValue();
                }
            }
        }
        String tokensign = request.getParameter("tokensign");
        if (tokensign != null && !tokensign.equals(Cryptography.base64Encoder(v))) {
            return false;
        }

        String token = String.valueOf(new Date().getTime());
        Cookie cookie1 = new Cookie("curt", token);
        //设置Cookie的有效期为1天  单位秒
        cookie1.setMaxAge(24 * 60 * 60);
        cookie1.setPath("/");
        response.addCookie(cookie1);
        Cookie cookie2 = new Cookie("tokensign", Cryptography.base64Encoder(token));
        //设置Cookie的有效期为1天  单位秒
        cookie2.setMaxAge(24 * 60 * 60);
        cookie2.setPath("/");
        response.addCookie(cookie2);
        cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("allianz")) {
                    String json = Cryptography.AESDecrypt(cookie.getValue());
                    if (json != "") {
                        try {
                            GsonBuilder gsonBuilder = new GsonBuilder();
                            gsonBuilder.setDateFormat("yyyy-MM-dd");//设置时间格式
                            gsonBuilder.registerTypeAdapter(String.class, new StringNullAdapter());
                            Gson gson = gsonBuilder.create();
                            User u = gson.fromJson(URLDecoder.decode(json, "utf-8"), User.class);
                            act = u.getAct();
                        } catch (Exception ex) {

                        }
                    }
                    hascookie = true;
                }
            }
        }
        if (hascookie) {
            String pathurl = request.getRequestURL().toString().toLowerCase();
            String path = pathurl.substring(pathurl.lastIndexOf("/"));
            //String path = pathurl.replace(domainroot.toLowerCase(), "");
            boolean hasrole = false;
            try {
                if (path.equals("/agencyuserlistp")) {
                    path = "/agencyuserlist";
                }
                if (path.equals("/uinfop")) {
                    path = "/uinfo";
                }
                if (path.equals("/agencyinfo")) {
                    path = "/agencyinfop";
                }
                String menuno = menuService.selectMenoByUrlLike(path);
                if (menuno != null && !"".equals(menuno)) {
                    boolean flag = menuService.selectRoleByActMenuno(act, menuno);
                    if (flag) {
                        hasrole = true;
                    }
                } else {
                    hasrole = true;
                }
//                Menu menu = menuService.getMenuByUrl(path);
//                if (menu != null) {
//                    boolean flag = menuService.selectRoleByActMenuno(act, menu.getMenuno().toString());
//                    if (flag) {
//                        hasrole = true;
//                    }
//                } else {
//                    hasrole = true;
//                }
            } catch (Exception ex) {
                hasrole = true;
            }
            if (!hasrole) {
                response.sendRedirect(domainroot + "error/deny");
                return false;
            } else {
                if (domainenter.equals("0")) {
                    if (pathurl.indexOf("manage/agencyuserlist") > -1 && pathurl.indexOf("manage/agencyuserlistp") == -1) {
                        response.sendRedirect("../manage/agencyuserlistp");
                    }
                    if (pathurl.indexOf("manage/uinfo") > -1 && pathurl.indexOf("manage/uinfop") == -1) {
                        response.sendRedirect("../manage/uinfop");
                    }
                    if (pathurl.indexOf("manage/agencyinfo") > -1 && pathurl.indexOf("manage/agencyinfop") == -1) {
                        response.sendRedirect("../manage/agencyinfop");
                    }
                }
                return true;
                //response.sendRedirect(request.getScheme()+"://"+request.getServerName() +":"+request.getServerPort()+request.getContextPath()+"/"+ "login/loginout");
            }
        }
        String url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath() + "/";
        if (url.toLowerCase().indexOf("autofu.cn") > -1) {
            response.sendRedirect(url + "login/loginout");
        } else {
            response.sendRedirect(domainroot + "login/loginout");
        }
        return false;
//        Object loginName = request.getSession().getAttribute("loginName");
//        if (null == loginName || !(loginName instanceof String)) {
//            // 未登录，重定向到登录页
//            response.sendRedirect("/");
//            return false;
//        }
//        String userName = (String) loginName;
//        //System.out.println("当前用户已登录，登录的用户名为： " + userName);
//        return true;

    }

    /**
     * 在请求被处理后，视图渲染之前调用
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    /**
     * 在整个请求结束后调用
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
