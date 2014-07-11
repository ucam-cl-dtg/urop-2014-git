/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import uk.ac.cam.cl.git.configuration.ConfigurationLoader;
import uk.ac.cam.cl.git.database.GitDb;

@Path("/")
public class GitService {
   
	@GET
	@Path("/git")
	@Produces("application/json")
	public Response listRepositories() {
	    List<Repository> repos = ConfigDatabase.getRepos();
	    List<String> toReturn = new LinkedList<String>();
	    for (Repository repo : repos) {
	        toReturn.add(repo.getName());
	    }
	    return Response.status(200).entity(toReturn).build();
	}

    @GET
    @Path("/git/{repoName}")
    @Produces("application/json")
    public Response listFiles(@PathParam("repoName") String repoName) throws IOException
    {
        Repository repo = ConfigDatabase.getRepoByName(repoName);
        Collection<String> files = repo.getSources();
        List<String> toReturn = new LinkedList<String>();
        for (String file : files) {
            toReturn.add(file);
        }
        return Response.status(200).entity(toReturn).build();
    }

    @GET
    @Path("/git/{repoName}/{fileName:.*}")
    public Response getFile(@PathParam("fileName") String fileName
                          , @PathParam("repoName") String repoName) throws IOException
    {
        GitDb gitDB = new GitDb(ConfigurationLoader.getConfig()
                                    .getGitoliteHome()
                                    + "/repositories/" + repoName);
        String output = gitDB.getFileByCommitSHA(gitDB.getHeadSha(), fileName).toString();
        /* TODO
         * 1. Open the file given by filePath in the Git repository
         *    repoName. This can be done with GitDb and TreeWalker
         *    somehow, to prevent the need of cloning the repository.
         * 2. Give the contents of the file as the entity
         */
        return Response.status(200).entity(output).build();
    }
    
    @POST
    @Path("/fork")
    public Response getForkURL()
    {
        // TODO implement
        return Response.status(503).build();
    }
}
