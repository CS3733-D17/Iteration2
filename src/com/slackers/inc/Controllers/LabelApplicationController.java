/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.Controllers;

import com.slackers.inc.database.DerbyConnection;
import com.slackers.inc.database.entities.Address;
import com.slackers.inc.database.entities.ApplicationApproval;
import com.slackers.inc.database.entities.BeerLabel;
import com.slackers.inc.database.entities.DistilledLabel;
import com.slackers.inc.database.entities.Label;
import com.slackers.inc.database.entities.Label.BeverageType;
import com.slackers.inc.database.entities.LabelApplication;
import com.slackers.inc.database.entities.LabelApplication.RevisionType;
import com.slackers.inc.database.entities.LabelComment;
import com.slackers.inc.database.entities.Manufacturer;
import com.slackers.inc.database.entities.UsEmployee;
import com.slackers.inc.database.entities.User;
import com.slackers.inc.database.entities.WineLabel;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
public class LabelApplicationController {
    
    private DerbyConnection db;
    private LabelApplication application;

    public LabelApplicationController(LabelApplication application) throws SQLException {
        db = DerbyConnection.getInstance();
        this.application = application;
    }
    
    public LabelApplicationController() throws SQLException {
        this(new LabelApplication());
    }
    
    public LabelApplicationController(long applicationId) throws SQLException {
        this.application = new LabelApplication(applicationId);
        db = DerbyConnection.getInstance();
        db.getEntity(application, application.getPrimaryKeyName());
    }
    
    public LabelApplication getLabelApplication()
    {
        return this.application;
    }
    
    public LabelApplication createApplicationFromRequest(HttpServletRequest request)
    {
        User pageUser = AccountController.getPageUser(request);
        if (!(pageUser instanceof Manufacturer))
            return null;
        
        Label l = this.createLabelFromRequest(request);
        if (l==null)
            return null;
        this.application.setLabel(l);
        
        this.application.setEmailAddress(request.getParameter("email"));
        this.application.setApplicant((Manufacturer) pageUser);
        this.application.setPhoneNumber(request.getParameter("phone"));
        this.application.setRepresentativeId(request.getParameter("representativeId"));
        try
        {            
            this.application.setApplicantAddress(Address.tryParse(request.getParameter("address")));
            this.application.setMailingAddress(Address.tryParse(request.getParameter("mailAddress")));
        } catch (Exception e) {}
        
        return this.application;
    }
    
    public Label createLabelFromRequest(HttpServletRequest request)
    {
        Label label = new Label();
        
        label.setIsAccepted(false);
        label.setPlantNumber(request.getParameter("plantNumber"));
        label.setBrandName(request.getParameter("brandName"));
        label.setRepresentativeIdNumber(request.getParameter("repIdNumber"));
        try
        {
            label.setProductSource(Label.BeverageSource.valueOf("source"));            
            Label.BeverageType type = Label.BeverageType.valueOf(request.getParameter("type"));
            if (type == BeverageType.BEER)
            {
                BeerLabel newLabel = new BeerLabel();
                newLabel.setEntityValues(label.getEntityValues());
                return newLabel;
            }
            if (type == BeverageType.DISTILLED)
            {
                DistilledLabel newLabel = new DistilledLabel();
                newLabel.setEntityValues(label.getEntityValues());
                return newLabel;
            }
            if (type == BeverageType.WINE)
            {
                WineLabel newLabel = new WineLabel();
                newLabel.setEntityValues(label.getEntityValues());
                return newLabel;
            }
        } catch (Exception e){}        
        return label;
    }
    
    public boolean setNewReviewer(UsEmployee employee) throws SQLException
    {
        this.application.setSubmitter(this.application.getReviewer());
        this.application.setReviewer(employee);      
        this.application.setStatus(LabelApplication.ApplicationStatus.UNDER_REVIEW);
        return db.writeEntity(this.application, this.application.getPrimaryKeyName());
    }
    
    public boolean attachComment(LabelComment coment) throws SQLException
    {
        this.application.getComments().add(coment);
        return db.writeEntity(this.application, this.application.getPrimaryKeyName());
    }
    
    public boolean attachApproval(ApplicationApproval approval) throws SQLException
    {
        this.application.setApplicationApproval(approval);
        return db.writeEntity(this.application, this.application.getPrimaryKeyName());
    }
    
