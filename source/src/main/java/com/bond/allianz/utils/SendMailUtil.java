package com.bond.allianz.utils;

import com.bond.allianz.Dao.logs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
@Component
public class SendMailUtil {

    private static String senderAddress ;

    private static String mailpassword;

    private static String mailhost ;

    private static String mailprotocol;

    private static int mailport;

    private static boolean mailssl;

    private static boolean mailauth;


    @Value("${email.account}")
    public  void setSenderAddress(String account){
        senderAddress=account;
    }
    @Value("${email.password}")
    public  void setPassword(String password){
        mailpassword=password;
    }
    @Value("${email.host}")
    public  void setHost(String host){
        mailhost=host;
    }
    @Value("${email.protocol}")
    public  void setProtocol(String protocol){
        mailprotocol=protocol;
    }
    @Value("${email.port}")
    public  void setPort(String port){
        mailport= Integer.parseInt(port);
    }
    @Value("${email.ssl}")
    public  void setSsl(String ssl){
        mailssl= Boolean.parseBoolean(ssl);
    }
    @Value("${email.auth}")
    public  void setAuth(String auth){
        mailauth= Boolean.parseBoolean(auth);
    }

    /**
     * 发送邮件
     * @param receiveMailAccount
     * @param title
     * @param content
     * @param attachmentfile 多个,分开
     * @throws Exception
     */
    public static void  sendMail(String receiveMailAccount ,String title,String content,String attachmentfile) throws Exception {
        try {
            logs.info("开始发送邮件，receive="+receiveMailAccount+",标题="+title+",内容="+content, "sms");
            Properties properties = new Properties();
            if(mailprotocol.isEmpty()){
                logs.error("mailprotocol为空");
                return ;
            }
            properties.put("mail.transport.protocol", mailprotocol);// 连接协议
            properties.put("mail.smtp.host", mailhost);// 主机名
            properties.put("mail.smtp.port", mailport);// 端口号
            properties.put("mail.smtp.auth", mailauth);
            properties.put("mail.smtp.ssl.enable", mailssl);// 设置是否使用ssl安全连接 ---一般都使用
            properties.put("mail.debug", "false");// 设置是否显示debug信息 true 会在控制台显示相关信息
            // 得到回话对象
            Session session = Session.getInstance(properties);
            // 获取邮件对象
            Message message = new MimeMessage(session);

            // 设置发件人邮箱地址
            message.setFrom(new InternetAddress(senderAddress));
            // 设置收件人邮箱地址
            // message.setRecipients(Message.RecipientType.TO, new InternetAddress[]{new InternetAddress("xxx@qq.com"),new InternetAddress("xxx@qq.com"),new InternetAddress("xxx@qq.com")});
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(receiveMailAccount));//一个收件人
            // 设置邮件标题
            message.setSubject(title);
            // 设置邮件内容
            Multipart mp = new MimeMultipart();
            MimeBodyPart mbp = new MimeBodyPart();
            // 设定邮件内容的类型为 text/plain 或 text/html
            mbp.setContent(content, "text/html;charset=utf-8");
            mp.addBodyPart(mbp);
            if(!"".equals(attachmentfile)&&attachmentfile!=null){
                String[] arr= attachmentfile.split(",");
                for(int i=0;i<arr.length;i++){
                    if(!"".equals(arr[i])){

                        MimeBodyPart attachment = new MimeBodyPart();
                        // 读取本地文件
                        DataHandler dh2 = new DataHandler(new FileDataSource(arr[i]));
                        // 将附件数据添加到"节点"
                        attachment.setDataHandler(dh2);
                        // 设置附件的文件名（需要编码）
                        attachment.setFileName(MimeUtility.encodeText(dh2.getName()));

                        // 10. 设置（文本+图片）和 附件 的关系（合成一个大的混合"节点" / Multipart ）
                        //MimeMultipart mm = new MimeMultipart();

                        mp.addBodyPart(attachment);     // 如果有多个附件，可以创建多个多次添加


                        // 11. 设置整个邮件的关系（将最终的混合"节点"作为邮件的内容添加到邮件对象）
                        //message.setContent(mm);
                    }
                }
            }
            message.setContent(mp);
//            if (!attachmentfile.equals("")) {
//                MimeBodyPart attachment = new MimeBodyPart();
//                // 读取本地文件
//                DataHandler dh2 = new DataHandler(new FileDataSource(attachmentfile));
//                // 将附件数据添加到"节点"
//                attachment.setDataHandler(dh2);
//                // 设置附件的文件名（需要编码）
//                attachment.setFileName(MimeUtility.encodeText(dh2.getName()));
//
//                // 10. 设置（文本+图片）和 附件 的关系（合成一个大的混合"节点" / Multipart ）
//                MimeMultipart mm = new MimeMultipart();
//
//                mm.addBodyPart(attachment);     // 如果有多个附件，可以创建多个多次添加
//
//
//                // 11. 设置整个邮件的关系（将最终的混合"节点"作为邮件的内容添加到邮件对象）
//                message.setContent(mm);
//            }
            // 得到邮差对象
            Transport transport = session.getTransport();

            // 连接自己的邮箱账户
            transport.connect(senderAddress, mailpassword);// 密码为QQ邮箱开通的stmp服务后得到的客户端授权码
            // 发送邮件
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

            logs.info("发送邮件成功，receive="+receiveMailAccount+",标题="+title+",内容="+content, "sms");
        } catch (Exception ex) {
            logs.error("发送邮件错误,receive=" + receiveMailAccount + ",标题=" + title + ",内容=" + content, ex);
            ex.printStackTrace();
        }
    }
}
