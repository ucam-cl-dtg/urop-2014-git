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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

import uk.ac.cam.cl.git.api.DuplicateRepoNameException;
import uk.ac.cam.cl.git.api.RepositoryNotFoundException;
import uk.ac.cam.cl.git.configuration.ConfigurationLoader;

import com.jcraft.jsch.*;

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

    @Inject private RepositoryCollection reposCollection;
    
    private static final Injector injector = Guice.createInjector(new DatabaseModule());
    
    private static final ConfigDatabase instance = injector.getInstance(ConfigDatabase.class);
    
    public static ConfigDatabase instance() {
        return instance;
    }

    /**
     * For unit testing only, to allow a mock collection to be used.
     * Replaces the repository collection with the argument.
     * @param reposCollection The collection to be used.
     */
    //@Inject
    void setReposCollection(RepositoryCollection rCollection) {
        reposCollection = rCollection;
    }

    /**
     * Returns a list of all the repository objects in the collection
     *
     * @return List of repository objects in the collection
     */
    public List<Repository> getRepos()
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
    public Repository getRepoByName(String name) {
        return reposCollection.findByName(name);
    }

    /**
     * Removes the repository object with the given name from the
     * database if present in the database.
     * 
     * @param name The name of the repository to remove
     * @throws IOException
     * @throws RepositoryNotFoundException
     */
    public void delRepoByName(String repoName) throws IOException, RepositoryNotFoundException {
        log.info("Deleting repository \"" + repoName + "\"");
        if (!reposCollection.contains(repoName))
            throw new RepositoryNotFoundException();
        reposCollection.removeByName(repoName);
        generateConfigFile();
        log.info("Deleted repository \"" + repoName + "\"");
    }
    
    /**
     * Removes all repositories from the collection.
     * For unit testing only.
     */
    void deleteAll() throws IOException {
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
    public void generateConfigFile() throws IOException {
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
     * @throws DuplicateRepoNameException A repository with this name already
     * exists.
     */
    public void addRepo(Repository repo) throws DuplicateRepoNameException, IOException {
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
    public void addSSHKey(String key, String userName) throws IOException {
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
    public void updateRepo(Repository repo) throws IOException
    {
        reposCollection.updateRepo(repo);
        generateConfigFile();
    }

    /**
     * Runs the gitolite update programs.
     * <p>
     * This is because gitolite is a perl program and compiles the
     * configuration file into a perl module, which it uses.
     * This just forces recompilation.
     *
     * @param updates List of things to recompile/reconfigure.
     */
    void runGitoliteUpdate(String[] updates) throws IOException
    {
        log.info("Starting gitolite recompilation");
        for (String command : updates)
        {
            try
            {
                JSch ssh = new JSch();
                ssh.setKnownHosts("~/.ssh/known_hosts");
                ssh.addIdentity("~/.ssh/id_rsa");
                Session session = ssh.getSession(ConfigurationLoader
                                                  .getConfig().getRepoUser()
                                               , "localhost"
                                               , 22);
                session.connect();

                ChannelExec channel = (ChannelExec)session.openChannel("exec");
                channel.setCommand(command);

                channel.setInputStream(null);
                /* Gitolite does native logging */
                channel.setOutputStream(null);
                channel.setErrStream(null);

                channel.connect();

                while (channel.isClosed())
                {
                    try
                    {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException e)
                    {
                        /* If we woke up earlier from sleep than
                         * expected, continue to check for channel
                         * status.
                         */
                    }
                }
                channel.disconnect();
                session.disconnect();
            }
            catch (JSchException e)
            {
                throw new IOException(e);
            }
        }
        log.info("Finished gitolite recompilation");
    }
    
    /**
     * This rebuilds the MongoDB database using the gitolite
     * configuration file, in case the two become out of sync.
     */
    private void rebuildDatabaseFromGitolite() throws IOException, DuplicateRepoNameException {
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

}
