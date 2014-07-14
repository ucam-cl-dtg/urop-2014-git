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
public class ClassToTest {

    private ToMock m;
    
    public void setToMock(ToMock m) {
        this.m = m;
    }
    
    public int exampleMethod() {
        return m.inc();
    }

}
