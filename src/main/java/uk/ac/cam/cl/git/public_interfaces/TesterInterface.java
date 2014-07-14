/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git.public_interfaces;

import java.util.Map;
import java.util.Collection;
import java.io.File;
import java.io.IOException;

import uk.ac.cam.cl.git.EmptyDirectoryExpectedException;

/**
 * This is the interface the testing end of the project should use.
 * <p>
 * For a concrete implementation
 *
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 * @author Kovacsics Robert &lt;rmk35@cam.ac.uk&gt;
 * @version 0.2
 */
public interface TesterInterface
{
    /**
     * Clones repository to specified directory, if it can get
     * repository access.
     * <p>
     * It tries to access the repository with the id_rsa key.
     *
     * @param directory The empty directory to which you want to clone
     * into.
     *
     * @throws EmptyDirectoryExpectedException The File given is either
     * not a directory or not empty.
     * @throws IOException Something went wrong (typically not
     * recoverable).
     */
    public void cloneTo(File directory) throws EmptyDirectoryExpectedException, IOException;

    /**
     * Gets the source files
     *
     * @return List of source files
     *
     * @throws IOException Something went wrong (typically not
     * recoverable).
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
     * @param results The object file encapsulating the results of the
     * test.
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
