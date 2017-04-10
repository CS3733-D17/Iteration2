/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.ui.web;

import com.slackers.inc.Controllers.AccountController;
import com.slackers.inc.database.entities.Manufacturer;
import static com.slackers.inc.ui.web.WebComponentProvider.WEB_ROOT;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Jason
 */
@WebServlet(name = "ApplicationsPageServlet", urlPatterns = {"/All"})
public class ApplicationsPageServlet extends HttpServlet {

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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            pg = WebComponentProvider.getCorrectFrame(request, "applicationPage");
            pg.setBody(WebComponentProvider.loadPartialPage(this, "applicationList-partial.html"));
            out.println(WebComponentProvider.buildPage(pg, request));
            
        }    
        Manufacturer manufacturer = (Manufacturer) (pg.getUser());
        for(int i = 0; i < manufacturer.getApplications().size(); i++){
            StringBuilder b = new StringBuilder();
            b.append("<div class=\"panel panel-default\">\n" +
"                       <div class=\"panel-heading\">\n" +
"                           <div class=\"row\">\n" +
"                               <div class=\"col-md-10\">\n" +
"                                   <a data-toggle=\"collapse\" data-parent=\"#applicationAccordion\" href=\"#collapse" + i + "\" style=\"font-size: 20px;\">" + manufacturer.getApplications().get(i).getLabel().getBrandName() + "</a>\n" +
"                               </div>\n" +
"                               <div class=\"col-md-1 pull-right\">\n" +
"                                   <button class='btn btn-primary btn-block'>Edit</button>\n" +
"                               </div>\n" +
"                           </div>\n" +
"                       </div>\n" +
"                   <div id=\"collapse"+ i + "\" class=\"panel-collapse collapse in\">\n" +
"                       <div class=\"panel-body\">Drink information</div>\n" +
"                       </div>\n" +
"                   </div>");
            b.append(pg.getBody());
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request,response);
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
