package com.slackers.inc.Controllers.Csv;

import com.slackers.inc.database.DerbyConnection;
import com.slackers.inc.database.entities.Label;
import com.slackers.inc.database.entities.LabelApplication;
import com.slackers.inc.database.entities.Manufacturer;
import com.slackers.inc.database.entities.UsEmployee;
import com.slackers.inc.database.entities.WineLabel;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
public class CsvApplicationImporter implements Runnable {
    private static final String MEM_FILE = "importer.mem";
    private static final Pattern ent = Pattern.compile("\\\"(.*?)\\\"");
    private static final SimpleDateFormat DATE_PARSER = new SimpleDateFormat("MM/dd/yyyy");
    private static final UsEmployee EMPLOYEE_BOT = (UsEmployee) new UsEmployee("Importer", "Bot", "agent-bot@superslackers.com", "agent-bot").setBot();
    private static final Manufacturer MANUFACTURER_BOT = (Manufacturer) new Manufacturer("Submitter", "Bot", "submitter-bot@superslackers.com", "submitter-bot").setBot();
    private BufferedReader file;
    private String currentFile;
    private List<String> files;
    private long lineNumber;
    private long targetStart;
    private long totalLines;
    private String[] headers;
    private LabelApplication application;
    private ApplicationConsumer consumer;
    private volatile boolean shouldRun;
    private UsEmployee approver;
    private Manufacturer submitter;

    private CsvApplicationImporter(String... filenames) {
        this.files = new LinkedList<>(Arrays.asList(filenames));
        this.consumer = null;
        this.shouldRun = false;
        this.lineNumber = 0;
        this.approver = EMPLOYEE_BOT;
        this.submitter = MANUFACTURER_BOT;
        this.targetStart = 0;
        this.totalLines = 0;
    }

    private CsvApplicationImporter() throws FileNotFoundException, IOException {
        this.consumer = null;
        this.files = new LinkedList<>();
        this.shouldRun = false;
        this.lineNumber = 0;
        this.targetStart = 0;
        this.totalLines = 0;
        this.approver = EMPLOYEE_BOT;
        this.submitter = MANUFACTURER_BOT;
        loadFiles();
    }

