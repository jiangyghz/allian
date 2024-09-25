package com.bond.allianz.controller;

import com.bond.allianz.entity.PageInfo;
import com.bond.allianz.service.*;
import com.bond.allianz.utils.ExcelUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/error")
public class ErrorController extends BaseController{


    @RequestMapping(value = "/deny")
    public ModelAndView deny(){
        ModelAndView mv = new ModelAndView();

        return mv;
    }
    @RequestMapping(value = "/404")
    public ModelAndView E404(){
        ModelAndView mv = new ModelAndView("/404");

        return mv;
    }
    @RequestMapping(value = "/500")
    public ModelAndView E500(){
        ModelAndView mv = new ModelAndView("/500");

        return mv;
    }

}
