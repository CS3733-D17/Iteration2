package com.slackers.inc.Controllers.Filters;


import com.slackers.inc.database.entities.Label;
/**
 * Created by jestrada on 4/2/17.
 *
 * Filter to find labels with the exact amount of alcohol
 as the "or" attribute given to the constructor
 */
public class ORFilter implements ExactFilter {

    String or;

    public ORFilter(String or){
        this.or = or;
    }

    

    @Override
    public String getColumn() {
        return "TBB_OR";
    }

    @Override
    public Object getValue() {
        return or;
    }

}
