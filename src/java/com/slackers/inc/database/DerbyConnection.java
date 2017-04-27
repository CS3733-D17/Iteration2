/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.database;

import com.slackers.inc.Controllers.Filters.ExactFilter;
import com.slackers.inc.Controllers.Filters.Filter;
import com.slackers.inc.Controllers.Filters.RangeFilter;

import java.io.*;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 *
 *     Used for connecting to our Derby database. Singleton pattern.
 */
public class DerbyConnection {
    // Class used for our database.
    private final static String DRIVER_CLASS = "org.apache.derby.jdbc.EmbeddedDriver";
    private final static String DB_PROTOCOL_BASE = "jdbc:derby:";
    // Location of database relative to this project
    private final static String DB_RELATIVE_LOCATION = "database/DB";
    private final static String COLLECTION_DELIMITER = ":::";
    // The single instance
    private static DerbyConnection INSTANCE;
    
    static
    {
        INSTANCE = null;
        try {
            DriverManager.registerDriver(new org.apache.derby.jdbc.EmbeddedDriver());
            INSTANCE = new DerbyConnection();
        } catch (Exception ex) {
            Logger.getLogger(DerbyConnection.class.getName()).log(Level.SEVERE, "Could not load database. Exiting");
            Logger.getLogger(DerbyConnection.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }       
    }

    // Connection to the database
    private Connection con;
    // Tables in this database
    private Set<String> tables;
    private boolean isOpen;

    // On construction, a connection to the database is open
    private DerbyConnection() throws SQLException
    {
        open();
    }

    // Shuts down the DB, then reopens it.
    public void reset()
    {
        this.shutdownDb();
        try {
            this.open();
        } catch (SQLException ex) {
            Logger.getLogger(DerbyConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // Opens a connection to the DB
    private void open() throws SQLException
    {
        if (!this.isOpen)
        {
            System.out.println("Connecting to database...");
            Map<String,String> properties = new HashMap<>();
            properties.put("create", "true");
            this.con = DriverManager.getConnection(makeConnectionString(properties));
            this.tables = new HashSet<>();
            isOpen=true;
        }
    }

    /**
     * @param tableName Name of a table
     * @return true if the table exists in the database. false otherwise
     * @throws SQLException
     */
    public boolean tableExists(String tableName) throws SQLException
    {
        DatabaseMetaData md = con.getMetaData();
        ResultSet rs = md.getTables(null, null, "%", null);
        while (rs.next()) {
            if (rs.getString(3).toLowerCase().equals(tableName.toLowerCase()))
                return true;
        }
        return false;
    }

    /**
     * Creates a table in the DB.
     * @param tableName Name of table to create
     * @param columns List of names of columns that table has
     * @return true if the creation was successful, false otherwise
     * @throws SQLException
     */
    public boolean createTable(String tableName, List<String> columns) throws SQLException
    {        
        this.open();
        String stmt = String.format("CREATE TABLE %s (%s)", tableName, String.join(", ", columns));
        CallableStatement call = con.prepareCall(stmt);
        return call.execute();
    }

    /**
     * Deletes a table from the DB
     * @param tableName Name of table to delete
     * @return true if the deletion was successful, false otherwise
     * @throws SQLException
     */
    public boolean deleteTable(String tableName) throws SQLException
    {
        String stmt = String.format("DROP TABLE IF EXISTS %s", tableName);
        CallableStatement call = con.prepareCall(stmt);
        return call.execute();
    }

    /**
     * Checks that the DB has all the tables necessary to hold given object.
     * @param entity Entity used to check for tables
     * @return true if the table exists, false otherwise
     * @throws SQLException
     */
    private boolean checkForTable(IEntity entity) throws SQLException
    {
        this.open();
        if (this.tables.contains(entity.getTableName()))
            return true;
        if (this.tableExists(entity.getTableName()))
        {
            this.tables.add(entity.getTableName());
            return true;
        }
        return this.createTable(entity.getTableName(), entity.tableColumnCreationSettings());
    }

    /**
     * Creates the entity in our database. Assumes it does not exist already.
     * Do not use this if you want to write an object to the DB, instead use
     * writeEntity
     * @param entity Entity to create
     * @return If the creation was successful.
     * @throws SQLException
     */
    public boolean createEntity(IEntity entity) throws SQLException
    {
        if (!checkForTable(entity)) // create table if non existant
            return false;
        
        StringBuilder cols = new StringBuilder();
        StringBuilder vPlace = new StringBuilder();
        Set<String> updatable = entity.getUpdatableEntityValues().keySet();
        List<Object> vals = new LinkedList<>();
        boolean first = true;
        for (Entry<String, Object> e : entity.getEntityValues().entrySet())
        {
            if (updatable.contains(e.getKey()))
            {
                if (first)
                {
                    first = false;
                }
                else
                {
                    cols.append(",");
                    vPlace.append(",");
                }
                cols.append(e.getKey());
                vPlace.append('?');
                vals.add(e.getValue());
            }
        }
        String stmt = String.format("INSERT INTO %s (%s) VALUES (%s)", entity.getTableName(), cols.toString(), vPlace.toString());
        PreparedStatement call = con.prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS);
        int i = 1;
        for (Object o : vals)
        {
            DerbyConnection.setStatementValue(con, call, i, o);
            i++;
        }
        boolean res = call.execute();
        ResultSet results = call.getGeneratedKeys();
        int c=0;
        while (results.next())
        {
            c++;
            if (c>1) // only get first
                return res;
            Class keyClass = entity.getEntityNameTypePairs().get(entity.getPrimaryKeyName());
            if (keyClass!=null && Long.class.isAssignableFrom(keyClass))
                entity.setPrimaryKeyValue(results.getLong(1));
        }
        con.commit();
        results.close();        
        return res;
    }

    /**
     * Deletes an entity from the DB.
     * @param entity Entity to delete
     * @param searchColumns Matches the information in the entity with
     *                      the information in the DB table to find the
     *                      correct entity.
     * @return If the deletion was successful
     * @throws SQLException
     */
    public boolean deleteEntity(IEntity entity, String... searchColumns) throws SQLException
    {
        if (!checkForTable(entity)) // create table if non existant
            return false;
        
        if (searchColumns.length<=0)
            return false; // avoid table deletion
        Set<String> cols = new HashSet<>(Arrays.asList(searchColumns));
        StringBuilder conds = new StringBuilder();
        List<Object> vals = new LinkedList<>();
        boolean first = true;
        for (Entry<String, Object> e : entity.getEntityValues().entrySet())
        {
            if (cols.contains(e.getKey()))
            {
                if (first)
                {
                    first = false;
                }
                else
                {
                    conds.append(" AND ");
                }
                conds.append(e.getKey());
                conds.append("=(?)");
                vals.add(e.getValue());
            }
        }
        String stmt = String.format("DELETE FROM %s WHERE %s", entity.getTableName(), conds.toString());
        PreparedStatement call = con.prepareStatement(stmt);
        int i = 1;
        for (Object o : vals)
        {
            DerbyConnection.setStatementValue(con, call, i, o);
            i++;
        }
        boolean res = call.execute();
        con.commit();
        call.close();
        return res;
    }

    /**
     * Updates an already existing entity in the DB
     * Do not use this if you want to write an object to the DB, instead use
     * writeEntity
     * @param entity Entity to update
     * @param searchColumns Columns to match in search to find entity
     * @return if the update was successful
     * @throws SQLException
     */
    public boolean updateEntity(IEntity entity, String... searchColumns) throws SQLException
    {
        if (!checkForTable(entity)){ // create table if non existant
           
            return false;
        }
        
        if (searchColumns.length<=0){
             
            return false; // avoid table deletion
        }
        Set<String> cols = new HashSet<>(Arrays.asList(searchColumns));
        StringBuilder conds = new StringBuilder();
        
        StringBuilder vPlace = new StringBuilder();
        List<Object> setvals = new LinkedList<>();
        List<Object> condvals = new LinkedList<>();
        Set<String> updatable = entity.getUpdatableEntityValues().keySet();
        boolean first = true;
        boolean firstSet = true;
        for (Entry<String, Object> e : entity.getEntityValues().entrySet())
        {
            if (cols.contains(e.getKey()))
            {
                if (first)
                {
                    first = false;
                }
                else
                {
                    conds.append(" AND ");
                }
                conds.append(e.getKey());
                conds.append("=(?)");
                condvals.add(e.getValue());
            }
            if (updatable.contains(e.getKey()))
            {
                if (firstSet)
                {
                    firstSet=false;
                }
                else
                {
                    vPlace.append(',');
                }
                vPlace.append(e.getKey());
                vPlace.append("=(?)");
                setvals.add(e.getValue());
            }
        }
        String stmt = String.format("UPDATE %s SET %s WHERE %s", entity.getTableName(), vPlace.toString(), conds.toString());
        PreparedStatement call = con.prepareStatement(stmt);
        int i = 1;
        for (Object o : setvals)
        {
            DerbyConnection.setStatementValue(con, call, i, o);
            i++;
        }
        for (Object o : condvals)
        {
            DerbyConnection.setStatementValue(con, call, i, o);
            i++;
        }        
        boolean res = call.execute();
        con.commit();
        call.close();
        return res;
    }

    /**
     * Checks if an entity exists
     * @param entity Entity to check for
     * @param searchColumns Columns used to find correct entity
     * @return if the entity was successful
     * @throws SQLException
     */
    public boolean entityExists(IEntity entity, String... searchColumns) throws SQLException
    {
        if (!checkForTable(entity)) // create table if non existant
            return false;
        
        if (searchColumns.length<=0)
            return true; // avoid table deletion
        Set<String> cols = new HashSet<>(Arrays.asList(searchColumns));
        StringBuilder conds = new StringBuilder();
        List<Object> vals = new LinkedList<>();
        boolean first = true;
        for (Entry<String, Object> e : entity.getEntityValues().entrySet())
        {
            if (cols.contains(e.getKey()))
            {
                if (first)
                {
                    first = false;
                }
                else
                {
                    conds.append(" AND ");
                }
                conds.append(e.getKey());
                conds.append("=(?)");
                vals.add(e.getValue());
            }
        }
        String stmt = String.format("SELECT * FROM %s WHERE %s", entity.getTableName(), conds.toString());
        PreparedStatement call = con.prepareStatement(stmt);
        int i = 1;
        for (Object o : vals)
        {
            DerbyConnection.setStatementValue(con, call, i, o);
            i++;
        }        
        ResultSet results = call.executeQuery();
        while (results.next())
        {
            results.close();
            return true;
        }
        results.close();
        return false;
    }

    /**
     * Retrieves an entity from the DB.
     * @param entity Entity to retrieve
     * @param searchColumns Columns to match to find the correct
     *                      entity.
     * @throws SQLException
     */
    public void getEntity(IEntity entity, String... searchColumns) throws SQLException
    {
        if (!checkForTable(entity)) // create table if non existant
            return;
        
        if (searchColumns.length<=0)
            return; // avoid table deletion
        Set<String> cols = new HashSet<>(Arrays.asList(searchColumns));
        StringBuilder conds = new StringBuilder();
        List<Object> vals = new LinkedList<>();
        boolean first = true;
        for (Entry<String, Object> e : entity.getEntityValues().entrySet())
        {
            if (cols.contains(e.getKey()))
            {
                if (first)
                {
                    first = false;
                }
                else
                {
                    conds.append(" AND ");
                }
                conds.append(e.getKey());
                conds.append("=(?)");
                vals.add(e.getValue());
            }
        }
        String stmt = String.format("SELECT * FROM %s WHERE %s", entity.getTableName(), conds.toString());
        PreparedStatement call = con.prepareStatement(stmt);
        int i = 1;
        for (Object o : vals)
        {
            DerbyConnection.setStatementValue(con, call, i, o);
            i++;
        }
        ResultSet results = call.executeQuery();
        int c=0;
        Map<String,Object> valMap = new HashMap<>();
        while (results.next())
        {
            valMap.clear();
            c++;
            if (c>1) // only get first
                return;
            for (String s : entity.getEntityNameTypePairs().keySet())
            {
                DerbyConnection.getStatementValue(con, results, s, entity, valMap);
            }
            entity.setEntityValues(valMap);
        }
        results.close();
    }

    /**
     * Get all entites that match the columns given.
     * @param entity Entity with information to match
     * @param searchColumns Columns to match
     * @return List of entities that match the information in the given entity
     *         as long as they are in the given searchColumns
     * @throws SQLException
     */
    public List<IEntity> getAllEntites(IEntity entity, String... searchColumns) throws SQLException
    {
        if (!checkForTable(entity)) // create table if non existant
            return null;
        
        if (searchColumns.length<=0)
            return null; // avoid table deletion
        Set<String> cols = new HashSet<>(Arrays.asList(searchColumns));
        StringBuilder conds = new StringBuilder();
        List<Object> vals = new LinkedList<>();
        boolean first = true;
        for (Entry<String, Object> e : entity.getEntityValues().entrySet())
        {
            if (cols.contains(e.getKey()))
            {
                if (first)
                {
                    first = false;
                }
                else
                {
                    conds.append(" AND ");
                }
                conds.append(e.getKey());
                conds.append("=(?)");
                vals.add(e.getValue());
            }
        }
        String stmt = String.format("SELECT * FROM %s WHERE %s", entity.getTableName(), conds.toString());
        PreparedStatement call = con.prepareStatement(stmt);
        int i = 1;
        for (Object o : vals)
        {
            DerbyConnection.setStatementValue(con, call, i, o);
            i++;
        }
        ResultSet results = call.executeQuery();
        
        int c=0;
        Map<String,Object> valMap = new HashMap<>();
        List<IEntity> entites = new LinkedList<>();
        while (results.next())
        {
            valMap.clear();
            c++;
            for (String s : entity.getEntityNameTypePairs().keySet())
            {
                DerbyConnection.getStatementValue(con, results, s, entity, valMap);
            }
            entity.setEntityValues(valMap);
            entites.add(entity.deepCopy());
        }
        results.close();
        return entites;
    }

    /**
     * Same as getAllEntites but uses a generic type T
     */
    public <T extends IEntity> List<T> getAllEntites_Typed(T entity, String... searchColumns) throws SQLException
    {
        if (!checkForTable(entity)) // create table if non existant
            return null;
        
        if (searchColumns.length<=0)
            return null; // avoid table deletion
        Set<String> cols = new HashSet<>(Arrays.asList(searchColumns));
        StringBuilder conds = new StringBuilder();
        List<Object> vals = new LinkedList<>();
        boolean first = true;
        for (Entry<String, Object> e : entity.getEntityValues().entrySet())
        {
            if (cols.contains(e.getKey()))
            {
                if (first)
                {
                    first = false;
                }
                else
                {
                    conds.append(" AND ");
                }
                conds.append(e.getKey());
                conds.append("=(?)");
                vals.add(e.getValue());
            }
        }
        String stmt = String.format("SELECT * FROM %s WHERE %s", entity.getTableName(), conds.toString());
        PreparedStatement call = con.prepareStatement(stmt);
        int i = 1;
        for (Object o : vals)
        {
            DerbyConnection.setStatementValue(con, call, i, o);
            i++;
        }
        ResultSet results = call.executeQuery();
        
        int c=0;
        Map<String,Object> valMap = new HashMap<>();
        List<T> entites = new LinkedList<>();
        while (results.next())
        {
            valMap.clear();
            c++;
            try {
                T ent = (T) entity.getClass().newInstance();
                for (String s : entity.getEntityNameTypePairs().keySet())
                {
                    DerbyConnection.getStatementValue(con, results, s, entity, valMap);
                }
                ent.setEntityValues(valMap);
                entites.add(ent);
            } catch (Exception ex) {
                Logger.getLogger(DerbyConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        results.close();
        return entites;
    }

    /**
     * Same as getAllEntites_Typed but matches all columns.
     */
    public <T extends IEntity> List<T> getAllEntites_Typed(T entity) throws SQLException
    {
        if (!checkForTable(entity)) // create table if non existant
            return null;
        String stmt = String.format("SELECT * FROM %s", entity.getTableName());
        PreparedStatement call = con.prepareStatement(stmt);
        ResultSet results = call.executeQuery();
        int c=0;
        Map<String,Object> valMap = new HashMap<>();
        List<T> entites = new LinkedList<>();
        while (results.next())
        {
            valMap.clear();
            c++;
            try {
                T ent = (T) entity.getClass().newInstance();
                for (String s : entity.getEntityNameTypePairs().keySet())
                {
                    DerbyConnection.getStatementValue(con, results, s, entity, valMap);
                }
                ent.setEntityValues(valMap);
                entites.add(ent);
            } catch (Exception ex) {
                Logger.getLogger(DerbyConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        results.close();
        return entites;
    }

    /**
     * Searches for entities in the DB given a list of filters
     * @param entity Entity given
     * @param filters Filters applied to narrow down search
     * @param <T> Type of entity
     * @return List of entities which match the filter
     * @throws SQLException
     */
    public <T extends IEntity> List<T> search(T entity, List<Filter> filters) throws SQLException
    {
        String stmt;
        List<String> statements = new LinkedList<>();
        List<Object> vals = new LinkedList<>();
        if (!filters.isEmpty())
        {      
            for (Filter f : filters)
            {
                if (f instanceof ExactFilter)
                {
                    statements.add(f.getColumn()+" =(?)");
                    vals.add(((ExactFilter)f).getValue());
                }
                if (f instanceof RangeFilter)
                {
                    statements.add(f.getColumn()+" between (?) and (?)");
                    vals.add(((RangeFilter)f).getValueMin());
                    vals.add(((RangeFilter)f).getValueMax());
                }
            }
            stmt = String.format("SELECT * FROM %s WHERE %s", entity.getTableName(), String.join(" and ", statements));
        }
        else
        {
            stmt = String.format("SELECT * FROM %s", entity.getTableName());
        }
        PreparedStatement call = con.prepareStatement(stmt);
        int i = 1;
        for (Object o : vals)
        {
            DerbyConnection.setStatementValue(con, call, i, o);
            i++;
        }
        ResultSet results = call.executeQuery();
        
        int c=0;
        Map<String,Object> valMap = new HashMap<>();
        List<T> entites = new LinkedList<>();
        while (results.next())
        {
            valMap.clear();
            c++;
            try {
                T ent = (T) entity.getClass().newInstance();
                for (String s : entity.getEntityNameTypePairs().keySet())
                {
                    DerbyConnection.getStatementValue(con, results, s, entity, valMap);
                }
                ent.setEntityValues(valMap);
                entites.add(ent);
            } catch (Exception ex) {
                Logger.getLogger(DerbyConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        results.close();
        return entites;
    }

    /**
     * Same as previous search, but only retrieves the given amount, with an
     * offset. This allows for more efficient use of the DB.
     */
    public <T extends IEntity> List<T> search(T entity, List<Filter> filters, int numberOfResults, int offset) throws SQLException
    {
        String stmt;
        List<String> statements = new LinkedList<>();
        List<Object> vals = new LinkedList<>();
        if (!filters.isEmpty())
        {
            for (Filter f : filters)
            {
                if (f instanceof ExactFilter)
                {
                    statements.add(f.getColumn()+" =(?)");
                    vals.add(((ExactFilter)f).getValue());
                }
                if (f instanceof RangeFilter)
                {
                    statements.add(f.getColumn()+" between (?) and (?)");
                    vals.add(((RangeFilter)f).getValueMin());
                    vals.add(((RangeFilter)f).getValueMax());
                }
            }
            stmt = String.format("SELECT * FROM %s WHERE %s OFFSET %d rows fetch first %d rows only", entity.getTableName(), String.join(" and ", statements), offset, numberOfResults);
        }
        else
        {
            stmt = String.format("SELECT * FROM %s OFFSET %d rows fetch first %d rows only ", entity.getTableName(), offset, numberOfResults);
        }
        PreparedStatement call = con.prepareStatement(stmt);
        int i = 1;
        for (Object o : vals)
        {
            DerbyConnection.setStatementValue(con, call, i, o);
            i++;
        }
        ResultSet results = call.executeQuery();
        
        int c=0;
        Map<String,Object> valMap = new HashMap<>();
        List<T> entites = new LinkedList<>();
        while (results.next())
        {
            valMap.clear();
            c++;
            try {
                T ent = (T) entity.getClass().newInstance();
                for (String s : entity.getEntityNameTypePairs().keySet())
                {
                    DerbyConnection.getStatementValue(con, results, s, entity, valMap);
                }
                ent.setEntityValues(valMap);
                entites.add(ent);
            } catch (Exception ex) {
                Logger.getLogger(DerbyConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        results.close();
        return entites;
    }
    
    /**
     * Same as previous search, but only retrieves the given amount, with an
     * offset. This allows for more efficient use of the DB. Allows union and intersect modes
     */
    public <T extends IEntity> List<T> search(T entity, List<List<Filter>> filtersList, int numberOfResults, int offset, boolean union) throws SQLException
    {
        List<Object> vals = new LinkedList<>();
        List<String> statementList = new LinkedList<>();
        for (List<Filter> filters : filtersList)
        {
            String stmt;
            List<String> statements = new LinkedList<>();            
            if (!filters.isEmpty())
            {
                for (Filter f : filters)
                {
                    if (f instanceof ExactFilter)
                    {
                        statements.add(f.getColumn()+" =(?)");
                        vals.add(((ExactFilter)f).getValue());
                    }
                    if (f instanceof RangeFilter)
                    {
                        statements.add(f.getColumn()+" between (?) and (?)");
                        vals.add(((RangeFilter)f).getValueMin());
                        vals.add(((RangeFilter)f).getValueMax());
                    }
                }
                stmt = String.format("SELECT * FROM %s WHERE %s", entity.getTableName(), String.join(" and ", statements));
            }
            else
            {
                stmt = String.format("SELECT * FROM %s", entity.getTableName());
            }
        }
        String st = String.format("%s OFFSET %d rows fetch first %d rows only ", String.join(" UNION ", statementList), offset, numberOfResults);
        PreparedStatement call = con.prepareStatement(st);
        int i = 1;
        for (Object o : vals)
        {
            DerbyConnection.setStatementValue(con, call, i, o);
            i++;
        }
        ResultSet results = call.executeQuery();
        
        int c=0;
        Map<String,Object> valMap = new HashMap<>();
        List<T> entites = new LinkedList<>();
        while (results.next())
        {
            valMap.clear();
            c++;
            try {
                T ent = (T) entity.getClass().newInstance();
                for (String s : entity.getEntityNameTypePairs().keySet())
                {
                    DerbyConnection.getStatementValue(con, results, s, entity, valMap);
                }
                ent.setEntityValues(valMap);
                entites.add(ent);
            } catch (Exception ex) {
                Logger.getLogger(DerbyConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        results.close();
        return entites;
    }

    /**
     * Use this to add entities to the database.
     * Checks if the entity exists already, and chooses the
     * correct function to use.
     */
    public boolean writeEntity(IEntity entity, String... searchColumns) throws SQLException
    {
        if (this.entityExists(entity, searchColumns))
            return this.updateEntity(entity, searchColumns);
        else
            return this.createEntity(entity);
    }
    
    private static void setStatementValue(Connection con, PreparedStatement stmt, int index, Object obj) throws SQLException
    {
        if (obj == null)
        {
            stmt.setNull(index, Types.NULL);
        }
        else if (obj instanceof Integer)
        {
            stmt.setInt(index, (Integer)obj);
        }
        else if (obj instanceof Short)
        {
            stmt.setShort(index, (Short)obj);
        }
        else if (obj instanceof Boolean)
        {
            stmt.setBoolean(index, (Boolean)obj);
        }
        else if (obj instanceof String)
        {
            stmt.setString(index, (String)obj);
        }
        else if (obj instanceof Date)
        {
            stmt.setDate(index, (Date)obj);
        }
        else if (obj instanceof Long)
        {
            stmt.setLong(index, (Long)obj);
        }
        else if (obj instanceof Double)
        {
            stmt.setDouble(index, (Double)obj);
        }
        else if (obj instanceof byte[])
        {
            Blob b = con.createBlob();
            b.setBytes(1, (byte[])obj);
            stmt.setBlob(index, b);
        }
        else if (obj instanceof Serializable)
        {
            try {
                stmt.setString(index, objectToString((Serializable)obj));
            } catch (IOException ex) {
                Logger.getLogger(DerbyConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static void getStatementValue(Connection con, ResultSet result, String colTitle, IEntity entity, Map<String,Object> valueCollection) throws SQLException
    {
        Class target = entity.getEntityNameTypePairs().get(colTitle);
        if (target == null)
            return;
        if (Integer.class.isAssignableFrom(target))
        {
            valueCollection.put(colTitle, result.getInt(colTitle));
        }
        else if (Short.class.isAssignableFrom(target))
        {
            valueCollection.put(colTitle, result.getShort(colTitle));
        }
        else if (String.class.isAssignableFrom(target))
        {
            valueCollection.put(colTitle, result.getString(colTitle));
        }
        else if (Date.class.isAssignableFrom(target))
        {
            valueCollection.put(colTitle, result.getDate(colTitle));
        }
        else if (Long.class.isAssignableFrom(target))
        {
            valueCollection.put(colTitle, result.getLong(colTitle));
        }
        else if (Boolean.class.isAssignableFrom(target))
        {
            valueCollection.put(colTitle, result.getBoolean(colTitle));
        }
        else if (Double.class.isAssignableFrom(target))
        {
            valueCollection.put(colTitle, result.getDouble(colTitle));
        }
        else if (byte[].class.isAssignableFrom(target))
        {
            Blob b = result.getBlob(colTitle);
            valueCollection.put(colTitle, b.getBytes(1, (int) b.length()));
            b.free();
        }
        else if (Serializable.class.isAssignableFrom(target))
        {
            try {
                valueCollection.put(colTitle, objectFromString(result.getString(colTitle)));
            } catch (IOException ex) {
                Logger.getLogger(DerbyConnection.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DerbyConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public static Object objectFromString( String s ) throws IOException ,
                                                       ClassNotFoundException {
        if (s==null)
            return null;
        byte [] data = Base64.getDecoder().decode( s );
        Object o;
        try (ObjectInputStream ois = new ObjectInputStream( 
                new ByteArrayInputStream(  data ) )) {
            o = ois.readObject();
        }
        return o;
   }
    
    public static String objectToString(Serializable o) throws IOException {
        if (o==null)
            return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream( baos )) {
            oos.writeObject( o );
        }
        return Base64.getEncoder().encodeToString(baos.toByteArray()); 
    }
    
    public void closeConnection() throws SQLException
    {
        this.con.close();
        this.isOpen=false;
    }

    /**
     * Get an instance of the DB connection.
     */
    public static DerbyConnection getInstance()
    {
        return DerbyConnection.INSTANCE;
    }
    
    
    private static String makeConnectionString(Map<String,String> properties)
    {
        StringBuilder propString = new StringBuilder();
        boolean first = true;
        for (Entry<String,String> e : properties.entrySet())
        {
            if (first)
            {
                first = false;
            }
            else
            {
                propString.append(";");
            }
            propString.append(e.getKey()).append("=").append(e.getValue());
        }
        
        String base = DB_PROTOCOL_BASE + DB_RELATIVE_LOCATION + ";" + propString.toString();
        return base;
    }

    /**
     * Shuts down the DB
     */
    public boolean shutdownDb()
    {
        System.out.println("Shutting down database...");
        try {
            this.closeConnection();
        } catch (SQLException ex) {
            Logger.getLogger(DerbyConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            Map<String,String> properties = new HashMap<>();
            properties.put("shutdown", "true");
            DriverManager.getConnection(makeConnectionString(properties));
        } catch (SQLException ex) {
            return true;
        }
        return false;
    }
    
    public static String collectionToString(List<String> collection)
    {
        return String.join(COLLECTION_DELIMITER, collection);
    }
    
    public static List<String> collectionFromString(String serialized)
    {
        return Arrays.asList(serialized.split(COLLECTION_DELIMITER));
    }
}
