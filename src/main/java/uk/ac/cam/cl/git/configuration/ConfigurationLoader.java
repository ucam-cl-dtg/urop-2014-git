/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git.configuration;

import java.io.IOException;
import java.io.File;
import com.fasterxml.jackson.databind.*;

/**
 *  A configuration file loader class, set at compile time, loaded at
 *  initialisation.
 *  
 * This just a simple class to load the file {@value fileName} in the
 * current directory and convert it into a
 * {@link uk.ac.cam.cl.git.configuration.ConfigurationFile}
 * class.
 *
 * @author Kovacsics Robert &lt;rmk35@cam.ac.uk&gt;
 */
public class ConfigurationLoader
{
    public static final String fileName = "configuration.json";
    static ConfigurationFile loadedConfig = new ConfigurationFile();

    static
    {
        try
        {
            File f = new File(fileName);
            ObjectMapper mapper = new ObjectMapper();
            loadedConfig = mapper.readValue
                ( f
                , ConfigurationFile.class);
        }
        catch (IOException e)
        {
            /* Exception thrown in getter */
            /* TODO: log */
            e.printStackTrace();
            System.err.println("Error in loading configuration file, using defaults.\n"
                    + e.getMessage());
        }
    }

    public static ConfigurationFile getConfig()
    {
        return loadedConfig;
    }
}
