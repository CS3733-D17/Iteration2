/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.Controllers.Filters;

import java.sql.Date;

/**
 *
 * @author jestrada
 */
public class DateFilter implements ExactFilter {
    
    Date date;
    
    public DateFilter(Date date){
        this.date = date;
    }

    @Override
    public Object getValue() {
        return this.date;
    }

    @Override
    public String getColumn() {
        return "approvalDate";
    }
    
}
