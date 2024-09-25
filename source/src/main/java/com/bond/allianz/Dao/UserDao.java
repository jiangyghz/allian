package com.bond.allianz.Dao;

import com.bond.allianz.entity.PageInfo;
import com.bond.allianz.utils.GuidUtil;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class UserDao extends BaseDao {


    /**
     * 获取盐值 自动加盐
     *
     * @param uid
     * @return
     */
    public String getSalt(String uid) {
        String sql = "select salt from usersalt where userid=?";

        Object[] arg;
        arg= new Object[1];
        arg[0]=uid;

        List<String> salts ;

        try
        {
            salts = jdbcTemplate.queryForList(sql, new Object[]{uid}, String.class);
        }
        catch (Exception ex)
        {
            salts=null;
        }




        if(salts!=null) {
            if (salts.size() > 0) {
                return salts.get(0);
            } else {
                String salt = GuidUtil.newSalt();
                try {
                    jdbcTemplate.update("insert into usersalt(userid,salt) values(?,?)", new Object[]{uid, salt});
                    return salt;
                } catch (Exception ex) {
                    return "";
                }
            }
        }
        else
        {
            String salt = GuidUtil.newSalt();
            try {
                jdbcTemplate.update("insert into usersalt(userid,salt) values(?,?)", new Object[]{uid, salt});
                return salt;
            } catch (Exception ex) {
                return "";
            }
        }
    }

    /**
     * 设置盐值
     * @param userid
     * @param salt
     * @return
     */
    public int setSalt(String userid,String salt) {
        String sql = "select count(*) from usersalt where userid=?";
        int has = jdbcTemplate.queryForObject(sql, new Object[]{userid}, Integer.class);
        if (has == 0) {
            return jdbcTemplate.update("insert into usersalt(userid,salt) values(?,?)", new Object[]{userid, salt});
        } else {
            return jdbcTemplate.update("update usersalt set salt=? where userid=?", new Object[]{salt, userid,});
        }
    }

    /**
     * 是否存在该用户名
     * @param username
     * @return
     */
    public int exitsUserName(String username){
        String sql="select count(*) from usermember where username=?";//id,actname
        List<Integer> has =jdbcTemplate.queryForList(sql,new Object[]{ username},int.class);
        if(has.size()>0){
            return has.get(0);
        }else{
            return 0;
        }
    }
    /**
     * 查询经销商用户
     * @param pageindex
     * @param pagesize
     * @param agencyid
     * @param agencyname
     * @param namelike
     * @param tel
     * @param valid
     * @return
     */
    public  PageInfo<Map<String, Object>> selectAgencyAll(int pageindex ,int pagesize,String agencyid,String agencyname,String brand, String namelike, String tel, int valid,int isactived){
        String sql ="select t.*,i.dealername as agencyname  from usermember t left join dealer i on t.agencyid=i.dealerno where 1=1  ";
        List<Object> queryList=new ArrayList<Object>();
        if(agencyid!=null&&!"".equals(agencyid)){
            sql+=" and t.agencyid like ?";
            queryList.add("%" + agencyid + "%");
        }
        if(agencyname!=null&&!"".equals(agencyname)){
            sql+=" and i.dealername like ?";
            queryList.add("%" + agencyname + "%");
        }
        if(brand!=null&&!"".equals(brand)){
            sql+=" and t.brand = ?";
            queryList.add(brand);
        }
        if(namelike!=null&&!"".equals(namelike)){
            sql+=" and (t.username like ? or t.truename like ?)";
            queryList.add("%" + namelike + "%");
            queryList.add("%" + namelike + "%");
        }
        if(tel!=null&&!"".equals(tel)){
            sql+=" and t.tel like ?";
            queryList.add("%" + tel + "%");
        }
        if(valid>-1){
            sql+=" and t.valid = ?";
            queryList.add(valid);
        }
        if(isactived>-1){
            sql+=" and t.isactived = ?";
            queryList.add(isactived);
        }
        sql+=" order by t.createddate desc";

        PageInfo<Map<String,Object>> list= queryForPage(pageindex,pagesize,sql,queryList.toArray());
        return  list;
    }

    public  PageInfo<Map<String, Object>> selectAgencyAll2(int pageindex ,int pagesize,String agencyid,String brand, String namelike, int valid,int isactived){
        String sql ="select t.*,i.dealername as agencyname  from usermember t left join dealer i on t.agencyid=i.dealerno where 1=1  ";
        List<Object> queryList=new ArrayList<Object>();
        if(agencyid!=null&&!"".equals(agencyid)){
            sql+=" and t.agencyid like ?";
            queryList.add("%" + agencyid + "%");
        }
        if(brand!=null&&!"".equals(brand)){
            sql+=" and t.brand = ?";
            queryList.add(brand);
        }
        if(namelike!=null&&!"".equals(namelike)){
            sql+=" and (t.username like ? or t.truename like ? or i.dealername like ? or t.tel like ?)";
            queryList.add("%" + namelike + "%");
            queryList.add("%" + namelike + "%");
            queryList.add("%" + namelike + "%");
            queryList.add("%" + namelike + "%");
        }
        if(valid>-1){
            sql+=" and t.valid = ?";
            queryList.add(valid);
        }
        if(isactived>-1){
            sql+=" and t.isactived = ?";
            queryList.add(isactived);
        }
        sql+=" order by t.createddate desc";

        PageInfo<Map<String,Object>> list= queryForPage(pageindex,pagesize,sql,queryList.toArray());
        return  list;
    }

    public  List<Map<String, Object>> selectAgencyAllExport(String agencyid,String agencyname,String brand, String namelike, String tel, int valid,int isactived,String act){
        String sql ="select t.*,i.dealername as agencyname  from usermember t left join dealer i on t.agencyid=i.dealerno where 1=1  ";
        List<Object> queryList=new ArrayList<Object>();
        if(agencyid!=null&&!"".equals(agencyid)){
            sql+=" and t.agencyid like ?";
            queryList.add("%" + agencyid + "%");
        }
        if(agencyname!=null&&!"".equals(agencyname)){
            sql+=" and i.dealername like ?";
            queryList.add("%" + agencyname + "%");
        }
        if(brand!=null&&!"".equals(brand)){
            sql+=" and t.brand = ?";
            queryList.add(brand);
        }
        if(act!=null&&!"".equals(act)){
            sql+=" and t.act = ?";
            queryList.add(act);
        }
        if(namelike!=null&&!"".equals(namelike)){
            sql+=" and (t.username like ? or t.truename like ?)";
            queryList.add("%" + namelike + "%");
            queryList.add("%" + namelike + "%");
        }
        if(tel!=null&&!"".equals(tel)){
            sql+=" and t.tel like ?";
            queryList.add("%" + tel + "%");
        }
        if(valid>-1){
            sql+=" and t.valid = ?";
            queryList.add(valid);
        }
        if(isactived>-1){
            sql+=" and t.isactived = ?";
            queryList.add(isactived);
        }
        sql+=" order by t.createddate desc";

        List<Map<String,Object>> list= jdbcTemplate.queryForList(sql,queryList.toArray());
        return  list;
    }


    public  String getUserAct(JdbcTemplate jdbcTemplate, String id, String username)
    {
        if (id.equals(""))id="zb";
        String sqlstring="select act from usermember where userid=? and username=?";
        Object[] args=new Object[2];
        args[0]=id;
        args[1]=username;
        String act="";
        String resultss="";
        Map<String,Object>map=queryForMap(jdbcTemplate,sqlstring,args);
        if (map!=null)act=setting.NUllToSpace(map.get("act"));
        if (act.equals("服务顾问"))resultss=username;
        return  act;
    }

    /**
     * 修改阅读协议状态
     * @param id
     * @param conditions
     * @return
     */
    public int updateConditions(String id,int  conditions) {
        String sql = "update   usermember set conditions=?  where id=? ";
        int r = jdbcTemplate.update(sql, new Object[]{conditions, id});
        return r;
    }
    public  String getuserid(String data)
    {
        JsonObject jsonObject = new JsonParser().parse(data).getAsJsonObject();
        String appid="",nonce="",time="",sign="";
        int multi=0;
        String result="";
        if (jsonObject.keySet().contains("appid"))appid=jsonObject.get("appid").getAsString();
        if (jsonObject.keySet().contains("nonce"))nonce=jsonObject.get("nonce").getAsString();
        if (jsonObject.keySet().contains("time"))time=jsonObject.get("time").getAsString();
        if (jsonObject.keySet().contains("sign"))sign=jsonObject.get("sign").getAsString();

        if (jsonObject.keySet().contains("data"))jsonObject=jsonObject.get("data").getAsJsonObject();

        result=checkSign(nonce,time,sign,appid,data);
        if (!result.equals(""))return result;

        String username="", dealerno="", truename="";
        if (jsonObject.keySet().contains("username"))username=jsonObject.get("username").getAsString();
        else return  GetErrorString(3,"username参数错误！");
        if (jsonObject.keySet().contains("dealerno"))dealerno=jsonObject.get("dealerno").getAsString();
        else return  GetErrorString(3,"dealerno参数错误！");
        if (jsonObject.keySet().contains("truename"))truename=jsonObject.get("truename").getAsString();
        String userid="";
        String sql="select id from usermember where agencyid=? and username=?";
        Object[] args=new Object[2];
        args[0]=dealerno;
        args[1]=username;
        Map<String,Object>map=queryForMap(jdbcTemplate,sql,args);
        if (map!=null) {
            userid = setting.NUllToSpace(map.get("id"));

        }else
        {

            sql="insert into usermember (id,agencyid,username,truename) values (uuid(),?,?,?)";
            args=new Object[3];
            args[0]=dealerno;
            args[1]=username;
            args[2]=truename;
            jdbcTemplate.update(sql,args);
            sql="select id from usermember where agencyid=? and username=?";
            args=new Object[2];
            args[0]=dealerno;
            args[1]=username;
            map=queryForMap(jdbcTemplate,sql,args);
            if (map!=null) {
                userid = setting.NUllToSpace(map.get("id"));

            }
        }
        JsonObject jb=new JsonObject();
        jb.addProperty("userid",userid);
        return  jb.toString();

    }

}
