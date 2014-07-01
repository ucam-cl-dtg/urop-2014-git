package uk.ac.cam.UROP.twentyfourteen;

public class GitRepoManager {

	/**
	 * Creates a new GitRepo.
	 * 
	 * @param name Name of the repository to be created
	 *  
	 * @return A newly created repository.
	 */
	public GitRepo newRepo(String name) {
		return new GitRepo(); //TODO fill in this method
	}
	
	
	/**
	 * Forks the appropriate tick repository, including the files that the student needs to access only. 
	 * 
	 * @param name Name of the repository to be created
	 * @param origin Repository to be forked
	 * 
	 * @returns A forked repository.
	 */
	public GitRepo forkRepo(String name, GitRepo origin) {
		return new GitRepo(); //TODO fill in this method
	}

}
