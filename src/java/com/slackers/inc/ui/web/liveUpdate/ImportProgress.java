/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.ui.web.liveUpdate;

import com.slackers.inc.Controllers.Csv.CsvApplicationImporter;
import com.slackers.inc.ui.web.IPageFrame;
import com.slackers.inc.ui.web.WebComponentProvider;
import java.io.IOException;
import java.io.PrintWriter;
import javax.json.Json;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
@WebServlet(name = "ImportProgress", urlPatterns = {"/live/import"})
public class ImportProgress extends HttpServlet {

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
        response.setContentType("application/json;");
        try (PrintWriter out = response.getWriter()) {
            CsvApplicationImporter importer = CsvApplicationImporter.getInstance();
            /*IPageFrame pg = WebComponentProvider.getCorrectFrame(request, "View Import Progress");
            if (importer.isRunning())
            {
                String file = importer.getCurrentFile();
            }
            else
            {
                
            }
            pg.setBody("");
            out.println(WebComponentProvider.buildPage(pg, request));*/
            double progress = (double)importer.getLineNumber();
            progress = progress/importer.getTotalLines();
            if (Double.isNaN(progress))
                progress = 0.0;
            progress = progress*100;
            if (progress>99.99)
                progress = 100;
            String file = importer.getCurrentFile();
            if (file==null)
                file = "Unknown";
            else
                file = "..."+file.substring(file.lastIndexOf("\\"));
            out.println(Json.createObjectBuilder().add("percent", (double)progress)
                    .add("file", file).add("running", importer.isRunning())
                    .add("filesToProcess", importer.getFiles().size()).build().toString());
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
