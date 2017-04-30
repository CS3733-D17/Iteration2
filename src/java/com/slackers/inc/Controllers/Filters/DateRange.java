/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.Controllers.Filters;

import java.util.Date;

/**
 *
 * @author jestrada
 */
public class DateRange implements RangeFilter{
    Date min;
    Date hi;
    
    public DateRange(Date min, Date hi){
        this.min = min;
        this.hi = hi;
    }

    @Override
    public Object getValueMin() {
        return this.min;
    }

    @Override
    public Object getValueMax() {
        return this.hi;
    }

    @Override
    public String getColumn() {
        return "date";
    }
    
}
