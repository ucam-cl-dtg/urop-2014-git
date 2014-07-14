/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git.configuration;

import java.io.IOException;
import java.io.File;

import com.fasterxml.jackson.core.JsonParser;
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
    static public final String fileName = "configuration.json";
    static private File file = new File(fileName);
    static private ObjectMapper mapper = new ObjectMapper();
    static long mTime;
    static ConfigurationFile loadedConfig = new ConfigurationFile();

    static
    {
        /* ObjectMapper (JSON syntax) configuration */
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

        try
        {
            loadedConfig =
                mapper.readValue(file, ConfigurationFile.class);
            mTime = file.lastModified();
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
        if (file.lastModified() > mTime)
        {
            try
            {
                loadedConfig = mapper.readValue(file, ConfigurationFile.class);
                mTime = file.lastModified();
            }
            catch (IOException e)
            {
                System.err.println("Unable to load new configuration file!\n"
                        + e.getMessage());
            }
        }
        return loadedConfig;
    }
}
