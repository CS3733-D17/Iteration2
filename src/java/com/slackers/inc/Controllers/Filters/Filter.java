package java.com.slackers.inc.Controllers.Filters;


import java.com.slackers.inc.database.entities.Label;

/**
 * Created by jestrada on 4/2/17.
 */

/**
 * When a filter is initialized, it is given the conditions
 * to satisfy the filter in the constructor. When a label
 * is passed to a filter in the preApply method, the filter
 * will add its condition to the label, and pass the label
 * back.
 */
public interface Filter {
    // Adds the characteristic held in the filter
    // to the label, and returns the label
    public Label preApply(Label aFilter);
    // Gets the name of the column that the filter
    // applies to
    public String getColumn();
}
