/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.Controllers.Email;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jestrada
 */
public class SMSWrapper {
    public enum Provider {
        VERIZON,
        ATT,
        TMOBILE,
        SPRINT
    }
    
    public static Map<Provider, String> providers = new EnumMap<Provider, String>(Provider.class);
    
    static {
        providers.put(Provider.ATT, "@txt.att.net");
        providers.put(Provider.VERIZON, "@vtext.com");
        providers.put(Provider.TMOBILE, "@tmomail.net");
        providers.put(Provider.SPRINT, "@messaging.sprintpcs.com");
    }
    
    String phoneNumber;
    Provider provider;
    String msg;
    String from;
    String pwd;
    String workingAddress;
    EmailWrapper method;
    
    public SMSWrapper(String from, String pwd, String phone, Provider provider, String msg){
        this.phoneNumber = phone;
        this.provider = provider;
        this.msg = msg;
        this.from = from;
        this.pwd = pwd;
        
        this.workingAddress = phoneNumber + providers.get(provider);
        
        
        
        
        
        method = new EmailWrapper(from, pwd, "",  msg, workingAddress);
    }
    
    public SMSWrapper(String phone, Provider provider, String msg){
        this.phoneNumber = phone;
        this.provider = provider;
        this.msg = msg;
       
        
        this.workingAddress = phoneNumber + providers.get(provider);
        
        
        
        
        
        method = new EmailWrapper("",  msg, workingAddress);
    }
    
    
    public void sendMsg(){
        method.sendEmail();
    }
    
}
