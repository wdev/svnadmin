package controllers;

import java.util.HashMap;
import java.util.Map;

import models.Group;
import models.Permission;
import models.Repository;

import org.junit.Before;
import org.junit.Test;

import play.mvc.Http.Response;
import play.test.Fixtures;
import play.test.FunctionalTest;

public class PermissionsTest extends FunctionalTest {

    private Repository repository;
    private Group group;
    
    @Before
    public void setUp() {
        Fixtures.deleteAllModels();
        Fixtures.loadModels("data.yml");
        repository = Repository.find("byName", "paol_admin").first();
        group = Group.find("byName", "paol_admin").first();
    }
    
    @Test
    public void testFilterByGroupNameAndRepositoryName() {
        Response response = GET("/permissions/filter?value=" + group.name);
        
        assertEquals("application/json; charset=utf-8", response.contentType);
        assertEquals("{\"elements\":[{\"id\":93,\"root\":\"r\",\"trunk\":\"rw\",\"branches\":\"\",\"tags\":\"\",\"group\":\"paol_admin\",\"repository\":\"paol_admin\"}]}", response.out.toString());
    }
    
    @Test
    public void testAddRepositoryPermissionToAnotherGroup() {
        group = Group.find("byName", "lexw_infraestrutura").first();
        
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("permission.repository.id", repository.id.toString());
        parameters.put("permission.group.id", group.id.toString());
        parameters.put("permission.root", "r");
        parameters.put("permission.trunk", "rw");
        
        POST("/permissions/save", parameters);
        
        Permission permission = getPermission(repository, group);
        assertEquals("r", permission.root);
        assertEquals("rw", permission.trunk);
        assertNull(permission.branches);
        assertNull(permission.tags);
    }
    
    @Test
    public void testDelete() {
        Permission permission = getPermission(repository, group);
        GET("/permissions/delete?id=" + permission.id);
        
        assertNull(getPermission(repository, group));
    }
    
    @Test
    public void testDeletePermissionWhenDeleteGroupToo() {
        Permission permission = getPermission(repository, group);
        group.delete();
        assertNull(Permission.findById(permission.id));
    }
    
    @Test
    public void testDeletePermissionWhenDeleteRepositoryToo() {
        Permission permission = getPermission(repository, group);
        repository.delete();
        assertNull(Permission.findById(permission.id));
    }
    
    
    private Permission getPermission(Repository repository, Group group) {
        return Permission.find("byRepositoryAndGroup", repository, group).first();
    }
}
