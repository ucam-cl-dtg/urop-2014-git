package uk.ac.cam.UROP.twentyfourteen;

public class GitRepo {
	
	/**
	 * Pulls in student repo and tick repo and hands over relevant files to be tested.
	 */
	public void submit() {
		//TODO implement
	}
	
	/**
	 * Saves test results into mongo database.
	 * <p>
	 * Needs to store commit number or similar, so that the test results cannot be changed.
	 * 
	 * @param results The results to be saved
	 */
	public void saveTestResults(Object results) { // won't be an Object eventually
		//TODO implement
	}
	
	/**
	 * Returns requested test results.
	 * 
	 * @param request Specifies which results are wanted
	 * 
	 * @returns The requested test results
	 */
	public Object getTestResults(String request) { // these types will change
		//TODO implement
		return new Object();
	}

}
