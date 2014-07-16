/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.git;

import java.util.Iterator;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 *
 */
public interface RepositoryCollection {

    public void insertRepo(Repository repo);

    public void updateRepo(Repository repo);

    public Iterator<Repository> findAll();

    public Repository findByName(String name);    

    public void removeAll();

    public void removeByName(String name);
}
