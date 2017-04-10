/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.ui.web;

import java.com.slackers.inc.Controllers.AccountController;
import java.com.slackers.inc.database.entities.User;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Scanner;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
public class WebComponentProvider {
    
    public static final String ACTION_SUCCESS_COOKIE = "SSINCSuc";
    public static final String WEB_ROOT = "http://localhost:8080/SuperSlackers/";
    
    public static String root(HttpServletRequest request)
    {
        return "http://" + request.getServerName()+":"+request.getServerPort()+"/SuperSlackers/";
    }
    
    public static String buildPage(IPageFrame frame, HttpServletRequest request)
    {
        StringBuilder b = new StringBuilder();
        b.append("<!DOCTYPE html>\n" +
                    "<html lang=\"en\"><head> <meta charset=\"utf-8\">\n" +
                    "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                    "  <link rel=\"stylesheet\" href=\""+WEB_ROOT+"css/bootstrap.min.css\">\n" +
                    "  <script src=\""+WEB_ROOT+"jquery-3.2.0.min.js\"></script>\n" +
                "  <script src=\""+WEB_ROOT+"js/Utils.js\"></script>\n" +
                    "  <script src=\""+WEB_ROOT+"js/bootstrap.min.js\"></script>\n");
        b.append("<title>").append(frame.getTitle()).append("</title><body>");
        b.append(frame.getNavBar(request));
        b.append(frame.getBody());
        b.append("</body></html>");
        return b.toString();
    }
    
    public static String printParameters(HttpServletRequest request)
    {
        StringBuilder b = new StringBuilder();
        for (Entry<String,String[]> e : request.getParameterMap().entrySet())
        {
            b.append("<br>").append("Key: ").append(e.getKey());
            b.append("<br>").append("Value: ").append(e.getValue()[0]).append("<br>");
        }
        return b.toString();
    }
    
    public static String loadPartialPage(HttpServlet source, String pagename)
    {
        InputStream login = source.getServletContext().getResourceAsStream("/WEB-INF/partial/"+pagename);
        Scanner s = new Scanner(login).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
    
    public static void setSuccessMessage(HttpServletResponse response, String message)
    {
        Cookie cookie = new Cookie(ACTION_SUCCESS_COOKIE, message);
        cookie.setMaxAge(60); // 1 minute
        response.addCookie(cookie);
    }
    
    public static IPageFrame getCorrectFrame(HttpServletRequest request, String title)
    {
        IPageFrame out;        
        User pageUser = AccountController.getPageUser(request);
        if (pageUser == null)
            return new DefaultPage(title);
        User.UserType type = pageUser.getUserType();
        if (type == User.UserType.MANUFACTURER)
        {
            out = new ManufacturerPage(title);
        }
        else if (type == User.UserType.US_EMPLOYEE)
        {
            out = new EmployeePage(title);
        }
        else
        {
            out = new DefaultPage(title);
        }
        out.setUser(pageUser);
        return out;
    }
}
