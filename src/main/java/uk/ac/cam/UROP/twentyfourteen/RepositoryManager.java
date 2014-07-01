/* vim: set et ts=4 sts=4 sw=4 tw=4 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.UROP.twentyfourteen;

import java.util.List;

/**
 * @author Isaac Dunn <ird28@cam.ac.uk>
 * @author Kovacsics Robert <rmk35@cam.ac.uk>
 * @version 0.1
 */
public class RepositoryManager {

    /**
     * Creates a new Repository.
     * 
     * @param name Name of the repository to be created
     * @param users List of users with access permissions to this repository
     *  
     * @return A newly created repository.
     */
    public Repository newRepo(String name, List<String> users) {
        /* TODO: implement
         *
         * 1) Create new GitRepo class
         * 2) Add repository to database via ConfigDatabase
         */
        return new Repository();
    }
    
    
    /**
     * Forks the appropriate tick repository, including the files that the student needs to access only. 
     * 
     * @param name Name of the repository to be created
     * @param origin Repository to be forked
     * 
     * @returns A forked repository.
     */
    public Repository forkRepo(String name, Repository origin) {
        /* TODO: implement
         *
         * 1) Clone repository into new directory, with a depth of one
         * 2) Create a new GitRepo class
         */
        return new Repository(); //TODO fill in this method
    }

}
