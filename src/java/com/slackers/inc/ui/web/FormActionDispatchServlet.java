/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.ui.web;

import com.slackers.inc.Controllers.AccountController;
import com.slackers.inc.Controllers.LabelApplicationController;
import com.slackers.inc.database.entities.Manufacturer;
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
@WebServlet(name = "FormActionDispatchServlet", urlPatterns = {"/form"})
public class FormActionDispatchServlet extends HttpServlet {

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
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            
            AccountController controller = new AccountController();
            if (controller.verifyPermission(request, response, AccountController.Permission.EMPLOYEE)||
                    controller.verifyPermission(request, response, AccountController.Permission.MANUFACTURER))
            {
                LabelApplicationController appControl = new LabelApplicationController();
                appControl.removeApplicationFromCookies(response);
                
                String action = request.getParameter("action");            
                if (action!=null) // id already validated as a number because didn't throw exception
                {
                    if (action.equalsIgnoreCase("create"))
                    {
                        try
                        {
                            Manufacturer m = (Manufacturer)AccountController.getPageUser(request);
                            if (!m.getApplications().isEmpty())
                            {
                                LabelApplicationController con = new LabelApplicationController(m.getApplications().get(m.getApplications().size()-1));
                                con.writeApplicationToCookies(response);
                            }
                        }catch (Exception e)
                        {}
                        this.getServletContext().getRequestDispatcher("/form/create").forward(request, response);
                    }
                    if (action.equalsIgnoreCase("edit"))
                        this.getServletContext().getRequestDispatcher("/form/edit").forward(request, response);
                    if (action.equalsIgnoreCase("view"))
                        this.getServletContext().getRequestDispatcher("/form/view").forward(request, response);
                    if (action.equalsIgnoreCase("process"))
                        this.getServletContext().getRequestDispatcher("/form/process").forward(request, response);
                }                
            }            
        } catch (Exception ex) {
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
