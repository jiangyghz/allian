package com.bond.allianz.service;

import com.bond.allianz.Dao.ClaimDao;
import com.bond.allianz.Dao.invoice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class ClaimService {
    @Autowired
    private ClaimDao claimDao;


    public String getClaimCount(String data){return claimDao.getClaimCount(data);}
    public String addorUpdateClaim(String data){return claimDao.addorUpdateClaim(data);}
    public String getClaim(String data){return claimDao.getClaim(data);}
    public String checkClaim(String data){return claimDao.checkClaim(data);}
    public String addClaimpic(String data, String filetype, MultipartFile file){return claimDao.addClaimpic(data,filetype,file);}
    public String getClaimpic(String data){return claimDao.getClaimpic(data);}
    public String deleteClaimpic(String data){return claimDao.deleteClaimpic(data);}
    public String getProvince(String data){return claimDao.getProvince(data);}
    public String getCity(String data){return claimDao.getCity(data);}
    public String claim_down(String data){return claimDao.claim_down(data);}
    public String getclaimtierdetail(String data){return claimDao.getclaimtierdetail(data);}
    public String addorupdateclaimtier(String data){return claimDao.addorupdateclaimtier(data);}
    public String getClaimRemark(String data){return claimDao.getClaimRemark(data);}

    public List<Map<String,Object>> getClaimPic(String claimnos){return claimDao.getClaimPic(claimnos);}

    public List<Map<String,Object>> getMotorClaimPic(String claimnos) {
        return claimDao.getMotorClaimPic(claimnos);
    }
    public List<Map<String,Object>> selectCheckPicList(int tid,String claimno ) {
        return claimDao.selectCheckPicList(tid,claimno);
    }

    public int insertCheckPic(int tid,String claimno,String name,String picurl) {
        return claimDao.insertCheckPic(tid,claimno,name,picurl);
    }

    public  int deleteCheckPicByID(int id){
        return claimDao.deleteCheckPicByID(id);
    }
}
