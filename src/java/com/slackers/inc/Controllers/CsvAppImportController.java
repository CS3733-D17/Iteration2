/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.Controllers;

import com.slackers.inc.Controllers.Csv.CsvApplicationImporter;
import com.slackers.inc.Controllers.Csv.CsvApplicationImporter.ApplicationConsumer;
import com.slackers.inc.database.entities.LabelApplication;
import com.slackers.inc.database.entities.LabelApplication.ApplicationStatus;
import java.sql.Date;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
public class CsvAppImportController implements ApplicationConsumer {
    
    private LabelApplicationController controller;
    
    private CsvAppImportController() {
        try {
            controller = new LabelApplicationController();
        } catch (SQLException ex) {
            Logger.getLogger(CsvAppImportController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static CsvAppImportController getInstance() {
        return CsvAppImportControllerHolder.INSTANCE;
    }

    @Override
    public void consume(LabelApplication app, CsvApplicationImporter importer) {
        try {
            if (app.getStatus()==ApplicationStatus.APPROVED)
            {
                controller.setApplication(app);
                controller.submitApplication(importer.getSubmitter());
                controller.approveApplication(importer.getApprover(), new Date(app.getApplicationDate().getTime()+(31536000000L*2)), "<h4>Loaded from csv</h4>");
            }
        } catch (SQLException ex) {
            Logger.getLogger(CsvAppImportController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static class CsvAppImportControllerHolder {

        private static final CsvAppImportController INSTANCE = new CsvAppImportController();
    }
}
