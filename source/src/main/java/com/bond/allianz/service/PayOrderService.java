package com.bond.allianz.service;

import com.bond.allianz.Dao.AgencyDao;
import com.bond.allianz.Dao.PayOrderDao;
import com.bond.allianz.entity.PageInfo;
import com.bond.allianz.utils.GuidUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class PayOrderService {
    @Autowired
    private PayOrderDao payOrderDao ;

    /**
     * 状态 -2 全部。  -1 过期, 0 下单,1 交易中, 2 交易失败,3  交易成功
     * @param dealerno
     * @param billno
     * @param state
     * @return
     */
    public  List<Map<String,Object>>  selectList (String dealerno,String  billno,int state){
        return  payOrderDao.selectList(dealerno,billno,state);
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
        return  payOrderDao.selectpage(pageindex,pagesize,dealerno,billno,state);
    }


    /**
     * 根据订单no 查询
     * @param payorderno
     * @return
     */
    public  Map<String,Object> selectByPayOrderno(String payorderno){
        return  payOrderDao.selectByPayOrderno(payorderno);
    }
    public  List<Map<String,Object>> selectBybillno(String billno){
        return  payOrderDao.selectBybillno(billno);
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
     * @param type 接口类型  0 银联，1 ping++
     * @param channel ping++支付渠道 0支付宝，1 微信
     * @param recharge 是否充值模式 0 否，1是
     * @return
     */
    public int insertOrder(String billno, String dealerno, String payorderno, float payamount, String paytime, int state, Date createddate, Date orderoverdate,int type,int channel,int recharge){
        return  payOrderDao.insertOrder(billno,dealerno,payorderno,payamount,paytime,state,createddate,orderoverdate,type,channel,recharge);
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
    public int updateOrderState(String payorderno,String queryid,String traceno,String tracetime,int state,Date backdate) {
        return payOrderDao.updateOrderState(payorderno, queryid, traceno, tracetime, state, backdate);
    }

    /**
     * 修改订单状态  状态-1 过期, 0 下单,1 交易中, 2 交易失败,3  交易成功
     * @param payorderno
     * @param queryid 流水号
     * @param state
     * @return
     */
    public int updateOrderStateIn(String payorderno,String queryid,int state) {
        return payOrderDao.updateOrderStateIn(payorderno, queryid, state);
    }
    /**
     * 修改订单状态 状态-1 过期, 0 下单,1 交易中, 2 交易失败,3  交易成功 ,4 退款成功
     * @param payorderno
     * @param traceno 支付系统跟踪号
     * @param tracetime 支付时间
     * @param state
     * @param backdate 回调时间
     * @return
     */
    public int updateOrderStateEnd(String payorderno,String traceno,String tracetime,int state,Date backdate) {
        return payOrderDao.updateOrderStateEnd(payorderno, traceno, tracetime, state, backdate);
    }
    /**
     * 修改状态 状态-1 过期, 0 下单,1 交易中, 2 交易失败,3  交易成功 ,4 退款成功
     * @param payorderno
     * @param state 状态-1 过期, 0 下单,1 交易中, 2 交易失败,3  交易成功 ,4 退款成功
     * @return
     */
    public int updateState(String payorderno,int state){
        return payOrderDao.updateState(payorderno,state);
    }
    public int updateChannel(String payorderno,int channel){
        return payOrderDao.updateChannel(payorderno,channel);
    }
    /**
     * 过期状态修改
     * @param currentdate 当前时间
     * @return
     */
    public int updateOverState(Date currentdate) {
        return payOrderDao.updateOverState(currentdate);
    }


}
