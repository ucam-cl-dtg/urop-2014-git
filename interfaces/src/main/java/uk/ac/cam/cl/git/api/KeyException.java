/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */

package uk.ac.cam.cl.git.api;

import java.util.List;

/**
 * @author rmk35
 *
 */
public class KeyException extends Exception
{
    private static final long serialVersionUID = -5216630858749031217L; /* Generated */

    public KeyException()
    {
        super();
    }

    public KeyException(String comment)
    {
        super(comment);
    }
}
