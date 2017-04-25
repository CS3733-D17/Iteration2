/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.ui.web.form;

import com.slackers.inc.Http.HttpRequest;
import com.slackers.inc.Http.HttpResponse;
import com.slackers.inc.database.entities.Address;
import com.slackers.inc.database.entities.BeerLabel;
import com.slackers.inc.database.entities.DistilledLabel;
import com.slackers.inc.database.entities.Label;
import com.slackers.inc.database.entities.LabelApplication;
import com.slackers.inc.database.entities.Manufacturer;
import com.slackers.inc.database.entities.WineLabel;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
public class FormImporter {

    public static String COLAURL = "https://www.ttbonline.gov/colasonline/viewColaDetails.do?action=publicFormDisplay&ttbid=";
    private String tbbId;

    public FormImporter(String id) {
        this.tbbId = id;
    }

    public String getImageUrl() {
        HttpRequest request = new HttpRequest(COLAURL + this.tbbId);//16309001000410
        HttpResponse response = request.submitRequest();
        if (response != null) {
            Pattern imgs = Pattern.compile("<img src=\"(\\/colasonline\\/publicViewAttachment[\\s\\S]+?)\"");
            Matcher matchedimgs = imgs.matcher(response.getResponse());
            if (matchedimgs.find()) {
                System.out.println(COLAURL + this.tbbId);
                System.out.println("URL:" + matchedimgs.group(1));
                return "https://www.ttbonline.gov" + matchedimgs.group(1);
            }
        }
        return "";
    }

