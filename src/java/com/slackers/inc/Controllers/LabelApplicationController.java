/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.Controllers;

import com.slackers.inc.database.DerbyConnection;
import com.slackers.inc.database.entities.Address;
import com.slackers.inc.database.entities.ApplicationApproval;
import com.slackers.inc.database.entities.BeerLabel;
import com.slackers.inc.database.entities.DistilledLabel;
import com.slackers.inc.database.entities.Label;
import com.slackers.inc.database.entities.Label.BeverageSource;
import com.slackers.inc.database.entities.Label.BeverageType;
import com.slackers.inc.database.entities.LabelApplication;
import com.slackers.inc.database.entities.LabelApplication.ApplicationType;
import com.slackers.inc.database.entities.LabelComment;
import com.slackers.inc.database.entities.Manufacturer;
import com.slackers.inc.database.entities.UsEmployee;
import com.slackers.inc.database.entities.User;
import com.slackers.inc.database.entities.WineLabel;
import com.slackers.inc.ui.web.form.LabelImageGenerator;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Base64;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
public class LabelApplicationController {

    public static final String APPLICATION_GENERAL_COOKIE_NAME = "SSINCAP_GEN";
    public static final String APPLICATION_DATA_COOKIE_NAME = "SSINCAP_DATA";
    public static final String APPLICATION_LABEL_COOKIE_NAME = "SSINCAP_LBL";

    private DerbyConnection db;
    private LabelApplication application;

    public LabelApplicationController(LabelApplication application) throws SQLException {
        db = DerbyConnection.getInstance();
        this.application = application;
    }

    public LabelApplicationController() throws SQLException {
        this(new LabelApplication());
    }

    public LabelApplicationController(long applicationId) throws SQLException {
        this.application = new LabelApplication(applicationId);
        db = DerbyConnection.getInstance();
        db.getEntity(application, application.getPrimaryKeyName());
    }

    public LabelApplication getApplication() {
        return application;
    }

    public void setApplication(LabelApplication application) {
        this.application = application;
    }

    public LabelApplication getLabelApplication() {
        return this.application;
    }

    public LabelApplication createApplicationFromRequest(ServletContext context, HttpServletRequest request) {
        User pageUser = AccountController.getPageUser(request);
        if (!(pageUser instanceof Manufacturer)) {
            return null;
        }

        Label l = this.createLabelFromRequest(context, request);
        if (l == null) {
            return null;
        }
        this.application.setLabel(l);

        this.application.setEmailAddress(request.getParameter("email"));
        this.application.setApplicant((Manufacturer) pageUser);
        this.application.setPhoneNumber(request.getParameter("phone"));
        this.application.setRepresentativeId(request.getParameter("representativeId"));
        if (request.getParameter("NEW") != null) {
            this.application.addApplicationType(LabelApplication.ApplicationType.NEW, null);
        }
        if (request.getParameter("DISTINCT") != null) {
            this.application.addApplicationType(LabelApplication.ApplicationType.DISTINCT, request.getParameter("capacity"));
        }
        if (request.getParameter("EXEMPT") != null) {
            this.application.addApplicationType(LabelApplication.ApplicationType.EXEMPT, request.getParameter("state"));
        }
        if (request.getParameter("RESUBMIT") != null) {
            this.application.addApplicationType(LabelApplication.ApplicationType.RESUBMIT, request.getParameter("tbbid"));
        }
        try {
            this.application.setApplicantAddress(Address.tryParse(request.getParameter("address")));
            this.application.setMailingAddress(Address.tryParse(request.getParameter("mailAddress")));
        } catch (Exception e) {
        }

        return this.application;
    }

