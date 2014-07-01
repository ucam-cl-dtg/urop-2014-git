/* vim: set et st=4 sts=4 tw=72 : */
package uk.ac.cam.UROP.twentyfourteen;

/**
 * @author Isaac Dunn <ird28@cam.ac.uk>
 * @author Kovacsics Robert <rmk35@cam.ac.uk>
 * @version 0.1
 */
public class GitRepo {
    
    /**
     * Pulls in student repo and tick repo and hands over relevant files to be tested
     */
    public void submit() {
        /* TODO: implement
         * 
         * 1) Pull in student's repo (the one this clas is encapsulating)
         * 2) Pull in ticker's repo (the one the origin of this class)
         * 3) Package up in some format for the testers' API to call
         * 4) Call the testers' test function
         * 4) i) Perhaps we could call it with an argument of this
         *       class, then they could use the visitor pattern on to
         *       get at the files and then use the saveTestResults
         *       method to give us the test results.
         */
    }
    
    /**
     * Saves test results into mongo database.
     * <p>
     * Needs to store commit number or similar, so that the test results cannot be changed.
     * 
     * @param results The results to be saved
     */
    public void saveTestResults(Object results) { // won't be an Object eventually
        /* TODO: implement
         *
         * 1) Append the commit hash to the test result
         * 2) Store the test result in a database
         */
    }
    
    /**
     * Returns requested test results.
     * 
     * @param request Specifies which results are wanted
     * 
     * @returns The requested test results
     */
    public Object getTestResults(String request) { // these types will change
        /* TODO: implement
         *
         * 1) Get the test result corresponding to the request
         * 2) Get the commit the test result refers to
         * 3) Return files and comments
         */
        return new Object();
    }

}
