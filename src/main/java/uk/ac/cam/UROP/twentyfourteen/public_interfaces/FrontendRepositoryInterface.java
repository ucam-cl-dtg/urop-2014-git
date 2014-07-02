/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.UROP.twentyfourteen.public_interfaces;

import java.util.Collection;

/**
 * @author Isaac Dunn <ird28@cam.ac.uk>
 * @author Kovacsics Robert <rmk35@cam.ac.uk>
 * @version 0.1
 */

public interface FrontendRepositoryInterface
{
    /**
     * Gets the source files
     *
     * @return List of source files
     */
    public Collection<String> getSources();
}
