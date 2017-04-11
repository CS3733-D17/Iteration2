/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.ui.web.form;

import com.slackers.inc.Controllers.LabelApplicationController;
import com.slackers.inc.database.entities.Label;
import com.slackers.inc.database.entities.LabelApplication;
import com.slackers.inc.ui.web.WebComponentProvider;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
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
@WebServlet(name = "LabelImageGet", urlPatterns = {"/image/label"})
@MultipartConfig
public class LabelImageGenerator extends HttpServlet {

    public static String getAccessStringForApplication(HttpServletRequest request, LabelApplication app)
    {
        return WebComponentProvider.root(request)+"image/label?id="+Long.toString(app.getApplicationId());
    }
    public static String getAccessStringForApplication(HttpServletRequest request, LabelApplicationController app)
    {
        return WebComponentProvider.root(request)+"image/label?id="+Long.toString(app.getApplicationId());
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
        try (OutputStream out = response.getOutputStream()) {
            
            if (request.getParameter("id")!=null)
            {
                long id = Long.parseLong(request.getParameter("id"));
                LabelApplicationController appControl = new LabelApplicationController();
                Label label = appControl.getLabelImage(id);
                
                response.setContentType(label.getLabelImageType());
                ByteArrayInputStream bis = new ByteArrayInputStream(label.getLabelImage());
                int len = 0;
                byte[] buffer = new byte[1024];
                while ((len = bis.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(LabelImageGenerator.class.getName()).log(Level.SEVERE, null, ex);
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