/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.easymock;

/**
 * @author ird28
 *
 */
public class ToMock {
    
    private int someState;
        
    public ToMock() {
        someState = 7;
    }
    
    public int inc() {
        return someState++;
    }

}
