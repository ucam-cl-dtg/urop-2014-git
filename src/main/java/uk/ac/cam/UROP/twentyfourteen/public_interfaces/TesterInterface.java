/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.UROP.twentyfourteen.public_interfaces;

import java.util.Map;
import java.util.Collection;
import java.io.IOException;

/**
 * This is the interface the testing end of the project should use.
 * <p>
 * For a concrete implementation
 * @author Isaac Dunn <ird28@cam.ac.uk>
 * @author Kovacsics Robert <rmk35@cam.ac.uk>
 * @version 0.1
 */
public interface TesterInterface
{
    /**
     * Gets the source files
     *
     * @return List of source files
     */
    public Collection<String> getSources() throws IOException;

    /**
     * Returns a map of test files and a list of required files for
     * those tests to run
     *
     * @return Map of test files and a list of the test's dependencies
     */
    public Map<String, Collection<String>> getTests();

    /**
     * Callback to let the repository store the test result
     *
     * @param
     */
    public void saveTestResults(Object results);

    /**
     * Gets the CRSID of the repository owner
     *
     * @return CRSID of the repository owner
     */
    public String getCRSID();

    /**
     * Gets the name of the repository
     *
     * @return Name of the repository
     */
    public String getName();
}
