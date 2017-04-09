/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.database;

/**
 *
 * @author Fabio Borges
 */
public class RealLabelImage implements ILabelImage
{
    private String fileName;
    
    public RealLabelImage(String fileName)
    {
        this.fileName = fileName;
        loadImage(fileName);
    }
    
    public String getFileName()
    { return fileName; }
    
    public void display()
    {
        
    }
    
    private void loadImage(String fileName)
    {
        
    }
}
