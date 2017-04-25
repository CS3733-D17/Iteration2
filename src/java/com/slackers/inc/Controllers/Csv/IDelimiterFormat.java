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
 *     Classes implement this interface if they are a way
 *     to export CSV files with a certain delimiter. This
 *     is used by DelimitedWriter.java
 */
public interface IDelimiterFormat {
    public String getDelimiter();
    public String getMimeType();
}
