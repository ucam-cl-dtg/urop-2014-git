/* vim: set et ts=4 sts=4 sw=4 tw=72 */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.UROP.twentyfourteen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

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
		output.append("repo create-new\n");
		output.append("    RW = admin\n");
		output.append("    R  = u2");
		try {
			File configFile = new File("/home/ird28/test.conf");
			BufferedWriter buffWriter = new BufferedWriter(new FileWriter(configFile, false));
			buffWriter.write(output.toString());
			buffWriter.close();
			Process p = Runtime.getRuntime().exec("/home/ird28/.gitolite/hooks/gitolite-admin/post-update",
						new String[] {"HOME=/home/ird28", "PATH=/home/ird28/bin/:/bin:/usr/bin", "GL_LIBDIR=/home/ird28/git/gitolite/src/lib"});
			
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
	
	public static void main(String[] args) {
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