    public Label createLabelFromRequest(ServletContext context, HttpServletRequest request) {
        Label label = new Label();

        label.setIsAccepted(false);
        label.setPlantNumber(request.getParameter("plantNumber"));
        label.setBrandName(request.getParameter("brandName"));

        label.setFancifulName(request.getParameter("fancifulName"));
        label.setGeneralInfo(request.getParameter("generalInfo"));
        label.setSerialNumber(request.getParameter("serialNumber"));
        label.setFormula(request.getParameter("formula"));

        label.setRepresentativeIdNumber(request.getParameter("representativeId"));
        try {

            label.setProductSource(Label.BeverageSource.valueOf(request.getParameter("source")));
            System.out.println("AL:"+request.getParameter("alcoholContent"));
            label.setAlcoholContent(Double.parseDouble(request.getParameter("alcoholContent").replace("%", "")));
            BeverageType type = BeverageType.valueOf(request.getParameter("type"));
            label.setProductType(type);
            if (type == BeverageType.BEER) {
                BeerLabel newLabel = new BeerLabel();
                newLabel.setEntityValues(label.getEntityValues());
                label = newLabel;
            }
            if (type == BeverageType.DISTILLED) {
                DistilledLabel newLabel = new DistilledLabel();
                newLabel.setEntityValues(label.getEntityValues());
                label = newLabel;
            }
            if (type == BeverageType.WINE) {
                WineLabel newLabel = new WineLabel();
                newLabel.setEntityValues(label.getEntityValues());
                newLabel.setPhLevel(Double.parseDouble(request.getParameter("pH")));
                newLabel.setVintage(Integer.parseInt(request.getParameter("vintage")));
                newLabel.setGrapeVarietal(request.getParameter("grapeVarietal"));
                newLabel.setWineAppelation(request.getParameter("wineAppelation"));
                label = newLabel;
            }
            System.out.println(label.getAlcoholContent());
            Part img = request.getPart("labelImageUpload");
            if (img != null) {
                label.setLabelImageType(context.getMimeType(img.getSubmittedFileName()));
                try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                    BufferedImage imgBuffered = ImageIO.read(img.getInputStream());
                    if (imgBuffered.getWidth() > 1000) {
                        Image temp = imgBuffered.getScaledInstance(1000, -1, Image.SCALE_DEFAULT);
                        BufferedImage toSave = new BufferedImage(temp.getWidth(null), temp.getHeight(null), BufferedImage.TYPE_INT_RGB);
                        toSave.getGraphics().drawImage(temp, 0, 0, null);
                        ImageIO.write(toSave, "png", buffer);
                        buffer.flush();
                        toSave.getGraphics().dispose();
                    } else {
                        ImageIO.write(imgBuffered, "png", buffer);
                        buffer.flush();
                    }
                    label.setLabelImage(buffer.toByteArray());
                    label.setLabelImageType("image/png");
                }
            } else if (request.getParameter("useUrl") != null) {
                try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                    HttpURLConnection connection = (HttpURLConnection) new URL(request.getParameter("useUrl")).openConnection();
                    connection.connect();
                    try (InputStream input = connection.getInputStream()) {
                        BufferedImage imgBuffered = ImageIO.read(input);
                        if (imgBuffered != null) {
                            if (imgBuffered.getWidth() > 1000) {
                                Image temp = imgBuffered.getScaledInstance(1000, -1, Image.SCALE_DEFAULT);
                                BufferedImage toSave = new BufferedImage(temp.getWidth(null), temp.getHeight(null), BufferedImage.TYPE_INT_RGB);
                                toSave.getGraphics().drawImage(temp, 0, 0, null);
                                ImageIO.write(toSave, "png", buffer);
                                buffer.flush();
                                toSave.getGraphics().dispose();
                            } else {
                                ImageIO.write(imgBuffered, "png", buffer);
                                buffer.flush();
                            }
                            label.setLabelImage(buffer.toByteArray());
                            label.setLabelImageType("image/png");
                        } else {
                            System.out.println("Image cannot be read. Using url reference");
                            label.setLabelImage(new URL(request.getParameter("useUrl")).toString().getBytes(StandardCharsets.US_ASCII));
                            label.setLabelImageType("urlAbsolute");
                        }
                    }
                    connection.disconnect();
                }
            }
            return label;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return label;
    }

    public LabelApplication editApplicationFromRequest(ServletContext context, HttpServletRequest request) {
        User pageUser = AccountController.getPageUser(request);
        if (!(pageUser instanceof Manufacturer)) {
            return null;
        }

        String idStr = request.getParameter("id");
        if (idStr == null) {
            return null;
        }

        try {
            long id = Long.parseLong(idStr);

            this.loadApplication(id);
            Label l = this.editLabelFromRequest(context, request);
            //DerbyConnection.getInstance().createEntity(l);
            this.application.setLabel(l);
            System.out.println(l);
            DerbyConnection.getInstance().writeEntity(this.application, this.application.getPrimaryKeyName());
        } catch (Exception e) {

        }

        return this.application;
    }

    public Label editLabelFromRequest(ServletContext context, HttpServletRequest request) {

        Label label = this.application.getLabel();
        boolean isAccepted = label.isIsAccepted();
        label.setIsAccepted(false);
        try {
            DerbyConnection.getInstance().writeEntity(label, label.getPrimaryKeyName());
        } catch (SQLException ex) {
            Logger.getLogger(LabelApplicationController.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<String> revisions = new LinkedList<>();

        long prevId = label.getLabelId();
        Set<String> revTypes = new HashSet<>();

        if (request.getParameter("rev1") != null) {
            revTypes.add("image");
        }
        if (request.getParameter("rev2") != null) {
            revTypes.add("image");
        }
        if (request.getParameter("rev3") != null) {
            revTypes.add("image");
        }
        if (request.getParameter("rev4") != null) {
            revTypes.add("image");
            revTypes.add("blend");
        }
        if (request.getParameter("rev5") != null) {
            revTypes.add("image");
            revTypes.add("vintage");
        }
        if (request.getParameter("rev6") != null) {
            revTypes.add("image");
        }
        if (request.getParameter("rev7") != null) {
            revTypes.add("image");
            revTypes.add("ph");
        }
        if (request.getParameter("rev8") != null) {
            revTypes.add("image");
            revTypes.add("general");
        }
        if (request.getParameter("rev9") != null) {
            revTypes.add("image");
        }
        if (request.getParameter("rev10") != null) {
            revTypes.add("image");
            revTypes.add("formula");
        }
        if (request.getParameter("rev11") != null) {
            revTypes.add("image");
            revTypes.add("alcohol");
        }
        if (request.getParameter("rev12") != null) {
            revTypes.add("image");
        }

        System.out.println(String.join(", ", revTypes));

        if (revTypes.contains("alcohol")) {
            try {
                
                label.setAlcoholContent(Double.parseDouble(request.getParameter("alcoholContent-new")));
                revisions.add("Changed alcohol content");
            } catch (Exception e) {
            }
        }
        if (revTypes.contains("vintage")) {
            try {
                ((WineLabel) label).setVintage(Integer.parseInt(request.getParameter("vintage-new")));
                System.out.println("Vintage: " + Integer.parseInt(request.getParameter("vintage-new")));
                revisions.add("Changed vintage");
            } catch (Exception e) {
            }
        }
        if (revTypes.contains("ph")) {
            try {
                ((WineLabel) label).setPhLevel(Double.parseDouble(request.getParameter("pH-new")));
                revisions.add("Changed vintage");
            } catch (Exception e) {
            }
        }
        if (revTypes.contains("blend")) {
            try {
                ((WineLabel) label).setGrapeVarietal(request.getParameter("grapeVarietal-new"));
                ((WineLabel) label).setWineAppelation(request.getParameter("wineAppelation-new"));
                revisions.add("Changed grape varietal");
                revisions.add("Changed wine appelation");
            } catch (Exception e) {
            }
        }
        if (revTypes.contains("general")) {
            try {
                label.setGeneralInfo(request.getParameter("generalInfo-new"));
                revisions.add("Changed info");
                System.out.println("Genral: " + request.getParameter("generalInfo-new"));
            } catch (Exception e) {
            }
        }
        if (revTypes.contains("formula")) {
            try {
                label.setFormula(request.getParameter("formula-new"));
                revisions.add("Changed formula");
            } catch (Exception e) {
            }
        }

        if (revTypes.contains("image")) {
            try {
                revisions.add("Changed label image");
                Part img = request.getPart("labelImageUpload-new");
                if (img != null) {
                    label.setLabelImageType(context.getMimeType(img.getSubmittedFileName()));
                    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                    BufferedImage imgBuffered = ImageIO.read(img.getInputStream());
                    if (imgBuffered.getWidth() > 1000) {
                        Image temp = imgBuffered.getScaledInstance(1000, -1, Image.SCALE_DEFAULT);
                        BufferedImage toSave = new BufferedImage(temp.getWidth(null), temp.getHeight(null), BufferedImage.TYPE_INT_RGB);
                        toSave.getGraphics().drawImage(temp, 0, 0, null);
                        ImageIO.write(toSave, "png", buffer);
                        buffer.flush();
                        toSave.getGraphics().dispose();
                    } else {
                        ImageIO.write(imgBuffered, "png", buffer);
                        buffer.flush();
                    }
                    label.setLabelImage(buffer.toByteArray());
                    label.setLabelImageType("image/png");
                    buffer.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            label.setIsAccepted(isAccepted);
            DerbyConnection.getInstance().createEntity(label);
        } catch (SQLException ex) {
            Logger.getLogger(LabelApplicationController.class.getName()).log(Level.SEVERE, null, ex);
        }
        User usr = AccountController.getPageUser(request);
        if (usr != null) {
            this.application.getComments().add(new LabelComment(usr, this.buildChangeComment(this.application.getApplicationId(), label.getLabelId(), prevId, revisions)));
        }

        return label;
    }

    public String validateApplication() {
        System.out.println("INVAL"+this.application.getLabel().getAlcoholContent());
        if (this.application.getEmailAddress() == null || this.application.getEmailAddress().length() < 3) {
            return "Invalid email address";
        }
        if (this.application.getApplicant() == null || this.application.getApplicant().getEmail().equals(Manufacturer.NULL_MANUFACTURER.getEmail())
                || !(this.application.getApplicant() instanceof Manufacturer)) {
            return "Invalid applicant";
        }
        if (this.application.getApplicantAddress() == null || this.application.getApplicantAddress().getZipCode() == -1) {
            return "Invalid applicant address";
        }
        if (this.application.getMailingAddress() == null) {
            this.application.setMailingAddress(this.application.getApplicantAddress());//use mailing address as default
        }
        if (this.application.getMailingAddress() == null || this.application.getMailingAddress().getZipCode() == -1) {
            return "Invalid mailing address";
        }
        if (this.application.getPhoneNumber() == null || this.application.getPhoneNumber().length() < 10 || this.application.getPhoneNumber().length() > 21) {
            return "Invalid phone number";
        }
        for (Entry<ApplicationType, String> e : this.application.getApplicationTypes().entrySet()) {
            if (e.getKey() == ApplicationType.DISTINCT) {
                if (e.getValue() == null || e.getValue().length() < 1) {
                    return "Invalid bottle capacity before closure";
                }
            }
            if (e.getKey() == ApplicationType.EXEMPT) {
                if (e.getValue() == null || e.getValue().length() != 2) {
                    return "Invalid state for exemption";
                }
            }
            if (e.getKey() == ApplicationType.RESUBMIT) {
                if (e.getValue() == null || e.getValue().length() < 1) {
                    return "Invalid TBB id for resubmission";
                }
            }
        }
        return this.validateLabel();
    }

    public String validateLabel() {
        System.out.println("VALLBL:"+this.application.getLabel().getAlcoholContent());
        Label l = this.application.getLabel();
        if (l == null) {
            return "Invalid label information";
        }
        if (l.getBrandName() == null || l.getBrandName().length() < 2) {
            return "Invalid brand name";
        }
        if (l.getPlantNumber() == null || l.getPlantNumber().length() < 2) {
            return "Invalid plant number";
        }
        if (l.getSerialNumber() == null || l.getSerialNumber().length() < 5 || l.getSerialNumber().length() > 10) {
            return "Invalid serial number";
        }
        if (l.getProductSource() == null || l.getProductSource() == BeverageSource.UNKNOWN) {
            return "Invalid beverage source";
        }
        if (l.getProductType() == null || l.getProductType() == BeverageType.UNKNOWN) {
            return "Invalid beverage type";
        }
        if (l.getProductType() == null || l.getProductType() == BeverageType.UNKNOWN) {
            return "Invalid beverage type";
        }
        if (l.getAlcoholContent() < 0 || l.getAlcoholContent() > 100) {
            System.out.println("ACL"+l.getAlcoholContent());
            return "Invalid alchohol content";
        }
        if (l.getFormula() == null || l.getFormula().length() < 2) {
            return "Formula is invalid";
        }
        if (l instanceof WineLabel) {
            if (((WineLabel) l).getPhLevel() < 0 || ((WineLabel) l).getPhLevel() > 14) {
                return "Invalid pH level";
            }
            if (((WineLabel) l).getGrapeVarietal() == null || ((WineLabel) l).getGrapeVarietal().length() < 2) {
                return "Grape varietal is invalid";
            }
        }
        if (l.getLabelImage() == null || l.getLabelImageType() == null || l.getLabelImageType().length() < 3) {
            return "Label image is invalid";
        }
        return null;
    }

    public void removeApplicationFromCookies(HttpServletResponse response) {
        Cookie data = new Cookie(APPLICATION_DATA_COOKIE_NAME, null);
        Cookie gen = new Cookie(APPLICATION_GENERAL_COOKIE_NAME, null);
        Cookie lbl = new Cookie(APPLICATION_LABEL_COOKIE_NAME, null);

        data.setMaxAge(0);
        gen.setMaxAge(0);
        lbl.setMaxAge(0);

        data.setPath("/");
        gen.setPath("/");
        lbl.setPath("/");

        response.addCookie(data);
        response.addCookie(gen);
        response.addCookie(lbl);
    }

    public void writeApplicationToCookies(HttpServletResponse response) {
        JsonObjectBuilder generalObj = Json.createObjectBuilder().add("email", this.application.getEmailAddress())
                .add("phone", this.application.getPhoneNumber())
                .add("TBB_ID", String.format("%012d", this.application.getApplicationId()))
                .add("TBB_OR", this.application.getTBB_OR())
                .add("TBB_CT", this.application.getTBB_CT())
                .add("representativeId", this.application.getRepresentativeId());
        if (this.application.getApplicantAddress() != null) {
            generalObj.add("address", this.application.getApplicantAddress().toString());
        }
        if (this.application.getMailingAddress() != null) {
            generalObj.add("mailAddress", this.application.getMailingAddress().toString());
        }
        generalObj.add("appStatus", this.application.getStatus().name());

        for (Entry<ApplicationType, String> e : this.application.getApplicationTypes().entrySet()) {
            if (e.getKey() == ApplicationType.NEW) {
                generalObj.add("NEW", "checked");
            }
            if (e.getKey() == ApplicationType.DISTINCT) {
                generalObj.add("DISTINCT", "checked");
                generalObj.add("capacity", e.getValue());
            }
            if (e.getKey() == ApplicationType.EXEMPT) {
                generalObj.add("EXEMPT", "checked");
                generalObj.add("state", e.getValue());
            }
            if (e.getKey() == ApplicationType.RESUBMIT) {
                generalObj.add("RESUBMIT", "checked");
                generalObj.add("tbbid", e.getValue());
            }
        }

        Label l = this.application.getLabel();

        Cookie gen = new Cookie(APPLICATION_GENERAL_COOKIE_NAME, Base64.getEncoder().encodeToString(generalObj.build().toString().getBytes(StandardCharsets.UTF_8)));

        gen.setMaxAge(3600);
        gen.setPath("/");
        response.addCookie(gen);
        //this.writeLabelToCookies(response, l);
    }

    public void writeLabelToCookies(HttpServletResponse response) {
        this.writeLabelToCookies(response, this.application.getLabel());
    }

    public void writeLabelToCookies(HttpServletResponse response, Label l) {
        JsonObjectBuilder labelObj = Json.createObjectBuilder().add("plantNumber", l.getPlantNumber())
                .add("brandName", l.getBrandName())
                .add("fancifulName", l.getFancifulName())
                .add("serialNumber", l.getSerialNumber())
                .add("type", l.getProductType().name())
                .add("source", l.getProductSource().name())
                .add("alcoholContent", Double.toString(l.getAlcoholContent()));

        if (l instanceof WineLabel) {
            labelObj.add("pH", Double.toString(((WineLabel) l).getPhLevel()));
            labelObj.add("vintage", Integer.toString(((WineLabel) l).getVintage()));
            if (((WineLabel) l).getGrapeVarietal() != null) {
                labelObj.add("grapeVarietal", ((WineLabel) l).getGrapeVarietal());
            }
            if (((WineLabel) l).getWineAppelation() != null) {
                labelObj.add("wineAppelation", ((WineLabel) l).getWineAppelation());
            }
        }

        JsonObjectBuilder dataObj = Json.createObjectBuilder().add("formula", l.getFormula())
                .add("generalInfo", l.getGeneralInfo());

        Cookie data = new Cookie(APPLICATION_DATA_COOKIE_NAME, Base64.getEncoder().encodeToString(dataObj.build().toString().getBytes(StandardCharsets.UTF_8)));
        Cookie lbl = new Cookie(APPLICATION_LABEL_COOKIE_NAME, Base64.getEncoder().encodeToString(labelObj.build().toString().getBytes(StandardCharsets.UTF_8)));

        data.setMaxAge(3600);
        lbl.setMaxAge(3600);
        data.setPath("/");
        lbl.setPath("/");
        response.addCookie(lbl);
        response.addCookie(data);
    }

    public void employeeJson() {
        UsEmployee employee = new UsEmployee();
        List<UsEmployee> list;
        try {
            list = db.getAllEntites_Typed(employee);
        } catch (SQLException ex) {
            Logger.getLogger(LabelApplicationController.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Something went horrible wrong");
            return;
        }
        // TODO Create json file

    }

    public String renderCommentList(HttpServletRequest request) {
        StringBuilder b = new StringBuilder();
        b.append("<div class=\"row\">").append("<div class=\"col-sm-1 col-md-2\"></div>");
        b.append("<div class=\"col-sm-10 col-md-8\">");
        for (LabelComment comment : this.application.getComments()) {
            b.append(this.renderComment(request, comment));
        }
        b.append("</div>");
        b.append("<div class=\"col-sm-1 col-md-2\"></div>").append("</div>");
        return b.toString();
    }

    public String renderComment(HttpServletRequest request, LabelComment comment) {
        User usr = comment.getSubmitter();
        StringBuilder b = new StringBuilder();
        b.append("<div class=\"panel panel-info\">").append("<div class=\"panel-heading\">");
        if (usr == null) {
            b.append("Unknown");
        } else {
            b.append(usr.getFirstName()).append(" ").append(usr.getLastName()).append(" (").append(usr.getEmail()).append(")");
        }
        b.append("<div style=\"float:right;\">").append(comment.getDate()).append("</div>");
        b.append("</div>").append("<div class=\"panel-body\">");
        b.append(comment.getComment());
        b.append("</div>").append("</div>");
        return b.toString();
    }

    public String buildChangeComment(long applicationId, long newLabelId, long prevLabelId, List<String> revisions) {

        StringBuilder b = new StringBuilder();

        b.append("<h4>Made the Following Label Revisions:</h4>");
        b.append("<ul>");

        for (String s : revisions) {
            b.append("<li>");
            b.append(s);
            b.append("</li>");
        }
        b.append("</ul>");
        b.append("<div class=\"row\">");
        b.append("<div class=\"col-sm-6\">");
        b.append("<br><label>Old Image:</label><br>");
        b.append("<img src=\"").append(LabelImageGenerator.getAccessStringForApplication(prevLabelId)).append("&targetWidth=300\">");
        b.append("</div>").append("<div class=\"col-sm-6\">");
        b.append("<br><label>New Image:</label><br>");
        b.append("<img src=\"").append(LabelImageGenerator.getAccessStringForApplication(newLabelId)).append("&targetWidth=300\">");
        b.append("</div>").append("</div>");
        b.append("<a class=\"btn btn-primary\" style=\"float:right;\" ");
        b.append("href=\"").append("/SuperSlackers/form/view?type=previous&id=").append(applicationId).append("&labelId=").append(prevLabelId).append("\">");
        b.append("View Previous Label");
        b.append("</a>");
        return b.toString();
    }

    public Label getLabelImage(long labelId) {
        return this.getLabelImage(labelId, true);
    }

    public Label getLabelImage(long labelId, boolean getImage) {
        try {
            Label l = new Label();
            Label l2;
            l.setLabelId(labelId);
            this.db.getEntity(l, l.getPrimaryKeyName());
            if (l.getProductType() == BeverageType.BEER) {
                l2 = new BeerLabel();
                l2.setEntityValues(l.getEntityValues());
                l2.setPullImageOut(getImage);
                this.db.getEntity(l2, l2.getPrimaryKeyName());
                return l2;
            } else if (l.getProductType() == BeverageType.WINE) {
                l2 = new WineLabel();
                l2.setEntityValues(l.getEntityValues());
                l2.setPullImageOut(getImage);
                this.db.getEntity(l2, l2.getPrimaryKeyName());
                return l2;
            } else if (l.getProductType() == BeverageType.DISTILLED) {
                l2 = new DistilledLabel();
                l2.setEntityValues(l.getEntityValues());
                l2.setPullImageOut(getImage);
                this.db.getEntity(l2, l2.getPrimaryKeyName());
                return l2;
            }
            l.setPullImageOut(getImage);
            this.db.getEntity(l, l.getPrimaryKeyName());
            return l;
        } catch (SQLException ex) {
            return null;
        }
    }

    public boolean setNewReviewer(UsEmployee employee) throws SQLException {
        this.application.setSubmitter(this.application.getReviewer());
        this.application.setReviewer(employee);
        this.application.setStatus(LabelApplication.ApplicationStatus.UNDER_REVIEW);
        return db.writeEntity(this.application, this.application.getPrimaryKeyName());
    }

    public boolean attachComment(LabelComment coment) throws SQLException {
        this.application.getComments().add(coment);
        return true;
    }

    public boolean attachApproval(ApplicationApproval approval) throws SQLException {
        this.application.getLabel().setApproval(approval);
        return db.writeEntity(this.application, this.application.getPrimaryKeyName());
    }

    public boolean saveApplication() throws SQLException {
        return db.writeEntity(this.application, this.application.getPrimaryKeyName());
    }

    public boolean editApplication() throws SQLException {
        this.application.updateLabel();
        return db.writeEntity(this.application, this.application.getPrimaryKeyName());
    }

    public boolean deleteApplication() throws SQLException {
        return db.deleteEntity(this.application, this.application.getPrimaryKeyName());
    }

    public boolean createApplication() throws SQLException {
        this.application.setStatus(LabelApplication.ApplicationStatus.NOT_COMPLETE);
        this.application.setReviewer(UsEmployee.NULL_EMPLOYEE);
        this.application.setSubmitter(UsEmployee.NULL_EMPLOYEE);
        db.createEntity(this.application);
        return true;
    }

    public boolean loadApplication(long id) throws SQLException {
        this.application.setApplicationId(id);
        db.getEntity(this.application, this.application.getPrimaryKeyName());
        return true;
    }

    public boolean createApplication(LabelApplication application) throws SQLException {
        this.application = application;
        return this.createApplication();
    }

    public boolean submitApplication(Manufacturer submitter) throws SQLException {
        this.application.setApplicant(submitter);
        this.application.setStatus(LabelApplication.ApplicationStatus.SUBMITTED);
        this.application.setApplicationDate(new Date(Date.from(Instant.now()).getTime()));
        this.application.setSubmitter(UsEmployee.NULL_EMPLOYEE);
        this.application.setReviewer(UsEmployee.NULL_EMPLOYEE);
        this.application.getLabel().setApproval(null);
        this.application.getComments().add(new LabelComment(submitter, "<h4>Submitted the application</h4>"));
        boolean res = db.writeEntity(this.application, this.application.getPrimaryKeyName());
        submitter.addApplications(this.application);
        this.db.writeEntity(submitter, submitter.getPrimaryKeyName());
        //this.autoSelectReviewer();
        return res;
    }

    public boolean approveApplication(UsEmployee submitter, Date experationDate) throws SQLException {
        ApplicationApproval approval = new ApplicationApproval(submitter, experationDate);
        approval.setApplication(application);
        this.application.setStatus(LabelApplication.ApplicationStatus.APPROVED);
        this.application.getLabel().setApproval(approval);
        this.application.setReviewer(UsEmployee.NULL_EMPLOYEE);
        this.application.setSubmitter(submitter);
        this.application.setApplicationDate(new Date(new java.util.Date().getTime()));
        submitter.getApplications().remove(this.application);
        this.db.writeEntity(submitter, submitter.getPrimaryKeyName());
        this.application.getComments().add(new LabelComment(submitter, "<h4><span style=\"color:green;\">Application Approved</span></h4><br><br>Expires: " + experationDate.toString()));
        for (LabelComment l : this.application.getComments()) {
            System.out.println(l);
        }
        return db.writeEntity(this.application, this.application.getPrimaryKeyName());
    }

    public boolean approveApplication(UsEmployee submitter, Date experationDate, String comment) throws SQLException {
        ApplicationApproval approval = new ApplicationApproval(submitter, experationDate);
        approval.setApplication(application);
        this.application.setStatus(LabelApplication.ApplicationStatus.APPROVED);
        this.application.getLabel().setApproval(approval);
        this.application.setReviewer(UsEmployee.NULL_EMPLOYEE);
        this.application.setSubmitter(submitter);
        this.application.setApplicationDate(new Date(new java.util.Date().getTime()));
        submitter.getApplications().remove(this.application);
        this.db.writeEntity(submitter, submitter.getPrimaryKeyName());
        this.application.getComments().add(new LabelComment(submitter, "<h4><span style=\"color:green;\">Application Approved</span></h4><br><br>Expires: " + experationDate.toString()
                + "<br><br><h5><strong>Comment:</strong></h5>" + comment));
        for (LabelComment l : this.application.getComments()) {
            System.out.println(l);
        }
        return db.writeEntity(this.application, this.application.getPrimaryKeyName());
    }

    public boolean rejectApplication(UsEmployee submitter) throws SQLException {
        this.application.setStatus(LabelApplication.ApplicationStatus.REJECTED);
        this.application.getLabel().setApproval(null);
        this.application.setReviewer(UsEmployee.NULL_EMPLOYEE);
        this.application.setSubmitter(submitter);
        this.application.setApplicationDate(new Date(new java.util.Date().getTime()));
        submitter.getApplications().remove(this.application);
        this.db.writeEntity(submitter, submitter.getPrimaryKeyName());
        this.application.getComments().add(new LabelComment(submitter, "<h4><span style=\"color:red;\">Application Rejected</span></h4>"));
        return this.saveApplication();
    }

    public boolean rejectApplication(UsEmployee submitter, String comment) throws SQLException {
        this.application.setStatus(LabelApplication.ApplicationStatus.REJECTED);
        this.application.getLabel().setApproval(null);
        this.application.setReviewer(UsEmployee.NULL_EMPLOYEE);
        this.application.setSubmitter(submitter);
        this.application.setApplicationDate(new Date(new java.util.Date().getTime()));
        submitter.getApplications().remove(this.application);
        this.db.writeEntity(submitter, submitter.getPrimaryKeyName());
        this.application.getComments().add(new LabelComment(submitter, "<h4><span style=\"color:red;\">Application Rejected</span></h4>"
                + "<br><br><h5><strong>Comment:</strong></h5>" + comment));
        return this.saveApplication();
    }

    public boolean sendForCorrections() throws SQLException {
        this.application.setStatus(LabelApplication.ApplicationStatus.SENT_FOR_CORRECTIONS);
        return this.saveApplication();
    }

    public boolean autoSelectReviewer() {
        UsEmployee target = new UsEmployee();
        target.setUserType(User.UserType.US_EMPLOYEE);
        try {
            List<UsEmployee> employees = DerbyConnection.getInstance().getAllEntites_Typed(target, "userType");
            if (!employees.isEmpty()) {

                employees.sort(new Comparator<UsEmployee>() {
                    @Override
                    public int compare(UsEmployee o1, UsEmployee o2) {
                        return o1.getApplications().size() - o2.getApplications().size();
                    }
                });
                UsEmployee reviewer = employees.get(0);
                reviewer.removeApplication(application);
                boolean add = true;
                for (LabelApplication a : reviewer.getApplications()) {
                    if (a.getApplicationId() == this.application.getApplicationId()) {
                        add = false;
                        break;
                    }
                }
                if (add) {
                    reviewer.getApplications().add(this.application);
                    this.application.setReviewer(reviewer);
                    this.application.setStatus(LabelApplication.ApplicationStatus.UNDER_REVIEW);
                    this.db.writeEntity(reviewer, reviewer.getPrimaryKeyName());
                    this.saveApplication();
                }
            }

        } catch (SQLException ex) {
            Logger.getLogger(LabelApplicationController.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return false;
    }

    public static List<LabelApplication> getNextBatch() {
        List<LabelApplication> out = new LinkedList<>();
        return out;
    }

    public void setEntityValues(Map<String, Object> values) {
        application.setEntityValues(values);
    }

    public Map<String, Class> getEntityNameTypePairs() {
        return application.getEntityNameTypePairs();
    }

    public void setPrimaryKeyValue(Serializable value) {
        application.setPrimaryKeyValue(value);
    }

    public String getRepresentativeId() {
        return application.getRepresentativeId();
    }

    public void setRepresentativeId(String representativeId) {
        application.setRepresentativeId(representativeId);
    }

    public long getApplicationId() {
        return application.getApplicationId();
    }

    public void setApplicationId(long applicationId) {
        application.setApplicationId(applicationId);
    }

    public Address getApplicantAddress() {
        return application.getApplicantAddress();
    }

    public void setApplicantAddress(Address applicantAddress) {
        application.setApplicantAddress(applicantAddress);
    }

    public Address getMailingAddress() {
        return application.getMailingAddress();
    }

    public void setMailingAddress(Address mailingAddress) {
        application.setMailingAddress(mailingAddress);
    }

    public String getPhoneNumber() {
        return application.getPhoneNumber();
    }

    public void setPhoneNumber(String phoneNumber) {
        application.setPhoneNumber(phoneNumber);
    }

    public String getEmailAddress() {
        return application.getEmailAddress();
    }

    public void setEmailAddress(String emailAddress) {
        application.setEmailAddress(emailAddress);
    }

    public Date getApplicationDate() {
        return application.getApplicationDate();
    }

    public void setApplicationDate(Date applicationDate) {
        application.setApplicationDate(applicationDate);
    }

    public LabelApplication.ApplicationStatus getStatus() {
        return application.getStatus();
    }

    public void setStatus(LabelApplication.ApplicationStatus status) {
        application.setStatus(status);
    }

    public Manufacturer getApplicant() {
        return application.getApplicant();
    }

    public void setApplicant(Manufacturer applicant) {
        application.setApplicant(applicant);
    }

    public UsEmployee getReviewer() {
        return application.getReviewer();
    }

    public void setReviewer(UsEmployee reviewer) {
        application.setReviewer(reviewer);
    }

    public UsEmployee getSubmitter() {
        return application.getSubmitter();
    }

    public void setSubmitter(UsEmployee submitter) {
        application.setSubmitter(submitter);
    }

    public Label getLabel() {
        return application.getLabel();
    }

    public void setLabel(Label label) {
        application.setLabel(label);
    }

    public List<LabelComment> getComments() {
        return application.getComments();
    }

    public void setComments(List<LabelComment> comments) {
        application.setComments(comments);
    }

    public ApplicationApproval getApplicationApproval() {
        return application.getLabel().getApproval();
    }

    public void setApplicationApproval(ApplicationApproval applicationApproval) {
        application.getLabel().setApproval(applicationApproval);
    }

    public void setLabelType(BeverageType type) {
        application.setLabelType(type);
    }

    public void addApplicationType(ApplicationType applicationType, String value) {
        application.addApplicationType(applicationType, value);
    }

    public Map<ApplicationType, String> getApplicationTypes() {
        return application.getApplicationTypes();
    }

    public String getTBB_CT() {
        return application.getTBB_CT();
    }

    public void setTBB_CT(String TBB_CT) {
        application.setTBB_CT(TBB_CT);
    }

    public String getTBB_OR() {
        return application.getTBB_OR();
    }

    public void setTBB_OR(String TBB_OR) {
        application.setTBB_OR(TBB_OR);
    }

}
