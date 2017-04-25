/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.database;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
public interface IEntity {
    // Returns the table that this class is held in
    // in our database.
    public String getTableName();
    // Returns a map from the name of the attributes in this class to
    // the value of the attributes. Used for the database.
    public Map<String,Object> getEntityValues();
    // Returns a map of the name of the attributes in this class to the
    // value of the updatable attributes. Used for the database.
    public Map<String, Object> getUpdatableEntityValues();
    // Takes a map of the name of the attributes in this class
    // to the value of the attributes, and sets these values
    // to the objects own values. Used for the database.
    public void setEntityValues(Map<String,Object> values);
    // Returns a map of the attributes names in this class to the attributes
    // types. Used for the database.
    public Map<String,Class> getEntityNameTypePairs();
    // Returns a list of strings of SQL instructions on how to create
    // the table that this class is held in in our database. This allows
    // us to recreate our database at runtime if it does not exist.
    public List<String> tableColumnCreationSettings();

    // Returns a String of the name of the attribute that
    // is the primary key in the database. Usually this
    // is some type of ID
    public String getPrimaryKeyName();
    // Returns the value of the primary key for this class
    // in the database.
    public Serializable getPrimaryKeyValue();
    // Sets the primary key that this class uses to
    // be the key in the database.
    public void setPrimaryKeyValue(Serializable value);

    // Returns a deep copy of this object.
    public IEntity deepCopy();
}
