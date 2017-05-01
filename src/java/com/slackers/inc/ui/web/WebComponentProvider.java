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
            "    background-attachment: fixed; \" ";
            background = " style=\"background: url('/SuperSlackers/background.jpg') no-repeat center center fixed; \n" +
            "  -webkit-background-size: cover;\n" +
            "  -moz-background-size: cover;\n" +
            "  -o-background-size: cover;\n" +
            "  background-size: cover;\" ";
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
        b.append(getHelpPage()).append(getThemeSelector()).append("</body></html>");
        return b.toString();
    }

    private static String getHelpPage() 
    {
        return 
"<div class=\"modal fade\" id=\"helpModal\" role=\"dialog\">\n" +
"    <div class=\"modal-dialog modal-lg\">\n" +
"        <!-- Modal content-->\n" +
"        <div class=\"modal-content\">\n" +
"\n" +
"            <div class=\"modal-body\">\n" +
"                <ul class=\"nav nav-tabs\">\n" +
"                    <li class=\"active\"><a data-toggle=\"tab\" href=\"#account_help\">Account Creation</a></li>\n" +
"                    <li><a data-toggle=\"tab\" href=\"#search_help\">Search Help</a></li>\n" +
"                    <li class=\"dropdown\">\n" +
"                        <a class=\"dropdown-toggle\" data-toggle=\"dropdown\" href=\"#\">Manufacturer Help\n" +
"                            <span class=\"caret\"></span></a>\n" +
"                        <ul class=\"dropdown-menu\">\n" +
"                            <li><a data-toggle=\"tab\" href=\"#form_submit_help\">Form Submission</a></li>\n" +
"                            <li><a data-toggle=\"tab\" href=\"#form_edit_help\">Form Editing</a></li>\n" +
"                            <li><a data-toggle=\"tab\" href=\"#form_import_help\">Form Importing</a></li>\n" +
"                        </ul>\n" +
"                    </li>\n" +
"                    <li class=\"dropdown\">\n" +
"                        <a class=\"dropdown-toggle\" data-toggle=\"dropdown\" href=\"#\">Employee Help\n" +
"                            <span class=\"caret\"></span></a>\n" +
"                        <ul class=\"dropdown-menu\">\n" +
"                            <li><a data-toggle=\"tab\" href=\"#process_help\">Application Processing Workflow</a></li>\n" +
"                            <li><a data-toggle=\"tab\" href=\"#form_process_help\">Application Processing</a></li>  \n" +
"                        </ul>\n" +
"                    </li>\n" +
"                </ul>\n" +
"\n" +
"                <div class=\"tab-content\">\n" +
"                    <div id=\"account_help\" class=\"tab-pane fade in active\">\n" +
"                        <h1>Create Account Help:</h1>\n" +
"                        <hr>\n" +
"                        <h3>Please fill the specified fields.</h3>\n" +
"                        <br>\n" +
"                        <br><strong>First Name Example:</strong> Bob\n" +
"                        <br><strong>Last Name Example:</strong> Wong\n" +
"                        <br>\n" +
"                        <br><strong>Email Address Example:</strong> name@email.com\n" +
"                        <br><strong>Password:</strong> any desired characters (ex: Peter1234!)\n" +
"                    </div>\n" +
"                    <div id=\"search_help\" class=\"tab-pane fade\">\n" +
"                        <h1>Label Search Help:</h1>\n" +
"                        <hr>\n" +
"                        <p><strong>Search Field:</strong> Enter the name of the brand you want to search for</p>\n" +
"                        <br>\n" +
"                        <strong>Advanced:</strong>\n" +
"                        <br><strong>Beverage Type:</strong> Select From “All”, “Wine”, “Beer”, or “Distilled”\n" +
"                        <br>\n" +
"                        <br><strong>Select Search Type:</strong> “Brand Name” or “Fanciful Name” or “Either”<br>\n" +
"                        <br><strong>Select Product Source:</strong> “DOMESTIC” or “IMPORTED”<br>\n" +
"                        <br><strong>Origin Code:</strong> Enter either the numerical code or the description of the origin code. Autocomplete options will be shown.<br>\n" +
"                        <br><strong>Product Category Code:</strong> Enter either the numerical code or the description of the product category code. Autocomplete options will be shown.<br>\n" +
"                        <br>\n" +
"                        <br><strong>Alcohol Content:</strong> Search for alcohol content “equal to” or “between”<br>\n" +
"                        <br><strong>Values:</strong> Enter the alcohol content value(s) for the search\n" +
"                        <br>\n" +
                        "<br><strong>Only Labels with Images:</strong> Only show results for labels with images<br>\n" +
"                        <br><strong>Wine Only:</strong>\n" +
"                        <br><strong>pH Level</strong> Select a pH level “equal to” or “between”\n" +
"                        <br><strong>Values:</strong> Enter the pH level(s) for the search (pH is between 0-14)<br>\n" +
                        "<br><strong>Vintage Year</strong> Select a vintage year “equal to” or “between”\n" +
"                        <br><strong>Values:</strong> Enter the vintage year(s) for the search\n" +
"                    </div>\n" +
"                    <div id=\"form_submit_help\" class=\"tab-pane fade\">\n" +
"                        <h1>Application Form Help:</h1>\n" +
"                        <hr>\n" +
"                        <strong>Representative ID:</strong> Your employee identification number for the manufacturer that you work for\n" +
"                        <br>\n" +
"                        <strong>Phone Number:</strong> Your work phone number (###-###-####)\n" +
"                        <br>\n" +
"                        <strong>Plant Number:</strong> Plant Identification Number (@@-@@-####)\n" +
"                        <br>\n" +
"                        <strong>Email Address:</strong> email where name@email.com\n" +
"                        <br>\n" +
"                        <br>\n" +
"                        <strong>Application Address:</strong> (Address of applicant)\n" +
"                        <br>Location Name (ex: manufacturer)\n" +
"                        <br>Street Address (ex: 23 Hickory Drive)\n" +
"                        <br>Town State, Zip (ex: Worcester MA, 01541)\n" +
"                        <br><strong>Mailing Address:</strong> (if different from Application Address)\n" +
"                        <br>Location Name (ex: manufacturer)\n" +
"                        <br>Street Address (ex: 23 Hickory Drive)\n" +
"                        <br>Town    State, Zip (ex: Worcester MA, 01541)\n" +
"                        <br>\n" +
"                        <br><strong>Serial Number:</strong> ##-####\n" +
"                        <br><strong>Serial Number Example:</strong> 55-1234\n" +
"                        <br>\n" +
"                        <br><strong>Alcohol content:</strong> 0-100(%)\n" +
"                        <br><strong>AC Example:</strong> 10.4\n" +
"                        <br>\n" +
"                        <br><strong>State:</strong> The state that the company you work for is located in (ex: MA)\n" +
"                        <br>\n" +
"                        <br><strong>Bottle capacity:</strong> The capacity of the bottle (mL)\n" +
"                        <br><strong>Capacity Example:</strong> 250\n" +
"                        <br>\n" +
"                        <br><strong>TTB ID:</strong> The identification number assigned to the COLA that you work for (ex: 12345678901234)\n" +
"                        <br>\n" +
"                        <br><strong>Brand Name Example:</strong> Samuel Adams\n" +
"                        <br>\n" +
"                        <br><strong>Fanciful Name Example:</strong> Pinot Noir\n" +
"                        <br>\n" +
"                        <br><strong>Additional Information:</strong> any additional information about the application\n" +
"                    </div>\n" +
"                    <div id=\"form_edit_help\" class=\"tab-pane fade\">\n" +
"                        <h1>Application Edit Help:</h1>\n" +
"                        <hr>\n" +
"                        <br><strong>Allowed Revisions:</strong> select all fields you wish to revise\n" +
"                        <br> The same restrictions to fields apply as did when creating the application.\n" +
"                        <br><strong>Submit Revisions:</strong> submit the edited application for review in place of the old application.\n" +
"                    </div>\n" +
"                    <div id=\"form_import_help\" class=\"tab-pane fade\">\n" +
"                        <h1>Import an Existing Application:</h1>\n" +
"                        <hr><br>\n" +
"                        <ul>\n" +
"                            <li>Click the import button at the top of the create application page</li>\n" +
"                            <li>Enter the existing TTB number into the text field</li>\n" +
"                            <li>Click import to begin importing the form</li>\n" +
"                            <li>Fill in any fields that were not found in the existing form with your label information</li>\n" +
"                            <li>Click “Submit” when you are finished.</li>\n" +
"                        </ul>\n" +
"                    </div>\n" +
"                    <div id=\"form_process_help\" class=\"tab-pane fade\">\n" +
"                        <h1>Application Processing Help:</h1>\n" +
"                        <hr>\n" +
"                        <br><strong>Accept:</strong> Accept the application and allow the label to be searched\n" +
"                        <br><strong>Send for Corrections:</strong> Send the application back to the applicant for corrections before accepting it\n" +
"                        <br><strong>Reject:</strong> Reject the application and prevent it from ever showing up on a search\n" +
"                        <br><strong>Send for Second Opinion:</strong> If you are unsure, send it to another employee for review with comments\n" +
"                        <br><strong>Second Opinion Employee:</strong> Email of employee desired to give a second opinion. This will provide autocomplete options\n" +
"                    </div>\n" +
"                    <div id=\"process_help\" class=\"tab-pane fade\">\n" +
"                        <h1>Application Processing Workflow:</h1>\n" +
"                        <br>\n" +
"                        An employee can look at the list of applications they have been assigned, \n" +
"                        and can review them. The name of the label application and some basic information is located in each box.\n" +
"                        <br>\n" +
"                        Click the blue “Review Application” button to review the application.\n" +
"                        <br>\n" +
"                        Employees get applications limited to ten at a time.\n" +
"                        <br>\n" +
"                        New applications will be automatically pulled when all current forms in the inbox are finished.\n" +
"                    </div>\n" +
"                </div>\n" +
"            </div>\n" +
"            <div class=\"modal-footer\">\n" +
"                <button type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\">Close</button>\n" +
"            </div>\n" +
"        </div>\n" +
"    </div>\n" +
"</div>\n" +
"<button type=\"button\" class=\"btn btn-primary btn-md\" id=\"myBtn\" style=\"float:left; margin: 15px;\" data-toggle=\"modal\" data-target=\"#helpModal\">Help</button>\n";
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
