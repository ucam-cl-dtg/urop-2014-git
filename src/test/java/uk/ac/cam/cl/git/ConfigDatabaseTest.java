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
import java.io.InputStream;

import org.easymock.*;
import org.junit.Test;

import uk.ac.cam.cl.git.configuration.ConfigurationLoader;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 * @version 0.1
 */
public class ConfigDatabaseTest extends EasyMockSupport {

    private static List<String> readOnlys = new LinkedList<String>();
    private static List<String> readAndWrites = new LinkedList<String>();
    private static List<String> emptyList = new LinkedList<String>();
    private static Repository testRepo1 = new Repository("test-repo-name1",
            "repository-owner", readAndWrites, readOnlys);
    private static Repository testRepo2 = new Repository("test-repo-name2",
            "repository-owner", readAndWrites, emptyList);
       
    private RepositoryCollection mockCollection =
        createMock(RepositoryCollection.class);
    private Runtime mockRuntime = createMock(Runtime.class);
    private Process processToReturn = createNiceMock(Process.class);

    {
        ConfigDatabase.setReposCollection(mockCollection);
        ConfigDatabase.setRuntime(mockRuntime);
    }

    static {
        readOnlys.add("readonlyUser1");
        readOnlys.add("readonlyUser2");
        readAndWrites.add("adminUser");
    }
    
    @Test
    public void checkRuntimeIsActuallyMocked() throws IOException {
        EasyMock.expect(mockRuntime.exec("bananas in pyjamas", ConfigDatabase.getEnvVar())).andReturn(processToReturn);
        EasyMock.replay(mockRuntime);
        ConfigDatabase.testRuntime();
        EasyMock.verify(mockRuntime);
    }

    /**
     * Checks that repositories can be added to the database.
     * @throws DuplicateKeyException 
     */
    @Test
    public void testAddRepo() throws IOException, DuplicateKeyException {

        /* The below method calls are expected */

        mockCollection.insertRepo(testRepo1);
        EasyMock.expectLastCall().once();
        EasyMock.expect(mockCollection.findAll()).andReturn(new LinkedList<Repository>());
        EasyMock.expect(
                mockRuntime.exec(
                        "env gitolite " + "compile", ConfigDatabase.getEnvVar()))
                        .andReturn(processToReturn);
        EasyMock.expect(
                mockRuntime.exec(
                        "env gitolite " + "trigger POST_COMPILE", ConfigDatabase.getEnvVar()))
                          .andReturn(processToReturn);

        mockCollection.insertRepo(testRepo2);
        EasyMock.expectLastCall().once();
        EasyMock.expect(mockCollection.findAll()).andReturn(new LinkedList<Repository>());
        EasyMock.expect(
                mockRuntime.exec("env gitolite compile", ConfigDatabase.getEnvVar()))
                          .andReturn(processToReturn);
        EasyMock.expect(
                mockRuntime.exec("env gitolite trigger POST_COMPILE", ConfigDatabase.getEnvVar()))
                          .andReturn(processToReturn);
        
        EasyMock.expect(processToReturn.getErrorStream()).andReturn(null).anyTimes();
        EasyMock.expect(processToReturn.getInputStream()).andReturn(null).anyTimes();

        EasyMock.replay(mockCollection);
        EasyMock.replay(mockRuntime);
        EasyMock.replay(processToReturn);

        /* The actual test begins here */

        ConfigDatabase.addRepo(testRepo1);
        ConfigDatabase.addRepo(testRepo2);

        EasyMock.verify(mockCollection);
        EasyMock.verify(mockRuntime);
        EasyMock.verify(processToReturn);
    }

    /**
     * Checks that finding repositories by name calls the correct methods
     */
    @Test
    public void testGetRepoByName() {

        /* The below method calls are expected */

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

        /* The below method calls are expected */

        mockCollection.updateRepo(testRepo1);
        EasyMock.expectLastCall().once();
        EasyMock.expect(mockCollection.findAll()).andReturn(new LinkedList<Repository>());
        EasyMock.expect(
                mockRuntime.exec("env gitolite compile", ConfigDatabase.getEnvVar()))
                          .andReturn(processToReturn);
        EasyMock.expect(
                mockRuntime.exec("env gitolite trigger POST_COMPILE", ConfigDatabase.getEnvVar()))
                          .andReturn(processToReturn);

        mockCollection.updateRepo(testRepo2);
        EasyMock.expectLastCall().once();
        EasyMock.expect(mockCollection.findAll()).andReturn(new LinkedList<Repository>());
        EasyMock.expect(
                mockRuntime.exec("env gitolite compile", ConfigDatabase.getEnvVar()))
                          .andReturn(processToReturn);
        EasyMock.expect(
                mockRuntime.exec("env gitolite trigger POST_COMPILE", ConfigDatabase.getEnvVar()))
                          .andReturn(processToReturn);
        
        EasyMock.expect(processToReturn.getErrorStream()).andReturn(null).anyTimes();
        EasyMock.expect(processToReturn.getInputStream()).andReturn(null).anyTimes();

        EasyMock.replay(mockCollection);
        EasyMock.replay(mockRuntime);
        EasyMock.replay(processToReturn);

        /* The actual test begins here */

        ConfigDatabase.updateRepo(testRepo1);
        ConfigDatabase.updateRepo(testRepo2);

        EasyMock.verify(mockCollection);
        EasyMock.verify(mockRuntime);
        EasyMock.verify(processToReturn);
    }



}
