/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.ui.web;

import com.slackers.inc.Controllers.SearchController;
import com.slackers.inc.database.entities.Label;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
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
        label.setBrandName(request.getParameter("keywords"));
        //label.setAlcoholContent(Double.parseDouble(request.getParameter("alcoholContent")));
        //label.setProductType(Label.BeverageType.valueOf(request.getParameter("type")));
        //label.setProductSource(Label.BeverageSource.valueOf(request.getParameter("source")));
        try (PrintWriter out = response.getWriter()) {  
            
            List<Label> drinkList = search.runSearch(label);
            
            IPageFrame pg = WebComponentProvider.getCorrectFrame(request, "results");
            String results = WebComponentProvider.loadPartialPage(this, "Results-partial.html");
            
            /*StringBuilder b = new StringBuilder();
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
"                           <div class=\"panel-body\">Drink information</div>\n" +
"                           </div>\n" +
"                       </div>");
            }
            */
            results = results.replace("##Drinks", "");
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

}
