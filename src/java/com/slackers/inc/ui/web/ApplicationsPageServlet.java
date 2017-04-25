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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
            List<LabelApplication> apps = new ArrayList<>(manufacturer.getApplications());
            Set<ApplicationStatus> targetStatus = new HashSet<>();
            targetStatus.add(ApplicationStatus.UNKNOWN);
            if (request.getParameter("subset")!=null)
            {
                if (request.getParameter("subset").equalsIgnoreCase("working"))
                {
                    targetStatus.add(ApplicationStatus.NOT_COMPLETE);
                }
                if (request.getParameter("subset").equalsIgnoreCase("submitted"))
                {
                    targetStatus.add(ApplicationStatus.SUBMITTED);
                    targetStatus.add(ApplicationStatus.UNDER_REVIEW);
                    targetStatus.add(ApplicationStatus.SUBMITTED_FOR_REVIEW);
                }
                if (request.getParameter("subset").equalsIgnoreCase("underReview"))
                {
                    targetStatus.add(ApplicationStatus.UNDER_REVIEW);
                    targetStatus.add(ApplicationStatus.SUBMITTED_FOR_REVIEW);
                }
                if (request.getParameter("subset").equalsIgnoreCase("accepted"))
                {
                    targetStatus.add(ApplicationStatus.APPROVED);
                }
                if (request.getParameter("subset").equalsIgnoreCase("rejected"))
                {
                    targetStatus.add(ApplicationStatus.REJECTED);
                }
                if (request.getParameter("subset").equalsIgnoreCase("corrections"))
                {
                    targetStatus.add(ApplicationStatus.SENT_FOR_CORRECTIONS);
                }
            }
            
            for(int i = 0; i < apps.size(); i++){
                if (targetStatus.contains(apps.get(i).getStatus()))
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
        if (app.getStatus() == ApplicationStatus.UNDER_REVIEW || app.getStatus() == ApplicationStatus.SUBMITTED_FOR_REVIEW)
        {
            return "<span style=\"margin-left:30px;\" class=\"label label-info\">Under Review</span>";
        }
        if (app.getStatus() == ApplicationStatus.SENT_FOR_CORRECTIONS)
        {
            return "<span style=\"margin-left:30px;\" class=\"label label-warning\">Needs Corrections</span>";
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
