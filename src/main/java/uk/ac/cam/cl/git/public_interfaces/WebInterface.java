/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git.public_interfaces;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/")
public interface WebInterface {
    /* TODO: JavaDoc */
    @GET
    @Path("/git")
    @Produces("application/json")
    public Response listRepositories();

    @GET
    @Path("/git/{repoName:.*}.git")
    @Produces("application/json")
    public Response listFiles(@PathParam("repoName") String repoName) throws IOException;

    @GET
    @Path("/git/{repoName:.*}.git/{fileName:.*}")
    @Produces("text/plain")
    public Response getFile(@PathParam("fileName") String fileName
                          , @PathParam("repoName") String repoName) throws IOException;
    
    @POST
    @Path("/fork")
    public Response getForkURL(ForkRequestInterface details);
}
