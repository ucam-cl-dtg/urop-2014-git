/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git;

import static org.junit.Assert.*;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * @author Kovacsics Robert &lt;rmk35@cam.ac.uk&gt;
 * @version 0.1
 */
public class RepositoryTest
{

    /**
     * Clones the testing repository and checks the README.md and
     * Test.java file, then deletes the repository.
     */
	@Test
	public void testing_repository()
	{
        try
        {
            Repository testRepo = new Repository("testing", "rmk35", null, null, null, null);
            File tmpDir = createTempDirectory();
            assertTrue(tmpDir.exists());

            testRepo.cloneTo(tmpDir);
            assertNotNull(testRepo.handle);
            assertNotNull(testRepo.workingCommit);

            // Check contents of test repo
            for (String s : testRepo.getSources("README.md"))
                assertEquals("Checking for the existence of README.md", s, "README.md");
            for (String s : testRepo.getSources("java"))
                assertEquals("Checking for the existence of Test.java", s, "src/main/java/Test.java");

            recursiveDelete(tmpDir);
            assertFalse(tmpDir.exists());
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

    private static void recursiveDelete(File f)
    {
        if (f.isDirectory())
        {
            if (f.list().length == 0)
                f.delete();
            else
            {
                for (String child : f.list())
                    recursiveDelete(new File(f, child));
                f.delete();
            }

        }
        else
        {
            f.delete();
        }
        
    }
}
