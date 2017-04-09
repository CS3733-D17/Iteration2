package java.com.slackers.inc.Controllers.Filters;


import java.com.slackers.inc.database.entities.Label;
import java.com.slackers.inc.database.entities.WineLabel;

/**
 * Created by jestrada on 4/2/17.
 */
public class PHFilter implements Filter {

    double id;

    public PHFilter(double id){
        this.id = id;
    }

    @Override
    public Label preApply(Label aFilter) {
        if (aFilter instanceof WineLabel)
            ((WineLabel)aFilter).setPhLevel(id);
        return aFilter;
    }

    @Override
    public String getColumn() {
        return "phLevel";
    }

}