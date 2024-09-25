package com.bond.allianz.controller;

import com.bond.allianz.Dao.logs;
import com.bond.allianz.service.*;
import com.bond.allianz.sdk.AcpService;
import com.bond.allianz.sdk.LogUtil;
import com.bond.allianz.sdk.SDKConstants;
import com.pingplusplus.model.*;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.X509EncodedKeySpec;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.commons.codec.binary.Base64;

@RestController
@RequestMapping("/notice")
public class NoticeController  extends BaseController{

    @Value("${pay.pubkeypath}")
    private String pingpubkeypath;

    @Autowired
    private PayOrderService payOrderService;
    @Autowired
    private BillService billService;
    @Autowired
    private ContractService contractService;
    @Autowired
    private BmwService bmwService;
    @Autowired
    private PayRequestService payRequestService;

    /**
     * 前台通知地址 （需设置为外网能访问 http https均可），支付成功后的页面 点击“返回商户”按钮的时候将异步通知报文post到该地址
     */
    @RequestMapping(value = "/payfronturl")
    public ModelAndView payfronturl(){
        ModelAndView mv = new ModelAndView();
        String result="";
        try {
            LogUtil.writeLog("FrontRcvResponse前台接收报文返回开始");

            String encoding = request.getParameter(SDKConstants.param_encoding);
            LogUtil.writeLog("返回报文中encoding=[" + encoding + "]");
            Map<String, String> respParam = getAllRequestParam(request);

            // 打印请求报文
            LogUtil.printRequestLog(respParam);

            Map<String, String> valideData = null;
            if (null != respParam && !respParam.isEmpty()) {
                Iterator<Map.Entry<String, String>> it = respParam.entrySet()
                        .iterator();
                valideData = new HashMap<String, String>(respParam.size());
                while (it.hasNext()) {
                    Map.Entry<String, String> e = it.next();
                    String key = (String) e.getKey();
                    String value = (String) e.getValue();
                    value = new String(value.getBytes(encoding), encoding);
                    valideData.put(key, value);
                }
            }
            if (!AcpService.validate(valideData, encoding)) {
                LogUtil.writeLog("验证签名结果[失败].");
                result="验证签名失败";
            } else {
                LogUtil.writeLog("验证签名结果[成功].");
                String payorderno=valideData.get("orderId");//订单号

                String respCode = valideData.get("respCode");
                if("00".equals(respCode)) { //成功
                    result="支付成功";
                }
                //判断respCode=00、A6后，对涉及资金类的交易，请再发起查询接口查询，确定交易成功后更新数据库。
            }
            LogUtil.writeLog("FrontRcvResponse前台接收报文返回结束");

            logs.info("payfronturl前台返回参数:"+JsonSerializer(respParam),"pay");

        }
        catch (Exception ex){
            logs.error(ex.toString(), "支付前台通知错误");
        }
        mv.addObject("result", result);
        return  mv;
    }

