/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.database.entities;

import com.slackers.inc.database.DerbyConnection;
import com.slackers.inc.database.IEntity;
import java.io.Serializable;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 *
 *     Represents an application approval. It holds information about which
 *     agent approved the application, when it was approved, when the approval will
 *     expire. It also holds information about the approved applications ID, and the
 *     approvals ID.
 */
public class ApplicationApproval implements IEntity{
    
    private static final String TABLE = "LABEL_APPROVALS";
    
    private UsEmployee agent;
    private Date approvalDate;
    private Date experationDate;
    private long applicationId;
    private long approvalId;

    public ApplicationApproval(UsEmployee agent, Date approvalDate, Date experationDate) {
        this.agent = agent;
        this.approvalDate = approvalDate;
        this.experationDate = experationDate;
        this.applicationId = 0;
        this.approvalId = 0;
    }
    
    public ApplicationApproval(UsEmployee agent, Date experationDate) {
        this(agent, new Date(new java.util.Date().getTime()), experationDate);
    }
    
    public ApplicationApproval() {
        this(UsEmployee.NULL_EMPLOYEE, new Date(new java.util.Date().getTime()));
    }
    
    public long getApprovalId() {
        return approvalId;
    }

    public void setApprovalId(long approvalId) {
        this.approvalId = approvalId;
    }
    
    public LabelApplication getApplication() {
        LabelApplication l = new LabelApplication(this.applicationId);
        try {
            DerbyConnection.getInstance().getEntity(l, l.getPrimaryKeyName());
        } catch (SQLException ex) {
            Logger.getLogger(ApplicationApproval.class.getName()).log(Level.SEVERE, null, ex);
        }
        return l;
    }

    public void setApplication(LabelApplication application) {
        this.applicationId = (Long)application.getPrimaryKeyValue();
    }   
    
    public UsEmployee getAgent() {
        try {
            DerbyConnection.getInstance().getEntity(this.agent, this.agent.getPrimaryKeyName());
        } catch (SQLException ex) {
            Logger.getLogger(ApplicationApproval.class.getName()).log(Level.SEVERE, null, ex);
        }
        return agent;
    }

    public void setAgent(UsEmployee agent) {
        this.agent = agent;
    }

    public Date getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }

    public Date getExperationDate() {
        return experationDate;
    }

    public void setExperationDate(Date experationDate) {
        this.experationDate = experationDate;
    }

    @Override
    public String getTableName() {
        return TABLE;
    }

    @Override
    public Map<String, Object> getEntityValues() {
        Map<String, Object> values = new HashMap<>();
        values.put("approvalDate", this.approvalDate);
        values.put("experationDate", this.experationDate);
        if (this.agent!=null)
            values.put("agent", this.agent.getPrimaryKeyValue());
        values.put("application", this.applicationId);
        values.put("approvalId", this.approvalId);
        return values;
    }

    @Override
    public Map<String, Object> getUpdatableEntityValues() {
        Map<String, Object> values = new HashMap<>();
        values.put("approvalDate", this.approvalDate);
        values.put("experationDate", this.experationDate);
        if (this.agent!=null)
            values.put("agent", this.agent.getPrimaryKeyValue());
        values.put("application", this.applicationId);
        return values;
    }

    @Override
    public void setEntityValues(Map<String, Object> values) {
        if (values.containsKey("approvalDate"))
        {
            this.approvalDate = (Date)values.get("approvalDate");
        }
        if (values.containsKey("experationDate"))
        {
            this.experationDate = (Date)values.get("experationDate");
        }
        if (values.containsKey("approvalId"))
        {
            this.approvalId = (long)values.get("approvalId");
        }
        if (values.containsKey("agent"))
        {
            this.agent.setPrimaryKeyValue((String)values.get("agent"));
        }
        if (values.containsKey("application"))
        {
            this.applicationId = (Long)values.get("application");
        }
    }

    @Override
    public Map<String, Class> getEntityNameTypePairs() {
        Map<String, Class> pairs = new HashMap<>();
        pairs.put("approvalDate", Date.class);
        pairs.put("experationDate", Date.class);
        pairs.put("agent", String.class);
        pairs.put("application", Long.class);
        pairs.put("approvalId", Long.class);
        return pairs;
    }

    @Override
    public List<String> tableColumnCreationSettings() {
        List<String> cols = new LinkedList<>();
        cols.add("approvalId bigint");
        cols.add("approvalDate Date");
        cols.add("experationDate Date");
        cols.add("application bigint");
        cols.add("agent varchar(512)");
        return cols;
    }

    @Override
    public String getPrimaryKeyName() {
        return "approvalId";
    }

    @Override
    public Serializable getPrimaryKeyValue() {
        return this.approvalId;
    }

    @Override
    public void setPrimaryKeyValue(Serializable value) {
        this.approvalId = (long) value;
    }

    @Override
    public ApplicationApproval deepCopy() {
        ApplicationApproval app = new ApplicationApproval(this.agent, this.experationDate);
        app.setEntityValues(this.getEntityValues());
        return app;
    }
    
    
    
   
}
