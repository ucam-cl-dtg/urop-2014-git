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
import com.mongodb.MongoException;

import uk.ac.cam.cl.git.configuration.ConfigurationLoader;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 * @author Kovacsics Robert &lt;rmk35@cam.ac.uk&gt;
 * @version 0.1
 */
public class ConfigDatabase {
    /* For logging */
    private static final Logger log = LoggerFactory.getLogger(ConfigDatabase.class);
    private static final String[] environmentVariables = new String[]
            {"HOME="  + ConfigurationLoader.getConfig().getGitoliteHome()
            , "PATH=" + ConfigurationLoader.getConfig().getGitolitePath()
            , "GL_LIBDIR=" + ConfigurationLoader.getConfig().getGitoliteLibdir()};

    private static RepositoryCollection reposCollection;
    private static Runtime runtime;

    static {
        Guice.createInjector(new DatabaseModule());
    }

    /**
     * For unit testing only, to allow a mock collection to be used.
     * Replaces the repository collection with the argument.
     * @param reposCollection The collection to be used.
     */
    @Inject
    static void setReposCollection(RepositoryCollection rCollection) {
        reposCollection = rCollection;
    }
    
    /**
     * For unit testing only, to allow a mock runtime to be used so that
     * the gitolite update calls are not actually executed.
     * Replaces the runtime field with the argument.
     * @param r The runtime to be used for the gitolite calls.
     */
    @Inject
    static void setRuntime(Runtime r) {
        runtime = r;
    }

    /**
     * Returns a list of all the repository objects in the collection
     *
     * @return List of repository objects in the collection
     */
    public static List<Repository> getRepos()
    {   /* TODO: Test ordered-ness or repositories. */
        return reposCollection.findAll();
        
    }
    
    
    /**
     * Returns the repository object with the given name in the
     * database.
     * 
     * @param name The name of the repository
     * @return The requested repository object
     */
    public static Repository getRepoByName(String name) {
        return reposCollection.findByName(name);
    }

    /**
     * Removes the repository object with the given name from the
     * database if present in the database.
     * 
     * @param name The name of the repository to remove
     * @return True if and only if a repository with name repoName existed in the database
     */
    public static boolean delRepoByName(String repoName) throws IOException {
        log.info("Deleting repository \"" + repoName + "\"");
        if (!reposCollection.contains(repoName))
            return false;
        reposCollection.removeByName(repoName);
        generateConfigFile();
        log.info("Deleted repository \"" + repoName + "\"");
        return true;
    }
    
    /**
     * Removes all repositories from the collection.
     * For unit testing only.
     */
    static void deleteAll() throws IOException {
        reposCollection.removeAll();
        generateConfigFile();
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
        runGitoliteUpdate(new String[] {"compile",
                                        "trigger POST_COMPILE"});
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
        reposCollection.insertRepo(repo);
        generateConfigFile();
    }

    /**
     * Takes public key and username as strings, writes the key to
     * getGitoliteSSHKeyLocation (see ConfigurationLoader), and calls the hook.
     *
     * @param key The SSH key to be added
     * @param username The name of the user to be added
     * @throws IOException 
     */
    public static void addSSHKey(String key, String userName) throws IOException {
        log.info("Adding key for \"" + userName + "\" to \""
                + ConfigurationLoader.getConfig()
                    .getGitoliteSSHKeyLocation() + "\"");
        File keyFile = new File(ConfigurationLoader.getConfig()
                .getGitoliteSSHKeyLocation() + "/" + userName + ".pub");
        if (!keyFile.exists()) {
            if (keyFile.getParentFile() != null)
                keyFile.getParentFile().mkdirs(); /* Make parent directories if necessary */
            keyFile.createNewFile();
        }
        BufferedWriter buffWriter = new BufferedWriter(new FileWriter(keyFile));
        buffWriter.write(key);
        buffWriter.close();
        runGitoliteUpdate(new String[] {"trigger SSH_AUTHKEYS"});
        log.info("Finished adding key for \"" + userName + "\"");
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
        reposCollection.updateRepo(repo);
        generateConfigFile();
    }

    /**
     * Runs the specified gitolite updates in a new process.
     * @param updates The gitolite commands to be run
     * @throws IOException
     */
    private static void runGitoliteUpdate(String[] updates) throws IOException
    {
        log.info("Starting gitolite recompilation");
        for (String command : updates)
        {
            Process p = runtime.exec("env gitolite " + command, environmentVariables);
            String line;
            if (p.getErrorStream() != null) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                while ((line = errorReader.readLine()) != null) {
                    log.warn(line);
                }
            }
            if (p.getInputStream() != null) {
                BufferedReader outputReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                while ((line = outputReader.readLine()) != null) {
                    log.info(line);
                }
            }
        }
        log.info("Finished gitolite recompilation");
    }
    
    private static void rebuildDatabaseFromGitolite() throws MongoException, IOException, DuplicateKeyException {
        reposCollection.removeAll(); // Empty database collection
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
            reposCollection.insertRepo(toInsert);
            reader.readLine(); // extra line between repos
        }
        reader.close();
    }
    
    static Process testRuntime() throws IOException {
        return runtime.exec("bananas in pyjamas", environmentVariables);
    }
    
    static String[] getEnvVar() { return environmentVariables; }

}
