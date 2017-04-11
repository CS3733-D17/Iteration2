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
import com.slackers.inc.database.entities.LabelApplication.ApplicationType;
import com.slackers.inc.database.entities.LabelComment;
import com.slackers.inc.database.entities.Manufacturer;
import com.slackers.inc.database.entities.UsEmployee;
import com.slackers.inc.database.entities.User;
import com.slackers.inc.database.entities.WineLabel;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
public class LabelApplicationController {
    
    public static final String APPLICATION_GENERAL_COOKIE_NAME = "SSINCAP_GEN";
    public static final String APPLICATION_DATA_COOKIE_NAME = "SSINCAP_DATA";
    public static final String APPLICATION_LABEL_COOKIE_NAME = "SSINCAP_LBL";
    
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
        if (request.getParameter("NEW")!=null)
        {
            this.application.addApplicationType(LabelApplication.ApplicationType.NEW, null);
        }
        if (request.getParameter("DISTINCT")!=null)
        {
            this.application.addApplicationType(LabelApplication.ApplicationType.DISTINCT, request.getParameter("capacity"));
        }
        if (request.getParameter("EXEMPT")!=null)
        {
            this.application.addApplicationType(LabelApplication.ApplicationType.EXEMPT, request.getParameter("state"));
        }
        if (request.getParameter("RESUBMIT")!=null)
        {
            this.application.addApplicationType(LabelApplication.ApplicationType.RESUBMIT, request.getParameter("tbbid"));
        }
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
        label.setSerialNumber(request.getParameter("serialNumber"));
        label.setFormula(request.getParameter("formula"));
        
