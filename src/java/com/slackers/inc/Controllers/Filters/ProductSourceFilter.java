package com.slackers.inc.Controllers.Filters;

import com.slackers.inc.database.entities.Label;

/**
 * Created by Matt on 4/2/2017.
 */
public class ProductSourceFilter  implements ExactFilter {
    String source;
    //Label.BeverageSource source;

    public ProductSourceFilter(String source) {
        this.source = source;
    }

    

    @Override
    public String getColumn() {
        return "productSource";
    }

    @Override
    public Object getValue() {
        return this.source;
    }
}
