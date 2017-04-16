package com.slackers.inc.Controllers;

import com.slackers.inc.database.DerbyConnection;
import com.slackers.inc.database.entities.Admin;
import com.slackers.inc.database.entities.ColaUser;
import com.slackers.inc.database.entities.Manufacturer;
import com.slackers.inc.database.entities.UsEmployee;
import com.slackers.inc.database.entities.User;
import com.slackers.inc.database.entities.User.UserType;
import com.sun.xml.wss.impl.misc.Base64;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Created by SrinuL on 3/30/17.
 */
public class AccountController {

    private static final String EMAIL_COOKIE = "SSINCEmail";
    private static final String PASSWORD_COOKIE = "SSINCMisc";
    private static final String PREV_UNAME = "SSINCPN";
    
    private static final String SALT = "?8kL91A!s";
    
    private DerbyConnection db;
    private User user;

    public static enum Permission
    {
        NONE,
        MANUFACTURER,
        EMPLOYEE,
        ADMIN;
    }
    
    public AccountController(User user) throws SQLException {
        db = DerbyConnection.getInstance();
        this.user = user;
    }
    
    public AccountController() throws SQLException {
        this(new User(null, null, null, null));
    }

    private String getPasswordEnc(String password)
    {
        password += SALT; // salt for security
        byte[] bytes = password.getBytes(StandardCharsets.UTF_8);
        try
        {
            MessageDigest md = MessageDigest.getInstance("MD5");
            bytes = md.digest(bytes);
        } catch (Exception e){}
        password = null; // promote gc of password. Prevent from hanging in memory
        return Base64.encode(bytes);
    }
    
    public static User getPageUser(HttpServletRequest request)
    {
        try
        {
            AccountController acc = new AccountController();
            String email = request.getParameter("email");
            String pass = request.getParameter("password");
            for (Cookie c : request.getCookies())
            {
                if (c.getName().equals(EMAIL_COOKIE))
                {
                    email = c.getValue();
                }
                if (c.getName().equals(PASSWORD_COOKIE))
                {
                    pass = c.getValue();
                }
            }
            if (email!=null && pass!=null)
            {
                return acc.getUser(email, pass);
            }
            return null;
        }catch (Exception e)
        {
            return null;
        }
    }
    
    public boolean verifyPermission(HttpServletRequest request, HttpServletResponse response, Permission requiredPermission) 
    {
        if (requiredPermission == Permission.NONE)
            return true;
        String email = null;
        String pass = null;
        for (Cookie c : request.getCookies())
        {
            if (c.getName().equals(EMAIL_COOKIE))
            {
                email = c.getValue();
            }
            if (c.getName().equals(PASSWORD_COOKIE))
            {
                pass = c.getValue();
            }
        }
        if (email!=null && pass!=null)
        {
            try {
                this.user.setEmail(email);
                this.user.setPassword(null);
                db.getEntity(user, "email");
                if (user.getPassword().equals(pass))
                {
                    if (user.getUserType() == UserType.ADMIN)
                    {
                        return true;
                    }
                    if (user.getUserType() == UserType.MANUFACTURER && requiredPermission == Permission.MANUFACTURER)
                    {
                        return true;
                    }
                    if (user.getUserType() == UserType.US_EMPLOYEE && requiredPermission == Permission.EMPLOYEE)
                    {
                        return true;
                    }
                }
            } catch (SQLException ex) {
                return false;
            }
        }
        return false;
    }
    
    public boolean verifyCredentials(HttpServletRequest request, HttpServletResponse response) 
    {
        String email = null;
        String pass = null;
        for (Cookie c : request.getCookies())
        {
            if (c.getName().equals(EMAIL_COOKIE))
            {
                email = c.getValue();
            }
            if (c.getName().equals(PASSWORD_COOKIE))
            {
                pass = c.getValue();
            }
        }
        if (email!=null && pass!=null)
        {
            try {
                return this.verifyCredentials(email, this.getPasswordEnc(pass));
            } catch (SQLException ex) {
                return false;
            }
        }
        else
        {
            this.loginUser(request, response);
        }
        return false;
    }
    
