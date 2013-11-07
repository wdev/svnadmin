package controllers;

import java.util.List;

import models.Repository;

import org.apache.commons.lang.StringUtils;

import play.data.validation.Valid;
import play.i18n.Messages;
import util.FormatterUtil;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Repositories extends AbstractController {
	
    public static void index() {
        long total = Repository.count();
//        for (Repository repository : repositories) {
//			repository.created = svnUtil.getRepositoryDir(repository.name).isDirectory();
//		}
        render(total);
    }
    
    public static void filter(String value) {
        if (StringUtils.isNotBlank(value)) {
            JsonArray array = new JsonArray();
            final String name = "%" + value + "%"; 
            List<Repository> repositories = Repository.find("byNameLike", name).fetch();
            for (Repository repository : repositories) {
                JsonObject json = new JsonObject();
                json.addProperty("id", repository.id);
                json.addProperty("name", repository.name);
                json.addProperty("category", getCategoryName(repository));
                json.addProperty("is_created", svnUtil.getRepositoryDir(repository.name).isDirectory());
                array.add(json);
            }
            renderJSON(array);
        }
    }

    private static String getCategoryName(Repository repository) {
        if (repository.category == null) {
            return "";
        }
        return repository.category.descricao;
    }
    
    public static void create(Repository repository) {
        render(repository);
    }

    public static void edit(java.lang.Long id) {
        Repository repository = Repository.findById(id);
        render(repository);
    }

    public static void delete(Long id) {
        Repository repository = Repository.findById(id);
        repository.delete();
        svnUtil.deleteLocalRepository(repository.name);
        index();
    }
    
    public static void save(@Valid Repository repository) {
        if (validation.hasErrors()) {
            flash.error(Messages.get("validation"));
            render("@create", repository);
        }
        try {
            repository.name = FormatterUtil.formatName(repository.name);
            repository.save();
        
            if (! "/".equals(repository.name)) {
            	createRepository(repository);
            }
            
            flash.success(Messages.get("created", "Repository"));
        }
        catch (Exception e) {
            flash.error(Messages.get("repository.exists"));
        }
        index();
    }

    public static void update(@Valid Repository repository) {
        if (validation.hasErrors()) {
            flash.error(Messages.get("validation"));
            render("@edit", repository);
        }
        repository.name = FormatterUtil.formatName(repository.name);
        repository = repository.merge();
        
        repository.save();
        flash.success(Messages.get("updated", "Repository"));
        createRepository(repository);
        
        index();
    }
}
