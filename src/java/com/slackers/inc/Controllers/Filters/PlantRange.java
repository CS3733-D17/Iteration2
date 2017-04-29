package com.slackers.inc.Controllers.Filters;


import com.slackers.inc.database.entities.Label;

/**
 * Created by matt on 4/16/17.
 *
 * Used to find beverages wherer the name of the plant
 * contains a string.
 */
public class PlantRange implements RangeFilter {

    String value;

    public PlantRange(String value){
        this.value = value;
    }

    
    
    @Override
    public String getColumn() {
        return "plantNumber";
    }

    @Override
    public Object getValueMin() {
        return value;
    }

    @Override
    public Object getValueMax() {
        return value + "z";
    }

}