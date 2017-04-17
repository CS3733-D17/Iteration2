package com.slackers.inc.Controllers.Filters;

import com.slackers.inc.database.entities.Label;

/**
 * Created by Matt on 4/2/2017.
 */
public class RepresentativeIdNumberRange implements RangeFilter {

    String id;

    public RepresentativeIdNumberRange(String id){
        this.id = id;
    }

    @Override
    public String getColumn() {
        return "representativeIdNumber";
    }

    @Override
    public Object getValueMin() {
       return id;
    }

    @Override
    public Object getValueMax() {
        return id + "z";
    }



}