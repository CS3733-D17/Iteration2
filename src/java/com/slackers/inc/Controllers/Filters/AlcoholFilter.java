package com.slackers.inc.Controllers.Filters;


import com.slackers.inc.database.entities.Label;
/**
 * Created by jestrada on 4/2/17.
 */
public class AlcoholFilter implements ExactFilter {

    double percent;

    public AlcoholFilter(double percent){
        this.percent = percent;
    }

    

    @Override
    public String getColumn() {
        return "alchoholContent";
    }

    @Override
    public Object getValue() {
        return percent;
    }

}