    /**
     * 后台通知地址（需设置为【外网】能访问 http https均可），支付成功后银联会自动将异步通知报文post到商户上送的该地址，失败的交易银联不会发送后台通知
     * //注意:1.需设置为外网能访问，否则收不到通知    2.http https均可  3.收单后台通知后需要10秒内返回http200或302状态码
     * 		//    4.如果银联通知服务器发送通知后10秒内未收到返回状态码或者应答码非http200，那么银联会间隔一段时间再次发送。总共发送5次，每次的间隔时间为0,1,2,4分钟。
     * 		//    5.后台通知地址如果上送了带有？的参数，例如：http://abc/web?a=b&c=d 在后台通知处理程序验证签名之前需要编写逻辑将这些字段去掉再验签，否则将会验签失败
     */
    @RequestMapping(value = "/paybackurl")
    public void paybackurl(){
        try {
            LogUtil.writeLog("BackRcvResponse接收后台通知开始");

            String encoding = request.getParameter(SDKConstants.param_encoding);
            // 获取银联通知服务器发送的后台通知参数
            Map<String, String> reqParam = getAllRequestParam(request);
            LogUtil.printRequestLog(reqParam);

            String respCode = reqParam.get("respCode");
            Date backdate=new Date();
            //重要！验证签名前不要修改reqParam中的键值对的内容，否则会验签不过
            if (!AcpService.validate(reqParam, encoding)) {
                LogUtil.writeLog("验证签名结果[失败].");
                logs.error("支付后台通知验证签名结果[失败]","pay");
                //验签失败，需解决验签问题
            } else {
                LogUtil.writeLog("验证签名结果[成功].");
                //【注：为了安全验签成功才应该写商户的成功处理逻辑】交易成功，更新商户订单状态

                String payorderno=reqParam.get("orderId");//订单号
                String queryId=reqParam.get("queryId");//查询流水号
                String traceno=reqParam.get("traceNo");//银联系统跟踪号
                String tracetime=reqParam.get("traceTime");//交易传输时间  MMddHHmmss
                //判断respCode=00、A6后，对涉及资金类的交易，请再发起查询接口查询，确定交易成功后更新数据库。
                if("00".equals(respCode)){ //成功
                    payOrderService.updateOrderState(payorderno, queryId, traceno, tracetime, 3, backdate);
                    Map<String,Object> pay=payOrderService.selectByPayOrderno(payorderno);
                    billService.updateStateByKey(pay.get("billno").toString(),"已支付",(float)pay.get("payamount"),pay.get("tracetime").toString(),payorderno);
                    contractService.updateStateByBillno(pay.get("billno").toString());
                }else {
                    payOrderService.updateOrderState(payorderno, queryId, traceno, tracetime, 2, backdate);
                }
            }
            LogUtil.writeLog("BackRcvResponse接收后台通知结束");

            logs.info("paybackurl后台返回参数:"+JsonSerializer(reqParam),"pay");
            //返回给银联服务器http 200  状态码
            response.getWriter().print("ok");
        }
        catch (Exception ex){
            logs.error(ex.toString(), "支付后台通知错误");
        }
    }

    /**
     * 获取文件内容
     * @param filePath
     * @return
     * @throws Exception
     */
    public  String getStringFromFile(String filePath) throws Exception {
        FileInputStream in = new FileInputStream(filePath);
        InputStreamReader inReader = new InputStreamReader(in, "UTF-8");
        BufferedReader bf = new BufferedReader(inReader);
        StringBuilder sb = new StringBuilder();
        String line;
        do {
            line = bf.readLine();
            if (line != null) {
                if (sb.length() != 0) {
                    sb.append("\n");
                }
                sb.append(line);
            }
        } while (line != null);

        return sb.toString();
    }

    /**
     * 获取公钥内容
     * @return
     * @throws Exception
     */
    public  PublicKey getPubKey() throws Exception {
        String pubKeyString = getStringFromFile(pingpubkeypath);
        pubKeyString = pubKeyString.replaceAll("(-+BEGIN PUBLIC KEY-+\\r?\\n|-+END PUBLIC KEY-+\\r?\\n?)", "");
        byte[] keyBytes = Base64.decodeBase64(pubKeyString.getBytes("UTF-8"));

        // generate public key
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        return keyFactory.generatePublic(spec);
    }