        label.setRepresentativeIdNumber(request.getParameter("representativeId"));
        try
        {
            label.setProductSource(Label.BeverageSource.valueOf(request.getParameter("source")));     
            label.setAlcoholContent(Double.parseDouble(request.getParameter("alcoholContent").replace("%", "")));
            BeverageType type = BeverageType.valueOf(request.getParameter("type"));
            label.setProductType(type);
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
        } catch (Exception e){
        e.printStackTrace();
        }        
        return label;
    }
    
    public String validateApplication()
    {
        if (this.application.getEmailAddress() == null || this.application.getEmailAddress().length()<3)
        {
            return "Invalid email address";
        }
        if (this.application.getApplicant() == null || this.application.getApplicant().getEmail().equals(Manufacturer.NULL_MANUFACTURER.getEmail())
                || !(this.application.getApplicant() instanceof Manufacturer))
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
        for (Entry<ApplicationType,String> e : this.application.getApplicationTypes().entrySet())
        {
            if (e.getKey() == ApplicationType.DISTINCT)
            {
                if (e.getValue()==null || e.getValue().length()<1)
                    return "Invalid bottle capacity before closure";
            }
            if (e.getKey() == ApplicationType.EXEMPT)
            {
                if (e.getValue()==null || e.getValue().length()!=2)
                    return "Invalid state for exemption";
            }
            if (e.getKey() == ApplicationType.RESUBMIT)
            {
                if (e.getValue()==null || e.getValue().length()<1)
                    return "Invalid TBB id for resubmission";
            }
        }
        return this.validateLabel();
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
    
    public void removeApplicationFromCookies(HttpServletResponse response)
    {
        Cookie data = new Cookie(APPLICATION_DATA_COOKIE_NAME, null);
        Cookie gen = new Cookie(APPLICATION_GENERAL_COOKIE_NAME, null);
        Cookie lbl = new Cookie(APPLICATION_LABEL_COOKIE_NAME, null);
        
        data.setMaxAge(0);
        gen.setMaxAge(0);
        lbl.setMaxAge(0);
        
        data.setPath("/");
        gen.setPath("/");
        lbl.setPath("/");
        
        response.addCookie(data);
        response.addCookie(gen);
        response.addCookie(lbl);
    }
    
    public void writeApplicationToCookies(HttpServletResponse response)
    {
        JsonObjectBuilder generalObj = Json.createObjectBuilder().add("email", this.application.getEmailAddress())
                .add("phone", this.application.getPhoneNumber())
                .add("representativeId", this.application.getRepresentativeId());
        if (this.application.getApplicantAddress()!=null)
            generalObj.add("address", this.application.getApplicantAddress().toString());
        if (this.application.getMailingAddress()!=null)
            generalObj.add("mailAddress", this.application.getMailingAddress().toString());
        generalObj.add("appStatus", this.application.getStatus().name());
        
        for (Entry<ApplicationType,String> e : this.application.getApplicationTypes().entrySet())
        {
            if (e.getKey() == ApplicationType.NEW)
            {
                generalObj.add("NEW", "checked");
            }
            if (e.getKey() == ApplicationType.DISTINCT)
            {
                generalObj.add("DISTINCT", "checked");
                generalObj.add("capacity", e.getValue());
            }
            if (e.getKey() == ApplicationType.EXEMPT)
            {
                generalObj.add("EXEMPT", "checked");
                generalObj.add("state", e.getValue());
            }
            if (e.getKey() == ApplicationType.RESUBMIT)
            {
                generalObj.add("RESUBMIT", "checked");
                generalObj.add("tbbid", e.getValue());
            }
        }
                
        Label l = this.application.getLabel();
        JsonObjectBuilder labelObj = Json.createObjectBuilder().add("plantNumber",l.getPlantNumber())
                .add("brandName", l.getBrandName())
                .add("fancifulName", l.getFancifulName())
                .add("serialNumber", l.getSerialNumber())
                .add("type", l.getProductType().name())
                .add("source", l.getProductSource().name())
                .add("alcoholContent", Double.toString(l.getAlcoholContent()));
        
        if (l instanceof WineLabel)
        {
            labelObj.add("pH", Double.toString(((WineLabel)l).getPhLevel()));
            if (((WineLabel)l).getGrapeVarietal() != null)
                    labelObj.add("grapeVarietal", ((WineLabel)l).getGrapeVarietal());
            if (((WineLabel)l).getWineAppelation() != null)
                    labelObj.add("wineAppelation", ((WineLabel)l).getWineAppelation());
        }
        
        JsonObjectBuilder dataObj = Json.createObjectBuilder().add("formula",l.getFormula())
                .add("gereralInfo", l.getGeneralInfo());
        
        Cookie data = new Cookie(APPLICATION_DATA_COOKIE_NAME, Base64.getEncoder().encodeToString(dataObj.build().toString().getBytes(StandardCharsets.UTF_8)));
        Cookie gen = new Cookie(APPLICATION_GENERAL_COOKIE_NAME, Base64.getEncoder().encodeToString(generalObj.build().toString().getBytes(StandardCharsets.UTF_8)));
        Cookie lbl = new Cookie(APPLICATION_LABEL_COOKIE_NAME, Base64.getEncoder().encodeToString(labelObj.build().toString().getBytes(StandardCharsets.UTF_8)));
        
        data.setMaxAge(3600);
        gen.setMaxAge(3600);
        lbl.setMaxAge(3600);
        
        data.setPath("/");
        gen.setPath("/");
        lbl.setPath("/");
        
        response.addCookie(data);
        response.addCookie(gen);
        response.addCookie(lbl);
    }
    
    public String renderCommentList(HttpServletRequest request)
    {
        StringBuilder b = new StringBuilder();
        b.append("<div class=\"row\">").append("<div class=\"col-sm-1 col-md-2\"></div>");
        b.append("<div class=\"col-sm-10 col-md-8\">");
        for (LabelComment comment : this.application.getComments())
        {
            b.append(this.renderComment(request, comment));
        }
        b.append("</div>");
        b.append("<div class=\"col-sm-1 col-md-2\"></div>").append("</div>");
        return b.toString();
    }
    
    public String renderComment(HttpServletRequest request, LabelComment comment)
    {
        User usr = comment.getSubmitter();
        StringBuilder b = new StringBuilder();
        b.append("<div class=\"panel panel-info\">").append("<div class=\"panel-heading\">");
        if (usr==null)
        {
            b.append("Unknown");
        }
        else
        {
            b.append(usr.getFirstName()+" "+usr.getLastName() + " ("+usr.getEmail()+")");
        }
        b.append("<div style=\"float:right;\">").append(comment.getDate()).append("</div>");
        b.append("</div>").append("<div class=\"panel-body\">");
        b.append(comment.getComment());
        b.append("</div>").append("</div>");
        return b.toString();
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
        this.application.getComments().add(new LabelComment(submitter, "Submitted the application"));
        boolean res = db.writeEntity(this.application, this.application.getPrimaryKeyName());
        submitter.addApplications(this.application);
        this.db.writeEntity(submitter, submitter.getPrimaryKeyName());        
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
        
        this.application.getComments().add(new LabelComment(submitter, "<span style=\"color:green;\">Application Approved</span><br><br>Expires: "+experationDate.toString()));
        for (LabelComment l :  this.application.getComments())
        {
            System.out.println(l);
        }
        return db.writeEntity(this.application, this.application.getPrimaryKeyName());
    }
    
    public boolean rejectApplication(UsEmployee submitter) throws SQLException
    {
        this.application.setStatus(LabelApplication.ApplicationStatus.REJECTED);
        this.application.setApplicationApproval(null);
        submitter.getApplications().remove(this.application);
        this.db.writeEntity(submitter, submitter.getPrimaryKeyName());
        this.application.getComments().add(new LabelComment(submitter, "<span style=\"color:red;\">Application Rejected</span>"));
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

    public void setEntityValues(Map<String, Object> values) {
        application.setEntityValues(values);
    }

    public Map<String, Class> getEntityNameTypePairs() {
        return application.getEntityNameTypePairs();
    }

    public void setPrimaryKeyValue(Serializable value) {
        application.setPrimaryKeyValue(value);
    }

    public String getRepresentativeId() {
        return application.getRepresentativeId();
    }

    public void setRepresentativeId(String representativeId) {
        application.setRepresentativeId(representativeId);
    }

    public long getApplicationId() {
        return application.getApplicationId();
    }

    public void setApplicationId(long applicationId) {
        application.setApplicationId(applicationId);
    }

    public Address getApplicantAddress() {
        return application.getApplicantAddress();
    }

    public void setApplicantAddress(Address applicantAddress) {
        application.setApplicantAddress(applicantAddress);
    }

    public Address getMailingAddress() {
        return application.getMailingAddress();
    }

    public void setMailingAddress(Address mailingAddress) {
        application.setMailingAddress(mailingAddress);
    }

    public String getPhoneNumber() {
        return application.getPhoneNumber();
    }

    public void setPhoneNumber(String phoneNumber) {
        application.setPhoneNumber(phoneNumber);
    }

    public String getEmailAddress() {
        return application.getEmailAddress();
    }

    public void setEmailAddress(String emailAddress) {
        application.setEmailAddress(emailAddress);
    }

    public Date getApplicationDate() {
        return application.getApplicationDate();
    }

    public void setApplicationDate(Date applicationDate) {
        application.setApplicationDate(applicationDate);
    }

    public LabelApplication.ApplicationStatus getStatus() {
        return application.getStatus();
    }

    public void setStatus(LabelApplication.ApplicationStatus status) {
        application.setStatus(status);
    }

    public Manufacturer getApplicant() {
        return application.getApplicant();
    }

    public void setApplicant(Manufacturer applicant) {
        application.setApplicant(applicant);
    }

    public UsEmployee getReviewer() {
        return application.getReviewer();
    }

    public void setReviewer(UsEmployee reviewer) {
        application.setReviewer(reviewer);
    }

    public UsEmployee getSubmitter() {
        return application.getSubmitter();
    }

    public void setSubmitter(UsEmployee submitter) {
        application.setSubmitter(submitter);
    }

    public Label getLabel() {
        return application.getLabel();
    }

    public void setLabel(Label label) {
        application.setLabel(label);
    }

    public List<LabelComment> getComments() {
        return application.getComments();
    }

    public void setComments(List<LabelComment> comments) {
        application.setComments(comments);
    }

    public ApplicationApproval getApplicationApproval() {
        return application.getApplicationApproval();
    }

    public void setApplicationApproval(ApplicationApproval applicationApproval) {
        application.setApplicationApproval(applicationApproval);
    }

    public void setLabelType(BeverageType type) {
        application.setLabelType(type);
    }

    public void addApplicationType(ApplicationType applicationType, String value) {
        application.addApplicationType(applicationType, value);
    }

    public Map<ApplicationType, String> getApplicationTypes() {
        return application.getApplicationTypes();
    }
    
    
}
