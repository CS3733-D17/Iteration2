/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.Controllers;

import com.slackers.inc.database.entities.LabelApplication;
import com.slackers.inc.database.entities.UsEmployee;
import com.slackers.inc.database.entities.User;
import com.slackers.inc.database.entities.User.UserType;
import java.util.UUID;
import javax.servlet.http.HttpServlet;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
public class ApplicationRenderer {

    private ApplicationRenderer() {
    }

    public String renderApplication(HttpServlet servlet, LabelApplication application, User user) {
        String randId = UUID.randomUUID().toString().replace("_", "");
        String fancy="";
        String button="";
        if (application.getLabel().getFancifulName()!=null && application.getLabel().getFancifulName().length()>0)
        {
            fancy = " ("+application.getLabel().getFancifulName()+")";
        }
        if (true)
        {            
            button = "<a style=\"width:100%;\" href=\"/SuperSlackers/form/view?id="+Long.toString(application.getApplicationId())+"\" class=\"btn btn-primary\">View</a>";
        }
        String out = "<div class=\"panel panel-default\">\n"
                + "                           <div class=\"panel-heading\">\n"
                + "                               <div class=\"row\">\n"
                + "                                   <div class=\"col-md-10\">\n"
                + "                                       <a data-toggle=\"collapse\" data-parent=\"#applicationAccordion\" href=\"#collapse" + randId + "\" style=\"font-size: 20px;\">" + application.getLabel().getBrandName()+ fancy+ "</a>\n"+getStatusBadge(application, user)
                + "                                   </div>\n"
                + "                                   <div class=\"col-md-2\">\n"
                + button +
                "                                   </div>\n"
                + "                               </div>\n"
                + "                           </div>\n"
                + "                       <div id=\"collapse" + randId + "\" class=\"panel-collapse collapse\">\n"
                + "                           <div class=\"panel-body\">\n"
                + this.renderApplicationContent(servlet, application, user)
                + "\n</div>\n"
                + "                           </div>\n"
                + "                       </div>";
        return out;
    }

    private String getStatusBadge(LabelApplication app, User usr)
    {
        if (usr.getUserType() == UserType.UNKNOWN || usr.getUserType() == UserType.COLA_USER || usr.getUserType() == UserType.US_EMPLOYEE)
            return "";
        if (app.getStatus() == LabelApplication.ApplicationStatus.APPROVED)
        {
            return "<span style=\"margin-left:30px;\" class=\"label label-success\">Approved</span>";
        }
        if (app.getStatus() == LabelApplication.ApplicationStatus.REJECTED)
        {
            return "<span style=\"margin-left:30px;\" class=\"label label-danger\">Rejected</span>";
        }
        if (app.getStatus() == LabelApplication.ApplicationStatus.UNDER_REVIEW || app.getStatus() == LabelApplication.ApplicationStatus.SUBMITTED_FOR_REVIEW)
        {
            return "<span style=\"margin-left:30px;\" class=\"label label-info\">Under Review</span>";
        }
        if (app.getStatus() == LabelApplication.ApplicationStatus.SENT_FOR_CORRECTIONS)
        {
            return "<span style=\"margin-left:30px;\" class=\"label label-warning\">Needs Corrections</span>";
        }
        return "";
    }
    
    public String renderApplicationContent(HttpServlet servlet, LabelApplication application, User user)
    {
        String label = LabelRenderer.getInstance().renderLabelContent(servlet, application.getLabel());
        String additional = "";
        if (user.getUserType() == UserType.ADMIN)
        {
            additional = "<br><br>";
            additional = additional+this.buildLine("Applicant:", userPrintout(application.getApplicant()));
            if (application.getReviewer().getEmail().equals(UsEmployee.NULL_EMPLOYEE.getEmail()))
            {
                additional = additional+this.buildLine("Reviewer:", "Not yet assigned");
            }
            else
            {
                additional = additional+this.buildLine("Reviewer:", userPrintout(application.getApplicant()));
            }
            additional = additional+this.buildLine("Last Activity:", application.getApplicationDate().toString());
            
        }
        else if (user.getUserType() == UserType.US_EMPLOYEE)
        {
            additional = "<br><br>";
            additional = additional+this.buildLine("Applicant:", userPrintout(application.getApplicant()));
            additional = additional+this.buildLine("Last Activity:", application.getApplicationDate().toString());
        }
        else if (user.getUserType() == UserType.MANUFACTURER)
        {
            additional = "<br><br>";
            if (application.getReviewer().getEmail().equals(UsEmployee.NULL_EMPLOYEE.getEmail()))
            {
                additional = additional+this.buildLine("Reviewer:", "Not yet assigned");
            }
            else
            {
                additional = additional+this.buildLine("Reviewer:", userPrintout(application.getApplicant()));
            }
            additional = additional+this.buildLine("Last Activity:", application.getApplicationDate().toString());
        }
        label = label.replace("##ADDITIONAL", additional);
        return label;
    }
    private String userPrintout(User user)
    {
        return user.getEmail()+" ("+user.getFirstName()+" "+user.getLastName()+")";
    }
    private String buildLine(String title, String value)
    {
        return "<div class=\"row\">\n" +
"            <div class=\"col-xs-6\">\n" +
"                <label>"+title+"</label>\n" +
"            </div>\n" +
"            <div class=\"col-xs-6\">\n" +
"                "+value+"\n" +
"            </div>            \n" +
"        </div>";
    }

    public static ApplicationRenderer getInstance() {
        return LabelRendererHolder.INSTANCE;
    }

    private static class LabelRendererHolder {

        private static final ApplicationRenderer INSTANCE = new ApplicationRenderer();
    }
}
