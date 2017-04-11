/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.database;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;

/**
 *
 * @author Fabio Borges
 */
public class RealLabelImage implements ILabelImage {
    private String fileName;
    private BufferedImage image;
    
    public RealLabelImage(String fileName) throws IOException {
        this.fileName = fileName;
        loadImage(fileName);
    }
    
    public String getFileName(){
        return fileName;
    }

    @Override
    public BufferedImage display() {
        return image;
    }
    
    private void loadImage(String fileName) throws IOException {
        image = ImageIO.read(new java.io.File("/images/" + fileName));
    }
}
