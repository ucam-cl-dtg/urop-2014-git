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
 * @author ird28
 *
 */
public class DatabaseModule extends AbstractModule {

    @Override
    protected void configure() {
        requestStaticInjection(ConfigDatabase.class);
        bind(RepositoryCollection.class).to(LocalRepositoryCollection.class);
    }
    
    @Provides
    public JacksonDBCollection<Repository, String> provideMongoColection() {
        return JacksonDBCollection.wrap
                ( Mongo.getDB().getCollection("repos")
                        , Repository.class
                        , String.class);
    }

}
