/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.ui.web;

import com.slackers.inc.Controllers.AccountController;
import static com.slackers.inc.Controllers.AccountController.getPageUser;
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
            pg = WebComponentProvider.getCorrectFrame(request, "settings");
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
            else{
                settings = settings.replace("##Manufacturer", "");
                UsEmployee employee = (UsEmployee) (pg.getUser());
                settings = settings.replace("##userFirstName", employee.getFirstName());
                settings = settings.replace("##userEmail", employee.getEmail());
                settings = settings.replace("##userLastName", employee.getLastName());
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
            IPageFrame pg = WebComponentProvider.getCorrectFrame(request, "settings");
            try {
                User user = getPageUser(request);
                System.out.println(user);
                user.setFirstName(request.getParameter("firstName"));
                user.setLastName(request.getParameter("lastName"));
                user.setEmail(pg.getUser().getEmail());
                account = new AccountController(user);
                
                
                
                if (account.editAccount()){
                 System.out.println("Works");   
                } else {
                    System.out.println("Dont work");   
                }
                pg.setUser(user);
                System.out.println(user);
            } catch (SQLException ex) {
                Logger.getLogger(ManufacturerSettingServlet.class.getName()).log(Level.SEVERE, null, ex);
                
            }
            
            pg.setBody(WebComponentProvider.loadPartialPage(this, "settings-partial.html"));
            out.println(WebComponentProvider.buildPage(pg, request));
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
