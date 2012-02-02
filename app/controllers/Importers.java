package controllers;

import java.util.Calendar;
import java.util.List;

import models.Group;
import models.Importer;
import models.Repository;
import play.Play;
import play.data.validation.Valid;
import play.i18n.Messages;
import util.CsvnConfig;
import util.CsvnImporter;

public class Importers extends AbstractController {
	
    public static void index() {
    	List<Importer> importers = Importer.all().fetch();
        render(importers);
    }

    public static void save(@Valid Importer importer) {
    	String error = Messages.get("validation");
    	if (! validation.hasErrors()) {
	    	try {
	    		importer.date = Calendar.getInstance().getTime();
	    		importer.save();
	    		
	    		importConfiguration(importer);
	    		
	    		createRepositories();
	            
	    		saveConfigurationFile();
	    		
	    		flash.success(Messages.get("created", "Importer"));
	    	}
	    	catch(Exception e) {
	    		error = e.getMessage();
	    	}
    	}
    	else {
    		flash.error(error);
    	}
    	index();
    }

	private static void importConfiguration(Importer importer) {
		new CsvnImporter().importConfiguration(importer.configuration);
	}

	private static void createRepositories() {
		List<Repository> repositories = Repository.all().fetch();
		for (Repository repository : repositories) {
			createRepository(repository);
		}
	}

	private static void saveConfigurationFile() {
		List<Group> groups = Group.all().fetch();
		List<Repository> repositories = Repository.all().fetch();
		CsvnConfig csvnConfig = new CsvnConfig();
		String configuration = csvnConfig.getConfig(groups, repositories);
		csvnConfig.saveConfigurationToFile(configuration, Play.configuration.getProperty("csvn.config.path"));
	}
    
    public static void show(Long id) {
    	Importer importer = Importer.findById(id);
    	render(importer);
    }

}
