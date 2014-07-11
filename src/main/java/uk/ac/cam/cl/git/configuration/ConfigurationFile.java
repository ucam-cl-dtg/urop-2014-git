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
}
