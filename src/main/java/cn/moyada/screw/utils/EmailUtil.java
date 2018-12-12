package cn.moyada.screw.utils;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * @author xueyikang
 * @create 2018-03-15 14:32
 */
public final class EmailUtil {

    private static final String TO = "xxxxxx@163.com";

    private static final String HOST = "smtp.163.com";
    private static final int PORT = 465;

    private static final String USER = "xxxxxx@163.com";
    private static final String PASSWD = "xxxxxx";

    /**
     * 使用加密的方式,利用465端口进行传输邮件,开启ssl
     * @param subject    邮箱主题
     * @param message    发送的消息
     */
    public static void sendEmil(String subject, String message) {
        //设置邮件会话参数
        Properties props = new Properties();

        props.put("mail.smtp.ssl.enable", true);
        props.put("mail.smtp.from", USER);
        props.put("mail.smtp.host", HOST);
        props.put("mail.smtp.port", PORT);
        props.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(props,
                new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(USER, PASSWD);
                    }
                });
        try {
            //通过会话,得到一个邮件,用于发送
            Message msg = new MimeMessage(session);
            String nick = javax.mail.internet.MimeUtility.encodeText("车型库");

            //设置发件人
            msg.setFrom(new InternetAddress(nick+"<"+USER+">"));
            //设置收件人,to为收件人,cc为抄送,bcc为密送
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(TO, false));
//            msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(TO, false));
//            msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(TO, false));

            msg.setSubject(subject);
            //设置邮件消息
            msg.setText(message);
            //设置发送的日期
            msg.setSentDate(DateUtil.nowDate());

            //调用Transport的send方法去发送邮件
            Transport.send(msg);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}