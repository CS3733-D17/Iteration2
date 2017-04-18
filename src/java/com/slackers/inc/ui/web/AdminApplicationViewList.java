/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.ui.web;

import com.slackers.inc.Controllers.Filters.ExactFilter;
import com.slackers.inc.Controllers.Filters.Filter;
import com.slackers.inc.database.DerbyConnection;
import com.slackers.inc.database.entities.Label;
import com.slackers.inc.database.entities.LabelApplication;
import com.slackers.inc.database.entities.LabelApplication.ApplicationStatus;
import com.slackers.inc.ui.web.form.LabelImageGenerator;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Jason
 */
@WebServlet(name = "AdminApplicationsPageServlet", urlPatterns = {"/admin/glblApps"})
public class AdminApplicationViewList extends HttpServlet {

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
            String applications = WebComponentProvider.loadPartialPage(this, "adminList-partial.html");
            List<Filter> filters = new LinkedList<>();
            if (request.getParameter("subset")!=null)
            {            
                if (request.getParameter("subset").equalsIgnoreCase("waiting"))
                {
                    filters.add(new ExactFilter(){
                        @Override
                        public Object getValue() {
                            return LabelApplication.ApplicationStatus.SUBMITTED;
                        }

                        @Override
                        public String getColumn() {
                            return "status";
                        }
                    });
                }
                if (request.getParameter("subset").equalsIgnoreCase("under"))
                {
                    filters.add(new ExactFilter(){
                        @Override
                        public Object getValue() {
                            return LabelApplication.ApplicationStatus.UNDER_REVIEW;
                        }

                        @Override
                        public String getColumn() {
                            return "status";
                        }
                    });
                }
                if (request.getParameter("subset").equalsIgnoreCase("accepted"))
                {
                    filters.add(new ExactFilter(){
                        @Override
                        public Object getValue() {
                            return LabelApplication.ApplicationStatus.APPROVED;
                        }

                        @Override
                        public String getColumn() {
                            return "status";
                        }
                    });
                }
                if (request.getParameter("subset").equalsIgnoreCase("rejected"))
                {
                    filters.add(new ExactFilter(){
                        @Override
                        public Object getValue() {
                            return LabelApplication.ApplicationStatus.REJECTED;
                        }

                        @Override
                        public String getColumn() {
                            return "status";
                        }
                    });
                }
            }
            int numberOfResults = 10;
            int offset = 0;
            try
            {
                offset = Integer.parseInt(request.getParameter("offset"));
                if (offset<0)
                    offset = 0;
            } catch(Exception e){}
            
            List<LabelApplication> apps = DerbyConnection.getInstance().search(new LabelApplication(), filters, numberOfResults, offset);
            
            int i=0;
            StringBuilder b = new StringBuilder();
            for (LabelApplication a : apps)
            {
                b.append(this.buildApp(a, i++));
            }
            
            /*for(int i = 0; i < apps.size(); i++){
                if (targetStatus == ApplicationStatus.UNKNOWN || apps.get(i).getStatus()== targetStatus)
                {
                b.append("<div class=\"panel panel-default\">\n" +
"                           <div class=\"panel-heading\">\n" +
"                               <div class=\"row\">\n" +
"                                   <div class=\"col-md-10\">\n" +
"                                       <a data-toggle=\"collapse\" data-parent=\"#applicationAccordion\" href=\"#collapse" + i + "\" style=\"font-size: 20px;\">" + manufacturer.getApplications().get(i).getLabel().getBrandName() + "</a>\n" +
"                                   </div>\n" +
"                                   <div style=\"float:right;\">\n" +
"                                       <a href=\"/SuperSlackers/form?action=edit&id="+Long.toString(apps.get(i).getApplicationId())+"\"class='btn btn-primary btn-block'>Edit</a>\n" +
"                                   </div>\n" +
"                               </div>\n" +
"                           </div>\n" +
"                       <div id=\"collapse"+ i + "\" class=\"panel-collapse collapse in\">\n" +
"                           <div class=\"panel-body\">"+ManufacturerSearchServlet.renderLabel(this, request, apps.get(i).getLabel())+"</div>\n" +
"                           </div>\n" +
"                       </div>");
                }
            }*/
            
