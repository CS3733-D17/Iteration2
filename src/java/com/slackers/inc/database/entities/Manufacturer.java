/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.database.entities;

import com.slackers.inc.database.DerbyConnection;
import java.io.IOException;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
public class Manufacturer extends User{

    public static final Manufacturer NULL_MANUFACTURER = new Manufacturer("unknown","unknown","unknown","unknown");
    
    private List<LabelApplication> applications;
    private LabelApplication templateApplication;

    public Manufacturer(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
        init();
    }

    public Manufacturer(String email) {
        super(email);
        init();
    }
    
    public Manufacturer()
    {
        super();
        init();
    }
    
    private void init()
    {
        applications = new LinkedList<>();
        templateApplication = new LabelApplication();
        super.setUserType(UserType.MANUFACTURER);
    }

    public List<LabelApplication> getApplications() {
        return applications;
    }

    public boolean removeApplications(LabelApplication application) {
        return this.applications.remove(application);
    }
    
    public boolean addApplications(LabelApplication application) {
        this.applications.remove(application);
        return this.applications.add(application);
    }

    public LabelApplication getTemplateApplication() {
        return templateApplication;
    }

    public void setTemplateApplication(LabelApplication templateApplication) {
        this.templateApplication = templateApplication;
    }

    @Override
    public void setEntityValues(Map<String, Object> values) {
        super.setEntityValues(values);
        if (values.containsKey("applications"))
        {
            List<LabelApplication> appList = LabelApplication.applicationListFromString((String)values.get("applications"));
            for (LabelApplication app : appList)
            {
                this.addApplications(app);
            }
        }
        if (values.containsKey("templateApplication"))
        {       
            this.templateApplication.setPrimaryKeyValue((Serializable) values.get("templateApplication"));
            try {                
                DerbyConnection.getInstance().getEntity(this.templateApplication, this.templateApplication.getPrimaryKeyName());
            } catch (Exception ex) {
                Logger.getLogger(Manufacturer.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }          
    }

    @Override
    public Map<String, Object> getUpdatableEntityValues() {
        Map<String, Object> values = super.getUpdatableEntityValues();
        values.put("applications", LabelApplication.applicationListToString(this.applications));
        if (this.templateApplication!=null)
            values.put("templateApplication", this.templateApplication.getPrimaryKeyValue());
        return values;
    }

    @Override
    public Map<String, Object> getEntityValues() {
        Map<String, Object> values = super.getEntityValues();
        values.put("applications", LabelApplication.applicationListToString(this.applications));
        if (this.templateApplication!=null)
            values.put("templateApplication", this.templateApplication.getPrimaryKeyValue());
        return values;
    }

    @Override
    public Manufacturer deepCopy() {
        Manufacturer m = new Manufacturer(this.getEmail());
        m.setEntityValues(this.getEntityValues());
        return m;
    }

    @Override
    public String toString() {
        return "Manufacturer{ " + super.toString()+ ", applications=" + LabelApplication.applicationListToString(applications) + ", templateApplication=" + templateApplication.getApplicationId() + '}';
    }

    

}
