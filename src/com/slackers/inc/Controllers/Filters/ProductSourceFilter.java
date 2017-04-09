package com.slackers.inc.Controllers.Filters;

import com.slackers.inc.database.entities.Label;

/**
 * Created by Matt on 4/2/2017.
 */
public class ProductSourceFilter  implements Filter {

    Label.BeverageSource source;

    public ProductSourceFilter(Label.BeverageSource source) {
        this.source = source;
    }

    public Label preApply(Label label) {
        label.setProductSource(source);
        return label;
    }

    @Override
    public String getColumn() {
        return "productSource";
    }
}
