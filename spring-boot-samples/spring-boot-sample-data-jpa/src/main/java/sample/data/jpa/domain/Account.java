package sample.data.jpa.domain;/**
 * Author: wge
 * Date: 03/12/2014
 * Time: 10:42
 */

import javax.persistence.Entity;
import java.io.Serializable;

@Entity
public class Account extends Item implements Serializable
{
//    @Id
//    private String id;
    private String contactId;
    private String organisationId;

    public Account(){
    }

//    public String getId()
//    {
//        return id;
//    }
//
//    public void setId(String id)
//    {
//        this.id = id;
//    }

    public String getContactId()
    {
        return contactId;
    }

    public void setContactId(String contactId)
    {
        this.contactId = contactId;
    }

    public String getOrganisationId()
    {
        return organisationId;
    }

    public void setOrganisationId(String organisationId)
    {
        this.organisationId = organisationId;
    }
}
