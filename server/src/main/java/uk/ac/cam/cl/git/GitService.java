/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git;

import java.io.IOException;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;

import uk.ac.cam.cl.git.api.RepoUserRequestBean;
import uk.ac.cam.cl.git.api.DuplicateRepoNameException;
import uk.ac.cam.cl.git.api.ForkRequestBean;
import uk.ac.cam.cl.git.api.HereIsYourException;
import uk.ac.cam.cl.git.api.RepositoryNotFoundException;
import uk.ac.cam.cl.git.api.Commit;
import uk.ac.cam.cl.git.api.FileBean;
import uk.ac.cam.cl.git.configuration.ConfigurationLoader;
import uk.ac.cam.cl.git.interfaces.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the concrete implementation of WebInterface, used on the
 * server to serve repos, files, add SSH keys, etc.
 *
 * @author Kovacsics Robert &lt;rmk35@cam.ac.uk&gt;
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */

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
    public List<Commit> listCommits(String repoName) throws IOException, RepositoryNotFoundException
    {
        if (repoName == null)
            throw new RepositoryNotFoundException("No repository given.");

        Repository repo = ConfigDatabase.instance().getRepoByName(repoName);
        if (repo == null)
            throw new RepositoryNotFoundException("Repository not found in database! "
                        + "It may exist on disk though.");

        try
        {
            repo.openLocal(repoName); /* throws IOException */
        }
        catch (org.eclipse.jgit.errors.RepositoryNotFoundException e)
        {
            /* Dangling repository entry, remove from database */
            ConfigDatabase.instance().delRepoByName(repoName);
            throw new RepositoryNotFoundException("Repository not found on disk! "
                        + "Removed from database.");
        }

        return repo.listCommits();
    }

    @Override
    public String resolveCommit(String repoName, String commitName)
        throws IOException, RepositoryNotFoundException
    {
        if (repoName == null)
            throw new RepositoryNotFoundException("No repository given.");

        Repository repo = ConfigDatabase.instance().getRepoByName(repoName);
        if (repo == null)
            throw new RepositoryNotFoundException("Repository not found in database! "
                        + "It may exist on disk though.");

        try
        {
            repo.openLocal(repoName); /* throws IOException */
        }
        catch (org.eclipse.jgit.errors.RepositoryNotFoundException e)
        {
            /* Dangling repository entry, remove from database */
            ConfigDatabase.instance().delRepoByName(repoName);
            throw new RepositoryNotFoundException("Repository not found on disk! "
                        + "Removed from database.");
        }

        return repo.resolveCommit(commitName);
    }

    @Override
    public List<String> listFiles(String repoName, String commitID)
        throws IOException, RepositoryNotFoundException
    {
        if (repoName == null)
            throw new RepositoryNotFoundException("No repository given.");

        Repository repo = ConfigDatabase.instance().getRepoByName(repoName);
        if (repo == null)
            throw new RepositoryNotFoundException("Repository not found in database! "
                        + "It may exist on disk though.");

        try
        {
            repo.openLocal(repoName, commitID); /* throws IOException */
        }
        catch (org.eclipse.jgit.errors.RepositoryNotFoundException e)
        {
            /* Dangling repository entry, remove from database */
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
                        , String commitID
                        , String repoName)
        throws IOException, RepositoryNotFoundException
    {
        if (repoName == null)
            throw new RepositoryNotFoundException("No repository given.");

        Repository repo = ConfigDatabase.instance().getRepoByName(repoName);
        if (repo == null)
            throw new RepositoryNotFoundException("Repository not found in database! "
                    + "It may exist on disk though.");

        try
        {
            repo.openLocal(repoName, commitID);
        }
        catch (org.eclipse.jgit.errors.RepositoryNotFoundException e)
        {
            /* Dangling repository entry, remove from database */
            ConfigDatabase.instance().delRepoByName(repoName);
            throw new RepositoryNotFoundException("Repository not found on disk! "
                    + "Removed from database.");
        }

        return repo.getFile(fileName);
    }

    @Override
    public List<FileBean> getAllFiles(String repoName, String commitID)
        throws IOException, RepositoryNotFoundException
    {
        List<FileBean> rtn = new LinkedList<FileBean>();
        for (String file : listFiles(repoName, commitID))
        {
            rtn.add(new FileBean(file, getFile(file, commitID, repoName)));
        }

        return rtn;
    }
                                    

    @Override
    public String forkRepository(ForkRequestBean details) throws IOException, DuplicateRepoNameException
    {
        log.debug("Forking repository \"" + details.getRepoName() + ".git\""
                + " to \"" + details.getNewRepoName() + ".git\""
                + " for user \"" + details.getUserName() + "\"");
        Repository rtn = new Repository(details.getNewRepoName()
                                      , details.getUserName()
                                      , null /* RW */
                                      , null /* RO */
                                      , details.getRepoName()
                                      , details.getOverlay());
        ConfigDatabase.instance().addRepo(rtn);
        while (!rtn.repoExists())
        {
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                /* If we woke up earlier from sleep than expected,
                 * continue to check for repo existence.
                 */
            }
        }
        rtn.cloneParent();
        /* No need to return only repo-name, as we either had it given
         * to us, or we created it in a known manner.
         */
        log.debug("Forked repository \"" + details.getRepoName() + ".git\""
                + " to \"" + details.getNewRepoName() + ".git\""
                + " for user \"" + details.getUserName() + "\"");
        return rtn.getRepoPath();
    }

    @Override
    public String addRepository(RepoUserRequestBean details) throws IOException, DuplicateRepoNameException
    { /* Triggers compile don't work */
        log.debug("Creating new repository \"" + details.getRepoName()
                + ".git\"" + " for user \"" + details.getUserName()
                + "\"");
        Repository rtn = new Repository(details.getRepoName()
                                      , details.getUserName()
                                      , null
                                      , null);
        ConfigDatabase.instance().addRepo(rtn);
        /* No need to return repo-name as we had it given to us.
         */
        log.debug("Created new repository \"" + details.getRepoName()
                + ".git\"" + " for user \"" + details.getUserName()
                + "\"");
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
    public String getRepoURI(String repoName) throws RepositoryNotFoundException {
        return ConfigDatabase.instance()
                .getRepoByName(repoName)
                .getRepoPath();
    }

    @Override
    public void addReadOnlyUser(RepoUserRequestBean details) throws IOException, RepositoryNotFoundException {
        Repository repo = ConfigDatabase.instance()
                .getRepoByName(details.getRepoName());
        repo.addReadOnlyUser(details.getUserName());
        ConfigDatabase.instance().updateRepo(repo);
    }

    @Override
    public List<String> getDanglingRepos() throws IOException
    {
        /* Just to be sure! */
        ConfigDatabase.instance().generateConfigFile();

        return Arrays.asList(ConfigDatabase.instance().listDanglingRepositories());
    }

    @Override
    public void removeDanglingRepos() throws IOException
    {
        /* Just to be sure! */
        ConfigDatabase.instance().generateConfigFile();

        for (String repo : ConfigDatabase.instance()
                            .listDanglingRepositories())
        {
            log.info("Removing (dangling) repository \"" + repo + "\"!");
            recursiveDelete(new File(
                        ConfigurationLoader.getConfig()
                            .getGitoliteHome() + "/repositories/"
                                + repo + ".git"));

            log.info("Removed (dangling) repository \"" + repo + "\"!");
        }
    }

    /*
     * Helper function to remove directories recursively
     */
    private static void recursiveDelete(File f)
    {
        if (f.isDirectory())
        {
            if (f.list().length == 0)
                f.delete();
            else
            {
                for (String child : f.list())
                    recursiveDelete(new File(f, child));
                f.delete();
            }

        }
        else
        {
            f.delete();
        }
    }

    /**
     * Rebuilds database from gitolite configuration file, in case the
     * two become out of sync.
     */
    @Override
    public void rebuildDatabase() throws IOException, DuplicateRepoNameException
    {
        ConfigDatabase.instance().rebuildDatabaseFromGitolite();
    }
}
