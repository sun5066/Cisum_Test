package com.like.sun.smtp;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class SendMail {
    public static void main(String[] args) {
        /*PORT = 25:non-ssl, 465:ssl, 587:tls */
        Properties props = System.getProperties();
        props.put("mail.smtp.host", "smtp.naver.com");
        props.put("mail.smtp.port", "25");
        props.put("defaultEncoding", "utf-8");
        props.put("mail.smtp.auth", "true");

        final String userId = "alstjr4434";
        final String userPw = "@@Xhahr1212";

        try {
            String sender = "alstjr4434@naver.com"; //보내는사람 메일주소. ex) mailSender@naver.com
            String subject = "메일 테스트"; //메일 제목
            String body = "ㅁ ㅏ 메일 받아라 !!"; //메일 본문

            Session session = Session.getDefaultInstance(props, new javax.mail.Authenticator() {
                String un = userId;
                String pw = userPw;

                protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new javax.mail.PasswordAuthentication(un, pw);
                }
            });
            session.setDebug(false); //Debug 모드 설정.

            Message mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(sender));

// 받는 사람 이메일주소 세팅
            InternetAddress[] toAddr = new InternetAddress[1];
            toAddr[0] = new InternetAddress("vasco__@naver.com");
            //toAddr[1] = new InternetAddress("메일받는사람 전체주소2");

            mimeMessage.setRecipients(Message.RecipientType.TO, toAddr); //수신자 셋팅

            mimeMessage.setSubject(subject); //제목 세팅
            mimeMessage.setText(body); //본문 세팅

//메일 발송
            Transport.send(mimeMessage);
            System.out.println("메일 발송 성공!!");
        } catch (Exception e) {
            System.out.println("메일보내기 오류 : " + e.getMessage());
        }
    }
}