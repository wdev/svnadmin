package controllers;

import models.Repository;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;

import play.i18n.Messages;
import play.mvc.Controller;
import util.SvnUtil;

public abstract class AbstractController extends Controller {
	
	protected static SvnUtil svnUtil = new SvnUtil();

	protected static void createRepository(Repository repository) {
    	if (! svnUtil.getRepositoryDir(repository.name).isDirectory()) {
    		try {
    			SVNURL svnUrl = svnUtil.createLocalRepository(repository.name);
				svnUtil.createDir("trunk", svnUrl);
				svnUtil.createDir("tags", svnUrl);
				svnUtil.createDir("branches", svnUrl);
				if ("BSAD2".equalsIgnoreCase(repository.category)) {
					SVNURL trunkUrl = SVNURL.parseURIEncoded(svnUrl + "/trunk");
					svnUtil.createDir("ejbClient", trunkUrl);
				}
			}
    		catch (SVNException e) {
				flash.error(Messages.get("updated", e.getErrorMessage()));
			}
        }
    }
}
