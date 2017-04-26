/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.Controllers.Csv;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 *
 *     Used for exporting in CSV format with comma as the delimeter
 */
public class CsvFormat implements IDelimiterFormat{

    // Getters
    @Override
    public String getDelimiter() {
        return ",";
    }

    @Override
    public String getMimeType() {
        return "text/csv";
    }
    
}
