package controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import models.Group;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.mvc.Http.Response;
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
    public void testFilterByName() {
        Response response = GET("/groups/filter?value=" + group.name);
        
        assertEquals("application/json; charset=utf-8", response.contentType);
        assertEquals("{\"elements\":[{\"id\":74,\"name\":\"gsin_gestaodesinistros\",\"users\":\"L568431 L966259 \"}]}", response.out.toString());
    }
    
    @Test
    public void testSaveGroupWithOneUser() {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("group.name", "mygroup");
        parameters.put("group.logins", "xpto1");
        
        POST("/groups/save", parameters);
        
        assertEquals(1, getGroup("mygroup").users.size());
    }
    
    @Test
    public void testSaveGroupWithThreeUsersSeparatingByComma() {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("group.name", "mygroup");
        parameters.put("group.logins", "xpto1, xpto2, xpto3");
        
        POST("/groups/save", parameters);
        
        assertEquals(3, getGroup("mygroup").users.size());
    }
    
    @Test
    public void testDelete() {
        GET("/groups/delete?id=" + group.id);
        
        assertNull(getGroup(group.name));
    }
    
    @Test
    public void testUpdateCannotAddADuplicatedUser() {
        Set<User> users = group.users;
        assertEquals(2, users.size());
        
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("group.id", group.id.toString());
        parameters.put("group.logins", users.iterator().next().login);
        
        POST("/groups/update", parameters);
        
        assertEquals(2, getGroup(group.name).users.size());
    }
    
    private Group getGroup(String name) {
        return Group.find("byName", name).first();
    }
}
