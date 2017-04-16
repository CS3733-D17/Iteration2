package com.slackers.inc.Controllers.Filters;

import com.slackers.inc.database.entities.Label;
import com.slackers.inc.database.entities.WineLabel;

/**
 * Created by Matt on 4/2/2017.
 */
public class VintageRange implements RangeFilter {

    int lo;
    int hi;

    public VintageRange(int lo, int hi){
        this.lo = lo;
        this.hi = hi;
    }

   

    @Override
    public String getColumn() {
        return "vintage";
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
