package com.slackers.inc.Controllers.Filters;


import com.slackers.inc.database.entities.Label;

/**
 * Created by jestrada on 4/2/17.
 */
public class OriginFilter implements ExactFilter {

    String value;

    public OriginFilter(String value){
        this.value = value;
    }

    

    @Override
    public String getColumn() {
        return "wineAppelation";
    }

    @Override
    public String getValue() {
        return this.value;
    }

}