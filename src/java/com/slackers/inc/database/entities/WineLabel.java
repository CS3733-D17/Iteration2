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
    
    public WineLabel() {
        super();
        this.vintage = 0;
        this.phLevel = 0;
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
    }

    @Override
    public Map<String, Object> getUpdatableEntityValues() {
        Map<String,Object> values = super.getUpdatableEntityValues();
        values.put("phLevel", this.phLevel);
        values.put("vintage", this.vintage);
        return values;
    }

    @Override
    public Map<String, Object> getEntityValues() {
        Map<String,Object> values = super.getEntityValues();
        values.put("phLevel", this.phLevel);
        values.put("vintage", this.vintage);
        return values;
    }
    
    
    
}
