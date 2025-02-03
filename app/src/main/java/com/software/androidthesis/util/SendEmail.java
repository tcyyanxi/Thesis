package com.software.androidthesis.util;

import android.util.Log;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * @Auther : Tcy
 * @Date : Create in 2025/2/2 19:21
 * @Decription:
 */
public class SendEmail {
    public static String myEmailSMTPHost = "smtp.qq.com";
    public static String account = "1972228514@qq.com"; // 自己的邮箱
    public static String password = "qbkzesyhkvpkcbha"; // 授权码

    // 存储邮箱对应的验证码
    private static final ConcurrentHashMap<String, Integer> emailCodeMap = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    // 发送邮件
    public static void sendMail(String to, int code) {
        Log.d("进入了邮件发送函数", "sendEmail: ");

        // 1. 创建连接对象，连接到邮箱服务器
        final Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", myEmailSMTPHost);
        props.setProperty("mail.smtp.auth", "true");

        // 启用 SSL
        props.setProperty("mail.smtp.ssl.enable", "true");
        props.setProperty("mail.smtp.port", "465");

        // 2. 创建会话对象
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(account, password);
            }
        });

        try {
            // 3. 创建邮件对象
            final Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(account));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("验证码");
            message.setContent("<h1>您的验证码是：" + code + "</h1><p>此验证码将在5分钟后失效。</p>", "text/html;charset=UTF-8");

            // 4. 发送邮件
            new Thread(() -> {
                try {
                    Transport.send(message);
                    Log.d("邮件发送成功", "验证码已发送");

                    // 存储验证码，并设置5分钟后过期
                    emailCodeMap.put(to, code);
                    scheduler.schedule(() -> {
                        emailCodeMap.remove(to);
                        Log.d("验证码过期", "验证码已删除：" + to);
                    }, 5, TimeUnit.MINUTES);

                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    // 验证用户输入的验证码
    public static boolean verifyCode(String email, int inputCode) {
        Integer storedCode = emailCodeMap.get(email);
        if (storedCode != null && storedCode == inputCode) {
            emailCodeMap.remove(email); // 验证成功后删除验证码，防止重复使用
            return true;
        }
        return false;
    }
}