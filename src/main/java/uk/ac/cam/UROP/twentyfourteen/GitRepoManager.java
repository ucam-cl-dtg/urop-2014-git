/* vim: set et st=4 sts=4 tw=72 : */
package uk.ac.cam.UROP.twentyfourteen;

import java.util.List;

/**
 * @author Isaac Dunn <ird28@cam.ac.uk>
 * @author Kovacsics Robert <rmk35@cam.ac.uk>
 * @version 0.1
 */
public class GitRepoManager {

    /**
     * Creates a new GitRepo.
     * 
     * @param name Name of the repository to be created
     * @param users List of users with access permissions to this repository
     *  
     * @return A newly created repository.
     */
    public GitRepo newRepo(String name, List<String> users) {
        /* TODO: implement
         *
         * 1) Create new GitRepo class
         * 2) Add repository to database via ConfigDatabase
         */
        return new GitRepo();
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
        /* TODO: implement
         *
         * 1) Clone repository into new directory, with a depth of one
         * 2) Create a new GitRepo class
         */
        return new GitRepo(); //TODO fill in this method
    }

}
