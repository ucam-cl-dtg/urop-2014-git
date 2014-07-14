/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.git.mongojack;

import java.io.IOException;
import java.io.Writer;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * @author ird28
 *
 */
public class GuiceExperiment {
    
    private final DB db;
    private final Writer writer;
    
    @Inject
    public GuiceExperiment(DB db, Writer writer) {
        this.db = db;
        this.writer = writer;
    }
    
    public void readThenProcessThenWrite() throws IOException {
        DBCollection collection = db.getCollection("guicetest");
        DBCursor results = collection.find();
        while (results.hasNext()) {
            DBObject result = results.next();
            System.out.println((String) result.get("name"));
            writer.write((String) result.get("name"));
        }
        writer.flush();
        writer.close();
        System.out.println("done");
    }
    
    public static void main(String[] args) throws IOException {
        Injector injector = Guice.createInjector(new GuiceModule());
        GuiceExperiment gex = injector.getInstance(GuiceExperiment.class);
        gex.readThenProcessThenWrite();
    }
    

}
