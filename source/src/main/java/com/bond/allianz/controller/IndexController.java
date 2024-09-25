package com.bond.allianz.controller;

import com.bond.allianz.entity.Menu;
import com.bond.allianz.entity.User;
import com.bond.allianz.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class IndexController extends BaseController  {
    @Autowired
    private UserService userService;
    @Autowired
    private AgencyService agencyService;
    @Autowired
    private InsuranceService insuranceService;
    @Autowired
    private PayRequestService payRequestService;


    @Autowired
    private MenuService menuService;
    @Autowired
    private BmwService bmwService;


    @RequestMapping(value = "/mainadmin",method = RequestMethod.GET)
    public ModelAndView mainadmin () throws Exception {

        if(domainenter.equals("0")){
            response.sendRedirect("mainadminp");
        }else if(domainenter.equals("2")){
            response.sendRedirect("mainadminb");
        }
        ModelAndView mv = new ModelAndView();
        List<Menu> menu=menuService.selectMenuByAct(UserInfo().getAct());
        mv.addObject("username", UserInfo().getUserName());
        mv.addObject("act",UserInfo().getAct().get(0));
        String brand=UserInfo().getBrand();
        mv.addObject("brand",brand);
        List<Menu> firstmenu=menu.stream().filter(m->m.getPmenuno()==0).collect(Collectors.toList());
        Map<String,List<Menu>> map=new HashMap<String,List<Menu>>();
        for (Menu m1:firstmenu) {
            List<Menu> node=menu.stream().filter(m->m.getPmenuno()==m1.getMenuno()).collect(Collectors.toList());

            if(brand.equals("Motor")){
                node=node.stream().filter(m->!m.getMenuname().equals("轮胎查询")).collect(Collectors.toList());
            }
            map.put(m1.getMenuno().toString(),node);
        }
        mv.addObject("firstmenu",firstmenu);
        mv.addObject("menumap",map);
        mv.addObject("tid",UserInfo().getUserID());
        mv.addObject("agencyid",UserInfo().getAgencyID());
        String name=UserInfo().getAct().get(0);
        if("经销商".equals(name)){
            name=agencyService.selectNameByCode(UserInfo().getAgencyID());
        }else if("保险公司".equals(name)){
            name=insuranceService.selectNameByCode(UserInfo().getAgencyID());
        }else if("管理员".equals(name)){
            name="Allianz";
        }
        if(Arrays.asList("BMW","Motor").contains(UserInfo().getBrand())){
            mv.addObject("showamount",1);
            float amount= bmwService.getBalanceBydealerno(UserInfo().getAgencyID());
            mv.addObject("amount",amount);
        }else{
            mv.addObject("showamount",0);
            mv.addObject("amount",0);
        }
        mv.addObject("name",name);
        mv.addObject("domainenter",domainenter);
        User u=userService.selectByPrimaryKey(UserInfo().getUserID());
        if(u!=null&&u.getConditions()==0){
            userService.updateConditions(UserInfo().getUserID(),1);
        }
        return mv;
    }

    @RequestMapping(value = "/mainadminb",method = RequestMethod.GET)
    public ModelAndView mainadminb () throws Exception {

        if(domainenter.equals("0")){
            response.sendRedirect("mainadminp");
        }else if(domainenter.equals("1")){
            response.sendRedirect("mainadmin");
        }
        ModelAndView mv = new ModelAndView();
        List<Menu> menu=menuService.selectMenuByAct(UserInfo().getAct());
        mv.addObject("username", UserInfo().getUserName());
        mv.addObject("act",UserInfo().getAct().get(0));
        List<Menu> firstmenu=menu.stream().filter(m->m.getPmenuno()==0).collect(Collectors.toList());
        Map<String,List<Menu>> map=new HashMap<String,List<Menu>>();
        for (Menu m1:firstmenu) {
            List<Menu> node=menu.stream().filter(m->m.getPmenuno()==m1.getMenuno()).collect(Collectors.toList());
            map.put(m1.getMenuno().toString(),node);
        }
        mv.addObject("firstmenu",firstmenu);
        mv.addObject("menumap",map);
        mv.addObject("tid",UserInfo().getUserID());
        mv.addObject("agencyid",UserInfo().getAgencyID());
        String name=UserInfo().getAct().get(0);
        String act=UserInfo().getAct().get(0);
        String brandname="";
        if("经销商".equals(act)){
            name=agencyService.selectNameByCode(UserInfo().getAgencyID());
            Map<String,Object> agency= agencyService.selectByDealerNo(UserInfo().getAgencyID());
            String brand=agency!=null?(agency.get("brand")!=null?agency.get("brand").toString():""):"";
            switch (brand){
                case "SVW":
                    brandname="上汽大众";
                    break;
                case "Jetta":
                    brandname="捷达";
                    break;
                case "Audi":
                    brandname="奥迪";
                    break;
                case "Skoda":
                    brandname="斯柯达";
                    break;
                case "FAWVW":
                    brandname="一汽大众";
                    break;
            }
        }else if("保险公司".equals(act)){
            name=insuranceService.selectNameByCode(UserInfo().getAgencyID());
        }else if("管理员".equals(act)){
            name="Allianz";
        }
        if((Arrays.asList("2","3").contains(domainenter)&& "经销商".equals(act))){
            mv.addObject("showamount",1);
            float amount= bmwService.getBalanceBydealerno(UserInfo().getAgencyID());
            mv.addObject("amount",amount);
        }else {
            mv.addObject("showamount", 0);
            mv.addObject("amount", 0);
        }
        mv.addObject("name",name);
        mv.addObject("brandname",brandname);
        mv.addObject("domainenter",domainenter);
        User u=userService.selectByPrimaryKey(UserInfo().getUserID());
        if(u!=null&&u.getConditions()==0){
            userService.updateConditions(UserInfo().getUserID(),1);
        }
        return mv;
    }

//保时捷 首页
    @RequestMapping(value = "/mainadminp",method = RequestMethod.GET)
    public ModelAndView mainadminp () throws Exception {

        if(domainenter.equals("1")){
            response.sendRedirect("mainadmin");
        }else if(domainenter.equals("2")){
            response.sendRedirect("mainadminb");
        }
        ModelAndView mv = new ModelAndView();
        List<Menu> menu=menuService.selectMenuByAct(UserInfo().getAct());
        mv.addObject("username", UserInfo().getUserName());
        mv.addObject("act",UserInfo().getAct().get(0));
        List<Menu> firstmenu=menu.stream().filter(m->m.getPmenuno()==0).collect(Collectors.toList());
        Map<String,List<Menu>> map=new HashMap<String,List<Menu>>();
        for (Menu m1:firstmenu) {
            List<Menu> node=menu.stream().filter(m->m.getPmenuno()==m1.getMenuno()).collect(Collectors.toList());
            map.put(m1.getMenuno().toString(),node);
        }
        mv.addObject("firstmenu",firstmenu);
        mv.addObject("menumap",map);
        mv.addObject("tid",UserInfo().getUserID());
        mv.addObject("agencyid",UserInfo().getAgencyID());
        String name=UserInfo().getAct().get(0);
        if("经销商".equals(name)){
            name=agencyService.selectNameByCode(UserInfo().getAgencyID());
        }else if("保险公司".equals(name)){
            name=insuranceService.selectNameByCode(UserInfo().getAgencyID());
        }else if("管理员".equals(name)){
            name="Allianz";
        }
        if("BMW".equals(UserInfo().getBrand())){
            mv.addObject("showamount",1);
            float amount= bmwService.getBalanceBydealerno(UserInfo().getAgencyID());
            mv.addObject("amount",amount);
        }else{
            mv.addObject("showamount",0);
            mv.addObject("amount",0);
        }
        mv.addObject("name",name);
        mv.addObject("domainenter",domainenter);
        User u=userService.selectByPrimaryKey(UserInfo().getUserID());
        if(u!=null&&u.getConditions()==0){
            userService.updateConditions(UserInfo().getUserID(),1);
        }
        return mv;
    }

    @RequestMapping(value = "/showamount")
    public String showamount () {
        String result = "";
        try {
            float amount = bmwService.getBalanceBydealerno(UserInfo().getAgencyID());
            result = String.valueOf(amount);
        } catch (Exception ex) {

        }
        return result;
    }
}
