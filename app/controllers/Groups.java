package controllers;

import java.util.ArrayList;
import java.util.List;

import models.Group;
import models.User;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.mvc.Controller;
import util.FormatterUtil;

public class Groups extends Controller {
    public static void index() {
        List<Group> groups = Group.all().fetch();
        render(groups);
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
    		    group.users = getOrCreateUsers(new ArrayList<User>(), group.logins);
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
    
    private static List<User> getOrCreateUsers(List<User> users, String logins) {
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
