package com.slackers.inc.Controllers.Filters;


import com.slackers.inc.database.entities.Label;
/**
 * Created by jestrada on 4/2/17.
 */
public class AlcoholFilter implements Filter {

    double percent;

    public AlcoholFilter(double percent){
        this.percent = percent;
    }

    @Override
    public Label preApply(Label aFilter) {
        aFilter.setAlcoholContent(percent);
        return aFilter;
    }

    @Override
    public String getColumn() {
        return "alchoholContent";
    }

}
