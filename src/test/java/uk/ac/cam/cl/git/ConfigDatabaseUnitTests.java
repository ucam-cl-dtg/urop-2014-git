/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.git;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import java.io.IOException;

import org.easymock.*;
import org.junit.Test;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 * @version 0.1
 */
public class ConfigDatabaseUnitTests extends EasyMockSupport {

    private static List<String> readOnlys = new LinkedList<String>();
    private static List<String> readAndWrites = new LinkedList<String>();
    private static List<String> emptyList = new LinkedList<String>();
    private static Repository testRepo1 = new Repository("test-repo-name1",
            "repository-owner", readAndWrites, readOnlys);
    private static Repository testRepo2 = new Repository("test-repo-name2",
            "repository-owner", readAndWrites, emptyList);
    @Mock
    private  RepositoryCollection mockCollection =
    createMock(RepositoryCollection.class);

    {
        ConfigDatabase.setReposCollection(mockCollection);
    }

    static {
        readOnlys.add("readonlyUser1");
        readOnlys.add("readonlyUser2");
        readAndWrites.add("adminUser");
    }

    /**
     * Checks that repositories can be added to the database.
     * @throws DuplicateKeyException 
     */
    @Test
    public void testAddRepo() throws IOException, DuplicateKeyException {

        /* The below method calls to the database are expected */

        mockCollection.insertRepo(testRepo1);
        EasyMock.expectLastCall().once();

        mockCollection.insertRepo(testRepo2);
        EasyMock.expectLastCall().once();

        EasyMock.replay(mockCollection);

        /* The actual test begins here */

        ConfigDatabase.addRepo(testRepo1);
        ConfigDatabase.addRepo(testRepo2);

        EasyMock.verify(mockCollection);
    }

    /**
     * Checks that finding repositories by name calls the correct methods
     */
    @Test
    public void testGetRepoByName() {

        /* The below method calls to the database are expected */

        EasyMock.expect(mockCollection
                .findByName("test-repo-name1"))
                .andReturn(testRepo1);

        EasyMock.expect(mockCollection
                .findByName("test-repo-name2"))
                .andReturn(testRepo2);

        EasyMock.replay(mockCollection);

        /* The actual test begins here */

        assertEquals(testRepo1, ConfigDatabase.getRepoByName("test-repo-name1"));
        assertEquals(testRepo2, ConfigDatabase.getRepoByName("test-repo-name2"));

        EasyMock.verify(mockCollection);
    }

    /**
     * Checks that the correct method is called when updating a repository
     */
    @Test
    public void testUpdateRepo() throws IOException {

        /* The below method calls to the database are expected */

        mockCollection.updateRepo(testRepo1);
        EasyMock.expectLastCall().once();

        mockCollection.updateRepo(testRepo2);
        EasyMock.expectLastCall().once();

        EasyMock.replay(mockCollection);

        /* The actual test begins here */

        ConfigDatabase.updateRepo(testRepo1);
        ConfigDatabase.updateRepo(testRepo2);

        EasyMock.verify(mockCollection);
    }



}
