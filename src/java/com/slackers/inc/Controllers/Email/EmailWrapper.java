/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.Controllers.Email;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import java.lang.NullPointerException;

/**
 *
 * @author jestrada
 */
public class EmailWrapper {
    String to;
    String from;
    String pwd;
    String sub; // Could probably set it to an enum
    String msg;
    Session session;
    
    Properties props;
    
    EmailWrapper _email;

    private EmailWrapper(String to, String from, String pwd, String sub, String msg){
        this.to = to;
        this.from = from;
        this.pwd = pwd;
        this.sub = sub;
        this.msg = msg;
        
        this.session = Session.getDefaultInstance(props,    
           new javax.mail.Authenticator() {    
           protected PasswordAuthentication getPasswordAuthentication() {    
           return new PasswordAuthentication(from,pwd);  
           }    
          });    
        
        this.props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");    
        props.put("mail.smtp.socketFactory.port", "465");    
        props.put("mail.smtp.socketFactory.class",    
                        "javax.net.ssl.SSLSocketFactory");    
        props.put("mail.smtp.auth", "true");    
        props.put("mail.smtp.port", "465");
    }
    
    public EmailWrapper setEmail(String to, String from, String pwd, String sub, String msg){
        if (_email == null){
            _email = new EmailWrapper(to,from,pwd,sub, msg);
        } else if( !this.to.equals(to) || !this.from.equals(from) || !this.pwd.equals(pwd) || !this.sub.equals(sub) || this.msg.equals(msg)){
            _email = new EmailWrapper(to,from,pwd,sub, msg);
        } else {
            System.out.println("EmailWrapper already exist");
        }
        return _email;
    } 
    
    public void sendEmail() throws Exception{
        if (_email == null){
            System.out.println("Email Wrapper is not set up yet");
            throw new NullPointerException(); // Not sure if appropriate exception
        } else {
            try {    
                MimeMessage message = new MimeMessage(session);    
                message.addRecipient(Message.RecipientType.TO,new InternetAddress(to));    
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
    
}
  
     
