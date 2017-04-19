package com.slackers.inc.Controllers.Filters;


import com.slackers.inc.database.entities.Label;

/**
 * Created by jestrada on 4/2/17.
 */
public class IDFilter implements ExactFilter {

    long id;

    public IDFilter(long id){
        this.id = id;
    }

    

    @Override
    public String getColumn() {
        return "labelId";
    }

    @Override
    public Object getValue() {
        return id;
    }

}
