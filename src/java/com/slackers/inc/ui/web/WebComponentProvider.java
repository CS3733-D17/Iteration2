/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.ui.web;

import com.slackers.inc.Controllers.AccountController;
import com.slackers.inc.database.entities.User;
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
    public static final String THEME_COOKIE = "SSINC_THEME";
    public static final String WEB_ROOT = "/SuperSlackers/";//"http://localhost:8080/SuperSlackers/";

    public static String root(HttpServletRequest request) {
        return "http://" + request.getServerName() + ":" + request.getServerPort() + "/SuperSlackers/";
    }

    public static String buildPage(IPageFrame frame, HttpServletRequest request) {
        String theme = getCookieValue(request, THEME_COOKIE);
        if (theme == null || theme.length()<3) {
            theme = "";
        } else {
            theme = theme + "-";
        }
        String background = "";
        if (theme.equals("green-"))
        {
            background = " background=\"" + WEB_ROOT + "background.jpg\" style=\"background-repeat: no-repeat;\n" +
            "    background-attachment: fixed;\" ";
        }
        StringBuilder b = new StringBuilder();
        b.append("<!DOCTYPE html>\n"
                + "<html lang=\"en\"><head> <meta charset=\"utf-8\">\n"
                + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
                + "  <link rel=\"stylesheet\" href=\"" + WEB_ROOT + "css/" + theme + "bootstrap.min.css\">\n"
                + "  <link rel=\"stylesheet\" href=\"" + WEB_ROOT + "css/" + theme + "bootstrap-theme.min.css\">\n"
                + "  <link rel=\"stylesheet\" href=\"" + WEB_ROOT + "css/inputStyles.css\">\n"
                + "  <script src=\"" + WEB_ROOT + "jquery-3.2.0.min.js\"></script>\n"
                + "  <script src=\"" + WEB_ROOT + "js/Utils.js\"></script>\n"
                + "  <script src=\"" + WEB_ROOT + "js/bootstrap.min.js\"></script>\n");
        b.append("<title>").append(frame.getTitle()).append("</title><body"+background+"style=\"background-size: 100%; background-attachment: fixed;\">");
        b.append(frame.getNavBar(request));
        b.append(frame.getBody());
        b.append(getThemeSelector()).append("</body></html>");
        return b.toString();
    }

    private static String getThemeSelector() {
        return "<div class=\"dropup\" style=\"float:right; margin: 15px;\">\n"
                + "    <button class=\"btn btn-default dropdown-toggle\" type=\"button\" data-toggle=\"dropdown\">Theme Selector\n"
                + "    <span class=\"caret\"></span></button>\n"
                + "    <ul class=\"dropdown-menu\">\n"
                + "      <li><a onclick=\"setCookie('SSINC_THEME','',7);location.reload();\">Default</a></li>\n"
                + "      <li><a onclick=\"setCookie('SSINC_THEME','blue',7);location.reload();\">Blue</a></li>\n"
                + "      <li><a onclick=\"setCookie('SSINC_THEME','green',7);location.reload();\">Green</a></li>\n"
                + "      <li><a onclick=\"setCookie('SSINC_THEME','lumen',7);location.reload();\">Lumen</a></li>\n"
                + "      <li><a onclick=\"setCookie('SSINC_THEME','paper',7);location.reload();\">Paper</a></li>\n"
                + "    </ul>\n"
                + "  </div>";
    }

    public static String buildFrontPage(IPageFrame frame, HttpServletRequest request) {
        StringBuilder b = new StringBuilder();
        b.append("<!DOCTYPE html>\n"
                + "   <html lang=\"en\"><head> <meta charset=\"utf-8\">\n"
                + "   <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n"
                + "   <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n"
                + "   <meta name=\"description\" content=\"\">\n"
                + "   <meta name=\"author\" content=\"SUP3R SLACK3rs\">\n"
                + "    <link href=\"css/normal.bootstrap.min.css\" rel=\"stylesheet\">\n"
                + "    <link href=\"css/font-awesome.css\" rel=\"stylesheet\" type=\"text/css\">\n"
                + "    <link href='https://fonts.googleapis.com/css?family=Open+Sans:300italic,400italic,600italic,700italic,800italic,400,300,600,700,800' rel='stylesheet' type='text/css'>\n"
                + "    <link href='https://fonts.googleapis.com/css?family=Merriweather:400,300,300italic,400italic,700,700italic,900,900italic' rel='stylesheet' type='text/css'>\n"
                + "    <link href=\"css/magnific-popup.css\" rel=\"stylesheet\">\n"
                + "    <link href=\"css/creative.css\" rel=\"stylesheet\">");
        b.append("<title>").append(frame.getTitle()).append("</title><body id=\"page-top\">");
        b.append(frame.getBody());
        b.append("</body></html>");
        return b.toString();
    }

    public static String printParameters(HttpServletRequest request) {
        StringBuilder b = new StringBuilder();
        for (Entry<String, String[]> e : request.getParameterMap().entrySet()) {
            b.append("<br>").append("Key: ").append(e.getKey());
            b.append("<br>").append("Value: ").append(e.getValue()[0]).append("<br>");
        }
        return b.toString();
    }

    public static String loadPartialPage(HttpServlet source, String pagename) {
        InputStream login = source.getServletContext().getResourceAsStream("/WEB-INF/partial/" + pagename);
        Scanner s = new Scanner(login).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public static void setSuccessMessage(HttpServletResponse response, String message) {
        Cookie cookie = new Cookie(ACTION_SUCCESS_COOKIE, message);
        cookie.setMaxAge(60); // 1 minute
        response.addCookie(cookie);
    }

    public static IPageFrame getCorrectFrame(HttpServletRequest request, String title) {
        IPageFrame out;
        User pageUser = AccountController.getPageUser(request);
        if (pageUser == null) {
            return new DefaultPage(title);
        }
        User.UserType type = pageUser.getUserType();
        if (type == User.UserType.MANUFACTURER) {
            out = new ManufacturerPage(title);
        } else if (type == User.UserType.US_EMPLOYEE) {
            out = new EmployeePage(title);
        } else if (type == User.UserType.ADMIN) {
            out = new AdminPage(title);
        } else {
            out = new DefaultPage(title);
        }
        out.setUser(pageUser);
        return out;
    }

    public static String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies==null)
            return null;
        for (int i = 0; i < cookies.length; i++) {
            if (cookies[i].getName().equals(cookieName)) {
                return cookies[i].getValue();
            }
        }
        return null;
    }
}
