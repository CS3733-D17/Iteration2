package java.com.slackers.inc.Controllers.Filters;

import java.com.slackers.inc.database.entities.Label;

/**
 * Created by Matt on 4/2/2017.
 */
public class RepresentativeIdNumberFilter implements Filter {

    String id;

    public RepresentativeIdNumberFilter(String id){
        this.id = id;
    }

    public Label preApply(Label label){
        label.setRepresentativeIdNumber(id);
        return label;
    }

    @Override
    public String getColumn() {
        return "representativeIdNumber";
    }


}
