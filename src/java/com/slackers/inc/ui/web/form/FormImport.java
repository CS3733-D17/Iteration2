/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.ui.web.form;

import com.slackers.inc.Controllers.AccountController;
import com.slackers.inc.Controllers.LabelApplicationController;
import com.slackers.inc.database.entities.LabelApplication;
import com.slackers.inc.database.entities.LabelComment;
import com.slackers.inc.database.entities.Manufacturer;
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
@WebServlet(name = "FormImport", urlPatterns = {"/form/import"})
@MultipartConfig
public class FormImport extends HttpServlet {

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
            
            String importId = request.getParameter("existingId");
            if (importId==null)
            {
                response.sendRedirect("/SuperSlackers/form/create");
                return;
            }
            
            String form = WebComponentProvider.loadPartialPage(this, "submit-label.html");
            String formTemplate;
            if (WebComponentProvider.getCookieValue(request, "SSVIEW_MODE")!=null && WebComponentProvider.getCookieValue(request, "SSVIEW_MODE").equalsIgnoreCase("legacy"))
            {
                formTemplate = WebComponentProvider.loadPartialPage(this, "label-form-partial-legacy.html"); 
            }
            else
            {
                formTemplate = WebComponentProvider.loadPartialPage(this, "label-form.html");
            }
            
            IPageFrame pg = WebComponentProvider.getCorrectFrame(request, "Import Label Application");
            
            form = form.replace("/SuperSlackers/form/create","/SuperSlackers/form/import?existingId="+importId);
            
            
            
            FormImporter importer = new FormImporter(importId);
            LabelApplicationController appControl = new LabelApplicationController();
            LabelApplication imported = importer.importApplication();
            if (imported!=null)
            {
                appControl.setApplication(imported);
                appControl.setEmailAddress(pg.getUser().getEmail());
                appControl.setApplicant((Manufacturer)pg.getUser());
                formTemplate = formTemplate.replace("<img id=\"lblImg\" src=\"##LABEL_IMAGE_PATH\" class=\"img-responsive\" alt=\"Label Image\">",
                        "<img id=\"lblImg\" src=\"##LABEL_IMAGE_PATH\" class=\"img-responsive\" alt=\"Label Image\">"+
                                "<a target=\"_blank\" href=\""+importer.getExistingApplicationURL()+"\" class=\"btn btn-danger\">Go to existing form to download image</a>"+
                                "<input type=\"hidden\" name=\"useUrl\" value=\""+importer.getTTBid()+"\">");
                formTemplate = formTemplate.replace("##LABEL_IMAGE_PATH",LabelImageGenerator.getAccessStringForExistingApplication(request, importId));
            }
            else
            {
                formTemplate = formTemplate.replace("##LABEL_IMAGE_PATH","");
            }
            appControl.removeApplicationFromCookies(response);
            appControl.writeApplicationToCookies(response);
            appControl.writeLabelToCookies(response);
            
            //String error = appControl.validateApplication();
            //WebComponentProvider.setSuccessMessage(response, error);
            
            
            pg.setBody(form.replace("##FORM_CONTENT", formTemplate));
            out.println(WebComponentProvider.buildPage(pg, request));
            
        } catch (SQLException ex) {
            Logger.getLogger(FormImport.class.getName()).log(Level.SEVERE, null, ex);
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
            
            String importId = request.getParameter("existingId");
            if (importId==null)
            {
                response.sendRedirect("/SuperSlackers/form/create");
                return;
            }
            
            LabelApplicationController appControl = new LabelApplicationController();
            appControl.createApplicationFromRequest(this.getServletContext(), request);
            appControl.writeApplicationToCookies(response);
            appControl.writeLabelToCookies(response);
            System.out.println(appControl.getApplication());
            String error = appControl.validateApplication();
            if (error==null)
            {
                try
                {
                    WebComponentProvider.setSuccessMessage(response, null);
                    Manufacturer man = (Manufacturer)AccountController.getPageUser(request);
                    appControl.attachComment(new LabelComment(man, "<h4>Imported application from existing source</h4><a class=\"btn btn-default\" target=\"_blank\" href=\""+FormImporter.COLAURL+importId+"\">Visit existing application</a>"));
                    appControl.submitApplication(man);
                    IPageFrame pg = WebComponentProvider.getCorrectFrame(request, "Form Submission Complete");
                    pg.setBody(WebComponentProvider.loadPartialPage(this, "form-submitted.html").replace("##ID", Long.toString(appControl.getLabelApplication().getApplicationId())));
                    out.println(WebComponentProvider.buildPage(pg, request));
                    
                }
                catch(Exception e){
                    WebComponentProvider.setSuccessMessage(response, "Unknown Error");
                    e.printStackTrace();
                    response.sendRedirect("/SuperSlackers/form/import?existingId="+importId);
                }
            }
            else
            {
                //out.println(WebComponentProvider.printParameters(request)+"<br><br>");
                //out.println(appControl.getLabelApplication().toString());
                WebComponentProvider.setSuccessMessage(response, error);
                response.sendRedirect("/SuperSlackers/form/import?existingId="+importId);
            }
        } catch (SQLException ex) {
            Logger.getLogger(FormCreate.class.getName()).log(Level.SEVERE, null, ex);
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
