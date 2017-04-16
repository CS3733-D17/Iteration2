/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.ui.web;

import com.slackers.inc.Controllers.Csv.CsvFormat;
import com.slackers.inc.Controllers.Csv.TsvFormat;
import com.slackers.inc.Controllers.Csv.CharFormat;
import com.slackers.inc.Controllers.Csv.DelimitedWriter;
import com.slackers.inc.Controllers.Csv.IDelimiterFormat;
import com.slackers.inc.Controllers.Filters.*;
import com.slackers.inc.Controllers.SearchController;
import com.slackers.inc.database.entities.BeerLabel;
import com.slackers.inc.database.entities.DistilledLabel;
import com.slackers.inc.database.entities.Label;
import com.slackers.inc.database.entities.WineLabel;
import com.slackers.inc.ui.web.form.LabelImageGenerator;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Jason
 */
@WebServlet(name = "ManufacturerSearchServlet", urlPatterns = {"/search"})
public class ManufacturerSearchServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

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
            IPageFrame pg = WebComponentProvider.getCorrectFrame(request, "Search");
            pg.setBody(WebComponentProvider.loadPartialPage(this, "search-partial.html"));
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

        SearchController search = new SearchController();
        Label label = new Label();
        search.reset();
        Map<String, String[]> param = request.getParameterMap();
        for (String parameter : param.keySet()) {
            switch (parameter) {
                case "keywords":
                    if (!(request.getParameter("keywords").equals(""))) {
                        ExactFilter_old brand = new BrandNameFilter(request.getParameter("keywords"));
                        search.addFilter(brand);
                    }
                    break;
                case "alcoholContent":
                    if (!(request.getParameter("alcoholContent").equals(""))) {
                        AlcoholFilter alcoholContent = new AlcoholFilter(Double.parseDouble(request.getParameter("alcoholContent")));
                        System.out.println("Give me alcohol " + Double.parseDouble(request.getParameter("alcoholContent")));
                        search.addFilter(alcoholContent);
                    }
                    break;
//                case "type":
//                    if(!(request.getParameter("type") == null)){
//                        Filter type;
//                        switch(request.getParameter("type")){
//                            case "Beer":
//                                type = new TypeFilter(Label.BeverageType.BEER);
//                                search.addFilter(type);
//                                break;
//                            case "Wine":
//                                type = new TypeFilter(Label.BeverageType.WINE);
//                                search.addFilter(type);
//                                break;
//                            case "Distilled":
//                                type = new TypeFilter(Label.BeverageType.DISTILLED);
//                                search.addFilter(type);
//                                break;
//                        }
//                    }
//                    break;
                case "originLocation": //Dont have a filter for origin location
//                    if(!(request.getParameter("originLocation").equals(""))){
//                        Filter alcoholContent = new AlcoholFilter(Integer.parseInt(request.getParameter("originLocation")));
//                        search.addFilter(alcoholContent);
//                    }
                    break;
                case "source":
                    if (!(request.getParameter("source").equals("na"))) {
                        ExactFilter_old source;
                        
                        switch (request.getParameter("source")) {
                            case "Domestic":
                                source = new ProductSourceFilter(Label.BeverageSource.DOMESTIC);
                                System.out.println("dom");
                                search.addFilter(source);
                                break;
                            case "Imported":
                                source = new ProductSourceFilter(Label.BeverageSource.IMPORTED);
                                System.out.println("imp");
                                search.addFilter(source);
                                break;
                        }
                    }
                    break;
                case "type":
                    if (!(request.getParameter("type").equals("ALL"))) {
                        ExactFilter_old source;                        
                        switch (request.getParameter("type")) {
                            case "WINE":
                                label = new WineLabel();
                                source = new TypeFilter(Label.BeverageType.WINE);
                                search.addFilter(source);
                                break;
                            case "BEER":
                                label = new BeerLabel();
                                source = new TypeFilter(Label.BeverageType.BEER);
                                search.addFilter(source);
                                break;
                            case "DISTILLED":
                                label = new DistilledLabel();
                                source = new TypeFilter(Label.BeverageType.DISTILLED);
                                search.addFilter(source);
                                break;
                        }
                    }
                    break;
                case "phLevel":
                    if (!(request.getParameter("alcoholContent").equals(""))) {
                        search.addFilter(new PHFilter(Double.parseDouble(request.getParameter("phLevel"))));
                    }
                    break;
                case "vintageYear":
                    if (!(request.getParameter("vintageYear").equals(""))) {
                        search.addFilter(new VintageFilter(Integer.parseInt(request.getParameter("vintageYear"))));
                    }
                    break;

            }
        }

        search.addFilter(new AcceptedFilter(true));
        List<Label> drinkList;
        try {
            drinkList = search.runSearch(label);
            for (Label l : drinkList)
            {
                System.out.println(l);
            }
        } catch (SQLException ex) {
            response.sendRedirect("/SuperSlackers/search");
            return;
        }
        if (drinkList==null)
        {
            response.sendRedirect("/SuperSlackers/search");
            return;
        }

        if (request.getParameter("action") != null && request.getParameter("action").equals("download")) {
            IDelimiterFormat format = new CsvFormat(); 
            if (request.getParameter("type")!=null && request.getParameter("type").equalsIgnoreCase("tsv"))
            {
                format = new TsvFormat();
            }
            if (request.getParameter("type")!=null && request.getParameter("type").equalsIgnoreCase("delimiter") && request.getParameter("delimiter")!=null)
            {
                format = new CharFormat(request.getParameter("delimiter"));
            }
            
            response.setContentType(format.getMimeType());
            try (OutputStream outStream = response.getOutputStream()) {
                DelimitedWriter out = new DelimitedWriter(outStream,format);
        
                out.init(com.slackers.inc.database.entities.Label.class);
                out.initSubtype(com.slackers.inc.database.entities.BeerLabel.class);
                out.initSubtype(com.slackers.inc.database.entities.WineLabel.class);
                out.initSubtype(com.slackers.inc.database.entities.DistilledLabel.class);

                out.addIgnoredGetMethod("getEntityNameTypePairs");
                out.addIgnoredGetMethod("getEntityValues");
                out.addIgnoredGetMethod("getUpdatableEntityValues");
                out.addIgnoredGetMethod("getApproval");
                out.addIgnoredGetMethod("getTableName");

                out.writeColumnHeader();
                out.write(drinkList);
                out.flush();
            }
        }
        else
        {
            response.setContentType("text/html;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {

                IPageFrame pg = WebComponentProvider.getCorrectFrame(request, "Results");
                String results = WebComponentProvider.loadPartialPage(this, "Results-partial.html");

                StringBuilder b = new StringBuilder();
                for (int i = 0; i < drinkList.size(); i++) {
                    b.append("<div class=\"panel panel-default\">\n"
                            + "                           <div class=\"panel-heading\">\n"
                            + "                               <div class=\"row\">\n"
                            + "                                   <div class=\"col-md-10\">\n"
                            + "                                       <a data-toggle=\"collapse\" data-parent=\"#applicationAccordion\" href=\"#collapse" + i + "\" style=\"font-size: 20px;\">" + drinkList.get(i).getBrandName() + "</a>\n"
                            + "                                   </div>\n"
                            + "                                   <div class=\"col-md-1 pull-right\">\n"
                            + //"                                       <button class='btn btn-primary btn-block'>Edit</button>\n" +
                            "                                   </div>\n"
                            + "                               </div>\n"
                            + "                           </div>\n"
                            + "                       <div id=\"collapse" + i + "\" class=\"panel-collapse collapse in\">\n"
                            + "                           <div class=\"panel-body\">\n"
                            + renderLabel(this, request, drinkList.get(i))
                            + "\n</div>\n"
                            + "                           </div>\n"
                            + "                       </div>");

                }

                results = results.replace("##Drinks", b);
                //results = results.replace("##BrandName", drinkList.get(0).getBrandName() );

                List<String> params = new LinkedList<>();
                for (String parameter : param.keySet()) {
                    if (request.getParameter(parameter) != null && request.getParameter(parameter).length() > 0) {
                        params.add(parameter + "=" + request.getParameter(parameter));
                    }
                }
                if (!params.isEmpty()) {
                    params.add("action=download");
                    String downloadUrl = request.getRequestURL().append('?').append(String.join("&", params)).toString();
                    results = results.replace("##SEARCH_URL", downloadUrl);
                } else {
                    results = results.replace("##SEARCH_URL", "/SuperSlackers/search");
                }
                pg.setBody(results);
                out.println(WebComponentProvider.buildPage(pg, request));
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

    public static String renderLabel(HttpServlet servlet, HttpServletRequest request, Label label) {
        String lbl = WebComponentProvider.loadPartialPage(servlet, "label-result-template.html");

        lbl = lbl.replace("##TYPE", label.getProductType().name());
        lbl = lbl.replace("##SOURCE", label.getProductSource().name());
        lbl = lbl.replace("##ALCOHOL", Double.toString(label.getAlcoholContent()) + "%");
        if (label.getApproval() != null && label.isIsAccepted()) {
            lbl = lbl.replace("##APR_DATE", label.getApproval().getApprovalDate().toString());
            lbl = lbl.replace("##EXP_DATE", label.getApproval().getExperationDate().toString());
        } else {
            lbl = lbl.replace("##APR_DATE", "Not approved yet");
            lbl = lbl.replace("##EXP_DATE", "Not approved yet");
        }
        lbl = lbl.replace("##IMGPATH", LabelImageGenerator.getAccessStringForApplication(label));
        return lbl;
    }

    public static String renderData(HttpServletRequest request, Label label) {
        StringBuilder b = new StringBuilder();
        b.append("<div class = \"row\">\n"
                + "		<div class=\"col-md-2\">\n"
                + "			<label>Representative ID:</label>\n"
                + "		</div>\n"
                + "		\n"
                + "		<div class=\"col-md-2\">\n"
                + "			<p>" + label.getRepresentativeIdNumber() + "</p>\n"
                + "		</div>\n"
                + "\n"
                + "\n"
                + "		<div class=\"col-md-2\">\n"
                + "			<label>Application Progress:</label>\n"
                + "		</div>\n"
                + "		\n"
                + "		<div class=\"col-md-2\">\n"
                + "			<p>Representative ID</p>\n"
                + "		</div>\n"
                + "	</div>\n"
                + "	<br>\n"
                + "	<div class = \"row\">\n"
                + "		<div class=\"col-md-2\">\n"
                + "			<label>Brand Name:</label>\n"
                + "		</div>\n"
                + "		\n"
                + "		<div class=\"col-md-2\">\n"
                + "			<p>" + label.getBrandName() + "</p>\n"
                + "		</div>\n"
                + "\n"
                + "\n"
                + "		<div class=\"col-md-2\">\n"
                + "			<label>Name of Agent:</label>\n"
                + "		</div>\n"
                + "		\n"
                + "		<div class=\"col-md-2\">\n"
                + "			<p>" + label.getApproval().getAgent().getFirstName() + " " + label.getApproval().getAgent().getLastName() + "</p>\n"
                + "		</div>\n"
                + "	</div>\n"
                + "\n"
                + "	<br>\n"
                + "	<div class = \"row\">\n"
                + "		<div class=\"col-md-2\">\n"
                + "			<label>Type:</label>\n"
                + "		</div>\n"
                + "		\n"
                + "		<div class=\"col-md-2\">\n"
                + "			<p>" + label.getProductType() + "</p>\n"
                + "		</div>\n"
                + "\n"
                + "\n"
                + "		<div class=\"col-md-2\">\n"
                + "			<label>Date of Approval:</label>\n"
                + "		</div>\n"
                + "		\n"
                + "		<div class=\"col-md-2\">\n"
                + "			<p>" + label.getApproval().getApprovalDate() + "</p>\n"
                + "		</div>\n"
                + "	</div>\n"
                + "\n"
                + "	<br>\n"
                + "	<div class = \"row\">\n"
                + "		<div class=\"col-md-2\">\n"
                + "			<label>Alcohol Content:</label>\n"
                + "		</div>\n"
                + "		\n"
                + "		<div class=\"col-md-2\">\n"
                + "			<p>" + Double.toString(label.getAlcoholContent()) + "</p>\n"
                + "		</div>\n"
                + "\n"
                + "\n"
                + "		<div class=\"col-md-2\">\n"
                + "			<label>Expiration:</label>\n"
                + "		</div>\n"
                + "		\n"
                + "		<div class=\"col-md-2\">\n"
                + "			<p>" + label.getApproval().getExperationDate() + "</p>\n"
                + "		</div>\n"
                + "	</div>\n"
                + "\n"
                + "	<br>\n"
                + "	<div class = \"row\">\n"
                + "		<div class=\"col-md-2\">\n"
                + "			<label>Source of Product:</label>\n"
                + "		</div>\n"
                + "		\n"
                + "		<div class=\"col-md-2\">\n"
                + "			<p>" + label.getProductSource() + "</p>\n"
                + "		</div>\n"
                + "\n"
                + "\n"
                + "		<div class=\"col-md-2\">\n"
                + "			<label>Other Stuff:</label>\n"
                + "		</div>\n"
                + "		\n"
                + "		<div class=\"col-md-2\">\n"
                + "			<p>Representative ID</p>\n"
                + "		</div>\n"
                + "	</div>");

        return b.toString();
    }

}
