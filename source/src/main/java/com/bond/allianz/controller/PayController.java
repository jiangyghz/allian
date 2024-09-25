package com.bond.allianz.controller;

import com.bond.allianz.Dao.logs;
import com.bond.allianz.entity.JsonResult;
import com.bond.allianz.service.BillService;
import com.bond.allianz.service.PayOrderService;
import com.bond.allianz.sdk.AcpService;
import com.bond.allianz.sdk.LogUtil;
import com.bond.allianz.sdk.SDKConfig;
import com.bond.allianz.service.PayRequestService;
import com.pingplusplus.model.Charge;
import com.pingplusplus.model.Transfer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pay")
public class PayController extends BaseController {

    @Value("${pay.merid}")
    private String paymerId;

    @Value("${pay.pingappid}")
    private String payPingAppId;



    @Autowired
    private BillService billService;

    @Autowired
    private PayOrderService payOrderService;
    @Autowired
    private PayRequestService payRequestService;


    @RequestMapping(value = "/cpb2b")
    public ModelAndView cpb2b() {
        ModelAndView mv = new ModelAndView();
        return  mv;
    }
    /**
     * 订单
     * @return
     */

    /**
     * 订单创建 并提交
     */
    @RequestMapping(value = "/order")
    public void orderpost(String billno) {
        try {
            //int type = ParamsInt("type", 0);//支付接口 0 银联，1 ping++
            int channel = ParamsInt("channel", 0);//ping++ 支付方式 0支付宝扫码，1 微信扫码 ，2 银联电子网银支付 3 银联电子企业网关支付
            //int recharge=ParamsInt("recharge");//是否是充值模式
            Map<String, Object> bill = billService.selectByKey(billno);
            if (bill.get("confirmamount") == null || "".equals(bill.get("confirmamount").toString())) {
                return;
            }
            float confirmamount = ParseFloat(bill.get("confirmamount").toString());
            if (confirmamount == 0) {
                return;
            }
            Date createddate = new Date();//创建时间
            String payorderno = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(createddate);//订单号
            if(channel==3){//企业网银支付只能是同一个订单号
                List<Map<String, Object>> paylist= payOrderService.selectList("", billno,-1);
                if(paylist.size()>0){
                    payorderno=paylist.get(0).get("payorderno").toString();
                }
            }
            String paytime = new SimpleDateFormat("yyyyMMddHHmmss").format(createddate);//订单日期 查询会使用到
            Date orderoverdate = new Date(createddate.getTime() + 15 * 60 * 1000);////订单过期时间


            int r = payOrderService.insertOrder(billno, bill.get("dealerno").toString(), payorderno, confirmamount, paytime, 0, createddate, orderoverdate,1,channel,0);
            if (r > 0) {
//                if (type == 0) { //银联支付
//                    payorder(payorderno);
//                } else if (type == 1) { //ping++支付
                    payorderping(payorderno,channel);
//                }
            }
        } catch (Exception ex) {
            logs.error("下单错误参数:", ex);
        }
    }

    /**
     * 充值
     * @param payrequestid
     */
    @RequestMapping(value = "/recharge")
    public void rechargepost(String payrequestid) {
        try {
            int channel = ParamsInt("channel", 0);//ping++ 支付方式 0支付宝扫码，1 微信扫码 ，2 银联电子企业网银支付 ,3 银联电子企业网关支付
            Map<String, Object> pay = payRequestService.selectByid(payrequestid);

            logs.info("recharge充值参数:payrequestid="+payrequestid+",channel="+channel+",金额="+pay.get("amount"),"pay");
            if (pay.get("amount") == null || "".equals(pay.get("amount").toString())) {
                return;
            }
            float amount = ParseFloat(pay.get("amount").toString());
            if (amount == 0) {
                return;
            }
            Date createddate = new Date();//创建时间
            String payorderno = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(createddate);//订单号
            String paytime = new SimpleDateFormat("yyyyMMddHHmmss").format(createddate);//订单日期 查询会使用到
            Date orderoverdate = new Date(createddate.getTime() + 15 * 60 * 1000);////订单过期时间

            List<Map<String,Object>> list=payOrderService.selectBybillno(payrequestid);

            logs.info("recharge充值参数:订单,"+(list.size()>0?"有":"否"),"pay");
            if(list.size()==0) {

                int r = payOrderService.insertOrder(payrequestid, pay.get("dealerno").toString(), payorderno, amount, paytime, 0, createddate, orderoverdate, 1, channel, 1);
                if (r > 0) {
                    payorderping(payorderno, channel);
                }
            }else{
                payorderno=list.get(0).get("payorderno").toString();
                payOrderService.updateChannel(payorderno,channel);
                payorderping(payorderno, channel);
            }
        } catch (Exception ex) {
            logs.error("下单错误参数:", ex);
        }
    }


