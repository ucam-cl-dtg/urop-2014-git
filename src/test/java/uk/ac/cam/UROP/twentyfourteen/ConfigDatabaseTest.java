/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.UROP.twentyfourteen;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import uk.ac.cam.UROP.twentyfourteen.database.Mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

/**
 * @author ird28
 *
 */
public class ConfigDatabaseTest {

	@Test
	public void generatingConfigFilesTest() {
		DBCollection repoTable = Mongo.getDB().getCollection("repos");
		if (repoTable != null)
			repoTable.remove(new BasicDBObject());
		ArrayList<String> group1 = new ArrayList<String>();
		group1.add("ird28");
		group1.add("rmk35");
		ArrayList<String> group2 = new ArrayList<String>();
		group2.add("prv22");
		group2.add("gk349");
		group2.add("ft267");
		ArrayList<String> group3 = new ArrayList<String>();
		group3.add("jag205");
		ConfigDatabase.addRepo("exampleRepository", group2, group1);
		ConfigDatabase.addRepo("anotherRepository", group3, new ArrayList<String>());
		ConfigDatabase.generateConfigFile();
		
		try {
			BufferedReader buffRead = new BufferedReader(new FileReader(System.getProperty("user.home")+"/test.conf"));
			assertTrue(buffRead.readLine().equals("repo exampleRepository"));
			assertTrue(buffRead.readLine().equals("    RW = ird28 rmk35 "));
			assertTrue(buffRead.readLine().equals("    R  = prv22 gk349 ft267 "));
			assertTrue(buffRead.readLine().equals("repo anotherRepository"));
			assertTrue(buffRead.readLine().equals("    R  = jag205 "));
			buffRead.close();
		} catch (FileNotFoundException e) {
			fail("The file that should have been written was not found.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void addingSSHKeysTest() {
		String key = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDS5IkY7Z8NQxsdDna/SW1X0PkHlW/KlXmGyhk8gufIdGrrJSTKfUH+45NvHTcSEnao1wxJVMQd4hbh/Ym+NxjuJSB+4qiEpwMYGD+cEu7aYAt8kaYTCSblRWpO4iNhikLXLv6fNapSxFzppxzGzFwLZyNwR6pkdLgUNkxmNke/Cm/9jMFp0fd1vklEXkeGaHJ5l6prH+zeDq40iInqrsb3CF4SbJaM+LMtVO3cMgvayjHI3Qwcp/gmvEWjMLSTg56mfS78MCiDEXp5QowICQv5XperPPG0oUrpPPgRnrgmI5Rr8R8qRLl03tmQjTKZMu7u71KsPf1022IsoDGx0PQf ird28@pccl067";
		ConfigDatabase.addSSHKey(key, "unittestuser");
		try {
			BufferedReader buffRead = new BufferedReader(new FileReader(System.getProperty("user.home")+"/.gitolite/keydir/UROP/unittestuser.pub"));
			assertTrue(buffRead.readLine().equals(key));
			buffRead.close();
		} catch (FileNotFoundException e) {
			fail("The file that should have been written was not found.");
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

}
