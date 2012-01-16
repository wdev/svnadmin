package util;

import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import models.Group;
import models.Permission;
import models.Repository;
import models.User;

import org.junit.Before;
import org.junit.Test;

import play.Play;
import play.test.Fixtures;
import play.test.UnitTest;

public class CsvnImporterTest extends UnitTest {

	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private String configuration;
	private CsvnImporter csvnImporter;
	
	
	@Before
	public void setUp() {
		final String double_line_separator = LINE_SEPARATOR;
		
		configuration = "[groups]" + double_line_separator +
	            "admin=bsipsadmin" + double_line_separator +
	            "apoio=G003404,G431643,G454422,K590961,L736659,L749224,L862968,L955173,L972642,L976348,G558392,G558945" + double_line_separator +
	            "suporte=G454422,L966258,L966633,L568431,K834359,L967017,L966259, @apoio" + double_line_separator +
	            "paol_admin=L802684,L801743,L801747,L101559,L123781,L905026,G445164,G519044,L801559,L929387,L946794,L902306" + double_line_separator +
	            "inet_infoseguro=L568478,L966255,L967075, L982072" + double_line_separator +
	            "sisa_saude=K40135" + double_line_separator +
				"[/]" + double_line_separator +
				"@admin=rw" + double_line_separator +
	            "[/]" + double_line_separator +
	            "@suporte=r" + double_line_separator +
	            "[paol_admin:/]" + double_line_separator +
	            "@paol_admin=r" + double_line_separator +
	            "[paol_admin:/trunk]" + double_line_separator +
	            "@paol_admin=rw" + double_line_separator +
	            "[inet_infoseguro:/branches]" + double_line_separator +
	            "@inet_infoseguro=r" + double_line_separator +
	            "[sisa_saude:/]" + double_line_separator +
	            "@sisa_saude=r" + double_line_separator +
	            "[sisa_saude:/]" + double_line_separator +
	            "@suporte=r" + double_line_separator;
		
		csvnImporter = new CsvnImporter();
	}
	
	@Test
	public void testImportGroupsAndUsers() {
		csvnImporter.importConfiguration(configuration);
		
		assertEquals(6, Group.all().fetch().size());
		
		Group admin = Group.find("byName", "admin").first();
		assertNotNull(admin);
		assertEquals(1, admin.users.size());
		
		Group apoio = Group.find("byName", "apoio").first();
		assertNotNull(apoio);
		assertEquals(12, apoio.users.size());
		
		Group suporte = Group.find("byName", "suporte").first();
		assertNotNull(suporte);
		// there is 19 but the login G454422 is duplicated
		assertEquals(18, suporte.users.size());
		
		Group paol = Group.find("byName", "paol_admin").first();
		assertNotNull(paol);
		assertEquals(12, paol.users.size());
		
		Group inet = Group.find("byName", "inet_infoseguro").first();
		assertNotNull(inet);
		assertEquals(4, inet.users.size());
		
		Group sisa = Group.find("byName", "sisa_saude").first();
		assertNotNull(sisa);
		assertEquals(1, sisa.users.size());
		
		
		assertEquals(4, Repository.all().fetch().size());
		
		Repository rootRepos = Repository.find("byName", "/").first();
		assertNotNull(rootRepos);
		
		Repository paolRepos = Repository.find("byName", "paol_admin").first();
		assertNotNull(paolRepos);
		
		Repository inetRepos = Repository.find("byName", "inet_infoseguro").first();
		assertNotNull(inetRepos);
		
		Repository sisaRepos = Repository.find("byName", "sisa_saude").first();
		assertNotNull(sisaRepos);
		
		
		assertEquals(6, Permission.all().fetch().size());
		
		Permission p1 = Permission.find("byRepositoryAndGroup", rootRepos, admin).first();
		assertEquals("rw", p1.root);
		
		Permission p2 = Permission.find("byRepositoryAndGroup", rootRepos, suporte).first();
		assertEquals("r", p2.root);
		
		Permission p3 = Permission.find("byRepositoryAndGroup", paolRepos, paol).first();
		assertEquals("r", p3.root);
		assertEquals("rw", p3.trunk);
		
		Permission p4 = Permission.find("byRepositoryAndGroup", inetRepos, inet).first();
		assertEquals("r", p4.branches);
		
		Permission p5 = Permission.find("byRepositoryAndGroup", sisaRepos, sisa).first();
		assertEquals("r", p5.root);
		
		Permission p6 = Permission.find("byRepositoryAndGroup", sisaRepos, suporte).first();
		assertEquals("r", p6.root);
	}
	
}
