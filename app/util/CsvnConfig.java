package util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import models.Group;
import models.Permission;
import models.User;

public class CsvnConfig {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	
	public String getGroupsAndUsersLines(List<Group> groups) {
		StringBuffer config = new StringBuffer();
		config.append("[groups]" + LINE_SEPARATOR);
		String users = null;
		for (Group group : groups) {
			if (hasUsersIn(group)) {
				users = getUsersLine(group.users);
				config.append(group.name + "=" + users + LINE_SEPARATOR);
			}
		}
		
		return config.toString();
	}

	public String getPermissionsLines(List<Permission> permissions) {
		StringBuffer config = new StringBuffer();

		for (Permission permission : permissions) {
			if (hasUsersIn(permission.group)) {
				if ("/".equals(permission.repository.name)) {
					config.append("[/]" + LINE_SEPARATOR);
					config = addPermissionLine(config, permission, permission.root);
				}
				else {
				
					if (isNotNull(permission.root)) {
						config = addReposLine(config, permission, "/");
						config = addPermissionLine(config, permission, permission.root);
					}
					
					if (isNotNull(permission.trunk)) {
						config = addReposLine(config, permission, "/trunk");
						config = addPermissionLine(config, permission, permission.trunk);
					}
					
					if (isNotNull(permission.branches)) {
						config = addReposLine(config, permission, "/branches");
						config = addPermissionLine(config, permission, permission.branches);
					}
				}
			}
		}

		return config.toString();
	}
	
	public String getConfig(List<Group> groups, List<Permission> permissions) {
		return this.getGroupsAndUsersLines(groups) + this.getPermissionsLines(permissions);
	}
	
	public String getConfigurationFromFile(String filePath) throws IOException {
		byte[] buffer = new byte[(int) new File(filePath).length()];
	    BufferedInputStream file = null;
	    try {
	        file = new BufferedInputStream(new FileInputStream(filePath));
	        file.read(buffer);
	    }
	    finally {
	        try {
	        	file.close();
	        }
	        catch (Exception ignored) {}
	        
	    }
	    return new String(buffer);
	}

	public Boolean saveConfigurationToFile(String configuration, String filePath) {
		try {
			FileUtils.writeStringToFile(new File(filePath), configuration, "UTF-8");
			return true;
		} catch (IOException e) {
			return false;
		}
	}
	
	private StringBuffer addPermissionLine(StringBuffer config, Permission permission, String value) {
		return config.append("@" + permission.group.name + "=" + value + LINE_SEPARATOR);
	}

	private StringBuffer addReposLine(StringBuffer config, Permission permission, String repos) {
		return config.append("[" + permission.repository.name + ":" + repos + "]" + LINE_SEPARATOR);
	}

	private boolean isNotNull(String value) {
		return (value != null && ! value.isEmpty());
	}

	private String getUsersLine(List<User> users) {
		StringBuffer config = new StringBuffer();
		for (User user : users) {
			config.append(user.login + ",");
		}
		return removeLastColon(config);
	}

	private String removeLastColon(StringBuffer config) {
		if (config.length() > 0) {
			return config.deleteCharAt(config.length() - 1).toString();
		}
		return config.toString();
	}
	
	private boolean hasUsersIn(Group group) {
		return group.users != null && group.users.size() > 0;
	}
}
