/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.git;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import uk.ac.cam.cl.git.configuration.ConfigurationLoader;

import com.google.inject.Guice;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 */
public class ConfigDatabaseIntegrationTests {

    private static List<String> readOnlys = new LinkedList<String>();
    private static List<String> readAndWrites = new LinkedList<String>();
    private static List<String> emptyList = new LinkedList<String>();
    
    static {
        readOnlys.add("readonlyUser1");
        readOnlys.add("readonlyUser2");
        readOnlys.add("readonlyUser3");
        readAndWrites.add("adminUser1");
        readAndWrites.add("adminUser2");
    }
    
    private static Repository testRepo1 = new Repository("test-repo-name1",
            "repository-owner", readAndWrites, readOnlys, "p1", "h1", null);
    private static Repository testRepo1a = new Repository("test-repo-name1",
            "other-owner", emptyList, readOnlys, "p1", "h1", null);
    private static Repository testRepo2 = new Repository("test-repo-name2",
            "other-owner", readAndWrites, emptyList, "p2", "h2", null);

    /**
     * Before each test, empty the database.
     */
    @Before
    public void setUp() throws IOException {        
        ConfigDatabase.deleteAll();
    }
    
    /**
     * @throws DuplicateKeyException 
     * Checks that the gitolite config file is written as expected and the
     * repositories are found in the expected place.
     * @throws  
     */
    @Test
    public void testGenerateConfigFile() throws IOException, DuplicateKeyException {
        ConfigDatabase.addRepo(testRepo1);
        ConfigDatabase.addRepo(testRepo2);
        try {
            ConfigDatabase.generateConfigFile();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Something went wrong with the I/O in the generateConfigFile method");
        }
        try {
            File configFile = new File(ConfigurationLoader.getConfig()
                    .getGitoliteGeneratedConfigFile());
            BufferedReader br = new BufferedReader(new FileReader(configFile));
            assertEquals(br.readLine(),
                    "repo test-repo-name1");
            assertEquals(br.readLine(),
                    "     RW = repository-owner adminUser1 adminUser2");
            assertEquals(br.readLine(),
                    "     R  = readonlyUser1 readonlyUser2 readonlyUser3");
            assertEquals("# p1 h1", br.readLine());
            assertEquals(br.readLine(), "");
            assertEquals(br.readLine(),
                    "repo test-repo-name2");
            assertEquals(br.readLine(),
                    "     RW = other-owner adminUser1 adminUser2");
            assertEquals("# p2 h2", br.readLine());
            assertEquals(br.readLine(), "");
            assertNull(br.readLine()); // end of file reached
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail("The file that should have been written to was not found");
        } catch (IOException e) {
            e.printStackTrace();
            fail("An I/O exception occurred when reading the config file");
        }
    }
    
    /**
     * Checks that when two repositories of the same name are inserted,
     * the second is not added and an exception is raised.
     * Assumes adding and getting repositories works as intended.
     * @throws DuplicateKeyException 
     */
    @Test
    public void testOnlyOneRepoPerName() throws IOException, DuplicateKeyException {
        ConfigDatabase.addRepo(testRepo1);
        assert testRepo1.getName().equals(testRepo1a.getName()); // conflicting names
        try {
            ConfigDatabase.addRepo(testRepo1a);
            fail("An exception should have been raised because a repo with this name already exists");
        } catch (DuplicateKeyException dke) {
            /* This should happen - fail otherwise */
        }
        assertEquals(testRepo1.getCRSID(), ConfigDatabase.getRepoByName("test-repo-name1").getCRSID());
    }
    
    /**
     * Checks that adding repos and getting them by name works.
     * Assumes that deleting repos works, and deleting a non-existent repo is fine.
     * @throws DuplicateKeyException 
     */
    @Test
    public void testStoringRepos() throws IOException, DuplicateKeyException {
        ConfigDatabase.addRepo(testRepo1);
        ConfigDatabase.addRepo(testRepo2);
        assertEquals(testRepo1.getCRSID(),
                ConfigDatabase.getRepoByName("test-repo-name1").getCRSID());
        assertEquals(testRepo2.getCRSID(),
                ConfigDatabase.getRepoByName("test-repo-name2").getCRSID());
        assertNotEquals(ConfigDatabase.getRepoByName("test-repo-name1").getCRSID(),
                ConfigDatabase.getRepoByName("test-repo-name2").getCRSID());
    }
    
    /**
     * Checks that calling update repo on an existing repo is fine.
     * However, is does NOT check that the repo has actually been updated. (TODO?)
     * Assumes adding a repo is fine.
     * @throws DuplicateKeyException 
     */
    @Test
    public void testUpdateRepo() throws IOException, DuplicateKeyException {
        ConfigDatabase.addRepo(testRepo1);
        ConfigDatabase.updateRepo(testRepo1);
    }
    
    /**
     * Checks that when a repo is added and deleted, it appears and disappears 
     * from the list of repositories.
     * Assumes adding and deleting repos works.
     * @throws DuplicateKeyException 
     */
    @Test
    public void testGetAndDeleteRepos() throws IOException, DuplicateKeyException {
        assertFalse(containsRepo(ConfigDatabase.getRepos(), "test-repo-name1"));
        
        ConfigDatabase.addRepo(testRepo1);
        assertTrue(containsRepo(ConfigDatabase.getRepos(), "test-repo-name1"));
        
        ConfigDatabase.delRepoByName("test-repo-name1");
        assertFalse(containsRepo(ConfigDatabase.getRepos(), "test-repo-name1"));
    }
    
    @Test
    public void testListRepos() throws IOException, DuplicateKeyException {
        assertEquals(0, ConfigDatabase.getRepos().size());
        ConfigDatabase.addRepo(testRepo1);
        ConfigDatabase.addRepo(testRepo2);
        List<Repository> repoList = ConfigDatabase.getRepos();
        assertEquals(2, repoList.size());
        assertTrue(containsRepo(repoList, "test-repo-name1"));
        assertTrue(containsRepo(repoList, "test-repo-name2"));
    }
    
    private boolean containsRepo(List<Repository> repos, String repoName) {
        for (Repository repo : repos) {
            if (repo.getName().equals(repoName)) {
                return true;
            }
        }
        return false;
    }

}
