/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.UROP.twentyfourteen.public_interfaces;

import java.util.Collection;
import java.io.IOException;
import java.io.File;
import uk.ac.cam.UROP.twentyfourteen.EmptyDirectoryExpectedException;

/**
 * @author Isaac Dunn &lt;ird28@cam.ac.uk&gt;
 * @author Kovacsics Robert &lt;rmk35@cam.ac.uk&gt;
 * @version 0.1
 */

public interface FrontendRepositoryInterface
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
