/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.git;

import org.mongojack.JacksonDBCollection;

import uk.ac.cam.cl.git.database.Mongo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 *
 */
public class DatabaseModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(RepositoryCollection.class).to(MongoRepositoryCollection.class);
    }
    
    @Provides
    public JacksonDBCollection<Repository, String> provideMongoCollection() {
        return JacksonDBCollection.wrap
                ( Mongo.getDB().getCollection("repos")
                        , Repository.class
                        , String.class);
    }
    
}
