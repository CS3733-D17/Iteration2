/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.Controllers.Filters;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 *
 *     Filter for finding a beverage with a particular
 *     fragment of a name
 */
public class ValidImageFilter implements RangeFilter{

    
    public ValidImageFilter()
    {
    }
    
    @Override
    public Object getValueMin() {
        return "image/";
    }

    @Override
    public Object getValueMax() {
        return "image/z";
    }

    @Override
    public String getColumn() {
        return "labelImageType";
    }

    
    
}
