/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git;

import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.mongodb.BasicDBObject;
import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;

import uk.ac.cam.cl.git.configuration.ConfigurationLoader;
import uk.ac.cam.cl.git.database.Mongo;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 * @author Kovacsics Robert &lt;rmk35@cam.ac.uk&gt;
 * @version 0.1
 */
public class ConfigDatabase {
    /* For logging */
    private static final Logger log = LoggerFactory.getLogger(ConfigDatabase.class);

    /* 
     * For Guice to inject dependencies, the following line must be run:
     * Guice.createInjector(new DatabaseModule());
     */
    private static JacksonDBCollection<Repository, String>
        reposCollection =
                JacksonDBCollection.wrap
                ( Mongo.getDB().getCollection("repos")
                , Repository.class
                , String.class);
    
    /**
     * For unit testing only, to allow a mock collection to be used.
     * Replaces the mongo collection with the argument.
     * @param reposCollection The collection to be used.
     */
    @Inject
    static void setReposCollection(JacksonDBCollection<Repository, String> rCollection) {
        reposCollection = rCollection;
    }

    /**
     * Returns a list of all the repository objects in the database
     *
     * @return List of repository objects in the database
     */
    public static List<Repository> getRepos()
    {   /* TODO: Test ordered-ness or repositories. */
        List<Repository> rtn = new LinkedList<Repository>();
        DBCursor<Repository> allRepos = reposCollection.find();

        while (allRepos.hasNext())
            rtn.add(allRepos.next());

        allRepos.close();

        return rtn;
    }
    
    
    /**
     * Returns the repository object with the given name in the
     * database.
     * 
     * @param name The name of the repository
     * @return The requested repository object
     */
    public static Repository getRepoByName(String name) {
        return reposCollection.findOne(new BasicDBObject("name", name));
    }

    /**
     * Removes the repository object with the given name from the
     * database.
     * 
     * @param name The name of the repository to remove
     */
    public static void delRepoByName(String name) throws IOException {
        reposCollection.remove(new BasicDBObject("name", name));
        generateConfigFile();
        runGitoliteUpdate();
    }

    /**
     * Generates config file for gitolite and writes it to gitoliteGeneratedConfigFile (see ConfigurationLoader).
     * <p>
     * Accesses the database to find repositories and assumes the
     * Repository.toString() method returns the appropriate representation. The
     * main conf file should have an include statement so that
     * when the hook is called, the updates are made. The hook is
     * called at the end of this method.
     *
     * @throws IOException Typically an unrecoverable problem.
     */
    public static void generateConfigFile() throws IOException {
        log.info("Generating config file \"" +
                ConfigurationLoader.getConfig()
                    .getGitoliteGeneratedConfigFile()
                + "\"");
        StringBuilder output = new StringBuilder();

        for (Repository r : getRepos())
            output.append(r.toString() + "\n");

        /* Write out file */
        File configFile = new File(ConfigurationLoader.getConfig()
                .getGitoliteGeneratedConfigFile());
        BufferedWriter buffWriter = new BufferedWriter(new FileWriter(configFile, false));
        buffWriter.write(output.toString());
        buffWriter.close();
        runGitoliteUpdate();
        log.info("Generated config file \"" +
                ConfigurationLoader.getConfig()
                    .getGitoliteGeneratedConfigFile()
                + "\"");
    }

    /**
     * Adds a new repository to the mongo database for inclusion in the
     * conf file when generated.
     *
     * @param repo The repository to be added
     * @throws DuplicateKeyException A repository with this name already
     * exists.
     */
    public static void addRepo(Repository repo) throws DuplicateKeyException, IOException {
        reposCollection.ensureIndex(new BasicDBObject("name", 1), null, true); // each repo name must be unique
        reposCollection.insert(repo);
        generateConfigFile();
        runGitoliteUpdate();
    }

