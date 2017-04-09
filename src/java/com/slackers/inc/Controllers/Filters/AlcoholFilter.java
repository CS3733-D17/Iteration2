package java.com.slackers.inc.Controllers.Filters;


import java.com.slackers.inc.database.entities.Label;

/**
 * Created by jestrada on 4/2/17.
 */
public class AlcoholFilter implements Filter {

    double percent;

    public AlcoholFilter(double percent){
        this.percent = percent;
    }

    @Override
    public Label preApply(Label aFilter) {
        aFilter.setAlcoholContent(percent);
        return aFilter;
    }

    @Override
    public String getColumn() {
        return "alcoholContent";
    }

}
