package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity(name="repositories")
public class Repository extends Model {

	@Required
	@Column(unique=true)
	public String name;
	
	public String category;
	
	@Transient
	public Boolean created;
	
	@OneToMany(cascade=CascadeType.REMOVE, mappedBy="repository")
	public List<Permission> permission;
	
	@Override
	public String toString() {
		return this.name;
	}
}
