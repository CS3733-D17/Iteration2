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
 * Represents a COLA user in our system.
 */
public class ColaUser extends User{

    // NULL_COLA_USER is used when a placeholder is needed for an unknown COLA user.
    public static final ColaUser NULL_COLA_USER = new ColaUser("unknown","unknown","unknown","unknown");
    
    public ColaUser(String firstName, String lastName, String email, String password) {
        super(firstName, lastName, email, password);
        init();
    }

    public ColaUser(String email) {
        super(email);
        init();
    }
    
    public ColaUser()
    {
        super();
    }

    private void init()
    {
        super.setUserType(User.UserType.COLA_USER);
    }

    @Override
    public ColaUser deepCopy() {
        ColaUser u = new ColaUser(this.getEmail());
        u.setEntityValues(this.getEntityValues());
        return u;
    }
    
    
}
