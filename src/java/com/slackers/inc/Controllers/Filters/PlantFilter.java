package com.slackers.inc.Controllers.Filters;


import com.slackers.inc.database.entities.Label;

/**
 * Created by jestrada on 4/2/17.
 */
public class PlantFilter implements ExactFilter_old {

    String value;

    public PlantFilter(String value){
        this.value = value;
    }

    @Override
    public Label preApply(Label aFilter) {
        aFilter.setPlantNumber(value);
        return aFilter;
    }

    @Override
    public String getColumn() {
        return "plantNumber";
    }

}