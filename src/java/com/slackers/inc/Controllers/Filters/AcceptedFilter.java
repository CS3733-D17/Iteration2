package com.slackers.inc.Controllers.Filters;


import com.slackers.inc.database.entities.Label;

/**
 * Created by jestrada on 4/2/17.
 */
public class AcceptedFilter implements ExactFilter {

    boolean value;

    public AcceptedFilter(boolean value){
        this.value = value;
    }

    

    @Override
    public String getColumn() {
        return "isAccepted";
    }

    @Override
    public Object getValue() {
        return this.value;
    }

}