package com.slackers.inc.Controllers.Filters;

import com.slackers.inc.database.entities.Label;
import com.slackers.inc.database.entities.WineLabel;

/**
 * Created by Matt on 4/2/2017.
 *
 * Used to find beverages with a particular vintage year
 */
public class VintageFilter implements ExactFilter {

    int vintage;

    public VintageFilter(int vintage){
        this.vintage = vintage;
    }

    public Label preApply(Label label){
        if (label instanceof WineLabel)
            ((WineLabel)label).setVintage(vintage);
        return label;
    }

    @Override
    public String getColumn() {
        return "vintage";
    }

    @Override
    public Object getValue() {
        return this.vintage;
    }

}
