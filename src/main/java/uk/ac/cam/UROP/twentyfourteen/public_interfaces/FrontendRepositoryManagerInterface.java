/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.UROP.twentyfourteen.public_interfaces;

/**
 * @author kr2
 *
 */
public interface FrontendRepositoryManagerInterface
{
    /**
     * Creates a new Repository.
     * <p>
     * For the moment it is world read/writeable, this will change
     * 
     * @param name Name of the repository to be created
     *  
     * @return A newly created repository.
     */
    public FrontendRepositoryInterface newRepo(String name);

    /**
     * Forks the appropriate tick repository, including the files that the student needs to access only. 
     * 
     * @param name Name of the repository to be created
     * @param origin Repository to be forked
     * 
     * @returns A forked repository.
     */
    public FrontendRepositoryInterface forkRepo(String name, FrontendRepositoryInterface origin);
}
