package com.slackers.inc.Controllers.Filters;


import com.slackers.inc.database.entities.Label;
/**
 * Created by jestrada on 4/2/17.
 *
 * Filter to find labels with the exact amount of alcohol
 as the "ct" attribute given to the constructor
 */
public class CTFilter implements ExactFilter {

    String ct;

    public CTFilter(String ct){
        this.ct = ct;
    }

    

    @Override
    public String getColumn() {
        return "TBB_CT";
    }

    @Override
    public Object getValue() {
        return ct;
    }

}
