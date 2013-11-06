package controllers;

import java.util.List;

import models.Group;
import models.Repository;
import models.Synchronization;
import play.Play;
import play.i18n.Messages;
import play.mvc.Controller;
import util.CsvnConfig;

public class Synchronizations extends Controller {
	
	private static CsvnConfig csvnConfig = new CsvnConfig();
	
    public static void index() {
    	List<Synchronization> synchronizations = Synchronization.all().fetch(10);
        render(synchronizations);
    }

    public static void save() {
    	String newConfiguration = getConfiguration();
    	
    	try {
    		String filePath = Play.configuration.getProperty("csvn.config.path");
			Synchronization sync = new Synchronization();
			
			sync.configuration = csvnConfig.getConfigurationFromFile(filePath);
			if (csvnConfig.saveConfigurationToFile(newConfiguration, filePath)) {
				sync.save();
				flash.success(Messages.get("created", "Synchronization"));
			}
			else {
				flash.error("Cannot create file "+filePath);
			}
		}
    	catch (Exception e) {
    		flash.error(e.getMessage());
		}
    	index();
    }
    
    public static void show(Long id) {
    	Synchronization synchronization = Synchronization.findById(id);
    	render(synchronization);
    }
    
	private static String getConfiguration() {
		List<Group> groups = Group.all().fetch();
        List<Repository> repositories = Repository.all().fetch();
        
        return csvnConfig.getConfig(groups, repositories);
	}

}
