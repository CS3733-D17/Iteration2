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
import com.slackers.inc.Controllers.LabelRenderer;
import com.slackers.inc.Controllers.SearchController;
import com.slackers.inc.database.DerbyConnection;
import com.slackers.inc.database.entities.BeerLabel;
import com.slackers.inc.database.entities.DistilledLabel;
import com.slackers.inc.database.entities.Label;
import com.slackers.inc.database.entities.WineLabel;
import com.slackers.inc.ui.web.form.LabelImageGenerator;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        doPost(request, response);
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
        search.setPage(0);
        if (request.getParameter("pg") != null) {
            int page = 0;
            try {
                page = Integer.parseInt(request.getParameter("pg"));
            } catch (Exception e) {
            }
            if (page < 0) {
                page = 0;
            }
            search.setPage(page);
        }

        Label label = new Label();
        search.reset();

        List<List<Filter>> combined = new LinkedList<>();
        if (request.getParameter("keywordType") != null && request.getParameter("keywordType").equals("BRAND")) {
            String brand = request.getParameter("keywords") != null ? request.getParameter("keywords") : "";
            List<Filter> filters = new LinkedList<>();
            filters.add(new BrandNameRange(brand));
            filters.add(new AcceptedFilter(true));
            combined.add(filters);
        } else if (request.getParameter("keywordType") != null && request.getParameter("keywordType").equals("FANCIFUL")) {
            String fancy = request.getParameter("keywords") != null ? request.getParameter("keywords") : "";
            List<Filter> filters = new LinkedList<>();
            filters.add(new FancifulNameRange(fancy));
            filters.add(new AcceptedFilter(true));
            combined.add(filters);
        } else if (request.getParameter("keywordType") != null && request.getParameter("keywordType").equals("BOTH")) {
            String brand = request.getParameter("keywords") != null ? request.getParameter("keywords") : "";
            List<Filter> filters = new LinkedList<>();
            filters.add(new BrandNameRange(brand));
            filters.add(new AcceptedFilter(true));
            combined.add(filters);
            String fancy = request.getParameter("keywords") != null ? request.getParameter("keywords") : "";
            filters = new LinkedList<>();
            filters.add(new FancifulNameRange(fancy));
            filters.add(new AcceptedFilter(true));
            combined.add(filters);
        } else {
            String brand = request.getParameter("keywords") != null ? request.getParameter("keywords") : "";
            List<Filter> filters = new LinkedList<>();
            filters.add(new BrandNameRange(brand));
            filters.add(new AcceptedFilter(true));
            combined.add(filters);
        }
        Map<String, String[]> param = request.getParameterMap();
        Filter filter;
        for (List<Filter> f : combined) {
            for (String parameter : param.keySet()) {
                switch (parameter) {

                    // This produces all, but is there some empty ones?
                    /*case "keywords": 
                    //if (!(request.getParameter("keywords").equals(""))) {
                        filter = new BrandNameRange(request.getParameter("keywords"));
                        f.add(filter);
                    //}
                    break;*/
                    case "TTB_CT-new":
                        if (!(request.getParameter("TTB_CT-new").equals(""))) {
                            filter = new CTFilter(request.getParameter("TTB_CT-new"));
                            f.add(filter);
                        }
                        break;
                    case "TTB_OR-new":
                        if (!(request.getParameter("TTB_OR-new").equals(""))) {
                            filter = new ORFilter(request.getParameter("TTB_OR-new"));
                            f.add(filter);
                        }
                        break;
                    case "alcoholSearchType":
                        if (request.getParameter("alcoholSearchType").equals("between")) {
                            if (!(request.getParameter("alcohol_low").equals("")) && !(request.getParameter("alcohol_hi").equals(""))) {
                                double lo = Double.parseDouble(request.getParameter("alcohol_low"));
                                double hi = Double.parseDouble(request.getParameter("alcohol_hi"));

                                filter = new AlcoholRange(lo, hi);
                                f.add(filter);
                            }
                        } else if (!(request.getParameter("alcohol_low") == null || request.getParameter("alcohol_low").equals(""))) {
                            filter = new AlcoholFilter(Double.parseDouble(request.getParameter("alcohol_low")));

                            f.add(filter);

                        }
                        break;

                    case "source":
                        if (!(request.getParameter("source").equals("na"))) {
                            ExactFilter source;

                            switch (request.getParameter("source")) {
                                case "Domestic":
                                    source = new ProductSourceFilter(Label.BeverageSource.DOMESTIC.name());

                                    f.add(source);
                                    break;
                                case "Imported":
                                    source = new ProductSourceFilter(Label.BeverageSource.IMPORTED.name());

                                    f.add(source);
                                    break;
                            }
                        }
                        break;

                    case "date":
                        if (request.getParameter("date").equals("between")) {
                            if (!(request.getParameter("date_low") != null) && !(request.getParameter("date_hi") != null)) {
                                String lo = request.getParameter("date_low");
                                String hi = request.getParameter("date_hi");
                                SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
                                //java.util.Date date = sdf1.parse(startDate);
                                //java.sql.Date sqlStartDate = new java.sql.Date(date.getTime());  

                                java.util.Date low = null;
                                java.sql.Date loDate = null;
                                java.util.Date high = null;
                                java.sql.Date hiDate = null;
                                try {
                                    low = ft.parse(lo);
                                    loDate = new java.sql.Date(low.getTime());
                                    high = ft.parse(hi);
                                    hiDate = new java.sql.Date(high.getTime());
                                    f.add(new DateRange(loDate, hiDate));
                                } catch (Exception ex) {
                                }
                            }
                        } else if (!(request.getParameter("date_low") == null || request.getParameter("date_low").equals(""))) {
                            SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
                            String lo = request.getParameter("date_low");
                            //System.out.println(lo);
                            java.util.Date low = null;
                            java.sql.Date loDate = null;
                            try {
                                low = ft.parse(lo);
                                loDate = new java.sql.Date(low.getTime());
                                f.add(new DateFilter(loDate));
                            } catch (ParseException ex) {
                                System.out.println("low dont work");
                                Logger.getLogger(ManufacturerSearchServlet.class.getName()).log(Level.SEVERE, null, ex);
                            }

                        }

                        break;

                    case "type":
                        if (!(request.getParameter("type").equals("ALL"))) {
                            ExactFilter source;
                            switch (request.getParameter("type")) {
                                case "WINE":
                                    label = new WineLabel();
                                    source = new TypeFilter(Label.BeverageType.WINE.name());
                                    f.add(source);
                                    break;
                                case "BEER":
                                    label = new BeerLabel();
                                    source = new TypeFilter(Label.BeverageType.BEER.name());
                                    f.add(source);
                                    break;
                                case "DISTILLED":
                                    label = new DistilledLabel();
                                    source = new TypeFilter(Label.BeverageType.DISTILLED.name());
                                    f.add(source);
                                    break;
                            }
                        }
                        break;

                    case "ph":
                        if (request.getParameter("ph").equals("between")) {
                            if (!(request.getParameter("ph_low").equals("")) && !(request.getParameter("ph_hi").equals(""))) {
                                double lo = Double.parseDouble(request.getParameter("ph_low"));
                                double hi = Double.parseDouble(request.getParameter("ph_hi"));
                                f.add(new PHRange(lo, hi));
                            }
                        } else if (!(request.getParameter("ph_low") == null || request.getParameter("ph_low").equals(""))) {

                            f.add(new PHFilter(Double.parseDouble(request.getParameter("ph_low"))));
                        }
                        break;

                    case "vintage":
                        if (request.getParameter("vintage").equals("between")) {
                            if (!(request.getParameter("vintage_low").equals("")) && !(request.getParameter("vintage_hi").equals(""))) {
                                int lo = Integer.parseInt(request.getParameter("vintage_low"));
                                int hi = Integer.parseInt(request.getParameter("vintage_hi"));
                                f.add(new VintageRange(lo, hi));
                            }
                        } else if (!(request.getParameter("vintage_low") == null || request.getParameter("vintage_low").equals(""))) {
                            f.add(new VintageFilter(Integer.parseInt(request.getParameter("vintage_low"))));
                        }
                        break;
                    case "origin":
                        if (request.getParameter("origin").equals("between")) {
                            if (!(request.getParameter("origin_low").equals("")) && !(request.getParameter("origin_hi").equals(""))) {
                                String lo = request.getParameter("origin_low");
                                String hi = request.getParameter("origin_hi");
                                f.add(new OriginRange(lo, hi));
                            }
                        } else if (!(request.getParameter("origin_low") == null || request.getParameter("origin_low").equals(""))) {
                            f.add(new OriginFilter(request.getParameter("origin_low")));
                        }
                        break;
                    case "FILTER_IMAGES":
                        if (request.getParameter("FILTER_IMAGES") != null && request.getParameter("FILTER_IMAGES").equalsIgnoreCase("on")) {
                            f.add(new ValidImageFilter());
                        }
                        break;
                }
            }
        }

        //f.add(new AcceptedFilter(true));
        List<Label> drinkList;
        int resultCount = 0;
        long time = 0;
        try {
            long start = System.currentTimeMillis();

            drinkList = search.runSearch(label, combined, "labelImage");
            resultCount = search.runSearchCount(label, combined, "labelImage");
            long end = System.currentTimeMillis();
            time = end - start;
        } catch (SQLException ex) {
            ex.printStackTrace();
            DerbyConnection.getInstance().reset();
            response.sendRedirect("/SuperSlackers/search");
            return;
        }
        if (drinkList == null) {
            response.sendRedirect("/SuperSlackers/search");
            return;
        }

        if (request.getParameter("action") != null && request.getParameter("action").equals("download")) {
            IDelimiterFormat format = new CsvFormat();
            if (request.getParameter("Dtype") != null && request.getParameter("Dtype").equalsIgnoreCase("tsv")) {
                format = new TsvFormat();
            }
            if (request.getParameter("Dtype") != null && request.getParameter("Dtype").equalsIgnoreCase("delimiter") && request.getParameter("delimiter") != null) {
                format = new CharFormat(request.getParameter("delimiter"));
            }
            try {
                search.setPage(SearchController.PAGE_GET_ALL);
                drinkList = search.runSearch(label);
            } catch (SQLException ex) {
                ex.printStackTrace();
                return;
            }
            response.setContentType(format.getMimeType());
            try (OutputStream outStream = response.getOutputStream()) {
                DelimitedWriter out = new DelimitedWriter(outStream, format);

                out.init(com.slackers.inc.database.entities.Label.class);
                out.initSubtype(com.slackers.inc.database.entities.BeerLabel.class);
                out.initSubtype(com.slackers.inc.database.entities.WineLabel.class);
                out.initSubtype(com.slackers.inc.database.entities.DistilledLabel.class);

                out.addIgnoredGetMethod("getEntityNameTypePairs");
                out.addIgnoredGetMethod("getEntityValues");
                out.addIgnoredGetMethod("getUpdatableEntityValues");
                out.addIgnoredGetMethod("getApproval");
                out.addIgnoredGetMethod("getTableName");
                out.addIgnoredGetMethod("getLabelImage");
                out.addIgnoredGetMethod("getLabelImageType");

                out.writeColumnHeader();
                out.write(drinkList);
                out.flush();
            }
        } else {
            response.setContentType("text/html;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {

                IPageFrame pg = WebComponentProvider.getCorrectFrame(request, "Results");
                String results = WebComponentProvider.loadPartialPage(this, "Results-partial.html");

                StringBuilder b = new StringBuilder();
                for (int i = 0; i < drinkList.size(); i++) {
                    b.append(LabelRenderer.getInstance().renderLabel(this, drinkList.get(i)));
                    /*b.append("<div class=\"panel panel-default\">\n"
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
                            + "                       <div id=\"collapse" + i + "\" class=\"panel-collapse collapse\">\n"
                            + "                           <div class=\"panel-body\">\n"
                            + renderLabel(this, request, drinkList.get(i))
                            + "\n</div>\n"
                            + "                           </div>\n"
                            + "                       </div>");*/

                }

                results = results.replace("##Drinks", b);
                results = results.replace("##RESULT_STATS", "Found " + resultCount + " results in " + (time / 1000.0) + " seconds");

                results = results.replace("##PAGE", "Page " + (search.getPage() + 1));
                List<String> params = new LinkedList<>();
                for (String parameter : param.keySet()) {
                    if (request.getParameter(parameter) != null && request.getParameter(parameter).length() > 0) {
                        if (!parameter.equalsIgnoreCase("pg")) {
                            params.add(parameter + "=" + request.getParameter(parameter));
                        }
                    }
                }
                if (drinkList.isEmpty()) {
                    results = results.replace("##NEXT", "/SuperSlackers/search?" + String.join("&", params) + "&pg=" + (search.getPage()));
                } else {
                    results = results.replace("##NEXT", "/SuperSlackers/search?" + String.join("&", params) + "&pg=" + (search.getPage() + 1));
                }
                if (search.getPage() <= 1) {
                    results = results.replace("##PREV", "/SuperSlackers/search?" + String.join("&", params) + "&pg=" + (0));
                } else {
                    results = results.replace("##PREV", "/SuperSlackers/search?" + String.join("&", params) + "&pg=" + (search.getPage() - 1));
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
