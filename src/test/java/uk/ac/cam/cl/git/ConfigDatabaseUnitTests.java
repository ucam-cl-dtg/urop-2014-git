/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.cl.git;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.easymock.*;
import org.junit.Test;
import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;

import com.mongodb.BasicDBObject;

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
    private  JacksonDBCollection<Repository, String> mockCollection =
        createMock(JacksonDBCollection.class);
	
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
	 */
	@Test
	public void testAddRepo() {
	    
	    /* The below method calls to the database are expected */
	    
	    mockCollection.ensureIndex(new BasicDBObject("name", 1), null, true);
        EasyMock.expectLastCall().once();
	    EasyMock.expect(mockCollection.insert(testRepo1)).andReturn(createMock(WriteResult.class));
	    
	    mockCollection.ensureIndex(new BasicDBObject("name", 1), null, true);
        EasyMock.expectLastCall().once();
        EasyMock.expect(mockCollection.insert(testRepo2)).andReturn(createMock(WriteResult.class));
                
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
                .findOne(new BasicDBObject("name", "test-repo-name1")))
                .andReturn(testRepo1);
        
        EasyMock.expect(mockCollection
                .findOne(new BasicDBObject("name", "test-repo-name2")))
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
    public void testUpdateRepo() {
        
        /* The below method calls to the database are expected */
        
        EasyMock.expect(mockCollection
                .updateById(testRepo1.get_id(), testRepo1))
                .andReturn(createMock(WriteResult.class));
        
        EasyMock.expect(mockCollection
                .updateById(testRepo2.get_id(), testRepo2))
                .andReturn(createMock(WriteResult.class));
        
        EasyMock.replay(mockCollection);
        
        /* The actual test begins here */
        
        ConfigDatabase.updateRepo(testRepo1);
        ConfigDatabase.updateRepo(testRepo2);
        
        EasyMock.verify(mockCollection);
    }
	
	

}
