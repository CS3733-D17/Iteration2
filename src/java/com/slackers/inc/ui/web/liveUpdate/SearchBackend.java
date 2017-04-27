/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.ui.web.liveUpdate;

import com.slackers.inc.Controllers.Filters.AcceptedFilter;
import com.slackers.inc.Controllers.Filters.BrandNameRange;
import com.slackers.inc.Controllers.Filters.Filter;
import com.slackers.inc.database.DerbyConnection;
import com.slackers.inc.database.entities.Label;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
@WebServlet(name = "AutocompleteServlet", urlPatterns = {"/search/autocomplete"})
public class SearchBackend extends HttpServlet {

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
        response.setContentType("text/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            
            Label l = new Label();
            
            String brand = request.getParameter("brand") != null ? request.getParameter("brand") : "";
            String fancy = request.getParameter("fancy") != null ? request.getParameter("fancy") : "";
            
            List<Filter> filters = new LinkedList<>();
            filters.add(new BrandNameRange(brand));
            filters.add(new AcceptedFilter(true));
            List<Label> labels = null;
            try {
                labels = DerbyConnection.getInstance().search(l, filters, 10, 0);
            } catch (SQLException ex) {
                ex.printStackTrace();
                out.println("");
                return;
            }
            
            if (labels==null)
            {
                out.println(Json.createArrayBuilder().build().toString());
                return;
            }
            List<String> searches = labels.stream().map((m)->m.getBrandName()+":::"+m.getFancifulName()).collect(Collectors.toList());
            Collections.sort(searches);
            
            JsonArrayBuilder array = Json.createArrayBuilder();
            for (String s : searches)
            {
                String[] split = s.split(":::");
                JsonObjectBuilder var = Json.createObjectBuilder().add("brand", split[0]);
                if (split.length==2 && split[1]!=null)
                {
                    var.add("fanciful", split[1]);
                }
                array = array.add(var);
            }
            out.println(array.build().toString());
        }
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
        processRequest(request, response);
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
        processRequest(request, response);
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