    /**
     * 银联订单支付
     * @param payorderno
     */
    @RequestMapping(value = "/payorder")
    public void payorder(String payorderno) {
        Map<String, String> requestData = new HashMap<String, String>();
        try {

            Map<String, Object> pay = payOrderService.selectByPayOrderno(payorderno);
            if (pay.get("payamount")==null||"".equals(pay.get("payamount").toString())) {
                return;
            }
            if(!"0".equals(pay.get("state").toString())){ //下单中才能继续支付
                return;
            }
            float payamount = ParseFloat(pay.get("payamount").toString());
            if (payamount == 0) {
                return;
            }
            String txnAmt = String.valueOf((int)(payamount * 100));//单位分
            String paytime = pay.get("paytime").toString();//订单日期 查询会使用到
            Date orderoverdate =(Date)pay.get("orderoverdate") ;//订单过期时间
            if(orderoverdate.getTime()<new Date().getTime()){ //过期了
                payOrderService.updateState(payorderno,-1);
                return;
            }
            String payTimeout = new SimpleDateFormat("yyyyMMddHHmmss").format(orderoverdate);//订单过期时间

            int r = payOrderService.updateState(payorderno,1); //修改状态为交易中
            if (r > 0) {

                response.setContentType("text/html; charset=UTF-8");
                /***银联全渠道系统，产品参数，除了encoding自行选择外其他不需修改***/
                requestData.put("version", SDKConfig.getConfig().getVersion());              //版本号，全渠道默认值
                requestData.put("encoding", "UTF-8");              //字符集编码，可以使用UTF-8,GBK两种方式
                requestData.put("signMethod", SDKConfig.getConfig().getSignMethod()); //签名方法
                requestData.put("txnType", "01");                          //交易类型 ，01：消费
                requestData.put("txnSubType", "01");                          //交易子类型， 01：自助消费
                requestData.put("bizType", "000201");                      //业务类型，B2C网关支付，手机wap支付
                requestData.put("channelType", "07");                      //渠道类型，这个字段区分B2C网关支付和手机wap支付；07：PC,平板  08：手机

                /***商户接入参数***/
                requestData.put("merId", paymerId);                              //商户号码，请改成自己申请的正式商户号或者open上注册得来的777测试商户号
                requestData.put("accessType", "0");                          //接入类型，0：直连商户
                requestData.put("orderId", payorderno);             //商户订单号，8-40位数字字母，不能含“-”或“_”，可以自行定制规则
                requestData.put("txnTime", paytime);        //订单发送时间，取系统时间，格式为yyyyMMddHHmmss，必须取当前时间，否则会报txnTime无效
                requestData.put("currencyCode", "156");                      //交易币种（境内商户一般是156 人民币）
                requestData.put("txnAmt", txnAmt);                              //交易金额，单位分，不要带小数点
                //requestData.put("reqReserved", "透传字段");        		      //请求方保留域，如需使用请启用即可；透传字段（可以实现商户自定义参数的追踪）本交易的后台通知,对本交易的交易状态查询交易、对账文件中均会原样返回，商户可以按需上传，长度为1-1024个字节。出现&={}[]符号时可能导致查询接口应答报文解析失败，建议尽量只传字母数字并使用|分割，或者可以最外层做一次base64编码(base64编码之后出现的等号不会导致解析失败可以不用管)。

                requestData.put("riskRateInfo", "{commodityName=账单" + pay.get("billno").toString()+"}");

                //前台通知地址 （需设置为外网能访问 http https均可），支付成功后的页面 点击“返回商户”按钮的时候将异步通知报文post到该地址
                //如果想要实现过几秒中自动跳转回商户页面权限，需联系银联业务申请开通自动返回商户权限
                //异步通知参数详见open.unionpay.com帮助中心 下载  产品接口规范  网关支付产品接口规范 消费交易 商户通知
                requestData.put("frontUrl", SDKConfig.getConfig().getFrontUrl());

                //后台通知地址（需设置为【外网】能访问 http https均可），支付成功后银联会自动将异步通知报文post到商户上送的该地址，失败的交易银联不会发送后台通知
                //后台通知参数详见open.unionpay.com帮助中心 下载  产品接口规范  网关支付产品接口规范 消费交易 商户通知
                //注意:1.需设置为外网能访问，否则收不到通知    2.http https均可  3.收单后台通知后需要10秒内返回http200或302状态码
                //    4.如果银联通知服务器发送通知后10秒内未收到返回状态码或者应答码非http200，那么银联会间隔一段时间再次发送。总共发送5次，每次的间隔时间为0,1,2,4分钟。
                //    5.后台通知地址如果上送了带有？的参数，例如：http://abc/web?a=b&c=d 在后台通知处理程序验证签名之前需要编写逻辑将这些字段去掉再验签，否则将会验签失败
                requestData.put("backUrl", SDKConfig.getConfig().getBackUrl());

                // 订单超时时间。
                // 超过此时间后，除网银交易外，其他交易银联系统会拒绝受理，提示超时。 跳转银行网银交易如果超时后交易成功，会自动退款，大约5个工作日金额返还到持卡人账户。
                // 此时间建议取支付时的北京时间加15分钟。
                // 超过超时时间调查询接口应答origRespCode不是A6或者00的就可以判断为失败。
                // 商户系统内订单过期时间
                requestData.put("payTimeout", payTimeout);

                //////////////////////////////////////////////////
                //
                //       报文中特殊用法请查看 PCwap网关跳转支付特殊用法.txt
                //
                //////////////////////////////////////////////////

                /**请求参数设置完毕，以下对请求参数进行签名并生成html表单，将表单写入浏览器跳转打开银联页面**/
                Map<String, String> submitFromData = AcpService.sign(requestData, "UTF-8");  //报文中certId,signature的值是在signData方法中获取并自动赋值的，只要证书配置正确即可。

                String requestFrontUrl = SDKConfig.getConfig().getFrontRequestUrl();  //获取请求银联的前台地址：对应属性文件acp_sdk.properties文件中的acpsdk.frontTransUrl
                String html = AcpService.createAutoFormHtml(requestFrontUrl, submitFromData, "UTF-8");   //生成自动跳转的Html表单
                //调试记录
                LogUtil.writeLog("打印请求HTML，此为请求报文，为联调排查问题的依据：" + html);
                //日志记录
                logs.info("下单支付参数:" + JsonSerializer(requestData), "pay");
                //将生成的html写到浏览器中完成自动跳转打开银联支付页面；这里调用signData之后，将html写到浏览器跳转到银联页面之前均不能对html中的表单项的名称和值进行修改，如果修改会导致验签不通过
                response.getWriter().write(html);
            }
        } catch (Exception ex) {
            logs.error("支付错误:", ex);
        }
    }


