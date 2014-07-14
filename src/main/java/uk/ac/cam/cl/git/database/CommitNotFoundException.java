/* vim: set et ts=4 sts=4 sw=4 tw=72 : */
/* See the LICENSE file for the license of the project */
package uk.ac.cam.cl.git.database;

/**
 * @author kr2
 *
 */
public class CommitNotFoundException extends Exception
{
    static final long serialVersionUID = 1L;
    public CommitNotFoundException (String message)
    {
        super(message);
    }
}
