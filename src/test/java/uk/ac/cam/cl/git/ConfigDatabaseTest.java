/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.git;

import static org.junit.Assert.*;
import static org.easymock.EasyMock.*;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.easymock.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;

import com.mongodb.BasicDBObject;
import com.mongodb.DuplicateKeyException;

import uk.ac.cam.cl.git.database.Mongo;

/**
 * @author ird28
 *
 */
public class ConfigDatabaseTest extends EasyMockSupport {
	
	private List<String> readOnlys;
	private List<String> readAndWrites;
	private List<String> emptyList;
	private Repository testRepo1;
	private Repository testRepo2;
	
	@Mock
	private JacksonDBCollection<Repository, String> mockCollection;
	
	@Before
	public void setUp() {
		readOnlys = new LinkedList<String>();
		readAndWrites = new LinkedList<String>();
		emptyList = new LinkedList<String>();
		readOnlys.add("readonlyUser1");
		readOnlys.add("readonlyUser2");
		readAndWrites.add("adminUser");
		testRepo1 = new Repository("test-repo-name1",
                "repository-owner", readAndWrites, readOnlys, 
                "test-parent1", "hidden-eg-parent");
		testRepo2 = new Repository("test-repo-name2",
                "repository-owner", readAndWrites, emptyList, 
                "test-parent2", "hidden-eg-parent");
		mockCollection = createMock(JacksonDBCollection.class);
		ConfigDatabase.setReposCollection(mockCollection);
		System.out.println(mockCollection == null);
	}

	/**
	 * 
	 */
	@Test
	public void testAddThenGetRepos() {
	    /*mockCollection.ensureIndex(new BasicDBObject("name", 1), null, true);
	    mockCollection.insert(testRepo1);
	    mockCollection.ensureIndex(new BasicDBObject("name", 1), null, true);
	    mockCollection.insert(testRepo2); */
	    mockCollection.find();
	    replayAll();
	    //ConfigDatabase.addRepo(testRepo1);
	    //ConfigDatabase.addRepo(testRepo2);
		List<Repository> allRepos = ConfigDatabase.getRepos();
		verifyAll();
	}
	
	/**
	 * Checks that when two repositories of the same name are inserted,
	 * the second is not added and an exception is raised.
	 */
	//@Test
	public void testOnlyOneRepoPerName() {
	    JacksonDBCollection<Repository, String> repoCollection = 
                JacksonDBCollection.wrap(Mongo.getDB().getCollection("repos"), Repository.class, String.class);
        repoCollection.remove(new BasicDBObject("name", "test-name"));
        Repository testRepo = new Repository("test-name",
                "repository-owner", readAndWrites, readOnlys, 
                "example-parent-repo", "hidden-eg-parent");
        ConfigDatabase.addRepo(testRepo);
        DBCursor<Repository> allRepos = repoCollection.find();
        int repoNumber = allRepos.size();
        Repository testRepo2 = new Repository("test-name",
                "other-owner", readAndWrites, readAndWrites, 
                "other-parent-repo", "other-hidden-parent");
        try {
            ConfigDatabase.addRepo(testRepo2);
            fail("An exception should have been raised because a repo with this name already exists");
        } catch (DuplicateKeyException dke) {
            // This should happen - fail otherwise
        }
        allRepos = repoCollection.find();
        assertTrue(allRepos.size() == repoNumber); // The number of repositories should not have changed        
	}

}
