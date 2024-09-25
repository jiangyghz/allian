package com.bond.allianz.service;

import com.bond.allianz.Dao.ActDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class ActService {

    @Autowired
    private ActDao actDao;

    public  List<Map<String,Object>>  selectActList (){
        return  actDao.selectActList();
    }
    public int exitsAct(String actname){
        return  actDao.exitsAct(actname);
    }
    public int insertAct(String actname){
        return  actDao.insertAct(actname);
    }
    public int deleteAct(String actname){
        return  actDao.deleteAct(actname);
    }
    public  List<String> selectRoleMenu(String act){
        return  actDao.selectRoleMenu(act);
    }
    public  void saveActMenu(String actname,List<String> menunos){
        actDao.saveActMenu(actname,menunos);
    }
    public int updatetActName(String actname,String newname){
        return actDao.updatetActName(actname,newname);
    }


    public String checkSign(String Nonce, String Time, String Sign, String Appid,String data){
        return  actDao.checkSign( Nonce,  Time,  Sign,  Appid, data);
    }
    public  String GetErrorString(int errorcode, String ss){
        return  actDao.GetErrorString( errorcode,  ss);
    }

    public String uploadJpg( MultipartFile file){
        return actDao.uploadJpg(file);
    }
}
