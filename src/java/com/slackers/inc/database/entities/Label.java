/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.database.entities;

import com.slackers.inc.Controllers.LabelApplicationController;
import com.slackers.inc.database.IEntity;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 *
 *     Represents a label. Classes such as BeerLabel, DistilledLabel
 *     extend this class to represent their respective beverages.
 */
public class Label implements IEntity{

    // Label class is held in the "LABELS" table in our database
    private static final String TABLE = "LABELS";

    // Where this beverage came from. Domestic, imported or unknown.
    public static enum BeverageSource
    {
        DOMESTIC,
        IMPORTED,
        UNKNOWN;
    }

    // What type of beverage this is.
    public static enum BeverageType
    {
        WINE,
        BEER,
        DISTILLED,
        UNKNOWN;
    }

    // The ID of this label in the database.
    private long labelId;
    private double alcoholContent;
    // is the label approved?
    private boolean isAccepted;

    // All of this is information about the label.
    private String representativeIdNumber;
    private String plantNumber;
    private BeverageSource productSource;
    private BeverageType productType;
    private String brandName;
    private String serialNumber;
    private String fancifulName;
    private String formula;
    private String generalInfo;
    private String TBB_CT;
    private String TBB_OR;
    private String labelImageType;
    private byte[] labelImage;

    // Contains the information about the label approval.
    private ApplicationApproval approval;
    
    private boolean pullImageOut;
    
    public Label()
    {
        this.alcoholContent = -1;
        this.labelId = 0;
        this.isAccepted = false;
        this.plantNumber = "";
        this.productSource = BeverageSource.UNKNOWN;
        this.productType = BeverageType.UNKNOWN;
        this.representativeIdNumber = "";
        this.brandName = "";
        this.serialNumber = "";
        this.approval = new ApplicationApproval();
        this.fancifulName = "";
        this.formula = "";
        this.generalInfo = "";
        this.TBB_CT = "";        
        this.TBB_OR = "";
        this.labelImage=null;
        this.labelImageType = "";
        this.pullImageOut = false;
    }
    
    public ApplicationApproval getApproval() {
        return approval;
    }

    public void setApproval(ApplicationApproval approval) {
        if (approval == null)
        {
            this.setIsAccepted(false);
            this.approval = new ApplicationApproval();
        }
        else
        {
            this.setIsAccepted(true);
            this.approval = approval;
        }
    }  

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getLabelImageType() {
        return labelImageType;
    }

    public void setLabelImageType(String labelImageType) {
        this.labelImageType = labelImageType;
    }

    public byte[] getLabelImage() {
        return labelImage;
    }

    public void setLabelImage(byte[] labelImage) {
        this.labelImage = labelImage;
    }

    public void setPullImageOut(boolean pullImageOut) {
        this.pullImageOut = pullImageOut;
    }

    public double getAlcoholContent() {
        return alcoholContent;
    }

    public void setAlcoholContent(double alchoholContent) {
        this.alcoholContent = alchoholContent;
    }

    public long getLabelId() {
        return labelId;
    }

    public void setLabelId(long labelId) {
        this.labelId = labelId;
    }

    public boolean isIsAccepted() {
        return isAccepted;
    }

    public void setIsAccepted(boolean isAccepted) {
        this.isAccepted = isAccepted;
    }

    public String getRepresentativeIdNumber() {
        return representativeIdNumber;
    }

    public void setRepresentativeIdNumber(String representativeIdNumber) {
        this.representativeIdNumber = representativeIdNumber;
    }

    public String getPlantNumber() {
        return plantNumber;
    }

    public void setPlantNumber(String plantNumber) {
        this.plantNumber = plantNumber;
    }

    public BeverageSource getProductSource() {
        return productSource;
    }

    public void setProductSource(BeverageSource productSource) {
        this.productSource = productSource;
    }

    public BeverageType getProductType() {
        return productType;
    }

