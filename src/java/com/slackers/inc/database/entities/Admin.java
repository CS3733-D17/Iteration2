/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.database.entities;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 *
 *     Represents an Admin in our system. Contains all fields and methods
 *     from the User class. Admins have more rights than normal users, as in most systems.
 */
public class Admin extends User{

    // NULL_ADMIN is used when we need a placeholder for an unknown admin.
    public static final Admin NULL_ADMIN = new Admin("unknown","unknown","unknown","unknown");
    
    public Admin(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
        init();
    }

    public Admin(String email) {
        super(email);
        init();
    }
    
    public Admin()
    {
        super();
    }

    private void init()
    {
        super.setUserType(User.UserType.ADMIN);
    }

    @Override
    public Admin deepCopy() {
        Admin u = new Admin(this.getEmail());
        u.setEntityValues(this.getEntityValues());
        return u;
    }
    
    
}
