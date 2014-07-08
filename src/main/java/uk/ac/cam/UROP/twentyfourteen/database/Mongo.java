/* vim: set et ts=4 sts=4 sw=4 tw=72 */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.UROP.twentyfourteen.database;

import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class Mongo {
	
	private static DB db;
	
	static {
		try {
			MongoClient client = new MongoClient();
			db = client.getDB("UROP");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns a handle to the local mongoDB instance
	 */
	public static DB getDB() {
		return db;
	}

}