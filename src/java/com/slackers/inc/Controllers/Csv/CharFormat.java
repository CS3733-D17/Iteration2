/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.Controllers.Csv;

/**
 *
 * @author Fabio Borges
 *
 * Used for exporting CSV with a custom delimiter
 */
public class CharFormat implements IDelimiterFormat
{
    // Custom delimited selected by user
    private String delim;
    
    public CharFormat(String delim)
    {
        this.delim = delim;
    }
    
    // Getters
     @Override
    public String getDelimiter() {
        return delim;
    }

    @Override
    public String getMimeType() {
        return "text/char";
    }
}
