/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.ui.web.form;

import com.slackers.inc.Controllers.AccountController;
import com.slackers.inc.Controllers.LabelApplicationController;
import com.slackers.inc.database.entities.Label.BeverageType;
import com.slackers.inc.database.entities.Manufacturer;
import com.slackers.inc.database.entities.User;
import com.slackers.inc.ui.web.IPageFrame;
import com.slackers.inc.ui.web.WebComponentProvider;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
@WebServlet(name = "FormCreate", urlPatterns = {"/form/create"})
@MultipartConfig
public class FormCreate extends HttpServlet {

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
            String form = WebComponentProvider.loadPartialPage(this, "submit-label.html");
            String formTemplate = WebComponentProvider.loadPartialPage(this, "label-form.html");
            formTemplate = formTemplate.replace("##LABEL_IMAGE_PATH","");
            IPageFrame pg = WebComponentProvider.getCorrectFrame(request, "Create Label Application");
            pg.setBody(form.replace("##FORM_CONTENT", formTemplate));
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
        try (PrintWriter out = response.getWriter()) {
            System.out.println("Do something");
            LabelApplicationController appControl = new LabelApplicationController();
            appControl.createApplicationFromRequest(this.getServletContext(), request);
            appControl.writeApplicationToCookies(response);
            appControl.writeLabelToCookies(response);
            String error = appControl.validateApplication();
            if (error==null)
            {
                try
                {
                    WebComponentProvider.setSuccessMessage(response, null);
                    Manufacturer man = (Manufacturer)AccountController.getPageUser(request);
                    appControl.submitApplication(man);
                    IPageFrame pg = WebComponentProvider.getCorrectFrame(request, "Form Submission Complete");
                    pg.setBody(WebComponentProvider.loadPartialPage(this, "form-submitted.html").replace("##ID", Long.toString(appControl.getLabelApplication().getApplicationId())));
                    out.println(WebComponentProvider.buildPage(pg, request));
                    
                }
                catch(Exception e){
                    response.sendRedirect("/SuperSlackers/form/create");
                }
            }
            else
            {
                //out.println(WebComponentProvider.printParameters(request)+"<br><br>");
                //out.println(appControl.getLabelApplication().toString());
                WebComponentProvider.setSuccessMessage(response, error);
                response.sendRedirect("/SuperSlackers/form/create");
            }
        } catch (SQLException ex) {
            Logger.getLogger(FormCreate.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception e){
            System.out.println("It broke");
            e.printStackTrace();
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
