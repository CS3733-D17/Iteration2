/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.Controllers;

import com.slackers.inc.Controllers.Email.EmailWrapper;
import com.slackers.inc.Controllers.Email.SMSWrapper;
import com.slackers.inc.Controllers.Email.SMSWrapper.Provider;
import com.slackers.inc.database.entities.LabelApplication;
import com.slackers.inc.database.entities.LabelApplication.ApplicationStatus;
import com.slackers.inc.database.entities.User;
import java.util.LinkedList;

/**
 *
 * @author jestrada
 */
public class NotificationController {
    private SMSWrapper text;
    private EmailWrapper email;
    private String msg;
    private User to;
    private String sub;
    private String phone;
    private Provider provider;
    private LinkedList<LabelApplication >listOfLabels;
    
    public NotificationController(User to, String phone, Provider provider){

        this.to = to;
        this.phone = phone;
        this.provider = provider;
        this.listOfLabels = new LinkedList();
    }
    
    public NotificationController(User to){
        this.to = to;
        this.phone = to.getPhone().replace("[\\(\\)-\\s+_]", "");
        this.provider = to.getProvider();
        this.listOfLabels = new LinkedList();
    }
    
    public void addLabelApplicatioin(LabelApplication label){
        listOfLabels.add(label);
    }
    
    public void sendApprovedReject(){
        
        this.msg = "The following is a list of approved labels";
        for (LabelApplication label : listOfLabels){
            if (label.getStatus().equals(ApplicationStatus.APPROVED))
                this.msg += "\n\t - " + label.getLabel().getBrandName();
        }
        this.msg += "/n/nThe following is the list of rejected labels";
        for (LabelApplication label : listOfLabels){
            if(label.getStatus().equals(ApplicationStatus.REJECTED))
                this.msg += "/n/t - " + label.getLabel().getBrandName();
        }
        
        this.sub = "Label Application Status";
        
        this.email = new EmailWrapper(this.sub, this.msg, this.to.getEmail());
        this.text = new SMSWrapper(this.phone, this.provider, this.msg);
        if (this.to.isEmailAllowed())
            email.sendEmail();
        if (this.phone.length()>=10 && this.phone.length()<=12 && this.provider!=Provider.DO_NOT_CONTACT)
            text.sendMsg();
        
    }
    
    public void sendApproved(String BrandName){
        
        this.msg = "The following application was approved \n\t -" +  BrandName;
        this.sub = "Label Application Approved";
        this.email = new EmailWrapper(this.sub, this.msg, this.to.getEmail());
        this.text = new SMSWrapper(this.phone, this.provider, this.msg);
        
        if (this.to.isEmailAllowed())
            email.sendEmail();
        if (this.phone.length()>=10 && this.phone.length()<=12 && this.provider!=Provider.DO_NOT_CONTACT)
            text.sendMsg();
        
    }
    public void sendRejected(String BrandName){
        
        this.msg = "The following application was NOT approved \n\t -" +  BrandName;
        this.sub = "Label Application Rejected";
        this.email = new EmailWrapper(this.sub, this.msg, this.to.getEmail());
        this.text = new SMSWrapper(this.phone, this.provider, this.msg);
        
        if (this.to.isEmailAllowed())
            email.sendEmail();
        if (this.phone.length()>=10 && this.phone.length()<=12 && this.provider!=Provider.DO_NOT_CONTACT)
            text.sendMsg();
        
    }
    
    public void sendRevision(String BrandName){
        this.msg = "The following application was NOT approved, but needs revisions \n\t -" +  BrandName;
        this.sub = "Applications In Need of Revision";
        this.email = new EmailWrapper(this.sub, this.msg, this.to.getEmail());
        this.text = new SMSWrapper(this.phone, this.provider, this.msg);
        
        if (this.to.isEmailAllowed())
            email.sendEmail();
        if (this.phone.length()>=10 && this.phone.length()<=12 && this.provider!=Provider.DO_NOT_CONTACT)
            text.sendMsg();
        
    }
    
    public void sendSubmit(String BrandName){
        this.msg = "You just submitted the following applications \n\t -" +  BrandName;
        this.sub = BrandName + " was Submitted Succesfully";
        this.email = new EmailWrapper(this.sub, this.msg, this.to.getEmail());
        this.text = new SMSWrapper(this.phone, this.provider, this.msg);
        
        if (this.to.isEmailAllowed())
            email.sendEmail();
        if (this.phone.length()>=10 && this.phone.length()<=12 && this.provider!=Provider.DO_NOT_CONTACT)
            text.sendMsg();
        
    }
    
    
    
    
}
