package com.slackers.inc.Controllers.Filters;

/**
 * Created by mattBoros on 4/16/17.
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
