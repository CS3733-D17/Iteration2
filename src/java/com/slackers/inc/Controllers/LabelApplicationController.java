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
import com.slackers.inc.database.entities.Label.BeverageSource;
import com.slackers.inc.database.entities.Label.BeverageType;
import com.slackers.inc.database.entities.LabelApplication;
import com.slackers.inc.database.entities.LabelComment;
import com.slackers.inc.database.entities.Manufacturer;
import com.slackers.inc.database.entities.UsEmployee;
import com.slackers.inc.database.entities.User;
import com.slackers.inc.database.entities.WineLabel;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.LinkedList;
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
        } catch (Exception e) {
        }
        
        return this.application;
    }
    
    public Label createLabelFromRequest(HttpServletRequest request)
    {
        Label label = new Label();
        
        label.setIsAccepted(false);
        label.setPlantNumber(request.getParameter("plantNumber"));
        label.setBrandName(request.getParameter("brandName"));
        
        label.setFancifulName(request.getParameter("fancifulName"));
        label.setGeneralInfo(request.getParameter("generalInfo"));
        
        label.setFormula(request.getParameter("formula"));
        
        label.setRepresentativeIdNumber(request.getParameter("representativeId"));
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
                newLabel.setPhLevel(Double.parseDouble(request.getParameter("pH")));
                newLabel.setVintage(Integer.parseInt(request.getParameter("vintage")));
                newLabel.setGrapeVarietal(request.getParameter("grapeVarietal"));
                newLabel.setWineAppelation(request.getParameter("wineAppelation"));
                return newLabel;
            }
        } catch (Exception e){}        
        return label;
    }
    
    public String validateApplication()
    {
        if (this.application.getEmailAddress() == null || this.application.getEmailAddress().length()<3)
        {
            return "Invalid email address";
        }
        if (this.application.getApplicant() == null || this.application.getApplicant().getEmail().equals(Manufacturer.NULL_MANUFACTURER.getEmail())
                || (this.application.getApplicant() instanceof Manufacturer))
        {
            return "Invalid applicant";
        }
        if (this.application.getApplicantAddress()== null || this.application.getApplicantAddress().getZipCode()==-1)
        {
            return "Invalid applicant address";
        }
        if (this.application.getMailingAddress() == null)
        {
            this.application.setMailingAddress(this.application.getApplicantAddress());//use mailing address as default
        }
        if (this.application.getMailingAddress() == null || this.application.getMailingAddress().getZipCode()==-1)
        {            
            return "Invalid mailing address";
        }
        if (this.application.getPhoneNumber() == null || this.application.getPhoneNumber().length()<10 || this.application.getPhoneNumber().length()>21)
        {
            return "Invalid phone number";
        }
        if (this.application.getRepresentativeId()== null || this.application.getRepresentativeId().length()<3)
        {
            return "Invalid representative Id ";
        }
        return null;
    }
    
    public String validateLabel()
    {
        Label l = this.application.getLabel();
        if (l == null)
        {
            return "Invalid label information";
        }
        if (l.getBrandName() == null || l.getBrandName().length()<2)
        {
            return "Invalid brand name";
        }
        if (l.getPlantNumber()== null || l.getPlantNumber().length()<2)
        {
            return "Invalid plant number";
        }
        if (l.getSerialNumber()== null || l.getSerialNumber().length()<5 || l.getSerialNumber().length()>10)
        {
            return "Invalid serial number";
        }
        if (l.getProductSource() == null || l.getProductSource()==BeverageSource.UNKNOWN)
        {
            return "Invalid beverage source";
        }
        if (l.getProductType()== null || l.getProductType()==BeverageType.UNKNOWN)
        {
            return "Invalid beverage type";
        }
        if (l.getProductType()== null || l.getProductType()==BeverageType.UNKNOWN)
        {
            return "Invalid beverage type";
        }
        if (l.getRepresentativeIdNumber()== null || l.getRepresentativeIdNumber().length()<5)
        {
            return "Invalid representative id";
        }        
        if (l.getAlcoholContent()<0 || l.getAlcoholContent()>100)
        {
            return "Invalid alchohol content";
        }
        
        if (l.getFormula()== null || l.getFormula().length()<5)
        {
            return "Formula is invalid";
        }
        if (l instanceof WineLabel)
        {
            if (((WineLabel)l).getPhLevel()<0||((WineLabel)l).getPhLevel()>14)
            {
                return "Invalid pH level";
            }
            if (((WineLabel)l).getGrapeVarietal()== null||((WineLabel)l).getGrapeVarietal().length()<5)
            {
                return "Grape varietal is invalid";
            }
        }
        return null;
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
        boolean res = db.writeEntity(this.application, this.application.getPrimaryKeyName());
        //this.autoSelectReviewer();
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
    
    public static List<LabelApplication> getNextBatch()
    {
        List<LabelApplication> out = new LinkedList<>();
        return out;
    }
}
