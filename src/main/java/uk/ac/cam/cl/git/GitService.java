/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.LinkedList;

import javax.ws.rs.core.Response;

import uk.ac.cam.cl.git.public_interfaces.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GitService implements WebInterface {
    /* For logging */
    private static final Logger log = LoggerFactory.getLogger(GitService.class);
   
    @Override
    public Response listRepositories() {
        List<Repository> repos = ConfigDatabase.getRepos();
        List<String> toReturn = new LinkedList<String>();
        for (Repository repo : repos) {
            toReturn.add(repo.getName());
        }
        return Response.status(200).entity(toReturn).build();
    }

    @Override
    public Response listFiles(String repoName) throws IOException
    {
        if (repoName == null)
            return Response.status(400)
                .entity("No repository given.").build();

        Repository repo = ConfigDatabase.getRepoByName(repoName);
        if (repo == null)
            return Response.status(404)
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
            return Response.status(500)
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

    @Override
    public Response getFile(String fileName
                          , String repoName) throws IOException
    {
        if (repoName == null)
            return Response.status(400)
                .entity("No repository given.").build();

        Repository repo = ConfigDatabase.getRepoByName(repoName);
        if (repo == null)
            return Response.status(404)
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
            return Response.status(500)
                .entity("Repository not found on disk! "
                        + "Removed from database.").build();
        }

        String output = repo.getFile(fileName);
        if (output == null)
            return Response.status(404).build();
        return Response.status(200).entity(output).build();
    }
    
    @Override
    public Response getForkURL(ForkRequestBean details) throws IOException
    {   /* TODO: Test */
        /* This forks the upstream repository
         * This may fail due to permissions, or the shell of tomcat7
         * Currently works with the shell `rssh' which is meant to be
         * restricted.
         */
        log.info("Forking repository \"" + details.getRepoName() + ".git\""
                + " to \"" + details.getNewRepoName() + ".git\""
                + " for user \"" + details.getRepoOwner() + "\"");
        Repository rtn = new Repository(details.getNewRepoName()
                                      , details.getRepoOwner()
                                      , null /* RW */
                                      , null /* RO */
                                      , details.getRepoName()
                                      , details.getOverlay());
        ConfigDatabase.addRepo(rtn);
        // TODO: better return, e.g. NewRepoName (as repoName perhaps)
        return Response.status(200).entity(rtn.getRepoPath()).build();
    }

    @Override
    public Response addRepository(AddRequestBean details) throws IOException
    {
        log.info("Creating new repository \"" + details.getRepoName()
                + ".git\"" + " for user \"" + details.getRepoOwner()
                + "\"");
        Repository rtn = new Repository(details.getRepoName()
                                      , details.getRepoOwner()
                                      , null
                                      , null);
        ConfigDatabase.addRepo(rtn);
        // TODO: better return
        return Response.status(200).entity(rtn.getRepoPath()).build();
    }

    @Override
    public Response delRepository(String repoName) throws IOException
    {
        ConfigDatabase.delRepoByName(repoName);
        return Response.status(200).build();
    }
}
