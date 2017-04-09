/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package java.com.slackers.inc.database;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
public interface IEntity {
    public String getTableName();
    public Map<String,Object> getEntityValues();
    public Map<String, Object> getUpdatableEntityValues();
    public void setEntityValues(Map<String,Object> values);
    public Map<String,Class> getEntityNameTypePairs();
    public List<String> tableColumnCreationSettings();
    
    public String getPrimaryKeyName();
    public Serializable getPrimaryKeyValue();
    public void setPrimaryKeyValue(Serializable value);
    
    public IEntity deepCopy();
}