            applications = applications.replace("##Applications", b.toString());
            if (apps.isEmpty())
                applications = applications.replace("##NEXT", "/SuperSlackers/admin/glblApps?subset="+request.getParameter("subset")+"&offset="+(offset));
            else
                applications = applications.replace("##NEXT", "/SuperSlackers/admin/glblApps?subset="+request.getParameter("subset")+"&offset="+(offset+10));
            if (offset<10)
                applications = applications.replace("##PREV", "/SuperSlackers/admin/glblApps?subset="+request.getParameter("subset")+"&offset="+(0));
            else
                applications = applications.replace("##PREV", "/SuperSlackers/admin/glblApps?subset="+request.getParameter("subset")+"&offset="+(offset-10));
            pg.setBody(applications);
            out.println(WebComponentProvider.buildPage(pg, request));
            
           
        } catch (SQLException ex) {
            ex.printStackTrace();
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request,response);
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

    
    private String buildApp(LabelApplication app, int i)
    {
        StringBuilder b = new StringBuilder();
        b.append("<div class=\"panel panel-default\">\n" +
"                           <div class=\"panel-heading\">\n" +
"                               <div class=\"row\">\n" +
"                                   <div class=\"col-md-10\">\n" +
"                                       <a data-toggle=\"collapse\" data-parent=\"#applicationAccordion\" href=\"#collapse" + i + "\" style=\"font-size: 20px;\">" + app.getLabel().getBrandName() +
                                    (app.getLabel().getFancifulName()!=null&&app.getLabel().getFancifulName().length()>1 ? (" ("+app.getLabel().getFancifulName()+")") : "")
                                    + "</a>\n" +
"                                   </div>\n" +
"                                   <div class=\"col-md-2\">\n" +
"                                       <a href=\"/SuperSlackers/form?action=view&id="+Long.toString(app.getApplicationId())+"\"class='btn btn-primary btn-block'>View</a>\n" +
"                                   </div>\n" +
"                               </div>\n" +
"                           </div>\n" +
"                       <div id=\"collapse"+ i + "\" class=\"panel-collapse collapse\">\n" +
"                           <div class=\"panel-body\">"+this.renderData(app)+"</div>\n" +
"                           </div>\n" +
"                       </div>");
        return b.toString();
    }

    private String renderData(LabelApplication app) {
        Label label = app.getLabel();
        String lbl = WebComponentProvider.loadPartialPage(this, "admin-result-template.html");

        lbl = lbl.replace("##TYPE", label.getProductType().name());
        lbl = lbl.replace("##SOURCE", label.getProductSource().name());
        lbl = lbl.replace("##ALCOHOL", Double.toString(label.getAlcoholContent()) + "%");
        
        if (label.getApproval() != null && label.isIsAccepted()) {
            lbl = lbl.replace("##APR_DATE", label.getApproval().getApprovalDate().toString());
            lbl = lbl.replace("##EXP_DATE", label.getApproval().getExperationDate().toString());
            lbl = lbl.replace("##APR_AGENT", label.getApproval().getAgent().getFirstName()+" "+label.getApproval().getAgent().getLastName());
        } else {
            if (app.getStatus()==ApplicationStatus.REJECTED)
            {
                lbl = lbl.replace("##APR_DATE", "Rejected");
                lbl = lbl.replace("##EXP_DATE", "Rejected");
                lbl = lbl.replace("##APR_AGENT", "Rejected");
            }
            else
            {
                lbl = lbl.replace("##APR_DATE", "Not approved yet");
                lbl = lbl.replace("##EXP_DATE", "Not approved yet");
                lbl = lbl.replace("##APR_AGENT", "Not approved yet");
            }
        }
        lbl = lbl.replace("##IMGPATH", LabelImageGenerator.getAccessStringForApplication(label));
        
        if (app.getReviewer().getEmail()==null || app.getReviewer().getEmail().equalsIgnoreCase("unknown"))
        {
            lbl = lbl.replace("##AGENT", "Not yet assigned");
        }
        else
        {
            lbl = lbl.replace("##AGENT", app.getReviewer().getEmail());
        }
        lbl = lbl.replace("##STATUS", app.getStatus().name());
        return lbl;
    }
}
