package controllers;

import java.util.List;

import models.Permission;
import models.User;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.mvc.Controller;

public class Permissions extends Controller {
    public static void index() {
        List<Permission> permissions = Permission.all().fetch();
        render(permissions);
    }

    public static void create(Permission permission) {
        render(permission);
    }

    public static void show(Long id) {
        Permission permission = Permission.findById(id);
        render(permission);
    }

    public static void edit(Long id) {
        Permission permission = Permission.findById(id);
        render(permission);
    }

    public static void delete(Long id) {
        Permission permission = Permission.findById(id);
        permission.delete();
        index();
    }
    
    public static void save(@Valid Permission permission) {
    	String error = Messages.get("validation");
    	if (! validation.hasErrors()) {
    		try {
    			permission.save();
    			flash.success(Messages.get("created", "Permission"));
    			index();
    		}
    		catch (Exception e) {
    			error = e.getMessage();
    		}
        }
    	flash.error(error);
    	render("@create", permission);
    }

    public static void update(@Valid Permission permission) {
    	String error = Messages.get("validation");
    	if (! validation.hasErrors()) {
    		try {
    			permission.save();
    			flash.success(Messages.get("updated", "Permission"));
    			index();
    		}
    		catch (Exception e) {
    			error = e.getMessage();
    		}
        }
    	flash.error(error);
    	render("@edit", permission);
    }
}
