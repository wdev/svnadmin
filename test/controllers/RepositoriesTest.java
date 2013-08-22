package controllers;

import java.util.HashMap;
import java.util.Map;

import models.Category;
import models.Repository;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.FunctionalTest;

public class RepositoriesTest extends FunctionalTest {

    private Repository repository;
    private Category category;
    
    @Before
    public void setUp() {
        Fixtures.deleteAllModels();
        Fixtures.loadModels("data.yml");
        repository = Repository.find("byName", "paol_admin").first();
        category = Category.find("byDescricao", "BSAD2").first();
    }
    
    @Test
    public void testSaveReplacingDashToUnderscore() {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("repository.name", "xp-to");
        parameters.put("repository.category.id", category.id.toString());
        
        POST("/repositories/save", parameters);
        
        assertNotNull(getRepository("xp_to"));
    }
    
    @Test
    public void testDelete() {
        GET("/repositories/delete?id=" + repository.id);
        
        assertNull(getRepository(repository.name));
    }
    
    @Test
    public void testUpdate() {
        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put("repository.id", repository.id.toString());
        parameters.put("repository.name", "xpto");
        parameters.put("repository.category.id", category.id.toString());
        
        POST("/repositories/update", parameters);
        
        assertNotNull(getRepository("xpto"));
    }
    
    private Repository getRepository(String name) {
        return Repository.find("byName", name).first();
    }
}
