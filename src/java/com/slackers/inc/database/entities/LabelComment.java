/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.slackers.inc.database.entities;

import com.slackers.inc.database.DerbyConnection;
import com.slackers.inc.database.IEntity;
import java.io.Serializable;
import java.sql.Date;
import java.sql.SQLException;
import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author John Stegeman <j.stegeman@labyrinth-tech.com>
 */
public class LabelComment implements IEntity{
    
    private static final String TABLE = "LABEL_COMMENTS";

    private long commentId;
    private String comment;
    private User commenter;
    private Date date;

    public LabelComment(User submitter, String comment) {
        this.comment = comment;
        this.commenter = submitter;
        this.commentId = 0;
        date = new Date(Date.from(Instant.now()).getTime());
    }

    public LabelComment()
    {
        this(User.NULL_USER, "");
    }
    
    public long getCommentId() {
        return commentId;
    }

    public void setCommentId(long commentId) {
        this.commentId = commentId;
    }
    
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public User getSubmitter() {
        return commenter;
    }

    public void setSubmitter(User submitter) {
        this.commenter = submitter;
    }    

    public Date getDate() {
        return date;
    }
    
    @Override
    public String getTableName() {
        return TABLE;
    }

    @Override
    public Map<String, Object> getEntityValues() {
        Map<String, Object> values = new HashMap<>();
        values.put("commentId", this.commentId);
        values.put("comment", this.comment);
        if (this.commenter!=null)
            values.put("commenter", this.commenter.getPrimaryKeyValue());
        values.put("date", this.date);
        return values;
    }

    @Override
    public Map<String, Object> getUpdatableEntityValues() {
        Map<String, Object> values = new HashMap<>();
        values.put("comment", this.comment);
        if (this.commenter!=null)
            values.put("commenter", this.commenter.getPrimaryKeyValue());
        values.put("date", this.date);
        return values;
    }

    @Override
    public void setEntityValues(Map<String, Object> values) {
        if (values.containsKey("commentId"))
        {
            this.commentId = (long) values.get("commentId");
        }
        if (values.containsKey("comment"))
        {
            this.comment = (String) values.get("comment");
        }
        if (values.containsKey("date"))
        {
            this.date = (Date) values.get("date");
        }
        if (values.containsKey("commenter"))
        {
            this.commenter.setPrimaryKeyValue((Serializable)values.get("commenter"));
            try {                
                DerbyConnection.getInstance().getEntity(this.commenter, this.commenter.getPrimaryKeyName());
            } catch (Exception ex) {
                Logger.getLogger(LabelComment.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public Map<String, Class> getEntityNameTypePairs() {
        Map<String, Class> pairs = new HashMap<>();
        pairs.put("commentId", Long.class);
        pairs.put("comment", String.class);
        pairs.put("commenter", String.class);
        pairs.put("date", Date.class);
        return pairs;
    }

    @Override
    public List<String> tableColumnCreationSettings() {
        List<String> cols = new LinkedList<>();
        cols.add("commentId bigint PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1)");
        cols.add("comment long varchar");
        cols.add("date date");
        cols.add("commenter varchar(512)");
        return cols;
    }

    @Override
    public String getPrimaryKeyName() {
        return "commentId";
    }

    @Override
    public Serializable getPrimaryKeyValue() {
        return this.commentId;
    }

    @Override
    public void setPrimaryKeyValue(Serializable value) {
        this.commentId = (long) value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + (int) (this.commentId ^ (this.commentId >>> 32));
        hash = 83 * hash + Objects.hashCode(this.comment);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LabelComment other = (LabelComment) obj;
        if (this.commentId != other.commentId) {
            return false;
        }
        return true;
    }

    public static String commentListToString(List<LabelComment> comments)
    {
        if (comments==null)
            return null;
        List<String> cString = new LinkedList<>();
        for (LabelComment e : comments)
        {
            try {
                if (e.getCommentId()==0)
                {
                    DerbyConnection.getInstance().createEntity(e);
                }
                cString.add(Long.toString(e.getCommentId()));
            } catch (SQLException ex) {
                Logger.getLogger(LabelComment.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return DerbyConnection.collectionToString(cString);
    }
    
    public static List<LabelComment> commentListFromString(String commentListString)
    {
        if (commentListString==null)
            return null;
        List<LabelComment> comments = new LinkedList<>();
        List<String> cStrings = DerbyConnection.collectionFromString(commentListString);
        for (String s : cStrings)
        {          
            try {
                if (s!=null && !s.equals(""))
                {
                    LabelComment temp = new LabelComment(new User(),null);
                    temp.setCommentId(Long.parseLong(s));
                    DerbyConnection.getInstance().getEntity(temp, temp.getPrimaryKeyName());
                    comments.add(temp);
                }
            } catch (Exception ex) {
                Logger.getLogger(LabelApplication.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
        return comments;
    }

    @Override
    public LabelComment deepCopy() {
        LabelComment c = new LabelComment(this.commenter, this.comment);
        c.setEntityValues(this.getEntityValues());
        return c;
    }

    @Override
    public String toString() {
        return "LabelComment{" + "commentId=" + commentId + ", comment=" + comment + ", commenter=" + commenter + '}';
    }
    
    
}
