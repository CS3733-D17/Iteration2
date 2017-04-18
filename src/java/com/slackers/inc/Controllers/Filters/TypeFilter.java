package com.slackers.inc.Controllers.Filters;


import com.slackers.inc.database.entities.Label;

/**
 * Created by jestrada on 4/2/17.
 */
public class TypeFilter implements ExactFilter {



    Label.BeverageType value;

    public TypeFilter(Label.BeverageType value){
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