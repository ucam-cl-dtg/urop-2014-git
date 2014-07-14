/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git.public_interfaces;

import java.io.IOException;
import java.util.Collection;

/**
 * This is the interface the front end of the project should use.
 * <p>
 * For a concrete implementation
 *
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 * @author Kovacsics Robert &lt;rmk35@cam.ac.uk&gt;
 * @version 0.2
 */
public interface FrontendRepositoryManagerInterface
{
    /**
     * Creates a new Repository.
     * <p>
     * For the moment it is world read/writeable, this will change
     * 
     * @param name Name of the repository to be created
     * @param crsid The CRSID of the owned of the repository
     *  
     * @return A newly created repository.
     *
     * @throws IOException Something went wrong (typically not
     * recoverable).
     */
    public FrontendRepositoryInterface newRepo(String name, String crsid) throws IOException;

    /**
     * Lists all repositories available
     *
     * @return Available repositories
     */
    public Collection<? extends FrontendRepositoryInterface> listRepos();

    /**
     * Forks the appropriate tick repository, including the files that the student needs to access only. 
     * 
     * @param name Name of the repository to be created
     * @param origin Repository to be forked
     * @param origin_hidden Repository to be overlaid on submission (but not visible to student)
     * @param crsid The CRSID of the owned of the repository
     * 
     * @return A forked repository.
     *
     * @throws IOException Something went wrong (typically not
     * recoverable).
     */
    public FrontendRepositoryInterface forkRepo(String name, String origin, String origin_hidden, String crsid) throws IOException;
}
