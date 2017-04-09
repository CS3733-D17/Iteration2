package com.slackers.inc.Controllers.Filters;

import com.slackers.inc.database.entities.Label;

/**
 * Created by Matt on 4/2/2017.
 */
public class BrandNameFilter implements Filter {

    String name;

    public BrandNameFilter(String name){
        this.name = name;
    }

    public Label preApply(Label label){
        label.setBrandName(name);
        return label;
    }

    @Override
    public String getColumn() {
        return "brandName";
    }

}

