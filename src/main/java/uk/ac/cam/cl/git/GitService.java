/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git;

import java.util.List;
import java.util.LinkedList;

import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/")
public class GitService {
   
	@GET
	@Path("/git")
	public Response listRepositories() {
	    List<Repository> repos = ConfigDatabase.getRepos();
	    List<String> toReturn = new LinkedList<String>();
	    for (Repository repo : repos) {
	        toReturn.add(repo.getName());
	    }
	    return Response.status(200).entity(toReturn.toString()).build();
	}

    @GET
    @Path("/git/{repoName}")
    public Response listFiles(@PathParam("repoName") String repoName)
    {
        /* TODO
         * 1. Open repository and list files
         * 2. Return files
         */
        return Response.status(200).entity(repoName).build();
    }

    @GET
    @Path("/git/{repoName}/{fileName:.*}")
    public Response getFile(@PathParam("fileName") String fileName
                          , @PathParam("repoName")  String repoName)
    {
        /* TODO
         * 1. Open the file given by filePath in the Git repository
         *    repoName. This can be done with GitDb and TreeWalker
         *    somehow, to prevent the need of cloning the repository.
         * 2. Give the contents of the file as the entity
         */
        return Response.status(200).entity(fileName).build();
    }
}
