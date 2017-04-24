/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.ui.web;

import com.slackers.inc.database.entities.LabelApplication;
import com.slackers.inc.database.entities.LabelApplication.ApplicationStatus;
import com.slackers.inc.database.entities.Manufacturer;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Jason
 */
@WebServlet(name = "ApplicationsPageServlet", urlPatterns = {"/myApplications"})
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
            pg = WebComponentProvider.getCorrectFrame(request, "Application Page");
            String applications = WebComponentProvider.loadPartialPage(this, "applicationList-partial.html");
            
            Manufacturer manufacturer = (Manufacturer) (pg.getUser());
            StringBuilder b = new StringBuilder();
            List<LabelApplication> apps = manufacturer.getApplications();
            ApplicationStatus targetStatus = ApplicationStatus.UNKNOWN;
            if (request.getParameter("subset")!=null)
            {
                if (request.getParameter("subset").equalsIgnoreCase("working"))
                {
                    targetStatus = ApplicationStatus.NOT_COMPLETE;
                }
                if (request.getParameter("subset").equalsIgnoreCase("submitted"))
                {
                    targetStatus = ApplicationStatus.SUBMITTED;
                }
                if (request.getParameter("subset").equalsIgnoreCase("underReview"))
                {
                    targetStatus = ApplicationStatus.UNDER_REVIEW;
                }
                if (request.getParameter("subset").equalsIgnoreCase("accepted"))
                {
                    targetStatus = ApplicationStatus.APPROVED;
                }
                if (request.getParameter("subset").equalsIgnoreCase("rejected"))
                {
                    targetStatus = ApplicationStatus.REJECTED;
                }
            }
            
            for(int i = 0; i < apps.size(); i++){
                if (targetStatus == ApplicationStatus.UNKNOWN || apps.get(i).getStatus()== targetStatus)
                {
                b.append("<div class=\"panel panel-default\">\n" +
"                           <div class=\"panel-heading\">\n" +
"                               <div class=\"row\">\n" +
"                                   <div class=\"col-md-10\">\n" +
"                                       <a data-toggle=\"collapse\" data-parent=\"#applicationAccordion\" href=\"#collapse" + i + "\" style=\"font-size: 20px;\">" + apps.get(i).getLabel().getBrandName() +
                        (apps.get(i).getLabel().getFancifulName().length()>2 ? " ("+apps.get(i).getLabel().getFancifulName()+")" : "")+ "</a>\n" +
                        getStatusBadge(apps.get(i))+
"                                   </div>\n" +
"                                   <div class=\"col-md-2\">\n" +
"                                       <a href=\"/SuperSlackers/form?action=view&id="+Long.toString(apps.get(i).getApplicationId())+"\"class='btn btn-primary btn-block'>View</a>\n" +
"                                   </div>\n" +
"                               </div>\n" +
"                           </div>\n" +
"                       <div id=\"collapse"+ i + "\" class=\"panel-collapse collapse\">\n" +
"                           <div class=\"panel-body\">"+ManufacturerSearchServlet.renderLabel(this, request, apps.get(i).getLabel())+"</div>\n" +
"                           </div>\n" +
"                       </div>");
                }
            }
            
            applications = applications.replace("##Applications", b);
            pg.setBody(applications);
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request,response);
    }
    
    private String getStatusBadge(LabelApplication app)
    {
        if (app.getStatus() == ApplicationStatus.APPROVED)
        {
            return "<span style=\"margin-left:30px;\" class=\"label label-success\">Approved</span>";
        }
        if (app.getStatus() == ApplicationStatus.REJECTED)
        {
            return "<span style=\"margin-left:30px;\" class=\"label label-danger\">Rejected</span>";
        }
        if (app.getStatus() == ApplicationStatus.UNDER_REVIEW)
        {
            return "<span style=\"margin-left:30px;\" class=\"label label-warning\">Under Review</span>";
        }
        return "";
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
