/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git;

import uk.ac.cam.cl.git.api.Commit;
import uk.ac.cam.cl.git.api.EmptyDirectoryExpectedException;
import uk.ac.cam.cl.git.api.IllegalCharacterException;
import uk.ac.cam.cl.git.configuration.ConfigurationLoader;
import uk.ac.cam.cl.git.interfaces.*;

import org.eclipse.jgit.treewalk.*;
import org.eclipse.jgit.revwalk.*;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.File;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

import com.fasterxml.jackson.annotation.*;

import org.mongojack.Id;
import org.mongojack.ObjectId;

import uk.ac.cam.cl.dtg.segue.git.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 * @author Kovacsics Robert &lt;rmk35@cam.ac.uk&gt;
 * @version 0.1
 */
public class Repository implements TesterInterface
{
    /* For logging */
    private static final Logger log = LoggerFactory.getLogger(ConfigDatabase.class);

    private final String parent;
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
     * <p>
     * If parent is not null, it tries to clone it.
     *
     * @param name The name of the repository. This must identify it
     * uniquely.
     * @param crsid The CRSID of the repository owner. The owner
     * automatically has read/write (but not force push) permissions, in
     * fact we do not allow force push permissions at all.
     * @param read_write A list of people or groups who can read and write to
     * the repository. This does not need to include the owner (toString method
     * automatically includes owner as RW).
     * @param read_only Like read_write without the write.
     */
    @JsonIgnore
    public Repository
        ( String name
        , String crsid
        , List<String> read_write
        , List<String> read_only
        ) throws IllegalCharacterException
    {
        Matcher illegalChar =
            Pattern.compile("[^0-9a-zA-Z_-]").matcher(name);
        if (name.charAt(0) == '_')
        {
            throw new IllegalCharacterException("You can not start" +
                    "with underscores");
        }
        else if (illegalChar.find())
        {
            boolean singular = true;
            StringBuilder match = new StringBuilder(illegalChar
                    .group());

            while (illegalChar.find())
            {
                singular = false;
                match.append(", " + illegalChar.group());
            }

            throw new IllegalCharacterException(
                    "Illegal character" +
                    (singular?"":"s") + ": " +
                    match.toString());
        }

        this.parent = null;
        this.repo = name;
        this.read_write = read_write;
        this.read_only = read_only;
        owner = crsid;
    }

    /**
     * Creates a repository object, to be added to the repository
     * database with ConfigDatabase.addRepo(Repository r).
     * <p>
     * If parent is not null, it tries to clone it.
     *
     * @param name The name of the repository. This must identify it
     * uniquely.
     * @param crsid The CRSID of the repository owner. The owner
     * automatically has read/write (but not force push) permissions, in
     * fact we do not allow force push permissions at all.
     * @param read_write A list of people or groups who can read and write to
     * the repository. This does not need to include the owner (toString method
     * automatically includes owner as RW).
     * @param read_only Like read_write without the write.
     * @param parent The parent repository, which is the one this was
     * forked off
     *
     * @throws IOException Unrecoverable error in cloning the parent
     * repository.
     */
    @JsonIgnore
    public Repository
        ( String name
        , String crsid
        , List<String> read_write
        , List<String> read_only
        , String parent
        ) throws IllegalCharacterException
    {
        Pattern illegalChars = Pattern.compile("[^0-9a-zA-Z_-]");
        Matcher nameIllegalChar =
            illegalChars.matcher(name);
        Matcher parentIllegalChar =
            illegalChars.matcher(name);
        if (name.charAt(0) == '_')
        {
            throw new IllegalCharacterException("You can not start " +
                    "with underscores");
        }
        else if (parent.charAt(0) == '_')
        {
            throw new IllegalCharacterException("Parent repository " +
                    " begins with an underscore.");
        }
        else if (nameIllegalChar.find())
        {
            boolean singular = true;
            StringBuilder match = new StringBuilder(nameIllegalChar
                    .group());

            while (nameIllegalChar.find())
            {
                singular = false;
                match.append(", " + nameIllegalChar.group());
            }

            throw new IllegalCharacterException(
                    "Illegal character" +
                    (singular?"":"s") + ": " +
                    match.toString());
        }
        else if (parentIllegalChar.find())
        {
            boolean singular = true;
            StringBuilder match = new StringBuilder(parentIllegalChar
                    .group());

            while (parentIllegalChar.find())
            {
                singular = false;
                match.append(", " + parentIllegalChar.group());
            }

            throw new IllegalCharacterException(
                    "Illegal character" +
                    (singular?"":"s") + ": " +
                    match.toString());
        }

        this.parent = parent;
        this.repo = name;
        this.read_write = read_write;
        this.read_only = read_only;
        owner = crsid;
    }

    /**
     * Creates a Repository object, for use with MongoJack.
     *
     * @param name The name of the repository. This must identify it
     * uniquely.
     * @param crsid The CRSID of the repository owner. The owner
     * automatically has read/write (but not force push) permissions, in
     * fact we do not allow force push permissions at all.
     * @param read_write A list of people or groups who can read and write to
     * the repository. This does not need to include the owner (toString method
     * automatically includes owner as RW).
     * @param read_only Like read_write without the write.
     * @param parent The parent repository, which is the one this was
     * forked off
     */
    @JsonCreator
    Repository
        ( @JsonProperty("name")          String name
        , @JsonProperty("owner")         String crsid
        , @JsonProperty("rw")            List<String> read_write
        , @JsonProperty("r")             List<String> read_only
        , @JsonProperty("parent")        String parent
        , @JsonProperty("_id")           String id
        )
    {
        this.parent = parent;
        this.repo = name;
        this.read_write = read_write;
        this.read_only = read_only;
        owner = crsid;
    }

