package uk.ac.cam.UROP.twentyfourteen;

import java.util.List;
import java.util.LinkedList;

import javax.ws.rs.GET;
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

}
