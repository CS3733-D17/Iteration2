package com.slackers.inc.Controllers;

import com.slackers.inc.database.DerbyConnection;
import com.slackers.inc.database.entities.Label;
import com.slackers.inc.database.entities.LabelApplication;
import com.slackers.inc.database.entities.Manufacturer;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ManufacturerController {

    private  Manufacturer manufacturer;
    private LabelApplicationController labelAppController;
// controller for anything manufacturer related. allows a cleaner flow of code to be used and take advantage of other controllers
    public ManufacturerController() throws SQLException {
        this.manufacturer = null;
        this.labelAppController = new LabelApplicationController();
    }

    public ManufacturerController(Manufacturer manufacturer) throws SQLException {
        this.manufacturer = manufacturer;
        this.labelAppController = new LabelApplicationController();
    }

    public ManufacturerController(Manufacturer manufacturer, LabelApplicationController labelAppController){
        this.manufacturer = manufacturer;
        this.labelAppController = labelAppController;
    }
// allows a manufacturer to create and application with all the appropriate fields and forms
    public boolean createApplication() throws SQLException {
        LabelApplication template = manufacturer.getTemplateApplication();
        LabelApplication app = labelAppController.getLabelApplication();
        app.setApplicant(manufacturer);
        if (template != null) {
            app.setApplicantAddress(template.getApplicantAddress());
            app.setEmailAddress(manufacturer.getEmail());
            app.setMailingAddress(template.getMailingAddress());
            app.setRepresentativeId(template.getRepresentativeId());
            Label l = new Label();
            l.setEntityValues(template.getLabel().getEntityValues());
            app.setLabel(l);
            app.setPhoneNumber(template.getPhoneNumber());
            app.setStatus(LabelApplication.ApplicationStatus.SUBMITTED_FOR_REVIEW);
        }
        boolean res = labelAppController.createApplication();
        this.manufacturer.addApplications(this.labelAppController.getLabelApplication());
        return res;
    }
//allows the manufacturer to subitthe application to the database
    public boolean submitApplication() throws SQLException {
        
        boolean res = labelAppController.submitApplication(manufacturer);
        DerbyConnection.getInstance().writeEntity(this.manufacturer, this.manufacturer.getPrimaryKeyName());
        return res;
    }
//allows a manufacturer to edit the application in the database
    public boolean editApplication() throws SQLException {
        this.manufacturer.removeApplications(this.labelAppController.getLabelApplication());
        this.manufacturer.addApplications(this.labelAppController.getLabelApplication());
        System.out.println(this.labelAppController.getLabelApplication());
        boolean res = labelAppController.editApplication();
        this.updateManufacturer();
        return res;
    }
//allows a manufacturer to sabe an application in progress
    public boolean saveApplication() throws SQLException {
        boolean res = labelAppController.saveApplication();
        DerbyConnection.getInstance().writeEntity(this.manufacturer, this.manufacturer.getPrimaryKeyName());
        return res;
    }
//allows a manufacturer to delete an application and remove it from the database
    public boolean deleteApplication() throws SQLException {
        this.manufacturer.removeApplications(this.labelAppController.getLabelApplication());
        boolean res = labelAppController.deleteApplication();
        DerbyConnection.getInstance().writeEntity(this.manufacturer, this.manufacturer.getPrimaryKeyName());
        return res;
    }
 //allows a manufacturer to edit thier profile information
    public boolean updateManufacturer() throws SQLException {
        return DerbyConnection.getInstance().writeEntity(this.manufacturer, this.manufacturer.getPrimaryKeyName());
    }
    
    public boolean refresh()
    {
        try {
            return new AccountController(this.manufacturer).reload();
        } catch (SQLException ex) {
            Logger.getLogger(ManufacturerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    public LabelApplicationController getLabelAppController(){
        return labelAppController;
    }

    public Manufacturer getManufacturer() {
        return manufacturer;
    }

    
}
