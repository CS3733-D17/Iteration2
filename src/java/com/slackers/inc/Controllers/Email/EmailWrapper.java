/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.Controllers.Email;

// Email : superslackersinc@gmail.com
// pwd : super123

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

/**
 * 
 * @author jestrada
 *
 * Used to hold the information we need to email clients
 * and can also send the email via javax.mail
 */
public class EmailWrapper {
    // Information about the email, such as who it is sent to
    // who it is from, the subject and message, etc
    String to;
    String from;
    String[] cc;
    String pwd;
    String sub; // Could probably set it to an enum
    String msg;
    MimeMessage message;
    Session session;
    
    Properties props;
    


    public EmailWrapper(String from, String pwd, String sub, String msg, String to){
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
        
        
        this.session = Session.getDefaultInstance(props,    
            new javax.mail.Authenticator() {    
                //@Override
                protected PasswordAuthentication getPasswordAuthentication() {    
                    return new PasswordAuthentication(from,pwd);  
                }    
            });    

        
        message = new MimeMessage(session);
        
        
    }
    
    public EmailWrapper(String from, String pwd, String sub, String msg, String[] cc, String to){
        this.to = to;
        this.from = from;
        this.pwd = pwd;
        this.sub = sub;
        this.msg = msg;
        this.cc = cc;
        
        this.props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");    
        props.put("mail.smtp.socketFactory.port", "465");    
        props.put("mail.smtp.socketFactory.class",    
                        "javax.net.ssl.SSLSocketFactory");    
        props.put("mail.smtp.auth", "true");    
        props.put("mail.smtp.port", "465");  
        
        this.session = Session.getDefaultInstance(props,    
            new javax.mail.Authenticator() {    
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {    
                    return new PasswordAuthentication(from,pwd);  
                }    
            });    
        
        message = new MimeMessage(session);
        
          
    }
    
    public EmailWrapper(String sub, String msg, String to){
        this.to = to;
        this.from = "superslackersinc@gmail.com";
        this.pwd = "super123";
        this.sub = sub;
        this.msg = msg;
        
        this.props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");    
        props.put("mail.smtp.socketFactory.port", "587");    
        props.put("mail.smtp.socketFactory.class",    
                        "javax.net.ssl.SSLSocketFactory");    
        props.put("mail.smtp.auth", "true");   
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.connectiontimeout", "100000");
        props.put("mail.smtp.timeout", "100000");
        props.put("mail.smtp.starttls.enable","true"); 
        props.put("mail.smtp.EnableSSL.enable","true");
        
        this.session = Session.getInstance(props,    
            new javax.mail.Authenticator() {    
                //@Override
                protected PasswordAuthentication getPasswordAuthentication() {    
                    return new PasswordAuthentication(from,pwd);  
                }    
            });    

        
        message = new MimeMessage(session);
        
        
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
    
   

    // Sends the email according to the informtion held
    // in this class via javax.mail
    public void sendEmail(){
        System.out.println("message should be sending");    

        try {    
            // Checks if theres anyone to send to
            // Assume that inputs are actual emails
            
            System.out.println("Email exist");
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to)); 
            System.out.println(to);
             
            
            
            message.setSubject(sub);    
            message.setText(msg);    
            //send message  
            System.out.println("Plz work");
            
            Transport.send(message);    
            System.out.println("message sent successfully");    
            } catch (MessagingException e) {
                e.printStackTrace();
                System.out.println("Timeout");
                //throw new RuntimeException(e);
            } catch (Exception e){
                System.out.println("Something broke");
            }
    }    
    
    
}

     