    /**
     * Takes public key and username as strings, writes the key to
     * getGitoliteSSHKeyLocation (see ConfigurationLoader), and calls the hook.
     *
     * @param key The SSH key to be added
     * @param username The name of the user to be added
     * @throws IOException 
     */
    public static void addSSHKey(String key, String username) throws IOException {
        File keyFile = new File(ConfigurationLoader.getConfig()
                .getGitoliteSSHKeyLocation() + username + ".pub");
        if (!keyFile.exists()) {
            keyFile.createNewFile();
        }
        BufferedWriter buffWriter = new BufferedWriter(new FileWriter(keyFile));
        buffWriter.write(key);
        buffWriter.close();
        runGitoliteUpdate();
    }

    /**
     * Updates the given repository.
     *
     * This selects the repository uniquely using the ID (not
     * technically the name of the repository, but is equivalent).
     *
     * @param repo The updated repository (there must also be a
     * repository by this name).
     * @throws MongoException If the update operation fails (for some
     * unknown reason).
     */
    public static void updateRepo(Repository repo) throws MongoException, IOException
    {
        reposCollection.updateById(repo.get_id(), repo);
        generateConfigFile();
        runGitoliteUpdate();
    }

    private static void runGitoliteUpdate() throws IOException
    {
        log.info("Starting gitolite recompilation");
        Process p = Runtime.getRuntime().exec(
          /*
          ConfigurationLoader.getConfig().getGitoliteHome()
            + "/.gitolite/hooks/gitolite-admin/post-update"
            */
          "gitolite compile"
          , new String[]
            {"HOME="  + ConfigurationLoader.getConfig().getGitoliteHome()
            , "PATH=" + ConfigurationLoader.getConfig().getGitolitePath()
            , "GL_LIBDIR=" + ConfigurationLoader.getConfig().getGitoliteLibdir()});
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
        BufferedReader outputReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
        String line;
        while ((line = outputReader.readLine()) != null) {
            System.out.println(line);
        }
        while ((line = errorReader.readLine()) != null) {
            System.err.println(line);
        }
        log.info("Finished gitolite recompilation");
    }
    
    private static void rebuildDatabaseFromGitolite() throws MongoException, IOException {
        reposCollection.remove(new BasicDBObject()); // Empty database collection
        BufferedReader reader = new BufferedReader(new FileReader(new File(
                ConfigurationLoader.getConfig().getGitoliteGeneratedConfigFile())));
        String firstLine;
        while ((firstLine = reader.readLine()) != null) { // While not end of file
            String repoName = firstLine.split("\\s\\+")[1]; // Repo name is second word of first line
            
            String[] readWriteLine = reader.readLine().split("=")[1].trim().split("\\s\\+");
            // We want the words to the right of the "RW ="
            
            String nextLine = reader.readLine();
            String[] readOnlyLine;
            String[] auxiliaryLine;
            if (nextLine.startsWith("#")) { // No users with read only access
                readOnlyLine = new String[0];
                auxiliaryLine = nextLine.split(" ");
            }
            else { // At least one user with read only access
                readOnlyLine = nextLine.split("=")[1].trim().split("\\s\\+");
                auxiliaryLine = reader.readLine().split("\\s\\+");
            }
            
            String owner = readWriteLine[0]; // Owner is always first RW entry - see Repository.toString()
            List<String> readWrites = new LinkedList<String>(Arrays.asList(readWriteLine));
            readWrites.remove(0); // remove owner from RW list as owner is automatically added
            List<String> readOnlys = Arrays.asList(readOnlyLine);
            String parent = auxiliaryLine[1]; // see Repository.toString()
            String parent_hidden = auxiliaryLine[2];
            Repository toInsert = new Repository(repoName, owner, readWrites,
                            readOnlys, parent, parent_hidden, null);
            reposCollection.insert(toInsert);
            reader.readLine(); // extra line between repos
        }
        reader.close();
    }

}
