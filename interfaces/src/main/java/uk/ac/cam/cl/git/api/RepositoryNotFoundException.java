/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.git.api;

/**
 * @author ird28
 *
 */
public class RepositoryNotFoundException extends Exception {
    
    private static final long serialVersionUID = -1330502365505285622L; // generated ID
    
    public RepositoryNotFoundException(String message) {
        super(message);
    }
    
    public RepositoryNotFoundException() {
        super();
    }

}
