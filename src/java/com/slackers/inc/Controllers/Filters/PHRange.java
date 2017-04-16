/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.Controllers.Filters;

/**
 *
 * @author Matt
 */
public class PHRange implements RangeFilter {

    double lo;
    double hi;
    
    public PHRange(double lo, double hi){
        this.lo = lo;
        this.hi = hi;
    }
    
    @Override
    public Object getValueMin() {
        return lo;
    }

    @Override
    public Object getValueMax() {
        return hi;
    }

    @Override
    public String getColumn() {
        return "phLevel";
    }
    
}
