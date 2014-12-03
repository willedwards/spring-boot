package sample.data.jpa.domain;/**
 * Author: wge
 * Date: 03/12/2014
 * Time: 10:43
 */

import org.slf4j.*;

public class Item
{
    private static final Logger log = LoggerFactory.getLogger(Item.class);

    public String id;
    public long   timestamp = System.currentTimeMillis();

}