    public boolean logout(HttpServletRequest request, HttpServletResponse response) 
    {
        String email = null;
        String pass = null;
        for (Cookie c : request.getCookies())
        {
            if (c.getName().equals(EMAIL_COOKIE))
            {
                c.setMaxAge(0);
                c.setValue(null);
                c.setPath("/");
                response.addCookie(c);
            }
            if (c.getName().equals(PASSWORD_COOKIE))
            {
                c.setMaxAge(0);
                c.setValue(null);
                c.setPath("/");
                response.addCookie(c);
            }
        }
        if (email!=null && pass!=null)
        {
            try {
                return this.verifyCredentials(email, pass);
            } catch (SQLException ex) {
                return false;
            }
        }
        return false;
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

    public boolean loginUser(HttpServletRequest request, HttpServletResponse response) 
    {
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        if (email!=null && password!=null)
        {
            User usr = null;
            try {
                
                usr = this.getUser(email, this.getPasswordEnc(password));
            } catch (SQLException ex) {
                Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalStateException ex) {
                Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (usr!=null)
            {
                Cookie prevName = new Cookie(PREV_UNAME, email);
                Cookie uName = new Cookie(EMAIL_COOKIE, email);
                Cookie uPass = new Cookie(PASSWORD_COOKIE, this.getPasswordEnc(password));
                uName.setMaxAge(60*60*24);
                uPass.setMaxAge(60*60*24);
                prevName.setMaxAge(60*60*24);
                uName.setPath("/");
                uPass.setPath("/");
                prevName.setPath("/");
                response.addCookie(uName);
                response.addCookie(uPass);
                response.addCookie(prevName);
            }     
            System.out.println(usr);
            return usr!=null;
        }
        else
        {
            System.out.println("U/P was null");
            return false;
        }
    }

    public User getUser(String email, String password) throws SQLException, IllegalStateException
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
            else if (this.user.getUserType() == UserType.ADMIN)
            {
                this.user = new Admin(this.user.getFirstName(), this.user.getLastName(), this.user.getEmail(), this.user.getPassword());
            }
            else
            {
               throw new IllegalStateException("User type is unknown!"); 
            }
            db.getEntity(this.user, this.user.getPrimaryKeyName());
            return this.user;
        }
        else
        {
            return null;
        }
    }
    
    public boolean createAccount(HttpServletRequest request, HttpServletResponse response) 
    {
        String firstName = request.getParameter("fName");
        String lastName = request.getParameter("lName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String uType = request.getParameter("uType");
        UserType type = null;
        if (uType != null && uType.equals("manufacturer"))
            type = UserType.MANUFACTURER;
        if (uType != null && uType.equals("employee"))
            type = UserType.US_EMPLOYEE;
        if (uType != null && uType.equals("admin"))
            type = UserType.ADMIN;
        
        if (firstName == null || lastName == null || email == null || password == null || type == null)
        {
            System.out.println("not filled");
            return false;
        }
        try
        {
            boolean res = this.createAccount(firstName, lastName, email, this.getPasswordEnc(password), type);
            System.out.println(res);
            return this.loginUser(request, response);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
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
        else if (type == UserType.ADMIN)
        {
            this.user = new Admin(firstName, lastName, email, password);
        }
        else
        {
           throw new IllegalStateException("User type is unknown!"); 
        }

        try {
            return db.createEntity(this.user);
        } catch (SQLException ex) {
            ex.printStackTrace();
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
        System.out.println("Writting: "+this.user);
        return db.writeEntity(user);
    }
    
    public boolean reload()
    {
        String em = this.user.getEmail();
        String pass = this.user.getPassword();
        try {
            return this.getUser(em, pass)!=null;
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


