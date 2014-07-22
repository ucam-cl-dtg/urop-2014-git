/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.git;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import uk.ac.cam.cl.git.api.DuplicateRepoNameException;
import uk.ac.cam.cl.git.api.RepositoryNotFoundException;


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
    public void insertRepo(Repository repo) throws DuplicateRepoNameException {
        if (collection.containsKey(repo.getName())) {
            try {
                throw new DuplicateRepoNameException(ConfigDatabase.instance()
                        .getRepoByName(repo.getName()).getRepoPath());
            } catch(RepositoryNotFoundException e) {
                throw new RuntimeException("This should never ever happen");
                /* This code only runs there both is and isn't a repository
                 * with the name repo.getName() in the database */
            }
            
        }
        collection.put(repo.getName(), repo);
    }

    @Override
    public void updateRepo(Repository repo) throws RepositoryNotFoundException {
        if (!contains(repo.getName()))
            throw new RepositoryNotFoundException();
        collection.remove(repo.getName());
        collection.put(repo.getName(), repo);
    }
    
    @Override
    public boolean contains(String repoName){
        return collection.containsKey(repoName);
    }

    @Override
    public List<Repository> listRepos() {
        return new ArrayList<Repository>(collection.values());
    }

    @Override
    public Repository getRepo(String repoName) throws RepositoryNotFoundException {
        if (!contains(repoName))
            throw new RepositoryNotFoundException();
        return collection.get(repoName);
    }

    @Override
    public void removeAll() {
        collection.clear();
    }

    @Override
    public void removeRepo(String repoName) throws RepositoryNotFoundException {
        if (!contains(repoName))
            throw new RepositoryNotFoundException();
        collection.remove(repoName);
    }

}
