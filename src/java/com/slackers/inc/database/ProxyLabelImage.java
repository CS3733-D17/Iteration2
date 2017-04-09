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
public class ProxyLabelImage implements ILabelImage {
    private RealLabelImage realLabel;
    private FileInputStream fileName;

    public ProxyLabelImage(FileInputStream fileName) {

        this.fileName = fileName;
    }

    public String getFileName() throws IOException{
        return fileName.getFD().toString();
    }

    // Load the real image if it has not been loaded and display that image
    @Override
    public void display() {
        if(realLabel == null)
            realLabel = new RealLabelImage(fileName);
        realLabel.display();
    }

}
