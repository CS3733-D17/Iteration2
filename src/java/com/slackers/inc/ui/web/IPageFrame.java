/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.ui.web;

import java.com.slackers.inc.database.entities.User;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
public interface IPageFrame {
    public String getNavBar(HttpServletRequest request);
    public String getBody();
    public void setBody(String body);
    public String getTitle();
    public void setUser(User user);
}
