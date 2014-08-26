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
import org.junit.Before;

import uk.ac.cam.cl.git.api.DuplicateRepoNameException;
import uk.ac.cam.cl.git.api.RepositoryNotFoundException;
import uk.ac.cam.cl.git.api.IllegalCharacterException;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 * @version 0.1
 */
public class ConfigDatabaseTest extends EasyMockSupport {

    ConfigDatabase partiallyMockedConfigDatabase = EasyMock
            .createMockBuilder(ConfigDatabase.class)
            .addMockedMethods("generateConfigFile", "runGitoliteUpdate")
            .createMock();

    private List<String> readOnlys = new LinkedList<String>();
    private List<String> readAndWrites = new LinkedList<String>();
    private List<String> emptyList = new LinkedList<String>();
    private Repository testRepo1, testRepo2;

    @Before
    public void initialize() throws IllegalCharacterException
    {
        testRepo1 = new Repository("test-repo-name1",
                "repository-owner", readAndWrites, readOnlys);
        testRepo2 = new Repository("test-repo-name2",
                "repository-owner", readAndWrites, emptyList);

        readOnlys.add("readonlyUser1");
        readOnlys.add("readonlyUser2");
        readAndWrites.add("adminUser");
    }

    private RepositoryCollection mockCollection =
        createMock(RepositoryCollection.class);

    {
        partiallyMockedConfigDatabase.setReposCollection(mockCollection);
    }

    /**
     * Checks that repositories can be added to the database.
     * @throws DuplicateRepoNameException
     */
    @Test
    public void testAddRepo() throws IOException,
           DuplicateRepoNameException, IllegalCharacterException {

        /* The method calls below are expected */
        mockCollection.insertRepo(testRepo1);
        EasyMock.expectLastCall().once();
        partiallyMockedConfigDatabase.generateConfigFile();
        EasyMock.expectLastCall().once();

        mockCollection.insertRepo(testRepo2);
        EasyMock.expectLastCall().once();
        partiallyMockedConfigDatabase.generateConfigFile();
        EasyMock.expectLastCall().once();

        EasyMock.replay(mockCollection);
        EasyMock.replay(partiallyMockedConfigDatabase);

        /* The actual test begins here */
        partiallyMockedConfigDatabase.addRepo(testRepo1);
        partiallyMockedConfigDatabase.addRepo(testRepo2);

        /* Test if we would try using incorrect strings */
        try
        {
            new Repository("_underscore", "foo", null, null);
            fail("Expected an IllegalCharacterException to be thrown.");
        }
        catch (IllegalCharacterException e)
        {
            /* Expecting an IllegalCharacterException */
        }

        try
        {
            new Repository("spaces in names", "bar", null, null);
            fail("Expected an IllegalCharacterException to be thrown.");
        }
        catch (IllegalCharacterException e)
        {
            /* Expecting an IllegalCharacterException */
        }

        EasyMock.verify(mockCollection);
        EasyMock.verify(partiallyMockedConfigDatabase);
    }

    /**
     * Checks that finding repositories by name calls the correct methods
     * @throws RepositoryNotFoundException
     */
    @Test
    public void testGetRepoByName() throws RepositoryNotFoundException {

        /* The method calls below are expected */
        EasyMock.expect(mockCollection
                .getRepo("test-repo-name1"))
                .andReturn(testRepo1);

        EasyMock.expect(mockCollection
                .getRepo("test-repo-name2"))
                .andReturn(testRepo2);

        EasyMock.replay(mockCollection);
        EasyMock.replay(partiallyMockedConfigDatabase);

        /* The actual test begins here */
        assertEquals(testRepo1, partiallyMockedConfigDatabase.getRepoByName("test-repo-name1"));
        assertEquals(testRepo2, partiallyMockedConfigDatabase.getRepoByName("test-repo-name2"));

        EasyMock.verify(mockCollection);
        EasyMock.verify(partiallyMockedConfigDatabase);
    }

    /**
     * Checks that the correct method is called when updating a repository
     * @throws RepositoryNotFoundException
     */
    @Test
    public void testUpdateRepo() throws IOException, RepositoryNotFoundException {

        /* The method calls below are expected */
        mockCollection.updateRepo(testRepo1);
        EasyMock.expectLastCall().once();
        partiallyMockedConfigDatabase.generateConfigFile();
        EasyMock.expectLastCall().once();

        mockCollection.updateRepo(testRepo2);
        EasyMock.expectLastCall().once();
        partiallyMockedConfigDatabase.generateConfigFile();
        EasyMock.expectLastCall().once();

        EasyMock.replay(mockCollection);
        EasyMock.replay(partiallyMockedConfigDatabase);

        /* The actual test begins here */
        partiallyMockedConfigDatabase.updateRepo(testRepo1);
        partiallyMockedConfigDatabase.updateRepo(testRepo2);

        EasyMock.verify(mockCollection);
        EasyMock.verify(partiallyMockedConfigDatabase);
    }

    @Test
    public void testDeleteRepo() throws IOException, RepositoryNotFoundException {

        /* The method calls below are expected */
        EasyMock.expect(mockCollection.contains("some-name")).andReturn(false);
        EasyMock.expect(mockCollection.contains("some-other-name")).andReturn(true);
        mockCollection.removeRepo("some-other-name");
        partiallyMockedConfigDatabase.generateConfigFile();
        EasyMock.expectLastCall().once();

        EasyMock.replay(mockCollection);
        EasyMock.replay(partiallyMockedConfigDatabase);

        /* The actual test begins here */
        try {
            partiallyMockedConfigDatabase.delRepoByName("some-name");
            fail("Should have thrown a RepositoryNotFoundException");
        } catch (RepositoryNotFoundException e) {
            /* This is supposed to happen */
        }
        try {
            partiallyMockedConfigDatabase.delRepoByName("some-other-name");
        } catch (RepositoryNotFoundException e) {
            fail("Threw a RepositoryNotFoundException when it wasn't suppose to");
        }

        EasyMock.verify(mockCollection);
        EasyMock.verify(partiallyMockedConfigDatabase);
    }



}
