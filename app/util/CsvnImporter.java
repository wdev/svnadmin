package util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import models.Group;
import models.Permission;
import models.Repository;
import models.User;

public class CsvnImporter {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	
	public void importConfiguration(String configuration) {
		String repositoryName = "/";
		String repositoryPath = "/";
		Repository repository = null;
		
		String[] lines = configuration.split(LINE_SEPARATOR);
		for (int i=0; i<lines.length; i++) {
			String line = lines[i].trim();
			
			if (hasGroupAndUsers(line)) {
				String[] vector = line.split("=");
				String groupName = vector[0];
				Group group = this.getOrCreateGroup(groupName);
				
				String usersLogin = vector[1];
				group.users = this.getOrCreateUsers(usersLogin);
				group.save();
			}
			
			if (hasBracketsAndBar(line)) {
				String reposLine = removeBrackets(line);
				
				if (! "/".equals(reposLine)) {
					String[] repos = reposLine.split(":");
					repositoryName = repos[0];
					repositoryPath = repos[1];
				}
				repository = this.getOrCreateRepository(repositoryName);
			}
			
			if (line.startsWith("@")) {
				line = line.replace("@", "");
				String[] perms = line.split("=");
				Group group = Group.find("byName", perms[0]).first();
				Permission permission = this.getOrCreatePermission(repository, group);
				String perm = perms[1];
				
				if ("/".equals(repositoryPath)) {
					permission.root = perm;
				}
				
				if ("/trunk".equals(repositoryPath)) {
					permission.trunk = perm;
				}
				
				if ("/branches".equals(repositoryPath)) {
					permission.branches = perm;
				}
				
				if ("/tags".equals(repositoryPath)) {
					permission.tags = perm;
				}
				
				permission.save();
			}
		}
	}

	private Permission getOrCreatePermission(Repository repository, Group group) {
		Permission permission = Permission.find("byRepositoryAndGroup", repository, group).first();
		if (permission == null) {
			permission = new Permission();
			permission.group = group;
			permission.repository = repository;
		}
		return permission;
	}

	private Repository getOrCreateRepository(String name) {
		Repository repository = Repository.find("byName", name).first();
		if (repository == null) {
			repository = new Repository();
			repository.name = name;
			repository.save();
		}
		
		return repository;
	}
	
	private String removeBrackets(String line) {
		return line.replace("[", "").replace("]", "");
	}

	private boolean hasBracketsAndBar(String line) {
		return (line.startsWith("[") && line.endsWith("]") && line.contains("/"));
	}

	private boolean hasGroupAndUsers(String line) {
		return (! line.startsWith("[") && ! line.startsWith("@") && line.contains("="));
	}

	private Set<User> getOrCreateUsers(String usersLogin) {
	    Set<User> users = new HashSet<User>();
		
		String[] logins = usersLogin.replace(" ", "").split(",");
		for (String login : logins) {
			if (login.startsWith("@")) {
				String groupName = login.replace("@", "");
				Group group = this.getOrCreateGroup(groupName);
				users.addAll(this.getUsersFromGroup(group));
			}
			else {
				User user = User.find("byLogin", login).first();
				if (user == null) {
					user = new User();
					user.login = login;
					user.save();
				}
				users.add(user);
			}
		}
		return users;
	}

	private Collection<? extends User> getUsersFromGroup(Group group) {
		return group.users;
	}

	private Group getOrCreateGroup(String name) {
		Group group = Group.find("byName", name).first();
		if (group == null) {
			group = new Group();
			group.name = name;
			group.save();
		}
		return group;
	}

}
