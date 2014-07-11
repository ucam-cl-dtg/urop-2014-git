/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git.configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import com.fasterxml.jackson.databind.*;

/**
 * @author Kovacsics Robert &lt;rmk35@cam.ac.uk&gt;
 *
 */
public class ConfigurationLoader
{
    static String fileName = "configuration.json";
    static ConfigurationFile loadedConfig = new ConfigurationFile();
    static String exception;

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
