package com.slackers.inc.Controllers;

import com.slackers.inc.database.DerbyConnection;
import com.slackers.inc.database.entities.ColaUser;
import com.slackers.inc.database.entities.Manufacturer;
import com.slackers.inc.database.entities.UsEmployee;
import com.slackers.inc.database.entities.User;
import com.slackers.inc.database.entities.User.UserType;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Created by SrinuL on 3/30/17.
 */
public class AccountController {

    private DerbyConnection db;
    private User user;

    public AccountController(User user) throws SQLException {
        db = DerbyConnection.getInstance();
        this.user = user;
    }
    
    public AccountController() throws SQLException {
        this(new User(null, null, null, null));
    }

    // returns true if the credentials are valid, and false otherwise
    public boolean verifyCredentials(String email, String password) throws SQLException {
        
        this.user.setEmail(email);
        this.user.setPassword(null);
        try {
            db.getEntity(user, "email");
        } catch (SQLException e) {
            System.out.println("Trouble accessing database for login verification");
            throw e;
        }
        return password.equals(user.getPassword());
    }


    public User loginUser(String email, String password) throws SQLException, IllegalStateException
    {
        if (this.verifyCredentials(email, password))
        {
            if (this.user.getUserType() == UserType.COLA_USER)
            {
                this.user = new ColaUser(this.user.getFirstName(), this.user.getLastName(), this.user.getEmail(), this.user.getPassword());
            }
            else if (this.user.getUserType() == UserType.MANUFACTURER)
            {
                this.user = new Manufacturer(this.user.getFirstName(), this.user.getLastName(), this.user.getEmail(), this.user.getPassword());
            }
            else if (this.user.getUserType() == UserType.US_EMPLOYEE)
            {
                this.user = new UsEmployee(this.user.getFirstName(), this.user.getLastName(), this.user.getEmail(), this.user.getPassword());
            }
            else
            {
               throw new IllegalStateException("User type is unknown!"); 
            }
            db.getEntity(this.user, this.user.getPrimaryKeyName());
            System.out.println("LOADED USER: "+this.user);
            return this.user;
        }
        else
        {
            return null;
        }
    }
    
    public boolean createAccount(String firstName, String lastName, String email, String password, UserType type) throws IllegalStateException
    {
        if (type == UserType.COLA_USER)
        {
            this.user = new ColaUser(firstName, lastName, email, password);
        }
        else if (type == UserType.MANUFACTURER)
        {
            this.user = new Manufacturer(firstName, lastName, email, password);
        }
        else if (type == UserType.US_EMPLOYEE)
        {
            this.user = new UsEmployee(firstName, lastName, email, password);
        }
        else
        {
           throw new IllegalStateException("User type is unknown!"); 
        }

        try {
            return db.createEntity(this.user);
        } catch (SQLException ex) {
            throw new IllegalStateException("User already exists!");
        }
    }
    
    public boolean logout() throws SQLException
    {
        return db.writeEntity(this.user);
    }
    
    public boolean deleteAccount()
    {
        try {
            return db.deleteEntity(this.user, this.user.getPrimaryKeyName());
        } catch (SQLException ex) {
            throw new IllegalStateException("User already exists!");
        }
    }
    
    public boolean editAccount() throws SQLException
    {
        return db.writeEntity(user);
    }
    
    public boolean reload()
    {
        String em = this.user.getEmail();
        String pass = this.user.getPassword();
        try {
            return this.loginUser(em, pass)!=null;
        } catch (SQLException ex) {
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalStateException ex) {
            Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public User getUser()
    {
        return this.user;
    }

    public void deleteTable(){
        try {
            db.deleteTable(this.user.getTableName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


