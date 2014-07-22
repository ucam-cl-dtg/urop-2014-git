/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.git;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.mongojack.JacksonDBCollection;

import uk.ac.cam.cl.git.api.DuplicateRepoNameException;
import uk.ac.cam.cl.git.api.RepositoryNotFoundException;

import com.google.inject.Inject;
import com.mongodb.BasicDBObject;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 *
 */
public class MongoRepositoryCollection implements RepositoryCollection {

    private static JacksonDBCollection<Repository, String> collection;
    
    @Inject
    public void setCollection(JacksonDBCollection<Repository, String> newCollection) {
        collection = newCollection;
    }

    @Override
    public void insertRepo(Repository repo) throws DuplicateRepoNameException {
        try {
            collection.ensureIndex(new BasicDBObject("name", 1), null, true); // each repo name must be unique
            collection.insert(repo);
        } catch(com.mongodb.MongoException dupKey) {
            try {
                /*
                 * If there is already a repo with repo.getName() as its name in the
                 * database, throw a DuplicateRepoNameException with that repo's URI
                 * as the message.
                 */
                
                throw new DuplicateRepoNameException(ConfigDatabase.instance()
                        .getRepoByName(repo.getName()).getRepoPath());
                
            } catch(RepositoryNotFoundException e) {
                throw new RuntimeException("This should never ever happen");
                /* This code only runs there both is and isn't a repository
                 * with the name repo.getName() in the database */
            }
        }
    }

    @Override
    public void updateRepo(Repository repo) throws RepositoryNotFoundException {
        if (!contains(repo.getName()))
            throw new RepositoryNotFoundException();
        collection.updateById(repo.get_id(), repo);
    }
    
    @Override
    public boolean contains(String name) {
        int matchingRepos = collection.find(new BasicDBObject("name", name)).count();
        assert (matchingRepos == 0 || matchingRepos == 1);
        return (matchingRepos == 1);
    }

    @Override
    public List<Repository> listRepos() {
        List<Repository> rtn = new LinkedList<Repository>();
        Iterator<Repository> allRepos = collection.find();

        while (allRepos.hasNext())
            rtn.add(allRepos.next());

        return rtn;
    }

    @Override
    public Repository getRepo(String repoName) throws RepositoryNotFoundException {
        if (!contains(repoName))
            throw new RepositoryNotFoundException();
        return collection.findOne(new BasicDBObject("name", repoName));
    }

    @Override
    public void removeAll() {
        collection.remove(new BasicDBObject());
    }

    @Override
    public void removeRepo(String repoName) throws RepositoryNotFoundException {
        if (!contains(repoName))
            throw new RepositoryNotFoundException();
        collection.remove(new BasicDBObject("name", repoName));
    }

}
