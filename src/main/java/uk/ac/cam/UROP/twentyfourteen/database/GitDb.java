/* vim: set et ts=4 sts=4 sw=4 tw=72 */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.UROP.twentyfourteen.database;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.transport.FetchResult;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.OpenSshConfig.Host;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.SshSessionFactory;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;

/**
 * This class is a representation of the Git Database and provides some helper
 * methods to allow file access.
 *
 * It is responsible for providing basic functionality to search a specified Git
 * Repository and find files based on a given SHA.
 *
 */
public class GitDb
{
    private static final Logger log = LoggerFactory.getLogger(GitDb.class);

    private final String privateKey;
    private final String sshFetchUrl;

    private Git gitHandle;

    /**
     * Create a new instance of a GitDb object
     *
     * This will immediately try and connect to the Git folder specified to
     * check its validity.
     *
     * @param repoLocation
     *            - location of the local git repository
     * @throws IOException
     */
    public GitDb(String repoLocation) throws IOException {
        Validate.notBlank(repoLocation);

        // unused for this constructor
        this.privateKey = null;
        this.sshFetchUrl = null;

        gitHandle = Git.open(new File(repoLocation));
    }

    /**
     * Create a new instance of a GitDb object
     *
     * This will immediately try and connect to the Git folder specified to
     * check its validity.
     *
     * This constructor is only necessary if we want to access a private
     * repository.
     *
     * @param repoLocation
     *            - location of the local git repository
     * @param sshFetchUrl
     *            - location of the remote git repository (ssh url used for
     *            fetching only)
     * @param privateKeyFileLocation
     *            - location of the local private key file used to access the
     *            ssh FetchUrl
     * @throws IOException
     */
    @Inject
    public GitDb(String repoLocation, String sshFetchUrl,
            String privateKeyFileLocation) throws IOException {
        Validate.notBlank(repoLocation);

        this.sshFetchUrl = sshFetchUrl;
        this.privateKey = privateKeyFileLocation;

        gitHandle = Git.open(new File(repoLocation));
    }

    /**
     * Clone a repository and create a GitDb object.
     * <p>
     * Note, you may wish to delete the directory after you have finished with it.
     * This is entirely your responsibility!
     *
     * @param src Source repository path
     * @param dest Destination directory
     * @param bare Clone bare?
     * @param branch Branch to clone
     */
    public GitDb(String src, File dest, boolean bare, String branch, String remote, final String privateKey) throws IOException
    {
        this.privateKey = privateKey;
        this.sshFetchUrl = src;

        try {
            SshSessionFactory factory = new JschConfigSessionFactory() {
                @Override
                public void configure(Host hc, com.jcraft.jsch.Session session) {
                    // // TODO: Bad!
                    // session.setConfig("StrictHostKeyChecking", "no");
                }

                @Override
                protected JSch getJSch(final OpenSshConfig.Host hc,
                        org.eclipse.jgit.util.FS fs) throws JSchException {
                    JSch jsch = super.getJSch(hc, fs);
                    jsch.removeAllIdentity();

                    if (null != privateKey) {
                        jsch.addIdentity(privateKey);
                    }

                    return jsch;
                }
            };

            if (src != null)
                SshSessionFactory.setInstance(factory);

            this.gitHandle =  Git.cloneRepository()
                .setURI(src)
                .setDirectory(dest)
                .setBare(bare)
                .setBranch(branch)
                .setRemote(remote)
                .call();
            this.gitHandle = Git.open(dest);

        } catch (GitAPIException e) {
            log.error(
                    "Error while trying to clone the repository.",
                    e);
            throw new RuntimeException(
                    "Error while trying to clone the repository."+
                    e);
        }
    }

    /**
     * Create a new instance of a GitDb object.
     *
     * This is meant to be used for unit testing, allowing injection of a mocked
     * Git object.
     *
     * @param gitHandle
     *            - The (probably mocked) Git object to use.
     */
    public GitDb(Git gitHandle) {
        Validate.notNull(gitHandle);

        this.privateKey = null;
        this.sshFetchUrl = null;

        this.gitHandle = gitHandle;
    }

    /**
     * getFileByCommitSHA
     *
     * This method will access the git repository given a particular SHA and
     * will attempt to locate a unique file and return a bytearrayoutputstream
     * of the files contents.
     *
     * @param SHA
     *            to search in.
     * @param Full
     *            file path to search for e.g. /src/filename.json
     * @return the ByteArrayOutputStream - which you can extract the file
     *         contents via the toString method.
     * @throws IOException
     * @throws UnsupportedOperationException
     *             - This method is intended to only locate one file at a time.
     *             If your search matches multiple files then this exception
     *             will be thrown.
     */
    public ByteArrayOutputStream getFileByCommitSHA(String sha,
            String fullFilePath) throws IOException,
            UnsupportedOperationException {
        if (sha == null || fullFilePath == null)
            return null;

        ObjectId objectId = this.findGitObject(sha, fullFilePath);

        if (objectId == null)
            return null;

        Repository repository = gitHandle.getRepository();
        ObjectLoader loader = repository.open(objectId);

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        loader.copyTo(out);

        repository.close();
        return out;
    }

