/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.database.entities;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
public class BeerLabel extends Label{

    public BeerLabel() {
        super();
        super.setProductType(Label.BeverageType.BEER);
    }    
}
