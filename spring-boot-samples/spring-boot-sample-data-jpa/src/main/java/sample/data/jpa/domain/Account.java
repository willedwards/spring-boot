package sample.data.jpa.domain;/**
 * Author: wge
 * Date: 03/12/2014
 * Time: 10:42
 */

import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name="ACCOUNT", schema = "cla.dbo")
public class Account extends Item implements Serializable
{
    private String contactId;
    private String organisationId;

    public Account(){
    }

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
