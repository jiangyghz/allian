package com.bond.allianz.service;

import com.bond.allianz.Dao.ActDao;
import com.bond.allianz.Dao.MenuDao;
import com.bond.allianz.entity.Menu;
import com.bond.allianz.mapper.MenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class MenuService {


    /**
     * 经销商菜单
     */
    @Autowired
    private MenuMapper menuMapper;

    @Autowired
    private MenuDao menuDao;


    public Menu getMenuById(Integer id){
        return menuMapper.selectByPrimaryKey(id);
    }
    public Menu getMenuByUrl(String url){
        return menuMapper.selectByUrl(url);
    }

    public int insertMenu(Menu target){
        return menuMapper.insert(target);
    }
    public  int updateMenuByKey(Menu target){
        return  menuMapper.updateByPrimaryKey(target);
    }
    public  int deleteMenuByKey(int menuno){
         menuMapper.deleteByPrimaryKey(menuno);
         menuMapper.deleteByPmenuno(menuno);
         return 1;
    }


    /**
     * 经销商菜单 -1 全部，0禁用，1 启用
     * @param valid
     * @return
     */
    public List<Menu> selectMenu(int valid){
        if(valid==-1){
            return menuMapper.selectAllList();
        }else {
            boolean b=valid==1?true:false;
            return menuMapper.selectList(b);
        }
    }
    public List<Menu> selectMenuByAct( List<String> act){
        if(act.size()>0){
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("act", act);
            return menuMapper.selectListByAct(params);
        }else{
            List<Menu> lists=new ArrayList<>();
            return lists;
        }
    }
    public boolean selectRoleByActMenuno(String actname,String menuno){
        return menuDao.selectRoleByActMenuno(actname,menuno);

    }

    public  String selectMenoByUrlLike(String urllike) {
        return menuDao.selectMenoByUrlLike(urllike);

    }
}
