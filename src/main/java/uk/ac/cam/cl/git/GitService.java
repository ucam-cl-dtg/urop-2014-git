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
        if (repoName == null)
            return Response.status(/*TODO:*/400)
                .entity("No repository given.").build();

        Repository repo = ConfigDatabase.getRepoByName(repoName);
        if (repo == null)
            return Response.status(/*TODO:*/500)
                .entity("Repository not found in database! "
                        + "It may exist on disk though.").build();

        try
        {
            repo.openLocal(repoName);
        }
        catch (org.eclipse.jgit.errors.RepositoryNotFoundException e)
        {
            /* Stale repository entry, remove from database */
            ConfigDatabase.delRepoByName(repoName);
            return Response.status(/*TODO:*/500)
                .entity("Repository not found on disk! "
                        + "Removed from database.").build();
        }

        Collection<String> files = repo.getSources();
        List<String> rtn = new LinkedList<String>();
        if (files != null)
            for (String file : files)
                rtn.add(file);

        return Response.status(200).entity(rtn).build();
    }

    @GET
    @Path("/git/{repoName}/{fileName:.*}")
    public Response getFile(@PathParam("fileName") String fileName
                          , @PathParam("repoName") String repoName) throws IOException
    {
        if (repoName == null)
            return Response.status(/*TODO:*/400)
                .entity("No repository given.").build();

        Repository repo = ConfigDatabase.getRepoByName(repoName);
        if (repo == null)
            return Response.status(/*TODO:*/500)
                .entity("Repository not found in database! "
                        + "It may exist on disk though.").build();

        try
        {
            repo.openLocal(repoName);
        }
        catch (org.eclipse.jgit.errors.RepositoryNotFoundException e)
        {
            /* Stale repository entry, remove from database */
            ConfigDatabase.delRepoByName(repoName);
            return Response.status(/*TODO:*/500)
                .entity("Repository not found on disk! "
                        + "Removed from database.").build();
        }

        String output = repo.getFile(fileName);
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
