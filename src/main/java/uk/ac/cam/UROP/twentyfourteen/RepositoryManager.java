/* vim: set et ts=4 sts=4 sw=4 tw=72 */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.UROP.twentyfourteen;

import uk.ac.cam.UROP.twentyfourteen.public_interfaces.*;

import java.io.IOException;
import java.util.Collection;

/**
 * @author Isaac Dunn <ird28@cam.ac.uk>
 * @author Kovacsics Robert <rmk35@cam.ac.uk>
 * @version 0.1
 */
public class RepositoryManager implements FrontendRepositoryManagerInterface
{

    /**
     * Creates a new Repository.
     * 
     * @param name Name of the repository to be created
     *  
     * @return A newly created repository.
     */
    public Repository newRepo(String name, String crsid) throws IOException
    {
        /* TODO: implement
         *
         * 1) Create new GitRepo class
         * 2) Add repository to database via ConfigDatabase
         */
        return new Repository(name, crsid);
    }
    
    
    /**
     * Forks the appropriate tick repository, including the files that the student needs to access only. 
     * 
     * @param name Name of the repository to be created
     * @param origin Repository to be forked
     * @param origin_hidden Repository to be overlaid on submission (but not visible to student)
     * 
     * @returns A forked repository.
     */
    public Repository forkRepo(String name, String origin, String origin_hidden, String crsid) throws IOException
    {
        /* TODO: implement
         *
         * 1) Clone repository into new directory, with a depth of one
         * 2) Create a new GitRepo class
         */
        return new Repository(name, crsid, origin, origin_hidden);
    }

    public Collection<FrontendRepositoryInterface> listRepositories () { return null; }
}
