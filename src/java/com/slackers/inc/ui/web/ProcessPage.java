/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.ui.web;

import com.slackers.inc.Controllers.UsEmployeeController;
import com.slackers.inc.database.entities.LabelApplication;
import com.slackers.inc.database.entities.UsEmployee;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Jason
 */
@WebServlet(name = "ProcessPageServlet", urlPatterns = {"/employee/applicationList"})
public class ProcessPage extends HttpServlet {

    private String renderApplication(LabelApplication app)
    {
        String fancy = "";
        if (app.getLabel().getFancifulName()!=null && app.getLabel().getFancifulName().length()>2)
            fancy = " ("+app.getLabel().getFancifulName()+")";
        StringBuilder b = new StringBuilder();
        b.append("<div class=\"panel panel-default\">");
        b.append("<div class=\"panel-heading\"><h3>").append(app.getLabel().getBrandName()).append(fancy)
                .append("</h3><a style=\"float:right; position:relative; top:-35px;\" class=\"btn btn-primary\" href=\"/SuperSlackers/form/process?id=")
                .append(app.getApplicationId()).append("\">Review Application</a></div>");
        b.append("<div class=\"panel-body\">");
        
        b.append("<div class=\"row\">");
        b.append("<div class=\"col-sm-4\">");
        b.append("<strong>Application Type </strong>").append(app.getLabel().getProductType());
        b.append("</div>").append("<div class=\"col-sm-4\">");
        b.append("<strong>Application Source </strong>").append(app.getLabel().getProductSource());
        b.append("</div>").append("<div class=\"col-sm-4\">");
        
        b.append("</div></div></div></div>");
        
        return b.toString();
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
            IPageFrame pg = WebComponentProvider.getCorrectFrame(request, "Process Applications");
            UsEmployee employee = (UsEmployee) (pg.getUser());
            UsEmployeeController.fillApplicationList(employee);
            StringBuilder b = new StringBuilder();
            if (employee.getApplications().isEmpty())
            {
                b.append("<h4>No applications to review at this time</h4>");
            }
            else
            {
                for(int i = 0; i < employee.getApplications().size(); i++){
                    b.append(this.renderApplication(employee.getApplications().get(i)));
                }
            }
            String page = WebComponentProvider.loadPartialPage(this, "employee-applist-partial.html");
            pg.setBody(page.replace("##APPLICATION_LIST", b.toString()));
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
