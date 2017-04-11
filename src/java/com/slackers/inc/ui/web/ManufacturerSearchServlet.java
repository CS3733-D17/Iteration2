/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.ui.web;

import com.slackers.inc.Controllers.Filters.*;
import com.slackers.inc.Controllers.SearchController;
import com.slackers.inc.database.entities.Label;
import com.slackers.inc.database.entities.Label.BeverageSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Enumeration;
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
        response.setContentType("text/html;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {
            IPageFrame pg = WebComponentProvider.getCorrectFrame(request, "searchPage");
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
        response.setContentType("text/html;charset=UTF-8");
        
        SearchController search = new SearchController();
        Label label = new Label();
        search.reset();
        Map<String, String[]> param = request.getParameterMap();
        for (String parameter: param.keySet()){
            switch(parameter){
                case "keywords":
                    if(!(request.getParameter("keywords").equals(""))){
                        Filter brand = new BrandNameFilter(request.getParameter("keywords"));
                        search.addFilter(brand);
                    }
                    break;
                case "alcoholContent":
                    if(!(request.getParameter("alcoholContent").equals(""))){
                        Filter alcoholContent = new AlcoholFilter(Double.parseDouble(request.getParameter("alcoholContent")));
                        search.addFilter(alcoholContent);
                    }
                    break;
                case "originLocation": //Dont have a filter for origin location
//                    if(!(request.getParameter("originLocation").equals(""))){
//                        Filter alcoholContent = new AlcoholFilter(Integer.parseInt(request.getParameter("originLocation")));
//                        search.addFilter(alcoholContent);
//                    }
                    break;
                case "source":
                    if(!(request.getParameter("source").equals("na"))){
                        Filter source;
                        switch(request.getParameter("source")){
                            case "Domestic":
                                source = new ProductSourceFilter(Label.BeverageSource.DOMESTIC);
                                break;
                            case "Imported":
                                source = new ProductSourceFilter(Label.BeverageSource.IMPORTED);
                                break;
                    }
                }
                    break;
                case "pHLevel":
                    break;
                case "vintageYear":
                    break;
                
            }
        }    
        
        System.out.println(label);
        try (PrintWriter out = response.getWriter()) {  
            
            List<Label> drinkList = search.runSearch(label);
            
            System.out.println("LIST");
            for (Label l : drinkList)
            {
                System.out.println(l);
            }
            
            IPageFrame pg = WebComponentProvider.getCorrectFrame(request, "results");
            String results = WebComponentProvider.loadPartialPage(this, "Results-partial.html");
            
            StringBuilder b = new StringBuilder();
            for(int i = 0; i < drinkList.size(); i++){
                b.append("<div class=\"panel panel-default\">\n" +
"                           <div class=\"panel-heading\">\n" +
"                               <div class=\"row\">\n" +
"                                   <div class=\"col-md-10\">\n" +
"                                       <a data-toggle=\"collapse\" data-parent=\"#applicationAccordion\" href=\"#collapse" + i + "\" style=\"font-size: 20px;\">" + drinkList.get(i).getBrandName() + "</a>\n" +
"                                   </div>\n" +
"                                   <div class=\"col-md-1 pull-right\">\n" +
"                                       <button class='btn btn-primary btn-block'>Edit</button>\n" +
"                                   </div>\n" +
"                               </div>\n" +
"                           </div>\n" +
"                       <div id=\"collapse"+ i + "\" class=\"panel-collapse collapse in\">\n" +
"                           <div class=\"panel-body\">\n"+
                        renderData(request,drinkList.get(i)) +
                                "\n</div>\n" +
"                           </div>\n" +
"                       </div>");
                
            }
            
            results = results.replace("##Drinks", b);
            results = results.replace("##BrandName", drinkList.get(0).getBrandName() );
            pg.setBody(results);
            out.println(WebComponentProvider.buildPage(pg, request));
            
            
        } catch (SQLException ex) {
            Logger.getLogger(ManufacturerSearchServlet.class.getName()).log(Level.SEVERE, null, ex);
        }    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
    
    public static String renderData(HttpServletRequest request, Label label)
    {
        StringBuilder b = new StringBuilder();
        b.append("<div class = \"row\">\n" +
"		<div class=\"col-md-2\">\n" +
"			<label>Representative ID:</label>\n" +
"		</div>\n" +
"		\n" +
"		<div class=\"col-md-2\">\n" +
"			<p>"+label.getRepresentativeIdNumber()+"</p>\n" +
"		</div>\n" +
"\n" +
"\n" +
"		<div class=\"col-md-2\">\n" +
"			<label>Application Progress:</label>\n" +
"		</div>\n" +
"		\n" +
"		<div class=\"col-md-2\">\n" +
"			<p>Representative ID</p>\n" +
"		</div>\n" +
"	</div>\n" +
"	<br>\n" +
"	<div class = \"row\">\n" +
"		<div class=\"col-md-2\">\n" +
"			<label>Brand Name:</label>\n" +
"		</div>\n" +
"		\n" +
"		<div class=\"col-md-2\">\n" +
"			<p>"+label.getBrandName()+"</p>\n" +
"		</div>\n" +
"\n" +
"\n" +
"		<div class=\"col-md-2\">\n" +
"			<label>Name of Agent:</label>\n" +
"		</div>\n" +
"		\n" +
"		<div class=\"col-md-2\">\n" +
"			<p>"+label.getApproval().getAgent().getFirstName() + " " + label.getApproval().getAgent().getLastName() +"</p>\n" +
"		</div>\n" +
"	</div>\n" +
"\n" +
"	<br>\n" +
"	<div class = \"row\">\n" +
"		<div class=\"col-md-2\">\n" +
"			<label>Type:</label>\n" +
"		</div>\n" +
"		\n" +
"		<div class=\"col-md-2\">\n" +
"			<p>"+label.getProductType()+"</p>\n" +
"		</div>\n" +
"\n" +
"\n" +
"		<div class=\"col-md-2\">\n" +
"			<label>Date of Approval:</label>\n" +
"		</div>\n" +
"		\n" +
"		<div class=\"col-md-2\">\n" +
"			<p>"+label.getApproval().getApprovalDate()+"</p>\n" +
"		</div>\n" +
"	</div>\n" +
"\n" +
"	<br>\n" +
"	<div class = \"row\">\n" +
"		<div class=\"col-md-2\">\n" +
"			<label>Alcohol Content:</label>\n" +
"		</div>\n" +
"		\n" +
"		<div class=\"col-md-2\">\n" +
"			<p>"+Double.toString(label.getAlcoholContent())+"</p>\n" +
"		</div>\n" +
"\n" +
"\n" +
"		<div class=\"col-md-2\">\n" +
"			<label>Expiration:</label>\n" +
"		</div>\n" +
"		\n" +
"		<div class=\"col-md-2\">\n" +
"			<p>"+label.getApproval().getExperationDate()+"</p>\n" +
"		</div>\n" +
"	</div>\n" +
"\n" +
"	<br>\n" +
"	<div class = \"row\">\n" +
"		<div class=\"col-md-2\">\n" +
"			<label>Source of Product:</label>\n" +
"		</div>\n" +
"		\n" +
"		<div class=\"col-md-2\">\n" +
"			<p>"+label.getProductSource()+"</p>\n" +
"		</div>\n" +
"\n" +
"\n" +
"		<div class=\"col-md-2\">\n" +
"			<label>Other Stuff:</label>\n" +
"		</div>\n" +
"		\n" +
"		<div class=\"col-md-2\">\n" +
"			<p>Representative ID</p>\n" +
"		</div>\n" +
"	</div>");
       
       
        return b.toString();
    }

}
