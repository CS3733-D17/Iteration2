package com.slackers.inc.Controllers.Filters;


import com.slackers.inc.database.entities.Label;
import com.slackers.inc.database.entities.WineLabel;

/**
 * Created by jestrada on 4/2/17.
 */
public class PHFilter implements ExactFilter {

    double id;

    public PHFilter(double id){
        this.id = id;
    }

    

    @Override
    public String getColumn() {
        return "phLevel";
    }

    @Override
    public Object getValue() {
        return this.id;
    }

}