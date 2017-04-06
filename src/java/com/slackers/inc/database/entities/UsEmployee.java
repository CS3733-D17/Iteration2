/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.database.entities;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
public class UsEmployee extends User{
    
    public static final UsEmployee NULL_EMPLOYEE = new UsEmployee("unknown","unknown","unknown","unknown");
    
    private List<LabelApplication> applications;
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
            this.applications.addAll(LabelApplication.applicationListFromString((String)values.get("applications")));
        }
        if (values.containsKey("previousApplications"))
        {
            this.previousApplications.clear();
            this.previousApplications.addAll(LabelApplication.applicationListFromString((String)values.get("previousApplications")));
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
