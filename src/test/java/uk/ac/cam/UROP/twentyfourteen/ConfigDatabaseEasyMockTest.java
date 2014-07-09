/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.UROP.twentyfourteen;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.IOException;

import org.easymock.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author ird28
 *
 * Note that this test doesn't actually work yet
 */
public class ConfigDatabaseEasyMockTest extends EasyMockSupport {
	
	private Repository testRepo;
	
	@Before
	public void setUp() {
		testRepo = createMock(Repository.class);
		ConfigDatabase.addRepo(testRepo);
	}
	
	@After
	public void tearDown() {
		testRepo = null;
	}
	
	@Test
	public void testGenerateConfig() {
		testRepo.toString();
		replayAll();
		ConfigDatabase.generateConfigFile();
		verifyAll();
	}

}