    private void loadFiles() {
        this.files = new LinkedList<>();
        this.lineNumber = 0;
        this.targetStart = 0;
        this.totalLines = 0;
        if (Files.exists(Paths.get(MEM_FILE))) {
            try {
                BufferedReader r = new BufferedReader(new FileReader(MEM_FILE));
                this.targetStart = Long.parseLong(r.readLine());
                String line;
                while ((line = r.readLine()) != null) {
                    this.files.add(line);
                }
            } catch (Exception ex) {
                Logger.getLogger(CsvApplicationImporter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try {
            List<Path> list = Files.list(Paths.get("")).filter((f) -> f.toString().endsWith(".csv")).collect(Collectors.toList());//.filter((f) -> f.endsWith(".csv"))
            for (Path p : list) {
                if (!this.files.contains(p.toAbsolutePath().toString())) {
                    this.files.add(p.toAbsolutePath().toString());
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(CsvApplicationImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setStartIndex(int index) {
        this.targetStart = index;
    }

    public long getTotalLines() {
        return this.totalLines;
    }

    public String getCurrentFile() {
        return this.currentFile;
    }

    private boolean initNext() throws IOException {
        if (this.files.isEmpty()) {
            return false;
        }
        this.lineNumber = 0;
        String nextName = this.files.get(0);
        currentFile = nextName;
        file = new BufferedReader(new FileReader(nextName));
        try
        {
            this.totalLines = Files.lines(Paths.get(nextName)).count();
        }
        catch (Exception e){this.totalLines = 500000;}
        String header = file.readLine();
        headers = header.split(",");
        while (this.lineNumber < this.targetStart) {
            this.skipEntry();
        }
        this.targetStart = 0;
        return true;
    }

    public void stop() {
        this.shouldRun = false;
    }

    private void saveProgress() {
        boolean delete = false;
        try (FileWriter w = new FileWriter(MEM_FILE)) {
            if (this.currentFile != null) {
                w.write(Long.toString(this.lineNumber) + "\n");
                w.write(this.currentFile + "\n");
                for (String s : this.files) {
                    w.write(s + "\n");
                }
            } else {
                delete = true;
            }
            w.flush();
        } catch (IOException ex) {
            Logger.getLogger(CsvApplicationImporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (delete) {
            try {
                Files.delete(Paths.get(MEM_FILE));
            } catch (IOException ex) {
                Logger.getLogger(CsvApplicationImporter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void advanceToMark(int mark) throws IOException {
        if (mark < lineNumber) {
            return;
        }
        while (lineNumber < mark) {
            skipEntry();
        }
    }

    public void skipEntry() throws IOException {
        file.readLine();
        lineNumber++;
    }

    public String doEntry() throws IOException {
        String line = file.readLine();

        if (line == null) {
            this.file.close();
            for (int i = 0; i < 5; i++) {
                try {
                    Files.move(Paths.get(this.currentFile), Paths.get(this.currentFile + ".finished"), REPLACE_EXISTING);
                } catch (Exception e) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        break;
                    }

                }
            }

            this.currentFile = null;
            return null;
        }
        line = this.sanitizeLine(line);
        lineNumber++;

        this.application = new LabelApplication();

        this.application.setApplicant(
                this.submitter);

        this.application.setEmailAddress(
                this.submitter.getEmail());

        this.application.setPhoneNumber(
                "555-555-5555");
        String[] linArr = line.split(",");
        String ttbId = "";
        boolean wasElectronic = false;
        for (int i = 0;
                i < linArr.length && i < headers.length;
                i++) {

            String hName = headers[i].toUpperCase();
            if (linArr[i].equals(".")) {
                linArr[i] = "";
            }
            linArr[i] = linArr[i].replaceAll("@@@@", ",");
            if (hName.contains("REP_ID")) {
                this.application.setRepresentativeId(linArr[i]);
            } else if (hName.contains("CFM_APPL_ID")) {
                ttbId = linArr[i];
            } else if (hName.contains("CLASS_TYPE_CODE")) {
                this.application.setTBB_CT(linArr[i]);
            } else if (hName.contains("ORIGIN_CODE")) {
                this.application.setTBB_OR(linArr[i]);
            } else if (hName.contains("PERMIT_ID")) {
                this.application.getLabel().setPlantNumber(linArr[i]);
            } else if (hName.contains("SOURCE_OF_PRODUCT")) {
                if (linArr[i].toLowerCase().contains("imported")) {
                    this.application.getLabel().setProductSource(Label.BeverageSource.IMPORTED);
                } else {
                    this.application.getLabel().setProductSource(Label.BeverageSource.DOMESTIC);
                }
            } else if (hName.contains("SERIAL_NUM")) {
                this.application.getLabel().setSerialNumber(linArr[i]);
            } else if (hName.contains("PRODUCT_TYPE")) {
                String type = linArr[i].toLowerCase();
                if (type.contains("wine")) {
                    this.application.setLabelType(Label.BeverageType.WINE, false);
                    this.application.getLabel().setProductType(Label.BeverageType.WINE);
                } else if (type.contains("distilled")) {
                    this.application.setLabelType(Label.BeverageType.DISTILLED, false);
                    this.application.getLabel().setProductType(Label.BeverageType.DISTILLED);
                } else {
                    this.application.setLabelType(Label.BeverageType.BEER, false);
                    this.application.getLabel().setProductType(Label.BeverageType.BEER);
                }
            } else if (hName.contains("PRODUCT_NAME")) {
                this.application.getLabel().setBrandName(linArr[i]);
            } else if (hName.contains("FANCIFUL_NAME")) {
                this.application.getLabel().setFancifulName(linArr[i]);
            } else if (hName.contains("PERMIT_NAME")) {
                this.application.getApplicantAddress().setLine1(linArr[i]);
            } else if (hName.contains("PERMIT_FRST_STRT_ADDR")) {
                this.application.getApplicantAddress().setLine2(linArr[i]);
            } else if (hName.contains("PERMIT_CITY_ADDR")) {
                this.application.getApplicantAddress().setCity(linArr[i]);
            } else if (hName.contains("PERMIT_STATE_ADDR")) {
                this.application.getApplicantAddress().setState(linArr[i]);
            } else if (hName.contains("RECEIVED_CODE")) {
                if (linArr[i].equalsIgnoreCase("Electronic Submission"))
                {
                    wasElectronic = true;                    
                }
            } else if (hName.contains("PERMIT_ZIP_ADDR")) {
                if (linArr[i].length() > 0) {
                    try {
                        this.application.getApplicantAddress().setZipCode(Integer.parseInt(linArr[i]));
                    } catch (Exception e) {
                    }
                }
            } else if (hName.contains("VARTL_NAME")) {
                if (this.application.getLabel() instanceof WineLabel) {
                    ((WineLabel) this.application.getLabel()).setGrapeVarietal(linArr[i]);
                }
            } else if (hName.contains("APPELLATION_DESC")) {
                if (this.application.getLabel() instanceof WineLabel) {
                    ((WineLabel) this.application.getLabel()).setWineAppelation(linArr[i]);
                }
            } else if (hName.contains("VINTAGE")) {
                if (this.application.getLabel() instanceof WineLabel) {
                    try {
                        ((WineLabel) this.application.getLabel()).setVintage(Integer.parseInt(linArr[i]));
                    } catch (Exception e) {
                    }
                }
            } else if (hName.contains("NET_CONTENTS")) {
                this.application.getLabel().setFormula(linArr[i]);
            } else if (hName.contains("ALCOHOL_PCT")) {
                if (linArr[i].length() > 0) {
                    try {
                        this.application.getLabel().setAlcoholContent(Double.parseDouble(linArr[i]));
                    } catch (Exception e) {
                    }
                }
            } else if (hName.contains("ISSUED_DATE")) {
                try {
                    Date parsed = DATE_PARSER.parse(linArr[i]);
                    this.application.setApplicationDate(new java.sql.Date(parsed.getTime()));
                } catch (Exception e) {
                }
            } else if (hName.contains("STATUS")) {
                if (linArr[i].toLowerCase().contains("approved")) {
                    this.application.setStatus(LabelApplication.ApplicationStatus.APPROVED);
                } else {
                    this.application.setStatus(LabelApplication.ApplicationStatus.REJECTED);
                }
            }
        }

        if (this.consumer!= null) {
            this.application.addApplicationType(LabelApplication.ApplicationType.NEW, null);
            if (wasElectronic)
            {
                this.application.getLabel().setLabelImageType("image/ttbId");
                this.application.getLabel().setLabelImage(ttbId.getBytes(StandardCharsets.US_ASCII));
            }
            else
            {
                this.application.getLabel().setLabelImageType("urlAbsolute");
                this.application.getLabel().setLabelImage("http://www.wellesleysocietyofartists.org/wp-content/uploads/2015/11/image-not-found.jpg".getBytes(StandardCharsets.US_ASCII));
            }
            this.consumer.consume(this.application, this);
        }
        return line;
    }

    private String sanitizeLine(String line) {
        line = line.replaceAll("\"+", "\"");
        line = line.replaceAll("[\\(\\)\\*\\^\\$\\[\\]\\+\\?\\{\\}\\\\]", "");
        Matcher m = ent.matcher(line);
        while (m.find())
        {
            String res = m.group(1);
            line = line.replaceAll(res, res.replace(",", "@@@@"));
        }
        line = line.replaceAll("\\\"([^,]+?)(,?)([^,]+?)\\\"", "$1$3");
        line = line.replaceAll("\"", "").replaceAll("\\(", "").replaceAll("\\)", "");
        return line;
    }

    public long getLineNumber() {
        return lineNumber;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setConsumer(ApplicationConsumer consumer) {
        this.consumer = consumer;
    }

    public UsEmployee getApprover() {
        return approver;
    }

    public void setApprover(UsEmployee approver) {
        this.approver = approver;
    }

    public Manufacturer getSubmitter() {
        return submitter;
    }

    public void setSubmitter(Manufacturer submitter) {
        this.submitter = submitter;
    }

    public boolean isRunning() {
        return this.shouldRun;
    }

    public void runAsync() {
        if (!this.shouldRun) {
            this.shouldRun = true;
            this.loadFiles();
            DerbyConnection.getInstance().setAutoCommit(false);
            Thread t = new Thread(this);
            t.start();
        }
    }

    @Override
    public void run() {
        try {
            while (this.initNext() && this.shouldRun) {
                try {
                    while (this.shouldRun && this.doEntry() != null);
                } catch (IOException ex) {
                    Logger.getLogger(CsvApplicationImporter.class
                            .getName()).log(Level.SEVERE, null, ex);
                }
                this.files.remove(0);

            }
        } catch (IOException ex) {
            Logger.getLogger(CsvApplicationImporter.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        DerbyConnection.getInstance().setAutoCommit(true);
        DerbyConnection.getInstance().commitChanges();
        this.saveProgress();

    }

    public static interface ApplicationConsumer {

        public void consume(LabelApplication app, CsvApplicationImporter importer);
    }

    public static CsvApplicationImporter getInstance() {
        return Helper.instance;

    }

    private static class Helper {

        private static CsvApplicationImporter instance;

        static {
            try {
                instance = new CsvApplicationImporter();
            } catch (IOException ex) {
                instance = null;
                Logger.getLogger(CsvApplicationImporter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