    /**
     * Adds the given user to the list of users with read-only permissions
     * for this repository. Note that it does not then update gitolite to
     * reflect the change.
     */
    public void addReadOnlyUser(String user) {
        read_only.add(user);
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
     * Clones parent repository's contents. Use only once, on the
     * initialisation of the repository.
     *
     * @throws IOException Something went wrong during cloning, perhaps
     * the directory was not empty?
     */
    public void cloneParent() throws IOException
    {
        GitDb tmp = new GitDb(ConfigurationLoader.getConfig()
                .getGitoliteHome() + "/repositories/" + parent + ".git");

        /* Now parent is cloned at tmpDir, push back to child */
        if (tmp.listCommits() != null)
        {
            try
            {
                /* tmp.pushTo(ConfigurationLoader.getConfig()
                    .getGitoliteHome() + "/repositories/" + repo + ".git"); */
                log.debug("Cloning parent; will push to "
                        + getRepoPath());
                tmp.pushTo(getRepoPath());
            }
            catch (PushFailedException e)
            {
                log.error("Failed to push parent repo onto child.", e);
                throw new IOException(
                        "Failed to push parent repo onto child. "
                        + "You will get an empty repository.\n", e);
            }
        }
    }

    /**
     * Opens a local repository.
     *
     * @param repoName The name of the repository to open.
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
     * Opens a local repository with the given commit.
     *
     * @param repoName The name of the repository to open.
     * @param commitID Identification for a commit.
     * @throws IOException Something went wrong (typically not
     * recoverable).
     */
    public void openLocal(String repoName, String commitID)
        throws IOException
    {
        System.out.println("Opening : " + ConfigurationLoader.getConfig()
                .getGitoliteHome() + "/repositories/" + repoName + ".git");
        handle = new GitDb(ConfigurationLoader.getConfig()
                .getGitoliteHome() + "/repositories/" + repoName + ".git");

        workingCommit = commitID;
    }

    /**
     * List the commits in the repository.
     */
    public List<Commit> listCommits()
    {
        List<Commit> rtn = new LinkedList<Commit>();

        for (RevCommit commit : handle.listCommits())
        {
            rtn.add(new Commit
                        (commit.getName()
                       , commit.getAuthorIdent().getName()
                       , commit.getFullMessage()
                       , new Date(commit.getCommitTime())
                   ));
        }

        return rtn;
    }

    /**
     * Resolves a commit reference such as HEAD or a branch name such as
     * master to a SHA.
     *
     * @param name The name to resolve.
     * @return The SHA of the latest matching commit.
     */
    public String resolveCommit(String name)
    {
        return handle.getSha(name);
    }

    /* Test team stores test results now. This is a placeholder to say
     * why code was removed.
     */

    /**
     * Returns a list of the source files in the repository.
     * <p>
     * Repository must first be cloned using cloneTo!
     *
     * @return The list of source files
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
     * @return The list of source files
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
    protected String get_id() { return this._id; }

    /**
     * For storing this in MongoDB
     *
     * @param id Object ID to set
     */
    @Id @ObjectId
    protected void set_id(String id) { _id = id; }

    /**
     * Gives the string representation of the repository, to be used in
     * conjuction with Gitolite.
     *
     * Please do not change this method without appropriately updating
     * rebuildDatabaseFromGitolite in ConfigDatabase!
     *
     * @return Gitolite config compatible string representation of the
     * repository
     */
    @Override
    @JsonIgnore
    public String toString()
    {
        StringBuilder strb = new StringBuilder("repo ");
        strb.append(repo);
        strb.append("\n");

        strb.append("     RW =");
        strb.append(" " + owner);
        strb.append(" tomcat7");
        /* Usernames or groups */
        if (read_write != null)
            for ( String name : read_write)
                strb.append(" " + name);
        strb.append("\n");

        strb.append("     R  =");
        strb.append(" unitTest");
        if (read_only != null)
            /* Usernames or groups */
            for ( String name : read_only)
                strb.append(" " + name);
        strb.append("\n");

        strb.append("# "); // To allow the rebuilding of the database
        strb.append(parent + "\n"); // from the gitolite config file

        return strb.toString();
    }

    /**
     * Gets the parent repository path as an SSH URI.
     *
     * @return Parent repository path as an SSH URI.
     */
    @JsonIgnore
    public String getParentRepoPath()
    {
        return "ssh://" + user  + "@" + host + "/" + parent + ".git";
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

    /**
     * Checks if this repository exists on disk.
     *
     * @return True if repository exists.
     */
    @JsonIgnore
    public boolean repoExists()
    {
        return new File(ConfigurationLoader.getConfig()
                        .getGitoliteHome()
                        + "/repositories/" + repo + ".git").exists();
    }
}