    public void setProductType(BeverageType productType) {
        this.productType = productType;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public String getFancifulName() {
        return fancifulName;
    }

    public void setFancifulName(String fancifulName) {
        this.fancifulName = fancifulName;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getGeneralInfo() {
        return generalInfo;
    }

    public void setGeneralInfo(String generalInfo) {
        this.generalInfo = generalInfo;
    }
    

    @Override
    public String getTableName() {
        return TABLE;
    }

    @Override
    public Map<String, Object> getEntityValues() {
        Map<String,Object> values = new HashMap<>();  
        values.put("labelId", this.labelId);
        values.put("isAccepted", this.isAccepted);
        values.put("alchoholContent", this.alcoholContent);
        values.put("representativeIdNumber", this.representativeIdNumber);
        values.put("plantNumber", this.plantNumber);        
        values.put("productSource", this.productSource.name());
        values.put("productType", this.productType.name());
        values.put("brandName", this.brandName);
        values.put("TBB_CT", this.TBB_CT);
        values.put("TBB_OR", this.TBB_OR);
        values.put("serialNumber", this.serialNumber);
        values.put("fancifulName", this.fancifulName);
        values.put("formula", this.formula);
        values.put("generalInfo", this.generalInfo);
        values.put("labelImageType", this.labelImageType);
        if (this.labelImage!=null)
        {
            values.put("labelImage", this.labelImage);            
        }
        
        values.putAll(this.approval.getEntityValues()); // approval
        
        return values;
    }

    @Override
    public Map<String, Object> getUpdatableEntityValues() {
        Map<String,Object> values = new HashMap<>();  
        values.put("isAccepted", this.isAccepted);
        values.put("alchoholContent", this.alcoholContent);
        values.put("representativeIdNumber", this.representativeIdNumber);
        values.put("plantNumber", this.plantNumber);        
        values.put("productSource", this.productSource.name());
        values.put("productType", this.productType.name());
        values.put("brandName", this.brandName);
        values.put("serialNumber", this.serialNumber);
        values.put("TBB_CT", this.TBB_CT);
        values.put("TBB_OR", this.TBB_OR);
        values.put("fancifulName", this.fancifulName);
        values.put("formula", this.formula);
        values.put("generalInfo", this.generalInfo);
        
        values.put("labelImageType", this.labelImageType);
        if (this.labelImage!=null)
        {
            values.put("labelImage", this.labelImage);
        }
        values.putAll(this.approval.getEntityValues()); // approval
        return values;
    }

    @Override
    public void setEntityValues(Map<String, Object> values) {
        
        if (values.containsKey("labelId"))
        {
            this.labelId = (long) values.get("labelId");
        }
        if (values.containsKey("isAccepted"))
        {
            this.isAccepted = (Boolean) values.get("isAccepted");
        }
        if (values.containsKey("alchoholContent"))
        {
            this.alcoholContent = (double) values.get("alchoholContent");
        }        
        if (values.containsKey("representativeIdNumber"))
        {
            this.representativeIdNumber = (String) values.get("representativeIdNumber");
        }
        if (values.containsKey("plantNumber"))
        {
            this.plantNumber = (String) values.get("plantNumber");
        }
        if (values.containsKey("productSource"))
        {
            this.productSource = BeverageSource.valueOf((String)values.get("productSource"));
        }
        if (values.containsKey("productType"))
        {
            this.productType = BeverageType.valueOf((String)values.get("productType"));
        }
        if (values.containsKey("brandName"))
        {
            this.brandName = (String) values.get("brandName");
        }
        if (values.containsKey("serialNumber"))
        {
            this.serialNumber = (String) values.get("serialNumber");
        }
        if (values.containsKey("TBB_CT"))
        {
            this.TBB_CT = (String) values.get("TBB_CT");
        }
        if (values.containsKey("TBB_OR"))
        {
            this.TBB_OR = (String) values.get("TBB_OR");
        }
        if (values.containsKey("fancifulName"))
        {
            this.fancifulName = (String) values.get("fancifulName");
        }
        if (values.containsKey("formula"))
        {
            this.formula = (String) values.get("formula");
        }
        
        if (values.containsKey("labelImage"))
        {
            this.labelImage = (byte[]) values.get("labelImage");
        }
        if (values.containsKey("labelImageType"))
        {
            this.labelImageType = (String) values.get("labelImageType");
        }
        
        if (values.containsKey("generalInfo"))
        {
            this.generalInfo = (String) values.get("generalInfo");
        }
        
        this.approval.setEntityValues(values); // approval
        
    }

    @Override
    public Map<String, Class> getEntityNameTypePairs() {
        Map<String,Class> pairs = new HashMap<>();
        pairs.put("labelId", Long.class);
        pairs.put("isAccepted", Boolean.class);
        pairs.put("alchoholContent", Double.class);
        pairs.put("representativeIdNumber", String.class);
        pairs.put("plantNumber", String.class);        
        pairs.put("productSource", String.class);
        pairs.put("productType", String.class);
        pairs.put("brandName", String.class);
        pairs.put("serialNumber", String.class);
        pairs.put("fancifulName", String.class);
        pairs.put("formula", String.class);   
        pairs.put("TBB_CT", String.class); 
        pairs.put("TBB_OR", String.class);  
        pairs.put("generalInfo", String.class);
        pairs.put("labelImageType", String.class);
        if (this.pullImageOut)
        {
            pairs.put("labelImage", byte[].class);
        }
        pairs.putAll(this.approval.getEntityNameTypePairs());
        return pairs;
    }

    @Override
    public List<String> tableColumnCreationSettings() {
        List<String> cols = new LinkedList<>();
        cols.add("labelId bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)");
        cols.add("isAccepted boolean");
        cols.add("alchoholContent float");        
        cols.add("representativeIdNumber varchar(128)");
        cols.add("plantNumber varchar(128)");
        cols.add("productSource varchar(64)");        
        cols.add("productType varchar(64)");
        cols.add("brandName varchar(128)");
        cols.add("fancifulName varchar(256)");
        cols.add("formula varchar(1024)");
        cols.add("grapeVarietal varchar(128)");
        cols.add("wineAppelation varchar(128)");
        cols.add("generalInfo varchar(1024)");
        cols.add("serialNumber varchar(64)");
        cols.add("TBB_CT varchar(32)");
        cols.add("TBB_OR varchar(32)");
        cols.add("phLevel float");
        cols.add("vintage int");        
        cols.add("labelImage blob");
        cols.add("labelImageType varchar(64)");
        cols.addAll(this.approval.tableColumnCreationSettings());
        return cols;
    }

    @Override
    public String getPrimaryKeyName() {
        return "labelId";
    }

    @Override
    public Serializable getPrimaryKeyValue() {
        return this.labelId;
    }

    @Override
    public void setPrimaryKeyValue(Serializable value) {
        this.labelId = (long) value;
    }
    
    @Override
    public Label deepCopy() {
        Label label = new Label();
        label.setEntityValues(this.getEntityValues());
        return label;
    }

    @Override
    public String toString() {
        return "Label{" + "labelId=" + labelId + ", alcoholContent=" + alcoholContent + ", isAccepted=" + isAccepted + ", representativeIdNumber=" + representativeIdNumber + ", plantNumber=" + plantNumber + ", productSource=" + productSource + ", productType=" + productType + ", brandName=" + brandName + ", general=" + generalInfo+ ", labelimage=" + labelImageType +": "+ (labelImage!=null) + '}';
    }
    
    public String getTBB_CT() {
        return TBB_CT;
    }

    public void setTBB_CT(String TBB_CT) {
        this.TBB_CT = TBB_CT;
    }

    public String getTBB_OR() {
        return TBB_OR;
    }

    public void setTBB_OR(String TBB_OR) {
        this.TBB_OR = TBB_OR;
    }
    
}
