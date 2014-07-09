/* vim: set et ts=4 sts=4 sw=4 tw=72 */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.UROP.twentyfourteen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.mongojack.DBCursor;
import org.mongojack.JacksonDBCollection;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import org.mongojack.JacksonDBCollection;

import uk.ac.cam.UROP.twentyfourteen.database.Mongo;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 * @author Kovacsics Robert &lt;rmk35@cam.ac.uk&gt;
 * @version 0.1
 */
public class ConfigDatabase {

	/**
	 * Generates config file for gitolite and writes it to ~/test.conf
	 * <p>
	 * Accesses mongoDB to find repositories and assumes the Repository.toString() returns the appropriate representation.
	 * The main conf file should have an include test.conf statement so that when the hook is called, the updates are made.
	 * The hook is called at the end of this method.
	 */
	public static void generateConfigFile() {
		StringBuilder output = new StringBuilder();
		JacksonDBCollection<Repository, String> repoCollection = 
				JacksonDBCollection.wrap(Mongo.getDB().getCollection("repos"), Repository.class, String.class);
		DBCursor<Repository> allRepos = repoCollection.find();
		while (allRepos.hasNext()) {
			/* old implementation
			DBObject repoDoc = allRepos.next();
			output.append("repo " + repoDoc.get("repoName") + "\n");
			BasicDBList readWriteCRSIDs = (BasicDBList) repoDoc.get("readWrite");
			if (readWriteCRSIDs.size() > 0) {
				output.append("    RW = ");
				for (Object id : readWriteCRSIDs) {
					output.append(id + " ");
				}
				output.append("\n");
			}
			BasicDBList readOnlyCRSIDs = (BasicDBList) repoDoc.get("readOnly");
			if (readOnlyCRSIDs.size() > 0) {
				output.append("    R  = ");
				for (Object id : readOnlyCRSIDs) {
					output.append(id + " ");
				}
				output.append("\n");
			}
			*/
			Repository currentRepo = allRepos.next();
			output.append(currentRepo.toString());
			output.append("\n");
		}
		try {
			String home = System.getProperty("user.home");
			File configFile = new File(home+"/UROP.conf");
			BufferedWriter buffWriter = new BufferedWriter(new FileWriter(configFile, false));
			buffWriter.write(output.toString());
			buffWriter.close();
			Process p = Runtime.getRuntime().exec(home+"/.gitolite/hooks/gitolite-admin/post-update",
						new String[] {"HOME="+home, "PATH="+home+"/bin/:/bin:/usr/bin", "GL_LIBDIR="+home+"/git/gitolite/src/lib"});
			BufferedReader errorReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
			BufferedReader outputReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = outputReader.readLine()) != null) {
				System.out.println(line);
			}
			while ((line = errorReader.readLine()) != null) {
				System.err.println(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Adds a new repository to the mongo database for inclusion in the conf file when generated.
	 *
	 * @param repo The repository to be added
	 * @throws IOException 
	 */
	public static void addRepo(Repository repo) {
		JacksonDBCollection<Repository, String> repoCollection =
				JacksonDBCollection.wrap(Mongo.getDB().getCollection("repos"), Repository.class, String.class);
		repoCollection.insert(repo);
	}
	
	public static void main(String[] args) throws IOException {
		Repository toBeTested = new Repository("testname", "someCRSID",
				new LinkedList<String>(), new LinkedList<String>(), "test-parent", "test-hidden-parent");
		addRepo(toBeTested);
		JacksonDBCollection<Repository, String> repoCollection = 
				JacksonDBCollection.wrap(Mongo.getDB().getCollection("repos"), Repository.class, String.class);
		DBCursor<Repository> allRepos = repoCollection.find();
		while (allRepos.hasNext()) {
			Repository currentRepo = allRepos.next();
			System.out.println("Name: " + currentRepo.getName() + " CRSID: " + currentRepo.getCRSID());
		}
	}

	
	
	/**
	 * Takes public key and username as strings, writes the key to keydir/UROP/username.pub, and calls the hook.
	 *
	 * @param key The SSH key to be added
	 * @param username The name of the user to be added
	 */
	public static void addSSHKey(String key, String username) {
		try {
			String home = System.getProperty("user.home");
			File keyFile = new File(home + "/.gitolite/keydir/UROP/" + username + ".pub");
			if (!keyFile.exists()) {
				keyFile.createNewFile();
			}
			BufferedWriter buffWriter = new BufferedWriter(new FileWriter(keyFile));
			buffWriter.write(key);
			buffWriter.close();
			Runtime.getRuntime().exec(home+"/.gitolite/hooks/gitolite-admin/post-update",
					new String[] {"HOME="+home, "PATH="+home+"/bin/:/bin:/usr/bin", "GL_LIBDIR="+home+"/git/gitolite/src/lib"});
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
