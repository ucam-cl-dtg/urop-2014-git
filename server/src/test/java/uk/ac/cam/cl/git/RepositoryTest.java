/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */

package uk.ac.cam.cl.git;

import uk.ac.cam.cl.git.api.IllegalCharacterException;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Kovacsics Robert &lt;rmk35@cam.ac.uk&gt;
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 * @version 0.1
 */
public class RepositoryTest
{
    private List<String> readOnlys = new LinkedList<String>();
    private List<String> readAndWrites = new LinkedList<String>();
    private List<String> emptyList = new LinkedList<String>();
    private Repository testRepo1, testRepo2, testRepo3, testRepo4;

    @Before
    public void initialize() throws IllegalCharacterException
    {
        readOnlys.add("readonlyUser1");
        readOnlys.add("readonlyUser2");
        readOnlys.add("readonlyUser3");
        readAndWrites.add("adminUser1");
        readAndWrites.add("adminUser2");

        testRepo1 = new Repository("test-repo-name1",
                "repository-owner", readAndWrites, readOnlys,
                "parent1");
        testRepo2 = new Repository("test-repo-name2",
                "repository-owner", readAndWrites, emptyList,
                "parent2");
        testRepo3 = new Repository("test-repo-name3",
                "other-repository-owner", emptyList, readOnlys,
                "parent3");
        testRepo4 = new Repository("test-repo-name4",
                "yet-another-repository-owner", emptyList, emptyList,
                "parent4");
    }

    /**
     * Tests that the String representations of repositories are as expected.
     */
    @Test
    public void checkStringRepresentation() {
        String shouldBeTestRepo1 =
                "repo test-repo-name1" + "\n" +
                "     RW = repository-owner tomcat7 adminUser1 adminUser2" + "\n" +
                "     R  = ro-repository-owner readonlyUser1 readonlyUser2 readonlyUser3" + "\n" +
                "# parent1" + "\n";
        String shouldBeTestRepo2 =
                "repo test-repo-name2" + "\n" +
                "     RW = repository-owner tomcat7 adminUser1 adminUser2" + "\n" +
                "     R  = ro-repository-owner" + "\n" +
                "# parent2" + "\n";
        String shouldBeTestRepo3 =
                "repo test-repo-name3" + "\n" +
                "     RW = other-repository-owner tomcat7" + "\n" +
                "     R  = ro-other-repository-owner readonlyUser1 readonlyUser2 readonlyUser3" + "\n" +
                "# parent3" + "\n";
        String shouldBeTestRepo4 =
                "repo test-repo-name4" + "\n" +
                "     RW = yet-another-repository-owner tomcat7" + "\n" +
                "     R  = ro-yet-another-repository-owner" + "\n" +
                "# parent4" + "\n";
        assertEquals(shouldBeTestRepo1, testRepo1.toString());
        assertEquals(shouldBeTestRepo2, testRepo2.toString());
        assertEquals(shouldBeTestRepo3, testRepo3.toString());
        assertEquals(shouldBeTestRepo4, testRepo4.toString());
    }
}
