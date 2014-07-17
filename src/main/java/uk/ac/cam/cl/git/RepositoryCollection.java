/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.git;

import java.util.Iterator;

import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;
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
     * @throws DuplicateKeyException A repository with this name already
     * exists.
     */
    public void insertRepo(Repository repo);

    /**
     * Updates the given repository.
     *
     * @param repo The updated repository (there must also be a
     * repository by this name).
     * @throws MongoException If the update operation fails (for some
     * unknown reason). FIXME: should be a more general exception
     */
    public void updateRepo(Repository repo);

    /**
     * Returns an iterator containing all the repository objects in the collection
     *
     * @return Iterator of repository objects in the collection
     */
    public Iterator<Repository> findAll();

    /**
     * Returns the repository object with the given name in the
     * database collection.
     * 
     * @param name The name of the repository
     * @return The requested repository object
     */
    public Repository findByName(String name);    

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
    public void removeByName(String name);
}
