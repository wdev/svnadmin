package models;

import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import play.data.validation.Required;
import play.db.jpa.JPABase;
import play.db.jpa.Model;

@Entity(name="groups")
public class Group extends Model {

	@Required
	@Column(unique=true)
	public String name;
	
	@ManyToMany
	public Set<User> users;
	
	@Transient
	public String logins;
	
	@Override
	public String toString() {
		return this.name;
	}
	
	@Override
	public <T extends JPABase> T delete() {
	    List<Permission> permissions = Permission.find("byGroup", this).fetch();
	    for (Permission permission : permissions) {
            permission.delete();
        }
	    return super.delete();
	}
}
