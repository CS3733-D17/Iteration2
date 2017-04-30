/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.Controllers.Filters;

import java.util.Date;

/**
 *
 * @author jestrada
 */
public class DateFilter implements ExactFilter {
    
    Date date;

    @Override
    public Object getValue() {
        return this.date;
    }

    @Override
    public String getColumn() {
        return "date";
    }
    
}
