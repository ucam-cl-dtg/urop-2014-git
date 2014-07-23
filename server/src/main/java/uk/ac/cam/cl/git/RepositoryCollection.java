/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */

package uk.ac.cam.cl.git;

import java.util.List;

import uk.ac.cam.cl.git.api.DuplicateRepoNameException;
import uk.ac.cam.cl.git.api.RepositoryNotFoundException;
/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 *
 */
public interface RepositoryCollection {

    /**
     * Adds a new repository to the database collection for inclusion in the
     * conf file when generated.
     *
     * @param repo The repository to be added
     * @throws DuplicateRepoNameException A repository with this name already
     * exists.
     */
    public void insertRepo(Repository repo) throws DuplicateRepoNameException;

    /**
     * Updates the given repository.
     *
     * @param repo The updated repository (there must also be a
     * repository by this name).
     */
    public void updateRepo(Repository repo) throws RepositoryNotFoundException;

    /**
     * Returns an list containing all the repository objects in the collection
     *
     * @return List of repository objects in the collection
     */
    public List<Repository> listRepos();

    /**
     * Returns true iff there is a repository with the given name in the collection
     */
    public boolean contains(String name);

    /**
     * Returns the repository object with the given name in the
     * database collection.
     *
     * @param name The name of the repository
     * @return The requested repository object
     */
    public Repository getRepo(String name) throws RepositoryNotFoundException;

    /**
     * Removes all repositories from the database collection.
     */
    public void removeAll();

    /**
     * Removes the repository object with the given name from the
     * database.
     *
     * @param name The name of the repository to remove
     */
    public void removeRepo(String name) throws RepositoryNotFoundException;
}
