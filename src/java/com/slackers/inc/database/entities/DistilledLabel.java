/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.database.entities;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 *
 *     Represents the label for a distilled beverage.
 */
public class DistilledLabel extends Label{

    public DistilledLabel() {
        super();
        super.setProductType(Label.BeverageType.DISTILLED);
    }    
}
