package com.bond.allianz.Dao;

import com.bond.allianz.entity.PageInfo;
import com.bond.allianz.utils.GuidUtil;
import com.pingplusplus.model.Refund;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

/**
 * 充值
 */
@Repository
public class PayRequestDao extends BaseDao {



    /**
     * 根据id 查询
     * @param id
     * @return
     */
    public  Map<String,Object> selectByid(String id){
        String sql ="select * from payrequest where id=? ";
        Map<String,Object> target=jdbcTemplate.queryForMap(sql,id);
        return  target;
    }

    /**
     * 查询付款成功的请求单号
     * @param contractno
     * @return
     */
    public  Map<String,Object> selectDoneByContractno(String contractno){
        String sql ="select * from payrequest t where contractno=? and exists(select 1 from payorder where billno=t.id and state=3) ";
        Map<String,Object> target=jdbcTemplate.queryForMap(sql,contractno);
        return  target;
    }
    public  Map<String,Object> selectOrderBybillno(String billno){
        String sql ="select * from payorder where billno=? ";
        Map<String,Object> target=jdbcTemplate.queryForMap(sql,billno);
        return  target;
    }
    public  Map<String,Object> selectOrderSuccBybillno(String billno){
        String sql ="select * from payorder where billno=? and state=3";
        Map<String,Object> target=jdbcTemplate.queryForMap(sql,billno);
        return  target;
    }

    public  Map<String,Object> selectByPayContractno(String contractno){
        String sql ="select * from contract where contractno=? ";
        if (contractno.startsWith("MT"))sql ="select * from mt_contract where contractno=? ";
        Map<String,Object> target=jdbcTemplate.queryForMap(sql,contractno);
        return  target;
    }
    /**
     * bmw支付退款
     * @param contractno
     * @return
     */
    public  boolean  rechargeRefunds(String contractno){
        try{
            Map<String ,Object> payrequest=selectDoneByContractno(contractno);
            //Map<String ,Object> payorder=selectOrderBybillno(payrequest.get("id").toString());
            Map<String ,Object> payorder=selectOrderSuccBybillno(payrequest.get("id").toString());
            Map<String ,Object> contract=selectByPayContractno(contractno);

            Map<String, Object> params = new HashMap<String, Object>();
            params.put("description", "凭证发起退款");
            params.put("amount", new BigDecimal(contract.get("backamount").toString()).multiply(new BigDecimal(100)));// 退款的金额, 单位为对应币种的最小货币单位，例如：人民币为分（如退款金额为 1 元，此处请填 100）。必须小于等于可退款金额，默认为全额退款

            logs.info("发起ping++退款请求开始,contractno="+contractno, "payrefund");
            Refund refund =Refund.create(payorder.get("queryid").toString(), params);
            logs.info("发起ping++退款请求成功,contractno="+contractno+params.toString(), "payrefund");
        }
        catch (Exception ex){
            ex.printStackTrace();
            logs.error("发起ping++退款错误,contractno="+contractno+","+ex.getMessage() ,"payrefund");
            logs.error("退款错误:", ex);
        }
        return true;
    }



}
