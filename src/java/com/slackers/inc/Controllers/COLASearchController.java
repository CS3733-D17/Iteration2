package com.slackers.inc.Controllers;

import com.slackers.inc.database.entities.ColaUser;
import com.slackers.inc.database.entities.Label;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class COLASearchController {

    private ColaUser colaUser;
    private SearchController searchControl;

    /**
     * Edited by Fabio Borges on 4/2/17
     */
    public COLASearchController(ColaUser user)
    {
        this(user, new SearchController());
    }

    /**
     * Edited by Fabio Borges on 4/2/17
     */
    public COLASearchController(ColaUser user, SearchController searchControl)
    {
        this.colaUser = user;
        this.searchControl = searchControl;
    }

    /**
     * Created by Fabio Borges on 4/2/17
     * Returns the COLA user associated with this controller
     */
    public ColaUser getColaUser()
    { return colaUser; }

    /**
     * Created by Michael Steidel on 4/2/17
     * Returns the search controller associated with this controller
     */
    public SearchController getSearchControl() {
        return searchControl;
    }

    public boolean refresh()
    {
        try {
            return new AccountController(this.colaUser).reload();
        } catch (SQLException ex) {
            Logger.getLogger(ManufacturerController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    
    /**
     * Created by Michael Steidel on 4/2/17
     * Searches the database for a specified label and returns the list of search results
     */
    public List<Label> search(Label target) throws SQLException
    {
        return searchControl.runSearch(target);
    }

}
