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
 *     Range filter is used to search for attributes of a
 *     beverage that are between a min value and a max
 *     value. These min and max values could be ints,
 *     longs, doubles, or Strings.
 */
public interface RangeFilter extends Filter{
    // Returns the min value that is acceptable
    // for this search
    public Object getValueMin();
    // Returns the max value that is acceptable
    // for this search
    public Object getValueMax();
}
