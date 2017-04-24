/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.database.entities;

import com.slackers.inc.database.IEntity;
import java.io.Serializable;
import java.util.*;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 *
 *     A class to represent a user in our system. Holds fields for email, password, the user's type,
 *     and the user's first and last name. The user type is an enum, telling us if the user is a
 *     manufacturer, US employee, COLA user, admin, or something else.
 */
public class User implements IEntity{
    
    public static final User NULL_USER = new User("unknown","unknown","unknown","unknown");
    
    private static final String TABLE = "USERS";
    
    public static enum UserType
    {
        UNKNOWN,
        MANUFACTURER,
        US_EMPLOYEE,
        COLA_USER,
        ADMIN;
    }
    
    private String password;
    private String email;
    private UserType userType;
    private String firstName;
    private String lastName;
    private boolean isUpdate;

    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.email = email;
        this.userType = UserType.UNKNOWN;
        this.isUpdate = false;
    }
    
    public User(String email) {
        this("", "", email, "");
    }

    public User() {
        this("", "", "", "");
    }
    
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }    
    
    public void setUpdateMode(boolean updateMode)
    {
        this.isUpdate = updateMode;
    }

    @Override
    public Map<String, Object> getEntityValues() {
        Map<String,Object> temp = new HashMap<>();
        temp.put("firstName", this.firstName);
        temp.put("lastName", this.lastName);
        temp.put("password", this.password);
        temp.put("email", this.email);
        temp.put("userType", this.userType.name());
        return temp;
    }

    @Override
    public Map<String, Object> getUpdatableEntityValues() {
        Map<String,Object> temp = new HashMap<>();
        temp.put("firstName", this.firstName);
        temp.put("lastName", this.lastName);
        temp.put("password", this.password);
        if (!this.isUpdate)
            temp.put("email", this.email);
        temp.put("userType", this.userType.name());
        return temp;
    }

    @Override
    public void setEntityValues(Map<String, Object> values) {
        if (values.containsKey("firstName"))
            this.firstName = (String)values.get("firstName");
        if (values.containsKey("lastName"))
            this.lastName = (String)values.get("lastName");
        if (values.containsKey("password"))
            this.password = (String)values.get("password");
        if (values.containsKey("email"))
            this.email = (String)values.get("email");
        if (values.containsKey("userType"))
            this.userType = UserType.valueOf((String)values.get("userType"));
    }

    @Override
    public Map<String, Class> getEntityNameTypePairs() {
        Map<String,Class> pairs = new HashMap<>();
        pairs.put("firstName", String.class);
        pairs.put("lastName", String.class);
        pairs.put("password", String.class);
        pairs.put("email", String.class);
        pairs.put("applications", String.class);
        pairs.put("previousApplications", String.class);
        pairs.put("templateApplication", Long.class);
        pairs.put("userType", String.class);
        return pairs;
    }

    @Override
    public String getPrimaryKeyName() {
        return "email";
    }

    @Override
    public Serializable getPrimaryKeyValue() {
        return this.email;
    }

    @Override
    public void setPrimaryKeyValue(Serializable value) {
        this.email = (String) value;
    }

    
    @Override
    public List<String> tableColumnCreationSettings() {
        List<String> cols = new LinkedList<>();
        cols.add("firstName varchar(256)");
        cols.add("lastName varchar(256)");
        cols.add("password varchar(256)");
        cols.add("email varchar(256) PRIMARY KEY");
        cols.add("applications long varchar");
        cols.add("previousApplications long varchar");
        cols.add("templateApplication varchar(8192)");
        cols.add("userType varchar(512)");
        return cols;
    }

    @Override
    public String getTableName() {
        return TABLE;
    }
    
    @Override
    public User deepCopy() {
        User usr = new User("");
        usr.setEntityValues(this.getEntityValues());
        return usr;
    }

    @Override
    public String toString() {
        return "User{" + "firstName=" + firstName + ", lastName=" + lastName + ", password=" + password + ", email=" + email + ", userType=" + userType + '}';
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.email);
        hash = 61 * hash + Objects.hashCode(this.userType);
        hash = 61 * hash + Objects.hashCode(this.firstName);
        hash = 61 * hash + Objects.hashCode(this.lastName);
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
        final User other = (User) obj;
        if (!Objects.equals(this.email, other.email)) {
            return false;
        }
        return true;
    }
    
    
}
