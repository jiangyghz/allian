package com.bond.allianz.service;

import com.bond.allianz.Dao.ClaimDao;
import com.bond.allianz.Dao.MtClaimDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public class MtClaimService {
    @Autowired
    private MtClaimDao mtclaimDao;
    public String getClaimCount(String data){return mtclaimDao.getClaimCount(data);}
    public String addorUpdateClaim(String data){return mtclaimDao.addorUpdateClaim(data);}
    public String getClaim(String data){return mtclaimDao.getClaim(data);}
    public String checkClaim(String data){return mtclaimDao.checkClaim(data);}
    public String addClaimpic(String data, String filetype, MultipartFile file){return mtclaimDao.addClaimpic(data,filetype,file);}
    public String getClaimpic(String data){return mtclaimDao.getClaimpic(data);}
    public String deleteClaimpic(String data){return mtclaimDao.deleteClaimpic(data);}
    public String getProvince(String data){return mtclaimDao.getProvince(data);}
    public String getCity(String data){return mtclaimDao.getCity(data);}
    public String claim_down(String data){return mtclaimDao.claim_down(data);}

    public String getClaimRemark(String data){return mtclaimDao.getClaimRemark(data);}

    public List<Map<String,Object>> getClaimPic(String claimnos){return mtclaimDao.getClaimPic(claimnos);}

    public List<Map<String,Object>> selectCheckPicList(int tid,String claimno ) {
        return mtclaimDao.selectCheckPicList(tid,claimno);
    }

    public int insertCheckPic(int tid,String claimno,String name,String picurl) {
        return mtclaimDao.insertCheckPic(tid,claimno,name,picurl);
    }

    public  int deleteCheckPicByID(int id){
        return mtclaimDao.deleteCheckPicByID(id);
    }

}
