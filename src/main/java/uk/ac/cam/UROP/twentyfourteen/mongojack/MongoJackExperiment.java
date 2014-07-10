/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
/**
 * 
 */
package uk.ac.cam.UROP.twentyfourteen.mongojack;

import org.mongojack.JacksonDBCollection;
import org.mongojack.WriteResult;

import com.mongodb.DB;

import uk.ac.cam.UROP.twentyfourteen.database.Mongo;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 * 
 */
public class MongoJackExperiment {
	
	public static void main(String[] args) {
		
		DB db = Mongo.getDB();
		
		JacksonDBCollection<MyPOJO, String> collection = JacksonDBCollection.wrap(db.getCollection("mongojack-test"), MyPOJO.class, String.class);
		MyPOJO testPojo = new MyPOJO(4, 17, 4.35, "This is just an example POJO");
		WriteResult<MyPOJO, String> result = collection.insert(testPojo);
		MyPOJO saved = result.getSavedObject();
		System.out.println(saved.getLabel());
		System.out.println(saved.getX());
		System.out.println(saved.getY());
		System.out.println(saved.getT());
	}

}
