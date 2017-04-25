/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.Controllers.Email;

// Email : superslackersin@gmail.com
// pwd : super123

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

/**
 *
 * @author jestrada
 */
public class EmailWrapper {
    String[] to;
    String from;
    String[] cc;
    String pwd;
    String sub; // Could probably set it to an enum
    String msg;
    MimeMessage message;
    Session session;
    
    Properties props;
    
//    EmailWrapper _email;
    
//    private EmailWrapper(){
//        this.to[0] = "";
//        this.from = "";
//        this.pwd = "";
//        this.sub = "";
//        this.msg = "";
//        
//        this.session = Session.getDefaultInstance(props,    
//           new javax.mail.Authenticator() {    
//           protected PasswordAuthentication getPasswordAuthentication() {    
//           return new PasswordAuthentication(from,pwd);  
//           }    
//          });    
//        
//        this.props = new Properties();
//        props.put("mail.smtp.host", "smtp.gmail.com");    
//        props.put("mail.smtp.socketFactory.port", "465");    
//        props.put("mail.smtp.socketFactory.class",    
//                        "javax.net.ssl.SSLSocketFactory");    
//        props.put("mail.smtp.auth", "true");    
//        props.put("mail.smtp.port", "465");
//    
//    }

    public EmailWrapper(String from, String pwd, String sub, String msg, String... to){
        this.to = to;
        this.from = from;
        this.pwd = pwd;
        this.sub = sub;
        this.msg = msg;
        
        this.props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");    
        props.put("mail.smtp.socketFactory.port", "465");    
        props.put("mail.smtp.socketFactory.class",    
                        "javax.net.ssl.SSLSocketFactory");    
        props.put("mail.smtp.auth", "true");    
        props.put("mail.smtp.port", "465");
        
        // This  part doesn't work
        this.session = Session.getDefaultInstance(props,    
            new javax.mail.Authenticator() {    
                //@Override
                protected PasswordAuthentication getPasswordAuthentication() {    
                    return new PasswordAuthentication(from,pwd);  
                }    
            });    

        // ---------------------------
        message = new MimeMessage(session);
        
        
    }
    
    public EmailWrapper(String from, String pwd, String sub, String msg,String[] cc, String... to){
        this.to = to;
        this.from = from;
        this.pwd = pwd;
        this.sub = sub;
        this.msg = msg;
        this.cc = cc;
        
        
        this.session = Session.getDefaultInstance(props,    
            new javax.mail.Authenticator() {    
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {    
                    return new PasswordAuthentication(from,pwd);  
                }    
            });    
        
        message = new MimeMessage(session);
        
        this.props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");    
        props.put("mail.smtp.socketFactory.port", "465");    
        props.put("mail.smtp.socketFactory.class",    
                        "javax.net.ssl.SSLSocketFactory");    
        props.put("mail.smtp.auth", "true");    
        props.put("mail.smtp.port", "465");    
    }
    
    
    
//    public EmailWrapper getEmail(){
//        
//        return _email;
//    }
    
//    public EmailWrapper setEmail(String from, String pwd, String sub, String msg, String... to){
//        if (_email == null){
//            _email = new EmailWrapper(from,pwd,sub, msg, to);
//        } else if( !this.to.equals(to) || !this.from.equals(from) || !this.pwd.equals(pwd) || !this.sub.equals(sub) || this.msg.equals(msg)){
//            _email = new EmailWrapper(from,pwd,sub, msg, to);
//        } else {
//            System.out.println("EmailWrapper already exist");
//        }
//        return _email;
//    } 
    
   
    
    public void sendEmail() throws Exception{

        try {    
            // Checks if theres anyone to send to
            // Assume that inputs are actual emails
            if (to.length > 0){
                for (String to1 : to) {
                    
                    message.addRecipient(Message.RecipientType.TO, new InternetAddress(to1)); 
                }
            }else {
                System.out.println("No recipient available");
                throw new MessagingException();
            }   
            
            if (cc != null && cc.length > 0){
                for (String cc1 : cc){
                    message.addRecipient(Message.RecipientType.CC, new InternetAddress(cc1));
                }
            }
            message.setSubject(sub);    
            message.setText(msg);    
            //send message  
            Transport.send(message);    
            System.out.println("message sent successfully");    
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }    
    }    
    
    
}

     
