package controllers;

import java.util.List;

import models.Group;
import models.User;
import play.data.validation.Valid;
import play.i18n.Messages;
import play.mvc.Controller;

public class Users extends Controller {

    public static void edit(Long id) {
        User user = User.findById(id);
        render(user);
    }

    public static void delete(Long id) {
        User user = User.findById(id);
        user.delete();
        redirect("/groups/index");
    }
    
    public static void update(@Valid User user) {
    	String error = Messages.get("validation");
    	if (! validation.hasErrors()) {
    		try {
    		    user.save();
    			flash.success(Messages.get("updated", "User"));
    			redirect("/groups/index");
    		}
    		catch (Exception e) {
    			error = e.getMessage();
    		}
        }
    	flash.error(error);
    	render("@edit", user);
    }

}
