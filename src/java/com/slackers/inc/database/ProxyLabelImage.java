/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.database;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 *
 * @author Fabio Borges
 */
public class ProxyLabelImage implements ILabelImage {
    private RealLabelImage realLabel;
    private String fileName;

    public ProxyLabelImage(String fileName) {
        this.fileName = fileName;
    }

    public String getFileName() throws IOException{
        return fileName;
    }

    @Override
    public BufferedImage display() {
        if(realLabel == null)
            try {
                realLabel = new RealLabelImage(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        return realLabel.display();
    }

}
