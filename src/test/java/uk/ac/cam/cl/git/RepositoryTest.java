/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Kovacsics Robert &lt;rmk35@cam.ac.uk&gt;
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 * @version 0.1
 */
public class RepositoryTest
{
    private List<String> readOnlys;
    private List<String> readAndWrites;
    private List<String> emptyList;
    private Repository testRepo1;
    private Repository testRepo2;
    private Repository testRepo3;
    
    @Before
    public void setUp() {
        readOnlys = new LinkedList<String>();
        readAndWrites = new LinkedList<String>();
        emptyList = new LinkedList<String>();
        readOnlys.add("readonlyUser1");
        readOnlys.add("readonlyUser2");
        readOnlys.add("readonlyUser3");
        readAndWrites.add("adminUser1");
        readAndWrites.add("adminUser2");
        testRepo1 = new Repository("test-repo-name1",
                "repository-owner", readAndWrites, readOnlys, 
                "test-parent1", "hidden-eg-parent");
        testRepo2 = new Repository("test-repo-name2",
                "repository-owner", readAndWrites, emptyList, 
                "test-parent2", "hidden-eg-parent");
        testRepo3 = new Repository("test-repo-name3",
                "other-repository-owner", emptyList, readOnlys, 
                "test-parent2", "hidden-eg-parent-3");
    }
    
    /**
     * Tests that the String representations of repositories are as expected.
     */
    @Test
    public void checkStringRepresentation() {
        String shouldBeTestRepo1 = 
                "repo test-repo-name1" + "\n" +
                "     RW = adminUser1 adminUser2 " + "\n" +
                "     R  = readonlyUser1 readonlyUser2 readonlyUser3 " + "\n";
        String shouldBeTestRepo2 =
                "repo test-repo-name2" + "\n" +
                "     RW = adminUser1 adminUser2 " + "\n";
        String shouldBeTestRepo3 = 
                "repo test-repo-name3" + "\n" +
                "     R  = readonlyUser1 readonlyUser2 readonlyUser3 " + "\n";
        assertEquals(shouldBeTestRepo1, testRepo1.toString());
        assertEquals(shouldBeTestRepo2, testRepo2.toString());
        assertEquals(shouldBeTestRepo3, testRepo3.toString());
                        
    }

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
