package util;

import java.io.File;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.ISVNSession;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import play.Play;
import play.vfs.VirtualFile;

public class SvnUtil {

	public SVNURL createLocalRepository(String name) {
		try {
			FSRepositoryFactory.setup();
			File repository = this.getRepositoryDir(name);
			return SVNRepositoryFactory.createLocalRepository(repository, true, false);
		}
		catch (SVNException e) {}
		
		return null;
	}
	
	public void deleteLocalRepository(String name) {
		File repository = getRepositoryDir(name);
		deleteDir(repository);
	}
	
	public void createDir(String name, SVNURL repositoryUrl) throws SVNException {
		SVNCommitClient commitClient = new SVNCommitClient(SVNWCUtil.createDefaultAuthenticationManager(), null);
		SVNURL[] urls = { SVNURL.parseURIEncoded("file://" + repositoryUrl.getPath() + "/" + name) };
		commitClient.doMkDir(urls, "Added directory " + name);
	}

	public File getRepositoryDir(String name) {
		String svnBase = Play.configuration.getProperty("svn.repo.base");
		return new File(svnBase + "/" + name);
	}
	
	public void deleteDir(File path) {
		if (path.isDirectory()) {
			if (path.list().length == 0) {
				path.delete();
			}
			else {
				String files[] = path.list();
				for (String temp : files) {
					File fileDelete = new File(path, temp);
					deleteDir(fileDelete);
				}
				if (path.list().length == 0) {
					path.delete();
				}
			}
		}
		else {
			path.delete();
		}
	}
	

}
