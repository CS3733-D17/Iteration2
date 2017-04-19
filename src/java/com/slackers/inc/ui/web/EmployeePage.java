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

    @Override
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
"       <button type=\"button\" class=\"navbar-toggle\" data-toggle=\"collapse\" data-target=\"#colaNav\">\n" +
"        <span class=\"icon-bar\"></span>\n" +
"        <span class=\"icon-bar\"></span>\n" +
"        <span class=\"icon-bar\"></span>\n" +
"      </button>"+
"      <a class=\"navbar-brand\" href=\""+WebComponentProvider.root(request)+"\"><span class=\"glyphicon glyphicon-glass\"></span> COLA Search Registry</a>\n" +
"    </div><div class=\"collapse navbar-collapse\" id=\"colaNav\">\n" +
"    <ul class=\"nav navbar-nav\">\n" +
"      <li><a href=\""+WebComponentProvider.root(request)+"search\">Search</a></li>\n" +
"      <li><a href=\"/SuperSlackers/employee/applicationList\">Process Applications</a></li>\n" +
"    </ul>\n" +
"    <ul class=\"nav navbar-nav navbar-right\">\n" +
"      <li><a href=\""+WebComponentProvider.root(request)+"account/settings\"><span class=\"glyphicon glyphicon-cog\"></span>"+(this.user!=null ? " Welcome "+ user.getFirstName()+" "+user.getLastName() : "")+"</a></li>\n" +
"      <li><a href=\""+WebComponentProvider.root(request)+"account/logout\"><span class=\"glyphicon glyphicon-log-out\"></span> Logout</a></li>\n" +
"    </ul>\n" +
"  </div>\n" +
"</nav>";
        
    }
    
    
}
