/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.easymock;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.easymock.EasyMockSupport;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Before;
import org.junit.Test;

/**
 * @author ird28
 *
 */
public class EasyMockTest extends EasyMockSupport {
    
    private ToMock mocked;
    private ClassToTest testSubj;
    
    @Before
    public void setUp() {
        mocked = createMock(ToMock.class);
        testSubj = new ClassToTest();
        testSubj.setToMock(mocked);
    }

    @Test
    public void test() {
        EasyMock.expect(mocked.inc()).andReturn(7);
        EasyMock.replay(mocked);
        assertEquals(7, testSubj.exampleMethod());
        EasyMock.verify(mocked);
    }

}
