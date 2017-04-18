/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.ui.web;

import com.slackers.inc.Controllers.AccountController;
import com.slackers.inc.database.entities.Admin;
import com.slackers.inc.database.entities.Manufacturer;
import com.slackers.inc.database.entities.UsEmployee;
import com.slackers.inc.database.entities.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author jestrada
 */
@WebServlet(name = "ManufacturerSettingServlet", urlPatterns = {"/account/settings"})
public class ManufacturerSettingServlet extends HttpServlet {

    IPageFrame pg;
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
         try (PrintWriter out = response.getWriter()) {
            pg = WebComponentProvider.getCorrectFrame(request, "Account Settings");
            String settings = WebComponentProvider.loadPartialPage(this, "settings-partial.html");
            User.UserType type = pg.getUser().getUserType();
            System.out.println(pg.getUser());
            if(type == User.UserType.MANUFACTURER){
                StringBuilder b = new StringBuilder();
                String manufacturerSettings = WebComponentProvider.loadPartialPage(this, "manufacturerSettings.html");
                settings = settings.replace("##Manufacturer", manufacturerSettings);
                
                Manufacturer manufacturer = (Manufacturer) (pg.getUser());
                settings = settings.replace("##userFirstName", manufacturer.getFirstName());
                settings = settings.replace("##userEmail", manufacturer.getEmail());
                settings = settings.replace("##userLastName", manufacturer.getLastName());

            }
            else if(type == User.UserType.US_EMPLOYEE){
                settings = settings.replace("##Manufacturer", "");
                UsEmployee employee = (UsEmployee) (pg.getUser());
                settings = settings.replace("##userFirstName", employee.getFirstName());
                settings = settings.replace("##userEmail", employee.getEmail());
                settings = settings.replace("##userLastName", employee.getLastName());
            }
            else if(type == User.UserType.ADMIN){
                settings = settings.replace("##Manufacturer", "");
                Admin admn = (Admin) (pg.getUser());
                settings = settings.replace("##userFirstName", admn.getFirstName());
                settings = settings.replace("##userEmail", admn.getEmail());
                settings = settings.replace("##userLastName", admn.getLastName());
            }
            pg.setBody(settings);
            out.println(WebComponentProvider.buildPage(pg, request));
            
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        AccountController account = null;
       
        try (PrintWriter out = response.getWriter()) {
            IPageFrame pg = WebComponentProvider.getCorrectFrame(request, "Account Settings");

            pg.getUser().setFirstName(request.getParameter("firstName"));
            pg.getUser().setLastName(request.getParameter("lastName"));
            pg.getUser().setEmail(request.getParameter("email"));
            pg.getUser().setUpdateMode(true);
            
            try {
                AccountController c = new AccountController(pg.getUser());
                c.editAccount();
                c.loginUser(request, response);
            } catch (SQLException ex) {
                Logger.getLogger(ManufacturerSettingServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
            response.sendRedirect("/SuperSlackers/account/settings");
        }
    }
    
    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
