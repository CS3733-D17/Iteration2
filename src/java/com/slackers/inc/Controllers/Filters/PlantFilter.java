package java.com.slackers.inc.Controllers.Filters;


import java.com.slackers.inc.database.entities.Label;

/**
 * Created by jestrada on 4/2/17.
 */
public class PlantFilter implements Filter {

    String value;

    public PlantFilter(String value){
        this.value = value;
    }

    @Override
    public Label preApply(Label aFilter) {
        aFilter.setPlantNumber(value);
        return aFilter;
    }

    @Override
    public String getColumn() {
        return "plantNumber";
    }

}