package com.bond.allianz.service;

import com.bond.allianz.Dao.DicDao;
import com.bond.allianz.entity.Dictionary;
import com.bond.allianz.mapper.DictionaryMapper;
import com.bond.allianz.utils.StringsUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.OptionalInt;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class DicService {

    @Autowired
    private DictionaryMapper dicMapper;
    @Autowired
    private DicDao dicDao;

    public Dictionary selectDicById(Integer dicid){
        return dicMapper.selectByPrimaryKey(dicid);
    }


    public int insert(Dictionary target){
        List<Dictionary> list=dicMapper.selectListByPId(target.getParentId(),1);
        int id;
        if(list.size()>0){
            OptionalInt ids=list.stream().mapToInt(d->d.getDicid()).max();
            id=ids.getAsInt();
            Pattern p=Pattern.compile("^(\\d+?)(0+)?$");
            Matcher m=p.matcher(String.valueOf(id));
            m.find();   //
            String str=m.group(1);
            String tmp = addZero(str);
            if(tmp.length()==10){//3级后取所有的最大值
                String newid=dicDao.selectMaxID(tmp.substring(0,6));
                id=Integer.parseInt(newid)+1;
            }else{
                id=Integer.parseInt(StringsUtil.padRight(String.valueOf(Integer.parseInt(tmp)+1),10,'0'));
            }
        }else{
            if(target.getParentId()==0){
                id = 1010000000;
            }else{
                id=target.getParentId();
                Pattern p=Pattern.compile("^(\\d+?)(0+)?$");
                Matcher m=p.matcher(String.valueOf(id));
                m.find();   //
                String tmp=m.group(1);
                int c = 0;
                int j = 0;
                for (int i = 1; i <= 4; i++)
                {
                    c += i;
                    if (c >= tmp.length())
                    {
                        if (c > tmp.length()) //整10的情况后面补0
                        {
                            tmp =StringsUtil.padRight(tmp,c, '0');
                        }
                        j = i;
                        break;
                    }
                }
                if (j == 0)
                {
                    id = Integer.parseInt(tmp) + 1;
                }
                else if (j == 4) //取最大值+1
                {
                    String newid=dicDao.selectMaxID(tmp.substring(0,6));
                    id = Integer.parseInt(newid) + 1;
                }
                else
                {
                    id = Integer.parseInt(StringsUtil.padRight((tmp + StringsUtil.padRight( "0",j, '0') + "1"),10, '0'));
                }
            }
        }
        target.setDicid(id);
        return dicMapper.insert(target);
    }

    /**
     * 补0 防止1100000000 变为120
     * @param dicsort
     * @return
     */
    private  String addZero(String dicsort)
    {
        int c = 0;
        int j = 0;
        for (int i = 1; i <= 4; i++)
        {
            c += i;
            if (c >= dicsort.length())
            {
                j = i;
                break;
            }
        }
        return StringsUtil.padRight(dicsort,c,'0');
    }
    public  int updateByKey(Dictionary target){
        return  dicMapper.updateByPrimaryKey(target);
    }

    /**
     * 虚拟删除 并删除子数据
     * @param target
     * @return
     */
    public  int deleteByKey(Dictionary target){
        target.setDisplay(-1);
        dicDao.deleteByPid(target.getDicid());
        return  dicMapper.updateByPrimaryKey(target);
    }

    /**
     * 通过父id 获取列表
     * @param pid
     * @param display
     * @return
     */
    public List<Dictionary> selectListByPId(Integer pid,int display){
        return dicMapper.selectListByPId(pid,display);
    }

    /**
     * 查询  -1 全部 1 显示，0 屏蔽
     * @param
     * @return
     */
    public List<Dictionary> selectAllList(){
        return dicMapper.selectAllList();
    }
    public String getDealerinfo(String data){return dicDao.getDealerinfo(data);}
    public String getclaimtype(String data){return dicDao.getclaimtype(data);}
    public String getinsurancetype(){return dicDao.getinsurancetype();}
    public String getinsurancetype(String data){
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();
        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();
        result=dicDao.checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;
        return dicDao.getinsurancetype();
    }
}
