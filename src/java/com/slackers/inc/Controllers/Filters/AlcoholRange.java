package com.slackers.inc.Controllers.Filters;

/**
 * Created by mattBoros on 4/16/17.
 *
 * Filter for searching for any beverages with
 * alcohol contents in the range of lo to hi
 */
public class AlcoholRange implements RangeFilter {

    double lo;
    double hi;

    public AlcoholRange(double lo, double hi){
        this.lo = lo;
        this.hi = hi;
    }

    @Override
    public Object getValueMin(){
        return lo;
    }
    
    @Override
    public Object getValueMax(){
        return hi;
    }
    
    @Override
    public String getColumn() {
        return "alchoholContent";
    }

}
