package util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import models.Group;
import models.Permission;
import models.Repository;

import org.hibernate.HibernateException;
import org.junit.Before;
import org.junit.Test;

import play.Play;
import play.test.Fixtures;
import play.test.UnitTest;

public class CsvnConfigTest extends UnitTest {
	
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	
	private CsvnConfig csvnConfig;
	
	@Before
	public void setUp() {
		Fixtures.deleteAllModels();
		Fixtures.loadModels("data.yml");
		csvnConfig = new CsvnConfig();
	}
	
	@Test
	public void testGetConfigurationGroups() {
		List<Group> groups = Group.all().fetch();
		String config = csvnConfig.getGroupsAndUsersLines(groups);
		
		String expected = "[groups]" + LINE_SEPARATOR +
		    "admin=ipsadmin" + LINE_SEPARATOR +
			"paol_admin=L568431,L966258" + LINE_SEPARATOR +
		    "inet_kitdigitalauto=L966259,L568478" + LINE_SEPARATOR +
			"gsin_gestaodesinistros=L966259,L568431" + LINE_SEPARATOR +
		    "lexw_infraestrutura=F199607" + LINE_SEPARATOR +
			"lexw_infraestrutura_branches=L791238" + LINE_SEPARATOR;

		assertEquals(expected, config);
	}
	
	@Test
	public void testGetConfigurationPermissions() {
		List<Permission> permissions = Permission.all().fetch();
		String config = csvnConfig.getPermissionsLines(permissions);
		
		String expected = "[/]" + LINE_SEPARATOR +
			"@admin=rw" + LINE_SEPARATOR +
		    "[paol_admin:/]" + LINE_SEPARATOR +
			"@paol_admin=r" + LINE_SEPARATOR +
		    "[paol_admin:/trunk]" + LINE_SEPARATOR +
			"@paol_admin=rw" + LINE_SEPARATOR +
		    "[lexw_infraestrutura:/branches]" + LINE_SEPARATOR +
			"@lexw_infraestrutura_branches=r" + LINE_SEPARATOR;

		assertEquals(expected, config);
	}
	
	@Test
	public void testGetConfigurationPermissionsAfterGroupHasBeenDeleted() {
		Group group = Group.find("byName", "paol_admin").first();
		group.delete();
		
		List<Permission> permissions = Permission.all().fetch();
		String config = csvnConfig.getPermissionsLines(permissions);
		
		String expected = "[/]" + LINE_SEPARATOR +
			"@admin=rw" + LINE_SEPARATOR +
		    "[lexw_infraestrutura:/branches]" + LINE_SEPARATOR +
			"@lexw_infraestrutura_branches=r" + LINE_SEPARATOR;

		assertEquals(expected, config);
	}
	
	@Test
	public void testGetConfigurationPermissionsAfterRepositoryHasBeenDeleted() {
		Repository repository = Repository.find("byName", "paol_admin").first();
		repository.delete();
		
		List<Permission> permissions = Permission.all().fetch();
		String config = csvnConfig.getPermissionsLines(permissions);
		
		String expected = "[/]" + LINE_SEPARATOR +
			"@admin=rw" + LINE_SEPARATOR +
		    "[lexw_infraestrutura:/branches]" + LINE_SEPARATOR +
			"@lexw_infraestrutura_branches=r" + LINE_SEPARATOR;

		assertEquals(expected, config);
	}
	
	@Test
	public void testGetConfigurationPermissionsWithoutBranches() {
		Repository repository = Repository.find("byName", "paol_admin").first();
		List<Permission> permissions = Permission.find("byRepository", repository).fetch();
		String config = csvnConfig.getPermissionsLines(permissions);
		
		String expected = "[paol_admin:/]" + LINE_SEPARATOR +
			"@paol_admin=r" + LINE_SEPARATOR +
		    "[paol_admin:/trunk]" + LINE_SEPARATOR +
			"@paol_admin=rw" + LINE_SEPARATOR;

		assertEquals(expected, config);
	}
	
