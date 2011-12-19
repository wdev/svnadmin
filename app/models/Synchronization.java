package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity(name="synchronizations")
public class Synchronization extends Model {

    @Lob
	@Required
	public String configuration;
	
	@Column(nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date date;
	
	@PrePersist
    protected void onCreate() {
	    date = new Date();
    }
}
