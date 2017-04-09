/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.database.entities;

import com.slackers.inc.database.DerbyConnection;
import com.slackers.inc.database.IEntity;
import com.slackers.inc.database.entities.Label.BeverageType;

import java.io.Serializable;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
public class LabelApplication implements IEntity{

    private static final String TABLE = "LABEL_APPLICATIONS";


    public static enum ApplicationStatus
    {
        NOT_COMPLETE,
        SUBMITTED,
        SUBMITTED_FOR_REVIEW,
        UNDER_REVIEW,
        REJECTED,
        APPROVED,
        SENT_FOR_CORRECTIONS,
        UNKNOWN;
    }
    public static enum RevisionType
    {
        CHANGE_NAME;
    }
    
    private long applicationId;
    
    
    
    private String representativeId;
    private Address applicantAddress;
    private Address mailingAddress;
    private String phoneNumber;
    private String emailAddress;
    private Date applicationDate;
    private ApplicationStatus status;
    private String applicant;
    private String reviewer;
    private String submitter;
    private Label label;
    private List<LabelComment> comments;
    private ApplicationApproval applicationApproval;
    private Set<RevisionType> allowedRevisions;
    
    public LabelApplication(long applicationId)
    {
        this.allowedRevisions = new HashSet<>();
        this.applicant = "";
        this.representativeId = "";
        this.applicantAddress = new Address();
        this.applicationApproval = null;
        this.applicationDate = new java.sql.Date(new java.util.Date().getTime());
        this.applicationId = applicationId;
        this.comments = new LinkedList<>();
        this.emailAddress = "";
        this.label = new Label();
        this.mailingAddress = new Address();
        this.phoneNumber = "";        
        this.reviewer = "";
        this.status = ApplicationStatus.UNKNOWN;
        this.submitter = "";
    }
    
    public LabelApplication()
    {
        this(0);
    }
    
    @Override
    public String getTableName() {
        return TABLE;
    }

    @Override
    public Map<String, Object> getEntityValues() {
        Map<String,Object> values = new HashMap<>();
        values.put("applicationId", this.applicationId);
        values.put("applicantAddress", this.applicantAddress);
        values.put("mailingAddress", this.mailingAddress);
        values.put("phoneNumber", this.phoneNumber);        
        values.put("emailAddress", this.emailAddress);
        values.put("applicationDate", this.applicationDate);
        values.put("representativeId", this.representativeId);
        values.put("status", this.status); 
        if (this.applicant!=null)
            values.put("applicant", this.applicant);
        if (this.reviewer!=null)
            values.put("reviewer", this.reviewer);
        if (this.submitter!=null)
            values.put("submitter", this.submitter);  
        if (this.label!=null)
            values.put("label", this.label.getPrimaryKeyValue());
        values.put("comments", LabelComment.commentListToString(this.comments));
        if (this.applicationApproval!=null)
            values.put("applicationApproval", (long)this.applicationApproval.getPrimaryKeyValue());        
        values.put("allowedRevisions", LabelApplication.allowedRevisionsToString(this.allowedRevisions));
        return values;
    }

    @Override
    public Map<String, Object> getUpdatableEntityValues() {
        Map<String,Object> values = new HashMap<>();        
        values.put("applicantAddress", this.applicantAddress);
        values.put("representativeId", this.representativeId);
        values.put("mailingAddress", this.mailingAddress);
        values.put("phoneNumber", this.phoneNumber);        
        values.put("emailAddress", this.emailAddress);
        values.put("applicationDate", this.applicationDate);
        values.put("status", this.status);        
        if (this.applicant!=null)
            values.put("applicant", this.applicant);
        if (this.reviewer!=null)
            values.put("reviewer", this.reviewer);
        if (this.submitter!=null)
            values.put("submitter", this.submitter);  
        if (this.label!=null)
            values.put("label", this.label.getPrimaryKeyValue());
        values.put("comments", LabelComment.commentListToString(this.comments));
        if (this.applicationApproval!=null)
            values.put("applicationApproval", (long)this.applicationApproval.getPrimaryKeyValue());          
        values.put("allowedRevisions", LabelApplication.allowedRevisionsToString(this.allowedRevisions));
        return values;
    }