	@Test
	public void testFailTryingToDuplicatePermission() {
		Permission permission = new Permission();
		permission.group = Group.find("byName", "paol_admin").first();
		permission.repository = Repository.find("byName", "paol_admin").first();
		try {
			permission.save();
			fail();
		}
		catch (Exception e) {}
	}
	
	@Test
	public void testGetConfigurationTextFromPermissionsWhenGroupDontHaveUsers() {
		List<Group> groups = Group.all().fetch();
		List<Permission> permissions = Permission.all().fetch();
		
		String config = csvnConfig.getConfig(groups, permissions);
		
		String expected = "[groups]" + LINE_SEPARATOR +
			    "admin=ipsadmin" + LINE_SEPARATOR +
				"paol_admin=L568431,L966258" + LINE_SEPARATOR +
			    "inet_kitdigitalauto=L966259,L568478" + LINE_SEPARATOR +
				"gsin_gestaodesinistros=L966259,L568431" + LINE_SEPARATOR +
			    "lexw_infraestrutura=F199607" + LINE_SEPARATOR +
				"lexw_infraestrutura_branches=L791238" + LINE_SEPARATOR +
				"[/]" + LINE_SEPARATOR +
				"@admin=rw" + LINE_SEPARATOR +
			    "[paol_admin:/]" + LINE_SEPARATOR +
				"@paol_admin=r" + LINE_SEPARATOR +
			    "[paol_admin:/trunk]" + LINE_SEPARATOR +
				"@paol_admin=rw" + LINE_SEPARATOR +
			    "[lexw_infraestrutura:/branches]" + LINE_SEPARATOR +
				"@lexw_infraestrutura_branches=r" + LINE_SEPARATOR;

		assertEquals(expected, config);
	}
	
	@Test
	public void testGetConfigurationFromFile() throws IOException {
		String expected = "[groups]" + LINE_SEPARATOR +
			"bsad_framework=L4343435" + LINE_SEPARATOR +
			"paol_admin=L909090,L4343435" + LINE_SEPARATOR +
			"[bsad_framework:/]" + LINE_SEPARATOR +
			"@bsad_framework=r" + LINE_SEPARATOR +
			"[bsad_framework:/trunk]" + LINE_SEPARATOR +
			"@bsad_framework=rw" + LINE_SEPARATOR +
			"[paol_admin:/]" + LINE_SEPARATOR +
			"@paol_admin=r" + LINE_SEPARATOR +
			"[paol_admin:/trunk]" + LINE_SEPARATOR +
			"@paol_admin=rw" + LINE_SEPARATOR;
		
		String filePath = Play.configuration.getProperty("csvn.config.path");
		String config = csvnConfig.getConfigurationFromFile(filePath);
		assertEquals(expected, config);
	}
	
	@Test
	public void testSaveConfigurationToFile() throws IOException {
		String configuration = "[groups]" + LINE_SEPARATOR +
			"lexw_fddfd=L4343435" + LINE_SEPARATOR +
			"bsad_framework=L4343435,L4343535" + LINE_SEPARATOR +
			"[lexw_teste:/]" + LINE_SEPARATOR +
			"@lexw_fddfd=r" + LINE_SEPARATOR +
			"[lexw_teste:/trunk]" + LINE_SEPARATOR +
			"@lexw_fddfd=rw" + LINE_SEPARATOR +
			"[lexw_teste:/branches]" + LINE_SEPARATOR +
			"@lexw_fddfd=r" + LINE_SEPARATOR +
			"[bsad_framework:/]" + LINE_SEPARATOR +
			"@bsad_framework=r" + LINE_SEPARATOR +
			"[bsad_framework:/trunk]" + LINE_SEPARATOR +
			"@bsad_framework=rw" + LINE_SEPARATOR;
		
		String filePath = "tmp/svn_access_file";
		assertTrue(csvnConfig.saveConfigurationToFile(configuration, filePath));
		assertTrue(new File(filePath).isFile());
	}
	
}
