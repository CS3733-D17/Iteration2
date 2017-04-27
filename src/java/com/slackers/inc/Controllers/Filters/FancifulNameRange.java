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
public class FancifulNameRange implements RangeFilter{

    private String fancy;
    
    public FancifulNameRange(String fanciful)
    {
        this.fancy = fanciful;
    }
    
    @Override
    public Object getValueMin() {
        return this.fancy;
    }

    @Override
    public Object getValueMax() {
        return this.fancy+"z";
    }

    @Override
    public String getColumn() {
        return "fancifulName";
    }

    
    
}