    @Override
    public void setEntityValues(Map<String, Object> values) {
        if (values.containsKey("applicationId"))
        {
            this.applicationId = (long) values.get("applicationId");
        }        
        if (values.containsKey("applicantAddress"))
        {
            this.applicantAddress = (Address) values.get("applicantAddress");
        }
        if (values.containsKey("mailingAddress"))
        {
            this.mailingAddress = (Address) values.get("mailingAddress");
        }
        if (values.containsKey("representativeId"))
        {
            this.representativeId = (String) values.get("representativeId");
        }
        if (values.containsKey("phoneNumber"))
        {
            this.phoneNumber = (String) values.get("phoneNumber");
        }
        if (values.containsKey("emailAddress"))
        {
            this.emailAddress = (String) values.get("emailAddress");
        }
        if (values.containsKey("applicationDate"))
        {
            this.applicationDate = (Date) values.get("applicationDate");
        }
        if (values.containsKey("status"))
        {
            this.status = (ApplicationStatus) values.get("status");
        }
        if (values.containsKey("applicant"))
        {
            this.applicant = (String)values.get("applicant");
        }
        if (values.containsKey("reviewer"))
        {
            this.reviewer = (String)values.get("reviewer");
        }
        if (values.containsKey("submitter"))
        {
            this.submitter = (String)values.get("submitter");
        }
        if (values.containsKey("label"))
        {
            this.label.setPrimaryKeyValue((Serializable)values.get("label"));
            try {
                DerbyConnection.getInstance().getEntity(this.label, this.label.getPrimaryKeyName());
                if (this.label.getProductType() == BeverageType.BEER)
                {
                    this.label = new BeerLabel();
                    this.label.setPrimaryKeyValue((Serializable)values.get("label"));
                }
                else if (this.label.getProductType() == BeverageType.WINE)
                {
                    this.label = new WineLabel();
                    this.label.setPrimaryKeyValue((Serializable)values.get("label"));
                }
                else if (this.label.getProductType() == BeverageType.DISTILLED)
                {
                    this.label = new DistilledLabel();
                    this.label.setPrimaryKeyValue((Serializable)values.get("label"));
                }
                DerbyConnection.getInstance().getEntity(this.label, this.label.getPrimaryKeyName());
            } catch (SQLException ex) {
                Logger.getLogger(LabelApplication.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        if (values.containsKey("comments"))
        {
            this.comments = LabelComment.commentListFromString((String) values.get("comments"));
        }
        if (values.containsKey("applicationApproval"))
        {
            if (this.applicationApproval != null && (Long)values.get("applicationApproval")!=0)
            {                
                this.applicationApproval.setPrimaryKeyValue((Serializable)values.get("applicationApproval"));
                try {
                    DerbyConnection.getInstance().getEntity(this.applicationApproval, this.applicationApproval.getPrimaryKeyName());
                } catch (SQLException ex) {
                    Logger.getLogger(LabelApplication.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else if (this.applicationApproval == null && (Long)values.get("applicationApproval")!=0)
            {
                this.applicationApproval = new ApplicationApproval(new UsEmployee(), new Date(new java.util.Date().getTime()));
                this.applicationApproval.setPrimaryKeyValue((Long)values.get("applicationApproval"));
                try {
                    DerbyConnection.getInstance().getEntity(this.applicationApproval, this.applicationApproval.getPrimaryKeyName());
                } catch (SQLException ex) {
                    Logger.getLogger(LabelApplication.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        if (values.containsKey("allowedRevisions"))
        {
            this.allowedRevisions = LabelApplication.allowedRevisionsFromString((String)values.get("allowedRevisions"));
        }
    }

    @Override
    public Map<String, Class> getEntityNameTypePairs() {
        Map<String,Class> pairs = new HashMap<>();
        pairs.put("applicationId", Long.class);        
        pairs.put("applicantAddress", Serializable.class);
        pairs.put("mailingAddress", Serializable.class);
        pairs.put("phoneNumber", String.class);    
        pairs.put("representativeId", String.class);
        pairs.put("emailAddress", String.class);
        pairs.put("applicationDate", Date.class);
        pairs.put("status", Serializable.class);        
        pairs.put("applicant", String.class);
        pairs.put("reviewer", String.class);
        pairs.put("submitter", String.class);        
        pairs.put("label", Long.class);
        pairs.put("comments", String.class);
        pairs.put("applicationApproval", Long.class);        
        pairs.put("allowedRevisions", String.class);
        return pairs;
    }

    @Override
    public List<String> tableColumnCreationSettings() {
        List<String> cols = new LinkedList<>();
        cols.add("applicationId bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)");
        cols.add("applicantAddress varchar(2048)");
        cols.add("representativeId varchar(128)");
        cols.add("mailingAddress varchar(2048)");
        cols.add("phoneNumber varchar(64)");
        cols.add("emailAddress varchar(128)");
        cols.add("applicationDate date");
        cols.add("status varchar(256)");
        cols.add("applicant varchar(256)");
        cols.add("reviewer varchar(256)");
        cols.add("submitter varchar(256)");
        cols.add("label varchar(4096)");
        cols.add("comments long varchar");
        cols.add("applicationApproval bigint");
        cols.add("allowedRevisions long varchar");
        return cols;
    }

    @Override
    public String getPrimaryKeyName() {
        return "applicationId";
    }
    
    @Override
    public Serializable getPrimaryKeyValue() {
        return this.applicationId;
    }

    @Override
    public void setPrimaryKeyValue(Serializable value) {
        this.applicationId = (long) value;
    }

    public String getRepresentativeId() {
        return representativeId;
    }

    public void setRepresentativeId(String representativeId) {
        this.representativeId = representativeId;
    }
    
    public long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(long applicationId) {
        this.applicationId = applicationId;
    }

    public Address getApplicantAddress() {
        return applicantAddress;
    }

    public void setApplicantAddress(Address applicantAddress) {
        this.applicantAddress = applicantAddress;
    }

    public Address getMailingAddress() {
        return mailingAddress;
    }

    public void setMailingAddress(Address mailingAddress) {
        this.mailingAddress = mailingAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public Manufacturer getApplicant() {
        Manufacturer m = new Manufacturer();
        m.setPrimaryKeyValue(this.applicant);
        try {
            DerbyConnection.getInstance().getEntity(m, m.getPrimaryKeyName());
        } catch (SQLException ex) {
            Logger.getLogger(LabelApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        return m;
    }

    public void setApplicant(Manufacturer applicant) {
        this.applicant = (String)applicant.getPrimaryKeyValue();
    }

    public UsEmployee getReviewer() {
        UsEmployee e = new UsEmployee();
        e.setPrimaryKeyValue(this.reviewer);
        try {
            DerbyConnection.getInstance().getEntity(e, e.getPrimaryKeyName());
        } catch (SQLException ex) {
            Logger.getLogger(LabelApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        return e;
    }

    public void setReviewer(UsEmployee reviewer) {
        this.reviewer = (String)reviewer.getPrimaryKeyValue();
    }

    public UsEmployee getSubmitter() {
        UsEmployee e = new UsEmployee();
        e.setPrimaryKeyValue(this.submitter);
        try {
            DerbyConnection.getInstance().getEntity(e, e.getPrimaryKeyName());
        } catch (SQLException ex) {
            Logger.getLogger(LabelApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        return e;
    }

    public void setSubmitter(UsEmployee submitter) {
        this.submitter = (String)submitter.getPrimaryKeyValue();
    }

    public Label getLabel() {
        return label;
    }

    public void setLabel(Label label) {
        this.label = label;
    }

    public List<LabelComment> getComments() {
        return comments;
    }

    public void setComments(List<LabelComment> comments) {
        this.comments = comments;
    }

    public ApplicationApproval getApplicationApproval() {
        return applicationApproval;
    }

    public void setApplicationApproval(ApplicationApproval applicationApproval) {
        this.applicationApproval = applicationApproval;
        this.label.setApproval(this.applicationApproval);
        try {
            DerbyConnection.getInstance().writeEntity(this.label, this.label.getPrimaryKeyName());
        } catch (SQLException ex) {
            Logger.getLogger(LabelApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (this.applicationApproval!=null)
            this.applicationApproval.setApplication(this);
    }

    public Set<RevisionType> getAllowedRevisions() {
        return allowedRevisions;
    }

    public void setAllowedRevisions(Set<RevisionType> allowedRevisions) {
        this.allowedRevisions = allowedRevisions;
    }

    public void setLabelType(BeverageType type)
    {
        Long labelId = this.label.getLabelId();
        Map<String, Object> entityValues = this.label.getEntityValues();
        if (type == BeverageType.BEER && this.label.getProductType()!= BeverageType.BEER)
        {
            this.label = new BeerLabel();
            this.label.setEntityValues(entityValues);
            this.label.setPrimaryKeyValue(labelId);
        }
        else if (type == BeverageType.WINE && this.label.getProductType()!= BeverageType.WINE)
        {
            this.label = new WineLabel();
            this.label.setEntityValues(entityValues);
            this.label.setPrimaryKeyValue(labelId);
        }
        else if (type == BeverageType.DISTILLED && this.label.getProductType()!= BeverageType.DISTILLED)
        {
            this.label = new DistilledLabel();
            this.label.setEntityValues(entityValues);
            this.label.setPrimaryKeyValue(labelId);
        }        
        try {
            DerbyConnection.getInstance().getEntity(this.label, this.label.getPrimaryKeyName());
        } catch (SQLException ex) {
            Logger.getLogger(LabelApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static String applicationListToString(List<LabelApplication> applications)
    {
        if (applications==null)
            return null;
        List<String> appIds = new LinkedList<>();        
        for (LabelApplication e : applications)
        {
            try {
                if (e.getApplicationId()==0)
                    DerbyConnection.getInstance().createEntity(e);
                appIds.add(Long.toString(e.getApplicationId()));
            } catch (SQLException ex) {
                Logger.getLogger(LabelApplication.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return DerbyConnection.collectionToString(appIds);
    }
    
    public static List<LabelApplication> applicationListFromString(String appListString)
    {
        if (appListString==null)
            return null;
        List<LabelApplication> applications = new LinkedList<>();
        List<String> appStrings = DerbyConnection.collectionFromString(appListString);
        for (String s : appStrings)
        {            
            if (s!=null && !s.equals(""))
            {
                try {
                    LabelApplication temp = new LabelApplication();
                    temp.setApplicationId(Long.parseLong(s));
                    DerbyConnection.getInstance().getEntity(temp, "applicationId");
                    applications.add(temp);
                } catch (Exception ex) {
                    Logger.getLogger(LabelApplication.class.getName()).log(Level.SEVERE, null, ex);
                }  
            }
        }
        return applications;
    }
    
    private static String allowedRevisionsToString(Set<RevisionType> allowedRevisions) {
        List<String> revs = new LinkedList<>();
        for (RevisionType e : allowedRevisions)
        {
            revs.add(e.name());
        }
        return DerbyConnection.collectionToString(revs);
    }
    private static Set<RevisionType> allowedRevisionsFromString(String revString) {
        Set<RevisionType> allowedRevisions = new HashSet<>();
        List<String> revStrings = DerbyConnection.collectionFromString(revString);
        for (String s : revStrings)
        {
            if (s!=null && !s.equals(""))
                allowedRevisions.add(RevisionType.valueOf(s));            
        }
        return allowedRevisions;
    }   
    
    @Override
    public LabelApplication deepCopy() {
        LabelApplication application = new LabelApplication();
        application.setEntityValues(this.getEntityValues());
        return application;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (int) (this.applicationId ^ (this.applicationId >>> 32));
        hash = 47 * hash + Objects.hashCode(this.representativeId);
        hash = 47 * hash + Objects.hashCode(this.phoneNumber);
        hash = 47 * hash + Objects.hashCode(this.emailAddress);
        hash = 47 * hash + Objects.hashCode(this.applicant);
        hash = 47 * hash + Objects.hashCode(this.reviewer);
        hash = 47 * hash + Objects.hashCode(this.submitter);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LabelApplication other = (LabelApplication) obj;
        if (this.applicationId != other.applicationId) {
            return false;
        }
        return true;
    }

    
    public void updateLabel()
    {
        try {
            DerbyConnection.getInstance().writeEntity(this.label, this.label.getPrimaryKeyName());
        } catch (SQLException ex) {
            Logger.getLogger(LabelApplication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public String toString() {
        return "LabelApplication{" + "applicationId=" + applicationId + ", representativeId=" + representativeId + ", applicantAddress=" + applicantAddress + ", mailingAddress=" + mailingAddress + ", phoneNumber=" + phoneNumber + ", emailAddress=" + emailAddress + ", applicationDate=" + applicationDate + ", status=" + status + ", applicant=" + applicant + ", reviewer=" + reviewer + ", submitter=" + submitter + ", label=" + label + '}';
    }
    
    
}
