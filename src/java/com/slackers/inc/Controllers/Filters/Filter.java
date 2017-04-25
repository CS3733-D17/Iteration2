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
 *     The interface for a filter for our search function.
 */
public interface Filter {
    /**
     * @return The column in our database that the value
     * is held.
     */
    public String getColumn();
}
