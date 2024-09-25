package com.bond.allianz.Dao;

import com.bond.allianz.utils.GuidUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * 角色
 */
@Repository
public class ActDao extends BaseDao {


    public  List<Map<String,Object>>  selectActList (){
        String sql="select * from act  order by actname";//id,actname
        List<Map<String, Object>> map =jdbcTemplate.queryForList(sql);
        return map;
    }





    /**
     * 是否存在该角色
     * @param actname
     * @return
     */
    public int exitsAct(String actname){
        String sql="select count(*) from act where  actname=?";//id,actname
        List<Integer> has =jdbcTemplate.queryForList(sql,new Object[]{ actname},int.class);
        if(has.size()>0){
            return has.get(0);
        }else{
            return 0;
        }
    }


    /**
     * 添加角色
     * @param actname
     * @return
     */
    public int insertAct(String actname){
        String sql="insert into  act(actname) values(?);";
        int r=jdbcTemplate.update(sql, actname);
        return  r;
    }


    /**
     * 删除角色
     * @param actname
     * @return
     */
    public int deleteAct(String actname){
        String sql="delete from act where  actname=?";
        int has =jdbcTemplate.update(sql,new Object[]{ actname});
        return  has;
    }


    /**
     * 根据角色获取权限菜单id
     * @param act
     * @return
     */
    public  List<String> selectRoleMenu(String act){
        String sql="select menuno from actmenu where  actname=?";//
        List<String> menus =jdbcTemplate.queryForList(sql,new Object[]{ act},String.class);
        return  menus;
    }



    public  void saveActMenu(String actname,List<String> menunos){
        try {
            if (menunos.size() == 0) {
                jdbcTemplate.update("delete from actmenu where actname=?", actname);
            } else {
                jdbcTemplate.update("delete from actmenu where  actname=? and  menuno not in ('" + StringUtils.join(menunos, "','") + "')", actname);
                for (String m : menunos) {
                    List<Integer> has = jdbcTemplate.queryForList("select count(*) from actmenu where  actname=? and menuno=?", new Object[]{ actname, m}, Integer.class);
                    if (has.size()>0&&has.get(0) == 0) {
                        jdbcTemplate.update("insert into actmenu(id,actname,menuno,action) values(?,?,?,?)", new Object[]{GuidUtil.newGuid(), actname, m, ""});
                    }
                }
            }
        }
        catch (Exception ex){
            throw ex;
        }
    }


    /**
     * 修改角色名字
     * @param actname
     * @param newname
     * @return
     */
    public int updatetActName(String actname,String newname){
        String sql="update   act set actname=? where actname=? ";
        jdbcTemplate.update(sql, newname,actname);
        int r=jdbcTemplate.update("update actmenu set actname=? where  actname=?", newname, actname);
        return  r;
    }

}