    /**
     * This method will configure a treewalk object that can be used to navigate
     * the git repository.
     *
     * @param sha
     *            - the version that the treewalk should be configured to search
     *            within.
     * @return A preconfigured treewalk object.
     * @throws IOException
     * @throws UnsupportedOperationException
     */
    public TreeWalk getTreeWalk(String sha)
            throws IOException, UnsupportedOperationException {
        Validate.notBlank(sha);

        ObjectId commitId = gitHandle.getRepository().resolve(sha);
        if (commitId == null) {
            log.error("Failed to buildGitIndex - Unable to locate resource with SHA: "
                    + sha);
        } else {
            RevWalk revWalk = new RevWalk(gitHandle.getRepository());
            RevCommit commit = revWalk.parseCommit(commitId);

            RevTree tree = commit.getTree();

            TreeWalk treeWalk = new TreeWalk(gitHandle.getRepository());
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);

            return treeWalk;
        }
        return null;
    }

    /**
     * This method will configure a treewalk object that can be used to navigate
     * the git repository.
     *
     * @param sha
     *            - the version that the treewalk should be configured to search
     *            within.
     * @param searchString
     *            - the search string which can be a full path or simply a file
     *            extension.
     * @return A preconfigured treewalk object.
     * @throws IOException
     * @throws UnsupportedOperationException
     */
    public TreeWalk getTreeWalk(String sha, String searchString)
            throws IOException, UnsupportedOperationException {
        Validate.notBlank(sha);
        Validate.notNull(searchString);

        ObjectId commitId = gitHandle.getRepository().resolve(sha);
        if (commitId == null) {
            log.error("Failed to buildGitIndex - Unable to locate resource with SHA: "
                    + sha);
        } else {
            RevWalk revWalk = new RevWalk(gitHandle.getRepository());
            RevCommit commit = revWalk.parseCommit(commitId);

            RevTree tree = commit.getTree();

            TreeWalk treeWalk = new TreeWalk(gitHandle.getRepository());
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);
            treeWalk.setFilter(PathSuffixFilter.create(searchString));

            return treeWalk;
        }
        return null;
    }

    /**
     * Get the git handle for the database
     *
     * @return
     */
    public Repository getGitRepository() {
        return gitHandle.getRepository();
    }

    /**
     * Attempt to verify if an object exists in the git repository for a given
     * sha and full path.
     *
     * @param sha
     * @param fullfilePath
     * @return True if we can successfully find the object, false if not. False
     *         if we encounter an exception.
     */
    public boolean verifyGitObject(String sha, String fullfilePath) {
        try {
            if (findGitObject(sha, fullfilePath) != null) {
                return true;
            }
        } catch (UnsupportedOperationException | IOException e) {
            return false;
        }
        return false;
    }

    /**
     * Check that a commit sha exists within the git repository.
     *
     * @param sha
     * @return True if we have found the git sha false if not.
     */
    public boolean verifyCommitExists(String sha) {
        if (sha == null) {
            log.warn("Null version provided. Unable to verify commit exists.");
            throw new NullPointerException();
        }

        try {
            Iterable<RevCommit> logs = gitHandle.log().all().call();

            for (RevCommit rev : logs) {
                if (rev.getName().equals(sha))
                    return true;
            }

        } catch (NoHeadException e) {
            log.error("Git returned a no head exception. Unable to list all commits.");
            e.printStackTrace();
        } catch (GitAPIException e) {
            log.error("Git returned an API exception. Unable to list all commits.");
            e.printStackTrace();
        } catch (IOException e) {
            log.error("Git returned an IO exception. Unable to list all commits.");
            e.printStackTrace();
        }

        log.debug("Commit " + sha + " does not exist");
        return false;
    }

    /**
     * Get the time of the commit specified
     *
     * @param sha
     *            - to search for.
     * @return integer value representing time since epoch.
     */
    public int getCommitTime(String sha) throws CommitNotFoundException {
        Validate.notBlank(sha);

        try {
            Iterable<RevCommit> logs = gitHandle.log().all().call();

            for (RevCommit rev : logs) {
                if (rev.getName().equals(sha))
                    return rev.getCommitTime();
            }

        } catch (NoHeadException e) {
            log.error("Git returned a no head exception. Unable to list all commits.");
            e.printStackTrace();
        } catch (GitAPIException e) {
            log.error("Git returned an API exception. Unable to list all commits.");
            e.printStackTrace();
        } catch (IOException e) {
            log.error("Git returned an IO exception. Unable to list all commits.");
            e.printStackTrace();
        }

        log.warn("Commit " + sha + " does not exist");
        throw new CommitNotFoundException("Commit " + sha + " does not exist");
    }

    /**
     * Gets a complete list of commits with the most recent commit first.
     *
     * Will return null if there is a problem and will write a log to the
     * configured logger with the stack trace.
     *
     * @return List of the commit shas we have found in the git repository.
     */
    public List<RevCommit> listCommits() {
        List<RevCommit> logList = null;
        try {
            Iterable<RevCommit> logs = gitHandle.log().all().call();
            logList = new ArrayList<RevCommit>();

            for (RevCommit rev : logs) {
                logList.add(rev);
            }

        } catch (GitAPIException e) {
            log.error(
                    "Git returned an API exception. While trying to to list all commits.",
                    e);
        } catch (IOException e) {
            log.error(
                    "Git returned an IO exception. While trying to to list all commits.",
                    e);
        }

        return logList;
    }

    /**
     * This method will execute a fetch on the configured remote git repository
     * and will return the latest sha.
     *
     * @return The version id of the latest version after the fetch.
     */
    public synchronized String pullLatestFromRemote() {
        try {
            SshSessionFactory factory = new JschConfigSessionFactory() {
                @Override
                public void configure(Host hc, com.jcraft.jsch.Session session) {
                    // TODO: Bad!
                    session.setConfig("StrictHostKeyChecking", "no");
                }

                @Override
                protected JSch getJSch(final OpenSshConfig.Host hc,
                        org.eclipse.jgit.util.FS fs) throws JSchException {
                    JSch jsch = super.getJSch(hc, fs);
                    jsch.removeAllIdentity();

                    if (null != privateKey) {
                        jsch.addIdentity(privateKey);
                    }

                    return jsch;
                }
            };

            if (this.sshFetchUrl != null)
                SshSessionFactory.setInstance(factory);

            RefSpec refSpec = new RefSpec("+refs/heads/*:refs/remotes/origin/*");
            FetchResult r = gitHandle.fetch().setRefSpecs(refSpec)
                    .setRemote(sshFetchUrl).call();

            log.debug("Fetched the following advertised Refs."
                    + r.getAdvertisedRefs().toString());
            log.debug("Fetched latest from git result: " + this.getHeadSha());

        } catch (GitAPIException e) {
            log.error(
                    "Error while trying to pull the latest from the remote repository.",
                    e);
        }
        return this.getHeadSha();
    }

    /**
     * Retrieve the SHA that is at the head of the repository (based on all
     * fetched commits)
     *
     * @return String of sha id
     */
    public String getHeadSha() {
        String result = null;

        try {
            ObjectId fetchHead = gitHandle.getRepository().resolve(
                    Constants.FETCH_HEAD);
            if (null != fetchHead) {
                result = fetchHead.getName();
            } else {
                log.warn("Problem fetching head from remote. Providing local head instead.");
                result = gitHandle.getRepository().resolve(Constants.HEAD)
                        .getName();
            }

        } catch (RevisionSyntaxException | IOException e) {
            e.printStackTrace();
            log.error("Error getting the head from the repository.");
        }
        return result;
    }

    /**
     * Will find an object from the git repository if given a sha and a full git
     * path.
     *
     * @param sha
     * @param filename
     * @return ObjectId which will allow you to access information about the
     *         node.
     */
    private ObjectId findGitObject(String sha, String filename)
            throws IOException, UnsupportedOperationException {
        if (sha == null || filename == null) {
            return null;
        }

        Repository repository = gitHandle.getRepository();

        ObjectId commitId = repository.resolve(sha);

        RevWalk revWalk = new RevWalk(repository);
        RevCommit commit = revWalk.parseCommit(commitId);

        RevTree tree = commit.getTree();

        TreeWalk treeWalk = new TreeWalk(repository);
        treeWalk.addTree(tree);
        treeWalk.setRecursive(true);
        treeWalk.setFilter(PathFilter.create(filename));

        int count = 0;
        ObjectId objectId = null;
        String path = null;
        while (treeWalk.next()) {
            count++;
            if (objectId == null) {
                objectId = treeWalk.getObjectId(0);
                path = treeWalk.getPathString();
            }
            // throw exception if we find that there is more than one that
            // matches the search.
            else if (count > 1) {
                StringBuilder sb = new StringBuilder();
                sb.append("Multiple results have been found in the git repository for the following search: ");
                sb.append(filename + ".");
                sb.append(" in ");
                sb.append(sha);
                sb.append(" Unable to decide which one to return.");
                throw new UnsupportedOperationException(sb.toString());
            }
        }

        if (objectId == null) {
            log.warn("No objects found matching the search criteria (" + sha
                    + "," + filename + ") in Git");
            return null;
        }

        revWalk.dispose();
        log.debug("Retrieved Commit Id: " + commitId.getName()
                + " Searching for: " + filename + " found: " + path);
        return objectId;
    }

}
