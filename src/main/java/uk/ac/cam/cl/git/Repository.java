/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git;

import uk.ac.cam.cl.git.configuration.ConfigurationLoader;
import uk.ac.cam.cl.git.database.*;
import uk.ac.cam.cl.git.public_interfaces.*;

import org.eclipse.jgit.treewalk.*;

import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.io.File;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

import com.fasterxml.jackson.annotation.*;

import org.mongojack.Id;
import org.mongojack.ObjectId;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 * @author Kovacsics Robert &lt;rmk35@cam.ac.uk&gt;
 * @version 0.1
 */
public class Repository implements TesterInterface, FrontendRepositoryInterface
{ 
    private final String parent;
    private final String parent_hidden;
    private final String repo;
    private final String host =
        ConfigurationLoader.getConfig().getRepoHost();
    private final String user =
        ConfigurationLoader.getConfig().getRepoUser();
    private final String owner;
    private final List<String> read_write;
    private final List<String> read_only;
    private String _id;

    String workingCommit;
    GitDb handle;

    /**
     * Creates a repository object, to be added to the repository
     * database with ConfigDatabase.addRepo(Repository r).
     *
     * @param name The name of the repository. This must identify it
     * uniquely.
     * @param crsid The CRSID of the repository owner. The owner
     * automatically has read/write (but not force push) permissions, in
     * fact we do not allow force push permissions at all.
     * @param read_write A list of people or groups who can read and write to
     * the repository. This does not need to include the owner. TODO:
     * testing and frontend team servers
     * @param read_only Like read_write without the write.
     * @param parent The parent repository which at the moment does
     * nothing. TODO
     * @param parent_hidden The hidden parent repository which at the
     * moment does nothing. TODO
     */
    @JsonCreator
    public Repository
        ( @JsonProperty("name")          String name
        , @JsonProperty("owner")         String crsid
        , @JsonProperty("rw")            List<String> read_write
        , @JsonProperty("r")             List<String> read_only
        , @JsonProperty("parent")        String parent
        , @JsonProperty("parent_hidden") String parent_hidden
        )
    {
        this.parent = parent;
        this.parent_hidden = parent_hidden;
        this.repo = name;
        this.read_write = read_write;
        this.read_only = read_only;
        owner = crsid;
    
    }

    /**
     * Clones repository to specified directory, if it can get
     * repository access.
     * <p>
     * It tries to access the repository with the id_rsa key.
     *
     * @param directory The empty directory to which you want to clone
     * into.
     *
     * @throws EmptyDirectoryExpectedException The File given is either
     * not a directory or not empty.
     * @throws IOException Something went wrong (typically not
     * recoverable).
     */
    @JsonIgnore
    public void cloneTo(File directory) throws EmptyDirectoryExpectedException, IOException
    {
        if (directory.listFiles() == null || directory.listFiles().length != 0)
            throw new EmptyDirectoryExpectedException();

        handle = new GitDb(
                 /* src            */ getRepoPath()
                ,/* dest           */ directory 
                ,/* bare           */ false
                ,/* branch         */ "master"
                ,/* remote         */ "origin"
                ,/* privateKeyPath */ ConfigurationLoader.getConfig()
                                            .getSshPrivateKeyFile());

        if (workingCommit == null)
            workingCommit = handle.getHeadSha();
    }

    /**
     * Opens a local repository
     * 
     * @param repoName the name of the repository to open.
     * @throws IOException Something went wrong (typically not
     * recoverable).
     */
    public void openLocal(String repoName) throws IOException
    {
        System.out.println("Opening : " + ConfigurationLoader.getConfig()
                .getGitoliteHome() + "/repositories/" + repoName + ".git");
        handle = new GitDb(ConfigurationLoader.getConfig()
                .getGitoliteHome() + "/repositories/" + repoName + ".git");

        if (workingCommit == null)
            workingCommit = handle.getHeadSha();
    }
    
    /**
     * Pulls in student repo and tick repo and hands over relevant files to be tested.
     *
     */
    public void submit() {
        /* TODO: implement
         * 
         * 1) Pull in student's repo (the one this class is encapsulating)
         * 2) Pull in ticker's repo (the one the origin of this class)
         * 3) Package up in some format for the testers' API to call
         * 4) Call the testers' test function
         * 4) i) Perhaps we could call it with an argument of this
         *       class, then they could use the visitor pattern on to
         *       get at the files and then use the saveTestResults
         *       method to give us the test results.
         */
    }
    
    /**
     * Saves test results into mongo database.
     * <p>
     * Needs to store commit number or similar, so that the test results cannot be changed.
     * 
     * @param results The results to be saved
     */
    @JsonIgnore
    public void saveTestResults(Object results) { // won't be an Object eventually
        /* TODO: implement
         *
         * 1) Append the commit hash to the test result
         * 2) Store the test result in a database
         */
    }
    
    /**
     * Returns requested test results.
     * 
     * @param request Specifies which results are wanted
     * 
     * @return The requested test results
     */
    @JsonIgnore
    public Object getTestResults(String request) { // these types will change
        /* TODO: implement
         *
         * 1) Get the test result corresponding to the request
         * 2) Get the commit the test result refers to
         * 3) Return files and comments
         */
        return new Object();
    }

