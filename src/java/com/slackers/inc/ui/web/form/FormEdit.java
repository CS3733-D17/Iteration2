/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.ui.web.form;

import com.slackers.inc.Controllers.AccountController;
import com.slackers.inc.Controllers.LabelApplicationController;
import com.slackers.inc.database.entities.UsEmployee;
import com.slackers.inc.ui.web.IPageFrame;
import com.slackers.inc.ui.web.WebComponentProvider;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Date;
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
@WebServlet(name = "FormEdit", urlPatterns = {"/form/edit"})
@MultipartConfig
public class FormEdit extends HttpServlet {

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
            String form = WebComponentProvider.loadPartialPage(this, "edit-label.html");
            String formTemplate = WebComponentProvider.loadPartialPage(this, "label-form.html");
            form = form.replace("##FORM_CONTENT", formTemplate); 
            form = form.replace("##LABEL_IMAGE_PATH", LabelImageGenerator.getAccessStringForApplication(request, appControl));
            IPageFrame pg = WebComponentProvider.getCorrectFrame(request, "Edit Label Application");
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
        
        
        String action = request.getParameter("action");
        String idStr = request.getParameter("id");
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            long id = Long.parseLong(idStr);
            LabelApplicationController appControl = new LabelApplicationController();
            appControl.loadApplication(id);
            if (appControl.editApplicationFromRequest(this.getServletContext(), request)==null)
            {
                    response.sendRedirect("/SuperSlackers/form/edit?id="+id);
            }
            else
            {
                response.sendRedirect("/SuperSlackers/form/view?id="+id);
            }           
        }catch (Exception e){
            e.printStackTrace();
            try 
            {
                long id = Long.parseLong(idStr);
                response.sendRedirect("/SuperSlackers/form/edit?id="+id);
            }
            catch (Exception ex)
            {
                response.sendRedirect(WebComponentProvider.root(request));
            }
            
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
