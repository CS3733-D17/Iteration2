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
 *     This filter is used to find an exact match in the
 *     database. Any class that wants to find an exact
 *     match should implement this interface
 */
public interface ExactFilter extends Filter{
    /**
     * @return The exact value that a beverage has to match
     * in order for it to come up in a search
     */
    public Object getValue();
}