    /**
     * Returns a list of the source files in the repository.
     * <p>
     * Repository must first be cloned using cloneTo!
     *
     * @return The list of source files, (TODO: as specified by the tick setter?)
     */
    @JsonIgnore
    public Collection<String> getSources() throws IOException
    {
        List<String> rtn = new LinkedList<String>();

        if (handle == null)
            throw new NullPointerException("Repository unset. Did you clone it?");
        if (workingCommit == null)
            /* Only way above is true if we have an empty repository, as
             * everything that sets handle also sets workingCommit (to
             * null, if we have an empty repository).
             */
            return null;

        TreeWalk tw = handle.getTreeWalk(workingCommit);
        while (tw.next())
            rtn.add(tw.getPathString());
        return rtn;
    }

    /**
     * Returns a list of the source files in the repository, filtered
     * according to filter.
     * <p>
     * Repository must first be cloned using cloneTo!
     *
     * @param filter Filter files according to this
     * @return The list of source files, (TODO: as specified by the tick setter?)
     *
     * @throws IOException Something went wrong (typically not
     * recoverable).
     */
    @JsonIgnore
    public Collection<String> getSources(String filter) throws IOException
    {
        List<String> rtn = new LinkedList<String>();

        if (handle == null)
            throw new NullPointerException("Repository unset. Did you clone it?");
        if (workingCommit == null)
            /* Only way above is true if we have an empty repository, as
             * everything that sets handle also sets workingCommit (to
             * null, if we have an empty repository).
             */
            return null;

        TreeWalk tw = handle.getTreeWalk(workingCommit, filter);
        while (tw.next())
            rtn.add(tw.getPathString());
        return rtn;
    }

    /**
     * Outputs the content of the file.
     *
     * @param filePath Full path of the file
     * @return Contents of the file asked for or null if file is not
     * found.
     */
    @JsonIgnore
    public String getFile(String filePath) throws IOException
    {
        if (handle == null)
            throw new NullPointerException("Repository unset. Did you clone it?");

        if (workingCommit == null)
            /* Only way above is true if we have an empty repository, as
             * everything that sets handle also sets workingCommit (to
             * null, if we have an empty repository).
             */
            return null;

        ByteArrayOutputStream rtn = handle.getFileByCommitSHA(workingCommit, filePath);

        if (rtn == null)
            return null;

        return rtn.toString();
    }

    /**
     * Returns a map of test files and a list of required files for
     * those tests to run
     *
     * @return Map of test files and a list of the test's dependencies
     */
    @JsonIgnore
    public Map<String, Collection<String>> getTests()
    {
        /* TODO: implement
         */
        return null;
    }

    /**
     * Gets the CRSID of the repository owner
     *
     * @return CRSID of the repository owner
     */
    @JsonProperty("owner")
    public String getCRSID() { return owner; }

    /**
     * Gets the name of the repository
     *
     * @return Name of the repository
     */
    @JsonProperty("name")
    public String getName() { return this.repo; }

    /**
     * Gets the read &amp; write capable users or groups, for
     * serialization.
     *
     * @return Read &amp; write capable users or groups.
     */
    @JsonProperty("rw")
    public List<String> getReadWrite() { return this.read_write; }

    /**
     * Gets the read only capable users or groups, for serialization.
     *
     * @return Read only capable users or groups.
     */
    @JsonProperty("r")
    public List<String> getReadOnly() { return this.read_only; }

    /**
     * Gets the parent of this repository, or null if this repository
     * has no parent.
     *
     * @return Parent or null
     */
    @JsonProperty("parent")
    public String parent() { return this.parent; }
    
    /**
     * For storing this in MongoDB
     *
     * @return ID of this object in MongoDB
     */
    @Id @ObjectId
    String get_id() { return this._id; }
    
    /**
     * For storing this in MongoDB
     *
     * @param id Object ID to set
     */
    @Id @ObjectId
    void set_id(String id) { _id = id; }

    /**
     * Gets the hidden parent of this repository, or null if this
     * repository has no hidden parent.
     * 
     * @return Hidden parent or null
     */
    @JsonProperty("parent_hidden")
    public String parent_hidden() { return this.parent_hidden; }

    /**
     * Gives the string representation of the repository, to be used in
     * conjuction with Gitolite.
     *
     * @return Gitolite config compatible string representation of the
     * repository
     */
    @Override
    public String toString()
    {
        StringBuilder strb = new StringBuilder("repo ");
        strb.append(repo);
        strb.append("\n");

        if (read_write.size() > 0)
        {
            strb.append("     RW =");
            strb.append(" " + owner);
            /* Usernames or groups */
            for ( String name : read_write)
                strb.append(" " + name);
            strb.append("\n");
        }

        if (read_only.size() > 0)
        {
            strb.append("     R  =");
            /* Usernames or groups */
            for ( String name : read_only)
                strb.append(" " + name);
            strb.append("\n");
        }

        return strb.toString();
    }

    /**
     * Gets the repository path as an SSH URI.
     *
     * @return Repository path as an SSH URI.
     */
    @JsonIgnore
    public String getRepoPath()
    {
        return "ssh://" + user  + "@" + host + "/" + repo + ".git";
    }
}
