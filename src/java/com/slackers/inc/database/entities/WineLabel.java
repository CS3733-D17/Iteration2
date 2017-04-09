/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.database.entities;

import java.util.Map;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
public class WineLabel extends Label{

    private int vintage;
    private double phLevel;
    private String grapeVarietal;
    private String wineAppelation;
    
    public WineLabel() {
        super();
        this.vintage = 0;
        this.phLevel = 0;        
        this.grapeVarietal = "";
        this.wineAppelation = "";
        super.setProductType(BeverageType.WINE);
    }

    public int getVintage() {
        return vintage;
    }

    public void setVintage(int vintage) {
        this.vintage = vintage;
    }

    public double getPhLevel() {
        return phLevel;
    }

    public void setPhLevel(double phLevel) {
        this.phLevel = phLevel;
    }
    
    
    public String getGrapeVarietal() {
        return grapeVarietal;
    }

    public void setGrapeVarietal(String grapeVarietal) {
        this.grapeVarietal = grapeVarietal;
    }

    public String getWineAppelation() {
        return wineAppelation;
    }

    public void setWineAppelation(String wineAppelation) {
        this.wineAppelation = wineAppelation;
    }

    @Override
    public WineLabel deepCopy() {
        WineLabel w = new WineLabel();
        w.setEntityValues(this.getEntityValues());
        return w;
    }

    @Override
    public Map<String, Class> getEntityNameTypePairs() {
        Map<String,Class> pairs = super.getEntityNameTypePairs();
        pairs.put("phLevel", Double.class);
        pairs.put("vintage", Integer.class);

        pairs.put("grapeVarietal", String.class);
        pairs.put("wineAppelation", String.class);        
        return pairs;
    }

    @Override
    public void setEntityValues(Map<String, Object> values) {
        super.setEntityValues(values);
        if (values.containsKey("phLevel"))
        {
            this.phLevel = (double) values.get("phLevel");
        }
        if (values.containsKey("vintage"))
        {
            this.vintage = (int) values.get("vintage");
        }
        if (values.containsKey("grapeVarietal"))
        {
            this.grapeVarietal = (String) values.get("grapeVarietal");
        }
        if (values.containsKey("wineAppelation"))
        {
            this.wineAppelation = (String) values.get("wineAppelation");
        }
    }

    @Override
    public Map<String, Object> getUpdatableEntityValues() {
        Map<String,Object> values = super.getUpdatableEntityValues();
        values.put("phLevel", this.phLevel);
        values.put("vintage", this.vintage);
        
        values.put("grapeVarietal", this.grapeVarietal);
        values.put("wineAppelation", this.wineAppelation);
        return values;
    }

    @Override
    public Map<String, Object> getEntityValues() {
        Map<String,Object> values = super.getEntityValues();
        values.put("phLevel", this.phLevel);
        values.put("vintage", this.vintage);
        
        values.put("grapeVarietal", this.grapeVarietal);
        values.put("wineAppelation", this.wineAppelation);
        return values;
    }
    
    
    
}
