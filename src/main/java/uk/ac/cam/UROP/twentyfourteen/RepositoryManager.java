/* vim: set et ts=4 sts=4 sw=4 tw=72 */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.UROP.twentyfourteen;

import uk.ac.cam.UROP.twentyfourteen.public_interfaces.*;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 * @author Kovacsics Robert &lt;rmk35@cam.ac.uk&gt;
 * @version 0.1
 */
public class RepositoryManager implements FrontendRepositoryManagerInterface
{
    /**
     * Lists all the available repositories.
     *
     * @return List of repositories.
     */
    public Collection<Repository> listRepositories()
    {
        return ConfigDatabase.getRepos();
    }

    /**
     * Creates a new Repository.
     * 
     * @param name Name of the repository to be created
     *  
     * @return A newly created repository.
     */
    public Repository newRepo(String name, String crsid) throws IOException
    {
        Repository rtn = new Repository(name, crsid, null, null, null, null);
        ConfigDatabase.addRepo(rtn);
        return rtn;
    }
    
    
    /**
     * Forks the appropriate tick repository, including the files that the student needs to access only. 
     * 
     * @param name Name of the repository to be created
     * @param origin Repository to be forked
     * @param origin_hidden Repository to be overlaid on submission (but not visible to student)
     * 
     * @return A forked repository.
     */
    public Repository forkRepo(String name, String origin, String origin_hidden, String crsid) throws IOException
    {
        Repository rtn = new Repository(name, crsid, null, null, origin, origin_hidden);
        ConfigDatabase.addRepo(rtn);
        return rtn;
    }
}
