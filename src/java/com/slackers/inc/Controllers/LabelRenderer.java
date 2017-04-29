/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.Controllers;

import com.slackers.inc.Lists.CT;
import com.slackers.inc.Lists.CTList;
import com.slackers.inc.Lists.Origin;
import com.slackers.inc.Lists.OriginList;
import com.slackers.inc.database.entities.Label;
import com.slackers.inc.database.entities.User;
import static com.slackers.inc.ui.web.ManufacturerSearchServlet.renderLabel;
import com.slackers.inc.ui.web.WebComponentProvider;
import com.slackers.inc.ui.web.form.LabelImageGenerator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServlet;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
public class LabelRenderer {

    private LabelRenderer() {
    }

    public String renderLabel(HttpServlet servlet, Label label) {
        String randId = UUID.randomUUID().toString().replace("_", "");
        String fancy="";
        String button="";
        if (label.getFancifulName()!=null && label.getFancifulName().length()>0)
        {
            fancy = " ("+label.getFancifulName()+")";
        }
        if (label.isIsAccepted() && label.getApproval().getApplication() != null)
        {
            
            button = "<a style=\"width:100%;\" href=\"/SuperSlackers/form/view?id="+Long.toString(label.getApproval().getApplication().getApplicationId())+"\" class=\"btn btn-primary\">View</a>";
        }
        String out = "<div class=\"panel panel-default\">\n"
                + "                           <div class=\"panel-heading\">\n"
                + "                               <div class=\"row\">\n"
                + "                                   <div class=\"col-md-10\">\n"
                + "                                       <a data-toggle=\"collapse\" data-parent=\"#applicationAccordion\" href=\"#collapse" + randId + "\" style=\"font-size: 20px;\">" + label.getBrandName()+ fancy + "</a>\n"
                + "                                   </div>\n"
                + "                                   <div class=\"col-md-2\">\n"
                + button +
                "                                   </div>\n"
                + "                               </div>\n"
                + "                           </div>\n"
                + "                       <div id=\"collapse" + randId + "\" class=\"panel-collapse collapse\">\n"
                + "                           <div class=\"panel-body\">\n"
                + this.renderLabelContent(servlet, label).replace("##ADDITIONAL", "")
                + "\n</div>\n"
                + "                           </div>\n"
                + "                       </div>";
        return out;
    }

    public String renderLabelContent(HttpServlet servlet, Label label) {
        String lbl = WebComponentProvider.loadPartialPage(servlet, "label-result-template.html");

        lbl = lbl.replace("##TYPE", label.getProductType().name());
        lbl = lbl.replace("##SOURCE", label.getProductSource().name());
        if (label.getAlcoholContent()==-1)
        {
            lbl = lbl.replace("##ALCOHOL", "Not provided");
        }
        else
        {
            lbl = lbl.replace("##ALCOHOL", Double.toString(label.getAlcoholContent()) + "%");
        }
        String ct = "";
        List<CT> cts = CTList.getInstance().getList().stream().filter((e) -> e.getCT().equals(label.getTTB_CT())).collect(Collectors.toList());
        if (!cts.isEmpty() && cts.size() == 1) {
            ct = " (" + cts.get(0).getDescription() + ")";
        }
        String or = "";
        List<Origin> ors = OriginList.getInstance().getList().stream().filter((e) -> e.getOC().equals(label.getTTB_OR())).collect(Collectors.toList());
        if (!ors.isEmpty() && ors.size() == 1) {
            or = " (" + ors.get(0).getDescription() + ")";
        }
        lbl = lbl.replace("##CT_CODE", label.getTTB_CT() + ct);
        lbl = lbl.replace("##OR_CODE", label.getTTB_OR() + or);
        if (label.getApproval() != null && label.isIsAccepted()) {
            lbl = lbl.replace("##APR_DATE", label.getApproval().getApprovalDate().toString());
            lbl = lbl.replace("##EXP_DATE", label.getApproval().getExperationDate().toString());
            lbl = lbl.replace("##APR_AGENT", userPrintout(label.getApproval().getAgent()));
        } else {
            lbl = lbl.replace("##APR_DATE", "Not approved yet");
            lbl = lbl.replace("##EXP_DATE", "Not approved yet");
            lbl = lbl.replace("##APR_AGENT", "Not approved yet");
        }
        lbl = lbl.replace("##IMGPATH", LabelImageGenerator.getAccessStringForApplication(label));
        return lbl;
    }
    private String userPrintout(User user)
    {
        return user.getEmail()+" ("+user.getFirstName()+" "+user.getLastName()+")";
    }

    public static LabelRenderer getInstance() {
        return LabelRendererHolder.INSTANCE;
    }

    private static class LabelRendererHolder {

        private static final LabelRenderer INSTANCE = new LabelRenderer();
    }
}
