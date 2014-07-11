/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git;

import java.util.List;
import java.util.LinkedList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;

import com.mongodb.BasicDBObject;
import com.mongodb.DuplicateKeyException;
import com.mongodb.MongoException;

import uk.ac.cam.cl.git.database.Mongo;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 * @author Kovacsics Robert &lt;rmk35@cam.ac.uk&gt;
 * @version 0.1
 */
public class ConfigDatabase {
    /**
     * Returns a list of all the repository objects in the database
     *
     * @return List of repository objects in the database
     */
    public static List<Repository> getRepos()
    {   /* TODO: Test ordered-ness or repositories. */
        List<Repository> rtn = new LinkedList<Repository>();

        JacksonDBCollection<Repository, String> reposCollection =
            JacksonDBCollection.wrap
                ( Mongo.getDB().getCollection("repos")
                , Repository.class
                , String.class);
        DBCursor<Repository> allRepos = reposCollection.find();

        while (allRepos.hasNext())
            rtn.add(allRepos.next());

        allRepos.close();

        return rtn;
    }

    /**
     * Generates config file for gitolite and writes it to ~/test.conf.
     * <p>
     * Accesses mongoDB to find repositories and assumes the
     * Repository.toString() returns the appropriate representation. The
     * main conf file should have an include test.conf statement so that
     * when the hook is called, the updates are made. The hook is
     * called at the end of this method.
     *
     * @throws IOException Typically an unrecoverable problem.
     */
    public static void generateConfigFile() throws IOException {
        StringBuilder output = new StringBuilder();

        for (Repository r : getRepos())
            output.append(r.toString() + "\n");

        /* Write out file */
        try {
            String home = System.getProperty("user.home");
            File configFile = new File(home + "/UROP.conf");
            BufferedWriter buffWriter = new BufferedWriter(new FileWriter(configFile, false));
            buffWriter.write(output.toString());
            buffWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Adds a new repository to the mongo database for inclusion in the
     * conf file when generated.
     *
     * @param repo The repository to be added
     * @throws DuplicateKeyException A repository with this name already
     * exists.
     */
    public static void addRepo(Repository repo) throws DuplicateKeyException {
        JacksonDBCollection<Repository, String> repoCollection =
                JacksonDBCollection.wrap(Mongo.getDB().getCollection("repos"), Repository.class, String.class);
        BasicDBObject query = new BasicDBObject("name", 1);
        repoCollection.ensureIndex(query, null, true);
        repoCollection.insert(repo);
    }

    /**
     * Takes public key and username as strings, writes the key to
     * keydir/UROP/username.pub, and calls the hook.
     *
     * @param key The SSH key to be added
     * @param username The name of the user to be added
     */
    public static void addSSHKey(String key, String username) {
        try {
            String home = System.getProperty("user.home");
            /* TODO: Proper keydir */
            File keyFile = new File(home + "/.gitolite/keydir/UROP/" + username + ".pub");
            if (!keyFile.exists()) {
                keyFile.createNewFile();
            }
            BufferedWriter buffWriter = new BufferedWriter(new FileWriter(keyFile));
            buffWriter.write(key);
            buffWriter.close();
            runGitoliteUpdate();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    public static void updateRepo(Repository repo) throws MongoException
    {
        JacksonDBCollection<Repository, String> reposCollection =
            JacksonDBCollection.wrap
                ( Mongo.getDB().getCollection("repos")
                , Repository.class
                , String.class);
        reposCollection.updateById(repo.get_id(), repo);
    }

    private static void runGitoliteUpdate() throws IOException
    {
            String home = System.getProperty("user.home");
            Process p = Runtime.getRuntime().exec(home+"/.gitolite/hooks/gitolite-admin/post-update",
                    /* TODO: Setup specific */
                    new String[] {"HOME=" + home
                                 , "PATH=" + home + "/bin/:/bin:/usr/bin"
                                 , "GL_LIBDIR=" + home + "/git/gitolite/src/lib"});
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            BufferedReader outputReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = outputReader.readLine()) != null) {
                System.out.println(line);
            }
            while ((line = errorReader.readLine()) != null) {
                System.err.println(line);
            }
    }
}
