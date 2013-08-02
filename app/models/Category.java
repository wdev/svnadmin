package models;

import javax.persistence.Column;
import javax.persistence.Entity;

import play.data.validation.Required;
import play.db.jpa.Model;

@Entity(name = "category")
public class Category extends Model {

	@Required
	@Column(unique=true)
	private String descricao;
	
	@Override
	public String toString() {
		return this.descricao;
	}

//	public List<Category> listarCategorias() {
//		List<Category> categorias = Category.find("descricao", this).fetch();
//		for (Category categoria : categorias){
//			categoria.getDescricao();
//		}
//		return categorias;
//	}
	
}
