package com.bond.allianz.controller;

import com.bond.allianz.utils.RedisUtil;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/redis")
public class RedisController extends BaseController {

    @Autowired
    private RedisUtil redisUtil;

    //添加
    @RequestMapping(value = "/saveRedis",method = RequestMethod.GET)
    public void saveRedis(){

        redisUtil.set("a","测试");

    }

    //获取
    @RequestMapping(value = "/getRedis",method = RequestMethod.GET)
    public String getRedis(){
        Object v=redisUtil.get("a");
        return v==null?"":v.toString();
    }
}