/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git.interfaces;

import java.io.IOException;
import java.util.List;

import uk.ac.cam.cl.git.api.*;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;

/**
 * This is the web interface that is to be used for accessing
 * repositories and their contents, making fork requests to
 * existing repositories, and requesting the creation of an
 * empty new repository. It can also be used to delete
 * repositories, throw an exception (for testing), and add
 * an SSH key to gitolite.
 *
 * @author Kovacsics Robert &lt;rmk35@cam.ac.uk&gt;
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
@Path("/")
public interface WebInterface {
    /**
     * @return A list of the names of the repositories currently stored in the database
     */
    @GET
    @Path("/repos")
    @Produces("application/json")
    public List<String> listRepositories();

    /**
     * Lists the commits.
     *
     * @param repoName The name of the repository.
     */
    @GET
    @Path("/repos/{repoName:.*}.git")
    @Produces("application/json")
    public List<Commit> listCommits(@PathParam("repoName") String repoName)
            throws IOException, RepositoryNotFoundException;

    /**
     * Resolves a commit reference (such as HEAD) to a SHA-1.
     *
     * @param repoName The name of the repository whose files are to be listed
     * @param commitID Either a SHA-1 or some other way for git to
     * identify the commit, e.g. HEAD or master TODO: <- Check validity.
     * @return A list of the filenames in the given repository
     * @throws IOException
     * @throws RepositoryNotFoundException
     */
    @GET
    @Path("/resolve/{repoName:.*}.git/{commitName}")
    @Produces("text/plain")
    public String resolveCommit(@PathParam("repoName")   String repoName
                              , @PathParam("commitName") String commitName)
            throws IOException, RepositoryNotFoundException;

    /**
     * @param repoName The name of the repository whose files are to be listed
     * @param commitID Either a SHA-1 or some other way for git to
     * identify the commit, e.g. HEAD or master TODO: <- Check validity.
     * @return A list of the filenames in the given repository
     * @throws IOException
     * @throws RepositoryNotFoundException
     */
    @GET
    @Path("/repos/{repoName:.*}.git/{commitID}")
    @Produces("application/json")
    public List<String> listFiles(@PathParam("repoName") String repoName
                                , @PathParam("commitID") String commitID)
            throws IOException, RepositoryNotFoundException;

    /**
     * @param fileName The name of the file whose contents are to be returned
     * @param repoName The name of the repository containing the file
     * @param commitID The revision to get.
     * @return The contents of the specified file
     * @throws IOException
     * @throws RepositoryNotFoundException
     */
    @GET
    @Path("/repos/{repoName:.*}.git/{commitID}/{fileName:.*}")
    @Produces("application/octet-stream")
    public String getFile(@PathParam("fileName") String fileName
                        , @PathParam("commitID") String commitID
                        , @PathParam("repoName") String repoName)
                        throws IOException, RepositoryNotFoundException;

    /**
     * Packs up the repository into a JSON file. Used to make only one
     * call to retrieve the repository, instead of multiple calls.
     *
     * @param repoName The name of the repo.
     * @param commitID The revision to pack up.
     * @return A list of {name: ?, content: ?} objects.
     */
    @GET
    @Path("/bundle/{repoName:.*}.git/{commitID}")
    @Produces("application/json")
    public List<FileBean> getAllFiles(@PathParam("repoName") String repoName
                                    , @PathParam("commitID") String commitID)
                        throws IOException, RepositoryNotFoundException;

    /**
     * Forks the specified repository and returns the URL that can be used
     * to clone the forked repository.
     *
     * @param details ForkRequestBean giving the necessary information
     * @return The URL of the forked repository
     * @throws IOException
     * @throws DuplicateRepoNameException
     */
    @POST
    @Path("/fork")
    @Consumes("application/json")
    @Produces("application/json")
    public String forkRepository(ForkRequestBean details)
            throws IOException, DuplicateRepoNameException;

    /**
     * Creates a new blank repository and returns the URL than can be used
     * to clone it.
     *
     * @param details RepoUserRequestBean giving the necessary information
     * @return The URL of the new repository
     * @throws IOException
     * @throws DuplicateRepoNameException
     */
    @PUT
    @Path("/add")
    @Consumes("application/json")
    @Produces("application/json")
    public String addRepository(RepoUserRequestBean details)
            throws IOException, DuplicateRepoNameException;

    /**
     * Removes the repository from the configuration and the database.
     * <p>
     * Does not remove repository from file system, to remove dangling
     * repositories, run removeDanglingRepos(); (though list them first
     * with getDanglingRepos(); to be sure!).
     *
     * @param repoName The name of the repository to be deleted
     * @throws IOException
     * @throws RepositoryNotFoundException
     */
    @DELETE
    @Path("/del/{repoName:.*}.git")
    public void deleteRepository(@PathParam("repoName") String repoName)
            throws IOException, RepositoryNotFoundException;

    /**
     * @throws HereIsYourException Certain to be thrown each call
     */
    @GET
    @Path("/exception-please")
    public void getMeAnException() throws HereIsYourException;

    /**
     * Adds an SSH key to the collection of SSH keys. If the key already
     * exists, it just overwrites it.
     *
     * @param key The body of the PUT request, given as plain text.
     * @param userName The name (CRSID) of the owner of the key.
     */
    @PUT
    @Path("/ssh/add/{userName}")
    @Consumes("text/plain")
    public void addSSHKey(String key, @PathParam("userName") String userName)
            throws IOException;

    /**
     * Returns the SSH URI that can be used to clone the given repository.
     * @throws RepositoryNotFoundException
     */
    @GET
    @Path("/URI/{repoName:.*}.git/")
    public String getRepoURI(@PathParam("repoName") String repoName) throws RepositoryNotFoundException;

    /**
     * Gives the specified user read-only access (allows cloning of) the
     * given repository.
     * @throws IOException
     * @throws RepositoryNotFoundException
     */
    @POST
    @Consumes("application/json")
    @Path("/permissions/add")
    public void addReadOnlyUser(RepoUserRequestBean details) throws IOException, RepositoryNotFoundException;

    /**
     * Gives the dangling repositories (those not managed by gitolite).
     *
     * @return The dangling repositories.
     */
    @GET
    @Path("/dangling-repos")
    @Produces("application/json")
    public List<String> getDanglingRepos() throws IOException;

    /**
     * Permanently removes the dangling repositories. Use with caution, at
     * least check with getDanglingRepos first before removing.
     */
    @DELETE
    @Consumes("application/json")
    @Path("/dangling-repos")
    public void removeDanglingRepos() throws IOException;

    /**
     * Rebuilds repository database from gitolite configuration file, in
     * case the two become out of sync.
     */
    @GET
    @Path("/rebuild-database")
    public void rebuildDatabase() throws IOException, DuplicateRepoNameException;
}
