/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.git;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * An alternative implementation of RepositoryCollection to ensure that the
 * Guice dependency injection actually does work (it does, I think).
 * 
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 *
 */
public class HashMapRepositoryCollection implements RepositoryCollection {
    

    private static HashMap<String, Repository> collection = new HashMap<String, Repository>();

    @Override
    public void insertRepo(Repository repo) throws DuplicateKeyException {
        if (collection.containsKey(repo.getName()))
            throw new DuplicateKeyException();
        collection.put(repo.getName(), repo);
    }

    @Override
    public void updateRepo(Repository repo) {
        collection.remove(repo.getName());
        collection.put(repo.getName(), repo);
    }
    
    @Override
    public boolean contains(String name) {
        return collection.containsKey(name);
    }

    @Override
    public List<Repository> findAll() {
        return new ArrayList<Repository>(collection.values());
    }

    @Override
    public Repository findByName(String name) {
        return collection.get(name);
    }

    @Override
    public void removeAll() {
        collection.clear();
    }

    @Override
    public void removeByName(String name) {
        collection.remove(name);
    }

}
