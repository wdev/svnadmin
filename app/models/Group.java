package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity(name="groups")
public class Group extends Model {

	@Required
	@Column(unique=true)
	public String name;
	
	@ManyToMany
	public List<User> users;
	
	@Transient
	public String logins;
	
	@OneToOne(cascade=CascadeType.REMOVE, mappedBy="group")
	public Permission permission;
	
	@Override
	public String toString() {
		return this.name;
	}
}
