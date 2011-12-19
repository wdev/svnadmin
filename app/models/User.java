package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity(name="users")
public class User extends Model {

	@Required
	@Column(unique=true)
	public String login;
	
    public String name;
	
//	@ManyToMany(cascade=CascadeType.DETACH, mappedBy="users")
//    public List<Group> groups;
	
	@Override
	public String toString() {
		return login;
	}
}