    public boolean isRevisionAllowed(RevisionType type)
    {
        return this.application.getAllowedRevisions().contains(type);
    }
    
    public boolean saveApplication() throws SQLException
    {
        return db.writeEntity(this.application, this.application.getPrimaryKeyName());
    }
    
    public boolean editApplication() throws SQLException
    {
        this.application.updateLabel();
        return db.writeEntity(this.application, this.application.getPrimaryKeyName());
    }

    public boolean deleteApplication() throws SQLException
    {
        return db.deleteEntity(this.application, this.application.getPrimaryKeyName());
    }

    public boolean createApplication() throws SQLException
    {
        this.application.setStatus(LabelApplication.ApplicationStatus.NOT_COMPLETE);
        this.application.setReviewer(UsEmployee.NULL_EMPLOYEE);
        this.application.setSubmitter(UsEmployee.NULL_EMPLOYEE);
        db.createEntity(this.application);
        return true;
    }
    
    public boolean loadApplication(long id) throws SQLException
    {
        this.application.setApplicationId(id); 
        db.getEntity(this.application, this.application.getPrimaryKeyName());
        return true;
    }
    
    public boolean createApplication(LabelApplication application) throws SQLException
    {
        this.application = application;
        return this.createApplication();
    }  
    
    public boolean submitApplication(Manufacturer submitter) throws SQLException
    {
        this.application.setApplicant(submitter);
        this.application.setStatus(LabelApplication.ApplicationStatus.SUBMITTED);
        this.application.setApplicationDate(new Date(new java.util.Date().getTime()));
        this.application.setSubmitter(UsEmployee.NULL_EMPLOYEE);
        this.application.setReviewer(UsEmployee.NULL_EMPLOYEE);
        this.application.setApplicationApproval(null);
        this.application.setAllowedRevisions(new HashSet<>());
        System.out.println(this.application);
        boolean res = db.writeEntity(this.application, this.application.getPrimaryKeyName());
        this.autoSelectReviewer();
        return res;
    }
    
    public boolean approveApplication(UsEmployee submitter, Date experationDate) throws SQLException
    {
        ApplicationApproval approval = new ApplicationApproval(submitter, experationDate);
        this.application.setStatus(LabelApplication.ApplicationStatus.APPROVED);
        this.application.setApplicationApproval(approval);
        submitter.getApplications().remove(this.application);
        this.db.writeEntity(submitter, submitter.getPrimaryKeyName());
        return db.writeEntity(this.application, this.application.getPrimaryKeyName());
    }
    
    public boolean rejectApplication() throws SQLException
    {
        this.application.setStatus(LabelApplication.ApplicationStatus.REJECTED);
        return this.saveApplication();
    }
    
    public boolean sendForCorrections() throws SQLException
    {
        this.application.setStatus(LabelApplication.ApplicationStatus.SENT_FOR_CORRECTIONS);
        return this.saveApplication();
    }
    
    public boolean autoSelectReviewer()
    {
        UsEmployee target = new UsEmployee();
        target.setUserType(User.UserType.US_EMPLOYEE);
        try {
            List<UsEmployee> employees = DerbyConnection.getInstance().getAllEntites_Typed(target,"userType");
            if (!employees.isEmpty())
            {
                
                employees.sort(new Comparator<UsEmployee>(){
                    @Override
                    public int compare(UsEmployee o1, UsEmployee o2) {
                        return o1.getApplications().size() - o2.getApplications().size();
                    }
                });
                UsEmployee reviewer = employees.get(0);
                reviewer.removeApplication(application);
                boolean add = true;
                for (LabelApplication a : reviewer.getApplications())
                {
                    if (a.getApplicationId() == this.application.getApplicationId())
                    {
                        add = false;
                        break;
                    }
                }
                if (add)
                {
                    reviewer.getApplications().add(this.application);
                    this.application.setReviewer(reviewer);
                    this.application.setStatus(LabelApplication.ApplicationStatus.UNDER_REVIEW);
                    this.db.writeEntity(reviewer, reviewer.getPrimaryKeyName());
                    this.saveApplication();
                }
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(LabelApplicationController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        
        return false;
    }
}
