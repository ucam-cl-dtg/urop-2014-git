/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;

import javax.ws.rs.PathParam;

import uk.ac.cam.cl.git.api.RepoUserRequestBean;
import uk.ac.cam.cl.git.api.DuplicateRepoNameException;
import uk.ac.cam.cl.git.api.ForkRequestBean;
import uk.ac.cam.cl.git.api.HereIsYourException;
import uk.ac.cam.cl.git.api.RepositoryNotFoundException;
import uk.ac.cam.cl.git.interfaces.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitService implements WebInterface {
    /* For logging */
    private static final Logger log = LoggerFactory.getLogger(GitService.class);
   
    @Override
    public List<String> listRepositories() {
        List<Repository> repos = ConfigDatabase.instance().getRepos();
        List<String> toReturn = new LinkedList<String>();
        for (Repository repo : repos) {
            toReturn.add(repo.getName());
        }
        return toReturn;
    }

    @Override
    public List<String> listFiles(String repoName) throws IOException, RepositoryNotFoundException
    {
        if (repoName == null)
            throw new RepositoryNotFoundException("No repository given.");

        Repository repo = ConfigDatabase.instance().getRepoByName(repoName);
        if (repo == null)
            throw new RepositoryNotFoundException("Repository not found in database! "
                        + "It may exist on disk though.");

        try
        {
            repo.openLocal(repoName);
        }
        catch (org.eclipse.jgit.errors.RepositoryNotFoundException e)
        {
            /* Stale repository entry, remove from database */
            ConfigDatabase.instance().delRepoByName(repoName);
            throw new RepositoryNotFoundException("Repository not found on disk! "
                        + "Removed from database.");
        }

        Collection<String> files = repo.getSources();
        List<String> rtn = new LinkedList<String>();
        if (files != null)
            for (String file : files)
                rtn.add(file);
        
        return rtn;
    }

    @Override
    public String getFile(String fileName
                          , String repoName) throws IOException, RepositoryNotFoundException
    {
        if (repoName == null)
            throw new RepositoryNotFoundException("No repository given.");

        Repository repo = ConfigDatabase.instance().getRepoByName(repoName);
        if (repo == null)
            throw new RepositoryNotFoundException("Repository not found in database! "
                    + "It may exist on disk though.");

        try
        {
            repo.openLocal(repoName);
        }
        catch (org.eclipse.jgit.errors.RepositoryNotFoundException e)
        {
            /* Stale repository entry, remove from database */
            ConfigDatabase.instance().delRepoByName(repoName);
            throw new RepositoryNotFoundException("Repository not found on disk! "
                    + "Removed from database.");
        }

        return repo.getFile(fileName);
    }
    
    @Override
    public String fork(ForkRequestInterface details) throws IOException, DuplicateRepoNameException
    {   /* TODO: Test */
        /* This forks the upstream repository
         * This may fail due to permissions, or the shell of tomcat7
         * Currently works with the shell `rssh' which is meant to be
         * restricted.
         */
        log.info("Forking repository \"" + details.getRepoName() + ".git\""
                + " to \"" + details.getNewRepoName() + ".git\""
                + " for user \"" + details.getRepoOwner() + "\"");
        Repository rtn = new Repository(details.getNewRepoName()
                                      , details.getRepoOwner()
                                      , null /* RW */
                                      , null /* RO */
                                      , details.getRepoName()
                                      , details.getOverlay());
        ConfigDatabase.instance().addRepo(rtn);
        // TODO: better return, e.g. NewRepoName (as repoName perhaps)
        return rtn.getRepoPath();
    }

    @Override
    public String addRepository(RepoUserRequestInterface details) throws IOException, DuplicateRepoNameException
    {
        log.info("Creating new repository \"" + details.getRepoName()
                + ".git\"" + " for user \"" + details.getUserName()
                + "\"");
        Repository rtn = new Repository(details.getRepoName()
                                      , details.getUserName()
                                      , null
                                      , null);
        ConfigDatabase.instance().addRepo(rtn);
        // TODO: better return
        return rtn.getRepoPath();
    }

    @Override
    public void deleteRepository(String repoName) throws IOException, RepositoryNotFoundException
    {
        ConfigDatabase.instance().delRepoByName(repoName);
    }
    
    @Override
    public void getMeAnException() throws HereIsYourException {
        boolean TRUE = true;
        if (TRUE)
            throw new HereIsYourException();
    }

    @Override
    public void addSSHKey(String key, String userName) throws IOException
    {
        ConfigDatabase.instance().addSSHKey(key, userName);
    }
    
    @Override
    public String getRepoURL(String repoName) throws RepositoryNotFoundException {
        return ConfigDatabase.instance()
                .getRepoByName(repoName)
                .getRepoPath();
    }
    
    @Override
    public void addReadOnlyUser(RepoUserRequestInterface details) throws IOException, RepositoryNotFoundException {
        Repository repo = ConfigDatabase.instance()
                .getRepoByName(details.getRepoName());
        repo.addReadOnlyUser(details.getUserName());
        ConfigDatabase.instance().updateRepo(repo);
    }
}
