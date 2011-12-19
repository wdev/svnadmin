package util;
import org.junit.*;
import org.tmatesoft.svn.cli.SVNAdmin;
import org.tmatesoft.svn.cli.svn.SVNAddCommand;
import org.tmatesoft.svn.cli.svn.SVNMkDirCommand;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.SVNObjectsPool;
import org.tmatesoft.svn.core.internal.wc.admin.SVNAdminArea;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import java.io.File;
import java.util.*;
import play.test.*;
import util.SvnUtil;
import models.*;

public class SvnUtilTest extends UnitTest {

	private SvnUtil svnUtil;
	private SVNURL repositoryUrl;

	@Before
	public void setUp() {
		svnUtil = new SvnUtil();
		svnUtil.deleteLocalRepository("myrepository");
		repositoryUrl = svnUtil.createLocalRepository("myrepository");
	}

	@Test
	public void testCreateRepository() {
		assertNotNull(repositoryUrl);
		assertTrue(new File("tmp/myrepository").isDirectory());
	}
	
	@Test
	public void testCreateTrunkDirectory() {
		try {
			svnUtil.createDir("trunk", repositoryUrl);
		}
		catch (SVNException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test(expected=SVNException.class)
	public void testFailWhenCreateTrunkDirectoryIfItAlreadyExists() throws SVNException {
		svnUtil.createDir("trunk", repositoryUrl);
		svnUtil.createDir("trunk", repositoryUrl);
	}
	
	@Test
	public void testCreateEjbClientSubDirectoryWithinTrunk() {
		try {
			svnUtil.createDir("trunk", repositoryUrl);
			
			SVNURL url = SVNURL.parseURIEncoded(repositoryUrl + "/trunk");
			svnUtil.createDir("ejbClient", url);
		}
		catch (SVNException e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testRepositoryExists() {
		assertTrue(svnUtil.getRepositoryDir("myrepository").isDirectory());
	}
	

}
