/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.database;

import java.io.FileInputStream;
import java.io.IOException;

/**
 *
 * @author Fabio Borges
 */
public class RealLabelImage implements ILabelImage {
    private FileInputStream fileName;
    
    public RealLabelImage(FileInputStream fileName) {
        this.fileName = fileName;
        loadImage(fileName);
    }
    
    public String getFileName() throws IOException{
        return fileName.getFD().toString();
    }

    @Override
    public void display() {
        
    }
    
    private void loadImage(FileInputStream fileName) {
        
    }
}
