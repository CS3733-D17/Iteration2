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

    private static final int RESULTS_PER_PAGE = 30;
    List<Filter> filters;
    DerbyConnection db;
    private int page;

    public SearchController(){
        filters = new LinkedList<>();
        db = DerbyConnection.getInstance();
        this.page = 0;
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

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    
    
    public List<Label> runSearch(Label target) throws SQLException {
        
        return db.search(target,filters, RESULTS_PER_PAGE, page*RESULTS_PER_PAGE);
    }


}
