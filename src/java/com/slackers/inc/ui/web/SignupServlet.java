/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java.com.slackers.inc.ui.web;

import java.com.slackers.inc.Controllers.AccountController;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
@WebServlet(name = "SignupServlet", urlPatterns = {"/account/signup"})
public class SignupServlet extends HttpServlet {

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
            DefaultPage pg = new DefaultPage("Signup");
            pg.setBody(WebComponentProvider.loadPartialPage(this, "createAccount-partial.html"));
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
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            DefaultPage pg = new DefaultPage("Create Account");
            AccountController c = null;
            try {
                c = new AccountController();
            } catch (SQLException ex) {
                Logger.getLogger(AccountController.class.getName()).log(Level.SEVERE, null, ex);
                response.sendRedirect("signup");
                return;
            }
            try
            {
                if (c.createAccount(request, response))
                {
                    response.sendRedirect(WebComponentProvider.WEB_ROOT);
                    return;
                }
                else
                {
                    WebComponentProvider.setSuccessMessage(response, "Signup Error");
                    response.sendRedirect("signup");
                    return;
                }
            }
            catch (Exception e)
            {
                WebComponentProvider.setSuccessMessage(response, "User already exists");
                response.sendRedirect("signup");
                return;
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

}
