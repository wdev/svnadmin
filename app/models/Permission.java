package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import play.data.validation.Required;
import play.db.jpa.Model;

@Table(uniqueConstraints={@UniqueConstraint(columnNames={"group_id","repository_id"})})
@Entity(name="permissions")
public class Permission extends Model {

	@Required
	@OneToOne
	public Group group;
	
	@Required
	@OneToOne
	public Repository repository;
	
	@Column(nullable=true)
	public String root;
	
	@Column(nullable=true)
	public String trunk;
	
	@Column(nullable=true)
	public String branches;
	
	@Column(nullable=true)
	public String tags;
	
	@Override
	public String toString() {
		return repository + " @" + group + " " + root + " " + trunk + " " + branches + " " + tags;
	}
}

