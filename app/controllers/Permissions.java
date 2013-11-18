package controllers;

import java.util.List;

import models.Permission;

import org.apache.commons.lang.StringUtils;

import play.data.validation.Valid;
import play.i18n.Messages;
import play.mvc.Controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Permissions extends Controller {

    public static void index() {
        long total = Permission.count();
        render(total);
    }
    
    public static void filter(String value) {
        if (StringUtils.isNotBlank(value)) {
            value = value.replace("-", "_").replace(" ", "");
            JsonArray array = new JsonArray();
            final String name = "%" + value + "%"; 
            List<Permission> permissions = Permission.find(
                "select p from permissions p, groups g " +
                "where p.group.id = g.id and g.name like ?", "%" + name + "%"
            ).fetch();
            
            for (Permission permission : permissions) {
                JsonObject json = new JsonObject();
                json.addProperty("id", permission.id);
                json.addProperty("root", permission.root);
                json.addProperty("trunk", getTrunkFrom(permission));
                json.addProperty("branches", getBranchesFrom(permission));
                json.addProperty("tags", getTagsFrom(permission));
                json.addProperty("group", permission.group.name);
                json.addProperty("repository", permission.repository.name);
                array.add(json);
            }
            renderJSON(array);
        }
    }

    private static String getTagsFrom(Permission permission) {
        return blankIfNull(permission.tags);
    }

    private static String getTrunkFrom(Permission permission) {
        return blankIfNull(permission.trunk);
    }

    private static String getBranchesFrom(Permission permission) {
        return blankIfNull(permission.branches);
    }
    
    private static String blankIfNull(String value) {
        if (value == null) {
            return "";
        }
        return value;
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
