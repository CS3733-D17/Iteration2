package com.slackers.inc.Controllers.Filters;

import com.slackers.inc.database.entities.Label;

/**
 * Created by Matt on 4/2/2017.
 *
 * Used to search for a beverage with an exact
 * representative id number
 */
public class RepresentativeIdNumberFilter implements ExactFilter {

    String id;

    public RepresentativeIdNumberFilter(String id){
        this.id = id;
    }

    public Label preApply(Label label){
        label.setRepresentativeIdNumber(id);
        return label;
    }

    @Override
    public String getColumn() {
        return "representativeIdNumber";
    }

    @Override
    public Object getValue() {
        return id;
    }


}
