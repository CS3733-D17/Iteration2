/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.ui.web;

import com.slackers.inc.Controllers.Filters.Filter;
import com.slackers.inc.Controllers.Filters.RangeFilter;
import com.slackers.inc.database.DerbyConnection;
import com.slackers.inc.database.entities.LabelApplication;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Redslaya
 */
@WebServlet(name = "EmployeeProductivity", urlPatterns = {"/admin/empProd"})
public class EmployeeProductivity extends HttpServlet {

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
            IPageFrame pg = WebComponentProvider.getCorrectFrame(request, "Employee Productivity");

            
            List<Filter> filter = new LinkedList<>();
            filter.add(new RangeFilter(){
                @Override
                public Object getValueMin() {
                    return new Date(new java.util.Date().getTime()-604800000);
                }

                @Override
                public Object getValueMax() {
                    return new Date(new java.util.Date().getTime());
                }

                @Override
                public String getColumn() {
                    return "applicationDate";
                }
            });
            LabelApplication app = new LabelApplication();
            List<LabelApplication> formsInTheWeek = DerbyConnection.getInstance().search(app, filter);
            
            Map<String, Map<String,Integer>> values = new HashMap<>();
            
            for (LabelApplication a : formsInTheWeek)
            {
                if (values.get(a.getSubmitter().getEmail())!=null)
                {
                    Map<String,Integer> day = values.get(a.getSubmitter().getEmail());
                    if (day.get(a.getApplicationDate().toString())!=null)
                    {
                        int c = day.get(a.getApplicationDate().toString());
                        c++;
                        day.put(a.getApplicationDate().toString(), c);
                        values.put(a.getSubmitter().getEmail(), day);
                    }
                    else
                    {
                        day.put(a.getApplicationDate().toString(), 1);
                        values.put(a.getSubmitter().getEmail(), day);
                    }
                }
                else
                {
                    Map<String,Integer> day = new HashMap<>();
                    day.put(a.getApplicationDate().toString(), 1);
                    Map<String,Integer> future = new HashMap<>();
                    Date d = new Date(new java.util.Date().getTime()+86400000);
                    future.put(d.toString(), 1);
                    values.put(a.getSubmitter().getEmail(), day);
                    values.put(a.getSubmitter().getEmail(), future);
                }
            }
            
            JsonArrayBuilder data = Json.createArrayBuilder();
            
            JsonArrayBuilder history = Json.createArrayBuilder();
            
            
            for(Map.Entry<String, Map<String,Integer>> t: values.entrySet()) {
                String key = t.getKey();
                for (Map.Entry<String,Integer> e : t.getValue().entrySet()){
                    JsonObjectBuilder day = Json.createObjectBuilder()
                            .add("date", e.getKey())
                            .add("forms", e.getValue());
                            
                    history.add(day);
                    System.out.println("OuterKey: " + key + " InnerKey: " + e.getKey()+ " VALUE:" +e.getValue());
                }
                JsonObjectBuilder employee = Json.createObjectBuilder()
                        .add("name", key);
                
                employee.add("history", history);
                data.add(employee);
            }
            
            String global = data.build().toString();
            System.out.println(global);
            
            
            
            /* TODO output your page here. You may use following sample code. */
            pg.setBody(renderViz(global));
            out.println(WebComponentProvider.buildPage(pg, request));
            //out.println(renderViz(global));
           
        } catch (SQLException ex) {
            ex.printStackTrace();
            Logger.getLogger(EmployeeProductivity.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private String renderViz(String global) {
        
        String viz = WebComponentProvider.loadPartialPage(this, "productivity-partial.html");
        String replace = viz.replace("##global", "var json = " + global + ";");
        return replace;
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

    
    private class DayCount
    {
        private String date;
        private int count;
        
        public DayCount(Date date)
        {
            this.date = date.toString();
            count=0;
        }
        public String getDate()
        {
            return date;
        }
        public int getCount()
        {
            return count;
        }
        public void incCount()
        {
            count++;
        }
    }
}