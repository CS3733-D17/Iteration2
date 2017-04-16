package com.slackers.inc.Controllers;

import com.slackers.inc.database.DerbyConnection;
import com.slackers.inc.database.entities.Label;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import com.slackers.inc.Controllers.Filters.ExactFilter_old;
import com.slackers.inc.Controllers.Filters.Filter;

/**
 * @author Created by SrinuL on 4/1/17.
 */
public class SearchController {

    List<Filter> filters;
    DerbyConnection db;

    public SearchController(){
        filters = new LinkedList<>();
        db = DerbyConnection.getInstance();
    }

    public void reset()
    {
        this.filters.clear();
    }
    
    public void addFilter(Filter filter){
        filters.add(filter);

    }

    public void removeFilter(Filter filter){
        filters.remove(filter);
    }

    public List<Label> runSearch(Label target) throws SQLException {
        
        List<String> columns = new LinkedList<>();
       
        System.out.println(target);
        System.out.println(String.join(", ", columns));
        return db.getAllEntites_Typed(target, columns.toArray(new String[columns.size()]));
    }


}
