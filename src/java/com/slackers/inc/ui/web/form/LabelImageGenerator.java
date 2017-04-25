/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.ui.web.form;

import com.slackers.inc.Controllers.LabelApplicationController;
import com.slackers.inc.database.entities.Label;
import com.slackers.inc.database.entities.LabelApplication;
import com.slackers.inc.ui.web.WebComponentProvider;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
@WebServlet(name = "LabelImageGet", urlPatterns = {"/image/label"})
@MultipartConfig
public class LabelImageGenerator extends HttpServlet {

    public static String getAccessStringForApplication(HttpServletRequest request, LabelApplication app) {
        return WebComponentProvider.root(request) + "image/label?id=" + Long.toString(app.getLabel().getLabelId());
    }

    public static String getAccessStringForApplication(HttpServletRequest request, LabelApplicationController app) {
        return WebComponentProvider.root(request) + "image/label?id=" + Long.toString(app.getLabel().getLabelId());
    }

    public static String getAccessStringForApplication(HttpServletRequest request, Label l) {
        return WebComponentProvider.root(request) + "image/label?id=" + Long.toString(l.getLabelId());
    }

    public static String getAccessStringForApplication(HttpServletRequest request, long id) {
        return WebComponentProvider.root(request) + "image/label?id=" + Long.toString(id);
    }

    public static String getAccessStringForApplication(Label l) {
        return WebComponentProvider.WEB_ROOT + "image/label?id=" + Long.toString(l.getLabelId());
    }

    public static String getAccessStringForApplication(long id) {
        return WebComponentProvider.WEB_ROOT + "image/label?id=" + Long.toString(id);
    }
    
    public static String getAccessStringForExistingApplication(HttpServletRequest request, String existingTTBid) {
        return WebComponentProvider.root(request) + "image/label?ttbId=" + existingTTBid;
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
        try (OutputStream out = response.getOutputStream()) {
            if (request.getParameter("ttbId") != null) {
                FormImporter.ImageData imageData = new FormImporter(request.getParameter("ttbId")).getImageData();
                if (imageData.getType() == FormImporter.ImageData.TYPE_PNG) {
                    response.setContentType("image/png;");
                } else {
                    response.setContentType("image/jpeg;");
                }
                try (ByteArrayInputStream bis = new ByteArrayInputStream(imageData.getBytes())) {
                    OutputStream outStream = response.getOutputStream();
                    byte[] b = new byte[256];
                    int off = 0;
                    int len = 0;
                    while ((len = bis.read(b)) != -1) {
                        outStream.write(b, off, b.length);
                    }
                }
                return;
            }
            if (request.getParameter("id") != null) {
                long id = Long.parseLong(request.getParameter("id"));
                LabelApplicationController appControl = new LabelApplicationController();
                Label label = appControl.getLabelImage(id);
                /*String mimeType = label.getLabelImageType();
                if (mimeType==null)
                {
                    mimeType = "image/png";
                }*/

                if (label.getLabelImageType().equalsIgnoreCase("urlAbsolute")) {
                    String url = new String(label.getLabelImage(), StandardCharsets.US_ASCII);
                    response.sendRedirect(url);
                    return;
                }

                response.setContentType(label.getLabelImageType());
                boolean wrote = false;
                if (request.getParameter("targetWidth") != null) {
                    try {
                        int width = Integer.parseInt(request.getParameter("targetWidth"));
                        try (ByteArrayInputStream bis = new ByteArrayInputStream(label.getLabelImage())) {
                            BufferedImage imgBuffered = ImageIO.read(bis);
                            Image temp = imgBuffered.getScaledInstance(width, -1, Image.SCALE_DEFAULT);
                            BufferedImage toSave = new BufferedImage(temp.getWidth(null), temp.getHeight(null), BufferedImage.TYPE_INT_RGB);
                            toSave.getGraphics().drawImage(temp, 0, 0, null);
                            ImageIO.write(toSave, "png", response.getOutputStream());
                            toSave.getGraphics().dispose();
                            wrote = true;
                        }
                    } catch (Exception e) {

                    }
                }
                if (!wrote) {
                    try (ByteArrayInputStream bis = new ByteArrayInputStream(label.getLabelImage())) {
                        BufferedImage imgBuffered = ImageIO.read(bis);
                        ImageIO.write(imgBuffered, "png", response.getOutputStream());
                    }
                }
            }
        } catch (Exception ex) {

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
        doGet(request, response);
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
