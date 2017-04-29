package com.slackers.inc.Controllers.Filters;


import com.slackers.inc.database.entities.Label;

/**
 * Created by mattBoros on 4/16/
 *
 * Used to search for any beverage with an ID
 * in the range lo to hi, where lo and hi are
 * held as attributes in this class.
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
