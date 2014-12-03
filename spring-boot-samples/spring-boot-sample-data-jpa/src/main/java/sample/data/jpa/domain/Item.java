package sample.data.jpa.domain;/**
 * Author: wge
 * Date: 03/12/2014
 * Time: 10:43
 */


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;

@Entity
@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
public class Item
{
    private static final Logger log = LoggerFactory.getLogger(Item.class);

    public String getId()
    {
        return id;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    @Id
    private String id;


    public long getTimestamp()
    {
        return timestamp;
    }

    public void setTimestamp(long timestamp)
    {
        this.timestamp = timestamp;
    }

    private long   timestamp = System.currentTimeMillis();

}