    public ImageData getImageData() {
        HttpRequest request = new HttpRequest(COLAURL + this.tbbId);//16309001000410
        HttpResponse response = request.submitRequest();
        if (response != null) {
            List<String> cookies = new LinkedList<>();
            for (String c : response.getHeaders().get("Set-Cookie")) {
                cookies.add(c.substring(0, c.indexOf(";")));
            }
            Pattern imgs = Pattern.compile("<img src=\"(\\/colasonline\\/publicViewAttachment[\\s\\S]+?)\"");
            Matcher matchedimgs = imgs.matcher(response.getResponse());
            if (matchedimgs.find()) {

                String url = "https://www.ttbonline.gov" + matchedimgs.group(1);
                url = url.replace(" ", "%20");
                BufferedImage imgBuffered = null;
                try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
                    HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestProperty("Cookie", String.join("; ", cookies));
                    connection.setRequestMethod("GET");
                    int type = ImageData.TYPE_PNG;
                    try (InputStream input = connection.getInputStream()) {
                        input.mark(Integer.MAX_VALUE);
                        try {
                            imgBuffered = ImageIO.read(input);
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
                                type = ImageData.TYPE_PNG;
                            }
                        } catch (Exception e) {
                        }
                        if (imgBuffered == null) {
                            input.reset();
                            byte[] b = new byte[256];
                            int off=0;
                            int len=0;
                            while ((len=input.read(b)) != -1) {
                                buffer.write(b, off, len);
                            }
                            type = ImageData.TYPE_JPEG;
                        }
                    }
                    connection.disconnect();
                    return new ImageData(buffer.toByteArray(), type);
                } catch (IOException ex) {
                    Logger.getLogger(FormImporter.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        }
        return null;
    }

    public String getExistingApplicationURL() {
        return COLAURL + this.tbbId;
    }

    public LabelApplication importApplication() {
        HttpRequest request = new HttpRequest(COLAURL + this.tbbId);//16309001000410
        HttpResponse response = request.submitRequest();
        if (response != null) {
            String resp = response.getResponse();

            LabelApplication app = new LabelApplication();
            Label lbl = new Label();

            Pattern boxes = Pattern.compile("<input([\\S\\s]*?)alt=\"(.*?)\">");
            Matcher matchedboxes = boxes.matcher(resp);

            while (matchedboxes.find()) {
                if (matchedboxes.group(1).contains("checked")) {
                    if (matchedboxes.group(2).contains("Source of Product: Imported")) {
                        lbl.setProductSource(Label.BeverageSource.IMPORTED);
                    } else if (matchedboxes.group(2).contains("Source of Product: Domestic")) {
                        lbl.setProductSource(Label.BeverageSource.DOMESTIC);
                    }
                    if (matchedboxes.group(2).contains("Type of Product: Wine")) {
                        Label old = lbl;
                        lbl = new WineLabel();
                        lbl.setEntityValues(old.getEntityValues());
                        lbl.setProductType(Label.BeverageType.WINE);
                    } else if (matchedboxes.group(2).contains("Type of Product: Malt")) {
                        Label old = lbl;
                        lbl = new BeerLabel();
                        lbl.setEntityValues(old.getEntityValues());
                        lbl.setProductType(Label.BeverageType.BEER);
                    } else if (matchedboxes.group(2).contains("Type of Product: Distilled")) {
                        Label old = lbl;
                        lbl = new DistilledLabel();
                        lbl.setEntityValues(old.getEntityValues());
                        lbl.setProductType(Label.BeverageType.DISTILLED);
                    }

                    if (matchedboxes.group(2).contains("Type of Application")) {
                        app.getApplicationTypes().put(LabelApplication.ApplicationType.NEW, "");
                    }
                    if (matchedboxes.group(2).contains("Certificate of label Approval")) {
                        app.getApplicationTypes().put(LabelApplication.ApplicationType.EXEMPT, "");
                    }
                    if (matchedboxes.group(2).contains("Distinctive Liquor Bottle Approval")) {
                        app.getApplicationTypes().put(LabelApplication.ApplicationType.DISTINCT, "");
                    }
                    if (matchedboxes.group(2).contains("Previous TTB Id")) {
                        app.getApplicationTypes().put(LabelApplication.ApplicationType.RESUBMIT, "");
                    }
                }
                /*System.out.println(matchedboxes.group(1));
                System.out.println(matchedboxes.group(2));
                System.out.println(matchedboxes.group(1).contains("checked"));
                System.out.println("\n\n\n\n");*/
                //n.add(matchedNames.group(1).trim());
            }

            Pattern names = Pattern.compile("<div class=\".*?label\">([\\S\\s]*?)<\\/div>[\\s\\S]*?<div class=\"data\">([\\S\\s]*?)<\\/div>");
            Matcher matchedNames = names.matcher(resp);

            while (matchedNames.find()) {

                if (matchedNames.group(1).contains("REP. ID. NO.")) {
                    app.setRepresentativeId(matchedNames.group(2).trim());
                    lbl.setRepresentativeIdNumber(matchedNames.group(2).trim());
                }
                if (matchedNames.group(1).equalsIgnoreCase("CT")) {
                    app.setTBB_CT(matchedNames.group(2).trim());
                }
                if (matchedNames.group(1).equalsIgnoreCase("OR")) {
                    app.setTBB_OR(matchedNames.group(2).trim());
                }
                if (matchedNames.group(1).contains("PLANT REGISTRY/BASIC PERMIT/BREWER'S NO.")) {
                    lbl.setPlantNumber(matchedNames.group(2).trim().replace("<br>", "").trim());
                }
                if (matchedNames.group(1).contains("SOURCE OF PRODUCT <I>(Required)</I>")) {
                    try {
                        String adr = matchedNames.group(2).trim();
                        adr = adr.replaceAll("<br>", "").trim();
                        //System.out.println("ADR: " + adr);
                        String[] adrLines = adr.split("\n");
                        String address = adrLines[0].trim().replaceAll("\\s+", " ") + "\n"
                                + adrLines[1].trim().replaceAll("\\s+", " ") + "\n"
                                + adrLines[3].trim().replaceAll("\\s+", " ") + " " + adrLines[4].trim().replaceAll("\\s+", "") + ", " + adrLines[5].trim().replaceAll("\\s+", "");
                        app.setApplicantAddress(Address.tryParse(address));
                    } catch (Exception e) {
                    }
                }
                if (matchedNames.group(1).contains("SERIAL NUMBER <I>(Required)</I>")) {
                    lbl.setSerialNumber(matchedNames.group(2).trim());
                }
                if (matchedNames.group(1).contains("TYPE OF PRODUCT <I>(Required)</I>")) {
                    lbl.setBrandName(matchedNames.group(2).trim());
                }
                if (matchedNames.group(1).contains("MAILING ADDRESS, IF DIFFERENT")) {
                    try {
                        String adr = matchedNames.group(2).trim();
                        adr = adr.replaceAll("<br>", "").trim();
                        //System.out.println("ADR: " + adr);
                        String[] adrLines = adr.split("\n");
                        String address = adrLines[0].trim().replaceAll("\\s+", " ") + "\n"
                                + adrLines[1].trim().replaceAll("\\s+", " ") + "\n"
                                + adrLines[3].trim().replaceAll("\\s+", " ") + " " + adrLines[4].trim().replaceAll("\\s+", "") + ", " + adrLines[5].trim().replaceAll("\\s+", "");
                        app.setMailingAddress(Address.tryParse(address));
                    } catch (Exception e) {
                        app.setMailingAddress(app.getApplicantAddress());
                    }
                    //System.out.println("MAIL ADR: " + adr);
                    //app.getLabel().setSerialNumber(matchedNames.group(2).trim());
                }
                if (matchedNames.group(1).contains("FANCIFUL NAME <I>(If any)</I>")) {
                    lbl.setFancifulName(matchedNames.group(2).trim());
                }
                if (matchedNames.group(1).contains("FORMULA")) {

                    if (matchedNames.group(2).trim().length() > 2) {
                        lbl.setFormula(matchedNames.group(2).trim());
                    } else {
                        lbl.setFormula("N/A");
                    }
                }
                if (matchedNames.group(1).contains("GRAPE VARIETAL(S) <I>(Wine Only)</I>")) {
                    if (lbl instanceof WineLabel) {
                        ((WineLabel) lbl).setGrapeVarietal(matchedNames.group(2).trim());
                    }
                }
                if (matchedNames.group(1).contains("WINE APPELLATION <I>(If on label)</I>")) {
                    if (lbl instanceof WineLabel) {
                        ((WineLabel) lbl).setWineAppelation(matchedNames.group(2).trim());
                    }
                }
                if (matchedNames.group(1).contains("PHONE NUMBER")) {
                    app.setPhoneNumber(matchedNames.group(2).trim());
                }
                if (matchedNames.group(1).contains("EMAIL ADDRESS")) {
                    app.setEmailAddress(matchedNames.group(2).trim().replace("&nbsp;", "").trim());
                }
                if (matchedNames.group(1).contains("DATE OF APPLICATION")) {
                    DateFormat df = new SimpleDateFormat("mm/dd/yyyy");
                    try {
                        df.parse(matchedNames.group(2).trim());
                        app.setApplicationDate(new Date(df.parse(matchedNames.group(2).trim()).getTime()));
                    } catch (ParseException ex) {
                        Logger.getLogger(FormImporter.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                if (matchedNames.group(1).contains("PRINT NAME OF APPLICANT OR AUTHORIZED AGENT")) {
                    String[] nameparts = matchedNames.group(2).trim().split("\n");
                    app.setApplicant(new Manufacturer(nameparts[0].trim(), nameparts[1].trim(), "unknown", "unknown"));
                }
            }

            app.setLabel(lbl);
            return app;
        }

        return null;
    }
    
    public static class ImageData
    {
        public static int TYPE_PNG=0;
        public static int TYPE_JPEG=1;
        private int type;
        private byte[] bytes;
        private ImageData(byte[] data, int type)
        {
            this.bytes = data;
            this.type = type;
        }

        public int getType() {
            return type;
        }

        public byte[] getBytes() {
            return bytes;
        }        
    }
}
