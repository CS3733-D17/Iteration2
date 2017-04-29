package com.slackers.inc.Controllers.Filters;


import com.slackers.inc.database.entities.Label;

/**
 * Created by jestrada on 4/2/17.
 *
 * Used to find beverages with an exact type.
 */
public class TypeFilter implements ExactFilter {


    String value;
    //Label.BeverageType value;

    public TypeFilter(String value){
        this.value = value;
    }



    @Override
    public String getColumn() {
        return "productType";
    }

    @Override
    public Object getValue() {
        return this.value;
    }

}