/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.git;

import java.util.Iterator;

import org.mongojack.JacksonDBCollection;

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
    public void insertRepo(Repository repo) {
        collection.ensureIndex(new BasicDBObject("name", 1), null, true); // each repo name must be unique
        collection.insert(repo);
    }

    @Override
    public void updateRepo(Repository repo) {
        collection.updateById(repo.get_id(), repo);
    }

    @Override
    public Iterator<Repository> findAll() {
        return collection.find();
    }

    @Override
    public Repository findByName(String name) {
        return collection.findOne(new BasicDBObject("name", name));
    }

    @Override
    public void removeAll() {
        collection.remove(new BasicDBObject());
    }

    @Override
    public void removeByName(String name) {
        collection.remove(new BasicDBObject("name", name));
    }

}
