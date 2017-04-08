/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.ui.web;

import com.slackers.inc.database.entities.User;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
public class EmployeePage implements IPageFrame{
    
    private String body;
    private String title;
    private User user;
    
    public EmployeePage(String title, String bodyContent)
    {
        this.title = title;
        this.body = bodyContent;
        this.user = null;
    }
    
    public EmployeePage(String title)
    {
        this(title, "");
    }
    
    public User getUser() {
        return user;
    }

    @Override
    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getNavBar(HttpServletRequest request) {
        return 
"<nav class=\"navbar navbar-default\">\n" +
"  <div class=\"container-fluid\">\n" +
"    <div class=\"navbar-header\">\n" +
"      <a class=\"navbar-brand\" href=\""+WebComponentProvider.root(request)+"\">COLA Search Registry</a>\n" +
"    </div>\n" +
"    <ul class=\"nav navbar-nav\">\n" +
"      <li class=\"active\"><a href=\""+WebComponentProvider.root(request)+"\">Home</a></li>\n" +
"      <li class=\"dropdown\"><a class=\"dropdown-toggle\" data-toggle=\"dropdown\" href=\"#\">Page 1 <span class=\"caret\"></span></a>\n" +
"        <ul class=\"dropdown-menu\">\n" +
"          <li><a href=\"#\">Page 1-1</a></li>\n" +
"          <li><a href=\"#\">Page 1-2</a></li>\n" +
"          <li><a href=\"#\">Page 1-3</a></li>\n" +
"        </ul>\n" +
"      </li>\n" +
"      <li><a href=\""+WebComponentProvider.root(request)+"\">Info</a></li>\n" +
"    </ul>\n" +
"    <ul class=\"nav navbar-nav navbar-right\">\n" +
"      <li><a href=\""+WebComponentProvider.root(request)+"account/signup\"><span class=\"glyphicon glyphicon-user\"></span> Sign Up</a></li>\n" +
"      <li><a href=\""+WebComponentProvider.root(request)+"account/login\"><span class=\"glyphicon glyphicon-log-in\"></span> Login</a></li>\n" +
"    </ul>\n" +
"  </div>\n" +
"</nav>";
        
    }
    
    
}
