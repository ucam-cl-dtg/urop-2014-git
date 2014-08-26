/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git;

import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.Collections;
import java.util.Set;
import java.util.HashSet;

import uk.ac.cam.cl.git.api.RepoUserRequestBean;
import uk.ac.cam.cl.git.api.DuplicateRepoNameException;
import uk.ac.cam.cl.git.api.ForkRequestBean;
import uk.ac.cam.cl.git.api.HereIsYourException;
import uk.ac.cam.cl.git.api.RepositoryNotFoundException;
import uk.ac.cam.cl.git.api.IllegalCharacterException;
import uk.ac.cam.cl.git.api.KeyException;
import uk.ac.cam.cl.git.api.Commit;
import uk.ac.cam.cl.git.api.FileBean;
import uk.ac.cam.cl.git.configuration.ConfigurationLoader;
import uk.ac.cam.cl.git.interfaces.*;

import com.jcraft.jsch.*;

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

    private static Set<String> sshKeyGeneration =
        Collections.synchronizedSet(new HashSet<String>());

    @Override
    public List<String> listRepositories(String securityToken)
    {
        checkSecurityToken(securityToken);
        List<Repository> repos = ConfigDatabase.instance().getRepos();
        List<String> toReturn = new LinkedList<String>();
        for (Repository repo : repos) {
            toReturn.add(repo.getName());
        }
        return toReturn;
    }

    @Override
    public List<Commit> listCommits
           (String securityToken
          , String repoName)
            throws IOException, RepositoryNotFoundException
    {
        checkSecurityToken(securityToken);
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
    public String resolveCommit
           (String securityToken
          , String repoName
          , String commitName)
            throws IOException, RepositoryNotFoundException
    {
        checkSecurityToken(securityToken);
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
    public List<String> listFiles
    (String securityToken
          , String repoName
          , String commitID)
            throws IOException, RepositoryNotFoundException
    {
        checkSecurityToken(securityToken);
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
    public String getFile
           (String securityToken
          , String fileName
          , String commitID
          , String repoName)
            throws IOException, RepositoryNotFoundException
    {
        checkSecurityToken(securityToken);
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
    public List<FileBean> getAllFiles
           (String securityToken
          , String repoName
          , String commitID)
            throws IOException, RepositoryNotFoundException
    {
        checkSecurityToken(securityToken);
        List<FileBean> rtn = new LinkedList<FileBean>();
        for (String file : listFiles(securityToken, repoName, commitID))
        {
            rtn.add(new FileBean(file, getFile(securityToken,
                                        file, commitID, repoName)));
        }

        return rtn;
    }
                                    

    @Override
    public String forkRepository
           (String securityToken
          , ForkRequestBean details)
            throws IOException, DuplicateRepoNameException,
                              IllegalCharacterException
    {
        checkSecurityToken(securityToken);
        log.debug("Forking repository \"" + details.getRepoName() + ".git\""
                + " to \"" + details.getNewRepoName() + ".git\""
                + " for user \"" + details.getUserName() + "\"");
        Repository rtn = new Repository(details.getNewRepoName()
                                      , details.getUserName()
                                      , null /* RW */
                                      , null /* RO */
                                      , details.getRepoName());
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
    public String addRepository
           (String securityToken
          , RepoUserRequestBean details)
            throws IOException, DuplicateRepoNameException,
                              IllegalCharacterException
    {
        checkSecurityToken(securityToken);
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
    public void deleteRepository
           (String securityToken
          , String repoName)
            throws IOException, RepositoryNotFoundException
    {
        checkSecurityToken(securityToken);
        ConfigDatabase.instance().delRepoByName(repoName);

        /* Delete the repository on disk */
        recursiveDelete(new File(
            ConfigurationLoader.getConfig()
                .getGitoliteHome() + "/repositories/"
                    + repoName + ".git"));
    }

    @Override
    public void getMeAnException
           (String securityToken)
            throws HereIsYourException
    {
        checkSecurityToken(securityToken);
        boolean TRUE = true;
        if (TRUE)
            throw new HereIsYourException();
    }

    @Override
    public void addSSHKey
           (String securityToken
          , String key
          , String userName)
            throws IOException, KeyException
    {
        checkSecurityToken(securityToken);
        ConfigDatabase.instance().addSSHKey(key, userName);
    }

    @Override
    public String getRepoURI
           (String securityToken
          , String repoName)
            throws RepositoryNotFoundException
    {
        checkSecurityToken(securityToken);
        return ConfigDatabase.instance()
                .getRepoByName(repoName)
                .getRepoPath();
    }

    @Override
    public void addReadOnlyUser
           (String securityToken
          , RepoUserRequestBean details)
            throws IOException, RepositoryNotFoundException
    {
        checkSecurityToken(securityToken);
        Repository repo = ConfigDatabase.instance()
                .getRepoByName(details.getRepoName());
        repo.addReadOnlyUser(details.getUserName());
        ConfigDatabase.instance().updateRepo(repo);
    }

    @Override
    public List<String> getDanglingRepos
           (String securityToken)
            throws IOException
    {
        checkSecurityToken(securityToken);
        /* Just to be sure! */
        ConfigDatabase.instance().generateConfigFile();

        return Arrays.asList(ConfigDatabase.instance().listDanglingRepositories());
    }

    @Override
    public void removeDanglingRepos
           (String securityToken)
            throws IOException
    {
        checkSecurityToken(securityToken);
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
    public void rebuildDatabase
           (String securityToken)
            throws IOException, DuplicateRepoNameException
    {
        checkSecurityToken(securityToken);
        ConfigDatabase.instance().rebuildDatabaseFromGitolite();
    }

    /**
     * Checks the security token in the configuration file against the
     * argument.
     *
     * @throws SecurityException When the token does not match or can
     * not be verified. This is a runtime exception as we can not
     * recover from this.
     */
    private void checkSecurityToken(String securityToken)
    {
        if (securityToken == null)
        {
            log.error("No securityToken query parameter given!");
            throw new SecurityException("No securityToken query parameter given!");
        }
        else if (ConfigurationLoader.getConfig()
                    .getSecurityToken() == null)
        {
            log.error("No securityToken configuration option set!");
            throw new SecurityException("No securityToken configuration option set!");
        }
        else if (!ConfigurationLoader.getConfig()
                    .getSecurityToken().equals(securityToken))
        {
            log.error("The given securityToken is invalid!\n"
                    + "Got      \"" + securityToken + "\"\n"
                    + "expected \""
                    + ConfigurationLoader.getConfig()
                        .getSecurityToken() + "\"");
            throw new SecurityException("The given securityToken is invalid!");
        }
    }

    public String getPrivateKey
           (String securityToken
          , String userName)
            throws IOException, JSchException
    {
        File privKey = new File(ConfigurationLoader.getConfig()
                .getGitoliteSSHKeyLocation() + "/" + "local/" +
                userName);
        File pubKey  = new File(ConfigurationLoader.getConfig()
                .getGitoliteSSHKeyLocation() + "/" + "local/" +
                userName + ".pub");
        JSch jsch = new JSch();

        /* Make sure for one user, only one key is being generated,
         * otherwise we will have two keys being passed back, the first
         * one being wrong.
         */
        synchronized (sshKeyGeneration)
        {
            while (sshKeyGeneration.contains(userName))
            {
                log.info("Waiting for another thread to finish with " +
                        userName);
                try
                {
                    sshKeyGeneration.wait();
                }
                catch (InterruptedException e)
                {
                    /* We will check again, by looping. */
                }
            }
            sshKeyGeneration.add(userName);
        }

        if (!privKey.exists() || !pubKey.exists())
        { /* Generate key pair */
            log.info("Generating keys for " + userName +
                    " for this machine");

            if (!pubKey.getParentFile().exists())
            {
                pubKey.getParentFile().mkdirs();
            }

            try
            {
                KeyPair keyPair = KeyPair.genKeyPair(jsch, KeyPair.RSA, 2048);
                keyPair.writePrivateKey(privKey.getAbsolutePath());
                keyPair.writePublicKey(pubKey.getAbsolutePath(),
                        "Local key for " + userName);
                keyPair.dispose();
            }
            catch (JSchException e)
            {
                privKey.delete();
                pubKey.delete();
                throw e;
            }

            log.info("Generated keys for " + userName +
                    " for this machine");
        }

        FileReader fileReader = new FileReader(privKey);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder rtn = new StringBuilder();
        String line = null;
        String lineSeparator = System.getProperty("line.separator");

        while ((line = bufferedReader.readLine()) != null)
        {
            rtn.append(line);
            rtn.append(lineSeparator);
        }

        /* Remove last line separator */
        rtn.deleteCharAt(rtn.length()-1);

        /* Tell other threads we have finished with this key */
        synchronized (sshKeyGeneration)
        {
            sshKeyGeneration.remove(userName);
            sshKeyGeneration.notify();
        }

        return rtn.toString();
    }
}
