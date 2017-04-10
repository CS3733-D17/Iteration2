/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.ui.web.form;

import com.slackers.inc.Controllers.AccountController;
import com.slackers.inc.Controllers.LabelApplicationController;
import com.slackers.inc.database.entities.User;
import com.slackers.inc.database.entities.User.UserType;
import com.slackers.inc.ui.web.IPageFrame;
import com.slackers.inc.ui.web.WebComponentProvider;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
@WebServlet(name = "FormView", urlPatterns = {"/form/view"})
public class FormView extends HttpServlet {

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
            LabelApplicationController appControl = new LabelApplicationController();
            Long appId = Long.parseLong(request.getParameter("id"));
            
            appControl.loadApplication(appId);
            appControl.writeApplicationToCookies(response);
            WebComponentProvider.setSuccessMessage(response, null);
            String form = WebComponentProvider.loadPartialPage(this, "view-label.html");
            String formTemplate = WebComponentProvider.loadPartialPage(this, "label-form.html");
            form = form.replace("##FORM_CONTENT", formTemplate);
            
            User usr = AccountController.getPageUser(request);
            if (usr==null || usr.getUserType() != UserType.MANUFACTURER || usr.getUserType() != UserType.US_EMPLOYEE)
            { }
            else if (usr.getUserType() != UserType.MANUFACTURER)
            {
                form = form.replace("##ACTION_BUTTON", " <a href = \"/SuperSlackers/form/edit?id=##ID\" class=\"btn btn-warning\" style=\"width:100%; margin-top: 30px;\">Edit Label Application</a>");
            }
            else if (usr.getUserType() != UserType.US_EMPLOYEE)
            {
                form = form.replace("##ACTION_BUTTON", " <a href = \"/SuperSlackers/form/process?id=##ID\" class=\"btn btn-warning\" style=\"width:100%; margin-top: 30px;\">Process Label Application</a>");
            }            
            IPageFrame pg = WebComponentProvider.getCorrectFrame(request, "View Label Application");
            pg.setBody(form.replace("##ID", Long.toString(appId))+appControl.renderCommentList(request));
            out.println(WebComponentProvider.buildPage(pg, request));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            response.sendRedirect(WebComponentProvider.root(request));
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
        doGet(request, response);
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
