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
public class BrandNameRange implements RangeFilter{

    private String brand;
    
    public BrandNameRange(String brand)
    {
        this.brand = brand;
    }
    
    @Override
    public Object getValueMin() {
        return this.brand;
    }

    @Override
    public Object getValueMax() {
        return this.brand+"z";
    }

    @Override
    public String getColumn() {
        return "brandName";
    }

    
    
}
