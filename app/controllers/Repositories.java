package controllers;

import java.io.File;
import java.util.List;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;

import models.Repository;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.mvc.Controller;
import util.SvnUtil;

public class Repositories extends AbstractController {
	
    public static void index() {
        List<Repository> repositories = Repository.all().fetch();
        for (Repository repository : repositories) {
			repository.created = svnUtil.getRepositoryDir(repository.name).isDirectory();
		}
        render(repositories);
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
            repository.name = formatName(repository.name);
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
        repository.name = formatName(repository.name);
        repository = repository.merge();
        
        repository.save();
        flash.success(Messages.get("updated", "Repository"));
        createRepository(repository);
        
        index();
    }
    
    private static String formatName(String name) {
        return name.replace("-", "_").toLowerCase();
    }
}
