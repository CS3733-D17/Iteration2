package java.com.slackers.inc.Controllers.Filters;


import java.com.slackers.inc.database.entities.Label;

/**
 * Created by jestrada on 4/2/17.
 */
public class TypeFilter implements Filter {



    Label.BeverageType value;

    public TypeFilter(Label.BeverageType value){
        this.value = value;
    }

    @Override
    public Label preApply(Label aFilter) {
        aFilter.setProductType(value);
        return aFilter;
    }

    @Override
    public String getColumn() {
        return "productType";
    }

}