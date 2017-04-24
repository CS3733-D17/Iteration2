package com.slackers.inc.Controllers.Filters;


import com.slackers.inc.database.entities.Label;

/**
 * Created by mattBoros on 4/16/2017.
 */
public class IDRange implements RangeFilter {

    long lo;
    long hi;

    public IDRange(long lo, long hi){
        this.lo = lo;
        this.hi = hi;
    }
    
    @Override
    public String getColumn() {
        return "labelId";
    }

    @Override
    public Object getValueMin() {
        return lo;
    }

    @Override
    public Object getValueMax() {
        return hi;
    }

}
