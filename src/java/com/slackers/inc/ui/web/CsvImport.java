/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.ui.web;

import com.slackers.inc.Controllers.Csv.CsvApplicationImporter;
import com.slackers.inc.Controllers.Csv.CsvApplicationImporter.ApplicationConsumer;
import com.slackers.inc.Controllers.CsvAppImportController;
import com.slackers.inc.database.entities.LabelApplication;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
@WebServlet(name = "CsvImport", urlPatterns = {"/admin/CsvImport"})
public class CsvImport extends HttpServlet {

    // <editor-fold defaultstate="expanded" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
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
        response.setContentType("text/html;");
        try (PrintWriter out = response.getWriter()) {
            
            IPageFrame pg = WebComponentProvider.getCorrectFrame(request, "Import Progress");
            pg.setBody(WebComponentProvider.loadPartialPage(this, "csv-import-partial.html"));
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
        response.setContentType("text/html;");
        try (PrintWriter out = response.getWriter()) {
            CsvApplicationImporter importer = CsvApplicationImporter.getInstance();
            if (request.getParameter("action")!=null)
            {
                if (request.getParameter("action").equalsIgnoreCase("start"))
                {
                    if (!importer.isRunning())
                    {
                        importer.runAsync();
                        importer.setConsumer(CsvAppImportController.getInstance());
                    }
                }
                if (request.getParameter("action").equalsIgnoreCase("stop"))
                {
                    if (importer.isRunning())
                        importer.stop();
                }
            }
            IPageFrame pg = WebComponentProvider.getCorrectFrame(request, "Import Progress");
            pg.setBody(WebComponentProvider.loadPartialPage(this, "csv-import-partial.html"));
            out.println(WebComponentProvider.buildPage(pg, request));
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

}
