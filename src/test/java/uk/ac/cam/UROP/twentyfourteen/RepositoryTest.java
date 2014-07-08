/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.UROP.twentyfourteen;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author kr2
 *
 */
public class RepositoryTest
{

	@Test
	public void test()
	{
        try
        {
            Repository testRepo = new Repository("testing", "rmk35");
            File tmpDir = createTempDirectory();
            testRepo.cloneTo(tmpDir);
            assertNotNull(testRepo.handle);
            assertNotNull(testRepo.workingCommit);

            // Check contents of test repo
            for (String s : testRepo.getSources("README.md"))
                assertEquals("Checking for the existence of README.md", s, "README.md");
            for (String s : testRepo.getSources("java"))
                assertEquals("Checking for the existence of Test.java", s, "src/main/java/Test.java");
        }
        catch (IOException e)
        {
            fail("Got I/O Exception!\n" + e.getMessage());
        }
        catch(EmptyDirectoryExpectedException e)
        {
            fail("Temporary directory was expected to be empty!");
        }
	}

    private static File createTempDirectory() throws IOException
    {
        final File temp;

        temp = File.createTempFile("temp", Long.toString(System.nanoTime()));

        if(!(temp.delete()))
        {
            throw new IOException("Could not delete temp file: " + temp.getAbsolutePath());
        }

        if(!(temp.mkdir()))
        {
            throw new IOException("Could not create temp directory: " + temp.getAbsolutePath());
        }

        return (temp);
    }
}
