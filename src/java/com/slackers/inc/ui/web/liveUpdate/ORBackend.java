/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.ui.web.liveUpdate;

import com.slackers.inc.Lists.Origin;
import com.slackers.inc.Lists.OriginList;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
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
@WebServlet(name = "ORBACKServlet", urlPatterns = {"/search/OR"})
public class ORBackend extends HttpServlet {

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
            
            List<Origin> list = new LinkedList<>();
            
            if (request.getParameter("code")!=null)
            {
                list.addAll(OriginList.getInstance().getList().stream().filter((e)->e.getOC().startsWith(request.getParameter("code"))).collect(Collectors.toList()));
            }
            if (request.getParameter("desc")!=null)
            {
                list.addAll(OriginList.getInstance().getList().stream().filter((e)->e.getDescription().startsWith(request.getParameter("desc"))).collect(Collectors.toList()));
            }            
            List<String> searches = list.stream().map((o)->o.getOC()+":::"+o.getDescription()).collect(Collectors.toList());
            Collections.sort(searches);            
            JsonArrayBuilder array = Json.createArrayBuilder();
            for (String s : searches)
            {
                String[] split = s.split(":::");
                JsonObjectBuilder var = Json.createObjectBuilder().add("code", split[0]);
                if (split.length==2 && split[1]!=null)
                {
                    var.add("desc", split[1]);
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
