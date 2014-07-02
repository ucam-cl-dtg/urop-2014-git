/* vim: set et ts=4 sts=4 sw=4 tw=72 */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.UROP.twentyfourteen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import uk.ac.cam.UROP.twentyfourteen.database.Mongo;

/**
 * @author Isaac Dunn <ird28@cam.ac.uk>
 * @author Kovacsics Robert <rmk35@cam.ac.uk>
 * @version 0.1
 */
public class ConfigDatabase {

	/**
	 * Generates config file for gitolite.
	 * <p>
	 * Accesses mongoDB look up relevant information.
	 * 
	 * @return The gitolite config file
	 */
	public static void generateConfigFile() {
		/* TODO: implement
         *
         * 1) Create a new StringBuilder
         * 2) Fill in StringBuilder according to a template
         * 3) Write file to disk
         */
		StringBuilder output = new StringBuilder();
		Cursor allRepos = Mongo.getDB().getCollection("repos").find();
		while (allRepos.hasNext()) {
			//DBObject repoDoc = allRepos.next();
			output.append("repo " + allRepos.next().get("repoName") + "\n");
			output.append("    RW = admin\n");
			output.append("    R  = u1\n\n");
		}
		try {
			String home = System.getProperty("user.home");
			File configFile = new File(home+"/test.conf");
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
	
	public static void addRepo(String repoName) {
		DBCollection repoTable = Mongo.getDB().getCollection("repos");
		BasicDBObject repoDoc = new BasicDBObject();
		repoDoc.put("repoName", repoName);
		repoTable.insert(repoDoc);
	}
	
	public static void main(String[] args) {
		DBCollection repoTable = Mongo.getDB().getCollection("repos");
		if (repoTable != null)
			repoTable.remove(new BasicDBObject());
		addRepo("test-repo-one");
		addRepo("test-repo-two");
		generateConfigFile();
		System.out.println("Done");
	}
	
	
	/**
	 * Adds student SSH public key.
	 * 
	 * @param key The SSH key to be added
	 */
	public void addSSHKey(String key) {
		/* TODO: implement
         *
         * See gitolite §11.5
         */
	}

}
