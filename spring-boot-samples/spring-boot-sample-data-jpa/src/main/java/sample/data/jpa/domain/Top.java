package sample.data.jpa.domain;/**
 * Author: wge
 * Date: 03/12/2014
 * Time: 13:42
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import java.io.Serializable;

@Entity
@Inheritance(strategy= InheritanceType.TABLE_PER_CLASS)
public class Top implements Serializable
{
    private static final Logger log = LoggerFactory.getLogger(Top.class);


    public String getAge()
    {
        return age;
    }

    public void setAge(String age)
    {
        this.age = age;
    }

    @Id
    private String age;

}
