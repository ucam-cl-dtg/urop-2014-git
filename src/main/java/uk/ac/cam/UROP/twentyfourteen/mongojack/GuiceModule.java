/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.UROP.twentyfourteen.mongojack;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import uk.ac.cam.UROP.twentyfourteen.database.Mongo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.mongodb.DB;

/**
 * @author ird28
 *
 */
public class GuiceModule extends AbstractModule {
    
    
    @Override
    protected void configure() {
        //bind(DB.class).to(DB.class);
        bind(Writer.class).to(BufferedWriter.class);
    }
    
    
    @Provides
    DB provideDB() {
        return Mongo.getDB();
    }
    
    @Provides
    BufferedWriter provideWriter() throws IOException {
        File writeFile = new File(System.getProperty("user.home") + "/guice.txt");
        if (!writeFile.exists()) {
            writeFile.createNewFile();
        }
        FileWriter fw = new FileWriter(writeFile);
        return new BufferedWriter(fw);
    }

}
