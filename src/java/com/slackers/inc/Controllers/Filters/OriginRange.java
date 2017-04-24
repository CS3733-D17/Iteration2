/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.Controllers.Filters;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
public class OriginRange implements RangeFilter{

    private String low;
    private String hi;
    
    public OriginRange(String low, String hi)
    {
        this.low = low;
        this.hi = hi;
    }
    
    @Override
    public Object getValueMin() {
        return this.low;
    }

    @Override
    public Object getValueMax() {
        return this.hi;
    }

    @Override
    public String getColumn() {
        return "wineAppelation";
    }

    
    
}
