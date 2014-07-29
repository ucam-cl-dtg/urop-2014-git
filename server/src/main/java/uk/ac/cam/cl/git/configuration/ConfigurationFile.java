/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git.configuration;

/**
 * @author kr2
 *
 */
public class ConfigurationFile
{
    private String repoHost = "puppy44.dtg.cl.cam.ac.uk";
    private String repoUser = "gitolite3";
    private String repoDatabase = "gitRepos";
    private String gitoliteGeneratedConfigFile = System.getProperty("user.home") + "/urop_gitolite.conf";
    private String gitoliteSSHKeyLocation = System.getProperty("user.home") + "/.gitolite/keydir/UROP/";
    private String gitoliteHome = "/var/lib/gitolite3";
    private String gitolitePath = "/bin:/usr/bin";
    private String gitoliteLibdir = "/usr/share/gitolite3/lib";
    private String sshPrivateKeyFile = System.getProperty("user.home") + "/.ssh/id_rsa";
    private String knownHostsFile = System.getProperty("user.home") + "/.ssh/known_hosts";

    /**
     * @return the repoHost
     */
    public String getRepoHost()
    {
        return repoHost;
    }

    /**
     * @param repoHost the repoHost to set
     */
    public void setRepoHost(String repoHost)
    {
        this.repoHost = repoHost;
    }

    /**
     * @return the repoUser
     */
    public String getRepoUser()
    {
        return repoUser;
    }

    /**
     * @param repoUser the repoUser to set
     */
    public void setRepoUser(String repoUser)
    {
        this.repoUser = repoUser;
    }

    /**
     * @return the repoDatabase
     */
    public String getRepoDatabase()
    {
        return repoDatabase;
    }

    /**
     * @param repoDatabase the repoDatabase to set
     */
    public void setRepoDatabase(String repoDatabase)
    {
        this.repoDatabase = repoDatabase;
    }

    /**
     * @return the gitoliteGeneratedConfigFile
     */
    public String getGitoliteGeneratedConfigFile()
    {
        return gitoliteGeneratedConfigFile;
    }

    /**
     * @param gitoliteGeneratedConfigFile the gitoliteGeneratedConfigFile to set
     */
    public void setGitoliteGeneratedConfigFile(String gitoliteGeneratedConfigFile)
    {
        this.gitoliteGeneratedConfigFile = gitoliteGeneratedConfigFile;
    }

    /**
     * @return the gitoliteSSHKeyLocation
     */
    public String getGitoliteSSHKeyLocation()
    {
        return gitoliteSSHKeyLocation;
    }

    /**
     * @param gitoliteSSHKeyLocation the gitoliteSSHKeyLocation to set
     */
    public void setGitoliteSSHKeyLocation(String gitoliteSSHKeyLocation)
    {
        this.gitoliteSSHKeyLocation = gitoliteSSHKeyLocation;
    }

    /**
     * @return the gitoliteHome
     */
    public String getGitoliteHome()
    {
        return gitoliteHome;
    }

    /**
     * @param gitoliteHome the gitoliteHome to set
     */
    public void setGitoliteHome(String gitoliteHome)
    {
        this.gitoliteHome = gitoliteHome;
    }

    /**
     * @return the gitolitePath
     */
    public String getGitolitePath()
    {
        return gitolitePath;
    }

    /**
     * @param gitolitePath the gitolitePath to set
     */
    public void setGitolitePath(String gitolitePath)
    {
        this.gitolitePath = gitolitePath;
    }

    /**
     * @return the gitoliteLibdir
     */
    public String getGitoliteLibdir()
    {
        return gitoliteLibdir;
    }

    /**
     * @param gitoliteLibdir the gitoliteLibdir to set
     */
    public void setGitoliteLibdir(String gitoliteLibdir)
    {
        this.gitoliteLibdir = gitoliteLibdir;
    }

    /**
     * @return the sshPrivateKeyFile
     */
    public String getSshPrivateKeyFile()
    {
        return sshPrivateKeyFile;
    }

    /**
     * @param sshPrivateKeyFile the sshPrivateKeyFile to set
     */
    public void setSshPrivateKeyFile(String sshPrivateKeyFile)
    {
        this.sshPrivateKeyFile = sshPrivateKeyFile;
    }

    /**
     * @return the knownHostsFile
     */
    public String getKnownHostsFile()
    {
        return knownHostsFile;
    }

    /**
     * @param knownHostsFile the knownHostsFile to set
     */
    public void setKnownHostsFile(String knownHostsFile)
    {
        this.knownHostsFile = knownHostsFile;
    }
}
