/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git.public_interfaces;

import java.io.IOException;

import uk.ac.cam.cl.git.ForkRequestBean;
import uk.ac.cam.cl.git.AddRequestBean;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.core.Response;

/**
 * This is the web interface that is to be used for accessing 
 * repositories and their contents, making fork requests to
 * existing repositories, and requesting the creation of an
 * empty new repository.
 * 
 * @author Kovacsics Robert &lt;rmk35@cam.ac.uk&gt;
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
/* TODO: Change from Response to whatever we are returning and for
 * errors, use exceptions, as they are sent across the wire.
 */
@Path("/")
public interface WebInterface {
    /**
     * Returns a JSON object listing the names of the repositories
     * currently stored in the database.
     * @return The JSON list of repositories
     */
    @GET
    @Path("/git")
    @Produces("application/json")
    public Response listRepositories();

    /**
     * Returns a JSON object listing all of the files contained within
     * the repository specified.
     * 
     * @param repoName The name of the repository whose contents are to be listed
     * @return The JSON list of contained filenames
     * @throws IOException
     */
    @GET
    @Path("/git/{repoName:.*}.git")
    @Produces("application/json")
    public Response listFiles(@PathParam("repoName") String repoName) throws IOException;

    /**
     * Returns the contents of the file requested.
     * 
     * @param fileName The name of the file whose contents are to be returned
     * @param repoName The name of the repository containing the file
     * @return The requested file
     * @throws IOException
     */
    @GET
    @Path("/git/{repoName:.*}.git/{fileName:.*}")
    @Produces("application/octet-stream")
    public Response getFile(@PathParam("fileName") String fileName
                          , @PathParam("repoName") String repoName) throws IOException;
    
    /**
     * Forks the specified repository and returns the URL that can be used
     * to clone the forked repository.
     * 
     * @param details Object giving the relevant information (see ForkRequestInterface)
     * @return The URL of the forked repository
     * @throws IOException
     */
    @POST
    @Path("/fork")
    @Consumes("application/json")
    @Produces("application/json")
    public Response getForkURL(ForkRequestBean details) throws IOException;
    
    /**
     * Creates a new blank repository and returns the URL than can be used
     * to clone the new repository.
     *  
     * @param details Object giving the relevant information (see AddRequestInterface)
     * @return The URL of the new repository
     */
    @PUT
    @Path("/add")
    @Consumes("application/json")
    @Produces("application/json")
    public Response addRepository(AddRequestBean details) throws IOException;

    /**
     * Removes the repository from the configuration and the database.
     * <p>
     * Does not remove repository from file system, to remove stale
     * repositories, run TODO: stale repository detector.
     */
    @DELETE
    @Path("/del/{repoName:.*}.git")
    public Response delRepository(@PathParam("repoName") String repoName) throws IOException;
}
