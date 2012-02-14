package util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import models.Group;
import models.Permission;
import models.Repository;
import models.User;

import org.apache.commons.io.FileUtils;

public class CsvnConfig {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private static final String ROOT = "/";
	private static final String TRUNK = "/trunk";
	private static final String BRANCHES = "/branches";
	private static final String TAGS = "/tags";
	
	
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

	public String getPermissionsLines(List<Repository> repositories) {
		
	    Map<String, Set<String>> repos = new TreeMap<String, Set<String>>();
	    
		for (Repository repository : repositories) {
		    List<Permission> permissions = Permission.find("byRepository", repository).fetch();
		    
		    for (Permission permission : permissions) {
	            if (hasUsersIn(permission.group)) {
                    addConfigLine(ROOT, repos, permission, permission.root);
                    addConfigLine(TRUNK, repos, permission, permission.trunk);
                    addConfigLine(BRANCHES, repos, permission, permission.branches);
                    addConfigLine(TAGS, repos, permission, permission.tags);
	            }
	        }
        }
		
		return getConfigLines(repos);
	}

    private String getConfigLines(Map<String, Set<String>> repositories) {
        StringBuffer lines = new StringBuffer();
		
		Set<String> keys = repositories.keySet();
		
		for (String repository : keys) {
		    lines.append(repository);
		    
		    Set<String> permissions = repositories.get(repository);
		    for (String permission : permissions) {
                lines.append(permission);
            }
        }
        return lines.toString();
    }

	private void addConfigLine(String directory, Map<String, Set<String>> repos, Permission permission, String perm) {
	    if (isNotNull(perm)) {
            String repositoryLine = getReposLine(permission, directory);
            if (dontExistsIn(repos, repositoryLine)) {
                repos.put(repositoryLine, new TreeSet<String>());
            }
            repos.get(repositoryLine).add(getPermissionLine(permission, perm));
        }
    }
	
	private boolean dontExistsIn(Map<String, Set<String>> repos, String reposName) {
        return ! repos.containsKey(reposName);
    }

    public String getConfig(List<Group> groups, List<Repository> repositories) {
		return this.getGroupsAndUsersLines(groups) + this.getPermissionsLines(repositories);
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
	
	private String getPermissionLine(Permission permission, String value) {
		return "@" + permission.group.name + "=" + value + LINE_SEPARATOR;
	}

	private String getReposLine(Permission permission, String repos) {
	    if ("/".equals(permission.repository.name)) {
	        return "[/]" + LINE_SEPARATOR;
	    }
		return "[" + permission.repository.name + ":" + repos + "]" + LINE_SEPARATOR;
	}

	private boolean isNotNull(String value) {
		return (value != null && ! value.isEmpty());
	}

	private String getUsersLine(Set<User> users) {
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
