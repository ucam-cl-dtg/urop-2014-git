/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.UROP.twentyfourteen;

import uk.ac.cam.UROP.twentyfourteen.database.*;
import uk.ac.cam.UROP.twentyfourteen.public_interfaces.*;

import org.eclipse.jgit.treewalk.*;

import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.Map;
import java.io.File;
import java.io.IOException;

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
    private final String host;
    private final String crsid;

    String workingCommit;
    GitDb handle;

    private String getRepoPath()
    { return "ssh://" + crsid + "@" + host + "/" + repo + ".git"; }
    private String getRepoPathAsUser(String user)
    { return "ssh://" + user  + "@" + host + "/" + repo + ".git"; }

    public Repository(String name, String owner_crsid) throws IOException
    {
        parent = null;
        parent_hidden = null;
        repo = name;
        // TODO: 
        host = "127.0.0.1";
        crsid = owner_crsid;
    }

    public Repository(String name, String owner_crsid, String parent, String parent_hidden) throws IOException
    {
        this.parent = parent;
        this.parent_hidden = parent_hidden;
        repo = name;
        // TODO: 
        host = "127.0.0.1";
        crsid = owner_crsid;
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
    public void cloneTo(File directory) throws EmptyDirectoryExpectedException, IOException
    {
        if (directory.listFiles() == null || directory.listFiles().length != 0)
            throw new EmptyDirectoryExpectedException();

        // TODO: Proper gitolite username
        handle = new GitDb(
                 /* src            */ getRepoPathAsUser("gitolite")
                ,/* dest           */ directory 
                ,/* bare           */ false
                ,/* branch         */ "master"
                ,/* remote         */ "origin"
                ,/* privateKeyPath */ "~/.ssh/id_rsa" /* TODO: proper key path */);

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
    public Collection<String> getSources() throws IOException
    {
        List<String> rtn = new LinkedList<String>();

        if (handle == null || workingCommit == null)
            throw new NullPointerException("You did not clone git repository!");

        TreeWalk tw = handle.getTreeWalk(workingCommit);
        while (tw.next())
            rtn.add(tw.getNameString());
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
    public Collection<String> getSources(String filter) throws IOException
    {
        List<String> rtn = new LinkedList<String>();

        if (handle == null || workingCommit == null)
            throw new NullPointerException("You did not clone git repository!");

        TreeWalk tw = handle.getTreeWalk(workingCommit, filter);
        while (tw.next())
            rtn.add(tw.getPathString());
        return rtn;
    }

    /**
     * Returns a map of test files and a list of required files for
     * those tests to run
     *
     * @return Map of test files and a list of the test's dependencies
     */
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
    public String getCRSID() { return this.crsid; }

    /**
     * Gets the name of the repository
     *
     * @return Name of the repository
     */
    public String getName() { return this.crsid; }
}
