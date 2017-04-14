package com.slackers.inc.Controllers.Filters;


import com.slackers.inc.database.entities.Label;
import com.slackers.inc.database.entities.WineLabel;

/**
 * Created by jestrada on 4/2/17.
 */
public class PHFilter implements ExactFilter_old {

    double id;

    public PHFilter(double id){
        this.id = id;
    }

    @Override
    public Label preApply(Label aFilter) {
        if (aFilter instanceof WineLabel)
            ((WineLabel)aFilter).setPhLevel(id);
        return aFilter;
    }

    @Override
    public String getColumn() {
        return "phLevel";
    }

}