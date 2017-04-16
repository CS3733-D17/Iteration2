/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.Controllers;

import com.slackers.inc.database.DerbyConnection;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
@WebListener
public class ShutdownCleanup implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        /*System.out.println("-----PERFORMING DB SHUTDOWN------");
        DerbyConnection.getInstance().shutdownDb();*/
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("-----PERFORMING DB SHUTDOWN------");
        DerbyConnection.getInstance().shutdownDb();
    }
    
}
