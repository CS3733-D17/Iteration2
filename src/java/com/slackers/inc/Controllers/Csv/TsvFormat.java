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
 * Used for exporting CSV format with tabs as the delimiter
 */
public class TsvFormat implements IDelimiterFormat
{
    // Getters
    @Override
    public String getDelimiter() {
        return "\t";
    }

    @Override
    public String getMimeType() {
        return "text/tsv";
    }
}
