package com.bond.allianz.Dao;

import com.bond.allianz.entity.PageInfo;
import com.bond.allianz.utils.GuidUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 支付订单
 */
@Repository
public class PayOrderDao extends BaseDao {

    /**
     * 状态 -2 全部。  -1 过期, 0 下单,1 交易中, 2 交易失败,3  交易成功
     * @param dealerno
     * @param billno
     * @param state
     * @return
     */
    public  List<Map<String,Object>>  selectList (String dealerno,String  billno,int state){
        String sql="select * from payorder where 1= 1 ";
        List<Object> queryList=new ArrayList<Object>();
        if(dealerno!=null&&!"".equals(dealerno)){
            sql+=" and dealerno = ?";
            queryList.add(dealerno);
        }
        if(billno!=null&&!"".equals(billno)){
            sql+=" and billno = ?";
            queryList.add(billno);
        }
        if(state>-2){
            sql+=" and state = ?";
            queryList.add(state);
        }
        sql+=" order by createddate desc";

        List<Map<String, Object>> map =jdbcTemplate.queryForList(sql,queryList.toArray());
        return map;
    }

    /**
     * 分页查询
     * @param pageindex
     * @param pagesize
     * @param dealerno
     * @param billno
     * @param state 状态 -2 全部  .-1 过期, 0 下单,1 交易中, 2 交易失败,3  交易成功
     * @return
     */
    public PageInfo<Map<String,Object>> selectpage(int pageindex, int pagesize, String dealerno,String  billno,int state){
        String sql ="select * from payorder where 1=1 ";
        List<Object> queryList=new ArrayList<Object>();
        if(dealerno!=null&&!"".equals(dealerno)){
            sql+=" and dealerno = ?";
            queryList.add(dealerno);
        }
        if(billno!=null&&!"".equals(billno)){
            sql+=" and billno = ?";
            queryList.add(billno);
        }
        if(state>-2){
            sql+=" and state = ?";
            queryList.add(state);
        }
        sql+=" order by createddate desc";

        PageInfo<Map<String,Object>> list= queryForPage(pageindex,pagesize,sql,queryList.toArray());
        return  list;
    }


    /**
     * 添加订单
     * @param billno 账单编号
     * @param dealerno 经销商代码
     * @param payorderno 支付订单号
     * @param payamount 支付金额 元
     * @param paytime 订单日期 yyyyMMddHHmmss
     * @param state 状态-1 过期, 0 下单,1 交易中, 2 交易失败,3  交易成功
     * @param createddate 创建时间
     * @param orderoverdate 订单超时时间
     * @return
     */
    public int insertOrder(String billno, String dealerno, String payorderno, float payamount, String paytime, int state, Date createddate,Date orderoverdate,int type,int channel,int recharge){
        String sql="insert into  payorder(id,billno,dealerno,payorderno,payamount,paytime,state,createddate,orderoverdate,type,channel,recharge) values(?,?,?,?,?,?,?,?,?,?,?,?);";
        int r=jdbcTemplate.update(sql, new Object[]{ GuidUtil.newGuid(),billno,dealerno,payorderno,payamount,paytime,state,createddate,orderoverdate,type,channel,recharge});
        return  r;
    }

    /**
     * 根据订单no 查询
     * @param payorderno
     * @return
     */
    public  Map<String,Object> selectByPayOrderno(String payorderno){
        String sql ="select * from payorder where payorderno=? ";
        Map<String,Object> target=jdbcTemplate.queryForMap(sql,payorderno);
        return  target;
    }

    /**
     * 获取待交易和成功的
     * @param billno
     * @return
     */
    public  List<Map<String,Object>> selectBybillno(String billno){
        String sql ="select * from payorder where billno=? and state in (0,1,3) and ((orderoverdate>now() and channel!=2) or channel=2) order by createddate desc ";
        List<Map<String,Object>> target=jdbcTemplate.queryForList(sql,billno);
        return  target;
    }

    /**
     * 修改订单回调状态
     * @param payorderno 支付订单号
     * @param queryid 查询流水号
     * @param traceno 银联系统跟踪号
     * @param tracetime 交易传输时间  MMddHHmmss
     * @param state 状态-1 过期, 0 下单,1 交易中, 2 交易失败,3  交易成功
     * @param backdate 回调处理时间
     * @return
     */
    public int updateOrderState(String payorderno,String queryid,String traceno,String tracetime,int state,Date backdate){
        String sql="update   payorder set queryid=? ,traceno=?,tracetime=?,state=?,backdate=? where payorderno=? ";
        int r=jdbcTemplate.update(sql, new Object[]{ queryid,traceno,tracetime,state,backdate,payorderno});
        return  r;
    }

    /**
     * 修改订单状态  状态-1 过期, 0 下单,1 交易中, 2 交易失败,3  交易成功
     * @param payorderno
     * @param queryid 流水号
     * @param state
     * @return
     */
    public int updateOrderStateIn(String payorderno,String queryid,int state){
        String sql="update   payorder set queryid=? ,state=? where payorderno=? ";
        int r=jdbcTemplate.update(sql, new Object[]{ queryid,state,payorderno});
        return  r;
    }

    /**
     * 修改订单状态 状态-1 过期, 0 下单,1 交易中, 2 交易失败,3  交易成功
     * @param payorderno
     * @param traceno 支付系统跟踪号
     * @param tracetime 支付时间
     * @param state
     * @param backdate 回调时间
     * @return
     */
    public int updateOrderStateEnd(String payorderno,String traceno,String tracetime,int state,Date backdate){
        String sql="update   payorder set  traceno=?,tracetime=?,state=?,backdate=? where payorderno=? ";
        int r=jdbcTemplate.update(sql, new Object[]{ traceno,tracetime,state,backdate,payorderno});
        return  r;
    }

    /**
     * 修改状态 状态-1 过期, 0 下单,1 交易中, 2 交易失败,3  交易成功
     * @param payorderno
     * @param state 状态-1 过期, 0 下单,1 交易中, 2 交易失败,3  交易成功
     * @return
     */
    public int updateState(String payorderno,int state){
        String sql="update   payorder set state=? where payorderno=? ";
        int r=jdbcTemplate.update(sql, new Object[]{ state,payorderno});
        return  r;
    }
    public int updateChannel(String payorderno,int channel){
        String sql="update   payorder set channel=? where payorderno=? ";
        int r=jdbcTemplate.update(sql, new Object[]{ channel,payorderno});
        return  r;
    }

    /**
     * 过期状态修改
     * @param currentdate 当前时间
     * @return
     */
    public int updateOverState(Date currentdate) {
        String sql = "update   payorder set state=-1 where  orderoverdate<? and state=0  ";
        int r = jdbcTemplate.update(sql, new Object[]{currentdate});
        return r;
    }

}
