package com.slackers.inc.Controllers.Filters;


import com.slackers.inc.database.entities.Label;

/**
 * Created by jestrada on 4/2/17.
 */
public class AcceptedFilter implements ExactFilter_old {

    boolean value;

    public AcceptedFilter(boolean value){
        this.value = value;
    }

    @Override
    public Label preApply(Label aFilter) {
        aFilter.setIsAccepted(value);
        return aFilter;
    }

    @Override
    public String getColumn() {
        return "isAccepted";
    }

}