    /**
     * 签名验证
     * @param dataString
     * @param signatureString
     * @param publicKey
     * @return
     */
    public  boolean verifyData(String dataString, String signatureString, PublicKey publicKey) {
        try {
            byte[] signatureBytes = Base64.decodeBase64(signatureString);
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(dataString.getBytes("UTF-8"));
            return signature.verify(signatureBytes);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    /**
     * ping++  通知
     */
    @RequestMapping(value = "/webhooks")
    public void webhooks(){
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(request.getInputStream(),"UTF-8"));
            String line = "";
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
            String webhooksRawPostData = sb.toString();
            String signature="";//签名
            Enumeration headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) { //忽略大小写处理
                String key = (String) headerNames.nextElement();
                String value = request.getHeader(key);
                if ("X-Pingplusplus-Signature".toLowerCase().equals(key.toLowerCase())) {
                    signature = value;
                    break;
                }
            }
            logs.info("webhooks后台返回参数:"+webhooksRawPostData+",signature="+signature,"pay");
            //boolean verify=true;
            boolean verify = verifyData(webhooksRawPostData, signature, getPubKey());
            if(verify){
                Event ev=Webhooks.eventParse(webhooksRawPostData);
                PingppObject pobject=Webhooks.getObject(webhooksRawPostData);
                if("charge.succeeded".equals(ev.getType())||"transfer.succeeded".equals(ev.getType())){ //支付成功
                    long t=0;
                    String orderno="";
                    String id="";
                    String tranno="";
                    boolean flag=false;
                    if("charge.succeeded".equals(ev.getType())){
                        Charge charge=(Charge)pobject;
                        t=charge.getTimePaid();
                        id=charge.getId();
                        tranno=charge.getTransactionNo();
                        orderno=charge.getOrderNo();
                        flag=charge.getPaid();
                    }
                    Date trackdate= new Date(t*1000);
                    if(flag) {
                        payOrderService.updateOrderState(orderno, id, tranno, new SimpleDateFormat("MMddHHmmss").format(trackdate), 3, new Date());
                        Map<String,Object> pay=payOrderService.selectByPayOrderno(orderno);
                        if("1".equals(pay.get("recharge").toString())){ //充值模式
                            Map<String,Object> payrequest= payRequestService.selectByid(pay.get("billno").toString());
                            String tradechannel="";
                            switch ((int)pay.get("channel")){
                                case 0:
                                    tradechannel="支付宝";
                                    break;
                                case 1:
                                    tradechannel="微信";
                                    break;
                                case 2:
                                    tradechannel="银联";
                                    break;
                                case 3:
                                    tradechannel="银联企业";
                                    break;
                            }
                            bmwService.paysuccess(pay.get("billno").toString(),(float)Float.parseFloat(payrequest.get("amount").toString()),tradechannel,tranno,"");
                        }else { //支付模式
                            billService.updateStateByKey(pay.get("billno").toString(), "已支付", (float) Float.parseFloat(pay.get("payamount").toString()), pay.get("tracetime").toString(), orderno);
                            contractService.updateStateByBillno(pay.get("billno").toString());
                        }
                        //order_no
                        //time_paid 10位 完成时的 Unix 时间戳
                        //charge.getTransactionNo() 支付渠道返回的交易流水号。
                        //paid true 是否已付款
                        //id  查询流水号
                        logs.info("支付成功", "pay");
                    }else{
                        payOrderService.updateOrderState(orderno, id, tranno, new SimpleDateFormat("MMddHHmmss").format(trackdate), 2, new Date());
                    }
                }else if("refund.succeeded".equals(ev.getType())) {//退款成功
                    Refund refund = (Refund) pobject;
                    //String chargeid= refund.getCharge();//chargeid
                    Map<String, Object> pay = payOrderService.selectByPayOrderno(refund.getChargeOrderNo());
                    Map<String, Object> payrequest = payRequestService.selectByid(pay.get("billno").toString());
                    //修改退款状态
                    payOrderService.updateState(refund.getChargeOrderNo(),4);
                    //退款回调
                    logs.info("ping++发起退款回调开始，contractno="+payrequest.get("contractno").toString(), "payrefund");
                    bmwService.paybacksuccess(payrequest.get("contractno").toString());
                    logs.info("ping++退款回调成功，contractno="+payrequest.get("contractno").toString(), "payrefund");
                    //logs.info(refund.toString(), "payrefund");
                }
            }else{
                logs.error("ping++支付后台通知验证签名结果[失败]","pay");
                response.getWriter().write("pingxx");
                response.getWriter().close();
            }
        }
        catch (Exception ex){
            logs.error(ex.toString(), "pay");
            ex.printStackTrace();
        }

    }

    /**
     * 检查订单是否支付成功 二维码支付
     * @param payorderno
     * @return
     */
    @RequestMapping(value = "/checkorder")
    public  String checkorder(String payorderno){
        String result="false";
        try {
            Map<String, Object> pay = payOrderService.selectByPayOrderno(payorderno);
            if("3".equals(pay.get("state").toString())){
                result="true";
            }
        }
        catch (Exception ex){
            result="false";
        }
        return  result;
    }
    @RequestMapping(value = "/paysuccess")
    public  ModelAndView paysuccess(){
        ModelAndView mv = new ModelAndView();
        return mv;
    }
}
