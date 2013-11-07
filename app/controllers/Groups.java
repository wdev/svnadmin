package controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import models.Group;
import models.User;

import org.apache.commons.lang.StringUtils;

import play.data.validation.Valid;
import play.i18n.Messages;
import play.mvc.Controller;
import util.FormatterUtil;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class Groups extends Controller {
    public static void index() {
        long total = Group.count();
        render(total);
    }
    
    public static void filter(String value) {
        if (StringUtils.isNotBlank(value)) {
            JsonArray array = new JsonArray();
            final String name = "%" + value + "%"; 
            List<Group> groups = Group.find("byNameLike", name).fetch();
            for (Group group : groups) {
                JsonObject json = new JsonObject();
                json.addProperty("id", group.id);
                json.addProperty("name", group.name);
                json.addProperty("users", getUsersFrom(group));
                array.add(json);
            }
            renderJSON(array);
        }
    }

    private static String getUsersFrom(Group group) {
        Set<User> users = group.users;
        StringBuffer stb = new StringBuffer();
        for (User user : users) {
            stb.append(user.login + " ");
        }
        return stb.toString();
    }

    public static void create(Group group) {
        render(group);
    }

    public static void show(Long id) {
        Group group = Group.findById(id);
        render(group);
    }

    public static void edit(Long id) {
        Group group = Group.findById(id);
        render(group);
    }

    public static void delete(Long id) {
        Group group = Group.findById(id);
        group.delete();
        index();
    }
    
    public static void save(@Valid Group group) {
    	String error = Messages.get("validation");
    	validation.required("required", group.name);
    	validation.required("required", group.logins);
    	
    	if (! validation.hasErrors()) {
    		try {
    		    group.name = FormatterUtil.formatName(group.name);
    		    group.users = getOrCreateUsers(new HashSet<User>(), group.logins);
    			group.save();
    			flash.success(Messages.get("created", "Group"));
    			index();
    		}
    		catch (Exception e) {
    			error = e.getMessage();
    		}
        }
    	flash.error(error);
    	render("@create", group);
    }

    public static void update(@Valid Group group) {
    	String error = Messages.get("validation");
    	if (! validation.hasErrors()) {
    		try {
    		    if (! "".equals(group.logins)) {
    		        group.users = getOrCreateUsers(group.users, group.logins);
    		    }
    		    group.name = FormatterUtil.formatName(group.name);
    			group.save();
    			flash.success(Messages.get("updated", "Group"));
    			index();
    		}
    		catch (Exception e) {
    			error = e.getMessage();
    		}
        }
    	flash.error(error);
    	render("@edit", group);
    }
    
    private static Set<User> getOrCreateUsers(Set<User> users, String logins) {
        logins = logins.replace(" ", "");
        String[] vector = logins.split(",");
        for (String login : vector) {
            User user = User.find("byLogin", login).first();
            if (user == null) {
                user = new User();
                user.login = login.toUpperCase();
                user.save();
            }
            users.add(user);
        }
        return users;
    }
}
