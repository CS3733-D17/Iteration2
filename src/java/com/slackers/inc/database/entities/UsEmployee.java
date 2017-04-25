/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.database.entities;

import com.slackers.inc.database.entities.User.UserType;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 *
 *     Represents a US employee. Can approve or reject applications.
 */
public class UsEmployee extends User{

    // NULL_EMPLOYEE is used when a placeholder is needed or an employee
    // is unknown.
    public static final UsEmployee NULL_EMPLOYEE = new UsEmployee("unknown","unknown","unknown","unknown");

    // The applications this employee is given to work on
    private List<LabelApplication> applications;
    // The previous applications this employee has worked on.
    private List<LabelApplication> previousApplications;

    public UsEmployee(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
        init();
    }

    public UsEmployee(String email) {
        super(email);
        init();
    }   
    
    public UsEmployee()
    {
        super();
        init();
    }

    private void init()
    {
        this.applications = new LinkedList<>();
        this.previousApplications = new LinkedList<>();
        super.setUserType(UserType.US_EMPLOYEE);
    }

    @Override
    public void setEntityValues(Map<String, Object> values) {
        super.setEntityValues(values);
        if (values.containsKey("applications"))
        {
            this.applications.clear();
            List<LabelApplication> apps = LabelApplication.applicationListFromString((String)values.get("applications"));
            if (apps!=null)
                this.applications.addAll(apps);
        }
        if (values.containsKey("previousApplications"))
        {
            this.previousApplications.clear();
            List<LabelApplication> apps = LabelApplication.applicationListFromString((String)values.get("previousApplications"));
            if (apps!=null)
                this.previousApplications.addAll(apps);
        }
    }

    @Override
    public Map<String, Object> getUpdatableEntityValues() {
        Map<String, Object> values = super.getUpdatableEntityValues();
        values.put("applications", LabelApplication.applicationListToString(this.applications));
        values.put("previousApplications", LabelApplication.applicationListToString(this.previousApplications));
        return values;
    }

    @Override
    public Map<String, Object> getEntityValues() {
        Map<String, Object> values = super.getEntityValues();
        values.put("applications", LabelApplication.applicationListToString(this.applications));
        values.put("previousApplications", LabelApplication.applicationListToString(this.previousApplications));
        return values;
    }

    @Override
    public UsEmployee deepCopy() {
        UsEmployee e = new UsEmployee(this.getEmail());
        e.setEntityValues(this.getEntityValues());
        return e;
    }

    public List<LabelApplication> getApplications() {
        return applications;
    }

    public void removeApplication(LabelApplication application) {
        this.applications.remove(application);
    }
    
    public void addApplication(LabelApplication application) {
        this.applications.remove(application);
        this.applications.add(application);
    }

    public List<LabelApplication> getPreviousApplications() {
        return previousApplications;
    }

    public void setPreviousApplications(List<LabelApplication> previousApplications) {
        this.previousApplications = previousApplications;
    }

    @Override
    public String toString() {
        return "UsEmployee{" + super.toString()+"applications=" + applications.size() + '}';
    }
}
