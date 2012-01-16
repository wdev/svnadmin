package controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import models.Group;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.FunctionalTest;

public class GroupsTest extends FunctionalTest {

    private Group group;
    
    @Before
    public void setUp() {
        Fixtures.deleteAllModels();
        Fixtures.loadModels("data.yml");
        group = Group.find("byName", "gsin_gestaodesinistros").first();
    }
    
    @Test
    public void testThatIndexPageWorks() {
        Set<User> users = group.users;
        assertEquals(2, users.size());
        
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("group.id", group.id.toString());
        parameters.put("group.logins", users.iterator().next().login);
        
        POST("/groups/update", parameters);
        
        Group sameGroup = Group.find("byName", group.name).first();
        assertEquals(2, sameGroup.users.size());
    }
}