    /**
     * ping++ 支付  返回二维码的图片地址
     * @param payorderno
     */
    @RequestMapping(value = "/payorderping")
    public void payorderping(String payorderno,int paychannel){

        Map<String, Object> pay = payOrderService.selectByPayOrderno(payorderno);

        logs.info("支付订单,金额="+pay.get("payamount")+",state="+pay.get("state").toString()+",paychannel="+paychannel,"pay");
        if (pay.get("payamount")==null||"".equals(pay.get("payamount").toString())) {
            return;
        }
        if(!("0".equals(pay.get("state").toString())||"1".equals(pay.get("state").toString()))){ //下单中才能继续支付
            return;
        }
        float payamount = ParseFloat(pay.get("payamount").toString());
        if (payamount == 0) {
            return;
        }
            String channel = "alipay_pc_direct";
            switch (paychannel){
                case 0: //支付宝电脑网站支付
                    channel="alipay_pc_direct";//alipay_qr
                    break;
                case 1://微信扫码
                    channel="wx_pub_qr";
                    break;
                case 2://银联网关支付（银联 PC 网页支付）
                    channel="upacp_pc";//  cp_b2b    upacp_pc
                    break;
                case 3://银联电子企业网关支付
                    channel="cp_b2b";//  cp_b2b  upacp_b2b  upacp_pc
                    break;
//                case 4://alipay 支付宝 转账到支付宝账户
//                    channel="alipay";
//                    break;
            }
            if(paychannel==4){ //转账
                Transfer transfer = null;

                Map<String, Object> transferMap = new HashMap<String, Object>();
                transferMap.put("channel", channel);
                transferMap.put("order_no", payorderno);
                transferMap.put("amount", (int) (payamount * 100)); // 付款金额，相关渠道的限额，请查看 https://help.pingxx.com/article/133366/ 。单位为对应币种的最小货币单位，例如：人民币为分。
                transferMap.put("type", "b2b"); // 付款类型，转账到个人用户为 b2c，转账到企业用户为 b2b（微信公众号 wx_pub 的企业付款，仅支持 b2c）。
                transferMap.put("currency", "cny");
                transferMap.put("recipient", channelRecipient(channel)); // 接收者
                // 备注信息。
                // 渠道为 unionpay 时，最多 99 个 Unicode 字符；
                // 渠道为 wx_pub 时，最多 99 个英文和数字的组合或最多 33 个中文字符，不可以包含特殊字符；
                // 渠道为 alipay 时，最多 100 个 Unicode 字符。
                // 渠道为 jdpay 最多100个 Unicode 字符。
                // 渠道为 allinpay 最多30个 Unicode 字符
                transferMap.put("description", "账单" + payorderno);

                transferMap.put("extra", channelExtra(channel,""));

                Map<String, String> app = new HashMap<String, String>();
                app.put("id", payPingAppId);
                transferMap.put("app", app);

                try {
                    response.setHeader("Content-type", "text/html;charset=UTF-8");
                    request.setCharacterEncoding("UTF-8");
                    //发起交易请求
                    transfer = Transfer.create(transferMap);
                    payOrderService.updateOrderStateIn(payorderno, transfer.getId(), 1);
                    // 传到客户端请先转成字符串 .toString(), 调该方法，会自动转成正确的 JSON 字符串
                    String transferString = transfer.toString();
                        JsonResult result = new JsonResult();
                        result.code = 0;
                        result.msg = "";
                        Map<String, Object> d = new HashMap<String, Object>();
                        d.put("payorderno", payorderno);
                        d.put("transfer", transferString);
                        result.data = d;
                        response.getWriter().write(JsonSerializer(result));
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        logs.error("转账transfer错误:", e);
                        JsonResult result = new JsonResult();
                        result.code = 1;
                        result.msg = e.getMessage();
                        result.data = "";
                        response.getWriter().write(JsonSerializer(result));
                    } catch (Exception ex) {

                    }
                }
            }
            else { //支付
                Charge charge = null;
                Map<String, Object> chargeMap = new HashMap<String, Object>();
                chargeMap.put("amount", (int) (payamount * 100));//订单总金额, 人民币单位：分（如订单总金额为 1 元，此处请填 100）
                chargeMap.put("currency", "cny");
                if (((int) pay.get("recharge")) == 1) {
                    chargeMap.put("subject", "充值支付");
                    chargeMap.put("body", "充值支付");
                } else {
                    chargeMap.put("subject", "账单" + pay.get("billno").toString());
                    chargeMap.put("body", "账单" + pay.get("billno").toString());
                }
                chargeMap.put("order_no", payorderno);// 推荐使用 8-20 位，要求数字或字母，不允许其他字符
                chargeMap.put("channel", channel);// 支付使用的第三方支付渠道取值，请参考：https://www.pingxx.com/api#api-c-new
                chargeMap.put("client_ip", GetIpAddress()); // 发起支付请求客户端的 IP 地址，格式为 IPV4，如: 127.0.0.1

                //chargeMap.put("created", ((Date)pay.get("createddate")).getTime());//订单创建时间戳
                chargeMap.put("time_expire", ((Date) pay.get("orderoverdate")).getTime() / 1000);//订单过期时间戳
                Map<String, String> app = new HashMap<String, String>();
                app.put("id", payPingAppId);
                chargeMap.put("app", app);

                // extra 取值请查看相应方法说明
                chargeMap.put("extra", channelExtra(channel, pay.get("billno").toString()));

                try {
                    response.setHeader("Content-type", "text/html;charset=UTF-8");
                    request.setCharacterEncoding("UTF-8");
                    //发起交易请求

                    logs.info("扫码支付charge参数:ping++请求开始" , "pay");
                    charge = Charge.create(chargeMap);
                    payOrderService.updateOrderStateIn(payorderno, charge.getId(), 1);
                    // 传到客户端请先转成字符串 .toString(), 调该方法，会自动转成正确的 JSON 字符串
                    String chargeString = charge.toString();
                    logs.info("扫码支付charge参数:ping++请求结束" , "pay");
                    if (paychannel == 1) { //微信扫码
                        logs.info("扫码支付charge参数:" + chargeString, "pay");
                        String qrcode = charge.getCredential().get(channel).toString();//二维码
                        String urls = GetPathurl() + "import/qrimage?w=" + URLEncoder.encode(qrcode, "UTF-8");

                        JsonResult result = new JsonResult();
                        result.code = 0;
                        result.msg = "";
                        Map<String, Object> d = new HashMap<String, Object>();
                        d.put("payorderno", payorderno);
                        d.put("url", urls);
                        result.data = d;
                        response.getWriter().write(JsonSerializer(result));

                    } else {//银联 支付宝
                        logs.info("银联支付宝charge参数:" + chargeString, "pay");
                        JsonResult result = new JsonResult();
                        result.code = 0;
                        result.msg = "";
                        Map<String, Object> d = new HashMap<String, Object>();
                        d.put("payorderno", payorderno);
                        d.put("charge", chargeString);
                        result.data = d;
                        response.getWriter().write(JsonSerializer(result));
                    }
                } catch (Exception e) {
                    logs.error("扫码支付charge异常:" + e.getMessage(), "pay");
                    e.printStackTrace();
                    try {
                        logs.error("扫码支付charge错误:", e);
                        JsonResult result = new JsonResult();
                        result.code = 1;
                        result.msg = e.getMessage();
                        result.data = "";
                        response.getWriter().write(JsonSerializer(result));
                    } catch (Exception ex) {

                    }
                }
            }
        }


    /**
     * ping++ 支付 扩展参数
     * @param channel
     * @return
     */
    private Map<String, Object> channelExtra(String channel,String productid) {
        Map<String, Object> extra = new HashMap<>();

        switch (channel) {
            case "alipay":
                extra = alipayExtra();
                break;
//            case "alipay_wap":
//                extra = alipayWapExtra();
//                break;
            case "alipay_pc_direct":
                extra = alipayPcDirectExtra();
                break;
            case "alipay_qr":
                extra = alipayQrExtra();
                break;
//            case "wx":
//                extra = wxExtra();
//                break;
//            case "wx_pub":
//                extra = wxPubExtra();
//                break;
            case "wx_pub_qr":
                extra = wxPubQrDirectExtra(productid);
                break;
//            case "wx_lite":
//                extra = wxLiteExtra();
//                break;
//            case "wx_wap":
//                extra = wxWapExtra();
//                break;
//            case "bfb":
//                extra = bfbExtra();
//                break;
//            case "bfb_wap":
//                extra = bfbWapExtra();
//                break;
//            case "upacp":
//                extra = upacpExtra();
//                break;
//            case "upacp_wap":
//                extra = upacpWapExtra();
//                break;
            case "upacp_pc":
                extra = upacpPcExtra();
                break;
            case "upacp_b2b":
                extra = upacpB2bExtra();
                break;
//            case "jdpay_wap":
//                extra = jdpayWapExtra();
//                break;
//            case "yeepay_wap":
//                extra = yeepayWapExtra();
//                break;
//            case "applepay_upacp":
//                extra = applepayUpacpExtra();
//                break;
//            case "qpay":
//                extra = qpayExtra();
//                break;
//            case "cmb_wallet":
//                extra = cmbWalletExtra();
//                break;
            case "cp_b2b":
                extra = cpB2bExtra();
                break;
//            case "isv_scan":
//                extra = isvScanExtra();
//                break;
//            case "isv_qr":
//                extra = isvQrExtra();
//                break;
//            case "isv_wap":
//                extra = isvWapExtra();
//                break;
//            case "alipay_scan":
//                extra = alipayScanExtra();
//                break;
//            case "wx_pub_scan":
//                extra = wxPubScanExtra();
//                break;
//            case "cb_alipay":
//                extra = cbAlipayExtra();
//                break;
//            case "cb_wx":
//                extra = cbWxExtra();
//                break;
//            case "cb_wx_pub":
//                extra = cbWxPubExtra();
//                break;
//            case "cb_wx_pub_qr":
//                extra = cbWxPubQrExtra();
//                break;
//            case "cb_wx_pub_scan":
//                extra = cbWxPubScanExtra();
//                break;
        }

        return extra;
    }


    private Map<String, Object> alipayExtra() {
        Map<String, Object> extra = new HashMap<>();

        // 必须，收款人姓名，1~50位。
        extra.put("recipient_name", "");

        return extra;
    }

    /**
     * 支付宝
     * @return
     */
    private Map<String, Object> alipayPcDirectExtra() {
        Map<String, Object> extra = new HashMap<>();
        // 必须，支付成功的回调地址，在本地测试不要写 localhost ，请写 127.0.0.1。URL 后面不要加自定义参数。
        extra.put("success_url", GetPathurl()+"notice/paysuccess");
        //extra.put("success_url", "http://127.0.0.1:8080/allianz/wxcheck/paySuccess.html");
        // 可选，是否开启防钓鱼网站的验证参数（如果已申请开通防钓鱼时间戳验证，则此字段必填）。
        // extra.put("enable_anti_phishing_key", false);

        // 可选，客户端 IP ，用户在创建交易时，该用户当前所使用机器的IP（如果商户申请后台开通防钓鱼IP地址检查选项，此字段必填，校验用）。
        // extra.put("exter_invoke_ip", "192.168.100.8");

        return extra;
    }
    private Map<String, Object> alipayQrExtra() {
        Map<String, Object> extra = new HashMap<>();

        return extra;
    }
    private Map<String, Object> wxExtra() {
        Map<String, Object> extra = new HashMap<>();
        // 可选，指定支付方式，指定不能使用信用卡支付可设置为 no_credit 。
        extra.put("limit_pay", "no_credit");

        // 可选，商品标记，代金券或立减优惠功能的参数。
        // extra.put("goods_tag", "YOUR_GOODS_TAG");

        return extra;
    }

    private Map<String, Object> wxPubQrDirectExtra(String productid) {
        Map<String, Object> extra = new HashMap<>();
        // 可选，指定支付方式，指定不能使用信用卡支付可设置为 no_credit 。
        extra.put("limit_pay", "no_credit");

        // 可选，商品标记，代金券或立减优惠功能的参数。
        // extra.put("goods_tag", "YOUR_GOODS_TAG");

        // 必须，商品 ID，1-32 位字符串。此 id 为二维码中包含的商品 ID，商户可自定义。
        extra.put("product_id", productid.replace("-",""));

        return extra;
    }

    private Map<String, Object> cpB2bExtra() {
        Map<String, Object> extra = new HashMap<>();

        return extra;
    }
    private Map<String, Object> upacpPcExtra() {
        Map<String, Object> extra = new HashMap<>();
        // 必须，支付完成的回调地址，在本地测试不要写 localhost ，请写 127.0.0.1。URL 后面不要加自定义参数。
        extra.put("result_url", GetPathurl()+"notice/paysuccess");
        //extra.put("result_url", "http://127.0.0.1:8080/allianz/wxcheck/paySuccess.html");

        return extra;
    }
    private Map<String, Object> upacpB2bExtra() {
        Map<String, Object> extra = new HashMap<>();
        // 必须，支付完成的回调地址，在本地测试不要写 localhost ，请写 127.0.0.1。URL 后面不要加自定义参数。
        extra.put("result_url", GetPathurl()+"notice/paysuccess");
        //extra.put("result_url", "http://127.0.0.1:8080/allianz/wxcheck/paySuccess.html");

        return extra;
    }

    /**
     * 转账
     * @param channel
     * @return
     */
    private String channelRecipient(String channel) {
        String recipient = null;

        switch (channel) {
            case "wx_pub":
                // 渠道为 wx_pub 时，需要传 recipient 为用户在商户 appid 下的 open_id
                recipient = "o7xEMsySBFG3MVHI-9VsAJX-j50W";
                break;
            case "alipay":
                // 渠道为 alipay 时，若 type 为 b2c，为个人支付宝账号，若 type 为 b2b，为企业支付宝账号。
                recipient = "";
                break;
            case "unionpay":
            case "allinpay":
            case "jdpay":
                break;
        }

        return recipient;
    }

}
